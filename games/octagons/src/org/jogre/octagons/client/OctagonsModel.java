/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
 * Copyright (C) 2005-2006  Richard Walter
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.octagons.client;

import nanoxml.XMLElement;

import java.util.Enumeration;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;

// Model which holds the data for a game of Octagons
public class OctagonsModel extends JogreModel {

	// Declare constants for ownership of the spaces
	public static final int PLAYER_NONE = 0;
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = 2;
	public static final int PLAYER_NEVER = 3;

	// Declare the size of the board
	public static final int ROWS = 6;

	// Declare the board elements
	// Each row of the board_owner array has 3 elements: each octagon half and a square.
	protected int [] [] [] board_owner = new int [ROWS][ROWS][3];
	protected int [] [] [] board_island_id = new int [ROWS][ROWS][3];

	// A player's turn consists of either choosing 1 half-octagon or 2 squares.
	// This keeps track of whether the player has already chosen a square or not.
	protected boolean already_played_square;

	// Keep track of if the pie-rule is in force.
	// (The pie rule is that after PLAYER_ONE has made his first move, PLAYER_TWO has
	//  the option of taking PLAYER_ONE's move as his own or not.  This is equivalent
	//  to the pie-problem where both players will agree that they have half of the
	//  pie if one person cuts and the other chooses which half to take first.)
	protected boolean pie_rule_in_force;

	// Keep track of the number of moves & number of turns made
	// The two are not the same;  A turn can consist of two moves when squares are selected.
	// Moves are used for numbering the islands as moves are made.
	// Turns are used for determining if the pie-rule is in effect or not.
	protected int current_move_num;
	protected int current_turn_num;

	// These keep track of the last turn made.
	protected OctLoc lastMoveLoc_A, lastMoveLoc_B;

	// This keeps track of the number of squares unclaimed on the board.  Since the
	// number of octagons on the side of the board is even, the number of squares on
	// each side is odd, which means that there are an odd number of squares on the
	// board.  Since a move with squares always claims two squares, then there is the
	// chance that there will be one square left over.  If that square is claimed, then
	// the player's turn is over and we shouldn't wait for him to select another square.
	protected int squares_unclaimed;

	// These ID's are the fixed island numbers for the bottom & right edges of the board.
	private static final int BOTTOM_ISLAND_ID = -1;
	private static final int RIGHT_ISLAND_ID = -2;
	private static final int NO_ISLAND_ID = -3;

	// XML attributes used for sending/receiving board state
	private static final String XML_SPACE_NAME = "space";
	private static final String XML_ATT_I = "i";
	private static final String XML_ATT_J = "j";
	private static final String XML_ATT_ELEMENT = "el";
	private static final String XML_ATT_OWNER = "owner";
	private static final String XML_ATT_ISLAND_ID = "island";

	private static final String XML_MISC_NAME = "misc";
	private static final String XML_ATT_CURR_MOVE_NUM = "curr_move";
	private static final String XML_ATT_CURR_TURN_NUM = "curr_turn";
	private static final String XML_ATT_PIE_RULE = "pie_rule";
	private static final String XML_ATT_ALREADY_PLAYED_SQUARE = "played_square";

	/**
	 * Constructor for the model
	 */
	public OctagonsModel() {
		super();
		reset_game ();
	}


	/**
	 * Reset the model back to the initial state
	 */
	public void reset_game () {
		int i,j,e;

		// Clear the board back to empty state
		for (i=0; i<ROWS; i++) {
			for (j=0; j<ROWS; j++) {
				for (e=0; e<3; e++) {
					board_owner[i][j][e] = PLAYER_NONE;
					board_island_id[i][j][e] = NO_ISLAND_ID;
				}
			}
		}

		// Set the last row & column of squares to dummy ownership,
		//	since they don't exist on the board.
		for (i=0; i<ROWS; i++) {
			board_owner[i][ROWS-1][OctLoc.SQUARE] = PLAYER_NEVER;
			board_owner[ROWS-1][i][OctLoc.SQUARE] = PLAYER_NEVER;
		}

		// Reset misc. state values
		current_move_num = 0;
		current_turn_num = 0;
		pie_rule_in_force = false;
		already_played_square = false;
		squares_unclaimed = (ROWS-1) * (ROWS-1);
		lastMoveLoc_A = new OctLoc();
		lastMoveLoc_B = new OctLoc();

		refreshObservers();
	}

