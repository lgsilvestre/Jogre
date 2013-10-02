/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
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
package org.jogre.octagons.client;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import org.jogre.client.awt.JogreComponent;

// Octagons visual board component (view of the model)
public class OctagonsBoardComponent extends JogreComponent {

	// Declare constants which define what the board looks like
	private static final int BASIC_SIZE = 20;

	// Main colors
	public static Color BORDER_COLOR = new Color (0, 0, 0);
	public static Color P1_COLOR = new Color (255, 0, 0);
	public static Color P2_COLOR = new Color (0, 0, 255);
	public static Color NO_COLOR = new Color (224, 224, 224);
	public static Color MOUSE_OUTLINE_COLOR = new Color (255, 255, 0);
	public static Color LAST_MOVE_OUTLINE_COLOR = new Color (0, 170, 0);

	// Link to the model
	protected OctagonsModel model;

	// Polygons for the board outline
	private Polygon topPoly, bottomPoly, leftPoly, rightPoly;

	// Location for the current mouse place
	private OctLoc currMouseLoc;

	/**
	 * Constructor which creates the board
	 *
	 * @param model					The game model
	 */
	public OctagonsBoardComponent (OctagonsModel model) {

		this.model = model;			// link to model

		int dim = (BASIC_SIZE * 3 * OctagonsModel.ROWS) + (BASIC_SIZE * 2);
		topPoly		= OctagonsGraphics.createTopBorderPoly		(BASIC_SIZE, OctagonsModel.ROWS);
		bottomPoly	= OctagonsGraphics.createBottomBorderPoly	(BASIC_SIZE, OctagonsModel.ROWS);
		leftPoly	= OctagonsGraphics.createLeftBorderPoly		(BASIC_SIZE, OctagonsModel.ROWS);
		rightPoly	= OctagonsGraphics.createRightBorderPoly	(BASIC_SIZE, OctagonsModel.ROWS);

		currMouseLoc = new OctLoc ();

		setPreferredSize(new Dimension (dim, dim));
		//setMinimumSize(new Dimension (dim, dim)); // Why doesn't this work?
	}

	/**
	 * Reset the board back to the initial state
	 */
	public void reset_game() {
		currMouseLoc = new OctLoc ();
	}

	/**
	 * Convert a player_id to the color to be used to draw a space owned by that player
	 *
	 * @param	player_id		The player ID
	 * @return					The color to use
	 */
	private Color player_id_to_color(int player_id) {
		if (player_id == OctagonsModel.PLAYER_ONE) {
			return P1_COLOR;
		} else if (player_id == OctagonsModel.PLAYER_TWO) {
			return P2_COLOR;
		} else {
			return NO_COLOR;
		}
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		int i,j;
		int curr_element;

		// Draw the board outlines first
		OctagonsGraphics.drawPolys(g, P1_COLOR, BORDER_COLOR, topPoly, bottomPoly);
		OctagonsGraphics.drawPolys(g, P2_COLOR, BORDER_COLOR, leftPoly, rightPoly);

		// Then draw the squares before the octagons
		for (i=0; i < (OctagonsModel.ROWS-1); i++) {
			for (j=0; j < (OctagonsModel.ROWS-1); j++) {
				OctagonsGraphics.drawSquare(
					g,													// graphics
					(BASIC_SIZE * 3 * (i+1)) + BASIC_SIZE,				// x
					(BASIC_SIZE * 3 * (j+1)) + BASIC_SIZE,				// y
					BASIC_SIZE,											// size
					player_id_to_color(model.get_owner(i, j, OctLoc.SQUARE))
				);
			}
		}

		// Draw the octagons next
		for (i=0; i < OctagonsModel.ROWS; i++) {
			for (j=0; j < OctagonsModel.ROWS; j++) {
				OctagonsGraphics.drawOctagon(
					g,													// graphics
					(BASIC_SIZE * 3 * i) + BASIC_SIZE,					// x
					(BASIC_SIZE * 3 * j) + BASIC_SIZE,					// y
					BASIC_SIZE,											// size
					(i+j) & 0x01,										// direction (up/down or left/right)
					player_id_to_color(model.get_owner(i, j, OctLoc.OCT_1)),	// color_1
					player_id_to_color(model.get_owner(i, j, OctLoc.OCT_2)),	// color_2
					BORDER_COLOR										// line_color
				);
			}
		}

		// Outline the last move, if there is one
		OctagonsGraphics.outlineLocation(g, BASIC_SIZE, model.get_last_move_loc(0), LAST_MOVE_OUTLINE_COLOR);
		OctagonsGraphics.outlineLocation(g, BASIC_SIZE, model.get_last_move_loc(1), LAST_MOVE_OUTLINE_COLOR);

		// Finally, draw the current cursor outline, if it is a valid play
		if (model.valid_play(currMouseLoc) == true) {
			OctagonsGraphics.outlineLocation(g, BASIC_SIZE, currMouseLoc, MOUSE_OUTLINE_COLOR);
		}
	}

	/**
	 * Convert a pixel location on the screen to a Octagon location.
	 *
	 * @param	(x,y)			Pixel location on the screen
	 * @return		The location that the point corresponds to
	 */
	public OctLoc convertPointToLocation(int x, int y) {
		int bi = (x - BASIC_SIZE) / (BASIC_SIZE * 3);		// i,j of the bounding square that contains (x,y)
		int bj = (y - BASIC_SIZE) / (BASIC_SIZE * 3);
		int ox = (x - BASIC_SIZE) % (BASIC_SIZE * 3);		// Offset into the bounding square that contains (x,y)
		int oy = (y - BASIC_SIZE) % (BASIC_SIZE * 3);
		int el;

		// Determine which area inside the bounding box we are in.

		if ((ox + oy) <= BASIC_SIZE) {
			// We're in the square that is in the upper left of the bounding box.
			bi = bi - 1;
			bj = bj - 1;
			el = OctLoc.SQUARE;
		} else if ((ox + oy) >= (BASIC_SIZE * 5)) {
			// We're in the square that is in the lower right of the bounding box.
			el = OctLoc.SQUARE;
		} else if ((ox - oy) >= (BASIC_SIZE * 2)) {
			// We're in the square that is in the upper right of the bounding box
			bj = bj - 1;
			el = OctLoc.SQUARE;
		} else if ((oy - ox) >= (BASIC_SIZE * 2)) {
			// We're in the square that is in the lower left of the bounding box
			bi = bi - 1;
			el = OctLoc.SQUARE;
		} else {
			// We're in the interior of the octagon
			if (((bi + bj) & 0x01) == 0) {
				// In a left/right octagon
				el = ((ox >= (BASIC_SIZE * 3) / 2) ? OctLoc.OCT_2 : OctLoc.OCT_1);
			} else {
				// In an up/down octagon
				el = ((oy >= (BASIC_SIZE * 3) / 2) ? OctLoc.OCT_2 : OctLoc.OCT_1);
			}
		}

		// Make sure the location is within the board
		if ((bi < 0) || (bi >= OctagonsModel.ROWS) ||
			(bj < 0) || (bj >= OctagonsModel.ROWS)) {
				return new OctLoc ();	// Not in a valid space
		}

		return new OctLoc(bi, bj, el);
	}

	/**
	 * Returns the current mouse location
	 *
	 * @return		The current mouse location
	 */
	public OctLoc getCurrMouseLocation() {
		return currMouseLoc;
	}

	/**
	 * Sets the current mouse location
	 *
	 * @param	newLoc			The new location to set the current location to
	 */
	public void setCurrMouseLocation(OctLoc newLoc) {
		currMouseLoc = newLoc;
	}
}
