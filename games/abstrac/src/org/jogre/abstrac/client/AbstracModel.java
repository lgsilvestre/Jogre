/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.client;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

import org.jogre.abstrac.std.Hand;
import org.jogre.abstrac.std.Card;

import java.lang.ArrayIndexOutOfBoundsException;


// Model for game of Abstrac
public class AbstracModel extends JogreModel {

	// Constants
	public final static int NUM_PLAYERS = 2;
	public final static int NUM_VALUES = 6;	// 9 through Ace
	public final static int NUM_SUITS = 4;

	// Hand object to hold the cards
	private Hand mainHand = new Hand();

	// The original hand of cards provided.  (This is used by the server
	// so that all players get the original hand even if the first
	// player makes an incredibly quick move.)
	private int [] origHand;

	// Array to hold information of which cards each player has
	private int [][][] taken = new int [NUM_PLAYERS][NUM_VALUES][NUM_SUITS];
	public final static int NOT_AVAILABLE = -1;
	public final static int NOT_HAVE = 0;
	public final static int TAKEN = 1;
	public final static int JUST_TAKEN = 2;


	// Array of scores for each player
	private int [][] setScores = new int [NUM_PLAYERS][NUM_VALUES];
	private int [][] runScores = new int [NUM_PLAYERS][NUM_SUITS];
	private int [] sumScores = new int [NUM_PLAYERS];
	private int [] numCards = new int [NUM_PLAYERS];
	private int [] totalScore = new int [NUM_PLAYERS];

	/**
	 * Constructor for the model
	 */
	public AbstracModel() {
		super();

		resetGame();
	}

	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame() {
		mainHand.empty();

		for (int pl=0; pl < NUM_PLAYERS; pl++) {
			for (int v=0; v < NUM_VALUES; v++) {
				for (int s=0; s < NUM_SUITS; s++) {
					taken[pl][v][s] = NOT_HAVE;
					runScores[pl][s] = 0;
				}
				setScores[pl][v] = 0;
			}
			sumScores[pl] = 0;
			numCards[pl] = 0;
			totalScore[pl] = 0;
		}
	}

	/**
	 * Give an array of cards to put into the hand
	 *
	 * @param	cards		An array of cards
	 */
	public void giveCards(int [] cards) {
		origHand = cards;

		mainHand.freeze();
		mainHand.empty();

		for (int i=0; i < cards.length; i++) {
			int suit = cards[i] / NUM_VALUES;
			int value = cards[i] % NUM_VALUES;
			mainHand.appendCard(new Card(suit, value));
		}

		mainHand.thaw();
	}

	/**
	 * Return the original hand of cards
	 *
	 * @return the original hand of cards as an array of ints
	 */
	public int [] getOrigHand() {
		return origHand;
	}

	/*******************************************************/
	/* Routines for playing the game                       */

	/**
	 * Indicate if the given player has the given card.
	 *
	 * @param	playerId	The player to check for the card
	 * @param	suit		The suit of the card
	 * @param	value		The value of the card
	 * @return	true or false
	 */
	public boolean playerHasCard(int playerId, int suit, int value) {
		try {
			return (taken[playerId][value][suit] > NOT_HAVE);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: Asked for pl= " + playerId + ", suit= " + suit + ", value= " + value);
			return false;
		}
	}

