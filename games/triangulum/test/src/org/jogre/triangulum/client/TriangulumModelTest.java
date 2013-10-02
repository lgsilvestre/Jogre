/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2004 - 2007-2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.client;

import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;

import nanoxml.XMLElement;

import java.awt.Point;

import junit.framework.TestCase;

/**
 * Test case for game model for the Triangulum game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		TriangulumTestModel model = new TriangulumTestModel(36, 4);

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
	private void saveRestoreTest (TriangulumTestModel srcModel) throws Exception {

		int gameFlavor = srcModel.getFlavor();
		int numPlayers = srcModel.getNumPlayers();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		TriangulumTestModel model = new TriangulumTestModel(gameFlavor, numPlayers);
		model.setState(elm);

		// Verify that the player data are the same.
		assertEquals (srcModel.getNumPiecesPerPlayer(), model.getNumPiecesPerPlayer());

		int piecesPerPlayer = srcModel.getNumPiecesPerPlayer();
		for (int p=0; p < numPlayers; p++) {
			assertEquals(srcModel.getScore(p), model.getScore(p));
			assertEquals(srcModel.getLastMove(p), model.getLastMove(p));
			for (int i=0; i < piecesPerPlayer; i++) {
				assertEquals(srcModel.getPlayerPiece(p, i), model.getPlayerPiece(p, i));
			}
		}

		// Verify that the boards are the same
		assertEquals(srcModel.getNumRows(), model.getNumRows());
		assertEquals(srcModel.getNumCols(), model.getNumCols());

		int numRows = srcModel.getNumRows();
		int numCols = srcModel.getNumCols();
		for (int r=0; r < numRows; r++) {
			for (int c=0; c < numCols; c++) {
				assertEquals(srcModel.getPieceAt(c,r), model.getPieceAt(c,r));
			}
		}

		// Verify misc. stuff
		assertEquals(srcModel.firstMove(), model.firstMove());
		assertEquals(srcModel.tilesToGo(), model.tilesToGo());
	}

	/**
	 * Compare two triangulum pieces to determine if they are equal.
	 * For this test, they must be the same orientation.
	 */
	private void assertEquals (TriangulumPiece p1, TriangulumPiece p2)
	throws Exception {
		if (p1 == null) {
			assertTrue (p2 == null);
		} else {
			assertEquals (p1.getColor(0), p2.getColor(0));
			assertEquals (p1.getColor(1), p2.getColor(1));
			assertEquals (p1.getColor(2), p2.getColor(2));
			assertEquals (p1.getValue(), p2.getValue());
		}
	}
}
