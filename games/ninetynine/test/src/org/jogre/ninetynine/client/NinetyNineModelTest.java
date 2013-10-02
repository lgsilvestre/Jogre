/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninetynine
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
package org.jogre.ninetynine.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

import org.jogre.ninetynine.server.NinetyNineServerModel;
import org.jogre.ninetynine.common.NinetyNineCoreModel;

/**
 * Test case for game model for the Ninety Nine game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class NinetyNineModelTest extends TestCase 
                                 implements IGameStateAlertee {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		NinetyNineServerModel model = new NinetyNineServerModel (3);

		// Make some moves
		moveTest (model);

		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test some moves on the game.
	 */
	private void moveTest (NinetyNineServerModel model) {
		model.shuffleAndDeal();

		// Note: ought to do more here....
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (NinetyNineServerModel srcModel) throws Exception {
		int numRounds = srcModel.getNumRoundsInGame();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		NinetyNineClientModel model = new NinetyNineClientModel (numRounds);
		model.setGameStateAlertee(this);
		model.setState(elm);

		// Verify misc. items
		assertEquals (srcModel.getNumRoundsInGame(), model.getNumRoundsInGame());
		assertEquals (srcModel.getCurrentRoundNumber(), model.getCurrentRoundNumber());
		assertEquals (srcModel.getTrumpSuit(), model.getTrumpSuit());
		assertEquals (srcModel.getCurrentPlayerId(), model.getCurrentPlayerId());
		assertEquals (srcModel.getDeclarer(), model.getDeclarer());
		assertEquals (srcModel.getRevealer(), model.getRevealer());

		assertEquals (srcModel.isPreStart(),  model.isPreStart());
		assertEquals (srcModel.isBidding(),   model.isBidding());
		assertEquals (srcModel.isPlaying(),   model.isPlaying());
		assertEquals (srcModel.isGameOver(),  model.isGameOver());
		assertEquals (srcModel.isRoundOver(), model.isRoundOver());

		// Verify player values
		for (int p=0; p<3; p++) {
			assertEquals (srcModel.getTotalScore(p), model.getTotalScore(p));
			assertEquals (srcModel.getBidType(p),    model.getBidType(p));
			assertEquals (srcModel.getBidLeader(p),  model.getBidLeader(p));
			assertEquals (srcModel.getWonTricks(p),  model.getWonTricks(p));
			assertEquals (srcModel.getPlayedCard(p), model.getPlayedCard(p));

			if ((srcModel.getBidType(p) == NinetyNineCoreModel.BID_DECLARE) ||
			    (srcModel.getBidType(p) == NinetyNineCoreModel.BID_REVEAL)) {
				// The bid cards should be the visible and the same
				assertEquals (srcModel.getBid(p), model.getBid(p));
			} else if (srcModel.getBidType(p) == NinetyNineCoreModel.BID_NORMAL) {
				// The bid cards should be unknown
				Hand clientBid = model.getBid(p);
				assertEquals (clientBid.length(), 3);
				assertTrue (allUnknown(model.getBid(p)));
			}

			if (srcModel.getBidType(p) == NinetyNineCoreModel.BID_REVEAL) {
				// The hand cards should be the visible and the same
				assertEquals (srcModel.getHand(p), model.getHand(p));
			} else if ((srcModel.getBidType(p) == NinetyNineCoreModel.BID_DECLARE) ||
			           (srcModel.getBidType(p) == NinetyNineCoreModel.BID_NORMAL)) {
				// The hand cards should be unknown
				assertTrue (allUnknown(model.getHand(p)));
			}
		}

		// Verify round values
		for (int r=0; r<numRounds; r++) {
			assertEquals (srcModel.getTrumpForRound(r), model.getTrumpForRound(r));
			for (int p=0; p<3; p++) {
				assertEquals (srcModel.getScoreForRound(p,r),
				                 model.getScoreForRound(p,r));
			}
		}

		// Verify cards are correct
		assertEquals (srcModel.getLeadCard(), model.getLeadCard());
	}

	/**
	 * Compare two cards to determine if they are equal.
	 */
	private void assertEquals (Card c1, Card c2) throws Exception {
		if (c1 == null) {
			assertTrue (c2 == null);
		} else {
			assertTrue (c1.equals(c2));
		}
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

	/**
	 * Make sure that all of the cards in the given hand are unknown.
	 */
	private boolean allUnknown (Hand h) {
		int len = h.length();
		for (int i=0; i<len; i++) {
			Card c = h.getNthCard(i);
			if (c.isKnown())
				return false;
		}
		return true;
	}

	/**
	 * Implement the function of IGameStateAlertee.  This is needed to test
	 * the setState() function of the client model.
	 */
	public void setState () {
	}
}
