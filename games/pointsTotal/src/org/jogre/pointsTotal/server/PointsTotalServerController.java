/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.pointsTotal.server;

import nanoxml.XMLElement;

import java.awt.Point;

import org.jogre.common.Table;
import org.jogre.common.IGameOver;

import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.pointsTotal.common.CommPointsTotalMove;

import org.jogre.common.comm.CommNextPlayer;

import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for a game of pointsTotal.  It stores a PointsTotalModel
 * on a running Jogre server and receives input from clients playing. It
 * can do further processing on the server side.
 *
 * The main aim of this class is to stop hacked pointsTotal clients.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalServerController extends ServerController {

    /**
     * Constructor to create a pointsTotal controller.
     *
     * @param gameKey  Game key.
     */
    public PointsTotalServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new pointsTotal model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
		Table theTable = getTable(tableNum);
		int numPlayers = theTable.getNumOfPlayers();
		setModel (tableNum, new PointsTotalModel (numPlayers));
    }

	/**
	 * Handle receving messages from the clients.
	 *
	 * @param conn      Connection to a client.
	 * @param message   Message.
	 * @param tableNum  Table number of message.
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
		String messageType = message.getName();

		if (messageType.equals(CommPointsTotalMove.XML_NAME)) {
			handleMove(conn, tableNum, new CommPointsTotalMove(message));
		}
	}

	/**
	 * Handle the Move message from a client.
	 *
	 * @param conn          Connection to a client
	 * @param tableNum      The table the client is at.
	 * @param theMoveMsg    The message containing the move.
	 */
	private void handleMove(ServerConnectionThread conn,
	                        int tableNum,
	                        CommPointsTotalMove theMoveMsg) {
		// Get the model for this game.
		PointsTotalModel model = (PointsTotalModel) getModel(tableNum);

		// Get the playerId of the player making the move.
		String userName = theMoveMsg.getUsername();
		int playerId = getSeatNum (userName, tableNum);

		// Get the location of the move.
		Point playLocation = theMoveMsg.getLocation();

		if ((model.getCurrentPlayer() == playerId) &&
		     model.validMove(playLocation)) {

			// Make the move in the model
			model.makeMove(playerId, theMoveMsg.getPiece(), playLocation);

			// Tell everyone else about the move
			conn.transmitToTablePlayers(userName, tableNum, theMoveMsg);

			// Ask the model who the next player is and set the next player to it.
			int nextPlayerSeat = model.getCurrentPlayer();
			if (nextPlayerSeat < 0) {
				// Game is over
				signalGameOver(conn, tableNum, model);
			} else {
				// Tell the system who the next player is
				getTable(tableNum).nextPlayer(nextPlayerSeat);
			}
		}
	}

	/*
	 * Generate the results when the game is over.
	 *
	 * @param  conn          Connection to a client
	 * @param  tableNum      The table number to signal game over
	 * @param  model         The model at that table
	 */
	 private void signalGameOver(ServerConnectionThread conn,
	                             int tableNum,
	                             PointsTotalModel model) {

		// Create the arrays of player names & results
		int [] resultArray = new int [model.getNumPlayers()];
		String scoreString = "";

		for (int i=0; i<resultArray.length; i++) {
			if (model.isTie()) {
				resultArray[i] = IGameOver.DRAW;
			} else {
				resultArray[i] = (model.isWinner(i) ? IGameOver.WIN : IGameOver.LOSE);
			}
			scoreString = scoreString + " " + model.getScore(i);
		}

		// Call the gameOver routine to indicate that the game is now over.
		gameOver(conn,
				 tableNum,
				 getTable(tableNum).getPlayerList().getInGamePlayers(),
				 resultArray,
				 scoreString,
				 null);
		}

    /**
     * This method is called when a client says that the game is over.
     * Since Points Total keeps track of game over in the server, this routine
     * isn't needed and does nothing.
     *
     * @see org.jogre.server.ServerController#gameOver(int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {}
}
