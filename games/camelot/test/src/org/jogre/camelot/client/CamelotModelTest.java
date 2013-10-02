/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
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
package org.jogre.camelot.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

/**
 * Test case for game model for the camelot game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class CamelotModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		CamelotModel model = new CamelotModel();

		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (CamelotModel srcModel) throws Exception {

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		CamelotModel model = new CamelotModel();
		model.setState(elm);

		// Verify that the board is the same
		for (int i=0; i<CamelotModel.COLS; i++) {
			for (int j=0; j<CamelotModel.ROWS; j++) {
				assertEquals (srcModel.getPieceAt(i,j), model.getPieceAt(i,j));
			}
		}

		// Verify that the # of castle moves left is the same
		assertEquals (srcModel.getCastleMovesLeft(0), model.getCastleMovesLeft(0));
		assertEquals (srcModel.getCastleMovesLeft(1), model.getCastleMovesLeft(1));
	}
}
