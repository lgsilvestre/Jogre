/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
 * Copyright (C) 2006  Richard Walter
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
package org.jogre.camelot.client;

import java.util.Vector;
import java.util.ListIterator;
import java.util.Enumeration;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;

import nanoxml.XMLElement;

// Model which holds the data for a game of Camelot
public class CamelotModel extends JogreModel {

	// Declare the size of the board (12 x 16)
	public static final int COLS = 12;
	public static final int ROWS = 16;

	// Declare the board elements
	private int [] [] board = new int [COLS][ROWS];
	private boolean [] [] validStart = new boolean [COLS][ROWS];
	private boolean foundImmediateCapture;

	// Define the values for use in the board[][] array
	public static final int MAN_FLAG = 1;
	public static final int KNIGHT_FLAG = 2;
	public static final int PLAYER_ONE_FLAG = 4;
	public static final int PLAYER_TWO_FLAG = 8;
	public static final int PLAYER_NEVER_FLAG = 0x100;
	public static final int PIECE_JUMP_ONLY_GHOST_FLAG = 0x200;
	public static final int PIECE_MUST_CAPTURE_GHOST_FLAG = 0x400;

	public static final int PLAYER_NEVER = PLAYER_NEVER_FLAG;
	public static final int PLAYER_NONE = 0;
	public static final int PLAYER_ONE_MAN = (PLAYER_ONE_FLAG | MAN_FLAG);
	public static final int PLAYER_TWO_MAN = (PLAYER_TWO_FLAG | MAN_FLAG);
	public static final int PLAYER_ONE_KNIGHT = (PLAYER_ONE_FLAG | KNIGHT_FLAG | MAN_FLAG);
	public static final int PLAYER_TWO_KNIGHT = (PLAYER_TWO_FLAG | KNIGHT_FLAG | MAN_FLAG);
	public static final int PIECE_JUMP_ONLY_GHOST = (PIECE_JUMP_ONLY_GHOST_FLAG);
	public static final int PIECE_MUST_CAPTURE_GHOST = (PIECE_MUST_CAPTURE_GHOST_FLAG);
	public static final int PIECE_GHOST = (PIECE_JUMP_ONLY_GHOST_FLAG | PIECE_MUST_CAPTURE_GHOST_FLAG);

	// Definitions for direction
	public static final int NORTH = 0;
	public static final int NE = 1;
	public static final int EAST = 2;
	public static final int SE = 3;
	public static final int SOUTH = 4;
	public static final int SW = 5;
	public static final int WEST = 6;
	public static final int NW = 7;

	public static final int [] i_off = {0, 1, 1, 1, 0, -1, -1, -1};
	public static final int [] j_off = {-1, -1, 0, 1, 1, 1, 0, -1};

	// Keep track of the # of pieces each player has on the board.
	// (This makes it faster to determine if a player has won by capturing
	//  all of the opponent's pieces without having to scan the board)
	private int [] pieces_on_board = {14, 14};

	// Keep track of the # of castle moves each player has left.
	private int [] castle_moves_left = {2, 2};

	// XML attributes used for sending/receiving board state
	private static final String XML_PIECE_NAME = "piece";
	private static final String XML_ATT_PIECE_TYPE = "type";
	private static final String XML_ATT_I = "i";
	private static final String XML_ATT_J = "j";

	private static final String XML_CM_NAME = "castle_moves";
	private static final String XML_ATT_PLAYER_0 = "p0";
	private static final String XML_ATT_PLAYER_1 = "p1";

	/**
	 * Constructor for the model
	 */
	public CamelotModel() {
		super();
		resetGame ();
	}


	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame () {
		int i,j;

		clearBoard();

		// Place the initial pieces on the board
		board[3] [9] = PLAYER_ONE_KNIGHT;	board[3][6] = PLAYER_TWO_KNIGHT;
		board[4] [9] = PLAYER_ONE_MAN;		board[4][6] = PLAYER_TWO_MAN;
		board[5] [9] = PLAYER_ONE_MAN;		board[5][6] = PLAYER_TWO_MAN;
		board[6] [9] = PLAYER_ONE_MAN;		board[6][6] = PLAYER_TWO_MAN;
		board[7] [9] = PLAYER_ONE_MAN;		board[7][6] = PLAYER_TWO_MAN;
		board[8] [9] = PLAYER_ONE_KNIGHT;	board[8][6] = PLAYER_TWO_KNIGHT;
		board[2][10] = PLAYER_ONE_KNIGHT;	board[2][5] = PLAYER_TWO_KNIGHT;
		board[3][10] = PLAYER_ONE_MAN;		board[3][5] = PLAYER_TWO_MAN;
		board[4][10] = PLAYER_ONE_MAN;		board[4][5] = PLAYER_TWO_MAN;
		board[5][10] = PLAYER_ONE_MAN;		board[5][5] = PLAYER_TWO_MAN;
		board[6][10] = PLAYER_ONE_MAN;		board[6][5] = PLAYER_TWO_MAN;
		board[7][10] = PLAYER_ONE_MAN;		board[7][5] = PLAYER_TWO_MAN;
		board[8][10] = PLAYER_ONE_MAN;		board[8][5] = PLAYER_TWO_MAN;
		board[9][10] = PLAYER_ONE_KNIGHT;	board[9][5] = PLAYER_TWO_KNIGHT;

		// Reset misc. state values
		pieces_on_board[0] = 14;
		pieces_on_board[1] = 14;
		castle_moves_left[0] = 2;
		castle_moves_left[1] = 2;

		// Update the start array for player 0.
		updateValidStart(0);

		refreshObservers();
	}

