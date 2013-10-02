/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for sending a move for Abstrac
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommAbstracMove extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "move";
	public static final String XML_ATT_NUM_CARDS = "n";

	// Info to send
	private int numCards;

	/**
	 * Constructor that takes a username and a number of cards to choose
	 * 
	 * @param username		Username
	 * @param numCards		The number of cards to choose
	 */
	public CommAbstracMove(String username, int numCards) {
		super(username);
		this.numCards = numCards;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommAbstracMove(XMLElement message) {
		super(message);

		// Pull the number of cards out of the message
		numCards = message.getIntAttribute(XML_ATT_NUM_CARDS);
	}

	/**
	 * Get the number of cards
	 *
	 * @return the number of cards
	 */
	public int getNumCards() {
		return this.numCards;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		// Attach the number of cards to the message
		message.setIntAttribute(XML_ATT_NUM_CARDS, numCards);

		return message;
	}
}
