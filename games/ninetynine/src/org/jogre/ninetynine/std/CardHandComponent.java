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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;
import org.jogre.ninetynine.std.DrawableHand;
import org.jogre.ninetynine.std.ICardRenderer;

import org.jogre.client.awt.JogreComponent;

// A component that draws a hand of cards
public class CardHandComponent extends JogreComponent {

	// The drawable hand that we're showing
	private DrawableHand theHand;

	// The renderer to use to draw the cards
	private ICardRenderer cardRenderer;

	// State of whether this component should respond to mouse activities or not.
	private boolean enabled;

	// State of whether this component should draw the card prompt or not.
	private boolean promptEnabled;
	private int selectYOffset;

	// The color to draw the background with
	private Color bgColor;

	/**
	 * Constructor which creates the hand component.
	 * Note: Components start out disabled and must be specifically enabled before
	 *		mouse operations are allowed.
	 *
	 * @param	theHand			The hand to draw
	 * @param	cardRenderer	The object that knows how to render a card
	 * @param	cardSpacing		The spacing between cards of the same suit
	 * @param	suitSpacing		The spacing between cards of different suits
	 * @param	selectYOffset	The Y-offset to apply to the selected card
	 * @param	maxCardsInHand	The maximum # of cards in the hand.
	 *								This is used to calculate the size of the component.
	 * @param	maxSuitsInHand	The maximum # of suits in the hand.
	 *								This is used to calculate the size of the component.
	 * @param	bgColor			The color to use for the background behind the cards.
	 *								Can be null for no background.
	 */
	public CardHandComponent (
		Hand theHand,
		ICardRenderer cardRenderer,
		int cardSpacing,
		int suitSpacing,
		int selectYOffset,
		int maxCardsInHand,
		int maxSuitsInHand,
		Color bgColor) {

		// Save parameters
		this.theHand = new DrawableHand(theHand, cardSpacing, suitSpacing, selectYOffset, cardRenderer.getCardWidth());
		this.cardRenderer = cardRenderer;
		this.bgColor = bgColor;
		this.selectYOffset = selectYOffset;

		// We observe the Drawable Hand
		this.theHand.addObserver(this);

		// Setup internal stuff
		enabled = false;
		promptEnabled = false;

		// Set the prefered size of our component
		Dimension dim = new Dimension (
			(maxCardsInHand - maxSuitsInHand) * cardSpacing +
			(maxSuitsInHand - 1) * suitSpacing +
			cardRenderer.getCardWidth(),
			selectYOffset + cardRenderer.getCardHeight());

		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Add a setController method for attaching to non-JogreController controllers
	 */
	public void setController (CardHandController controller) {
		addMouseListener (controller);
		addMouseMotionListener (controller);
	}

	/**
	 * Return the drawable hand that is used for this component
	 *
	 * @returns	the drawable hand
	 */
	public DrawableHand getDrawableHand() {
		return theHand;
	}

	/** 
	 * Set the background color that is drawn underneath the cards.
	 *
	 * @param	bgColor		The color to make the background.
	 */
	public void setBackgroundColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * Get the state of the component.
	 *
	 * @return	true => The component can respond to mouse activities
	 *			false => The component should not respond to mouse activities
	 */
	 public boolean isEnabled() {
	 	return enabled;
	 }

	/**
	 * Set the state of the enable flag.
	 */
	public void setEnable(boolean enable) { this.enabled = enable; }

	/**
	 * Set the state of the promptEnable flag.
	 */
	public void setPromptEnable(boolean enable) {
		// Only bother repainting if the enable has changed...
		if (enable != promptEnabled) {
			promptEnabled = enable;
			repaint();
		}
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
	
		// Draw the background, if we have a color
		if (bgColor != null) {
			g.setColor(bgColor);
			Rectangle bounds = getBounds();
			g.fillRect( 0, 0, bounds.width, bounds.height);
		}

		// Draw the hand
		theHand.paintHand(g, cardRenderer, 0, 0);

		// If the prompt is enabled, then draw the prompt.
		if (promptEnabled) {
			cardRenderer.paintPrompt(g, 0, selectYOffset);
		}
	}
}
