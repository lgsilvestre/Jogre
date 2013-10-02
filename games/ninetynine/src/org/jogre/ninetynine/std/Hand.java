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
package org.jogre.ninetynine.std;

import java.util.Collections;
import java.util.Vector;
import java.util.Observable;

// Structure to hold a hand of cards
public class Hand extends Observable {

	// The array of cards
	private Vector cards;

	// Parameters for the vector created.  Most games will have an hand size
	// of 13 cards, so that is what the initial capacity is set to.
	private static final int INIT_CAPACITY = 13;
	private static final int CAP_INCREMENT = 5;

	// Keep track of the # of cards of each suit in the hand
	private int [] suitCount;
	private int visibleCount = 0;
	private boolean cardsCounted = false;

	// Decide if I should pass up changed alerts or not.
	private boolean frozen = false;

	/**
	 * Constructor for an empty hand
	 */
	public Hand() {
		cards = new Vector(INIT_CAPACITY, CAP_INCREMENT);
	}

	/**
	 * Constructor for a hand given an array of cards
	 *
	 * @param	theCards	The array of cards for the hand.
	 */
	public Hand(Card [] theCards) {
		cards = new Vector(theCards.length, CAP_INCREMENT);

		for (int i=0; i<theCards.length; i++) {
			cards.addElement(theCards[i]);
		}
	}		

	/**
	 * Constructor for a hand given an array of suits & values for the cards
	 *
	 * @param	suits		An array of suits
	 * @param	values		An array of values
	 */
	public Hand(int [] suits, int [] values) {
		cards = new Vector(suits.length, CAP_INCREMENT);

		for (int i=0; i<suits.length; i++) {
			cards.addElement(new Card(suits[i], values[i]));
		}
	}

	/**
	 * Constructor for a hand given a two-dimensional array where the
	 * first entry is the suits and the second is the values.
	 *
	 * @param	suitsAndValues	The array of suits & values
	 */
	public Hand(int [][] suitsAndValues) {
		this (suitsAndValues[0], suitsAndValues[1]);
	}

	/**
	 * Constructor for a hand given an existing hand
	 *
	 * @param	oldHand		The old hand to copy
	 */
	public Hand(Hand oldHand) {
		int size = oldHand.cards.size();
		cards = new Vector(size);

		for (int i=0; i<size; i++) {
			cards.addElement(oldHand.cards.get(i));
		}
	}

	/**
	 * Signal that this hand has changed.
	 */
	private void signalChanged() {
		setChanged();
		if (!frozen) {
			notifyObservers();
		}
	}

	/**
	 * Freeze notifications for this hand.
	 */
	public void freeze() {
		frozen = true;
	}

	/**
	 * Un-freeze notifications for this hand.
	 */
	public void thaw() {
		frozen = false;
		notifyObservers();
	}

	/**
	 * Set the hand to the given array of cards.
	 *
	 * @param	theCards	The array of cards for the hand.
	 */
	public void setCards(Card [] theCards) {
		cards.clear();
		cardsCounted = false;

		for (int i=0; i<theCards.length; i++) {
			cards.addElement(theCards[i]);
		}

		signalChanged();
	}

	/**
	 * Set the hand to the same array of cards as a given hand.
	 *
	 * @param	givenHand	The hand whose cards we are to copy.
	 */
	public void setHand(Hand givenHand) {
		Vector givenCards = givenHand.cards;
		cards.clear();
		cardsCounted = false;

		for (int i=0; i<givenCards.size(); i++) {
			cards.addElement(givenCards.get(i));
		}

		signalChanged();
	}

	/**
	 * Add a card to a hand.
	 *
	 * @param	newCard			The card to add to the front of the hand
	 */
	public int addCard(Card newCard) {
		cards.add(0, newCard);
		cardsCounted = false;

		signalChanged();

		return cards.size();
	}

	/**
	 * Add a card to the end of a hand.
	 *
	 * @param	newCard			The card to add to the end of the hand
	 *
	 * @return the new length of the hand
	 */
	public int appendCard(Card newCard) {
		cards.add(newCard);
		cardsCounted = false;

		signalChanged();

		return cards.size();
	}

	/**
	 * Remove a card from the hand.
	 *
	 * @param	oldCard			The card to remove from the hand
	 *
	 * @return the new length of the hand
	 */
	public int removeCard(Card oldCard) {
		cards.remove(oldCard);
		cardsCounted = false;

		signalChanged();

		return cards.size();
	}

	/**
	 * Remove the last card from the hand, regardless of what it is.
	 *
	 * @return the new length of the hand
	 */
	public int removeLastCard() {
		int n = cards.size() - 1;
		cards.removeElementAt(n);
		cardsCounted = false;

		signalChanged();

		return n;
	}

