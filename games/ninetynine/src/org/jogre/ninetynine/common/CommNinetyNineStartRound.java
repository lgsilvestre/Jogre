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
import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

/**
 * Communications object for telling clients to start a round of Ninety Nine
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommNinetyNineStartRound extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "nn_startRound";
	public static final String XML_ATT_FIRST_PLAYER = "first";

	// The player who is to start this round
	private int firstPlayer;

	/**
	 * Constructor given a playerId.
	 *
	 * @param	firstPlayer		The player who is to start this round.
	 */
	public CommNinetyNineStartRound(int firstPlayer) {
		super();

		this.firstPlayer = firstPlayer;
	}

	/**
	 * Constructor given an xml message.
	 */
	public CommNinetyNineStartRound(XMLElement message) {
		super(message);

		firstPlayer = message.getIntAttribute(XML_ATT_FIRST_PLAYER);
	}

	/**
	 * Return the player who is to play first.
	 */
	public int getFirstPlayerId() {
		return firstPlayer;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_FIRST_PLAYER, firstPlayer);
		return message;
	}
}
