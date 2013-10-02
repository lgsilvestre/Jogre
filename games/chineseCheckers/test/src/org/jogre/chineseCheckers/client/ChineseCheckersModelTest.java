/*
 * JOGRE (Java Online Gaming Real-time Engine) - ChineseCheckers
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
package org.jogre.chineseCheckers.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

import org.jogre.client.awt.AbstractHexBoards.AbstractStarHexBoardComponent;

import java.awt.Point;

import java.util.Vector;

/**
 * Test case for game model for the chineseCheckers game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class ChineseCheckersModelTest extends TestCase {

	// The size of the board (rows & cols)
	private int numRows, numCols;

	// The existArray keeps track of which spaces in the array actually
	// exist as spaces on the board.
	private boolean [][] existArray;

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make our own exists array & board sizes.
		existArray = AbstractStarHexBoardComponent.makeExistsArray(4);
		numCols = existArray.length;
		numRows = existArray[0].length;

		// Make a model to test.
		ChineseCheckersModel model = new ChineseCheckersModel(2, false);

		// Make some moves and test them.
		moveTestShort (model);

		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test making some moves on the board using short jump rules.
	 */
	private void moveTestShort (ChineseCheckersModel model) throws Exception {
		// Set valid moves for marble at 3,5
		model.setValidSpaces(new Point (3,5));

		// Only 2 spaces should be valid: (4,5) & (4,6)
		assertTrue (model.getMoveVector(4,5) != null);
		assertTrue (model.getMoveVector(4,6) != null);
		assertEquals (model.getMoveVector(4,5).size(), 2);
		assertEquals (model.getMoveVector(4,6).size(), 2);

		// Move the marble to (4,5)
		model.makeMove(model.getMoveVector(4,5));

		// Set valid moves for marble at (1,6)
		model.setValidSpaces(new Point (1,6));

		// Only 2 spaces should be valid: (3,5) & (5,4)
		assertTrue (model.getMoveVector(3,5) != null);
		assertTrue (model.getMoveVector(5,4) != null);
		assertEquals (model.getMoveVector(3,5).size(), 2);
		assertEquals (model.getMoveVector(5,4).size(), 3);

		// Move the marble to (5,4)
		model.makeMove(model.getMoveVector(5,4));
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (ChineseCheckersModel srcModel) throws Exception {
		int numPlayers = srcModel.getNumPlayers();
		boolean longJumps = srcModel.usesLongJumps();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		ChineseCheckersModel model = new ChineseCheckersModel (numPlayers, longJumps);
		model.setState(elm);

		// Verify that the board spaces are the same.
		for (int c=0; c < numCols; c++) {
			for (int r=0; r < numRows; r++) {
				assertEquals (srcModel.getOwner(c,r), model.getOwner(c,r));
			}
		}
	}

}
