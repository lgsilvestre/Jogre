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
package org.jogre.ninetynine.common;

import nanoxml.XMLElement;

import java.util.Enumeration;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

// Common model for the game Ninety Nine.  This contains the model
// elements which are common for both the client and the server.
// Because Ninety Nine is a card game, the server knows all of the
// cards and the clients only know their hand of cards.  This requires
// different models for the client and server.
public class NinetyNineCoreModel extends JogreModel {

	// The results of the bids
	public static final int BID_NOT_COMPLETE = 0;
	public static final int NO_PREMIUM_BIDS = 1;
	public static final int DECLARER_ONLY = 2;
	public static final int REVEALER_ONLY = 3;
	public static final int DECLARER_AND_REVEALER = 4;

	// The bid types that can be made
	public static final int BID_UNKNOWN = 0;
	public static final int BID_NORMAL = 1;
	public static final int BID_DECLARE = 2;
	public static final int BID_REVEAL = 3;

	// The player whose turn it is.
	protected int currentPlayerId;

	// The number of rounds in the game
	protected int roundsInGame;
	
	// The hand number of the current hand
	protected int roundNumber;

	// The current trump suit for this round
	protected int currentTrumpSuit;

	// The hands of cards and bids
	protected Hand [] hands = new Hand[3];
	protected Hand [] bids = new Hand[3];

	// The cards that have been played so far this trick
	protected Card [] playedCards = new Card[3];

	// The lead card of this round
	protected Card leadCard;

	// The number of tricks won this round by each player.
	protected int [] wonTricks = new int[3];

	// The bid types for each player this round
	protected int [] bidType = new int[3];

	// The bid lead player for each player this round.  (These are only
	// used in case of a Reveal bid.)
	protected int [] bidLeader = new int[3];

	// A card that is always invisible & a card that is always unknown
	protected Card invisibleCard, unknownCard;

	// The scores & trump suits for each round
	protected int [][] roundScores;
	protected int [] roundTrumpSuits;

	// The total score for each player
	protected int [] totalScores;

	// The phase of the game
	protected int gamePhase;
	private static final int PHASE_PRE_START = 0;
	private static final int PHASE_BIDDING = 1;
	private static final int PHASE_POST_BID = 2;
	private static final int PHASE_PLAYING = 3;
	private static final int PHASE_ROUND_OVER = 4;
	private static final int PHASE_GAME_OVER = 5;

	// The current declarer & revealer
	protected int declarer, revealer;

	// Indicates if it is time to reveal the hand of the player who bid reveal
	private boolean revealTime;

	/**
	 * Constructor for the model
	 *
	 * @param	roundsInGame	The number of rounds in the game.
	 */
	public NinetyNineCoreModel(int roundsInGame) {
		super();

		// Save parameters
		this.roundsInGame = roundsInGame;

		// Do local initialization
		invisibleCard = new Card (Card.UNKNOWN, 0, false);
		unknownCard = new Card (Card.UNKNOWN, 0, true);

		roundScores = new int [3][roundsInGame];
		roundTrumpSuits = new int [roundsInGame];
		totalScores = new int [3];

		for (int i=0; i<3; i++) {
			hands[i] = new Hand();
			bids[i] = new Hand();
		}

		// Reset the game
		core_resetGame();
	}

	/**
	 * Private version Reset the model back to the initial state
	 */
	private void core_resetGame() {
		currentPlayerId = -1;
		roundNumber = -1;
		currentTrumpSuit = Card.UNKNOWN;

		// Reset the scores
		for (int i=0; i<3; i++) {
			for (int j=0; j<roundsInGame; j++) {
				roundScores[i][j] = 0;
			}
			totalScores[i] = 0;
		}

		// Reset the trump suit history
		for (int i=0; i<roundsInGame; i++) {
			roundTrumpSuits[i] = -2;
		}

		core_readyForNextRound();

		// This is done after readyForNextRound(), since that sets
		// gamePhase to PHASE_BIDDING.
		gamePhase = PHASE_PRE_START;

		refreshObservers();
	}