	/**
	 * Clear the board so that there are no pieces on it.
	 */
	private void clearBoard() {
		int i,j;

		// Clear the board back to empty state
		for (i=0; i<COLS; i++) {
			for (j=0; j<ROWS; j++) {
				board[i][j] = PLAYER_NONE;
				validStart[i][j] = false;
			}
		}

		// Mask off the spaces that aren't playable
		board [0][0] = PLAYER_NEVER;		board [0][13] = PLAYER_NEVER;
		board [1][0] = PLAYER_NEVER;		board[11][13] = PLAYER_NEVER;
		board [2][0] = PLAYER_NEVER;		board [0][14] = PLAYER_NEVER;
		board [3][0] = PLAYER_NEVER;		board [1][14] = PLAYER_NEVER;
		board [4][0] = PLAYER_NEVER;		board[10][14] = PLAYER_NEVER;
		board [7][0] = PLAYER_NEVER;		board[11][14] = PLAYER_NEVER;
		board [8][0] = PLAYER_NEVER;		board [0][15] = PLAYER_NEVER;
		board [9][0] = PLAYER_NEVER;		board [1][15] = PLAYER_NEVER;
		board[10][0] = PLAYER_NEVER;		board [2][15] = PLAYER_NEVER;
		board[11][0] = PLAYER_NEVER;		board [3][15] = PLAYER_NEVER;
		board [0][1] = PLAYER_NEVER;		board [4][15] = PLAYER_NEVER;
		board [1][1] = PLAYER_NEVER;		board [7][15] = PLAYER_NEVER;
		board[10][1] = PLAYER_NEVER;		board [8][15] = PLAYER_NEVER;
		board[11][1] = PLAYER_NEVER;		board [9][15] = PLAYER_NEVER;
		board [0][2] = PLAYER_NEVER;		board[10][15] = PLAYER_NEVER;
		board[11][2] = PLAYER_NEVER;		board[11][15] = PLAYER_NEVER;

		pieces_on_board[0] = 0;
		pieces_on_board[1] = 0;
	}


	/**
	 * Retrieve the piece on a space on the board
	 *
	 * @param (i, j)			Location of board space
	 * @return piece on the space
	 */
	public int getPieceAt(int i, int j) {
		if ((i < 0) || (i >= COLS) || (j < 0) || (j >= ROWS)) {
			return (PLAYER_NEVER);
		} else {
			return (board[i][j]);
		}
	}

	/**
	 * Retrieve the piece on a space on the board
	 *
	 * @param theLoc			Location
	 * @return piece on the space
	 */
	public int getPieceAt(CamelotLoc theLoc) {
		return (getPieceAt(theLoc.get_i(), theLoc.get_j()));
	}

	/**
	 * Set a piece on a space on the board
	 *
	 * @param theLoc			Location
	 * @param code				The piece code to put on the location
	 */
	public void setPieceAt(int i, int j, int code) {
		if ((i >= 0) && (i < COLS) && (j >= 0) || (j < ROWS)) {
			board[i][j] = code;
		}
	}

	/**
	 * Set a piece on a space on the board
	 *
	 * @param theLoc			Location
	 * @param code				The piece code to put on the location
	 */
	public void setPieceAt(CamelotLoc theLoc, int code) {
		setPieceAt(theLoc.get_i(), theLoc.get_j(), code);
	}

	/**
	 * Get the # of castle moves left for the given player.
	 *
	 * @param player_seat    The player whose castle moves left are desired.
	 * @return the number of castle moves left.
	 */
	public int getCastleMovesLeft(int player_seat) {
		return castle_moves_left[player_seat];
	}

	/**
	 * Convenience functions for determining what the codes for things on the
	 * board are.
	 *
	 * @param code				Code for a board piece (returned from getPieceAt() )
	 * @return true/false determination of the code.
	 */
	public boolean isPartOfBoard(int code) {
		return (code != PLAYER_NEVER);
	}
	public boolean isEmpty(int code) {
		return (code == PLAYER_NONE);
	}
	public boolean isPiece(int code) {
		return (code != PLAYER_NONE);
	}
	public boolean isMan(int code) {
		return ((code & MAN_FLAG) != 0);
	}
	public boolean isKnight(int code) {
		return ((code & KNIGHT_FLAG) != 0);
	}
	public boolean isPlayerOne(int code) {
		return ((code & PLAYER_ONE_FLAG) != 0);
	}
	public boolean isPlayerTwo(int code) {
		return ((code & PLAYER_TWO_FLAG) != 0);
	}
	public boolean isPlayer(int code, int player_seat) {
		if (player_seat == 0) {
			return (isPlayerOne(code));
		} else if (player_seat == 1) {
			return (isPlayerTwo(code));
		}
		return (false);
	}
	public boolean samePlayer(int code1, int code2) {
		return ( (code1 & code2 & (PLAYER_ONE_FLAG | PLAYER_TWO_FLAG)) != 0 );
	}
	public int pieceToPlayer(int code) {
		return (isPlayerOne(code) ? 0 : 1);
	}

