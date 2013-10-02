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
 * that make a triangle shape.</p>
 *
 * <p>Triangle boards look like this:
 * <pre>
 *            /\
 *           /  \
 *          /    \
 *         / 2,0  \
 *         --------
 *        /\ 2,1  /\
 *       /  \    /  \
 *      /    \  /    \
 *     / 1,1  \/ 3,1  \
 *     ----------------
 *    /\ 1,2  /\ 3,2  /\
 *   /  \    /  \    /  \
 *  /    \  /    \  /    \
 * / 0,2  \/ 2,2  \/ 4,2  \
 * ------------------------
 * </pre>
 * This is a triangle of order=3.</p>
 *
 * <p>Because of the way the triangle board component works, if the order is odd,
 * then the large triangular board lines up exactly on the left side of the component.
 * If the order is even, then the triangular board is offset 1/2 of the bounding
 * box in the x-direction.  RAW: Hmm, maybe make this class deal with the offset
 * by overriding the other functions to deal with this?</p>
 *
 * <p>See the AbstractTriangleBoardComponent for more details on Triangle Board components.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractTriangleTriangleBoardComponent
extends AbstractPartialTriangleBoardComponent {

	/**
	 * Constructor to create a triangular triangle board.
	 *
	 * @param order               The number of triangles on each side
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each triangle.
	 */
	public AbstractTriangleTriangleBoardComponent (
		int order,
		Dimension cellDim,
		int controlLineLength
	) {
		// Call the super class to create the AbstractTriangleBoard component
		super (makeTriangleExistsArray(order),
		       cellDim,
		       controlLineLength);
	}

	/**
	 * Utility function to create the existsArray[] for a triangle board that is
	 * a triangle shape with sides of the given length (in triangles).
	 *
	 * @param order    The number of triangles on each side of the board.
	 * @return a boolean array of which triangles exist on the board and which don't.
	 */
	public static boolean [][] makeTriangleExistsArray(int order) {
		boolean evenOrder = ((order & 0x01) == 0);
		int numCols = (order * 2) - (evenOrder ? 0 : 1);
		int numRows = order;

		// Create the array (and initialize to all false.)
		boolean [][] theArray = new boolean [numCols][numRows];

		// Now, populate the array with the spaces that actually exist
		int len = 1;
		for (int r = 0; r < order; r++) {
			int start = (order & ~0x01) - r;
			for (int c = 0; c < len; c++) {
				theArray[start + c][r] = true;
			}
			len += 2;
		}

		return theArray;
	}

}