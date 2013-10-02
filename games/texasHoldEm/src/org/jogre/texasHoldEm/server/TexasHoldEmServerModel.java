/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.server;

import java.util.Collections;
import java.util.Vector;
import java.util.ListIterator;

import org.jogre.texasHoldEm.common.TexasHoldEmCoreModel;
import org.jogre.texasHoldEm.common.Card;

/**
 * Server Model for the TexasHoldEm game
 *
 * Note: Hand scoring logic is inspired by the algorithm contained in
 *       HandEvaluator.java in the JPoker program written by Aaron Davidson,
 *       Darse Billings & Denis Papp from the University of Alberta
 *       Department of Computing Science and is available at sourceforge.net
 *       in the "JPoker Server" project.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmServerModel extends TexasHoldEmCoreModel {

	// The deck of cards for this game.
	Vector deck;

	// The time that the game began, in milliseconds
	private long gameStartTime;

	// Arrays used when scoring a hand.
	private int [] handValue;
	private int [] scoredHandValue;
	private int [] scoringValueCount = new int [15];
	private int [] scoringSuitCount = new int [4];
	private int [] scoringSetSize = new int [5];
	private boolean [][] cardExist = new boolean [4][15];
	private int flushSuit;
	private int [][] scoringSetValue = new int [5][2];

	// Keep track of whether this is the first deal or not.
	private boolean firstDealFlag = true;

	/**
	 * Constructor for the model
	 */
	public TexasHoldEmServerModel (
		int numPlayers,
		int initialBankroll,
		int initialBlindSchedule,
		int blindAdvanceTime,
		int raiseLimit
	) {
		super(numPlayers,
		      initialBankroll,
		      initialBlindSchedule,
		      blindAdvanceTime,
		      raiseLimit);

		// Create arrays.
		handValue       = new int [numPlayers];
		scoredHandValue = new int [numPlayers];

		// Create the deck of cards
		deck = new Vector ();
		for (int s=0; s<4; s++) {
			for (int v=2; v<15; v++) {
				deck.add(new Card (s, v));
			}
		}

		// Deal out initial cards to the players
		dealCards();

		gameStartTime = System.currentTimeMillis();
	}

	/**
	 * Deal out cards to all of the players
	 *
	 * Note: After shuffling the deck, this deals out all of the cards
	 *       to the players and the 5 common cards.  Therefore, the
	 *       server model knows the complete state of the game at the
	 *       time of deals.  However, it only tells the clients the values
	 *       as the game progresses.
	 */
	public void dealCards() {
		// Shuffle the deck
		Collections.shuffle(deck);
		ListIterator iter = deck.listIterator();

		// Deal each player his cards
		for (int i=0; i<numPlayers; i++) {
			if (playerIsAlive(i)) {
				super.giveCardToPlayer(i, 0, (Card) iter.next());
				super.giveCardToPlayer(i, 1, (Card) iter.next());
			}
		}

		// Deal 5 common cards
		for (int i=0; i<5; i++) {
			super.giveCommonCard(i, (Card) iter.next());
		}
	}

	/**
	 * Determine if the hand is over.
	 * The hand is over if:
	 *     - Only one player is left still bidding
	 *  or - Round is over, but this is round 3.
	 */
	public boolean isHandOver() {
		if (numberBiddingPlayers == 1) {
			return true;
		}

		if ((roundNum == 3) && isRoundOver()) {
			return true;
		}

		return false;
	}

	/**
	 * Determine if the current round is over.
	 * The round is over if the current bidder's last action was to the
	 * value of the current bid.
	 */
	public boolean isRoundOver() {
		return ((lastAction[activePlayer] != LAST_ACTION_BLANK) &&
		        (lastActionAmount[activePlayer] == currentBid));
	}

	/**
	 * Return the seat number of the winner.
	 */
	public int getWinner() {
		for (int i = 0; i < numPlayers; i++) {
			if (playerState[i] != OUT_OF_GAME) {
				return i;
			}
		}

		// Shouldn't get here!
		return 0;
	}

	/**
	 * Update the current blind schedule and determine the number of seconds
	 * until the next change to the blinds.
	 *
	 * @return the number of seconds until the next blind change stage.
	 */
	public int updateBlindTime() {
		// If there is no advancing of blinds, then nothing to do
		if (blindAdvanceTime == 0) {
			return 0;
		}

		// Compute the time elapsed in the game so far.
		long currentTime = System.currentTimeMillis();
		int gameElapsedTimeSecs = (int) ((currentTime - gameStartTime) / 1000);

		// Compute the # of blind advance periods elapsed and the offset into
		// the current elapsed time
		int advancedBlinds = gameElapsedTimeSecs / blindAdvanceTime;
		int timeIntoNext = gameElapsedTimeSecs % blindAdvanceTime;

		// Set the blind schedule and return the time until the next change.
		setCurrentBlindScheduleStage(initialBlindSchedule + advancedBlinds);
		return (blindAdvanceTime - timeIntoNext);
	}

	public boolean isFirstDeal() {
		return firstDealFlag;
	}

	public void setFirstDeal(boolean newValue) {
		firstDealFlag = newValue;
	}

