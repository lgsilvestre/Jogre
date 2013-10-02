/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter
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

import java.lang.Comparable;

import org.jogre.common.util.JogreUtils;

/**
 * Structure to hold a card for Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public final class JumpingCard implements Comparable {

	// Declare constants for Types
	public static final int UNKNOWN = -1;		// Unknown shows a card back
	public static final int CANTER = 0;
	public static final int HEIGHT = 1;
	public static final int LENGTH = 2;
	public static final int SPECIAL = 3;
	public static final int IMMEDIATE = 4;

	public static final int MAX_TYPE = 4;
	public static final int MIN_TYPE = -1;

	// Declare constants for SPECIAL card values
	public static final int RIBBON = 1;
	public static final int SADDLE = 2;
	public static final int FAKE_SADDLE = 3;

	// Declare constants for IMMEDIATE card values
	public static final int FAULT_1_4 = 1;
	public static final int FAULT_2_4 = 2;
	public static final int FAULT_3_4 = 3;
	public static final int STABLE = 4;
	public static final int DUAL_RIDER = 5;
	public static final int OFFICIAL = 6;

	public static final int MIN_VALUE = 1;
	public static final int MAX_VALUE = 6;

	// Features of the card
	private final int type;
	private final int value;
	private final boolean visible;

	// Flags to mark properties of the card
	private boolean playable = false;
	private boolean marked = false;
	private boolean added = false;

	// Spaces that this card is associated with
	private int visualSpace = -1;
	private int effectiveSpace = -1;

	/**
	 * Constructor for an unknown card
	 */
	public JumpingCard() {
		type = UNKNOWN;
		value = MIN_VALUE;
		visible = true;
	}

	/**
	 * Constructor for a card given a type, value & visibility
	 */
	public JumpingCard(int type, int value, boolean visible) {

		// If values aren't in the valid range, then make the card UNKNOWN
		if ((type < MIN_TYPE) || (type > MAX_TYPE) ||
			(value < MIN_VALUE) || (value > MAX_VALUE)) {
			this.type = UNKNOWN;
		} else {
			this.type = type;
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
	 * Constructor for a visible card given type and value
	 */
	public JumpingCard(int type, int value) {
		this (type, value, true);
	}

	/**
	 * Constructor for a card given a card
	 */
	public JumpingCard(JumpingCard card) {
		this.type = card.type;
		this.value = card.value;
		this.visible = card.visible;
		this.playable = card.playable;
		this.marked = card.marked;
		this.added = card.added;
		this.visualSpace = card.visualSpace;
		this.effectiveSpace = card.effectiveSpace;
	}

	/**
	 * Constructor for a card given a card
	 */
	private JumpingCard(JumpingCard card, boolean visible) {
		this.type = card.type;
		this.value = card.value;
		this.visible = visible;
		this.playable = card.playable;
		this.marked = card.marked;
		this.added = card.added;
		this.visualSpace = card.visualSpace;
		this.effectiveSpace = card.effectiveSpace;
	}

	/**
	 * Static factory for making invisible versions of cards
	 *
	 * @param	theCard		The card to use as the basis for making the invisible card
	 */
	public static JumpingCard makeInvisibleCard(JumpingCard theCard) {
		return new JumpingCard(theCard, false);
	}

	/**
	 * Static factory for making an invisible card
	 */
	public static JumpingCard makeInvisibleCard() {
		return new JumpingCard(UNKNOWN, MIN_VALUE, false);
	}

	/**
	 * Static factory for making various cards
	 */
	public static JumpingCard makeRibbonCard()    { return new JumpingCard(SPECIAL, RIBBON, true); }
	public static JumpingCard makeOfficialCard()  { return new JumpingCard(IMMEDIATE, OFFICIAL, true); }
	public static JumpingCard makeDualRiderCard() { return new JumpingCard(IMMEDIATE, DUAL_RIDER, true); }
	public static JumpingCard makeFakeSaddle()    { return new JumpingCard(SPECIAL, FAKE_SADDLE, true); }

	/**
	 * Retrieve the type of a card
	 *
	 * @return the type
	 */
	public int cardType() {
		return (type);
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
	 * Determine properties of the card
	 */
	public boolean isPlayable()    { return playable; }
	public boolean isMarked()      { return marked; }
	public boolean isAdded()       { return added; }
	public int getVisualSpace()    { return visualSpace; }
	public int getEffectiveSpace() { return effectiveSpace; }

	/**
	 * Set properties of the card
	 */
	public void setPlayable       (boolean value) { playable = value; }
	public void setMarked         (boolean value) { marked = value; }
	public void setAdded          (boolean value) { added = value; }
	public void setVisualSpace    (int value)     { visualSpace = value; }
	public void setEffectiveSpace (int value)     { effectiveSpace = value; }

	public void invertMark() { marked = !marked; }

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
		// Make sure this is a JumpingCard before continuing
		if (!(o instanceof JumpingCard)) {
			return (false);
		}

		// Compare the fields
		JumpingCard argCard = (JumpingCard) o;
		return ((this.type == argCard.type) &&
				(this.value == argCard.value));
	}

	/**
	 * If overriding equals, then need to override hashCode as well
	 */
	public int hashCode() {
		return (((this.type-MIN_TYPE) * MAX_VALUE) + this.value);
	}

	/**
	 * Determine if the given card is of a certain type or not
	 *
	 * @return	true or false		True if the card is of that type.
	 *								False if the card is not of that type.
	 */
	public boolean isCanter()     { return (type == CANTER); }
	public boolean isHeight()     { return (type == HEIGHT); }
	public boolean isLength()     { return (type == LENGTH); }
	public boolean isSpecial()    { return (type == SPECIAL); }
	public boolean isImmediate()  { return (type == IMMEDIATE); }
	public boolean isSaddle()     { return ((type == SPECIAL) && (value == SADDLE)); }
	public boolean isFakeSaddle() { return ((type == SPECIAL) && (value == FAKE_SADDLE)); }
	public boolean isRibbon()     { return ((type == SPECIAL) && (value == RIBBON)); }
	public boolean isOfficial()   { return ((type == IMMEDIATE) && (value == OFFICIAL)); }
	public boolean isDualRider()  { return ((type == IMMEDIATE) && (value == DUAL_RIDER)); }
	public boolean isStable()     { return ((type == IMMEDIATE) && (value == STABLE)); }

	/**
	 * Determine if the given card is known (is not clear, or unknown)
	 *
	 * @return	true or false		True if the card is known.
	 *								False if the card is not known.
	 */
	public boolean isKnown() {
		return ((visible == true) && (type != UNKNOWN));
	}

	/**
	 * Provide compareTo function so that cards can be sorted.
	 */
	public int compareTo(Object o) {
		JumpingCard argCard = (JumpingCard) o;

		// Compare types first
		int tdiff = (this.type - argCard.type);
		if (tdiff != 0) {
			return (tdiff);
		}

		// if type is same, compare values
		return (this.value - argCard.value);
	}


	// Strings for encoding the type
	private static String [] typeStrings = {"?", "C", "H", "L", "S", "I"};

	/**
	 * Override toString to that cards can be printed out nicely & easily
	 */
	public String toString() {
		if (visible) {
			return typeStrings[type+1] + " " + value;
		} else {
			return "- 0";
		}

	}

	/**
	 * Parse two strings to convert it back into a card.
	 *
	 * @param typeStr		The string representation for the type.
	 * @param valueStr		The string representation for the value.
	 * @return the card
	 */
	public static JumpingCard fromString(String typeStr, String valueStr) {
		int value;

		if ("-".equals(typeStr)) { return new JumpingCard(UNKNOWN, MIN_VALUE, false); }

		try {
			value = Integer.parseInt(valueStr);
		} catch (NumberFormatException e) {
			// Can't parse the value: make an unknown card
			return new JumpingCard();
		}

		// Try to find the type in the type array
		for (int i = 0; i < typeStrings.length; i++) {
			if (typeStrings[i].equals(typeStr)) {
				// Found a match, can create the card!
				return new JumpingCard (i-1, value);
			}
		}

		// Don't know what this is, so make an unknown card
		return new JumpingCard();

	}

	/**
	 * Parse a string to convert it back into a card.
	 *
	 * @param cardStr		The string representation of a card.
	 * @return the card.
	 */
	public static JumpingCard fromString(String cardStr) {
		// Break the string into it's two pieces.
		String [] pair = JogreUtils.convertToStringArray(cardStr);

		if (pair.length == 2) {
			// Convert the pieces into a card.
			return fromString(pair[0], pair[1]);
		} else {
			// We don't have just 2 pieces, so return an unknown card.
			return new JumpingCard();
		}
	}

	/**
	 * Return a properties keyname for displaying the name of this card.
	 */
	public String getPropertiesKeyname() {
		if (type == CANTER) return "cardname.canter" + value;
		if (type == HEIGHT) return "cardname.height" + value;
		if (type == LENGTH) return "cardname.length" + value;
		if (type == SPECIAL) {
			if (value == RIBBON) return "cardname.ribbon";
			if (value == SADDLE) return "cardname.saddle";
		}
		if (type == IMMEDIATE) {
			if (value == FAULT_1_4) return "cardname.fault1";
			if (value == FAULT_2_4) return "cardname.fault2";
			if (value == FAULT_3_4) return "cardname.fault3";
			if (value == STABLE) return "cardname.stable";
			if (value == DUAL_RIDER) return "cardname.dualrider";
			if (value == OFFICIAL) return "cardname.official";
		}
		return "cardname.unknown";
	}

}
