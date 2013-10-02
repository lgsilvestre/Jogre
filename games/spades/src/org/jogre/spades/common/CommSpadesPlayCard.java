/*
 * JOGRE (Java Online Gaming Real-time Engine) - TicTacToe
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
package org.jogre.spades.common;

import java.util.Enumeration;
import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.games.Card;

/**
 * Communications object for transmitting a card for spades.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class CommSpadesPlayCard extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "spades_play_card";

	// card object to transmit
	private Card card = null;

	/**
	 * Constructor that takes a username and card object.
	 *
	 * @param username
	 *            Username
	 * @param card
	 *            Card
	 */
	public CommSpadesPlayCard(String username, Card card) {
		super(username);
		this.card = card;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message
	 *            Xml element
	 */
	public CommSpadesPlayCard(XMLElement message) {
		super(message);

		// Read child elements - card objects
		Enumeration e = message.enumerateChildren();
		while (e != null && e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement) e.nextElement();

			if (childMessage.getName().equals(Card.XML_NAME)) {
				this.card = new Card(childMessage);
				break;
			}
		}
	}

	/**
	 * Get card
	 *
	 * @return a Card
	 */
	public Card getCard() {
		return this.card;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.addChild(this.card.flatten());
		return message;
	}
}