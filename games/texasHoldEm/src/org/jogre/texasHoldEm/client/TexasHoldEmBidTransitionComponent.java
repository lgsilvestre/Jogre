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
package org.jogre.texasHoldEm.client;

import org.jogre.client.awt.JogreComponent;

import org.jogre.common.util.GameLabels;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;

/**
 * TexasHoldEm Bid Transition component.
 * This displays the time remaining until the next change to the blind schedule
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmBidTransitionComponent extends JogreComponent {

	// Link to the model, graphics helper & text labels
	TexasHoldEmClientModel model;
	TexasHoldEmGraphics thGraphics;
	GameLabels labels;

	// Metrics for the game font.
	// (The font is created and kept in TexasHoldEmGraphics.java)
	FontMetrics gameFontMetrics;

	// Strings to display (read from game_labels.properties)
	private String titleText;
	private String changeLessMinuteText;
	private String changeNeverText;

	// Object array used for substituting the time into the standard text.
	Object [] textArgs = new Object [1];

	// The actual string to display.
	private String changeText;

	// The anchor point for the title & text
	private Point titleAnchor;
	private Point textAnchor;

	// Values to remember the currently displayed data so we only need to
	// update the strings when they change.
	private int displayedMinutesToGo = -2;
	private int displayedBlindStage = -1;

	// Parameters for drawing the table
	private static final int TEXT_BORDER = 2;
	private static final int LINE_SPACING = 4;
	private static final int TEXT_INSET = 20;

	// Timer used to update the display.
	private Timer transitionTimer;

	/**
	 * Constructor which creates the component
	 *
	 * @param model					The game model
	 */
	public TexasHoldEmBidTransitionComponent (TexasHoldEmClientModel model) {

		// Save parameters provided
		this.model = model;
		thGraphics = TexasHoldEmGraphics.getInstance();
		labels = GameLabels.getInstance();

		// Get the Font Metrics for the font we're using
		gameFontMetrics = getFontMetrics(thGraphics.gameFont);

		// Set the size of the component
		setPreferredSize ( calcTextData() );

		// Timer to update the text display - Update every 15 seconds
		transitionTimer = new Timer (15000, new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				repaint();
			}
		});
		transitionTimer.start();
	}

	/*
	 * This will calculate the dimensions of the table component.
	 */
	private Dimension calcTextData() {
		// Get the text strings from the labels.
		titleText = labels.get("blind.change.title");
		changeLessMinuteText = labels.get("blind.change.less1min");
		changeNeverText      = labels.get("blind.change.never");

		// Compute the width of the text to determine the max size of the component.

		// Start with the title
		int width = gameFontMetrics.stringWidth(titleText) - TEXT_INSET;

		// Try all times from 0 to 60 minutes...
		for (int i=0; i<=60; i++) {
			textArgs[0] = new Integer(i);
			changeText = labels.get("blind.change.standard", textArgs);
			width = Math.max(width, gameFontMetrics.stringWidth(changeText));
		}

		// Check the other two strings.
		width = Math.max(width, gameFontMetrics.stringWidth(changeLessMinuteText));
		width = Math.max(width, gameFontMetrics.stringWidth(changeNeverText));

		// Add the border & insert
		width += (TEXT_INSET + TEXT_BORDER * 2);


		// Now, compute the height
		int lineSpacing = gameFontMetrics.getMaxAscent() +
		                  gameFontMetrics.getMaxDescent() +
		                  LINE_SPACING;
		int height = lineSpacing * 2 - LINE_SPACING + TEXT_BORDER * 2;

		// Place the text strings
		titleAnchor = new Point (TEXT_BORDER, TEXT_BORDER + gameFontMetrics.getMaxAscent());
		textAnchor = new Point (titleAnchor.x + TEXT_INSET, titleAnchor.y + lineSpacing);

		// Return the dimension of the component
		return new Dimension (width, height);
	}

	/**
	 * Paint the component
	 *
	 * @param g		The graphics context to draw on.
	 */
	public void paintComponent (Graphics g) {
		// Set the font of the graphics context to the game font.
		thGraphics.setGameFont(g);

		// Update the change text to what it should be now.
		updateChangeText();

		// Draw the text
		g.setColor(thGraphics.blackColor);
		thGraphics.drawLeftJustifiedText(g, titleText, titleAnchor);
		thGraphics.drawLeftJustifiedText(g, changeText, textAnchor);
	}

	/*
	 * Change the changeText to display the correct string to indicate the
	 * time remaining until the next change to blind schedule.
	 */
	private void updateChangeText() {
		int minutesToGo = model.minutesToNextBlindChange();

		if (minutesToGo == displayedMinutesToGo) {
			// There has been no change, so display the current string
			return;
		}

		if (minutesToGo < 0) {
			// We've got infinite time until change, so use the infinite string
			displayedMinutesToGo = minutesToGo;
			changeText = changeNeverText;
			return;
		} else if (minutesToGo == 0) {
			// We've got less than 1 minute until time change.
			displayedMinutesToGo = 0;
			changeText = changeLessMinuteText;
			return;
		}

		if (minutesToGo > displayedMinutesToGo) {
			// Only change the text time upwards if we've also changed blind stages.
			int currentStage = model.getCurrentBlindScheduleStage();
			if (currentStage == displayedBlindStage) {
				displayedMinutesToGo = 0;
				changeText = changeLessMinuteText;
				return;
			}

			// We're now displaying the current stage.
			displayedBlindStage = currentStage;
		}

		displayedMinutesToGo = minutesToGo;

		textArgs[0] = new Integer(minutesToGo);
		changeText = labels.get("blind.change.standard", textArgs);
	}
}
