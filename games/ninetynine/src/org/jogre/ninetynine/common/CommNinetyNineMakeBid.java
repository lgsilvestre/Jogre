/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;

/**
 * Communications object for making a bid for Ninety Nine.
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommNinetyNineMakeBid extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "nn_bid";
	public static final String XML_ATT_CARD1 = "c1";
	public static final String XML_ATT_CARD2 = "c2";
	public static final String XML_ATT_CARD3 = "c3";
	public static final String XML_ATT_TYPE = "t";
	public static final String XML_ATT_LEADER = "l";

	// Cards to bid
	private Hand bidHand;

	// Type of bid to make (unknow, normal, declare, reveal from CoreModel)
	private int bidType;

	// Player that should lead.  Only defined when bid type is reveal.
	private int leadPlayer;

	/**
	 * Constructor that takes a username and a hand with the bid in it
	 * 
	 * @param username		Username
	 * @param bidHand		The cards to be sent as a bid
	 * @param bidType		The type of bid to make.
	 */
	public CommNinetyNineMakeBid(String username, Hand bidHand, int bidType) {
		super(username);
		this.bidHand = bidHand;
		if (bidHand == null) {
			// If no bid provided, then set the type to unknown
			this.bidType = NinetyNineCoreModel.BID_UNKNOWN;
		} else {
			this.bidType = bidType;
		}
		this.leadPlayer = -1;
	}

	/**
	 * Constructor that takes a username and a hand with the bid in it and
	 * the lead player.
	 *
	 * Note: Since this only makes sense when the bid is REVEAL, bidType
	 * could be implicit.  However, that would make this constructor look
	 * the same as the one above (String, Hand, int).  So, for now I just
	 * leave bidType in.  Eventually, maybe change this to be a static
	 * factory...
	 * 
	 * @param username		Username
	 * @param bidHand		The cards to be sent as a bid
	 * @param bidType		The type of bid to make.
	 * @param leadPlayer	The Player ID to lead
	 */
	public CommNinetyNineMakeBid(String username, Hand bidHand, int bidType, int leadPlayer) {
		this(username, bidHand, bidType);
		this.leadPlayer = leadPlayer;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommNinetyNineMakeBid(XMLElement message) {
		super(message);

		// First, get the type of bid from the message
		bidType = message.getIntAttribute(XML_ATT_TYPE);
		leadPlayer = message.getIntAttribute(XML_ATT_LEADER);

		if (bidType != NinetyNineCoreModel.BID_UNKNOWN) {
			bidHand = new Hand();
			bidHand.addCard(Card.fromString(message.getStringAttribute(XML_ATT_CARD1)));
			bidHand.addCard(Card.fromString(message.getStringAttribute(XML_ATT_CARD2)));
			bidHand.addCard(Card.fromString(message.getStringAttribute(XML_ATT_CARD3)));
		} else {
			bidHand = null;
		}
	}

	/**
	 * Get the bid hand
	 * 
	 * @return the bid hand
	 */
	public Hand getBidHand() {
		return this.bidHand;
	}

	/**
	 * Get the type of bid
	 *
	 * @return the type of bid
	 */
	public int getBidType() {
		return this.bidType;
	}

	/**
	 * Get the lead player
	 *
	 * @return the lead player
	 */
	public int getLeadPlayer() {
		return this.leadPlayer;
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		if (bidHand != null) {
			// Only send an actual bid hand if there is one to send.
			message.setAttribute(XML_ATT_CARD1, bidHand.getNthCard(0).toString());
			message.setAttribute(XML_ATT_CARD2, bidHand.getNthCard(1).toString());
			message.setAttribute(XML_ATT_CARD3, bidHand.getNthCard(2).toString());
		}
		message.setIntAttribute(XML_ATT_TYPE, bidType);
		message.setIntAttribute(XML_ATT_LEADER, leadPlayer);
		return message;
	}
}