	/**
	 * Get ready for the next round.
	 *
	 * @returns		true = Ready for next round
	 *				false = Game over
	 */
	private boolean core_readyForNextRound() {
		roundNumber += 1;
		if (roundNumber == roundsInGame) {
			gamePhase = PHASE_GAME_OVER;
			// The game is over!
			return false;
		}

		for (int i=0; i<3; i++) {
			hands[i].empty();
			bids[i].empty();
			playedCards[i] = invisibleCard;
			wonTricks[i] = 0;
			bidType[i] = BID_UNKNOWN;
			bidLeader[i] = -1;
		}

		gamePhase = PHASE_BIDDING;
		leadCard = null;
		currentPlayerId = -1;
		declarer = -1;
		revealer = -1;
		revealTime = false;
		return true;
	}

	/**
	 * Reset the model back to the initialbidLeader[ state
	 */
	public void resetGame() {
		core_resetGame();
	}


	/**
	 * Get ready for the next round.
	 *
	 * @returns		true = Ready for next round
	 *				false = Game over
	 */
	public boolean readyForNextRound() {
		return core_readyForNextRound();
	}

	/**
	 * Set the ID of the current Player.
	 *
	 * @param	newPlayerId		The ID of the player to be new current player
	 */
	public void setCurrentPlayer(int newPlayerId) {
		currentPlayerId = newPlayerId;
	}

	/**
	 * Retrieve one of the hands
	 *
	 * @param	playerId	The player whose hand is returned.
	 */
	public Hand getHand(int playerId) {
		return hands[playerId];
	}

	/**
	 * Retrieve one of the bids
	 *
	 * @param	playerId	The player whose hand is returned.
	 */
	public Hand getBid(int playerId) {
		return bids[playerId];
	}

	/**
	 * Retrieve one of the played cards
	 *
	 * @param	playerId	The player whose played card is returned
	 */
	public Card getPlayedCard(int playerId) {
		return playedCards[playerId];
	}

	/**
	 * Provide a new hand of cards to a player.
	 *
	 * @param	playerId	The player whose hand is being given
	 * @param	newHand		The new hand for the player
	 */
	public void giveHand(int playerId, Hand newHand) {
		hands[playerId].setHand(newHand);
	}

	/**
	 * Provide a hand of cards to a player.
	 *
	 * @param	playerId	The player whose hand is being given
	 * @param	newCards	The new cards for the player
	 */
	public void giveHand(int playerId, Card [] newCards) {
		hands[playerId].setCards(newCards);
	}

	/**
	 * Set the trump suit.
	 *
	 * @param	newTrump	The suit for new trumps. Range from 0..3.  -1 is no-trump
	 */
	public void setTrumpSuit(int newTrump) {
		// If out of valid range, then set no-trump.
		if ((newTrump < -1) || (newTrump >= 4)) {
			currentTrumpSuit = -1;
		} else {
			currentTrumpSuit = newTrump;
		}

		roundTrumpSuits[roundNumber] = currentTrumpSuit;
		refreshObservers();
	}


	/**
	 * Provide a new bid for a player.
	 *
	 * @param	playerId	The player whose bid is being given
	 * @param	newBid		The new bid hand for the player
	 * @param	bidType		The type of bid.
	 * @param	leadPlayer	The player to lead, in case of a Reveal bid.
	 */
	public void giveBid(int playerId, Hand newBid, int bidType, int leadPlayer) {
		bids[playerId].setHand(newBid);
		this.bidType[playerId] = bidType;
		this.bidLeader[playerId] = leadPlayer;
	}

	/**
	 * Evaluate the bids and determine what the current bid state is.
	 *
	 * If there are only declarers or revealers (but not both), then
	 * adjust the bids so that only the priority bidder is the bidder
	 * of record.
	 */
	public int evaluateBids() {
		int code =
			(1 << (bidType[0] * 2)) +
			(1 << (bidType[1] * 2)) +
			(1 << (bidType[2] * 2));

		if ((code & 0x03) != 0) {
			return BID_NOT_COMPLETE;
		}

		if (code == 0x0C) {
			declarer = -1;
			revealer = -1;
			currentPlayerId = (roundNumber % 3);
			return NO_PREMIUM_BIDS;
		}

		if ((code & 0xC0) == 0x00) {
			declarer = findFirstDeclarer();
			revealer = -1;
			adjustBids(declarer, BID_DECLARE);
			currentPlayerId = (roundNumber % 3);
			return DECLARER_ONLY;
		}

		// At least one Reveal bid was made
		int p0 = (roundNumber + 0) % 3;
		int p1 = (roundNumber + 1) % 3;
		int p2 = (roundNumber + 2) % 3;
		if ((bidType[p0] == BID_REVEAL) ||
			((bidType[p0] == BID_NORMAL) && (bidType[p1] == BID_REVEAL)) ||
			((bidType[p0] == BID_NORMAL) && (bidType[p1] == BID_NORMAL) && (bidType[p2] == BID_REVEAL))) {
			declarer = -1;
			revealer = findFirstRevealer();
			adjustBids(revealer, BID_REVEAL);
			currentPlayerId = bidLeader[revealer];
			revealTime = true;
			return REVEALER_ONLY;
		}

		declarer = findFirstDeclarer();
		revealer = findFirstRevealer();
		currentPlayerId = revealer;
		return DECLARER_AND_REVEALER;
	}

