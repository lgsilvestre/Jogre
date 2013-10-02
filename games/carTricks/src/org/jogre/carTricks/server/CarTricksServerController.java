/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks Server
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

import nanoxml.XMLElement;

import org.jogre.common.Table;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.PlayerList;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommNextPlayer;
import org.jogre.common.util.JogreUtils;
import org.jogre.carTricks.common.CommCarTricksSetBid;
import org.jogre.carTricks.common.CommCarTricksPlayCard;
import org.jogre.carTricks.common.CommCarTricksRequestHand;
import org.jogre.carTricks.common.CommCarTricksSendHand;
import org.jogre.carTricks.common.CommCarTricksMoveCar;
import org.jogre.carTricks.common.CommCarTricksScore;
import org.jogre.carTricks.common.CommCarTricksGetTrackDB;
import org.jogre.carTricks.common.CommCarTricksTrackDB;
import org.jogre.carTricks.common.CommCarTricksTileRequest;
import org.jogre.carTricks.common.CommCarTricksTrackTileData;
import org.jogre.carTricks.common.CarTricksTrackDB;
import org.jogre.carTricks.common.CarTricksTrackDBCache;
import org.jogre.carTricks.common.CarTricksTrackDBTile;
import org.jogre.carTricks.common.CarTricksCustomGameProperties;
import org.jogre.carTricks.common.CarTricksCard;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;
import org.jogre.server.ServerProperties;

