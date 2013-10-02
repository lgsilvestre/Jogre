/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.client.awt;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;

import org.jogre.common.User;

/**
 * Declare a constructor for a rating square which can take a rating.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class RatingSquare extends JogrePanel {
	
	private static final int BOX_SIZE  = 8;
	private static Color COLOURS []  =
		{new Color (240, 208, 72),  new Color (180, 180, 180),
		 new Color (184, 141, 44),  new Color (32,  166, 88),
	     new Color (233, 32,  38),	new Color (86,  116, 185)};
	
	private ColouredSquare colouredSquare;
	
	/**
	 * Constructor for a ratings square which takes a colour.
	 * 
	 * @param provisional  Provisional rating.
	 * @param rating       Rating of user.
	 */
	public RatingSquare (User user) { 
		super (new double [][] {{TableLayout.FILL},{TableLayout.FILL}});
		
		setPreferredSize (new Dimension (16, 16));
		
		// Create square
		colouredSquare = new ColouredSquare (BOX_SIZE, BOX_SIZE);
		add (colouredSquare, "0,0,c,c");
		
		refresh(user);
	}
	
	/**
	 * Refresh the boxes.
	 * 
	 * @param provisional  Provisional rating.
	 * @param rating       Rating of user.
	 */
	public void refresh (User user) {
		if (user == null) {
			setVisible(false);
			colouredSquare.setVisible(false);
		}
		else {
			boolean provisional = user.isProvisional();
			int rating = user.getRating();
			
			colouredSquare.setColor (COLOURS [getIndexFromRating(provisional, rating)]);
			setVisible(true);
			colouredSquare.setVisible(true);
		}
	}
	
	/**
	 * Return the index from a rating.
	 * 
	 * @param provisional If profisional is true this overrides the rating.
	 * @param rating      Rating between -1 to 2200 something.
	 * @return            Index between 0 (red) to 5 (gray)
	 */ 
	private int getIndexFromRating (boolean provisional, int rating) {
		if (provisional)
			return 5;
		else if (rating > 2100) 
			return 0;
		else if (rating > 1800) 
			return 1;
		else if (rating > 1500) 
			return 2;
		else if (rating > 1200) 
			return 3;
		else if (rating > 0) 
			return 4;
		
		return 0;
	}
}