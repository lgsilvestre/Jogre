/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2003 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.client;

import java.awt.event.MouseEvent;
import java.awt.Point;

import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;
import org.jogre.triangulum.common.CommTriangulumMakeMove;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

import nanoxml.XMLElement;

/**
 * Controller for the Triangulum game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumController extends JogreController {

	// links to game data and the board component
	protected TriangulumModel     model;
	protected TriangulumComponent component;

	/**
	 * Default constructor for the triangulum controller which takes a
	 * model and a view.
	 *
	 * @param model      Triangulum model class.
	 * @param component  Triangulum view class.
	 * @param conn
	 */
	public TriangulumController (
		TriangulumModel     model,         // link to players game data
		TriangulumComponent component      // Link to game view
	) {
		super (model, component);

		this.model     = model;
		this.component = component;
	}

	/**
	 * Start method which restarts the model.
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.reset();
	}
	
	/**
	 * Handle mouse movement events
	 *
	 * @param mEv   The mouse event
	 */
	public void mouseMoved (MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			int x = mEv.getX();
			int y = mEv.getY();
			if (component.selectSmallIcon(x,y)) {
				// The mouse was within one of the small icons and has now
				// been selected within the component.
				return;
			}

			// Convert the graphical (x,y) location to a logical board space
			Point mouseLoc = component.getBoardCoords(x, y);

			// See if the mouse is on the main board
			if (component.existsOnBoard(mouseLoc)) {
				if (model.getMultiplier(mouseLoc.x, mouseLoc.y) != 0) {
					// Highlight point is on the main board.
					component.setBoardHighlightPoint(mouseLoc);
					return;
				}
			}

			// See if the mouse is in the player's hand tile area
			int tileIndex = component.decodeHandTile(x, y);
			component.setHandTileHighlightPoint(tileIndex);
		}
	}

	/**
	 * Implementation of the mouse pressed interface.
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn ()) {
			int seatNum = getCurrentPlayerSeatNum();

			// See if we have an active piece being placed on the board
			TriangulumPiece activePiece = component.getActivePiece();
			if (activePiece != null) {
				Point activeLoc = component.getHighlightPoint();

				// Try to play this piece on the board
				boolean valid = model.makeMove(seatNum, activePiece, activeLoc);
				if (valid) {
					// Yea! Valid move, so tell the server
					int handIndex = component.getActiveHandTileIndex();

					// Remove the piece from the player's hand so that we can
					// correctly check for game over.
					model.givePiece(seatNum, null, handIndex);

					finishMove(
					      new CommTriangulumMakeMove (
					            conn.getUsername(),
					            activePiece,
					            handIndex,
					            activeLoc));
/*
					// Clear the highlights
					component.clearHighlights();

					// Send the message to the server
					CommTriangulumMakeMove moveMsg =
					      new CommTriangulumMakeMove (
					            conn.getUsername(),
					            activePiece,
					            handIndex,
					            activeLoc);
					conn.send(moveMsg);

					// Indicate the next player
					if (!checkGameOver()) {
						nextPlayer();
					}
*/
				}
				return;
			}

			// See if the mouse is in the player's hand tile area
			int handIndex = component.decodeHandTile(mEv.getX(), mEv.getY());
			if (model.numValidMovesForPiece(seatNum, handIndex) > 0) {
				component.setActivatedHandTile(handIndex);
			} else if (model.canDiscard(seatNum, handIndex)) {
				// Tell the server
				finishMove (
				      new CommTriangulumMakeMove (
				            conn.getUsername(),
				            null,
				            handIndex,
				            null));
			}
		}
	}

	/*
	 * Handle finishing a move.
	 * This is common stuff between placing a tile on the board and discarding
	 * a tile.
	 */
	private void finishMove (CommTriangulumMakeMove moveMsg)
	{
		// Clear the highlights on the board
		component.clearHighlights();

		// Send the message to the server
		conn.send(moveMsg);

		// Indicate the next player
		if (!checkGameOver()) {
			nextPlayer();
		}
	}
	
	/**
	 * Handle a table message from the server.
	 *
	 * @param   message   The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();
		if (messageType.equals(CommTriangulumMakeMove.XML_NAME)) {
			handleMakeMove(new CommTriangulumMakeMove(message));
		}
	}

	/**
	 * Handle a make move message from the server.
	 *
	 * @param   moveMsg   The message from the server
	 */
	public void handleMakeMove (CommTriangulumMakeMove moveMsg) {
		int seatNum = getSeatNum(moveMsg.getUsername());
		TriangulumPiece piece = moveMsg.getPiece();

		Point dest = moveMsg.getLocation();

		if (dest == null) {
			// This is a new piece being given to our hand
			model.givePiece(seatNum, piece, moveMsg.getHandIndex());
		} else {
			// The piece is being played to the board
			model.makeMove(seatNum, piece, dest);
		}
	}

	/*
	 * Check to see if the game is over and, if so, send a message to the
	 * server telling it.
	 * Note: I always send WIN as the type, but the server will determine the
	 *       correct win/lose/tie results and ignores what I send.
	 */
	private boolean checkGameOver () {
		if (model.isGameOver()) {
			conn.send (new CommGameOver(IGameOver.WIN));
			return true;
		} 
		return false;
	}
}
