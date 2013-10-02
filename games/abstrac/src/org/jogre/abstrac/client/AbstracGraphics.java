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

import org.jogre.abstrac.std.Card;
import org.jogre.abstrac.std.ICardRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.jogre.client.awt.GameImages;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

// Abstrac graphics - Support functions for drawing things
public class AbstracGraphics implements ICardRenderer, INumberRenderer {

	// Static instance provided by the static factory method getInstance()
	private static AbstracGraphics myInstance = null;

	// The pictures of the card faces
	private ImageIcon cardImagesIcon;
	private Image cardImages;
	private int cardWidth, cardHeight;

	// The pictures of the suit Markers
	private ImageIcon suitMarkerImagesIcon;
	private Image suitMarkerImages;
	private int suitMarkerWidth, suitMarkerHeight;

	// The pictures of the digits
	private ImageIcon digitImagesIcon;
	private Image digitImages;
	private int digitWidth, digitHeight;

	// The colors to use for each of the players
	private Color [] playerColors;

	// Values for the markers
	public static final int VALUE_9_MARKER = 4;
	public static final int DASH_MARKER = 10;
	public static final int HIGHLIGHT_STAR_MARKER = 11;
	public static final int GOLD_STAR_MARKER = 12;
	public static final int SHADOW_STAR_MARKER = 13;

