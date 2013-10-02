/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.chess.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Chess board component.
 */
public class ChessBoardComponent extends AbstractBoardComponent {

	protected ChessModel chessModel;

	/**
	 * @param numOfRows
	 * @param numOfCols
	 */
	public ChessBoardComponent(ChessModel chessModel) {
		super (8, 8);
		this.chessModel = chessModel;
	}

	/**
	 * Paint component.
	 * 
	 * @see org.jogre.client.awt.AbstractBoardComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the default board
		super.paintComponent (g);

		// draw each chess piece on the board
		drawPieces(g);

		// Draw the mouse boards
		drawMouseBorders(g);
	}

	/**
	 * @param g
	 */
	private void drawPieces(Graphics g) {

		int xIndent = (cellSize - GameImages.getImageIcon(ChessImages.WHITE_PAWN).getIconWidth()) / 2;
		int yIndent = (cellSize - GameImages.getImageIcon(ChessImages.WHITE_PAWN).getIconHeight()) / 2;

		// update each square with the chess piece
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				int curPiece = chessModel.getPiece(x, y);
				Point sCoords = getScreenCoords (x, y);

				if (curPiece != ChessModel.EMPTY) {
					g.drawImage(
						GameImages.getImage(curPiece + 2),
						sCoords.x + xIndent, sCoords.y + yIndent,
						null
					);
				}

				if (false) {
					g.setColor(Color.red);
					if (chessModel.isAllowedMove (x, y)) {
						g.drawRect(sCoords.x, sCoords.y, cellSize - 1, cellSize - 1);
					}
				}
			}
		}
	}

	/**
	 * Change so that it SETS the mouse borders.
	 *
	 * @param g
	 */
	private void drawMouseBorders(Graphics g) {

		Point lastMove = chessModel.getLastMove();
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

				if (chessModel.isAllowedMove (dragPoint.x, dragPoint.y)) {

					for (int i = 0; i < mouseBorderWidth; i++)
						g.drawRect (
							sCoords.x + i, sCoords.y + i,
							cellSize - (i * 2) - 1, cellSize - (i * 2) - 1
						);
				}
			}
		}
	}
}
