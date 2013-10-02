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

import java.awt.Dimension;
import java.awt.Graphics;

import org.jogre.client.awt.JogreComponent;

import org.jogre.grandPrixJumping.common.JumpingCard;

/**
 * View of a player's immediate cards for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 *
 * Note: At some point, maybe make PlayerHandComponent & ImmCardComponent
 * both extend a common GenericHandComponent, since 90% of the code is identical.
 */
public class JumpingImmCardComponent extends JogreComponent {

	// Link to the model & graphics helper
	private JumpingClientModel model;
	private JumpingGraphics Jgraphics;

	// The playerId that we're showing
	private int playerId;

	// Parameters for drawing the cards
	private int cardSpacing;
	private int verticalOffset;
	private int cardWidth;
	private int cardHeight;
	private int wh;
	private int halfWidth;
	private int halfHeight;

	// The index of the card that is currently selected
	private int selectedCardIndex = -1;

	// The image index & half of the card that is currently activated
	// (Note: image index is the distance into the activated card images the
	//  activated card is.  The index into the player's hand is <selectedCardIndex>.)
	private int activatedCardImageIndex = -1;
	private int activatedHalf = -1;

	// The spacing between the two cards when selecting
	private static final int DUAL_RIDER_CARD_SPACING = 5;

	// For debug, setting this to true will cause the requested & actual bounds
	// of the component to be drawn.
	private boolean debugShowBounds = false;

	/**
	 * Constructor which creates the component
	 *
	 * @param model				The game model
	 * @param playerId			The playerId of the player we're showing
	 * @param cardSpacing		Card Spacing
	 * @param verticalOffset	Vertical Offset
	 */
	public JumpingImmCardComponent (	JumpingClientModel model,
										int playerId,
										int cardSpacing,
										int verticalOffset) {

		// link to model
		this.model = model;
		this.Jgraphics = JumpingGraphics.getInstance();

		// Save parameters
		this.playerId = playerId;
		this.cardSpacing = cardSpacing;
		this.verticalOffset = verticalOffset;

		// Set the prefered size of our component
		cardWidth  = Jgraphics.imageWidths [JumpingImages.CARDS];
		cardHeight = Jgraphics.imageHeights[JumpingImages.CARDS];
		halfWidth = (cardWidth / 2);
		halfHeight = (cardHeight / 2);
		wh = cardWidth * cardHeight;

		Dimension dim = new Dimension (
			cardWidth * 2 + DUAL_RIDER_CARD_SPACING,
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
			return (JumpingCard) model.getImmediateHand(playerId).elementAt(selectedCardIndex);
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
		Vector hand = model.getImmediateHand(playerId);

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
					if (card.isMarked()) {
						return setSelectedIndex(-1);
					} else {
						return setSelectedIndex(i);
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

		// The only activatable immediate card is the official (well, the only one that needs
		// to be activated and select halfs from), so if we're activating a card, it
		// must be the official card.
		activatedCardImageIndex = JumpingImages.ACTIVATED_CARDS_OFFICIAL_INDEX;

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
	 * For the dualRider cards, determine which point (x,y) is at.
	 * Return value indicates if this is a different area than is now selected.
	 */
	public boolean selectDualRiderCardAt(int x, int y) {
		int newHalf = -1;

		if (y >= verticalOffset) {
			if ((x >= 0) && (x <= cardWidth)) {
				newHalf = 0;
			} else if ((x >= cardWidth + DUAL_RIDER_CARD_SPACING) && (x <= 2*cardWidth + DUAL_RIDER_CARD_SPACING)) {
				newHalf = 1;
			}
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
	public void paintHandComponent (Graphics g) {
		// Paint cards for the player
		Jgraphics.paintHand(g, 0, 0, model.getImmediateHand(playerId), selectedCardIndex, verticalOffset, cardSpacing);

		// If we're in the mode of selecting an activated card (either immediate or in
		// the hand cards), then shadow out the cards.
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
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
 	public void paintComponent (Graphics g) {
		if (model.isDualRiderActive() && (playerId == model.getCurrentPlayer())) {
			// Paint the two rider cards
			Jgraphics.paintCard(g, 0, verticalOffset, model.getDualRiderCard(0));
			Jgraphics.paintCard(g, cardWidth + DUAL_RIDER_CARD_SPACING, verticalOffset, model.getDualRiderCard(1));

			if (activatedHalf == 0) {
				Jgraphics.paintImage(g,
						0, verticalOffset,
						JumpingImages.CARDS_ACTIVATED_SPECIALS,
						JumpingImages.ACTIVATED_CARDS_HIGHLIGHT_CARD_INDEX, 0);
				paintArrows(g, 2, 4);
			} else if (activatedHalf == 1) {
				Jgraphics.paintImage(g,
						cardWidth + DUAL_RIDER_CARD_SPACING, verticalOffset,
						JumpingImages.CARDS_ACTIVATED_SPECIALS,
						JumpingImages.ACTIVATED_CARDS_HIGHLIGHT_CARD_INDEX, 0);
				paintArrows(g, 4, 2);
			}

		} else {
			paintHandComponent(g);
		}

		// For debug, show the requested & actual bounds
		if (debugShowBounds) {
			Jgraphics.paintRects(g, getBounds(), getPreferredSize());
		}
	}

	/*
	 * This will draw the arrows on the cards to indicate which will be taken by the
	 * player and which will be given to the opponent when a dual rider card is active.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	card0Image		The image index to draw on the left card
	 * @param	card1Image		The image index to draw on the right card
	 */
	private void paintArrows(Graphics g, int card0Image, int card1Image) {
		int y = verticalOffset + halfHeight;
		Jgraphics.paintImage(g,
		                     halfWidth, y,
		                     JumpingImages.TRACK_ICONS_HIGHLIGHT,
		                     card0Image, 0);
		Jgraphics.paintImage(g,
		                     halfWidth + cardWidth + DUAL_RIDER_CARD_SPACING, y,
		                     JumpingImages.TRACK_ICONS_HIGHLIGHT,
		                     card1Image, 0);
	}

}
