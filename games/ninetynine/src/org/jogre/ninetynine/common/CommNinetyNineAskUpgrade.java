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
 * Communications object for asking a player if he wishes
 * to upgrade a declare bid to a reveal.
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommNinetyNineAskUpgrade extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "nn_askUpgrade";
	public static final String XML_ATT_DECLARER = "did";
	public static final String XML_ATT_REVEALER = "rid";

	// declarer & revealer id's
	private int declarer, revealer;

	/**
	 * Constructor that takes a declarer and revealer
	 *
	 * @param	declarer	The player who made the declare bid
	 * @param	revealer	The player who made the reveal bid
	 */
	public CommNinetyNineAskUpgrade(int declarer, int revealer) {
		super();
		this.declarer = declarer;
		this.revealer = revealer;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommNinetyNineAskUpgrade(XMLElement message) {
		super(message);

		declarer = message.getIntAttribute(XML_ATT_DECLARER);
		revealer = message.getIntAttribute(XML_ATT_REVEALER);
	}

	/**
	 * Get the declarer & revealers
	 */
	public int getDeclarer() { return declarer; }
	public int getRevealer() { return revealer; }

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_DECLARER, declarer);
		message.setIntAttribute(XML_ATT_REVEALER, revealer);

		return message;
	}
}
