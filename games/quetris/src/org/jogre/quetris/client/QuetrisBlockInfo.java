/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
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
package org.jogre.quetris.client;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class defines the structrue of the quetris pieces and how they can move. 
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class QuetrisBlockInfo {

	public static final int NUMBER_OF_SHAPES = 12;
	public static final int NUMBER_OF_POSITIONS = 8;
	public static final int NUMBER_OF_BLOCKS = 5;
	
	// Declare an array of data to hold all the various shape positions.
 
	/** Block info array. */
	private static final int [][][] BLOCK_DATA = {
		{{2,0,2,1,2,2,1,3,2,3},{1,1,1,2,2,2,3,2,4,2},{2,1,3,1,2,2,2,3,2,4},{0,2,1,2,2,2,3,2,3,3},{2,0,2,1,2,2,2,3,3,3},{1,2,2,2,3,2,4,2,1,3},{1,1,2,1,2,2,2,3,2,4},{3,1,0,2,1,2,2,2,3,2}},
		{{2,1,3,1,1,2,2,2,2,3},{2,1,1,2,2,2,3,2,3,3},{2,1,2,2,3,2,1,3,2,3},{1,1,1,2,2,2,3,2,2,3},{1,1,2,1,2,2,3,2,2,3},{3,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,2,3,3,3},{2,1,1,2,2,2,3,2,1,3}},
		{{1,1,2,1,3,1,1,2,1,3},{1,1,2,1,3,1,3,2,3,3},{3,1,3,2,1,3,2,3,3,3},{1,1,1,2,1,3,2,3,3,3},{1,1,2,1,3,1,3,2,3,3},{3,1,3,2,1,3,2,3,3,3},{1,1,1,2,1,3,2,3,3,3},{1,1,2,1,3,1,1,2,1,3}},
		{{1,1,2,1,3,1,1,2,2,2},{2,1,3,1,2,2,3,2,3,3},{2,2,3,2,1,3,2,3,3,3},{1,1,1,2,2,2,1,3,2,3},{1,1,2,1,3,1,2,2,3,2},{3,1,2,2,3,2,2,3,3,3},{1,2,2,2,1,3,2,3,3,3},{1,1,2,1,1,2,2,2,1,3}},
		{{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3},{2,1,1,2,2,2,3,2,2,3}},
		{{1,1,1,2,2,2,2,3,3,3},{2,1,3,1,1,2,2,2,1,3},{1,1,2,1,2,2,3,2,3,3},{3,1,2,2,3,2,1,3,2,3},{3,1,2,2,3,2,1,3,2,3},{1,1,1,2,2,2,2,3,3,3},{2,1,3,1,1,2,2,2,1,3},{1,1,2,1,2,2,3,2,3,3}},
		{{1,1,2,1,3,1,1,2,3,2},{2,1,3,1,3,2,2,3,3,3},{1,2,3,2,1,3,2,3,3,3},{1,1,2,1,1,2,1,3,2,3},{1,1,2,1,3,1,1,2,3,2},{2,1,3,1,3,2,2,3,3,3},{1,2,3,2,1,3,2,3,3,3},{1,1,2,1,1,2,1,3,2,3}},
		{{2,2,3,2,4,2,1,3,2,3},{1,1,1,2,2,2,2,3,2,4},{2,1,3,1,0,2,1,2,2,2},{2,0,2,1,2,2,3,2,3,3},{0,2,1,2,2,2,2,3,3,3},{2,0,2,1,1,2,2,2,1,3},{2,2,3,2,4,2,1,1,2,1},{3,1,2,2,3,2,2,3,2,4}},
		{{2,1,2,2,3,2,2,3,2,4},{0,2,1,2,2,2,3,2,2,3},{2,0,2,1,1,2,2,2,2,3},{2,1,1,2,2,2,3,2,4,2},{2,1,1,2,2,2,2,3,2,4},{2,1,0,2,1,2,2,2,3,2},{2,0,2,1,2,2,3,2,2,3},{1,2,2,2,3,2,4,2,2,3}},
		{{2,1,2,2,1,3,2,3,3,3},{1,1,1,2,2,2,3,2,1,3},{1,1,2,1,3,1,2,2,2,3},{3,1,1,2,2,2,3,2,3,3},{2,1,2,2,1,3,2,3,3,3},{1,1,1,2,2,2,3,2,1,3},{1,1,2,1,3,1,2,2,2,3},{3,1,1,2,2,2,3,2,3,3}},
		{{0,2,1,2,2,2,3,2,4,2},{2,0,2,1,2,2,2,3,2,4},{0,2,1,2,2,2,3,2,4,2},{2,0,2,1,2,2,2,3,2,4},{0,2,1,2,2,2,3,2,4,2},{2,0,2,1,2,2,2,3,2,4},{0,2,1,2,2,2,3,2,4,2},{2,0,2,1,2,2,2,3,2,4}},
		{{1,1,1,2,2,2,3,2,3,3},{2,1,3,1,2,2,1,3,2,3},{1,1,1,2,2,2,3,2,3,3},{2,1,3,1,2,2,1,3,2,3},{3,1,1,2,2,2,3,2,1,3},{1,1,2,1,2,2,3,3,2,3},{3,1,1,2,2,2,3,2,1,3},{1,1,2,1,2,2,3,3,2,3}}};
	
  	/**
  	 * Declare accessor to return true if a shape is at a particular point.
  	 * 
  	 * @param shape
  	 * @param pos
  	 * @param block
  	 * @return
  	 * @throws IndexOutOfBoundsException
  	 */
  	public static int getShapeX (int shape, int pos, int block) 
		throws IndexOutOfBoundsException 
	{
		if (
			shape >= 0 && shape < NUMBER_OF_SHAPES && 
			pos >= 0 && pos < NUMBER_OF_POSITIONS && 
			block >= 0 && block < NUMBER_OF_BLOCKS
		) {
			return BLOCK_DATA [shape][pos][block * 2]; 
		}		
		else 
			throw new IndexOutOfBoundsException ("shape: " + shape + " pos: " + pos + " block: " + block);
	}
		
	/**
	 * Declare accessor to return true if a shape is at a particular point.
	 * 
	 * @param shape
	 * @param pos
	 * @param block
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public static int getShapeY (int shape, int pos, int block) 
		throws IndexOutOfBoundsException 
	{
		if (
				shape >= 0 && shape < NUMBER_OF_SHAPES && 
				pos >= 0 && pos < NUMBER_OF_POSITIONS && 
				block >= 0 && block < NUMBER_OF_BLOCKS 
		) {
			return BLOCK_DATA [shape][pos][block * 2 + 1]; 
		}		
		else 
			throw new IndexOutOfBoundsException ("shape: " + shape + " pos: " + pos + " block: " + block);
	}
	
	public static void main (String [] args) {
		JFrame frame = new JFrame ();
		JPanel panel = new JPanel () {
			public void paintComponent (Graphics g) {
				int s = 6;
				for (int i = 0; i < 12; i++) {
					for (int j = 0; j < 8; j++) {
						g.drawString("" + j, (j * 40) + 45, 20);
						for (int b = 0; b < 5; b++) {
							int xo = (j * 40) + 30;
							int yo = (i * 40) + 30;
							int x = xo + (QuetrisBlockInfo.getShapeX(i, j, b) * s);
							int y = yo + (QuetrisBlockInfo.getShapeY(i, j, b) * s);
							g.drawRect(x, y, s, s);
						}
					}
				}
			}
		};
		frame.getContentPane().add (panel, BorderLayout.CENTER);
		frame.setSize(640, 480);
		frame.setVisible(true);
	}
	
	//     0 1 2 3 4 5 6 7
	// vf  4 7 6 5 0 3 2 1          
	// hf                
	
}