/*************************************/
/* Code related to scoring the hand */

	public int [] getHandValues()    { return scoredHandValue; }
	public int getWinningHandValue() { return winningHandValue; }

	/**
	 * Determine which player has won the hand and adjust the current holdings
	 * appropriately for each player's winnings.
	 * Also adjust numberAlivePlayers to keep track of the number of players
	 * still in the game.
	 *
	 * @return true  => This was a show-down and all of the hands should be shown
	 *                 to all players.
	 *         false => Every player but one folded, so don't show cards.
	 */
	public boolean determineHandWinner() {
		// If there is only one player left bidding, then he's the winner
		// and gets the entire pot.
		if (numberBiddingPlayers == 1) {
			int winner = findOnlyBiddingPlayer();
			holdings[winner] += totalPotValue;
			winningHandValue = (HAND_TYPE_UNKNOWN << 20) + 1;
			for (int i=0; i<numPlayers; i++) {
				scoredHandValue[i] = 0;
			}
			scoredHandValue[winner] = winningHandValue;
			countAlivePlayers();
			return false;
		}

		// Initialize the hand scoring data structures
		initHandScoreData();

		// Score each player's hand, remembering the overall winning hand value
		winningHandValue = 0;
		for (int i=0; i<numPlayers; i++) {
			if (handVisible(i)) {
				handValue[i] = scoredHandValue[i] = scoreHand(i);
				if (handValue[i] > winningHandValue) {
					winningHandValue = handValue[i];
				}
			} else {
				handValue[i] = scoredHandValue[i] = 0;
			}
		}

		// Hand out winnings, as long as there is a pot to distribute
		while (totalPotValue > 0) {
			// First, find out the minimum value contributed by any of the
			// winners in this hand (and also the # of winners)
			int currMaxHandValue = -1;
			int numWinningPlayers = 0;
			int minContributedValue = 9999999;
			for (int i=0; i<numPlayers; i++) {
				if (handValue[i] > currMaxHandValue) {
					currMaxHandValue = handValue[i];
					numWinningPlayers = 1;
					minContributedValue = potEquity[i];
				} else if (handValue[i] == currMaxHandValue) {
					numWinningPlayers += 1;
					if (potEquity[i] < minContributedValue) {
						minContributedValue = potEquity[i];
					}
				}
			}

			// Second, go through all players and create the pot to be divided
			// up among all of the winners.
			int winningPot = 0;
			for (int i=0; i<numPlayers; i++) {
				if (potEquity[i] > 0) {
					int playerContrib = Math.min(potEquity[i], minContributedValue);
					winningPot += playerContrib;
					potEquity[i] -= playerContrib;
				}
			}

			// Third, give equal shares of this winning pot to all of the winning players
			int perPlayerPayout = (winningPot / numWinningPlayers);
			for (int i=0; i<numPlayers; i++) {
				if (handValue[i] == currMaxHandValue) {
					// This is a winner, so he gets a share
					holdings[i] += perPlayerPayout;
				}

				if (potEquity[i] <= 0) {
					// This player has no more equity in the total pot, so his hand
					// doesn't matter anymore.
					handValue[i] = 0;
				}
			}

			// Remove the winning pot money from the overall pot
			totalPotValue -= winningPot;
		}

		// Finally, round everyone's current holdings to the nearest $10.
		// This will round $5 down, so in the case of a ties, $10 of
		// the total money in the game will be lost.
		for (int i=0; i<numPlayers; i++) {
			holdings[i] = ((holdings[i] + 4) / 10) * 10;
		}

		// Update the number of players that are still in the game.
		countAlivePlayers();
		return true;
	}

	/*
	 * Initialize the data used to score hands.  This should be done once
	 * before scoring all of the hands in a round.
	 */
	private void initHandScoreData() {
		// Initialize the value & suit counts to 0
		for (int i=2; i<15; i++) {
			scoringValueCount[i] = 0;
			for (int s=0; s<3; s++) {
				cardExist[s][i] = false;
			}
		}
		for (int i=0; i<4; i++)  {scoringSuitCount[i]  = 0;}
		for (int i=0; i<5; i++)  {scoringSetSize[i]    = 0;}
		flushSuit = -1;

		// Add the common cards.
		for (int i=0; i<commonCards.length; i++) {
			addCardToScore(commonCards[i]);
		}
	}

	/*
	 * Add a card to the scoring data structure.
	 *
	 * @param theCard   The card to be added.
	 */
	private void addCardToScore(Card theCard) {
		int val = theCard.cardValue();
		scoringValueCount[val] += 1;
		scoringSetSize[scoringValueCount[val]] += 1;

		int suit = theCard.cardSuit();
		scoringSuitCount[suit] += 1;
		if (scoringSuitCount[suit] >= 5) {
			flushSuit = suit;
		}

		cardExist[suit][val] = true;
	}

	/*
	 * Remove a card from the scoring data structure.
	 *
	 * @param theCard   The card to be removed.
	 */
	private void removeCardFromScore(Card theCard) {
		int val = theCard.cardValue();
		scoringSetSize[scoringValueCount[val]] -= 1;
		scoringValueCount[val] -= 1;

		int suit = theCard.cardSuit();
		scoringSuitCount[suit] -= 1;
		if (scoringSuitCount[suit] == 4) {
			flushSuit = -1;
		}

		cardExist[suit][val] = false;
	}

	/*
	 * This routine will score the hand for the player sitting in the given seat
	 * number and return the value of the hand.  Higher valued scores are better
	 * hands.
	 *
	 * Hand values are encoded as 24 bit numbers:
	 *		bits [23:20] = hand type (1 pair, 2 pair, etc...)
	 *		bits [19:16] = highest card
	 *		bits [15:12] = 2nd highest card
	 *		...
	 *		bits [3:0]   = 5th highest card
	 *
	 * This allows hands to be absolutely compared just by determining which
	 * score is higher.
	 *
	 * @param   seatNum   The seatNumber of the player whose hand is to be scored.
	 *
	 * @return the hand value.
	 */
	private int scoreHand(int seatNum) {
		// Add the private cards to the common cards
		addCardToScore(playerCards[seatNum][0]);
		addCardToScore(playerCards[seatNum][1]);

		// Score the hand
		int score = scoreTheHand();

		// Remove the private cards
		removeCardFromScore(playerCards[seatNum][0]);
		removeCardFromScore(playerCards[seatNum][1]);

		return score;
	}

	/*
	 * Do the actual scoring of the hand.
	 */
	private int scoreTheHand() {
		// Initialize the scoringSetValue array
		for (int i=1; i<=4; i++) {
			scoringSetValue[i][0] = 0;
			scoringSetValue[i][1] = 0;
		}

		// Scan for straights and sets.
		// (If player has an ACE, then we start with 1 card of an ACE-low straight)
		int straightHighCardValue = 0;
		int straightLength = (scoringValueCount[14] == 0) ? 0 : 1;
		for (int i=2; i<15; i++) {
			int size = scoringValueCount[i];

			if (size != 0) {
				straightLength += 1;
				if (straightLength >= 5) {
					straightHighCardValue = i;
				}

				scoringSetValue[size][1] = scoringSetValue[size][0];
				scoringSetValue[size][0] = i;
			} else {
				straightLength = 0;
			}
		}

		// Score the hand
		if ((flushSuit >= 0) && (straightHighCardValue != 0)) {
			int highCardValue = checkStraightFlush(straightHighCardValue);
			if (highCardValue > 0) {
				return makeHandScore(HAND_TYPE_STRAIGHT_FLUSH, highCardValue, 0, 0);
			}
		}

		if (scoringSetSize[4] > 0) {
			int bestOtherCard = bestOtherScore(1, scoringSetValue[4][0], -1);
			return makeHandScore(HAND_TYPE_FOUR_KIND, scoringSetValue[4][0], 0, bestOtherCard);
		}

		if (scoringSetSize[3] == 2) {
			return makeHandScore(HAND_TYPE_FULL_HOUSE, scoringSetValue[3][0], scoringSetValue[3][1], 0);
		}

		if ((scoringSetSize[3] == 1) && (scoringSetSize[2] > 1)) {
			return makeHandScore(HAND_TYPE_FULL_HOUSE, scoringSetValue[3][0], scoringSetValue[2][0], 0);
		}

		if (flushSuit >= 0) {
			return makeHandScore(HAND_TYPE_FLUSH, 0, 0, scoreFlush());
		}

		if (straightHighCardValue > 0) {
			return makeHandScore(HAND_TYPE_STRAIGHT, straightHighCardValue, 0, 0);
		}

		if (scoringSetSize[3] > 0) {
			int bestOtherCards = bestOtherScore(2, scoringSetValue[3][0], -1);
			return makeHandScore(HAND_TYPE_THREE_KIND, scoringSetValue[3][0], 0, bestOtherCards);
		}

		if (scoringSetSize[2] >= 2) {
			int bestOtherCard = bestOtherScore(1, scoringSetValue[2][0], scoringSetValue[2][1]);
			return makeHandScore(HAND_TYPE_TWO_PAIR, scoringSetValue[2][0], scoringSetValue[2][1], bestOtherCard);
		}

		if (scoringSetSize[2] == 1) {
			int bestOtherCards = bestOtherScore(3, scoringSetValue[2][0], -1);
			return makeHandScore(HAND_TYPE_ONE_PAIR, scoringSetValue[2][0], 0, bestOtherCards);
		}

		int bestOtherCards = bestOtherScore(5, -1, -1);
		return makeHandScore(HAND_TYPE_NOTHING, 0, 0, bestOtherCards);
	}

	/*
	 * Return the best score for <numCards> cards, excluding cards of the
	 * exclude values.
	 *
	 * @param numCards   The number of cards to include in the score.
	 * @param exclude1   Cards of this value are to be ignored.
	 * @param exclude2   Cards of this value are to be ignored.
	 *
	 * @return the base score.
	 */
	private int bestOtherScore(int numCards, int exclude1, int exclude2) {
		int score = 0;
		int i = 14;	// Start at the Ace
		while (numCards > 0) {
			// Find the next non-zero, non-excluded value.
			while ((scoringValueCount[i] == 0) || (i == exclude1) || (i == exclude2)) {
				i--;
			}

			// Add this card to the score.
			// Note: We don't worry about multiples at this point, since whenever
			// this is called, we've already taken care of the combo's and we're
			// just looking for the extra high cards.
			score = (score << 4) + i;
			numCards -= 1;
			i -= 1;
		}

		return score;
	}

	/*
	 * Return the base score for a flush.
	 * This score is the 5 highest values in the flush suit.
	 *
	 * @return the base score.
	 */
	private int scoreFlush() {
		boolean [] existRow = cardExist[flushSuit];

		int score = 0;
		int i=14;	// Start at the Ace
		int numCards = 5;
		while (numCards > 0) {
			while (!existRow[i]) i--;

			score = (score << 4) + i;
			numCards -= 1;
			i -= 1;
		}

		return score;
	}

	/*
	 * Determine if there is a straight in the flushSuit suit, given that
	 * we know that there is a flush and a straight.
	 *
	 * @param straightHighCardValue   The highest card in a known straight
	 *
	 * @return the highest card value in the straight flush, or -1 if there
	 *         isn't a straight flush.
	 */
	private int checkStraightFlush(int straightHighCardValue) {
		boolean [] existRow = cardExist[flushSuit];
		if (!existRow[straightHighCardValue-2] ||
		    !existRow[straightHighCardValue-3] ||
		    !existRow[straightHighCardValue-4])
		    return -1;

		if (existRow[straightHighCardValue-1]) {
			if (existRow[straightHighCardValue]) {
				return straightHighCardValue;
			} else if (existRow[straightHighCardValue-5]) {
				return straightHighCardValue-1;
			}
			return -1;
		}

		if (existRow[straightHighCardValue-5] &&
		    existRow[straightHighCardValue-6]) {
			return straightHighCardValue-2;
		}

		return -1;
	}

	/*
	 * Make a hand score given the hand type and card values.
	 */
	private int makeHandScore(int type, int cv1, int cv2, int cv5) {
		return (type << 20) + (cv1 << 16) + (cv2 << 12) + (cv5);
	}

	/*
	 * Return the seat number of the player who is still bidding.
	 */
	private int findOnlyBiddingPlayer() {
		for (int i=0; i<numPlayers; i++) {
			if (playerIsStillBidding(i)) {
				return i;
			}
		}
		// Note: Should never get here!
		return 0;
	}

}
