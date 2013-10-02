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

package org.jogre.server.controllers;

import java.util.StringTokenizer;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.IError;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.User;
import org.jogre.common.UserList;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.*;
import org.jogre.common.playerstate.PlayerState;
import org.jogre.server.JogreServer;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;
import org.jogre.server.data.ServerDataException;

/**
 * Class which parses table communication messages (messages with a
 * "table" attribute).
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ServerTableController {

    /** Convenience link to the server. */
	protected JogreServer server;

	/**
	 * Constructor of a ServerTable parser.
	 */
	public ServerTableController() {
	    // Set up fields
	    this.server = JogreServer.getInstance();
	}

	/**
	 * This implements parsing of a table message.
	 *
	 * @param message
	 */
	public synchronized void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
		// First, make sure that the given table number is valid.
		// (Doing this once here means that all of the routines below can assume
		//  that the table is valid.)
		Table table = conn.getTableList().getTable (tableNum);
		if (table == null) {
			return;
		}

		// Retrieve the message type
		String messageType = message.getName();

		try {
			// Parse the message
			if (messageType.equals (Comm.CHAT_TABLE)) {
			    chatTable (conn, new CommChatTable(message), tableNum);
			}
			else if (messageType.equals (Comm.JOIN_TABLE)) {
			    joinTable (conn, new CommJoinTable (message), tableNum, table);
			}
			else if (messageType.equals (Comm.SIT_DOWN)) {
			    sitDown (conn, new CommSitDown (message), tableNum, table);
			}
			else if (messageType.equals (Comm.STAND_UP)) {
			    standUp (conn, new CommStandUp (message), tableNum, table);
			}
			else if (messageType.equals (Comm.READY_TO_START)) {
			    readyToStart (conn, new CommReadyToStart(message), tableNum, table);
			}
			else if (messageType.equals (Comm.NEXT_PLAYER)) {
			    nextPlayer (conn, new CommNextPlayer (message), tableNum, table);
			}
			else if (messageType.equals (Comm.EXIT_TABLE)) {
			    exitTable (conn, new CommExitTable (message), tableNum);
			}
			else if (messageType.equals (Comm.OFFER_DRAW)) {
				offerDraw (conn, new CommOfferDraw (message), tableNum, table);
			}
			else if (messageType.equals (Comm.GAME_OVER)) {
				gameOver (conn, new CommGameOver (message), tableNum, table);
			}
			else if (messageType.equals (Comm.CONTROLLER_PROPERTY)) {
				controllerProperty (conn, new CommControllerProperty (message), tableNum);
			}
			else if (messageType.equals (Comm.CONTROLLER_OBJECT)) {
				controllerObject (conn, new CommControllerObject (message), tableNum);
			}
			else if (messageType.equals (Comm.TABLE_PROPERTY)) {
			    tableProperty (conn, new CommTableProperty (message), table);
			}

			// Delegate to the correct controller to do a additional processing
		    String gameID = conn.getGameID();
		    if (gameID != null) {
		    	ServerController controller = conn.getServerController();
			    if (controller != null)
			        controller.parseTableMessage (conn, message, tableNum);
		    }
		}
		catch (TransmissionException transEx) {
			transEx.printStackTrace();
		}
		catch (ServerDataException sdEx) {
			conn.send (new CommError (IError.SERVER_DATA_ERROR, sdEx.getMessage()));
		}
	}

	/**
	 * Transmit a message to everyone in the room except the person who sent it
	 * in the first place.
	 *
	 * @param conn        Connection back to the client.
	 * @param commMessage Communications message.
	 * @param tableNum    Table number.
	 * @throws TransmissionException
	 */
	private void chatTable (ServerConnectionThread conn, CommChatTable commMessage, int tableNum) throws TransmissionException {
		// Broadcast to all users in a room (in this case a table)
		commMessage.setUsername (conn.getUsername());

		// Retrieve table and then player list from the table
		conn.transmitToTablePlayers (conn.getUsername (), tableNum, commMessage);
	}

	/**
	 * A client is sending a property to the server.  Update all the other clients
	 * at the the table with this property.
	 *
	 * @param conn          Connection back to the client.
	 * @param commContProp  Communications message.
	 * @param tableNum      Table number.
	 * @throws TransmissionException
	 */
	private void controllerProperty (ServerConnectionThread conn, CommControllerProperty commContProp, int tableNum) throws TransmissionException {
		// Transmit to other players
		commContProp.setUsername (conn.getUsername ());
		conn.transmitToTablePlayers (conn.getUsername (), tableNum, commContProp);

		// And also inform server controller
		ServerController controller = conn.getServerController();
		if (controller != null) {

			// Retrieve and check that model isn't null
			JogreModel model = controller.getModel (tableNum);
			if (model != null) {

				// Depending on type of property delegate to the correct adapter method
				switch (commContProp.getStatus()) {
					case CommControllerProperty.TYPE_STRING:
						controller.receiveProperty (model, commContProp.getKey(), commContProp.getValue());
						return;

					case CommControllerProperty.TYPE_INT:
						int value = Integer.parseInt (commContProp.getValue());
						controller.receiveProperty (model, commContProp.getKey(), value);
						return;

					case CommControllerProperty.TYPE_INT_TWO:
						StringTokenizer st = new StringTokenizer (commContProp.getValue());
						int value1 = Integer.parseInt (st.nextToken());
						int value2 = Integer.parseInt (st.nextToken());
						controller.receiveProperty (model, commContProp.getKey(), value1, value2);
						return;
				}
			}
		}
	}

	/**
	 * A client is sending an object to the server. Update all other clients
	 * at a table with this property.
	 *
	 * @param conn          Connection back to the client.
	 * @param commContObj   Communications message.
	 * @param tableNum      Table number.
	 * @throws TransmissionException
	 */
	private void controllerObject (ServerConnectionThread conn, CommControllerObject commContObj, int tableNum) throws TransmissionException {
		// Transmit to other players
		commContObj.setUsername (conn.getUsername ());
		conn.transmitToTablePlayers (conn.getUsername (), tableNum, commContObj);

		// And send to server controller
		ServerController controller = conn.getServerController();
		if (controller != null) {

			// Retrieve and check that model isn't null
			JogreModel model = controller.getModel (tableNum);
			if (model != null) {
				controller.receiveObject (model, commContObj.getData(), tableNum);
			}
		}
	}

	/**
	 * Send a table property to everyone.
	 *
	 * @param conn
	 * @param commTableProperty
	 * @param table              The table.
	 */
	private void tableProperty (ServerConnectionThread conn, CommTableProperty commTableProperty, Table table) {
	    // Update table with key / value
	    String key   = commTableProperty.getKey();
	    String value = commTableProperty.getValue();
	    table.addProperty (key, value);

	    conn.broadcast (commTableProperty);
	    conn.sendDataMessageToAdmin (commTableProperty);
	}

	/**
	 * The new way of performing a game over is that a client MAY inform a server
	 * that a game is over if the game over detection requires a lot of
     * processing e.g. detecting chess game overs.  This means the client tries
     * to detect if its game over after each move and the only time the server
     * needs to check is if the client says its game over.  This enables the
     * server to be more efficient.
	 *
	 * @param conn          Connection to client.
	 * @param commGameOver  GameOver object.
	 * @param tableNum      Table number.
	 * @param table         The table.
	 */
	private void gameOver (ServerConnectionThread conn, CommGameOver commGameOver, int tableNum, Table table) {
		// Ensure that the player sending this message is actually playing the game.
		String msgPlayerName = conn.getUsername();
		if (!table.isParticipatingPlayer (msgPlayerName)) {
			return;
		}

		// Inform server controller that the client says the game is over.
		// The server should perform its own check to ensure this is correct
		ServerController controller = conn.getServerController();
		if (controller != null) {
			// Check status.  It should either be user / resign
			int status = commGameOver.getStatus();
			switch (status) {
				case IGameOver.USER_RESIGNS:
					controller.userResigns (conn, tableNum);
					break;
				default:
					controller.gameOver (conn, tableNum, status);
					break;
			}
		}
	}

	/**
	 * Transmit offer draw communications object.
	 *
	 * @param conn           Connection back to the client.
	 * @param commOfferDraw  Communications message.
	 * @param tableNum       Table number.
	 * @param table          The table.
	 */
	private void offerDraw (ServerConnectionThread conn, CommOfferDraw commOfferDraw, int tableNum, Table table) {
		// Ensure that the player sending this message is actually playing the game.
		String msgPlayerName = conn.getUsername();
		Player player = table.getPlayerList ().getPlayer (msgPlayerName);
		if ((player == null) ||
		    (player.getSeatNum() == Player.NOT_SEATED)) {
			return;
		}

		// Parse the message
		int status = commOfferDraw.getStatus();
		// Message can be accept, decline or request
		if (status == CommInvite.REQUEST) {
			if (table.hasOutstandingDrawOffer()) {
				// If this table has an outstanding draw offer, then take this
				// request for draw as an accept (and do the code below for accept.)
				status = CommInvite.ACCEPT;
			} else {
				// This table doesn't have an outstanding draw offer, so start
				// one, with this player being the instigator
				int offerSerialNum = table.startDrawOffer (player);

				// Send the message to everyone else to ask them for response.
				commOfferDraw.setSerialNum (offerSerialNum);
				commOfferDraw.setUsername (msgPlayerName);
				conn.transmitToTablePlayers (msgPlayerName, tableNum, commOfferDraw, true);
			}
		}

		if (status == CommInvite.ACCEPT) {
			table.acceptDrawOffer (player, commOfferDraw.getSerialNum());

			if (table.drawOfferComplete()) {
				// Clear the draw offer from the table
				table.clearDrawOffer();

				// Delegate this call to the server controller
				ServerController controller = conn.getServerController();
				if (controller != null) {
					controller.usersAgreeDraw (conn, tableNum);
				}
			}
		}
		else if (status == CommInvite.DECLINE) {
			if (table.declineDrawOffer (player, commOfferDraw.getSerialNum())) {
				// Clear the draw offer from the table
				table.clearDrawOffer();

				// Tell everyone that this player declined the draw offer
				commOfferDraw.setUsername (msgPlayerName);
				conn.transmitToTablePlayers (msgPlayerName, tableNum, commOfferDraw);
			}
		}
	}

	/**
	 * Transmit a next player request to users.
	 *
	 * @param conn            Connection to client.
	 * @param commNextPlayer  Next player message.
	 * @param tableNum        Table number.
	 * @param table           The table.
	 */
	private void nextPlayer (ServerConnectionThread conn, CommNextPlayer commNextPlayer, int tableNum, Table table) {
		// Advance to the next player
		table.nextPlayer();

		// Set the username of the next player and transmit this to players at table
		commNextPlayer.setUsername(table.getPlayerList().getCurrentPlayerName());
		conn.transmitToTablePlayers (tableNum, commNextPlayer);
		conn.sendDataMessageToAdmin (commNextPlayer);
	}

	/**
	 * Transmit exit table message.,
	 *
	 * @param conn           Connection to client.
	 * @param commExitTable  Exit table communication message.
	 * @param tableNum       Table number.
	 */
	private void exitTable (ServerConnectionThread conn, CommExitTable commExitTable, int tableNum) throws ServerDataException {
		// Retrieve table list
		TableList tableList = conn.getTableList();
		String username = conn.getUsername();
		int numOfTables = tableList.size();

		// Make sure the player was removed before sending out messages
		if (tableList.removePlayer (tableNum, username)) {

			// Broadcast to all user and admin
			CommExitTable commExitReply = new CommExitTable (tableNum, username);
			conn.broadcast (username, commExitReply);		// inform all users
			conn.sendDataMessageToAdmin (commExitReply);

			// Update snapshot as a table may have been removed
			if (tableList.size() != numOfTables) {
				server.getServerData().updateSnapshot (
				                            conn.getGameID(),
				                            conn.getUserList().size(),
				                            conn.getTableList().size());
			}
		}
	}

	/**
	 * User joins a table.
	 *
	 * @param conn            Connection to client.
	 * @param commJoinTable   CommJoinTable communication message from client.
	 * @param tableNum        Table number we are interested in.
	 * @param table           The table we are interested in.
	 */
	private void joinTable (ServerConnectionThread conn, CommJoinTable commJoinTable, int tableNum, Table table) {
	    // Unpack join table message from XML
	    String username = conn.getUsername();
	    User newUser = conn.getUserList().getUser(username);

		// Make sure the player joined successfully before sending out replies
		if (table.addPlayer (newUser)) {

			// Create response join table message
			// The user who is joining also gets the player list
			commJoinTable = new CommJoinTable (username, table);
			conn.transmit (username, commJoinTable);

			// set back to normal again and broadcast the table object to all users
			// omiting this user (as they already have it) and to admin.
			commJoinTable = new CommJoinTable (username, tableNum);
			conn.broadcast (username, commJoinTable);
			conn.sendDataMessageToAdmin (commJoinTable);
		}
	}

	/**
	 * User stands up at a table.
	 *
	 * @param conn         Connection to the server.
	 * @param commStandUp  CommStandUp message.
	 * @param tableNum     The table number we're standing up from.
	 * @param table        The table we're standing up from.
	 */
	private void standUp (ServerConnectionThread conn, CommStandUp commStandUp, int tableNum, Table table) {
		// Get the player
		Player player = table.getPlayerList ().getPlayer (conn.getUsername());
		if (player == null) {
			return;
		}

		// Change the player's state
		player.stand();

		// Transmit to all players at table & admin that this user has sat down
		CommPlayerState commPlayerState = new CommPlayerState (
		     tableNum, conn.getUsername(), player.getState());
		conn.transmitToTablePlayers (tableNum, commPlayerState);
		conn.sendDataMessageToAdmin (commPlayerState);
	}

	/**
	 * Ready to start.
	 *
	 * @param conn         Connection to client.
	 * @param commReady    Ready to start communications message.
	 * @param tableNum     Table number.
	 * @param table        The table
	 */
	private void readyToStart (ServerConnectionThread conn, CommReadyToStart commReady, int tableNum, Table table) {
		// Get the player
		PlayerList players = table.getPlayerList();
		Player player = players.getPlayer (conn.getUsername());
		if (player == null) {
			return;
		}

		// Change the player's state
		player.start ();

		// Transmit state to table players & admin
		CommPlayerState commPlayerState = new CommPlayerState (
		    tableNum, conn.getUsername(), player.getState());
		conn.transmitToTablePlayers (tableNum, commPlayerState);
		conn.sendDataMessageToAdmin (commPlayerState);

		// Check if this game is to start
		int numOfPlayers = table.getNumOfPlayers();
		int count = players.getPlayerStateCount (PlayerState.READY_TO_START);

		if (count == numOfPlayers) {

			// Update all the users to have the status of started and
			// inform everyone at the table
			Vector playersVector = players.getPlayersSortedBySeat (PlayerState.READY_TO_START);
			for (int i = 0; i < playersVector.size(); i++) {
				Player curPlayer = (Player)playersVector.get(i);
				curPlayer.start();

				// Transmit "start" state to table players
				commPlayerState = new CommPlayerState (
				    tableNum, curPlayer.getPlayerName(), curPlayer.getState());
				conn.transmitToTablePlayer(curPlayer.getPlayerName(), tableNum, commPlayerState);

				conn.sendDataMessageToAdmin (commPlayerState);
			}

			// Reset the current player
			players.resetCurrentPlayer();

			// Inform the server controller if it exists
			ServerController controller = conn.getServerController();
			if (controller != null)
				controller.startGame (tableNum);

			// Send individual game start messages to all players
			CommStartGame commStartGame = new CommStartGame (players.getCurrentPlayerName(), tableNum);
			conn.transmitToTablePlayers(tableNum, commStartGame);

			// Let the game specific server send initial messages to the clients
			// now that they've been told the game has started.
			if (controller != null)
				controller.sendInitialClientMessages (conn, tableNum);
	    }
	}

	/**
	 * User sits down at a table.
	 *
	 * @param conn         Connection to client.
	 * @param commSitDown  Message to sit down.
	 * @param tableNum     Table number.
	 * @param table        The table we're sitting at
	 */
	private void sitDown (ServerConnectionThread conn, CommSitDown commSitDown, int tableNum, Table table) {
		// Get the Player
		PlayerList players = table.getPlayerList ();
		Player player = players.getPlayer (conn.getUsername());
		if (player == null) {
			return;
		}

		int seatNum = commSitDown.getSeatNum();

		// Ensure that the seat is free
		if (players.isSeatFree (seatNum)) {
			player.setSeatNum (seatNum);
			player.sit ();

			// Transmit to all users that this user has sat down
			CommPlayerState commPlayerState = new CommPlayerState (
			     tableNum, conn.getUsername(), seatNum);
			conn.transmitToTablePlayers (tableNum, commPlayerState);
			conn.sendDataMessageToAdmin (commPlayerState);
		}
	}
}
