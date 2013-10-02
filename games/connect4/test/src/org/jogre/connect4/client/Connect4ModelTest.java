/*
 * JOGRE (Java Online Gaming Real-time Engine) - Connect4
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
package org.jogre.connect4.client;

import junit.framework.TestCase;
import nanoxml.XMLElement;

/**
 * Test case for game model for the connect4 game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class Connect4ModelTest extends TestCase {

    /**
     * Test the model.
     */
    public void test () {
        Connect4Model model = new Connect4Model();
        model.setData(2, Connect4Model.RED);
        model.setData(3, Connect4Model.YELLOW);
        model.setData(2, Connect4Model.RED);
        model.setData(4, Connect4Model.YELLOW);
        model.setData(1, Connect4Model.RED);
        model.setData(2, Connect4Model.YELLOW);
        model.setData(3, Connect4Model.RED);
        model.setData(4, Connect4Model.YELLOW);
        model.setData(3, Connect4Model.RED);
        model.setData(2, Connect4Model.YELLOW);
        
        XMLElement elm = model.flatten();		
    	Connect4Model modelFromElm = new Connect4Model ();
    	modelFromElm.setState(elm);
    	
    	// Check integrity of object created from element
    	for (int x = 0; x < Connect4Model.COLS; x++) 
			for (int y = 0; y < Connect4Model.ROWS; y++) 
				assertEquals (model.getData(x, y), modelFromElm.getData(x, y));
    }
}
