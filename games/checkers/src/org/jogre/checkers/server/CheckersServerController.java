/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.checkers.server;

import nanoxml.XMLElement;

import org.jogre.checkers.client.CheckersModel;
import org.jogre.checkers.client.CheckersPieceMover;
import org.jogre.checkers.common.CommCheckersMove;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

public class CheckersServerController extends ServerController {

	/**
	 * Constructor to create a chess controller.
	 * 
	 * @param gameKey  Game key.
	 */
	public CheckersServerController (String gameKey) {
		super (gameKey);
	}
	
    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	setModel (tableNum, new CheckersModel ());
    }
	
    /**
     * Receive checkers object.
     * 
     * @see org.jogre.server.ServerController#receiveObject(org.jogre.common.JogreModel, nanoxml.XMLElement)
     */
    public void receiveObject (JogreModel model, XMLElement object) { 
	    if (object.getName().equals(CommCheckersMove.XML_NAME)) {
			CommCheckersMove move = new CommCheckersMove (object);
			((CheckersModel)model).executeMove (move);
		}
    }

    /**
     * Declare that the game is over.
     * 
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
    	CheckersPieceMover pieceMover = new CheckersPieceMover ((CheckersModel)getModel(tableNum));
    	int opponent = JogreUtils.invert (getSeatNum (conn.getUsername(), tableNum));
    	
    	// Rule 1 - if opponents has no pieces left then its game over.
		int count = pieceMover.getPlayerCount (opponent);

		// Create game over object if a win or draw
		if (count == 0 && conn != null) {
			// Update the server data
			gameOver (conn, tableNum, conn.getUsername(), IGameOver.WIN);
		}		
    }
}