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

import org.jogre.common.Table;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JTextArea;

import org.jogre.client.awt.JogreComponent;

import org.jogre.common.util.GameLabels;

// Car Tricks Game State display
//
// Right now, this component doesn't really draw anything.  It just creates
// a JTextArea and when told to update, it changes the text within that area.
// The CarTricksTableFrame pulls theTextArea out of this and puts it onto the
// main panel and it puts this component into a dummy area of the screen.  This
// is a 1x1 component just so that the paintComponent() method is called.
// I think that this ought to really extend a container and encapsulate the
// JTextArea properly...
public class CarTricksGameStateComponent extends JogreComponent {

	// Link to the model
	private CarTricksClientModel model;
	private Table table;

	// Text Area to hold the text
	public JTextArea theTextArea;

	// The current string in the text area.
	private String currentString;

	/**
	 * Constructor which creates the game state component
	 *
	 * @param model					The game model
	 */
	public CarTricksGameStateComponent (CarTricksClientModel model, Table table) {

		// link to model & table
		this.model = model;
		this.table = table;

		// Create the text area that will hold the "game state" display
		theTextArea = new JTextArea(GameLabels.getInstance().get("gamestate.waiting"));
		theTextArea.setEditable(false);
		theTextArea.setLineWrap(true);
		theTextArea.setWrapStyleWord(true);

		currentString = null;

		// The preferred size of this component is 1x1.
		setPreferredSize(new Dimension (1, 1));
	}

	/**
	 * Update the graphics depending on the model.
	 *
	 * Note: This doesn't actually draw anything.  What it does is check the state
	 * of the model and change the text of the provided Text Area to the new string
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		GameLabels labels = GameLabels.getInstance();

		String newString = labels.get("gamestate.unknown");

		// Given the state of the model, choose the correct string to display
		if (model.isWaitingForPlayers()) {
			newString = labels.get("gamestate.waiting");
		} else if (model.isSettingBid()) {
			newString = labels.get("gamestate.settingBid");
		} else if (model.isSpectatorSettingBid()) {
			newString = labels.get("gamestate.settingBidSpectator");
		} else if (model.hasSetBidPhase()) {
			newString = labels.get("gamestate.submittedBid");
		} else if (model.isSelectingCard()) {
			String activePlayerName = table.getPlayerList().getPlayer(model.getActivePlayerId()).getPlayerName();
			newString = labels.get("gamestate.selecting.0") + " " + activePlayerName +
						" " + labels.get("gamestate.selecting.1");
		} else if (model.isMovingCar()) {
			String movingPlayerName = table.getPlayerList().getPlayer(model.getActivePlayerId()).getPlayerName();
			String movingCarColor = labels.get("gamestate.movingCar." + model.getActiveCar());

			newString = labels.get("gamestate.movingTitle") + "\n" +
						labels.get("gamestate.movingPlayername") + " " + movingPlayerName + "\n" +
						labels.get("gamestate.movingCarTitle") + " " + movingCarColor + "\n" +
						labels.get("gamestate.movingDistance") + " " + model.getSpacesToMove();
		} else if (model.isGameOver()) {
			newString = labels.get("gamestate.gameOver");
		}

		// Display the new string, if is different from what we are currently showing
		if (newString != currentString) {
			theTextArea.replaceRange(newString, 0, theTextArea.getText().length());
			currentString = newString;
		}
	}
}
