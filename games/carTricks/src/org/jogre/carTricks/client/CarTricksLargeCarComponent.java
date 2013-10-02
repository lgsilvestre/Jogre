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

import java.awt.Dimension;
import java.awt.Graphics;

import org.jogre.client.awt.JogreComponent;

// Car Tricks visual component (view of the model)
// This shows a single large car image
public class CarTricksLargeCarComponent extends JogreComponent {

	// Link to the graphics helper
	private CarTricksGraphics CT_graphics;

	// The color of the car to display
	private int carColor;

	// The width & height to the center of a large car picture
	private int hcenter, vcenter;

	/**
	 * Constructor which creates the Large Car component
	 *
	 * @param	carColor		The color of the car image to display
	 */
	public CarTricksLargeCarComponent ( int carColor ) {
		this.CT_graphics = CarTricksGraphics.getInstance();
		this.carColor = carColor;

		hcenter = (CT_graphics.largeCarWidth/2);
		vcenter = (CT_graphics.largeCarHeight/2);

		// Compute the size of the component
		Dimension dim = new Dimension (CT_graphics.largeCarWidth, CT_graphics.largeCarHeight);

		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g			The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		CT_graphics.paintLargeCar(g, hcenter, vcenter, carColor);
	}
}
