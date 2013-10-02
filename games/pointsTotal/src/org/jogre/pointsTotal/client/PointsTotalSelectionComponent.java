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

import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.client.awt.JogreComponent;

/**
 * Component that shows which players can play which pieces for the
 * Points Total game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalSelectionComponent extends JogreComponent {

    // Link to the model & graphics helper
    protected PointsTotalModel model;
    protected PointsTotalGraphics ptGraphics;

    // Offset into the component where the display starts
    private int board_offx, board_offy;

    // Offsets from the upper-left corner of a score box to where the digits
    // will be drawn.
    private int scoreOffset_x, scoreOffset_y;

    // The size of the small icons
    private int iconWidth, iconHeight;

    // Vertical spacing between small icons
    private int vSpacing;

    // Horizontal spacing between the player's column and the other columns
    private int hSpacingMain;

    // Horizontal spacing between the other player's columns
    private int hSpacingOthers;

    // The number of players in the game.
    private int numPlayers;

    // The player that we are showing.
    private int myPlayer = 0;

    // The currently selected value
    private int currSelectedValue = -1;

    // The currently moused-over value
    private int currMousedValue = -1;

    // The spacing from the start of one digit to the next in the score.
    private int scoreDigitSpacing;

    // Constructor which creates the board
    public PointsTotalSelectionComponent (PointsTotalModel model) {
        super ();

        // Link to model & graphics helper
        this.model = model;
        this.ptGraphics = PointsTotalGraphics.getInstance();

        iconWidth  = ptGraphics.imageWidths [PointsTotalGraphics.EMPTY_SMALL_TILE];
        iconHeight = ptGraphics.imageHeights[PointsTotalGraphics.EMPTY_SMALL_TILE];
        scoreDigitSpacing = ptGraphics.imageWidths[PointsTotalGraphics.SCORE_DIGITS];

        // Make room for the highlight symbol around the edge of the board
        board_offx = (ptGraphics.imageWidths [PointsTotalGraphics.SMALL_HIGHLIGHT] -
                      iconWidth) / 2;

        board_offy = (ptGraphics.imageHeights[PointsTotalGraphics.SMALL_HIGHLIGHT] -
                      iconHeight) / 2;

		// Setup various values;
        numPlayers = model.getNumPlayers();
		vSpacing = board_offy;
		hSpacingMain = board_offx * 2;
		hSpacingOthers = board_offx;

		scoreOffset_x = (ptGraphics.imageWidths[PointsTotalGraphics.SCORE_BOX] -
		                 3*scoreDigitSpacing) / 2;
		scoreOffset_y = (ptGraphics.imageHeights[PointsTotalGraphics.SCORE_BOX] -
		                 ptGraphics.imageHeights[PointsTotalGraphics.SCORE_DIGITS]) / 2;

		// Set preferred dimension
		Dimension boardDim = new Dimension (
			(
				(2 * board_offx) + hSpacingMain +
				(hSpacingOthers * (numPlayers - 2)) +
				(numPlayers * iconWidth)
			),
			(
				(3 * board_offy) + (4 * (iconHeight + vSpacing)) +
				ptGraphics.imageHeights[PointsTotalGraphics.SCORE_BOX]
			)
		);
		setPreferredSize ( boardDim );
    }

    // Update the graphics depending on the model
    public void paintComponent (Graphics g) {
        super.paintComponent (g);

        // Paint the array of pieces
        int p = myPlayer;
        int x = board_offx;
        do {
	        // Paint the column for player p
	        for (int v = 0; v < 4; v++) {
		        int y = board_offy + v * (iconHeight + vSpacing);
				if (model.isAvailToPlay(p, v)) {
					ptGraphics.paintImage(g,
					                      x, y,
					                      PointsTotalGraphics.SMALL_ARROWS,
					                      p, v);
				} else {
					ptGraphics.paintImage(g,
					                      x, y,
					                      PointsTotalGraphics.EMPTY_SMALL_TILE,
					                      0, 0);
				}
			}

			// Paint player p's score
			paintScore(g, x, p);

			// Advance to the next column
			x += iconWidth;
			x += (p == myPlayer) ? hSpacingMain : hSpacingOthers;
			p = (p + 1) % numPlayers;
        } while (p != myPlayer);

        // If there is a selected piece, then draw the selected highlight
        if (currSelectedValue != -1) {
	        ptGraphics.paintImage(g,
	                              0, currSelectedValue * (iconHeight + vSpacing),
	                              PointsTotalGraphics.SMALL_HIGHLIGHT,
	                              0, 0);
		}

		// If there is a "moused over" piece, then draw the moused over highlight
		if (currMousedValue != -1) {
	        ptGraphics.paintImage(g,
	                              0, currMousedValue * (iconHeight + vSpacing),
	                              PointsTotalGraphics.SMALL_HIGHLIGHT,
	                              1, 0);
		}

		// Paint the first-player icons
        p = myPlayer;
        x = 0;
        int firstPlayer = model.getFirstPlayer();
        do {
	        if (p >= firstPlayer) {
		        int whichIcon = (p == firstPlayer) ? 1 : 0;
		        ptGraphics.paintImage(g,
		                              x, 0,
		                              PointsTotalGraphics.FIRST_PLAYER_ICON,
		                              whichIcon, 0);
			}
			// Advance to the next column
			x += iconWidth;
			x += (p == myPlayer) ? hSpacingMain : hSpacingOthers;
			p = (p + 1) % numPlayers;
        } while (p != myPlayer);
    }

    /*
     * Paint the score for a player.
     *
     * @param g           The graphics context to draw on.
     * @param x           The x-coordinate to draw at.
     * @param playerSeat  The player whose score is to be drawn.
     */
    private void paintScore(Graphics g, int x, int playerSeat) {
	    int y = board_offy + 4 * (iconHeight + vSpacing) + board_offy;

	    // Paint the score box
	    ptGraphics.paintImage(g, x, y, PointsTotalGraphics.SCORE_BOX, 0, playerSeat);

	    // Move to where the score will start
	    x += scoreOffset_x;
	    y += scoreOffset_y;

	    // Paint the score
	    int score = model.getScore(playerSeat);
	    if (score >= 100) {
		    ptGraphics.paintImage(g, x, y, PointsTotalGraphics.SCORE_DIGITS,
		                          (score / 100), 0);
		}
		x += scoreDigitSpacing;

		if (score >= 10) {
			ptGraphics.paintImage(g, x, y, PointsTotalGraphics.SCORE_DIGITS,
			                      ((score % 100) / 10), 0);
		}
		x += scoreDigitSpacing;

		ptGraphics.paintImage(g, x, y,  PointsTotalGraphics.SCORE_DIGITS,
		                      (score % 10), 0);
    }

    /**
     * Set the seat number of the first column to be displayed
     *
     * @param   seatNum    The seat number to show first.
     */
    public void setMySeatNumber(int seatNum) {
	    if ((seatNum < 0) || (seatNum >= 4)) {
		    myPlayer = 0;
	    } else {
		    myPlayer = seatNum;
	    }

	    repaint();
    }

    /**
     * Set the "mouse over" space to the given one.
     *
     * @param newSpace   The new space to set the "mouse over" to.
     * @return true  => A new space was set.
     *         false => The new space was the same as the old space.
     */
    public boolean setMouseSpace(int newSpace) {
	    if (newSpace != currMousedValue) {
		    currMousedValue = newSpace;
		    return true;
	    }
	    return false;
    }

    /**
     * Set the selected space to the given one.
     *
     * @param newSpace   The new space to set the selected space to.
     * @return true  => A new space was set.
     *         false => The new space was the same as the old space.
     */
    public boolean setSelectedSpace(int newSpace) {
	    if (newSpace != currSelectedValue) {
		    currSelectedValue = newSpace;
		    return true;
	    }
	    return false;
    }

    /**
     * Unselect the spaces.
     */
    public void unselectSpaces() {
	    currSelectedValue = -1;
	    currMousedValue = -1;
    }

    /*
     * Decode an (x,y) graphical space to which value'd icon it is pointing to.
     *
     * @param   (x,y)   The graphical point to select
     * @return -1 => Not in a space.
     *         other => Value of space the point is in.
     */
    public int decodeSpace(int x, int y) {
	    x -= board_offx;
	    y -= board_offy;

	    // If we're not in the first column, then nothing to select.
	    if ((x < 0) ||
	        (x > ptGraphics.imageWidths [PointsTotalGraphics.EMPTY_SMALL_TILE])) {
	        return -1;
        }

        // Compute which vertical space we're in & offst into that space.
		int value = (y / (iconHeight + vSpacing));
		int offset = (y % (iconHeight + vSpacing));

		// If we're offset more than an icon, then we're in the space between
		// icons, and therefore nothing to select.
		if (offset > iconHeight) {
			return -1;
		}

		// If we're farther down than 3, then nothing there
		if (value > 3) {
			return -1;
		}

		// We've found our space
		return value;
    }

}
