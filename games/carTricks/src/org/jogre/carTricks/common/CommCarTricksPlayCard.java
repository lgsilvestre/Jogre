/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a Play Card message for Car Tricks.
 * 
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class CommCarTricksPlayCard extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "car_tricks_play_card";
	public static final String XML_ATT_COLOR = "color";
	public static final String XML_ATT_VALUE = "value";
	public static final String XML_ATT_FINALCARD = "final";

	// Card to transmit
	private CarTricksCard card;

	// Indicates if this is the final card in the player's hand
	private boolean finalCard;

	/**
	 * Constructor that takes a username and a card.
	 * 
	 * @param username		Username
	 * @param card			Card
	 */
	public CommCarTricksPlayCard(String username, CarTricksCard card, boolean isFinalCard) {
		super(username);
		this.card = new CarTricksCard (card);
		this.finalCard = isFinalCard;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param message		XML element
	 */
	public CommCarTricksPlayCard(XMLElement message) {
		super(message);
		this.card = new CarTricksCard (
			message.getIntAttribute(XML_ATT_COLOR),
			message.getIntAttribute(XML_ATT_VALUE));
		this.finalCard = message.getBooleanAttribute(XML_ATT_FINALCARD, "t", "f", false);
	}

	/**
	 * Get the card
	 * 
	 * @return the card
	 */
	public CarTricksCard getCard() {
		return this.card;
	}

	/**
	 * Get the state of the finalCard field
	 *
	 * @return If this is the player's final card
	 */
	public boolean isFinalCard() {
		return this.finalCard;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_COLOR, this.card.cardColor());
		message.setIntAttribute(XML_ATT_VALUE, this.card.cardValue());
		message.setAttribute(XML_ATT_FINALCARD, this.finalCard ? "t" : "f");
		return message;
	}
}
