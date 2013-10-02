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
package org.jogre.quetris.server;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.quetris.client.QuetrisModel;
import org.jogre.quetris.client.QuetrisPlayerModel;
import org.jogre.quetris.common.CommQuetrisMove;
import org.jogre.quetris.common.CommQuetrisPlayerState;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for a game of quetris.  It stores a QuetrisModel
 * on a running Jogre server and receives input from clients playing. It
 * can do further processing on the server side.
 *
 * The main aim of this class is to stop hacked quetris clients.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class QuetrisServerController extends ServerController {

    /**
     * Constructor to create a quetris controller.
     *
     * @param gameKey  Game key.
     */
    public QuetrisServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new quetris model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
        setModel (tableNum, new QuetrisModel ());
    }

    /* (non-Javadoc)
     * @see org.jogre.server.ServerController#receiveObject(org.jogre.common.JogreModel, nanoxml.XMLElement)
     */
    public void receiveObject (JogreModel model, XMLElement message) {
	    String name = message.getName();
	    QuetrisModel QuetrisModel = (QuetrisModel)model;
	    
	    // Simple quetris move - i.e. next shape
	    if (name.equals (CommQuetrisMove.XML_NAME)) {
	        CommQuetrisMove commMove = new CommQuetrisMove (message);
	        
	        // Retrieve player model from seat in message
	        QuetrisPlayerModel playerModel = 
	            QuetrisModel.getPlayerModel (commMove.getSeatNum());
	        
	        // Add shape to move.
	        playerModel.addShape (
	            commMove.getCurShapeNum(),
	            commMove.getCurShapePos(),
	            commMove.getCurShapeX(),
	            commMove.getCurShapeY(),
	            commMove.getNextShapeNum());
	    }
	    // Entire player state updated (used in lines have been filled or added)
	    else if (name.equals (CommQuetrisPlayerState.XML_NAME)) {
	        // Update the model with the extra lines
	        CommQuetrisPlayerState commState = new CommQuetrisPlayerState (message);
	        
	        QuetrisPlayerModel playerModel = 
	            QuetrisModel.getPlayerModel (commState.getSeatNum());
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
