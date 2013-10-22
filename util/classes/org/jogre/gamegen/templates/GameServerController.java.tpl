filename=%game_id%/src/org/jogre/%game_id%/server/%Game_id%ServerController.java
/*
 * JOGRE (Java Online Gaming Real-time Engine) - %Game_id%
 * Copyright (C) 2004 - %year%  %Author% (%email%)
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
package org.jogre.%game_id%.server;

import nanoxml.XMLElement;

import org.jogre.%game_id%.common.%Game_id%Model;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for a game of %game_id%.  It stores a %Game_id%Model
 * on a running Jogre server and receives input from clients playing. It
 * can do further processing on the server side.
 *
 * The main aim of this class is to stop hacked %game_id% clients.
 *
 * @author  %Author%
 * @version %Version%
 */
public class %Game_id%ServerController extends ServerController {

    /**
     * Constructor to create a %game_id% controller.
     *
     * @param gameKey  Game key.
     */
    public %Game_id%ServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new %game_id% model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
        setModel (tableNum, new %Game_id%Model ());
    }

    /**
     * This method is called when a client says that the game
     * is over.
     *
     * @see org.jogre.server.ServerController#gameOver(int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
        // TODO - Fill in
        //gameOver (conn, tableNum, conn.getUsername(), resultType);
    }
}
