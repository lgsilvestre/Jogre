/*
 * JOGRE (Java Online Gaming Real-time Engine) - Hex
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.hex.client;

import org.jogre.client.awt.AbstractHexBoards.AbstractRhombusHexBoardComponent;

import org.jogre.common.util.HexBoardUtils;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Hex visual board component (view of the model)
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class HexBoardComponent extends AbstractRhombusHexBoardComponent {

	// Link to the model
	private HexModel model;

	// The controlLineLength for each hexagon on the board.
	private final static int HEXAGON_SIZE = 16;

	// The colors for each player & the neutral color
	private Color [] cellColor = new Color[3];

	// Full & half of the size of the component.
	// (Saved so that we can draw the player colors in the corners)
	private int compWidth, compHeight;
	private int halfWidth, halfHeight;

	/**
	 * Constructor which creates the boardcomponent
	 *
	 * @param model					The game model
	 */
	public HexBoardComponent (HexModel model, int boardSize) {

		// Create the Rhombus board of the given size
		super (
			boardSize,
			HexBoardUtils.makeRegularBoundingDim(HEXAGON_SIZE),
			HEXAGON_SIZE
		);

		// Save parameters provided
		this.model = model;

		// Get the colors from the game.properties file
		GameProperties props = GameProperties.getInstance();
		cellColor[0] = JogreUtils.getColour (props.get("player.colour.0"));
		cellColor[1] = JogreUtils.getColour (props.get("player.colour.1"));
		cellColor[2] = JogreUtils.getColour (props.get("background.colour"));

		// Add extra space around the board.
		setInset (new Dimension (3, 3) );
		setOutset (new Dimension (4, 4) );

		// Recompute the preferred dimension now that we've changed the geometry.
		Dimension boardDim = getBoardComponentDim ();
		setPreferredSize ( boardDim );

		compWidth = boardDim.width;
		compHeight = boardDim.height;
		halfWidth = boardDim.width / 2;
		halfHeight = boardDim.height / 2;
	}

	/**
	 * Overriding paintComponent() so that we can add a bit of customization.
	 */
	public void paintComponent (Graphics g) {
		// First, paint the whole background with player 0's color
		g.setColor(cellColor[0]);
		g.fillRect(0, 0, compWidth, compHeight);

		// Then, draw the correct corners with player 1's color
		g.setColor(cellColor[1]);
		if (userFlip) {
			g.fillRect(0, 0, halfWidth, halfHeight);
			g.fillRect(halfWidth, halfHeight, halfWidth+1, halfHeight+1);
		} else {
			g.fillRect(0, halfHeight, halfWidth, halfHeight+1);
			g.fillRect(halfWidth, 0, halfWidth+1, halfHeight);
		}

		// Then, paint the main board.
		super.paintComponent(g);
	}

	/**
	 * Overriding getHexColor() method so that we can draw the hex's in the
	 * appropriate color given the state of the model.
	 */
	public Color getHexColor(int col, int row) {
		return cellColor[model.getOwner(col, row)];
	}
}
