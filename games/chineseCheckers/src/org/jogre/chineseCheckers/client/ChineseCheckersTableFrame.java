/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.chineseCheckers.client;

import info.clearthought.layout.TableLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import javax.swing.JSeparator;
import java.awt.Color;

/**
 * Chinese Checkers table frame.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class ChineseCheckersTableFrame extends JogreTableFrame {

	private ChineseCheckersModel model;
	private ChineseCheckersBoardComponent boardComponent;
	private ChineseCheckersController controller;

	// Constructor which passes a client connection thread
	public ChineseCheckersTableFrame (TableConnectionThread conn) {
		super (conn);
		
		// Get the long jump parameter from the table property
		boolean longJumps = "t".equals(table.getProperty("longJumps"));
		int numPlayers = table.getNumOfPlayers();

		// Create model, view and register view to model
		model = new ChineseCheckersModel (numPlayers, longJumps);
		boardComponent = new ChineseCheckersBoardComponent (model);
		model.addObserver(boardComponent);

		// Create controller which updates model and controls the view
		controller = new ChineseCheckersController (model, boardComponent);
		controller.setConnection (conn);
		boardComponent.setController(controller);

		// Set up MVC classes in super class
		setupMVC (model, boardComponent, controller);

		// Add view to a panel and set on table frame
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10}, {10, pref, 10, pref, pref, 5, pref, 5, pref, 5}};
		JogrePanel panel = new JogrePanel (sizes, Color.white, new Color (210, 210, 210));
		panel.add (boardComponent, "1,1");
		panel.add (new JSeparator (), "1,3");

		panel.add (new PlayerComponent (conn, 0, true), "1,4,l,c");
		panel.add (new PlayerComponent (conn, 1, true), "1,6,l,c");
		panel.add (new PlayerComponent (conn, 2, true), "1,8,l,c");
		panel.add (new PlayerComponent (conn, 3, false), "1,4,r,c");
		panel.add (new PlayerComponent (conn, 4, false), "1,6,r,c");
		panel.add (new PlayerComponent (conn, 5, false), "1,8,r,c");

		// Set game panel on the table frame
		setGamePanel (panel);
		pack();
	}

}
