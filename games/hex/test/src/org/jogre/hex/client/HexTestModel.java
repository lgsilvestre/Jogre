/*
 * JOGRE (Java Online Gaming Real-time Engine) - Hex
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.hex.client;

/**
 * Test model for the Hex game.
 *
 * This test model provides access to protected variables that aren't used by
 * the regular game to allow for more complete testing.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class HexTestModel extends HexModel {

	private int boardSize;

	public HexTestModel (int boardSize) {
		super (boardSize);

		this.boardSize = boardSize;
	}

	public int getBoardSize () {
		return boardSize;
	}

	/**
	 * Provide access to protected variables
	 */
	public int getNumRows () {
		return numRows;
	}

	public int getNumCols () {
		return numCols;
	}

	public int getTurnNumber () {
		return turnNumber;
	}

	public int getIslandId(int col, int row) {
		return islandBoard[col][row];
	}

}
