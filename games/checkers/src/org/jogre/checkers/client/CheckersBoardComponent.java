/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
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
package org.jogre.checkers.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Checkers board component.
 */
public class CheckersBoardComponent extends AbstractBoardComponent {

	protected CheckersModel checkersModel;

	/**
	 * @param numOfRows
	 * @param numOfCols
	 */
	public CheckersBoardComponent (CheckersModel checkersModel) {
		super (8, 8);			// standard 8x8 board with letters
		this.checkersModel = checkersModel;
	}


	/**
	 * Update the graphics.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the default board
		super.paintComponent (g);

		// draw each checkers piece on the board
		drawPieces (g);

		drawMouseBorders (g);
	}

	/**
	 * Draw the pieces.
	 *
	 * @param g
	 */
	private void drawPieces (Graphics g) {
		int xIndent = (cellSize - GameImages.getImageIcon(CheckersImages.WHITE_NORMAL_PIECE).getIconWidth()) / 2;
		int yIndent = (cellSize - GameImages.getImageIcon(CheckersImages.WHITE_NORMAL_PIECE).getIconHeight()) / 2;

		for (int i = 0; i < 32; i++) {
			int curPiece = checkersModel.getPiece(i);

			// Get board point
			Point boardPos = checkersModel.getBoardPoint(i);
			// Now get screen co-ordinates
			Point sCoords = getScreenCoords (boardPos.x, boardPos.y);

			g.setColor (Color.red);
			if (checkersModel.isAllowedMove (i))
				g.drawRect (
					sCoords.x, sCoords.y,
					cellSize - 1, cellSize - 1
				);

			if (curPiece != CheckersModel.EMPTY) {
				g.drawImage(
					GameImages.getImage(curPiece),
					sCoords.x + xIndent, sCoords.y + yIndent,
					null
				);
			}
		}
	}

	/**
	 * Draw mouse borders.
	 *
	 * @param g
	 */
	private void drawMouseBorders(Graphics g) {
		Point lastMove = checkersModel.getLastMove();
		if (!lastMove.equals (OFF_SCREEN_POINT)) {
			Point sLastMove = getScreenCoords (lastMove.x, lastMove.y);
			g.setColor(new Color (0, 0, 255));
			g.drawRect (sLastMove.x, sLastMove.y, cellSize - 1, cellSize - 1);
		}

		// check to see if the mouse is currently held down
		if (!pressedPoint.equals(OFF_SCREEN_POINT)) {
			Point sCoords = getScreenCoords (pressedPoint.x, pressedPoint.y);

			// draw border around the mouse
			g.setColor(mouseBorderColour);
			for (int i = 0; i < mouseBorderWidth; i++)
				g.drawRect (
					sCoords.x + i, sCoords.y + i,
					cellSize - (i * 2) - 1, cellSize - (i * 2) - 1
				);

			// check to see if the mouse is being dragged
			if (!dragPoint.equals(OFF_SCREEN_POINT)) {
				sCoords = getScreenCoords (dragPoint.x, dragPoint.y);
			}
		}
	}
}