	/**
	 * Determine if the given location is a valid starting space for a move.
	 *
	 * @param	theLoc			The location to be tested
	 * @return		true = the player can start here.
	 * @return		false = the player cannot start here.
	 */
	public boolean validStartLocation(CamelotLoc theLoc) {
		int piece_code = getPieceAt(theLoc);

		// Can't move from here if it's not on the board
		if (isPartOfBoard(piece_code) == false) {
			return (false);
		}

		return (validStart[theLoc.get_i()][theLoc.get_j()]);
	}

	/**
	 * Determine if the the given player has a piece in his own castle or not
	 *
	 * @param	player_seat		The seat # for the player (0 or 1)
	 * @return true/false
	 */
	private boolean hasPieceInOwnCastle(int player_seat) {
		if (player_seat == 0) {
			return (isPlayerOne(getPieceAt(5,15)) || isPlayerOne(getPieceAt(6,15)));
		} else {
			return (isPlayerTwo(getPieceAt(5,0)) || isPlayerTwo(getPieceAt(5,0)));
		}
	}

	/**
	 * Set the validStart array to all the given state
	 *
	 * @param	state			The state to set the array to
	 */
	private void setAllValidStart(boolean state) {
		int i,j;

		for (i=0; i<COLS; i++) {
			for (j=0; j<ROWS; j++) {
				validStart[i][j] = state;
			}
		}
	}

	/**
	 * Remove all of the ghost flags from the board
	 *
	 * @param	state			The state to set the array to
	 */
	private void removeGhosts() {
		int i,j;

		for (i=0; i<COLS; i++) {
			for (j=0; j<ROWS; j++) {
				board[i][j] &= ~(PIECE_GHOST);
			}
		}
	}


	/**
	 * Update the array that keeps track of which spaces are valid starts
	 * for the given player.
	 *
	 * @param	player_seat		The ID of the player who would be moving
	 * @return	the end-game code if the given player has no valid moves.
	 *				PLAYER_NONE = no one has won yet.
	 *				PLAYER_ONE_FLAG = Player 1 won. (player 2 has no valid moves)
	 *				PLAYER_TWO_FLAG = Player 2 won. (player 1 has no valid moves)
	 */
	public int updateValidStart(int player_seat) {
		boolean has_start = false;
		foundImmediateCapture = false;

		// If a player has a piece in his own castle, then the only
		// valid start is that piece.
		if (hasPieceInOwnCastle(player_seat)) {

			// Set all spaces invalid...
			setAllValidStart(false);

			// ...except for the one that has the piece in his own castle
			if (player_seat == 0) {
				if (isPlayerOne(getPieceAt(5,15))) {checkStart(5, 15, false);}
				if (isPlayerOne(getPieceAt(6,15))) {checkStart(6, 15, false);}
				has_start = (validStart[5][15] | validStart[6][15]);
			} else {
				if (isPlayerTwo(getPieceAt(5,0))) {checkStart(5, 0, false);}
				if (isPlayerTwo(getPieceAt(6,0))) {checkStart(6, 0, false);}
				has_start = (validStart[5][0] | validStart[6][0]);
			}

			// And we're done, so check if the player can't move out of his castle
			if (has_start == false ) {
				return ((player_seat == 0) ? PLAYER_TWO_FLAG : PLAYER_ONE_FLAG);
			}
			return (PLAYER_NONE);
		}

		// If the player doesn't have a piece in his own castle, then potentially
		// any of his pieces are valid starts.  However, if a player has at least
		// one immediate capture, then he must do a capture.  If a player doesn't
		// have an immediate capture, then he can select from any valid move.
		int i,j;
		int piece_code;

		// ***NOTE: this search could be made more efficient by storing the locations
		// of "my" men & knights in vectors and just iterating over the vector rather
		// than searching the board...

		// First, check all of the men.  Since men can only either jump or capture,
		// but not both, by checking the men first we will know whether the knights
		// must make a capture or not when checking them.
		for (i=0; i<COLS; i++) {
			for (j=1; j<(ROWS-1); j++) {
				piece_code = getPieceAt(i,j);
				if ((isPlayer(piece_code, player_seat)) &&
					(isMan(piece_code))) {
					// This is our man to move.
					foundImmediateCapture = checkStart(i, j, foundImmediateCapture);
					has_start |= validStart[i][j];
				} else {
					// This isn't our piece to move
					validStart[i][j] = false;
				}
			}
		}

		// Now, check the knights.
		for (i=0; i<COLS; i++) {
			for (j=1; j<(ROWS-1); j++) {
				piece_code = getPieceAt(i,j);
				if ((isPlayer(piece_code, player_seat)) &&
					(isKnight(piece_code))) {
					// This is our knght to move.
					foundImmediateCapture = checkStart(i, j, foundImmediateCapture);
					has_start |= validStart[i][j];
				}
			}
		}

		// Special case check if the player has a piece in the other player's
		// castle.  If so, that piece can only move within the castle.

		validStart[5][0] = false;
		validStart[6][0] = false;
		validStart[5][15] = false;
		validStart[6][15] = false;
		if (castle_moves_left[player_seat] > 0) {
			if (player_seat == 0) {
				validStart[5][0] = ((isPlayerOne(getPieceAt(5,0))) && (isEmpty(getPieceAt(6,0))));
				validStart[6][0] = ((isPlayerOne(getPieceAt(6,0))) && (isEmpty(getPieceAt(5,0))));
				has_start |= (validStart[5][0] | validStart[6][0]);
			} else {
				validStart[5][15] = ((isPlayerTwo(getPieceAt(5,15))) && (isEmpty(getPieceAt(6,15))));
				validStart[6][15] = ((isPlayerTwo(getPieceAt(6,15))) && (isEmpty(getPieceAt(5,15))));
				has_start |= (validStart[5][15] | validStart[6][15]);
			}
		}

		// If there weren't any starts, then this player has lost
		if (has_start == false) {
			return ((player_seat == 0) ? PLAYER_TWO_FLAG : PLAYER_ONE_FLAG);
		}
		return (PLAYER_NONE);
	}

