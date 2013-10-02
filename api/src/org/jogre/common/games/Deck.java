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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;
import org.jogre.common.comm.CommTableMessage;

/**
 * Deck defines a deck of cards in the game of Spades
 *
 * @author Garrett Lehman
 * @version Alpha 0.2.3
 */
/**
 * @author GarrettL
 *
 */
public class Deck extends CommTableMessage {

	// name for xml
	public static final String XML_NAME = "deck";

	// attribute name for position in deck
	public static final String XML_ATT_POSITION = "position";

	// data structure for cards
	private Vector cards = null;

	// position through the deck as the deck is dealed
	private int position = 0;

	/**
	 * Default constructor
	 */
	public Deck() {
		this.cards = new Vector();
	}

	/**
	 * Contructor that takes a xml message
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public Deck(XMLElement message) {
		// Read card values
		this.position = message.getIntAttribute(XML_ATT_POSITION);

		// Create a new vector for Cards
		this.cards = new Vector();

		// Read child elements - card objects
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement) e.nextElement();

			if (childMessage.getName().equals(Card.XML_NAME)) {
				cards.add(new Card(childMessage));
			}
		}
	}

	/**
	 * Getter for getting all the cards in the deck.
	 *
	 * @return the cards in the deck
	 */
	public Vector getCards() {
		return this.cards;
	}

	/**
	 * Dealing a card removes a card off the top of the deck.
	 *
	 * @return a card from the top of the deck.
	 */
	public synchronized Card deal() {
		this.position++;
		return (Card) this.cards.remove(0);
	}

	/**
	 * Get card from deck by index
	 *
	 * @param index
	 *            Index in deck
	 * @return a card from the specified index
	 */
	public Card get(int index) {
		return (Card) this.cards.get(index);
	}

	/**
	 * Shuffles the deck one time.
	 */
	public synchronized void shuffle() {
		this.position = 0;
		Collections.shuffle(this.cards);
	}

	/**
	 * Gets number of cards left in deck.
	 *
	 * @return number of cards left in deck
	 */
	public int cardsLeft() {
		return this.cards.size() - position;
	}

	/**
	 * Adds card to deck.
	 *
	 * @param card
	 *            Card
	 * @return a boolean if the card was added or not
	 */
	public synchronized boolean addCard(Card card) {
		return this.cards.add(card);
	}

	/**
	 * Gets the number of cards in the deck.
	 *
	 * @return the number of cards in the deck
	 */
	public int size() {
		return this.cards.size();
	}