	/*
	 * Find the player who bid declare first.
	 */
	private int findFirstDeclarer() {
		for (int i=0; i<3; i++) {
			int pl = (roundNumber + i) % 3;
			if (bidType[pl] == BID_DECLARE)
				return pl;
		}
		// Note: Shouldn't get here...
		return 0;
	}

	/*
	 * Find the player who bid reveal first.
	 */
	private int findFirstRevealer() {
		for (int i=0; i<3; i++) {
			int pl = ((roundNumber % 3) + i) % 3;
			if (bidType[pl] == BID_REVEAL)
				return pl;
		}
		// Note: Shouldn't get here...
		return 0;
	}

	/*
	 * Adjust the bids so that the only premium bid is
	 * that the priorityBidder made priority bid.
	 */
	private void adjustBids(int priorityBidder, int priorityBid) {
		for (int i=0; i<3; i++) {
			int pl = ((roundNumber % 3) + i) % 3;
			if (pl == priorityBidder) {
				bidType[pl] = priorityBid;
			} else {
				bidType[pl] = BID_NORMAL;
			}
		}
	}

	/**
	 * Return a hand that is the hand of the player that bid reveal.
	 * IF this is the time to do so.  After sending once, don't
	 * send again.
	 *
	 * @return	the hand of the revealer.
	 */
	public Hand handToReveal() {
		if (revealTime == false) {
			return null;
		}

		revealTime = false;
		return removeInvisibles(hands[revealer]);
	}

	/**
	 * Create a new hand that contains only the visible cards of the hand
	 * provided.
	 *
	 * @param	oldHand		The old hand to copy
	 * @return	the new hand
	 */
	private Hand removeInvisibles(Hand oldHand) {
		Hand newHand = new Hand();

		for (int i=0; i<oldHand.length(); i++) {
			Card c = oldHand.getNthCard(i);
			if (c.isVisible()) {
				newHand.appendCard(c);
			}
		}

		return newHand;	
	}

	/**
	 * Return the information about the current phase of the game.
	 */
	public boolean isPreStart()  { return (gamePhase == PHASE_PRE_START); }
	public boolean isBidding()   { return (gamePhase == PHASE_BIDDING); }
	public boolean isPlaying()   { return (gamePhase == PHASE_PLAYING); }
	public boolean isGameOver()  { return (gamePhase == PHASE_GAME_OVER); }
	public boolean isRoundOver() { return (gamePhase == PHASE_ROUND_OVER); }
	/**
	 * Move to various phases
	 */
	public void goToBidPhase() 		{ gamePhase = PHASE_BIDDING; }
	public void goToPostBidPhase()	{ gamePhase = PHASE_POST_BID; }
	public void goToPlayPhase()		{ gamePhase = PHASE_PLAYING; }

	/**
	 * Get info about the current state of the game
	 */
	public int getTrumpSuit()					{ return currentTrumpSuit; }
	public int getCurrentPlayerId()				{ return currentPlayerId; }
	public int getDeclarer()					{ return declarer; }
	public int getRevealer()					{ return revealer; }
	public int getNumRoundsInGame()				{ return roundsInGame; }
	public int getCurrentRoundNumber()			{ return roundNumber; }
	public int getTotalScore(int playerId)		{ return totalScores[playerId]; }
	public int getTrumpForRound(int roundNum)	{ return roundTrumpSuits[roundNum]; }
	public int getScoreForRound(int playerId, int roundNum) { return roundScores[playerId][roundNum]; }
	public int getBidType(int playerId)			{ return bidType[playerId]; }
	public int getBidLeader(int playerId)		{ return bidLeader[playerId]; }
	public int getWonTricks(int playerId)		{ return wonTricks[playerId]; }
	public Card getLeadCard()                   { return leadCard; }

