/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.spades.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.games.Card;
import org.jogre.common.games.Deck;
import org.jogre.common.util.JogreLogger;

/**
 * The spades server controller has several table variables that it needs to
 * keep track of. This spades table model is a convenient way to hold all
 * variables for a specific table. This model includes the hands of all players
 * with the deck is dealt. Keeping track of the current round's played cards.
 * Keeping track of what round it is. Keeping track of how many bids and bags,
 * etc.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class SpadesServerTableModel extends JogreModel {

	// constant for game over
	private static final int GAME_OVER_POINTS = 500;

	// one round of hands delt to players
	private Deck[] hands = null;

	// current round of cards on table
	private Card[] cards = null;

	// true or false if the player has requested their hand
	private boolean[] requestedHands = null;

	// the bids of all players by seat number
	private int[] bids = null;

	// the tricks of all players by seat number
	private int[] tricks = null;

	// current bags of all players by seat number
	private int[] bags = null;

	// current score of all players by seat number
	private int[] score = null;

	// hands that have been played
	private int hand = 0;

	// round that have been played
	private int round = 0;

	// first card played in hand
	private Card firstCardPlayedInHand = null;

	// seat number of first card played in hand
	private int seatFirstPlayed = 0;

	private int dealerSeat = 0;

	// logger for debugging purposes
	private JogreLogger logger = new JogreLogger(this.getClass());

	/**
	 * Contructor that requires table number. Each spades table should contain
	 * one of these server objects in order to keep all variables in server
	 * memory.
	 */
	public SpadesServerTableModel() {
		this.reset();
	}

	/**
	 * Reset all variables.
	 */
	public void reset() {
		this.round = 0;
		this.resetRound();
		this.resetHand();
		this.bags = new int[2];
		this.score = new int[2];
		this.dealerSeat = 0;
	}

	/**
	 * Reset variables when round is over.
	 */
	public void resetRound() {
		this.hand = 0;
		this.hands = new Deck[4];
		this.requestedHands = new boolean[4];
		this.bids = new int[4];
		this.tricks = new int[4];

		dealHands();
	}

	/**
	 * Reset variables when hand is over.
	 */
	public void resetHand() {
		this.cards = new Card[4];
		this.firstCardPlayedInHand = null;
		this.seatFirstPlayed = -1;
	}

	/**
	 * Deal hands for all players.
	 */
	public synchronized void dealHands() {

		Deck deck = new Deck();
		deck.loadStandardDeck();
		deck.shuffle();
		deck.shuffle();
		deck.shuffle();

		for (int h = 0; h < 4; h++)
			hands[h] = new Deck();

		for (int c = 0; c < 13; c++) {
			for (int h = 0; h < 4; h++) {
				hands[h].addCard(deck.deal());
			}
		}

	}

	/**
	 * Request a specific player's hand.
	 *
	 * @param index
	 *            Seat number
	 * @return a players hand of cards
	 */
	public Deck requestHand(int seatNum) {
		this.requestedHands[seatNum] = true;
		return this.hands[seatNum];
	}

	/**
	 * Place a bid for a particular player
	 *
	 * @param index
	 *            Seat number
	 * @param bid
	 *            Bid for game
	 */
	public void bid(int seatNum, int bid) {
		this.bids[seatNum] = bid;
	}

	/**
	 * Plays a card on the table and assigns it in memory. This method is needed
	 * to keep track of when the round is over and determining the first card
	 * played in the round.
	 *
	 * @param index
	 *            Seat number
	 * @param card
	 *            Card played
	 */
	public void playCard(int seatNum, Card card) {
		if (cardsPlayed() == 0) {
			this.firstCardPlayedInHand = card;
			this.seatFirstPlayed = seatNum;
		}
		this.cards[seatNum] = card;
	}

	/**
	 * Gets the number of cards played in round
	 *
	 * @return the number of cards played in round
	 */
	public int cardsPlayed() {
		int count = 0;
		for (int i = 0; i < this.cards.length; i++) {
			if (this.cards[i] != null)
				count++;
		}
		return count;
	}

	/**
	 * Sets the next round of play by incrementing the number of rounds and
	 * reseting all current round variables.
	 */
	public void nextRound() {
		this.round++;
		resetRound();
	}

	/**
	 * Get current round number
	 *
	 * @return current round number
	 */
	public int getRound() {
		return this.round;
	}

	/**
	 * Sets the next hand of play by incrementing the number of hands played and
	 * reseting all current hand variables.
	 */
	public void nextHand() {
		this.hand++;
		resetHand();
	}

	/**
	 * Get current hand number
	 *
	 * @return current hand number
	 */
	public int getHand() {
		return this.hand;
	}

	/**
	 * Checks to see if round is over by seeing if all cards have been received
	 * from all players.
	 *
	 * @return a true or false if the round is over
	 */
	public boolean isHandOver() {
		for (int i = 0; i < this.cards.length; i++) {
			if (this.cards[i] == null)
				return false;
		}
		return true;
	}

	/**
	 * Gets seat that won the round. Assumes isRoundOver() is called before
	 * executing.
	 *
	 * @return seat of round winner.
	 */
	public int getTrickFromRound() {
		int seatWithMaxCard = this.seatFirstPlayed;
		Card maxCard = this.firstCardPlayedInHand;

		for (int i = 0; i < 4; i++) {
			if (i != seatWithMaxCard) {
				if (this.cards[i].spadesCompareTo(maxCard) > 0) {
					if (this.cards[i].getSuit() == maxCard.getSuit()
							|| this.cards[i].getSuit() == Card.SPADE) {
						seatWithMaxCard = i;
						maxCard = this.cards[i];
					}
				}
			}
		}

		this.tricks[seatWithMaxCard]++;

		return seatWithMaxCard;
	}

	/**
	 * Calculates the score and bags for the specified team number.
	 *
	 * @param team
	 *            Team number
	 */
	public void calculateScoreAndBags(int team) {
		int team1 = 0;
		int team2 = 1;
		int score = 0;
		int bags = 1;

		int[] totals1 = getScoreAndBags(team1);
		this.bags[team1] += totals1[bags];
		this.score[team1] += totals1[score];
		if (this.bags[team1] > 9) {
			this.bags[team1] = this.bags[team1] - 10;
			this.score[team1] -= 100;
		}

		int[] totals2 = getScoreAndBags(team2);
		this.bags[team2] += totals2[bags];
		this.score[team2] += totals2[score];
		if (this.bags[team2] > 9) {
			this.bags[team2] = this.bags[team2] - 10;
			this.score[team2] -= 100;
		}
	}

	/**
	 * Gets the score and bags for the specified team number.
	 *
	 * @param team Team number
	 * @return the score and bags for the specified team number
	 */
	public int[] getScoreAndBags(int team) {
		int BID_BLIND_NIL = -1;

		int seat1 = 0;
		int seat2 = 2;
		if (team != 0) {
			seat1 = 1;
			seat2 = 3;
		}

		int[] totals = new int[2];
		int score = 0;
		int bags = 1;

		if (this.bids[seat1] <= 0) {
			int nilTotal = 100;
			if (this.bids[seat1] == BID_BLIND_NIL)
				nilTotal = 200;
			if (this.tricks[seat1] == 0)
				totals[score] += nilTotal;
			else
				totals[score] -= nilTotal;
		}
		if (this.bids[seat2] <= 0) {
			int nilTotal = 100;
			if (this.bids[seat2] == BID_BLIND_NIL)
				nilTotal = 200;
			if (this.tricks[seat2] == 0)
				totals[score] += nilTotal;
			else
				totals[score] -= nilTotal;
		}

		int bid1 = 0;
		if (this.bids[seat1] > 0)
			bid1 = this.bids[seat1];
		int bid2 = 0;
		if (this.bids[seat2] > 0)
			bid2 = this.bids[seat2];

		int totalBids = bid1 + bid2;
		int totalTricks = this.tricks[seat1] + this.tricks[seat2];

		if (totalTricks >= totalBids)
			totals[score] += (totalBids * 10) + (totalTricks - totalBids);
		else
			totals[score] -= (totalBids * 10);

		totals[bags] += (totalTricks - totalBids);

		if (totals[bags] < 0)
			totals[bags] = 0;

		return totals;
	}

	/**
	 * Returns integer based on CommGameOver telling whether the seat number
	 * won, lossed or drawed.
	 *
	 * @param seatNum
	 *            Seat number
	 * @return an integer based on CommGameOver telling whether the seat number
	 *         won, lossed or drawed.
	 */
	public int getGameStatus(int seatNum)
	{
		int team = 0;
		if (seatNum == 1 || seatNum == 3)
			team = 1;

		int otherTeam = 0;
		if (otherTeam == team)
			otherTeam = 1;

		if (this.score[team] > GAME_OVER_POINTS && this.score[team] > this.score[otherTeam])
			return IGameOver.WIN;
		else if (this.score[team] > GAME_OVER_POINTS && this.score[team] < this.score[otherTeam])
			return IGameOver.LOSE;
		else
			return IGameOver.DRAW;
	}

	/**
	 * Check if game is over
	 *
	 * @return true if game is over, false otherwise
	 */
	public boolean gameOver() {
		logger.debug("gameOver", "team1: " + score[0] + ", team 2: " + score[1]);
		if (this.score[0] > GAME_OVER_POINTS || this.score[1] > GAME_OVER_POINTS)
			return true;
		return false;
	}

	/**
	 * Sets the next dealer seat and return the seat number of the dealer. Since
	 * the deal passes to the left, the dealer seat will be incremented (0, 1,
	 * 2, 3).
	 *
	 * @return the next dealer seat
	 */
	public int nextDealerSeat() {
		this.dealerSeat++;
		if (this.dealerSeat > 3)
			this.dealerSeat = 0;
		return this.dealerSeat;
	}

	public XMLElement flatten() {
		// FIXME FILL IN
		return null;
	}

	public void setState(XMLElement message) {
		// FIXME FILL IN
	}

}