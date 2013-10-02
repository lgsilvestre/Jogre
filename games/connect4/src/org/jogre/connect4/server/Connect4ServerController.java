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
package org.jogre.connect4.server;

import java.awt.Point;

import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.connect4.client.Connect4Model;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Create chess server controller.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class Connect4ServerController extends ServerController {

	/**
	 * Constructor to create a chess controller.
	 * 
	 * @param gameKey  Game key.
	 */
	public Connect4ServerController (String gameKey) {
		super (gameKey);
	}
	
    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	setModel (tableNum, new Connect4Model ());
    }
    
    /**
     * Receive connect 4 move.
     * 
     * @see org.jogre.server.ServerController#receiveProperty(org.jogre.common.JogreModel, java.lang.String, int, int)
     */
    public void receiveProperty (JogreModel model, String key, int x, int y) { 
		((Connect4Model)model).setData (x, y);
    }
    
    /**
     * This method is called when a client says that the game
     * is over.
     * 
     * @see org.jogre.server.ServerController#gameOver(int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
    	Connect4Model model = ((Connect4Model)getModel(tableNum));
    	int player = getSeatNum(conn.getUsername(), tableNum);
    	
		// Status is either -1, DRAW or WIN
    	int status = -1;		
		Point lastMove = model.getLastMove();
		if (model.isGameWon(player, lastMove.x, lastMove.y))
			status = IGameOver.WIN;
		else if (model.isNoCellsLeft ())
			status = IGameOver.DRAW;

		// Create game over object if a win or draw
		if (status != -1 && conn != null) {
			// Update the server data
			gameOver (conn, tableNum, conn.getUsername(), status);
		}		
    }
}