	/**
	 * Determine if the potential card is valid to play at this point in time.
	 *
	 * @param	playerId		The player who is making the play
	 * @param	potentialCard	The potential card to play
	 * @return true of false to indicate if the potential card is a valid play
	 */
	public boolean isValidPlay(int playerId, Card potentialCard) {
		// Make preliminary failure checks
		if (playerId != currentPlayerId) {
			// Only the current player can play
			return false;
		}

		if (hands[playerId].getIndexOf(potentialCard) == -1) {
			// The requested card isn't in the hand, so can't play it
			return false;
		}

		if (!potentialCard.isVisible()) {
			// Can't play an invisible card
			return false;
		}

		// Check if this card is valid
		if (leadCard == null) {
			// There is no lead card, so we can lead any card in the hand
			return true;
		}

		int leadSuit = leadCard.cardSuit();
		if (potentialCard.cardSuit() == leadSuit) {
			// This card is the same suit as the lead card, so it is valid to play
			return true;
		}

		if (hands[playerId].getNumCardsOfSuit(leadSuit) == 0) {
			// The player doesn't have any cards of the lead suit, so any card is valid
			return true;
		}

		return false;
	}

	/**
	 * Play the given card.
	 *
	 * This assumes that the play is valid.  This core routine can't make the
	 * check itself, because a client is making the play provided by the server,
	 * it can't look a the hand of the player who made the move and must therefore
	 * trust that the server did the check and that the move is valid.
	 *
	 * @param	playerId		The player who is making the play
	 * @param	playedCard		The card to play
	 */
	public void playCard(int playerId, Card playedCard) {
		// Play the card
		playedCards[playerId] = playedCard;

		if (!hands[playerId].invisiblizeCard(playedCard)) {
			// If we don't know this player's cards, then remove the last one
			hands[playerId].removeLastCard();
		}

		if (leadCard == null) {
			// There was no lead card, so this one is, by definition the lead card
			leadCard = playedCard;
		}
	}

	/** 
	 * Evaluate the current trick to determine who gets it and becomes the new leader.
	 * Set the currentPlayerId to the player who will be playing next.
	 *
	 * @return false = trick not over yet.
	 *					currentPlayerId is set to the next player.
	 *         true = trick is over.
	 *					currentPlayerId is set to the winner.
	 */
	public boolean evaluateTrick() {
		if (!playedCards[0].isVisible() ||
			!playedCards[1].isVisible() ||
			!playedCards[2].isVisible()) {
				// Not all cards are played yet, so nothing to evaluate
				// Advance to the next player
				currentPlayerId = (currentPlayerId + 1) % 3;
				return false;
			}

		// See which player had the best card, and he becomes the next player
		currentPlayerId = betterCardPlayer(betterCardPlayer(0, 1), 2);

		// See if the round is over
		if (hands[0].isEmpty()) {
			gamePhase = PHASE_ROUND_OVER;
		}

		return true;
	}

	/**
	 * Determine which of the two given players has the better card.
	 *
	 * @param	p0		First player
	 * @param	p1		Second player
	 *
	 * @return the player id of who has played the better card
	 */
	private int betterCardPlayer(int p0, int p1) {
		int p0Suit = playedCards[p0].cardSuit();
		int p1Suit = playedCards[p1].cardSuit();
		
		if (p0Suit == p1Suit) {
			// If same suit, the value determines which is better
			if (playedCards[p0].cardValue() > playedCards[p1].cardValue()) {
				return p0;
			} else {
				return p1;
			}
		}

		// If different suits, then trump suit wins.
		if (p0Suit == currentTrumpSuit) {
			return p0;
		} else if (p1Suit == currentTrumpSuit) {
			return p1;
		}

		// If different suits and neither is trump, then lead suit wins
		if (p0Suit == leadCard.cardSuit()) {
			return p0;
		} else {
			// Note: If neither p0 nor p1 played trump nor had lead suit, then that
			// means that the third card must be the lead card and this is just
			// the first comparison.  So, it doesn't matter which one we decide
			// is better, since the third card will beat it.  So, just pick p1
			// as the winner rather than checking to see if he played the lead suit.
			return p1;
		}
	}