	/**
	 * Determine if the piece at (i,j) can move.  <mustCapture> is a flag
	 * that indicates if another piece has a capturing move.  If it is
	 * true, then this piece can only move if it can make a capture.
	 * if it is false, then this piece can make any move.
	 *
	 * This will set the value of the validStart[i][j] array to the value
	 * that reflects if the piece can move.
	 *
	 * @param	(i,j)			Location of the piece
	 * @param	mustCapture		Indicates if the piece must make a capture
	 * @return			new value of mustCapture.
	 */
	private boolean checkStart(int i, int j, boolean mustCapture) {
		int piece_id = getPieceAt(i,j);

		if (mustCapture) {
			// This piece must make a capture to be a valid move.
			if (isKnight(piece_id)) {
				// The piece is a knight, and he might be able to jump around
				// before capturing, so we need to search his move tree to see if
				// there are any captures that can be made
				validStart[i][j] = addMoveTreeForJumps(
						i, j,
						(isPlayerOne(piece_id) ? 0 : 1),
						true, true, true,
						new Vector (),
						new Vector ());
				removeGhosts();
			} else {
				// The piece is a man, so he can only capture if he does
				// so immediately.
				validStart[i][j] = canImmCapture(i,j);
			}

			// We keep the mustCapture flag true
			return (true);
		} else {
			// This piece is not obligated to make a capture, so we only
			// need to find at least one initial move that this can make
			// to have it be a valid move.
			if (canImmCapture(i, j)) {
				// This piece does have an immediate capture, and it is
				// the first to have so (because <mustCapture> is false).
				// So, wipe out all prior moves that may be valid (because
				// they are not), set this move to valid, and return true
				// so that we are limited to capture moves for future testing.
				setAllValidStart(false);
				validStart[i][j] = true;
				return (true);
			} else if (canWalk(i,j) || canJump(i,j)) {
				// This piece can move, but we still aren't forced to capture
				validStart[i][j] = true;
				return (false);
			} else {
				// This piece can't move
				validStart[i][j] = false;
				return (false);
			}
		}
	}

	/**
	 * Determine if the piece at (i,j) has an immediate capture or not
	 *
	 * @param	(i,j)			Location of the piece
	 * @return		if the piece has an immediate capture or not
	 */
	private boolean canImmCapture(int i, int j) {
		int direction;
		int other_player = (isPlayerOne(getPieceAt(i,j)) ? 1 : 0);

		for (direction = NORTH; direction <= NW; direction++) {
			if ((isPlayer(getPieceAt(i + i_off[direction], j + j_off[direction]), other_player)) &&
				(isEmpty(getPieceAt(i + (2*i_off[direction]), j + (2*j_off[direction]))))) {
				return (true);
			}
		}
		return (false);
	}

	/**
	 * Determine if the piece at (i,j) can make a walking move
	 *
	 * @param	(i,j)			Location of the piece
	 * @return		if the piece can walk
	 */
	private boolean canWalk(int i, int j) {
		int direction;

		for (direction = NORTH; direction <= NW; direction++) {
			if (isEmpty(getPieceAt(i + i_off[direction], j + j_off[direction]))) {
				return (true);
			}
		}
		return (false);
	}

	/**
	 * Determine if the piece at (i,j) can make a jumping move
	 *
	 * @param	(i,j)			Location of the piece
	 * @return		if the piece can jump
	 */
	private boolean canJump(int i, int j) {
		int direction;

		for (direction = NORTH; direction <= NW; direction++) {
			if ((isPiece(getPieceAt(i + i_off[direction], j + j_off[direction]))) &&
				(isEmpty(getPieceAt(i + (2*i_off[direction]), j + (2*j_off[direction]) )))) {
				return (true);
			}
		}
		return (false);
	}

