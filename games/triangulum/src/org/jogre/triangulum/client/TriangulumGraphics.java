/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
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
package org.jogre.triangulum.client;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.jogre.client.awt.GameImages;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import org.jogre.triangulum.common.TriangulumPiece;

/**
 * Support functions for drawing things.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class TriangulumGraphics {

	// Static instance provided by the static factory method getInstance()
	private static TriangulumGraphics myInstance = null;

	// The data for each of the images
	private ImageIcon [] imageIcons = new ImageIcon [TriangulumImages.NUM_IMAGES];
	private Image [] images         = new Image     [TriangulumImages.NUM_IMAGES];
	public  int [] imageWidths      = new int       [TriangulumImages.NUM_IMAGES];
	public  int [] imageHeights     = new int       [TriangulumImages.NUM_IMAGES];
	private int [] imagePin         = new int       [TriangulumImages.NUM_IMAGES];

	// Types of pinning supported
	private static final int UL_CORNER = 0;
	private static final int CENTERED = 1;

	// Data about the images to be managed by the graphics helper
	private int [][] image_data = {
		{TriangulumImages.BOARD_TILE, 1, 1, CENTERED},
		{TriangulumImages.DIGITS, 7, 1, CENTERED},
		{TriangulumImages.MULTIPLIERS, 3, 1, CENTERED},
		{TriangulumImages.BOTTOM_PIECES, 2, 7, UL_CORNER},
		{TriangulumImages.LEFT_PIECES, 7, 2, UL_CORNER},
		{TriangulumImages.RIGHT_PIECES, 7, 2, UL_CORNER},
		{TriangulumImages.PIECE_OUTLINE, 2, 1, CENTERED},
		{TriangulumImages.SMALL_BOTTOM_PIECES, 2, 7, UL_CORNER},
		{TriangulumImages.SMALL_LEFT_PIECES, 7, 2, UL_CORNER},
		{TriangulumImages.SMALL_RIGHT_PIECES, 7, 2, UL_CORNER},
		{TriangulumImages.SMALL_PIECE_OUTLINE, 2, 1, CENTERED},
		{TriangulumImages.SELECTION_HIGHLIGHTS, 2, 2, CENTERED},
		{TriangulumImages.ACTIVE_HIGHLIGHT, 2, 2, CENTERED},
		{TriangulumImages.TRASH, 1, 1, CENTERED},
		{TriangulumImages.SMALL_HIGHLIGHT, 1, 1, CENTERED},
		{TriangulumImages.FOUR_PIECE_OUTLINE, 1, 1, UL_CORNER},
		{TriangulumImages.FIVE_PIECE_OUTLINE, 1, 1, UL_CORNER},
		{TriangulumImages.EMBLEM, 1, 1, CENTERED},
	};

	// Colors for each player
	private Color [] playerColors = new Color[4];

	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 *
	 */
	private TriangulumGraphics () {

		// Load all of the images
		for (int i=0; i<image_data.length; i++) {
			int index = image_data[i][0];
			imageIcons[index]   = GameImages.getImageIcon(index);
			images[index]       = imageIcons[index].getImage();
			imageWidths[index]  = imageIcons[index].getIconWidth() / image_data[i][1];
			imageHeights[index] = imageIcons[index].getIconHeight() / image_data[i][2];
			imagePin[index]     = image_data[i][3];
		}

		// Load the player colors
		GameProperties props = GameProperties.getInstance();
		playerColors[0] = JogreUtils.getColour (props.get("player.colour.0"));
		playerColors[1] = JogreUtils.getColour (props.get("player.colour.1"));
		playerColors[2] = JogreUtils.getColour (props.get("player.colour.2"));
		playerColors[3] = JogreUtils.getColour (props.get("player.colour.3"));
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static TriangulumGraphics getInstance () {
		if (myInstance == null) {
			myInstance = new TriangulumGraphics();
		}

		return myInstance;
	}

	/**
	 * Paint an image
	 *
	 * @param   g               The graphics area to draw on
	 * @param   (x, y)          Position in g to paint the image
	 * @param   imageId         The id of the source image
	 * @param   tx, ty          source tile within the source image
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
	 * @param   tx, ty          source tile within the source image
	 */
	public void paintImage (
		Graphics g,
		Point screenPoint,
		int imageId,
		int tx, int ty) {
		paintImage(g, screenPoint.x, screenPoint.y, imageId, tx, ty);
	}

	/**
	 * Return the color to use for the given player
	 */
	public Color getPlayerColor (int seatNum) {
		return playerColors[seatNum];
	}

	/* These tables are offsets from the anchor point used to draw the pieces correctly.
	   They are intimately tied with the size of the graphics images.
	   Offsets are for bottom, left, right, outline & value respectively for
	   upward and downward triangles.
	*/
	private static final int [][] pieceOffsetsX = {
		{0, 4, 40, 38, 39},
		{0, 4, 40, 38, 39}
	};
	private static final int [][] pieceOffsetsY = {
		{44, 4, 4, 34, 44},
		{ 2, 5, 4, 34, 24}
	};

	/**
	 * Draw a single piece on the screen.
	 *
	 * @param g              The graphics context to draw on.
	 * @param piece          The piece to draw.
	 * @param anchor         The anchor point of where to draw the piece.
	 * @param upwardFacing   If this piece is to be drawn up or down.
	 */
	public void drawAPiece (Graphics g, TriangulumPiece piece, Point anchor, boolean upwardFacing) {
		if (piece == null) {
			// Nothing to draw, so return
			return;
		}

		int facingIndex = (upwardFacing ? 0 : 1);

		paintImage(g,
		    anchor.x + pieceOffsetsX[facingIndex][0], anchor.y + pieceOffsetsY[facingIndex][0],
		    TriangulumImages.BOTTOM_PIECES,
		    facingIndex, piece.getColor(TriangulumPiece.BOTTOM_SECTION));
		paintImage(g,
		    anchor.x + pieceOffsetsX[facingIndex][1], anchor.y + pieceOffsetsY[facingIndex][1],
		    TriangulumImages.LEFT_PIECES,
		    piece.getColor(TriangulumPiece.LEFT_SECTION), facingIndex);
		paintImage(g,
		    anchor.x + pieceOffsetsX[facingIndex][2], anchor.y + pieceOffsetsY[facingIndex][2],
		    TriangulumImages.RIGHT_PIECES,
		    piece.getColor(TriangulumPiece.RIGHT_SECTION), facingIndex);
		paintImage(g,
		    anchor.x + pieceOffsetsX[facingIndex][3], anchor.y + pieceOffsetsY[facingIndex][3],
		    TriangulumImages.PIECE_OUTLINE,
		    facingIndex, 0);
		paintImage(g,
		    anchor.x + pieceOffsetsX[facingIndex][4], anchor.y + pieceOffsetsY[facingIndex][4],
		    TriangulumImages.DIGITS,
		    piece.getValue()-1, 0);
	}

	/* These tables are offsets from the center point used to draw the small pieces correctly.
	   They are intimately tied with the size of the graphics images.
	   Offsets are for bottom, left, right & outline respectively for
	   upward and downward triangles.  (Small pieces don't have values displayed.)
	*/
	private static final int [][] smallPieceOffsetsX = {
		{-11, -10, 1, 0},
		{-11, -10, 1, 0}
	};
	private static final int [][] smallPieceOffsetsY = {
		{1, -12, -11, -3},
		{-7, -5, -5, 3}
	};

	/**
	 * Draw a small piece on the screen.
	 *
	 * @param g              The graphics context to draw on.
	 * @param piece          The piece to draw.
	 * @param (x,y)          The center point of where to draw the piece.
	 * @param upwardFacing   If this piece is to be drawn up or down.
	 */
	public void drawSmallPiece (Graphics g, TriangulumPiece piece, int x, int y, boolean upwardFacing) {
		if (piece == null) {
			// Nothing to draw, so return
			return;
		}

		int facingIndex = (upwardFacing ? 0 : 1);

		paintImage(g,
		    x + smallPieceOffsetsX[facingIndex][0],
		    y + smallPieceOffsetsY[facingIndex][0],
		    TriangulumImages.SMALL_BOTTOM_PIECES,
		    facingIndex, piece.getColor(TriangulumPiece.BOTTOM_SECTION));
		paintImage(g,
		    x + smallPieceOffsetsX[facingIndex][1],
		    y + smallPieceOffsetsY[facingIndex][1],
		    TriangulumImages.SMALL_LEFT_PIECES,
		    piece.getColor(TriangulumPiece.LEFT_SECTION), facingIndex);
		paintImage(g,
		    x + smallPieceOffsetsX[facingIndex][2],
		    y + smallPieceOffsetsY[facingIndex][2],
		    TriangulumImages.SMALL_RIGHT_PIECES,
		    piece.getColor(TriangulumPiece.RIGHT_SECTION), facingIndex);
		paintImage(g,
		    x + smallPieceOffsetsX[facingIndex][3],
		    y + smallPieceOffsetsY[facingIndex][3],
		    TriangulumImages.SMALL_PIECE_OUTLINE,
		    facingIndex, 0);
	}

	/**
	 * Draw a small piece on the screen.
	 *
	 * @param g              The graphics context to draw on.
	 * @param piece          The piece to draw.
	 * @param location       The center point of where to draw the piece.
	 * @param upwardFacing   If this piece is to be drawn up or down.
	 */
	public void drawSmallPiece (Graphics g, TriangulumPiece piece, Point location, boolean upwardFacing) {
		drawSmallPiece(g, piece, location.x, location.y, upwardFacing);
	}

}
