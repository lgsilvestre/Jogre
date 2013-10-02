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
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommNextPlayer;
import org.jogre.common.games.Deck;
import org.jogre.common.util.JogreLogger;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;
import org.jogre.spades.common.CommSpadesHandOver;
import org.jogre.spades.common.CommSpadesMakeBid;
import org.jogre.spades.common.CommSpadesPlayCard;
import org.jogre.spades.common.CommSpadesRequestHand;
import org.jogre.spades.common.CommSpadesRoundOver;

/**
 * Server table parser for spades. This server controller is slightly more
 * complex since it needs to keep track of all tables' deck of cards. By using a
 * hashtable, with the key being the table number, we are able to keep Deck
 * objects in server memory. This will also be useful for other card games. A
 * seperate variable named dealtHands keeps track of all the connected players
 * grabbing their hands from the server. When someone tries to grab a deck from
 * the server and all hands have already been grabbed, then a new deck is
 * shuffled and all variables are reset again.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class SpadesServerController extends ServerController {

	// Logger to debug this class
	private JogreLogger logger = new JogreLogger(this.getClass());

	/**
	 * Constructor which takes a game key (read from directory).
	 *
	 * @param gameKey
	 *            gamekey (read from directory).
	 */
	public SpadesServerController(String gameKey) {
		super(gameKey);
	}

	/**
     * Start game creates a new model.
     *
	 * @see org.jogre.server.ServerController#startGame(int)
	 */
	public void startGame (int tableNum) {
		setModel (tableNum, new SpadesServerTableModel());
	}

	public void gameOver(ServerConnectionThread conn, int tableNum, int resultType) {
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the server table model when a table number is specified
	 *
	 * @param tableNum
	 *            Table number
	 * @return the server table model associated to the table number provided
	 */
	public synchronized SpadesServerTableModel getServerTableModel(int tableNum) {

		// Getting model from table so when tables get removed, so do the models.
		JogreModel model = this.getModel(tableNum);
		if (model == null) {
			model = new SpadesServerTableModel();
			this.setModel(tableNum, model);
		}
		return (SpadesServerTableModel) model;

	}

	/**
	 * Parse a spades message and does all the spades server processing.
	 *
	 * @see org.jogre.server.ITableParser#parseTableMessage(org.jogre.server.ServerConnectionThread,
	 *      nanoxml.XMLElement)
	 */
	public void parseTableMessage(ServerConnectionThread conn,
			XMLElement message, int tableNum) {

		String messageType = message.getName();

		logger.debug("parseTableMessage", "messageType: " + messageType);

		try
		{
			if (messageType.equals(CommSpadesRequestHand.XML_NAME)) {
				requestHand(conn, new CommSpadesRequestHand(message), tableNum);
			} else if (messageType.equals(CommSpadesPlayCard.XML_NAME)) {
				playCard(conn, new CommSpadesPlayCard(message), tableNum);
			} else if (messageType.equals(CommSpadesMakeBid.XML_NAME)) {
				makeBid(conn, new CommSpadesMakeBid(message), tableNum);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Play card from CommSpadesRequestHand object
	 *
	 * @param conn
	 *            Server connection thread
	 * @param playCard
	 *            Spades play card communication object
	 * @param tableNum
	 *            Table number
	 */
	public void playCard(ServerConnectionThread conn, CommSpadesPlayCard playCard, int tableNum) {

		// Get user who played the card
		String username = playCard.getUsername();

		// Get server table model by table number
		SpadesServerTableModel model = getServerTableModel(tableNum);

		// Get seat number of username by table number
		int seatNum = this.getSeatNum(username, tableNum);

		// Store card in server table model
		model.playCard(seatNum, playCard.getCard());

		// Trasnmit card to other players at table
		conn.transmitToTablePlayers(username, tableNum, playCard);

		// Check if hand is finished
		if (model.isHandOver()) {

			// Get hand number and seat who won the trick
			int hand = model.getHand();
			int seatWon = model.getTrickFromRound();

			// Transmit hand over object to all players
			CommSpadesHandOver handOver = new CommSpadesHandOver(hand, seatWon);
			conn.transmitToTablePlayers(tableNum, handOver);

			// Reset table model for next hand to be played
			model.nextHand();

			// Get table object
			Table table = conn.getTableList().getTable(tableNum);

			// Check if round if over (all hands have been played)
			if (model.getHand() > 12) {

				// Get round number and team scores and bags.
				int round = model.getRound();
				int[] team1 = model.getScoreAndBags(0);
				int[] team2 = model.getScoreAndBags(1);
				int score = 0;
				int bags = 1;
				model.calculateScoreAndBags(0);
				model.calculateScoreAndBags(1);

				// Create round-over transmission object
				CommSpadesRoundOver roundOver = new CommSpadesRoundOver(
						round, team1[score], team2[score], team1[bags],
						team2[bags]);

				// Check if game is over before sending round-over object
				if (model.gameOver())
				{
					// Send each player a round-over object containing a game over object
					for (int i = 0; i < 4; i++)
					{
						int status = model.getGameStatus(i);
						int scoreIncrement = 0;
						if (status == IGameOver.LOSE)
							scoreIncrement = -20;
						else if (status == IGameOver.WIN)
							scoreIncrement = 20;
						CommGameOver gameOver = new CommGameOver (status);
						roundOver.setGameOver(gameOver);
						String usernameTo = table.getPlayerList().getPlayer(i).getPlayerName();
						conn.transmitToTablePlayer(usernameTo, tableNum, roundOver);
					}

				} else {

					// If game isn't over, transmit round-over to them
					conn.transmitToTablePlayers(tableNum, roundOver);

					// If game is not over, next player is the next dealer
					Player player = table.getPlayerList().getPlayer(model.nextDealerSeat());
					table.nextPlayer(player);
					CommNextPlayer nextPlayer = new CommNextPlayer(tableNum, player.getPlayerName());
					conn.transmitToTablePlayers(tableNum, nextPlayer);
				}

				// Reset model for next round of hands
				model.nextRound();

			} else {

				// If round is not over, next player is player who won the hand
				Player player = table.getPlayerList().getPlayer(seatWon);
				table.nextPlayer(player);
				CommNextPlayer nextPlayer = new CommNextPlayer(tableNum, player.getPlayerName());
				conn.transmitToTablePlayers(tableNum, nextPlayer);

			}
		} else {

			// If hand is not over, get next player from table
			Table table = conn.getTableList().getTable(tableNum);
			table.nextPlayer();
			CommNextPlayer nextPlayer = new CommNextPlayer(tableNum,
					table.getPlayerList().getCurrentPlayerName());
			conn.transmitToTablePlayers(tableNum, nextPlayer);
		}
	}

	/**
	 * Request hand from CommSpadesRequestHand object
	 *
	 * @param conn
	 *            Server connection thread
	 * @param requestHand
	 *            Spades request hand communication object
	 * @param tableNum
	 *            Table number
	 */
	public void requestHand(ServerConnectionThread conn, CommSpadesRequestHand requestHand, int tableNum) {

		// Get user who requested the hand of cards
		String username = requestHand.getUsername();

		// Get server table model by table number
		SpadesServerTableModel model = getServerTableModel(tableNum);

		// Get seat number of username by table number
		int seatNum = this.getSeatNum(username, tableNum);

		// Only give hands to real players (spectators don't get one)
		if (seatNum >= 0) {
			// Get hand from server table model by seat number
			Deck hand = model.requestHand(seatNum);

			logger.debug("requestHand", "hand(" + seatNum + ", " + username + "): " + hand);

			// Transmit hand to specific user
			conn.transmitToTablePlayer(username, tableNum, hand);
		}

	}

	/**
	 * Make bid from CommSpadesMakeBid object
	 *
	 * @param conn
	 *            Server connection thread
	 * @param makeBid
	 *            Spades bid communication object
	 * @param tableNum
	 *            Table number
	 */
	public void makeBid(ServerConnectionThread conn, CommSpadesMakeBid makeBid, int tableNum) {

		// Get user who requested the hand of cards
		String username = makeBid.getUsername();

		// Get server table model by table number
		SpadesServerTableModel model = getServerTableModel(tableNum);

		// Get seat number of username by table number
		int seatNum = this.getSeatNum(username, tableNum);

		// Set bid within server table model
		model.bid(seatNum, makeBid.getBid());

		// Transmit bid to all players
		conn.transmitToTablePlayers(tableNum, makeBid);

	}
}