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

import java.awt.Graphics;
import java.util.Observer;
import java.util.Observable;

// Structure to hold a drawable & selectable hand of cards
public class DrawableHand extends Observable implements Observer {

	// This is the non-drawable Hand that we are observing
	private Hand theHand;

	// This remembers if the hand has been enumerated yet or not
	private boolean hand_enumerated;

	// This array holds the x offsets for when drawing the hand
	private int [] x_offsets;

	// Parameters used to draw the hand
	private int selectYOffset;
	private int cardSpacing;
	private int suitSpacing;
	private int cardWidth;

	// The currently selected card
	private Card selectedCard;
	public Card noCard;

	/**
	 * Constructor for a drawable hand
	 *
	 * @param	theHand			The hand that we will be drawing
	 * @param	cardSpacing		The spacing between cards of the same suit
	 * @param	suitSpacing		The spacing between cards of different suits
	 * @param	selectYOffset	The Y-offset to apply to the selected card
	 * @param	cardWidth		The width of a card image.
	 */
	public DrawableHand (
		Hand theHand,
		int cardSpacing,
		int suitSpacing,
		int selectYOffset,
		int cardWidth) {

		// Save parameters
		this.theHand = theHand;
		this.cardSpacing = cardSpacing;
		this.suitSpacing = suitSpacing;
		this.selectYOffset = selectYOffset;
		this.cardWidth = cardWidth;

		// Setup internal stuff
		noCard = Card.makeUniqueCard();
		selectedCard = noCard;
		hand_enumerated = false;

		// We observe the Hand
		theHand.addObserver(this);
	}

	/**
	 * Enumerate the graphics positions for the cards in the hand.
	 */
	private boolean enumerateGraphics() {

		if (theHand.length() == 0) {
			// There are no cards to enumerate
			return false;
		}

		if (hand_enumerated == true) {
			// This hand has been enumerated before, so nothing to do
			return true;
		}

		// Create a new array for the x-offsets
		x_offsets = new int [theHand.length()];

		// Walk down the cards, calculating the appropriate x_offset
		int curr_x = -cardSpacing;
		int lastCardSuit = theHand.getNthCard(0).cardSuit();
		for (int i=0; i<theHand.length(); i++) {
			int thisSuit = theHand.getNthCard(i).cardSuit();
			curr_x += ((thisSuit == lastCardSuit) ? cardSpacing : suitSpacing);
			x_offsets[i] = curr_x;
			lastCardSuit = thisSuit;
		}

		// The hand is now enumerated
		hand_enumerated = true;

		return true;
	}

	/**
	 * The Hand is telling us that it has changed and so we need to re-enumerate.
	 */
	public void update (Observable observerable, Object args) {
		hand_enumerated = false;

		// Notify the guys observing us
		setChanged();
		notifyObservers();
	}

	/**
	 * Return the Hand object that we are displaying.
	 */
	public Hand getHand() {
		return theHand;
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
	public boolean selectCardAt(int x, int y) {
		if (enumerateGraphics() == false) {
			// Only allow selection after enumeration has occurred.
			return setSelectedCard (noCard);
		}

		int i = getFirstIndex(x);
		while (i >= 0) {
			if (x <= x_offsets[i] + cardWidth) {
				// The point is within the bounds of the card.
				Card thisCard = theHand.getNthCard(i);
				if (thisCard.isVisible()) {
					// We've found our selected card !
					return setSelectedCard(thisCard);
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
	public boolean setSelectedCard(Card newCard) {
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
	public Card getSelectedCard() {
		return selectedCard;
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
			for (int i = x_offsets.length-1; i>=0; i--) {
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
	 * @param	cardPainter		The object that knows how to draw a card
	 * @param	(x, y)			Offset into g to paint the hand
	 */
	public void paintHand(
		Graphics g,
		ICardRenderer cardPainter,
		int x, int y)
	{
		// Only paint after enumeration has occurred
		if (enumerateGraphics()) {
			// Draw all of the cards in the hand
			int cardCount = theHand.length();
			if (cardCount > 0) {
				for (int i=0; i<theHand.length(); i++) {
					Card thisCard = theHand.getNthCard(i);
					int real_y = (selectedCard.equals(thisCard) ? y : y + selectYOffset);
					cardPainter.paintCard(g, x + x_offsets[i], real_y, thisCard);
				}
			}
		}
	}

}
