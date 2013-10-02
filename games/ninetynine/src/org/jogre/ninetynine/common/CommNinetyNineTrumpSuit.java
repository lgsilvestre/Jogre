/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.ninetynine.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

/**
 * Communications object for sending the current trump suit
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommNinetyNineTrumpSuit extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "nn_trump";
	public static final String XML_ATT_TRUMP_SUIT = "trump";

	// Info to send
	private int trumpSuit;

	/**
	 * Constructor that takes a username and a trump suit
	 * 
	 * @param username		Username
	 * @param trumpSuit		The trump suit
	 */
	public CommNinetyNineTrumpSuit(String username, int trumpSuit) {
		super(username);
		this.trumpSuit = trumpSuit;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommNinetyNineTrumpSuit(XMLElement message) {
		super(message);

		// Pull the trump suit out of the message
		trumpSuit = message.getIntAttribute(XML_ATT_TRUMP_SUIT);
	}

	/**
	 * Get the trump suit
	 *
	 * @return the trump suit
	 */
	public int getTrumpSuit() {
		return this.trumpSuit;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		// Attach the trump suit to the message
		message.setIntAttribute(XML_ATT_TRUMP_SUIT, trumpSuit);

		return message;
	}
}
