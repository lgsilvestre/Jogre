/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for transmitting a complete database for Car Tricks.
 * 
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommCarTricksTrackDB extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "car_tricks_trackDatabase";

	private CarTricksTrackDB theDB;

	/**
	 * Constructor that takes a database
	 *
	 * @param theDB			The track database to send
	 */
	public CommCarTricksTrackDB(CarTricksTrackDB theDB) {
		super();
		this.theDB = theDB;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param message		XML element
	 */
	public CommCarTricksTrackDB(XMLElement message) {
		super(message);

		this.theDB = new CarTricksTrackDB((XMLElement) message.getChildren().firstElement());
	}

	/**
	 * Get the database
	 * 
	 * @return the database
	 */
	public CarTricksTrackDB getDatabase() {
		return this.theDB;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		// Use the CarTricksTrackDB.java flatten() method to provide us
		// with an XML tree to attach as our child.
		message.addChild(theDB.flatten());

		return message;
	}
}
