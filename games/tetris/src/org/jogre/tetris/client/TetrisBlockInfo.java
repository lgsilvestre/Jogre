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

/**
 * This class defines the structrue of the tetris pieces and how they can move. 
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TetrisBlockInfo {

	public static final int NUMBER_OF_BLOCKS = 4;
	
	// Declare an array of data to hold all the various shapes into
	private static final int [][][] blockdata = {
		{{1,1,0,2,1,2,2,2},{1,1,1,2,2,2,1,3},{0,2,1,2,2,2,1,3},{1,1,0,2,1,2,1,3}},
		{{1,1,2,1,1,2,2,2},{1,1,2,1,1,2,2,2},{1,1,2,1,1,2,2,2},{1,1,2,1,1,2,2,2}},
		{{1,0,1,1,1,2,1,3},{0,1,1,1,2,1,3,1},{2,0,2,1,2,2,2,3},{0,2,1,2,2,2,3,2}},
		{{1,1,1,2,2,2,2,3},{1,2,2,2,0,3,1,3},{1,1,1,2,2,2,2,3},{1,2,2,2,0,3,1,3}},
		{{2,1,1,2,2,2,1,3},{1,2,2,3,0,2,1,3},{2,1,1,2,2,2,1,3},{1,2,2,3,0,2,1,3}},
		{{1,1,2,1,2,2,2,3},{2,1,0,2,1,2,2,2},{1,0,1,1,1,2,2,2},{1,1,2,1,3,1,1,2}},
		{{1,1,2,1,1,2,1,3},{0,1,1,1,2,1,2,2},{2,0,2,1,1,2,2,2},{1,1,1,2,2,2,3,2}}
	};
 
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
			shape >= 0 && shape < 7 && 
			pos >= 0 && pos < 4 && 
			block >= 0 && block < 4
		) {
			return blockdata [shape][pos][block * 2]; 
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
				shape >= 0 && shape < 7 && 
				pos >= 0 && pos < 4 && 
				block >= 0 && block < 4 
		) {
			return blockdata [shape][pos][block * 2 + 1]; 
		}		
		else 
			throw new IndexOutOfBoundsException ("shape: " + shape + " pos: " + pos + " block: " + block);
	}
}
