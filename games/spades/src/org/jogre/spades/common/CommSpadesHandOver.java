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

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting end of hand protocol.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class CommSpadesHandOver extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "spades_hand_over";
	public static final String XML_ATT_HAND = "hand";
	public static final String XML_ATT_TRICK = "trick";

	// hand to transmit
	private int hand = 0;

	// trick seat num to transmit
	private int trick = 0;

	/**
	 * Constructor that takes a username, hand and trick.
	 *
	 * @param username
	 *            Username
	 * @param bid
	 *            Bid
	 */
	public CommSpadesHandOver(int hand, int trick) {
		super();
		this.hand = hand;
		this.trick = trick;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message
	 *            Xml element
	 */
	public CommSpadesHandOver(XMLElement message) {
		super(message);
		this.hand = message.getIntAttribute(XML_ATT_HAND);
		this.trick = message.getIntAttribute(XML_ATT_TRICK);
	}

	/**
	 * Get hand
	 *
	 * @return a hand
	 */
	public int getHand() {
		return this.hand;
	}

	/**
	 * Get trick seat number
	 *
	 * @return a trick seat number
	 */
	public int getTrick() {
		return this.trick;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_HAND, this.hand);
		message.setIntAttribute(XML_ATT_TRICK, this.trick);
		return message;
	}
}