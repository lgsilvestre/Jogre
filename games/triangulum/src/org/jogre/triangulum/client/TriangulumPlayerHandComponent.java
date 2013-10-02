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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;

import java.util.Vector;
import java.util.ListIterator;

import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;

/**
 * A helper class that displays a single player's tiles for a game of Triangulum.
 *
 * Note: This is called a "component", but it isn't actually a real component
 *       by itself.  It sits within the main Triangulum Component.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumPlayerHandComponent {

	// Link to the model
	protected TriangulumModel model;

	// Link to the graphics helper routines
	private TriangulumGraphics spGraphics;

	// The player number that I am showing.
	private int myPlayerSeatNum;

	// My anchor location within the larger component.
	private Point myAnchor;

	// The Polygon for the background area of the hand component
	private Polygon backgroundPoly;

	// Which of the two area outline images to draw.
	private int outlineAreaIndex;
	
	// Locations of each player piece
	private Point [] pieceAnchor = new Point [5];
	private Point [] pieceCenter = new Point [5];

	// The index & state of the hand tile that is currently "selected"
	// (Selected means mouse moved over.)
	private int selectedTileIndex = -1;
	private boolean selectedTileValidPlay = false;

	// The index of the hand tile that is currently "Active"
	// (Active means that this is the piece that will be placed on the board.
	private int activeTileIndex = -1;

	// Indication of whether or not to draw the trash icon to indicate
	// discarding of the tile.
	private boolean drawTrash;

	// Parameters used to draw the board.
	// Note: These are intimately connected with the size of the images.
	//       but there is no way, in general, to convert image sizes into
	//       these parameters.  Therefore, it is *not* possible to change
	//       the size of the images and have the game change the size of
	//       the board.  Sorry.
	private static final int SPACE_WIDTH = 76;
	private static final int SPACE_HEIGHT = 66;
	private static final int HALF_SPACE_WIDTH = (SPACE_WIDTH / 2);
	private static final int HALF_SPACE_HEIGHT = (SPACE_HEIGHT / 2);
	private static final int CENTER_VERTICAL_OFFSET = 12;
	private static final int BORDER = 5;
	private static final int VERT_OFFSET = 5;
	private static final int HORIZ_SPACE = 5;
	private static final int POLYHEIGHT = SPACE_HEIGHT + BORDER * 2 + VERT_OFFSET;
	private static final int TRASH_X_UP = 30;
	private static final int TRASH_X_DOWN = 0;
	private static final int TRASH_Y = 25;

	// Black color
	private final static Color blackColor = new Color (0,0,0);

	// Constructor which creates the player's hand
	public TriangulumPlayerHandComponent (TriangulumModel model,
	                                       int myPlayerSeatNum,
	                                       Point myAnchor) {

		// Link to the model & graphics helper
		this.model = model;
		this.spGraphics = TriangulumGraphics.getInstance();

		// Save parameters
		this.myPlayerSeatNum = myPlayerSeatNum;
		this.myAnchor = new Point (myAnchor);

		// Setup the background polygon
		outlineAreaIndex = TriangulumImages.FOUR_PIECE_OUTLINE;
		int [] x = {0,
			     SPACE_WIDTH * 2 + HORIZ_SPACE * 4 + BORDER * 2 - 2,
			     SPACE_WIDTH * 2 + HORIZ_SPACE * 4 + BORDER * 2 + 47 - 2,
			     47};
		if (model.getNumPiecesPerPlayer() == 5) {
			outlineAreaIndex = TriangulumImages.FIVE_PIECE_OUTLINE;
			x[1] += (SPACE_WIDTH + HORIZ_SPACE * 2 + 5);
			x[2] -= 2;
		}
		int [] y = {0, 0, POLYHEIGHT, POLYHEIGHT};
		backgroundPoly = new Polygon (x, y, 4);
		backgroundPoly.translate(myAnchor.x, myAnchor.y);

		// Set the locations of the player pieces.
		for (int i=0; i<5; i++) {
			// Compute anchor points
			pieceAnchor[i] = new Point (myAnchor);
			pieceAnchor[i].translate(BORDER*2, BORDER);
			pieceAnchor[i].translate((HALF_SPACE_WIDTH + HORIZ_SPACE) * i, -1);
			pieceAnchor[i].translate(1, 0);
			if ((i & 1) != 0) {
				pieceAnchor[i].translate(0, VERT_OFFSET);
			}

			// Computer center points
			pieceCenter[i] = new Point (pieceAnchor[i]);
			pieceCenter[i].translate(spGraphics.imageWidths [TriangulumImages.PIECE_OUTLINE] / 2,
			                         spGraphics.imageHeights[TriangulumImages.PIECE_OUTLINE] / 2);
		}

		// Finally, translate the anchor point a little bit up & left to point to
		// where the outline image should be.  Since we've already computed the
		// locations of things, we don't need the original anchor point anymore.
		this.myAnchor.translate(-1, -2);
	}

	/**
	 * Set the player seat number whose hand we should draw.
	 */
	public void setSeatNumber (int newSeatNum) {
		myPlayerSeatNum = newSeatNum;
	}

	/**
	 * Get the location of the right-most part of the player area.
	 */
	public int getRightBound () {
		return myAnchor.x + spGraphics.imageWidths[outlineAreaIndex] + BORDER;
	}

	/**
	 * Get the currently active hand tile index.
	 */
	public int getActiveHandTileIndex () {
		return activeTileIndex;
	}

	/**
	 * Draw this hand on the given graphics context.
	 */
	private final static boolean [] upwardTile = {false, true, false, true, false};
	public void paintHand (Graphics g) {
		// Paint the background section
		g.setColor(spGraphics.getPlayerColor(myPlayerSeatNum));
		g.fillPolygon(backgroundPoly);
		g.setColor(blackColor);
		spGraphics.paintImage(g, myAnchor, outlineAreaIndex, 0, 0);
///		g.drawPolygon(backgroundPoly);

		// Draw the active tile highlight
		if (activeTileIndex >= 0) {
			spGraphics.paintImage(g,
			                      pieceCenter[activeTileIndex].x,
			                      pieceCenter[activeTileIndex].y,
			                      TriangulumImages.ACTIVE_HIGHLIGHT,
			                      (upwardTile[activeTileIndex] ? 0 : 1),
			                      0);
		}

		// Paint the player's tiles
		for (int i=0; i<model.getNumPiecesPerPlayer(); i++) {
			spGraphics.drawAPiece(g,
			                      model.getPlayerPiece(myPlayerSeatNum, i),
			                      pieceAnchor[i],
			                      upwardTile[i]);
		}

		// Draw the selected tile highlight if it is in the tile hand.
		if (selectedTileIndex >= 0) {
			int x = pieceCenter[selectedTileIndex].x;
			int y = pieceCenter[selectedTileIndex].y;
			spGraphics.paintImage(g,
			                      x, y,
			                      TriangulumImages.SELECTION_HIGHLIGHTS,
			                      (upwardTile[selectedTileIndex] ? 0 : 1),
			                      selectedTileValidPlay ? 1 : 0);

			// Draw the trash can icon, if it's enabled
			if (drawTrash) {
				y += TRASH_Y;
				x += (upwardTile[selectedTileIndex] ? TRASH_X_UP : TRASH_X_DOWN);
				spGraphics.paintImage(g, x, y,
				                      TriangulumImages.TRASH, 0, 0);
			}
		}

	}

	/**
	 * Set the highlighted point to be in the player's hand area at the given
	 * tile index.
	 *
	 * @param newTileIndex   The new index to set as the highlighted tile.
	 * @return true if this is different than the currently selected tile index.
	 *         false if this is the same as the currently selected tile index.
	 */
	public boolean setHandTileHighlightPoint (int newTileIndex, boolean validMove) {
		// If we're asked to select a non-existant tile, then don't highlight
		// anything.
		if (model.getPlayerPiece(myPlayerSeatNum, newTileIndex) == null) {
			newTileIndex = -1;
		}

		// Change the selection
		if (newTileIndex != selectedTileIndex) {
			selectedTileIndex = newTileIndex;
			selectedTileValidPlay = validMove;

			// If we're being asked to highlight a non-valid move and the player
			// doesn't have any moves, then we want to draw the trash icon to indicate
			// that clicking will result in discard of this piece.
			drawTrash = (!validMove &&
			             (newTileIndex >= 0) &&
			             !model.hasValidMove(myPlayerSeatNum));

			return true;
		}
		return false;
	}

	/**
	 * Set the activated tile to be the given tile index.
	 *
	 * @param newTileIndex   The new index to set as the activated tile.
	 * @return true if this is diferent than the currently activated tile index.
	 *         false if this is the same as the currently activated tile index.
	 */
	public boolean setActivatedHandTile (int newTileIndex) {
		if (newTileIndex != activeTileIndex) {
			activeTileIndex = newTileIndex;
			return true;
		}
		return false;
	}

	/**
	 * Determine which hand tile the given point lies within.
	 *
	 * @param (x,y) The point to decode.
	 * @return the index (0..4) of the hand tile that the point is within or
	 *         -1 if the point isn't within the tile.
	 */
	public int decodeHandTile (int x, int y) {
		for (int i=0; i<model.getNumPiecesPerPlayer(); i++) {
			Point anchor = pieceAnchor[i];
			if (isPointInHandTile(x - anchor.x, y - anchor.y, upwardTile[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Determine if the given point lies within the piece with the given
	 * anchor point and pointing in the given direction.
	 *
	 * @param (x,y)        The point to test.
	 * @param isUpward     True if this piece points upward.
	 * @return if the point lies within the triangle of the piece.
	 */
	private boolean isPointInHandTile (int x, int y, boolean isUpward) {
		int t1 = y * HALF_SPACE_WIDTH;
		int t2 = x * SPACE_HEIGHT;
		int t3 = HALF_SPACE_WIDTH * SPACE_HEIGHT;

		if (isUpward) {
			// Is upward pointing
			return ( ( y <= SPACE_HEIGHT) &&
			         ((t1 - t2) >= -t3) &&
			         ((t1 + t2) >= t3)
			       );
		} else {
			// Is downward pointing
			return ( ( y >= 0) &&
			         ((t1 - t2) <= 0) &&
			         ((t1 + t2) <= (2 * t3))
			       );
		}
	}
}