	/**
	 * Clean up the state of the last trick and get ready for the next one.
	 */
	public void getReadyForNextTrick() {
		leadCard = null;
		playedCards[0] = invisibleCard;
		playedCards[1] = invisibleCard;
		playedCards[2] = invisibleCard;
		wonTricks[currentPlayerId] += 1;
	}

	// The bonus points for each player when N players make their bids.
	// Ie: if 0 players make their bid, then success is worth 0
	//     if 1 player makes their bid, then success is worth 30
	//     if 2 players make their bid, then success is worth 20
	//     if 3 players make their bid, then success is worth 10
	private static final int [] successBonus = new int [] {0, 30, 20, 10};

	/**
	 * Return the bid value of a card.
	 *
	 * @param	theCard		The card whose value is sought.
	 * @return the value of the card if bid
	 */
	public int bidValueOfCard(Card theCard) {
		int suit = theCard.cardSuit();
		if (suit == Card.DIAMONDS) { return 0; }
		if (suit == Card.SPADES)   { return 1; }
		if (suit == Card.HEARTS)   { return 2; }
		if (suit == Card.CLUBS)    { return 3; }

		// Shouldn't get here, but if someone bid a card other than those,
		// then make the bid 10 (which can't be done... :)
		return 10;
	}

	/**
	 * Compute the scores for this round.
	 */
	public void scoreRound() {
		// Determine which players succesfully made their bids
		boolean [] successfulBid = new boolean [] {false, false, false};
		boolean premiumBidSuccess = false;
		int premiumBidValue = 0;
		int totalSuccessBids = 0;

		for (int i=0; i<3; i++) {
			Hand bh = bids[i];
			if (wonTricks[i] == (bidValueOfCard(bh.getNthCard(0)) + 
								 bidValueOfCard(bh.getNthCard(1)) +
								 bidValueOfCard(bh.getNthCard(2)))) {
				successfulBid[i] = true;
				totalSuccessBids += 1;
			}

			if (bidType[i] != BID_NORMAL) {
				premiumBidValue = (bidType[i] == BID_DECLARE) ? 30 : 60;
				premiumBidSuccess = successfulBid[i];
			}
		}

		// Compute the scores for this round
		for (int i=0; i<3; i++) {
			// Base score is number of tricks taken
			roundScores[i][roundNumber] = wonTricks[i];

			// If this player bid correctly, then successBonus is added
			if (successfulBid[i]) {
				roundScores[i][roundNumber] += successBonus[totalSuccessBids];
			}

			// If there was a premium bid made, then add the premium bid value
			// depending on whether successful player or defeating team.
			if ( ((bidType[i] == BID_NORMAL) && !premiumBidSuccess) ||
				 ((bidType[i] != BID_NORMAL) &&  premiumBidSuccess) ) {
				roundScores[i][roundNumber] += premiumBidValue;
			}

			// Accumulate the round score into the total score for the player
			totalScores[i] += roundScores[i][roundNumber];
		}

        refreshObservers();
	}

	/**
	 * Set the score for playerId to score for the current round.
	 *
	 * @param	playerId	The player whose score is to be set.
	 * @param	score		The score for the player.
	 */
	public void setScore(int playerId, int score) {
		roundScores[playerId][roundNumber] = score;
		calcTotalScoreForPlayer(playerId);

		refreshObservers();
	}

	/**
	 * Calculate the total score for a given player.
	 *
	 * @param	playerId	The player whose total score is to be calculated.
	 */
	private void calcTotalScoreForPlayer(int playerId) {
		// Update the total score for this player
		totalScores[playerId] = 0;
		for (int i=0; i<=roundNumber; i++) {
			totalScores[playerId] += roundScores[playerId][i];
		}
	}

	/**
	 * Determine if the given playerId is a winner or not.
	 */
	public boolean isWinner(int playerId) {
		int maxScore = Math.max(Math.max(totalScores[0], totalScores[1]), totalScores[2]);

		return totalScores[playerId] == maxScore;
	}

/*****************************************************************************************/
/* Save/restore game state methods */

