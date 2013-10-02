/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
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
package org.jogre.checkers.client;

import info.clearthought.layout.TableLayout;

import javax.swing.ImageIcon;

import org.jogre.checkers.common.CommCheckersMove;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.PlayerComponent;

/**
 * Checkers table frame.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CheckersTableFrame extends JogreTableFrame {

    private static final int WIDTH = 510, HEIGHT = 504;

	// Declare the game data.
	private CheckersModel checkersModel;

	// Declare components
	private CheckersBoardComponent checkersBoard;

	// Declare the game controller
	private CheckersController checkersController;

	/**
	 * @param conn
	 * @param table
	 */
	public CheckersTableFrame (TableConnectionThread conn) {
		super (conn, WIDTH, HEIGHT);

		// Create game data and checkers board and add observers
		this.checkersModel = new CheckersModel ();
		this.checkersBoard = new CheckersBoardComponent (checkersModel);

		// Add component observers to the game data.
		checkersModel.addObserver (checkersBoard);

		// Set up game controller
		this.checkersController = new CheckersController (checkersModel, checkersBoard);
		checkersController.setConnection (conn);
		checkersBoard.setController (checkersController);   // set controller on board

		// Set game data and controller (constructor must always call these)
		setupMVC (checkersModel, checkersBoard, checkersController);

		// Create game panel and add components to it.
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10, pref, 10}, {10, pref, 10}};
		JogrePanel panel = new JogrePanel (sizes);
		
		panel.add (checkersBoard, "1,1");
		panel.add (new PlayerComponent (conn, 1, true), "3,1,l,t");
		panel.add (new PlayerComponent (conn, 0, true), "3,1,l,b");

		setGamePanel (panel);

		pack();
	}

	/**
	 * Override to ensure that the player is seated in the correct position.
	 *
	 * @param tableAction
	 */
	public void startGame () {
		super.startGame ();

		// Ensure board is correctly reversed.
		checkersBoard.setReversed (gameController.getSeatNum() == ICheckersModel.PLAYER_TWO);
		checkersBoard.repaint();
	}
}
