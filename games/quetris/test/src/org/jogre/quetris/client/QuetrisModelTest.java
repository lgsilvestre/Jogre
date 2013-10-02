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
import nanoxml.XMLElement;

/**
 * Test case for game model for the quetris game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class QuetrisModelTest extends TestCase {

    /**
     * Test the model.
     */
    public void testModels () {
        QuetrisModel model = new QuetrisModel();
        for (int i = 0; i < 2; i++) {
        	QuetrisPlayerModel pModel = model.getPlayerModel(i); 
        	int [][] data = new int[QuetrisPlayerModel.NUM_OF_COLS][QuetrisPlayerModel.NUM_OF_ROWS]; 
        	for (int x = 0; x < QuetrisPlayerModel.NUM_OF_COLS; x++) {
        		for (int y = 0; y < QuetrisPlayerModel.NUM_OF_ROWS; y++) {
        			data [x][y] = -1;
        			if (y < 15)
        				data [x][y] = (int)(Math.random() * 12);
        		}
        	}
        	pModel.setGameData(data);
        	pModel.setCurShapeX(7); pModel.setCurShapeY(3);
        	pModel.setNumOfLines((int)(Math.random() * 1000));
        	pModel.setScore((int)(Math.random() * 50));
        	pModel.setNextShape (3);
        	pModel.setNextShape2 (6);
        }
        
        // Flatten and reconstruct from XML
    	XMLElement elm = model.flatten();		
    	QuetrisModel modelFromElm = new QuetrisModel ();
    	modelFromElm.setState(elm);
    	
    	for (int i = 0; i < 2; i++) {
        	QuetrisPlayerModel pModel = model.getPlayerModel(i); 
        	QuetrisPlayerModel pModelFromElm = modelFromElm.getPlayerModel(i);
        	for (int x = 0; x < QuetrisPlayerModel.NUM_OF_COLS; x++)
        		for (int y = 0; y < QuetrisPlayerModel.NUM_OF_ROWS; y++)
        			assertEquals(pModel.getGridShapeNum(x, y), pModelFromElm.getGridShapeNum(x, y));
        	assertEquals (pModel.getCurShapeNum(),  pModelFromElm.getCurShapeNum());
        	assertEquals (pModel.getCurShapePos(),   pModelFromElm.getCurShapePos());
        	assertEquals (pModel.getCurShapeX(),     pModelFromElm.getCurShapeX());
        	assertEquals (pModel.getCurShapeY(),     pModelFromElm.getCurShapeY());
        	assertEquals (pModel.getNextShapeNum(),  pModelFromElm.getNextShapeNum());
        	assertEquals (pModel.getNextShapeNum2(), pModelFromElm.getNextShapeNum2());
        	assertEquals (pModel.getNumOfLines(),    pModelFromElm.getNumOfLines());
        	assertEquals (pModel.getScore(),         pModelFromElm.getScore());
    	}	
    }
}