	// XML attributes used for sending/receiving board state
	private static final String XML_STATE_NAME = "nn_state";
	private static final String XML_ATT_CURR_PLAYER_ID = "cp";
	private static final String XML_ATT_ROUND_NUMBER = "r";
	private static final String XML_ATT_TRUMP_SUITS = "ts";
	private static final String XML_ATT_SCORES = "sc";
	private static final String XML_ATT_PHASE = "p";
	private static final String XML_ATT_LEAD_CARD = "lc";
	private static final String XML_ATT_PLAYED_CARD_0 = "pc0";
	private static final String XML_ATT_PLAYED_CARD_1 = "pc1";
	private static final String XML_ATT_PLAYED_CARD_2 = "pc2";
	private static final String XML_ATT_WON_TRICKS = "wt";
	private static final String XML_ATT_BID_0 = "b0";
	private static final String XML_ATT_BID_1 = "b1";
	private static final String XML_ATT_BID_2 = "b2";
	private static final String XML_ATT_HAND_0 = "h0";
	private static final String XML_ATT_HAND_1 = "h1";
	private static final String XML_ATT_HAND_2 = "h2";

	/**
	 * Set the model state from the contents of the message.  This is used to
	 * decode the message sent from the server when attaching so that the
	 * client gets the current state of the game, even if attaching in the middle
	 * of a game.
	 *
	 * @param message    Message from the server
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {
		// Reset the board back to the starting value before using the
		// message to fill it in.
		core_resetGame ();

		// Pull all of the bits out of the message
		Enumeration msgEnum = message.enumerateChildren();
		XMLElement msgEl;
		String elementName;

		while (msgEnum.hasMoreElements()) {
			msgEl = (XMLElement) msgEnum.nextElement();
			elementName = msgEl.getName();
			if (elementName.equals(XML_STATE_NAME)) {
				currentPlayerId = msgEl.getIntAttribute(XML_ATT_CURR_PLAYER_ID);
				roundNumber = msgEl.getIntAttribute(XML_ATT_ROUND_NUMBER);
				roundTrumpSuits = JogreUtils.convertToIntArray(msgEl.getStringAttribute(XML_ATT_TRUMP_SUITS));
				roundScores = JogreUtils.convertTo2DArray(
								JogreUtils.convertToIntArray(msgEl.getStringAttribute(XML_ATT_SCORES)),
								3, roundsInGame);
				gamePhase = msgEl.getIntAttribute(XML_ATT_PHASE);
				leadCard = Card.fromString(msgEl.getStringAttribute(XML_ATT_LEAD_CARD));
				playedCards[0] = Card.fromString(msgEl.getStringAttribute(XML_ATT_PLAYED_CARD_0));
				playedCards[1] = Card.fromString(msgEl.getStringAttribute(XML_ATT_PLAYED_CARD_1));
				playedCards[2] = Card.fromString(msgEl.getStringAttribute(XML_ATT_PLAYED_CARD_2));
				wonTricks = JogreUtils.convertToIntArray(msgEl.getStringAttribute(XML_ATT_WON_TRICKS));

				bids[0].setHand(decodeHand(msgEl.getStringAttribute(XML_ATT_BID_0)));
				bids[1].setHand(decodeHand(msgEl.getStringAttribute(XML_ATT_BID_1)));
				bids[2].setHand(decodeHand(msgEl.getStringAttribute(XML_ATT_BID_2)));
				hands[0].setHand(decodeHand(msgEl.getStringAttribute(XML_ATT_HAND_0)));
				hands[1].setHand(decodeHand(msgEl.getStringAttribute(XML_ATT_HAND_1)));
				hands[2].setHand(decodeHand(msgEl.getStringAttribute(XML_ATT_HAND_2)));

				// Fix up the cards (since we can't send null or invisible cards)
				if (!leadCard.isKnown()) { leadCard = null; }
				if (!playedCards[0].isKnown()) { playedCards[0] = invisibleCard; }
				if (!playedCards[1].isKnown()) { playedCards[1] = invisibleCard; }
				if (!playedCards[2].isKnown()) { playedCards[2] = invisibleCard; }

				calcTotalScoreForPlayer(0);
				calcTotalScoreForPlayer(1);
				calcTotalScoreForPlayer(2);

				hands[0].sort();
				hands[1].sort();
				hands[2].sort();

				if ((roundNumber >= 0) && (roundNumber < roundsInGame)) {
					currentTrumpSuit = roundTrumpSuits[roundNumber];
				}

				clientModelSetState();
			}
		}

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

	/**
 	 * This method should be overridden in the client model so that it can notified when
	 * the game state is to be set and can make additional set ups.
	 */
	public void clientModelSetState() {}

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

