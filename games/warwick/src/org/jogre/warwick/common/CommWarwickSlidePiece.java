/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
 * Copyright (C) 2003 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.warwick.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a move of a piece for Warwick.
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommWarwickSlidePiece extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "k_slide";
	public static final String XML_ATT_FROM_REGION = "fr";
	public static final String XML_ATT_FROM_SPACE  = "fs";
	public static final String XML_ATT_TO_REGION   = "tr";
	public static final String XML_ATT_TO_SPACE    = "ts";

	// Information about the move.
	private int fromRegion, fromSpace;
	private int toRegion, toSpace;

	/**
	 * Constructor that takes a username and move info.
	 *
	 * @param username                Username
	 * @param fromRegion, fromSpace   The space the piece is moving from
	 * @param toRegion, toSpace       The space the piece is moving to
	 */
	public CommWarwickSlidePiece (String username,
	       int fromRegion, int fromSpace, int toRegion, int toSpace) {
		super(username);

		this.fromRegion = fromRegion;
		this.fromSpace  = fromSpace;
		this.toRegion   = toRegion;
		this.toSpace    = toSpace;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param    message    XML element
	 */
	public CommWarwickSlidePiece (XMLElement message) {
		super(message);

		fromRegion = message.getIntAttribute(XML_ATT_FROM_REGION);
		fromSpace  = message.getIntAttribute(XML_ATT_FROM_SPACE);
		toRegion   = message.getIntAttribute(XML_ATT_TO_REGION);
		toSpace    = message.getIntAttribute(XML_ATT_TO_SPACE);
	}

	/**
	 * Get move info
	 */
	public int getFromRegion () { return fromRegion; }
	public int getFromSpace ()  { return fromSpace; }
	public int getToRegion ()   { return toRegion; }
	public int getToSpace ()    { return toSpace; }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_FROM_REGION, fromRegion);
		message.setIntAttribute(XML_ATT_FROM_SPACE, fromSpace);
		message.setIntAttribute(XML_ATT_TO_REGION, toRegion);
		message.setIntAttribute(XML_ATT_TO_SPACE, toSpace);

		return message;
	}

}
