/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.std;

import java.lang.Comparable;

import org.jogre.common.util.JogreUtils;

// Structure to hold a card
public final class Card implements Comparable {

	// Declare constants for Suits
	public static final int UNIQUE_SUIT = -2;	// Suit for guaranteed unique cards
	public static final int UNKNOWN = -1;		// Unknown shows a card back
	public static final int DIAMONDS = 0;
	public static final int SPADES = 1;
	public static final int HEARTS = 2;
	public static final int CLUBS = 3;

	private final int suit;
	private final int value;
	private final boolean visible;

	/**
	 * Constructor for a card given suit, value and visibility flag
	 */
	public Card(int suit, int value, boolean visible) {
		// If the suit isn't valid, then make unknown
		if ((suit > CLUBS) | (suit < UNIQUE_SUIT)) {
			this.suit = UNKNOWN;
		} else {
			this.suit = suit;
		}

		// If the value is less than 0, then make 0
		if (value < 0) {
			this.value = 0;
		} else {
			this.value = value;
		}

		this.visible = visible;
	}

	/**
	 * Constructor for an unknown card
	 */
	public Card() {
		this (UNKNOWN, 0, true);
	}

	/**
	 * Constructor for a visible card given suit and value
	 */
	public Card(int suit, int value) {
		this (suit, value, true);
	}

	/**
	 * Constructor for a card given a card
	 */
	public Card(Card card) {
		this(card.suit, card.value, card.visible);
	}

	/**
	 * Constructor for a card given a card, but force visibility
	 */
	private Card(Card card, boolean visible) {
		this(card.suit, card.value, visible);
	}

	/**
	 * Static factories for making visible & invisible versions of cards
	 *
	 * @param	theCard		The card to use as the basis for making the visible/invisible card
	 */
	public static Card makeVisibleCard(Card theCard) {
		return new Card(theCard, true);
	}
	public static Card makeInvisibleCard(Card theCard) {
		return new Card(theCard, false);
	}

	/**
	 * Private constructor for making unique cards.
	 */
	private Card (int suit) {
		this.suit = suit;
		this.value = 0;
		this.visible = false;
	}

	/**
	 * Static factory for making unique cards
	 */
	public static Card makeUniqueCard() {
		return new Card(UNIQUE_SUIT);
	}

	/**
	 * Retrieve the suit of a card
	 *
	 * @return the suit
	 */
	public int cardSuit() {
		return (suit);
	}

	/**
	 * Retrieve the value of a card
	 *
	 * @return the value
	 */
	public int cardValue() {
		return (value);
	}

	/**
	 * Determine if the given card is visible or not
	 *
	 * @return true or false
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Determine if the given card is known (is not clear, or unknown)
	 *
	 * @return	true or false		True if the card is known.
	 *								False if the card is not known.
	 */
	public boolean isKnown() {
		return ((visible == true) && (suit != UNKNOWN));
	}

	/**
	 * Compare two cards for equality.
	 * Note: Visibility does NOT affect equality.  This is to keep hands
	 * correctly sorted, even when cards turn invisible.
	 *
	 * @param	o					The object to compare against
	 * @return	true or false		True if the cards are equal.
	 *								False if the cards are not equal.
	 */
	public boolean equals(Object o) {
		// Make sure this is an Card before continuing
		if (!(o instanceof Card)) {
			return (false);
		}

		// Compare the fields
		Card argCard = (Card) o;
		if ((this.suit == UNIQUE_SUIT) || (argCard.suit == UNIQUE_SUIT)) {
			return false;
		}

		return ((this.suit == argCard.cardSuit()) &&
				(this.value == argCard.cardValue()));
	}

	/**
	 * If overriding equals, then need to override hashCode as well
	 */
	public int hashCode() {
		return ((this.suit * 1000) + this.value);
	}

	/**
	 * Provide compareTo function so that cards can be sorted.
	 */
	public int compareTo(Object o) {
		Card argCard = (Card) o;

		// Compare suits first
		int cdiff = (this.suit - argCard.cardSuit());
		if (cdiff != 0) {
			return (cdiff);
		}

		// if suit is same, compare values
		return (this.value - argCard.cardValue());
	}

	private static String [] suitStrings = {"Diamonds", "Spades", "Hearts", "Clubs"};

	/**
	 * Override toString to that cards can be printed out nicely & easily
	 */
	public String toString() {

		if (visible) {
			if ((suit == UNKNOWN) || (suit == UNIQUE_SUIT)) {
				return "Unknown";
			} else {
				return suitStrings[suit] + " " + value;
			}
		} else {
			return "Invisible";
		}

	}

	/**
	 * Parse a string to convert it back into a card.
	 */
	public static Card fromString(String str) {

		// Break the parts out of the single string
		String [] parts = JogreUtils.convertToStringArray(str);

		if (parts.length == 1) {
			// Try the single string special cases
			if ("Invisible".equals(str))	{return new Card(UNKNOWN, 0, false);}
		} else if (parts.length == 2) {
			// Need to parse the "suit value" string

			// Try to parse the value as an integer
			int value;
			try {
				value = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				// Can't parse the value: make an unknown card
				return new Card();
			}

			// Try to find the suit in the suit array
			for (int i = 0; i < suitStrings.length; i++) {
				if (suitStrings[i].equals(parts[0])) {
					// Found a match, can create the card!
					return new Card (i, value);
				}
			}
		}

		// Don't know what this is, so make an unknown card
		return new Card();

	}
}
