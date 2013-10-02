/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter
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

import java.lang.Comparable;

import org.jogre.common.util.JogreUtils;

// Structure to hold a card for Car Tricks
public final class CarTricksCard implements Comparable {

	// Declare constants for colors
	public static final int UNKNOWN = -2;		// Unknown shows a card back
	public static final int EVENT = -1;			// One of the event cards (L, W, D, S)
	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int PURPLE = 4;
	public static final int ORANGE = 5;
	public static final int MAX_COLOR = 6;		// Special color used as the largest possible card.  Used to
												// make sorting in a hand easier

	public static final int MIN_COLOR = -2;

	// Declare constants for values
	public static final int LEADER = 2;				// Leader card
	public static final int WRECK = 3;				// Wreck card
	public static final int DRIVER = 4;				// Driver card
	public static final int SLIPSTREAM = 5;			// Slipstream card

	public static final int MIN_VALUE = 2;
	public static final int MAX_VALUE = 11;

	private final int color;
	private final int value;
	private final boolean visible;

	/**
	 * Constructor for an unknown card
	 */
	public CarTricksCard() {
		color = UNKNOWN;
		value = MIN_VALUE;
		visible = true;
	}

	/**
	 * Constructor for a card given color, value and visibility flag
	 */
	public CarTricksCard(int color, int value, boolean visible) {

		// If values aren't in the valid range, then make the card UNKNOWN
		if ((color < MIN_COLOR) || (color > MAX_COLOR) ||
			(value < MIN_VALUE) || (value > MAX_VALUE)) {
			this.color = UNKNOWN;
		} else {
			this.color = color;
		}

		// Cap the range to be within MIN_VALUE, MAX_VALUE
		if (value < MIN_VALUE) {
			this.value = MIN_VALUE;
		} else if (value > MAX_VALUE) {
			this.value = MAX_VALUE;
		} else {
			this.value = value;
		}

		this.visible = visible;
	}

	/**
	 * Constructor for a visible card given color and value
	 */
	public CarTricksCard(int color, int value) {
		this (color, value, true);
	}

	/**
	 * Constructor for a card given a card
	 */
	public CarTricksCard(CarTricksCard card) {
		this.color = card.cardColor();
		this.value = card.cardValue();
		this.visible = card.isVisible();
	}

	/**
	 * Constructor for a card given a card
	 */
	private CarTricksCard(CarTricksCard card, boolean visible) {
		this.color = card.cardColor();
		this.value = card.cardValue();
		this.visible = visible;
	}

	/**
	 * Static factory for making invisible versions of cards
	 *
	 * @param	theCard		The card to use as the basis for making the invisible card
	 */
	public static CarTricksCard makeInvisibleCard(CarTricksCard theCard) {
		return new CarTricksCard(theCard, false);
	}
	
	/**
	 * Retrieve the color of a card
	 *
	 * @return the color
	 */
	public int cardColor() {
		return (color);
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
	 * Compare two cards for equality.
	 * Note: Visibility does NOT affect equality.  This is to keep hands
	 * correctly sorted, even when cards turn invisible.
	 *
	 * @param	o					The object to compare against
	 * @return	true or false		True if the cards are equal.
	 *								False if the cards are not equal.
	 */
	public boolean equals(Object o) {
		// Make sure this is a CarTricksCard before continuing
		if (!(o instanceof CarTricksCard)) {
			return (false);
		}

		// Compare the fields
		CarTricksCard argCard = (CarTricksCard) o;
		return ((this.color == argCard.cardColor()) &&
				(this.value == argCard.cardValue()));
	}

	/**
	 * If overriding equals, then need to override hashCode as well
	 */
	public int hashCode() {
		return (((this.color-MIN_COLOR) * MAX_VALUE) + this.value);
	}

	/**
	 * Determine if the given card is an Event card or not
	 *
	 * @return	true or false		True if the card is an event.
	 *								False if the card is not an event.
	 */
	public boolean isEvent() {
		return (color == EVENT);
	}

	/**
	 * Determine if the given card is known (is not clear, or unknown)
	 *
	 * @return	true or false		True if the card is known.
	 *								False if the card is not known.
	 */
	public boolean isKnown() {
		return ((visible == true) && (color != UNKNOWN));
	}

	/**
	 * Provide compareTo function so that cards can be sorted.
	 */
	public int compareTo(Object o) {
		CarTricksCard argCard = (CarTricksCard) o;

		// Compare colors first
		int cdiff = (this.color - argCard.cardColor());
		if (cdiff != 0) {
			return (cdiff);
		}

		// if color is same, compare values
		return (this.value - argCard.cardValue());
	}

	private static String [] colorStrings = {"Red", "Blue", "Yellow", "Green", "Purple", "Orange"};
	/**
	 * Override toString to that cards can be printed out nicely & easily
	 */
	public String toString() {

		if (visible) {
			switch (color) {
				case UNKNOWN	:	return "Unknown";
				case EVENT		:
					switch (value) {
						case LEADER 	: return "Leader";
						case WRECK		: return "Wreck";
						case DRIVER		: return "Driver";
						case SLIPSTREAM : return "Slipstream";
						default : return "EVENT." + value;
					}
				default :
					return colorStrings[color] + " " + value;
			}
		} else {
			return "Invisible";
		}

	}

	/**
	 * Parse a string to convert it back into a card.
	 */
	public static CarTricksCard fromString(String str) {

		// Break the parts out of the single string
		String [] parts = JogreUtils.convertToStringArray(str);

		if (parts.length == 1) {
			// Try the single string special cases
			if ("Leader".equals(str))		{return new CarTricksCard(EVENT, LEADER);}
			if ("Wreck".equals(str))		{return new CarTricksCard(EVENT, WRECK);}
			if ("Driver".equals(str))		{return new CarTricksCard(EVENT, DRIVER);}
			if ("Slipstream".equals(str))	{return new CarTricksCard(EVENT, SLIPSTREAM);}
			if ("Invisible".equals(str))	{return new CarTricksCard(UNKNOWN, MIN_VALUE, false);}

			// Don't know what it is, so make it unknown
			return new CarTricksCard();
		} else if (parts.length == 2) {
			// Need to parse the "color value" string

			// Try to parse the value as an integer
			int value;
			try {
				value = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				// Can't parse the value: make an unknown card
				return new CarTricksCard();
			}

			// Try to find the color in the color array
			for (int i = 0; i < colorStrings.length; i++) {
				if (colorStrings[i].equals(parts[0])) {
					// Found a match, can create the card!
					return new CarTricksCard (i, value);
				}
			}
		}

		// Don't know what this is, so make an unknown card
		return new CarTricksCard();
		
	}
}
