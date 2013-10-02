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

import org.jogre.client.awt.JogreComponent;

import org.jogre.common.util.GameLabels;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Point;

import org.jogre.texasHoldEm.common.Card;

/**
 * TexasHoldEm board component.  This displays the playing table with the player's
 * cards and the common cards.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmBoardComponent extends JogreComponent {

	// Link to the model, graphics helper & text labels
	private TexasHoldEmClientModel model;
	private TexasHoldEmGraphics thGraphics;
	private GameLabels labels;

	// The number of players in the game
	private int numPlayers;

	// Metrics for the game font.
	// (The font is created and kept in TexasHoldEmGraphics.java)
	private FontMetrics gameFontMetrics;

	// Array used for replcement while building text strings
	private Object [] replaceArray = new Object [1];

	// The player info areas
	private TexasHoldEmPlayerComponent [] playerArea = new TexasHoldEmPlayerComponent[8];

	// The rectangle of the table itself
	private Rectangle tableRect;

	// Anchor position for the common cards.
	private Point commonCardAnchor;

	// Offset from the upper-left corner of one common card to the upper-left
	// corner of the next common card.
	private int commonCardOffset;

	// Anchor position for the current Pot value text.
	private Point potTextAnchor;

	// The anchor point for the last action text for each player
	private Point [] lastActionAnchor = new Point [8];

	// Constants for indication justification of the last action text
	private int [] lastActionJustify = {
		TexasHoldEmGraphics.CENTER_JUSTIFY, TexasHoldEmGraphics.CENTER_JUSTIFY,
		TexasHoldEmGraphics.LEFT_JUSTIFY, TexasHoldEmGraphics.CENTER_JUSTIFY,
		TexasHoldEmGraphics.CENTER_JUSTIFY, TexasHoldEmGraphics.CENTER_JUSTIFY,
		TexasHoldEmGraphics.RIGHT_JUSTIFY, TexasHoldEmGraphics.CENTER_JUSTIFY
	};

	// These are the keys for the text for the last action strings
	private String [] actionKeys = {
		"text.lastAction.fold",
		"text.lastAction.bid",
		"text.lastAction.raise",
		"text.lastAction.call",
		"text.lastAction.allIn",
		"text.lastAction.thinking"
	};

	// The size of an individual player area
	private Dimension playerAreaDim;

	// Parameters for drawing the table area.
	private static final int PLAYER_TO_PLAYER_HORIZONTAL_SPACING = 30;
	private static final int ADDITIONAL_PLAYER_HORIZONTAL_SPACING = 5;
	private static final int TABLE_OUTLINE_THICKNESS = 8;
	private static final int PLAYER_HIGHLIGHT_THICKNESS = 8;
	private static final int OVERALL_BORDER = 5;
	private static final int TEXT_BORDER = 4;
	private static final int COMMON_CARD_SPACING = 5;

// RAW: Maybe change the seating a bit so that the left & right sides are
//      only used for 7 & 8 player games.  And then, make the board narrower
//      when the sides aren't used.  That will make the game window not waste
//      the horizontal space when it isn't needed.
	private static final int [][] seatAssignments = {
		{0, -1, -1, -1,  1, -1, -1, -1},		// Two players
		{0, -1, -1,  1, -1,  2, -1, -1},		// Three players
		{0, -1,  1, -1,  2, -1,  3, -1},		// Four players
		{0, -1,  1,  2,  3, -1,  4, -1},		// Five players
		{0,  1, -1,  2,  3,  4, -1,  5},		// Six players
		{0,  1, -1,  2,  3,  4,  5,  6},		// Seven players
		{0,  1,  2,  3,  4,  5,  6,  7}			// Eight players
	};

	/**
	 * Constructor which creates the boardcomponent
	 *
	 * @param model					The game model
	 */
	public TexasHoldEmBoardComponent (TexasHoldEmClientModel model) {

		// Save parameters provided
		this.model = model;
		thGraphics = TexasHoldEmGraphics.getInstance();
		labels = GameLabels.getInstance();

		// Get the Font Metrics for the font we're using
		gameFontMetrics = getFontMetrics(thGraphics.gameFont);

		// Calculate the largest size required to display any currency string
		// in the game font & localization.  Then tell the graphics that size
		// so that it's available to other components.
		Dimension dim = CalculateLargestDollarDimension();
		thGraphics.setLargestCurrencyDimension(dim);

		// Create the player areas
		this.numPlayers = model.getNumPlayers();
		for (int i=0; i < 8 ; i++) {
			playerArea[i] = new TexasHoldEmPlayerComponent (
				model, seatAssignments[numPlayers-2][i], gameFontMetrics, dim
			);
		}

		// Now that we know the size of a player area, create the full table
		Dimension boardDim = createFullTable();

		// Set the preferred dimension
		setPreferredSize ( boardDim );
	}

	/*
	 * This will calculate the dimensions of the table component.
	 */
	private Dimension createFullTable() {
		// Get the size of a player area;
		playerAreaDim = playerArea[0].getOverallSize();

		// Get the height size of a line of text.
		int textHeight = gameFontMetrics.getMaxAscent() +
		                 gameFontMetrics.getMaxDescent() +
		                 TEXT_BORDER * 2;

		// Position the player areas
		int p2x = OVERALL_BORDER;
		int p1x = (playerAreaDim.width / 2) - (TABLE_OUTLINE_THICKNESS / 2) +
		           thGraphics.imageWidths[TexasHoldEmImages.TABLE_CORNERS] +
		           ADDITIONAL_PLAYER_HORIZONTAL_SPACING
		           + OVERALL_BORDER;
		int p0x = p1x + playerAreaDim.width + PLAYER_TO_PLAYER_HORIZONTAL_SPACING;
		int p7x = p0x + playerAreaDim.width + PLAYER_TO_PLAYER_HORIZONTAL_SPACING;
		int p6x = p7x + (playerAreaDim.width / 2) + ADDITIONAL_PLAYER_HORIZONTAL_SPACING +
		           thGraphics.imageWidths[TexasHoldEmImages.TABLE_CORNERS] -
		           (TABLE_OUTLINE_THICKNESS / 2);
		int p3y = OVERALL_BORDER;
		int p2y = p3y + playerAreaDim.height + textHeight;
		int p1y = p2y + playerAreaDim.height + 2 * textHeight;

		playerArea[0].translate(p0x, p1y);
		playerArea[1].translate(p1x, p1y);
		playerArea[2].translate(p2x, p2y);
		playerArea[3].translate(p1x, p3y);
		playerArea[4].translate(p0x, p3y);
		playerArea[5].translate(p7x, p3y);
		playerArea[6].translate(p6x, p2y);
		playerArea[7].translate(p7x, p1y);

		// Position the center card & Pot text area
		commonCardOffset = thGraphics.imageWidths[TexasHoldEmImages.CARDS] + COMMON_CARD_SPACING;
		int middleCardAreaHeight =
			thGraphics.imageHeights[TexasHoldEmImages.CARDS] +
			textHeight;
		int middleCardAreaWidth =
			thGraphics.imageWidths[TexasHoldEmImages.CARDS] * 5 +
			COMMON_CARD_SPACING * 4;
		commonCardAnchor = new Point (
			p0x + (playerAreaDim.width / 2) - (middleCardAreaWidth / 2),
			p2y + textHeight
		);
		potTextAnchor = new Point (
			p0x + (playerAreaDim.width / 2),
			p2y + textHeight - gameFontMetrics.getMaxDescent() - TEXT_BORDER
		);

		// Determine whether the sides or the middle needs to move down to make room for the other.
		int playerSideAreaHeight = playerAreaDim.height + textHeight;
		int deltaY = (middleCardAreaHeight - playerSideAreaHeight) / 2;
		if (deltaY > 0) {
			// Sides move down.
			playerArea[0].translate(0, deltaY);
			playerArea[1].translate(0, deltaY);
			playerArea[2].translate(0, deltaY);
			playerArea[6].translate(0, deltaY);
			playerArea[7].translate(0, deltaY);
		} else {
			// Middle moves down.
			commonCardAnchor.translate(0, -deltaY);
			potTextAnchor.translate(0, -deltaY);
		}

		// Set the anchor locations of the last action text
		for (int i=0; i<8; i++) {
			lastActionAnchor[i] = new Point (playerArea[i].getLocation());
			if ((i==0) || (i==1) || (i==7)) {
				lastActionAnchor[i].translate(playerAreaDim.width / 2, - gameFontMetrics.getMaxDescent() - TEXT_BORDER);
			} else{
				lastActionAnchor[i].translate(playerAreaDim.width / 2, playerAreaDim.height + textHeight - gameFontMetrics.getMaxDescent() - TEXT_BORDER);
			}
		}
		lastActionAnchor[2].translate((TABLE_OUTLINE_THICKNESS / 2) + TEXT_BORDER, 0);
		lastActionAnchor[6].translate(-((TABLE_OUTLINE_THICKNESS / 2) + TEXT_BORDER), 0);

		// Return the overall table size
		int DealerButtonHangDown = (thGraphics.imageHeights[TexasHoldEmImages.DEALER_BUTTON] / 2) - OVERALL_BORDER; // playerPoint.y + playerAreaDim.height,
		Dimension tableComponentSize = new Dimension (
			p6x + playerAreaDim.width + OVERALL_BORDER,
		    p1y + playerAreaDim.height + OVERALL_BORDER + DealerButtonHangDown);

		tableRect = new Rectangle(p2x + playerAreaDim.width / 2 - TABLE_OUTLINE_THICKNESS / 2,
		                          p3y + playerAreaDim.height / 2 - TABLE_OUTLINE_THICKNESS / 2,
		                          tableComponentSize.width - playerAreaDim.width,
		                          tableComponentSize.height - playerAreaDim.height - DealerButtonHangDown);

		return tableComponentSize;
	}

	// This array is the values to use to encode to find the largest sized string
	int [] testArray = {10000, 11110, 12220, 13330, 14440, 15550, 16660, 17770, 18880, 19990, 20000};

	private Dimension CalculateLargestDollarDimension() {
		int maxHeight = gameFontMetrics.getMaxAscent() +
		                gameFontMetrics.getMaxDescent();
		int maxWidth = -1;

		// Go through the test array, and find the maximum width
		for (int i = 0; i < testArray.length; i++) {
			maxWidth = Math.max(maxWidth, gameFontMetrics.stringWidth(thGraphics.currencyFormatter.format(testArray[i])));
		}

		return new Dimension (maxWidth, maxHeight);
	}

	/**
	 * Set the seatNumber for the player in the main player area, and move
	 * the other players around the table.
	 */
	public void setSeatZeroPlayer(int seatNum) {
		if (seatNum < 0) { seatNum = 0; }

		int currSeatNum = seatNum;
		for (int i = 0; i < 8; i++) {
			if (seatAssignments[numPlayers-2][i] >= 0) {
				playerArea[i].setSeatNumber(currSeatNum);
				currSeatNum = ((currSeatNum + 1) % numPlayers);
			}
		}
	}

	/**
	 * Paint the component
	 *
	 * @param g		The graphics context to draw on.
	 */
	public void paintComponent (Graphics g) {
		// Set the font of the graphics context to the game font.
		thGraphics.setGameFont(g);

		// Draw the table
		thGraphics.drawNiceRoundRectangle (
			g,
			tableRect,
			TexasHoldEmImages.TABLE_CORNERS,
			TABLE_OUTLINE_THICKNESS,
			thGraphics.getTableColor()
		);

		// Draw each of the players
		int dealer = model.getCurrentDealer();
		for (int i = 0; i < 8; i++) {
			int seatNum = playerArea[i].getSeatNumber();
			if (seatNum >= 0) {
				playerArea[i].paintComponent(g);
				drawLastActionText(g, i, seatNum);
				if (seatNum == dealer) {
					Point playerPoint = playerArea[i].getLocation();
					thGraphics.paintImage(g,
						playerPoint.x, playerPoint.y + playerAreaDim.height,
						TexasHoldEmImages.DEALER_BUTTON,
						0, 0);
				}
			}
		}

		// Draw the common cards
		int dx = commonCardAnchor.x;
		int dy = commonCardAnchor.y;
		for (int i=0; i<5; i++) {
			Card theCard = model.getCommonCard(i);
			if (theCard.isVisible()) {
				thGraphics.paintCard(g, dx, dy, theCard);
			} else {
				thGraphics.paintImage(g, dx, dy, TexasHoldEmImages.JOGRE_TEXT, i, 0);
			}
			dx += commonCardOffset;
		}

		// Draw the current amount of the Pot
		replaceArray[0] = thGraphics.currencyFormatter.format(model.getPotValue());
		String potValueString = labels.get("text.potValue", replaceArray);
		thGraphics.drawCenterJustifiedText(g, potValueString, potTextAnchor);
	}

	/*
	 * This method will draw the "last action text" for the given player
	 *
	 * @param g					The graphics context to draw on.
	 * @param placementIndex	The place around the table to draw.
	 * @param seatNum			The seat number of the player to draw.
	 */
	private void drawLastActionText(Graphics g, int placementIndex, int seatNum) {
		int lastAction = model.getLastAction(seatNum);
		if (lastAction >= 0) {
			replaceArray[0] = thGraphics.currencyFormatter.format(model.getCurrentRoundBid(seatNum));
			String actionString =
			    (lastAction == TexasHoldEmClientModel.LAST_ACTION_BLANK) ?
			         "" : labels.get(actionKeys[lastAction], replaceArray);
			thGraphics.drawJustifiedText(g, lastActionJustify[placementIndex], actionString, lastActionAnchor[placementIndex]);
		}
	}

}
