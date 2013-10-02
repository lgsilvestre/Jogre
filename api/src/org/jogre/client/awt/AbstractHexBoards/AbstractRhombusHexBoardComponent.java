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
 * that make a rhombus shape.</p>
 *
 * <p>Rhombus boards look like this:
 * <pre>
 *  .     .     *     .     .
 *     .     *     *     .
 *  .     *     *     *     .
 *     *     *     *     *
 *  *     *     *     *     *
 *     *     *     *     *
 *  .     *     *     *     .
 *     .     *     *     .
 *  .     .     *     .     .
 * </pre>
 * where hex's that exist are shown with *'s and hex's that don't exist are
 * shown with .'s.  (This is a rhombus of size=5.)</p>
 *
 * <p>See the AbstractHexBoardComponent for more details on Hex Board components.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractRhombusHexBoardComponent extends AbstractPartialHexBoardComponent {

	/**
	 * Constructor for a Rhombus board.
	 *
	 * @param sideSize            The number of hexagons for each side of the board.
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each hexagon.
	 */
	public AbstractRhombusHexBoardComponent (
		int sideSize,
		Dimension cellDim,
		int controlLineLength
	) {

		super (
			makeExistsArray(sideSize),     // existsArray
			cellDim,                       // cellDim
			controlLineLength,             // controlLineLength
			((sideSize & 0x01) == 0),      // trimLow
			((sideSize & 0x01) != 0)       // trimHigh
		);
	}

	/**
	 * Utility function to create the existsArray[] for a hex board that is a
	 * rhombus shape with sides of the given length (in hexagons).
	 *
	 * @param size          The number of hexagons on each side of the rhombus.
	 * @return a boolean array of which hexagons exist on the board and which don't
	 */
	public static boolean [][] makeExistsArray(int size) {
		int numCols = size + size - 1;
		int numRows = size;

		// Create the array (and initialize to all false.)
		boolean [][] theArray = new boolean [numCols][numRows];

		// Populate the array with the spaces that actually exist.
		HexBoardUtils.fillHexTriangle(theArray, size-1, 0, numRows, 1, false, true);
		HexBoardUtils.fillHexTriangle(theArray, size-1, 0, numRows, 1, true, true);

		return theArray;
	}

}
