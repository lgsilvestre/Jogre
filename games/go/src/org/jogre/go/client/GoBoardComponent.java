/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.go.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;

/**
 * Go visual board component (view of the model).
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoBoardComponent extends AbstractBoardComponent {

	// Declare constants which define what the board looks like
	private static final int CELL_SIZE = 17;
	private static final int CELL_SPACING = 0;
	private static final int BORDER_WIDTH = 20;

	// Link to the model
	protected GoModel model;

	private final static Color BG_COLOUR   = new Color (233, 155, 93);
	private final static Color LINE_COLOUR = new Color (61,  61,  61);	
	private final static Color BG_COLOUR_B = new Color (151, 105, 56);
	private final static Color BG_COLOUR_W = new Color (253, 207, 158);

	/**
	 * Constructor which creates the board.
	 * 
	 * @param model
	 */
	public GoBoardComponent (GoModel model) {
		// Call constructor in AbstractBoardComponent
		super (model.getNumOfCells(), model.getNumOfCells(), CELL_SIZE, CELL_SPACING,
			   BORDER_WIDTH, DEFAULT_MOUSE_BORDER_WIDTH, false, false, true);

		model.addObserver (this);	// board observes model
		this.model = model;			// link to model

		// set colours
		Color bgColor = new Color (100, 70, 34);
		setColours (bgColor, bgColor, bgColor, new Color (64, 255, 0));
	}

	/**
	 * Update the graphics depending on the model.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the board (AbstractBoardComponent)
		super.paintComponent (g);

		// draw each Go piece on the board (loop through the model)
		for (int x = 0; x < model.getNumOfCells(); x++) {
			for (int y = 0; y < model.getNumOfCells(); y++) {
				// get piece from model
				int piece = model.getData(x, y);

				// Now get screen co-ordinates (AbstractBoardComponent)
				Point screen = getScreenCoords (x, y);
				
				// Draw piece over background
				if (piece > GoModel.BLANK && piece <= GoModel.WHITE_MARKED_DEAD)
					g.drawImage (GameImages.getImage(piece), 
					             screen.x, 
					             screen.y, 
					             null);
			}
		}
		
		if (!dragPoint.equals(OFF_SCREEN_POINT)) {
			Point sCoords = getScreenCoords (dragPoint.x, dragPoint.y);
			g.setColor(mouseBorderColour);
			
			for (int i = 0; i < mouseBorderWidth; i++)
				g.drawRect (
						sCoords.x + i, sCoords.y + i,
						cellSize - (i * 2) - 1, cellSize - (i * 2) - 1
				);
		}
	}
	
	/**
	 * Draw board background.
	 * 
	 * @see org.jogre.client.awt.AbstractBoardComponent#drawBoardBackground(java.awt.Graphics)
	 */
	protected void drawBoardBackground (Graphics g) {
		// Take note of number of cells.
		int numOfCells = model.getNumOfCells();
			    
	    // Draw background pieces
		int gameState = model.getGameState();
	    int sx, sy;
	    for (int x = 0; x < numOfCells; x++) {
	    	for (int y = 0; y < numOfCells; y++) {
	    		sx = this.borderWidth + (x * CELL_SIZE);
			    sy = this.borderWidth + (y * CELL_SIZE); 
			   
			    g.setColor(BG_COLOUR);	// default colour
			    
			    // Fill in background colours if we are in the marking / end state of the game
		    	if (gameState == GoModel.STATE_MARK || 
		    			 gameState == GoModel.STATE_WHITE_HAPPY ||
		    			 gameState == GoModel.STATE_BLACK_HAPPY ||
		    			 gameState == GoModel.STATE_BOTH_HAPPY) {
		    		int territory = model.getTerritory(x, y);
		    		if (territory == GoModel.BLACK) {
		    			g.setColor(BG_COLOUR_B);
		    		}
		    		else if (territory == GoModel.WHITE) {
		    			g.setColor(BG_COLOUR_W);
		    		} 
		    	}
			    g.fillRect(sx, sy, CELL_SIZE, CELL_SIZE);
	    	}
	    }
	    
	    // Loop through the various lines
	    int indent = this.borderWidth + (CELL_SIZE / 2);
	    int s, length;
		for (int i = 0; i < numOfCells; i++) {		    	
			// retrieve screen co-ordinates
		    s = this.borderWidth + (CELL_SIZE / 2) + (i * CELL_SIZE);
		    length = (CELL_SIZE * (numOfCols - 1));
		
		    g.setColor (LINE_COLOUR);
		    g.drawLine (s, indent, s, length + indent);
		    g.drawLine (indent, s, length + indent, s);
		}
		
		// Draw squares
		for (int x = 0; x < numOfCells; x++) {
		    for (int y = 0; y < numOfCells; y++) {		    	
		        sx = -1; sy = -1;
		        if (numOfCells == 19 && 
		            (x == 3 || x == 9 || x == 15) &&
		            (y == 3 || y == 9 || y == 15)) {
		            sx = this.borderWidth + (CELL_SIZE / 2) + (x * CELL_SIZE);
		            sy = this.borderWidth + (CELL_SIZE / 2) + (y * CELL_SIZE);		            
		        }
		        else if (numOfCells == 13 && 
		                 ((x == 3 && y == 3) || (x == 9 && y == 3) ||
		                  (x == 6 && y == 6) || (x == 3 && y == 9) ||
		                  (x == 9 && y == 9))) {
		            sx = this.borderWidth + (CELL_SIZE / 2) + (x * CELL_SIZE);
		            sy = this.borderWidth + (CELL_SIZE / 2) + (y * CELL_SIZE);
		        }
		        else if (numOfCells == 9 && 
		                 ((x == 2 && y == 2) || (x == 6 && y == 2) ||
		                  (x == 4 && y == 4) || (x == 2 && y == 6) ||
		                  (x == 6 && y == 6))) {
		            sx = this.borderWidth + (CELL_SIZE / 2) + (x * CELL_SIZE);
		            sy = this.borderWidth + (CELL_SIZE / 2) + (y * CELL_SIZE);
		        }
		        
		        g.fillRect (sx - 2, sy - 2, 5, 5);
		    }
		}
		
		g.setColor (LINE_COLOUR);
	    g.drawRect (borderWidth, 
	    		    borderWidth, 
	    		    (numOfCells) * CELL_SIZE, 
	    		    (numOfCells) * CELL_SIZE); 
	}
     
    public static void main (String [] args) {
    	GoModel model = new GoModel (9, 0, 0);
    	GoBoardComponent component = new GoBoardComponent (model);
    	JFrame frame = new JFrame ();
    	frame.getContentPane().add(component);
    	frame.pack();
    	frame.setLocation(400, 200);
    	frame.setVisible(true);
    }
}
