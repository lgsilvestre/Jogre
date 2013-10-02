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
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;

import org.jogre.client.awt.JogreComponent;

import org.jogre.carTricks.common.CarTricksCard;
import org.jogre.carTricks.common.CarTricksTrackDB;

// Car Tricks visual component (view of the model)
// This shows the card that someone has played
public class CarTricksPlayedCardComponent extends JogreComponent {

	// Declare constants which define what the layout looks like.
	private static final int OUTSIDE_SPACING = 1;
	private static final int INSIDE_SPACING = 4;

	// Link to the model & graphics
	private CarTricksClientModel model;
	private CarTricksGraphics CT_graphics;

	// Which player id we're displaying
	private int myPlayer;

	// Array used when drawing the small special card markers
	private int [] sm_x = new int [4];
	private int [] sm_y = new int [4];
	private int lrg_x, lrg_y;

	// Keep track of the small event card that we're currently selecting
	private int selectedIndex;

	// Keep track of badges to show
	private int carBadgeX, carBadgeY;
	private int wheelBadgeX, wheelBadgeY;
	private int checkBadgeX, checkBadgeY;

	// Indicates if we're supposed to display the card prompt or not.
	// For now, card prompts are always enabled.
	private boolean cardPromptEnabled = true;


	/**
	 * Constructor which creates the opponent played component
	 *
	 * @param model					The game model
	 * @param whichPlayer			The player number (seat number) of the player
	 *								whose card I am showing.
	 */
	public CarTricksPlayedCardComponent (CarTricksClientModel model,
										 int whichPlayer) {
		Dimension dim;
		int h;

		// link to model & graphics
		this.model = model;
		this.CT_graphics = CarTricksGraphics.getInstance();

		this.myPlayer = whichPlayer;
		this.selectedIndex = -1;

		// Calculate dimensions of the component
		int w = (CT_graphics.cardHeight + 2*OUTSIDE_SPACING + 2*INSIDE_SPACING + 2*CT_graphics.smallWidth);
		int h1 = (CT_graphics.cardHeight + 2*OUTSIDE_SPACING);
		int h2 = (CT_graphics.smallHeight*2 + 2*OUTSIDE_SPACING + INSIDE_SPACING);

		if (h1 > h2) {
			// Component height is set by height of big card
			h = h1;
			dim = new Dimension (w, h1);
			this.sm_y[0] = OUTSIDE_SPACING + ((CT_graphics.cardHeight - INSIDE_SPACING - CT_graphics.smallHeight*2)/2);
			lrg_y = OUTSIDE_SPACING;

		} else {
			// Component height is set by height of small cards
			h = h2;
			dim = new Dimension (w, h2);
			this.sm_y[0] = OUTSIDE_SPACING;
			lrg_y = OUTSIDE_SPACING + (h2-CT_graphics.cardHeight)/2;
		}

		// Place the items inside the component
		lrg_x = OUTSIDE_SPACING + ((CT_graphics.cardHeight - CT_graphics.cardWidth) / 2);

		this.sm_x[0] = OUTSIDE_SPACING + CT_graphics.cardHeight + INSIDE_SPACING;
		this.sm_x[1] = this.sm_x[0] + INSIDE_SPACING + CT_graphics.smallWidth;
		this.sm_x[2] = this.sm_x[0];
		this.sm_x[3] = this.sm_x[1];

		this.sm_y[1] = this.sm_y[0];
		this.sm_y[2] = this.sm_y[0] + INSIDE_SPACING + CT_graphics.smallHeight;
		this.sm_y[3] = this.sm_y[2];

		carBadgeX = CT_graphics.carWidth / 2;
		carBadgeY = CT_graphics.carHeight / 2;
		wheelBadgeX = 0;
		wheelBadgeY = h/2 - CT_graphics.steeringHeight/2;
		checkBadgeX = 0;
		checkBadgeY = h - CT_graphics.checkHeight;

		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Change the player number of who I should be showing
	 *
	 * @param whichPlayer		The player number (seat number) of the player
	 *							whose card I am showing.
	 */
	public void setWhichPlayer(int whichPlayer) {
		this.myPlayer = whichPlayer;
	}

	/**
	 * Set the value of whether or not to display the card prompt.
	 *
	 * @param	value			true => enable prompt.
	 *							fales => disable prompt.
	 */
	public void setCardPromptEnable(boolean value) {
		this.cardPromptEnabled = value;
	}

	/**
	 * Given a graphical (x,y) position, select the event card that matches the
	 * small event symbol at that (x,y) location.
	 *
	 * @param (x,y)				The graphical position to select at.
	 * @return					True  => the newly selected card is different than what it used to be.
	 *							False => the newly selected card is the same as the card that was selected.
	 */
	public boolean selectEventCardAt(int x, int y) {
		int newIndex = -1;

		// Look to see which of the 4 small symbols the mouse is in.
		for (int i=0; i < 4; i++) {
			if ((x > sm_x[i]) && (x < (sm_x[i] + CT_graphics.smallWidth)) &&
				(y > sm_y[i]) && (y < (sm_y[i] + CT_graphics.smallHeight))) {
				newIndex = i;
			}
		}

		// Determine if we're allowed to select the event card that the mouse is in.
		if (newIndex != -1) {
			if (model.eventCardState(myPlayer, newIndex) != model.EVENT_AVAILABLE) {
				// Nope, not allowed to select this card
				newIndex = -1;
			}
		}

		// Determine if a new item is selected or not...
		if (newIndex == selectedIndex) {
			return false;
		} else {
			selectedIndex = newIndex;
			return true;
		}
	}

	/**
	 * Unselect the currently selected card.
	 *
	 * @returns				True  => There was a previously selected card that is being deselected.
	 *						False => There was no previously selected card.
	 */
	public boolean unSelectCard() {
		if (selectedIndex == -1) {
			return false;
		} else {
			selectedIndex = -1;
			return true;
		}
	}

	/**
	 * Return the currently selected card.
	 *
	 * @return the currently selected card.
	 */
	public CarTricksCard getSelectedCard() {
		if (selectedIndex < 0) {
			return new CarTricksCard();
		} else {
			return new CarTricksCard(CarTricksCard.EVENT, selectedIndex + CarTricksCard.MIN_VALUE);
		}
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g			The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {

		// If one of the small event symbols is selected, then paint that large card
		if (selectedIndex != -1) {
			CT_graphics.paintCard(g, lrg_x, lrg_y, getSelectedCard());
		} else {
			// If one of the small event symbols is not selected, then draw the card
			// that the player has played (if there is one...)
			CarTricksCard playedCard = model.getPlayedCard(myPlayer);
			CT_graphics.paintCard(g, lrg_x, lrg_y, playedCard);

			// If we're configured for card promts, it's our turn and we haven't
			// chosen anything, then display the card prompt.
			if ( cardPromptEnabled &&
			    !playedCard.isVisible() &&
			    (myPlayer == model.getActivePlayerId())) {
					CT_graphics.paintCardPrompt(g, lrg_x, lrg_y);
			}
		}

		// Paint the small event symbols
		for (int i=0; i<4; i++) {
			int show = model.eventCardState(myPlayer, i);
			if (show != model.EVENT_ALREADY_PLAYED) {
				CT_graphics.paintSmallEvent(g,
					sm_x[i], sm_y[i],
					i,
					(show == model.EVENT_AVAILABLE));
			}
		}

		// Paint any badges that are attached to the area.
		if (model.choseActiveCar(myPlayer)) {
			int activeCar = model.getActiveCar();
			int [] car_locs = model.getCarLocations();
			CarTricksTrackDB trackDB = model.getTrackDatabase();
			int [] graph_info = trackDB.getGraphicalArrayForSpace(car_locs[activeCar]);

			CT_graphics.paintCar(g,
				carBadgeX, carBadgeY,
				activeCar,
				graph_info[CarTricksTrackDB.GRAPH_ROT]);
		}

		if (model.willDriveCar(myPlayer)) {
			CT_graphics.paintSteeringBadge(g, wheelBadgeX, wheelBadgeY);
		}

		if (model.willLeadNext(myPlayer)) {
			CT_graphics.paintCheckBadge(g, checkBadgeX, checkBadgeY);
		}

	}

}
