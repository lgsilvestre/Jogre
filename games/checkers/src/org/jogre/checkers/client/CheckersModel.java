/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
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
package org.jogre.checkers.client;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.checkers.common.CommCheckersMove;
import org.jogre.client.IJogreModel;
import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Checkers model which extends the JogreModel can contains a 32 interger
 * array.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CheckersModel extends JogreModel implements ICheckersModel {

	// Declare attributes
	private int [] checkerPieces = new int [32];

	// Array showing the various allowed moves
	private boolean [] allowedMoves = new boolean [32];

	// Am I going to keep this?
	private Point lastMove = new Point (-1, -1);

	private CheckersPieceMover checkersPieceMover;
	
	private static final String XML_ATT_PIECES   = "pieces";

	/**
	 * Default constructor
	 */
	public CheckersModel () {
		super (IJogreModel.GAME_TYPE_TURN_BASED);

		checkersPieceMover = new CheckersPieceMover (this);
		reset ();
	}

	/**
	 * Reset default pieces.
	 *
	 * @see org.jogre.common.JogreModel#reset()
	 */
	public void reset () {
		// set pieces  back to the start
		for (int i = 0; i < 32; i++) {
			if (i < 12)
				checkerPieces [i] = B_NORMAL;
			else if (i < 20)
				checkerPieces [i] = EMPTY;
			else
				checkerPieces [i] = W_NORMAL;
		}

		refreshObservers();
	}

	/**
	 * Return the piece at a specified position.
	 *
	 * @param index
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public int getPiece (int index) {
		// check position is in bounds and then return it value
		if (index >= 0 && index < 32)
			return checkerPieces [index];
		else return -1;
	}

	/**
	 * Return a piece.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPiece (int x, int y) {
		int index = getBoardIndex(x, y);
		return getPiece (index);
	}

	/**
	 * Sets the value of a piece on the board.
	 *
	 * @param index
	 * @param newPiece
	 */
	public void setPiece (int index, int newPiece) {
		// check position is in bounds and then return it value
		if (index >= 0 && index < 64)
			checkerPieces [index] = newPiece;
	}

	/**
	 * Convience method for returning the colour at a particular square.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPieceColour (int index) {
		int piece = getPiece(index);			// retrieve piece
		if (piece == EMPTY)
			return -1;
		else if (piece < B_NORMAL)
			return PLAYER_ONE;
		else
			return PLAYER_TWO;
	}

	/**
	 * Overloaded verison which takes the x and y co-ordinates.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPieceColour (int x, int y) {
		return getPieceColour (getBoardIndex (x, y));
	}

	/**
	 * Try and move a checkers piece from one co-ordinate to another.
	 *
	 * @param x1  Starting X co-ordinate
	 * @param y1  Starting Y co-ordinate
	 * @param x2  Ending X co-ordinate
	 * @param y2  Ending Y co-ordinate
	 * @return
	 */
	public boolean executeMove (int x1, int y1, int x2, int y2) {
		boolean move = false;
		if (checkersPieceMover.isPossibleMove (x1, y1, x2, y2)) {
			move = checkersPieceMover.executeMove(x1, y1, x2, y2);
			refreshObservers();
		}

		return move;
	}
	
	/**
	 * Execute a checkers move.
	 * 
	 * @param checkersMove  Checkers move as a Comm object.
	 * @return              True if the move is OK.
	 */
	public boolean executeMove (CommCheckersMove checkersMove) {
		Point start = new Point (getBoardPoint(checkersMove.getStart()));
		Point end = new Point (getBoardPoint(checkersMove.getEnd()));

		return executeMove (start.x, start.y, end.x, end.y);
	}
	
	/**
	 * Retrieve the piece mover.
	 *
	 * @return
	 */
	public CheckersPieceMover getPieceMover () {
		return checkersPieceMover;
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
	 * Displays the pieces in the board in a String grid e.g. W = White,
	 * BK = Black King.  This is very useful for debugging purposes.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer str = new StringBuffer ();
		for (int i = 0; i < 32; i++) {
			if (i % 8 == 0)
				str.append ("  ");
			str.append(PIECES_STR_ABRV[getPiece(i)] + " ");
			if (i % 4 == 3)
				str.append ("\n");

		}
		return str.toString();
	}

	/**
	 * Update the allowed moves array.
	 *
	 * @param x1
	 * @param y1
	 */
	public void updateAllowedMoves (int x1, int y1) {
		for (int index = 0; index < 32; index++) {
			Point p = getBoardPoint(index);
			allowedMoves [index] =
				checkersPieceMover.isPossibleMove (x1, y1, p.x, p.y);
		}
	}

	/**
	 * Return true if this is an allowed move.
	 *
	 * @param index
	 * @return
	 */
	public boolean isAllowedMove (int index) {
		return allowedMoves [index];
	}

	/**
	 * Reset the allowed moves.
	 */
	public void resetAllowedMoves () {
		for (int i = 0; i < 32; i++)
			allowedMoves [i] = false;
	}

	/**
	 * Convience method to convert a board piece (0 to 31) into a
	 * Point on a virtual board where x and y are between 0 to 7.
	 *
	 * @param index	  Index to piece on board (between 0 and 31)
	 * @return
	 */
	public Point getBoardPoint (int index) {
		int x = ((index % 4) * 2) + ((index + 4) / 4) % 2;
		int y = (index / 4);
		return new Point (x, y);
	}

	/**
	 * Does the opposite of the getBoardPoint (int index) method. returns an
	 * index from a point in the board.
	 *
	 * @param point
	 * @return
	 */
	public int getBoardIndex (int x, int y) {
		return (x / 2) + (y * 4);
	}
	
	/**
	 * Set the checkers pieces from the contents of the message. 
	 *
	 * @param message    Data stored in message.
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {		
		// Set chess pieces
		this.checkerPieces = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_PIECES));
		
		// If everything is read sucessfully then refresh observers
		refreshObservers();
	}
	
	/**
	 * Implementation of a checkers game - This is stored on the server and is used
	 * when a player visits a game and a game is in progress. 
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);
				
		// Flatten 2d data to a single array and then to a String
		state.setAttribute (XML_ATT_PIECES,   JogreUtils.valueOf (checkerPieces));

		return state;
	}	
}
