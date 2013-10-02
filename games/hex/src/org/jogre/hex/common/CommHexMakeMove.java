/*
 * JOGRE (Java Online Gaming Real-time Engine) - Hex
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.hex.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a move for Hex
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommHexMakeMove extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "hex_move";
	public static final String XML_ATT_C = "c";
	public static final String XML_ATT_R = "r";
	public static final String XML_ATT_PLAYERID =  "p";

	// Location to transmit
	private int col;
	private int row;
	private int playerId;

	/**
	 * Constructor that takes a username, location and playerId.
	 *
	 * @param username      Username
	 * @param (c, r)        Location
	 * @param playerId      The player ID
	 */
	public CommHexMakeMove(String username, int c, int r, int playerId) {
		super(username);
		this.col = c;
		this.row = r;
		this.playerId = playerId;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommHexMakeMove(XMLElement message) {
		super(message);
		this.col = message.getIntAttribute(XML_ATT_C);
		this.row = message.getIntAttribute(XML_ATT_R);
		this.playerId = message.getIntAttribute(XML_ATT_PLAYERID);
	}

	/**
	 * Get location
	 *
	 * @return a location
	 */
	public int getCol() { return this.col; }
	public int getRow() { return this.row; }

	/**
	 * Get the player id
	 *
	 * @return the Id
	 */
	public int getPlayerSeat() {
		return this.playerId;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_C, this.col);
		message.setIntAttribute(XML_ATT_R, this.row);
		message.setIntAttribute(XML_ATT_PLAYERID, this.playerId);
		return message;
	}
}
