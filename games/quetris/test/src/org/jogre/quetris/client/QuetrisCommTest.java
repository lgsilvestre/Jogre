/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
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
package org.jogre.quetris.client;

import junit.framework.TestCase;

import org.jogre.quetris.common.CommQuetrisMove;
import org.jogre.quetris.common.CommQuetrisPlayerState;

/**
 * Quetris communication test.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class QuetrisCommTest extends TestCase {

	/**
	 * Test CommQuetrisMove communiations object.
	 * 
	 * @throws Exception
	 */
	public void testCommQuetrisMove () throws Exception {
		CommQuetrisMove move = new CommQuetrisMove (1, 4, 3, 10, 12, 9);
		CommQuetrisMove moveFromElm = new CommQuetrisMove (move.flatten());
		assertEquals (move.getCurShapeNum(), moveFromElm.getCurShapeNum());
		assertEquals (move.getCurShapePos(), moveFromElm.getCurShapePos());
		assertEquals (move.getCurShapeX(), moveFromElm.getCurShapeX());
		assertEquals (move.getCurShapeY(), moveFromElm.getCurShapeY());
		assertEquals (move.getNextShapeNum(), moveFromElm.getNextShapeNum());	
	}
	
	/**
	 * Test CommQuetrisPlayerState communiations object.
	 * 
	 * @throws Exception
	 */
	public void testCommQuetrisPlayerState () throws Exception {
    	int [][] data = new int[QuetrisPlayerModel.NUM_OF_COLS][QuetrisPlayerModel.NUM_OF_ROWS]; 
    	for (int x = 0; x < QuetrisPlayerModel.NUM_OF_COLS; x++) {
    		for (int y = 0; y < QuetrisPlayerModel.NUM_OF_ROWS; y++) {
    			data [x][y] = -1;
    			if (y < 15)
    				data [x][y] = (int)(Math.random() * 12);
    		}
    	}
		CommQuetrisPlayerState state = new CommQuetrisPlayerState (1, data);
		CommQuetrisPlayerState stateFromElm = new CommQuetrisPlayerState (state.flatten());
		for (int x = 0; x < QuetrisPlayerModel.NUM_OF_COLS; x++)
    		for (int y = 0; y < QuetrisPlayerModel.NUM_OF_ROWS; y++)
    			assertEquals(state.getGridData()[x][y], stateFromElm.getGridData()[x][y]);
		assertEquals(state.getSeatNum(), stateFromElm.getSeatNum());
	}
} 