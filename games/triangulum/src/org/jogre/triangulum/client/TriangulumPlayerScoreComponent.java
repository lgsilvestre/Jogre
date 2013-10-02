/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2004 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.client;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

import java.util.Vector;
import java.util.ListIterator;

import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;

import org.jogre.common.util.GameProperties;
import org.jogre.client.awt.JogreComponent;

/**
 * A helper class that displays the scores for a game of Triangulum.
 *
 * Note: This is called a "component", but it isn't actually a real component
 *       by itself.  It sits within the main Triangulum Component.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumPlayerScoreComponent {

	// Link to the model
	protected TriangulumModel model;

	// Link to the graphics helper routines
	private TriangulumGraphics spGraphics;

	// The number of players in the game
	private int numPlayers;

	// The player number that should be shown at the top of the list
	private int firstPlayerSeatNum;

	// My anchor location within the larger component.
	private Point myAnchor;

	// Width & Height of an individual player's score box
	private int scoreRectWidth;
	private int scoreRectHeight;

	// Locations for the text
	private Point [] scoreTextAnchor = new Point [5];

	// Black color
	private final static Color blackColor = new Color (0,0,0);

	// The font to use to draw the score text
	private Font gameFont;
	private FontMetrics gameFontMetrics;

	// The default size of the font to use for the score display
	private static final int DEFAULT_FONT_SIZE = 24;
	private static final String DEFAULT_FONT_NAME = "Dialog";
	private static final int TEXT_HEIGHT_BORDER = 3;

	// Constructor which creates the score component
	public TriangulumPlayerScoreComponent (JogreComponent parentComponent,
	                                        TriangulumModel model,
	                                        Point myAnchor) {

		// Link to the model & graphics helper
		this.model = model;
		this.spGraphics = TriangulumGraphics.getInstance();

		// Save parameters
		this.myAnchor = myAnchor;

		// Initialize stuff
		firstPlayerSeatNum = 0;
		numPlayers = model.getNumPlayers();

		// Get parameters related to the font
		GameProperties props = GameProperties.getInstance();
		int fontSize = props.getInt("scoreFontSize", DEFAULT_FONT_SIZE);
		String fontName = props.get("scoreFont", DEFAULT_FONT_NAME);
		gameFont = new Font (fontName, Font.BOLD, fontSize);
		gameFontMetrics = parentComponent.getFontMetrics(gameFont);
		int maxAscent = gameFontMetrics.getMaxAscent();
		int maxDescent = gameFontMetrics.getMaxDescent();
		scoreRectHeight = maxAscent + maxDescent + 2 * TEXT_HEIGHT_BORDER;

		// This is a quick'n dirty method to find the width of the score block.
		// I'm using a 7-digit "9999999" score on the assumption that real scores
		// will be at most 3-digits so that there will be 2-digits of space around
		// the real scores.  Also, I'm assuming that 9's are sufficiently wide
		// that 7-digits of 9's are wider than the widest 3-digit display of a
		// real score.
		scoreRectWidth = gameFontMetrics.stringWidth("9999999");

		// Set the locations of the various text strings
		for (int i=0; i<5; i++) {
			scoreTextAnchor[i] = new Point (myAnchor);
			scoreTextAnchor[i].translate(scoreRectWidth / 2,
			                             scoreRectHeight * i + TEXT_HEIGHT_BORDER + maxAscent);
		}
	}

	/**
	 * Set the seat number for who we draw on top of the score list
	 */
	public void setFirstSeatNumber (int newFirstSeatNum) {
		firstPlayerSeatNum  = newFirstSeatNum;
	}

	/**
	 * Draw the score
	 */
	public void paintScores (Graphics g) {
		// Set the font to our game font.
		g.setFont(gameFont);

		// Start at our anchor spot.
		int y = myAnchor.y;

		// Draw the player area rectangles
		for (int i=0; i<numPlayers; i++) {
			int thisPlayerNum = (i + firstPlayerSeatNum) % numPlayers;

			// Draw the background rect in the player's color
			g.setColor(spGraphics.getPlayerColor(thisPlayerNum));
			g.fillRect(myAnchor.x, y, scoreRectWidth, scoreRectHeight);

			// Outline the rect in black
			g.setColor(blackColor);
			g.drawRect(myAnchor.x, y, scoreRectWidth, scoreRectHeight);

			// Draw the player's score in black in the middle of the rectangle
			g.setColor(blackColor);
			drawCenterJustifiedText(g, "" + model.getScore(thisPlayerNum), scoreTextAnchor[i]);

			// Advance to the next rectangle down.
			y += scoreRectHeight;
		}

	}

	/*
	 * This method will draw text center justified on the given point.
	 *
	 * @param g             The graphics context to draw on.
	 * @param theText       The text to draw
	 * @param anchorPoint   The point to center the text about.
	 */
	public void drawCenterJustifiedText (Graphics g, String theText, Point anchorPoint) {
		int stringWidth = gameFontMetrics.stringWidth(theText);
		g.drawString(theText, anchorPoint.x - (stringWidth / 2), anchorPoint.y);
	}
}
