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

import nanoxml.XMLElement;

import org.jogre.common.Table;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.PlayerList;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommNextPlayer;
import org.jogre.common.util.JogreUtils;
import org.jogre.ninetynine.common.NinetyNineCoreModel;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

import org.jogre.ninetynine.common.CommNinetyNineMakeBid;
import org.jogre.ninetynine.common.CommNinetyNineRequestHand;
import org.jogre.ninetynine.common.CommNinetyNineSendHand;
import org.jogre.ninetynine.common.CommNinetyNineAskUpgrade;
import org.jogre.ninetynine.common.CommNinetyNineStartRound;
import org.jogre.ninetynine.common.CommNinetyNinePlayCard;
import org.jogre.ninetynine.common.CommNinetyNineRoundScore;
import org.jogre.ninetynine.common.CommNinetyNineTrumpSuit;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

/**
 * Server controller for the game of Ninety Nine
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class NinetyNineServerController extends ServerController {

    /**
     * Constructor to create a Ninety Nine server controller.
     *
     * @param gameKey  Game key.
     */
    public NinetyNineServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
		Table theTable = getTable(tableNum);
		int numRoundsInGame = Integer.parseInt(theTable.getProperty("rounds"));

		NinetyNineServerModel model = new NinetyNineServerModel(numRoundsInGame);
        setModel (tableNum,model);

		model.shuffleAndDeal();
		model.goToBidPhase();
    }

	/**
	 * Handle receving messages from the clients.
	 *
	 * @param conn      Connection to a client.
	 * @param message   Message.
	 * @param tableNum  Table number of message.
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
		String messageType = message.getName();

		if (messageType.equals(CommNinetyNineRequestHand.XML_NAME)) {
			handleRequestHand(conn, tableNum, new CommNinetyNineRequestHand(message));
		} else if (messageType.equals(CommNinetyNineMakeBid.XML_NAME)) {
			handleMakeBid(conn, tableNum, new CommNinetyNineMakeBid(message));
		} else if (messageType.equals(CommNinetyNinePlayCard.XML_NAME)) {
			handlePlayCard(conn, tableNum, new CommNinetyNinePlayCard(message));
		}
	}

	/**
	 * Handle the requestHand message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theReqMsg		The message requesting the hand.
	 */
	private void handleRequestHand(ServerConnectionThread conn, int tableNum, CommNinetyNineRequestHand theReqMsg) {
		String userName = theReqMsg.getUsername();
		NinetyNineServerModel model = (NinetyNineServerModel) getModel(tableNum);
		int playerId = getSeatNum (userName, tableNum);

		if (playerId >= 0) {
			// Send the user his hand
			conn.transmitToTablePlayer (userName, tableNum,
					new CommNinetyNineSendHand( userName,
												model.getHand(playerId),
												model.getTrumpSuit()));
		} else {
			// Send the observer a message so that he gets the trump suit
			conn.transmitToTablePlayer (userName, tableNum,
					new CommNinetyNineTrumpSuit( userName, model.getTrumpSuit()));
		}
	}

	/**
	 * Handle a Make bid message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theReqMsg		The message making the bid
	 */
	private void handleMakeBid(ServerConnectionThread conn, int tableNum, CommNinetyNineMakeBid theBidMsg) {
		int revealer, declarer;
		String revealerName, declarerName;
		int firstPlayer;

		String userName = theBidMsg.getUsername();
		NinetyNineServerModel model = (NinetyNineServerModel) getModel(tableNum);
		int playerId = getSeatNum (userName, tableNum);

		// If we (the server) haven't seen this player bid before, then tell all of the
		// clients that this player has bid.
		if (model.getBidType(playerId) == NinetyNineCoreModel.BID_UNKNOWN) {
			// Tell other players that this one has made his bid
			conn.transmitToTablePlayers (userName, tableNum,
					new CommNinetyNineMakeBid ( userName, null, NinetyNineCoreModel.BID_UNKNOWN ));
		}

		// Change the bid in the server.
		model.giveBid(playerId, theBidMsg.getBidHand(), theBidMsg.getBidType(), theBidMsg.getLeadPlayer());

		// Determine what to do next...
		switch (model.evaluateBids()) {
			case NinetyNineCoreModel.BID_NOT_COMPLETE :
				// Bidding not complete yet, so nothing to do.
				break;
			case NinetyNineCoreModel.NO_PREMIUM_BIDS :
				// No premium bids made, so set the active player and start the round.
				firstPlayer = model.getCurrentPlayerId();
				setLocalNextPlayer(tableNum, firstPlayer);
				conn.transmitToTablePlayers(tableNum, new CommNinetyNineStartRound(firstPlayer));
				model.goToPlayPhase();
				break;
			case NinetyNineCoreModel.DECLARER_ONLY :
				// There is a declarer only, so set the active player and tell
				// all of the clients what the declarers bid was.
				firstPlayer = model.getCurrentPlayerId();
				setLocalNextPlayer(tableNum,firstPlayer);
				declarer = model.getDeclarer();
				declarerName = playerNameForTable(tableNum, declarer);
				conn.transmitToTablePlayers(tableNum,
							new CommNinetyNineMakeBid(  declarerName,
														model.getBid(declarer),
														NinetyNineCoreModel.BID_DECLARE,
														firstPlayer));
				model.goToPlayPhase();
				break;
			case NinetyNineCoreModel.REVEALER_ONLY :
				// There is a revealer only, so tell all of the clients what his
				// bid was, and who he chose to be the lead player.
				revealer = model.getRevealer();
				revealerName = playerNameForTable(tableNum, revealer);
				firstPlayer = model.getCurrentPlayerId();
				setLocalNextPlayer(tableNum, firstPlayer);
				conn.transmitToTablePlayers(tableNum,
							new CommNinetyNineMakeBid(  revealerName,
														model.getBid(revealer),
														NinetyNineCoreModel.BID_REVEAL,
														firstPlayer));
				model.goToPlayPhase();
				break;
			case NinetyNineCoreModel.DECLARER_AND_REVEALER :
				// Need to ask the declarer if he wishes to upgrade to a reveal.
				declarer = model.getDeclarer();
				setLocalNextPlayer(tableNum, declarer);
				conn.transmitToTablePlayers(tableNum,
							new CommNinetyNineAskUpgrade(declarer, model.getRevealer()));
				break;
		}
	}

	/**
	 * Handle the playCard message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param thePlayMsg	The message playing the card
	 */
	private void handlePlayCard(ServerConnectionThread conn, int tableNum, CommNinetyNinePlayCard thePlayMsg) {
		String userName = thePlayMsg.getUsername();
		NinetyNineServerModel model = (NinetyNineServerModel) getModel(tableNum);
		int playerId = getSeatNum (userName, tableNum);
		Card playedCard = thePlayMsg.getCard();

		if (model.isValidPlay(playerId, playedCard)) {
			model.playCard(playerId, playedCard);
			model.evaluateTrick();
			setLocalNextPlayer(tableNum, model.getCurrentPlayerId());
			conn.transmitToTablePlayers(userName, tableNum, thePlayMsg);
			if (model.isRoundOver()) {
				// Score the round
				model.scoreRound();

				// Send an end-of-round message to all players so that they can see what each player
				// bid & scored.
				CommNinetyNineRoundScore scoreMsg = new CommNinetyNineRoundScore(
					model.getBid(0), model.getScoreForRound(0, model.getCurrentRoundNumber()),
					model.getBid(1), model.getScoreForRound(1, model.getCurrentRoundNumber()),
					model.getBid(2), model.getScoreForRound(2, model.getCurrentRoundNumber())
				);
				conn.transmitToTablePlayers(tableNum, scoreMsg);

				// Move onto the next round
				model.readyForNextRound();

				// If the game isn't over yet, then shuffle and deal for the next round
				if (!checkGameOver(conn, tableNum, model)) {
					model.shuffleAndDeal();
				}
			}

			Hand revealHand = model.handToReveal();
			if (revealHand != null) {
				// The lead card has been played, and now is the time to show the hand of
				// the player who bid Reveal to the rest of the players.
				String revealPlayerName = playerNameForTable(tableNum, model.getRevealer());
				conn.transmitToTablePlayers (revealPlayerName, tableNum,
					new CommNinetyNineSendHand( revealPlayerName,
												revealHand,
												-1));
			}
		} else {
			System.out.println("Player " + playerId + " tried to play " + playedCard + ", which was bad play.");
		}
	}

	/*
	 * Return the name of the given player at the given table.
	 *
	 * @param	tableNum			The table number
	 * @param	playerNum			The player number
	 * @return the name of the player
	 */
	private String playerNameForTable(int tableNum, int playerNum) {
		return getTable(tableNum).getPlayerList().getPlayer(playerNum).getPlayerName();
	}

	/*
	 * Set the server's idea of who is to play next.
	 *
	 * @param	tableNum			The table number
	 * @param	nextPlayerSeatNum	The seat number to make the next player
	 */
	private void setLocalNextPlayer(int tableNum, int nextPlayerSeatNum) {
		String playerName;
		Table theTable = getTable(tableNum);

		// If the seat number is -1, then it's everyone's turn, so set the
		// player to "NO_PLAYER"
		if (nextPlayerSeatNum == -1) {
			playerName = PlayerList.NO_PLAYER;
		} else {
			playerName = theTable.getPlayerList().getPlayer(nextPlayerSeatNum).getPlayerName();
		}

		theTable.nextPlayer(playerName);
	}

	/**
	 * Check to see if the game is over.
	 */
	 private boolean checkGameOver(ServerConnectionThread conn, int tableNum, NinetyNineServerModel model) {
		Table theTable = getTable(tableNum);
		boolean value = model.isGameOver();
		if (value) {
			int [] resultArray = new int [3];
			String scoreString = "";
			for (int i=0; i<resultArray.length; i++) {
				resultArray[i] = (model.isWinner(i) ? IGameOver.WIN : IGameOver.LOSE);
				scoreString = scoreString + " " + model.getTotalScore(i);
			}

			// Call the gameOver routine in the parent to indicate that the game is now over.
			gameOver(conn,
					 tableNum,
					 theTable.getPlayerList().getInGamePlayers(),
					 resultArray,
					 scoreString,
					 null);
		}

		return value;
	 }

    /**
     * The server handles game over's internally and doesn't need clients to tell it when the game
	 * is over.  Therefore, this method does nothing, although it is needed to implement the abstract
	 * method defined in ServerController.
     *
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {}

}
