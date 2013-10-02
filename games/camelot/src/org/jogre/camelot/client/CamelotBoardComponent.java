/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.camelot.client;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Image;

import java.util.Vector;
import java.util.ListIterator;

import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.GameImages;

// Camelot visual board component (view of the model)
public class CamelotBoardComponent extends JogreComponent {

	// Declare constants which define what the board looks like

	// Main colors
	public static Color BORDER_COLOR = new Color (0, 0, 0);
	public static Color OUTLINE_COLOR = new Color (255, 255, 0);
	public static Color END_HIGHLIGHT_COLOR = new Color (0, 128, 0);
	public static Color MOVE_COLOR = new Color (80, 80, 80);

	// Link to the model
	protected CamelotModel model;

	// Polygon for the board outline
	private Polygon boardOutlinePoly;
	BasicStroke thickStroke = new BasicStroke(3);
	BasicStroke thinStroke = new BasicStroke(1);

	// Images of the pieces
	private Image piecesImage;
	// basicSize is the # of pixels in size a board space it.
	// (It is calculated by the size of the piecesImage)
	private int basicSize;

	// Flag to determine if the board should be reversed or not.
	private boolean reverseFlag = false;

	// Locations of things.
	private CamelotLoc outlineLoc;
	private CamelotLoc currSelectionLoc;

	// The active move tree & move
	private Vector activeMoveTree;
	private Vector activeMove;

	// Arrays used to set the location of the move indicators.
	private Image [] showStep_image = new Image [8];
	private int [][] showStep_xoff = new int [8][3];
	private int [][] showStep_yoff = new int [8][3];

	/**
	 * Constructor which creates the board
	 *
	 * @param model					The game model
	 */
	public CamelotBoardComponent (CamelotModel model) {

		// link to the model
		this.model = model;

		// Load the image of the pieces and use the image to calculate the
		// basic size of the pieces
		piecesImage = GameImages.getImage(1);
		basicSize = (piecesImage.getHeight(null) / 2);

		// Create the board outline polygon
		boardOutlinePoly = createOutlinePoly();

		// Initialize the tables for the move indicators
		initMoveIndicatorData();

		int x_dim = (basicSize * CamelotModel.COLS) + 3;
		int y_dim = (basicSize * CamelotModel.ROWS) + 3;
		setPreferredSize(new Dimension (x_dim, y_dim));

		resetGame();
	}

	/**
	 * Reset the view back to the initial state
	 */
	public void resetGame() {
		outlineLoc = null;
		currSelectionLoc = null;
		activeMoveTree = null;
		activeMove = null;
	}

	/**
	 * Create the board outline polygon
	 *
	 * @return the outline polygon
	 */
	private Polygon createOutlinePoly() {
		int [] x = {0, basicSize, basicSize, 2*basicSize, 2*basicSize,
					5*basicSize, 5*basicSize, 7*basicSize, 7*basicSize,
					10*basicSize, 10*basicSize, 11*basicSize, 11*basicSize,
					12*basicSize, 12*basicSize, 11*basicSize, 11*basicSize,
					10*basicSize, 10*basicSize, 7*basicSize, 7*basicSize,
					5*basicSize, 5*basicSize, 2*basicSize, 2*basicSize,
					basicSize, basicSize, 0};

		int [] y = {3*basicSize, 3*basicSize, 2*basicSize, 2*basicSize,
					basicSize, basicSize, 0, 0, basicSize, basicSize,
					2*basicSize, 2*basicSize, 3*basicSize, 3*basicSize,
					13*basicSize, 13*basicSize, 14*basicSize, 14*basicSize,
					15*basicSize, 15*basicSize, 16*basicSize, 16*basicSize,
					15*basicSize, 15*basicSize, 14*basicSize, 14*basicSize,
					13*basicSize, 13*basicSize};
		Polygon poly = new Polygon (x, y, 28);
		poly.translate(1, 1);
		return (poly);
	}

