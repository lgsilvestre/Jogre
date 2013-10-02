/*
 * JOGRE (Java Online Gaming Real-time Engine) - Connect 4
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.connect4.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;
import org.jogre.common.util.GameProperties;

// Connect4 visual board component (view of the model)
public class Connect4BoardComponent extends AbstractBoardComponent {

	// Declare constants which define what the board looks like
	private static final int NUM_OF_COLS = 7;
	private static final int NUM_OF_ROWS = 6;
	private static final int CELL_SIZE = 50;
	private static final int CELL_SPACING = 0;
	private static final int BORDER_WIDTH = 0;
	
	private static final int INDENT = 5;

	// Declare colours
	private static final Color COLOR_RED    = GameProperties.getPlayerColour(0);
	private static final Color COLOR_YELLOW = GameProperties.getPlayerColour(1);
	
	// Link to the model
	protected Connect4Model model;
	
	protected int curMousePoint = -1;
	protected int seatNum = 0;

	// Constructor which creates the board
	public Connect4BoardComponent (Connect4Model model) {
		// Call constructor in AbstractBoardComponent
		super (NUM_OF_ROWS, NUM_OF_COLS, CELL_SIZE, CELL_SPACING,
			   BORDER_WIDTH, 0, false, false, false);

		this.model = model;			// link to model

		// set colours
		Color bgColor = new Color (34, 34, 102);
		setColours (bgColor, bgColor, Color.black, Color.white);
	}

	// Update the graphics depending on the model
	public void paintComponent (Graphics g) {
		// Draw the board (AbstractBoardComponent)
		super.paintComponent (g);
		
		// draw each TicTacToe piece on the board (loop through the model)		
		for (int x = 0; x < Connect4Model.COLS; x++) {
			for (int y = 0; y < Connect4Model.ROWS; y++) {
				// get piece from model
				int piece = model.getData(x, y);    
				
				// Now get screen co-ordinates (AbstractBoardComponent)
				Point screen = getScreenCoords (x, y);
				
				Image image = GameImages.getImage(piece + 2); 	    // image
				g.drawImage (image, screen.x, screen.y, null);	// draw
			}
		}
		
		// Draw the box around the board
		if (curMousePoint != -1) {
		    g.setColor (seatNum == 0 ? COLOR_RED : COLOR_YELLOW);
			Point screen = getScreenCoords (curMousePoint, 0);
			g.drawRect (screen.x, 0, CELL_SIZE - 1, (CELL_SIZE * NUM_OF_ROWS) - 1);
		}
	}
	
	/**
	 * Set the current player.
	 * 
	 * @param seatNum
	 */
	public void setSeatNum (int seatNum) {
	    this.seatNum = seatNum;
	}
	
	/**
	 * Return the current mouse point.
	 * 
	 * @return
	 */
	public int getCurMousePoint() {
		return curMousePoint;
	}

	/**
	 * Set the current mouse point to another point.
	 * 
	 * @param newPoint   New point.
	 */
	public void setCurMousePoint (int newPoint) {
		curMousePoint = newPoint;
	}	
}
