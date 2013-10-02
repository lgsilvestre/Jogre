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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;
import org.jogre.common.playerstate.PlayerState;
import org.jogre.common.playerstate.PlayerStateViewing;

/**
 * This class contains a list of Player object.s
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PlayerList extends Observable implements ITransmittable {

	/** Number of header tokens. */
	protected static final int NUM_OF_HEADER_TOKENS = 3;

	/** Number of tokens which a PlayerList flattens to. */
	protected static final int NUM_OF_TOKENS = 3;

	// Declare constants
	public static final String NO_PLAYER = "NO_PLAYER";
	private static final String XML_ATT_OWNER = "owner";
	private static final String XML_ATT_CUR_PLAYER = "curPlayer";

	// List of players and owner (first player added to a table)
	private HashMap players;
	private String owner;
	private String currentPlayer;

	/**
	 * Default constructor.
	 */
	public PlayerList (String owner) {
		this.owner = owner;				    // Setowner
		this.currentPlayer = NO_PLAYER; 	// ... no current player
		this.players = new HashMap ();		// Declare players hash
	}

	/**
	 * Constructor which creates a Player object from the flatten () method of
	 * another Player object.
	 *
	 * @param  message    XML element version of object.
	 */
	public PlayerList (XMLElement message) {
		this (message.getStringAttribute(XML_ATT_OWNER));
		this.currentPlayer = message.getStringAttribute(XML_ATT_CUR_PLAYER);

		// Parse the players
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childElement = (XMLElement)e.nextElement();
			Player player = new Player (childElement);
			this.players.put (player.getPlayerName(), player);
		}
	}

	/**
	 * Add a player to the table.
	 *
	 * @param playerName  Name of the player to add to table.
	 * @return true if the playerName was added succesfully.
	 */
	public boolean addPlayer (User theUser) {
		String playerName = theUser.getUsername();

		// If this player is already at the table, then don't add
		if (this.players.containsKey (playerName)) {
			return false;
		}

		// Add the player
		Player player = new Player (theUser, new PlayerStateViewing ());
		this.players.put (playerName, player);

	    // notify any listeners on data
		setChanged ();
		notifyObservers ("+P " + playerName);

		return true;
	}

	/**
	 * Remove a player.
	 *
	 * @param playerName   Name of player to remove.
	 * @return true if the playerName was removed succesfully.
	 */
	public boolean removePlayer (String playerName) {
	    // remove player
	    if (this.players.remove (playerName) == null) {
		    return false;
	    }

	    // Notify listeners
	    setChanged ();
		notifyObservers ("-P " + playerName);
		return true;
	}

	/**
	 * Return the number of users playing at a particular table.
	 *
	 * @return  Number of players.
	 */
	public int size () {
	    return this.players.size ();
	}

	/**
	 * Return true if the list contains the player.
	 *
	 * @param  playerName  Username of player
	 * @return             True if player is on this list.
	 */
	public boolean containsUser (String playerName) {
		return this.players.containsKey (playerName);
	}

	/**
	 * Update the list of players to include the pointers to their users.
	 * (This is needed because the messages from the server to the clients
	 *  only uses names for players at a table, and therefore when a client
	 *  joins a table, he only gets the names of the players at that table
	 *  and so we need to go though the list and match them up with the
	 *  users that they correspond to.)
	 *
	 * @param userList   The list of users
	 */
	public void updateUsers (UserList userList) {
		Vector allPlayers = getPlayers ();

		// Loop through all the players and set the user field to the correct user.
		for (int i = 0; i < players.size(); i++) {
			Player player = (Player) allPlayers.get(i);
			if (player.getUser() == null) {
				player.setUser (userList.getUser (player.getPlayerName ()));
			}
		}
	}

	/**
	 * Return the owner of this table.
	 *
	 * @return   Username of owner of table.
	 */
	public String getOwner () {
		return this.owner;
	}

	/**
	 * Return a player.
	 *
	 * @param playerName  Username of player.
	 * @return            Player object.
	 */
	public Player getPlayer (String playerName) {
		return (Player) this.players.get(playerName);
	}

	/**
	 * Return a player from a seat number.  Returns null if a player isn't
	 * found.
	 *
	 * @param seatNum   Seat number of a user.
	 * @return          Player object.
	 */
	public Player getPlayer (int seatNum) {
		Vector allPlayers = getPlayers ();

		// Loop through all the players until you get the correct player
		for (int i = 0; i < players.size(); i++) {
			Player player = (Player) allPlayers.get(i);
			if (player.getSeatNum() == seatNum) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Return a vector of players which arn't sorted.
	 *
	 * @return     Unsorted list of players.
	 */
	public Vector getPlayers () {
	    // return data
		return new Vector (this.players.values());
	}

	/**
	 * Sets the next player
	 *
	 * @param playerName Next player
	 */
	public void nextPlayer(String playerName) {
		this.currentPlayer = playerName;
        refreshObservers();
	}

	/**
	 * Sets the next player
	 *
	 * @param player Next player
	 */
	public void nextPlayer(Player player) {
		nextPlayer(player.getPlayerName());
	}

	/**
	 * Sets the next player
	 *
	 * @param seatNum  The seat number of the next player
	 */
	public void nextPlayer(int seatNum) {
		nextPlayer(getPlayer(seatNum).getPlayerName());
	}

	/**
	 * Makes the current player equal to the next player.
	 */
	public void nextPlayer () {
		// Find out what seat number the current player is
		Player curPlayer = getPlayer (this.currentPlayer);

		// Retrieve all the players who are actually in a game
		Vector allPlayers = getPlayersSortedBySeat (PlayerState.GAME_STARTED);

		// If this user is the only user in the list then return this user
		int size = allPlayers.size();
		if (allPlayers.size() != 1) {

		    // Find location in vector of user
			int index = allPlayers.indexOf (curPlayer);

			// If last player then go to start again otherwise increment
			if (index == size - 1)
				index = 0;
			else
				index++;

			// index should point to the next player
			Player nextPlayer = (Player)allPlayers.get(index);
		    this.currentPlayer = nextPlayer.getPlayerName();		// update this object
		}
	}

	/**
	 * Set current player.
	 *
	 * @param username   Username of current player.
	 */
	public void setCurrentPlayer (String username) {

	    // Set current player
	    this.currentPlayer = username;		// update this object

	    // Notify listeners
	    setChanged ();
		notifyObservers ("CP " + username);
	}

	/**
	 * Sets the current player to the person with the smallest seat number.
	 */
	public void resetCurrentPlayer () {
		Vector players = getPlayersSortedBySeat (PlayerState.GAME_STARTED);

		if (players.size() != 0) {
		    // retrieve first player and set current player to that
			Player firstPlayer = (Player)players.get (0);
			setCurrentPlayer (firstPlayer.getPlayerName());
		}
	}

	/**
	 * Return the currrent layer at a turned based table.
	 *
	 * @return  Current player in String form.
	 */
	public String getCurrentPlayerName () {
		return this.currentPlayer;
	}

	/**
	 * Return the currrent layer at a turned based table.
	 *
	 * @return  Current player as a Player object.
	 */
	public Player getCurrentPlayer () {
		return (Player)this.players.get (this.currentPlayer);
	}

	/**
	 * Determine if the given player is the current player or not.
	 *
	 * @param player   The player to check
	 * @return         True if the given player is the current player.
	 */
	public boolean isCurrentPlayer (Player player) {
		return player.getPlayerName().equals(this.currentPlayer);
	}

	/**
	 * Return a vector of all the players in this list which are currently
	 * actually playing a game.
	 *
	 * @param state   State of the player e.g. game in state.
	 * @return        List of players sorted by seat.
	 */
	public Vector getPlayersSortedBySeat (String state) {

		// 1) Sort - Retrieve the players sorted by seat
		Vector sortedPlayers = getPlayersSortedBySeat ();

		// 2) Filter - Only add the players who are in a particular state
		Vector statePlayers = new Vector ();
		for (int i = 0; i < sortedPlayers.size(); i++) {
			Player player = (Player)sortedPlayers.get(i);
			if (player.getState().stringValue().equals(state))
				statePlayers.add (player);
		}

		// return data
		return statePlayers;
	}

	/**
	 * Convience method to return in-game players sorted by seat and
	 * in the form of Strings (just the players usernames)
	 *
	 * @return
	 */
	public String [] getInGamePlayers () {
		// Return vector of players sorted by seat
		Vector v = getPlayersSortedBySeat (PlayerState.GAME_STARTED);
		String [] inGamePlayers = new String [v.size()];
		for (int i = 0; i < inGamePlayers.length; i++)
			inGamePlayers [i] = ((Player)v.get(i)).getPlayerName();

		// Return as a String array.
		return inGamePlayers;
	}

	/**
	 * Return a vector of all the players in this list which are sorted by
	 * seat number (used for the game logic).
	 *
	 * @return  List of players sorted by seat.
	 */
	public Vector getPlayersSortedBySeat () {

	    // sort by seat number
	    Vector v = new Vector (this.players.values());
		Collections.sort (v, new Comparator () {
			public int compare(Object o1, Object o2) {
				Player player1 = (Player)o1;
				Player player2 = (Player)o2;
				int returnVal =
					(player1.getSeatNum() < player2.getSeatNum()) ? -1 : 1;
				return (returnVal);
			}
		});

		// return data
		return v;
	}

	/**
	 * Returns the number of players in a particular state.
	 *
	 * @param state
	 * @return  Count of the specified state.
	 */
	public int getPlayerStateCount (String state) {

	    int count = 0;

		Vector vPlayers = getPlayers ();
		for (int i = 0; i < this.players.size(); i++) {
			Player player = (Player)vPlayers.get(i);
			if (player.getState().stringValue().equals(state))
				count ++;
		}

	    // return count
		return count;
	}

	/**
	 * Return true if a seat at a table is free.
	 *
	 * @param  seatNum   Seat number to check.
	 * @return           If seat is free, return true.
	 */
	public boolean isSeatFree (int seatNum) {
		Vector vPlayers = getPlayers ();

		for (int i = 0; i < this.players.size(); i++) {
			Player player = (Player)vPlayers.get(i);
			if (player.getSeatNum() == seatNum) {
				return false;					// seat isn't free!
			}
		}

		return true;
	}

	/**
	 * Flatten the String so that it can be transmitted.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = new XMLElement (Comm.PLAYER_LIST);
		message.setAttribute(XML_ATT_OWNER, this.owner);
		message.setAttribute(XML_ATT_CUR_PLAYER, this.currentPlayer);

		// Add player children
		Vector vPlayers = getPlayers ();
		for (int i = 0; i < this.players.size(); i++) {
			Player player = (Player)vPlayers.get(i);
			message.addChild (player.flatten());
		}

		return message;		// turn XML element into a String
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		Vector vPlayers = getPlayers ();

		for (int i = 0; i < this.players.size(); i++) {
			Player player = (Player)vPlayers.get(i);
			sb.append (player.toString());
			if (i != this.players.size() - 1)
			    sb.append (", ");
		}

		return sb.toString();
	}

	/**
	 * Notify observers.
	 *
	 * @see java.util.Observable#notifyObservers()
	 */
	public void refreshObservers() {
	    setChanged();
	    super.notifyObservers();
	}
}