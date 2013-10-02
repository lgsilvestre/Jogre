/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.border.Border;
import javax.swing.BorderFactory;

import org.jogre.abstrac.std.ICardRenderer;
import org.jogre.abstrac.std.Hand;
import org.jogre.abstrac.std.Card;

import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.util.GameProperties;


/*
	A component that shows the cards taken for a game of Abstrac
*/
public class AbstracTakenComponent extends JogreComponent {

	private static final Color GRADIANT_COLOR_TOP = Color.white;
	private static final Color GRADIANT_COLOR_BOTTOM = new Color (230, 230, 230);
	private static final int BORDER_WIDTH = 2;
	
	// The model to get taken info from
	private AbstracModel model;

	// The renderer to use to draw the numbers
	private INumberRenderer numRenderer;

	// The renderer to use to draw the trump suits
	private ICardRenderer suitRenderer;

	// The player whose status this component is to show
	private int playerId;

	// Parameters for drawing the array
	private int digitSpacing;
	private int HSpacing;
	private int VSpacing;
	private int halfHSpacing;
	private int halfVSpacing;

	private int singleDigitWidth, doubleDigitWidth, tripleDigitWidth;
	private int suitHeight, suitWidth;

	// Our preferred size
	private int preferredHSize, preferredVSize;

	// The hand component that we should use to display selected cards
	private AbstracHandComponent theHandComponent = null;

	// The borders to use for different players
	private Border [] borders = new Border [2];

	/**
	 * Constructor which creates the taken component.
	 *
	 * @param	model			The model to get the info from
	 * @param	playerId		The Id of the player that I am to show
	 * @param	numRenderer		The object that knows how to render a number
	 * @param	suitRenderer	The object that knows how to render a suit object
	 * @param	digitSpacing	The spacing between digits of a multi-digit number
	 * @param	HSpacing		The horizontal spacing between columns
	 * @param	VSpacing		The vertical spacing between rows
	 */
	public AbstracTakenComponent (
		AbstracModel model,
		int playerId,
		INumberRenderer numRenderer,
		ICardRenderer suitRenderer,
		int digitSpacing,
		int HSpacing,
		int VSpacing)
	{
		// Save parameters
		this.model = model;
		this.playerId = playerId;
		this.numRenderer = numRenderer;
		this.suitRenderer = suitRenderer;
		this.digitSpacing = digitSpacing;
		this.HSpacing = HSpacing;
		this.VSpacing = VSpacing;
		this.halfVSpacing = (VSpacing / 2);
		this.halfHSpacing = (HSpacing / 2);

		// We observe the model
		model.addObserver(this);

		// Calculate parameters to use for drawing the table
		singleDigitWidth = numRenderer.getDigitWidth();
		doubleDigitWidth = (singleDigitWidth * 2) + digitSpacing;
		tripleDigitWidth = doubleDigitWidth + singleDigitWidth + digitSpacing;

		suitHeight = suitRenderer.getSuitHeight();
		suitWidth = suitRenderer.getSuitWidth();

		// Set the prefered size of our component
		preferredHSize = (
			HSpacing * 13 +
			suitWidth * 7 +
			singleDigitWidth * 2 +
			doubleDigitWidth * 2 +
			tripleDigitWidth
		);

		preferredVSize = (
			VSpacing * 7 +
			suitHeight * 5 +
			numRenderer.getDigitHeight()
		);

		Dimension dim = new Dimension (preferredHSize, preferredVSize);
		setPreferredSize(dim);
		setMinimumSize(dim);

		borders[0] = BorderFactory.createLineBorder(GameProperties.getPlayerColour(0), BORDER_WIDTH);
		borders[1] = BorderFactory.createLineBorder(GameProperties.getPlayerColour(1), BORDER_WIDTH);
		setBorder(borders[0]);
	}

