/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.std;

import org.jogre.client.awt.JogreButton;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * TexasHoldEm button.
 * This is a subclass of Jogre button that can draw it's text in a different
 * font and always fill the space given to it, centering the text in the button.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmButton extends JogreButton {

	private int calcWidth = -1;
	private int calcHeight = -1;

	/**
	 * Constructor which creates the button
	 *
	 */
	public TexasHoldEmButton (String text) {
		super(text);
	}

	/**
	 * Paint the button.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		Rectangle bounds = getBounds();
		if ((calcWidth != bounds.width) || (calcHeight != bounds.height)) {
			// Need to recompute the location of things
			FontMetrics fontMetrics = g.getFontMetrics();
			Rectangle2D textRect = fontMetrics.getStringBounds(text, g);

			// Set width, height and text location for the JogreButton...
			calcWidth = width = bounds.width;
			calcHeight = height = bounds.height;
			textX = (width - (int)textRect.getWidth()) / 2;
			textY = ((height - (int)textRect.getHeight()) / 2) + fontMetrics.getMaxAscent();
		}

		// Now, let the JogreButton paint things.
		super.paintComponent (g);
	}

	/**
	 * Set the Text of the button.
	 *
	 * @param text
	 */
	public void setText(String text) {
		calcWidth = -1;
		super.setText(text);
	}
}
