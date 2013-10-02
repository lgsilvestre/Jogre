/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
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
package org.jogre.octagons.client;

import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.octagons.common.CommOctagonsMakeMove;

// Controller for the Octagons game
public class OctagonsController extends JogreController {

	// links to game data and the board component
	protected OctagonsModel model;
	protected OctagonsBoardComponent boardComponent;

	// A location that is initialized to be "nowhere" and never changed.
	private OctLoc nowhereLoc;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param boardComponent		The board component
	 */
	public OctagonsController(OctagonsModel model, OctagonsBoardComponent boardComponent) {
		super(model, boardComponent);

		this.model = model;						// set fields
		this.boardComponent = boardComponent;

		this.nowhereLoc = new OctLoc ();
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.reset_game ();
		boardComponent.reset_game();
	}

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
    	if (isGamePlaying () && isThisPlayersTurn ()) {
			// Convert the graphical (x,y) location to an OctLoc
			OctLoc newLocation = boardComponent.convertPointToLocation(mEv.getX(), mEv.getY());

			// If the location has moved, then need to draw the new outline
			if (newLocation.equals(boardComponent.getCurrMouseLocation()) == false) {
				boardComponent.setCurrMouseLocation(newLocation);
				boardComponent.repaint();
			}
		}
	}

	/**
	 * Get the player_id value for the model that corresponds to the current player seat.
	 *
	 * @return		The current player id
	 *				PLAYER_ONE or PLAYER_TWO
	 */
	private int getCurrPlayer() {
		return ( (getCurrentPlayerSeatNum() == 0) ? OctagonsModel.PLAYER_ONE : OctagonsModel.PLAYER_TWO );
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical (x,y) location to an OctLoc
			OctLoc theLoc = boardComponent.convertPointToLocation(mEv.getX(), mEv.getY());

			// Try to make the move on the local model
			int curr_playerId = getCurrPlayer();
			if (model.make_play(theLoc, curr_playerId) == true) {
				// The play was valid, so send a message to the other client
				CommOctagonsMakeMove theMove = new CommOctagonsMakeMove (conn.getUsername(), theLoc, curr_playerId);
				sendObject (theMove.flatten());

				// See if the game is over yet
				if (checkGameOver() == false) {
					// See if this player's turn is over.
					if (model.turn_over()) {
						// Yup, it's time for the next player's turn
						nextPlayer();

						// Kill the current mouse location so that it isn't drawn outlined anymore.
						boardComponent.setCurrMouseLocation(nowhereLoc);
					}
				}
			}
		}
	}

	/**
	 * Handle receiving objects from the server
	 *
	 * @param	message			The message from the server
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (XMLElement message) {
        String messageType = message.getName();

        if (messageType.equals (CommOctagonsMakeMove.XML_NAME)) {
			// Decode the message into a move
            CommOctagonsMakeMove theMove = new CommOctagonsMakeMove (message);
			// Make the move
			model.make_play(theMove.getLoc(),theMove.getPlayerId());
        }
	}

	/* Note: Jogre Alpha 0.2.3 has a typo, where it uses "recieveObject" instead of
		"receiveObject".  So, this method converts from the wrong name to the right name */
	public void recieveObject (XMLElement message) {
		receiveObject(message);
	}

	/**
	 * Check to see if the game is over or not.  If the game is over, this will send a game
	 *	over message to the server.
	 *
	 * @return		true = Game over
	 * @return		falst = Game not over
	 */
	private boolean checkGameOver () {

		if (model.getWinner() == OctagonsModel.PLAYER_NONE) {
			// Game isn't over yet
			return false;
		}

		// Create a GameOver object since I have won.
		// (This is only called after I make a move, so if the game is over
		// then I must be the winner)
		if (conn != null) {
			CommGameOver gameOver = new CommGameOver (IGameOver.WIN);
			conn.send (gameOver);
		}

		return true;
	}

}