	/**
	 * Update the screen depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		int i,j;
		int piece_type;
		int sx, sy;
		Point dstPoint;
		Graphics2D g2d = (Graphics2D) g;

		// Draw the spaces on the board
		for (i=0; i < CamelotModel.COLS; i++) {
			for (j=0; j < CamelotModel.ROWS; j++) {

				dstPoint = toScreenPoint(i, j, 2);
				piece_type = model.getPieceAt(i, j);

				if (model.isPartOfBoard(piece_type)) {
					// Draw the piece on the space
					sy = ((i+j) & 1) * basicSize;

					if (model.isPiece(piece_type)) {
						sx = basicSize * (1 +
							(model.isPlayerOne(piece_type) ? 0 : 1) +
							(model.isKnight(piece_type) ? 2 : 0));
					} else {
						if (j == 0) {
							// Draw player 2 flag
							sx = basicSize * 6;
						} else if (j == (CamelotModel.ROWS-1)) {
							// Draw player 1 flag
							sx = basicSize * 5;
						} else {
							// Draw empty space
							sx = 0;
						}
					}

					g.drawImage (piecesImage,
						dstPoint.x, dstPoint.y,
						dstPoint.x+basicSize, dstPoint.y+basicSize,
						sx, sy, sx+basicSize, sy+basicSize,
						null);

				}
			}
		}

		// Next draw the board outline
		g2d.setStroke(thickStroke);
		g.setColor(BORDER_COLOR);
		g.drawPolygon(boardOutlinePoly);
		g2d.setStroke(thinStroke);

		// If there is an active move tree, then outline all of the ending
		// locations in the tree
		if (activeMoveTree != null) {
			g2d.setStroke(thickStroke);
			g.setColor(END_HIGHLIGHT_COLOR);

			ListIterator iter = activeMoveTree.listIterator();
			CamelotLoc loc;
			while (iter.hasNext()) {
				loc = ((CamelotMoveFamily) iter.next()).getEnd();
				dstPoint = toScreenPoint(loc.get_i(), loc.get_j(), 1);
				g.drawRect( dstPoint.x, dstPoint.y,
							basicSize, basicSize);
			}
			g2d.setStroke(thinStroke);
		}

		// If there is a space to be outlined, then do so
		if (outlineLoc != null) {
			g.setColor(OUTLINE_COLOR);
			g2d.setStroke(thickStroke);
			dstPoint = toScreenPoint(outlineLoc.get_i(), outlineLoc.get_j(), 1);
			g.drawRect(dstPoint.x, dstPoint.y,
					   basicSize, basicSize);
			g2d.setStroke(thinStroke);
		}

		// If there is an active move, then draw all of the connections
		if (activeMove != null) {
			g.setColor(MOVE_COLOR);
			g2d.setStroke(thickStroke);

			ListIterator iter = activeMove.listIterator();
			CamelotStep step;
			CamelotLoc l1, l2;
			CamelotLoc fromLoc;
			int step_dir;
			while (iter.hasNext()) {
				step = ((CamelotStep) iter.next());
				fromLoc = step.get_from();
				step_dir = step.get_direction();
				/* Old code that drew lines
				g.drawLine(
					l1.get_i() * basicSize + (basicSize/2),
					l1.get_j() * basicSize + (basicSize/2),
					l2.get_i() * basicSize + (basicSize/2),
					l2.get_j() * basicSize + (basicSize/2)
				);
				*/
				if (step.get_type() != CamelotStep.WALK) {
					// If not a walk, then draw the second half of the
					// step highlight
					showStep(
						g,
						fromLoc.get_i() + CamelotModel.i_off[step_dir],
						fromLoc.get_j() + CamelotModel.j_off[step_dir],
						step_dir);
				}
				// Show the step highlight
				showStep(g, fromLoc.get_i(), fromLoc.get_j(), step_dir);
			}

			g2d.setStroke(thinStroke);
		}
	}

	/**
	 * Initialize the data for the move markers.
	 */
	private void initMoveIndicatorData() {
		int dir, step;

		for (dir = CamelotModel.NORTH; dir <= CamelotModel.NW; dir++) {
			// Load the marker image
			showStep_image[dir] = GameImages.getImage(dir+2);

			// Set the location of the markers
			for (step=0; step<3; step++) {
				showStep_xoff[dir][step] =
						(int) (((basicSize / 3) * step + (basicSize / 6)) * CamelotModel.i_off[dir]) +
						(basicSize / 2) -
						(showStep_image[dir].getWidth(null) / 2);
				showStep_yoff[dir][step] =
						(int) (((basicSize / 3) * step + (basicSize / 6)) * CamelotModel.j_off[dir]) +
						(basicSize / 2) -
						(showStep_image[dir].getHeight(null) / 2);
			}
		}
	}

	/**
	 * Display the move markers for a single step.
	 *
	 * @param	g				The graphics context to draw on
	 * @param	(src_i, src_j)	The (i,j) coordinates of the location we're stepping from
	 * @param	direction		The direction of the step.
	 */
	private void showStep(Graphics g, int src_i, int src_j, int direction) {
		int c;
		Point dstPoint;

		// If we're in reverse mode, then directions are also rotated 180 degrees...
		if (reverseFlag) {
			direction = (direction + 4) % 8;
		}

		for (c=0; c<3; c++) {
			dstPoint = toScreenPoint(src_i, src_j, 2);
			g.drawImage(
				showStep_image[direction],
				dstPoint.x+showStep_xoff[direction][c],
				dstPoint.y+showStep_yoff[direction][c],
				dstPoint.x+showStep_xoff[direction][c] + showStep_image[direction].getWidth(null),
				dstPoint.y+showStep_yoff[direction][c] + showStep_image[direction].getHeight(null),
				0, 0,
				showStep_image[direction].getWidth(null),
				showStep_image[direction].getHeight(null),
				null);
		}
	}

	/**
	 * Convert a pixel location on the screen to a Camelot location.
	 *
	 * @param	(x,y)			Pixel location on the screen
	 * @return		The location that the point corresponds to
	 */
	public CamelotLoc convertPointToLocation(int x, int y) {
		if (reverseFlag) {
			return new CamelotLoc(
					(CamelotModel.COLS - 1) - ((x-2) / basicSize),
					(CamelotModel.ROWS - 1) - ((y-2) / basicSize)
			);
		} else {
			return new CamelotLoc( ((x-2) / basicSize), ((y-2) / basicSize));
		}
	}

	/**
	 * Convert a logical board location (i,j) to a screen point (x,y)
	 *
	 * @param	(i,j)			The logical coordinates
	 * @param	offset			Additional screen offset to add
	 * @return	The (x,y) Point of upper left corner of the location
	 */
	private Point toScreenPoint(int i, int j, int offset) {
		if (reverseFlag) {
			return new Point (
					((CamelotModel.COLS - 1 - i) * basicSize) + offset,
					((CamelotModel.ROWS - 1 - j) * basicSize) + offset
			);
		} else {
			return new Point (
					(i * basicSize) + offset,
					(j * basicSize) + offset
			);
		}
	}

	/**
	 * Set the space to be outlined
	 *
	 * @param	newLoc			The new location to be outlined.
	 */
	public void setOutlineLocation(CamelotLoc newLoc) {
		outlineLoc = newLoc;
	}

	/**
	 * Set the active move to the one given
	 *
	 * @param moveList			The new move to be made the active one
	 */
	public void setActiveMove(Vector move) {
		activeMove = move;
	}


	/**
	 * Get the current active move
	 *
	 * @return	The current active move
	 */
	public Vector getActiveMove() {
		return (activeMove);
	}

	/**
	 * Set the active move tree to the one given
	 *
	 * @param moveTree			The new moveTree to be made the active one
	 */
	public void setActiveMoveTree(Vector moveTree) {
		activeMoveTree = moveTree;
	}

	/**
	 * Get the current active move tree
	 *
	 * @return	The current active move tree
	 */
	public Vector getActiveMoveTree() {
		return (activeMoveTree);
	}


	/**
	 * Set the reversed flag so that the board is drawn "upside down"
	 *
	 * @param	reverse			Boolean if the board should be reversed or not
	 */
	public void setReversed(boolean reverse) {
		reverseFlag = reverse;
	}

}