	/**
	 * Retrieve the owner of a space on the board
	 *
	 * @param (i, j)            Location of board space
	 * @param element           Element within the space
	 * @return owner of the space (one of PLAYER_NONE, PLAYER_ONE, or PLAYER_TWO)
	 */
	public int get_owner(int i, int j, int element) {
		return (board_owner[i][j][element]);
	}

	/**
	 * Return the location of the last turn.
	 *
	 * @param which    Which one of the two possible last moves are to be returned
	 * @return The location
	 */
	public OctLoc get_last_move_loc(int which) {
		return ( (which == 0) ? lastMoveLoc_A : lastMoveLoc_B );
	}

	/**
	 * Determine if the current player's turn is over.
	 *
	 * @return true or false
	 */
	public boolean turn_over() {
		return (!already_played_square);
	}

	/**
	 * Determine if the requested play is valid or not
	 *
	 * @param theLoc       Location to be tested
	 * @return true or false
	 */
	public boolean valid_play(OctLoc theLoc) {
		int i = theLoc.get_i();
		int j = theLoc.get_j();
		int element = theLoc.get_element();

		// Check bounds
		if ((element == OctLoc.NOWHERE) ||
			(i < 0) || (i >= ROWS) || (j < 0) || (j >= ROWS) ) {
			return false;
		}

		// Make sure the board space is clear.
		if (pie_rule_in_force) {
			// pie-rule in force: so only a PLAYER_TWO space is invalid.
			if (board_owner[i][j][element] == PLAYER_TWO) {
				return false;
			}

			// If a square has already been played, then only the other PLAYER_ONE's
			// square is valid
			if (already_played_square && (board_owner[i][j][element] != PLAYER_ONE)) {
				return false;
			}

		} else {
			// pie-rule not in force: so any non-empty space is invalid.
			if (board_owner[i][j][element] != PLAYER_NONE) {
				return false;
			}
		}

		// If a square has already been played, then only another square is valid
		if (already_played_square && (element != OctLoc.SQUARE)) {
			return false;
		}

		return true;
	}

	/**
	 * Attempt to make a play on the board.
	 *
	 * @param theLoc            Location to be played.
	 * @param player_id         The player which is playing on the space
	 * @return validity of the play
	 *          true = valid play
	 *          false = invalid play
	 */
	public boolean make_play(OctLoc theLoc, int player_id) {
		int i = theLoc.get_i();
		int j = theLoc.get_j();
		int element = theLoc.get_element();
		int oct_type = (i+j) & 0x01;    // type of octagon (0 = left/right, 1 = up/down)
		int old_owner;

		// Make sure the play is valid
		if ((valid_play(theLoc) == false) ||
			(player_id < PLAYER_ONE) || (player_id > PLAYER_TWO)) {
			return false;
		}

		// Remember the old owner of the space
		// (This is needed to determine if the pie rule stays in force...)
		old_owner = board_owner[i][j][element];

		// Set the value of the space to the given player
		board_owner[i][j][element] = player_id;

		// Set the island id of the space to the current move number and increment to the next move number
		board_island_id[i][j][element] = current_move_num;
		current_move_num = current_move_num + 1;

		// Set already_played_square flag appropriately
		if (element == OctLoc.SQUARE) {
			squares_unclaimed = squares_unclaimed - 1;
			already_played_square = ((already_played_square == false) && (squares_unclaimed != 0));
		} else {
			already_played_square = false;
		}

		// Now, connect any islands that may be joined by this piece that was just added.
		switch (element) {
			case OctLoc.OCT_1:
				if (oct_type == 0) {
					join_LR_oct_left_half(i, j);
				} else {
					join_UD_oct_up_half(i, j);
				}
				break;
			case OctLoc.OCT_2:
				if (oct_type == 0) {
					join_LR_oct_right_half(i, j);
				} else {
					join_UD_oct_down_half(i, j);
				}
				break;
			case OctLoc.SQUARE:
				join_square(i, j);
				break;
		}

		// Save this move as the last move
		set_last_move_location(theLoc);

		// Determine if the pie-rule will stay in force after this move or not.
		pie_rule_in_force = (
				// Player one just finished the first turn OR
			((current_turn_num == 0) && turn_over()) ||
				// The Pie rule was in force and player two just chose one of player one's squares
			(pie_rule_in_force && already_played_square && (old_owner == PLAYER_ONE))
			);

		// If the turn is over, then increment the turn number
		if (turn_over()) {
			current_turn_num = current_turn_num + 1;
		}

		// Update the Observers
		refreshObservers();

		return true;
	}

