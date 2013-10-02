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
 * Communications object for transmitting a Score message for Car Tricks.
 * 
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class CommCarTricksScore extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "car_tricks_score";
	public static final String XML_ATT_BID = "bid";
	public static final String XML_ATT_SCORE = "score";

	// Bid to transmit
	private int [] bid;
	private int score;

	/**
	 * Constructor that takes a username bid and score.
	 * 
	 * @param username		Username
	 * @param bid			the Bid
	 */
	public CommCarTricksScore(String username, int [] bid, int score) {
		super(username);
		this.bid = bid;
		this.score = score;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param message		XML element
	 */
	public CommCarTricksScore(XMLElement message) {
		super(message);
		bid = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_BID));
		score = message.getIntAttribute(XML_ATT_SCORE);
	}

	/**
	 * Get the bid
	 * 
	 * @return the bid
	 */
	public int [] getBid() {
		return bid;
	}

	/**
	 * Get the score
	 * 
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_BID, JogreUtils.valueOf(bid));
		message.setIntAttribute(XML_ATT_SCORE, this.score);

		return message;
	}
}
