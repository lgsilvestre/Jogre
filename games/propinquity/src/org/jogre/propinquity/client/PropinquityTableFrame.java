/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import info.clearthought.layout.TableLayout;
import nanoxml.XMLElement;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;
import org.jogre.propinquity.common.CommPropinquityAttackNum;
import org.jogre.propinquity.common.CommPropinquityMove;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Propinquity table frame.
 */
public class PropinquityTableFrame extends JogreTableFrame {

	private PropinquityModel         propinquityModel;
	private PropinquityComponent     propinquityComponent;
	private PropinquityInfoComponent propinquityInfoComponent;
	private PropinquityController    propinquityController;

	/**
	 * Create a new propinquity table.
	 *
	 * @param conn
	 * @param table
	 */
	public PropinquityTableFrame (TableConnectionThread conn) {
		super (conn);

		// Create 1 model, 2 views and 1 controller
		propinquityModel = new PropinquityModel ();
		propinquityComponent = new PropinquityComponent (propinquityModel);
		propinquityController = new PropinquityController (propinquityModel, propinquityComponent);
		propinquityController.setConnection (conn);

		propinquityComponent.setController (propinquityController);
		propinquityInfoComponent = new PropinquityInfoComponent (propinquityModel, propinquityController);

		// Add observers to game components
		propinquityModel.addObserver (propinquityComponent);
		propinquityModel.addObserver (propinquityInfoComponent);
        table.getPlayerList().addObserver (propinquityInfoComponent);

		// Create game panel and add
		double border = 10;
		double pref = TableLayout.PREFERRED, fill = TableLayout.PREFERRED;
		double [][] sizes =
			{{border, pref, border, pref, border},    // rows
		     {border, pref, border}};                 // columns
		JogrePanel gamePanel = new JogrePanel (sizes);		
		JogrePanel infoPanel = new JogrePanel (new double [][] {{pref}, {pref, fill, pref}});
		
		// add components to game panel
		gamePanel.add (new PlayerComponent (conn, 0), "3,1,l,t");
		gamePanel.add (propinquityInfoComponent,      "3,1,l,c");
		gamePanel.add (new PlayerComponent (conn, 1), "3,1,l,b");
		gamePanel.add (propinquityComponent, "1,1");
		
		// Set game panel
		setGamePanel (gamePanel);

		// Setup model/view/controller in super class (required method call)
		setupMVC (propinquityModel, propinquityComponent, propinquityController);

		// Add the game panel to the JogreTableFrame		
		pack ();
	}

	/**
	 * Start the game.
	 *
	 * @see org.jogre.awt.JogreTableFrame#startGame(org.jogre.comm.CommTableAction)
	 */
	public void startGame () {
		super.startGame ();
		propinquityController.start();
	}

	/**
	 * Implementation of the recieve message method.
	 *
	 * @see org.jogre.client.awt.JogreTableFrame#receiveMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage (XMLElement message) {
        String messageType = message.getName();

        if (messageType.equals (CommPropinquityMove.XML_NAME)) {
            CommPropinquityMove move = new CommPropinquityMove (message);
            propinquityController.receiveMove(move);
        }
        else if (messageType.equals (CommPropinquityAttackNum.XML_NAME)) {
            CommPropinquityAttackNum attackNum = new CommPropinquityAttackNum (message);
            propinquityModel.setAttackNum (attackNum.getAttackNum());
        }
	}
}