		XMLElement child;

		// Add the element that indicates the misc. values
		child = new XMLElement(XML_STATE_NAME);
		child.setIntAttribute(XML_ATT_CURR_PLAYER_ID, currentPlayerId);
		child.setIntAttribute(XML_ATT_ROUND_NUMBER, roundNumber);
		child.setAttribute(XML_ATT_TRUMP_SUITS, JogreUtils.valueOf(roundTrumpSuits));
		child.setAttribute(XML_ATT_SCORES, JogreUtils.valueOf(JogreUtils.convertTo1DArray(roundScores)));
		child.setIntAttribute(XML_ATT_PHASE, gamePhase);
		if (leadCard != null) {
			child.setAttribute(XML_ATT_LEAD_CARD, leadCard.toString());
		} else {
			child.setAttribute(XML_ATT_LEAD_CARD, unknownCard);
		}
		child.setAttribute(XML_ATT_PLAYED_CARD_0, playedCards[0].toString());
		child.setAttribute(XML_ATT_PLAYED_CARD_1, playedCards[1].toString());
		child.setAttribute(XML_ATT_PLAYED_CARD_2, playedCards[2].toString());
		child.setAttribute(XML_ATT_WON_TRICKS, JogreUtils.valueOf(wonTricks));

		child.setAttribute(XML_ATT_BID_0, encodeBid(0));
		child.setAttribute(XML_ATT_BID_1, encodeBid(1));
		child.setAttribute(XML_ATT_BID_2, encodeBid(2));
		child.setAttribute(XML_ATT_HAND_0, encodeHand(0));
		child.setAttribute(XML_ATT_HAND_1, encodeHand(1));
		child.setAttribute(XML_ATT_HAND_2, encodeHand(2));

		state.addChild(child);

		return state;
	}

	/**
	 * Encode a bid to send across as part of the game state.
	 * Encoding takes the bid of cards and creates a string representation
	 * that is made up of the suits & values of the cards in the bid.
	 *
	 * @param	playerId	The player whose bid is to be encoded.
	 * @return	a string representing the player's bid.
	 */
	private String encodeBid(int playerId) {
		// Assume we'll send the real hand
		Hand toEncode = bids[playerId];

		if ((gamePhase != PHASE_PLAYING) ||
			((playerId != declarer) && (playerId != revealer))) {
			// Send a hand of unknown cards of the same size as the real hand
			toEncode = makeUnknownHand(toEncode.length());
		}

		return JogreUtils.valueOf(JogreUtils.convertTo1DArray(toEncode.extractSuitsAndValues()));
	}

	/**
	 * Encode a hand to send across as part of the game state.
	 * Encoding takes the hand of cards and creates a string representation
	 * that is made up of the suits & values of the cards in the bid.
	 *
	 * @param	playerId	The player whose bid is to be encoded.
	 * @return	a string representing the player's hand.
	 */
	private String encodeHand(int playerId) {
		// Assume we'll send the real hand
		Hand toEncode = removeInvisibles (hands[playerId]);

		if ((gamePhase != PHASE_PLAYING) ||
			(playerId != revealer)) {
			// Send a hand of unknown cards of the same size as the real hand
			toEncode = makeUnknownHand(toEncode.length());
		}

		return JogreUtils.valueOf(JogreUtils.convertTo1DArray(toEncode.extractSuitsAndValues()));
	}

	/**
	 * Make a hand that is composed of unknown cards.
	 *
	 * @param	size	The number of cards to put into the hand.
	 * @return	A hand with size number of unknown cards in it.
	 */
	private Hand makeUnknownHand(int size) {
		Hand newHand = new Hand();

		while (size != 0) {
			newHand.addCard(unknownCard);
			size -= 1;
		}

		return newHand;
	}

	/**
	 * Decode a string sent from the server back into a hand of cards.
	 *
	 * @param	encoded		The string representation of the hand.
	 * @return	The hand.
	 */
	private Hand decodeHand(String encoded) {
		int [] singleArray = JogreUtils.convertToIntArray(encoded);

		return new Hand(JogreUtils.convertTo2DArray(singleArray, 2, singleArray.length / 2));
	}

}
