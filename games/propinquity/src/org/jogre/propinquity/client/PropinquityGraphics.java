/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;


/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Class for doing some cool grahical things such as drawing hexagons and text
 * with shadows etc.
 */
public class PropinquityGraphics {

	/** Constructor */
	public PropinquityGraphics() {}

	/**
	 * @param size
	 * @return
	 */
	public static Polygon createHexagon (int size) {
		int [] x = {size / 2, size, size, size / 2, 0, 0};
		int [] y = {0, size / 4, size / 4 * 3, size - 2, size / 4 * 3, size / 4};

		return new Polygon (x, y, 6);
	}

	/**
	 * Draw text on the screen with a 2 border pixel shadow.
	 *
	 * @param g
	 * @param text
	 * @param textColour
	 * @param shadowColour
	 * @param x
	 * @param y
	 */
	public static void drawTextShadow (Graphics g, String text, Color textColour, Color shadowColour, int x, int y) {
		// Draw the shadows
		g.setColor (shadowColour);
		g.drawString (text, x + 2, y + 2);

		// Draw the text
		g.setColor (textColour);
		g.drawString (text, x, y);
	}

	/**
	 * Convience method for drawing a hexagon of varying sizes and optional emboss.
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param size
	 * @param color
	 */
	public static void drawHexagon (Graphics g, int x, int y, int size, boolean emboss, Color colour) {
		Polygon hexagon = PropinquityGraphics.createHexagon (size);
		hexagon.translate (x, y);

		g.setColor (colour);
		g.fillPolygon (hexagon);

		// Draw embross lines
		if (emboss) {
			int add = 64;
			int r1 = colour.getRed () + add < 256 ? colour.getRed () + add : 255;
			int g1 = colour.getGreen () + add < 256 ? colour.getGreen () + add : 255;
			int b1 = colour.getBlue () + add < 256 ? colour.getBlue () + add : 255;
			g.setColor (new Color (r1, g1, b1));
			g.drawLine(hexagon.xpoints[0], hexagon.ypoints[0] + 1, hexagon.xpoints[1] - 1, hexagon.ypoints[1] + 1);
			g.drawLine(hexagon.xpoints[1] - 1, hexagon.ypoints[1] + 1, hexagon.xpoints[2] - 1, hexagon.ypoints[2] - 1);
			g.drawLine(hexagon.xpoints[2] - 1, hexagon.ypoints[2] - 1, hexagon.xpoints[3] - 1, hexagon.ypoints[3]);

			int r2 = colour.getRed () - add >= 0 ? colour.getRed () - add : 0;
			int g2 = colour.getGreen () - add >= 0 ? colour.getGreen () - add : 0;
			int b2 = colour.getBlue () - add >= 0 ? colour.getBlue () - add : 0;
			g.setColor (new Color (r2, g2, b2));
			g.drawLine(hexagon.xpoints[3], hexagon.ypoints[3] - 1, hexagon.xpoints[4], hexagon.ypoints[4] - 1);
			g.drawLine(hexagon.xpoints[4] + 1, hexagon.ypoints[4] - 1, hexagon.xpoints[5] + 1, hexagon.ypoints[5] + 1);
			g.drawLine(hexagon.xpoints[5] + 1, hexagon.ypoints[5] + 1, hexagon.xpoints[0], hexagon.ypoints[0] + 1);
		}

		g.setColor (PropinquityLookAndFeel.LINE_COLOUR);
		g.drawPolygon (hexagon);
	}

	/**
	 * Convience method for drawing a hexagon of varying sizes with text
	 * centered inside.
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param size
	 * @param colour
	 * @param fontMetrics
	 * @param num
	 */
	public static void drawHexagonWithText (Graphics g, int x, int y, int size, boolean emboss, Color colour, int num) {
		// Draw the hexagon
		drawHexagon (g, x, y, size, emboss, colour);

		// Draw the text using the drawShadow method.
		String numStr = String.valueOf (num);
		int textWidthPixels = g.getFontMetrics().stringWidth(numStr);

		// And draw the text using the default colours
		int textX = x + (size / 2) - (textWidthPixels / 2);
		int textY = y + (size / 2) + (g.getFontMetrics().getAscent() / 3);

		drawTextShadow (
			g,											// Graphics object
			numStr, 									// Number
			PropinquityLookAndFeel.TEXT_COLOUR,			// Text colour
			PropinquityLookAndFeel.TEXT_SHADOW_COLOUR,	// Shadow colour
			textX, 										// x position
			textY										// y position
		);
	}
}