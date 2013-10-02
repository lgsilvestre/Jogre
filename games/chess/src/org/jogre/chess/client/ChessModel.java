/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.chess.client;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class ChessModel extends JogreModel implements IChessModel {

	// Declare chess specific attributes
	private int [] chessPieces;				// All the chess pieces on the board
	private int [] capturedPieces;          // Captured pieces
	private int numOfCapturedPieces;        // Num of captured pieces

	private boolean [] allowedMoves = new boolean [64];

	// Declare booleans for the king, rooks, en passant etc
	private boolean [] flags = new boolean [NUM_OF_FLAGS];

	// Store moves of a particular game
	private GameHistory gameHistory;

	// Create a move checker
	private ChessPieceMover moveChecker;

	// Location of the last move
	private Point lastMove = null;

	// Declare XML attributes
	private static final String XML_ATT_PIECES    = "pieces";
	private static final String XML_ATT_CAPTURED  = "captured";
	private static final String XML_ATT_FLAGS     = "flags";
	private static final String XML_ATT_LAST_MOVE = "last_move";

	/**
	 * Default constructor.
	 *
	 * @param table
	 * @param username
	 */
	public ChessModel () {
		super (GAME_TYPE_TURN_BASED);

		// Set up data arrays
		chessPieces = new int [64];
		capturedPieces = new int [32];
		numOfCapturedPieces = 0;
		reset ();

		// declare a new move checker using this instance
		moveChecker = new ChessPieceMover (this);

		// declare a new game history
		gameHistory = new GameHistory ();
	}

	/**
	 * Method which reads in a String which has been created from a
	 * ITransmittable.flatten () method of this class.
	 *
	 * @param message    Data stored in message.
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {
		// Wipe everything
		reset ();

		// Set chess pieces
		this.chessPieces = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_PIECES));

		// Set state CP
		int [] stateCP = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_CAPTURED));
		this.numOfCapturedPieces = stateCP.length;
		for (int i = 0; i < numOfCapturedPieces; i++)
			this.capturedPieces [i] = stateCP [i];

		this.flags  = JogreUtils.convertToBoolArray(message.getStringAttribute(XML_ATT_FLAGS));
		this.gameHistory = new GameHistory ((XMLElement)message.getChildren().get(0));
		this.lastMove = JogreUtils.convertToPoint(message.getStringAttribute(XML_ATT_LAST_MOVE));

		// If everything is read sucessfully then refresh observers
		refreshObservers();
	}

	/**
	 * Reset the pieces.
	 */
	public void reset () {
		// reset last move to off the screen
		lastMove = new Point (-1, -1);

		// set pieces  back to the start
		for (int i = 0; i < 64; i++)
			chessPieces [i] = START_POSITIONS[i];
			//chessPieces [i] = TEST_START_POSITIONS[i];

		// reset flags
		for (int i = 0; i < NUM_OF_FLAGS; i++)
			flags [i] = true;

		// Reset captured pieces
		for (int i = 0; i < 32; i++)
			capturedPieces [i] = 0;

		// inform any graphical observers
		refreshObservers();
	}

	/**
	 * Wipes the board by making all the pieces equal to Pieces.EMPTY
	 */
	public void wipeBoard () {
		for (int i = 0; i < 64; i++)
			chessPieces [i] = EMPTY;
	}

	/**
	 * Return the current chess piece.
	 *
	 * @param x   X co-ordinate in the board
	 * @param y   Y co-ordinate in the board
	 * @return    The current chess piece as determined in com.bob.chess.Pieces
	 */
	public int getPiece (int x, int y) throws IndexOutOfBoundsException {
		// check position is in bounds and then return it value
		if (x >= 0 && x < 8 && y >= 0 && y < 8)
			return chessPieces[y * 8 + x];
		else throw new IndexOutOfBoundsException ("x=" + x + " y=" + y);
	}

	/**
	 * Return the piece at this position.
	 *
	 * @param index
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public int getPiece (int index) throws IndexOutOfBoundsException {
		// check position is in bounds and then return it value
		if (index >= 0 && index < 64)
			return chessPieces[index];
		else throw new IndexOutOfBoundsException ();
	}

	/**
	 * Set the chess piece.
	 *
	 * @param x
	 * @param y
	 * @param newPiece
	 * @throws IndexOutOfBoundsException
	 */
	public void setPiece (int x, int y, int newPiece) throws IndexOutOfBoundsException {
		// check position is in bounds and then return it value
		if (x >= 0 && x < 8 && y >= 0 && y < 8)
			chessPieces[y * 8 + x] = newPiece;
		else throw new IndexOutOfBoundsException ();
	}

	/** Sets the value of a piece on the board.
	 * @param index
	 * @param newPiece
	 */
	public void setPiece (int index, int newPiece) {
		// check position is in bounds and then return it value
		if (index >= 0 && index < 64)
			chessPieces[index] = newPiece;
		else throw new IndexOutOfBoundsException ();
	}

	/**
	 * Convience method for returning the colour at a particular square.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPieceColour (int x, int y) {
		int piece = getPiece(x, y);			// retrieve piece
		return getPieceColour (piece);
	}

	/**
	 * Convience method for returning the colour at a particular square.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPieceColour (int piece) {
		if (piece >= W_PAWN && piece <= W_KING)
			return PLAYER_ONE;
		else if (piece >= B_PAWN && piece <= B_KING)
			return PLAYER_TWO;
		return -1;
	}

	/**
	 * Overloaded version of this.
	 *
	 * @param move
	 * @return
	 */
	public boolean executeMove (ChessMove move) {
		return executeMove (move.getX1(), move.getY1(), move.getX2(), move.getY2());
	}

	/**
	 * Try and move a shape from one co-ordinate to another.
	 *
	 * @param x1  Starting X co-ordinate
	 * @param y1  Starting Y co-ordinate
	 * @param x2  Ending X co-ordinate
	 * @param y2  Ending Y co-ordinate
	 * @return
	 */
	public boolean executeMove (int x1, int y1, int x2, int y2) {
		// check position is in bounds and then return it value
		if (x1 >= 0 && x1 < 8 && y1 >= 0 && y1 < 8 &&
				x2 >= 0 && x2 < 8 && y2 >= 0 && y2 < 8)
		{
			int piece1 = getPiece(x1, y1);
			int player = getPieceColour (piece1);

			// A pawn may have moved
			boolean validMove = moveChecker.checkMove (player, x1, y1, x2, y2);

			// check to see if this is a valid move or not
			if (validMove) {
				// if a valid move then execute this move
				moveChecker.executeMove(this, x1, y1, x2, y2);

				// add move to the history
				ChessMove move = new ChessMove (x1, y1, x2, y2);
				gameHistory.addMove (move);

				lastMove = new Point (x2, y2);

				// notify listeners (components)
				refreshObservers();
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a captured white piece.
	 *
	 * @param piece
	 */
	public void addCapturedPiece (int piece) {
		capturedPieces [numOfCapturedPieces++] = piece;
	}

	/**
	 * Updates the allowed moves for a particular piece.
	 *
	 * @param x1
	 * @param y1
	 */
	public void updateAllowedMoves (int x1, int y1) {
		int player = getPieceColour(x1, y1);
		for (int x2 = 0; x2 < 8; x2++) {
			for (int y2 = 0; y2 < 8; y2++) {
				allowedMoves [y2 * 8 + x2] = moveChecker.checkMove (player, x1, y1, x2, y2);
			}
		}
	}

	/**
	 * Reset all the moves in the board.
	 */
	public void resetAllowedMoves () {
		for (int i = 0; i < 64; i++)
			allowedMoves [i] = false;
	}

	/**
	 * Return the allowed moves vector.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isAllowedMove (int x, int y) {
		int index = y * 8 + x;
		if (index >= 0 && index < 64)
			return allowedMoves [index];
		return false;
	}

	/**
	 * Return a list of all the captured pieces.
	 *
	 * @return
	 */
	public int [] getCapturedPieces () {
		return capturedPieces;
	}

	/**
	 * Returns true if a flag is set.
	 *
	 * @param index
	 * @return
	 */
	public boolean flag (int index) {
		return flags [index];
	}

	/**
	 * Sets a flag to a specified value.
	 *
	 * @param index
	 * @param value
	 */
	public void setFlag (int index, boolean value) {
		flags [index] = value;
	}

	/**
	 * Return the co-ordinates of the last move.
	 *
	 * @return
	 */
	public Point getLastMove () {
		return lastMove;
	}

	/**
	 * Sets the last move.
	 */
	public void setLastMove (Point lastMove) {
		this.lastMove = lastMove;
	}

	/**
	 * Displays the pieces in the board in a String grid e.g. white pawn = WP.
	 * This is very useful for debugging purposes.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer str = new StringBuffer ();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				str.append(PIECES_STR_ABRV[getPiece(x, y)] + " ");
			}
			str.append ("\n");
		}
		return str.toString();
	}

	/**
	 * @return Returns the gameHistory.
	 */
	public GameHistory getGameHistory() {
		return gameHistory;
	}

	/**
	 * Return the piece mover.
	 *
	 * @return
	 */
	public ChessPieceMover getPieceMover () {
		return moveChecker;
	}

	/**
	 * Implementation of a chess game - This is stored on the server and is used
	 * when a player visits a game and a game is in progress.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		// Create new int array of size numOfCapturedPieces
		int [] cp = new int [numOfCapturedPieces];
		for (int i = 0; i < numOfCapturedPieces; i++)
			cp[i] = capturedPieces [i];

		// Flatten 2d data to a single array and then to a String
		state.setAttribute (XML_ATT_LAST_MOVE,  JogreUtils.valueOf (lastMove));
		state.setAttribute (XML_ATT_PIECES,     JogreUtils.valueOf (chessPieces));
		state.setAttribute (XML_ATT_CAPTURED,   JogreUtils.valueOf (cp));
		state.setAttribute (XML_ATT_FLAGS,      JogreUtils.valueOf (flags));
		state.addChild (gameHistory.flatten());

		return state;
	}
}