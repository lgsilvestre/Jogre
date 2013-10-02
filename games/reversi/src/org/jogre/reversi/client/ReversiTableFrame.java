/*
 * JOGRE (Java Online Gaming Real-time Engine) - Reversi
 * Copyright (C) 2005  Ugnich Anton (anton@portall.zp.ua)
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
package org.jogre.reversi.client;

import info.clearthought.layout.TableLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;

// Jogre table frame
public class ReversiTableFrame extends JogreTableFrame {

	private ReversiModel model;
	private ReversiBoardComponent board;
	private ReversiScoreComponent score = null;
	private ReversiController controller;

	// Constructor which passes a client connection thread and a table
	public ReversiTableFrame (TableConnectionThread conn) {
		super (conn);

		// Create model, view and register view to model
		model = new ReversiModel ();		// create model
		board = new ReversiBoardComponent (model);	// board
        score = new ReversiScoreComponent (model, 100, 25);
		model.addObserver(board);			    // board observes model
		model.addObserver(score);

		// Create controller which updates model and controlls the view
		controller = new ReversiController (model, board);
		controller.setConnection (conn);       // connection
		board.setController(controller);

        // Set up MVC classes in super class
		setupMVC (model, board, controller);

		// Create game panel and add components to it.
		double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
		double[][] size = {{10, pref, 10, fill, 10},
				           {10, pref, 10}};
		JogrePanel gamePanel = new JogrePanel(size);
		gamePanel.add(board, "1,1,c,t");
		gamePanel.add(new PlayerComponent (conn, 0), "3,1,l,t");
		gamePanel.add(new PlayerComponent (conn, 1), "3,1,l,b");
		gamePanel.add(score, "3,1,l,c");

		// Add the game panel
		setGamePanel (gamePanel);

		// pack frame
		pack();
	}
}
