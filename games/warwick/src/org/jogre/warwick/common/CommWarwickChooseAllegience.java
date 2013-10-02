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
 * Communications object for transmitting a choose allegience move for Warwick.
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommWarwickChooseAllegience extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "k_all";
	public static final String XML_ATT_A = "a";

	// Information about the chosen allegience
	private int ChooseAllegience;

	/**
	 * Constructor that takes a username and allegience.
	 *
	 * @param username      Username
	 * @param allegience    The allegience to make
	 */
	public CommWarwickChooseAllegience (String username, int allegience) {
		super(username);
		ChooseAllegience = allegience;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param    message    XML element
	 */
	public CommWarwickChooseAllegience (XMLElement message) {
		super(message);

		ChooseAllegience = message.getIntAttribute(XML_ATT_A);
	}

	/**
	 * Get the allegience
	 *
	 * @return the allegience
	 */
	public int getAllegience () { return ChooseAllegience; }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_A, ChooseAllegience);

		return message;
	}

}
