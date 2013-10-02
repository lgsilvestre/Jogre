/*
 * JOGRE (Java Online Gaming Real-time Engine) - Hex
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.hex.client;

import nanoxml.XMLElement;

import java.lang.ArrayIndexOutOfBoundsException;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;

import org.jogre.common.comm.Comm;
import org.jogre.common.util.HexBoardUtils;
import org.jogre.common.util.JogreUtils;

import org.jogre.client.awt.AbstractHexBoards.AbstractRhombusHexBoardComponent;

/**
 * Model for the Hex game
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class HexModel extends JogreModel {

	// Defines for ownership of board spaces
	public static final int PLAYER_ZERO = 0;
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_NONE = 2;

	// The size of the board (rows & cols)
	protected int numRows, numCols;

	// The ownerBoard keeps track of which player owns which hex's
	private int [][] ownerBoard;

	// The islandBoard keeps track of which island a hex is part of
	protected int [][] islandBoard;

	// The existArray keeps track of which spaces in the array actually
	// exist as hex's on the board.
	private boolean [][] existArray;

	// The turn number of the game.
	protected int turnNumber = 1;

	// Array used to get the neighbors for a given hex.
	private int [] [] neighborArray = new int [2] [6];

	// Because of the way the board is layed out, there is one valid space
	// on the board that touches both of the winning corner areas, but does
	// not actually have any neighbors in those areas.  For even-sized boards,
	// this is the bottom-most hex and for odd-sized boards, this is the
	// top-most hex.  To handle this case, we keep track of this special
	// location and when selected for play, we set it to the island number that
	// we know it is next to so that it will be part of the correct island even
	// though it won't be detected via the neighbors.
	private int magicCol, magicRow;
	private int [] magicIsland = new int [2];

	/**
	 * Constructor for the model
	 */
	public HexModel(int boardSize) {
		super();

		// Make the existArray for the board (using the component).
		existArray = AbstractRhombusHexBoardComponent.makeExistsArray(boardSize);

		// Get the size of the board from the size of the existArray
		numCols = existArray.length;
		numRows = existArray[0].length;

		// Make the board the same size as the exist array.
		ownerBoard = new int [numCols][numRows];
		islandBoard = new int [numCols][numRows];

		// Configure the magic space
		magicCol = boardSize - 1;
		if ((boardSize & 1) == 0) {
			// Even board, so the magic hex is the bottom-most in the middle
			magicRow = boardSize - 1;
			magicIsland[0] = -4;
			magicIsland[1] = -3;
		} else {
			// Odd board, so the magic hex is the top-most in the middle
			magicRow = 0;
			magicIsland[0] = -1;
			magicIsland[1] = -2;
		}

		resetGame ();
	}


	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame () {
		clearBoards();
		setInitialIslands();

		turnNumber = 1;

		refreshObservers();
	}

	/*
	 * Clear the ownerboard so that all spaces are unowned and clear the
	 * island board so that there are no islands.
	 */
	private void clearBoards() {
		for (int c = 0; c < numCols; c++) {
			for (int r = 0; r < numRows; r++) {
				ownerBoard[c][r] = PLAYER_NONE;
				islandBoard[c][r] = 0;
			}
		}
	}

	/*
	 * Set the initial island Id's for the corners of the array that aren't
	 * actually on the board to -1, -2, -3, & -4.  This way, when two corners
	 * are connected, then either the top-left corner will change from island
	 * -1 to -4 or the top-right corner will change from island -2 to -3.
	 */
	private void setInitialIslands() {
		setIslandCols(0, 1, 0, 1, -1, PLAYER_ZERO);
		setIslandCols(numCols - 1, -1, 0, 1, -2, PLAYER_ONE);
		setIslandCols(0, 1, numRows - 1, -1, -3, PLAYER_ONE);
		setIslandCols(numCols - 1, -1, numRows - 1, -1, -4, PLAYER_ZERO);
	}

	/*
	 * This will set a range of non-exist spaces to a given island Id.
	 *
	 * @param startCol      The starting column to begin setting island Id's.
	 * @param colIncrement  The amount to increment each column when the column
	 *                      is done.
	 * @param startRow      The starting row in each column.
	 * @param rowIncrement  The amount to increment each row index in the column.
	 * @param islandId      The island Id to give each non-existant space.
	 */
	private void setIslandCols(int startCol, int colIncrement,
	                           int startRow, int rowIncrement,
	                           int islandId, int ownerId) {
		for (int c = startCol; !existArray[c][startRow]; c += colIncrement) {
			for (int r = startRow; !existArray[c][r]; r += rowIncrement) {
				islandBoard[c][r] = islandId;
				ownerBoard[c][r] = ownerId;
			}
		}
	}

	/**
	 * Retrieve the owner of the a space on the board
	 *
	 * @param (col, row)			Location of board space
	 * @return owner of the space
	 */
	public int getOwner(int col, int row) {
		return (ownerBoard[col][row]);
	}

	/**
	 * Determine if the requested play is valid or not.
	 *
	 * To be valid the space must exist and it must either be empty OR
	 * it must be turn number 2.  (In the case where it is turn number 2,
	 * the the second player is invoking the "pie-rule" and taking the
	 * first player's move for himself.)
	 *
	 * @param (col, row)		Location of board space to be tested
	 * @return true or false
	 */
	public boolean isValidPlay(int col, int row) {
		try {
			return (existArray[col][row] &&
                      ((ownerBoard[col][row] == PLAYER_NONE) ||
                       (turnNumber == 2)) );
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Attempt to make a move on the board.
	 *
	 * @param (col, row)		Location of board space to be played
	 * @param playerSeat		The player which is playing on the space
	 * @return validity of the play
	 *			true = valid play
	 *			false = invalid play
	 */
	public boolean makeMove(int col, int row, int playerSeat) {
		if (!isValidPlay(col, row)) {
			return false;
		}

		// Check for the second player invoking the "pie-rule"
		if ((turnNumber == 2) && (ownerBoard[col][row] != PLAYER_NONE)) {
			// Remove player 1's piece from the board.
			ownerBoard[col][row] = PLAYER_NONE;
			islandBoard[col][row] = 0;

			// Change player 2's move to the mirror image position.
			col = (numCols - 1 - col);
		}

		// Make the play on the board
		ownerBoard[col][row] = playerSeat;
		if ((col == magicCol) && (row == magicRow)) {
			islandBoard[col][row] = magicIsland[playerSeat];
		} else {
			islandBoard[col][row] = turnNumber;
		}

		// See if we join any islands as a result of this move.
		HexBoardUtils.getNeighbors(col, row, neighborArray);
		for (int i=0; i<6; i++) {
			int nc = neighborArray[0][i];
			int nr = neighborArray[1][i];
			try {
				if (ownerBoard[nc][nr] == playerSeat) {
					mergeIslands(islandBoard[col][row], islandBoard[nc][nr]);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// The neighbor was off the board, so ignore this one...
			}
		}

		turnNumber += 1;

		// Update the Observers
		refreshObservers();

		return true;
	}

	/*
	 * This method will merge two islands on the board into one.  The island
	 * with the smaller Id will take over all of the spaces of the island with
	 * the larger Id.
	 *
	 * @param islandOne    The Id of one of the islands to merge.
	 * @param islandTwo    The id of the other island to merge.
	 */
	private void mergeIslands(int islandOne, int islandTwo) {
		// Only need to merge if the islands are different.
		if (islandOne != islandTwo) {

			int victimId = Math.max(islandOne, islandTwo);
			int victorId = Math.min(islandOne, islandTwo);

			for (int c = 0; c < numCols; c++) {
				for (int r = 0; r < numRows; r++) {
					if (islandBoard[c][r] == victimId) {
						islandBoard[c][r] = victorId;
					}
				}
			}
		}
	}

	/**
	 * Return the winner of the game.
	 *
	 * @return PLAYER_ZERO if player 0 is the winner.
	 *         PLAYER_ONE if player 1 is the winner.
	 *         PLAYER_NONE if there is no winner yet.
	 */
	public int getWinner() {
		if (islandBoard[0][0] == -4) {
			return PLAYER_ZERO;
		}

		if (islandBoard[numCols - 1][0] == -3) {
			return PLAYER_ONE;
		}

		return PLAYER_NONE;
	}

/****************************************************/

	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_OWNERS = "own";
	private static final String XML_ATT_ISLAND = "isle";
	private static final String XML_ATT_TURN = "turn";

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

		// Reset the board back to the starting value before using the
		// message to fill it in.
		resetGame ();

		// Pull all of the bits out of the message
		turnNumber = message.getIntAttribute(XML_ATT_TURN);
		fillFromString(ownerBoard, message.getStringAttribute(XML_ATT_OWNERS));
		fillFromString(islandBoard, message.getStringAttribute(XML_ATT_ISLAND));

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

		state.setAttribute(XML_ATT_OWNERS, JogreUtils.valueOf(JogreUtils.convertTo1DArray(ownerBoard)));
		state.setAttribute(XML_ATT_ISLAND, JogreUtils.valueOf(JogreUtils.convertTo1DArray(islandBoard)));
		state.setIntAttribute(XML_ATT_TURN, turnNumber);

		return state;
	}

	/*
	 * This method will fill in the 2-D array provided with data from the
	 * string.
	 *
	 * @param array       The array to be filled in.
	 * @param srcString   A space separated string of values to be put into the array.
	 */
	private void fillFromString(int [][] array, String srcString) {
		int [] srcArray = JogreUtils.convertToIntArray(srcString);
		int srcIndex = 0;
		for (int c = 0; c < array.length; c++) {
			for (int r = 0; r < array[0].length; r++) {
				array[c][r] = srcArray[srcIndex];
				srcIndex += 1;
			}
		}
	}
}
