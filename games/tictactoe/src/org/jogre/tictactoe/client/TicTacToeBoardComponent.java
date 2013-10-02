/*
 * JOGRE (Java Online Gaming Real-time Engine) - TicTacToe
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
package org.jogre.tictactoe.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;

// TicTacToe visual board component (view of the model)
public class TicTacToeBoardComponent extends AbstractBoardComponent {

	// Declare constants which define what the board looks like
	private static final int NUM_OF_ROWS_COLS = 3;
	private static final int CELL_SIZE = 65;
	private static final int CELL_SPACING = 1;
	
	// Link to the model
	protected TicTacToeModel model;
	
	// Constructor which creates the board
	public TicTacToeBoardComponent (TicTacToeModel model) {
		// Call constructor in AbstractBoardComponent 
		super (NUM_OF_ROWS_COLS, NUM_OF_ROWS_COLS, CELL_SIZE, CELL_SPACING, 
			   0, 0, false, false, false);

		this.model = model;			// link to model
		
		// set colours 
		setColours (Color.white, Color.white, Color.black, Color.white);
	}	
	
	// Update the graphics depending on the model
	public void paintComponent (Graphics g) {
		// Draw the board (AbstractBoardComponent)
		super.paintComponent (g);
		
		// draw each TicTacToe piece on the board (loop through the model)		
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				// get piece from model
				int piece = model.getData(x, y);    
				
				// Now get screen co-ordinates (AbstractBoardComponent)
				Point screen = getScreenCoords (x, y);
				
				// If piece isn't an blank piece then draw the image on the screen
				if (piece != TicTacToeModel.BLANK) {
					Image image = GameImages.getImage(piece+1); 	    // image
					g.drawImage (image, screen.x, screen.y, null);	// draw
				}
			}
		}
	}
}
