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

import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.AbstractTriangleBoards.AbstractTriangleTriangleBoardComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;

import java.util.Vector;
import java.util.ListIterator;

import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;


/**
 * Main visual view for a game of Triangulum which should show a
 * graphical representation of the TriangulumModel.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumComponent extends AbstractTriangleTriangleBoardComponent {

	private static final Color GRAD_COLOR_TOP = Color.white;
	private static final Color GRAD_COLOR_BOTTOM = new Color (220, 220, 220);
	
	// Link to the model
	protected TriangulumModel model;

	// Link to the graphics helper routines
	private TriangulumGraphics spGraphics;

	// The active valid orientations for the currently highlighted space on the
	// board.  If null, then this space is not a valid move.
	private TriangulumPiece [] activeValidOrientations;

	// The index into activeValidOrientations that is currently chosen.
	private int currentOrientationIndex;

	// The number of valid orientations in activeValidOrientations
	private int numValidOrientations;

	// Parameters for the board space that is currently highlighted (has the
	// mouse in it.
	private Point highlightScreenAnchor;
	private Point highlightCenter;
	private boolean highlightPointUpward;

	// The center locations for the small icons currently being displayed
	private Point [] smallIconCenter = new Point [6];

	// The number of players in the game.
	private int numPlayers;

	// The player number that my client is.
	private int myPlayerSeatNum;

	// The helpers for drawing the player hands
	private TriangulumPlayerHandComponent [] playerHands;

	// The helper for the player scores
	private TriangulumPlayerScoreComponent scoreHelper;

	// The sensitivity radius of the small icons, squared.
	private int smallIconRadiusSquared;

	// The stroke used to outline the last moves
	private BasicStroke lastMoveStroke = new BasicStroke(3);

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
	private static final int BORDER = 10;
	private static final int OVERHANG_SIZE = 20;

	// A private class to hold locations of non-piece items on the screen
	private class screenItem {
		private Point screenLoc;
		private int imageId;
		private int imageIndex;

		public screenItem(Point screenLoc, int imageId, int imageIndex) {
			this.screenLoc = screenLoc;
			this.imageId = imageId;
			this.imageIndex = imageIndex;
		}

		public void drawItem(Graphics g, TriangulumGraphics graphicsHelper) {
			graphicsHelper.paintImage(g, screenLoc, imageId, imageIndex, 0);
		}
	}

	// A vector to hold all of the screen items created.
	private Vector screenItems;

	// Constructor which creates the board
	public TriangulumComponent (TriangulumModel model, int numRows) {
		super (
			numRows,
			new Dimension(SPACE_WIDTH, SPACE_HEIGHT),
			HALF_SPACE_WIDTH
		);

		// Link to the model & graphics helper
		this.model = model;
		this.spGraphics = TriangulumGraphics.getInstance();

		// We observe the model
		model.addObserver(this);

		// Set the inset & outset for the component.
		setInset(new Dimension (-HALF_SPACE_WIDTH + BORDER, BORDER));
		setOutset(new Dimension (BORDER, BORDER));

		// Compute things that only need to be done once.
		int smallHighlightRadius = (spGraphics.imageWidths[TriangulumImages.SMALL_HIGHLIGHT] >> 1);
		smallIconRadiusSquared = smallHighlightRadius * smallHighlightRadius;
		numPlayers = model.getNumPlayers();
		screenItems = new Vector ();

		// Create the screen items
		for (int row = 0; row < existsArray[0].length; row++) {
			for (int col = 0; col < existsArray.length; col++) {
				// Add basic board spaces (only for upward triangles)
				if (existsOnBoard(col, row) && isUpwardTriangle(col, row)) {
					screenItems.add(createBoardTile(col, row));
				}

				// Add multiplier spaces (if this space is a multipler)
				int multiplier = model.getMultiplier(col, row);
				if (multiplier > 1) {
					screenItems.add(createMultiplierPoint(col, row, multiplier));
				}
			}
		}

		// Add the emblem, if this is on the big board
		if (model.getFlavor() == 60) {
			screenItems.add(createEmblem());
		}

		// Create the small icon array
		for (int i=0; i<smallIconCenter.length; i++) {
			smallIconCenter[i] = new Point (0, 0);
		}

		// Create the player hand areas
		playerHands = new TriangulumPlayerHandComponent[numPlayers];
		Point currAnchor = (model.getFlavor() == 36) ?
		              getScreenAnchorFor(new Point (7, 0)) :
		              getScreenAnchorFor(new Point (9, 0));
		currAnchor.translate(BORDER + BORDER, BORDER);
		for (int i=0; i<numPlayers; i++) {
			playerHands[i] = new TriangulumPlayerHandComponent (model, i, currAnchor);
			currAnchor.translate(HALF_SPACE_WIDTH + 15, SPACE_HEIGHT + 26);
		}

		// Create the player score area
		scoreHelper = new TriangulumPlayerScoreComponent(this, model, new Point (BORDER, BORDER));

		// Recompute the preferred dimension now that we've changed the geometry.
		int rightSide = playerHands[numPlayers-1].getRightBound();
		Dimension TriangleDim = getBoardComponentDim();
		setPreferredSize ( new Dimension (rightSide, TriangleDim.height + OVERHANG_SIZE) );
	}

	/**
	 * Create a new Point with screen coordinates for the center of given board space.
	 *
	 * @param col       The logical column of the triangle
	 * @param row       The logical row of the triangle
	 * @return a Point that will be set to the screen center of the triangle.
	 */
	public Point logicalToScreenCenter (int col, int row) {
		Point anchorPoint = getScreenAnchorFor (col, row);
		anchorPoint.translate(HALF_SPACE_WIDTH, HALF_SPACE_HEIGHT);
		return anchorPoint;
	}

	/**
	 * Create a new screen item for a multiplier sign.
	 */
	private screenItem createMultiplierPoint (int col, int row, int multValue) {
		Point newPoint = logicalToScreenCenter(col, row);

		if (isUpwardTriangle(col, row)) {
			newPoint.translate(0,  CENTER_VERTICAL_OFFSET);
		} else {
			newPoint.translate(0, -CENTER_VERTICAL_OFFSET);
		}

		return new screenItem (newPoint, TriangulumImages.MULTIPLIERS, multValue - 2);
	}

	/**
	 * Create a new screen item for a background board tile.
	 */
	private screenItem createBoardTile (int col, int row) {
		return new screenItem (logicalToScreenCenter(col, row),
		                       TriangulumImages.BOARD_TILE,
		                       0);
	}

	/**
	 * Create the emblem item.
	 */
	private screenItem createEmblem () {
		Point center = logicalToScreenCenter(8, 5);
		center.translate(0, -33);
		return new screenItem (center , TriangulumImages.EMBLEM, 0);
	}

	/**
	 * Update the graphics depending on the model.
	 */
	public void paintComponent (Graphics g) {
		
		JogreAwt.drawVerticalGradiant(g, 0, 0, getWidth(), getHeight(), GRAD_COLOR_TOP, GRAD_COLOR_BOTTOM);
		
		// Draw the basic board elements
		ListIterator iter = screenItems.listIterator();
		while (iter.hasNext()) {
			((screenItem) iter.next()).drawItem(g, spGraphics);
		}

		// Draw all of the pieces on the board.
		for (int row = 0; row < existsArray[0].length; row++) {
			for (int col = 0; col < existsArray.length; col++) {
				spGraphics.drawAPiece(g,
				                      model.getPieceAt(col, row), 
				                      getScreenAnchorFor(col, row),
				                      isUpwardTriangle(col, row));
			}
		}

		// Draw the player's last moves.
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(lastMoveStroke);
		Color bgColor = new Color (230, 230, 230);
		for (int i=0; i<numPlayers; i++) {
			Point lastMove = model.getLastMove(i);
			if (lastMove != null) {
				int col = lastMove.x;
				int row = lastMove.y;

				// Get anchor point
				Point anchorPoint = getScreenAnchorFor(col, row);

				
				// Get correct polygon
				Polygon basePoly = isUpwardTriangle(col, row) ?
				                         baseTriPoly[0][row & 0x01] :
				                         baseTriPoly[1][row & 0x01];

				// Move it into position
				basePoly.translate(anchorPoint.x, anchorPoint.y);

				// Outline with the correct player color
				g.setColor(bgColor);
				g.fillPolygon (basePoly);
				g.setColor(spGraphics.getPlayerColor(i));
				g.drawPolygon (basePoly);

				// Move the base polygon back to to the origin
				basePoly.translate (-anchorPoint.x, -anchorPoint.y);
			}
		}

		// Draw the player's hand pieces
		for (int i=0; i<numPlayers; i++) {
			playerHands[i].paintHand(g);
		}

		// Draw the player scores
		scoreHelper.paintScores(g);

		// If there is a space highlighted, then draw the highlight outline.
		if (existsOnBoard(highlightPoint)) {

			// If there are active valid orientations, then draw the current one
			if (activeValidOrientations != null) {
				if (numValidOrientations > 1) {
					// Draw the highlight for the current orientation
					spGraphics.paintImage(g,
					                      smallIconCenter[currentOrientationIndex],
					                      TriangulumImages.SMALL_HIGHLIGHT,
					                      0, 0);
				}

				// Draw the current piece
				spGraphics.drawAPiece(g,
				                      activeValidOrientations[currentOrientationIndex],
				                      highlightScreenAnchor,
				                      highlightPointUpward);

				// Draw the green valid outline indicator
				spGraphics.paintImage(g, highlightCenter,
				                      TriangulumImages.SELECTION_HIGHLIGHTS,
				                      (highlightPointUpward ? 0 : 1),
				                      1);

				if (numValidOrientations > 1) {
					// Draw the small tiles
					for (int i=0; i<activeValidOrientations.length; i++) {
						spGraphics.drawSmallPiece(g,
						                          activeValidOrientations[i],
						                          smallIconCenter[i],
						                          highlightPointUpward);
					}
				}

			} else {
				// Draw the red invalid outline indicator
				spGraphics.paintImage(g, highlightCenter,
				                      TriangulumImages.SELECTION_HIGHLIGHTS,
				                      (highlightPointUpward ? 0 : 1),
				                      0);
			}
		}
	}

	// Offsets for the small tile icons.
	// Tables for small icons when two colors on the tile are the same:
	private static final int [][] smallOffset_3_up   = {{40, 75}, {14, 24}, {64, 24}};
	private static final int [][] smallOffset_3_down = {{40, -7}, {13, 43}, {67, 43}};

	// Tables for small icons when all three colors are different and all 6
	// orientations are valid:
	private static final int [][] smallOffset_6_up   = {{21, 74}, {56, 74}, {73, 42},
	                                                    {58, 12}, {21, 12}, {4, 42}};
	private static final int [][] smallOffset_6_down = {{22, -6}, {56, -6}, {75, 27},
	                                                    {60, 56}, {20, 56}, {4, 27}};

	// Tables for small icons when all three colors are different, but only 2
	// orientations are valid:
	private static final int [][] smalloffset_62_indexTable = {
		{0, 1, 0}, {2, 5, 0}, {3, 4, 0},
		{0, 3, 1}, {1, 2, 1}, {4, 5, 1},
		{0, 5, 2}, {1, 4, 2}, {2, 3, 2}
	};

	private static final int [][][] smallOffset_62_up = {
		{{14, 24}, {64, 24}, {14, 24}, {64, 24}, {14, 24}, {64, 24}},
		{{40, 75}, {64, 24}, {40, 75}, {64, 24}, {40, 75}, {64, 24}},
		{{40, 75}, {14, 24}, {40, 75}, {14, 24}, {40, 75}, {14, 24}}
	};

	private static final int [][][] smallOffset_62_down = {
		{{13, 43}, {67, 43}, {13, 43}, {67, 43}, {13, 43}, {67, 43}},
		{{40, -7}, {67, 43}, {40, -7}, {67, 43}, {40, -7}, {67, 43}},
		{{40, -7}, {13, 43}, {40, -7}, {13, 43}, {40, -7}, {13, 43}}
	};

	/**
	 * Set the center locations for the small piece icons.
	 */
	private void setSmallIconLocations (Point newHighlightPoint) {
		int [][] smallOffset;

		if (activeValidOrientations.length == 3) {
			// Two colors are the same, so only 3 orientations
			smallOffset = highlightPointUpward ? smallOffset_3_up : smallOffset_3_down;
		} else if (activeValidOrientations.length == 6) {
			// Three colors are different, so 6 orientations
			if (numValidOrientations == 6) {
				// All 6 are valid orientations
				smallOffset = highlightPointUpward ? smallOffset_6_up : smallOffset_6_down;
			} else {
				// Only 2 are valid orientations, so pick the right two.
				int activeTableIndex = findActiveTableIndex();
				smallOffset = highlightPointUpward ?
				                        smallOffset_62_up  [activeTableIndex] :
				                        smallOffset_62_down[activeTableIndex];
			}
		} else {
			// If some other length is provided, then there are no small icons
			return;
		}

		// Compute the locations for the small icons
		Point screenAnchor = getScreenAnchorFor(newHighlightPoint.x, newHighlightPoint.y);

		for (int i=0; i<activeValidOrientations.length; i++) {
			smallIconCenter[i].setLocation(screenAnchor.x + smallOffset[i][0],
			                               screenAnchor.y + smallOffset[i][1]);
		}
	}

	/*
	 * When all 3 colors are different and the piece is being placed next to one
	 * and only one existing piece, then there are only two valid orientations.
	 * This function will use table look ups to determine where to place the
	 * small icons in this case.
	 */
	private int findActiveTableIndex() {
		for (int i=0; i<smalloffset_62_indexTable.length; i++) {
			if ((activeValidOrientations[smalloffset_62_indexTable[i][0]] != null) &&
			    (activeValidOrientations[smalloffset_62_indexTable[i][1]] != null)) {
				return smalloffset_62_indexTable[i][2];
			}
		}

		// Note: Should never get here...
		return 0;
	}

	/**
	 * Determine if the currentOrientationIndex is still a valid orientation.
	 * If not, then find one that is and set that.
	 */
	private void validateCurrentOrientationIndex () {
		if ((currentOrientationIndex < activeValidOrientations.length) &&
		    (activeValidOrientations[currentOrientationIndex] != null)) {
			return;
		}

		// Find the first non-null entry and use that.
		for (int i=0; i<activeValidOrientations.length; i++) {
			if (activeValidOrientations[i] != null) {
				currentOrientationIndex = i;
				return;
			}
		}

		// Shouldn't be able to get here.
		currentOrientationIndex = 0;
	}
	
	/**
	 * Set the highlighted point to be on the board at the given logical coordinates.
	 *
	 * @param newHighlightPoint   The new point on the board (in logical coordinates)
	 *                            to highlight.
	 */
	public void setBoardHighlightPoint (Point newHighlightPoint) {
		// Change the point.
		if (setHighlightPoint(newHighlightPoint)) {
			// This is a new point, so we need to determine if this space is a
			// valid move for the currently active hand tile or not.
			int activeTileIndex = playerHands[0].getActiveHandTileIndex();

			// Determine paramters of this new highlighted point.
			int col = newHighlightPoint.x;
			int row = newHighlightPoint.y;
			highlightScreenAnchor = getScreenAnchorFor(col, row);
			highlightCenter = logicalToScreenCenter(col, row);
			highlightPointUpward = isUpwardTriangle(col, row);

			if (activeTileIndex < 0) {
				// No active tile, so all spaces are invalid
				activeValidOrientations = null;
			} else {
				// There is an active tile, so determine the valid orientations
				// for the active tile on this spot.
				activeValidOrientations =
				           model.getValidOrientationsForPiece(myPlayerSeatNum,
				                                              activeTileIndex,
				                                              newHighlightPoint);

				if (activeValidOrientations != null) {
					// Count the number of valid orientations
					numValidOrientations = 0;
					for (int i=0; i<activeValidOrientations.length; i++) {
						if (activeValidOrientations[i] != null) {
							numValidOrientations += 1;
						}
					}

					// Calculate the graphical locations for the small icons
					setSmallIconLocations(newHighlightPoint);

					// If the currentOrientationIndex is not a valid move, then we
					// need to find one.
					validateCurrentOrientationIndex();
				}
			}
		}

		playerHands[0].setHandTileHighlightPoint(-1, false);
	}

	/**
	 * See if the given point is within the sensitivity radius of the given
	 * small icon.
	 */
	private boolean withinSmallIcon(int x, int y, int iconIndex) {
		int xd = (x - smallIconCenter[iconIndex].x);
		int yd = (y - smallIconCenter[iconIndex].y);
		return (((xd * xd) + (yd * yd)) < smallIconRadiusSquared);
	}

	/**
	 * Try to use the given point to select one of the small icons currently
	 * displayed.
	 *
	 * @param (x,y)    Location to check.
	 * @return true => The point lies within one of the small icons and that
	 *                 small icon has now been selected.
	 *         false => The point does not lie within one of the small icons.
	 */
	public boolean selectSmallIcon (int x, int y) {
		// Only check for small icons if there are active orientations drawn.
		if (activeValidOrientations != null) {
			// First, check if we're within the radius of the currently selected
			// orientation.  This provides hysteresis such that we only change
			// orientations when we move away from the current one.
			if (withinSmallIcon(x, y, currentOrientationIndex)) {
				return true;
			}

			// Not in the current one, so check all of the small icons
			for (int i=0; i<activeValidOrientations.length; i++) {
				if (withinSmallIcon(x, y, i)) {
					// Yup, point is in this icon ...
					if (activeValidOrientations[i] != null) {
						// ... and it's a valid orientation, and it must be different
						// from the current one (because we already checked for the
						// current index before beginning the search here).
						currentOrientationIndex = i;
						repaint();
						return true;
					}
				}
			}
		}

		// Point is not inside any of the icons.
		return false;
	}

	/**
	 * Set the highlighted point to be in the player's hand area at the given
	 * tile index.
	 */
	public void setHandTileHighlightPoint (int tileIndex) {
		// Change the point.
		setHighlightPoint(OFF_SCREEN_POINT);
		activeValidOrientations = null;

		// Tell the player hand to update its highlighting as well
		boolean validPiece = model.numValidMovesForPiece(myPlayerSeatNum, tileIndex) > 0;
		if (playerHands[0].setHandTileHighlightPoint(tileIndex, validPiece)) {
			repaint();
		}
	}

	/**
	 * Clear all highlights, both on the main board and in the hand.
	 */
	public void clearHighlights () {
		// Clear the board highlight
		setHighlightPoint(OFF_SCREEN_POINT);
		activeValidOrientations = null;

		// Clear the hand highlight
		playerHands[0].setHandTileHighlightPoint(-1, false);
		playerHands[0].setActivatedHandTile(-1);
	}

	/**
	 * Activate the hand tile at the given index.
	 * Note: The hand tile is only activated if it has a valid move.
	 *
	 * @param tileIndex    The index in the hand for the tile to be activated
	 * @return true if this piece should be discard
	 */
	public void setActivatedHandTile (int tileIndex) {
		if (playerHands[0].setActivatedHandTile(tileIndex)) {
			repaint();
		}
	}

	/**
	 * Determine which hand tile the given point lies within.
	 *
	 * @param (x,y) The point to decode.
	 * @return the index (0..4) of the hand tile that the point is within or
	 *         -1 if the point isn't within the tile.
	 */
	public int decodeHandTile (int x, int y) {
		return playerHands[0].decodeHandTile(x,y);
	}

	/**
	 * Return the currently active piece, including rotations, or null if there
	 * isn't an active piece.
	 */
	public TriangulumPiece getActivePiece () {
		if (activeValidOrientations != null) {
			return activeValidOrientations[currentOrientationIndex];
		}
		return null;
	}

	/**
	 * Return the index in the player's hand for the active piece.
	 */
	public int getActiveHandTileIndex () {
		return playerHands[0].getActiveHandTileIndex();
	}

	/**
	 * Change the seat number that is displayed as the top one.
	 *
	 * @param newSeatNumber   The new seat number to make the top.
	 */
	public void setSeatNumber(int newSeatNumber) {
		// If we're not seated, then display number 0 at the top.
		if (newSeatNumber < 0) {
			newSeatNumber = 0;
		}

		// Only need to actually change things if the new seat number is
		// different than what we're showing now.
		if (newSeatNumber != myPlayerSeatNum) {
			// Set our seat number
			myPlayerSeatNum = newSeatNumber;

			// Set all of the player hand areas
			for (int i=0; i<numPlayers; i++) {
				playerHands[i].setSeatNumber((newSeatNumber + i) % numPlayers);
			}

			// Set the score area to start with the correct new seat number
			scoreHelper.setFirstSeatNumber(newSeatNumber);

			repaint();
		}
	}
}
