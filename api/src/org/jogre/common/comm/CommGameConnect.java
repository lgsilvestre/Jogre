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
package org.jogre.common.comm;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;

/**
 * Sends a connect message to a JOGRE server.  Consists of a gameID, a username and
 * an optional password (if required).
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommGameConnect extends CommGameMessage {

	// XML attribute name
    protected static final String XML_ATT_PASSWORD = "password";
    protected static final String XML_ATT_GAME_ID  = "gameID";

    /** Game ID of game client is playing. */
    protected String gameID = null;

	/** Password if connecting to the JOGRE master server. */
	protected String password = null;

	/**
	 * Used by server to inform other clients that someone has
	 * logged onto a game.
	 *
	 * @param username    Username of person logging on.
	 */
	public CommGameConnect (String username) {
	    super (username);
	}

	/**
	 * Constructor which takes a username, gameID but no password.
	 *
	 * @param username    Username of person logging on.
	 * @param gameID      Game ID i.e. game-version (e.g. chess-0.2).
	 */
	public CommGameConnect (String username, String gameID) {
	    super (username);

	    this.gameID   = gameID;
	}

	/**
	 * Constructor for a communications object for connecting to a client
	 * game which includes a password.
	 *
	 * @param username   Username of user logging on.
	 * @param password   Password of user.
	 * @param gameID     Game ID i.e. game-version (e.g. chess-0.2).
	 */
	public CommGameConnect (String username, String password, String gameID) {
	    this (username, gameID);

	    this.password = password;
	}

	/**
	 * Constructor which creates a CommConnect object from the flatten ()
	 * method of another CommConnect object.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public CommGameConnect (XMLElement message) throws TransmissionException {
		super (message);

		this.gameID   = message.getStringAttribute (XML_ATT_GAME_ID);
		this.password = message.getStringAttribute (XML_ATT_PASSWORD);
	}

	/**
	 * Return the game ID.
	 *
	 * @return   Game id
	 */
	public String getGameID () {
		return this.gameID;
	}

	/**
	 * Return the password.
	 *
	 * @return  Password
	 */
	public String getPassword () {
		return this.password;
	}

	/**
	 * Flatten the connect object into a XML communication object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
	    XMLElement message = super.flatten (Comm.GAME_CONNECT);

	    if (password != null)
	        message.setAttribute (XML_ATT_PASSWORD, password);
	    if (gameID != null)
	        message.setAttribute (XML_ATT_GAME_ID,  gameID);

	    return message;
	}
}