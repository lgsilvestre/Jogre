/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter@yahoo.com)
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
package org.jogre.carTricks.client;

import java.util.Arrays;

import org.jogre.carTricks.common.CarTricksCard;

import java.awt.Graphics;

// Structure to hold a hand of cards for Car Tricks
public class CarTricksDrawableHand {

	// This remembers if the hand has been enumerated yet or not
	private boolean hand_enumerated;

	// This array holds the x offsets for when drawing the hand
	private int [] x_offsets = new int [0];

	// This array holds the cards in the hand
	private CarTricksCard [] hand = new CarTricksCard [0];

	// This is the number of visible cards in the hand.
	// When this becomes 0, then the hand is empty.
	private int numVisibleCardsInHand;

	// Parameters used to draw the hand
	private int selected_y_offset;
	private int card_spacing;
	private int color_spacing;

	// The currently selected card
	private CarTricksCard selectedCard;
	public CarTricksCard noCard;

	/**
	 * Constructor for an empty hand
	 */
	public CarTricksDrawableHand() {
		// Create the special cards that we hold
		noCard = new CarTricksCard();
		selectedCard = noCard;

		numVisibleCardsInHand = 0;

		// Set misc. flags
		hand_enumerated = false;
		selected_y_offset = 0;
		card_spacing = 0;
		color_spacing = 0;
	}

	/**
	 * Constructor for a hand given an array of cards
	 *
	 * @param	theCards	The array of cards for the hand.
	 */
	public CarTricksDrawableHand(CarTricksCard [] theCards) {
		// Use the empty constructor to initialize things
		this();

		// Set the cards
		this.setCards(theCards);

		// Assume that none of the cards provided to us are invisible.
		numVisibleCardsInHand = theCards.length;
	}		

	/**
	 * Set the hand to the given array of cards.
	 *
	 * @param	theCards	The array of cards for the hand.
	 */
	public void setCards(CarTricksCard [] theCards) {
		// Copy the array of cards provided.
		hand = new CarTricksCard [theCards.length];
		System.arraycopy(theCards, 0, hand, 0, theCards.length);
		Arrays.sort(hand);
		hand_enumerated = false;
		numVisibleCardsInHand = theCards.length;
	}

	/**
	 * Add a card to a hand.
	 *
	 * @param	newCard				The card to add to the hand
	 */
	public void addCard(CarTricksCard newCard) {
		CarTricksCard [] newHand = new CarTricksCard [hand.length + 1];
		newHand[0] = newCard;
		System.arraycopy(hand, 0, newHand, 1, hand.length);
		Arrays.sort(hand);
		hand = newHand;
		hand_enumerated = false;
		numVisibleCardsInHand += 1;
	}

	/**
	 * Remove all of the cards from the hand.
	 */
	public void empty() {
		hand = new CarTricksCard [0];
		hand_enumerated = false;
		numVisibleCardsInHand = 0;
	}

	/**
	 * Set graphics parameters to use when drawing the hand
	 *
	 * @param	card_spacing		The number of pixels between the left edges of cards
	 *								  in the same color.
	 * @param	color_spacing		The number of pixels between the left edges	of cards
	 *								  when changing colors.
	 * @param	y_offset			The number of pixels down to draw a non-selected card.
	 */
	public void setGraphicsParams(
		int card_spacing,
		int color_spacing,
		int selected_y_offset
	) {
		this.card_spacing = card_spacing;
		this.color_spacing = color_spacing;
		this.selected_y_offset = selected_y_offset;
	}

	/**
	 * Enumerate the graphics positions for the cards in the hand.
	 */
	private boolean enumerateGraphics() {

		if (card_spacing == 0) {
			// The graphics params haven't been set up yet, so fail
			return false;
		}

		if (hand.length == 0) {
			// There are no cards to enumerate
			return false;
		}

		if (hand_enumerated == true) {
			// This hand has been enumerated before, so nothing to do
			return true;
		}

		// Create a new array for the x-offsets
		x_offsets = new int [hand.length];

		// Walk down the cards, calculating the appropriate x_offset
		int curr_x = -card_spacing;
		int lastCardColor = hand[0].cardColor();
		for (int i=0; i<hand.length; i++) {
			curr_x += ((hand[i].cardColor() == lastCardColor) ? card_spacing : color_spacing);
			x_offsets[i] = curr_x;
			lastCardColor = hand[i].cardColor();
		}

		// The hand is now enumerated
		hand_enumerated = true;

		return true;
	}

