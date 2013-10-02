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
import org.jogre.common.util.GameProperties;

// Car Tricks visual player hand component (view of the model)
public class CarTricksPlayerHandComponent extends JogreComponent {

	// Declare constants which define what the layout looks like
	private static final int DEFAULT_HAND_CARD_SPACING = 18;
	private static final int DEFAULT_HAND_COLOR_SPACING = 36;
	private static final int DEFAULT_HAND_SELECTED_Y_OFFSET = 20;

	// Link to the model
	private CarTricksClientModel model;

	// Used to decide the size of the hand
	private static final int MAX_CARDS = 20;
	private static final int MAX_COLORS = 6;

	/**
	 * Constructor which creates the hand component
	 *
	 * @param model					The game model
	 */
	public CarTricksPlayerHandComponent (CarTricksClientModel model) {

		// link to model & graphics
		this.model = model;

		// Get properties from the properties file
		GameProperties props = GameProperties.getInstance();
		int handCardSpacing = props.getInt("hand.card.spacing", DEFAULT_HAND_CARD_SPACING);
		int handColorSpacing = props.getInt("hand.color.spacing", DEFAULT_HAND_COLOR_SPACING);
		int handSelectedVerticalOffset = props.getInt("hand.selected.vertical.offset", DEFAULT_HAND_SELECTED_Y_OFFSET);;

		// Set the graphics parameters for the hand in the model
		model.getHand().setGraphicsParams(
			handCardSpacing,
			handColorSpacing,
			handSelectedVerticalOffset
		);

		// Set the prefered size of our component
		CarTricksGraphics CT_graphics = CarTricksGraphics.getInstance();
		Dimension dim = new Dimension (
			(MAX_CARDS - MAX_COLORS) * handCardSpacing +
			(MAX_COLORS - 1) * handColorSpacing +
			CT_graphics.cardWidth,
			handSelectedVerticalOffset + CT_graphics.cardHeight);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		model.getHand().paint(g, 0, 0);
	}

}
