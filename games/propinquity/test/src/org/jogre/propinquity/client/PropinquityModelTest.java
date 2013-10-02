/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import junit.framework.TestCase;

/**
 * Test case for game model for the propinquity game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropinquityModelTest extends TestCase {

    /**
     * Test the model.
     */
    public void test () {
        PropinquityModel model = new PropinquityModel();
        int player = 0;
        for (int i = 0; i < 40; i++) {   
        	model.setAttackNum((int)(Math.random() * Cell.MAX_AMOUNT) + 1);
        	int index = (int)(Math.random() * 99);
        	if (model.getGridData(index).getState() == Cell.CELL_BLANK) 
        		model.playerMove(player, index);
        	player = player == 0 ? 1 : 0;
        }
        
        // Create model from state
        PropinquityModel modelFromElm = new PropinquityModel ();
        modelFromElm.setState(model.flatten());
        
        for (int i = 0; i < model.getNumOfCells (); i++) {
        	assertEquals (model.getGridData(i).getArmies(), modelFromElm.getGridData(i).getArmies());
        	assertEquals (model.getGridData(i).getState(), modelFromElm.getGridData(i).getState());
        }
    }
}
