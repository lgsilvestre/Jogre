/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
 * Copyright (C) 2004 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.warwick.server;

import nanoxml.XMLElement;

import org.jogre.warwick.common.WarwickModel;
import org.jogre.warwick.common.CommWarwickChooseAllegience;
import org.jogre.warwick.common.CommWarwickSlidePiece;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

import org.jogre.common.Table;
import org.jogre.common.IGameOver;

/**
 * Server controller for a game of Warwick.  It stores a WarwickModel
 * on a running Jogre server and receives input from clients playing. It
 * can do further processing on the server side.
 *
 * The main aim of this class is to stop hacked warwick clients.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickServerController extends ServerController {

	/**
	 * Constructor to create a warwick controller.
	 *
	 * @param gameKey  Game key.
	 */
	public WarwickServerController (String gameKey) {
		super (gameKey);
	}

	/**
	 * Create a new warwick model when the game starts.
	 *
	 * @see org.jogre.server.ServerController#startGame(int)
	 */
	public void startGame (int tableNum) {
		Table theTable = getTable(tableNum);
		int numPlayers = theTable.getNumOfPlayers();
		setModel (tableNum, new WarwickModel (numPlayers));
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

		if (messageType.equals(CommWarwickChooseAllegience.XML_NAME)) {
			handleChooseAllegience(conn, tableNum, new CommWarwickChooseAllegience(message));
		} else if (messageType.equals(CommWarwickSlidePiece.XML_NAME)) {
			handleSlidePiece(conn, tableNum, new CommWarwickSlidePiece(message));
		}
	}

	/*
	 * Handle the Move message from a client.
	 *
	 * @param conn          Connection to a client
	 * @param tableNum      The table the client is at.
	 * @param theMoveMsg    The message containing the move.
	 */
	private void handleChooseAllegience (ServerConnectionThread conn,
	                                     int tableNum,
	                                     CommWarwickChooseAllegience theMsg) {
		// Get the model for this game.
		WarwickModel model = (WarwickModel) getModel(tableNum);

		// Get the playerId of the player making the move.
		String userName = theMsg.getUsername();
		int playerId = getSeatNum (userName, tableNum);

		int chosenAlly = theMsg.getAllegience();

		// Verify validity of the choice
		if ((chosenAlly >= 0) &&
		    (chosenAlly <= model.getNumPlayers()) &&
		    (!model.isAllegienceChosen(playerId))) {
			// Set allegience.
			model.chooseAllegience(playerId, chosenAlly);

			// Tell everyone else that this player has chosen allegience
			//    (without releaving the allegience yet!)
			CommWarwickChooseAllegience newMsg = new CommWarwickChooseAllegience
			      (userName, WarwickModel.ALLEGIENCE_UNKNOWN);
			conn.transmitToTablePlayers(userName, tableNum, newMsg);

			// See if we're ready to begin placing pieces
			if (model.allChosenAllegience()) {
				model.setGamePhase(WarwickModel.PLACE_PIECE);
			}
		}
	}

	/*
	 * Handle the Slide piece message from a client.
	 *
	 * @param conn          Connection to a client
	 * @param tableNum      The table the client is at.
	 * @param theMoveMsg    The message containing the move.
	 */
	private void handleSlidePiece (ServerConnectionThread conn,
	                               int tableNum,
	                               CommWarwickSlidePiece theMsg) {
		// Get the model for this game.
		WarwickModel model = (WarwickModel) getModel(tableNum);

		// Get the playerId of the player making the move.
		String userName = theMsg.getUsername();
		int playerId = getSeatNum (userName, tableNum);

		// Get the region the piece is moving from
		int fromRegion = theMsg.getFromRegion();
		if (fromRegion < 0) {
			// This is a piece being played from off the board.
			if (model.addPiece(
			       playerId,
			       theMsg.getToRegion(), theMsg.getToSpace())) {
				// Valid move, so tell everyone else about the move
				conn.transmitToTablePlayers(userName, tableNum, theMsg);

				// Advance to the sliding phase.
				model.setGamePhase(WarwickModel.SLIDE_PIECE);
			}
		} else {
			// This is a piece sliding on the board.
			if (model.slidePiece(
			       fromRegion, theMsg.getFromSpace(),
			       theMsg.getToRegion(), theMsg.getToSpace())) {
				// Valid move, so tell everyone else about the move
				conn.transmitToTablePlayers(userName, tableNum, theMsg);

				// Advance to the next player
				int nextPlayerSeat = model.setNextPlayer();

				if (model.getPiecesToPlace(nextPlayerSeat) > 0) {
					// It is the next player's turn to move.
					model.setGamePhase(WarwickModel.PLACE_PIECE);
				} else {
					// All moves this round are done, so we need to score.
					doScore(conn, tableNum, model);
				}

				// Tell the Jogre table who the next player is
				getTable(tableNum).nextPlayer(model.getActivePlayerSeatNum());
			}
		}
	}

	/*
	 * Do the scoring phase.
	 *
	 * @param  conn          Connection to a client
	 * @param  tableNum      The table number to score
	 * @param  model         The model at that table
	 */
	private void doScore (ServerConnectionThread conn,
	                      int tableNum,
	                      WarwickModel model) {

		// Tell everyone what everyone else's chosen allegience was.
		// This allows the clients to determine the scores
		for (int p = 0; p < model.getNumPlayers(); p++) {
			String playerName = getPlayerName(p, tableNum);
			CommWarwickChooseAllegience theMsg = new CommWarwickChooseAllegience
			    (playerName, model.getAllegience(p));
			conn.transmitToTablePlayers(playerName, tableNum, theMsg);
		}

		// Do the scoring in the model
		if (model.updateScores()) {
			// The game is over.
			signalGameOver(conn, tableNum, model);
		} else {
			// The game is not over yet, so reset for the next round
			model.setNextPlayer(model.getCurrentRoundNumber());
			model.setGamePhase(WarwickModel.CHOOSE_ALLEGIENCE);
			model.clearBoard();
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
	                             WarwickModel model) {

		// Tell the model that the game is over.
		model.setGamePhase(WarwickModel.GAME_OVER);

		int numPlayers = model.getNumPlayers();

		// Find the best score
		int bestScore = -1;
		for (int i = 0; i < numPlayers; i++) {
			bestScore = Math.max(bestScore, model.getScore(i));
		}

		// Create the array of player results and string of scores
		int [] resultArray = new int [numPlayers];
		String scoreString = "";

		for (int i = 0; i < numPlayers; i++) {
			int score = model.getScore(i);
			resultArray[i] = (score == bestScore) ? IGameOver.WIN : IGameOver.LOSE;
			scoreString = scoreString + " " + score;
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
	 * Since Warwick keeps track of game over in the server, this routine
	 * isn't needed and does nothing.
	 *
	 * @see org.jogre.server.ServerController#gameOver(int)
	 */
	public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {}

}
