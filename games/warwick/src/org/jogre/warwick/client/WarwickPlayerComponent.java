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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jogre.warwick.common.WarwickModel;
import org.jogre.client.awt.JogreComponent;

/**
 * Component which draws information for a single player for the Warwick
 * game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickPlayerComponent extends JogreComponent {

	// Link to the model
	protected WarwickModel model;

	// Graphics helper
	protected WarwickGraphics kGraphics;

	// The player # whom I am displaying the information for
	private int mySeatNum;

	// The graphical offsets used to "skew" the stack of to-be-played pieces.
	private int [][] pieceOffsets = {{29, 46}, {32, 33}, {28, 20}};

	// The rectangle that sits below the other graphical elements.
	private Rectangle coolRect = new Rectangle (0, 10, 114, 50);

	// Constructor which creates the board
	public WarwickPlayerComponent (WarwickModel model, int mySeatNum) {
		super ();

		// Save parameters
		this.model = model;
		kGraphics = WarwickGraphics.getInstance();

		// Initialize stuff;
		this.mySeatNum = mySeatNum;

		// Set component dimension        
		Dimension dim = new Dimension (120, 75);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Set the seat number for the player that I'm supposed to display info for.
	 */
	public void setMySeatNum (int newSeatNum) {
		mySeatNum = newSeatNum;
		repaint();
	}

	/**
	 * Draw the player area
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent (g);

		// If we're not showing anyone, then don't draw anything.
		if (mySeatNum < 0) {
			return;
		}

		// Draw the underlying rectangle
		g.setColor(Color.black);
		kGraphics.outlineRect (g, coolRect, 3, WarwickGraphics.CORNER10);

		// Draw the stack of "to-be-played" pieces (or a score marker, if there
		// are none left to play.)
		int toBePlayed = model.getPiecesToPlace(mySeatNum);
		if (toBePlayed == 0) {
			kGraphics.paintImage (g, 10, 51,
			                      WarwickGraphics.SCORE_MARKERS, mySeatNum, 0);
		} else {
			for (int i=0; i<toBePlayed; i++) {
				kGraphics.paintImage (g, pieceOffsets[i][0], pieceOffsets[i][1],
				                      WarwickGraphics.TOKENS, mySeatNum, 0);
			}
		}

		// Draw the allegience rose symbol
		int allegience = model.getAllegience(mySeatNum);
		kGraphics.paintImage (g, 85, 35, WarwickGraphics.ROSES, allegience, 0);

		// Draw the chosen or not symbol
		if (model.isAllegienceChosen(mySeatNum)) {
			kGraphics.paintImage (g, 101, 48, WarwickGraphics.CHECK, 0, 0);
		} else {
			kGraphics.paintImage (g, 101, 46, WarwickGraphics.QUESTION, 0, 0);
		}
	}
}