	/**
	 * Set the player number that we should be drawing to the given
	 * given player number.
	 *
	 * @param	playerId		The seat number of the player to show.
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
		setBorder(borders[playerId]);
		this.repaint();
	}

	/**
	 * Set the Hand component that we should be watching to display selected cards.
	 *
	 * @param	theHandComponent	The hand component that we are to watch.
	 */
	public void setHandComponent(AbstracHandComponent theHandComponent) {
		this.theHandComponent = theHandComponent;
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		int x, y;
		int code;

		AbstracGraphics AB_graphics = AbstracGraphics.getInstance();

		// We want to center horizontally
		Rectangle bounds = getBounds();
		int xoff = (bounds.width - preferredHSize) / 2;

		// Draw the background with a vertical gradiant
		JogreAwt.drawVerticalGradiant(g, 0, 0, getWidth(), getHeight(),
		                              GRADIANT_COLOR_TOP, GRADIANT_COLOR_BOTTOM);

		// Draw the grid
		x = xoff + HSpacing + suitWidth + halfHSpacing;
		y = VSpacing + suitHeight + halfVSpacing;
		g.setColor(Color.black);
		for (int i=0; i<7; i++) {
			int lx = x + (HSpacing + suitWidth) * i;
			int ly = y + (VSpacing + suitHeight) * i;
			g.drawLine(lx, y, lx, y + 4 * (VSpacing + suitWidth));
			if (i < 5) {
				g.drawLine(x, ly, x + 6 * (HSpacing + suitWidth), ly);
			}
		}

		// Draw the card suits down the left side
		x = xoff + HSpacing;
		y = VSpacing * 2 + suitHeight;
		for (int i=0; i<4; i++) {
			suitRenderer.paintSuitMarker(g, x, y + (VSpacing + suitHeight) * i, i);
		}

		// Draw the values along the top edge
		x = HSpacing * 2 + suitWidth;
		y = VSpacing;
		for (int i=0; i<6; i++) {
			AB_graphics.paintSpecialMarker(g, x + (HSpacing + suitWidth) * i, y, AB_graphics.VALUE_9_MARKER + i);
		}

		// Fill in the stars where this player has a card
		x = HSpacing * 2 + suitWidth;
		y = VSpacing * 2 + suitHeight;
		for (int v=0; v<6; v++) {
			for (int s=0; s<4; s++) {
				// Determine which image to draw for this space
				switch (model.getPlayerCardCode(playerId, s, v)) {
					case AbstracModel.NOT_AVAILABLE : code = AB_graphics.DASH_MARKER;           break;
					case AbstracModel.TAKEN :         code = AB_graphics.GOLD_STAR_MARKER;      break;
					case AbstracModel.JUST_TAKEN :    code = AB_graphics.HIGHLIGHT_STAR_MARKER; break;
					default: code = 0; break;
				}

				// If we have something to draw, then draw it.
				if (code > 0) {
					int dx = x + (HSpacing + suitWidth) * v;
					int dy = y + (VSpacing + suitHeight) * s;
					AB_graphics.paintSpecialMarker(g, dx, dy, code);
				}
			}
		}

		// Draw the scores along the bottom
		x = HSpacing * 2 + suitWidth + (suitWidth - singleDigitWidth) / 2;
		y = VSpacing * 6 + suitHeight * 5;
		for (int i=0; i<6; i++) {
			numRenderer.paintSingleDigit(g, x + (HSpacing + suitWidth) * i, y, model.getSetScore(playerId, i));
		}

		// Draw the scores along the right edge
		x = HSpacing * 8 + suitWidth * 7;
		y = VSpacing * 2 + suitHeight + (suitHeight - numRenderer.getDigitHeight()) / 2;
		for (int i=0; i<4; i++) {
			numRenderer.paintDoubleDigit(g, x, y + (VSpacing + suitHeight) * i, model.getRunScore(playerId, i), digitSpacing);
		}

		// Draw the lines for the scores at the bottom
		x = HSpacing * 8 + suitWidth * 7 - halfHSpacing;
		y = VSpacing * 6 + suitHeight * 5 - halfVSpacing;
		g.drawLine(x, y, x, preferredVSize);
		g.drawLine(x, y, preferredHSize, y);

		// Draw the score stuff at the bottom
		x = HSpacing * 8 + suitWidth * 7;
		y = VSpacing * 6 + suitHeight * 5;
		numRenderer.paintDoubleDigit(g, x, y, model.getSumScore(playerId), digitSpacing);
		x += (doubleDigitWidth + HSpacing);
		AB_graphics.paintSingleDigit(g, x, y, AB_graphics.TIMES_MARKER);
		x += (singleDigitWidth + HSpacing);
		numRenderer.paintDoubleDigit(g, x, y, model.getNumCards(1-playerId), digitSpacing);
		x += (doubleDigitWidth + HSpacing);
		AB_graphics.paintSingleDigit(g, x, y, AB_graphics.EQUALS_MARKER);
		x += (singleDigitWidth + HSpacing);
		numRenderer.paintTripleDigit(g, x, y, model.getTotalScore(playerId), digitSpacing);

		// See if there are any selected cards in the hand component that should be drawn.
		if (theHandComponent != null) {
			int num_selected = theHandComponent.getNumSelectedCards();
			if (num_selected > 0) {
				Hand theHand = model.getHand();
				int handLength = theHand.length();
				for (int i=0; i<num_selected; i++) {
					Card theCard = theHand.getNthCard(handLength - i - 1);
					int dx = theCard.cardValue() * (suitWidth + HSpacing) + suitWidth + HSpacing * 2;
					int dy = theCard.cardSuit() * (suitHeight + VSpacing) + suitHeight + VSpacing * 2;
					AB_graphics.paintSpecialMarker(g, dx, dy, AB_graphics.SHADOW_STAR_MARKER);
				}
			}
		}
	}

}