	/**
	 * This routine will make the given step on the board.
	 *
	 * @param	theStep			The step to make
	 * @returns		true  => The step was made successfully.
	 *				false => The step was illegal.
	 */
	private boolean makeStep (CamelotStep theStep) {
		CamelotLoc fromLoc = theStep.get_from();
		CamelotLoc toLoc = theStep.get_to();
		int from_piece = getPieceAt(fromLoc);
		int to_piece = getPieceAt(toLoc);

		switch (theStep.get_type()) {
			case CamelotStep.WALK:
				// Simple sanity check
				if ((isPiece(from_piece) == false) ||
					(isEmpty(to_piece) == false)) {
					return (false);
				}

				// Check for castle moves
				if (theStep.get_captured_piece() != PLAYER_NONE) {
					castle_moves_left[pieceToPlayer(from_piece)] -= 1;
				}
				break;

			case CamelotStep.JUMP:
				int jump_i = fromLoc.get_i() + i_off[theStep.get_direction()];
				int jump_j = fromLoc.get_j() + j_off[theStep.get_direction()];
				int jump_piece = getPieceAt(jump_i, jump_j);

				// Simple sanity check
				if ((isPiece(from_piece) == false) ||
					(isEmpty(to_piece) == false) ||
					(isPiece(jump_piece) == false) ||
					(samePlayer(from_piece, jump_piece) == false)) {
					return (false);
				}
				break;

			case CamelotStep.CAPTURE:
				int capture_i = fromLoc.get_i() + i_off[theStep.get_direction()];
				int capture_j = fromLoc.get_j() + j_off[theStep.get_direction()];
				int capture_piece = getPieceAt(capture_i, capture_j);

				// Simple sanity check
				if ((isPiece(from_piece) == false) ||
					(isEmpty(to_piece) == false) ||
					(isPiece(capture_piece) == false) ||
					(samePlayer(from_piece, capture_piece) == true)) {
					return (false);
				}

				// Remove the captured piece from the board
				board[capture_i][capture_j] = PLAYER_NONE;
				pieces_on_board[pieceToPlayer(capture_piece)] -= 1;
				break;

			default:
				System.out.println("Error: makeStep() with unknown type of " + theStep.get_type());
				return (false);
		}

		// Move the piece
		setPieceAt(fromLoc, PLAYER_NONE);
		setPieceAt(toLoc, from_piece);
		return (true);
	}


	/**
	 * This routine will make the given move on the board.
	 * Note: This only checks the validity of each step.  It does *NOT* check the
	 * validity of the move as a whole.  We probably ought to do that...
	 *
	 * @param	theMove			The move to make
	 * @returns		true  => The move was made successfully.
	 *				false => The move was illegal.
	 */
	public boolean makeMove (Vector theMove) {
		ListIterator iter = theMove.listIterator();
		while (iter.hasNext()) {
			if (makeStep((CamelotStep) iter.next()) == false) {
				// We've hit an illegal step
				return (false);
			}
		}
		return (true);
	}

	/**
	 * This routine will un-make the given step on the board.
	 *
	 * @param	theStep			The step to un-make
	 * @returns		true  => The step was un-made successfully.
	 *				false => The step was illegal.
	 */
	private boolean unmakeStep (CamelotStep theStep) {
		CamelotLoc fromLoc = theStep.get_from();
		CamelotLoc toLoc = theStep.get_to();
		int from_piece = getPieceAt(fromLoc);
		int to_piece = getPieceAt(toLoc);

		switch (theStep.get_type()) {
			case CamelotStep.WALK:
				// Simple sanity check
				if ((isPiece(to_piece) == false) ||
					(isEmpty(from_piece) == false)) {
					return (false);
				}

				// Check for castle moves
				if (theStep.get_captured_piece() != PLAYER_NONE) {
					castle_moves_left[pieceToPlayer(to_piece)] += 1;
				}
				break;

			case CamelotStep.JUMP:
				int jump_i = fromLoc.get_i() + i_off[theStep.get_direction()];
				int jump_j = fromLoc.get_j() + j_off[theStep.get_direction()];
				int jump_piece = getPieceAt(jump_i, jump_j);

				// Simple sanity check
				if ((isPiece(to_piece) == false) ||
					(isEmpty(from_piece) == false) ||
					(isPiece(jump_piece) == false) ||
					(samePlayer(to_piece, jump_piece) == false)) {
					return (false);
				}
				break;

			case CamelotStep.CAPTURE:
				int capture_i = fromLoc.get_i() + i_off[theStep.get_direction()];
				int capture_j = fromLoc.get_j() + j_off[theStep.get_direction()];
				int capture_piece = theStep.get_captured_piece();

				// Simple sanity check
				if ((isPiece(to_piece) == false) ||
					(isEmpty(from_piece) == false) ||
					(isEmpty(getPieceAt(capture_i, capture_j)) == false) ||
					(samePlayer(to_piece, capture_piece) == true)) {
					return (false);
				}

				// Add the captured piece to the board
				board[capture_i][capture_j] = capture_piece;
				pieces_on_board[pieceToPlayer(capture_piece)] += 1;
				break;

			default:
				System.out.println("Error: unmakeStep() with unknown type of " + theStep.get_type());
				return (false);
		}

		// Move the piece back
		setPieceAt(fromLoc, to_piece);
		setPieceAt(toLoc, PLAYER_NONE);
		return (true);
	}

