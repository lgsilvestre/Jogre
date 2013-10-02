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
package org.jogre.abstrac.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jogre.abstrac.std.Card;
import org.jogre.abstrac.std.Hand;
import org.jogre.abstrac.std.ICardRenderer;

import org.jogre.client.awt.JogreComponent;

// A component that draws a hand of cards
public class AbstracHandComponent extends JogreComponent {

	// The hand that we're showing
	private Hand theHand;

	// The renderer to use to draw the cards
	private ICardRenderer cardRenderer;

	// Parameters used to draw the hand
	private int selectYOffset;
	private int cardSpacing;
	private int cardWidth;

	// The currently selected card
	private int numSelectedCards;

	/**
	 * Constructor which creates the hand component.
	 *
	 * @param	theHand			The hand to draw
	 * @param	cardRenderer	The object that knows how to render a card
	 * @param	cardSpacing		The spacing between cards
	 * @param	selectYOffset	The Y-offset to apply to the selected card
	 * @param	maxCardsInHand	The maximum # of cards in the hand.
	 *								This is used to calculate the size of the component.
	 */
	public AbstracHandComponent (
		Hand theHand,
		ICardRenderer cardRenderer,
		int cardSpacing,
		int selectYOffset,
		int maxCardsInHand) {

		// Save parameters
		this.theHand = theHand;
		this.cardRenderer = cardRenderer;
		this.cardSpacing = cardSpacing;
		this.selectYOffset = selectYOffset;
		this.cardWidth = cardRenderer.getCardWidth();

		// We observe the Hand
		this.theHand.addObserver(this);

		// Setup internal stuff
		numSelectedCards = 0;

		// Set the prefered size of our component
		Dimension dim = new Dimension (
			(maxCardsInHand - 1) * cardSpacing + cardWidth,
			selectYOffset + cardRenderer.getCardHeight());

		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Return the hand that is used for this component
	 *
	 * @returns	the hand
	 */
	public Hand getHand() {
		return theHand;
	}


	/**
	 * Select the cards that are located at (x,y).
	 *
	 * @param	(x,y)			Offset (in pixels) into the hand
	 * @return					true = the number of selected cards is different than it used to be
	 *							false = the number of selected cards is the same as it used to be
	 */
	public boolean selectCardsAt(int x, int y) {

		// Determine how many cards from the top the cursor is at
		int numCards = theHand.length();
		int selectedCards =  numCards - (x / cardSpacing);

		// Need to special case the top card (since it is entirely displayed and
		// not just the sliver of the width.)
		if (selectedCards <= 0) {
			if (x < ((numCards - 1) * cardSpacing + cardWidth)) {
				// The cursor is on the last card, so selectedCards is 1
				selectedCards = 1;
			} else {
				// The cursor is beyond the last card, so selectedCards is 0
				selectedCards = 0;
			}
		}

		// Can only select up to 3 cards
		if (selectedCards > 3) {
			selectedCards = 0;
		}

		if (selectedCards == numSelectedCards) {
			return false;
		} else {
			numSelectedCards = selectedCards;
			return true;
		}
	}

	/**
	 * Unselect all of the currently selected cards
	 *
	 * @return true => some cards were unselected
	 * @return false => no cards were unselected because there were none selected
	 */
	public boolean unselectAllCards() {
		if (numSelectedCards == 0) {
			return false;
		} else {
			numSelectedCards = 0;
			return true;
		}
	}

	/**
	 * Return the number of currently selected cards
	 *
	 * @return	the number of currently selected cards
	 */
	public int getNumSelectedCards() {
		return numSelectedCards;
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
		// Draw all of the cards in the hand
		int cardCount = theHand.length();
		int firstSelected = cardCount - numSelectedCards;
		if (cardCount > 0) {
			for (int i=0; i<cardCount; i++) {
				Card thisCard = theHand.getNthCard(i);
				int real_y = ((i >= firstSelected) ? y : y + selectYOffset);
				cardPainter.paintCard(g, x + cardSpacing * i, real_y, thisCard);
			}
		}
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		// Draw the hand
		paintHand(g, cardRenderer, 0, 0);

	}
}
