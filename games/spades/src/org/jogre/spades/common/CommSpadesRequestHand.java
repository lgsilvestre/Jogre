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
 * Communications object for requesting a hand for spades.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class CommSpadesRequestHand extends CommTableMessage {

	// xml name for communication object
	public static final String XML_NAME = "spades_request_hand";

	/**
	 * Constructor that takes a username so that the message can include
	 * username within it.
	 *
	 * @param username
	 *            Username
	 */
	public CommSpadesRequestHand(String username) {
		super(username);
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message
	 */
	public CommSpadesRequestHand(XMLElement message) {
		super(message);
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		return message;
	}
}