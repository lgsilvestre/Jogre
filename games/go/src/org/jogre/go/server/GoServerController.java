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
package org.jogre.go.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.Table;
import org.jogre.go.client.GoModel;
import org.jogre.go.client.GoPieceMover;
import org.jogre.go.client.GoScore;
import org.jogre.go.common.CommGoMove;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Go server controller.
 * 
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoServerController extends ServerController {

    /**
     * Construrctor for a go server controller.
     * 
     * @param gameKey
     */
    public GoServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Start game of "go".
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	Table table = getTable(tableNum);
    	int boardSize = Integer.parseInt(table.getProperty("size", String.valueOf(GoModel.DEFAULT_BOARD_SIZE)));
    	int komi = Integer.parseInt(table.getProperty("komi")); 
    	int score_type = Integer.parseInt(table.getProperty("score_type"));
    	    	
    	setModel (tableNum, new GoModel (boardSize, score_type, komi));
    }

    /**
     * Game over of "go".
     * 
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
    	GoPieceMover pieceMover = new GoPieceMover ((GoModel)getModel(tableNum));
    	GoScore goScore = pieceMover.getScore();
		int winner = goScore.getWinningPlayer();
		String winningPlayer = getPlayerName(winner, tableNum);
		gameOver (conn, tableNum, winningPlayer, IGameOver.WIN);
    }
    
    /**
     * Receive chess object.
     * 
     * @see org.jogre.server.ServerController#receiveObject(org.jogre.common.JogreModel, nanoxml.XMLElement)
     */
    public void receiveObject (JogreModel model, XMLElement object, int tableNum) { 
	    if (object.getName().equals(CommGoMove.XML_NAME)) {
			CommGoMove move = new CommGoMove (object);
			GoModel goModel = ((GoModel)model);
			GoPieceMover pieceMover = new GoPieceMover (goModel);
			
			String username = move.getUsername();
			int player = getSeatNum(username, tableNum);
			switch (move.getStatus()) {
				case CommGoMove.TYPE_MOVE:
					pieceMover.move(move.getX(), move.getY(), player);
					break;
				case CommGoMove.TYPE_MARK:
					pieceMover.mark(move.getX(), move.getY());
					break;
				case CommGoMove.TYPE_PASS:
					pieceMover.setPass(player);
					break;
				case CommGoMove.TYPE_HAPPY:
					pieceMover.setHappy(player);
					break;
				case CommGoMove.TYPE_UNHAPPY:
					pieceMover.setUnhappy(player);
					break;
			}
			
			// If bother players are happy then the game is over
			if (goModel.getGameState() == GoModel.STATE_BOTH_HAPPY) {
				GoScore goScore = pieceMover.getScore();
				int winner = goScore.getWinningPlayer();
				String winningPlayer = getPlayerName(winner, tableNum);
				ServerConnectionThread conn = getServerConnectionThread (move.getUsername());
				gameOver (conn, tableNum, winningPlayer, IGameOver.WIN);				
			}				
		}
    }
}