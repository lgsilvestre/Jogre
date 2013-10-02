/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.common;

import nanoxml.XMLElement;

import org.jogre.common.util.JogreUtils;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for indicating a hand is over for TexasHoldEm
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommTexasHoldEmHandOver extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "handOver";
	public static final String XML_ATT_HOLDINGS = "h";
	public static final String XML_ATT_WIN_VALUE = "w";
	public static final String XML_ATT_HANDVALUES = "v";

	// Information in the message
	private int [] holdings;
	private int winningHandValue;
	private int [] handValues;

	/**
	 * Constructor that takes the parts of the message.
	 *
	 * @param username      	Username sending the message
	 * @param newHoldings		Array of the new holdings for each player
	 * @param winningHandType	The value of the hand that won
	 * @parma handValues		All of the hand values for the game
	 */
	public CommTexasHoldEmHandOver (String username, int [] holdings, int winningHandValue, int [] handValues) {
		super(username);

		this.holdings = holdings;
		this.winningHandValue = winningHandValue;
		this.handValues = handValues;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommTexasHoldEmHandOver (XMLElement message) {
		super(message);

		holdings = JogreUtils.convertToIntArray(
		                    message.getStringAttribute(XML_ATT_HOLDINGS));
		handValues = JogreUtils.convertToIntArray(
		                    message.getStringAttribute(XML_ATT_HANDVALUES));
		winningHandValue = message.getIntAttribute(XML_ATT_WIN_VALUE);
	}

	/**
	 * Return the holdings array
	 */
	public int [] getHoldings() {
		return holdings;
	}

	/**
	 * Return the value of the winning hand
	 */
	public int getWinningHandValue() {
		return winningHandValue;
	}

	/**
	 * Return the hand values
	 */
	public int [] getHandValues() {
		return handValues;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_HOLDINGS, JogreUtils.valueOf(holdings));
		message.setAttribute(XML_ATT_HANDVALUES, JogreUtils.valueOf(handValues));
		message.setIntAttribute(XML_ATT_WIN_VALUE, winningHandValue);

		return message;
	}

}
