/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
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
package org.jogre.pointsTotal.client;

import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.pointsTotal.common.PointsTotalPiece;

import junit.framework.TestCase;

import nanoxml.XMLElement;
import java.awt.Point;

/**
 * Test case for game model for the pointsTotal game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		PointsTotalTestModel model = new PointsTotalTestModel(3);

		// Test moves
		moveTest (model);
		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test making some moves.
	 */
	private void moveTest (PointsTotalTestModel model) throws Exception {
		PointsTotalPiece p = new PointsTotalPiece (0, 2, 0);

		// Make a move
		assertTrue (model.makeMove(0, p, new Point (1,1)));

		// Verify the correct score.
		assertEquals (model.getScore(0), 3);
		assertEquals (model.getScore(1), 0);
		assertEquals (model.getScore(2), 0);

		// Verify the piece is no longer available to play
		assertFalse (model.isAvailToPlay(0, 2));
		assertTrue  (model.isAvailToPlay(1, 2));
		assertTrue  (model.isAvailToPlay(2, 2));
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (PointsTotalTestModel srcModel) throws Exception {
		int numPlayers = srcModel.getNumPlayers();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		PointsTotalTestModel model = new PointsTotalTestModel(numPlayers);
		model.setState(elm);

		// Verify that the board pieces are the same.
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				assertEquals (srcModel.getPiece(i,j), model.getPiece(i,j));
			}
		}

		// Verify that the available to play arrays are the same.
		for (int p=0; p<numPlayers; p++) {
			for (int v=0; v<4; v++) {
				assertEquals (srcModel.isAvailToPlay(p, v), model.isAvailToPlay(p, v));
			}
		}

		// Verify scores are the same.
		for (int p=0; p<numPlayers; p++) {
			assertEquals (srcModel.getScore(p), model.getScore(p));
		}

		// Verify misc. stuff are the same.
		assertEquals (srcModel.getCurrentPlayer(), model.getCurrentPlayer());
		assertEquals (srcModel.getFirstPlayer(), model.getFirstPlayer());
		assertEquals (srcModel.getTurnsInRound(), model.getTurnsInRound());

	}

	/**
	 * Compare two pieces to determine if they are equal.
	 */
	private void assertEquals (PointsTotalPiece p1, PointsTotalPiece p2)
	throws Exception {
		assertEquals (p1.owner, p2.owner);
		assertEquals (p1.value, p2.value);
		assertEquals (p1.rotation, p2.rotation);
	}
}
