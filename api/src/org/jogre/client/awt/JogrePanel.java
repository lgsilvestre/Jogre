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
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * Jogre transparenty panel.  This class simply extends a JPanel
 * and ensures that its transparent.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogrePanel extends JPanel {

	// Gradiant directions
	public static final int UP = 1;
	public static final int DOWN = 2;

	// Gradient colours
	public Color color1 = null, color2 = null;
	
	/**
	 * Constructor with no parameters.
	 */
	public JogrePanel () {
		setOpaque (false);		// transparent
	}
	
	/**
	 * Convenience constructor which takes a table layout size.
	 * 
	 * @param layout    Layout manager.
	 */
	public JogrePanel (double [][] sizes) {
		this ();
		
		setLayout (sizes);
	}
	
	/**
	 * Convenience constructor which takes a BorderLayout.
	 * 
	 * @param borderLayout
	 */
	public JogrePanel (LayoutManager layout) {
		this ();
		
		setLayout (layout);
	}
	
	
	/**
	 * Constructor which takes TableLayout sizes and 2 gradient colours.
	 * 
	 * Gradients current go from top of panel to bottom although this class can be easily
	 * upgrade to support different gradient directions if this is required.
	 * 
	 * @param color1    Gradient 1 
	 * @param color2    Gradient 2
	 */
	public JogrePanel (Color color1, Color color2) {
		this (null, color1, color2);
	}
	
	/**
	 * Constructor which takes a Table
	 * 
	 * @param sizes
	 * @param color1
	 * @param color2
	 */
	public JogrePanel (double [][] sizes, Color color1, Color color2) {
		if (sizes != null)
			setLayout(sizes);
		this.color1 = color1;
		this.color2 = color2;
	}
	
	/**
	 * Set layout to be TableLayout.
	 * 
	 * @param sizes
	 */
	public void setLayout(double[][] sizes) {
		setLayout (new TableLayout (sizes));
	}
	
	/**
	 * Override the paint component method e.g. for gradients etc.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		if (color1 != null && color2 != null) {
			// Draw gradient
			JogreAwt.drawVerticalGradiant(g, 0, 0, getWidth(), getHeight(), color1, color2);
		} else {
			// Call the standard paint to draw non-gradients
			super.paintComponent (g);
		}
	}
}