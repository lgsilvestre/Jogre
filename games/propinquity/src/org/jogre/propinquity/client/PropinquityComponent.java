/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.propinquity.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.jogre.client.awt.JogreComponent;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Component which shows a hexagon component.
 */
public class PropinquityComponent extends JogreComponent {

	private int cellSize = 42;
	private int borderSize = 4;
	private Polygon hexagon;

	// Link to game data
	private PropinquityModel propinquityModel;

	// Declare font and font metrics for this font
	protected final Font numbersFont = new Font ("SansSerif", Font.BOLD, 16);
	protected FontMetrics fontMetrics = null;

	private final Point offScreen = new Point (-1, -1);
	private int curMousePoint = -1;

	/**
	 * Constructor.
	 *
	 * @param propinquityModel
	 */
	public PropinquityComponent (PropinquityModel propinquityModel) {
		this.propinquityModel = propinquityModel;

		int width = (propinquityModel.getNumOfCols() * cellSize) +
		 	(cellSize / 2) +
			(borderSize * 2) +
		    1;
		int height = (propinquityModel.getNumOfRows() * (cellSize / 4 * 3)) +
			(cellSize / 4) +
			(borderSize * 2) +
		    1;

		setPreferredSize(new Dimension (width, height));
	}

	/**
	 * Paint component.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the background
		drawBackGround (g);

		// Draw each cell
		drawCells(g);

		// Draw the mouse border if it exists
		drawMouseBorder(g);
	}

	/**
	 * @param g
	 */
	private void drawMouseBorder(Graphics g) {
		// Draw yellow border of mouse
		if (curMousePoint != -1) {
			g.setColor (PropinquityLookAndFeel.MOUSE_COLOUR);
			hexagon = PropinquityGraphics.createHexagon (cellSize);
			Point position = getCoord (curMousePoint);

			hexagon.translate (position.x, position.y);
			g.drawPolygon (hexagon);
		}
	}

	/**
	 * @param g
	 */
	private void drawCells (Graphics g) {
		// Only create font metrics once (for performance)
		if (fontMetrics == null) {
			fontMetrics = g.getFontMetrics();
		}
		g.setFont(numbersFont);

		// Loop through the various cells
		for (int index = 0; index < propinquityModel.getNumOfCells(); index++) {

			Point pos = getCoord (index);
			Cell curCell = propinquityModel.getGridData (index);

			if (curCell.getState() == Cell.CELL_BLANK) {
				// draw blank hexagon
				Color bgColour = PropinquityLookAndFeel.EMPTY_BG_COLOUR;
				PropinquityGraphics.drawHexagon (
					g, pos.x, pos.y, cellSize, false, bgColour);
			}
			else if (curCell.getState() == Cell.CELL_PLAYER_1 || curCell.getState() == Cell.CELL_PLAYER_2) {
				// Set the correct colour for the player
				Color bgColor = curCell.getState() == Cell.CELL_PLAYER_1 ?
						PropinquityLookAndFeel.PLAYER1_BG_COLOUR : PropinquityLookAndFeel.PLAYER2_BG_COLOUR;

				PropinquityGraphics.drawHexagonWithText (
					g, pos.x, pos.y, cellSize, true, bgColor,	curCell.getArmies());
			}
		}
	}

	/**
	 * @param g
	 */
	private void drawBackGround(Graphics g) {
		g.setColor (PropinquityLookAndFeel.BG_COLOUR_1);
		g.fillRect (0, 0, getWidth() - 1, getHeight() - 1);

		g.setColor (PropinquityLookAndFeel.BORDER_COLOUR);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	/**
	 * @param index
	 * @return
	 */
	public Point getCoord (int index) {
		int x = (index % propinquityModel.getNumOfCols()) * cellSize + borderSize;
		int y = (index / propinquityModel.getNumOfCols()) * (cellSize / 4 * 3) + borderSize;
		if ((index / propinquityModel.getNumOfCols()) % 2 == 1)
			x += (cellSize / 2);

		return new Point (x, y);
	}

	/**
	 * Retrieves the index when the user clicks on this component.
	 *
	 * @param coOrds
	 * @return
	 */
	public int getIndex (Point point) {
		// Compute row number first
		int row = (point.y - borderSize) / (cellSize / 4 * 3);
		if (row % 2 == 1)
			point.x -= cellSize / 2;

		// Compute colum point
		int maxWidth = (propinquityModel.getNumOfCols() * cellSize) - borderSize;
		if (point.x > maxWidth)
			point.x = maxWidth;
		int column = (point.x - borderSize) / cellSize;

		// now retrieve index using the row & column
		int index = (propinquityModel.getNumOfCols() * row) + column;
		// Check that the index is in range and return it to the user
		if (index >= 0 && index < propinquityModel.getNumOfCols() * propinquityModel.getNumOfRows())
			return index;

		return -1;
	}

	/**
	 * Return the current mouse point.
	 *
	 * @return
	 */
	public int getCurMousePoint() {
		return curMousePoint;
	}

	/**
	 * Set the current mouse point to another point.
	 *
	 * @param newPoint   New point.
	 */
	public void setCurMousePoint(int newPoint) {
		curMousePoint = newPoint;
	}
}
