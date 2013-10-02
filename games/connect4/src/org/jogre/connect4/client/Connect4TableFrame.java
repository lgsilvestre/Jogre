/*
 * JOGRE (Java Online Gaming Real-time Engine) - Connect4
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
package org.jogre.connect4.client;

import info.clearthought.layout.TableLayout;

import javax.swing.ImageIcon;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.PlayerComponent;

// Jogre table frame
public class Connect4TableFrame extends JogreTableFrame {
	
	private static final int WIDTH = 510, HEIGHT = 550;

	private Connect4Model model;
	private Connect4BoardComponent boardComponent;
	private Connect4Controller controller;

	// Constructor which passes a client connection thread and a table
	public Connect4TableFrame (TableConnectionThread conn) {
		super (conn);

		// Create model, view and register view to model
		model = new Connect4Model ();		// create model
		boardComponent = new Connect4BoardComponent (model);	// board
		model.addObserver(boardComponent);			    // board observes model

		// Create controller which updates model and controlls the view
		controller = new Connect4Controller (model, boardComponent);
		controller.setConnection (conn);       // connection
		boardComponent.setController(controller);

		// Add view to a panel and set on table frame
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10, pref, 10},
		                     {10, pref, 10, pref, 10}};
		JogrePanel panel = new JogrePanel (sizes);
		
		panel.add (boardComponent, "1,1,1,3");
		panel.add (new PlayerComponent (conn, 1, true), "3,1,l,t");
		panel.add (new PlayerComponent (conn, 0, true), "3,3,l,b");
		
		setGamePanel (panel);	// add panel to table frame

		// Set up MVC classes in super class
		setupMVC (model, boardComponent, controller);
		
		invalidate();
		pack ();		
	}
	
	public void startGame () {
	    super.startGame();
	    boardComponent.setSeatNum (controller.getSeatNum());
	}
}