	/**
	 * Join the islands that are touched by the left half of a left/right octagon
	 *
	 * @param (i,j)    Indexes of the octagon
	 */
	private void join_LR_oct_left_half(int i, int j) {
		check_link(i, j, OctLoc.OCT_1, i, j, OctLoc.OCT_2);         // right half of same octagon
		check_link(i, j, OctLoc.OCT_1, (i-1), (j-1), OctLoc.SQUARE);// upper left square
		check_link(i, j, OctLoc.OCT_1, (i-1), j, OctLoc.SQUARE);    // lower left square
		check_link(i, j, OctLoc.OCT_1, i, (j-1), OctLoc.OCT_2);     // lower half of octagon above
		check_link(i, j, OctLoc.OCT_1, i, (j+1), OctLoc.OCT_1);     // upper half of octagon below
		check_link(i, j, OctLoc.OCT_1, (i-1), j, OctLoc.OCT_1);     // upper half of octagon to left
		check_link(i, j, OctLoc.OCT_1, (i-1), j, OctLoc.OCT_2);     // lower half of octagon to left
	}

	/**
	 * Join the islands that are touched by the right half of a left/right octagon
	 *
	 * @param (i,j)    Indexes of the octagon
	 */
	private void join_LR_oct_right_half(int i, int j) {
		check_link(i, j, OctLoc.OCT_2, i, j, OctLoc.OCT_1);         // left half of same octagon
		check_link(i, j, OctLoc.OCT_2, i, (j-1), OctLoc.SQUARE);    // upper right square
		check_link(i, j, OctLoc.OCT_2, i, j, OctLoc.SQUARE);        // lower right square
		check_link(i, j, OctLoc.OCT_2, i, (j-1), OctLoc.OCT_2);     // lower half of octagon above
		check_link(i, j, OctLoc.OCT_2, i, (j+1), OctLoc.OCT_1);     // upper half of octagon below
		check_link(i, j, OctLoc.OCT_2, (i+1), j, OctLoc.OCT_1);     // upper half of octagon to right
		check_link(i, j, OctLoc.OCT_2, (i+1), j, OctLoc.OCT_2);     // lower half of octagon to right
	}

	/**
	 * Join the islands that are touched by the upper half of an up/down octagon
	 *
	 * @param (i,j)    Indexes of the octagon
	 */
	private void join_UD_oct_up_half(int i, int j) {
		check_link(i, j, OctLoc.OCT_1, i, j, OctLoc.OCT_2);         // bottom half of same octagon
		check_link(i, j, OctLoc.OCT_1, (i-1), (j-1), OctLoc.SQUARE);// upper left square
		check_link(i, j, OctLoc.OCT_1, i, (j-1), OctLoc.SQUARE);    // upper right square
		check_link(i, j, OctLoc.OCT_1, (i-1), j, OctLoc.OCT_2);     // right half of octagon to left
		check_link(i, j, OctLoc.OCT_1, (i+1), j, OctLoc.OCT_1);     // left half of octagon to right
		check_link(i, j, OctLoc.OCT_1, i, (j-1), OctLoc.OCT_1);     // left half of octagon above
		check_link(i, j, OctLoc.OCT_1, i, (j-1), OctLoc.OCT_2);     // right half of octagon above
	}

