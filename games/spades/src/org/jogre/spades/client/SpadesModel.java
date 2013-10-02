/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.spades.client;

import nanoxml.XMLElement;

import org.jogre.client.IJogreModel;
import org.jogre.common.JogreModel;
import org.jogre.common.games.Card;
import org.jogre.common.games.Deck;
import org.jogre.common.util.JogreLogger;

/**
 * Spades model which extends the JogreModel contains a deck of cards.
 *
 * @author Garrett Lehman
 * @version Alpha 0.2.3
 */
public class SpadesModel extends JogreModel {

	// Game states
	public static final int GAME_STATE_PRE_HAND = 0;
	public static final int GAME_STATE_BIDDING = 1;
	public static final int GAME_STATE_PLAYING = 2;
	public static final int GAME_STATE_WAITING_TO_PLAY = 3;
	public static final int GAME_STATE_FINISHED_ROUND = 4;

	// Current score by team
	private int[] score = null;

	// Current bags by team
	private int[] bags = null;

	// Current round number
	private int round = 0;

	// Current game state for current player
	private int gameState = GAME_STATE_PRE_HAND;

	// Current hand of cards
	private Deck hand = null;

	// Cards that are on table
	private Card[] table = null;

	// Current bids by seat index (@see getSeatIndex())
	private int[] bids = null;

	// Current tricks by seat index (@see getSeatIndex())
	private int[] tricks = null;

	// boolean array of cards played from current hand
	private boolean[] cardsPlayed = null;

	// boolean array of cards selected from current hand
	private boolean[] cardsSelected = null;

	// card selected (used to check if previous == current selected)
	private int cardSelected = -1;

	// last card selected (used to check if previous == current selected)
	private int lastCardSelected = -1;

	// first card played on table
	private Card firstCardPlayedOnTable = null;

	// has the round been spaded yet
	private boolean spadedRound = false;

	// seat indexs (0,1,2,3 = bottom, left, top, right) assoc to seatNum
	private int[] seatIndex = null;

	/**
	 * Default constructor
	 */
	public SpadesModel() {
		super(IJogreModel.GAME_TYPE_TURN_BASED);
		reset();
	}

	/**
	 * Reset all game variables
	 *
	 * @see org.jogre.common.JogreModel#reset()
	 */
	public void reset() {
		this.score = new int[2];
		this.bags = new int[2];
		this.round = -1;
		this.seatIndex = null;

		resetRound();
		resetHand();
	}

	/**
	 * Reset all round variables
	 */
	public void resetRound() {
		this.round++;
		this.hand = null;
		this.bids = new int[4];
		this.tricks = new int[4];
		this.cardsPlayed = new boolean[13];
		this.cardsSelected = new boolean[13];
		this.cardSelected = -1;
		this.lastCardSelected = -1;
		this.spadedRound = false;

		for (int i = 0; i < this.bids.length; i++)
			this.bids[i] = SpadesBiddingComponent.BID_NO_BID;
	}

	/**
	 * Reset all hand variables
	 */
	public void resetHand() {
		this.table = new Card[4];
		this.firstCardPlayedOnTable = null;

		refreshObservers();
	}

	/**
	 * Check whether all cards have been played on the table
	 *
	 * @return true if all cards have been played on the table, otherwise false.
	 */
	public boolean cardAlreadyPlayedInCurrentHand() {
		if (this.table == null)
			return false;
		return this.table[0] != null;
	}

	/**
	 * Set hand for current player
	 *
	 * @param hand
	 *            Hand of cards
	 */
	public void setHand(Deck hand) {
		this.hand = hand;
		this.hand.sort();
		this.cardsPlayed = new boolean[13];
		this.turnOverHand(false);
	}

	/**
	 * Get hand of cards from current player
	 *
	 * @return a hand of cards
	 */
	public Deck getHand() {
		return this.hand;
	}

	/**
	 * Get cards played from current player (booleans)
	 *
	 * @return cards played (booleans)
	 */
	public boolean[] getCardsPlayed() {
		return this.cardsPlayed;
	}

	/**
	 * Get cards selected from current player (booleans)
	 *
	 * @return cards selected (booleans)
	 */
	public boolean[] getCardsSelected() {
		return this.cardsSelected;
	}

	/**
	 * Get last selected card from current player
	 *
	 * @return last selected card
	 */
	public int getLastSelectedCard() {
		return this.lastCardSelected;
	}

