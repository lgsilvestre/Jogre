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

import org.jogre.client.awt.JogreComponent;
import org.jogre.common.games.Card;

/**
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 *
 * Spades board component.
 */
public class SpadesBoardComponent extends JogreComponent {

	// board players
	public final static int BOTTOM = 0;
	public final static int LEFT = 1;
	public final static int TOP = 2;
	public final static int RIGHT = 3;

	// card spacing on board
	private final static int SPACING = 5;

	// spades model
	private SpadesModel model = null;

	/**
	 * Default constructor which requires a spades model.
	 *
	 * @param model
	 *            Spades model
	 */
	public SpadesBoardComponent(SpadesModel model) {
		this.model = model;

		int cardHeight = Card.CARD_PIXEL_HEIGHT;
		int cardWidth = Card.CARD_PIXEL_WIDTH;

		int width = (SPACING * 4) + (cardHeight * 2) + cardWidth;
		int height = (SPACING * 3) + (cardHeight * 2);

		setPreferredSize(new Dimension(width, height));
		repaint();
	}

	/**
	 * Refresh the component.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		g.setColor(SpadesLookAndFeel.TABLE_BG_COLOUR);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.setColor(SpadesLookAndFeel.BORDER_COLOUR);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		int cardHeight = Card.CARD_PIXEL_HEIGHT;
		int cardWidth = Card.CARD_PIXEL_WIDTH;
		Image img = null;
		int x = 0;
		int y = 0;

		Card[] cards = this.model.getTableCards();

		// draw top card
		if (cards[TOP] != null) {
			x = (getWidth() / 2) - (cardWidth / 2);
			y = SPACING;
			cards[TOP].paintComponent(g, x, y);
		}

		// draw bottom card
		if (cards[BOTTOM] != null) {
			x = (getWidth() / 2) - (cardWidth / 2);
			y = (cardHeight) + (SPACING * 2);
			cards[BOTTOM].paintComponent(g, x, y);
		}

		// draw left card
		if (cards[LEFT] != null) {
			x = SPACING;
			y = (getHeight() / 2) - (cardWidth / 2);
			cards[LEFT].paintComponent(g, x, y, true);
		}

		// draw right card
		if (cards[RIGHT] != null) {
			x = (cardHeight) + (cardWidth) + (SPACING * 3);
			y = (getHeight() / 2) - (cardWidth / 2);
			cards[RIGHT].paintComponent(g, x, y, true);
		}
	}
}