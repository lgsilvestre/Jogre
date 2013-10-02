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

import org.jogre.common.util.JogreUtils;

/**
 * Communications object for reporting the end-of-round
 * information for Ninety Nine.
 * 
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommNinetyNineRoundScore extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "nn_eor";
	public static final String XML_ATT_BID01 = "b01";
	public static final String XML_ATT_BID02 = "b02";
	public static final String XML_ATT_BID03 = "b03";
	public static final String XML_ATT_BID11 = "b11";
	public static final String XML_ATT_BID12 = "b12";
	public static final String XML_ATT_BID13 = "b13";
	public static final String XML_ATT_BID21 = "b21";
	public static final String XML_ATT_BID22 = "b22";
	public static final String XML_ATT_BID23 = "b23";
	public static final String XML_ATT_SCORES = "s";

	// Hands bid by the players
	private Hand [] bids;

	// Player's scores
	private int [] scores;

	/**
	 * Constructor that takes bids & scores
	 *
	 * @param p0Bid			Player 0's bid
	 * @param p0Score		Player 0's score
	 * @param p1Bid			Player 1's bid
	 * @param p1Score		Player 1's score
	 * @param p2Bid			Player 2's bid
	 * @param p2Score		Player 2's score
	 */
	public CommNinetyNineRoundScore(
		Hand p0Bid, int p0Score,
		Hand p1Bid, int p1Score,
		Hand p2Bid, int p2Score
	) {
		super();

		this.bids = new Hand [] {new Hand(p0Bid), new Hand(p1Bid), new Hand(p2Bid)};
		this.scores = new int [] {p0Score, p1Score, p2Score};
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 * 
	 * @param	message		XML element
	 */
	public CommNinetyNineRoundScore(XMLElement message) {
		super(message);

		bids = new Hand [] {new Hand(), new Hand(), new Hand()};

		bids[0].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID01)));
		bids[0].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID02)));
		bids[0].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID03)));

		bids[1].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID11)));
		bids[1].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID12)));
		bids[1].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID13)));

		bids[2].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID21)));
		bids[2].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID22)));
		bids[2].addCard(Card.fromString(message.getStringAttribute(XML_ATT_BID23)));

		scores = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_SCORES));
	}

	/**
	 * Get a bid hand for a player
	 *
	 * @param	playerId	The plyaer number whose bid hand is returned
	 * @return the bid hand for the given player
	 */
	public Hand getBidHand(int playerId) {
		return bids[playerId];
	}

	/**
	 * Get the score for a player
	 *
	 * @param	playerId	The plyaer number whose score is returned
	 * @return the score for the given player
	 */
	public int getScore (int playerId) {
		return scores[playerId];
	}

	/**
	 * Flattens this object into xml.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_BID01, bids[0].getNthCard(0).toString());
		message.setAttribute(XML_ATT_BID02, bids[0].getNthCard(1).toString());
		message.setAttribute(XML_ATT_BID03, bids[0].getNthCard(2).toString());
		
		message.setAttribute(XML_ATT_BID11, bids[1].getNthCard(0).toString());
		message.setAttribute(XML_ATT_BID12, bids[1].getNthCard(1).toString());
		message.setAttribute(XML_ATT_BID13, bids[1].getNthCard(2).toString());

		message.setAttribute(XML_ATT_BID21, bids[2].getNthCard(0).toString());
		message.setAttribute(XML_ATT_BID22, bids[2].getNthCard(1).toString());
		message.setAttribute(XML_ATT_BID23, bids[2].getNthCard(2).toString());

		message.setAttribute(XML_ATT_SCORES, JogreUtils.valueOf(scores));
		return message;
	}
}
