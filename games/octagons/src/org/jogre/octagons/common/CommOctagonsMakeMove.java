/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.octagons.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.octagons.client.OctLoc;

/**
 * Communications object for transmitting a move for Octagons.
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommOctagonsMakeMove extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "octagons_move";
	public static final String XML_ATT_I = "i";
	public static final String XML_ATT_J = "j";
	public static final String XML_ATT_ELEMENT = "element";
	public static final String XML_ATT_PLAYERID =  "player_id";

	// Location to transmit
	private OctLoc location;
	private int playerId;

	/**
	 * Constructor that takes a username, location and playerId.
	 * 
	 * @param username		Username
	 * @param loc			Location
	 * @param playerId		The player ID
	 */
	public CommOctagonsMakeMove(String username, OctLoc loc, int playerId) {
		super(username);
		this.location = new OctLoc (loc);
		this.playerId = playerId;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommOctagonsMakeMove(XMLElement message) {
		super(message);
		this.location = new OctLoc (
			message.getIntAttribute(XML_ATT_I),
			message.getIntAttribute(XML_ATT_J),
			message.getIntAttribute(XML_ATT_ELEMENT));
		this.playerId = message.getIntAttribute(XML_ATT_PLAYERID);
	}

	/**
	 * Get location
	 * 
	 * @return a location
	 */
	public OctLoc getLoc() {
		return this.location;
	}

	/**
	 * Get the player id
	 * 
	 * @return the Id
	 */
	public int getPlayerId() {
		return this.playerId;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_I, this.location.get_i());
		message.setIntAttribute(XML_ATT_J, this.location.get_j());
		message.setIntAttribute(XML_ATT_ELEMENT, this.location.get_element());
		message.setIntAttribute(XML_ATT_PLAYERID, this.playerId);
		return message;
	}
}
