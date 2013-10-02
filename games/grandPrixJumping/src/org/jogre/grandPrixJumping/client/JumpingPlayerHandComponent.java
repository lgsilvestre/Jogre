/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.client;

import java.util.Vector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.lang.Math;

import org.jogre.client.awt.JogreComponent;

import org.jogre.common.util.GameLabels;

import org.jogre.grandPrixJumping.common.JumpingCard;

/**
 * View of a player's hand for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingPlayerHandComponent extends JogreComponent {

	// Link to the model & graphics helper
	private JumpingClientModel model;
	private JumpingGraphics Jgraphics;

	// Used to decide the size of the hand
	private static final int MAX_CARDS = 20;

	// The playerId that we're showing
	private int playerId;

	// Parameters for drawing the cards
	private int cardSpacing;
	private int verticalOffset;
	private int cardWidth;
	private int cardHeight;
	private int wh;

	// The index of the card that is currently selected
	private int selectedCardIndex = -1;

	// The image index & half of the card that is currently activated
	// (Note: image index is the distance into the activated card images the
	//  activated card is.  The index into the player's hand is <selectedCardIndex>.)
	private int activatedCardImageIndex = -1;
	private int activatedHalf = -1;

	// The index into the highlight graphic to use to highlight the horse
	// image in the lower-left corner of the hand when it is our turn.
	private int highlightIndex = 0;

	// For debug, setting this to true will cause the requested & actual bounds
	// of the component to be drawn.
	private boolean debugShowBounds = false;

	// Stuff used to draw the "x/y" hand size text
	private final static int TEXT_BORDER = 3;
	private Font handFont;
	private FontMetrics handFontMetrics;
	private int textLineHeight;
	private int cornerWidth, cornerHeight;
	private Color handDarkColor = null;

	/**
	 * Constructor which creates the hand component
	 *
	 * @param model				The game model
	 * @param playerId			The playerId of the player we're showing
	 * @param cardSpacing		Card Spacing
	 * @param verticalOffset	Vertical Offset
	 */
	public JumpingPlayerHandComponent (	JumpingClientModel model,
										int playerId,
										int cardSpacing,
										int verticalOffset,
										int highlightIndex) {

		// link to model
		this.model = model;
		this.Jgraphics = JumpingGraphics.getInstance();

		// Save parameters
		this.playerId = playerId;
		this.cardSpacing = cardSpacing;
		this.verticalOffset = verticalOffset;
		this.highlightIndex = highlightIndex;

		// Get the font & metrics for the font we're using to display hand size
		handFont = new Font("Dialog", Font.BOLD, 12);
		handFontMetrics = getFontMetrics(handFont);
		textLineHeight = handFontMetrics.getMaxAscent() +
		                 handFontMetrics.getMaxDescent() +
		                 TEXT_BORDER * 2;
		cornerWidth  = Jgraphics.imageWidths [JumpingImages.HAND_SIZE_CORNERS];
		cornerHeight = Jgraphics.imageHeights[JumpingImages.HAND_SIZE_CORNERS];
	
		// Set the prefered size of our component
		cardWidth  = Jgraphics.imageWidths [JumpingImages.CARDS];
		cardHeight = Jgraphics.imageHeights[JumpingImages.CARDS];
		wh = cardWidth * cardHeight;

		Dimension dim = new Dimension (
			MAX_CARDS * cardSpacing + cardWidth,
			verticalOffset + cardHeight);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Set the playerId of the player that this component should be showing
	 *
	 * @param	playerId		The playerId that this component should be showing
	 */
	public void setPlayerId(int playerId) {
		this.playerId = (playerId == 1) ? 1 : 0;
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
	public JumpingCard getSelectedCard() {
		try {
			return (JumpingCard) model.getPlayableHand(playerId).elementAt(selectedCardIndex);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the currently activated half
	 */
	public int getActivatedHalf() {
		return activatedHalf;
	}

	/**
	 * Select the card pointed to at (x,y).
	 * Return value indicates if this is a different card than is now selected.
	 */
	public boolean selectCardAt(int x, int y) {
		Vector hand = model.getPlayableHand(playerId);

		if (hand.size() == 0) {
			// If there are no cards in the hand, then there is nothing to select
			return setSelectedIndex(-1);
		}

		// Start at the correct card index for the given point
		int i = Math.min((x / cardSpacing), hand.size()-1);
		while (i >= 0) {
			if (x <= (i * cardSpacing) + cardWidth) {
				// The point is within the bounds of the card.
				JumpingCard card = (JumpingCard) hand.elementAt(i);
				if (card.isVisible()) {
					// We've found our selected card
					if (card.isPlayable()) {
						return setSelectedIndex(i);
					} else {
						return setSelectedIndex(-1);
					}
				} else {
					// This card is clear, so step to the left and try that card
					i -= 1;
				}
			} else {
				// The point is outside the range, so there is no card to select
				return setSelectedIndex(-1);
			}
		}

		// All cards to the left of the starting index are invisible
		return setSelectedIndex(-1);
	}

	/**
	 * For the activated card, determine which point (x,y) is at.
	 * Return value indicates if this is a different area than is now selected.
	 */
	public boolean selectActivatedCardAt(int x, int y) {
		int newHalf;

		// The only activatable hand card is the ribbon, so if we're activating
		// a card, it must be the ribbon card.
		activatedCardImageIndex = 1;

		// Subtract out the offset to the card
		x -= selectedCardIndex * cardSpacing;

		if ((x < 0) || (x > cardWidth) || (y > cardHeight)) {
			// Outside of the card image, so no area is selected
			newHalf = -1;
		} else if ( (x * cardHeight) + (y * cardWidth) < wh) {
			newHalf = 0;
		} else {
			newHalf = 1;
		}

		if (activatedHalf == newHalf) {
			return false;
		} else {
			activatedHalf = newHalf;
			return true;
		}
	}

	/**
	 * Deactivate the activated card.
	 */
	public void deactivateCard() {
		activatedCardImageIndex = -1;
		activatedHalf = -1;
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {

		// Paint hand cards for the player
		Vector theHand = (Vector) model.getPlayableHand(playerId);
		Jgraphics.paintHand(g, 0, 0, theHand, selectedCardIndex, verticalOffset, cardSpacing);

		// If we're in the mode of selecting an activated card (either in this hand, or in
		// the immediate cards), then shadow out the cards.
		if (model.isCardActivated() && (playerId == model.getCurrentPlayer())) {
			Jgraphics.shadowRect(g, getBounds());
		}

		// If we have an activated card, then draw it
		if (activatedCardImageIndex != -1) {
			Jgraphics.paintActivatedCard(g,
										 selectedCardIndex * cardSpacing, 0,
										 activatedCardImageIndex,
										 activatedHalf);
		}

		// If it's our turn, then draw our horse in the lower right corner.
		if (model.getCurrentPlayer() == playerId) {
			int horsex = Jgraphics.imageWidths[JumpingImages.HORSE0] / 2;
			int horsey = getBounds().height - (Jgraphics.imageHeights[JumpingImages.HORSE0] / 2);

			Jgraphics.paintImage(g, horsex, horsey, JumpingImages.TURN_HIGHLIGHT, highlightIndex, 0);
			Jgraphics.paintImage(g, horsex, horsey, JumpingImages.HORSE0 + playerId, 0, 0);
		}

		// Paint the "x/y" text in the lower-right corner of the cards to
		// indicate how many cards the player has.
		int numCards = theHand.size();
		int maxCards = model.getMaxHandSize(playerId);

		Object [] sizeArgs = new Object [2];
		sizeArgs[0] = String.valueOf(numCards);
		sizeArgs[1] = String.valueOf(maxCards);
		String handSizeString = GameLabels.getInstance().get("hand.size", sizeArgs);

		paintHandSizeText(g, cardSpacing * (numCards-1), getHeight() - textLineHeight, handSizeString);

		// For debug, show the requested & actual bounds
		if (debugShowBounds) {
			Jgraphics.paintRects(g, getBounds(), getPreferredSize());
		}
	}

	/*
	 * Paint the area the indicates the number of cards in the hand.
	 *
	 * @param g           The graphics context to draw on.
	 * @param ul_x, ul_y  The upper-left corner to place the hand text at.
	 * @param handText    The text to draw inside the rectangle.
	 */
	private void paintHandSizeText(Graphics g, int ul_x, int ul_y, String handText) {

		// Calculate the size & location of the box
		int textWidth = handFontMetrics.stringWidth(handText);
		int boxWidth = Math.max(textWidth + 2 * TEXT_BORDER, 2 * cornerWidth);
		int boxHeight = Math.max(textLineHeight, 2 * cornerHeight);
		ul_x = Math.max(0, ul_x); // Force ul_x to 0 if less than 0.
		int lr_x = ul_x + textWidth + 2 * TEXT_BORDER;
		int lr_y = ul_y + textLineHeight;

		// Draw the corners
		int rc_x = lr_x - cornerWidth;
		int bc_y = lr_y - cornerHeight;
		Jgraphics.paintImage(g, ul_x, ul_y, JumpingImages.HAND_SIZE_CORNERS, 0, 0);
		Jgraphics.paintImage(g, rc_x, ul_y, JumpingImages.HAND_SIZE_CORNERS, 1, 0);
		Jgraphics.paintImage(g, ul_x, bc_y, JumpingImages.HAND_SIZE_CORNERS, 0, 1);
		Jgraphics.paintImage(g, rc_x, bc_y, JumpingImages.HAND_SIZE_CORNERS, 1, 1);

		// Draw the middle sections
		if (handDarkColor == null) {
			handDarkColor = new Color (0, 0, 0, 196);
		}
		g.setColor(handDarkColor);
		g.fillRect(ul_x + cornerWidth, ul_y, boxWidth - 2 * cornerWidth, boxHeight);
		g.fillRect(ul_x, ul_y + cornerHeight, cornerWidth, boxHeight - 2 * cornerHeight);
		g.fillRect(rc_x, ul_y + cornerHeight, cornerWidth, boxHeight - 2 * cornerHeight);

		// Draw the outlines around the middle sections
		g.setColor(Color.black);
		g.fillRect(ul_x + cornerWidth, ul_y, boxWidth - 2 * cornerWidth, 2);
		g.fillRect(ul_x + cornerWidth, lr_y - 2, boxWidth - 2 * cornerWidth, 2);
		g.fillRect(ul_x, ul_y + cornerHeight, 2, boxHeight - 2 * cornerHeight);
		g.fillRect(lr_x - 2, ul_y + cornerHeight, 2, boxHeight - 2 * cornerHeight);

		// Draw the box & text
		g.setFont(handFont);
		g.setColor(Color.white);
		g.drawString(handText, ul_x + TEXT_BORDER, lr_y - TEXT_BORDER - handFontMetrics.getMaxDescent());
	}


}
