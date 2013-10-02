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
 * Communications object for showing a common card for TexasHoldEm
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommTexasHoldEmCommonCard extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "commonCard";
	public static final String XML_ATT_CARD = "c";
	public static final String XML_ATT_INDEX = "i";
	public static final String XML_ATT_NEXT_ROUND = "n";

	// Information about the card
	private Card card;
	private int index;
	private boolean nextRound;

	/**
	 * Constructor that takes the parts of the message.
	 *
	 * @param username      Username sending the message
	 * @param card          The common card to send.
	 * @param index			Which common card this is.
	 * @param nextRound		If this card causes the advancing to the next round.
	 */
	public CommTexasHoldEmCommonCard (String username, Card card, int index, boolean nextRound) {
		super(username);

		this.card = card;
		this.index = index;
		this.nextRound = nextRound;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommTexasHoldEmCommonCard (XMLElement message) {
		super(message);

		card = Card.fromString(message.getStringAttribute(XML_ATT_CARD));
		index = message.getIntAttribute(XML_ATT_INDEX);
		nextRound = "t".equals(message.getStringAttribute(XML_ATT_NEXT_ROUND));
	}

	/**
	 * Return the card
	 */
	public Card getCard () {
		return card;
	}

	/**
	 * Return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Return the nextRound flag
	 */
	public boolean getNextRound() {
		return nextRound;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_CARD, card.toString());
		message.setIntAttribute(XML_ATT_INDEX, index);
		message.setAttribute(XML_ATT_NEXT_ROUND, (nextRound ? "t" : "f"));

		return message;
	}

}
