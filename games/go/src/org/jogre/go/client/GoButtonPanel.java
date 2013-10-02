/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import org.jogre.client.awt.JogreButton;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.util.GameLabels;

/**
 * Go button panel.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoButtonPanel extends JogreComponent implements ActionListener {

	// Declare constants
	private static final String PASS    = "pass";
    private static final String HAPPY   = "happy";
    private static final String UNHAPPY = "unhappy";
	    
	// Buttons
    private JogreButton  passButton, happyButton, unhappyButton;
    private GoController controller;
    private GoModel      model;
	
	/**
	 * Constructor to a go button panel.
	 */
	public GoButtonPanel (GoModel model, GoController controller) {
		this.model = model;
		model.addObserver (this);
		controller.getTable().getPlayerList().addObserver(this);
		
		this.controller = controller;
		
		// Set up layout
		double pref = TableLayout.PREFERRED;
		double [][] sizes = new double [][] {{pref, 5, pref, 5, pref}, {pref}};
		setLayout(new TableLayout (sizes));
		
		// Create and add buttons
        GameLabels labels = GameLabels.getInstance();
        passButton    = new JogreButton (labels.get(PASS));
        happyButton   = new JogreButton (labels.get(HAPPY));
        unhappyButton = new JogreButton (labels.get(UNHAPPY));
        
        // Add listeners
        passButton.setName(PASS);                  	     
        happyButton.setName(HAPPY);        
        unhappyButton.setName(UNHAPPY);        
        passButton.addActionListener(this);
        happyButton.addActionListener(this);
        unhappyButton.addActionListener(this);
        
        // Add items to panel
        add (passButton,    "0,0");
        add (happyButton,   "2,0");
        add (unhappyButton, "4,0");	
	}

	/**
	 * Button listeners which delegate to controller.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent event) {
		JogreButton button = (JogreButton)event.getSource();
		String name = button.getName();
		
		if (name.equals(PASS)) {
			controller.pass ();
		}
		else if (name.equals (HAPPY)) {
			controller.happy ();
		}
		else if (name.equals (UNHAPPY)) {
			controller.notHappy();
		}
	}
	
	/**
	 * Update enable state of buttons
	 * 
	 * @see org.jogre.client.awt.JogreComponent#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable observerable, Object args) {
		updateEnabled ();
	}
	
	/**
	 * Updated enabled true / false.
	 */
	private void updateEnabled () {
		int curPlayer = controller.getCurrentPlayerSeatNum();
		passButton.setEnabled    (model.canPass (curPlayer) && controller.isThisPlayersTurn());
		happyButton.setEnabled   (model.canHappyOrUnhappy(controller.getSeatNum()));
		unhappyButton.setEnabled (model.canHappyOrUnhappy(controller.getSeatNum()));
		
		if (model.canMark())
			controller.updateTerritories();
	}
}