/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter (rwalter42@yahoo.com)
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

import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for indicating where the active jump
 * shadow horse should be put for Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommJumpingSetShadowHorse extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "setHorse";
	public static final String XML_ATT_SPACE = "s";

	private int space;

	/**
	 * Constructor when given a space
	 *
	 * @param username		Username
	 * @param space			The space to put the horse
	 */
	public CommJumpingSetShadowHorse(String username, int space)
	{
		super(username);

		this.space = space;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message		XML element
	 */
	public CommJumpingSetShadowHorse(XMLElement message) {
		super(message);

		this.space = message.getIntAttribute(XML_ATT_SPACE);
	}

	/**
	 * Return fields of this message.
	 */
	public int getSpace()		{ return this.space; }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_SPACE, space);

		return message;
	}
}
