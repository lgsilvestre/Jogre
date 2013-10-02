/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.common.games;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;
import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreLogger;
import org.jogre.common.util.JogreUtils;

/**
 * Card defines a generic playing card that is transmittable
 *
 * @author Garrett Lehman
 * @version Alpha 0.2.3
 */
public class Card extends CommTableMessage implements Comparable {

	// Card dimensions
	public final static int CARD_PIXEL_HEIGHT = 123;
	public final static int CARD_PIXEL_WIDTH = 79;
	public final static int CARD_SPACING = 15;

	// Card images
	private final static String[] cardImageOrder = { "a", "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k" };
	public static Image cardImages = null;

	// xml names and attributes
	public static final String XML_NAME = "card";
	public static final String XML_ATT_CARD_SUIT = "suit";
	public static final String XML_ATT_CARD_STRING_VALUE = "stringValue";
	public static final String XML_ATT_CARD_INT_VALUE = "intValue";
	public static final String XML_ATT_CARD_FACE_UP = "faceUp";

	// clubs, spades, diamonds, hearts
	public final static char CLUB = 'c';
	public final static char SPADE = 's';
	public final static char DIAMOND = 'd';
	public final static char HEART = 'h';

	// cards - they are strings because of "10"
	public final static String TWO = "2";
	public final static String THREE = "3";
	public final static String FOUR = "4";
	public final static String FIVE = "5";
	public final static String SIX = "6";
	public final static String SEVEN = "7";
	public final static String EIGHT = "8";
	public final static String NINE = "9";
	public final static String TEN = "10";
	public final static String JACK = "j";
	public final static String QUEEN = "q";
	public final static String KING = "k";
	public final static String ACE = "a";

