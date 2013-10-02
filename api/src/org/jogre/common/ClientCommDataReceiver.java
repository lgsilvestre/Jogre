/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommDisconnect;
import org.jogre.common.comm.CommExitTable;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommJoinTable;
import org.jogre.common.comm.CommNextPlayer;
import org.jogre.common.comm.CommPlayerState;
import org.jogre.common.comm.CommStartGame;
import org.jogre.common.comm.CommTableProperty;
import org.jogre.common.playerstate.PlayerState;
import org.jogre.common.playerstate.PlayerStateSeated;
import org.jogre.common.playerstate.PlayerStateViewing;

/**
 * This class is used to receive Comm message from the server which are used to
 * ensure the data tree on the client is that same as that on the server.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ClientCommDataReceiver {

	private Game game = null;			// Game object.

	/**
	 * Constructor which takes a game object.
	 *
	 * @param game      Game object.
	 * @param username  Client username.
	 */
	public ClientCommDataReceiver (Game game) {
		this.game = game;
	}

	/**
	 * Receive game message.
	 *
	 * @param message    Message.
	 * @param gameID     Game ID .
	 * @param username   Username of user.
	 */
	public void receiveGameMessage (XMLElement message, String username) {

		try {
			String messageType = message.getName();

			// 1) Implementation of Comm commands
			if (messageType.equals(Comm.TABLE)) {
				Table table = new Table (message);
				table.getPlayerList().updateUsers(game.getUserList());
				game.getTableList().updateTable (table.getTableNum(), table);
			}
			else if (messageType.equals(Comm.USER)) {
				User user = new User (message);
				game.getUserList().addUser (user);
			}
			else if (messageType.equals (Comm.DISCONNECT)) {
				CommDisconnect commDisconnect = new CommDisconnect (message);
				String user = commDisconnect.getUsername();
				game.getUserList().removeUser (user);
				game.getTableList().removeUserFromTables (user);
			}
		}
		catch (TransmissionException transEx) {
			transEx.printStackTrace();
		}
	}

	/**
	 * Receive table message which will update the data.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement, int)
	 */
	public void receiveTableMessage (XMLElement message, String username, int tableNum) {

		String messageType = message.getName();
		Table table = game.getTableList().getTable(tableNum);

		if (messageType.equals (Comm.JOIN_TABLE)) {
			receiveJoinTable (new CommJoinTable (message), username, table);
		}
		else if (messageType.equals (Comm.EXIT_TABLE)) {
			CommExitTable commExitTable = new CommExitTable (message);
			game.getTableList().removePlayer (commExitTable.getTableNum(), commExitTable.getUsername());
		}
		else if (messageType.equals (Comm.GAME_OVER)) {
			gameOver (new CommGameOver (message), username, table);
		}
		else if (messageType.equals (Comm.PLAYER_STATE)) {
			playerStateUpdate (new CommPlayerState (message), table);
		}
		else if (messageType.equals (Comm.START_GAME)) {
			CommStartGame commStartGame = new CommStartGame (message);
			table.getPlayerList().setCurrentPlayer (commStartGame.getCurrentPlayer());
		}
		else if (messageType.equals (Comm.NEXT_PLAYER)) {
			CommNextPlayer commNextPlayer = new CommNextPlayer (message);
			table.getPlayerList().setCurrentPlayer (commNextPlayer.getUsername());
		}
		else if (messageType.equals(Comm.TABLE_PROPERTY)) {
			CommTableProperty commTableProperty = new CommTableProperty (message);
			table.addProperty (commTableProperty.getKey(), commTableProperty.getValue());
		}

	}

	/**
	 * Receive a join table message.
	 *
	 * @param commJoinTable  Join table object.
	 * @param username       Username.
	 * @param table          Table object.
	 */
	private void receiveJoinTable (CommJoinTable commJoinTable, String username, Table table) {

		PlayerList playerList = commJoinTable.getPlayerList();

		if (playerList == null) {
			// No player list, so just add the single player to the table.
			String joiningPlayerName = commJoinTable.getUsername();
			User joiningUser = game.getUserList().getUser (joiningPlayerName);
			table.addPlayer (joiningUser);
		} else {
			// This message has a player list, so use that to set the playerlist
			// at the table.

			// First, Augment the player objects in the playerList with their users.
			playerList.updateUsers(game.getUserList());

			// Now, set the playerlist at the table
			table.setPlayerList (playerList);
		}

        // Update anything depending on tablelist
        game.getTableList().refreshObservers ();
    }

	/**
     * Recieve a game over message and update the user objects.
     *
     * @param commGameOver  CommGameOver object.
     * @param username      Username object.
     * @param table         Table number.
     */
    private void gameOver (CommGameOver commGameOver, String username, Table table) {
		// Depending on the status create the game over message
		GameOver gameOver = commGameOver.getGameOver();
		String [] players = gameOver.getPlayers();
		int [] results    = gameOver.getResults();
	    int [] newRatings = gameOver.getNewRatings();

	    // Update ratings of each player
	    for (int i = 0; i < players.length; i++) {
	    	User user = game.getUserList().getUser(players[i]);
			user.update (results [i], newRatings[i]);
	    }
	    // Up$date table object.
	    table.gameOver();

	    game.getUserList().refreshObservers();
    }

    /**
	 * Player state change i.e. a player is standing up / sitting down etc.
	 *
	 * @param commPlayerState
	 */
	private void playerStateUpdate (CommPlayerState commPlayerState, Table table) {
	    // Retreive state change
	    PlayerState commState = commPlayerState.getState();

	    // Retreive player
	    PlayerList commPlayers = table.getPlayerList ();
	    Player commPlayer = commPlayers.getPlayer (commPlayerState.getUsername());

	    // update state on client player
	    if (commState == null || commPlayer == null)
	    	System.out.println ();
	    commPlayer.setState (commState);

	    // Check to see if the seat number needs updated.
	    if (commState instanceof PlayerStateSeated)
	    	commPlayer.setSeatNum (commPlayerState.getSeatNum());
	    else if (commState instanceof PlayerStateViewing)
	    	commPlayer.setSeatNum (Player.NOT_SEATED);

	    commPlayers.refreshObservers();
	}
}