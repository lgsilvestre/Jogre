/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.quetris.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreAwt;

/**
 * Quetris grid component which displays the various different 
 * quetris pieces on the screen.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class QuetrisGridComponent extends AbstractBoardComponent {

	// Declare constants which define what the board looks like
	private static final int NUM_OF_COLS = QuetrisPlayerModel.NUM_OF_COLS;
	private static final int NUM_OF_ROWS = QuetrisPlayerModel.NUM_OF_ROWS;
	private static final int CELL_SIZE = 16;
	private static final int CELL_SPACING = 0;
	private static final int BORDER_WIDTH = 10;
	
	private QuetrisPlayerModel quetrisPlayerModel;
	
	private static final Color bgColor = new Color (240, 240, 240);			// FIXME label.properties
	
	private Color color1, color2;	
    
    /**
     * Constructor for a quetris grid component.
     */
    public QuetrisGridComponent (QuetrisPlayerModel quetrisPlayerModel, Color color1, Color color2) {
        // Call board stuff in abstract board component
        super (NUM_OF_ROWS, NUM_OF_COLS, CELL_SIZE, CELL_SPACING,
    		   BORDER_WIDTH, 0, false, false, false);
        
        // Set colours
        this.color1 = color1;
        this.color2 = color2;
        
        // Set up colours to default colours
		setColours (			// FIXME label.properties
		    bgColor,
		    bgColor,
			new Color (64, 64, 64),
			new Color (250, 250, 250)
		);		
        
        // Set link to model
        this.quetrisPlayerModel = quetrisPlayerModel;
        
        // Add observer on model
        quetrisPlayerModel.addObserver (this);
    }

	/** 
	 * Method used for udpating the background.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
	    // Call paint in super class.
	    super.paintComponent(g);
	    
	    // Draw gradient
	    JogreAwt.drawVerticalGradiant(g, BORDER_WIDTH, BORDER_WIDTH, getWidth() - BORDER_WIDTH - 1, getHeight() - BORDER_WIDTH, color1, color2);
				
		// Now draw the various blocks in each shape		
		for (int i = 0; i < QuetrisPlayerModel.NUM_OF_COLS; i++) {
			for (int j = 0; j < QuetrisPlayerModel.NUM_OF_ROWS; j++) {
				// Calculate co-ordinates
				Point sCoords = getScreenCoords (i, j);  
				
				// Check to see if a block is at the particular position
				int block = quetrisPlayerModel.getGridShapeNum(i, j) + 1;
				if (block > 0) {					
					g.drawImage(
						GameImages.getImage(block),
						sCoords.x, sCoords.y, null
					);
				}
			}
		}
	}
}