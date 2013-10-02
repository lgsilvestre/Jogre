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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

/**
 * Convience class which holds a number of static constants such as fonts etc.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class JogreAwt {

	/**
	 * Font used in the user list, table list and player list boxes.
	 */
	public static final Font LIST_FONT = new Font ("Dialog", Font.PLAIN, 12); 

	/**
	 * Bold font used in the user list, table list and player list boxes.
	 */
	public static final Font LIST_FONT_BOLD = new Font ("Dialog", Font.BOLD, 13);
	
	private static Map gradients = new HashMap ();

	/**
	 * Return the minimum size.
	 * 
	 * @param window   Window e.g. frame that component is called from.
	 * @return         Dimension of size.
	 */
	public static Dimension getMinimumSize (Window window, int defaultWidth, int defaultHeight) {		
		Dimension minSize = window.getMinimumSize();
		Dimension size = new Dimension(
				Math.max((int)minSize.getWidth(),  defaultWidth),
				Math.max((int)minSize.getHeight(), defaultHeight));

		return size;
	}

	/**
	 * Return the location of a centred component on screen.
	 * 
	 * @param window       Window e.g. frame that component is called from.
	 * @param screenSize   Screen size.
	 * @return             Dimenion contain location.
	 */
	public static Point getCentredLocation (Window window, Dimension componentSize) {
		Dimension screenSize = window.getToolkit().getScreenSize();
		Point p = new Point (
				(int)(screenSize.getWidth() / 2 - componentSize.getWidth() / 2), 
				(int)(screenSize.getHeight() / 2 - componentSize.getHeight() / 2));

		return p;
	}

	/**
	 * Draw vertical gradient (top to bottom) start with color1 and ending with color2 inside the
	 * box (x1, y1)-(x2, y2);
	 * 
	 * @param g         Graphics object.
	 * @param x1        Top left X coordinate.
	 * @param y1        Top left Y coordinate.
	 * @param x2        Bottom right X coordinate.
	 * @param y2        Bottom right Y coordinate.
	 * @param color1    Color of gradient at top of box.
	 * @param color2    Color of gradient at bottom of box.
	 */
	public static void drawVerticalGradiant (Graphics g, int x1, int y1, int x2, int y2, Color color1, Color color2) {
		if (color1 != null && color2 != null) {
			float r1 = color1.getRed(), g1 = color1.getGreen(), b1 = color1.getBlue();
			float r2 = color2.getRed(), g2 = color2.getGreen(), b2 = color2.getBlue();
			float r3, g3, b3;

			float h = y2 - y1 - 1;
			float rDiff = (r2 - r1);
			float gDiff = (g2 - g1);
			float bDiff = (b2 - b1);
			for (float i = y1; i < y2; i++) {
				r3 = r1 + (i / h * rDiff);
				g3 = g1 + (i / h * gDiff);
				b3 = b1 + (i / h * bDiff);

				// Cache the gradients to improve performance
				int ir = (int)r3, ig = (int)g3, ib = (int)b3;
				String colorCode = ir + "." + ig + "." + ib;
				Color color = (Color) gradients.get(colorCode);
				if (color == null) {
					color = new Color (ir, ig, ib);
					gradients.put (colorCode, color);
				}

				g.setColor (color);
				g.drawLine (x1, (int)i, x2, (int)i);
			}
		}
	}
}