	// These images are in the digits image and are drawn by using
	// paintSingleDigit with these values.
	public static final int TIMES_MARKER = 10;
	public static final int EQUALS_MARKER = 11;


	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 */
	private AbstracGraphics() {

		// Load the picture of the cards
		cardImagesIcon = GameImages.getImageIcon (AbstracImages.CARDS);
		cardImages = cardImagesIcon.getImage();
		cardWidth = cardImagesIcon.getIconWidth() / 6;
		cardHeight = cardImagesIcon.getIconHeight() / 4;

		// Load the picture of the suit markers
		suitMarkerImagesIcon = GameImages.getImageIcon (AbstracImages.SUIT_MARKERS);
		suitMarkerImages = suitMarkerImagesIcon.getImage();
		suitMarkerWidth = suitMarkerImagesIcon.getIconWidth() / 14;
		suitMarkerHeight = suitMarkerImagesIcon.getIconHeight();

		// Load the picture of the digits
		digitImagesIcon = GameImages.getImageIcon (AbstracImages.DIGITS);
		digitImages = digitImagesIcon.getImage();
		digitWidth = digitImagesIcon.getIconWidth() / 12;
		digitHeight = digitImagesIcon.getIconHeight();

		// Generate the player colors by reading from the properties file
		playerColors = new Color [2];
		GameProperties props = GameProperties.getInstance();

		for (int i=0; i<2; i++) {
			playerColors[i] = JogreUtils.getColorDelta(
								JogreUtils.getColour (props.get("player.colour." + i)), 160);
		}
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	static AbstracGraphics getInstance() {
		if (myInstance == null) {
			myInstance = new AbstracGraphics();
		}

		return myInstance;
	}

	/**
	 * Return the color for a given player.
	 *
	 * @param	playerId	The player whose color is wanted.
	 */
	public Color getPlayerColor(int playerId) {
		return playerColors[(playerId % 2)];
	}

	/**
	 * Paint a special marker at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the marker.
	 * @param	y			Y coordinate of where to draw the marker.
	 * @param	markerNum	The special marker to draw
	 */
	public void paintSpecialMarker (Graphics g, int x, int y, int markerNum) {
		// Note: The special markers are in the same image as the suit markers...
		int sx = suitMarkerWidth * markerNum;
		g.drawImage(suitMarkerImages,
					x, y,											// dx1, dy1
					x + suitMarkerWidth, y + suitMarkerHeight,		// dx2, dy2
					sx, 0,											// sx1, sy1
					sx + suitMarkerWidth, suitMarkerHeight,			// sx2, sy2
					null);
	}

	//==========================================================================
	// Methods for "ICardRenderer"
    //==========================================================================

	/**
	 * Return the width of a card, in pixels.
	 *
	 * @return	the width of a card, in pixels.
	 */
	public int getCardWidth() {
		return cardWidth;
	}

	/**
	 * Return the height of a card, in pixels.
	 *
	 * @return	the height of a card, in pixels.
	 */
	public int getCardHeight() {
		return cardHeight;
	}

	/**
	 * Return the width of a suit marker, in pixels.
	 *
	 * @return	the width of a suit marker, in pixels.
	 */
	public int getSuitWidth() {
		return suitMarkerWidth;
	}

	/**
	 * Return the height of a suit marker, in pixels.
	 *
	 * @return	the height of a suit marker, in pixels.
	 */
	public int getSuitHeight() {
		return suitMarkerHeight;
	}

	/**
	 * Paint a card at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theCard		The Card to draw
	 */
	public void paintCard(Graphics g, int x, int y, Card theCard) {
		// If clear, then nothing to draw
		if (!theCard.isVisible()) {
			return;
		}

		int sx = theCard.cardValue() * cardWidth;
		int sy = theCard.cardSuit() * cardHeight;
		g.drawImage(cardImages,
					x, y,								// dx1, dy1
					x + cardWidth, y + cardHeight,		// dx2, dy2
					sx, sy,								// sx1, sy1
					sx + cardWidth, sy + cardHeight,	// sx2, sy2
					null);
	}

	/**
	 * Paint a card prompt at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the prompt.
	 * @param	y			Y coordinate of where to draw the prompt.
	 */
	public void paintPrompt(Graphics g, int x, int y) {
		// Abstrac doesn't have a prompt
	}

	/**
	 * Paint a suit marker at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the marker.
	 * @param	y			Y coordinate of where to draw the marker.
	 * @param	theSuit		The suit to draw, with -1 being "noTrump" suit
	 */
	public void paintSuitMarker (Graphics g, int x, int y, int theSuit) {
		if ((theSuit >= 0) && (theSuit <= 3)) {
			int sx = suitMarkerWidth * theSuit;
			g.drawImage(suitMarkerImages,
						x, y,											// dx1, dy1
						x + suitMarkerWidth, y + suitMarkerHeight,		// dx2, dy2
						sx, 0,											// sx1, sy1
						sx + suitMarkerWidth, suitMarkerHeight,			// sx2, sy2
						null);
		}
	}

	//==========================================================================
	// Methods for "INumberRenderer"
    //==========================================================================

	/**
	 * Return the width of a digit, in pixels.
	 *
	 * @return	the width of a digit, in pixels.
	 */
	public int getDigitWidth() {
		return digitWidth;
	}

	/**
	 * Return the height of a digit, in pixels.
	 *
	 * @return	the height of a digit, in pixels.
	 */
	public int getDigitHeight() {
		return digitHeight;
	}

	/**
	 * Paint a single digit number at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theNum		The Number to draw (0..9)
	 */
	public void paintSingleDigit (Graphics g, int x, int y, int num) {
		int sx = digitWidth * num;
		g.drawImage(digitImages,
					x, y,									// dx1, dy1
					x + digitWidth, y + digitHeight,		// dx2, dy2
					sx, 0,									// sx1, sy1
					sx + digitWidth, digitHeight,			// sx2, sy2
					null);
	}

	/**
	 * Paint a double digit number at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theNum		The Number to draw (0..99)
	 * @param	spacing		The number of pixels to put between the digits
	 */
	public void paintDoubleDigit (Graphics g, int x, int y, int num, int spacing) {
		int tens = num / 10;
		int ones = num % 10;
		if (tens != 0) {
			paintSingleDigit (g, x, y, tens);
		}
		paintSingleDigit (g, x + digitWidth + spacing, y, ones);
	}

	/**
	 * Paint a triple digit number at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theNum		The Number to draw (0..999)
	 * @param	spacing		The number of pixels to put between the digits
	 */
	public void paintTripleDigit (Graphics g, int x, int y, int num, int spacing) {
		int hundreds = num / 100;
		int tens = (num / 10) % 10;
		int ones = num % 10;
		if (hundreds != 0) {
			paintSingleDigit (g, x, y, hundreds);
		}
		if ((tens != 0)  || (hundreds != 0)) {
			paintSingleDigit (g, x + digitWidth + spacing, y, tens);
		}
		paintSingleDigit (g, x + (digitWidth + spacing) * 2, y, ones);
	}
}
