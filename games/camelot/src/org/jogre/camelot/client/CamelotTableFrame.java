/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
 * Copyright (C) 2006  Richard Walter
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
package org.jogre.camelot.client;

import info.clearthought.layout.TableLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;

import javax.swing.JSeparator;

/**
 * Table frame to a game of camelot.
 */
public class CamelotTableFrame extends JogreTableFrame {
    
	private CamelotModel model;
	private CamelotBoardComponent boardComponent;
	private CamelotController controller;

	/**
	 * Constructor to a camelot game.
	 * 
	 * @param conn
	 */
	public CamelotTableFrame (TableConnectionThread conn) {
		super (conn);

		// Create model, view and register view to model
		model = new CamelotModel ();                            // create model
		boardComponent = new CamelotBoardComponent (model);     // board
		model.addObserver(boardComponent);                      // board observes model

		// Create controller which updates model and controls the view
		controller = new CamelotController (model, boardComponent);
		controller.setConnection (conn);
		boardComponent.setController(controller);

		// Set up MVC classes in super class
		setupMVC (model, boardComponent, controller);

		// Create the panel to place the game items on
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10}, {10, pref, 10, pref, 10, pref, 10}};
		JogrePanel panel = new JogrePanel (sizes);

		// Add the player components & a separator
		panel.add (new PlayerComponent (conn, 0, true),  "1,1,l,c");
		panel.add (new PlayerComponent (conn, 1, false), "1,1,r,c");
		panel.add (new JSeparator (), "1,3");

		// Add the game board
		panel.add (boardComponent, "1,5");

		// Place the panel on the table frame.
		setGamePanel (panel);

		// Set the size of the components on the table.
		pack();
	}

	/**
	 * Override so that when the game starts, if this is player two, then we want to
	 * flip the board over.  This way, each player is always moving toward the top
	 * of the board.
	 */
	public void startGame () {
		super.startGame ();

		// Ensure board is correctly reversed.
		boardComponent.setReversed (controller.getSeatNum() == 1);
		boardComponent.repaint();
	}
}