	/**
	 * Select the card that is located at (x,y).
	 * If there is already a selected card, then deselect that one also.
	 *
	 * @param	(x,y)			Offset (in pixels) into the hand
	 * @param	card_width		Width (in pixels) of a card
	 * @return					true = the newly selected card is different from the old one
	 *							false = the newly selected card is not different from the old one
	 */
	public boolean selectCardAt(int x, int y, int card_width) {
		CarTricksCard newCard;

		if (enumerateGraphics() == false) {
			// Only allow selection after enumeration has occurred.
			return setSelectedCard (noCard);
		}

		if (hand.length == 0) {
			// If there are no cards in the hand, then there is nothing to select
			return setSelectedCard (noCard);
		}

		int i = getFirstIndex(x);
		while (i >= 0) {
			if (x <= x_offsets[i] + card_width) {
				// The point is within the bounds of the card.
				if (hand[i].isVisible()) {
					// We've found our selected card !
					return setSelectedCard(hand[i]);
				} else {
					// This card is clear, so step to the left and try that card
					i -= 1;
				}
			} else {
				// The point is outside the range, so there is no card to select
				return setSelectedCard(noCard);
			}
		}

		// All cards to the left of the starting index are invisible
		return setSelectedCard(noCard);
	}

	/**
	 * Set the currently selected card to the given one.
	 *
	 * @param	newCard			The new card to set.
	 * @return					true = the newly selected card is different from the old one
	 *							false = the newly selected card is not different from the old one
	 */
	public boolean setSelectedCard(CarTricksCard newCard) {
		if (newCard.equals(selectedCard)) {
			return (false);
		} else {
			selectedCard = newCard;
			return (true);
		}
	}

	/**
	 * Unselect the currently selected card
	 */
	public void unselectCard() {
		selectedCard = noCard;
	}

	/**
	 * Return the currently selected card
	 *
	 * @return					The currently selected card.
	 */
	public CarTricksCard getSelectedCard() {
		return selectedCard;
	}

	/**
	 * Set the given card to invisible
	 *
	 * @param	theCard		The card to make invisible
	 */
	public void invisiblizeCard(CarTricksCard theCard) {
		int index = Arrays.binarySearch(hand, theCard);
		if (index >= 0) {
			hand[index] = CarTricksCard.makeInvisibleCard(theCard);
		}
		numVisibleCardsInHand -= 1;
	}

	/**
	 * Determine if there are any visible cards still in the hand or not.
	 *
	 * @return	true => hand has no more visible cards
	 */
	public boolean isEmpty() {
		return (numVisibleCardsInHand == 0);
	}

	/**
	 * Determine if the hand has one last visible card still in it.
	 *
	 * @return	true => hand has exactly 1 more visible card
	 */
	public boolean lastCard() {
		return (numVisibleCardsInHand == 1);
	}

	/**
	 * Determine the index i, such that x_offset[i] <= x <= x_offset[i+1].
	 *
	 * @param	x				Offset (in pixels) into the hand
	 * @return					index (0..n)
	 *							-1 => x is less than x_offset[0].
	 */
	private int getFirstIndex (int x) {
		if (x < x_offsets[0]) {
			// Point is to the left of the first card
			return -1;
		} else {
			// Start at the right most card and work back
			for (int i = hand.length-1; i>=0; i--) {
				if (x >= x_offsets[i]) {
					return i;
				}
			}
		}

		// Can't get here, but the compiler doesn't know that...
		return 0;
	}

	/**
	 * Paint the hand of cards
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the hand
	 */
	public void paint(
		Graphics g,
		int x, int y)
	{
		if (enumerateGraphics()) {
			// Only paint after enumeration has occurred
			CarTricksGraphics CT_graphics = CarTricksGraphics.getInstance();

			// Draw all of the cards in the hand
			for (int i=0; i<hand.length; i++) {
				int real_y = (selectedCard.equals(hand[i]) ? y : y + selected_y_offset);
				CT_graphics.paintCard(g, x + x_offsets[i], real_y, hand[i]);
			}
		}
	}

}
