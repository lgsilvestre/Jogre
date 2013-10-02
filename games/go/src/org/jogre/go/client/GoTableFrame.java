/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.go.client;

import info.clearthought.layout.TableLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;
import org.jogre.common.Table;

/**
 * Go table frame.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoTableFrame extends JogreTableFrame {
	
    private static final double pref = TableLayout.PREFERRED;
    private static final double fill = TableLayout.FILL;
    
    // Declare MVC objects
    private GoModel model;
    private GoBoardComponent boardComponent;
    private GoButtonPanel buttonPanel;
    private GoController controller;
    
    /**
     * Constructor which passes a client connection thread and a table.
     * 
     * @param conn
     */
    public GoTableFrame (TableConnectionThread conn) {
        super (conn);
        
        // Create game model and component
	    Table table = conn.getTable();
	    int boardSize = Integer.parseInt(table.getProperty("size", String.valueOf(GoModel.DEFAULT_BOARD_SIZE)));
	    int score_type = Integer.parseInt(table.getProperty("score_type"));
	    int komi = Integer.parseInt(table.getProperty("komi"));
        
        // Create model, view and register view to model
        model = new GoModel (boardSize, score_type, komi);		// create model        
        boardComponent = new GoBoardComponent (model);	// board
        controller = new GoController (model, boardComponent);
        controller.setConnection (conn);       // connection
        boardComponent.setController(controller);
        buttonPanel = new GoButtonPanel (model, controller);
        
        // Create main panel and populate with view and sub views
        double [][] sizes = {{10, pref, 10, fill, 10}, {10, pref, 5, pref, 5, pref, fill, pref, 5, pref, 10}};
        JogrePanel panel = new JogrePanel (sizes);
        panel.add (buttonPanel,                              "1,1,l,t");
        panel.add (getInfoPanel(),                           "3,1,l,t");
        panel.add (boardComponent,                           "1,3,1,9,l,t");        
        panel.add (new PlayerComponent (conn, 0),     "3,3");
        panel.add (new GoStatusPanel (table, controller, 0), "3,5");
        panel.add (new PlayerComponent (conn, 1),     "3,7");
        panel.add (new GoStatusPanel (table, controller, 1), "3,9");       
        
        // Add game panel
        setGamePanel (panel);
        
        // Set up MVC classes in super class
        setupMVC (model, boardComponent, controller);
        invalidate();
        pack();
        
        // Update components
        model.refreshObservers();
    }
    
    /**
     * Return the info panel.
     * 
     * @return
     */
    public JPanel getInfoPanel() {
    	String [] scoreMethods = {"Chinese (Area)", "Japanese (Territory)"}; 
    	
    	double [][] sizes = {{5, 0.5, 0.5, 5},{pref}};
        JogrePanel panel = new JogrePanel (sizes);
        
        panel.add (new JogreLabel ("Komi: " + String.valueOf (model.getKomi()), 'p', 11), "1,0"); 
        panel.add (new JogreLabel ("Scoring: " + scoreMethods[model.getScoreMethod()], 'p', 11), "2,0");
		
        return panel;
    }
    
    /**
     * Start game.
     * 
     * @see org.jogre.client.awt.JogreTableFrame#startGame()
     */
    public void startGame () {
        super.startGame();				
    }
}