	/**
	 * Join the islands that are touched by the lower half of an up/down octagon
	 *
	 * @param (i,j)    Indexes of the octagon
	 */
	private void join_UD_oct_down_half(int i, int j) {
		check_link(i, j, OctLoc.OCT_2, i, j, OctLoc.OCT_1);         // upper half of same octagon
		check_link(i, j, OctLoc.OCT_2, (i-1), j, OctLoc.SQUARE);    // lower left square
		check_link(i, j, OctLoc.OCT_2, i, j, OctLoc.SQUARE);        // lower right square
		check_link(i, j, OctLoc.OCT_2, (i-1), j, OctLoc.OCT_2);     // right half of octagon to left
		check_link(i, j, OctLoc.OCT_2, (i+1), j, OctLoc.OCT_1);     // left half of octagon to right
		check_link(i, j, OctLoc.OCT_2, i, (j+1), OctLoc.OCT_1);     // left half of octagon below
		check_link(i, j, OctLoc.OCT_2, i, (j+1), OctLoc.OCT_2);     // right half of octagon below
	}

	/**
	 * Join the islands that are touched by a square
	 *
	 * @param (i,j)    Indexes of the square
	 */
	private void join_square(int i, int j) {
		check_link(i, j, OctLoc.SQUARE, i, j, OctLoc.OCT_2);           // second half of octagon to upper left
		check_link(i, j, OctLoc.SQUARE, (i+1), (j+1), OctLoc.OCT_1);   // first half of octagon to lower right
		if (((i+j) & 0x01) == 0) {
			// For this square, the lower left & upper right octagons are up/down
			check_link(i, j, OctLoc.SQUARE, i, (j+1), OctLoc.OCT_1);   // top half of octagon to lower left
			check_link(i, j, OctLoc.SQUARE, (i+1), j, OctLoc.OCT_2);   // bottom half of octagon to upper right
		} else {
			// For this square, the lower left & upper right octagons are left/right
			check_link(i, j, OctLoc.SQUARE, i, (j+1), OctLoc.OCT_2);   // right half of octagon to lower left
			check_link(i, j, OctLoc.SQUARE, (i+1), j, OctLoc.OCT_1);   // left half of octagon to upper right
		}
	}

	/**
	 * Change all of the parts of the board that are one island number to a different island
	 *	number.
	 *
	 * @param old_num       Old island number to be changed
	 * @param new_num       New island number that the old numbers will be changed to
	 */
	private void change_island_nums(int old_num, int new_num) {
		int i, j;

		// System.out.println("Changing Island nums " + old_num + " -> " + new_num);

		for (i=0; i<ROWS; i++) {
			for (j=0; j<ROWS; j++) {
				if (board_island_id[i][j][OctLoc.OCT_1] == old_num) {
					board_island_id[i][j][OctLoc.OCT_1] = new_num;
				}
				if (board_island_id[i][j][OctLoc.OCT_2] == old_num) {
					board_island_id[i][j][OctLoc.OCT_2] = new_num;
				}
				if (board_island_id[i][j][OctLoc.SQUARE] == old_num) {
					board_island_id[i][j][OctLoc.SQUARE] = new_num;
				}
			}
		}
	}