	/**
	 * This routine will un-make the given move on the board.
	 * Note: This only checks the validity of each step.  It does *NOT* check the
	 * validity of the move as a whole.  We probably ought to do that...
	 *
	 * @param	theMove			The move to un-make
	 * @returns		true  => The move was made successfully.
	 *				false => The move was illegal.
	 */
	public boolean unmakeMove (Vector theMove) {
		// We have to un-make the move from the end forward toward the front
		ListIterator iter = theMove.listIterator(theMove.size());
		while (iter.hasPrevious()) {
			if (unmakeStep((CamelotStep) iter.previous()) == false) {
				// We've hit an illegal step
				return (false);
			}
		}
		return (true);
	}

	/**
	 * This routine finds the moveFamily in the moveTree that ends at the
	 * given endLoc.  If the family doesn't exist, then this will create
	 * a new family and return that new one.
	 *
	 * @param	(i,j)			Location to move from
	 * @param	player_seat		The player making the move
	 * @param	move			The moves made that got us to (i,j)
	 * @param	moveTree		The list of all moves made so far
	 */
	private CamelotMoveFamily findFamily (Vector moveTree, CamelotLoc endLoc, boolean createFlag) {
		ListIterator iter = moveTree.listIterator();
		CamelotMoveFamily fam;

		// Search the moveTree for a family that ends at <endLoc>
		while (iter.hasNext()) {
			fam = (CamelotMoveFamily) iter.next();
			if (fam.endsAt(endLoc)) {
				return (fam);
			}
		}

		// Didn't find it.

		if (createFlag) {
			// Create an empty family, add it to the moveTree and return that.
			fam = new CamelotMoveFamily(endLoc);
			moveTree.add(fam);

			return (fam);
		} else {
			// Don't want to create a new one
			return (null);
		}
	}

	/**
	 * This routine will add all captures that start at (i, j) for <player_seat> to the
	 *	move tree.  It will return a boolean that indicates if any captures were added.
	 *
	 * @param	(i,j)			Location to move from
	 * @param	player_seat		The player making the move
	 * @param	move			The moves made that got us to (i,j)
	 * @param	moveTree		The list of all moves made so far
	 */
	private boolean addMoveTreeForCaptures(int i, int j, int player_seat, Vector move, Vector moveTree) {
		int direction;
		int other_player = ((player_seat == 0) ? 1 : 0);
		int new_i, new_j, captured_i, captured_j;
		int captured_piece;
		int land_space;
		CamelotLoc newLoc;
		boolean captured = false;

		for (direction = NORTH; direction <= NW; direction++) {
			// Calculate the spaces that are captured and landed on
			captured_i = i + i_off[direction];
			captured_j = j + j_off[direction];
			new_i = i + (2*i_off[direction]);
			new_j = j + (2*j_off[direction]);

			// During captures, ignore the ghost pieces
			land_space = getPieceAt(new_i, new_j) & ~(PIECE_GHOST);

			if ((isPlayer(getPieceAt(captured_i, captured_j), other_player)) &&
				(isEmpty(land_space))) {
					// We can capture a piece and land on (new_i, new_j)

					// Remove the captured piece from the board (but remember it)
					captured_piece = board[captured_i][captured_j];
					board[captured_i][captured_j] = PLAYER_NONE;

					// Add this location to the current move
					newLoc = new CamelotLoc(new_i, new_j);
					move.add(new CamelotStep(
							new CamelotLoc(i, j),		// from
							newLoc,						// to
							CamelotStep.CAPTURE,		// type
							direction,					// direction
							captured_piece));			// captured_piece

					// Recurse to see if there are more captures to be done from the new spot
					if (addMoveTreeForCaptures(new_i, new_j, player_seat, move, moveTree) == false) {
						// The jump to (new_i, move_j) did not result in more captures, so we can
						// save it by adding it to the right family in the moveTree.
						findFamily(moveTree, newLoc, true).addMove(move);
					}

					// Remove this step from the move
					move.removeElementAt(move.size() - 1);

					// Restore the captured piece back to the board
					board[captured_i][captured_j] = captured_piece;

					captured = true;
			}
		}

		return (captured);
	}

	/**
	 * This routine will determine if it is ok to jump on a piece of the given type
	 *
	 * @param	tgt_piece		The type of space that we're going to jump on
	 * @param	mustCapture		If the jump piece must capture (because there is a capture
	 *								upstream already)
	 * @return		if this space is ok to jump onto.
	 */
	private boolean canJumpOn(int tgt_piece, boolean mustCapture) {
		if (mustCapture) {
			// If I must capture, then only truly empty spaces are ok to jump on
			return (isEmpty(tgt_piece));
		} else {
			// If I don't have to capture, then I can jump on empty spaces or
			// spaces that have must capture ghosts (resulting in them being upgraded
			// to jump-only ghosts
			return (isEmpty(tgt_piece) || (tgt_piece == PIECE_MUST_CAPTURE_GHOST));
		}
	}

