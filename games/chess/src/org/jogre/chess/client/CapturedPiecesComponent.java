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

import org.jogre.client.awt.GameImages;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;


/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Component for a captured chess piece.
 */
public class CapturedPiecesComponent extends JComponent implements Observer {

	// link to current game data structure
	protected ChessModel chessModel;
	protected int colour;			// 0=white, 1=black
	protected int padding;          // amount of pixels around each piece

	/**
	 * Default constructor which takes a model and a colour
	 *
	 * @param chessModel  Link to the main chess model
	 * @param colour      0=white, 1=black
	 */
	public CapturedPiecesComponent (ChessModel chessModel, int colour) {
		this.chessModel = chessModel;
		this.colour = colour;

		// Work out the dimensions of the component depending on the image size
		int imageWidth = GameImages.getImageIcon(ChessImages.WHITE_PAWN).getIconWidth();
		int imageHeight = GameImages.getImageIcon(ChessImages.WHITE_PAWN).getIconHeight();

		padding = 2;
		int width = ((imageWidth + padding) * 2) + padding;
		int height = ((imageHeight + padding) * 8) + padding;

		setPreferredSize (new Dimension (width, height));
		repaint ();
	}

	/**
	 * Refresh the component.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// wipe the background (spacing colour)
		g.setColor(new Color (255, 255, 255));
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		// Draw small border
		g.setColor(Color.darkGray);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		// Retrieve the correct pieces from the captured pieces Vector.
		int [] pieces = chessModel.getCapturedPieces();

		// Retrieve the height and width of a chess image
		int imageWidth = GameImages.getImageIcon(ChessImages.WHITE_PAWN).getIconWidth();
		int imageHeight = GameImages.getImageIcon(ChessImages.WHITE_PAWN).getIconHeight();

		int counter = 0;
		for (int i = 0; i < 32; i++) {
			int x = padding;
			int y = counter * (imageHeight + padding) + padding;

			if (counter >= 8) {		// Start at the next column
				x+= imageWidth + padding;
				y = (counter - 8) * (imageHeight + padding) + padding;
			}
			// Retrieve piece number and render onto the screen.
			int curPiece = pieces [i];

			if (chessModel.getPieceColour(curPiece) == colour){
				counter ++;

				if (curPiece > 0 && curPiece <= 12) {
					g.drawImage (
						GameImages.getImage(curPiece + 2), x, y, null
					);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable observable, Object obj) {
		repaint ();
	}
}
