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
package org.jogre.tictactoe.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;
import org.jogre.tictactoe.client.TicTacToeModel;
import org.jogre.tictactoe.common.CommTicTacToeMove;

/**
 * This is the server table parser for a game of checkers.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TictactoeServerController extends ServerController {
    
    /**
     * Constructor for a game of tictactoe.
     * 
     * @param gameKey
     */
    public TictactoeServerController (String gameKey) {
        super (gameKey);
    }
    
    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	setModel (tableNum, new TicTacToeModel());
    }
    
	/**
	 * Parse a checkers message.
	 *
	 * @see org.jogre.server.ITableParser#parseTableMessage(org.jogre.server.ServerConnectionThread, nanoxml.XMLElement)
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
	    // Retrieve the message type
		String messageType = message.getName();

		// Parse the message
		if (messageType.equals (CommTicTacToeMove.XML_NAME)) {
		    // unpack message
			CommTicTacToeMove move = new CommTicTacToeMove (message);
			
			movePiece(conn, tableNum, move);		    
		}
	}

	/**
	 * Move the tic-tac-toe piece.
	 * 
	 * @param conn      Connection to server.
	 * @param tableNum  Table number.
	 * @param move      TicTacToe move.
	 */
	private void movePiece (ServerConnectionThread conn, int tableNum, CommTicTacToeMove move) {
		// retrieve table num and username
		String username = move.getUsername();
		
		// Update server model
		TicTacToeModel model = (TicTacToeModel) getModel (tableNum);
		model.setData (move.getX(), move.getY(), move.getValue());
		
		// Trasmit to other players
		conn.transmitToTablePlayers (username, tableNum, move);
		
		// Check to see if the game is over
		checkGameOver (conn, tableNum);
	}
	
	/**
	 * Check to see if the game is over or not.
	 * 
	 * @param conn       Connection to the server.
	 * @param model      TicTacToe model.
	 * @param tableNum   Table number.
	 */
	private void checkGameOver (ServerConnectionThread conn, int tableNum) {		  
		// Status is either -1, DRAW or WIN
		int resultType = -1;
		int curSeatNum = getSeatNum (conn.getUsername(), tableNum);
		TicTacToeModel model = (TicTacToeModel) getModel (tableNum);
		boolean isGameOver = model.isGameWon (curSeatNum); 
		if (isGameOver) {
			resultType = IGameOver.WIN;
		}
		else if (model.isNoCellsLeft ()) {  
			resultType = IGameOver.DRAW;
		}

		// Create game over object if a win or draw
		if (resultType != -1 && conn != null) {
			// Update the server data
			gameOver (conn, tableNum, conn.getUsername(), resultType);
		}		
	}

	public void gameOver(ServerConnectionThread conn, int tableNum, int resultType) {
		// TODO Auto-generated method stub
		
	}
}