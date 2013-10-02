/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.go.client;

import info.clearthought.layout.TableLayout;

import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePanel;
import org.jogre.common.Player;
import org.jogre.common.Table;

/**
 * Go status panel.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoStatusPanel extends JogrePanel implements Observer {

	private Table table; 
	private GoController controller;
	private GoModel model;
	private int index;
	
	private JogreLabel capturesValue, prisonersValue, areasValue, territoriesValue;
	
	/**
	 * Constructor for a Go status player.
	 * 
	 * @param table
	 * @param model
	 * @param index
	 */
	public GoStatusPanel (Table table, GoController controller, int index) {
		// Set fields
		this.controller = controller;			// link to model
		this.model = controller.getModel();
		this.table = table;
		this.index = index;
		
		// Setup GUI
		setupGUI ();
		
		// Add observers
		model.addObserver (this);	// board observes model
		table.addObserver (this);	
		table.getPlayerList().addObserver (this);
	}
	
	/**
	 * Set up the GUI.
	 */
	private void setupGUI () {
		// Set 
		double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
		double [][] sizes = {{5, 0.5, 30, 0.5, 30, 5},{5, pref, 5, pref, 5}};
		setLayout(new TableLayout (sizes));
		
		// Create label
		JogreLabel capturesText = new JogreLabel ("Captures: ", 'p', 11);
		JogreLabel prisonersText = new JogreLabel ("Prisoners: ", 'p', 11);
		JogreLabel areasText = new JogreLabel ("Areas: ", 'p', 11);
		JogreLabel territoriesText = new JogreLabel ("Territories: ", 'p', 11);		
		capturesValue = new JogreLabel ("0", 'p', 11);
		this.prisonersValue = new JogreLabel ("0", 'p', 11);
		this.areasValue = new JogreLabel ("0", 'p', 11);
		this.territoriesValue = new JogreLabel ("0", 'p', 11);
		
		// Add labels
		add (capturesText, "1,1");    add (capturesValue, "2,1");
		add (prisonersText, "3,1");   add (prisonersValue, "4,1");
		add (areasText, "1,3");       add (areasValue, "2,3");
		add (territoriesText, "3,3"); add (territoriesValue, "4,3");
		
		setBorder(BorderFactory.createEtchedBorder());		// add an etched border
	}
	
	/**
	 * Update method.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable observerable, Object args) {
		Player player = table.getPlayerList().getPlayer(index);
		if (player != null) {
			capturesValue.setText(String.valueOf(controller.getModel().getCapturedStones(index)));
			GoScore goScore = controller.getPieceMover().getScore();
			if (goScore != null) {
				prisonersValue.setText (String.valueOf(goScore.getPrisoner(index)));
				areasValue.setText (String.valueOf(goScore.getArea(index)));
				territoriesValue.setText (String.valueOf(goScore.getTerritory(index)));
			}
		}
	}
}