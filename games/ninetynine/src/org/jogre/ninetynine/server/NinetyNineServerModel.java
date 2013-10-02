/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.ninetynine.server;

import org.jogre.ninetynine.common.NinetyNineCoreModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

// Server model for game of NinetyNine
public class NinetyNineServerModel extends NinetyNineCoreModel {

	// The deck of cards
	private Card [] deck;

	// Temporary array used during dealing cards.
	private Card [] tempCardArray;

	// Random generator used to pick trump suits
	Random rand;

	/**
	 * Constructor for the model
	 */
	public NinetyNineServerModel(int roundsInGame) {
		super(roundsInGame);
		
		// Create the deck with all of the cards
		deck = new Card[36];
		int i=0;
		for (int v=6; v<15; v++) {
			for (int s=0; s<4; s++) {
				deck[i] = new Card (s, v);
				i += 1;
			}
		}

		// This temporary array is used during dealing cards.
		tempCardArray = new Card[12];

		rand = new Random();

		resetGame();
	}

	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame() {
		super.resetGame();
	}

	/**
	 * Provide a new bid for a player.
 	 * Overriding this method from CoreModel so that we can remove the
	 * bid cards from the player's hand.
	 *
	 * @param	playerId	The player whose bid is being given
	 * @param	newBid		The new bid hand for the player
	 * @param	bidType		The type of bid
	 * @param	leadPlayer	The player who should lead, in case of a Reveal bid
	 */
	public void giveBid(int playerId, Hand newBid, int bidType, int leadPlayer) {
		super.giveBid(playerId, newBid, bidType, leadPlayer);

		// Invisiblize the cards that were bid from the player's hand
		for (int i=0; i<3; i++) {
			hands[playerId].invisiblizeCard(newBid.getNthCard(i));
		}
	}

	/**
	 * Shuffle the deck and deal out the cards to the players.
	 * This also picks a trump suit for the hand.
	 *
	 */
	public void shuffleAndDeal() {
		// Shuffle the deck
		Collections.shuffle(Arrays.asList(deck));

		// Give each player his 12 cards
		for (int i=0; i<3; i++) {
			System.arraycopy(deck, i*12, tempCardArray, 0, 12);
			giveHand(i, tempCardArray);
		}

		// Pick a trump suit.
		// There is 9/37 chance for each of the real suits, and 1/37 change for no-trump
		int trump = rand.nextInt(37);
		super.setTrumpSuit(trump / 9);
	}

	/**
	 * Override the core model's routine for evaluating the current trick so
	 * that we can do more stuff.
	 *
	 * Evaluate the current trick to determine who gets it and becomes the new leader.
	 * Set the currentPlayerId to the player who will be playing next.
	 *
	 * @return false = trick not over yet.
	 *         true = trick is over.  currentPlayerId is set to the winner.
	 */
	public boolean evaluateTrick() {
		boolean value = super.evaluateTrick();
		if (value) {
			// The trick is over, so let's do some clean-up and get ready for the next trick.
			super.getReadyForNextTrick();
		}

		return value;
	}
}
