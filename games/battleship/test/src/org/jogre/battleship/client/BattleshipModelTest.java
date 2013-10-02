/*
 * JOGRE (Java Online Gaming Real-time Engine) - Battleship
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
package org.jogre.battleship.client;

import java.awt.Point;

import junit.framework.TestCase;
import nanoxml.XMLElement;

/**
 * Test case for game model for the battleship game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class BattleshipModelTest extends TestCase {

    /**
     * Test the model.
     * 
     * 5, 4, 3, 3, 2
     */
    public void test () {
    	// Create battleship model
        BattleshipModel model = new BattleshipModel();
        
        // Place ships
        model.placeShip (0, new Point (1,1), 5, 1, true);
        model.placeShip (0, new Point (3,3), 4, 2, false);
        model.placeShip (0, new Point (5,3), 3, 3, true);
        model.placeShip (0, new Point (1,6), 3, 4, false);
        model.placeShip (0, new Point (6,6), 2, 5, true);
        model.placeShip (1, new Point (3,0), 5, 1, true);
        model.placeShip (1, new Point (2,2), 4, 2, false);
        model.placeShip (1, new Point (5,5), 3, 3, true);
        model.placeShip (1, new Point (0,6), 3, 4, false);
        model.placeShip (1, new Point (5,7), 2, 5, true);
        
        // Start sinking ships
        model.setMove(0, 2, 2);
        model.setMove(0, 3, 4);
        model.setMove(0, 4, 2);
        model.setMove(0, 5, 5);
        model.setMove(0, 6, 2);
        model.setMove(0, 3, 3);
        model.setMove(0, 6, 5);
        model.setMove(0, 4, 4);
        model.setMove(0, 2, 3);
        model.setMove(0, 1, 2);
        model.setMove(1, 1, 0);
        model.setMove(1, 2, 1);
        model.setMove(1, 3, 3);
        model.setMove(1, 4, 2);
        model.setMove(1, 3, 4);
        model.setMove(1, 2, 6);
        model.setMove(1, 3, 4);
        model.setMove(1, 1, 2);
        model.setMove(1, 4, 3);
        model.setMove(1, 5, 1);
        
        // Flatten and reconstruct from XML
    	XMLElement elm = model.flatten();		
    	BattleshipModel modelFromElm = new BattleshipModel ();
    	modelFromElm.setState(elm);
    	
    	// Check integrity of object created from element
    	for (int i = 0; i < 2; i++) {
    		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
    			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
    				assertEquals (model.getHitsAndMisses()[i][x][y], modelFromElm.getHitsAndMisses()[i][x][y]);
    				assertEquals (model.getPlacedShips()[i][x][y], modelFromElm.getPlacedShips()[i][x][y]);
    			}    				
    		}
    	}
    }
}
