/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
 * Copyright (C) 2004 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.warwick.client;

import info.clearthought.layout.TableLayout;

import nanoxml.XMLElement;

import org.jogre.warwick.common.WarwickModel;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;

import org.jogre.common.comm.Comm;

/**
 * Game table for a Warwick.  This class holds the MVC class for a
 * game of warwick.  The MVC classes are WarwickModel, WarwickComponent
 * and WarwickController respectively.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickTableFrame extends JogreTableFrame {

	// Declare MVC classes
	private WarwickModel              model;           // model
	private WarwickBoardComponent     boardComponent;  // board view
	private WarwickPlayerComponent [] playerComponent; // player views
	private WarwickController         controller;      // controller

	/**
	 * Constructor which sets up the MVC classes and takes
	 * table connection.
	 *
	 * @param conn
	 * @param tableNum
	 */
	public WarwickTableFrame (TableConnectionThread conn)
	{
		super (conn);

		// Get the number of players from the table.
		int numPlayers = table.getNumOfPlayers();

		// Initialise MVC classes
		model = new WarwickModel (numPlayers);
		boardComponent = new WarwickBoardComponent (model);
		playerComponent = new WarwickPlayerComponent [numPlayers];
		for (int i=0; i<numPlayers; i++) {
			playerComponent[i] = new WarwickPlayerComponent (model, i);
		}
		controller = new WarwickController (model, boardComponent);

		// The components observe the model
		model.addObserver(boardComponent);
		for (int i=0; i<numPlayers; i++) {
			model.addObserver(playerComponent[i]);
		}

		// Set client/server connection on controller
		controller.setConnection (conn);

		// Enable main view to recieve user input (e.g. mouse clicks) by
		// setting controller
		boardComponent.setController (controller);

		// Set game data and controller (constructor must always call these)
		setupMVC (model, boardComponent, controller);

		// Create game panel and add views to it
		double pref = TableLayout.PREFERRED;
		int SPACE = 5;

			// Make the player column
		double [][] playerColumnSizes = {{pref},
		         {pref, SPACE, pref, SPACE, pref, SPACE, pref, SPACE, pref}};
		JogrePanel playerColumn = new JogrePanel (playerColumnSizes);
		for (int i=0; i<numPlayers; i++) {
			playerColumn.add (playerComponent[i], "0, " + i*2);
		}

			// Make the player component row
		double [][] playerCompRowSizes = {{0.2, 0.2, 0.2, 0.2, 0.2}, {pref}};
		JogrePanel playerCompRow = new JogrePanel (playerCompRowSizes);
		playerCompRow.add (new PlayerComponent (conn, 0), "0,0,l,c");
		playerCompRow.add (new PlayerComponent (conn, 1), "1,0,l,c");
		playerCompRow.add (new PlayerComponent (conn, 2), "2,0,l,c");
		playerCompRow.add (new PlayerComponent (conn, 3), "3,0,l,c");
		playerCompRow.add (new PlayerComponent (conn, 4), "4,0,l,c");

/*
			// Add the main board next to it.
		double [][] mainSizes = {{pref, SPACE, pref}, {pref}};
		JogrePanel panel = new JogrePanel (mainSizes);
		panel.add (boardComponent, "0,0");
		panel.add (playerColumn, "2,0");
*/

		double [][] mainSizes = {{SPACE, pref, SPACE, pref, SPACE},
		                         {SPACE, pref, SPACE, pref, SPACE}};
		JogrePanel panel = new JogrePanel (mainSizes);
		panel.add (boardComponent, "1,3");
		panel.add (playerColumn, "3,3");
		panel.add (playerCompRow, "1,1,3,1");

		// Set game panel
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
/*
		// Tell the components what our seat number is so that it can display
		// things correctly
		int numPlayers = model.getNumPlayers();
		int mySeatNum = controller.getSeatNum();

		boardComponent.setMySeatNum(mySeatNum);
		for (int p = 0; p < numPlayers; p++) {
			playerComponent[p].setMySeatNum((mySeatNum + p) % numPlayers);
		}
*/
	}

	/**
	 * Receive the table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	protected void receiveMessage (XMLElement message) {
		String messageType = message.getName();

		// If a player sat down or stood up (or left), then we may need to
		// update the order of the player components to show the correct
		// player at the top.
		if (messageType.equals(Comm.PLAYER_STATE) ||
		    messageType.equals(Comm.EXIT_TABLE)) {
			updatePlayerComponents();
		} else {
			// Send all other Table messages to the controller
			controller.receiveTableMessage(message);
		}
	}

	/**
	 * Update the order of pieces & scores shown on the screen.
	 *
	 * This takes into account changes as players sit down and stand up.
	 *
	 * A player that is sitting always sees himself at the top, with the other
	 * players progressing down.
	 *
	 * A player not seated always sees player 0 at the top.
	 */
	public void updatePlayerComponents () {
		int numPlayers = model.getNumPlayers();
		int mySeatNum = controller.getSeatNum();

		if ((mySeatNum < 0) || (mySeatNum >= numPlayers)) {
			// Spectators always see the board as if they were player 0.
			mySeatNum = 0;
		}

		boardComponent.setMySeatNum(mySeatNum);
		for (int p = 0; p < numPlayers; p++) {
			playerComponent[p].setMySeatNum((mySeatNum + p) % numPlayers);
		}
	}
}
