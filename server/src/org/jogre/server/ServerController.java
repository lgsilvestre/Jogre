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
package org.jogre.server;

import java.util.Date;

import nanoxml.XMLElement;

import org.jogre.common.Game;
import org.jogre.common.GameOver;
import org.jogre.common.IError;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.UserList;
import org.jogre.common.comm.CommControllerProperty;
import org.jogre.common.comm.CommError;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.data.GameInfo;
import org.jogre.server.data.ServerDataException;

/**
 * New class for creating server side controller which is adapter in style i.e.
 * override methods where necessary. 
 *
 * It also contains the server equivalent of the client properties methods
 * for very quickly reading and dealing with data.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public abstract class ServerController {

	/**
	 * Game key that we are working with.
	 */
	protected String gameId = null;

    /**
     * Convience link to the server.
     */
    protected JogreServer server;

    /**
     * Convience link to the table list.
     */
    protected TableList tableList = null;

    /**
     * Convience link to the userlist.
     */
    protected UserList userList = null;

    /**
     * Method which is called when a create game is created.
     *
     * @param tableNum  Table number.
     */
    public abstract void startGame (int tableNum);

    /**
     * Method which is called once after the game has started (after startGame()
     * above has been called) and after all of the clients have been told that
     * the game is starting.
     *
     * This method should be overridden by a game specific server controller
     * that wants to send out messages to clients that needs to happen only
     * once per game.  (For example, dealing out a hand of cards.)
     *
     * @param conn       The connection thread for the last player which
     *                     clicked the "Start" button.
     * @param tableNum   The table number.
     */
    public void sendInitialClientMessages (ServerConnectionThread conn, int tableNum) {
    }

    /**
     * Method which is called when a game is over.
     *
     * @conn             Connection to client.
     * @param tableNum   Table number.
     * @param resultType Result type (e.g. win, lose etc)
     */
    public abstract void gameOver (ServerConnectionThread conn, int tableNum, int resultType);

	/**
	 * Resign method - the default behavior is that this users loses (and
	 * so the other users win).  Over write this method if this isn't the
	 * required functionality or you wish to record a score/history etc.
	 *
	 * @param conn      Connection to server
	 * @param tableNum  Table number.
	 */
	public void userResigns (ServerConnectionThread conn, int tableNum) {
		gameOver (conn, tableNum, conn.getUsername(), IGameOver.LOSE);
	}

	/**
	 * User agrees on a draw. The default behavior is that this user draws
	 * (and so the other users draw also).  Over write this method if this
	 * isn't the required functionality or you wish to record a score/history
	 * etc.
	 *
	 * @param conn      Connection to server
	 * @param tableNum
	 */
	public void usersAgreeDraw (ServerConnectionThread conn, int tableNum) {
		gameOver (conn, tableNum, conn.getUsername(), IGameOver.DRAW);
	}

    /**
     * Constructor for a server controller which sets convience fields for each game
     * that a client can utilise.
     *
     * @param gameKey  Game key.
     */
    public ServerController (String gameKey) {
    	this.gameId = gameKey;
        this.server = JogreServer.getInstance();

        // If game key is OK - these game keys should be OK
        Game game = server.getGameList().getGame (gameKey);
        if (game != null) {
            this.userList = game.getUserList();
            this.tableList = game.getTableList();
        }
    }

	/**
	 * A class can override this method to provide custom properties of the game
	 * that will be sent to all clients when they send the game_connect message.
	 * Clients can use getCustomGameProperties() on the game to retrieve this XML tree.
	 *
	 * @return an XML tree of custom properties.
	 */
	public XMLElement getCustomGameProperties() {return null;}

	/**
	 * The class must implement this method which reads a message
	 * from a client.
	 *
	 * @param conn     Connection from the server to a client.
	 * @param message  Message from the client.
	 */
	public void parseGameMessage (ServerConnectionThread conn, XMLElement message) {}

	/**
	 * The class must implement this method which reads a message
	 * from a client.
	 *
	 * @param conn      Connection to a client.
	 * @param message   Message.
	 * @param tableNum  Table number of message.
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {}
	
	/**
	 * Adaper method for keeping the server model in synch with the other clients
	 * when a key / value property is sent from one client to another.
	 *
	 * @param model  Server model which needs updated.
	 * @param key    String key.
	 * @param value
	 */
	public void receiveProperty (JogreModel model, String key, String value) {}

	/**
	 * Adaper method for keeping the server model in synch with the other clients
	 * when a key / int value property is sent from one client to another.
	 *
	 * @param model  Server model which needs updated.
	 * @param key    String key.
	 * @param value  Value.
	 */
	public void receiveProperty (JogreModel model, String key, int value) {}

	/**
	 * Adaper method for keeping the server model in synch with the other clients
	 * when a key / int x, int y value property is sent from one client to another.
	 *
	 * @param model  Server model which needs updated.
	 * @param key    String key.
	 * @param x      Integer x value.
	 * @param y 	     Integer y value.
	 */
	public void receiveProperty (JogreModel model, String key, int x, int y) {}
	
	/**
	 * Adaper method for keeping the server model in synch with the other clients
	 * when an object property is sent from one client to another.
	 *
	 * @param model    Server model which needs updated.
	 * @param message  More complex object value.
	 * @param table    Table number if the user needs a handle of that.
	 */
	public void receiveObject (JogreModel model, XMLElement message, int tableNum) {
		receiveObject(model, message);
	}

	/**
	 * Adaper method for keeping the server model in synch with the other clients
	 * when an object property is sent from one client to another.
	 *
	 * @param model    Server model which needs updated.
	 * @param message  More complex object value.
	 */
	public void receiveObject (JogreModel model, XMLElement message) {}

	/**
	 * Send a normal String valued property.
	 *
	 * @param conn
	 * @param key
	 * @param value
	 */
	public void sendProperty (ServerConnectionThread conn, String key, String value) {
		// Create property communications object and send to server
		CommControllerProperty commContProp = new CommControllerProperty (
			CommControllerProperty.TYPE_STRING, key, value);

		conn.send(commContProp);
	}

	/**
	 * Send a single integer property.
	 *
	 * @param conn
	 * @param key
	 * @param value
	 */
	public void sendProperty (ServerConnectionThread conn, String key, int value) {
		// Create property communications object and send to server
		String valueStr = String.valueOf(value);
		CommControllerProperty commContProp = new CommControllerProperty (
			CommControllerProperty.TYPE_INT, key, valueStr);
		conn.send(commContProp);
	}

	/**
	 * Send a co-ordinate property (x and y integer values).
	 *
	 * @param conn
	 * @param key
	 * @param x
	 * @param y
	 */
	public void sendProperty (ServerConnectionThread conn, String key, int x, int y) {
		// Create property communications object and send to server
		CommControllerProperty commContProp = new CommControllerProperty (
			CommControllerProperty.TYPE_INT_TWO, key, x + " " + y);
		conn.send(commContProp);
	}
	
	/**
	 * Return model associated with this table.
	 *
	 * @param tableNum  Specified table number.
	 * @return          Current model.
	 */
	public JogreModel getModel (int tableNum) {
	    Table table = getTable (tableNum);
	    if (table != null)
	        return table.getModel();

	    return null;
	}

	/**
	 * Sets model associated with this table.
	 *
	 * @param tableNum	Specified table number.
	 * @param model		Current model.
	 */
	public void setModel (int tableNum, JogreModel model) {
		Table table = getTable(tableNum);
		if (table != null)
			table.setModel(model);
	}

	/**
	 * Return a table based on a table number.
	 *
	 * @param tableNum   Specified table number.
	 * @return           Current table.
	 */
	public Table getTable (int tableNum) {
	    if (tableList != null)
	        return tableList.getTable (tableNum);

	    return null;
	}
	
	/**
	 * Return the seat number of a specified player.
	 * 
	 * @param username	Username of the player.
	 * @param tableNum  Table number of current table.
	 * @return          Number of specified players seat.
	 */
	protected int getSeatNum (String username, int tableNum) {
		Table table = this.getTable(tableNum);
		if (table != null) {
			PlayerList playerList = table.getPlayerList();
			if (playerList != null) {
				Player player = playerList.getPlayer(username);
				if (player != null)
					return player.getSeatNum();
			}
		}
		return Player.NOT_SEATED;
	}
	
	/**
	 * Get player name from a seat number - does opposite of getSeatNum.
	 * 
	 * @param seatNum    Seat number of player.
	 * @param tableNum   Table number of player.
	 * @return
	 */
	protected String getPlayerName (int seatNum, int tableNum) {
		Table table = this.getTable(tableNum);
		if (table != null) {
			PlayerList playerList = table.getPlayerList();
			if (playerList != null) {
				Player player = playerList.getPlayer(seatNum);
				if (player != null)
					return player.getPlayerName();
			}
		}
		return null;
	}
	
	/**
	 * Return server connection thread for a username / game id.
	 * 
	 * @param username
	 * @return
	 */
	protected ServerConnectionThread getServerConnectionThread (String username) {
		return server.getConnections().getServerConnectionThread (gameId, username);
	}

    /**
     * Return true/false if the game is started or not.
     *
     * @return  Returns true if a game is underway.
     */
    public boolean isGamePlaying (int tableNum) {
        Table table = this.getTable(tableNum);
        if (table != null) {
            return table.isGamePlaying();
        }
        return false;
    }

	/**
	 * Method for declaring that a game is over which takes
	 * indivual players and their results.
	 *
	 * @param conn        Connection to server.
	 * @param tableNum    Table number.
	 * @param players     List of players.
	 * @param score       Optional score.
	 * @param history     Optional history.
	 * @param resultType  List of results.
	 */
	public void gameOver (ServerConnectionThread conn,
			              int tableNum,
			              String [] players,
			              int [] results,
			              String score,
			              String history)
	{
		try {
			//  Retrieve table object
			Table table = getTable (tableNum);

			// Create game info object.
			GameInfo gameInfo = new GameInfo (
					gameId, 						// Game key (e.g. chess)
					JogreUtils.valueOf (players),	// In game players
					JogreUtils.valueOf (results),	// Results
					table.getStartTime(),			// Start time
					new Date (),					// End time (current time)
					score,							// Optional score
					history							// Optional history
			);

			// Add game info object
			GameOver gameOver = server.getServerData().addGame (gameInfo, true);

			// Declare game over
			table.gameOver();

			// inform everyone at this table that the game is up...
			CommGameOver commGameOver = new CommGameOver (table.getTableNum(), gameOver);
			conn.transmitToTablePlayers (tableNum, commGameOver);
			conn.sendDataMessageToAdmin (commGameOver);
		} catch (ServerDataException sdEx) {
			conn.send (new CommError (IError.SERVER_DATA_ERROR, sdEx.getMessage()));
		}
	}

	/**
	 * Method for declaring that a game is over which takes
	 * indivual players and their results.
	 *
	 * This game over doesn't declare a game score or its history
	 * and is used for simplier games.
	 *
	 * @param conn        Connection to server.
	 * @param tableNum    Table number.
	 * @param players     List of players.
	 * @param resultType  List of results.
	 */
	public void gameOver (ServerConnectionThread conn,
			              int tableNum,
			              String [] players,
			              int [] results)
	{
		gameOver (conn, tableNum, players, results, null, null);
	}

	/**
	 * Method for declaring that a game is over which takes a
	 * single player and the result type.  The other players are
	 * worked out and their results automatically.
	 *
	 * @param conn        Connection to server.
	 * @param tableNum    Table number.
	 * @param username    Username of player who has won/lost
	 * @param resultType  Result type.
	 * @param score       Optional score.
	 * @param history     Optional history.
	 */
	public void gameOver (ServerConnectionThread conn,
			              int tableNum,
			              String username,
			              int resultType,
			              String score,
			              String history)
	{
		// Retreive table object
		Table table = getTable (tableNum);

		// Retreive players and create result player if
		// result type was not a draw
		String [] players = table.getPlayerList().getInGamePlayers();
		int [] results = new int [players.length];

		if (resultType == IGameOver.DRAW) {
			for (int i = 0; i < results.length; i++) {
				results[i] = IGameOver.DRAW;
			}
		} else {
			for (int i = 0; i < results.length; i++) {
				if (resultType == IGameOver.WIN)
					if (username.equals (players[i]))
						results[i] = IGameOver.WIN;
					else results[i] = IGameOver.LOSE;
				else if (resultType == IGameOver.LOSE)
					if (username.equals (players[i]))
						results[i] = IGameOver.LOSE;
					else results[i] = IGameOver.WIN;
			}
		}

		gameOver (conn, tableNum, players, results, score, history);
	}

	/**
	 * Method for declaring that a game is over which takes a
	 * single player and the result type.  The other players are
	 * worked out and their results automatically.
	 *
	 * This game over doesn't declare a game score or its history
	 * and is used for simplier games.
	 *
	 * @param conn        Connection to server.
	 * @param tableNum    Table number.
	 * @param username    Username of player who has won/lost
	 * @param resultType  Result type.
	 */
	public void gameOver (ServerConnectionThread conn,
			              int tableNum,
			              String username,
			              int resultType)
	{
		gameOver (conn, tableNum, username, resultType, null, null);
	}
}