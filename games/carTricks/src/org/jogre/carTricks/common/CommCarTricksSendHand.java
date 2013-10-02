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
 * Communications object for transmitting a Hand message for Car Tricks.
 * 
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class CommCarTricksSendHand extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "car_tricks_hand";
	public static final String XML_ATT_COLORS = "colors";
	public static final String XML_ATT_VALUES = "values";

	private CarTricksCard [] theHand;

	/**
	 * Constructor
	 * 
	 * @param username		Username
	 */
	public CommCarTricksSendHand(String username, CarTricksCard [] theHand) {
		super(username);

		this.theHand = theHand;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param message		XML element
	 */
	public CommCarTricksSendHand(XMLElement message) {
		super(message);

		// Pull out the attribute strings for color & value
		int [] colorArray = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_COLORS));
		int [] valueArray = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_VALUES));

		// Create a new hand to put the cards in
		theHand = new CarTricksCard [colorArray.length];

		// Create the cards based on the color & value arrays
		for (int i=0; i<colorArray.length; i++) {
			theHand[i] = new CarTricksCard(colorArray[i], valueArray[i]);
		}
	}

	/**
	 * Return the hand
	 *
	 * @return  the hand
	 */
	public CarTricksCard [] getHand() {
		return theHand;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		// Extract the colors & values into two arrays
		int [] colorArray = new int [theHand.length];
		int [] valueArray = new int [theHand.length];

		for (int i=0; i<theHand.length; i++) {
			colorArray[i] = theHand[i].cardColor();
			valueArray[i] = theHand[i].cardValue();
		}

		// Send the arrays as attribute strings
		message.setAttribute(XML_ATT_COLORS, JogreUtils.valueOf(colorArray));
		message.setAttribute(XML_ATT_VALUES, JogreUtils.valueOf(valueArray));

		return message;
	}
}