	/**
	 * Remove all of the cards from the hand.
	 */
	public void empty() {
		cards.clear();
		cardsCounted = false;

		signalChanged();
	}

	/**
	 * Sort the hand.
	 */
	public void sort() {
		Collections.sort(cards);

		signalChanged();
	}

	/**
	 * Return the number of cards in the hand.
	 *
	 * @return the number of the cards in the hand.
	 */
	 public int length() {
	 	return cards.size();
	 }

	/**
	 * Return the card at the Nth position. 0 = First card.
	 *
	 * @param	n		The position to return.
	 *
	 * @return the card at the Nth position.
	 */
	public Card getNthCard(int n) {
		try {
			return (Card) cards.get(n);
		} catch (ArrayIndexOutOfBoundsException e) {
			// Return an unknown card
			return new Card();
		}
	}

	/**
	 * Change the Nth card to the one provided.
	 *
	 * @param	n			The position to set the card
	 * @param	theCard		The card to place at that position
	 */
	public void setNthCard(int n, Card theCard) {
		cardsCounted = false;
		try {
			cards.set(n, theCard);

			signalChanged();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Return the index of the given card in the hand
	 */
	public int getIndexOf(Card theCard) {
		return cards.indexOf(theCard);
	}

	/*
	 * Count the # of visible cards and the number of visible cards
	 * of each suit in the hand.
	 */
	private void countCards() {
		if (!cardsCounted) {
			suitCount = new int [] {0, 0, 0, 0, 0};
			visibleCount = 0;
			for (int i=0; i<cards.size(); i++) {
				Card theCard = (Card) cards.get(i);
				if (theCard.isVisible()) {
					suitCount[theCard.cardSuit() + 1] += 1;
					visibleCount += 1;
				}
			}
			cardsCounted = true;
		}
	}

	/**
	 * Return the number of cards of a given suit that the hand
	 * has in it.
	 */
	public int getNumCardsOfSuit(int theSuit) {
		countCards();
		return suitCount[theSuit+1];
	}

	/**
	 * Set the given card to invisible
	 *
	 * @param	theCard		The card to make invisible
	 *
	 * @return true => Card was made invisible.
	 *         false => Card wasn't found in the hand
	 */
	public boolean invisiblizeCard(Card theCard) {
		int index = cards.indexOf(theCard);
		if (index >= 0) {
			setNthCard(index, Card.makeInvisibleCard(theCard));
		}
		return (index >= 0);
	}

	/**
	 * Set the given card to visible
	 *
	 * @param	theCard		The card to make visible
	 *
	 * @return true => Card was made visible.
	 *         false => Card wasn't found in the hand
	 */
	public boolean visiblizeCard(Card theCard) {
		int index = cards.indexOf(theCard);
		if (index >= 0) {
			setNthCard(index, Card.makeVisibleCard(theCard));
		}
		return (index >= 0);
	}

	/**
	 * Determine if there are any visible cards still in the hand or not.
	 *
	 * @return	true => hand has no more visible cards
	 */
	public boolean isEmpty() {
		countCards();
		return (visibleCount == 0);
	}

	/**
	 * Determine if the hand has one last visible card still in it.
	 *
	 * @return	true => hand has exactly 1 more visible card
	 */
	public boolean isOnLastCard() {
		countCards();
		return (visibleCount == 1);
	}


	/**
	 * Override toString to that hands can be printed out nicely & easily
	 */
	public String toString() {
		String output = "(" + cards.size() + ") ";
		if (cards.size() > 0) {
			for (int i=0; i < cards.size(); i++) {
				output = output + getNthCard(i).toString() + ", ";
			}
		}
		return output;
	}

	/**
	 * Return an array of suits of the cards in the hand
	 */
	public int [] extractSuits() {
		int [] suits = new int [cards.size()];

		if (cards.size() != 0) {
			for (int i=0; i<cards.size(); i++) {
				suits[i] = getNthCard(i).cardSuit();
			}
		}

		return suits;
	}

	/**
	 * Return an array of suits of the cards in the hand
	 */
	public int [] extractValues() {
		int [] values = new int [cards.size()];

		if (cards.size() != 0) {
			for (int i=0; i<cards.size(); i++) {
				values[i] = getNthCard(i).cardValue();
			}
		}

		return values;
	}

	/**
	 * Return a two dimensional array of suits & values
	 */
	public int [][] extractSuitsAndValues() {
		int [][] ret = new int [2][cards.size()];
	
		if (cards.size() != 0) {
			for (int i=0; i<cards.size(); i++) {
				Card c = getNthCard(i);
				ret[0][i] = c.cardSuit();
				ret[1][i] = c.cardValue();
			}
		}

		return ret;
	}
}
