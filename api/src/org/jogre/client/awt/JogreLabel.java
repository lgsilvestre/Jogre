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

import javax.swing.JLabel;

/**
 * Simple extension of a label.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreLabel extends JLabel {

	private static final Color DEFAULT_COLOUR = Color.black;

	/**
	 * Constructor for creating a JogreLabel.
	 */
	public JogreLabel () {
		super ();
		setOpaque (false);
	}	
	
	/**
	 * Constructor for creating a JogreLabel.
	 */
	public JogreLabel (String text) {
		super (text);
		setOpaque (false);
	}

	/**
	 * Constructor which sets a Font as well.
	 * 
	 * @param text
	 * @param style
	 * @param size
	 */
	public JogreLabel (String text, char style, int size) {
		this (text, style, size, DEFAULT_COLOUR);
	}
	
	/**
	 * Constructor which sets a Font as well.
	 * 
	 * @param text
	 * @param style
	 * @param size
	 */
	public JogreLabel (String text, char style, int size, Color color) {
		this (text);
		int iStyle = 0;
		if (style == 'P' || style == 'p') iStyle = Font.PLAIN;
		else if (style == 'B' || style == 'b') iStyle = Font.BOLD;
		else if (style == 'I' || style == 'i') iStyle = Font.ITALIC;
		setFont(new Font ("Dialog", iStyle, size));
		setForeground(color);
	}	
}
