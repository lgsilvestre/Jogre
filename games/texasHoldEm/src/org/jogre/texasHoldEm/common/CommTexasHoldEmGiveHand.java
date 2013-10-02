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
 * Communications object for giving a player two private cards for TexasHoldEm
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommTexasHoldEmGiveHand extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "giveHand";
	public static final String XML_ATT_TO_PLAYER = "p";
	public static final String XML_ATT_CARD1 = "c1";
	public static final String XML_ATT_CARD2 = "c2";

	// Information about the move
	private int destPlayerId;
	private Card card1, card2;

	/**
	 * Constructor that takes the parts of the message.
	 *
	 * @param username      Username sending the message
	 * @param destPlayerId	The player the cards are going to.
	 * @param card1, card2	The two cards to give to the player.
	 */
	public CommTexasHoldEmGiveHand (String username, int destPlayerId, Card card1, Card card2) {
		super(username);

		this.destPlayerId = destPlayerId;
		this.card1 = card1;
		this.card2 = card2;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommTexasHoldEmGiveHand (XMLElement message) {
		super(message);

		destPlayerId = message.getIntAttribute(XML_ATT_TO_PLAYER);
		card1 = Card.fromString(message.getStringAttribute(XML_ATT_CARD1));
		card2 = Card.fromString(message.getStringAttribute(XML_ATT_CARD2));
	}

	/**
	 * Get the seat number of the player the cards are going to
	 *
	 * @return the Id
	 */
	public int getDestPlayerSeat() {
		return this.destPlayerId;
	}

	/**
	 * Return the cards
	 */
	public Card getCard (int which) {
		return (which == 0) ? card1 : card2;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_TO_PLAYER, destPlayerId);
		message.setAttribute(XML_ATT_CARD1, card1.toString());
		message.setAttribute(XML_ATT_CARD2, card2.toString());

		return message;
	}

}
