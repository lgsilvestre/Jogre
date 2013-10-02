/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.chess.client;

import nanoxml.XMLElement;

import junit.framework.TestCase;

/**
 * Test case for game model for the chess game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ChessModelTest extends TestCase {

    /**
     * Test the model.
     */
    public void testBasic () throws Exception {
        ChessModel model = new ChessModel();
        ChessPieceMover mover = new ChessPieceMover (model);
        model.executeMove(4, 6, 4, 4);
        model.executeMove(4, 1, 4, 3);
        model.executeMove(3, 6, 3, 4);
        model.executeMove(4, 3, 3, 4);
        model.executeMove(2, 6, 2, 5);
        model.executeMove(3, 4, 2, 5);
        model.executeMove(2, 5, 1, 6);
        model.executeMove(2, 7, 1, 6);
        model.executeMove(6, 0, 5, 2);
        model.executeMove(1, 7, 2, 5);
        
        // Flatten and reconstruct from XML
    	XMLElement elm = model.flatten();		
    	ChessModel modelFromElm = new ChessModel ();
    	modelFromElm.setState(elm);
    	
    	// Check integrity of object created from element
    	assertEquals (model.getCapturedPieces(), modelFromElm.getCapturedPieces());
    	assertEquals (model.getLastMove(), modelFromElm.getLastMove());
    	
    	// Check board
    	for (int i = 0; i < 64; i++) {
    		assertEquals (model.getPiece(i), modelFromElm.getPiece(i));
    		assertEquals (model.getPieceColour(i), modelFromElm.getPieceColour(i));
    	}
    	
    	for (int i = 0; i < ChessModel.NUM_OF_FLAGS; i++) 
    		assertEquals (model.flag(0), modelFromElm.flag(1));
    	
    	// Check  historys
    	GameHistory h1 = model.getGameHistory(), h2 = modelFromElm.getGameHistory();
    	assertEquals (h1.size(), h2.size());
    	for (int i = 0; i < h1.size(); i++) 
    		assertTrue (h1.getMove(i).equals(h2.getMove(i)));
    }
    
    /**
     * Check 2 arrays of integers 
     * 
     * @param values1cc
     * @param values2
     * @throws Exception
     */
    private void assertEquals (int [] values1, int [] values2) throws Exception {
    	 assertEquals(values1.length, values2.length);
    	 for (int i = 0; i < values1.length; i++)
    		 assertEquals(values1[i], values2[i]);
    }
}