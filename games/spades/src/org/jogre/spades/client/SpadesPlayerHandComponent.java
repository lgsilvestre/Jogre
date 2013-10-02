/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
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
package org.jogre.spades.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import org.jogre.client.awt.JogreComponent;
import org.jogre.common.games.Card;
import org.jogre.common.games.Deck;

/**
 * Component for making a bid for a spades hand
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class SpadesPlayerHandComponent extends JogreComponent {

	// Spades model
	private SpadesModel model = null;

	// Card pressed
	private int cardPressed = -1;

	// Card clicked
	private int cardClicked = -1;

	// Last card clicked
	private int lastCardClicked = -1;

	/**
	 * Default constructor which takes a spades model
	 *
	 * @param spadesModel
	 *            Link to the main spades model
	 */
	public SpadesPlayerHandComponent(SpadesModel model) {
		this.model = model;

		int cardHeight = Card.CARD_PIXEL_HEIGHT;
		int cardWidth = Card.CARD_PIXEL_WIDTH;
		int cardSpacing = Card.CARD_SPACING;

		int width = cardWidth + (cardSpacing * 12);
		int height = cardHeight + cardSpacing;

		setPreferredSize(new Dimension(width, height));
		repaint();
	}

	/**
	 * Refresh the component.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		// draw background
		drawBackground(g);

		// draw cards on table
		drawCards(g);
	}

	/**
	 * Draw background
	 *
	 * @param g
	 *            Graphics
	 */
	public void drawBackground(Graphics g) {
		g.setColor(SpadesLookAndFeel.BG_COLOUR);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void drawCards(Graphics g) {
		boolean[] cardsSelected = this.model.getCardsSelected();
		boolean[] cardsPlayed = this.model.getCardsPlayed();
		Deck hand = this.model.getHand();
		if (hand == null)
			return;
		int size = hand.size();
		Card card = null;
		Image img = null;
		int x = 0;
		int y = 0;
		for (int i = 0; i < size; i++) {
			if (!cardsPlayed[i]) {
				card = hand.get(i);
				//img = SpadesCardImages.getCardImage(card.getSuit() + card.getCard());
				x = i * Card.CARD_SPACING;
				y = Card.CARD_SPACING;

				int selected = 0;
				if (cardsSelected[i])
					selected = 1;

				y -= selected * Card.CARD_SPACING;

				card.paintComponent(g, x, y);
			}
		}
	}

	/**
	 * Get card index by mouse x and y coordinates
	 *
	 * @param mouseX
	 *            Mouse x coordinate
	 * @param mouseY
	 *            Mouse y coordinate
	 * @return the card index by mouse x and mouse y
	 */
	public int getCard(int mouseX, int mouseY) {
		int width = this.getWidth();
		int height = this.getHeight();

		Point topLeftPoint = new Point(0, 0);
		Point bottomRightPoint = new Point(width, height);

		if (mouseX < topLeftPoint.x || mouseX > bottomRightPoint.x
				|| mouseY < topLeftPoint.y || mouseY > bottomRightPoint.y) {
			return -1;
		}

		boolean[] cardsPlayed = this.model.getCardsPlayed();
		boolean[] cardsSelected = this.model.getCardsSelected();
		int x = 0;
		int y = 0;
		Point topLeftCardPoint = null;
		Point bottomRightCardPoint = null;
		for (int i = 12; i >= 0; i--) {
			if (!cardsPlayed[i]) {
				x = i * Card.CARD_SPACING;
				y = Card.CARD_SPACING;
				if (cardsSelected[i])
					y = 0;
				topLeftCardPoint = new Point(x, y);

				x += Card.CARD_PIXEL_WIDTH;
				y += Card.CARD_PIXEL_HEIGHT;
				bottomRightCardPoint = new Point(x, y);

				if (mouseX > topLeftCardPoint.x
						&& mouseX < bottomRightCardPoint.x
						&& mouseY > topLeftCardPoint.y
						&& mouseY < bottomRightCardPoint.y) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Sets card pressed
	 *
	 * @param cardPressed
	 *            Index of card pressed
	 */
	public void setCardPressed(int cardPressed) {
		this.cardPressed = cardPressed;
	}

	/**
	 * Gets index of card pressed
	 *
	 * @return index of card pressed
	 */
	public int getCardPressed() {
		return this.cardPressed;
	}

	/**
	 * Sets card clicked, as well as setting last card clicked from previous
	 * variable
	 *
	 * @param cardClicked
	 *            Index of card clicked
	 */
	public void setCardClicked(int cardClicked) {
		setLastCardClicked(this.cardClicked);
		this.cardClicked = cardClicked;
	}

	/**
	 * Gets index of card clicked
	 *
	 * @return index of card clicked
	 */
	public int getCardClicked() {
		return this.cardClicked;
	}

	/**
	 * Sets last card clicked
	 *
	 * @param lastCardClicked
	 *            Index of last card clicked
	 */
	public void setLastCardClicked(int lastCardClicked) {
		this.lastCardClicked = lastCardClicked;
	}

	/**
	 * Gets index of last card clicked
	 *
	 * @return index of last card clicked
	 */
	public int getLastCardClicked() {
		return this.lastCardClicked;
	}
}