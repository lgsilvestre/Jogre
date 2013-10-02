/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.server;

import org.jogre.common.JogreModel;

import org.jogre.carTricks.common.CarTricksCoreModel;
import org.jogre.carTricks.common.CarTricksCard;

import org.jogre.carTricks.common.CarTricksTrackDB;

import java.util.Arrays;
import java.util.Random;

// Model which holds the data for a game of Car Tricks
// This is the server model which knows all of the game
public class CarTricksServerModel extends CarTricksCoreModel {

	// Declare various constants

	// Declare the array of hands and bids.
	private CarTricksCard [][] playerHands;
	private int [][] playerBids;

	// Keep track of the number of cards each player has in their hand.  When one player
	// runs out of cards, then the game is over.
	private int [] numCardsInHand;

	// The player's scores (calculated at the end of the game)
	private int [] playerScores;
	private int winningScore;


	/**
	 * Constructor for the server model.
	 * A new table is created every time a new game starts.
	 * Note that the server always knows the track database upon model creation.
	 *
	 * @param	numPlayers		The number of players playing this game
	 * @param	theTrack		The track to use for this model
	 * @param	enableEvents	Indicates if event cards should be used or not.
	 */
	public CarTricksServerModel(int numPlayers, CarTricksTrackDB theTrack, boolean enableEvents) {
		super(numPlayers, enableEvents);
		super.addDatabase(theTrack);

		// Create things
		playerHands = new CarTricksCard [numPlayers][];
		playerBids = new int [numPlayers][];
		numCardsInHand = new int [numPlayers];
		playerScores = new int [numPlayers];

		// Initialize things
		for (int pl=0; pl < numPlayers; pl++) {
			playerBids[pl] = null;						// Will be submitted by the clients
			playerScores[pl] = 0;
		}
		winningScore = -1;

		// The server starts in SETTING_BID phase.  (By the time the ServerModel is created,
		// all player clients have connected and started...)
		changePhase(CarTricksCoreModel.SETTING_BID);

		// Finally, deal the cards out to the players
		dealCards(theTrack.getNumCars());
	}

	/**
	 * This routine will create a deck of cards and deal them out
	 * the players.
	 *
	 * @param	numCars			The number of cars that are on the track
	 */
	private void dealCards(int numCars) {
		int i, j, pl;
		int deckSize = (numCars * 10);
		CarTricksCard [] deck = new CarTricksCard [deckSize];
		CarTricksCard tempCard;

		// Populate the deck with all of the cards
		for (i=0; i < numCars; i++) {
			for (j=0; j < 10; j++) {
				deck[(i*10)+j] = new CarTricksCard (i, j+2);	// Color = i, value = j+2;
			}
		}

		// Shuffle the deck
		Random rand = new Random();
		for (i=0; i < deckSize; i++) {
			// Pick a random card from i to end-of-deck
			j = rand.nextInt(deckSize-i) + i;

			// Swap cards at i & j
			tempCard = deck[i];
			deck[i] = deck[j];
			deck[j] = tempCard;
		}

		// Deal cards out to each player
		int numPlayers = getNumPlayers();
		int cardsPerPlayer = deckSize / numPlayers;
		int excessCards = deckSize % numPlayers;

		i=0;
		for (pl=0; pl < numPlayers; pl ++) {
			if (excessCards > 0) {
				playerHands[pl] = new CarTricksCard [cardsPerPlayer + 1];
				excessCards -= 1;
			} else {
				playerHands[pl] = new CarTricksCard [cardsPerPlayer];
			}

			// Keep track of the number of cards dealt to each player
			numCardsInHand[pl] = playerHands[pl].length;

			System.arraycopy(deck, i, playerHands[pl], 0, playerHands[pl].length);
			Arrays.sort(playerHands[pl]);
			i += playerHands[pl].length;
		}
	}

	/**
	 * Return the hand for the given player
	 *
	 * @param	player_id			The seat number of the player whose hand is desired.
	 * @return		the hand for that player.
	 */
	public CarTricksCard [] getPlayerHand(int player_id) {
		return playerHands[player_id];
	}

	/**
	 * Set the bid for a given player
	 *
	 * @param	player_id			The seat number of the player whose bid is being set
	 * @param	theBid				The bid
	 */
	public void setBid(int player_id, int [] theBid) {
		playerBids[player_id] = theBid;
	}

	/**
	 * Return the bid for a given player
	 *
	 * @param	player_id			The seat number of the player whose bid is being sought
	 */
	public int [] getBid(int player_id) {
		return playerBids[player_id];
	}

	/**
	 * Determine if all players have submitted their bids
	 *
	 * @return true/false
	 */
	public boolean allBidsDone() {
		for (int i=0; i < playerBids.length; i++) {
			if (playerBids[i] == null) {
				// This player hasn't finished bidding yet, so we're not done
				return (false);
			}
		}

		// All players have submitted bids, so we're done bidding
		return true;
	}

	/**
	 * Play a card for the given player
	 *
	 * @param	player_id			The seat number of the player who is playing the card
	 * @param	theCard				The card being played
	 * @return			true  => The card is legal to play and has been played
	 *					false => The card is not legal to play
	 */
	public boolean playCard(int player_id, CarTricksCard theCard) {

		int index = Arrays.binarySearch(playerHands[player_id], theCard);
		if (!theCard.isEvent() && (index < 0)) {
			// The card is not an event card and is not in the player's hand,
			// so it is invalid.
			return false;
		}

		// Try to play the card
		boolean playable = super.playCard(player_id, theCard);

		if (playable && (index >= 0)) {
			// Need to remove it from the player's hand
			playerHands[player_id][index] = CarTricksCard.makeInvisibleCard(theCard);

			numCardsInHand[player_id] -= 1;
			if (numCardsInHand[player_id] == 0) {
				signalEndOfGameCondition();
			}
		}

		return playable;
	}

	/**
	 * Calculate the scores for all of the players
	 */
	public void calculateScores() {
		int [] finalPos = getPositionsByCar();

		// Calculate scores for each player, as well as the winning score.
		for (int i=0; i<playerBids.length; i++) {

			int score = 0;
			for (int j=0; j<playerBids[i].length; j++) {
				// add the score for the car that the player bid for position j
				score += ((j+1) * (finalPos[playerBids[i][j]]));
			}

			playerScores[i] = score;
			if (score > winningScore) {
				winningScore = score;
			}
		}
	}

	/**
	 * Retrieve the score for the given player
	 *
	 * @param	player_id		The seat number of the player whose score is being sought
	 */
	public int getScore(int player_id) {
		return playerScores[player_id];
	}

	/**
	 * Determine if the given player is the winner.
	 *
	 * @param	player_id		The seat number of the player whose winning status is being sought
	 */
	public boolean isWinner(int player_id) {
		return (getScore(player_id) == winningScore);
	}
}
