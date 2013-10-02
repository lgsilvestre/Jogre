/*
 * JOGRE (Java Online Gaming Real-time Engine) - Tetris
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
package org.jogre.tetris.client;

import info.clearthought.layout.TableLayout;

import java.awt.Color;

import javax.swing.BorderFactory;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

/**
 * Tetris component.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TetrisComponent extends JogreComponent {
	
	// Constants
	private static final Color [] COLORS = {
		new Color (224, 235, 248), new Color (150, 195, 250),
		new Color (233, 248, 224), new Color (187, 250, 150)};
	private static final int NUM_OF_PLAYERS = 2;		// might change this at some stage
	
	// Declare model
    private TetrisModel gameData;
    private TableConnectionThread conn;
		
	/** 
	 * Default constructor to the game.
	 * 
	 * @param gameData   Model
	 * @param conn       Connection to server
	 */
	public TetrisComponent  (TetrisModel gameData, TableConnectionThread conn) {
		this.gameData = gameData;
		this.conn = conn;
		
		// Create GUI elements
		setUpGUI ();
	}
	
	/**
	 * Set up the GUI frame.
	 */
	private void setUpGUI () {
	 
		// Set up the layout (TableLayout)
	    double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
	    double [][] sizes = {{10, pref, 10, pref, 10, pref, 10, pref, 10}, {10, pref, 10}};
	    setLayout(new TableLayout (sizes));
		
	    // Declare arrays of object.
	    TetrisPlayerModel [] playerModels = new TetrisPlayerModel [NUM_OF_PLAYERS];
	    TetrisGridComponent [] gameGrids = new TetrisGridComponent [NUM_OF_PLAYERS];
	    TetrisNextShapeComponent [] nextShapes = new TetrisNextShapeComponent [NUM_OF_PLAYERS];
	    TetrisLabelComponent [] scoreLabels = new TetrisLabelComponent [NUM_OF_PLAYERS];
	    
	    // Create instances of each object
		for (int i = 0; i < NUM_OF_PLAYERS; i++) {
	    	// Create models
			playerModels [i] = gameData.getPlayerModel(i);
		    
			// Create GUI components
	    	gameGrids [i] = new TetrisGridComponent (
		    	playerModels[i], COLORS[i * 2], COLORS[i * 2 + 1]);		    
		    nextShapes [i] = new TetrisNextShapeComponent (playerModels[i]);
		    scoreLabels [i] = new TetrisLabelComponent (playerModels[i]);
		    
		    // Add next shape / labels 
		    sizes = new double [][] {{5, fill, 5}, {5, pref, 5, fill, 5, pref, 5}}; 
		    JogrePanel panel = new JogrePanel (sizes);
		    panel.add (new PlayerComponent (conn, i, i == 0, false), i == 0 ? "1,1,l,t" : "1,1,r,t");		    
		    panel.add (scoreLabels[i], "1,3,c,b");
		    panel.add (nextShapes[i], "1,5,c,c");
		    
		    panel.setBorder(BorderFactory.createEtchedBorder());
		    
		    // Add GUI items to game panel
		    add (gameGrids[i], "" + (i * 6) + 1 + ",1");
			add (panel, "" + ((i * 2) + 3) + ",1");
		}
	}	
}