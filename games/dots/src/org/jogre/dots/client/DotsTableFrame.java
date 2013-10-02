/*
 * JOGRE (Java Online Gaming Real-time Engine) - Dots
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
package org.jogre.dots.client;

import info.clearthought.layout.TableLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.common.Table;

/**
 * Dots table frame.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class DotsTableFrame extends JogreTableFrame {

	// Declare the game data.
	private DotsModel model = null;

	// Declare components
	private DotsBoardComponent board = null;
	private DotsScoreComponent score = null;

	// Declare the game controller
	private DotsController controller = null;

	/**
	 * Default constructor
	 *
	 * @param conn
	 */
	public DotsTableFrame (TableConnectionThread conn) {
		super (conn);

		// Create game model and component
	    Table table = conn.getTable();
	    int boardSize = Integer.parseInt(table.getProperty("size", String.valueOf(DotsModel.DEFAULT_BOARD_SIZE)));

		this.model = new DotsModel(boardSize, boardSize);
		this.model.setNumOfPlayers(table.getNumOfPlayers());
		this.board = new DotsBoardComponent (model);
		this.score = new DotsScoreComponent (model, 50, 25);

		// Create game panel and add components to it.
		double pref = TableLayout.PREFERRED;
		double[][] size = { { 20, pref, 20 },
				{ 20, pref, 5, pref, 20 } };
		JogrePanel gamePanel = new JogrePanel(size);
		gamePanel.add(board, "1,1,c,t");
		gamePanel.add(score, "1,3,c,t");

		// Add component observers to the game data
		this.model.addObserver (board);
		this.model.addObserver (score);

		// Set up game controller
		this.controller = new DotsController (model, board);
		this.controller.setConnection (conn);
		this.board.setController (controller);

		// Set game data and controller (constructor must always call these)
		setupMVC (model, board, controller);

		//gamePanel
		setGamePanel (gamePanel);

		// pack frame
		pack();
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogreTableFrame#startGame()
	 */
	public void startGame () {
		this.board.setCurrentSeat(this.controller.getSeatNum());
	}
}