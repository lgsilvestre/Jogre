/*
 * JOGRE (Java Online Gaming Real-time Engine) - Tetris
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
package org.jogre.tetris.client;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;

import javax.swing.BorderFactory;

import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePanel;
import org.jogre.common.util.GameLabels;

/**
 * Tetris component which shows the next shape that is coming onto
 * the screen.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TetrisNextShapeComponent extends JogreComponent {

	private TetrisPlayerModel playerModel;
	
	private NextShape nextShape;
	
	/** 
	 * Default constructor to the game
	 * 
	 * @param playerModel   Player model.
	 */
	public TetrisNextShapeComponent (TetrisPlayerModel playerModel) {
		// Set field
		this.playerModel = playerModel;
		
		// Create GUI items
		GameLabels labels = GameLabels.getInstance();
		Color labelColor = new Color (111, 66, 154);
		this.nextShape   = new NextShape (TetrisPlayerModel.NO_SHAPE);		
		JogreLabel nextLabel   = new JogreLabel (labels.get("next"), 'b', 16);
		
		// Add item component
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10}, {10, pref, 5, pref, 5, pref, 5, pref, 10}};		
		setLayout (new TableLayout (sizes));
		add (nextLabel,   "1,1,c,c");
		add (nextShape,   "1,3,c,c");
		
		// Add observer on model
        playerModel.addObserver (this);
	}
	
	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable observerable, Object args) {
		nextShape.setShape(playerModel.getNextShapeNum());
		
		repaint ();
	}
	
	/**
	 * Little inner class which draws the shape
	 */
	private class NextShape extends JogrePanel {
		
		private final Color GRADIANT_COLOR_TOP = Color.white;
		private final Color GRADIANT_COLOR_BOTTOM = new Color (230, 230, 230);		
		private final int BLOCK_SIZE = 16;
		private final int BORDER     = 8;		
		private final int WIDTH      = BLOCK_SIZE * 5 + BORDER * 2;
		private final int HEIGHT     = BLOCK_SIZE * 5 + BORDER * 2;
		
		int shape = TetrisPlayerModel.NO_SHAPE;
		
		/**
		 * Empty constructor.
		 */
		public NextShape (int shape) {
			setPreferredSize(new Dimension (WIDTH, HEIGHT));
			
			this.shape = shape;
			setBorder(BorderFactory.createEtchedBorder());
		}
		
		/**
		 * Set shape.
		 * 
		 * @param shape
		 */
		public void setShape (int shape) {
			this.shape = shape;
			repaint ();
		}

		/**
		 * Paint component.
		 * 
		 * @see org.jogre.client.awt.JogrePanel#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent (Graphics g) {
			// Draw gradient
			JogreAwt.drawVerticalGradiant(g, 0, 0, getWidth(), getHeight(), GRADIANT_COLOR_TOP, GRADIANT_COLOR_BOTTOM);
			
			// Draw shapes on screen
			for (int i = 0; i < TetrisBlockInfo.NUMBER_OF_BLOCKS; i++) {
				
				// Calculate co-ordinates			
				if (shape != TetrisPlayerModel.NO_SHAPE) {
					int x = (TetrisBlockInfo.getShapeX(shape, 0, i) * BLOCK_SIZE) + BORDER;
					int y = (TetrisBlockInfo.getShapeY(shape, 0, i) * BLOCK_SIZE) + BORDER;
						
					// Check to see if a block is at the particular position
					if (shape >= 0) {					
						g.drawImage(
							GameImages.getImage(shape + 1),
							x, y, null
						);
					}	
					else {		
						// other wise it is background
						g.setColor (Color.white);
						g.fillRect (x, y, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
					}
				}
			}
		}
	}
}