	// order arrays for sorting
	public final static String[] cardStringValues = { "2", "3", "4", "5", "6",
			"7", "8", "9", "10", "j", "q", "k", "a" };
	public final static int[] cardIntValues = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 10,
			10, 10, 11 };
	public final static int[] cardIntOrderValues = { 2, 3, 4, 5, 6, 7, 8, 9,
			10, 11, 12, 13, 14 };

	// String value of the card
	private String card = null;

	// int value of the card
	private int value = 0;

	// String value of the suit
	private char suit = (char) 0;

	// flag for if the card is face up or face down
	private boolean faceUp = true;

	// logger for debug purposes
	private JogreLogger logger = new JogreLogger(this.getClass());

	/**
	 * Default constructor
	 */
	public Card() {
	}

	/**
	 * Constructor that takes card value. Card is defaulted to face down, until
	 * flipped over.
	 *
	 * @param card
	 *            String value of card.
	 * @param value
	 *            Integer value of card
	 * @param suit
	 *            String value of suit of card.
	 */
	public Card(String card, int value, char suit) {
		this.card = card;
		this.value = value;
		this.suit = suit;
	}

	/**
	 * Contructor that takes an xml message
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public Card(XMLElement message) {
		String suitString = message.getStringAttribute(XML_ATT_CARD_SUIT);
		this.suit = suitString.charAt(0);
		this.card = message.getStringAttribute(XML_ATT_CARD_STRING_VALUE);
		this.value = message.getIntAttribute(XML_ATT_CARD_INT_VALUE);
		this.faceUp = message.getBooleanAttribute(XML_ATT_CARD_FACE_UP, "true",
				"false", false);
	}

	/**
	 * Getter for card string value.
	 *
	 * @return Returns the card string value.
	 */
	public String getCard() {
		return this.card;
	}

	/**
	 * Setter for card string value.
	 *
	 * @param card
	 *            The card string value to set.
	 */
	public void setCard(String card) {
		this.card = card;
	}

	/**
	 * Getter for if the card is face up.
	 *
	 * @return Returns if the card is face up.
	 */
	public boolean isFaceUp() {
		return this.faceUp;
	}

	/**
	 * Setter for if the card is face up or down.
	 *
	 * @param faceUp
	 *            Sets whether or not the card is face up or not.
	 */
	public void setFaceUp(boolean faceUp) {
		this.faceUp = faceUp;
	}

	/**
	 * Getter for the string value of the card suit.
	 *
	 * @return Returns the suit.
	 */
	public char getSuit() {
		return this.suit;
	}

	/**
	 * Setter for the string value of the card suit.
	 *
	 * @param suit
	 *            The suit to set.
	 */
	public void setSuit(char suit) {
		this.suit = suit;
	}

	/**
	 * Getter for the integer value of the card.
	 *
	 * @return Returns the value.
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Setter for the integer value of the card.
	 *
	 * @param value
	 *            The value to set.
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Get order value of card. This is usually used for sorting cards,
	 * especially face cards where all face card values are 10 but a king is
	 * greater than a queen. This is where order value comes in. A king will
	 * return a higher order value than a queen.
	 *
	 * @return order value of card
	 */
	public int getOrderValue() {
		int size = Card.cardStringValues.length;
		for (int i = 0; i < size; i++) {
			if (Card.cardStringValues[i].equals(this.card))
				return Card.cardIntOrderValues[i];
		}
		return -1;
	}

	/**
	 * Compares two Card objects in the game of spades.
	 *
	 * @param obj
	 *            The Card object to compare to.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int spadesCompareTo(Card card) {
		logger.debug("spadesCompareTo", "card1: " + this.toString()
				+ ", card2: " + card.toString());

		if (this.suit != card.getSuit()) {
			if (this.suit == Card.SPADE)
				return 1;
			else if (this.suit == Card.SPADE)
				return -1;
		}
		int card1Order = this.getOrderValue();
		int card2Order = card.getOrderValue();

		logger.debug("spadesCompareTo", "card1 order: " + card1Order
				+ ", card2: " + card2Order);

		if (card1Order > card2Order)
			return 1;
		else if (card1Order < card2Order)
			return -1;
		return 0;
	}

	/**
	 * Compares two Card objects based on card value. Here, a king will be equal
	 * to a queen.
	 *
	 * @param obj
	 *            The Card object to compare to.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int compareTo(Object obj) {
		Card card = (Card) obj;
		if (this.value > card.value)
			return 1;
		else if (this.value < card.value)
			return 1;
		else
			return 0;
	}

	/**
	 * Tells whether two cards are equal or not.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		try {
			Card card = (Card) obj;
			return (card.getCard().equals(this.card)
					&& card.getValue() == this.value && card.getSuit() == this.suit);

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Outputs the Card object in a string format
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.suit + this.card;
	}

	/**
	 * Flatten this object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setAttribute(XML_ATT_CARD_SUIT, String.valueOf(this.suit));
		message.setAttribute(XML_ATT_CARD_STRING_VALUE, this.card);
		message.setIntAttribute(XML_ATT_CARD_INT_VALUE, this.value);
		if (this.faceUp)
			message.setAttribute(XML_ATT_CARD_FACE_UP, "true");
		else
			message.setAttribute(XML_ATT_CARD_FACE_UP, "false");

		return message;
	}

	public void paintComponent(Graphics g, int x, int y) {
		this.paintComponent(g, x, y, false);
	}

	public void paintComponent(Graphics g, int x, int y, boolean horizontal) {
		if (cardImages == null) {
			if (JogreUtils.isApplet()) {
				cardImages = new ImageIcon(getClass().getResource("/images/cards.png")).getImage();
			} else {
				cardImages = new ImageIcon("images" + java.io.File.separator + "cards.png").getImage();
			}
		}

		if (horizontal)
			this.drawCardHorizontal(g, x, y);
		else
			this.drawCardVertical(g, x, y);
	}

	private void drawCardVertical(Graphics g, int dx1, int dy1) {
		int row = -1;
		int col = -1;

		// get row
        switch (this.getSuit()) {
	        case Card.CLUB:		row = 0;  break;
	        case Card.DIAMOND:	row = 1;  break;
	        case Card.HEART:	row = 2;  break;
	        case Card.SPADE:	row = 3;  break;
        }
        if (row == -1)
        	return;

		// get col
		int length = cardImageOrder.length;
		for (int i = 0; i < length; i++) {
			if (cardImageOrder[i].equals(this.getCard())) {
				col = i;
				break;
			}
		}
		if (col == -1)
			return;

		int dx2 = dx1 + CARD_PIXEL_WIDTH;
		int dy2 = dy1 + CARD_PIXEL_HEIGHT;
		int sx1 = CARD_PIXEL_WIDTH * col;
        int sy1 = CARD_PIXEL_HEIGHT * row;
		int sx2 = sx1 + CARD_PIXEL_WIDTH;
		int sy2 = sy1 + CARD_PIXEL_HEIGHT;
		g.drawImage(cardImages, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	private void drawCardHorizontal(Graphics g, int dx1, int dy1) {
		int row = -1;
		int col = -1;

		// get col
        switch (this.getSuit()) {
	        case Card.CLUB:		col = 0;  break;
	        case Card.DIAMOND:	col = 1;  break;
	        case Card.HEART:	col = 2;  break;
	        case Card.SPADE:	col = 3;  break;
        }
        if (col == -1)
        	return;

		// get row
		int length = cardImageOrder.length;
		for (int i = 0; i < length; i++) {
			if (cardImageOrder[i].equals(this.getCard())) {
				row = (cardImageOrder.length - i - 1);
				break;
			}
		}
		if (row == -1)
			return;

		Graphics2D g2d = (Graphics2D) g.create(dx1, dy1, CARD_PIXEL_HEIGHT, CARD_PIXEL_WIDTH);
		g2d.rotate(Math.toRadians(-90), 0, 0);
		g2d.translate(-cardImages.getWidth(null) + (CARD_PIXEL_WIDTH * row), (CARD_PIXEL_HEIGHT * -col));
		g2d.drawImage(cardImages, 0, 0, null);
	}
}