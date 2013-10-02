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
package org.jogre.reversi.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.JogreUtils;
import org.jogre.reversi.client.ReversiModel;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for the game reversi.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ReversiServerController extends ServerController {

    /**
     * Constructor to create a reversi controller.
     * 
     * @param gameKey  Game key.
     */
    public ReversiServerController (String gameKey) {
        super (gameKey);
    }
    
    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
        setModel (tableNum, new ReversiModel ());
    }

    /**
     * Receive checkers object.
     * 
     * @see org.jogre.server.ServerController#receiveObject(org.jogre.common.JogreModel, nanoxml.XMLElement)
     */
    public void receiveProperty (JogreModel model, String key, int xy, int value) { 
        if (key.equals("move")) {            
            ((ReversiModel)model).setData((int) (xy / ReversiModel.COLS), xy % ReversiModel.COLS,
                   value);
        }
    }
    
    /**
     * Implementation of a game over method.
     * 
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
        ReversiModel model = (ReversiModel)getModel(tableNum);
        int player   = getSeatNum (conn.getUsername(), tableNum);
        int opponent = JogreUtils.invert (player);        
        int winner = model.getWinner(opponent);
        
        int result = -1;
        if (winner == player)
            result = IGameOver.WIN;
        else if (winner == opponent)
            result = IGameOver.LOSE;
        else if (winner == -1)
            result = IGameOver.DRAW;
        
        if (winner > -2) {            
            // Update the server data
            gameOver (conn, tableNum, conn.getUsername(), result);
        }
    }
}
