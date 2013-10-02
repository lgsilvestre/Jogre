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

import java.awt.Color;
import java.awt.Font;

import org.jogre.common.User;

import info.clearthought.layout.TableLayout;

/**
 * Small class which shows a rating square beside a players name.
 */
public class UserLabel extends JogrePanel {
		
	private RatingSquare ratingSquare;
	private JogreLabel   userLabel;
	
	/**
	 * Create a user label with a blank user.
	 */
	public UserLabel () {
		this (new User ("", -1, 0, 0, 0, 0));
	}
	
	/**
	 * Create a new user label where the label size is of type
	 * TableLayout.PREFFERRED.
	 * 
	 * @param user   Link to user.
	 */
	public UserLabel (User user) {
		this (user, TableLayout.PREFERRED);
	}
	
	/**
	 * Create a new user label with a specified label width.
	 * 
	 * @param user        Link to user.
	 * @param labelWidth  Specified label with (using TableLayout).
	 */
	public UserLabel (User user, double labelWidth) {
    	super (new double [][] {{16, labelWidth},{TableLayout.PREFERRED}}); 
    	
    	ratingSquare = new RatingSquare (user);
    	userLabel = new JogreLabel   (user.getUsername());    	
    	setLabelFont (JogreAwt.LIST_FONT);
    	
    	add (ratingSquare, "0, 0");
    	add (userLabel,  "1, 0");
	}
	
	/**
	 * Set the label font
	 * 
	 * @param font
	 */
	public void setLabelFont (Font font) {
		userLabel.setFont (JogreAwt.LIST_FONT);
	}
	
	/**
	 * Set the label colour
	 * 
	 * @param font
	 */
	public void setLabelColour(Color color) {
		userLabel.setForeground(color);
	}
	
	/**
	 * Refresh a player label.
	 */
	public void refresh (User user) {
		ratingSquare.refresh (user);
		if (user != null) 					        
			userLabel.setText (user.getUsername());
		else 
			userLabel.setText("");	
	}
}