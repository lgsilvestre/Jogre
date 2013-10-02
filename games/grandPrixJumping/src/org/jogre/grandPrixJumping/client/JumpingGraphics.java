/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006  Richard Walter
 * http//jogre.sourceforge.org
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

import org.jogre.grandPrixJumping.common.JumpingCard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import java.util.ListIterator;
import java.util.Vector;

import org.jogre.client.awt.GameImages;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

/**
 * Support functions for drawing things.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingGraphics {

	// Static instance provided by the static factory method getInstance()
	private static JumpingGraphics myInstance = null;

	// The data for each of the images
	private ImageIcon [] imageIcons = new ImageIcon [JumpingImages.NUM_IMAGES];
	private Image [] images         = new Image     [JumpingImages.NUM_IMAGES];
	public  int [] imageWidths      = new int       [JumpingImages.NUM_IMAGES];
	public  int [] imageHeights     = new int       [JumpingImages.NUM_IMAGES];
	private int [] imagePin         = new int       [JumpingImages.NUM_IMAGES];

	// Types of pinning supported
	private static final int UL_CORNER = 0;
	private static final int CENTERED = 1;

	// Data about the images to be managed by the graphics helper
	private int [][] image_data = {
		{JumpingImages.TRACK, 1, 1, UL_CORNER},
		{JumpingImages.FENCES, 5, 1, CENTERED},
		{JumpingImages.CARD_BACK, 1, 1, UL_CORNER},
		{JumpingImages.CARD_OUTLINE, 2, 1, UL_CORNER},
		{JumpingImages.CARDS, 5, 3, UL_CORNER},
		{JumpingImages.CARDS_IMMEDIATE, 6, 1, UL_CORNER},
		{JumpingImages.CARDS_SPECIAL, 2, 1, UL_CORNER},
		{JumpingImages.HORSE0, 2, 2, CENTERED},
		{JumpingImages.HORSE1, 2, 2, CENTERED},
		{JumpingImages.TRACK_ICONS, 17, 1, CENTERED},
		{JumpingImages.HORSE_HIGHLIGHT, 2, 1, CENTERED},
		{JumpingImages.WATER_JUMP, 1, 2, CENTERED},
		{JumpingImages.CARDS_ACTIVATED_SPECIALS, 5, 1, UL_CORNER},
		{JumpingImages.CAUTION_SIGNS, 5, 1, CENTERED},
		{JumpingImages.TRACK_ICONS_HIGHLIGHT, 5, 1, CENTERED},
		{JumpingImages.PLUS, 2, 1, CENTERED},
		{JumpingImages.DARK_CORNERS, 2, 2, UL_CORNER},
		{JumpingImages.TURN_HIGHLIGHT, 4, 1, CENTERED},
		{JumpingImages.TRACK_HIGHLIGHT, 1, 1, CENTERED},
		{JumpingImages.PADLOCK, 1, 1, CENTERED},
		{JumpingImages.HAND_SIZE_CORNERS, 2, 2, UL_CORNER}
	};

	// The color used to darken played cards
	private Color darkenColor = null;

	// The colors of the horses
	public Color [] horseColors = new Color [2];

	// The color of the background & shadow version;
	public Color backgroundColor = null;
	public Color shadowColor = null;

	// The color to draw overflows of the fault indicator
	public Color faultOverflowColor = null;

	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 *
	 * Note: Cards are loaded here because there are multiple containers that
	 *		 draw cards and so this puts all of that in one place.
	 *		 The track, on the other hand, is only ever drawn by the track
	 *		 container, so that picture is loaded and drawn by the TrackComponent.
	 */
	private JumpingGraphics() {

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
		horseColors[0]     = JogreUtils.getColour (props.get("player.colour.0"));
		horseColors[1]     = JogreUtils.getColour (props.get("player.colour.1"));
		backgroundColor    = JogreUtils.getColour (props.get("background.colour"));
		faultOverflowColor = JogreUtils.getColour (props.get("faults.overflow.colour"));
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static JumpingGraphics getInstance() {
		if (myInstance == null) {
			myInstance = new JumpingGraphics();
		}

		return myInstance;
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

	/**
	 * Paint a card.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card
	 * @param	theCard			The card to draw
	 */
	public void paintCard(
		Graphics g,
		int x, int y,
		JumpingCard theCard)
	{
		// If clear, then nothing to draw
		if (!theCard.isVisible()) {
			return;
		}

		int type = theCard.cardType();
		int val = theCard.cardValue() - 1;

		if (type == JumpingCard.UNKNOWN) {
			// Draw a card back
			paintImage(g, x, y, JumpingImages.CARD_BACK, 0, 0);
		} else if (type == JumpingCard.SPECIAL) {
			paintImage(g, x, y, JumpingImages.CARDS_SPECIAL, val, 0);
		} else if (type == JumpingCard.IMMEDIATE) {
			paintImage(g, x, y, JumpingImages.CARDS_IMMEDIATE, val, 0);
		} else {
			paintImage(g, x, y, JumpingImages.CARDS, val, type);
		}
	}

	/**
	 * Paint a collection of cards
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the hand
	 * @param	cards			Vector of cards to draw
	 * @param	selectedIndex	Card index to draw as selected
	 * @param	verticalOffset	The offset to move the selected card up from the rest
	 * @param	cardSpacing		The horizontal amount to each card over
	 */
	public void paintHand(
		Graphics g,
		int x, int y,
		Vector cards,
		int selectedIndex,
		int verticalOffset,
		int cardSpacing)
	{
		if (cards == null) {
			return;
		}

		if (darkenColor == null) {
			darkenColor = new Color(0, 0, 0, 128);
		}

		// Draw all of the cards in the hand
		// (Note: I'm using a clone of the vector to avoid "Concurrent Modification
		//        Exceptions" that occur if we're painting a hand and a network
		//        message comes in that modifies the hand.)
		Vector cardClone = (Vector) cards.clone();
		ListIterator iter = cardClone.listIterator();

		int i = 0;
		while (iter.hasNext()) {
			JumpingCard theCard = (JumpingCard) iter.next();
			int real_y = ((i == selectedIndex) ? y : y + verticalOffset);
			paintCard(g, x, real_y, theCard);

		// This code would outline newly added cards.  However, after testing this,
		// I didn't think that it worked out well, so I'm removing it from the code.
		// However, I'm leaving it here on the off chance that someone may want to
		// add it in the future.  (Although the code to modify the isAdded() value
		// to cards may not be up-to-date, so there is other work to do to enable this
		// feature.)
		///		if (theCard.isAdded()) {
		///			paintImage(g, x, real_y, JumpingImages.CARD_OUTLINE, 1, 0);
		///		}

			if (theCard.isMarked()) {
				// Darken it by drawing a semi-transparent black over top.
				g.setColor(darkenColor);
				g.fillRect(x+5, real_y, imageWidths[JumpingImages.CARDS] - 10, imageHeights[JumpingImages.CARDS]);
				g.fillRect(x, real_y+5, 5, imageHeights[JumpingImages.CARDS] - 10);
				g.fillRect(x + imageWidths[JumpingImages.CARDS] - 5, real_y+5, 5, imageHeights[JumpingImages.CARDS] - 10);
				paintImage(g, x, real_y, JumpingImages.DARK_CORNERS, 0, 0);
				paintImage(g, x + imageWidths[JumpingImages.CARDS] - 5, real_y, JumpingImages.DARK_CORNERS, 1, 0);
				paintImage(g, x, real_y + imageHeights[JumpingImages.CARDS] - 5, JumpingImages.DARK_CORNERS, 0, 1);
				paintImage(g, x + imageWidths[JumpingImages.CARDS] - 5, real_y + imageHeights[JumpingImages.CARDS] - 5,
							JumpingImages.DARK_CORNERS, 1, 1);
			}

			i = i + 1;
			x = x + cardSpacing;
		}
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

	/**
	 * Shadow a rectangle by painting it with a 50% clear copy of the background color
	 */
	public void shadowRect(Graphics g, Rectangle r) {
		if (shadowColor == null) {
			shadowColor = new Color(backgroundColor.getRed(),
									backgroundColor.getGreen(),
									backgroundColor.getBlue(),
									128);
		}
		g.setColor(shadowColor);
		g.fillRect(r.x, r.y, r.width, r.height);
	}

	/**
	 * Paint an activated card and highlight the appropriate half.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card
	 * @param	imageIndex		Card index into the activated specials to draw.
	 * @param	activatedHalf	0 = Draw upper left outline.
	 *							1 = Draw lower right outline.
	 *							other = draw no outline.
	 */
	 public void paintActivatedCard(Graphics g,
								   int x, int y,
								   int imageIndex,
								   int activatedHalf) {

		// Draw the activated card's active image
		paintImage(g, x, y, JumpingImages.CARDS_ACTIVATED_SPECIALS, imageIndex, 0);

		// If one half is selected, then draw the outline of that half
		if (activatedHalf == 0) {
			paintImage(g, x, y, JumpingImages.CARDS_ACTIVATED_SPECIALS,
						JumpingImages.ACTIVATED_CARDS_HIGHLIGHT_UPPER_LEFT_INDEX, 0);
		} else if (activatedHalf == 1) {
			paintImage(g, x, y, JumpingImages.CARDS_ACTIVATED_SPECIALS,
						JumpingImages.ACTIVATED_CARDS_HIGHLIGHT_LOWER_RIGHT_INDEX, 0);
		}
	}

}