	/**
	 * Adds the standard 52 cards to the deck. This may be called several times
	 * if a deck needs to have more than 52 cards in the deck.
	 */
	public synchronized void loadStandardDeck() {
		if (this.cards == null)
			this.cards = new Vector();

		cards.add(new Card(Card.TWO, 2, Card.CLUB));
		cards.add(new Card(Card.THREE, 3, Card.CLUB));
		cards.add(new Card(Card.FOUR, 4, Card.CLUB));
		cards.add(new Card(Card.FIVE, 5, Card.CLUB));
		cards.add(new Card(Card.SIX, 6, Card.CLUB));
		cards.add(new Card(Card.SEVEN, 7, Card.CLUB));
		cards.add(new Card(Card.EIGHT, 8, Card.CLUB));
		cards.add(new Card(Card.NINE, 9, Card.CLUB));
		cards.add(new Card(Card.TEN, 10, Card.CLUB));
		cards.add(new Card(Card.JACK, 10, Card.CLUB));
		cards.add(new Card(Card.QUEEN, 10, Card.CLUB));
		cards.add(new Card(Card.KING, 10, Card.CLUB));
		cards.add(new Card(Card.ACE, 11, Card.CLUB));

		cards.add(new Card(Card.TWO, 2, Card.SPADE));
		cards.add(new Card(Card.THREE, 3, Card.SPADE));
		cards.add(new Card(Card.FOUR, 4, Card.SPADE));
		cards.add(new Card(Card.FIVE, 5, Card.SPADE));
		cards.add(new Card(Card.SIX, 6, Card.SPADE));
		cards.add(new Card(Card.SEVEN, 7, Card.SPADE));
		cards.add(new Card(Card.EIGHT, 8, Card.SPADE));
		cards.add(new Card(Card.NINE, 9, Card.SPADE));
		cards.add(new Card(Card.TEN, 10, Card.SPADE));
		cards.add(new Card(Card.JACK, 10, Card.SPADE));
		cards.add(new Card(Card.QUEEN, 10, Card.SPADE));
		cards.add(new Card(Card.KING, 10, Card.SPADE));
		cards.add(new Card(Card.ACE, 11, Card.SPADE));

		cards.add(new Card(Card.TWO, 2, Card.DIAMOND));
		cards.add(new Card(Card.THREE, 3, Card.DIAMOND));
		cards.add(new Card(Card.FOUR, 4, Card.DIAMOND));
		cards.add(new Card(Card.FIVE, 5, Card.DIAMOND));
		cards.add(new Card(Card.SIX, 6, Card.DIAMOND));
		cards.add(new Card(Card.SEVEN, 7, Card.DIAMOND));
		cards.add(new Card(Card.EIGHT, 8, Card.DIAMOND));
		cards.add(new Card(Card.NINE, 9, Card.DIAMOND));
		cards.add(new Card(Card.TEN, 10, Card.DIAMOND));
		cards.add(new Card(Card.JACK, 10, Card.DIAMOND));
		cards.add(new Card(Card.QUEEN, 10, Card.DIAMOND));
		cards.add(new Card(Card.KING, 10, Card.DIAMOND));
		cards.add(new Card(Card.ACE, 11, Card.DIAMOND));

		cards.add(new Card(Card.TWO, 2, Card.HEART));
		cards.add(new Card(Card.THREE, 3, Card.HEART));
		cards.add(new Card(Card.FOUR, 4, Card.HEART));
		cards.add(new Card(Card.FIVE, 5, Card.HEART));
		cards.add(new Card(Card.SIX, 6, Card.HEART));
		cards.add(new Card(Card.SEVEN, 7, Card.HEART));
		cards.add(new Card(Card.EIGHT, 8, Card.HEART));
		cards.add(new Card(Card.NINE, 9, Card.HEART));
		cards.add(new Card(Card.TEN, 10, Card.HEART));
		cards.add(new Card(Card.JACK, 10, Card.HEART));
		cards.add(new Card(Card.QUEEN, 10, Card.HEART));
		cards.add(new Card(Card.KING, 10, Card.HEART));
		cards.add(new Card(Card.ACE, 11, Card.HEART));
	}

	/**
	 * Sorts the deck by suit, then by card order value. H, C, D, S then 2, 3, 4
	 * ... Q, K, A.
	 */
	public synchronized void sort() {
		Collections.sort(this.cards, new Comparator() {
			public int compare(Object o1, Object o2) {
				Card card1 = (Card) o1;
				Card card2 = (Card) o2;

				int card1Value = getCardValue(card1);
				int card2Value = getCardValue(card2);
				if (card1Value > card2Value)
					return 1;
				else if (card1Value < card2Value)
					return -1;
				else
					return 0;
			}

			public int getCardValue(Card card) {
				int value = 0;
				String cardValue = card.getCard();
				if (Card.JACK.equals(cardValue))
					value += 11;
				else if (Card.QUEEN.equals(cardValue))
					value += 12;
				else if (Card.KING.equals(cardValue))
					value += 13;
				else if (Card.ACE.equals(cardValue))
					value += 14;
				else
					value += card.getValue();

				char cardSuit = card.getSuit();
				if (Card.CLUB == cardSuit)
					value += 15;
				else if (Card.DIAMOND == cardSuit)
					value += 30;
				else if (Card.SPADE == cardSuit)
					value += 45;

				return value;
			}
		});
	}

	/**
	 * A string representation of the deck of cards. For debug purposes only.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.cards == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.cards.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(((Card) this.cards.get(i)).toString());
		}
		return sb.toString();
	}

	/**
	 * Flatten this object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_POSITION, this.position);

		Card card = null;
		int length = this.cards.size();
		for (int i = 0; i < length; i++) {
			card = (Card) this.cards.get(i);
			message.addChild(card.flatten());
		}

		return message;
	}
}