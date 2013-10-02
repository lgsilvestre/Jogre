/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
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
package org.jogre.octagons.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

/**
 * Test case for game model for the octagons game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class OctagonsModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		OctagonsTestModel model = new OctagonsTestModel();

		// Verify the initial state of the model.
		int unclaimedSquares = (OctagonsModel.ROWS - 1) * (OctagonsModel.ROWS - 1);
		assertEquals (model.pieRuleInForce(), false);
		assertEquals (model.alreadyPlayedSquare(), false);
		assertEquals (model.getCurrentMoveNum(), 0);
		assertEquals (model.getCurrentTurnNum(), 0);
		assertEquals (model.getNumUnclamiedSquares(), unclaimedSquares);

		// Make some moves
		doMoveTest (model, OctagonsModel.PLAYER_ONE, 2, 2, 0);
		assertEquals (model.pieRuleInForce(), true);
		assertEquals (model.getCurrentMoveNum(), 1);
		assertEquals (model.getCurrentTurnNum(), 1);

		doMoveTest (model, OctagonsModel.PLAYER_TWO, 2, 2, 1);
		assertEquals (model.pieRuleInForce(), false);
		assertEquals (model.getCurrentMoveNum(), 2);
		assertEquals (model.getCurrentTurnNum(), 2);

		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test if the given move is valid and make the move.
	 */
	private void doMoveTest (OctagonsTestModel model, int playerId, int i, int j, int e)
	throws Exception {
		OctLoc l = new OctLoc (i,j,e);
		assertTrue (model.valid_play (l));
		assertTrue (model.make_play(l, playerId));

		assertEquals (model.get_last_move_loc(0), l);
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (OctagonsTestModel srcModel) throws Exception {

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		OctagonsTestModel model = new OctagonsTestModel();
		model.setState(elm);

		// Verify that the boards are the same.
		for (int i=0; i<OctagonsModel.ROWS; i++) {
			for (int j=0; j<OctagonsModel.ROWS; j++) {
				for (int e=0; e<3; e++) {
					assertEquals (srcModel.get_owner(i,j,e), model.get_owner(i,j,e));
					assertEquals (srcModel.get_island_id(i,j,e), model.get_island_id(i,j,e));
				}
			}
		}

		assertEquals (srcModel.alreadyPlayedSquare(), model.alreadyPlayedSquare());
		assertEquals (srcModel.pieRuleInForce(), model.pieRuleInForce());
		assertEquals (srcModel.getCurrentMoveNum(), model.getCurrentMoveNum());
		assertEquals (srcModel.getCurrentTurnNum(), model.getCurrentTurnNum());
		assertEquals (srcModel.getNumUnclamiedSquares(), model.getNumUnclamiedSquares());
	}

	/**
	 * Compare two locations to determine if they are equal.
	 */
	private void assertEquals (OctLoc loc1, OctLoc loc2) throws Exception {
		assertTrue (loc1.equals(loc2));
	}

}
