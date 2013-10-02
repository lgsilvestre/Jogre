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
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * Small coloured square class which is used extensively in JOGRE
 * to denote table visiblity, player rating, etc.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class ColouredSquare extends JComponent {

	private static final Color DEFAULT_COLOUR = Color.white;
	
	private Color color;
	
	private boolean border;
	
	/**
	 * Very simple constructor that takes a colour.
	 * 
	 * @param color  Color of box.
	 */
	public ColouredSquare (Color color) {
		this (color, -1, -1, false);
	}
	
	/**
	 * Constructor which takes a colour, a width and a height.
	 * 
	 * @param color
	 * @param width
	 * @param height
	 */
	public ColouredSquare (Color color, int width, int height) {
		this (color, width, height, false);
	}
	
	/**
	 * Constructor which takes width / height and uses default coloured square. 
	 * 
	 * No border and no hover on.
	 * 
	 * @param width
	 * @param height
	 */
	public ColouredSquare (int width, int height) {
		this (DEFAULT_COLOUR, width, height, false);
	}
	
	/**
	 * Constructor which takes a colour, a width and a height.
	 * 
	 * @param color
	 * @param width
	 * @param height
	 * @param border
	 */
	public ColouredSquare (Color color, int width, int height, boolean border) {
		this.color = color;
		this.border = border;
		
		if (width != -1 && height != -1) 
			setPreferredSize (new Dimension (width, height));
		
		repaint ();
	}	
	
	/**
	 * Override the paint component to set the colour of the box.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		g.setColor (color);
		g.fillRect (0, 0, getWidth(), getHeight());
		
		if (border) {
			g.setColor (Color.black);			
			g.drawRect (0, 0, getWidth() - 1, getHeight() - 1);
			g.setColor (Color.white);			
			g.drawRect (1, 1, getWidth() - 3, getHeight() - 3);
		}
	}
	
	/**
	 * Update the colour of the component.
	 * 
	 * @param color
	 */
	public void setColor (Color color) {
		this.color = color;
		repaint();
	}
	
	/**
	 * Method to turn the border enabling on / off.
	 * 
	 * @param enableBorder   If true draws a border.
	 */
	public void enableBorder (boolean border) {
		this.border = border;
	}
}
