/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
 * http//jogre.sourceforge.org
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
package org.jogre.carTricks.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jogre.client.awt.JogreComponent;

// The component will draw a single line
public class LineComponent extends JogreComponent {

	// Parameters of the line to draw
	private Color lineColor;
	private BasicStroke lineStroke;
	private int inset;

	// Direction of line to draw
	private boolean drawHorizontal;

	// Default color is black
	private static final Color defaultColor = new Color (0, 0, 0);

	/**
	 * Constructor which creates the Line component
	 *
	 * @param	horiz			If true, line is horizontal.
	 *							If false, line is vertical.
	 * @param	lineColor		The color of the line
	 * @param	lineThickness	The thickness of the line, in pixels
	 */
	public LineComponent (boolean horiz, Color lineColor, int lineThickness, int inset) {

		this.drawHorizontal = horiz;
		this.lineColor = lineColor;
		this.lineStroke = new BasicStroke(lineThickness);
		this.inset = inset;

		Dimension dim = horiz
							? new Dimension (1, lineThickness)
							: new Dimension (lineThickness, 1);

		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Constructors that use default values.
	 */
	public LineComponent (boolean horiz) {
		this (horiz, defaultColor, 1, 0);
	}

	public LineComponent (boolean horiz, Color lineColor) {
		this (horiz, lineColor, 1, 0);
	}

	public LineComponent (boolean horiz, int lineThickness) {
		this (horiz, defaultColor, lineThickness, 0);
	}

	public LineComponent (boolean horiz, int lineThickness, int inset) {
		this (horiz, defaultColor, lineThickness, inset);
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g			The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle rect = getBounds();

		g.setColor(lineColor);
		g2d.setStroke(lineStroke);

		if (drawHorizontal) {
			int y = (rect.height / 2 );
			g.drawLine(inset, y, rect.width-inset, y);
		} else {
			int x = (rect.width / 2);
			g.drawLine(x, inset, x, rect.height-inset);
		}

	}
}
