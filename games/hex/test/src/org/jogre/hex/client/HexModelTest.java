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

import junit.framework.TestCase;

import nanoxml.XMLElement;

import org.jogre.common.util.HexBoardUtils;

/**
 * Test case for game model for the hex game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class HexModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		HexTestModel model = new HexTestModel (5);

		// Make some moves
		moveTest (model);

		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test making some moves.
	 */
	private void moveTest (HexTestModel model) throws Exception {
		assertTrue (model.makeMove(5, 3, 0));
		assertTrue (model.makeMove(5, 2, 1));
		assertFalse (model.makeMove(6, 8, 0));  // Not a valid move
		assertEquals (model.getTurnNumber(), 3);
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (HexTestModel srcModel) throws Exception {
		int boardSize = srcModel.getBoardSize();
		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		HexTestModel model = new HexTestModel (boardSize);
		model.setState(elm);

		// Verify that the boards are the same
		assertEquals (srcModel.getNumRows(), model.getNumRows());
		assertEquals (srcModel.getNumCols(), model.getNumCols());

		int numRows = srcModel.getNumRows();
		int numCols = srcModel.getNumCols();
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				assertEquals (srcModel.getOwner(c,r), model.getOwner(c,r));
				assertEquals (srcModel.getIslandId(c,r), model.getIslandId(c,r));
			}
		}

		// Verify that misc. stuff is the same
		assertEquals (srcModel.getTurnNumber(), model.getTurnNumber());
	}

}
