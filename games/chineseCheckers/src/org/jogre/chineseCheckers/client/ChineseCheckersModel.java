/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
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
package org.jogre.chineseCheckers.client;

import nanoxml.XMLElement;

import java.lang.ArrayIndexOutOfBoundsException;

import java.awt.Point;

import java.util.Vector;

import org.jogre.client.awt.AbstractHexBoards.AbstractStarHexBoardComponent;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;

import org.jogre.common.comm.Comm;
import org.jogre.common.util.HexBoardUtils;
import org.jogre.common.util.JogreUtils;

/**
 * Model for the Chinese Checkers game
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class ChineseCheckersModel extends JogreModel {

	// Chinese Checker boards are always of size 4
	public static final int BOARD_SIZE = 4;

	// The size of the board (rows & cols)
	private int numRows, numCols;

	// The ownerBoard keeps track of which player's marble is on each space.
	private int [][] ownerBoard;

	// The existArray keeps track of which spaces in the array actually
	// exist as spaces on the board.
	private boolean [][] existArray;

	// An array of vectors that keeps track of the spaces travelled to get
	// to a given space.
	private Vector [][] moveVectors;

	// Array used to get the neighbors for a given hex.
	private int [][] neighborArray = new int [2] [6];

	// Flag to determine if this game is played with long jumps or not.
	private boolean longJumps;

	// Number of players in the game
	private int numPlayers;

	// The star board has 6 points where the players start the game.
	// Point 0 is the one pointing left on the left side of the board.
	// The other points run clockwise around the board.
	// These tables describe the locations of the 6 points.

	// StartCol, StartRow & StartFace descibe the triangle parameters for finding
	// the given point
	int [] pointStartCol = new int [6];
	int [] pointStartRow = new int [6];
	boolean [] pointStartFace = {false, true, false, true, false, true};

	// homePoints indicates, for each player, which point is his home point (where
	// his pieces begin) and his target point (where he is trying to get his
	// pieces to).
	int [][] homePoints = {
		{0, 3, -1, -1, -1, -1},     // 2-player game
		{0, 2,  4, -1, -1, -1},     // 3-player game
		{1, 2,  4,  5, -1, -1},     // 4-player game
		{0, 1,  2,  3,  4, -1},     // 5-player game
		{0, 1,  2,  3,  4,  5}      // 6-player game
	};

	int [][] targetPoints = {
		{3, 0, -1, -1, -1, -1},     // 2-player game
		{3, 5,  1, -1, -1, -1},     // 3-player game
		{4, 5,  1,  2, -1, -1},     // 4-player game
		{3, 4,  5,  0,  1, -1},     // 5-player game
		{3, 4,  5,  0,  1,  2}      // 6-player game
	};

	/**
	 * Constructor for the model
	 */
	public ChineseCheckersModel(int numPlayers, boolean longJumps) {
		super();

		// Save parameters
		this.numPlayers = numPlayers;
		this.longJumps = longJumps;

		// Make the existArray for the board.
		existArray = AbstractStarHexBoardComponent.makeExistsArray (BOARD_SIZE);

		// Get the size of the board from the size of the existArray
		numCols = existArray.length;
		numRows = existArray[0].length;

		// Make the board the same size as the exist array.
		ownerBoard = new int [numCols][numRows];
		moveVectors = new Vector [numCols][numRows];

		// Initialize the point arrays given the board size.
		pointStartCol[0] = BOARD_SIZE - 1;
		pointStartCol[1] = pointStartCol[5] = BOARD_SIZE;
		pointStartCol[2] = pointStartCol[4] = BOARD_SIZE*3;
		pointStartCol[3] = BOARD_SIZE*3 + 1;
		pointStartRow[0] = pointStartRow[3] = BOARD_SIZE;
		pointStartRow[1] = pointStartRow[2] = 0;
		pointStartRow[4] = pointStartRow[5] = BOARD_SIZE*2 + 1;

		resetGame ();
	}

	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame () {
		clearBoard();
		placeInitialPieces();

		refreshObservers();
	}

	/*
	 * Clear the ownerboard so that all spaces are unowned.
	 */
	private void clearBoard() {
		for (int c = 0; c < numCols; c++) {
			for (int r = 0; r < numRows; r++) {
				ownerBoard[c][r] = -1;
			}
		}
	}

	/*
	 * Place the initial pieces on the board.
	 */
	private void placeInitialPieces() {
		for (int pl=0; pl < numPlayers; pl++) {
			int pt = homePoints[numPlayers-2][pl];
			fillHexTriangle(ownerBoard, pointStartCol[pt], pointStartRow[pt], BOARD_SIZE, 1, pointStartFace[pt], pl);
		}
	}

	/**
	 * This is a helper function that will fill in an integer array for a hex
	 * board with a triangle shape of the given value.
	 *
	 * @param theArray     The int array to modify.
	 * @param startCol     The starting column of the array to modify.
	 * @param startRow     The starting row of the array to modify.
	 * @param startHeight  The starting height of the first row to modify.
	 * @param endHeight    The ending height of the triangle.
	 * @param faceRight    If true, triangle will face right.
	 *                     If false, the triangle will face left.
	 * @param value        The value to put into the array.
	 */
	public static void fillHexTriangle(
	        int [][] theArray,
	        int startCol, int startRow, int startHeight,
	        int endHeight, boolean faceRight,
	        int value
	) {
		while (startHeight >= endHeight) {
			fillColumn(theArray, startCol, startRow, startHeight, value);
			startRow += (startCol & 0x01);
			startCol += (faceRight ? 1 : -1);
			startHeight -= 1;
		}
	}

	/**
	 * This is a helper function that will fill in part of a column of a
	 * two-dimensional integer array with a given value.  All of the entries of
	 * the given array in the given column between startRow &
	 * (startRow + numRows - 1) (inclusize) will be set to the given value.
	 *
	 * @param theArray   The int array to modify.
	 * @param col        The column of the array to modify.
	 * @param startRow   The starting row of the array to modify.
	 * @param numRows    The number of rows to modify.
	 * @param value      The value to put into the array.
	 */
	public static void fillColumn(int [][] theArray, int col, int startRow, int numRows, int value) {
		for (int r = 0; r < numRows; r++) {
			theArray[col][startRow + r] = value;
		}
	}

	/**
	 * This will clear a triangle of any moves that end within it.
	 *
	 * @param theArray     The move array to modify.
	 * @param startCol     The starting column of the array to modify.
	 * @param startRow     The starting row of the array to modify.
	 * @param startHeight  The starting height of the first row to modify.
	 * @param endHeight    The ending height of the triangle.
	 * @param faceRight    If true, triangle will face right.
	 *                     If false, the triangle will face left.
	 */
	public static void clearHexTriangle(
	        Vector [][] theArray,
	        int startCol, int startRow, int startHeight,
	        int endHeight, boolean faceRight
	) {
		while (startHeight >= endHeight) {
			for (int r = 0; r < startHeight; r++) {
				theArray[startCol][startRow + r] = null;
			}
			startRow += (startCol & 0x01);
			startCol += (faceRight ? 1 : -1);
			startHeight -= 1;
		}
	}

	/**
	 * Retrieve the number of players in this game.
	 *
	 * @return the number of players in the game.
	 */
	public int getNumPlayers () {
		return numPlayers;
	}

	/**
	 * Retrieve if this game uses long jumps or not.
	 *
	 * @return if this game uses long jumps.
	 */
	public boolean usesLongJumps () {
		return longJumps;
	}

	/**
	 * Retrieve the owner of a space on the board
	 *
	 * @param (col, row)           Location of board space
	 * @return owner of the space
	 */
	public int getOwner(int col, int row) {
		try {
			return ownerBoard[col][row];
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	public int getOwner(Point theSpace) {
		return getOwner(theSpace.x, theSpace.y);
	}

	/**
	 * Retrieve the move vector for a space on the board
	 *
	 * @param (col, row)           Location of board space
	 * @param theSpace             The board space
	 * @return the move vector that ends at that space.
	 */
	public Vector getMoveVector(int col, int row) {
		try {
			return moveVectors[col][row];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public Vector getMoveVector(Point theSpace) {
		return getMoveVector(theSpace.x, theSpace.y);
	}

	/*
	 * Clear the moveVectors array to all null.
	 */
	public void clearMoveVectors() {
		for (int c = 0; c < numCols; c++) {
			for (int r = 0; r < numRows; r++) {
				moveVectors[c][r] = null;
			}
		}
	}

	/**
	 * Determine if the given space is empty or not.
	 */
	public boolean isEmpty(Point boardPoint) {
		return (getOwner(boardPoint) < 0);
	}

	/**
	 * Determine if the given space exists or not.
	 */
	public boolean exists(Point boardPoint) {
		try {
			return existArray[boardPoint.x][boardPoint.y];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Return the owner of the given point.
	 */
	public int getPointOwner(int pointNum) {

		// Search through the home list looking for the owner of this point.
		for (int owner = 0; owner < 6; owner++) {
			if (homePoints[numPlayers-2][owner] == pointNum) {
				return owner;
			}
		}

		// Nobody owns the given point.
		return -1;
	}

	/**
	 * Return the player who is targeting the given point.
	 */
	public int getPointTarget(int pointNum) {

		// Search through the target list looking for the owner of this point.
		for (int owner = 0; owner < 6; owner++) {
			if (targetPoints[numPlayers-2][owner] == pointNum) {
				return owner;
			}
		}

		// Nobody targets the given point.
		return -1;
	}

	/**
	 * Compute the valid spaces on the board where the marble on the given
	 * space can move.
	 *
	 * @param srcPoint             The point where the source marble is going
	 *                             to move from.
	 */
	public void setValidSpaces(Point srcPoint) {
		// First, clear the moveVectors
		clearMoveVectors();

		// Initialize the vector for the starting space
		Vector myVector = new Vector();
		myVector.add(srcPoint);
		moveVectors[srcPoint.x][srcPoint.y] = myVector;

		// Remove the moving marble from the board (so that we don't
		// try to jump it over itself.)
		int owner = ownerBoard[srcPoint.x][srcPoint.y];
		ownerBoard[srcPoint.x][srcPoint.y] = -1;

		// Then create move vectors for the jumps
		if (longJumps) {
			setMoveVectorsLongJump(srcPoint);
		} else {
			setMoveVectorsShortJump(srcPoint);
		}

		// Put the marble back again
		ownerBoard[srcPoint.x][srcPoint.y] = owner;

		// Remove the starting point as a valid move.
		moveVectors[srcPoint.x][srcPoint.y] = null;

		// Now that jumps are done, make move vectors for just
		// sliding the marble to a neighboring space.
		Point [] neighbors = makeNeighbors(srcPoint);
		for (int dir = 0; dir < 6; dir++) {
			Point n = neighbors[dir];
			if (exists(n) && isEmpty(n) && (getMoveVector(n) == null)) {
				moveVectors[n.x][n.y] = new Vector (myVector);
				moveVectors[n.x][n.y].add(n);
			}
		}

		// Finally, clear any moves that end in one of the points that this
		// player is not allowed to end it.  The player is only allowed to
		// end in his home point and his target point.
		for (int pt = 0; pt < 6; pt++) {
			if ((pt != homePoints[numPlayers-2][owner]) &&
			    (pt != targetPoints[numPlayers-2][owner])) {
				clearHexTriangle(moveVectors, pointStartCol[pt], pointStartRow[pt], BOARD_SIZE, 1, pointStartFace[pt]);
			}
		}
	}

	/**
	 * Compute the valid spaces on the board where the marble on the given
	 * space can move only allowing short jumps.
	 *
	 * @param fromPoint        The point where the marble is moving from.
	 */
	private void setMoveVectorsShortJump(Point fromPoint) {
		Point [] neighbors = makeNeighbors(fromPoint);
		Point [] nextNeighbors = makeNextNeighbors(neighbors);

		for (int dir = 0; dir < 6; dir++) {
			Point neighbor = neighbors[dir];
			Point nextNeighbor = nextNeighbors[dir];
			if (exists(neighbor) && exists(nextNeighbor) &&
			    !isEmpty(neighbor) && isEmpty(nextNeighbor)) {
				// This is a valid move.
				int landCol = nextNeighbor.x;
				int landRow = nextNeighbor.y;
				if (moveVectors[landCol][landRow] == null) {
					// This is the first time we've gotten here...
					moveVectors[landCol][landRow] = new Vector (moveVectors[fromPoint.x][fromPoint.y]);
					moveVectors[landCol][landRow].add(nextNeighbor);
					setMoveVectorsShortJump(nextNeighbor);
				}
			}
		}
	}

	/*
	 * Return an array with the 6 neighbors around the given Point.
	 */
	private Point [] makeNeighbors(Point thePoint) {
		Point [] neighbors = new Point [6];
		HexBoardUtils.getNeighbors(thePoint, neighbors);
		return neighbors;
	}

	/*
	 * Return an array of the neighbors next to the given array of spaces
	 * that are in the same direction as the given neighbors.
	 */
	private Point [] makeNextNeighbors(Point [] neighbors) {
		Point [] nextNeighbors = new Point [6];
		for (int dir = 0; dir < 6; dir++) {
			nextNeighbors[dir] = HexBoardUtils.makeNeighbor(neighbors[dir], dir);
		}
		return nextNeighbors;
	}

	/**
	 * Compute the valid spaces on the board where the marble on the given
	 * space can move allowing long jumps.
	 *
	 * @param fromPoint        The point where the marble is moving from.
	 */
	private void setMoveVectorsLongJump(Point fromPoint) {
		for (int dir = 0; dir < 6; dir++) {
			setLongJump(fromPoint, dir);
		}
	}

	/**
	 * Compute the valid spaces on the board where the marble on the given
	 * space jumping in the given direction can move allowing long jumps.
	 *
	 * @param fromPoint        The point where the marble is moving from.
	 * @param toPoint          The direction the marble is jumping.
	 */
	private void setLongJump(Point fromPoint, int direction) {
		// Start at the fromPoint
		Point currPoint = new Point (fromPoint);

		// Count the distance to the first marble in the given direction.
		int distance = 0;
		do {
			HexBoardUtils.moveHex(currPoint, direction);
			if (!exists(currPoint)) {
				return;
			}
			distance += 1;
		} while (isEmpty(currPoint));

		// Count down to the emtpy space we'll be landing on.
		while (distance != 0) {
			HexBoardUtils.moveHex(currPoint, direction);
			if (!exists(currPoint) || !isEmpty(currPoint)) {
				return;
			}
			distance -= 1;
		}

		// This is a valid jump
		int landCol = currPoint.x;
		int landRow = currPoint.y;
		if (moveVectors[landCol][landRow] == null) {
			// This is the first time we've gotten here...
			moveVectors[landCol][landRow] = new Vector (moveVectors[fromPoint.x][fromPoint.y]);
			moveVectors[landCol][landRow].add(currPoint);
			setMoveVectorsLongJump(currPoint);
		}
	}

	/**
	 * Attempt to make a move on the board.
	 *
	 * Ok, for those of you reading at home, here is a good place to make a
	 * comment about laziness in programming.  A move list is a list of spaces
	 * that a player's marble is jumping through.  This list can contain
	 * multiple spaces because a legal move allows for multiple hops.
	 * This routine to make the move just looks at the first & last spots of
	 * the move and moves the marble from the start point and places it on the
	 * end point.  Note that I don't verify that all of the intermediate points
	 * are valid.  Normally the client ensures that it only sends valid moves.
	 * However, since this is open-source software, it is conceivable that a
	 * malicious person could modify their version of the client to generate
	 * whatever illegal moves they'd like and trusting clients would happily
	 * accept those moves.  I can't imagine that there is a huge likelyhood
	 * of someone doing that just to win at chinese checkers, but it's something
	 * to keep in mind if you're doing you own games.
	 *
	 * @param theMoveList      The vector of Points that the move moves through.
	 * @return validity of the play
	 *          true = valid play
	 *          false = invalid play
	 */
	public boolean makeMove(Vector theMoveList) {

		// Pull the start and end positions from the move
		Point fromPoint = (Point) theMoveList.firstElement();
		Point toPoint = (Point) theMoveList.lastElement();
		int player = ownerBoard[fromPoint.x][fromPoint.y];

		// Move the marble from the "fromPoint" to the "toPoint"
		ownerBoard[toPoint.x][toPoint.y] = player;
		ownerBoard[fromPoint.x][fromPoint.y] = -1;

		// Update the Observers
		refreshObservers();

		return true;
	}

	/**
	 * Determine if the given player is the winner
	 *
	 * @param seatNum      The seat number for check for winner.
	 * @return true if the requested player has won.
	 */
	public boolean checkWinner(int seatNum) {
		int pt = targetPoints[numPlayers-2][seatNum];

		return (countPlayerMarblesInTriangle(
		        seatNum,
		        pointStartCol[pt], pointStartRow[pt],
		        BOARD_SIZE, 1,
		        pointStartFace[pt]) > 0);
	}

	/**
	 * This will count the number of marbles within a triangle that belong to
	 * a given player.  However, if there is an empty space within the triangle,
	 * then this will return 0.
	 *
	 * @param seatNum      The player whose marbles are to be counted.
	 * @param startCol     The starting column of the array to count.
	 * @param startRow     The starting row of the array to count.
	 * @param startHeight  The starting height of the first row to count.
	 * @param endHeight    The ending height of the triangle.
	 * @param faceRight    If true, triangle will face right.
	 *                     If false, the triangle will face left.
	 * @return the number of the player's marbles counted.
	 */
	private int countPlayerMarblesInTriangle(int seatNum,
	        int startCol, int startRow, int startHeight,
	        int endHeight, boolean faceRight
	) {
		int count = 0;
		while (startHeight >= endHeight) {
			for (int r = 0; r < startHeight; r++) {
				int owner = getOwner(startCol, startRow + r);
				if (owner < 0) {
					// There is an empty space in the triangle, so can't be
					// a winner.
					return 0;
				} else if (owner == seatNum) {
					count =+ 1;
				}
			}
			startRow += (startCol & 0x01);
			startCol += (faceRight ? 1 : -1);
			startHeight -= 1;
		}
		return count;
	}

/****************************************************/

	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_OWNERS = "own";

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
		fillFromString(ownerBoard, message.getStringAttribute(XML_ATT_OWNERS));

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
