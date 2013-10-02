/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.client.awt.AbstractHexBoards;

import java.awt.*;

import org.jogre.common.util.HexBoardUtils;

/**
 * <p>This graphical component is a base component for hexagonal boards
 * that make a star shape.</p>
 *
 * <p>Star boards look like this:
 * <pre>
 *     .     *     .     .     *     .
 *  .     .     *     .     *     .     .
 *     .     *     *     *     *     .
 *  .     .     *     *     *     .     .
 *     .     *     *     *     *     .
 *  .     .     *     *     *     .     .
 *     .     *     *     *     *     .
 *  .     *     *     *     *     *     .
 *     *     *     *     *     *     *
 *  *     *     *     *     *     *     *
 *     *     *     *     *     *     *
 *  .     *     *     *     *     *     .
 *     .     *     *     *     *     .
 *  .     .     *     *     *     .     .
 *     .     *     *     *     *     .
 *  .     .     *     *     *     .     .
 *     .     *     *     *     *     .
 *  .     .     *     .     *     .     .
 *     .     *     .     .     *     .
 * </pre>
 * where hex's that exist are shown with *'s and hex's that don't exist are
 * shown with .'s.  (This is a star of size=3.)</p>
 *
 * <p>See the AbstractHexBoardComponent for more details on Hex Board components.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractStarHexBoardComponent extends AbstractPartialHexBoardComponent {

	/**
	 * Constructor for a Star board.
	 *
	 * @param sideSize            The number of hexagons for each side of the corner of
	 *                              a star on the board.
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each hexagon.
	 */
	public AbstractStarHexBoardComponent (
		int sideSize,
		Dimension cellDim,
		int controlLineLength
	) {

		super (
			makeExistsArray(sideSize),     // existsArray
			cellDim,                       // cellDim
			controlLineLength,             // controlLineLength
			((sideSize & 0x01) != 0),      // trimLow
			((sideSize & 0x01) == 0)       // trimHigh
		);
	}

	/**
	 * Utility function to create the existsArray[] for a hex board that is a
	 * star shape with sides of each point of the given length (in hexagons).
	 *
	 * @param size    The number of hexagons on each side of each point.
	 * @return a boolean array of which hexagons exist on the board and which don't
	 */
	public static boolean [][] makeExistsArray(int size) {
		int numCols = (size * 4) + 1;
		int numRows = (size * 3) + 1;

		// Create the array (and initialize to all false.)
		boolean [][] theArray = new boolean [numCols][numRows];

		// Now, populate the array with the spaces that actually exist
		// A star is formed by two overlapping triangles, one facing left
		// and one facing right.
		HexBoardUtils.fillHexTriangle(theArray, size, 0, numRows, 1, true, true);
		HexBoardUtils.fillHexTriangle(theArray, (size * 3), 0, numRows, 1, false, true);

		return theArray;
	}

}
