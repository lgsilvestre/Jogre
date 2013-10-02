/*
 * JOGRE (Java Online Gaming Real-time Engine) - Hex
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
package org.jogre.hex.client;

import java.awt.event.MouseEvent;
import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

import org.jogre.hex.common.CommHexMakeMove;

/**
 * Controller for the Hex game
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class HexController extends JogreController {

	// links to game data and the board component
	protected HexModel model;
	protected HexBoardComponent boardComponent;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param boardComponent		The board component
	 */
	public HexController (HexModel model, HexBoardComponent boardComponent) {
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

		// If we're player 1, then flip the board over.  This way both players
		// are trying to get from the upper-left to the lower-right from their
		// point of view.
		boardComponent.setOrientation(0, (getSeatNum() == 1));
		boardComponent.repaint();
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
			if (model.isValidPlay(mouseLoc.x, mouseLoc.y)) {
				boardComponent.setHighlightPoint(mouseLoc);
			} else {
				boardComponent.setHighlightPoint(HexBoardComponent.OFF_SCREEN_POINT);
			}
		}
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {

		if (isGamePlaying() && isThisPlayersTurn()) {
			Point mouseLoc = boardComponent.getBoardCoords(mEv.getX(), mEv.getY());

			int currPlayerSeat = getCurrentPlayerSeatNum();
			if (model.makeMove(mouseLoc.x, mouseLoc.y, currPlayerSeat)) {
				// The play was valid, so send a message to the server.
				CommHexMakeMove theMove = new CommHexMakeMove (
				                                conn.getUsername(),
				                                mouseLoc.x, mouseLoc.y,
				                                currPlayerSeat);
				sendObject (theMove.flatten());

				if (!checkGameOver ()) {
					// Unhighlight the mouse point
					boardComponent.setHighlightPoint(HexBoardComponent.OFF_SCREEN_POINT);

					// It's now the other player's turn
					nextPlayer();
				}
			}
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

		if (messageType.equals (CommHexMakeMove.XML_NAME)) {
			// Decode the message into a move
			CommHexMakeMove theMove = new CommHexMakeMove (message);
			// Make the move
			model.makeMove(theMove.getCol(), theMove.getRow(), theMove.getPlayerSeat());
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

		if (model.getWinner() == HexModel.PLAYER_NONE) {
			// Game isn't over yet
			return false;
		}

		// This is only called after I make a move, so if the game is over
		// then I must be the winner.
		conn.send (new CommGameOver(IGameOver.WIN));

		return true;
	}
}
