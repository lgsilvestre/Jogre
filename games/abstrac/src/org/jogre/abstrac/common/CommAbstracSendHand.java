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
 * Communications object for sending a hand of cards to the players
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommAbstracSendHand extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "hand";
	public static final String XML_ATT_CARDS = "cards";

	// Array of cards to send
	private int [] theCards;

	/**
	 * Constructor that takes a hand
	 * 
	 * @param theCards		The cards to be sent
	 */
	public CommAbstracSendHand(int [] theCards) {
		super();
		this.theCards = theCards;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommAbstracSendHand(XMLElement message) {
		super(message);

		theCards = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_CARDS));
	}

	/**
	 * Get the cards
	 * 
	 * @return the cards
	 */
	public int [] getCards() {
		return this.theCards;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_CARDS, JogreUtils.valueOf(theCards));

		return message;
	}
}
