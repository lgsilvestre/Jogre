/*
 * JOGRE (Java Online Gaming Real-time Engine) - Tetris
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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

import junit.framework.TestCase;

import org.jogre.tetris.common.CommTetrisMove;
import org.jogre.tetris.common.CommTetrisPlayerState;

/**
 * Tetris communication test.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class TetrisCommTest extends TestCase {

	/**
	 * Test CommTetrisMove communiations object.
	 * 
	 * @throws Exception
	 */
	public void testCommTetrisMove () throws Exception {
		CommTetrisMove move = new CommTetrisMove (1, 4, 3, 10, 12, 9);
		CommTetrisMove moveFromElm = new CommTetrisMove (move.flatten());
		assertEquals (move.getCurShapeNum(), moveFromElm.getCurShapeNum());
		assertEquals (move.getCurShapePos(), moveFromElm.getCurShapePos());
		assertEquals (move.getCurShapeX(), moveFromElm.getCurShapeX());
		assertEquals (move.getCurShapeY(), moveFromElm.getCurShapeY());
		assertEquals (move.getNextShapeNum(), moveFromElm.getNextShapeNum());	
	}
	
	/**
	 * Test CommTetrisPlayerState communiations object.
	 * 
	 * @throws Exception
	 */
	public void testCommTetrisPlayerState () throws Exception {
    	int [][] data = new int[TetrisPlayerModel.NUM_OF_COLS][TetrisPlayerModel.NUM_OF_ROWS]; 
    	for (int x = 0; x < TetrisPlayerModel.NUM_OF_COLS; x++) {
    		for (int y = 0; y < TetrisPlayerModel.NUM_OF_ROWS; y++) {
    			data [x][y] = -1;
    			if (y < 10)
    				data [x][y] = (int)(Math.random() * 7);
    		}
    	}
		CommTetrisPlayerState state = new CommTetrisPlayerState (1, data);
		CommTetrisPlayerState stateFromElm = new CommTetrisPlayerState (state.flatten());
		for (int x = 0; x < TetrisPlayerModel.NUM_OF_COLS; x++)
    		for (int y = 0; y < TetrisPlayerModel.NUM_OF_ROWS; y++)
    			assertEquals(state.getGridData()[x][y], stateFromElm.getGridData()[x][y]);
		assertEquals(state.getSeatNum(), stateFromElm.getSeatNum());
	}
} 