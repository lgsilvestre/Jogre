/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
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
package org.jogre.quetris.client;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.JogreLabel;
import org.jogre.common.util.GameLabels;

/**
 * Component for displaying the scores.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class QuetrisLabelComponent extends JogreComponent {
	
    // Link to the player model
    private QuetrisPlayerModel playerModel;
    
    // Link to the GUI label.
    private JogreLabel linesLabel;
	private JogreLabel scoreLabel;
    
	/**
	 * Constructor for the label component.
	 * 
	 * @param gameData
	 */
	public QuetrisLabelComponent (QuetrisPlayerModel playerModel){
		super ();
		
		// Set link to the player model
		this.playerModel = playerModel;
		this.playerModel.addObserver(this);		
		
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{0.5, 10, 0.5}, {10, pref, 10, pref, 10}};
		setLayout(new TableLayout (sizes));
		
		// Create labels
		GameLabels labels = GameLabels.getInstance();
		JogreLabel linesText = new JogreLabel (labels.get("lines") + ":", 'B', 16);
		JogreLabel scoreText = new JogreLabel (labels.get("score") + ":", 'B', 16);
		Color scoreColour = new Color (100, 40, 150);			// FIXME labels.properties
		linesLabel = new JogreLabel ("", 'b', 16, scoreColour);
		scoreLabel = new JogreLabel ("", 'b', 16, scoreColour);
		
		// Add to component
		add (linesText,  "0,1,r,c");
		add (linesLabel, "2,1,l,c");
		add (scoreText,  "0,3,r,c");
		add (scoreLabel, "2,3,l,c");
		
		// Add observer on model
        this.playerModel.addObserver (this);
	}
	
	/**
	 * Update the label.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
	    int numOfLines = playerModel.getNumOfLines();
	    int score = playerModel.getScore();
	    linesLabel.setText(String.valueOf(numOfLines));
	    scoreLabel.setText(String.valueOf(score));
	}}
