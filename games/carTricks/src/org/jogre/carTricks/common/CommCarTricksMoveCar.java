/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.carTricks.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for transmitting a Play Card message for Car Tricks.
 * 
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class CommCarTricksMoveCar extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "car_tricks_move_car";
	public static final String XML_ATT_PATH = "path";

	// Path to transmit
	private CarTricksPath path;

	/**
	 * Constructor that takes a username and a path.
	 * 
	 * @param username		Username
	 * @param parh			Path to send
	 */
	public CommCarTricksMoveCar(String username, CarTricksPath path) {
		super(username);
		this.path = path;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param message		XML element
	 */
	public CommCarTricksMoveCar(XMLElement message) {
		super(message);
		
		this.path = new CarTricksPath(JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_PATH)));
	}

	/**
	 * Get the path
	 * 
	 * @return the path
	 */
	public CarTricksPath getPath() {
		return this.path;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_PATH, JogreUtils.valueOf(path.getLocationArray()));

		return message;
	}
}
