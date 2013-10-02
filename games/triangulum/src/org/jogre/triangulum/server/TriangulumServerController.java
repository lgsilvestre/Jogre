/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
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
package org.jogre.triangulum.server;

import org.jogre.common.Table;
import org.jogre.common.PlayerList;
import org.jogre.common.Player;
import org.jogre.common.IGameOver;

import org.jogre.triangulum.common.CommTriangulumMakeMove;
import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;

import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

import nanoxml.XMLElement;

import java.awt.Point;

/**
 * Server controller for a game of Triangulum.  It stores a TriangulumModel
 * on a running Jogre server and receives input from clients playing. It
 * can do further processing on the server side.
 *
 * The main aim of this class is to stop hacked Triangulum clients.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumServerController extends ServerController {

	/**
	 * Constructor to create a triangulum controller.
	 *
	 * @param gameKey  Game key.
	 */
	public TriangulumServerController (String gameKey) {
		super (gameKey);
	}

	/**
	 * Create a new triangulum model when the game starts.
	 *
	 * @see org.jogre.server.ServerController#startGame(int)
	 */
	public void startGame (int tableNum) {
		Table theTable = getTable(tableNum);

		// Get the table properties
		int numPlayers = theTable.getNumOfPlayers();
		int flavor = Integer.parseInt(theTable.getProperty("flavor"));

		// Create the model & attach it to the table
		setModel (tableNum, new TriangulumServerModel (flavor, numPlayers));
	}

	/**
	 * Override this method to send the initial player hand tiles.
	 */
	public void sendInitialClientMessages (ServerConnectionThread conn, int tableNum) {
		Table theTable = getTable(tableNum);
		PlayerList playerList = theTable.getPlayerList();

		TriangulumServerModel model = (TriangulumServerModel) getModel(tableNum);

		// Give the players their initial tiles
		int numPlayers = theTable.getNumOfPlayers();
		int piecesPerPlayer = model.getNumPiecesPerPlayer();

		for (int i=0; i<numPlayers; i++) {
			String playerName = playerList.getPlayer(i).getPlayerName();
			for (int j=0; j<piecesPerPlayer; j++) {
				conn.transmitToTablePlayers(
				        tableNum,
				        new CommTriangulumMakeMove(
				                playerName,
				                model.getPlayerPiece(i, j),
				                j,
				                null));
			}
		}
	}

	/**
	 * Handle receiving messages from the clients.
	 *
	 * @param conn       The connection receiving the message.
	 * @param message    The message from the client.
	 * @param tableNum   The table number that the client is playing on.
	 *
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
		String messageType = message.getName();

		if (messageType.equals(CommTriangulumMakeMove.XML_NAME)) {
			handleMakeMove(conn, tableNum, new CommTriangulumMakeMove (message));
		}
	}

	/**
	 * Handle a make move request.
	 *
	 * @param conn         The connection from the client
	 * @param tableNum     The table number that the client is playing at.
	 * @param theMoveMsg   The move message from the client.
	 */
	private void handleMakeMove (ServerConnectionThread conn, int tableNum, CommTriangulumMakeMove theMoveMsg) {
		String userName = theMoveMsg.getUsername();
		TriangulumServerModel model = (TriangulumServerModel) getModel(tableNum);
		int seatNum = getSeatNum(userName, tableNum);

		boolean needsNewPiece = false;

		Point dest = theMoveMsg.getLocation();
		int handIndex = theMoveMsg.getHandIndex();

		if (dest == null) {
			// The player is attempting to discard a hand tile.
			needsNewPiece = model.canDiscard(seatNum, handIndex);
		} else {
			// The player is attempting to play a tile.
			if (attemptToPlay(model, seatNum, dest, handIndex, theMoveMsg.getPiece())) {
				// Tell everyone this move.
				conn.transmitToTablePlayers(userName, tableNum, theMoveMsg);
				needsNewPiece = true;
			}
		}

		if (needsNewPiece) {
			// Need to give out a new piece to the player to take the place
			// of the piece just played.
			TriangulumPiece newPiece = model.getNextPieceFromBag();
			model.givePiece(seatNum, newPiece, handIndex);

			// Tell everyone the new piece
			CommTriangulumMakeMove newMsg = new CommTriangulumMakeMove(userName, newPiece, handIndex, null);
			conn.transmitToTablePlayers(tableNum, newMsg);
		}
	}

	/*
	 * Attempt to make the play and return the status of success.
	 *
	 * @param model      The model the move is being played on.
	 * @param seatNum    The seat number of the player making the move.
	 * @param dest       The logical place on the board where the tile is being placed.
	 * @param handIndex  The index in the player's hand that the piece is moving from.
	 * @param piece      The piece being played.
	 * @return true => Move was valid and made.
	 *         false => Move was invalid.
	 */
	private boolean attemptToPlay(TriangulumServerModel model, int seatNum, Point dest, int handIndex, TriangulumPiece piece) {
		if (piece == null) {
			return false;
		}

		if (!piece.isSamePiece(model.getPlayerPiece(seatNum, handIndex))) {
			return false;
		}

		return model.makeMove(seatNum, piece, dest);
	}

	/**
	 * This method is called when a client says that the game is over.
	 * Note: For Triangulum, when one of the clients sends the game over message,
	 *       the server will ignore the result given by the client and
	 *       determine it's own set of win/loss/tie results.
	 *
	 * @see org.jogre.server.ServerController#gameOver(int)
	 */
	public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {

		// Verify that the game is over.
		TriangulumServerModel model = (TriangulumServerModel) getModel(tableNum);
		if (!model.isGameOver()) {
			return;
		}

		Table theTable = getTable(tableNum);
		int numPlayers = theTable.getNumOfPlayers();

		// Find the highest score
		int [] scores = model.getScores();
		int maxScore = 0;
		for (int i=0; i<numPlayers; i++) {
			maxScore = Math.max(maxScore, scores[i]);
		}

		// Create the result array with Win/Lose as appropriate for each player
		int [] resultArray = new int [numPlayers];
		String scoreString = "";
		for (int i=0; i<numPlayers; i++) {
			resultArray[i] = (scores[i] == maxScore) ? IGameOver.WIN : IGameOver.LOSE;
			scoreString = scoreString + " " + scores[i];
		}

		// Call the gameOver routine in the parent to indicate that the game is now over.
		gameOver(conn,
		         tableNum,
		         theTable.getPlayerList().getInGamePlayers(),
		         resultArray,
		         scoreString,
		         null);
	}
}
