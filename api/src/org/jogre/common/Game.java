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
package org.jogre.common;

import java.util.Enumeration;
import java.util.Observable;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;


/**
 * Game class which extends the data class and can be transmitted.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class Game extends Observable implements ITransmittable {

    private static final String XML_ATT_GAME_KEY = "gameKey";
    private static final String XML_ATT_MIN_PLAYERS = "minPlayers";
    private static final String XML_ATT_MAX_PLAYERS = "maxPlayers";
	private static final String XML_CUSTOM_GAME_PROPS = "customGameProperties";
	private XMLElement customGamePropertiesTree = null;

	/** Game key <game title>-<version> e.g. chess. */
	protected String gameKey;

	/** List of users which are currently connected to the server. */
	protected UserList userList;

	/** List of tables current being played on this server. */
	protected TableList tableList;

	/** Minimum and maximum number of players on table. */
	private int minNumOfPlayers, maxNumOfPlayers;

	/**
	 * Constructor to a Game object.
	 *
	 * @param gameKey Game key
	 */
	public Game (String gameKey, int minNumOfPlayers, int maxNumOfPlayers) {
		this.gameKey = gameKey;

		// Create new list of users and list of tables
		userList = new UserList ();
		tableList = new TableList ();

		// Set min / max number of players
		this.minNumOfPlayers = minNumOfPlayers;
		this.maxNumOfPlayers = maxNumOfPlayers;
	}

	/**
	 * Game constructor which takes an XMLElement as a parameter.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public Game (XMLElement message) throws TransmissionException {
	    // Read game key and create empty userlist / tablelist.
	    this.gameKey = message.getStringAttribute (XML_ATT_GAME_KEY);
	    // Set min / max number of players
		this.minNumOfPlayers = message.getIntAttribute (XML_ATT_MIN_PLAYERS);
		this.maxNumOfPlayers = message.getIntAttribute (XML_ATT_MAX_PLAYERS);

	    // Read child elements - userlist and tablelist
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement)e.nextElement();

			if (childMessage.getName().equals (Comm.USER_LIST)) {
				this.userList = new UserList (childMessage);
			}
			else if (childMessage.getName().equals (Comm.TABLE_LIST)) {
				this.tableList = new TableList (childMessage);
			}
			else if (childMessage.getName().equals (XML_CUSTOM_GAME_PROPS)) {
				this.customGamePropertiesTree =
					(XMLElement) (childMessage.getChildren().firstElement());
			}
		}

		// If no users / tables - create empty user and table lists
		if (userList == null)
		    userList = new UserList ();
		if (tableList == null)
		    tableList = new TableList ();
	}

	/**
	 * Return the key of the game.
	 *
	 * @return
	 */
	public String getKey () {
		return gameKey;
	}

	/**
	 * Return the <i>game</i> part of the game key.
	 *
	 * @return
	 */
	public String getGameStr () {
	    return gameKey;
	}

	/**
	 * Return the minimum number of players in a game.
	 *
	 * @return    Max num of players.
	 */
	public int getMinNumOfPlayers () {
		return minNumOfPlayers;
	}

	/**
	 * Return the minimum number of players in a game.
	 *
	 * @return    Max num of players.
	 */
	public int getMaxNumOfPlayers () {
		return maxNumOfPlayers;
	}

	/**
	 * Returns the UserList object of current connected users.
	 *
	 * @return
	 */
	public UserList getUserList () {
		return userList;
	}

	/**
	 * Returns the custom game properties for this game.
	 *
	 * @return	An XML tree of custom properties
	 */
	public XMLElement getCustomGameProperties() {
		return customGamePropertiesTree;
	}

	/**
	 * Set the userlist.
	 *
	 * @param userlist   New userlist which replaces the older one.
	 */
	public void setUserList (UserList userlist) {
	    this.userList = userlist;
	}

	/**
	 * Set the table list.
	 *
	 * @param tableList  New tablelist which replaces the older one.
	 */
	public void setTableList (TableList tableList) {
	    this.tableList = tableList;
	}

	/**
	 * Return the TableList object or table currently being played.
	 *
	 * @return
	 */
	public TableList getTableList () {
		return tableList;
	}

	/**
	 * Set the custom game properties for this game.
	 * This is for server controllers to use to set custom properties
	 *
	 * @param custProperties	An XML tree of custom properties
	 */
	public void setCustomGameProperties(XMLElement custProperties) {
		customGamePropertiesTree = custProperties;
	}

	/**
	 * Return the game as a String.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		return gameKey;
	}

    /**
     * Flatten this object.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        XMLElement message = new XMLElement (Comm.GAME);

        message.setAttribute    (XML_ATT_GAME_KEY, gameKey);
        message.setIntAttribute (XML_ATT_MIN_PLAYERS, minNumOfPlayers);
        message.setIntAttribute (XML_ATT_MAX_PLAYERS, maxNumOfPlayers);

        if (userList.size() > 0)
            message.addChild (userList.flatten());
        if (tableList.size() > 0)
            message.addChild (tableList.flatten());

		if (customGamePropertiesTree != null) {
			/* Wrap the game's custom properties in a child element of
				type XML_CUSTOM_GAME_PROPS */
			XMLElement custEl = new XMLElement (XML_CUSTOM_GAME_PROPS);
			custEl.addChild (customGamePropertiesTree);
			message.addChild (custEl);
		}

        return message;
    }
}
