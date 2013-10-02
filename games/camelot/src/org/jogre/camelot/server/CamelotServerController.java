/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
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
package org.jogre.camelot.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.JogreUtils;
import org.jogre.camelot.client.CamelotModel;
import org.jogre.camelot.common.CommCamelotMakeMove;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for the game Camelot
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class CamelotServerController extends ServerController {

    /**
     * Constructor to create an Camelot controller.
     *
     * @param gameKey  Game key.
     */
    public CamelotServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
        setModel (tableNum, new CamelotModel ());
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
		String messageType = object.getName();

		if (messageType.equals (CommCamelotMakeMove.XML_NAME)) {
			// Decode the message into a move
			CommCamelotMakeMove theMoveMsg = new CommCamelotMakeMove (object);
			((CamelotModel)model).makeMove(theMoveMsg.getMove());
		}
	}

    /**
     * Verify that the game is over.
	 * (A client has just told us that the game is over, but we ought to verify it)
     *
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
		CamelotModel model = (CamelotModel) getModel(tableNum);
		int winner = model.getWinner();
		int seat_num = getSeatNum(conn.getUsername(), tableNum);

		if (winner == CamelotModel.PLAYER_NONE) {
			// No one is the obvious winner, so need to check if the requesting
			// player really has no valid moves
			winner = model.updateValidStart(seat_num);
		}

		if (winner == CamelotModel.PLAYER_ONE_FLAG) {
			gameOver (	conn,
						tableNum,
						conn.getUsername(),
						((seat_num == 0) ? IGameOver.WIN : IGameOver.LOSE));
		} else if (winner == CamelotModel.PLAYER_TWO_FLAG) {
			gameOver (	conn,
						tableNum,
						conn.getUsername(),
						((seat_num == 0) ? IGameOver.LOSE : IGameOver.WIN));
		} else if (winner == CamelotModel.PLAYER_NEVER_FLAG) {
			gameOver (	conn,
						tableNum,
						conn.getUsername(),
						IGameOver.DRAW);
		} else if (winner == CamelotModel.PLAYER_NONE) {
			// The end-of-game was erroneously reported.
			// What to do?
		}

	}

}