	/**
	 * Indicate the status of the given card with respect to the
	 * given player.
	 *
	 * @param	playerId	The player to check for the card
	 * @param	suit		The suit of the card
	 * @param	value		The value of the card
	 * @return	NOT_AVAILABLE => Other player has already taken card
	 *			NOT_HAVE => This card is still not taken by either player
	 *			TAKEN => This card has been taken by this player
	 *			JUST_TAKEN => This card has been taken by this player on his last turn
	 */
	public int getPlayerCardCode(int playerId, int suit, int value) {
		try {
			return (taken[playerId][value][suit]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: Asked for pl= " + playerId + ", suit= " + suit + ", value= " + value);
			return NOT_HAVE;
		}
	}

	/**
	 * Get various stuff from the model.
	 */
	public int getSetScore  (int playerId, int value) {return setScores [playerId][value];}
	public int getRunScore  (int playerId, int suit)  {return runScores [playerId][suit]; }
	public int getSumScore  (int playerId)            {return sumScores [playerId];       }
	public int getNumCards  (int playerId)            {return numCards  [playerId];       }
	public int getTotalScore(int playerId)            {return totalScore[playerId];       }
	public Hand getHand()                             {return mainHand;                   }

	/**
	 * Downgrade all of the "just taken" cards to regular taken cards.
	 */
	public void downgradeJustTaken(int playerId) {
		for (int v=0; v < NUM_VALUES; v++) {
			for (int s=0; s < NUM_SUITS; s++) {
				if (taken[playerId][v][s] == JUST_TAKEN) {
					taken[playerId][v][s] = TAKEN;
				}
			}
		}
	}

	/**
	 * Play a number of cards from the hand into the holdings of the given player.
	 *
	 * @param	playerId		The Player who is claiming the cards
	 * @param	takenCards		The number of cards being claimed (1 to 3)
	 */
	public void takeCards(int playerId, int takenCards) {
		if ((playerId < 0) || (playerId > 1) || (takenCards < 1) || (takenCards > 3)) {
			System.out.println("Bad Move: playerId = " + playerId + "; takenCards = " + takenCards);
			return;
		}

		int opponentId = 1-playerId;

		for (int i=0; i < takenCards; i++) {
			// Get the currently last card from the hand...
			Card theCard = mainHand.getNthCard(mainHand.length() - 1);
			// .. and remove it
			mainHand.removeLastCard();

			// Give it to the player
			numCards[playerId] += 1;
			int value = theCard.cardValue();
			int suit = theCard.cardSuit();
			taken[playerId][value][suit] = JUST_TAKEN;
			taken[opponentId][value][suit] = NOT_AVAILABLE;
		}

		// Recalculate the scores
		calcScore(playerId);

		// Redraw the screen
		refreshObservers();
	}

	private static final int [] setScoreArray = new int [] {0, 0, 0, 2, 8};
	private static final int [] runScoreArray = new int [] {0, 0, 0, 3, 4, 6, 12};

	/**
	 * Calculate the score of the given player.
	 *
	 * @param	playerId		The Player whose score is to be calculated
	 */
	private void calcScore(int playerId) {
		int sumScore = 0;

		// Calculate the set scores
		for (int v=0; v < NUM_VALUES; v++) {
			int total = 0;
			for (int s=0; s < NUM_SUITS; s++) {
				if (playerHasCard(playerId, s, v)) {
					total += 1;
				}
			}
			setScores[playerId][v] = setScoreArray[total];
			sumScore += setScoreArray[total];
		}

		// Calculate the run scores
		for (int s=0; s < NUM_SUITS; s++) {
			int longestRun = 0;
			int currentRun = 0;
			for (int v=0; v < NUM_VALUES; v++) {
				if (playerHasCard(playerId, s, v)) {
					currentRun += 1;
					if (currentRun > longestRun) {
						longestRun = currentRun;
					}
				} else {
					currentRun = 0;
				}
			}
			runScores[playerId][s] = runScoreArray[longestRun];
			sumScore += runScoreArray[longestRun];
		}

		// Set the sum score for this player
		sumScores[playerId] = sumScore;

		// Set the grand total score for both players (since the numCards for playerId
		// changed, the total score for the opponent has also changed.)
		int opponentId = 1-playerId;
		totalScore[playerId] = sumScore * numCards[opponentId];
		totalScore[opponentId] = sumScores[opponentId] * numCards[playerId];
	}

	public static final int DRAW_GAME = 2;
	public static final int NOT_OVER = 3;

	/**
	 * Get the ID of the winner of the game.
	 *
	 * @return	0 = Player 0 won.
	 *			1 = Player 1 won.
	 *			DRAW_GAME = game was a draw.
	 *			NOT_OVER = game not over yet.
	 */
	public int getWinner() {
		if (mainHand.length() != 0) {
			return NOT_OVER;
		}

		if (totalScore[0] > totalScore[1]) {
			return 0;
		} else if (totalScore[1] > totalScore[0]) {
			return 1;
		} else {
			return DRAW_GAME;
		}
	}

/*****************************************************************************************/
/* Save/restore game state methods */
	/*** RAW: Need to add save/restore state routines for when spectators join a game in progress... */
	
	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_PLAYER0_TAKEN = "p0";
	private static final String XML_ATT_PLAYER1_TAKEN = "p1";
	private static final String XML_ATT_HAND = "h";
	
	public void setState (XMLElement message) {
		// Reset the board back to the starting value before using the
		// message to fill it in.
		resetGame ();

		// Pull all of the bits out of the message
		decode_taken(0, message.getStringAttribute(XML_ATT_PLAYER0_TAKEN));
		decode_taken(1, message.getStringAttribute(XML_ATT_PLAYER1_TAKEN));
		decode_hand(message.getStringAttribute(XML_ATT_HAND));

		// Calculate the scores for both players
		calcScore(0);
		calcScore(1);

		// Display the new state of the game
        refreshObservers();
    }

	/**
	 * Used to bundle up the state of the model.  This is used so that when
	 * a client attaches, it gets the current state of the board from the
	 * server.  This allows an observer to attach to a game in progress and
	 * get the up-to-date values.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		state.setAttribute(XML_ATT_PLAYER0_TAKEN, encode_taken(0));
		state.setAttribute(XML_ATT_PLAYER1_TAKEN, encode_taken(1));
		state.setAttribute(XML_ATT_HAND, encode_hand());

		return state;
	}

	/**
	 * Encode a taken array into a string message
	 *
	 * @param	playerId	The player whose taken array is to be encoded
	 * @return a space-delimited string of codes for cards taken by the player
	 */
	private String encode_taken(int playerId) {
		int [] takenArray = new int [numCards[playerId]];

		int i = 0;
		for (int s=0; s < NUM_SUITS; s++) {
			for (int v=0; v < NUM_VALUES; v++) {
				if (playerHasCard(playerId, s, v)) {
					takenArray[i] = s * NUM_VALUES + v;
					i += 1;
				}
			}
		}

		return JogreUtils.valueOf(takenArray);
	}

	/**
	 * Decode a string provided back into a taken array.
	 *
	 * @param	playerId	The player whose taken array is to be decoded
	 * @param	takens		A space-delimited string of codes of cards
	 */
	private void decode_taken(int playerId, String takens) {
		int [] takenArray = JogreUtils.convertToIntArray(takens);
		int opponentId = 1-playerId;

		for (int i=0; i<takenArray.length; i++) {
			int suit = takenArray[i] / NUM_VALUES;
			int value = takenArray[i] % NUM_VALUES;
			taken[playerId][value][suit] = TAKEN;
			taken[opponentId][value][suit] = NOT_AVAILABLE;
		}

		numCards[playerId] = takenArray.length;
	}

	/**
	 * Encode the hand of cards into a string message.
	 *
	 * @return the space-delimited string of codes for cards taken by the player
	 */
	private String encode_hand() {
		int [] handArray = new int [mainHand.length()];

		for (int i=0; i<handArray.length; i++) {
			Card c = mainHand.getNthCard(i);
			handArray[i] = c.cardSuit() * NUM_VALUES + c.cardValue();
		}

		return JogreUtils.valueOf(handArray);
	}

	/**
	 * Decode a string provided back into the hand.
	 *
	 * @param	cardString		A space-delimited string of codes of cards
	 */
	private void decode_hand(String cardString) {
		giveCards(JogreUtils.convertToIntArray(cardString));
	}

}
