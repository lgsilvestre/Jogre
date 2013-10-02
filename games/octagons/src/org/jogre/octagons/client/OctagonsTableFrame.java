/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.octagons.client;

import info.clearthought.layout.TableLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import javax.swing.BorderFactory;
import java.awt.Color;

// Jogre table frame
public class OctagonsTableFrame extends JogreTableFrame {

	private OctagonsModel model;
	private OctagonsBoardComponent boardComponent;
	private OctagonsController controller;

	// Constructor which passes a client connection thread
	public OctagonsTableFrame (TableConnectionThread conn) {
		super (conn);

		// Create model, view and register view to model
		model = new OctagonsModel ();
		boardComponent = new OctagonsBoardComponent (model);
		model.addObserver(boardComponent);

		// Create controller which updates model and controls the view
		controller = new OctagonsController (model, boardComponent);
		controller.setConnection (conn);
		boardComponent.setController(controller);

		// Set up MVC classes in super class
		setupMVC (model, boardComponent, controller);

		// Create game panel and add main view to it
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{pref}, {10, pref, 5, pref, 10}};
		JogrePanel panel = new JogrePanel (sizes);
		panel.add (new PlayerComponent (conn, 0, true), "0,1,l,c");
		panel.add (new PlayerComponent (conn, 1, false), "0,1,r,c");
		boardComponent.setBorder (BorderFactory.createLineBorder (Color.black, 3));
		panel.add (boardComponent, "0,3,c,c");

		// Set game panel on the table frame
		setGamePanel (panel);
		pack();
	}

}