	/**
	 * Check two see if two areas of the board should be linked together and converted to the
	 *  same island number.
	 * The first space of the board (src) must be on the board.  The second space (dest)
	 *  may be off of the board.
	 *
	 * @param (src_i, src_j)       Indexes of the first area of the board.
	 * @param src_el               Element of the first area of the board.
	 * @param (dest_i, dest_j)     Indexes of the second area of the board.
	 * @param dest_el              Element of the second area of the board.
	 */
	private void check_link(int src_i, int src_j, int src_el, int dest_i, int dest_j, int dest_el) {

		// Do quick check for off the left or top edge of the board.
		if ((dest_i < 0) || (dest_j < 0)) {
			return;
		}

		// Check for off the right edge of the board
		if (dest_i == ROWS) {
			// (Only PLAYER_TWO can connect to the right edge, so if it isn't PLAYER_TWO
			//	then there is nothing to do)
			if (board_owner[src_i][src_j][src_el] == PLAYER_TWO) {
				// By definition, the right edge of the board is RIGHT_ISLAND_ID for PLAYER_TWO
				change_island_nums(board_island_id[src_i][src_j][src_el], RIGHT_ISLAND_ID);
			}
			return;
		}

		// Check for off the bottom edge of the board
		if (dest_j == ROWS) {
			// (Only PLAYER_ONE can connect to the bottom edge, so if it isn't PLAYER_ONE
			//	then there is nothing to do)
			if (board_owner[src_i][src_j][src_el] == PLAYER_ONE) {
				// By definition, the bottom edge of the board is BOTTOM_ISLAND_ID for PLAYER_ONE
				change_island_nums(board_island_id[src_i][src_j][src_el], BOTTOM_ISLAND_ID);
			}
			return;
		}

		// We're in the middle of the board, so need to check to see if the two spaces are
		// parts of different islands for the same player that need to be joined.

		if (board_owner[src_i][src_j][src_el] == board_owner[dest_i][dest_j][dest_el]) {
			// Yes, they belong to the same player, so check if they are different islands.
			// If they are different islands, then we need to change the larger ID to the
			// smaller so that only the smaller ID survives.

			int src_id   = board_island_id[src_i ][src_j ][src_el ];
			int dest_id  = board_island_id[dest_i][dest_j][dest_el];

			if (src_id > dest_id) {
				// Need to change src_id (the larger one) to dest_id (the smaller one)
				change_island_nums(src_id, dest_id);
			} else if (src_id < dest_id) {
				// Need to change dest_id (the larger one) to src_id (the smaller one)
				change_island_nums(dest_id, src_id);
			}
		}
	}

	/**
	 * Set the given location as the last move.
	 *
	 * @param moveLoc        Location of the move.
	 */
	private void set_last_move_location(OctLoc moveLoc) {
		switch (moveLoc.get_element()) {
			case OctLoc.OCT_1:
			case OctLoc.OCT_2:
				// If the move is an octagon part, then set location A to it and clear
				// location B (since an octagon move is the only move)
				lastMoveLoc_A = moveLoc;
				lastMoveLoc_B = new OctLoc ();
				break;
			case OctLoc.SQUARE:
				// If lastMoveLoc_A is a square and lastMoveLoc_B is not a valid
				// location, then this square move must be the second square of a
				// single turn.
				// Otherwise, this square move must be the first move of a turn.
				if ((lastMoveLoc_A.get_element() == OctLoc.SQUARE) &&
					(lastMoveLoc_B.get_element() == OctLoc.NOWHERE)) {
					lastMoveLoc_B = moveLoc;
				} else {
					lastMoveLoc_A = moveLoc;
					lastMoveLoc_B = new OctLoc ();
				}
				break;
			default:
				// If the location is not a valid location, then wipe out the two last moves.
				lastMoveLoc_A = new OctLoc ();
				lastMoveLoc_B = new OctLoc ();
		}
	}

	/**
	 * Determine who has won the game
	 *
	 * @return          Who has won
	 *      PLAYER_NONE = no one has won yet.
	 *      PLAYER_ONE = Player 1 won.
	 *      PLAYER_TWO = Player 2 won.
	 */
	public int getWinner() {
		int i;

		// PLAYER_ONE has won if there is an island BOTTOM_ISLAND_ID in the top row
		for (i=0; i<ROWS; i++) {
			if ((i & 0x01) == 0)  {
				// left/right octagon, so check both halfs
				if ((board_island_id[i][0][OctLoc.OCT_1] == BOTTOM_ISLAND_ID) ||
					(board_island_id[i][0][OctLoc.OCT_2] == BOTTOM_ISLAND_ID)) {
					return PLAYER_ONE;
				}
			} else {
				// up/down octagon, so only check first half (the top one)
				if (board_island_id[i][0][OctLoc.OCT_1] == BOTTOM_ISLAND_ID) {
					return PLAYER_ONE;
				}
			}
		}

		// PLAYER_TWO has won if there is an island RIGHT_ISLAND_ID in the left column
		for (i=0; i<ROWS; i++) {
			if ((i & 0x01) == 0) {
				// left/right octagon, so only check first half (the left one)
				if (board_island_id[0][i][OctLoc.OCT_1] == RIGHT_ISLAND_ID) {
					return PLAYER_TWO;
				}
			} else {
				// up/down octagon, so check both halfs
				if ((board_island_id[0][i][OctLoc.OCT_1] == RIGHT_ISLAND_ID) ||
					(board_island_id[0][i][OctLoc.OCT_2] == RIGHT_ISLAND_ID)) {
					return PLAYER_TWO;
				}
			}
		}

		// If neither PLAYER_ONE nor PLAYER_TWO has won, then no one has yet.
		return (PLAYER_NONE);
	}


