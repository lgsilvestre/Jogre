/*
 * JOGRE (Java Online Gaming Real-time Engine) - Reversi
 * Copyright (C) 2005  Ugnich Anton (anton@portall.zp.ua)
 * http//jogre.sourceforge.org
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
package org.jogre.reversi.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;

// Reversi visual board component (view of the model)
public class ReversiBoardComponent extends AbstractBoardComponent {

	// Declare constants which define what the board looks like
	private static final int NUM_OF_COLS = 8;
	private static final int NUM_OF_ROWS = 8;
	private static final int CELL_SIZE = 30;
	private static final int CELL_SPACING = 0;

	// Link to the model
	protected ReversiModel model;
	
	private Point mousePoint = null;

	// Constructor which creates the board
	public ReversiBoardComponent (ReversiModel model) {
		// Call constructor in AbstractBoardComponent
		super (NUM_OF_ROWS, NUM_OF_COLS, CELL_SIZE, CELL_SPACING,
			   DEFAULT_BORDER_WIDTH, 0, false, true, false);

		this.model = model;			// link to model

		// set colours
		Color bgColor = new Color (34, 34, 102);
		setColours (bgColor, bgColor, Color.black, Color.white);
	}

	// Update the graphics depending on the model
	public void paintComponent (Graphics g) {
		// Draw the board (AbstractBoardComponent)
		super.paintComponent (g);
		
		// draw pieces
		drawPieces(g);
		
		// draw mouse piece
		drawMousePiece(g);		
	}
	
	public void drawPieces(Graphics g) {
		// draw each TicTacToe piece on the board (loop through the model)		
		for (int x = 0; x < ReversiModel.COLS; x++) {
			for (int y = 0; y < ReversiModel.ROWS; y++) {
				// get piece from model
				int piece = model.getData(x, y);    
				
				// Now get screen co-ordinates (AbstractBoardComponent)
				Point screen = getScreenCoords (x, y);
				
				Image image = GameImages.getImage(piece + 3);
				if (image != null)
					g.drawImage (image, screen.x, screen.y, null);	// draw
			}
		}
	}
	
	/**
	 * Draw the mouse piece.
	 * 
	 * @param g   Graphics object.
	 */
	public void drawMousePiece(Graphics g) {
		if (this.mousePoint != null) {
			Image image = GameImages.getImage (ReversiImages.MOUSE_PIECE);
			if (image != null) {
				Point screen = getScreenCoords (mousePoint.x, mousePoint.y);
				g.drawImage (image, screen.x, screen.y, null);
			}
		}
	}
	
	/**
	 * Set the mouse point.
	 * 
	 * @param mousePoint
	 */
	public void setMousePoint(Point mousePoint) {
		boolean changed = false;
		if (mousePoint != null && !mousePoint.equals(this.mousePoint))
			changed = true;
		else if (this.mousePoint != null && !this.mousePoint.equals(mousePoint))
			changed = true;
		this.mousePoint = mousePoint;
		if (changed)
			repaint();
	}
}
