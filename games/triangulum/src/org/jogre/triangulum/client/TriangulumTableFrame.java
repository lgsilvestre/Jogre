/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.client;

import java.awt.Color;

import info.clearthought.layout.TableLayout;

import javax.swing.BorderFactory;

import nanoxml.XMLElement;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;
import org.jogre.common.comm.Comm;
import org.jogre.triangulum.common.TriangulumModel;

/**
 * Game table for a Triangulum.  This class holds the MVC class for a
 * game of triangulum.  The MVC classes are TriangulumModel, TriangulumComponent
 * and TriangulumController respectively.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumTableFrame extends JogreTableFrame {

	// Declare MVC classes
	private TriangulumModel         triangulumModel;      // model
	private TriangulumComponent     triangulumComponent;  // view
	private TriangulumController    triangulumController; // controller

	/**
	 * Constructor which sets up the MVC classes and takes
	 * table connection.
	 *
	 * @param conn
	 * @param tableNum
	 */
	public TriangulumTableFrame (TableConnectionThread conn)
	{
		super (conn);

		// Get the game parameters from the table property
		int flavor = Integer.parseInt(table.getProperty("flavor"));
		int numPlayers = table.getNumOfPlayers();

		// Initialize MVC classes
		this.triangulumModel = new TriangulumModel (flavor, numPlayers);
		this.triangulumComponent = new TriangulumComponent (triangulumModel, triangulumModel.getNumRows());
		this.triangulumComponent.setBorder (BorderFactory.createLineBorder(Color.black, 3));
		this.triangulumController = new TriangulumController (triangulumModel, triangulumComponent);

		// Set client/server connection on controller FIXME - constructor?
		this.triangulumController.setConnection (conn);

		// Enable main view to recieve user input (e.g. mouse clicks) by setting controller
		this.triangulumComponent.setController (triangulumController);

		// Set game data and controller (constructor must always call these)
		setupMVC (triangulumModel, triangulumComponent, triangulumController);

		// Create game panel and add main view to it
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10}, {10, pref, 5, pref, 5, pref, 10}};       // simple 1x1 table
		JogrePanel panel = new JogrePanel (sizes);
		panel.add (new PlayerComponent (conn, 0, true), "1,1,l,c");
		panel.add (new PlayerComponent (conn, 1, false), "1,1,r,c");
		panel.add (new PlayerComponent (conn, 2, true), "1,5,l,c");
		panel.add (new PlayerComponent (conn, 3, false), "1,5,r,c");
		panel.add (triangulumComponent, "1,3");

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
	}

	/**
	 * Recieve the table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage(XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(Comm.PLAYER_STATE)) {
			updatePlayerComponents();
		} else if (messageType.equals(Comm.EXIT_TABLE)) {
			updatePlayerComponents();
		} else {
			// Send all other Table messages to the master controller
			triangulumController.receiveTableMessage(message);
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
		triangulumComponent.setSeatNumber(triangulumController.getSeatNum());
	}
}