	/**
	 * This routine will add all jumps & captures that start at (i, j) for <player_seat> to the
	 *	move tree.
	 *
	 * @param	(i,j)			Location to move from
	 * @param	player_seat		The player making the move
	 * @param	firstStep		If this is the first step for a piece.
	 *								true  => Non-knight can capture
	 *								false => Only knights can capture
	 * @param	knightPiece		If the piece that is moving is a knight.
	 *								true  => can jump and then capture
	 *								false => can jump only
	 * @param	mustCapture		If this piece must capture (because there is a capture
	 *								upstream already)
	 * @param	move			The moves made that got us to (i,j)
	 * @param	moveTree		The list of all moves found so far
	 *
	 * @return		if there were any captures down-stream
	 */
	private boolean addMoveTreeForJumps(
		int i, int j,
		int player_seat,
		boolean firstStep,
		boolean knightPiece,
		boolean mustCapture,
		Vector move,
		Vector moveTree
	) {
		int direction;
		int other_player = ((player_seat == 0) ? 1 : 0);
		int new_i, new_j, jumped_i, jumped_j;
		CamelotLoc newLoc;
		boolean imm_captures = false;
		boolean down_stream_captures = false;

		// Put a ghost piece on the space where we are
		board[i][j] |= (mustCapture ? PIECE_MUST_CAPTURE_GHOST_FLAG : PIECE_JUMP_ONLY_GHOST_FLAG);

		if ( knightPiece || firstStep) {
			// Knights can capture after jumping, so check for captures at this location
			// And, if there is an immediate capture from here, then any steps after here
			// must also capture.
			imm_captures = addMoveTreeForCaptures(i, j, player_seat, move, moveTree);
			mustCapture |= imm_captures;
		}

		// Now, look in all 8 directions for jumps
		for (direction = NORTH; direction <= NW; direction++) {
			jumped_i = i + i_off[direction];
			jumped_j = j + j_off[direction];
			new_i = i + (2*i_off[direction]);
			new_j = j + (2*j_off[direction]);

			// Can we jump over a friendly piece ?
			if ( (isPlayer(getPieceAt(jumped_i, jumped_j), player_seat)) &&
				 (canJumpOn(getPieceAt(new_i, new_j), mustCapture)) ) {

				// Add the jump to (new_i, new_j) to the current move
				newLoc = new CamelotLoc(new_i, new_j);
				move.add(new CamelotStep(
						new CamelotLoc(i, j),		// from
						newLoc,						// to
						CamelotStep.JUMP,			// type
						direction));				// direction

				// Recurse to see if there are more jumps/captures to be done from the new spot
				down_stream_captures |= addMoveTreeForJumps(
					new_i, new_j, player_seat, false, knightPiece, mustCapture,
					move, moveTree);

				// Take the jump off of the current move
				move.removeElementAt(move.size() - 1);
			}
		}

		// If there are no immediate captures from this space, and I'm not forced to
		// capture, then the move to this space is a valid move and should be added
		if ((imm_captures == false) && (mustCapture == false) && (move.size() != 0)) {
			// Add the move to the right family in the moveTree
			newLoc = new CamelotLoc(i, j);
			findFamily(moveTree, newLoc, true).addMove(move);
		}

		// Return a the code that indicates if we've capture anyone downstream from here
		return (imm_captures || down_stream_captures);
	}

	/**
	 * This routine will add all walks that start at (i, j) to the move tree.
	 *
	 * @param	fromLocation		Location to walk from
	 * @param	moveTree		The list of all moves found so far
	 */
	private void addMoveTreeForWalks(
		CamelotLoc fromLocation,
		Vector moveTree
	) {
		int direction;
		int new_i, new_j;
		CamelotLoc newLoc;
		Vector move = new Vector();
		int i = fromLocation.get_i();
		int j = fromLocation.get_j();

		// Now, look in all 8 directions for walks
		for (direction = NORTH; direction <= NW; direction++) {
			new_i = i + i_off[direction];
			new_j = j + j_off[direction];
			if (isEmpty(getPieceAt(new_i, new_j))) {
				// Create a new move with only this step;
				newLoc = new CamelotLoc(new_i, new_j);
				move.add(new CamelotStep(
						fromLocation,			// from
						newLoc,					// to
						CamelotStep.WALK,		// type
						direction));			// direction

				// Add the move to the move tree
				findFamily(moveTree, newLoc, true).addMove(move);

				// Remove the step from the move
				move.removeElementAt(0);
			}
		}
	}

