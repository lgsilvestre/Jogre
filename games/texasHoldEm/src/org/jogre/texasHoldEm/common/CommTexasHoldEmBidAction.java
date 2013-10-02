/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a bid action for TexasHoldEm
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommTexasHoldEmBidAction extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "bid";
	public static final String XML_ATT_PLAYERID =  "p";
	public static final String XML_ATT_AMOUNT = "a";

	// Information about the bid to transmit
	private int playerId;
	private int amount;

	/**
	 * Constructor that takes the parts of the message.
	 *
	 * @param username      Username
	 * @param playerId      The player ID
	 * @param amount		Amount of the bid.
	 */
	public CommTexasHoldEmBidAction(String username, int playerId, int amount) {
		super(username);
		this.playerId = playerId;
		this.amount = amount;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommTexasHoldEmBidAction(XMLElement message) {
		super(message);
		this.playerId = message.getIntAttribute(XML_ATT_PLAYERID);
		this.amount = message.getIntAttribute(XML_ATT_AMOUNT);
	}

	/**
	 * Get the player id
	 *
	 * @return the Id
	 */
	public int getPlayerSeat() {
		return this.playerId;
	}

	/**
	 * Get the bid amount
	 *
	 * @return the amount
	 */
	public int getAmount() {
		return this.amount;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_PLAYERID, this.playerId);
		message.setIntAttribute(XML_ATT_AMOUNT, this.amount);
		return message;
	}
}
