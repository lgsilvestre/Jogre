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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import java.text.NumberFormat;

import javax.swing.ImageIcon;

import org.jogre.client.awt.GameImages;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import org.jogre.texasHoldEm.common.Card;

/**
 * Support functions for drawing things.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class TexasHoldEmGraphics {

	// Static instance provided by the static factory method getInstance()
	private static TexasHoldEmGraphics myInstance = null;

	// The data for each of the images
	private ImageIcon [] imageIcons = new ImageIcon [TexasHoldEmImages.NUM_IMAGES];
	private Image [] images         = new Image     [TexasHoldEmImages.NUM_IMAGES];
	public  int [] imageWidths      = new int       [TexasHoldEmImages.NUM_IMAGES];
	public  int [] imageHeights     = new int       [TexasHoldEmImages.NUM_IMAGES];
	private int [] imagePin         = new int       [TexasHoldEmImages.NUM_IMAGES];

	// The colors for each player.
	private Color [] playerColors = new Color [8];
	private Color tableColor;
	public Color blackColor = new Color(0,0,0);


	// Types of pinning supported
	private static final int UL_CORNER = 0;
	private static final int CENTERED = 1;

	// Data about the images to be managed by the graphics helper
	private int [][] image_data = {
		{TexasHoldEmImages.CARDS, 13, 4, UL_CORNER},
		{TexasHoldEmImages.TABLE_CORNERS, 2, 2, UL_CORNER},
		{TexasHoldEmImages.PLAYER_AREA_CORNERS, 2, 2, UL_CORNER},
		{TexasHoldEmImages.BID_SLIDER, 1, 1, CENTERED},
		{TexasHoldEmImages.JOGRE_TEXT, 5, 1, UL_CORNER},
		{TexasHoldEmImages.CARD_BACK, 1, 1, UL_CORNER},
		{TexasHoldEmImages.DEALER_BUTTON, 1, 1, CENTERED},
		{TexasHoldEmImages.TINY_CARDS, 13, 4, UL_CORNER},
		{TexasHoldEmImages.TINY_CARD_BACK, 1, 1, UL_CORNER},
		{TexasHoldEmImages.WINNER_HIGHLIGHT, 4, 1, CENTERED}
	};

	// Constants for justification of text
	public static final int LEFT_JUSTIFY = 0;
	public static final int CENTER_JUSTIFY = 1;
	public static final int RIGHT_JUSTIFY = 2;

	// The formatter that will render currency in the local Locale
	public NumberFormat currencyFormatter;

	// The font to use for the text display
	public Font gameFont;

	// Size of the box required to display all possible currency strings
	// given the font & localizations.
	private Dimension largestCurrencyDimension = null;

	// Default values for things.
	private static final String DEFAULT_TABLE_COLOR = "0,128,0";
	private static final int DEFAULT_FONT_SIZE = 12;

	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 *
	 */
	private TexasHoldEmGraphics() {

		// Load all of the images
		for (int i=0; i<image_data.length; i++) {
			int index = image_data[i][0];
			imageIcons[index]   = GameImages.getImageIcon(index);
			images[index]       = imageIcons[index].getImage();
			imageWidths[index]  = imageIcons[index].getIconWidth() / image_data[i][1];
			imageHeights[index] = imageIcons[index].getIconHeight() / image_data[i][2];
			imagePin[index]     = image_data[i][3];
		}

		// Set colors
		GameProperties props = GameProperties.getInstance();
		playerColors[0] = JogreUtils.getColour (props.get("player.colour.0"));
		playerColors[1] = JogreUtils.getColour (props.get("player.colour.1"));
		playerColors[2] = JogreUtils.getColour (props.get("player.colour.2"));
		playerColors[3] = JogreUtils.getColour (props.get("player.colour.3"));
		playerColors[4] = JogreUtils.getColour (props.get("player.colour.4"));
		playerColors[5] = JogreUtils.getColour (props.get("player.colour.5"));
		playerColors[6] = JogreUtils.getColour (props.get("player.colour.6"));
		playerColors[7] = JogreUtils.getColour (props.get("player.colour.7"));
		tableColor = JogreUtils.getColour (props.get("tableColor", DEFAULT_TABLE_COLOR));

		// Set the font to the requested size
		int fontSize = props.getInt("fontSize", DEFAULT_FONT_SIZE);
		gameFont = new Font ("Dialog", Font.BOLD, fontSize);

		// Get information to allow us to draw the text in localized fashion.
		currencyFormatter = NumberFormat.getCurrencyInstance();
		currencyFormatter.setMaximumFractionDigits(0);	// Turn off cents
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static TexasHoldEmGraphics getInstance() {
		if (myInstance == null) {
			myInstance = new TexasHoldEmGraphics();
		}

		return myInstance;
	}

	/**
	 * Set the font of the given graphics context to the game font.
	 */
	public void setGameFont(Graphics g) {
		g.setFont(gameFont);
	}

	/**
	 * Set the largest currency dimension.
	 *
	 * @param Dimension		The dimension to set as the largest currency dimension.
	 */
	public void setLargestCurrencyDimension(Dimension dim) {
		largestCurrencyDimension = dim;
	}

	/**
	 * Return the largest currency dimension.
	 *
	 * @param Dimension		The dimension to set as the largest currency dimension.
	 */
	public Dimension getLargestCurrencyDimension() {
		return largestCurrencyDimension;
	}

	/**
	 * Return the color of the given player.
	 *
	 * @param seatNum		The seat number of the player whose color is desired.
	 * @return that player's color.
	 */
	public Color getPlayerColor(int seatNum) {
		return playerColors[seatNum];
	}

	/**
	 * Return the color of the table top.
	 *
	 * @return the table colo.
	 */
	public Color getTableColor() {
		return tableColor;
	}

	/**
	 * Paint an outline of two rectangles.
	 */
	public void paintRects(Graphics g, Rectangle redRect, Dimension greenRect) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1));

		g.setColor(new Color(255, 0, 0));
		g.drawRect(0, 0, redRect.width-1, redRect.height-1);

		g.setColor(new Color(0, 255, 0));
		g.drawRect(1, 1, greenRect.width-3, greenRect.height-3);
	}

	/**
	 * Paint an image
	 *
 	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Position in g to paint the image
	 * @param	imageId			The id of the source image
	 * @param	tx, ty			source tile within the source image
	 */
	public void paintImage(
		Graphics g,
		int x, int y,
		int imageId,
		int tx, int ty) {

		// Get width & height
		int w = imageWidths[imageId];
		int h = imageHeights[imageId];

		// Adjust the destination points, if the image is to be centered
		if (imagePin[imageId] == CENTERED) {
			x -= (w / 2);
			y -= (h / 2);
		}

		// Calculate source rectangle
		int sx = tx * w;
		int sy = ty * h;

		g.drawImage(images[imageId],		// Src image
			x, y, x+w, y+h,					// Dest rect
			sx, sy, sx+w, sy+h,				// Src rect
			null
		);
	}

	/**
	 * Paint part of an image
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Position in g to paint the image
	 * @param	imageId			The id of the source image
	 * @param	tx, ty			source tile within the source image
	 * @param	xsize, ysize	The size of the image to draw
	 */
	public void paintPartialImage(
		Graphics g,
		int x, int y,
		int imageId,
		int tx, int ty,
		int xsize, int ysize) {

		// Get width & height
		int w = imageWidths[imageId];
		int h = imageHeights[imageId];

		// Adjust the destination points, if the image is to be centered
		if (imagePin[imageId] == CENTERED) {
			x -= (w / 2);
			y -= (h / 2);
		}

		// Calculate source rectangle
		int sx = tx * w;
		int sy = ty * h;

		g.drawImage(images[imageId],		// Src image
			x, y, x+xsize, y+ysize,			// Dest rect
			sx, sy, sx+xsize, sy+ysize,		// Src rect
			null
		);
	}

	/*
	 * Paint a card.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card
	 * @param	theCard			The card to draw
	 */
	private void paintCardCore(
		Graphics g,
		int x, int y,
		Card theCard,
		int backImageId,
		int frontImageId)
	{
		// If clear, then nothing to draw
		if (!theCard.isVisible()) {
			return;
		}

		int suit = theCard.cardSuit();
		int val = theCard.cardValue() - 2;

		if (suit == Card.UNKNOWN) {
			// Draw a card back
			paintImage(g, x, y, backImageId, 0, 0);
		} else {
			paintImage(g, x, y, frontImageId, val, suit);
		}
	}

	/**
	 * Paint a regular sized card.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card
	 * @param	theCard			The card to draw
	 */
	public void paintCard(Graphics g, int x, int y, Card theCard) {
		paintCardCore(g, x, y, theCard, TexasHoldEmImages.CARD_BACK, TexasHoldEmImages.CARDS);
	}

	public void paintCard(Graphics g, Point location, Card theCard) {
		paintCardCore(g, location.x, location.y, theCard, TexasHoldEmImages.CARD_BACK, TexasHoldEmImages.CARDS);
	}

	/**
	 * Paint a tiny card.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card
	 * @param	theCard			The card to draw
	 */
	public void paintTinyCard(Graphics g, int x, int y, Card theCard) {
		paintCardCore(g, x, y, theCard, TexasHoldEmImages.TINY_CARD_BACK, TexasHoldEmImages.TINY_CARDS);
	}

	public void paintTinyCard(Graphics g, Point location, Card theCard) {
		paintCardCore(g, location.x, location.y, theCard, TexasHoldEmImages.TINY_CARD_BACK, TexasHoldEmImages.TINY_CARDS);
	}

	/**
	 * Draw a round rectangle with nice corners
	 */
	public void drawNiceRoundRectangle (
		Graphics g,
		Rectangle theRect,
		int cornerImageId,
		int borderThickness,
		Color fillColor
	) {
		Graphics2D g2d = (Graphics2D) g;
		int cornerSize = imageWidths[cornerImageId];
		int x = theRect.x;
		int y = theRect.y;
		int w = theRect.width;
		int h = theRect.height;
		int halfBorderThickness = borderThickness / 2;

		// Fill the interior
		g2d.setColor(fillColor);
		g2d.fillRoundRect(x + halfBorderThickness, y + halfBorderThickness,
		                  w - borderThickness, h - borderThickness,
		                  cornerSize * 2, cornerSize * 2);

		// Paint the corners
		g2d.setColor(blackColor);
		paintImage(g, x, y, cornerImageId, 0, 0);
		paintImage(g, x + w - cornerSize, y, cornerImageId, 1, 0);
		paintImage(g, x, y + h - cornerSize, cornerImageId, 0, 1);
		paintImage(g, x + w - cornerSize, y + h - cornerSize, cornerImageId, 1, 1);

		// Draw the sides
		g2d.fillRect(x + cornerSize, y, w - (cornerSize * 2), borderThickness);
		g2d.fillRect(x + cornerSize, y + h - borderThickness, w - (cornerSize * 2), borderThickness);
		g2d.fillRect(x, y + cornerSize, borderThickness, h - (cornerSize * 2));
		g2d.fillRect(x + w - borderThickness, y + cornerSize, borderThickness, h - (cornerSize * 2));
	}

	/**
	 * Determine if the given point is within the rectangle covered by an image located
	 * at a given place.
	 *
	 * @param	px, py		The point to test
	 * @param	ix, iy		The point that the image is located
	 * @param	imageId		The Id of the image to test against
	 * @return true => point (px, py) is within the image
	 *         false => point (px, py) is not within the image
	 */
	public boolean pointWithinImage(int px, int py, int ix, int iy, int imageId) {

		// Get width & height
		int w = imageWidths[imageId];
		int h = imageHeights[imageId];

		// Adjust the destination points, if the image is to be centered
		if (imagePin[imageId] == CENTERED) {
			ix -= (w / 2);
			iy -= (h / 2);
		}

		return ((px >= ix) && (px <= (ix + w)) && (py >= iy) && (py <= (iy + h)));
	}

	/*
	 * This method will draw text center justified on the given point.
	 *
	 * @param g					The graphics context to draw on.
	 * @param theText			The text to draw
	 * @param (x,y)				The point to center the text about.
	 */
	public void drawCenterJustifiedText (Graphics g, String theText, int x, int y) {
		int stringWidth = g.getFontMetrics().stringWidth(theText);
		g.drawString(theText, x - (stringWidth / 2), y);
	}

	/*
	 * This method will draw text center justified on the given point.
	 *
	 * @param g					The graphics context to draw on.
	 * @param theText			The text to draw
	 * @param anchorPoint		The point to center the text about.
	 */
	public void drawCenterJustifiedText (Graphics g, String theText, Point anchorPoint) {
		int stringWidth = g.getFontMetrics().stringWidth(theText);
		g.drawString(theText, anchorPoint.x - (stringWidth / 2), anchorPoint.y);
	}

	/*
	 * This method will draw text left justified on the given point.
	 *
	 * @param g					The graphics context to draw on.
	 * @param theText			The text to draw
	 * @param anchorPoint		The point to center the text about.
	 */
	public void drawLeftJustifiedText (Graphics g, String theText, Point anchorPoint) {
		g.drawString(theText, anchorPoint.x, anchorPoint.y);
	}

	/*
	 * This method will draw text right justified on the given point.
	 *
	 * @param g					The graphics context to draw on.
	 * @param theText			The text to draw
	 * @param anchorPoint		The point to center the text about.
	 */
	public void drawRightJustifiedText (Graphics g, String theText, Point anchorPoint) {
		int stringWidth = g.getFontMetrics().stringWidth(theText);
		g.drawString(theText, anchorPoint.x - stringWidth, anchorPoint.y);
	}

	/*
	 * This method will draw text justified on the given point.
	 *
	 * @param g					The graphics context to draw on.
	 * @param justification		Indicates left, right or center justification
	 * @param theText			The text to draw
	 * @param anchorPoint		The point to center the text about.
	 */
	public void drawJustifiedText(Graphics g, int justification, String theText, Point anchorPoint) {
		if (justification == LEFT_JUSTIFY) {
			drawLeftJustifiedText(g, theText, anchorPoint);
		} else if (justification == CENTER_JUSTIFY) {
			drawCenterJustifiedText(g, theText, anchorPoint);
		} else {
			drawRightJustifiedText(g, theText, anchorPoint);
		}
	}
}