	/**
	 * This routine will build a MoveTree for all of the valid moves that
	 * start at the given location.
	 *
	 * @param	startingLocation		Location to start from
	 * @returns		A move tree with all of the legal moves.
	 */
	public Vector buildMoveTree(CamelotLoc startingLocation) {
		Vector currMove = new Vector();
		Vector moveTree = new Vector();
		int i = startingLocation.get_i();
		int j = startingLocation.get_j();
		int piece = getPieceAt(i,j);
		int player_seat = (isPlayerOne(piece) ? 0 : 1);
////		System.out.println("Building move tree for ("+i+","+j+")");

		// If this is a piece within the opponent's castle, then the
		// only valid move is a walk to the other space in the castle
		if (((j == 0) && (isPlayerOne(piece))) ||
			((j == (ROWS-1)) && (isPlayerTwo(piece))) ) {
				CamelotLoc newLoc = new CamelotLoc (((i == 5) ? 6 : 5), j);
				currMove.add(new CamelotStep(
						startingLocation,				// from
						newLoc,							// to
						CamelotStep.WALK,				// type
						((i == 5) ? EAST : WEST),		// direction
						PIECE_GHOST));					// captured_piece

				// Add the move to the move tree
				findFamily(moveTree, newLoc, true).addMove(currMove);

				return (moveTree);
		}

		// Set ghosts in the player's own castle so that he can't walk/jump into
		// them (but they can still capture into them)
		if (player_seat == 0) {
			board[5][15] |= PIECE_GHOST;
			board[6][15] |= PIECE_GHOST;
		} else {
			board[5][0] |= PIECE_GHOST;
			board[6][0] |= PIECE_GHOST;
		}

		// Remove the piece that will be moving from the board so that it doesn't
		// get in it's own way.
		setPieceAt(i, j, PLAYER_NONE);

		if (foundImmediateCapture) {
			// If there is an immediate capture somewhere, then all moves
			// must capture.  (And, by definition, it cannot walk)
			addMoveTreeForJumps(i, j, player_seat, true, isKnight(piece), true, currMove, moveTree);
		} else {
			// If there aren't any immediate captures, then it can walk
			// in addition to jumps/captures.
			addMoveTreeForWalks(startingLocation, moveTree);
			addMoveTreeForJumps(i, j, player_seat, true, isKnight(piece), false, currMove, moveTree);
		}

		// Put the moving piece back.
		setPieceAt(i, j, piece);

		// Remove the ghosts
		removeGhosts();

		return (moveTree);
	}

	/**
	 * Determine who has won the game
	 *
	 * @return				Who has won
	 *		PLAYER_NONE = no one has won yet.
	 *		PLAYER_ONE_FLAG = Player 1 won.
	 *		PLAYER_TWO_FLAG = Player 2 won.
	 *		PLAYER_NEVER_FLAG = Draw.
	 */
	public int getWinner() {

		// First, check if one player has taken all of the pieces of the other player.
		if (pieces_on_board[0] == 0) {
			return ((pieces_on_board[1] < 2) ? PLAYER_NEVER_FLAG : PLAYER_TWO_FLAG);
		}
		if (pieces_on_board[1] == 0) {
			return ((pieces_on_board[0] < 2) ? PLAYER_NEVER_FLAG : PLAYER_ONE_FLAG);
		}

		// Check if both players only have 1 piece left, then it's a draw
		if ((pieces_on_board[0] == 1) && (pieces_on_board[1] == 1)) {
			return (PLAYER_NEVER_FLAG);
		}

		// Check if one player has moved two pieces into the opponent's castle
		if (isPlayerOne(getPieceAt(5,0)) && isPlayerOne(getPieceAt(6,0))) {
			return (PLAYER_ONE_FLAG);
		}
		if (isPlayerTwo(getPieceAt(5,15)) && isPlayerTwo(getPieceAt(6,15))) {
			return (PLAYER_TWO_FLAG);
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
	 * @param message    Data stored in message.
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {
		int type;

		// Clear all of the pieces off of the board
		clearBoard();

		// Pull all of the pieces out of the message
		Enumeration msgEnum = message.enumerateChildren();
		XMLElement msgEl;
		String elementName;
		while (msgEnum.hasMoreElements()) {
			msgEl = (XMLElement) msgEnum.nextElement();
			elementName = msgEl.getName();
			if (elementName.equals(XML_PIECE_NAME)) {
				type = msgEl.getIntAttribute(XML_ATT_PIECE_TYPE);
				setPieceAt(
					msgEl.getIntAttribute(XML_ATT_I),
					msgEl.getIntAttribute(XML_ATT_J),
					type);
				pieces_on_board[pieceToPlayer(type)] += 1;
			} else if (elementName.equals(XML_CM_NAME)) {
				castle_moves_left[0] = msgEl.getIntAttribute(XML_ATT_PLAYER_0);
				castle_moves_left[1] = msgEl.getIntAttribute(XML_ATT_PLAYER_1);
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
		int i, j, type;

		// Scan the board looking for pieces.
		for (i=0; i<CamelotModel.COLS; i++) {
			for (j=0; j<CamelotModel.ROWS; j++) {
				type = getPieceAt(i,j);
				if (isPlayerOne(type) || isPlayerTwo(type)) {
					// Create a child element for this piece
					child = new XMLElement(XML_PIECE_NAME);
					child.setIntAttribute(XML_ATT_I, i);
					child.setIntAttribute(XML_ATT_J, j);
					child.setIntAttribute(XML_ATT_PIECE_TYPE, type);

					// Add the child to the state structure
					state.addChild(child);
				}
			}
		}

		// Add the element that indicates the number of castle moves left
		child = new XMLElement(XML_CM_NAME);
		child.setIntAttribute(XML_ATT_PLAYER_0, castle_moves_left[0]);
		child.setIntAttribute(XML_ATT_PLAYER_1, castle_moves_left[1]);
		state.addChild(child);

		return (state);
	}
}
