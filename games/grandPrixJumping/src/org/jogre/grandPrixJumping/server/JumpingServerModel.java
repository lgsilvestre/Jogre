/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping Server
 * Copyright (C) 2006-2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.grandPrixJumping.server;

import org.jogre.common.JogreModel;

import org.jogre.grandPrixJumping.common.JumpingCoreModel;
import org.jogre.grandPrixJumping.common.JumpingCard;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.ListIterator;

import nanoxml.XMLElement;

/**
 * Model which holds the data for a game of Grand Prix Jumping
 * This is the server model which knows all of the game.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingServerModel extends JumpingCoreModel {

	// Declare various constants

	// The deck & discard piles
	Vector deck, discard;

	/**
	 * Constructor for the server model.
	 * A new table is created every time a new game starts.
	 *
	 * @param	openHands		Indicates if the hands will be open or not
	 * @param	allowEdits		Indicates if board editing is allowed or not
	 * @param	initialLayout	The initial fence layout string
	 */
	public JumpingServerModel(boolean openHands, boolean allowEdits, String initialLayout) {
		super(openHands, allowEdits, initialLayout);

		// Create things

		// Create the deck & discard piles.
		// Note: The deck is initially empty, and all cards are in the discard
		//       pile.  This way, when the first card is needed, the discard
		//       pile will be reshuffled into the deck.
		deck = new Vector();
		discard = createDeck();
	}

	private static final int [][] deckConfig = {
		{5, JumpingCard.CANTER, 1},
		{5, JumpingCard.CANTER, 2},
		{3, JumpingCard.CANTER, 3},
		{2, JumpingCard.CANTER, 4},
		{4, JumpingCard.HEIGHT, 1},
		{5, JumpingCard.HEIGHT, 2},
		{4, JumpingCard.HEIGHT, 3},
		{1, JumpingCard.HEIGHT, 4},
		{1, JumpingCard.HEIGHT, 5},
		{4, JumpingCard.LENGTH, 1},
		{6, JumpingCard.LENGTH, 2},
		{3, JumpingCard.LENGTH, 3},
		{1, JumpingCard.LENGTH, 4},
		{1, JumpingCard.LENGTH, 5},
		{7, JumpingCard.SPECIAL, JumpingCard.RIBBON},
		{11, JumpingCard.SPECIAL, JumpingCard.SADDLE},
		{2, JumpingCard.IMMEDIATE, JumpingCard.FAULT_1_4},
		{2, JumpingCard.IMMEDIATE, JumpingCard.FAULT_2_4},
		{2, JumpingCard.IMMEDIATE, JumpingCard.FAULT_3_4},
		{5, JumpingCard.IMMEDIATE, JumpingCard.STABLE},
		{4, JumpingCard.IMMEDIATE, JumpingCard.DUAL_RIDER},
		{2, JumpingCard.IMMEDIATE, JumpingCard.OFFICIAL}
	};

	/**
	 * This routine will create a deck of cards.
	 */
	private Vector createDeck() {
		Vector newDeck = new Vector();

		// Populate the deck with all of the cards
		for (int i=0; i < deckConfig.length; i++) {
			for (int c=0; c < deckConfig[i][0]; c++) {
				newDeck.add(new JumpingCard(deckConfig[i][1], deckConfig[i][2]));
			}
		}

		// Return the new deck
		return newDeck;
	}

	/**
	 * Get the next card from the top of the deck.  If the deck is now empty,
	 * then the discard pile is shuffled and becomes the new deck.
	 */
	public JumpingCard getNextCard() {
		if (deck.size() == 0) {
			deck = discard;
			Collections.shuffle(deck);

			discard = new Vector();
		}

		JumpingCard topCard = (JumpingCard) deck.firstElement();
		deck.remove(0);

		return topCard;
	}

	/**
	 * Return a vector of new cards from the top of the deck.
	 *
	 * @param	n		The number of cards to return in the vector.
	 * @return a vector of the next n cards.
	 */
	public Vector getNNewCards(int n) {
		Vector newCards = new Vector();
/*
		if (n == 7) {
			// For testing, return the same 7 cards:
			newCards.add(new JumpingCard(JumpingCard.SPECIAL, JumpingCard.SADDLE));
			newCards.add(new JumpingCard(JumpingCard.CANTER, 2));
			newCards.add(new JumpingCard(JumpingCard.HEIGHT, 5));
			newCards.add(new JumpingCard(JumpingCard.HEIGHT, 2));
			newCards.add(new JumpingCard(JumpingCard.SPECIAL, JumpingCard.RIBBON));
			newCards.add(new JumpingCard(JumpingCard.SPECIAL, JumpingCard.RIBBON));
			newCards.add(new JumpingCard(JumpingCard.IMMEDIATE, JumpingCard.FAULT_1_4));
			return newCards;
		}
*/	
		// Pull the next n cards from the deck.
		for (int i=0; i<n; i++) {
			newCards.add(getNextCard());
		}

		return newCards;
	}

	/**
	 * Put the given card into the discard pile.
	 *
	 * @param	oldCard			The card to put onto the discard pile.
	 */
	public void moveToDiscard(JumpingCard oldCard) {
		// We don't put stable cards or fake saddle cards into the discard pile.
		if (!oldCard.isStable() && !oldCard.isFakeSaddle()) {
			discard.add(oldCard);
		}
	}

	/**
	 * Put the given vector of cards into the discard pile.
	 * This will not put stable cards into the discard pile.  Once they are
	 * played, they are removed for good.
	 *
	 * @param cards				The vector of cards to discard.
	 */
	public void moveToDiscard(Vector cards) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			moveToDiscard((JumpingCard) iter.next());
		}
	}

	/**
	 * Move all of the cards in both immediate piles into the discard pile.
	 */
	public void discardImmediates() {
		moveToDiscard(getImmediateHand(0));
		moveToDiscard(getImmediateHand(1));
		clearImmediateHand(0);
		clearImmediateHand(1);
	}

	/**
	 * Return the current size of the deck.
	 */
	public int getDeckSize() {
		return deck.size();
	}

	/**
	 * Override the flatten() method so that we can add our special non-core values
	 * to send the state to a client when it is attaching.
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = super.flatten();

		state.setIntAttribute("deckSize", deck.size());

		return state;
	}

}
