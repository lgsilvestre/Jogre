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
import org.jogre.common.util.GameProperties;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Point;

/**
 * TexasHoldEm history component.
 * This displays the playing table of the last hand and is used to 
 * show what all of the player cards were and who won.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmHistoryComponent extends JogreComponent {

	// Link to the model, graphics helper & text labels
	private TexasHoldEmClientModel model;
	private TexasHoldEmGraphics thGraphics;
	private GameLabels labels;

	// The number of players in the game
	private int numPlayers;
	
	// The rectangle of the table itself
	private Rectangle tableRect;

	// Anchor position for the common cards
	private Point commonCardAnchor;

	// Anchor position for the hand type text
	private Point handTypeAnchor;

	// Anchor positions & rectangles for the player areas
	private Point [] playerAreaAnchor = new Point [8];
	private Rectangle [] playerAreaRect = new Rectangle [8];
	private Point [][] playerCardAnchor = new Point [8][2];

	// Anchor position for the highlight image
	private Point [] playerHighlightPoint = new Point [8];

	// Which player is seated at which logical place at the table.
	private int [] playerAreaSeatNumber = new int [8];

	// Offset from the upper-left corner of one common card to the upper-left
	// corner of the next common card.
	private int commonCardOffset;

	// The index into the winnerHighlight graphic to use for highlighting
	private int winnerHighlightIndex;

	// These are the keys for the text for the hand type strings
	private String [] handTypeKeys = {
		"text.handValue.unknown",
		"text.handValue.nothing",
		"text.handValue.onePair",
		"text.handValue.twoPair",
		"text.handValue.threeKind",
		"text.handValue.stright",
		"text.handValue.flush",
		"text.handValue.fullHouse",
		"text.handValue.fourKind",
		"text.handValue.straightFlush",
		"text.handValue.royalFlush"
	};

	// Parameters for drawing the history area.
	private static final int CARD_TO_CARD_SPACING = 2;
	private static final int PLAYER_AREA_BORDER_SPACING = 4;
	private static final int PLAYER_TO_PLAYER_HORIZONTAL_SPACING = 11;
	private static final int ADDITIONAL_PLAYER_HORIZONTAL_SPACING = 5;
	private static final int TABLE_OUTLINE_THICKNESS = 2;
	private static final int PLAYER_OUTLINE_THICKNESS = 2;
	private static final int PLAYER_HIGHLIGHT_THICKNESS = 8;
	private static final int OVERALL_BORDER = 5;
	private static final int VERT_TEXT_SPACING = 5;

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
	 * Constructor which creates the history component
	 *
	 * @param model					The game model
	 */
	public TexasHoldEmHistoryComponent (TexasHoldEmClientModel model) {

		// Save parameters provided
		this.model = model;
		thGraphics = TexasHoldEmGraphics.getInstance();
		labels = GameLabels.getInstance();

		// Initialize things
		this.numPlayers = model.getNumPlayers();
		setSeatZeroPlayer(0);

		// Read preferences
		GameProperties props = GameProperties.getInstance();
		winnerHighlightIndex = props.getInt("winnerHighlightIndex", 0);

		// Create the table geometry
		Dimension boardDim = createTableGeometry();
		
		// Set the preferred dimension
		setPreferredSize ( boardDim );
	}

	/*
	 * This will calculate the dimensions of the table component and place
	 * the various pieces within it.
	 */
	private Dimension createTableGeometry() {
		// Get the Font Metrics for the font we're using
		FontMetrics gameFontMetrics = getFontMetrics(thGraphics.gameFont);

		// Calculate the largest size required to display any of the text strings that are used
		Dimension textDim = CalculateMaxTextDimension(gameFontMetrics);

		int cardHeight = thGraphics.imageHeights[TexasHoldEmImages.TINY_CARDS];
		int cardWidth  = thGraphics.imageWidths [TexasHoldEmImages.TINY_CARDS];

		// Compute the size of a player area
		Dimension playerAreaDim = new Dimension (
			cardWidth * 2 + CARD_TO_CARD_SPACING + PLAYER_AREA_BORDER_SPACING * 2,
			cardHeight + PLAYER_AREA_BORDER_SPACING * 2
		);

		// Position the player areas
		int p2x = OVERALL_BORDER;
		int p1x = (playerAreaDim.width / 2) - (TABLE_OUTLINE_THICKNESS / 2) +
		           thGraphics.imageWidths[TexasHoldEmImages.PLAYER_AREA_CORNERS] +
		           ADDITIONAL_PLAYER_HORIZONTAL_SPACING
		           + OVERALL_BORDER;
		int p0x = p1x + playerAreaDim.width + PLAYER_TO_PLAYER_HORIZONTAL_SPACING;
		int p7x = p0x + playerAreaDim.width + PLAYER_TO_PLAYER_HORIZONTAL_SPACING;
		int p6x = p7x + (playerAreaDim.width / 2) + ADDITIONAL_PLAYER_HORIZONTAL_SPACING +
		           thGraphics.imageWidths[TexasHoldEmImages.PLAYER_AREA_CORNERS] -
		           (TABLE_OUTLINE_THICKNESS / 2);
		int p3y = OVERALL_BORDER;
		int p2y = p3y + playerAreaDim.height;
		int p1y = p2y + playerAreaDim.height;

		playerAreaAnchor[0] = new Point (p0x, p1y);
		playerAreaAnchor[1] = new Point (p1x, p1y);
		playerAreaAnchor[2] = new Point (p2x, p2y);
		playerAreaAnchor[3] = new Point (p1x, p3y);
		playerAreaAnchor[4] = new Point (p0x, p3y);
		playerAreaAnchor[5] = new Point (p7x, p3y);
		playerAreaAnchor[6] = new Point (p6x, p2y);
		playerAreaAnchor[7] = new Point (p7x, p1y);

		// Use the anchors to place other items
		for (int i=0; i < 8; i++) {
			playerAreaRect[i] = new Rectangle (	playerAreaAnchor[i].x,
												playerAreaAnchor[i].y,
												playerAreaDim.width,
												playerAreaDim.height);
			playerCardAnchor[i][0] = new Point ( playerAreaAnchor[i].x + PLAYER_AREA_BORDER_SPACING,
												 playerAreaAnchor[i].y + PLAYER_AREA_BORDER_SPACING);
			playerCardAnchor[i][1] = new Point ( playerAreaAnchor[i].x + PLAYER_AREA_BORDER_SPACING + cardWidth + CARD_TO_CARD_SPACING,
												 playerAreaAnchor[i].y + PLAYER_AREA_BORDER_SPACING);
			playerHighlightPoint[i] = new Point (playerAreaAnchor[i].x + (playerAreaDim.width / 2),
												 playerAreaAnchor[i].y + (playerAreaDim.height / 2));
		}

		// Position the center cards
		commonCardOffset = cardWidth + CARD_TO_CARD_SPACING;
		int middleCardAreaHeight = cardHeight;
		int middleCardAreaWidth = cardWidth * 5 + CARD_TO_CARD_SPACING * 4;

		commonCardAnchor = new Point (
			p0x + (playerAreaDim.width / 2) - (middleCardAreaWidth / 2),
			p2y + (playerAreaDim.height / 2) - (middleCardAreaHeight / 2)
		);

		// Position the hand type text
		handTypeAnchor = new Point (
			p0x + (playerAreaDim.width / 2),
			p1y + playerAreaDim.height + VERT_TEXT_SPACING + gameFontMetrics.getMaxAscent()
		);
		
		// Return the overall table size
		Dimension tableComponentSize = new Dimension (
			p6x + playerAreaDim.width + OVERALL_BORDER,
		    p1y + playerAreaDim.height + textDim.height + OVERALL_BORDER + VERT_TEXT_SPACING);

		tableRect = new Rectangle(p2x + playerAreaDim.width / 2 - TABLE_OUTLINE_THICKNESS / 2,
		                          p3y + playerAreaDim.height / 2 - TABLE_OUTLINE_THICKNESS / 2,
		                          p6x,
		                          p1y );

		return tableComponentSize;
	}

	/*
	 * Calculate the maximum size of the text rectangle needed to draw any of
	 * the hand type strings.
	 */
	private Dimension CalculateMaxTextDimension(FontMetrics gameFontMetrics) {
		int maxHeight = gameFontMetrics.getMaxAscent() +
		                gameFontMetrics.getMaxDescent();
		int maxWidth = -1;

		// Go through all of the strings and find the maximum width
		for (int i = 0; i < handTypeKeys.length; i++) {
			maxWidth = Math.max(maxWidth, gameFontMetrics.stringWidth(labels.get(handTypeKeys[i])));
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
				playerAreaSeatNumber[i] = currSeatNum;
				currSeatNum = ((currSeatNum + 1) % numPlayers);
			} else {
				playerAreaSeatNumber[i] = -1;
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
			TexasHoldEmImages.PLAYER_AREA_CORNERS,
			TABLE_OUTLINE_THICKNESS,
			thGraphics.getTableColor()
		);

		// Draw each of the players
		for (int i = 0; i < 8; i++) {
			paintPlayerArea(g, i, playerAreaSeatNumber[i]);
		}

		// Draw the common cards
		int dx = commonCardAnchor.x;
		int dy = commonCardAnchor.y;
		for (int i=0; i<5; i++) {
			thGraphics.paintTinyCard(g, dx, dy, model.getHistoryCommonCard(i));
			dx += commonCardOffset;
		}

		// Draw the type of hand text
		int handType = model.getHistoryHandType();
		if (handType >= 0) {
			thGraphics.drawJustifiedText(g, thGraphics.CENTER_JUSTIFY, labels.get(handTypeKeys[handType]), handTypeAnchor);
		}
	}

	private void paintPlayerArea(Graphics g, int physicalIndex, int logicalPlayer) {
		if (logicalPlayer >= 0) {
			// If this player has won, then draw the winning highlight image
			if (model.getHistoryPlayerHighlight(logicalPlayer)) {
				thGraphics.paintImage(
					g,
					playerHighlightPoint[physicalIndex].x,
					playerHighlightPoint[physicalIndex].y,
					TexasHoldEmImages.WINNER_HIGHLIGHT,
					winnerHighlightIndex, 0);
			}

			// Draw the background round rectangle
			thGraphics.drawNiceRoundRectangle (
				g,
				playerAreaRect[physicalIndex],
				TexasHoldEmImages.PLAYER_AREA_CORNERS,
				PLAYER_OUTLINE_THICKNESS,
				thGraphics.getPlayerColor(logicalPlayer));

			// Draw the cards
			thGraphics.paintTinyCard(g, playerCardAnchor[physicalIndex][0], model.getHistoryPlayerCard(logicalPlayer, 0));
			thGraphics.paintTinyCard(g, playerCardAnchor[physicalIndex][1], model.getHistoryPlayerCard(logicalPlayer, 1));
		}
	}

}