	/**
	 * Face all cards in hand upright for current player
	 *
	 * @param faceUp
	 *            If true, then face all cards up, else face all cards down.
	 */
	public void turnOverHand(boolean faceUp) {
		if (this.hand != null) {
			int cards = this.hand.size();
			for (int i = 0; i < cards; i++)
				this.hand.get(i).setFaceUp(faceUp);
		}
		refreshObservers();
	}

	/**
	 * Get number of cards left in hand for current player (un-played cards)
	 *
	 * @return number of cards left in hand
	 */
	public int getNumOfCardsLeftInHand() {
		if (this.hand == null)
			return 0;

		int cardsLeft = 0;
		for (int i = 0; i < 13; i++) {
			if (!this.cardsPlayed[i])
				cardsLeft++;
		}
		return cardsLeft;
	}

	/**
	 * Checks whether the handIndex provided is a valid card to play at the
	 * moment.
	 *
	 * @param handIndex
	 *            Index of card in hand
	 * @return true if the handIndex provided is a valid card to play at the
	 *         moment, false otherwise
	 */
	public boolean validCard(int handIndex) {
		if (this.hand == null || handIndex < 0 || handIndex >= this.hand.size())
			return false;

		Card card = this.hand.get(handIndex);

		if (this.firstCardPlayedOnTable != null) {
			char suit = this.firstCardPlayedOnTable.getSuit();
			if (card.getSuit() != suit) {
				// Check if there is a card in hand correct suit
				int size = this.hand.size();
				for (int i = 0; i < size; i++) {
					if (!this.cardsPlayed[i] && i != handIndex) {
						Card handCard = this.hand.get(i);
						if (handCard.getSuit() == suit)
							return false;
					}
				}
			}
		} else if (!this.spadedRound && !handOnlyContainsSpades()
				&& card.getSuit() == Card.SPADE)
			return false;

		// By the time we reach here, the card is valid
		if (card.getSuit() == Card.SPADE)
			this.spadedRound = true;

		return true;
	}

	public boolean handOnlyContainsSpades() {
		int size = this.hand.size();
		for (int i = 0; i < size; i++) {
			if (!this.cardsPlayed[i]) {
				Card handCard = this.hand.get(i);
				if (handCard.getSuit() != Card.SPADE)
					return false;
			}
		}
		return true;
	}


	/**
	 * Select card from hand
	 *
	 * @param index
	 *            Index of card in hand
	 */
	public boolean selectCard(int handIndex) {
		this.cardsSelected[handIndex] = true;
		this.lastCardSelected = this.cardSelected;
		this.cardSelected = handIndex;
		refreshObservers();
		return true;
	}

	/**
	 * Unselect card from hand
	 *
	 * @param index
	 *            Index of card in hand
	 */
	public void unselectCard(int handIndex) {
		this.cardsSelected[handIndex] = false;
		refreshObservers();
	}

	/**
	 * Unselect all cards from hand
	 *
	 * @param index
	 *            Index of card in hand
	 */
	public void unselectAllCards() {
		int length = this.cardsSelected.length;
		this.cardsSelected = new boolean[length];
		refreshObservers();
	}

	/**
	 * Get cards that are on table
	 *
	 * @return cards that are on table
	 */
	public Card[] getTableCards() {
		return this.table;
	}

	/**
	 * Get bid by seat number
	 *
	 * @param seatNum
	 *            Seat number
	 * @return a bid by the seat number provided
	 */
	public int getBid(int seatNum) {
		if (seatNum < 0)
			return SpadesBiddingComponent.BID_NO_BID;

		return this.bids[this.getSeatIndex(seatNum)];
	}

	/**
	 * Get tricks by seat number
	 *
	 * @param seatNum Seat number
	 * @return tricks by the seat number provided
	 */
	public int getTricks(int seatNum) {
		if (seatNum < 0)
			return 0;

		return this.tricks[this.getSeatIndex(seatNum)];
	}

	/**
	 * Play card (should only be called for current user)
	 *
	 * @param index
	 *            Index of card in hand to play
	 */
	public void playCard(int handIndex, int seatNum) {
		this.cardsPlayed[handIndex] = true;
		this.playCardOnTable(seatNum, this.hand.get(handIndex));
	}

