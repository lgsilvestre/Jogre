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
package org.jogre.texasHoldEm.common;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;

import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Core Model for the TexasHoldEm game
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmCoreModel extends JogreModel {

	Card theFakeCard = new Card (0, 2, true);	// 2 of clubs
	Card invisibleCard = new Card (0, 0, false);
	Card unknownCard = new Card ();

	// Parameters of the game (set by the game's creater)
	protected int numPlayers;
	protected int blindAdvanceTime;
	protected int raiseLimit;
	protected int initialBankroll;
	protected int initialBlindSchedule;

	// Table for small blind, big blind & ante for each stage
	public static final int [] smallBlind = {0, 10, 30, 50, 100, 200, 400, 400, 800};
	public static final int [] bigBlind = {0, 20, 60, 100, 200, 400, 800, 800, 1600};
	public static final int [] ante = {0, 0, 0, 0, 0, 0, 0, 100, 200};

	// The current row of the blind schedule that the game is at.
	protected int currentBlindScheduleStage;

	// The state of each player
	protected int [] playerState;
	public final static int ACTIVE = 0;
	public final static int FOLDED = 1;
	public final static int ALL_IN = 2;
	public final static int OUT_OF_GAME = 3;

	// Holdings for each player
	protected int [] holdings;
	protected int [] potEquity;
	protected Card [][] playerCards;

	// The common cards for all players
	protected Card [] commonCards = new Card [5];

	// The current total pot value;
	protected int totalPotValue;

	// The current bid amount that all players need to meet to stay in the game.
	protected int currentBid;

	// The valid limits of bids
	private int [] validBidLimits = new int [2];
	private int [] zeroBidLimits = {0, 0};

	// The last action made by each player
	protected int [] lastAction;
	public final static int LAST_ACTION_FOLD = 0;
	public final static int LAST_ACTION_BID = 1;
	public final static int LAST_ACTION_RAISE = 2;
	public final static int LAST_ACTION_CALL = 3;
	public final static int LAST_ACTION_ALL_IN = 4;
	public final static int LAST_ACTION_THINKING = 5;
	public final static int LAST_ACTION_BLANK = 6;

	// For bid or raise last action, this is the amount of the bid that
	// the player made.
	protected int [] lastActionAmount;

	// The current dealer, small blind & big blind player Id's
	private int currentDealer;
	private int smallBlindPlayer;
	private int bigBlindPlayer;
	protected int activePlayer;

	// The number of players still alive in the game.
	private int numberAlivePlayers;

	// The number of players still bidding in the current round.
	protected int numberBiddingPlayers;

	// The round number
	protected int roundNum;

	// The type of hand that won
	protected int winningHandValue;
	public static final int HAND_TYPE_BLANK = -1;
	public static final int HAND_TYPE_UNKNOWN = 0;
	public static final int HAND_TYPE_NOTHING = 1;
	public static final int HAND_TYPE_ONE_PAIR = 2;
	public static final int HAND_TYPE_TWO_PAIR = 3;
	public static final int HAND_TYPE_THREE_KIND = 4;
	public static final int HAND_TYPE_STRAIGHT = 5;
	public static final int HAND_TYPE_FLUSH = 6;
	public static final int HAND_TYPE_FULL_HOUSE = 7;
	public static final int HAND_TYPE_FOUR_KIND = 8;
	public static final int HAND_TYPE_STRAIGHT_FLUSH = 9;
	public static final int HAND_TYPE_ROYAL_FLUSH = 10;

	/**
	 * Constructor for the model
	 *
	 * @param numPlayers        The number of players in the game.
	 * @param initialBankroll   The amount of money each player starts with.

	 */
	public TexasHoldEmCoreModel(
		int numPlayers,
		int initialBankroll,
		int initialBlindSchedule,
		int blindAdvanceTime,
		int raiseLimit)
	{
		super();

		// Create things that depend on the parameters
		playerState      = new int [numPlayers];
		holdings         = new int [numPlayers];
		potEquity        = new int [numPlayers];
		playerCards      = new Card [numPlayers][2];
		lastAction       = new int [numPlayers];
		lastActionAmount = new int [numPlayers];

		// Save parameters
		this.numPlayers = numPlayers;
		this.blindAdvanceTime = blindAdvanceTime;
		this.raiseLimit = raiseLimit;
		this.initialBankroll = initialBankroll;
		this.initialBlindSchedule = initialBlindSchedule;

		// Reset the game.
		resetGame ();
	}


	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame ()
	{
		// Give the player's their initial bankroll
		for (int i=0; i < numPlayers; i++) {
			playerState[i] = ACTIVE;
			holdings[i] = initialBankroll;
		}

		// Call startNextHand() to reset everything for the first hand.
		currentDealer = -1;
		currentBlindScheduleStage = 0;
		numberAlivePlayers = numPlayers;
		winningHandValue = (HAND_TYPE_UNKNOWN << 20);
		startNextHand();

		// Initialize misc. stuff
		currentBlindScheduleStage = initialBlindSchedule;

		refreshObservers();
	}

	/**
	 * Get info from the model.
	 */
	public int getNumPlayers() { return numPlayers; }
	public int getPlayerState (int seatNum) { return playerState[seatNum]; }
	public int getPlayerHoldings (int seatNum) { return holdings[seatNum]; }
	public int [] getPlayerHoldings () { return holdings; }
	public int getPotEquity (int seatNum) { return potEquity[seatNum]; }
	public Card getPlayerCard(int seatNum, int cardNum) { return playerCards[seatNum][cardNum]; }
	public Card getCommonCard(int cardNum) { return commonCards[cardNum]; }
	public int getPotValue() { return totalPotValue; }
	public int getLastAction(int seatNum) { return lastAction[seatNum]; }
	public int getRoundNum() { return roundNum; }
	public int getCurrentRoundBid(int seatNum) { return lastActionAmount[seatNum]; }

	public int getSmallBlind(int stageNum) { return smallBlind[stageNum]; }
	public int getBigBlind(int stageNum) { return bigBlind[stageNum]; }
	public int getAnte(int stageNum) { return ante[stageNum]; }
	public int getSmallBlind() { return smallBlind[currentBlindScheduleStage]; }
	public int getBigBlind() { return bigBlind[currentBlindScheduleStage]; }
	public int getAnte() { return ante[currentBlindScheduleStage]; }
	public int getCurrentBlindScheduleStage() { return currentBlindScheduleStage; }

	public int getCurrentDealer()    { return currentDealer; }
	public int getSmallBlindPlayer() { return smallBlindPlayer; }
	public int getBigBlindPlayer()   { return bigBlindPlayer; }
	public int getActivePlayerId()   { return activePlayer; }
	public int getCurrentBid()       { return currentBid; }

	public boolean playerIsAlive(int seatNum) { return (playerState[seatNum] != OUT_OF_GAME); }
	public boolean playerIsStillBidding(int seatNum) { return (playerState[seatNum] == ACTIVE); }
	public boolean handVisible(int seatNum) { return playerCards[seatNum][0].isVisible(); }


	public boolean isGameOver() { return (numberAlivePlayers == 1); }

	public int [] getLegalBidRange(int seatNum) {
		if (seatNum < 0) {
			return zeroBidLimits;
		} else {
			calcLegalBidRange(seatNum);
			return validBidLimits;
		}
	}

	/**
	 * Start the next hand of a game.
	 */
	public void startNextHand() {
		int anteWorth;

		// Advance the current dealer & Big/Small blind players.
		currentDealer    = findNextAlivePlayer(currentDealer);
		smallBlindPlayer = (numberAlivePlayers == 2) ?
		                                currentDealer :
		                                findNextAlivePlayer(currentDealer);
		bigBlindPlayer   = findNextAlivePlayer(smallBlindPlayer);
		activePlayer     = findNextAlivePlayer(bigBlindPlayer);

		// Clear the pot (must do this before antes...)
		totalPotValue = 0;

		// Initialize the settings for the players, and take out blinds/antes
		for (int i=0; i < holdings.length; i++) {
			// If a player is still in the game, then he gets unknown cards,
			// otherwise he gets invisible cards
			if (holdings[i] > 0) {
				playerCards[i][0] = unknownCard;
				playerCards[i][1] = unknownCard;
				playerState[i] = ACTIVE;
			} else {
				playerCards[i][0] = invisibleCard;
				playerCards[i][1] = invisibleCard;
				playerState[i] = OUT_OF_GAME;
			}				 
			lastAction[i] = LAST_ACTION_BLANK;

			// Compute the ante amount for this player
			if (i == smallBlindPlayer) {
				anteWorth = Math.min(smallBlind[currentBlindScheduleStage], holdings[i]);
			} else if (i == bigBlindPlayer) {
				anteWorth = Math.min(bigBlind[currentBlindScheduleStage], holdings[i]);
			} else {
				anteWorth = ante[currentBlindScheduleStage];
			}

			// Make the ante.
			potEquity[i] = anteWorth;
			holdings[i] -= anteWorth;
			totalPotValue += anteWorth;
			lastActionAmount[i] = anteWorth;
		}

		// The current bid is the big blind.
		currentBid = bigBlind[currentBlindScheduleStage];

		// All alive players are now bidding.
		numberBiddingPlayers = numberAlivePlayers;

		// Set the common cards to all invisible
		for (int i=0; i < commonCards.length; i++) {
			commonCards[i] = invisibleCard;
		}
		// We start at round 0
		roundNum = 0;
	}

	/**
	 * Advance to the next round of bidding.
	 * Note: This is called only by the server.
	 */
	public void advanceToNextRound() {
		// Increment the round number
		roundNum += 1;

		// Clear the last actions of the players.
		clearLastAction();

		// Bidding starts again with the player after the dealer
		activePlayer = currentDealer;
		advanceToNextBidder();
	}

	/**
	 * Clear the last action state of all of the players
	 * RAW: This may be client only...
	 */
	public void clearLastAction() {
		// Set all last actions to blank
		for (int i=0; i < numPlayers; i++) {
			if (playerIsAlive(i)) {
				lastAction[i] = LAST_ACTION_BLANK;
			}
		}

		refreshObservers();
	}

	/*
	 * Find the player located after the given startingPlayer who is
	 * still in the game.
	 *
	 * @param startingPlayer   The player to start with while locating the next
	 *                         player still in the game.
	 */
	private int findNextAlivePlayer(int startingPlayer) {
		do {
			startingPlayer = (startingPlayer + 1) % numPlayers;
		} while (playerState[startingPlayer] == OUT_OF_GAME);

		return startingPlayer;
	}

	/**
	 * Give a card to a player.
	 *
	 * @param seatNum   The player to give the card to.
	 * @param cardNum   The instance to put the card into.
	 * @param theCard   The card to give.
	 */
	public void giveCardToPlayer(int seatNum, int cardNum, Card theCard) {
		playerCards[seatNum][cardNum] = theCard;
		refreshObservers();
	}

	/**
	 * Set a card to the common cards area.
	 *
	 * @param cardNum   The instance to put the card into.
	 * @param theCard   The card to place.
	 */
	public void giveCommonCard(int cardNum, Card theCard) {
		commonCards[cardNum] = theCard;
		refreshObservers();
	}

	/**
	 * Determine the minimum & maximum legal bid at this point.
	 */
	public void calcLegalBidRange(int seatNum) {

		int playerMax = holdings[seatNum] + potEquity[seatNum];

		// If the current bid is more than the player has, then his only
		// choice is to go all in.
		if (currentBid > playerMax) {
			validBidLimits[0] = playerMax;
			validBidLimits[1] = playerMax;
			return;
		}

		if (raiseLimit == 0) {
			// No limit - can bid from current bid up to the player's entire holdings.
			validBidLimits[0] = currentBid;
			validBidLimits[1] = playerMax;
		} else {
			// Pot or Blind limit
			//     - Minimum raise is based on blinds
			//     - Maximum raise is either same as min raise or pot value
			int minRaise = getMinRaise();
			int maxRaise = (raiseLimit == 2) ? minRaise : totalPotValue;

			validBidLimits[0] = Math.min(playerMax, currentBid + minRaise);
			validBidLimits[1] = Math.min(playerMax, currentBid + maxRaise);
		}
	}

	/*
	 * Determine the minimum raise for a pot-limit or blind-limit game.
	 */
	private int getMinRaise() {
		int bb = getBigBlind();
		if (bb == 0) {
			// In the case of 0 blind, then limit to the smallest non-zero blind.
			bb = bigBlind[1];
		}
		return commonCards[3].isVisible() ? 2 * bb : bb;
	}

	/**
	 * Try to make a bid.
	 *
	 * @param seatNum    The player making the bid
	 * @param bidAmount  The amount of bid.  A value of less than 0 is equal to a fold.
	 *
	 * @return True  => bid is valid and was recorded.
	 *         False => bid was invalid.
	 */
	public boolean makeBid(int seatNum, int bidAmount) {
		// Only the active player can bid.
		if (seatNum != activePlayer) {
			System.out.println(seatNum + " tried to bid, but " + activePlayer + " is the active player");
			return false;
		}

		// Check for fold
		if (bidAmount < 0) {
			playerState[seatNum] = FOLDED;
			playerCards[seatNum][0] = invisibleCard;
			playerCards[seatNum][1] = invisibleCard;
			lastAction[seatNum] = LAST_ACTION_FOLD;
			lastActionAmount[seatNum] = 0;
			numberBiddingPlayers -= 1;
			refreshObservers();
			return true;
		}

		// Check to make sure the bid is within the valid range.
		calcLegalBidRange(seatNum);
		if ( (bidAmount != currentBid) &&
		    ((bidAmount < validBidLimits[0]) || (bidAmount > validBidLimits[1]))) {
			System.out.println(bidAmount + " is outside of valid range of " + validBidLimits[0] + " to " + validBidLimits[1]);
			return false;
		}

		// Do the bid....
		int addedThisTurn = bidAmount - potEquity[seatNum];
		holdings[seatNum] -= addedThisTurn;
		potEquity[seatNum] = bidAmount;
		totalPotValue += addedThisTurn;
		lastActionAmount[seatNum] = bidAmount;

		if (bidAmount == currentBid) {
			lastAction[seatNum] = LAST_ACTION_CALL;
		} else {
			lastAction[seatNum] = LAST_ACTION_RAISE;
		}

		// If this player has gone all-in, then record that.
		if (holdings[seatNum] == 0) {
			playerState[seatNum] = ALL_IN;
			lastAction[seatNum] = LAST_ACTION_ALL_IN;
		}

		// Set the new current bid, if this was a raise.
		currentBid = Math.max(currentBid, bidAmount);

		refreshObservers();
		return true;
	}

	/**
	 * Set the active player to the given one.
	 * Note: This is only called by the client.
	 */
	public void setActivePlayer(int newPlayer) {
		activePlayer = newPlayer;
		lastAction[newPlayer] = LAST_ACTION_THINKING;
	}

	/**
	 * Set the current blind schedule stage.
	 */
	public void setCurrentBlindScheduleStage(int stage) {
		currentBlindScheduleStage = Math.min(stage, bigBlind.length-1);
	}

	/**
	 * Advance around the table to the player that will be the next bidder.
	 * Update activePlayer to be the player who should bid next.
	 */
	public void advanceToNextBidder() {
		int startingPlayer = activePlayer;
		do {
			activePlayer = (activePlayer + 1) % numPlayers;
			if (playerIsStillBidding(activePlayer)) {
				return;
			}
		} while (activePlayer != startingPlayer);
	}

	/**
	 * Update the players' holdings.
	 * Note: This is only called by the clients.
	 */
	public void updateHoldings(int [] newHoldings) {
		holdings = newHoldings;
		countAlivePlayers();
		refreshObservers();
	}

	/*
	 * Count the number of players that are still in the game.
	 */
	protected void countAlivePlayers() {
		numberAlivePlayers = 0;
		for (int i=0; i < numPlayers; i++) {
			if (holdings[i] == 0) {
				playerState[i] = OUT_OF_GAME;
			} else {
				numberAlivePlayers += 1;
			}
		}
	}

/****************************************************/

// RAW: Still need to implement this, so people can join a game in progres...

	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_CURRENT_BLIND_SCHEDULE_STAGE = "blindStage";
	private static final String XML_ATT_PLAYER_STATE = "playState";
	private static final String XML_ATT_HOLDINGS = "holdings";
	private static final String XML_ATT_POT_EQUITY = "potEquity";
	private static final String XML_ATT_CURRENT_BID = "currBid";
	private static final String XML_ATT_LAST_ACTION = "lastAction";
	private static final String XML_ATT_LAST_ACTION_AMOUNT = "lastActionAmount";
	private static final String XML_ATT_CURRENT_DEALER = "dealer";
	private static final String XML_ATT_ACTIVE_PLAYER = "active";
	private static final String XML_ATT_CARD0 = "card0";
	private static final String XML_ATT_CARD1 = "card1";
	private static final String XML_ATT_CARD2 = "card2";
	private static final String XML_ATT_CARD3 = "card3";
	private static final String XML_ATT_CARD4 = "card4";

	/**
	 * Set the model state from the contents of the message.  This is used to
	 * decode the message sent from the server when attaching so that the
	 * client gets the current state of the game, even if attaching in the middle
	 * of a game.
	 *
	 * @param message    Message from the server
	 * @throws Transmissionavannahception
	 */
	public void setState (XMLElement message) {
		// Reset the board back to the starting value before using the
		// message to fill it in.
		resetGame();

		// Pull all of the bits out of the message
		playerState = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_PLAYER_STATE));
		potEquity = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_POT_EQUITY));
		updateHoldings(JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_HOLDINGS)));
		currentBlindScheduleStage = message.getIntAttribute(XML_ATT_CURRENT_BLIND_SCHEDULE_STAGE);
		currentBid = message.getIntAttribute(XML_ATT_CURRENT_BID);
		lastAction = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_LAST_ACTION));
		lastActionAmount = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_LAST_ACTION_AMOUNT));
		currentDealer = message.getIntAttribute(XML_ATT_CURRENT_DEALER);
		activePlayer = message.getIntAttribute(XML_ATT_ACTIVE_PLAYER);

		commonCards[0] = Card.fromString(message.getStringAttribute(XML_ATT_CARD0));
		commonCards[1] = Card.fromString(message.getStringAttribute(XML_ATT_CARD1));
		commonCards[2] = Card.fromString(message.getStringAttribute(XML_ATT_CARD2));
		commonCards[3] = Card.fromString(message.getStringAttribute(XML_ATT_CARD3));
		commonCards[4] = Card.fromString(message.getStringAttribute(XML_ATT_CARD4));

		// Compute data that is not sent
		totalPotValue = 0;
		for (int i=0; i < numPlayers; i++) {
			totalPotValue += potEquity[i];
			if ((playerState[i] == FOLDED) || (playerState[i] == OUT_OF_GAME)) {
				playerCards[i][0] = invisibleCard;
				playerCards[i][1] = invisibleCard;
			}
		}
		if ((activePlayer > 0) && (activePlayer < numPlayers)) {
			lastAction[activePlayer] = LAST_ACTION_THINKING;
		}

        // If everything is read sucessfully then refresh observers
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

		// Set attributes on the state
		state.setAttribute(XML_ATT_PLAYER_STATE, JogreUtils.valueOf(playerState));
		state.setAttribute(XML_ATT_POT_EQUITY, JogreUtils.valueOf(potEquity));
		state.setAttribute(XML_ATT_HOLDINGS, JogreUtils.valueOf(holdings));
		state.setIntAttribute(XML_ATT_CURRENT_BLIND_SCHEDULE_STAGE, currentBlindScheduleStage);
		state.setIntAttribute(XML_ATT_CURRENT_BID, currentBid);
		state.setAttribute(XML_ATT_LAST_ACTION, JogreUtils.valueOf(lastAction));
		state.setAttribute(XML_ATT_LAST_ACTION_AMOUNT, JogreUtils.valueOf(lastActionAmount));
		state.setIntAttribute(XML_ATT_CURRENT_DEALER, currentDealer);
		state.setIntAttribute(XML_ATT_ACTIVE_PLAYER, activePlayer);

		String invisibleString = invisibleCard.toString();
		state.setAttribute(XML_ATT_CARD0, (roundNum >= 1) ? commonCards[0].toString() : invisibleString);
		state.setAttribute(XML_ATT_CARD1, (roundNum >= 1) ? commonCards[1].toString() : invisibleString);
		state.setAttribute(XML_ATT_CARD2, (roundNum >= 1) ? commonCards[2].toString() : invisibleString);
		state.setAttribute(XML_ATT_CARD3, (roundNum >= 2) ? commonCards[3].toString() : invisibleString);
		state.setAttribute(XML_ATT_CARD4, (roundNum >= 3) ? commonCards[4].toString() : invisibleString);

		return state;
	}

}