	/**
	 * Set the model state from the contents of the message.  This is used to
	 * decode the message sent from the server when attaching so that the
	 * client gets the current state of the game, even if attaching in the middle
	 * of a game.
	 *
	 * @param message    Message from the server
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {
		int i,j,e;

		// Reset the board back to the starting value before using the
		// message to fill it in.
		reset_game ();

		// Pull all of the bits out of the message
		Enumeration msgEnum = message.enumerateChildren();
		XMLElement msgEl;
		String elementName;

		while (msgEnum.hasMoreElements()) {
			msgEl = (XMLElement) msgEnum.nextElement();
			elementName = msgEl.getName();
			if (elementName.equals(XML_SPACE_NAME)) {
				i = msgEl.getIntAttribute(XML_ATT_I);
				j = msgEl.getIntAttribute(XML_ATT_J);
				e = msgEl.getIntAttribute(XML_ATT_ELEMENT);
				board_owner[i][j][e] = msgEl.getIntAttribute(XML_ATT_OWNER);
				board_island_id[i][j][e] = msgEl.getIntAttribute(XML_ATT_ISLAND_ID);
				if (e == OctLoc.SQUARE) {
					squares_unclaimed -= 1;
				}
			} else if (elementName.equals(XML_MISC_NAME)) {
				current_move_num = msgEl.getIntAttribute(XML_ATT_CURR_MOVE_NUM);
				current_turn_num = msgEl.getIntAttribute(XML_ATT_CURR_TURN_NUM);
				pie_rule_in_force = (msgEl.getIntAttribute(XML_ATT_PIE_RULE) == 1);
				already_played_square = (msgEl.getIntAttribute(XML_ATT_ALREADY_PLAYED_SQUARE) == 1);
			}
		}

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

	/**
	 * Used to bundle up the state of the model.  This is used so that when
	 * a client attaches, it gets the current state of the board from the
	 * server.  This allows an observer to attach to a game in progress and
	 * get the up-to-date values.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		XMLElement child;
		int i, j, e;

		// Scan the board looking for claimed spaces
		for (i=0; i<ROWS; i++) {
			for (j=0; j<ROWS; j++) {
				for (e=0; e<3; e++) {
					if ((board_owner[i][j][e] == PLAYER_ONE) ||
						(board_owner[i][j][e] == PLAYER_TWO)) {
						// Create a child element for this space
						child = new XMLElement(XML_SPACE_NAME);
						child.setIntAttribute(XML_ATT_I, i);
						child.setIntAttribute(XML_ATT_J, j);
						child.setIntAttribute(XML_ATT_ELEMENT, e);
						child.setIntAttribute(XML_ATT_OWNER, board_owner[i][j][e]);
						child.setIntAttribute(XML_ATT_ISLAND_ID, board_island_id[i][j][e]);

						// Add the child to the state structure
						state.addChild(child);
					}
				}
			}
		}

		// Add the element that indicates the misc. values
		child = new XMLElement(XML_MISC_NAME);
		child.setIntAttribute(XML_ATT_CURR_MOVE_NUM, current_move_num);
		child.setIntAttribute(XML_ATT_CURR_TURN_NUM, current_turn_num);
		child.setIntAttribute(XML_ATT_PIE_RULE, (pie_rule_in_force ? 1 : 0));
		child.setIntAttribute(XML_ATT_ALREADY_PLAYED_SQUARE, (already_played_square ? 1 : 0));
		state.addChild(child);

		return (state);
	}
}
