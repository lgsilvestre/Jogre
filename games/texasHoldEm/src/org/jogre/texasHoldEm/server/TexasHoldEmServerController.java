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

import nanoxml.XMLElement;

import org.jogre.common.Table;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommNextPlayer;
import org.jogre.texasHoldEm.common.CommTexasHoldEmBidAction;
import org.jogre.texasHoldEm.common.CommTexasHoldEmCommonCard;
import org.jogre.texasHoldEm.common.CommTexasHoldEmGiveHand;
import org.jogre.texasHoldEm.common.CommTexasHoldEmHandOver;
import org.jogre.texasHoldEm.common.CommTexasHoldEmHandRequest;
import org.jogre.texasHoldEm.common.CommTexasHoldEmOfficialTime;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for the game TexasHoldEm
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmServerController extends ServerController {

	/**
	 * Constructor to create a TexasHoldEm controller.
	 *
	 * @param gameKey  Game key.
	 */
	public TexasHoldEmServerController (String gameKey) {
		super (gameKey);
	}

	/**
	 * Create a new model when the game starts.
	 *
	 * @see org.jogre.server.ServerController#startGame(int)
	 */
	public void startGame (int tableNum) {
		Table theTable = getTable(tableNum);

		// Get the table properties
		int numPlayers = theTable.getNumOfPlayers();
		int initialBankroll = Integer.parseInt(theTable.getProperty("initialBankroll"));
		int initialBlindSchedule = Integer.parseInt(theTable.getProperty("initialBlindSched"));
		int blindAdvanceTime = Integer.parseInt(theTable.getProperty("BlindAdvance"));
		int raiseLimit = Integer.parseInt(theTable.getProperty("RaiseLimit"));

		// Create the model
		TexasHoldEmServerModel model = new TexasHoldEmServerModel (numPlayers,
		                                                           initialBankroll,
		                                                           initialBlindSchedule,
		                                                           blindAdvanceTime,
		                                                           raiseLimit);
		setModel (tableNum, model);
	}

	/**
	 * Handle receiving messages from the clients
	 *
	 * @param conn		The connection receiving the message.
	 * @param message	The message from the client.
	 * @param tableNum	The table number that the client is playing on.
	 *
	 */
	public void parseTableMessage(ServerConnectionThread conn, XMLElement message, int tableNum) {
		String messageType = message.getName();

		if (messageType.equals (CommTexasHoldEmHandRequest.XML_NAME)) {
			handleHandRequest (conn, tableNum, new CommTexasHoldEmHandRequest (message));
		} else if (messageType.equals (CommTexasHoldEmBidAction.XML_NAME)) {
			handleBidAction (conn, tableNum, new CommTexasHoldEmBidAction (message));
		}
	}

	/**
	 * Handle the request for a player's hand
	 */
	private void handleHandRequest (ServerConnectionThread conn, int tableNum, CommTexasHoldEmHandRequest theHandMsg) {
		String userName = theHandMsg.getUsername();
		TexasHoldEmServerModel model = (TexasHoldEmServerModel) getModel(tableNum);
		int playerId = getSeatNum (userName, tableNum);

		conn.transmitToTablePlayer (
		    userName,
		    tableNum, 
		    new CommTexasHoldEmGiveHand (
		        userName,
		        playerId,
		        model.getPlayerCard(playerId, 0),
		        model.getPlayerCard(playerId, 1)
		    )
		);

		// If this is the first deal of the game, then we need to send a
		// nextPlayer message to everyone so that the clients start with the
		// correct player.
		if (model.isFirstDeal()) {
			model.setFirstDeal(false);
			sendNextPlayerMsg (conn, tableNum, model);
		}
			
	}

	/*
	 * Handle a message that tells us the bid of a given player
	 */
	private void handleBidAction (ServerConnectionThread conn, int tableNum, CommTexasHoldEmBidAction theBidMsg) {
		String userName = theBidMsg.getUsername();
		TexasHoldEmServerModel model = (TexasHoldEmServerModel) getModel(tableNum);
		int playerId = getSeatNum (userName, tableNum);

		// Tell the model that we're making this bid.
		if (model.makeBid(playerId, theBidMsg.getAmount())) {
			// It was valid, so tell everyone this message
			conn.transmitToTablePlayers(tableNum, theBidMsg);

			model.advanceToNextBidder();
			if (model.isHandOver()) {
				// The hand is over, so determine who the winner of the hand is.
				boolean shouldReveal = model.determineHandWinner();
				if (shouldReveal) {
					sendAllHands(conn, tableNum, userName, model);
				}

				// Let everyone know who won the hand
				conn.transmitToTablePlayers(tableNum,
				        new CommTexasHoldEmHandOver(userName,
				                                    model.getPlayerHoldings(),
				                                    model.getWinningHandValue(),
				                                    model.getHandValues()));

				if (model.isGameOver()) {
					// The game is over, so we have a winner!
					Table theTable = getTable(tableNum);
					int winnerId = model.getWinner();
					String winnerName = theTable.getPlayerList().getPlayer(winnerId).getPlayerName();
					gameOver(conn, tableNum, winnerName, IGameOver.WIN);
				} else {
					// The game is not over, so reshuffle and tell everyone to start the
					// next hand.
					model.startNextHand();
					model.dealCards();

					sendNextPlayerMsg (conn, tableNum, model);
				}
			} else {
				// Hand isn't over yet.
				if (model.isRoundOver()) {
					// The round is over, so we need to display some more common
					// cards and start bidding the next round.
					model.advanceToNextRound();
					int roundNum = model.getRoundNum();
					if (roundNum == 1) {
						// After round 0, we need to send 3 common cards
						conn.transmitToTablePlayers(tableNum, new CommTexasHoldEmCommonCard(userName, model.getCommonCard(0), 0, false));
						conn.transmitToTablePlayers(tableNum, new CommTexasHoldEmCommonCard(userName, model.getCommonCard(1), 1, false));
					}
					conn.transmitToTablePlayers(tableNum, new CommTexasHoldEmCommonCard(userName, model.getCommonCard(roundNum+1), roundNum+1, true));
				}
				sendNextPlayerMsg (conn, tableNum, model);
			}
		}
	}
	
	/*
	 * Change the current active player at the table and tell everyone about it.
	 * This will also update the official time until next blind change.
	 *
	 * @param	conn        The connection thread for the game.
	 * @param	tableNum	The table whose active player is to be set.
	 * @param	model		The model at that table.
	 */
	private void sendNextPlayerMsg (ServerConnectionThread conn, int tableNum, TexasHoldEmServerModel model) {
		Table theTable = getTable(tableNum);
		int playerSeatNum = model.getActivePlayerId();
		String playerName = theTable.getPlayerList().getPlayer(playerSeatNum).getPlayerName();

		// Update the official time.
		int timeToGo = model.updateBlindTime();
		conn.transmitToTablePlayers(
		    tableNum,
		    new CommTexasHoldEmOfficialTime(model.getCurrentBlindScheduleStage(),
		                                    timeToGo));

		// Update the next player
		theTable.nextPlayer(playerName);
		conn.transmitToTablePlayers(tableNum, new CommNextPlayer(tableNum, playerName));
	}

	/*
	 * Send all of the hands of players still in the game to all players.
	 * This happens at the "showdown" when players compare hands.
	 */
	private void sendAllHands (ServerConnectionThread conn, int tableNum, String userName, TexasHoldEmServerModel model) {
		Table theTable = getTable(tableNum);

		// Pass cards out
		for (int i=0; i < model.getNumPlayers(); i++) {
			if (model.handVisible(i)) {
				// Send this player's cards to everyone else
				conn.transmitToTablePlayers (
						theTable.getPlayerList().getPlayer(i).getPlayerName(),
						tableNum,
						new CommTexasHoldEmGiveHand (
							userName,
							i,
							model.getPlayerCard(i, 0),
							model.getPlayerCard(i, 1)
						)
				);
			}
		}
	}

    /**
     * Verify that the game is over.
	 * (A client has just told us that the game is over, but we ought to verify it)
	 *
	 * Note: The TexasHoldEm server already keeps track of game over, so this
	 * routine doesn't do anything.
     *
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
	}

}
