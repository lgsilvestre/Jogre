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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.BasicStroke;

import java.awt.geom.GeneralPath;

import java.util.Vector;

import org.jogre.warwick.common.WarwickModel;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

/**
 * Main visual view for a game of warwick which should show a
 * graphical representation of the WarwickModel.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickBoardComponent extends JogreComponent {

	// Link to the model
	protected WarwickModel model;

	// Graphics helper
	protected WarwickGraphics kGraphics;

	// # of players in the game
	private int numPlayers;

	// The player # of my client.
	private int mySeatNum;

	// The locations of the allegience cards that can be selected
	private Rectangle [] allegienceCardRects;

	// The id's of the allegience cards that can be selected
	private int [] allegienceCardOwners;

	// The index of the currently moused-over allegience card during allegience selection phase.
	private int currentAllegienceCardSelection = -1;

	// The current location of the potential piece during placement phase.
	private int currentPotentialPieceRegion = -1;
	private int currentPotentialPieceSpace = -1;

	// The index of the currently moused-over piece during piece movement phase.
	private int currentSlideSelectionRegion = -1;
	private int currentSlideSelectionSpace = -1;
	private int currentSlideTargetRegion = -1;
	private int currentSlideTargetSpace = -1;

	// The line showing the current slide
	private GeneralPath slideLine = null;
	private Color slideLineColor = null;

	// The outlines of the 4 regions of the board
	private GeneralPath [] regionOutlines = new GeneralPath [4];
	private Stroke outlineStroke = new BasicStroke (6);
	private Color outlineColor = null;

	// The lines that are used to show how a player made his last score
	private GeneralPath [] scoreLine = new GeneralPath [2];
	private Color underScoreLineColor = null;
	private Color [] playerColor;
	private Stroke underScoreLineStroke = new BasicStroke (8);
	private Stroke scoreLineStroke = new BasicStroke (4);

	// The player whose score line is shown
	private int mainScorePlayer;

	// The allied player of the player whose score line is shown.
	private int allyScorePlayer;

	// The locations of the score markers around the board, in pixels
	private Point [] scoreMarkerLocation;

	// # of players which have the given score
	private int [] scoreCount = new int [100];

	// # of players which have had their score marker drawn on a given space already.
	private int [] scoreIndex = new int [100];

	// Half of the board width/height
	private int halfBoardWidth, halfBoardHeight;

	// Constructor which creates the board
	public WarwickBoardComponent (WarwickModel model) {
		super ();

		// Save parameters
		this.model = model;
		kGraphics = WarwickGraphics.getInstance();

		numPlayers = model.getNumPlayers();

		// Create stuff
		createAllegienceCardStuff();
		playerColor = new Color [numPlayers];
		scoreMarkerLocation = new Point [numPlayers];

		// Initialize stuff
		for (int i=0; i<100; i++) {
			scoreCount[i] = -1;
			scoreIndex[i] = 0;
		}

		for (int i=0; i<numPlayers; i++) {
			scoreMarkerLocation[i] = new Point (-50, -50);
		}

		scoreLine[0] = null;
		scoreLine[1] = null;

		setNewAllegienceCardSelection(-1);
		setNewSlideSelection(-1, -1);
		setNewPotentialPieceLocation(-1, -1);
		setNewScoreMarkerSelection(-1);

		setMySeatNum(-1);

		for (int i=0; i<4; i++) {
			regionOutlines[i] = new GeneralPath();
			regionOutlines[i].moveTo(outlinePoints[i][0], outlinePoints[i][1]);
			regionOutlines[i].lineTo(280.0f, 280.0f);
			regionOutlines[i].lineTo(outlinePoints[i][2], outlinePoints[i][3]);
			regionOutlines[i].quadTo(outlinePoints[i][4], outlinePoints[i][5],
			                         outlinePoints[i][0], outlinePoints[i][1]);
		}

		halfBoardWidth  = kGraphics.imageWidths [WarwickGraphics.BOARD] / 2;
		halfBoardHeight = kGraphics.imageHeights[WarwickGraphics.BOARD] / 2;

		// Set component dimension
		Dimension dim = new Dimension (
		                 kGraphics.imageWidths[WarwickGraphics.BOARD],
		                 kGraphics.imageHeights[WarwickGraphics.BOARD]);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/*
	 * Create and initialize some of the allegience card stuff.
	 * Since these structures depend only on the number of players in the game,
	 * they can be done once during construction of the board component.
	 */
	private void createAllegienceCardStuff () {
		// Create new arrays.
		allegienceCardRects = new Rectangle [numPlayers-1];
		allegienceCardOwners = new int [numPlayers-1];

		// Position the allegience card rectangles on the screen.
		int x = (190 - (numPlayers-3) * 50);
		for (int i=0; i<numPlayers-1; i++) {
			allegienceCardRects[i] = new Rectangle (x, 45,
			             kGraphics.imageWidths[WarwickGraphics.ROSE_CARDS],
		                 kGraphics.imageHeights[WarwickGraphics.ROSE_CARDS]);
			x += 100;
		}
		
	}

	/**
	 * Set the seat number for the player that I'm supposed to display info for.
	 */
	public void setMySeatNum (int newSeatNum) {
		// Save the seat Number
		mySeatNum = newSeatNum;

		// Reset the seat numbers of the allegience cards to exclude my new
		// seat number.
		for (int i=0; i<numPlayers-1; i++) {
			allegienceCardOwners[i] = (i < newSeatNum) ? i : i+1;
		}
	}

	/**
	 * Select an allegience card given a graphical location on the screen.
	 *
	 * @param (x,y)    The location on the screen that should be tested.
	 * @return true if this new location changes the current selected allegience
	 *         card, and false if it does not.
	 */
	public boolean selectAllegienceCardAt (int x, int y) {
		return setNewAllegienceCardSelection (findCardAt(x, y));
	}

	/*
	 * Find the index of the allegience card that contains the given point.
	 */
	private int findCardAt (int x, int y) {
		for (int i = 0; i < allegienceCardRects.length; i++) {
			if (allegienceCardRects[i].contains(x, y)) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * Change the current selected allegience card to the given index.
	 *
	 * @param newIndex  The new index to set the selection to.
	 * @return true if this new selection is different than the old one.
	 */
	private boolean setNewAllegienceCardSelection (int newIndex) {
		if (newIndex != currentAllegienceCardSelection) {
			currentAllegienceCardSelection = newIndex;
			return true;
		}
		return false;
	}

	/**
	 * Unselect the current allegience card.
	 */
	public void clearAllegienceCardSelection () {
		setNewAllegienceCardSelection (-1);
	}

	/**
	 * Return the currently selected allegience card.
	 */
	public int getSelectedAllegience () {
		return (currentAllegienceCardSelection >= 0) ?
		             allegienceCardOwners[currentAllegienceCardSelection] :
		             -1;
	}

	// This array provides the neighbors for each space in the quarters of
	// the game board.
	// Arranged as [nodeNum][neighborNum], so the 3rd neighbor of node 6 is
	//   pieceNieghbors[6][3], which is 11.  (Nodes & nieghbors are numbered
	//   from 0.)
	private int [][] pieceNeighbors = {
		{1, 2, -1, -1, -1, -1},
		{0, 2, 3, 4, -1, -1},
		{0, 1, 4, 5, -1, -1},
		{1, 4, 6, 7, -1, -1},
		{1, 2, 3, 5, 7, 8},
		{2, 4, 8, 9, -1, -1},
		{3, 7, 10, 11, -1, -1},
		{3, 4, 6, 8, 11, 12},
		{4, 5, 7, 9, 12, 13},
		{5, 8, 13, 14, -1, -1},
		{6, 11, -1, -1, -1, -1},
		{6, 7, 10, 12, -1, -1},
		{7, 8, 11, 13, -1, -1},
		{8, 9, 12, 14, -1, -1},
		{9, 13, -1, -1, -1, -1},
	};

	// The location of the centers of the pieces on the screen (in pixels).
	// Arranged as [quarter][nodeNum].
	private int [][] pieceCentersX = {
		{281,  251, 311,  221, 281, 341,  191, 244, 318, 371,  161, 216, 281, 346, 401},
		{281,  311, 251,  341, 281, 221,  371, 318, 244, 191,  401, 346, 281, 216, 161},
		{240,  210, 210,  180, 173, 180,  150, 135, 135, 150,  120,  97,  86,  97, 120},
		{323,  353, 353,  383, 390, 383,  413, 428, 428, 413,  443, 466, 477, 466, 443}
	};

	private int [][] pieceCentersY = {
		{319,  349, 349,  379, 386, 379,  409, 424, 424, 409,  439, 462, 473, 462, 439},
		{242,  212, 212,  182, 175, 182,  152, 137, 137, 152,  122,  99,  88,  99, 122},
		{280,  250, 310,  220, 280, 340,  190, 243, 317, 370,  160, 215, 280, 345, 400},
		{280,  310, 250,  340, 280, 220,  370, 317, 243, 190,  400, 345, 280, 215, 160},
	};

	// The locations to use for drawing the outline of the regions when placing
	// a new piece on the board.
	private float outlinePoints[][] = {
		{120.0f, 440.0f, 440.0f, 440.0f, 280.0f, 570.0f},
		{120.0f, 120.0f, 440.0f, 120.0f, 280.0f, -10.0f},
		{120.0f, 120.0f, 120.0f, 440.0f, -10.0f, 280.0f},
		{440.0f, 440.0f, 440.0f, 120.0f, 570.0f, 280.0f}
	};

	/**
	 * Move the active shadow piece to the given graphical location on the screen.
	 *
	 * @param (x,y)    The location on the screen to move the shadow piece to.
	 * @return true if this new location changes the current selected shadow
	 *         piece, and false if it does not.
	 */
	public boolean placePotentialPieceAt (int x, int y) {
		// Determine which region & space the point lies closest to.
		int newRegion = translatePointToRegion(x, y);
		int newSpace = -1;

		if (newRegion >= 0) {
			newSpace = snapToClosestSpace(x, y, newRegion);

			// If the space is not empty, then can't place a new piece there.
			if (!model.spaceEmpty(newRegion, newSpace)) {
				newRegion = -1;
				newSpace = -1;
			}
		}

		return setNewPotentialPieceLocation(newRegion, newSpace);
	}

	/*
	 * Find the space closest to the given (x,y) location in the given region
	 * of the board and return the index # of that region.
	 */
	private int snapToClosestSpace (int x, int y, int region) {
		if (region < 0) {
			// Piece isn't on the board.
			return -1;
		}

		int closestDistance = 99999999;
		int closestId = 0;

		for (int i = 0; i < pieceCentersX[region].length; i++) {
			int dx = x - pieceCentersX[region][i];
			int dy = y - pieceCentersY[region][i];

			int d2 = dx * dx + dy * dy;
			if (d2 < closestDistance) {
				closestDistance = d2;
				closestId = i;
			}
		}

		return closestId;
	}

	/*
	 * Move the active shadow piece to the given graphical location on the screen.
	 *
	 * @param region   The region to move the shadow piece to.
	 * @param space    The space to move the shadow piece to.
	 * @return true if this new location changes the current selected shadow
	 *         piece, and false if it does not.
	 */
	private boolean setNewPotentialPieceLocation (int region, int space) {
		// If the point hasn't moved, then we don't have anything to do.
		if ((region == currentPotentialPieceRegion) &&
		    (space == currentPotentialPieceSpace)) {
			return false;
		}

		currentPotentialPieceSpace = space;
		currentPotentialPieceRegion = region;

		return true;
	}

	/**
	 * Unselect the potential piece location.
	 */
	public void clearPotentialPieceSelection () {
		setNewPotentialPieceLocation(-1, -1);
	}

	/*
	 * Translate a graphical point (x,y) to a region of the board (1..4) that
	 * is the value of a piece that would be placed there.
	 */
	private int translatePointToRegion (int x, int y) {
		// Translate the point (x,y) to be relative to the center of the board
		x -= halfBoardWidth;
		y -= halfBoardHeight;

		int r2 = x*x + y*y;
		if (r2 > 227*227) {
			// Point is outside of the circle
			return -1;
		}

		// Determine which quadrant it is in
		if (x < y) {
			return (x < -y) ? 2 : 0;
		} else {
			return (x < -y) ? 1 : 3;
		}
	}

	/**
	 * Return the currently selected potential piece location
	 */
	public int getPotentialPieceRegion () { return currentPotentialPieceRegion; }
	public int getPotentialPieceSpace ()  { return currentPotentialPieceSpace; }

	/**
	 * Select a new board piece that could be slid given a graphical location on
	 * the screen.
	 *
	 * @param (x,y)    The location on the screen that should be tested.
	 * @return true if this new location changes the current selected piece,
	 *         and false if it does not.
	 */
	public boolean selectSlidablePieceAt (int x, int y) {
		// Determine which region & space the point lies closest to.
		int newRegion = translatePointToRegion(x, y);
		int newSpace = -1;

		if (newRegion >= 0) {
			newSpace = snapToClosestSpace(x, y, newRegion);
			if (model.spaceEmpty(newRegion, newSpace)) {
				newRegion = -1;
				newSpace = -1;
			}
		}

		return setNewSlideSelection (newRegion, newSpace);
	}

	// The map of which region is next to which other region on the board.
	private int [] nextRegion = {2, 3, 1, 0};

	/*
	 * Change the current selected region to the given index.
	 *
	 * @param newIndex  The new index to set the selection to.
	 * @return true if this new selection is different than the old one.
	 */
	private boolean setNewSlideSelection (int region, int space) {
		// If the point hasn't moved, then we don't have anything to do.
		if ((region == currentSlideSelectionRegion) &&
		    (space == currentSlideSelectionSpace)) {
			return false;
		}

		// Save the "from" location
		currentSlideSelectionSpace = space;
		currentSlideSelectionRegion = region;

		if (region < 0) {
			// The new selection is an "off-the-board" location, so clear things up.
			currentSlideTargetRegion = -1;
			currentSlideTargetSpace = -1;
			slideLine = null;
			return true;
		}

		// Determine the "to" location
		currentSlideTargetRegion = nextRegion[region];
		currentSlideTargetSpace = findEmptySpaceInRegion(currentSlideTargetRegion, space);

		// Compute the line connecting the two locations
		// Get the two endpoints.
		float p1x = (float) pieceCentersX[currentSlideSelectionRegion][currentSlideSelectionSpace];
		float p1y = (float) pieceCentersY[currentSlideSelectionRegion][currentSlideSelectionSpace];
		float p2x = (float) pieceCentersX[currentSlideTargetRegion][currentSlideTargetSpace];
		float p2y = (float) pieceCentersY[currentSlideTargetRegion][currentSlideTargetSpace];

		Point2D.Float ctrlPt =
		       WarwickGraphics.getControlPoint (p1x, p1y, p2x, p2y, 50.0f);

		// Make the line.
		slideLine = new GeneralPath ();
		slideLine.moveTo(p1x, p1y);
		slideLine.quadTo(ctrlPt.x, ctrlPt.y, p2x, p2y);

		return true;
	}

	/**
	 * Unselect the potential slide information
	 */
	public void clearSlideSelection () {
		setNewSlideSelection(-1, -1);
	}

	/*
	 * Find an empty space in the given region that is close to the requested
	 * space.
	 */
	private int findEmptySpaceInRegion (int region, int startSpace) {
		Vector spaces = new Vector ();
		boolean [] marked = new boolean [15];

		// Seed the vector with the initial space.
		spaces.add(new Integer (startSpace));
		marked[startSpace] = true;

		// Seach for an empty space
		while (spaces.size() != 0) {
			// Pop the top of the vector of possible spaces.
			int trySpace = ((Integer) spaces.remove(0)).intValue();

			if (model.spaceEmpty(region, trySpace)) {
				// Yup, this one is empty, so return it.
				return trySpace;
			}

			// Add the neighbors of this space to the spaces vector.
			marked[trySpace] = true;
			int [] neighbors = pieceNeighbors[trySpace];
			for (int i = 0; i < neighbors.length; i++) {
				int next = neighbors[i];
				if ((next >= 0) && !marked[next]) {
					marked[next] = true;
					spaces.add(new Integer (next));
				}
			}
		}

		// Should never get here!
		return 0;
	}

	/**
	 * Return the currently selected potential piece location
	 */
	public int getSlidePieceRegion () { return currentSlideSelectionRegion; }
	public int getSlidePieceSpace ()  { return currentSlideSelectionSpace; }
	public int getTargetPieceRegion () { return currentSlideTargetRegion; }
	public int getTargetPieceSpace ()  { return currentSlideTargetSpace; }

	/**
	 * Select a new scoring marker at a graphical location on the screen.
	 *
	 * @param (x,y)    The location on the screen that should be tested.
	 * @return true if this new location changes the current selected score
	 *         marker piece, and false if it does not.
	 */
	public boolean selectScoreMarkerAt (int x, int y) {
		return setNewScoreMarkerSelection(findScoreMarkerAt(x, y));
	}

	/*
	 * Determine if the given screen location is within a scoring marker.
	 *
	 * @return the player number of that scoring marker, or -1 if there isn't
	 *         one there.
	 */
	private int findScoreMarkerAt (int x, int y) {
		int halfScoreWidth  = kGraphics.imageWidths [WarwickGraphics.SCORE_MARKERS] / 2;
		int halfScoreHeight = kGraphics.imageHeights[WarwickGraphics.SCORE_MARKERS] / 2;
		for (int i = 0; i < numPlayers; i++) {
			int dx = (x - scoreMarkerLocation[i].x);
			int dy = (y - scoreMarkerLocation[i].y);
			if ((dx >= -halfScoreWidth)  && (dx <= halfScoreWidth) &&
			    (dy >= -halfScoreHeight) && (dy <= halfScoreHeight)) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * Set the new score line to the player whose index is given.
	 *
	 * @param newPlayer  The new player to select score marker of.
	 * @return true if this new selection is different than the old one.
	 */
	private boolean setNewScoreMarkerSelection (int newPlayer) {
		if (newPlayer == mainScorePlayer) {
			return false;
		}

		mainScorePlayer = newPlayer;

		if ((newPlayer < 0) || (model.getScore(newPlayer) == 0)) {
			// Deselect the score lines.
			scoreLine[0] = null;
			scoreLine[1] = null;
			return true;
		}

		int playerScore = model.getScore(newPlayer);
		int selfAddition = model.getLastOwnScore(newPlayer);
		int allyAddition = model.getLastAllyScore(newPlayer);
		allyScorePlayer = model.getLastAllegience(newPlayer);

		Point p1 = locationOfScoreBox(playerScore);
		Point p2 = locationOfScoreBox(playerScore - allyAddition);
		Point p3 = locationOfScoreBox(playerScore - allyAddition - selfAddition);

		Point2D.Float c1Pt =
		       WarwickGraphics.getControlPoint (p1, p2, 20.0f);
		Point2D.Float c2Pt =
		       WarwickGraphics.getControlPoint (p2, p3, 20.0f);

		// Make the lines.
		scoreLine[0] = new GeneralPath ();
		scoreLine[0].moveTo((float) p3.x, (float) p3.y);
		scoreLine[0].quadTo((float) c2Pt.x, (float) c2Pt.y,
		                    (float) p2.x, (float) p2.y);
		scoreLine[1] = new GeneralPath ();
		scoreLine[1].moveTo((float) p1.x, (float) p1.y);
		scoreLine[1].quadTo((float) c1Pt.x, (float) c1Pt.y,
		                    (float) p2.x, (float) p2.y);

		return true;
	}

	/*
	 * Return the center point of the score box on the screen for the given score.
	 */
	private Point locationOfScoreBox (int score) {
		score = score % 100;

		// See if this is a corner score space
		if ((score % 25) == 0) {
			int cornerId = (score / 25);
			return new Point (cornerBaseX[cornerId], cornerBaseY[cornerId]);
		}

		// Return points for end spaces.
		if (score <= 25) {
			return new Point (score * 20 + 30, 20);
		} else if (score <= 50) {
			return new Point (540, (score-25) * 20 + 30);
		} else if (score <= 75) {
			return new Point (530 - (score-50) * 20, 540);
		} else {
			return new Point (20, 530 - (score-75) * 20);
		}
	}


	/**
	 * Draw the main board.
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent (g);

		// Set anti-aliasing on for nicer curves
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                     RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw the board
		kGraphics.paintImage (g, 0, 0, WarwickGraphics.BOARD, 0, 0);

		// TESTING ***
		// Fill the board with pieces just to see where they are.
		if (false) {
		for (int region=0; region < 4; region++) {
			for (int space=0; space < 15; space++) {
				int x = pieceCentersX[region][space];
				int y = pieceCentersY[region][space];
				kGraphics.paintImage (g, x, y,
					                      WarwickGraphics.SHADOW_TOKENS,
					                      3, 0);
			}
		}
		}
		// TESTING END ****

		// Draw the highlight locations for the last placed piece & last slid piece.
		drawHighlight (g, 0,
		               model.getLastPlacedRegion(), model.getLastPlacedSpace());
		drawHighlight (g, 1,
		               model.getLastSlideRegion(), model.getLastSlideSpace());

		// If there is a slide line active, then draw it underneath the pieces.
		if (slideLine != null) {
			drawCurrentSlide(g2d);
		}

		// Draw the pieces on the board
		for (int region=0; region < 4; region++) {
			for (int space=0; space < 15; space++) {
				int owner = model.getOwnerAt(region, space);
				if (owner >= 0) {
					int x = pieceCentersX[region][space];
					int y = pieceCentersY[region][space];

					kGraphics.paintImage (g, x, y,
					                      WarwickGraphics.TOKENS,
					                      owner, 0);
				}
			}
		}

		// If there is a score line active, the draw it underneath the score markers.
		if (scoreLine[0] != null) {
			drawScoreLine(g2d);
		}

		// Draw the scoring pieces
		for (int i=0; i<numPlayers; i++) {
			int thisScore = model.getScore(i) % 100;
			scoreCount[thisScore] += 1;
		}
		for (int i=0; i<numPlayers; i++) {
			int thisScore = model.getScore(i) % 100;
			drawScoreMarker(g, i, thisScore,
			                scoreIndex[thisScore], scoreCount[thisScore]);
			scoreIndex[thisScore] += 1;
		}
		for (int i=0; i<numPlayers; i++) {
			int thisScore = model.getScore(i) % 100;
			scoreCount[thisScore] = -1;
			scoreIndex[thisScore] = 0;
		}

		// Draw extra stuff depending on the current phase.
		if (model.isChooseAllegience()) {
			drawAllegienceCards(g);
		} else if (currentPotentialPieceRegion >= 0) {
			drawCurrentPotentialPiece(g);
		}
	}

	// Coordinates for drawing score markers in the corner spaces.
	private static final int [] cornerOffsetX = {-10, 10, 10, -10, 0};
	private static final int [] cornerOffsetY = {-10, -10, 10, 10, 0};
	private static final int [] cornerBaseX = {20, 540, 540, 20};
	private static final int [] cornerBaseY = {20, 20, 540, 540};

	// Coordinates for drawing score markers on the non-corner spaces.
	private static final int [][] scoreOffset = {
		{0, 10, 10, 12, 12},
		{0, -10, 0, 4, 6},
		{0, 0, -10, -4, 0},
		{0, 0, 0, -12, -6},
		{0, 0, 0, 0, -12}
	};

	/*
	 * Draw a score marker on the board.
	 *
	 * @param g           The graphics context to draw on.
	 * @param owner       The player whose color should be drawn.
	 * @param score       The score space to put the marker on.
	 * @param thisToken   The index of which score marker this one is on the
	 *                    given score space.
	 * @param ofTokens    The total score tokens which will be drawn on this
	 *                    score space.
	 */
	private void drawScoreMarker (Graphics g, int owner, int score,
	                              int thisToken, int ofTokens) {
		int x, y;

		// Compute the location to place the score marker
		if ((score % 25) == 0) {
			int cornerId = (score / 25);
			x = cornerBaseX[cornerId] + cornerOffsetX[thisToken];
			y = cornerBaseY[cornerId] + cornerOffsetY[thisToken];
		} else {
			if (score <= 25) {
				x = score * 20 + 30;
				y = 20 + scoreOffset[thisToken][ofTokens];
			} else if (score <= 50) {
				x = 540 + scoreOffset[thisToken][ofTokens];
				y = (score-25) * 20 + 30;
			} else if (score <= 75) {
				x = 530 - (score-50) * 20;
				y = 540 + scoreOffset[thisToken][ofTokens];
			} else {
				x = 20 + scoreOffset[thisToken][ofTokens];
				y = 530 - (score-75) * 20;
			}
		}

		// Paint the score marker
		kGraphics.paintImage (g, x, y, WarwickGraphics.SCORE_MARKERS, owner, 0);

		// Keep track of this location to be able to determine if the mouse has
		// moved over it or not.
		scoreMarkerLocation[owner] = new Point (x, y);
	}

	/*
	 * Draw the allegience cards
	 *
	 * @param g      The graphics context to draw on.
	 */
	private void drawAllegienceCards (Graphics g) {
		for (int i=0; i<allegienceCardRects.length; i++) {
			Rectangle r = allegienceCardRects[i];
			kGraphics.paintImage (g, r.x, r.y, WarwickGraphics.ROSE_CARDS,
			                     allegienceCardOwners[i],
			                     (i == currentAllegienceCardSelection) ? 1 : 0);
		}
	}

	/*
	 * Outline the potential segment and draw the potential piece.
	 *
	 * @param g      The graphics context to draw on.
	 */
	private void drawCurrentPotentialPiece (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (outlineColor == null) {
			outlineColor = new Color (255, 255, 0, 80);
		}

		// Draw the outline of the segment.
		g2d.setStroke(outlineStroke);
		g2d.setColor(outlineColor);
		g2d.draw(regionOutlines[currentPotentialPieceRegion]);

		// Paint the potential piece as a shadow
		int x = pieceCentersX[currentPotentialPieceRegion][currentPotentialPieceSpace];
		int y = pieceCentersY[currentPotentialPieceRegion][currentPotentialPieceSpace];
		kGraphics.paintImage (g, x, y,
		                      WarwickGraphics.SHADOW_TOKENS, mySeatNum, 0);
	}

	/*
	 * Draw the possible sliding action by drawing the slide target as a shadow
	 * and the line showing the move.
	 *
	 * @param g      The graphics context to draw on.
	 */
	private void drawCurrentSlide (Graphics2D g2d) {
		if (slideLineColor == null) {
			slideLineColor = new Color (255, 255, 0, 200);
		}

		// Draw the line showing the slide action
		g2d.setStroke(outlineStroke);
		g2d.setColor(slideLineColor);
		g2d.draw(slideLine);

		// Paint the potential piece as a shadow
		int x = pieceCentersX[currentSlideTargetRegion][currentSlideTargetSpace];
		int y = pieceCentersY[currentSlideTargetRegion][currentSlideTargetSpace];
		int owner = model.getOwnerAt(currentSlideSelectionRegion, currentSlideSelectionSpace);
		kGraphics.paintImage (g2d, x, y,
		                      WarwickGraphics.SHADOW_TOKENS,
		                      owner, 0);
	}

	/*
	 * Draw the line indicating the last score values for a player.
	 *
	 * @param g      The graphics context to draw on.
	 */
	private void drawScoreLine (Graphics2D g2d) {
		if (underScoreLineColor == null) {
			loadScoreLineColors();
		}
		

		// Draw the underline for the point jumps
		g2d.setStroke(underScoreLineStroke);
		g2d.setColor(underScoreLineColor);
		g2d.draw(scoreLine[0]);
		g2d.draw(scoreLine[1]);

		// Draw the colored point line jumps
		g2d.setStroke(scoreLineStroke);
		g2d.setColor(playerColor[mainScorePlayer]);
		g2d.draw(scoreLine[0]);
		g2d.setColor(playerColor[allyScorePlayer]);
		g2d.draw(scoreLine[1]);
	}

	/*
	 * Load the score line colors (which are the player colors)
	 */
	private void loadScoreLineColors () {
		underScoreLineColor = new Color (0, 0, 0, 200);

		GameProperties props = GameProperties.getInstance();
		for (int i = 0; i < numPlayers; i++) {
			Color rawColor = JogreUtils.getColour (props.get("player.colour." + i));
			playerColor[i] = new Color (rawColor.getRed(),
			                            rawColor.getGreen(),
			                            rawColor.getBlue(),
			                            160);
		}
	}

	/*
	 * Draw a highlight outline at the given region/space.
	 *
	 * @param g               The graphics context to draw on.
	 * @param highlightType   The type of highlight to draw.
	 * @param reigon, space   The location on the board to draw the highlight on
	 */
	private void drawHighlight (Graphics g, int highlightType, int region, int space) {
		if (region >= 0) {
			int x = pieceCentersX[region][space];
			int y = pieceCentersY[region][space];
			kGraphics.paintImage (g, x, y,
			                      WarwickGraphics.TOKEN_HIGHLIGHT,
			                      highlightType, 0);
		}
	}
}
