/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.grandPrixJumping.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.lang.Math;

import java.util.ListIterator;
import java.util.Vector;

import org.jogre.client.awt.JogreComponent;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import org.jogre.grandPrixJumping.common.JumpingCard;

/**
 * Component for the sorting area for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingSortAreaComponent extends JogreComponent {

	// Declare constants which define what the layout looks like
	// These are default values and can be overridden by settings in
	// the game.properties file.
	private static final int DEFAULT_SORT_CARD_SPACING = 30;
	private static final int DEFAULT_SORT_SELECTED_X_OFFSET = 30;
	private static final int DEFAULT_SORT_WELL_SPACING = 10;
	private static final int DEFAULT_BORDER_SIZE = 5;
	private static final int DEFAULT_OUTLINE_THICKNESS = 3;
	private static final String DEFAULT_OUTLINE_COLOUR = "255,186,0";

	// Color & stroke used to outline the sort area when choosing halfs
	private Color outlineColor;
	private BasicStroke outlineStroke;

	// Link to the model
	private JumpingClientModel model;
	private JumpingGraphics Jgraphics;

	// Used to decide the size of the area
	private static final int MAX_CARDS = 7;

	// Parameters for drawing the sort area
	private int cardSpacing;
	private int selectedHorizOffset;
	private int wellSpacing;
	private int borderSize;
	private int outlineThickness;
	private int halfOutlineThickness;
	private int outlineWidth, outlineHeight;
	private int rightOffset;
	private int cardWidth, cardHeight;
	private int bottomCardEdge;

	// The index of the currently selected card
	private int selectedCardIndex;

	// The side that the currently selected card / area is on
	private int side;
	public static final int NO_AREA = -1;
	public static final int LEFT_AREA = 0;
	public static final int RIGHT_AREA = 1;

	// For debug, setting this to true will cause the requested & actual bounds
	// of the component to be drawn.
	private boolean debugShowBounds = false;

	/**
	 * Constructor which creates the component
	 *
	 * @param model					The game model
	 */
	public JumpingSortAreaComponent (JumpingClientModel model) {

		// link to model & graphics helper
		this.model = model;
		this.Jgraphics = JumpingGraphics.getInstance();

		// Get properties from the properties file
		GameProperties props = GameProperties.getInstance();
		cardSpacing = props.getInt("sort.card.spacing", DEFAULT_SORT_CARD_SPACING);
		selectedHorizOffset = props.getInt("sort.selected.horiz.offset", DEFAULT_SORT_SELECTED_X_OFFSET);
		wellSpacing = props.getInt("sort.well.spacing", DEFAULT_SORT_WELL_SPACING);
		borderSize = props.getInt("sort.border.size", DEFAULT_BORDER_SIZE);
		outlineThickness = props.getInt("sort.outline.thickness", DEFAULT_OUTLINE_THICKNESS);
		outlineColor = JogreUtils.getColour(props.get("sort.outline.colour", DEFAULT_OUTLINE_COLOUR));

		// Get the size of a card from the graphics helper
		cardWidth = Jgraphics.imageWidths[JumpingImages.CARDS];
		cardHeight = Jgraphics.imageHeights[JumpingImages.CARDS];

		// Calculate the X-offset for the right column
		rightOffset = borderSize + cardWidth + wellSpacing;

		// Initialize stuff
		selectedCardIndex = -1;
		side = NO_AREA;
		outlineStroke = new BasicStroke(outlineThickness);

		halfOutlineThickness = outlineThickness / 2;

		outlineWidth = borderSize + cardWidth + wellSpacing / 2 - outlineThickness;
		outlineHeight = borderSize * 2 + cardHeight + (MAX_CARDS - 1) * cardSpacing - outlineThickness;

		bottomCardEdge = cardHeight + (MAX_CARDS - 1) * cardSpacing;

		// Set the prefered size of our component
		Dimension dim = new Dimension (
			borderSize * 2 + cardWidth * 2 + wellSpacing,
			borderSize * 2 + bottomCardEdge);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Set the currently selected card index to the given value.
	 * Return value indicates if this is a different card that is now selected.
	 *
	 * @param	index			The index to make the currently selected value.
	 * @return true => The new selected card is different than the old one.
	 *         false => The new selected card is the same as the old one.
	 */
	public boolean setSelectedIndex(int index) {
		if (selectedCardIndex != index) {
			selectedCardIndex = index;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the currently selected card index.
	 *
	 * @return the current selected card index.
	 */
	public int getSelectedIndex() {
		return selectedCardIndex;
	}

	/**
	 * Select the card pointed to at (x,y).
	 * Return value indicates if this is a different card than is now selected.
	 */
	public boolean selectCardAt(int x, int y) {
		Vector cards = null;

		// Determine which column the point is in.
		if ((x > borderSize) && (x < cardWidth + borderSize)) {
			// In the left column
			cards = model.getSortCards(0);
		} else if ((x > rightOffset) && (x < rightOffset + cardWidth)) {
			// In the right column
			cards = model.getSortCards(1);
		}

		// Make sure we have cards to select from
		if (cards == null) {
			return setSelectedIndex(-1);
		}

		// Remove the border from the y coordinate
		y -= borderSize;
		if ((y < 0) || (y > bottomCardEdge)) {
			// We're in the border
			return setSelectedIndex(-1);
		}
			

		// Start at the index for the card assuming all are shown.
		int index = Math.min((y / cardSpacing), cards.size()-1);

		while (index >= 0) {
			if (y <= (index * cardSpacing) + cardHeight) {
				// The point is within the bounds of the card.
				JumpingCard card = (JumpingCard) cards.get(index);
				if (card.isVisible()) {
					// We've found our selected card !
					return setSelectedIndex(index);
				} else {
					// This card is clear, so step up and try that card
					index -= 1;
				}
			} else {
				// The point is outside the range, so there is no card to select
				return setSelectedIndex(-1);
			}
		}

		// All cards above the starting index are invisible
		return setSelectedIndex(-1);
	}

	/**
	 * Select the area pointed to at (x,y).
	 * Return value indicates if this is a different area than is now selected.
	 */
	public boolean selectAreaAt(int x, int y) {
		if ((x > borderSize) && (x < cardWidth + borderSize)) {
			return setSelectedArea(LEFT_AREA);
		} else if ((x > rightOffset) && (x < rightOffset + cardWidth)) {
			return setSelectedArea(RIGHT_AREA);
		} else {
			return setSelectedArea(NO_AREA);
		}
	}

	/**
	 * Select the given area
	 * Return value indicates if this is a different area than is now selected.
	 *
	 * @param	area		The area to select.
	 */
	public boolean setSelectedArea(int area) {
		if (this.side != area) {
			this.side = area;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the current selected area
	 */
	public int getSelectedArea() {
		return this.side;
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		// Draw the two columns of cards
		paintColumn(g, borderSize, selectedHorizOffset, model.getSortCards(0));
		paintColumn(g, rightOffset, -selectedHorizOffset, model.getSortCards(1));

		// If a player is choosing one of the sorted areas, outline the selected area
		if (model.isChoosingSorted() && (side != NO_AREA)) {
			int left = halfOutlineThickness;

			if (side == RIGHT_AREA) {
				left += outlineWidth;
			}

			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(outlineStroke);

			g.setColor(outlineColor);
			g.drawRect(left + (borderSize / 2), halfOutlineThickness, outlineWidth, outlineHeight);
		}

		// If it is sorting or choosing time, then display the horse icon of the
		// current player.
		if (model.isSortingCards() || model.isChoosingSorted()) {
			int horsex = Jgraphics.imageWidths[JumpingImages.HORSE0] / 2;
			int horsey = getBounds().height - (Jgraphics.imageHeights[JumpingImages.HORSE0] / 2);

			Jgraphics.paintImage(g, horsex, horsey, JumpingImages.HORSE0 + model.getCurrentPlayer(), 0, 0);
		}

		// For debug, show the requested & actual bounds
		if (debugShowBounds) {
			Jgraphics.paintRects(g, getBounds(), getPreferredSize());
		}
	}

	/**
	 * Paint a column of cards
	 *
	 * @param	g				The graphics area to draw on
	 * @param	xOffset			The x-offset to draw the column
	 * @param	selectOffset	The amount to offset the currently selected card
	 * @param	cards			The vector of cards to be drawn
	 */
	private void paintColumn(Graphics g, int xOffset, int selectOffset, Vector cards) {
		if (cards == null) {
			// If there are no cards, then nothing to draw.
			return;
		}

		ListIterator iter = cards.listIterator();

		int y = borderSize;
		int i = 0;

		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			int x = ((i == selectedCardIndex) ? xOffset + selectOffset : xOffset);
			Jgraphics.paintCard(g, x, y, card);
			y += cardSpacing;
			i += 1;
		}
	}

}
