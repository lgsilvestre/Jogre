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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jogre.client.awt.JogreComponent;
import org.jogre.common.util.GameProperties;

/**
 * View of both player's fault count for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingFaultIndicatorComponent extends JogreComponent {

	// Link to the model & graphics helper
	private JumpingClientModel model;
	private JumpingGraphics Jgraphics;

	// The playerId that we're showing on top
	private int topPlayerId;

	// Parameters for drawing the thermometers
	private final static int DEFAULT_THERM_HEIGHT = 20;
	private final static int DEFAULT_THERM_POINT_SPREAD = 8;
	private int thermHeight;
	private int thermPointSpread;
	private int thermWidth;

	private int [] thermX = new int [2];
	private int [] thermY = new int [2];
	private int [] thermSpread = new int [2];

	// Parameters for drawing the horses
	private int horseXoff;
	private int horseYoffTop, horseYoffBot;

	// Offset of horses (This is twice the value in JumpingTrackComponent...)
	private static final int HORSE_VERT_OFFSET = 24;

	private Color blackColor = new Color(0,0,0);
	private Color whiteColor = new Color(255,255,255);

	// For debug, setting this to true will cause the requested & actual bounds
	// of the component to be drawn.
	private boolean debugShowBounds = false;

	/**
	 * Constructor which creates the component
	 *
	 * @param model				The game model
	 * @param thermHeight		The height of a thermometer
	 * @param themPointSpread	The horizontal distance between points in the thermometer
	 */
	public JumpingFaultIndicatorComponent (	JumpingClientModel model) {

		// link to model
		this.model = model;
		this.Jgraphics = JumpingGraphics.getInstance();

		// Get properties from the properties file
		GameProperties props = GameProperties.getInstance();
		thermHeight = props.getInt("fault.therm.height", DEFAULT_THERM_HEIGHT);
		thermPointSpread = props.getInt("fault.therm.pointSpread", DEFAULT_THERM_POINT_SPREAD);

		this.topPlayerId = 0;
		this.thermWidth = thermPointSpread * 16;

		this.horseXoff = Jgraphics.imageWidths[JumpingImages.HORSE0] / 2;
		this.horseYoffTop = Jgraphics.imageHeights[JumpingImages.HORSE0] / 2;
		this.horseYoffBot = this.horseYoffTop + HORSE_VERT_OFFSET;

		this.thermX[0] = Jgraphics.imageWidths[JumpingImages.HORSE0] + 1;
		this.thermY[0] = Jgraphics.imageHeights[JumpingImages.HORSE0] - thermHeight - 1;
		this.thermX[1] = Jgraphics.imageWidths[JumpingImages.HORSE0] + 1;
		this.thermY[1] = Jgraphics.imageHeights[JumpingImages.HORSE0] + HORSE_VERT_OFFSET - thermHeight - 1;


		// Set the prefered size of our component
		Dimension dim = new Dimension (
			thermWidth + 2 + Jgraphics.imageWidths[JumpingImages.HORSE0],
			Jgraphics.imageHeights[JumpingImages.HORSE0] + HORSE_VERT_OFFSET
		);

		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Set the PlayerId of the player that this component should be showing on top.
	 *
	 * @param	playerId		The playerId that this component should be showing on top.
	 */
	public void setTopPlayerId(int playerId) {
		this.topPlayerId = (playerId == -1) ? 0 : playerId;
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {

		// Draw the thermometers
		paintATherm(g, topPlayerId, thermX[0], thermY[0]);
		paintATherm(g, (1-topPlayerId), thermX[1], thermY[1]);

		// Draw the horses
		Jgraphics.paintImage(g, horseXoff, horseYoffTop, JumpingImages.HORSE0 + topPlayerId, 0, 0);
		Jgraphics.paintImage(g, horseXoff, horseYoffBot, JumpingImages.HORSE1 - topPlayerId, 0, 0);

		// For debug, show the requested & actual bounds
		if (debugShowBounds) {
			Jgraphics.paintRects(g, getBounds(), getPreferredSize());
		}
	}

	/**
	 * Paint a single thermometer.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	playerId		The player whose faults are to be shown
	 * @param	Yoff			The Y-offset to start the thermometer
	 */
	private void paintATherm(Graphics g, int playerId, int Xoff, int Yoff) {
		int qfaults = model.getQuarterFaults(playerId);
		int overqfaults = Math.max(0, qfaults - 16);
		qfaults = Math.min(qfaults, 16);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1));

		// Draw the rectangle for faults in the right color.
		g.setColor(Jgraphics.horseColors[playerId]);
		g.fillRect(Xoff, Yoff, qfaults * thermPointSpread, thermHeight);
		g.setColor(Jgraphics.faultOverflowColor);
		g.fillRect(Xoff, Yoff, overqfaults * thermPointSpread, thermHeight);

		// Draw the thermometer tick marks
		int x = Xoff + thermPointSpread;
		for (int i=0; i<4; i++) {
			drawNormalRectOut(g, x, Yoff+6, 2, (thermHeight - 6*2)); x+= thermPointSpread;
			drawNormalRectOut(g, x, Yoff+6, 2, (thermHeight - 6*2)); x+= thermPointSpread;
			drawNormalRectOut(g, x, Yoff+6, 2, (thermHeight - 6*2)); x+= thermPointSpread;
			if (i != 3) {
				drawSunkRectOut(g, x, Yoff+0, 2, (thermHeight - 0*2)); x+= thermPointSpread;
			}
		}

		// Draw the thermometer outline
		drawSunkRectIn(g, Xoff, Yoff, thermWidth, thermHeight);

	}

	/*
	 * Helper function to draw a sunk in rectangle.
	 */
	private void drawSunkRectIn(Graphics g, int x, int y, int w, int h) {
		g.setColor(blackColor);
		g.drawLine(x, y, x+w, y);
		g.drawLine(x, y, x, y+h);
		g.setColor(whiteColor);
		g.drawLine(x+w, y, x+w, y+h);
		g.drawLine(x, y+h, x+w, y+h);
	}

	/*
	 * Helper function to draw a popped-out rectangle.
	 */
	private void drawSunkRectOut(Graphics g, int x, int y, int w, int h) {
		g.setColor(whiteColor);
		g.drawLine(x, y, x+w, y);
		g.drawLine(x, y, x, y+h);
		g.setColor(blackColor);
		g.drawLine(x+w, y, x+w, y+h);
		g.drawLine(x, y+h, x+w, y+h);
	}

	/*
	 * Helper function to draw a line in rectangle.
	 */
	private void drawNormalRectOut(Graphics g, int x, int y, int w, int h) {
		g.setColor(blackColor);
		g.drawLine(x, y, x, y+h);
	}
}
