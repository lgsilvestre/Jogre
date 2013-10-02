/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.common;

import java.awt.Point;

/**
 * Element of valid moves for the Triangulum game.
 *
 * This class is used as the basis for the vectors of valid moves for
 * each player hand piece.  This allows the game to keep track of which
 * pieces can go where on the board.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumValidMoveElement {

	// The location on the board that this piece can go
	private Point boardLoc;

	// The array of pieces that have been fliped/rotated that fit on this space.
	private TriangulumPiece [] orientations;

	/**
	 * Constructor which creates the element
	 *
	 * @param loc            The board locations
	 * @param orientations   The orientations for this location
	 */
	public TriangulumValidMoveElement (Point loc, TriangulumPiece [] orientations) {
		this.boardLoc = loc;
		this.orientations = orientations;
	}

	/**
	 * Determine if this board location matches the one provided.
	 */
	public boolean isSameSpace (Point testLoc) {
		return (boardLoc.x == testLoc.x) && (boardLoc.y == testLoc.y);
	}

	/**
	 * Return the list of orientations
	 */
	public TriangulumPiece [] getOrientations () {
		return orientations;
	}
}
