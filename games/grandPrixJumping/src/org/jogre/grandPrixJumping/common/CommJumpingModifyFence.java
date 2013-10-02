/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.grandPrixJumping.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a fence modification message for
 * Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommJumpingModifyFence extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "modify";
	public static final String XML_ATT_LOCATION = "l";
	public static final String XML_ATT_DIRECTION = "d";

	private int location;
	private int direction;

	/**
	 * Constructor
	 *
	 * @param username		Username
	 * @param location		The space to be modified
	 * @param direction		The direction to modify
	 */
	public CommJumpingModifyFence( String username, int location, int direction) {
		super(username);

		this.location = location;
		this.direction = direction;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message		XML element
	 */
	public CommJumpingModifyFence(XMLElement message) {
		super(message);

		this.location = message.getIntAttribute(XML_ATT_LOCATION);
		this.direction = message.getIntAttribute(XML_ATT_DIRECTION);
	}

	/**
	 * Return fields of this message.
	 */
	public int getLocation()	{ return location; }
	public int getDirection()	{ return direction; }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_LOCATION, location);
		message.setIntAttribute(XML_ATT_DIRECTION, direction);

		return message;
	}
}
