/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.common.comm;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;

/**
 * Communication object to send a key/value property from a client to server
 * or vice versa.  Used in the JogreController to simplify the sending and
 * receiving of data.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommControllerProperty extends CommTableMessage {

	/** Type is a String. */
	public static final int TYPE_STRING = 0;

	/** Type is an integer. */
	public static final int TYPE_INT = 1;

	/** Type is two integer e.g. co-ordinate for example. */
	public static final int TYPE_INT_TWO = 2;

	// Fields which hold the key and the value.
	private String key = null, value;

	private static final String XML_ATT_KEY   = "key";
	private static final String XML_ATT_VALUE = "value";

	/**
	 * Constructor which takes a username, status, table number and a value.
	 *
	 * @param status       Type of message e.g. TYPE_STRING, TYPE_INT etc.
	 * @param key          Key
	 * @param value        Value
	 */
	public CommControllerProperty (int status, String key, String value) {
		super (status);

		this.key = key;
		this.value = value;
	}

	/**
	 * Constructor which reads the game over object from a String.
	 *
	 * @param message                     XML element version of object.
	 * @throws TransmissionException      Thrown if problem in transmission
	 */
	public CommControllerProperty (XMLElement message) throws TransmissionException {
		super (message);

		if (!message.getName().equals (Comm.CONTROLLER_PROPERTY))
			throw new TransmissionException ("Error parsing CommControllerProperty");

		this.key   = message.getStringAttribute (XML_ATT_KEY);
		this.value = message.getStringAttribute (XML_ATT_VALUE);
	}

	/**
	 * Return the key.
	 *
	 * @return   Controller key.
	 */
	public String getKey () {
		return key;
	}

	/**
	 * Return the value.
	 *
	 * @return   Controller property.
	 */
	public String getValue () {
		return value;
	}

	/**
	 * Flatten communciation object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = flatten (Comm.CONTROLLER_PROPERTY);
		message.setAttribute (XML_ATT_KEY, key);
		message.setAttribute (XML_ATT_VALUE, value);

		return message;
	}
}