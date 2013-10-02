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

import java.util.Observable;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;
import org.jogre.common.playerstate.PlayerState;
import org.jogre.common.playerstate.PlayerStateFactory;
import org.jogre.common.playerstate.PlayerStateGameStarted;
import org.jogre.common.playerstate.PlayerStateReady;
import org.jogre.common.playerstate.PlayerStateSeated;
import org.jogre.common.playerstate.PlayerStateViewing;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.JogreLabels;

/**
 * <p>This class contains information about a <i>player</i>, which is a user
 * who has joined a table and wishes to play a game. Each Table object
 * contains a list of Player objects.</p>
 *
 * <p>This class contains the username of the player, their seat number (which
 * is set when they sit down) and the state of the player (PlayerState) which
 * uses the state design pattern as state can be quite complex.</p>
 *
 * <p>This class also implements the ITransmittable interface which means it
 * can be transferred across a network using the flatten() method.</p>
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class Player extends Observable implements ITransmittable {

	/** Constant to show that a player hasn't been seated. */
	public static final int NOT_SEATED = -1;

	public static final String XML_ATT_NAME = "name";
	public static final String XML_ATT_STATE = "state";
	public static final String XML_ATT_SEAT = "seat";

	/** Username of the user. */
	protected String playerName;

	/**
	 * Seat number of the user e.g. in chess, 0=White, 1=Black. NOT_SEATED (-1)
	 * indicates that the Player hasn't sat down yet.
	 */
	protected int seatNum;

	/**
	 * State of the player.  The playerState can either be seated, viewing,
	 * ready etc.  If a Player is in the viewing state for example this variable
	 * will be an instance of the PlayerStateViewing class (state design pattern).
	 */
	protected PlayerState playerState;   // State of the player

	/**
	 * The user that this player is representing
	 */
	protected User theUser = null;

	/**
	 * Constructor requires both a player and a status as parameters and sets
	 * the seat number to NOT_SEATED.
	 *
	 * @param playerName   Name of player
	 * @param playerState  State of the player
	 */
	public Player (User theUser, PlayerState playerState) {
		this.theUser = theUser;
		this.playerName = theUser.getUsername();
		this.playerState = playerState;
		this.seatNum = NOT_SEATED;
	}

	/**
	 * Constructor which creates a Player object from the flatten () method of
	 * another Player object.
	 *
	 * @param  message    XML element version of object.
	 */
	public Player (XMLElement message) {
		this.theUser = null;
		this.playerName = message.getStringAttribute(XML_ATT_NAME);
		this.playerState = PlayerStateFactory.getState (
		                         message.getStringAttribute(XML_ATT_STATE));
		this.seatNum = message.getIntAttribute(XML_ATT_SEAT);
	}

	/**
	 * Player sits down at a table.
	 */
	public void sit () {
	    // change state
		playerState = playerState.sit ();

		// Notify observers
		setChanged();
		notifyObservers ();
	}

	/**
	 * Player stands up from a table.
	 */
	public void stand () {
	    // change state
		playerState = playerState.stand();
		seatNum = NOT_SEATED;

		// Notify observers
		setChanged();
		notifyObservers ();
	}

	/**
	 * Player decides to start.
	 */
	public void start () {
	    // change state
		playerState = playerState.start();

		// Notify observers
		setChanged();
		notifyObservers ();
	}

	/**
	 * Return true if a player can sit or not.
	 *
	 * @param table      Link to table
	 * @return         True if player can sit down.
	 */
	public boolean canSit (Table table) {
	    // return state
		return playerState.canSit (table);
	}

	/**
	 * Returns true if a player can stand.
	 *
	 * @param table      Link to table
	 * @return           True if player can stand up.
	 */
	public boolean canStand (Table table) {

	    // return state
		return playerState.canStand (table);
	}

	/**
	 * Returns true if a player can start.
	 *
	 * @param table      Link to table.
	 * @param game       Link to the game.
	 * @return           True if player can start.
	 */
	public boolean canStart (Table table, Game game) {
	    // return state
		return playerState.canStart (table, game);
	}

	/**
	 * Return if it a player can offer a draw or resign a game.
	 *
	 * @return  True if player can offer draw / resign.
	 */
	public boolean canOfferDrawResign () {
	    // return state
		return playerState.canOfferDrawResign();
	}

	/**
	 * Return the username of this Player object.
	 *
	 * @return   Username of player.
	 */
	public String getPlayerName () {
		return playerName;
	}

	/**
	 * Return the user that this player represents.
	 *
	 * @return   The user object of this player.
	 */
	public User getUser () {
		return theUser;
	}

	/**
	 * Set the user that this player represenets.
	 *
	 * @param theUser   The user to associate with this player.
	 */
	public void setUser (User theUser) {
		this.theUser = theUser;
	}

	/**
	 * Set the seat number of this player (set when a player sits down).
	 *
	 * @param seatNum  Integer >= 0 and < maximum number of seats.
	 */
	public void setSeatNum (int seatNum) {
	    // Set seat number
		this.seatNum = seatNum;

		// Notify observers
		setChanged();
		notifyObservers ();
	}

	/**
	 * Set the seat number of this player (set when a player sits down).
	 *
	 * @param seatNum  Integer >= 0 and < maximum number of seats.
	 */
	public void setState (PlayerState state) {
	    // Set state
		this.playerState = state;

		// Notify observers
		setChanged();
		notifyObservers ();
	}

	/**
	 * Return the seat number of this player.
	 *
	 * @return   If greater than zero the player is seated, -1 if standing.
	 */
	public int getSeatNum() {
	    // return data
		return seatNum;
	}

	/**
	 * Returns the status of this player as a String.
	 *
	 * @return  Get the status of the player as a String.
	 */
	public String getStatusStr () {
		String str;
		// If the status is VIEWING then the player isn't playing
		if (playerState instanceof PlayerStateViewing)
			str = JogreLabels.getInstance().get("not.playing");
		else
			str = GameLabels.getPlayerLabel (seatNum);

		// Return the string wrapped with brackets
		return "(" + str + ")";
	}

	/**
	 * Transmittable String representation of this object (XML).
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = new XMLElement (Comm.PLAYER);
		message.setAttribute (XML_ATT_NAME, playerName);
		message.setAttribute (XML_ATT_STATE, playerState.stringValue());
		message.setIntAttribute(XML_ATT_SEAT, seatNum);

		return message;
	}

	/**
	 * String representation of this player.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		return playerName + " " + getStatusStr();
	}

	/**
	 * Returns the state of this player as a PlayerState object.
	 *
	 * @return     Return the state of the player.
	 */
	public PlayerState getState () {
	    // return state
		return playerState;
	}

	/**
	 * Return true if this player is viewing.
	 *
	 * @return  True if player state is PlayerStateViewing.
	 */
	public boolean isViewing () {
	    return (getState () instanceof PlayerStateViewing);
	}

	/**
	 * Return true if this player is seated.
	 *
	 * @return  True if player state is PlayerStateSeated.
	 */
	public boolean isSeated () {
	    return (getState () instanceof PlayerStateSeated);
	}

	/**
	 * Return true if this player is ready.
	 *
	 * @return  True if player state is PlayerStateReady.
	 */
	public boolean isReady () {
	    return (getState () instanceof PlayerStateReady);
	}

	/**
	 * Return true if this player is playing.
	 *
	 * @return  True if player state is PlayerStateGameStarted.
	 */
	public boolean isPlaying () {
	    return (getState () instanceof PlayerStateGameStarted);
	}
}