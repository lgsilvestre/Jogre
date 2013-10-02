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
package org.jogre.common.util;

import java.awt.Dimension;
import java.awt.Point;

/**
 * <p>This file contains general utilities that might come in handy while
 * using the abstract hex board components.
 *
 * See org.jogre.client.awt.AbstractHexBoards for the hexagonal board components
 *
 * </p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class HexBoardUtils {

	/**
	 * Given the length of a side, return the bounding box for a regular hexagon
	 * with that length side in the horizontal orientation.
	 *
	 * This should be used by subClasses of AbstractHexBoardComponent to create boards
	 * with regular hexagon spaces
	 *
	 * @param sideLength    The length of a side.
	 * @return the bounding Dimension.
	 */
	public static Dimension makeRegularBoundingDim(int sideLength) {
		// A regular hexagon sitting on the X-axis has the first side angling at
		// 60-degrees up.
		// cos(60) = 0.5 & sin(60) = 0.86603
		// The magic lengths are:
		//    x = sideLength + 2*cos(60)*sideLength
		//    y = 2*sin(60)*sideLength
		return new Dimension (2*sideLength, (int) (1.73206*sideLength));
	}

	/**
	 * This is a helper function that will fill in an exist array for a hex
	 * board with a triangle shape of the given value.
	 *
	 * @param theArray     The boolean array to modify.
	 * @param startCol     The starting column of the array to modify.
	 * @param startRow     The starting row of the array to modify.
	 * @param startHeight  The starting height of the first row to modify.
	 * @param endHeight    The ending height of the triangle.
	 * @param faceRight    If true, triangle will face right.
	 *                     If false, the triangle will face left.
	 * @param value        The value to put into the array.
	 */
	public static void fillHexTriangle(
	        boolean [][] theArray,
	        int startCol, int startRow, int startHeight,
	        int endHeight, boolean faceRight,
	        boolean value
	) {
		while (startHeight >= endHeight) {
			fillColumn(theArray, startCol, startRow, startHeight, value);
			startRow += (startCol & 0x01);
			startCol += (faceRight ? 1 : -1);
			startHeight -= 1;
		}
	}

	/**
	 * This is a helper function that will fill in part of a row of a two-dimensional boolean
	 * array with a given value.  All of the entries of the given array in the given row between
	 * startCol & (startCol + numCols - 1) (inclusize) will be set to the given value.
	 *
	 * @param theArray   The boolean array to modify.
	 * @param row        The row of the array to modify.
	 * @param startCol   The starting column of the array to modify.
	 * @param numCols    The number of columns to modify.
	 * @param value      The value to put into the array.
	 */
	public static void fillRow(boolean [][] theArray, int row, int startCol, int numCols, boolean value) {
		for (int c = 0; c < numCols; c++) {
			theArray[startCol + c][row] = value;
		}
	}

	/**
	 * This is a helper function that will fill in part of a column of a two-dimensional boolean
	 * array with a given value.  All of the entries of the given array in the given column between
	 * startRow & (startRow + numRows - 1) (inclusize) will be set to the given value.
	 *
	 * @param theArray   The boolean array to modify.
	 * @param col        The column of the array to modify.
	 * @param startRow   The starting row of the array to modify.
	 * @param numRows    The number of rows to modify.
	 * @param value      The value to put into the array.
	 */
	public static void fillColumn(boolean [][] theArray, int col, int startRow, int numRows, boolean value) {
		for (int r = 0; r < numRows; r++) {
			theArray[col][startRow + r] = value;
		}
	}

	/* These tables are used to find the (row, column) coordinates for the
	 * six neighbor cells of a given cell. */
	private static int [] neighborCols = {0, 1, 1, 0, -1, -1};
	private static int [] [] neighborRows = { {-1, -1, 0, 1, 0, -1}, {-1, 0, 1, 1, 1, 0} };

	/**
	 * This method will fill in the given array with the coordinates of the six
	 * hexagons that surround the given hexagon.  This all operates in logical
	 * board coordinates.
	 *
	 * @param theHex         The location of the hexagon whose neighbors are sought.
	 * @param neighborArray  An array of points that will be filled in with the
	 *                       locations of the neighbors.
	 */
	public static void getNeighbors(Point theHex, Point [] neighborArray) {
		getNeighbors(theHex.x, theHex.y, neighborArray);
	}

	/**
	 * This method will fill in the given array with the coordinates of the six
	 * hexagons that surround the given hexagon.  This all operates in logical
	 * board coordinates.
	 *
	 * @param col            The column of the hexagon whose neighbors are sought.
	 * @param row            The row of the hexagon whose neighbors are sought.
	 * @param neighborArray  An array of points that will be filled in with the
	 *                       locations of the neighbors.
	 */
	public static void getNeighbors(int col, int row, Point [] neighborArray) {
		int colPolarity = (col & 0x01);

		for (int i=0; i < 6; i++) {
			neighborArray[i] = new Point (col + neighborCols[i],
			                              row + neighborRows[colPolarity][i]);
		}
	}

	/**
	 * This method will fill in the given array with the coordinates of the six
	 * hexagons that surround the given hexagon.  This all operates in logical
	 * board coordinates.
	 *
	 * @param col            The column of the hexagon whose neighbors are sought.
	 * @param row            The row of the hexagon whose neighbors are sought.
	 * @param neighborArray  An [2][6] array that will be filled in with the locations
	 *                       of the neighbors.
	 *                       The [0][0..5] elements are the columns of the neighbors.
	 *                       The [1][0..5] elements are the rows of the neighbors.
	 */
	public static void getNeighbors(int col, int row, int [] [] neighborArray) {
		int colPolarity = (col & 0x01);

		for (int i=0; i < 6; i++) {
			neighborArray[0][i] = col + neighborCols[i];
			neighborArray[1][i] = row + neighborRows[colPolarity][i];
		}
	}

	/**
	 * This method will return a point that is the neighbor of the given point
	 * in the given direction.
	 *
	 * @param theHex         The location of the hexagon whose neighbor is sought.
	 * @param direction      The direction to get the neighbor.  Valid range is
	 *                       0 to 5, with 0 meaning up and advancing clockwise
	 *                       around the hex.
	 * @return a new point that is the neighbor of the given hex in the requested
	 *         direction.
	 */
	public static Point makeNeighbor(Point theHex, int direction) {
		int colPolarity = (theHex.x & 0x01);
		return new Point (theHex.x + neighborCols[direction],
		                  theHex.y + neighborRows[colPolarity][direction]);
	}

	/**
	 * This method will move the given point one space in the given direction.
	 * Note: This changes the given point.  Use makeNeighbor() to get a new
	 *       point while keeping the old one.
	 *
	 * @param theHex         The location of the hexagon who is moving.
	 * @param direction      The direction to move.  Valid range is
	 *                       0 to 5, with 0 meaning up and going clockwise
	 *                       around the hex.
	 */
	public static void moveHex(Point theHex, int direction) {
		int colPolarity = (theHex.x & 0x01);
		theHex.translate(neighborCols[direction], neighborRows[colPolarity][direction]);
	}
}
