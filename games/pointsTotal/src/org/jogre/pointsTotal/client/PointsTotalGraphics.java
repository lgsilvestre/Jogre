/*
 * JOGRE (Java Online Gaming Real-time Engine) - Points Total
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.pointsTotal.client;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.jogre.client.awt.GameImages;

/**
 * Support functions for drawing things.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalGraphics {

	// Static instance provided by the static factory method getInstance()
	private static PointsTotalGraphics myInstance = null;

	// The images defined in the game.properties file
	public static final int GAME_ICON = 0;
	public static final int EMPTY_TILE = 1;
	public static final int ARROW_BASE = 2;
	public static final int ARROW_MASK = 3;
	public static final int NUMBERS = 4;
	public static final int EMPTY_SMALL_TILE = 5;
	public static final int SMALL_ARROWS = 6;
	public static final int BIG_HIGHLIGHT = 7;
	public static final int SMALL_HIGHLIGHT = 8;
	public static final int FIRST_PLAYER_ICON = 9;
	public static final int SCORE_DIGITS = 10;
	public static final int SCORE_BOX = 11;

	public static final int NUM_IMAGES = 12;

	// The data for each of the images
	private ImageIcon [] imageIcons = new ImageIcon [NUM_IMAGES];
	private Image [] images         = new Image     [NUM_IMAGES];
	public  int [] imageWidths      = new int       [NUM_IMAGES];
	public  int [] imageHeights     = new int       [NUM_IMAGES];
	private int [] imagePin         = new int       [NUM_IMAGES];

	// Types of pinning supported
	private static final int UL_CORNER = 0;
	private static final int CENTERED = 1;

	// Data about the images to be managed by the graphics helper
	private int [][] image_data = {
		{EMPTY_TILE, 1, 1, UL_CORNER},
		{ARROW_BASE, 4, 1, UL_CORNER},
		{ARROW_MASK, 4, 1, UL_CORNER},
		{NUMBERS, 4, 1, UL_CORNER},
		{EMPTY_SMALL_TILE, 1, 1, UL_CORNER},
		{SMALL_ARROWS, 4, 4, UL_CORNER},
		{BIG_HIGHLIGHT, 2, 1, UL_CORNER},
		{SMALL_HIGHLIGHT, 2, 1, UL_CORNER},
		{FIRST_PLAYER_ICON, 2, 1, UL_CORNER},
		{SCORE_DIGITS, 10, 1, UL_CORNER},
		{SCORE_BOX, 1, 4, UL_CORNER}
	};

	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 *
	 */
	private PointsTotalGraphics() {

		// Load all of the images
		for (int i=0; i<image_data.length; i++) {
			int index = image_data[i][0];
			imageIcons[index]   = GameImages.getImageIcon(index);
			images[index]       = imageIcons[index].getImage();
			imageWidths[index]  = imageIcons[index].getIconWidth() / image_data[i][1];
			imageHeights[index] = imageIcons[index].getIconHeight() / image_data[i][2];
			imagePin[index]     = image_data[i][3];
		}
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static PointsTotalGraphics getInstance() {
		if (myInstance == null) {
			myInstance = new PointsTotalGraphics();
		}

		return myInstance;
	}

	/**
	 * Paint an image
	 *
	 * @param   g               The graphics area to draw on
	 * @param   (x, y)          Position in g to paint the image
	 * @param   imageId         The id of the source image
	 * @param   tx, ty          Source tile within the source image
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

		g.drawImage(images[imageId],     // Src image
			x, y, x+w, y+h,              // Dest rect
			sx, sy, sx+w, sy+h,          // Src rect
			null
		);
	}

	/**
	 * Paint an image
	 *
	 * @param   g               The graphics area to draw on
	 * @param   screenPoint     Position in g to paint the image
	 * @param   imageId         The id of the source image
	 * @param   tx, ty          Source tile within the source image
	 */
	public void paintImage(
		Graphics g,
		Point screenPoint,
		int imageId,
		int tx, int ty) {
		paintImage(g, screenPoint.x, screenPoint.y, imageId, tx, ty);
	}

}
