/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jogre.ninetynine.std.ICardRenderer;

import org.jogre.client.awt.JogreComponent;

// A component that shows the score for a game of NinetyNine
public class NinetyNineScoreComponent extends JogreComponent {

	// The model to get score info from
	private NinetyNineClientModel model;

	// The renderer to use to draw the numbers in the score
	private INumberRenderer numRenderer;

	// The renderer to use to draw the trump suits in the score
	private ICardRenderer suitRenderer;

	// Parameters for drawing the score array
	private int digitSpacing;
	private int HSpacing;
	private	int VSpacing;
	private int halfVSpacing;

	private int numRoundsInGame;
	private int scoreWidth, scoreHeight;
	private int totalScoreWidth;
	private int trumpWidth, trumpHeight;
	private int singleDigitOffset, trumpOffset;

	// Our preferred horizontal size
	private int preferredHSize;

	// Line color is black
	private static final Color lineColor = new Color (0, 0, 0);

	// The colors to use to draw the background for each player's area
	private Color c0, c1, c2;

	/**
	 * Constructor which creates the hand component.
	 * Note: Components start out disabled and must be specifically enabled before
	 *		mouse operations are allowed.
	 *
	 * @param	model			The model to get the score from
	 * @param	numRenderer		The object that knows how to render a number
	 * @param	suitRenderer	The object that knows how to render a suit object
	 * @param	digitSpacing	The spacing between digits of a multi-digit number
	 * @param	HSpacing		The horizontal spacing between columns of scores
	 * @param	VSpacing		The vertical spacing between rows of scores
	 */
	public NinetyNineScoreComponent (
		NinetyNineClientModel model,
		INumberRenderer numRenderer,
		ICardRenderer suitRenderer,
		int digitSpacing,
		int HSpacing,
		int VSpacing)
	{
		// Save parameters
		this.model = model;
		this.numRenderer = numRenderer;
		this.suitRenderer = suitRenderer;
		this.digitSpacing = digitSpacing;
		this.HSpacing = HSpacing;
		this.VSpacing = VSpacing;
		this.halfVSpacing = (VSpacing / 2);

		// Setup colors as if we were player 0
		this.setFirstPlayer(0);

		// We observe the model
		model.addObserver(this);

		// Calculate parameters to use for drawing the score
		int singleDigitWidth = numRenderer.getDigitWidth();

		numRoundsInGame = model.getNumRoundsInGame();
		scoreWidth = singleDigitWidth * 2 + digitSpacing;
		scoreHeight = numRenderer.getDigitHeight();
		totalScoreWidth = singleDigitWidth * 3 + digitSpacing * 2;
		trumpHeight = suitRenderer.getSuitHeight();
		trumpWidth = suitRenderer.getSuitWidth();
		singleDigitOffset = (scoreWidth - singleDigitWidth) / 2;
		trumpOffset = (scoreWidth - trumpWidth) / 2;

		// Set the prefered size of our component
		preferredHSize = (
			HSpacing * 2 +						// Left & Right borders
			numRoundsInGame * scoreWidth + 		// Scores
			numRoundsInGame * HSpacing +		// Between score spaces
			totalScoreWidth						// Total score
		);

		int vsize = (
			VSpacing * 2 +						// Top & Bottom borders
			scoreHeight +						// Round #'s
			VSpacing +							// Spacing between round # and trump
			trumpHeight +						// Trump markers
			3 * scoreHeight +					// Height of scores
			3 * VSpacing						// Between score spaces
		);

		Dimension dim = new Dimension (preferredHSize, vsize);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Set the player number to be at the top of the score chart to the
	 * given player number.  This only affects the color of the lines.
	 *
	 * @param	firstPlayerNum	The seat number of the player to put at the top
	 *							of the score chart.
	 */
	public void setFirstPlayer(int firstPlayerNum) {
		NinetyNineGraphics NN_Graphics = NinetyNineGraphics.getInstance();
		this.c0 = NN_Graphics.getPlayerColor(firstPlayerNum);
		this.c1 = NN_Graphics.getPlayerColor(firstPlayerNum+1);
		this.c2 = NN_Graphics.getPlayerColor(firstPlayerNum+2);
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		int x, y;
		int roundNum = model.getCurrentRoundNumber();

		// We want to center horizontally
		Rectangle bounds = getBounds();
		int xoff = (bounds.width - preferredHSize) / 2;

		// Calculate some screen coordinates
		int xLeft = xoff + (HSpacing / 2);
		int xRight = xLeft + (scoreWidth + HSpacing) * numRoundsInGame + totalScoreWidth + HSpacing;
		int yTop = halfVSpacing;
		int yBot = (bounds.height - halfVSpacing) - 1;

		// Draw the colored bands for each of the players
		// (This is done first so that the black lines can be drawn overtop the
		// colored bands.)
		y = (VSpacing * 2) + scoreHeight + trumpHeight + halfVSpacing;
		int y2 = y  + (scoreHeight + VSpacing);
		int y3 = y2 + (scoreHeight + VSpacing);
		g.setColor(c0);
		g.fillRect(xLeft, y , (xRight - xLeft), (scoreHeight + VSpacing));
		g.setColor(c1);
		g.fillRect(xLeft, y2, (xRight - xLeft), (scoreHeight + VSpacing));
		g.setColor(c2);
		g.fillRect(xLeft, y3, (xRight - xLeft), (scoreHeight + VSpacing));

		// Draw the vertical lines for the grid
		g.setColor(lineColor);
		x = xLeft;
		for (int i=0; i<=numRoundsInGame; i++) {
			g.drawLine(x, yTop, x, yBot);
			x += (scoreWidth + HSpacing);
		}
		g.drawLine(xRight, yTop, xRight, yBot);

		// Draw the top horizontal line
		g.drawLine(xLeft, halfVSpacing, xRight, halfVSpacing);

		// Draw the row of round numbers
		x = xoff + HSpacing + singleDigitOffset; 		// Center the digit in the space alloted
		y = VSpacing;
		for (int i=1; i<=numRoundsInGame; i++) {
			numRenderer.paintSingleDigit(g, x, y, i);
			x += (scoreWidth + HSpacing);
		}

		// Draw the horizontal line below the round numbers
		y += scoreHeight;
		g.drawLine(xLeft, y + halfVSpacing, xRight, y + halfVSpacing);

		// Draw the trump symbols for rounds up to this one
		x = xoff + HSpacing + trumpOffset;				// Center the trump symbol in the space alloted
		y += VSpacing;
		if (!model.isPreStart()) {
			for (int i=0; i<=roundNum; i++) {
				suitRenderer.paintSuitMarker(g, x, y, model.getTrumpForRound(i));
				x += (scoreWidth + HSpacing);
			}
		}

		// Draw the horizontal line below the trump symbols
		g.setColor(lineColor);
		y += trumpHeight;
		g.drawLine(xLeft, y + halfVSpacing, xRight, y + halfVSpacing);

		// Draw the player scores for rounds up to this one
		y += VSpacing;
		y2 = y  + (scoreHeight + VSpacing);
		y3 = y2 + (scoreHeight + VSpacing);
		if (roundNum > 0) {
			x = xoff + HSpacing;
			for (int i=0; i<roundNum; i++) {
				numRenderer.paintDoubleDigit(g, x, y,  model.getScoreForRound(0, i), digitSpacing);
				numRenderer.paintDoubleDigit(g, x, y2, model.getScoreForRound(1, i), digitSpacing);
				numRenderer.paintDoubleDigit(g, x, y3, model.getScoreForRound(2, i), digitSpacing);
				x += (scoreWidth + HSpacing);
			}
		}

		// Draw the current total scores
		x = xoff + HSpacing + (scoreWidth + HSpacing) * numRoundsInGame;
		numRenderer.paintTripleDigit(g, x, y,  model.getTotalScore(0), digitSpacing);
		numRenderer.paintTripleDigit(g, x, y2, model.getTotalScore(1), digitSpacing);
		numRenderer.paintTripleDigit(g, x, y3, model.getTotalScore(2), digitSpacing);

		// Draw the horizontal lines below the player scores
		y += scoreHeight;
		y2 += scoreHeight;
		y3 += scoreHeight;
		g.drawLine(xLeft, y  + halfVSpacing, xRight, y  + halfVSpacing);
		g.drawLine(xLeft, y2 + halfVSpacing, xRight, y2 + halfVSpacing);
		g.drawLine(xLeft, y3 + halfVSpacing, xRight, y3 + halfVSpacing);
		
	}

}
