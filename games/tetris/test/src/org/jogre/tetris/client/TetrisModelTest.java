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
import nanoxml.XMLElement;

/**
 * Test case for game model for the tetris game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class TetrisModelTest extends TestCase {

    /**
     * Test the model.
     */
    public void testModels () {
        TetrisModel model = new TetrisModel();
        for (int i = 0; i < 2; i++) {
        	TetrisPlayerModel pModel = model.getPlayerModel(i); 
        	int [][] data = new int[TetrisPlayerModel.NUM_OF_COLS][TetrisPlayerModel.NUM_OF_ROWS]; 
        	for (int x = 0; x < TetrisPlayerModel.NUM_OF_COLS; x++) {
        		for (int y = 0; y < TetrisPlayerModel.NUM_OF_ROWS; y++) {
        			data [x][y] = -1;
        			if (y < 15)
        				data [x][y] = (int)(Math.random() * 7);
        		}
        	}
        	pModel.setGameData(data);
        	pModel.setCurShapeX(7); pModel.setCurShapeY(3);
        	pModel.setNumOfLines((int)(Math.random() * 1000));
        	pModel.setScore((int)(Math.random() * 50));
        	pModel.setNextShape (3);
        }
        
        // Flatten and reconstruct from XML
    	XMLElement elm = model.flatten();		
    	TetrisModel modelFromElm = new TetrisModel ();
    	modelFromElm.setState(elm);
    	
    	for (int i = 0; i < 2; i++) {
        	TetrisPlayerModel pModel = model.getPlayerModel(i); 
        	TetrisPlayerModel pModelFromElm = modelFromElm.getPlayerModel(i);
        	for (int x = 0; x < TetrisPlayerModel.NUM_OF_COLS; x++)
        		for (int y = 0; y < TetrisPlayerModel.NUM_OF_ROWS; y++)
        			assertEquals(pModel.getGridShapeNum(x, y), pModelFromElm.getGridShapeNum(x, y));
        	assertEquals (pModel.getCurShapeNum(),  pModelFromElm.getCurShapeNum());
        	assertEquals (pModel.getCurShapePos(),  pModelFromElm.getCurShapePos());
        	assertEquals (pModel.getCurShapeX(),    pModelFromElm.getCurShapeX());
        	assertEquals (pModel.getCurShapeY(),    pModelFromElm.getCurShapeY());
        	assertEquals (pModel.getNextShapeNum(), pModelFromElm.getNextShapeNum());
        	assertEquals (pModel.getNumOfLines(),   pModelFromElm.getNumOfLines());
        	assertEquals (pModel.getScore(),        pModelFromElm.getScore());
    	}	
    }
}