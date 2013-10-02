/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.grandPrixJumping.client;

import org.jogre.common.Table;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JTextArea;

import org.jogre.client.awt.JogreComponent;

import org.jogre.common.util.GameLabels;

/**
 * Game state display component for the Grand Prix Jumping game
 *
 * Right now, this component doesn't really draw anything.  It just creates
 * a JTextArea and when told to update, it changes the text within that area.
 * The JumpingTableFrame pulls theTextArea out of this and puts it onto the
 * main panel and it puts this component into a dummy area of the screen.  This
 * is a 1x1 component just so that the paintComponent() method is called.
 * I think that this ought to really extend a container and encapsulate the
 * JTextArea properly...
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingGameStateComponent extends JogreComponent {

	// Link to the model
	private JumpingClientModel model;
	private Table table;

	// Text Area to hold the text
	public JTextArea theTextArea;

	// The current string in the text area.
	private String currentString;

	// An array of objects that is used for text substitution in the displayed strings
	private Object [] textArgs = new Object [4];
	private Object [] deckSizeArg = new Object [1];

	/**
	 * Constructor which creates the game state component
	 *
	 * @param model					The game model
	 */
	public JumpingGameStateComponent (JumpingClientModel model, Table table) {

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

	private void fillSubstitutionArray() {
		int currPlayerId = model.getCurrentPlayer();
		int chooserId = model.getChooserSeatNum();

		if ((currPlayerId == 0) || (currPlayerId == 1)) {
			try {
				textArgs[0] = table.getPlayerList().getPlayer(currPlayerId).getPlayerName();
				textArgs[1] = table.getPlayerList().getPlayer(1-currPlayerId).getPlayerName();
				textArgs[2] = table.getPlayerList().getPlayer(chooserId).getPlayerName();
				textArgs[3] = table.getPlayerList().getPlayer(1-chooserId).getPlayerName();
			} catch (NullPointerException e) {
				textArgs[0] = "";
				textArgs[1] = "";
				textArgs[2] = "";
				textArgs[3] = "";
			}
		} else {
			textArgs[0] = "";
			textArgs[1] = "";
			textArgs[2] = "";
			textArgs[3] = "";
		}
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

		String keyString = "gamestate.unknown";

		fillSubstitutionArray();

		if (model.isCreatingTrack()) {
			keyString = "gamestate.creatingTrack";
		} else if (model.isSortingCards()) {
			keyString = "gamestate.dividingCards";
		} else if (model.isChoosingSorted()) {
			keyString = "gamestate.choosingSorted";
		} else if (model.isChooserPlayingCards()) {
			keyString = "gamestate.chooserPlaying";
		} else if (model.isSorterPlayingCards()) {
			keyString = "gamestate.dividerPlaying";
		} else if (model.isChooserPlayingImmediate()) {
			keyString = "gamestate.chooserPlayingImm";
		} else if (model.isSorterPlayingImmediate()) {
			keyString = "gamestate.dividerPlayingImm";
		} else if (model.isChooserDiscarding()) {
			keyString = "gamestate.chooserDiscarding";
		} else if (model.isSorterDiscarding()) {
			keyString = "gamestate.dividerDiscarding";
		} else if (model.isGameOver()) {
			keyString = "gamestate.gameOver";
		}

		deckSizeArg[0] = Integer.toString(model.getDeckSize());

		// Get the correct display string, using the current substitutions
		String newString = labels.get("gamestate.deckSize", deckSizeArg) + "\n" +
		                   labels.get(keyString, textArgs);

		// Display the new string, if is different from what we are currently showing
		if (newString != currentString) {
			theTextArea.replaceRange(newString, 0, theTextArea.getText().length());
			currentString = newString;
		}
	}
}
