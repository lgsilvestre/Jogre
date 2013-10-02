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

/**
 * Communications object for transmitting a card activation message for
 * Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommJumpingActivate extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "activate";
	public static final String XML_ATT_CARD = "c";
	public static final String XML_ATT_HALF = "h";

	// values for card codes
	public static final int RIBBON = 0;
	public static final int OFFICIAL = 1;
	public static final int DUAL_RIDER = 2;

	private int cardCode;
	private int half;

	/**
	 * Constructor
	 *
	 * @param username		Username
	 * @param theCard		The card to be activated
	 * @param half			The half to activate
	 */
	public CommJumpingActivate( String username,
								JumpingCard theCard,
								int half)
	{
		super(username);

		if (theCard.isRibbon()) {
			cardCode = RIBBON;
		} else if (theCard.isOfficial()) {
			cardCode = OFFICIAL;
		} else if (theCard.isDualRider()) {
			cardCode = DUAL_RIDER;
		}

		this.half = half;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message		XML element
	 */
	public CommJumpingActivate(XMLElement message) {
		super(message);

		cardCode = message.getIntAttribute(XML_ATT_CARD);
		half     = message.getIntAttribute(XML_ATT_HALF);
	}

	/**
	 * Return fields of this message.
	 */
	public int getCardCode()	{ return cardCode; }
	public int getHalf()		{ return half; }

	/**
	 * Convert a card Code into a card
	 */
	public JumpingCard getCard() {
		if (cardCode == RIBBON) {
			return JumpingCard.makeRibbonCard();
		} else if (cardCode == OFFICIAL) {
			return JumpingCard.makeOfficialCard();
		} else if (cardCode == DUAL_RIDER) {
			return JumpingCard.makeDualRiderCard();
		}

		// Don't know what to do, so return an unknown card
		return new JumpingCard();
	}

	/**
	 * Verify if the card code is various values.
	 */
	public boolean isRibbon()    { return (cardCode == RIBBON); }
	public boolean isOfficial()  { return (cardCode == OFFICIAL); }
	public boolean isDualRider() { return (cardCode == DUAL_RIDER); }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_CARD, cardCode);
		message.setIntAttribute(XML_ATT_HALF, half);

		return message;
	}
}