	/**
	 * Play card on table
	 *
	 * @param seatNum
	 *            Seat number
	 * @param card
	 */
	public void playCardOnTable(int seatNum, Card card) {
		if (this.cardsPlayedOnTable() == 0)
			this.firstCardPlayedOnTable = card;
		this.table[this.getSeatIndex(seatNum)] = card;

		refreshObservers();
	}

	/**
	 * Make bid on table
	 *
	 * @param seatNum
	 *            Seat number
	 * @param bid
	 *            Bid
	 */
	public void makeBidOnTable(int seatNum, int bid) {
		this.bids[this.getSeatIndex(seatNum)] = bid;

		refreshObservers();
	}

	/**
	 * Make trick on table
	 *
	 * @param seatNum
	 *            Seat number
	 * @param trick
	 *            Trick
	 */
	public void makeTrickOnTable(int seatNum, int trick) {
		this.tricks[this.getSeatIndex(seatNum)] = trick;

		refreshObservers();
	}

	/**
	 * Get number of cards played on table
	 *
	 * @return number of cards played on table
	 */
	public int cardsPlayedOnTable() {
		int count = 0;
		for (int i = 0; i < this.table.length; i++) {
			if (this.table[i] != null)
				count++;
		}
		return count;
	}

	/**
	 * Check whether all bids have been made
	 *
	 * @return true if all bids have been made, false otherwise
	 */
	public boolean allBidsHaveBeenMade() {
		for (int i = 0; i < this.bids.length; i++) {
			if (this.bids[i] == SpadesBiddingComponent.BID_NO_BID)
				return false;
		}
		return true;
	}

	/**
	 * Get card from hand by index
	 *
	 * @param index
	 *            Index of card in hand
	 * @return card from hand by index
	 */
	public Card getCardFromHand(int index) {
		return this.hand.get(index);
	}

	/**
	 * Set game state for current player
	 *
	 * @param gameState Game state defined by SpadesModel
	 */
	public void setGameState(int gameState) {
		this.gameState = gameState;
	}

	/**
	 * Get game state (defined by SpadesModel)
	 *
	 * @return game state (defined by SpadesModel)
	 */
	public int getGameState() {
		return this.gameState;
	}

	/**
	 * Checks whether seat indexes have been assigned (basically if all seat
	 * have been satten into)
	 *
	 * @return true if all seat have been indexed, false otherwise
	 */
	public boolean seatIndexesSet() {
		return this.seatIndex != null;
	}

	/**
	 * Get seat index by seat number. Seats are indexed by 0,1,2,3 <=>
	 * bottom,left,top,right. This was designed so the current player is always
	 * at the bottom position.
	 *
	 * @param seatNum
	 *            Seat number
	 * @return seat index by seat number
	 */
	public int getSeatIndex(int seatNum) {
		for (int i = 0; i < 4; i++) {
			if (this.seatIndex[i] == seatNum)
				return i;
		}
		return -1;
	}

	/**
	 * Set seat indexes by providing the current player's seat number and
	 * setting indexes accordingly.
	 *
	 * @param seatNum
	 *            current player's seat number
	 */
	public void setSeatIndexes(int seatNum) {
		// set seat indexes from 0, 1, 2, 3 (bottom, left, top, left)
		this.seatIndex = new int[4];
		int i = 0;
		while (i < 4) {
			this.seatIndex[i++] = seatNum++;
			if (seatNum > 3)
				seatNum = 0;
		}
	}

	/**
	 * Set score for team
	 *
	 * @param score Team score (team 1 = 0,2 and team 2 = 1,3)
	 */
	public void setScore(int[] score) {
		this.score = score;
	}

	/**
	 * Set bags for team
	 *
	 * @param bags Team bags (team 1 = 0,2 and team 2 = 1,3)
	 */
	public void setBags(int[] bags) {
		this.bags = bags;
		for (int i = 0; i < 2; i++) {
			while (this.bags[i] > 9) {
				this.bags[i] -= 10;
				this.score[i] -= 100;
			}
		}
	}

	/**
	 * Gets current scores
	 *
	 * @return current scores in an array by team (0 = team 1, 1 = team 2)
	 */
	public int[] getScore() {
		return this.score;
	}

	/**
	 * Gets current bags
	 *
	 * @return current bags in an array by team (0 = team 1, 1 = team 2)
	 */
	public int[] getBags() {
		return this.bags;
	}

	public XMLElement flatten() {
		// FIXME FILL IN
		return null;
	}

	public void setState(XMLElement message) {
		// FIXME FILL IN		
	}
}