/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
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
package org.jogre.warwick.client;

import java.awt.AlphaComposite;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import java.util.ListIterator;
import java.util.Vector;

import org.jogre.client.awt.GameImages;

import org.jogre.common.util.GameProperties;

/**
 * Support functions for drawing things for the game of Warwick.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickGraphics {

	// Static instance provided by the static factory method getInstance()
	private static WarwickGraphics myInstance = null;

	// Image Id's
	public static final int BOARD = 1;
	public static final int TOKENS = 2;
	public static final int SCORE_MARKERS = 3;
	public static final int ROSES = 4;
	public static final int ROSE_CARDS = 5;
	public static final int TOKEN_HIGHLIGHT = 6;
	public static final int CHECK = 7;
	public static final int QUESTION = 8;
	public static final int CORNER10 = 9;

	public static final int SHADOW_TOKENS = 10;

	public static final int NUM_IMAGES = 11;

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
	private static final int [][] image_data = {
		{BOARD, 1, 1, UL_CORNER},
		{TOKENS, 5, 1, CENTERED},
		{SCORE_MARKERS, 5, 1, CENTERED},
		{ROSES, 6, 1, CENTERED},
		{ROSE_CARDS, 5, 2, UL_CORNER},
		{TOKEN_HIGHLIGHT, 2, 1, CENTERED},
		{CHECK, 1, 1, CENTERED},
		{QUESTION, 1, 1, CENTERED},
		{CORNER10, 2, 2, UL_CORNER}
	};

	// Data about the shadow images to be created from normal images
	private static final int [][] shadowImageCreationData = {
		{TOKENS, SHADOW_TOKENS, 50}
	};

	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 *
	 */
	private WarwickGraphics () {

		// Load all of the images
		for (int i=0; i<image_data.length; i++) {
			int index = image_data[i][0];
			imageIcons[index]   = GameImages.getImageIcon(index);
			images[index]       = imageIcons[index].getImage();
			imageWidths[index]  = imageIcons[index].getIconWidth() / image_data[i][1];
			imageHeights[index] = imageIcons[index].getIconHeight() / image_data[i][2];
			imagePin[index]     = image_data[i][3];
		}

		// Create shadowed versions some images by creating new buffered images
		for (int i=0; i<shadowImageCreationData.length; i++) {
			// Get the shadow data from the table.
			int fromIndex = shadowImageCreationData[i][0];
			int toIndex = shadowImageCreationData[i][1];
			float alphaRatio = ((float) shadowImageCreationData[i][2] / 100.0f);

			// Create & render the new image with a given alpha value
			BufferedImage newImage = new BufferedImage (imageIcons[fromIndex].getIconWidth(),
			                                            imageIcons[fromIndex].getIconHeight(),
			                                            BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = newImage.createGraphics();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alphaRatio));
			((Graphics) g2d).drawImage(images[fromIndex], 0, 0, null);

			// Save the new image data in the image structures
			images[toIndex]       = newImage;
			imageWidths[toIndex]  = imageWidths[fromIndex];
			imageHeights[toIndex] = imageHeights[fromIndex];
			imagePin[toIndex]     = imagePin[fromIndex];
		}
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static WarwickGraphics getInstance () {
		if (myInstance == null) {
			myInstance = new WarwickGraphics();
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
	public void paintImage (
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
	public void paintImage (
		Graphics g,
		Point screenPoint,
		int imageId,
		int tx, int ty) {
		paintImage(g, screenPoint.x, screenPoint.y, imageId, tx, ty);
	}

	/**
	 * Outline the rectangle provided.
	 *
	 * @param   g               The graphics area to draw on
	 * @param   r               The rectangle to outline
	 * @param   lineThickness   The thickness of the side lines
	 * @param   cornerImageId   The image to use for the corners
	 */
	public void outlineRect (Graphics g, Rectangle r, 
	                         int lineThickness, int cornerImageId) {
		int cornerWidth = imageWidths[cornerImageId];
		int cornerHeight = imageHeights[cornerImageId];

		// Draw the 4 sides
		g.fillRect(r.x + cornerWidth, r.y,
		           r.width - 2*cornerWidth, lineThickness);
		g.fillRect(r.x + cornerWidth, r.y + r.height - lineThickness,
		           r.width - 2*cornerWidth, lineThickness);
		g.fillRect(r.x, r.y + cornerHeight,
		           lineThickness, r.height - 2*cornerHeight);
		g.fillRect(r.x + r.width - lineThickness, r.y + cornerHeight,
		           lineThickness, r.height - 2*cornerHeight);

		// Draw the corners
		paintImage(g, r.x, r.y, cornerImageId, 0, 0);
		paintImage(g, r.x + r.width - cornerWidth, r.y, cornerImageId, 1, 0);
		paintImage(g, r.x, r.y + r.height - cornerHeight, cornerImageId, 0, 1);
		paintImage(g, r.x + r.width - cornerWidth,r.y + r.height - cornerHeight, cornerImageId, 1, 1);
	}

	/**
	 * Return the location of the control point to use for making a quad entry
	 * in a GeneralPath that connects the two end points given.
	 *
	 * @param p1x, p1y     One end point of the line.
	 * @param p2x, p2y     Other end point of the line.
	 * @param curveDist    The distance from the line to the control point (in pixels)
	 *                     Higher values make more curvy lines.
	 * @return the control point to use.
	 */
	static Point2D.Float getControlPoint (float p1x, float p1y,
	                                      float p2x, float p2y,
	                                      float curveDist) {
		// Compute the center point.
		float ctx = (p1x + p2x) / 2.0f;
		float cty = (p1y + p2y) / 2.0f;

		// Compute the distance from one end to the center for scaling.
		float dx = (p1x - ctx);
		float dy = (p1y - cty);
		float d = (float) Math.sqrt((dx * dx) + (dy * dy));

		// Move the center point perpendicular to the line angle to place it
		// at the control point for the quad segment.
		float scaleFactor = (curveDist / d);
		if (dx > 0) {
			ctx += dy * scaleFactor;
			cty -= dx * scaleFactor;
		} else {
			ctx -= dy * scaleFactor;
			cty += dx * scaleFactor;
		}

		return new Point2D.Float (ctx, cty);
	}

	/**
	 * Return the location of the control point to use for making a quad entry
	 * in a GeneralPath that connects the two end points given.
	 *
	 * @param p1           One end point of the line.
	 * @param p2           Other end point of the line.
	 * @param curveDist    The distance from the line to the control point (in pixels)
	 *                     Higher values make more curvy lines.
	 * @return the control point to use.
	 */
	static Point2D.Float getControlPoint (Point p1, Point p2, float curveDist) {
		return getControlPoint( (float) p1.x, (float) p1.y,
		                        (float) p2.x, (float) p2.y, 
		                        curveDist);
	}
}
