/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

import org.jogre.abstrac.std.Hand;

/**
 * Test case for game model for the abstrac game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class AbstracModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		AbstracModel model = new AbstracModel();

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
	private void saveRestoreTest (AbstracModel srcModel) throws Exception {
		int numPlayers = AbstracModel.NUM_PLAYERS;
		int numValues = AbstracModel.NUM_VALUES;
		int numSuits = AbstracModel.NUM_SUITS;

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		AbstracModel model = new AbstracModel();
		model.setState(elm);

		// Verify that the scores are the same
		for (int p = 0; p < numPlayers; p++) {
			assertEquals (srcModel.getTotalScore(p), model.getTotalScore(p));
			assertEquals (srcModel.getNumCards(p),   model.getNumCards(p));
			assertEquals (srcModel.getSumScore(p),   model.getSumScore(p));
			for (int v = 0; v < numValues; v++) {
				assertEquals (srcModel.getSetScore(p, v), model.getSetScore(p, v));
			}
			for (int s = 0; s < numSuits; s++) {
				assertEquals (srcModel.getRunScore(p, s), model.getRunScore(p, s));
			}
		}

		// Verify that the player's taken arrays are the same
		for (int p = 0; p < numPlayers; p++) {
			for (int v = 0; v < numValues; v++) {
				for (int s = 0; s < numSuits; s++) {
					assertEquals (srcModel.getPlayerCardCode(p, s, v),
					              model.getPlayerCardCode(p, s, v));
				}
			}
		}

		// Verify that the hand of cards is the same
		assertEquals (srcModel.getHand(), model.getHand());
	}

	/**
	 * Compare two hands of cards to determine if they are equal.
	 * For this test, they must contain the same cards in the same order.
	 */
	private void assertEquals (Hand h1, Hand h2) throws Exception {
		// Convert the hands into two arrays of suits & values
		int [][] c1 = h1.extractSuitsAndValues();
		int [][] c2 = h2.extractSuitsAndValues();

		assertEquals (c1.length, c2.length);

		for (int i=0; i < c1.length; i++) {
			assertEquals (c1[i].length, c2[i].length);

			for (int j=0; j < c1[i].length; j++) {
				assertEquals (c1[i][j], c2[i][j]);
			}
		}
	}
}
