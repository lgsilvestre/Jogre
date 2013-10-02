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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittableWithProps;
import org.jogre.common.playerstate.PlayerState;
import org.jogre.common.playerstate.PlayerStateSeated;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.JogreLogger;
import org.jogre.common.util.JogrePropertyHash;
import org.jogre.common.util.JogreUtils;

/**
 * <p>Server side data object which holds information on various tables
 * and their current users etc.</p>
 *
 * <p>This class implements the ITransmittable and be transmitabled to
 * a client. It also extends the Observable interface so that other class
 * can listen to changes on the data.</p>
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class Table extends Observable implements ITransmittableWithProps {

	private JogreLogger logger = new JogreLogger(this.getClass());

	private static final String XML_ATT_TABLE_NUM       = "tableNum";
	private static final String XML_ATT_VIS             = "vis";
	private static final String XML_ATT_TIME_STARTED    = "timeStarted";

	// Properties
	public static final String NUM_OF_PLAYERS = "numOfPlayers";

	// Declare fields
	private int tableNumber;
	private boolean isPublic;              // tables can be public / private
	private Date startTime;                // time started
	private PlayerList playerList;         // list of players
	private JogrePropertyHash properties;  // Hashmap of key/value properties
	                                       // e.g. required number of people
	                                       //      for this game
	private Vector inviteList;             // list of invites active on this table
	private boolean drawOfferActive;       // if a draw offer is active
	private int drawOfferSerialNum;        // serial number of the current draw offer
	private boolean [] drawOfferResponseArray;  // Array of draw offer responses

	// A game can contain a model and this state is stored on the table.
	private JogreModel model;              // only used if JogreServer stores the state of a game

	/**
	 * Create a table and 1 user (owner)
	 *
	 * @param tableNumber  The table number of the new table.
	 * @param theUser      The user creating the table (the owner).
	 * @param isPublic     boolean to show if a table is public or private.
	 */
	public Table (int tableNumber, User theUser, boolean isPublic, JogrePropertyHash properties) {
		this.tableNumber = tableNumber;
		this.isPublic = isPublic;
		this.startTime = new Date ();
		this.properties = properties;
		this.inviteList = new Vector ();
		this.drawOfferActive = false;
		this.drawOfferSerialNum = 0;

		// Set game data to null
		model = null;

		playerList = new PlayerList (theUser.getUsername());
		playerList.addPlayer (theUser);
	}

	/**
	 * Create Table object from a String.
	 *
	 * @param message                 XMLElement communication object.
	 * @throws TransmissionException  Thrown if there is a problem in transmission.
	 * @throws NumberFormatException  Problem parsing an int.
	 */
	 /* RAW: Maybe make this take a userList as a parameter and to the
	         playerList.UpdateUsers() automatically?   Maybe do this
	         for setPlayerList as well? */
	public Table (XMLElement message) throws TransmissionException {
		this.tableNumber = message.getIntAttribute(XML_ATT_TABLE_NUM);
		this.isPublic = (message.getAttribute(XML_ATT_VIS).equals ("pub"));

		String dateStr = message.getStringAttribute (XML_ATT_TIME_STARTED);
		this.startTime = JogreUtils.readDate (dateStr, IJogre.DATE_FORMAT_FULL);
		this.properties = new JogrePropertyHash (
		    message.getStringAttribute(XML_ATT_PROPERTIES));

		// Parse the player list and the optional model
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement)e.nextElement();

			if (childMessage.getName().equals(Comm.PLAYER_LIST)) {
				playerList = new PlayerList (childMessage);
			}
			else if (childMessage.getName().equals (Comm.MODEL)) {
				model.setState (childMessage);
			}
		}
	}

	/**
	 * Add a property to the hash map.
	 *
	 * @param key        Key of the value to add.
	 * @param value      Value of the property
	 */
	public void addProperty (String key, String value) {

	    // Update properties
	    properties.put (key, value);

	    // notify any listeners on data
		setChanged ();
		notifyObservers ("+p" + key + " " + value);
	}

	/**
	 * Return a property from a key.  If key does not exist, then
	 * return default value;
	 *
	 * @param key
	 * @return
	 */
	public String getProperty (String key, String defaultValue) {
		String property = getProperty(key);
		if (property == null)
			return defaultValue;
		return property;
	}

	/**
	 * Return a property from a key.
	 *
	 * @param key
	 * @return
	 */
	public String getProperty (String key) {

	    // Update properties
	    return (String)properties.get (key);
	}

	/**
	 * Add a user to the table i.e. a user joins a table
	 *
	 * @param theUser   The user to add to the table.
	 */
	public boolean addPlayer (User theUser) {
		return playerList.addPlayer (theUser);
	}

	/**
	 * Game over.  Update all players that are already seated
	 * to be ready for press start again.
	 */
	public void gameOver () {
		// Update all the users to be seated and ready to press start
	    Vector players = getPlayerList().getPlayers();
		for (int i = 0; i < players.size(); i++) {
			Player player = (Player)players.get(i);
			if (player.getSeatNum() != Player.NOT_SEATED)
				player.setState(new PlayerStateSeated());
		}
		getPlayerList().refreshObservers();
	}

	/**
	 * Get the number of this table.
	 *
	 * @return  table number as an integer.
	 */
	public int getTableNum () {
		return tableNumber;
	}

	/**
	 * Return the time this table was created.
	 *
	 * @return   Time table was created.
	 */
	public Date getStartTime () {
		return this.startTime;
	}

	/**
	 * Return the number of players as specified by a client
	 * through a property.
	 *
	 * @return  Minimum number of players
	 */
	public int getNumOfPlayers () {
	    String numPlayersProp = (String)properties.get (Comm.PROP_PLAYERS);
	    if (numPlayersProp != null)
	        return Integer.parseInt (numPlayersProp);

	    return IJogre.DEFAULT_NUM_OF_PLAYERS;		// 2
	}

	/**
	 * Return a player list.
	 *
	 * @return    PlayerList at this table.
	 */
	public PlayerList getPlayerList () {
	    return playerList;
	}

	public void setPlayerList (PlayerList playerList) {
	    this.playerList = playerList;
	}

	/**
	 * Returns true if a specified user is at a table.
	 *
	 * @param user  Username to check.
	 * @return      True if specified is at table.
	 */
	public boolean containsPlayer (String user) {
		return (playerList.containsUser(user));
	}

	/**
	 * Returns true if a specified user is at a table and is seated in a seat.
	 * ie: Is <i>not</i> just a spectator.
	 *
	 * @param user  Username to check.
	 * @return      True if user is at the table and not a spectator.
	 */
	public boolean isParticipatingPlayer (String user) {
		Player thePlayer = playerList.getPlayer(user);
		return ((thePlayer != null) &&
		        (thePlayer.getSeatNum() != Player.NOT_SEATED));
	}

	/**
	 * Get the owner of this table.
	 *
	 * @return  Username of owner.
	 */
	public String getOwner () {
		return playerList.getOwner ();
	}

	/**
	 * Returns true if this is a public table.
	 *
	 * @return  True if table is public.
	 */
	public boolean isPublic () {
		return isPublic;
	}

	/**
	 * Retrieve the model from the server (if its being used).
	 *
	 * @return  Model (will be null if doesn't exist).
	 */
	public JogreModel getModel () {
		return model;
	}

	/**
	 * Set the game data.
	 *
	 * @param model
	 */
	public void setModel (JogreModel model) {
		this.model = model;
	}

	/**
	 * Sets the next player.
	 */
	public void nextPlayer (String playerName) {
		playerList.nextPlayer(playerName);		// next player
	}

	/**
	 * Sets the next player.
	 */
	public void nextPlayer (Player player) {
		playerList.nextPlayer(player);		// next player
	}

	/**
	 * Sets the next player.
	 */
	public void nextPlayer (int seatNum) {
		playerList.nextPlayer(seatNum);   // next player
	}

	/**
	 * Sets the next player username.
	 */
	public void nextPlayer () {
		playerList.nextPlayer();		// next player
	}


	/**
	 * Flatten the table object.  Also include the model of the current game
	 * if the modelStateIsAttached variable is set.
	 *
	 * @return                     XML element version of object.
	 */
	public XMLElement flatten () {
		// Format time started into a String
		String timeStartedTrans = JogreUtils.valueOf(startTime, IJogre.DATE_FORMAT_FULL);

		// Create <table> element and populate with data for transmission
		XMLElement message = new XMLElement (Comm.TABLE);
		message.setIntAttribute (XML_ATT_TABLE_NUM, tableNumber);
		String visStr = isPublic ? "pub" : "priv";		// TODO make better at some stage
		message.setAttribute (XML_ATT_VIS, visStr);
		message.setAttribute (XML_ATT_TIME_STARTED, timeStartedTrans);
		message.setAttribute (XML_ATT_PROPERTIES, properties);

		// Add player list as a child element
		XMLElement playerListElement = playerList.flatten();
		message.addChild (playerListElement);

		return message;
	}

	/**
	 * Return the time started as a String.
	 *
	 * @return
	 */
	public String getTimeFormatted () {
	    SimpleDateFormat dateFormatter = new SimpleDateFormat (IJogre.DATE_FORMAT_TIME);
		return dateFormatter.format (startTime);
	}

	/**
	 * Return the String version of this table.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		String isPublicStr = isPublic ? "public" : "private";
		StringBuffer sb = new StringBuffer (
			tableNumber + ": (" +
			JogreUtils.valueOf(startTime, IJogre.DATE_FORMAT_TIME) +
			" - " + isPublicStr +  " table) - "
		);

		// Loop through the players and create the correct
		Vector players = playerList.getPlayersSortedBySeat();
		for (int i = 0; i < players.size(); i++) {

		    // Retreive player
		    Player player = (Player)players.get(i);

		    // Append the players name
		    sb.append (player.getPlayerName());

		    // Check if owner
		    if (player.getPlayerName().equals(playerList.getOwner())) {
				if (i != players.size()) {
					sb.append (
					    " (" +
					    JogreLabels.getInstance().get ("owner") +
					    ")"
					);
				}
		    }

		    if (i < players.size() - 1)
		        sb.append (", ");		// seperate with comma.
		}

		return sb.toString();
	}

	/**
	 * Return true if a game is in progress.  This method loops through the
	 * various Player object in the PlayerList to see if any of them are in the
	 * PlayerStateGameStarted state.
	 *
	 * @return  True if game is playing.
	 */
	public boolean isGamePlaying() {
		Vector players = getPlayerList().getPlayers();
		for (int i = 0; i < players.size(); i++) {
			Player player = (Player)players.get(i);
			// is the state of tihs player equal to "game started"?
			if (player.getState().stringValue().equals (PlayerState.GAME_STARTED)) {
				return true;
			}
		}

		return false;
	}

    /**
     * Return the properties hash.
     *
     * @see org.jogre.common.comm.ITransmittableWithProps#getProperties()
     */
    public JogrePropertyHash getProperties() {
        return properties;
    }

	/**
	 * Add a player to the invite list of this table
	 *
	 * @param playerName  The player name to be added to the invite list
	 */
	public void addInvite (String playerName) {
		// Make sure that this player isn't already invited to the table
		if (!inviteList.contains (playerName)) {
			inviteList.add(playerName);
		}
	}

	/**
	 * Determine if the given player is invited to this table
	 *
	 * @param playerName   The player to check for invitation status
	 *
	 * @return true if the player is invited to the table.
	 */
	public boolean isInvited (String playerName, boolean remove) {
		return inviteList.contains (playerName);
	}

	/**
	 * Remove an invite from the table
	 *
	 * @param playerName   The player to remove from the invite list
	 *
	 * @return true if the player had been invited to the table.
	 */
	public boolean removeInvite (String playerName) {
		return inviteList.remove (playerName);
	}

	/**
	 * Check to see if there is an outstanding draw offer or not.
	 */
	public boolean hasOutstandingDrawOffer () {
		return drawOfferActive;
	}

	/**
	 * Start a new draw offer with the given player as the initiator.
	 *
	 * Note: This assumes that thePlayer is an active player of the game (he
	 *       is <i>not<i/> a spectator).  This check is done in
	 *       ServerTableController, but if someone changes the code, make sure
	 *       that this is only called for known active players.
	 *
	 * @param thePlayer   The player asking for the draw offer.
	 * @return the serial number of the draw offer.
	 */
	public int startDrawOffer (Player thePlayer) {
		// Can't start a new offer if there is an old one still active
		if (drawOfferActive) {
			return -1;
		}

		// Advance to the next serial number
		drawOfferSerialNum += 1;

		// Initialize the response array to all false;
		drawOfferResponseArray = new boolean [getNumOfPlayers ()];

		// The initiating player implicitly accepts his own offer.
		drawOfferResponseArray[thePlayer.getSeatNum()] = true;

		return drawOfferSerialNum;
	}

	/**
	 * Accept a draw offer for a player.
	 *
	 * Note: This assumes that thePlayer is an active player of the game (he
	 *       is <i>not<i/> a spectator).  This check is done in
	 *       ServerTableController, but if someone changes the code, make sure
	 *       that this is only called for known active players.
	 *
	 * @param thePlayerName   The player accepting the draw offer.
	 * @param serialNum       The serial number of the offer being accepted.
	 */
	public void acceptDrawOffer (Player thePlayer, int serialNum) {
		// Make sure that this offer is not stale before accepting it.
		if (serialNum == drawOfferSerialNum) {
			drawOfferResponseArray[thePlayer.getSeatNum()] = true;
		}
	}

	/**
	 * Decline a draw offer for a player.
	 *
	 * Note: This assumes that thePlayer is an active player of the game (he
	 *       is <i>not<i/> a spectator).  This check is done in
	 *       ServerTableController, but if someone changes the code, make sure
	 *       that this is only called for known active players.
	 *
	 * @param thePlayerName   The player accepting the draw offer.
	 * @param serialNum       The serial number of the offer being accepted.
	 * @return true if the draw offer declination is accepted.
	 */
	public boolean declineDrawOffer (Player thePlayer, int serialNum) {
		// Make sure that this offer is not stale before accepting it.
		return (serialNum == drawOfferSerialNum);
	}

	/**
	 * Determine if the current draw offer is complete (ie: all players have
	 *  accepted the draw).
	 *
	 * @return true if the current offer is complete.
	 */
	public boolean drawOfferComplete () {
		// If there is at least one player who hasn't responded yet, then the
		//   offer is not complete.
		for (int i = 0; i < drawOfferResponseArray.length; i++) {
			if (drawOfferResponseArray[i] == false) {
				return false;
			}
		}

		// Everyone has responded, so this offer is complete.
		return true;
	}

	/**
	 * Clear the active draw offer.
	 */
	public void clearDrawOffer () {
		drawOfferActive = false;
	}

	/**
	 * Refresh observers - calls the setChanged() and notifyObservers ()
	 * methods in the Observable class.
	 */
	public void refreshObservers () {
		setChanged();
		notifyObservers();	// notify any class which observe this class
	}
}