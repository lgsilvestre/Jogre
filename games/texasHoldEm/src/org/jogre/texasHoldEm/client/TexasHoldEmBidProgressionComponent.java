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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * TexasHoldEm Bid Progression component.
 * This displays the progression of the blind bids, highlighting the one
 * that is currently active.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmBidProgressionComponent extends JogreComponent {

	// Link to the model, graphics helper & text labels
	private TexasHoldEmClientModel model;
	TexasHoldEmGraphics thGraphics;
	GameLabels labels;

	// Metrics for the game font.
	// (The font is created and kept in TexasHoldEmGraphics.java)
	FontMetrics gameFontMetrics;

	// Height of each row of text
	int textHeight;

	// Offset to row 0 of the text
	int row0Offset;

	// Width of the whole table
	int tableWidth;

	// Color to highlight the current table entry
	Color rowHighlightColor = new Color (255, 255, 0);

	// Strings to display
	private String [] SBText = new String [10];
	private String [] BBText = new String [10];
	private String [] AnteText = new String [10];

	// Locations to place the strings
	private Point [] SBTextAnchor = new Point [10];
	private Point [] BBTextAnchor = new Point [10];
	private Point [] AnteTextAnchor = new Point [10];

	// Parameters for drawing the table
	private static final int TEXT_BORDER = 2;
	private static final int COLUMN_SPACING = 6;

	/**
	 * Constructor which creates the component
	 *
	 * @param model					The game model
	 */
	public TexasHoldEmBidProgressionComponent (TexasHoldEmClientModel model) {

		// Save parameters provided
		this.model = model;
		thGraphics = TexasHoldEmGraphics.getInstance();
		labels = GameLabels.getInstance();

		// Get the Font Metrics for the font we're using
		gameFontMetrics = getFontMetrics(thGraphics.gameFont);

		// Create the table entries
		tableWidth = calcTextAnchors();

		// Set the preferred dimension
		Dimension tableDim = new Dimension (
		    tableWidth,
		    AnteTextAnchor[8].y - gameFontMetrics.getMaxAscent() + textHeight
		);

		setPreferredSize ( tableDim );
	}

	/*
	 * This will calculate the dimensions of the table component.
	 * It will return the width of the table.
	 */
	private int calcTextAnchors() {
		// Init the width of the columns to the header text
		SBText[9] = labels.get("blind.text.0");
		BBText[9] = labels.get("blind.text.1");
		AnteText[9] = labels.get("blind.text.2");

		int SBTextWidth = gameFontMetrics.stringWidth(SBText[9]);
		int BBTextWidth = gameFontMetrics.stringWidth(BBText[9]);
		int AnteTextWidth = gameFontMetrics.stringWidth(AnteText[9]);

		textHeight = gameFontMetrics.getMaxAscent() +
		             gameFontMetrics.getMaxDescent() +
		             TEXT_BORDER * 2;
		row0Offset = TEXT_BORDER + gameFontMetrics.getMaxAscent();

		// Calculate the widths of the columns
		for (int i=0; i<9; i++) {
			SBText[i] = thGraphics.currencyFormatter.format(model.getSmallBlind(i));
			BBText[i] = thGraphics.currencyFormatter.format(model.getBigBlind(i));
			AnteText[i] = thGraphics.currencyFormatter.format(model.getAnte(i));

			SBTextWidth   = Math.max(SBTextWidth, gameFontMetrics.stringWidth(SBText[i]));
			BBTextWidth   = Math.max(BBTextWidth, gameFontMetrics.stringWidth(BBText[i]));
			AnteTextWidth = Math.max(SBTextWidth, gameFontMetrics.stringWidth(AnteText[i]));
		}

		// Set the anchor points for the text
		int c1x = TEXT_BORDER + (SBTextWidth / 2);
		int c2x = TEXT_BORDER + SBTextWidth + COLUMN_SPACING + (BBTextWidth / 2);
		int c3x = TEXT_BORDER + SBTextWidth + COLUMN_SPACING + BBTextWidth + COLUMN_SPACING + (AnteTextWidth /2);
		int y = row0Offset;

		SBTextAnchor[9] = new Point (c1x, y);
		BBTextAnchor[9] = new Point (c2x, y);
		AnteTextAnchor[9] = new Point (c3x, y);

		for (int i=0; i<9; i++) {
			y += textHeight;
			SBTextAnchor[i] = new Point (c1x, y);
			BBTextAnchor[i] = new Point (c2x, y);
			AnteTextAnchor[i] = new Point (c3x, y);
		}

		// return the overall width of the text columns
		return SBTextWidth + BBTextWidth + AnteTextWidth + (2 * TEXT_BORDER) + (2 * COLUMN_SPACING);
	}

	/**
	 * Paint the component
	 *
	 * @param g		The graphics context to draw on.
	 */
	public void paintComponent (Graphics g) {
		// Set the font of the graphics context to the game font.
		thGraphics.setGameFont(g);

		// Highlight the current row
		g.setColor(rowHighlightColor);
		int r = model.getCurrentBlindScheduleStage();
		g.fillRect(0, textHeight * (r+1), tableWidth, textHeight);

		// Draw the table
		g.setColor(thGraphics.blackColor);
		for (int i=0; i<10; i++) {
			thGraphics.drawCenterJustifiedText(g, SBText[i], SBTextAnchor[i]);
			thGraphics.drawCenterJustifiedText(g, BBText[i], BBTextAnchor[i]);
			thGraphics.drawCenterJustifiedText(g, AnteText[i], AnteTextAnchor[i]);
		}

	}


}
