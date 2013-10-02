/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter (rwalter42@yahoo.com)
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

import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for transmitting a Card moving message for
 * Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommJumpingMoveCards extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "moveCards";
	public static final String XML_ATT_CARD = "c";
	public static final String XML_ATT_MCODE = "m";
	public static final String XML_ATT_DECKSIZE = "ds";

	// values for move codes
	public static final int DECK_TO_SORT = 0;
	public static final int SORT_LEFT_TO_RIGHT = 1;
	public static final int SORT_RIGHT_TO_LEFT = 2;
	public static final int HAND_TO_TRACK = 3;
	public static final int DECK_TO_HAND = 4;
	public static final int DECK_TO_DUAL_RIDER = 5;
	public static final int DUAL_RIDER_TO_HAND = 6;
	public static final int HAND_TO_DISCARD = 7;

	private Vector theCards;
	private int moveCode;
	private int deckSize = -1;

	/**
	 * Constructor when given a vector of cards
	 *
	 * @param username		Username
	 * @param theCards		The vector of cards to move
	 * @param moveCode		The code that indicates the src/dest of the cards moving
	 */
	public CommJumpingMoveCards( String username,
								 Vector theCards,
								 int moveCode)
	{
		super(username);

		this.theCards = theCards;
		this.moveCode = moveCode;
	}

	/**
	 * Constructor when given a single card
	 *
	 * @param username		Username
	 * @param theCard		The card to move
	 * @param moveCode		The code that indicates the src/dest of the card moving
	 */
	public CommJumpingMoveCards( String username,
								 JumpingCard theCard,
								 int moveCode)
	{
		super(username);

		this.theCards = new Vector();
		theCards.add(theCard);

		this.moveCode = moveCode;
	}

	/**
	 * Constructor when given two cards
	 *
	 * @param username		Username
	 * @param firstCard		The first card to move
	 * @param secondCard	The second card to move
	 * @param moveCode		The code that indicates the src/dest of the card moving
	 */
	public CommJumpingMoveCards( String username,
								 JumpingCard firstCard,
								 JumpingCard secondCard,
								 int moveCode)
	{
		super(username);

		this.theCards = new Vector();
		theCards.add(firstCard);
		theCards.add(secondCard);

		this.moveCode = moveCode;
	}

	/**
	 * Constructor when given a number of cards to move
	 *
	 * @param username		Username
	 * @param theCard		The card to move <numCards> times
	 * @param numCards		The number of cards to move
	 * @param moveCode		The code that indicates the src/dest of the card moving
	 */
	public CommJumpingMoveCards( String username,
								 JumpingCard theCard,
								 int numCards,
								 int moveCode)
	{
		super(username);

		this.theCards = new Vector();

		for (int i=0; i<numCards; i++) {
			theCards.add(theCard);
		}

		this.moveCode = moveCode;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message		XML element
	 */
	public CommJumpingMoveCards(XMLElement message) {
		super(message);

		this.theCards = toVector(message.getStringAttribute(XML_ATT_CARD));
		this.moveCode = message.getIntAttribute(XML_ATT_MCODE);
		this.deckSize = message.getIntAttribute(XML_ATT_DECKSIZE);
	}

	/**
	 * Set the current decksize in the message.
	 *
	 * @param deckSize		The size of the deck after the cards are moved.
	 *							Only valid for DECK_TO_SORT, DECK_TO_HAND & DECK_TO_DUAL_RIDER moves
	 */
	public void setDeckSize(int deckSize) {
		this.deckSize = deckSize;
	}

	/**
	 * Return fields of this message.
	 */
	public Vector getCards()	{ return this.theCards;  }
	public int getMoveCode()	{ return this.moveCode; }
	public int getDeckSize()	{ return this.deckSize; }

	/**
	 * Convert a string of card items into a vector of cards
	 *
	 * @param	cardString	A string of space-separated card info
	 * @return a vector of cards from that string
	 */
	private Vector toVector (String cardString) {
		String [] items = JogreUtils.convertToStringArray(cardString);
		Vector newCards = new Vector();

		int i = 0;
		while (i < items.length) {
			newCards.add(JumpingCard.fromString(items[i], items[i+1]));
			i += 2;
		}

		return newCards;
	}

	/**
	 * Convert a vector of cards into a string that represents them
	 *
	 * @param	cards	A vector of cards
	 * @return a string to represent the cards in the vector
	 */
	private String toString(Vector cards) {
		String str = "";

		for (int i=0; i<cards.size(); i++) {
			str = str + " " + ((JumpingCard) cards.get(i)).toString();
		}

		return str;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setAttribute(XML_ATT_CARD, toString(theCards));
		message.setIntAttribute(XML_ATT_MCODE, moveCode);
		message.setIntAttribute(XML_ATT_DECKSIZE, deckSize);

		return message;
	}
}
