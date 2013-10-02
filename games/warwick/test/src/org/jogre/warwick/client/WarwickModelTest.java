/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
 * Copyright (C) 2004 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.warwick.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

import org.jogre.warwick.common.WarwickModel;

/**
 * Test case for game model for the warwick game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		WarwickModel model = new WarwickModel (3);

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
	private void saveRestoreTest (WarwickModel srcModel) throws Exception {
		int numPlayers = srcModel.getNumPlayers();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		WarwickModel model = new WarwickModel (numPlayers);
		model.setState(elm);

		// Verify that the boards are the same
		for (int r = 0; r < 4; r++) {
			for (int s = 0; s < 15; s++) {
				assertEquals (srcModel.getOwnerAt(r, s), model.getOwnerAt(r, s));
			}
		}

		// Verify the players are in the same state
		for (int p = 0; p < numPlayers; p++) {
			assertEquals (srcModel.getScore(p), model.getScore(p));
			assertEquals (srcModel.getPiecesToPlace(p), model.getPiecesToPlace(p));
			assertEquals (srcModel.isAllegienceChosen(p), model.isAllegienceChosen(p));
			assertEquals (srcModel.getLastOwnScore(p), model.getLastOwnScore(p));
			assertEquals (srcModel.getLastAllyScore(p), model.getLastAllyScore(p));
			assertEquals (srcModel.getLastAllegience(p), model.getLastAllegience(p));
		}

		// Verify misc. other stuff
		assertEquals (srcModel.getActivePlayerSeatNum(), model.getActivePlayerSeatNum());
		assertEquals (srcModel.getGamePhase(), model.getGamePhase());
		assertEquals (srcModel.getCurrentRoundNumber(), model.getCurrentRoundNumber());
	}

}
