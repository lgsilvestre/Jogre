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

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * JOGRE radio button which is transparent.  This may be 
 * customised further in the future.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreRadioButton extends JRadioButton {

	// Convience radio group
	private ButtonGroup group;
	
	/**
	 * Constructor which takes a name and a group.  The default selected
	 * value is false.
	 * 
	 * @param name     Button text.
	 * @param group    Group of button for convience.
	 */
	public JogreRadioButton (String name, ButtonGroup group) {
		this (name, group, false);
	}
	
	/**
	 * Constructor which takes a name, group and a boolean default 
	 * value.
	 * 
	 * @param name       Button text.
	 * @param group      Group of button for convience.
	 * @param selected   Button selected by default if true.
	 */
	public JogreRadioButton (String name, ButtonGroup group, boolean selected) {
		super (name, selected);			// call methods in super class
		
		this.group = group;
		group.add (this);
		setOpaque (false);
	}
}