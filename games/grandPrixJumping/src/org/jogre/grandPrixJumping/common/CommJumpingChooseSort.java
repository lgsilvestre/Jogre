/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for transmitting a message for choosing
 * one of the sorted piles for Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommJumpingChooseSort extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "chooseSort";
	public static final String XML_ATT_CHOICE = "choice";

	// The chosen direction
	private boolean choseLeft;

	/**
	 * Constructor
	 *
	 * @param username		Username
	 * @param choice		True => Choose left; False => Choose right;
	 */
	public CommJumpingChooseSort( String username, boolean choice) {
		super(username);

		this.choseLeft = choice;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message		XML element
	 */
	public CommJumpingChooseSort(XMLElement message) {
		super(message);

		choseLeft = message.getStringAttribute(XML_ATT_CHOICE).equals("t");
	}

	/**
	 * Return fields of this message.
	 */
	public boolean getChoice() { return this.choseLeft; }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_CHOICE, choseLeft ? "t" : "f");

		return message;
	}
}
