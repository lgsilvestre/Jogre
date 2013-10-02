/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Abstract Partial Triangular Board Component
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
package org.jogre.client.awt.AbstractTriangleBoards;

import java.awt.*;

import org.jogre.client.awt.JogreComponent;

/**
 * <p>This graphical component is a base component for triangular boards
 * where not every triangle is on the board.</p>
 *
 * <p>This PartialTriangleBoardComponent extends the TriangleBoardComponent by
 *    adding a boolean array that indicates which triangles on the board exist.
 *    Triangles that don't exist can't be selected and won't be drawn.</p>
 *
 * <p>See the AbstractTriangleBoardComponent for more details on Triangle Board
 *    components.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractPartialTriangleBoardComponent
extends AbstractTriangleBoardComponent {

	/** A array that indicates which triangles are on the board and which are
	    not. */
	protected boolean [][] existsArray;

	/**
	 * Constructor to create a partial triangle board.
	 *
	 * @param existsArray  An array that indicates which triangles on the board
	 *                     exist and which don't.  The size of the array sets
	 *                     the size of the board component.
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each triangle.
	 */
	public AbstractPartialTriangleBoardComponent (
		boolean [][] existsArray,
		Dimension cellDim,
		int controlLineLength
	) {
		// Call the super class to create the AbstractTriangleBoard component
		super(existsArray.length, existsArray[0].length,
		          cellDim, controlLineLength);
		this.existsArray = existsArray;
	}

	/**
	 * Determine if the triangle at the given (col, row) exists on the board.
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