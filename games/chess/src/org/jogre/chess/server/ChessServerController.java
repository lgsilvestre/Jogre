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
package org.jogre.chess.server;

import nanoxml.XMLElement;

import org.jogre.chess.client.ChessModel;
import org.jogre.chess.client.ChessPieceMover;
import org.jogre.chess.common.CommChessMove;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Create chess server controller.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ChessServerController extends ServerController {

	/**
	 * Constructor to create a chess controller.
	 * 
	 * @param gameKey  Game key.
	 */
	public ChessServerController (String gameKey) {
		super (gameKey);
	}
	
    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	setModel (tableNum, new ChessModel ());
    }
    
    /**
     * This method is called when a client says that the game
     * is over.
     * 
     * @see org.jogre.server.ServerController#gameOver(int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
    	ChessPieceMover pieceMover = new ChessPieceMover ((ChessModel)getModel(tableNum));
    	int player = getSeatNum(conn.getUsername(), tableNum);
    	
    	int opponent = JogreUtils.invert (player);
		if (pieceMover.isGameADraw (opponent)) {
			resultType = IGameOver.DRAW;
		}
		else if (pieceMover.isPlayerInCheckMate(opponent)) {  
			resultType = IGameOver.WIN;
		}

		// Create game over object if a win or draw
		if (resultType != -1 && conn != null) {
			// Update the server data
			gameOver (conn, tableNum, conn.getUsername(), resultType);
		}		
    }
    
    /**
     * Receive chess object.
     * 
     * @see org.jogre.server.ServerController#receiveObject(org.jogre.common.JogreModel, nanoxml.XMLElement)
     */
    public void receiveObject (JogreModel model, XMLElement object) { 
	    if (object.getName().equals(CommChessMove.XML_NAME)) {
			CommChessMove move = new CommChessMove (object);
			((ChessModel)model).executeMove (move.getMove());
		}
    }
}
