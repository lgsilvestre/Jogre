/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.chineseCheckers.client;

import java.awt.event.MouseEvent;
import java.awt.Point;

import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

import org.jogre.chineseCheckers.common.CommChineseCheckersMakeMove;

/**
 * Controller for the Chinese Checkers game
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class ChineseCheckersController extends JogreController {

	// links to game data and the board component
	protected ChineseCheckersModel model;
	protected ChineseCheckersBoardComponent boardComponent;


	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param boardComponent		The board component
	 */
	public ChineseCheckersController (ChineseCheckersModel model, ChineseCheckersBoardComponent boardComponent) {
		super(model, boardComponent);

		// Save parameters.
		this.model = model;
		this.boardComponent = boardComponent;
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.resetGame();
		boardComponent.setActiveMarbleHex(ChineseCheckersBoardComponent.OFF_SCREEN_POINT);
	}

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved (MouseEvent mEv) {
    	if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical (x,y) location to a logical hex
			Point mouseLoc = boardComponent.getBoardCoords(mEv.getX(), mEv.getY());

			// Movement can always select one of our own marbles.
			boolean validHighlight = (model.getOwner(mouseLoc) == getSeatNum());

			// If there is a selected marble, then movement can also select valid moves.
			if (model.exists(boardComponent.getActiveMarbleHex())) {
				Vector theMoveVector = model.getMoveVector(mouseLoc);
				boardComponent.setHighlightMove(theMoveVector, getSeatNum());
				validHighlight |= (theMoveVector != null);
			}

			// If it is valid to highlight the space, then do so, otherwise
			// set the highlighted space to off the board.
			boardComponent.setHighlightPoint(validHighlight ? mouseLoc : ChineseCheckersBoardComponent.OFF_SCREEN_POINT);
		}
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {

		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical (x,y) location to a logical hex
			Point mouseLoc = boardComponent.getBoardCoords(mEv.getX(), mEv.getY());
			Point currActiveMarble = boardComponent.getActiveMarbleHex();
			if (model.getOwner(mouseLoc) == getSeatNum()) {
				// The user is clicking on one of his own marbles.
				if (mouseLoc.equals(currActiveMarble)) {
					// The user is clicking on the current active marble, so deactivate
					// the active marble.
					boardComponent.setActiveMarbleHex(ChineseCheckersBoardComponent.OFF_SCREEN_POINT);
					boardComponent.setHighlightMove(null, 0);
					model.clearMoveVectors();
				} else {
					// The user is clicking on a non-active marble, so activate it.
					boardComponent.setActiveMarbleHex(mouseLoc);
					model.setValidSpaces(mouseLoc);
					boardComponent.setHighlightPoint(mouseLoc);
				}
			} else {
				// The user is clicking somewhere other than his own marble.
				Vector theMoveVector = model.getMoveVector(mouseLoc);
				if ((theMoveVector != null)  && (model.exists(currActiveMarble))) {
					// There is a selected marble, so clicking should make a move.
					model.makeMove(theMoveVector);
					CommChineseCheckersMakeMove theMove = new CommChineseCheckersMakeMove(
					                                          conn.getUsername(),
					                                          theMoveVector,
					                                          getSeatNum());
					sendObject (theMove.flatten());

					// Clear the highlights from the board.
					boardComponent.setActiveMarbleHex(ChineseCheckersBoardComponent.OFF_SCREEN_POINT);
					boardComponent.setHighlightPoint(ChineseCheckersBoardComponent.OFF_SCREEN_POINT);
					boardComponent.setHighlightMove(null, 0);
					model.clearMoveVectors();

					if (!checkGameOver()) {
						nextPlayer();
					}
				}
			}

			// Refresh the view
			model.refreshObservers();
		}
	}

	/**
	 * Receive a message from the server that indicates the other player's
	 * move.
	 *
	 * @param	message			The message from the server
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals (CommChineseCheckersMakeMove.XML_NAME)) {
			// Decode the message into a move
			CommChineseCheckersMakeMove theMove = new CommChineseCheckersMakeMove (message);

			// Set the move on the board
			boardComponent.setHighlightMove(theMove.getMoveVector(), getSeatNum(theMove.getUsername()));

			// Make the move
			model.makeMove(theMove.getMoveVector());
        }
	}

	/*
	 * Check to see if the game is over or not.  If the game is over, this will
	 * send a game over message to the server.
	 *
	 * @return		true = Game over
	 * @return		false = Game not over
	 */
	private boolean checkGameOver () {

		// This is only called after I make a move, so I only need to check if
		// I am the winner.
		if (model.checkWinner(getSeatNum())) {
			conn.send (new CommGameOver(IGameOver.WIN));
			return true;
		}

		// Game isn't over yet
		return false;
	}
}
