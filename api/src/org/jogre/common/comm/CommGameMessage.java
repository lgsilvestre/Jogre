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

/**
 * This is a message class which goes to a particular game such as chess,
 * checkers etc.  The extending class doesn't have to set the game - this is
 * done behind the scenes.  The gameID should be the game and version
 * seperated with a colon e.g. chess:0.1
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public abstract class CommGameMessage implements ITransmittable {

    private static final int NO_STATUS = -1;

	/** Integer status of this comm message. */
	protected int status;

	/**
	 * Username of the person who sent this message.  This is usually
	 * required to be set if a client needs to know who sent a message
	 * and is set in the server.
	 */
	protected String username = null;

	/**
	 * Username of the person who this is being sent to (if empty then no one).
	 */
	protected String usernameTo = null;

	/** Type of message is used to determine which layer the message is for. */
	public static final String XML_ATT_STATUS      = "status";
	public static final String XML_ATT_USERNAME    = "username";
	public static final String XML_ATT_USERNAME_TO = "usernameTo";

	/**
	 * Constructor with no parameters (no status).
	 */
	protected CommGameMessage () {
		this (NO_STATUS, null);
	}

	/**
	 * Constructor which takes a username and a status.
	 *
	 * @param status    A status
	 */
	protected CommGameMessage (int status) {
		this (status, null);
	}

	/**
	 * Constructor which only takes a username.
	 *
	 * @param usernameTo
	 */
	public CommGameMessage (String username) {
	    this (NO_STATUS, username);
	}

	/**
	 * Constructor which takes a status and a username to.
	 *
	 * @param status
	 * @param usernameTo
	 */
	public CommGameMessage (int status, String username) {
	    this.status = status;
		this.username = username;
	}

	/**
	 * Constructor which parses the XMLElement into class fields.
	 *
	 * @param message  Element to parse.
	 */
	protected CommGameMessage (XMLElement message) {
		this.status     = message.getIntAttribute    (XML_ATT_STATUS, NO_STATUS);
		this.username   = message.getStringAttribute (XML_ATT_USERNAME);
		this.usernameTo = message.getStringAttribute (XML_ATT_USERNAME_TO);
	}

	/**
	 * Return the status.
	 *
	 * @return    Status of user.
	 */
	public int getStatus () {
		return status;
	}

	/**
	 * Set the optional uesrname variable.
	 *
	 * @param username
	 */
	public void setUsername (String username) {
	    this.username = username;
	}

	/**
	 * Return the optional usernameTo variable.
	 *
	 * @return  username
	 */
	public String getUsername () {
		return username;
	}

	/**
	 * Set the optional usernameTo variable.
	 *
	 * @param usernameTo
	 */
	public void setUsernameTo (String usernameTo) {
	    this.usernameTo = usernameTo;
	}

	/**
	 * Return the optional usernameTo variable.
	 *
	 * @return  username
	 */
	public String getUsernameTo () {
		return usernameTo;
	}

	/**
	 * Return true / false on the username being set.
	 *
	 * @return   True if username is set.
	 */
	public boolean isUsernameSet () {
		return (username != null);
	}

	/**
	 * Return true / false on the usernameTo being set.
	 *
	 * @return   True if usernameTo is set.
	 */
	public boolean isUsernameToSet () {
		return (usernameTo != null);
	}

	/**
	 * Flatten a JOGRE message and indent it.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
	    return flatten ().toString (true);
	}

	/**
	 * Create a generic (nameless) XMLElement which sub classes can use.
	 *
	 * @param name	Name of the XML element.
	 * @return      XML version of this class.
	 */
	public XMLElement flatten (String name) {
		XMLElement message = new XMLElement (name);

		if (status != NO_STATUS)
		    message.setIntAttribute (XML_ATT_STATUS, status);
		if (isUsernameSet())
			message.setAttribute (XML_ATT_USERNAME, username);
		if (isUsernameToSet())
			message.setAttribute (XML_ATT_USERNAME_TO, usernameTo);

		return message;			// return element back to user
	}
}