/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;


/**
 * Helper file to display a single player's information area
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmPlayerComponent {

	// Link to the model & graphics helper
	private TexasHoldEmClientModel model;

	// Link to the graphics helper
	private TexasHoldEmGraphics thGraphics;

	// The seat number for the player to display.
	private int seatNum;

	// The color for the player.
	private Color playerColor;

	// Points that are the offsets from the upper-left corner of this
	// player display area to where certain features should be drawn.
	private Point [] cardOffset = new Point[2];
	private Point holdingsOffset;
	private Point potEquityOffset;

	// The overall size of the player information area.
	private Dimension overallSize;

	// A rectangle of the overall player information area.
	private Rectangle placementRect;

	// Parameters for placing the parts in the display area.
	private final static int BORDER_SPACING = 5;
	private final static int CARD_OVERLAP = 13;
	private final static int PLAYER_TEXT_HEIGHT_SPACING = 5;
	private final static int CARD_TO_TEXT_SPACING = 5;

	/**
	 * Constructor which creates the player component
	 *
	 * @param model					The game model
	 * @param seatNum				The seat number of the player to display
	 * @param currencyFormatter		A formatter to display currency in localized fashion.
	 * @param dollarBoxDimension	The size to reserve for the two currency boxes
	 * @param textDescent			The maximum descent for text.
	 */
	public TexasHoldEmPlayerComponent (	TexasHoldEmClientModel model,
										int seatNum,
										FontMetrics gameFontMetrics,
										Dimension dollarBoxDimension )
	{

		// Save parameters provided
		this.model = model;
		this.thGraphics = TexasHoldEmGraphics.getInstance();
		setSeatNumber(seatNum);

		// Calculate the position & size of the various parts of this component.
		setPlayerAreaGeometry(dollarBoxDimension, gameFontMetrics.getMaxDescent());
	}

	/*
	 * This will calculate various parameters for the geometry of the player
	 * area display.
	 *
	 * @param dollarBoxDimension	The size to reserve for the two currency boxes
	 * @param textDescent			The maximum descent for text.
	 */
	private void setPlayerAreaGeometry (Dimension dollarBoxDimension, int textDescent) {
		int overallHeight;

		// Calculate the dimensions of the card area
		int cardHeight = thGraphics.imageHeights[TexasHoldEmImages.CARDS];
		int cardWidth =  thGraphics.imageWidths [TexasHoldEmImages.CARDS];

		// Calculate the dimensions of the text area
		int textHeight = 2*dollarBoxDimension.height + PLAYER_TEXT_HEIGHT_SPACING;
		int textWidth = dollarBoxDimension.width;

		// Initialize the first card & holdings points.
		cardOffset[0] = new Point (BORDER_SPACING, BORDER_SPACING);
		holdingsOffset = new Point (
		        BORDER_SPACING + CARD_OVERLAP + cardWidth + CARD_TO_TEXT_SPACING + textWidth,
		        BORDER_SPACING + dollarBoxDimension.height - textDescent);

		// Adjust the one that needs to be centered
		if (cardHeight > textHeight) {
			// Card Height sets the height of the overall box
			holdingsOffset.translate(0, ((cardHeight - textHeight) / 2));
			overallHeight = cardHeight + 2*BORDER_SPACING;
		} else {
			// Text Height set the height of the overall box
			cardOffset[0].translate(0, ((textHeight - cardHeight) / 2));
			overallHeight = textHeight + 2*BORDER_SPACING;
		}

		// Set the locations of the second card & pot equity based on the first points
		cardOffset[1] = new Point (cardOffset[0]);
		cardOffset[1].translate(CARD_OVERLAP, 0);

		potEquityOffset = new Point (holdingsOffset);
		potEquityOffset.translate(0, dollarBoxDimension.height + PLAYER_TEXT_HEIGHT_SPACING);

		overallSize = new Dimension (holdingsOffset.x + BORDER_SPACING, overallHeight);
		placementRect = new Rectangle (0, 0, holdingsOffset.x + BORDER_SPACING, overallHeight);
	}

	/**
	 * Return the overall size of the playerComponent.
	 * @return the overall size.
	 */
	public Dimension getOverallSize() {
		return overallSize;
	}

	/**
	 * Translate the player component by (dx, dy).
	 *
	 * @param dx	The amount to change the x coordinate by
	 * @param dy	The amount to change the y coordinate by
	 */
	public void translate(int dx, int dy) {
		cardOffset[0].translate(dx,dy);
		cardOffset[1].translate(dx,dy);
		holdingsOffset.translate(dx,dy);
		potEquityOffset.translate(dx,dy);
		placementRect.translate(dx,dy);
	}

	/**
	 * Get the current location of the upper left corner of the component.
	 * @return the upper left corner.
	 */
	public Point getLocation() {
		return new Point (placementRect.x, placementRect.y);
	}

	/**
	 * Set the seat # that this player area should be displaying.
	 * Setting the seat # to -1 will result in this area being invisible.
	 *
	 * @param seatNum	The new seat number to show.
	 */
	public void setSeatNumber(int seatNum) {
		this.seatNum = seatNum;
		if (seatNum >= 0) {
			this.playerColor = thGraphics.getPlayerColor(seatNum);
		}
	}

	/**
	 * Return the current seat number that this place is showing.
	 *
	 * @return the seat number current showing.
	 */
	public int getSeatNumber() {
		return seatNum;
	}

	/**
	 * Paint the component.
	 *
	 * @param g		The graphics context to draw on.
	 */
	public void paintComponent (Graphics g) {
		if (seatNum >= 0) {

			// Draw the background round rectangle
			thGraphics.drawNiceRoundRectangle (
				g,
				placementRect,
				TexasHoldEmImages.PLAYER_AREA_CORNERS,
				2,
				playerColor);

			// Draw the cards
			thGraphics.paintCard(g, cardOffset[0], model.getPlayerCard(seatNum, 0));
			thGraphics.paintCard(g, cardOffset[1], model.getPlayerCard(seatNum, 1));

			// Draw the text boxes
			thGraphics.drawRightJustifiedText(
				g,
				thGraphics.currencyFormatter.format(model.getPlayerHoldings(seatNum)),
				holdingsOffset
			);

			thGraphics.drawRightJustifiedText(
				g,
				thGraphics.currencyFormatter.format(model.getPotEquity(seatNum)),
				potEquityOffset
			);
		}
	}
}
