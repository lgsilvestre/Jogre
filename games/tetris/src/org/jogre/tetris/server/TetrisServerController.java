/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.tetris.server;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;
import org.jogre.tetris.client.TetrisModel;
import org.jogre.tetris.client.TetrisPlayerModel;
import org.jogre.tetris.common.CommTetrisMove;
import org.jogre.tetris.common.CommTetrisPlayerState;

/**
 * Constructor for a tetris game.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TetrisServerController extends ServerController {

    /**
     * Construrctor for a tetris server controller.
     * 
     * @param gameKey
     */
    public TetrisServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Start the game.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
        setModel (tableNum, new TetrisModel ());
    }
    
    /* (non-Javadoc)
     * @see org.jogre.server.ServerController#receiveObject(org.jogre.common.JogreModel, nanoxml.XMLElement)
     */
    public void receiveObject (JogreModel model, XMLElement message) {
	    String name = message.getName();
	    TetrisModel tetrisModel = (TetrisModel)model;
	    
	    // Simple tetris move - i.e. next shape
	    if (name.equals (CommTetrisMove.XML_NAME)) {
	        CommTetrisMove commMove = new CommTetrisMove (message);
	        
	        // Retrieve player model from seat in message
	        TetrisPlayerModel playerModel = 
	            tetrisModel.getPlayerModel (commMove.getSeatNum());
	        
	        // Add shape to move.
	        playerModel.addShape (
	            commMove.getCurShapeNum(),
	            commMove.getCurShapePos(),
	            commMove.getCurShapeX(),
	            commMove.getCurShapeY(),
	            commMove.getNextShapeNum());
	    }
	    // Entire player state updated (used in lines have been filled or added)
	    else if (name.equals (CommTetrisPlayerState.XML_NAME)) {
	        // Update the model with the extra lines
	        CommTetrisPlayerState commState = new CommTetrisPlayerState (message);
	        
	        TetrisPlayerModel playerModel = 
	            tetrisModel.getPlayerModel (commState.getSeatNum());
	        playerModel.setGameData (commState.getGridData());
	    }
    }
    
    /**
     * Called when a game is over.
     * 
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
        if (isGamePlaying (tableNum)) {
            gameOver (conn, tableNum, conn.getUsername(), resultType);
            
        }
    }
}
