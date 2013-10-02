/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.abstrac.client;

import javax.swing.ImageIcon;

import java.awt.Color;

import org.jogre.common.comm.Comm;
import org.jogre.common.util.GameProperties;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import nanoxml.XMLElement;

import info.clearthought.layout.TableLayout;

// Jogre table frame
public class AbstracTableFrame extends JogreTableFrame {

	// Main model & controller
	private AbstracModel model;
	private AbstracController masterController;
	
	// Components of the view
	private AbstracHandComponent handComponent;
	private AbstracTakenComponent takenComponents[] = new AbstracTakenComponent[2];

	// Controller for hand component.
	private AbstracHandController handController;

	// Constructor which passes a client connection thread
	public AbstracTableFrame (TableConnectionThread conn) {
		super (conn);

		// Create the model
		model = new AbstracModel();

		// Make the components that display the model
		makeComponents();

		// Create the master controller which updates the model and communicates
		// with the server
		masterController = new AbstracController(model);
		masterController.setConnection(conn);

		// Create the controllers for my hand & bid components and attach to components
		handController = new AbstracHandController(model, handComponent, takenComponents[0]);
		handController.setConnection(conn);
		handComponent.setController(handController);

		// Set up MVC classes in super class
		setupMVC (model, handComponent, masterController);

		// Create the panel and set it in the table frame
		JogrePanel panel = makeAbstracPanel(conn);
		setGamePanel(panel);
		pack();
	}

	/**
	 * Recieve the table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage(XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(Comm.PLAYER_STATE)) {
			updateComponentColors();
		} else if (messageType.equals(Comm.EXIT_TABLE)) {
			updateComponentColors();
		} else {
			// Send all other Table messages to the master controller
			masterController.receiveTableMessage(message);
		}
	}

	/** 
	 * Update the colors of the components based upon the current player ID's.
	 */
	private void updateComponentColors() {
		// Determine our seat number so that we can put ourselves at the top.
		int mySeatNum = masterController.getSeatNum();

		if ((mySeatNum != 0) && (mySeatNum != 1)) {
			// If I'm a spectator, put player 0 on the top
			mySeatNum = 0;
		}

		// Change the players that the components are showing to the correct ones.
		// This ensures that active players always see themselves on the top
		takenComponents[0].setPlayerId(mySeatNum);
		takenComponents[1].setPlayerId(1-mySeatNum);
	}

	/**
	 * Create the panels of the table
	 */
	private JogrePanel makeAbstracPanel(TableConnectionThread conn) {

		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
			{5, pref, 5},
			{5, pref, 5, pref, 5, pref, 5, pref, 5} };

		JogrePanel gamePanel = new JogrePanel (table_params);

		gamePanel.add(new PlayerComponent (conn, 0, true), "1,1,l,t");
		gamePanel.add(new PlayerComponent (conn, 1, false), "1,1,r,t");
		gamePanel.add(takenComponents[0], "1,3,c,c");
		gamePanel.add(handComponent, "1,5,c,c");
		gamePanel.add(takenComponents[1], "1,7,c,c");

		return gamePanel;
	}

	/**
	 * Create the components used for the Abstrac game
	 */
	private void makeComponents() {
		AbstracGraphics AB_graphics = AbstracGraphics.getInstance();

		// Read settings from the game.properties file
		GameProperties props = GameProperties.getInstance();
		int cardSpacing = props.getInt("display.hand.cardSpacing", 19);
		int selectYOffset = props.getInt("display.hand.selectYOffset", 20);
		int takenDigitSpacing = props.getInt("display.taken.DigitSpacing", 2);
		int takenHSpacing = props.getInt("display.taken.HSpacing", 5);
		int takenVSpacing = props.getInt("display.taken.VSpacing", 3);

		// Create component for the hand to choose from
		handComponent = new AbstracHandComponent (
		    model.getHand(),            // theHand
		    AB_graphics,                // cardRenderer
		    cardSpacing,                // cardSpacing
		    selectYOffset,              // selectYOffset
		    24                          // maxCardsInHand
		);

		// Create components for showing cards taken
		for (int i=0; i<2; i++) {
			takenComponents[i] = new AbstracTakenComponent (
			    model,                  // model
			    i,                      // PlayerId
			    AB_graphics,            // numRenderer
			    AB_graphics,            // suitRenderer
			    takenDigitSpacing,      // digitSpacing
			    takenHSpacing,          // HSpacing
			    takenVSpacing           // VSpacing
			);
		}

		takenComponents[0].setHandComponent(handComponent);
	}
}

