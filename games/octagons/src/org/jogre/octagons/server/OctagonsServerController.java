/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons Server
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.octagons.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.JogreUtils;
import org.jogre.octagons.client.OctagonsModel;
import org.jogre.octagons.client.OctLoc;
import org.jogre.octagons.common.CommOctagonsMakeMove;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for the game Octagons
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class OctagonsServerController extends ServerController {

    /**
     * Constructor to create an octagons controller.
     * 
     * @param gameKey  Game key.
     */
    public OctagonsServerController (String gameKey) {
        super (gameKey);
    }
    
    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
        setModel (tableNum, new OctagonsModel ());
    }

  	/**
	 * Handle receiving objects from the clients
	 *
	 * By the time this is called, all of the clients have already been copied
	 * on the message.  So, this doesn't have to do that.
	 *
	 * @param	model			The model that the message is being made against
	 * @param	object			The object from the client
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (JogreModel model, XMLElement object) {
        if (object.getName().equals (CommOctagonsMakeMove.XML_NAME)) {
			// Make the move on the server model
            CommOctagonsMakeMove theMove = new CommOctagonsMakeMove (object);
			((OctagonsModel)model).make_play(theMove.getLoc(), theMove.getPlayerId());
        }
	}
   
	/**
	 * Get the player_id value for the given seat number.
	 *
	 * @param 	seatNum			The seat number
	 * @return					The player id that corresponds to that seat number.
	 *							PLAYER_ONE or PLAYER_TWO
	 */
	private int getPlayerId(int seatNum) {
		return ((seatNum == 0) ? OctagonsModel.PLAYER_ONE : OctagonsModel.PLAYER_TWO );
	}

	/**
	 * Get the player_id value for the opponent of a given player
	 *
	 * @param 	playerId		A playerId
	 * @return					The player id that corresponds to the opponent.
	 *							PLAYER_ONE or PLAYER_TWO
	 */
	private int getOpponentPlayerId(int playerId) {
		return ((playerId == OctagonsModel.PLAYER_TWO) ? OctagonsModel.PLAYER_ONE : OctagonsModel.PLAYER_TWO );
	}

    /**
     * Verify that the game is over.
	 * (A client has just told us that the game is over, but we ought to verify it)
     * 
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
		OctagonsModel model = (OctagonsModel) getModel(tableNum);
		int winner = model.getWinner();
		int this_id = getPlayerId(getSeatNum(conn.getUsername(), tableNum));

		if (winner == this_id) {
			gameOver (conn, tableNum, conn.getUsername(), IGameOver.WIN);
		} else if (winner == getOpponentPlayerId(this_id)) {
			gameOver (conn, tableNum, conn.getUsername(), IGameOver.LOSE);
		}
	}

}
