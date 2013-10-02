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

/**
 * <p>This graphical component is a base component for hexagonal boards
 * where not every hex is on the board.</p>
 *
 * <p>This PartialHexBoardComponent extends the HexBoardComponent by adding
 *    a boolean array that indicates which hexagons on the board exist.
 *    Hexagons that don't exist can't be selected and won't be drawn.</p>
 *
 * <p>See the AbstractHexBoardComponent for more details on Hex Board components.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractPartialHexBoardComponent extends AbstractHexBoardComponent {

	/** A array that indicates which hex's are on the board and which are not. */
	protected boolean [][] existsArray;

	/**
	 * Constructor to create a partial hex board.
	 *
	 * @param existsArray  An array that indicates which hex's on the board exist
	 *                     and which don't.  The size of the array sets the size
	 *                     of the board component.
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each hexagon.
	 * @param trimLow             If true, trim the top row of hex's.
	 * @param trimHigh            If true, trim the bottom row of hex's.
	 */
	public AbstractPartialHexBoardComponent (
		boolean [][] existsArray,
		Dimension cellDim,
		int controlLineLength,
		boolean trimLow,
		boolean trimHigh
	) {
		// Call the super class to create the AbstractHexBoard component
		super(existsArray.length, existsArray[0].length,
				cellDim, controlLineLength,
				trimLow, trimHigh);
		this.existsArray = existsArray;
	}

	/**
	 * Determine if the hex at the given (col, row) exists on the board.
	 * Because of trimLow & trimHigh, some hex's within the array
	 * should not be drawn.
	 *
	 * @param (col, row)    The location to check for existance.
	 * @return true => hex is on the board.
	 */
	public boolean existsOnBoard (int col, int row) {
		if (super.existsOnBoard(col, row)) {
			return existsArray[col][row];
		}
		return false;
	}

	/**
	 * Return the exists array for the board.
	 *
	 * @return the exists array for the board.
	 */
	public boolean [][] getExistsArray() {
		return existsArray;
	}
}