/**
 * Server controller for the game Car Tricks
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CarTricksServerController extends ServerController {

	private CarTricksCustomGameProperties customGameProperties;
	private CarTricksTrackDBCache trackCache;

    /**
     * Constructor to create a Car Tricks controller.
     *
     * @param gameKey  Game key.
     */
    public CarTricksServerController (String gameKey) {
        super (gameKey);

		// Create the DB cache on the data directory
		String databaseDir = ServerProperties.getInstance().getStringCustomProp("carTricks", "dataRootDir", "none");
		trackCache = CarTricksTrackDBCache.getInstance(databaseDir);

		// Now create a custom properties object for Car Tricks
		customGameProperties = new CarTricksCustomGameProperties (trackCache.getValidTracks());
    }

	/**
	 * We provide a getCustomGameProperties() method so that we can add the list of
	 * valid tracks as custom properties of the game.
	 *
	 * @return the XML tree of a list of tracks
	 */
	public XMLElement getCustomGameProperties() {
		return (customGameProperties.flatten());
	}

    /**
     * Create a new model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
		Table theTable = getTable(tableNum);
		CarTricksTrackDB track = trackCache.findTrack(
							theTable.getProperty("trackName"),
							Integer.parseInt(theTable.getProperty("fp", "0")));
		int numPlayers = Integer.parseInt(theTable.getProperty("players"));
		boolean enableEventCards = ("t".equals(theTable.getProperty("enableEventCards")));

		if (track != null) {
			// Create the server model for this table
			setModel (tableNum, new CarTricksServerModel(numPlayers, track, enableEventCards));
		} else {
			// The client asked us to start a track which we don't have...
			// FIXME: Probably ought to do something...
		}
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

		if (messageType.equals(CommCarTricksRequestHand.XML_NAME)) {
			handleRequestHand(conn, tableNum, new CommCarTricksRequestHand(message));
		} else if (messageType.equals(CommCarTricksSetBid.XML_NAME)) {
			handleSetBid(conn, tableNum, new CommCarTricksSetBid(message));
		} else if (messageType.equals(CommCarTricksPlayCard.XML_NAME)) {
			handlePlayCard(conn, tableNum, new CommCarTricksPlayCard(message));
		} else if (messageType.equals(CommCarTricksMoveCar.XML_NAME)) {
			handleMoveCar(conn, tableNum, new CommCarTricksMoveCar(message));
		} else if (messageType.equals(CommCarTricksGetTrackDB.XML_NAME)) {
			handleGetTrackDB(conn, tableNum, new CommCarTricksGetTrackDB(message));
		} else if (messageType.equals(CommCarTricksTileRequest.XML_NAME)) {
			handleTileRequest(conn, tableNum, new CommCarTricksTileRequest(message));
		}
	}

	/**
	 * Handle the requestHand message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theReqMsg		The message requesting the hand.
	 */
	private void handleRequestHand(ServerConnectionThread conn, int tableNum, CommCarTricksRequestHand theReqMsg) {
		String userName = theReqMsg.getUsername();
		CarTricksServerModel model = (CarTricksServerModel) getModel(tableNum);
		int player_id = getSeatNum (userName, tableNum);

		// Send the user his hand
		conn.transmitToTablePlayer (userName, tableNum, new CommCarTricksSendHand(userName, model.getPlayerHand(player_id)));
	}

	/**
	 * Handle the setBid message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theBidMsg		The message making the bid.
	 */
	private void handleSetBid(ServerConnectionThread conn, int tableNum, CommCarTricksSetBid theBidMsg) {
		String userName = theBidMsg.getUsername();
		CarTricksServerModel model = (CarTricksServerModel) getModel(tableNum);
		int player_id = getSeatNum (userName, tableNum);

		// Set the bid in the model
		model.setBid(player_id, theBidMsg.getBid());
		model.playCard(player_id, theBidMsg.getCard());

		// If all bids are now in, then
		if (model.allBidsDone()) {
			// If all of the bids are now in, then we can reveal the selected cards to everyone and
			// start the game.

			Table theTable = getTable(tableNum);

			// Send a PlayCard message from all players to all players so that everyone sees what cards were played
			for (int i=0; i<model.getNumPlayers(); i++) {
				String tempPlayerName = theTable.getPlayerList().getPlayer(i).getPlayerName();
				conn.transmitToTablePlayers (tempPlayerName, tableNum, new CommCarTricksPlayCard(tempPlayerName, model.getPlayedCard(i), false));
			}

			// There is a new active player
			conn.transmitToTablePlayers(tableNum, genNextPlayerMsg(theTable));

		} else {
			// All of the bids are not in, but we should let all the players know that this player
			// has finished making his bid.

			// Send a PlayCard message to everyone (except the sender) indicating that this player played an unknown card
			conn.transmitToTablePlayers (userName, tableNum, new CommCarTricksPlayCard(userName, new CarTricksCard(CarTricksCard.UNKNOWN, CarTricksCard.MIN_VALUE), false));
		}
	}

	/**
	 * Handle the playCard message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param thePlayMsg	The message playing the card.
	 */
	private void handlePlayCard(ServerConnectionThread conn, int tableNum, CommCarTricksPlayCard thePlayMsg) {
		String userName = thePlayMsg.getUsername();
		CarTricksServerModel model = (CarTricksServerModel) getModel(tableNum);
		int player_id = getSeatNum (userName, tableNum);
		Table theTable = getTable(tableNum);

		// If this play is valid, then send the message to all the players
		if (model.playCard(player_id, thePlayMsg.getCard())) {
			conn.transmitToTablePlayers(userName, tableNum, thePlayMsg);
			conn.transmitToTablePlayers(tableNum, genNextPlayerMsg(theTable));
		}
	}

	/**
	 * Handle the moveCar message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theMoveMsg	The message moving the car.
	 */
	private void handleMoveCar(ServerConnectionThread conn, int tableNum, CommCarTricksMoveCar theMoveMsg) {
		String userName = theMoveMsg.getUsername();
		CarTricksServerModel model = (CarTricksServerModel) getModel(tableNum);
		int player_id = getSeatNum (userName, tableNum);
		Table theTable = getTable(tableNum);

		// If this play is valid, then send the message to all the players
		if (model.moveCar(player_id, theMoveMsg.getPath())) {
			conn.transmitToTablePlayers(userName, tableNum, theMoveMsg);
			conn.transmitToTablePlayers(tableNum, genNextPlayerMsg(theTable));

			if (model.isGameOver()) {
				// Calculate everyone's score.
				model.calculateScores();

				// Send the scores out to everyone, and calculate the result array & score array as well
				int [] resultArray = new int [model.getNumPlayers()];
				String scoreString = "";
				for (int i=0; i<resultArray.length; i++) {
					String tempPlayerName = theTable.getPlayerList().getPlayer(i).getPlayerName();
					resultArray[i] = (model.isWinner(i) ? IGameOver.WIN : IGameOver.LOSE);
					scoreString = scoreString + " " + model.getScore(i);
					conn.transmitToTablePlayers(tableNum, new CommCarTricksScore(tempPlayerName, model.getBid(i), model.getScore(i)));
				}

				// Call the gameOver routine in the parent to indicate that the game is now over.
				gameOver(conn,
						 tableNum,
						 theTable.getPlayerList().getInGamePlayers(),
						 resultArray,
						 scoreString,
						 null);
			}
		}
	}

	/**
	 * Handle the getTrackDB message from a client.
	 * Note: When this message is sent, there isn't a server model attached to the table
	 *       yet.  So, the track database needs to be found in the cache by name & fingerprint
	 *		 and not by looking in the model...
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theMsg		The message asking for the database
	 */
	private void handleGetTrackDB(ServerConnectionThread conn, int tableNum, CommCarTricksGetTrackDB theMsg) {
		Table theTable = getTable(tableNum);
		CarTricksTrackDB track = trackCache.findTrack(
							theTable.getProperty("trackName"),
							Integer.parseInt(theTable.getProperty("fp", "0")));
		conn.transmitToTablePlayer(theMsg.getUsername(), tableNum, new CommCarTricksTrackDB(track));
	}

	/**
	 * Handle the get Tile data message from a client.
	 * Note: When this message is sent, there isn't a server model attached to the table
	 *       yet.  So, the track database needs to be found in the cache by name & fingerprint
	 *		 and not by looking in the model...
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theMsg		The message asking for the tile data
	 */
	private void handleTileRequest(ServerConnectionThread conn, int tableNum, CommCarTricksTileRequest theMsg) {
		Table theTable = getTable(tableNum);
		CarTricksTrackDB theDB = trackCache.findTrack(
							theTable.getProperty("trackName"),
							Integer.parseInt(theTable.getProperty("fp", "0")));
		CarTricksTrackDBTile theTile = theDB.getTileByName(theMsg.getTileName());
		conn.transmitToTablePlayer(
						theMsg.getUsername(),
						tableNum,
						new CommCarTricksTrackTileData(theTile.getFilename(), theTile.getRawData()));
	}

	/**
	 * Change the current active player at the table and tell everyone about it.
	 *
	 * @param	theTable	The table whose active player is to be set.
	 */
	private CommNextPlayer genNextPlayerMsg (Table theTable) {
		String playerName;
		int playerSeatNum = ((CarTricksServerModel) theTable.getModel()).getActivePlayerId();

		// If the seat number is -1, then it's everyone's turn, so set the
		// player to "NO_PLAYER"
		if (playerSeatNum == -1) {
			playerName = PlayerList.NO_PLAYER;
		} else {
			playerName = theTable.getPlayerList().getPlayer(playerSeatNum).getPlayerName();
		}

		theTable.nextPlayer(playerName);
		return new CommNextPlayer(theTable.getTableNum(), playerName);
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
