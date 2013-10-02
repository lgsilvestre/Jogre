/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.pointsTotal.common.PointsTotalPiece;
import org.jogre.client.awt.JogreComponent;

/**
 * Main visual view for a game of pointsTotal which should show a
 * graphical representation of the PointsTotalModel.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalComponent extends JogreComponent {

    // Link to the model & graphics helper
    protected PointsTotalModel model;
    protected PointsTotalGraphics ptGraphics;

    // Offset into the component where the board starts
    private int board_offx, board_offy;

    // The size of the board icons
    private int iconWidth, iconHeight;

    // The currently selected space on the board, in logical coordinates
    // (x=column, y=row)
    private Point selectedSpace;

    // The current type of piece being placed on the board
    private PointsTotalPiece placingPiece;

    // A point that is guaranteed to be off the board.
    public Point OFF_BOARD_POINT = new Point (-1, -1);

    // Constructor which creates the board
    public PointsTotalComponent (PointsTotalModel model) {
        super ();

        // Link to model & graphics helper
        this.model = model;
        this.ptGraphics = PointsTotalGraphics.getInstance();

        // Get size of tiles that we use to draw the board.
        iconWidth  = ptGraphics.imageWidths [PointsTotalGraphics.EMPTY_TILE];
        iconHeight = ptGraphics.imageHeights[PointsTotalGraphics.EMPTY_TILE];

        // Make room for the highlight symbol around the edge of the board
        board_offx = (ptGraphics.imageWidths [PointsTotalGraphics.BIG_HIGHLIGHT] -
                      iconWidth) / 2;

        board_offy = (ptGraphics.imageHeights[PointsTotalGraphics.BIG_HIGHLIGHT] -
                      iconHeight) / 2;

		// Initialize various things.
		selectedSpace = OFF_BOARD_POINT;
		placingPiece = new PointsTotalPiece(-1, 0, 0);

		// Set preferred dimension
		Dimension boardDim = new Dimension (
			(
				(2 * board_offx) +
				(4 * iconWidth)
			),
			(
				(2 * board_offy) +
				(4 * iconHeight)
			)
		);
		setPreferredSize ( boardDim );
    }

    // Update the graphics depending on the model
    public void paintComponent (Graphics g) {
        super.paintComponent (g);

        // Paint the board
        for (int col = 0; col < 4; col++) {
	        for (int row = 0; row < 4; row++) {
		        int x = board_offx + (col * iconWidth);
		        int y = board_offy + (row * iconHeight);
		        paintPiece(g, x, y, model.getPiece(col, row));
	        }
        }

        // If one of the spaces is moused over, then draw that piece and
        // highlight it
        if (!OFF_BOARD_POINT.equals(selectedSpace)) {
	        // Draw the piece.
	        int x = board_offx + (selectedSpace.x * iconWidth);
	        int y = board_offy + (selectedSpace.y * iconHeight);
	        paintPiece(g, x, y, placingPiece);

	        // Draw the highlight on the piece
	        ptGraphics.paintImage(g,
	                              x - board_offx, y - board_offy,
	                              PointsTotalGraphics.BIG_HIGHLIGHT,
	                              1, 0);
		}

		// If there is a last-move space, then draw the highlight around it.
		Point lastMoveSpace = model.getLastMoveSpace();
		if (lastMoveSpace != null) {
	        ptGraphics.paintImage(g,
	                              lastMoveSpace.x * iconWidth,
	                              lastMoveSpace.y * iconHeight,
	                              PointsTotalGraphics.BIG_HIGHLIGHT,
	                              0, 0);
		}
    }

    /**
     * Paint a piece on the board.
     *
     * @param g          The graphics context to draw on.
     * @param (x,y)      The place to draw the piece at.
     * @parma thePiece   The piece to draw.
     */
    private void paintPiece(Graphics g,
                            int x, int y,
                            PointsTotalPiece thePiece) {
	    // First, draw the background
		if (thePiece.owner < 0) {
			ptGraphics.paintImage(g, x, y, PointsTotalGraphics.EMPTY_TILE, 0, 0);
		} else {
			// Paint the base image for the player
			ptGraphics.paintImage(g, x, y, PointsTotalGraphics.ARROW_BASE,
			                      thePiece.owner, 0);

			// Cover the arrows depending on rotation & type
			for (int r=0; r<4; r++) {
				if (!thePiece.isPointing(r)) {
					ptGraphics.paintImage(g, x, y, PointsTotalGraphics.ARROW_MASK,
					                      r, 0);
				}
			}
		}

		// Now, draw the value number on top
		ptGraphics.paintImage(g, x, y, PointsTotalGraphics.NUMBERS,
		                      thePiece.value, 0);

    }

    /**
     * Set the selected space to the given one.
     *
     * @param newSpace   The new space to set the selected space to.
     * @return true  => A new space was set.
     *         false => The new space was the same as the old space.
     */
    public boolean setSelectedSpace(Point newSpace) {
	    if (selectedSpace.equals(newSpace)) {
		    return false;
	    }

	    selectedSpace = newSpace;
	    return true;
    }

    /**
     * Return the current piece being placed on the board.
     */
    public PointsTotalPiece getCurrentPiece() {
	    return placingPiece;
    }

    /**
     * Set the value of the piece being placed on the board.
     *
     * @param newValue   The new value to set the piece to.
     */
    public void setCurrentPieceValue(int newValue) {
	    placingPiece.setValue(newValue);
    }

    /**
     * Set the rotation of the piece being placed on the board.
     *
     * @param newRot     The new rotation to set the piece to.
     */
    public void setCurrentPieceRotation(int newRot) {
	    placingPiece.setRotation(newRot);
    }

    /**
     * Rotate the current piece by the given number of 90-degree rotations.
     *
     * @param rotAmoutn   The number of 90-degree rotations to rotate the
     *                    current piece by.
     */
    public void rotateCurrentPiece(int rotAmount) {
	    placingPiece.setRotation(placingPiece.rotation + rotAmount);
    }

    /**
     * Set the seat number of the player sitting at this seat.
     *
     * @param mySeat   The seat number of this player.
     */
    public void setMySeatNumber(int mySeat) {
	    placingPiece.setOwner(mySeat);
    }

    /*
     * Decode an (x,y) graphical space to the point on the board that it is
     * pointing to.
     *
     * @param   (x,y)   The graphical point to select
     * @return a point that indicates the space on the board.
     *         OFF_BOARD_POINT is returned if the given point is not on the
     *         board.
     */
    public Point decodeSpace(int x, int y) {
	    x -= board_offx;
	    y -= board_offy;

	    // If we're outside of the board, then nothing to select.
	    if ((x < 0) || (y < 0)) {
		    return OFF_BOARD_POINT;
	    }

        // Compute the space we're in
        int xCoord = (x / iconWidth);
        int yCoord = (y / iconHeight);

        // If we're outside of the board, then nothing to select.
		if ((xCoord >= 4) || (yCoord >= 4)) {
			return OFF_BOARD_POINT;
		}

		// We've found our space
		return new Point (xCoord, yCoord);
    }

}
