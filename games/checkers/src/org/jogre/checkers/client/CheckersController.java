/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.checkers.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.checkers.common.CommCheckersMove;
import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.JogreUtils;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Controller for the checkers game.
 */
public class CheckersController extends JogreController {

	// links to game data and the board component
	protected CheckersModel checkersModel;
	protected CheckersBoardComponent boardComponent;

	/**
	 * @param gameModel
	 * @param boardComponent
	 */
	public CheckersController(
		CheckersModel checkersModel,
		CheckersBoardComponent boardComponent)
	{
		// Call super class
		super (checkersModel, (JogreComponent)boardComponent);

		// Set fields
		this.checkersModel = checkersModel;
		this.boardComponent = boardComponent;
	}

	/**
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		checkersModel.reset ();
	}

	/**
	 * Implementation of the mouse pressed interface.  If the correct player is
	 * trying to lift the correct colour piece then take a note of its pressed
	 * board co-ordinate (0..7, 0..7).
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (isGamePlaying () && isThisPlayersTurn ()) {
			// get mouse co-ordinates
			int mouseX = e.getX();
			int mouseY = e.getY();

			boardComponent.setPressedPoint(CheckersBoardComponent.OFF_SCREEN_POINT);

			int width = boardComponent.getWidth();
			int height = boardComponent.getHeight();
			int cellSpacing = boardComponent.getCellSpacing();

			if (mouseX >= cellSpacing && mouseX < width && mouseY >= cellSpacing && mouseY < height) {
				Point pressedPoint = boardComponent.getBoardCoords (mouseX, mouseY);
				boardComponent.setPressedPoint (pressedPoint);
				checkersModel.updateAllowedMoves (pressedPoint.x, pressedPoint.y);

				// Check user isn't trying to lift nothing
				if (pressedPoint.x >= 0 && pressedPoint.x < 8 && pressedPoint.y >= 0 && pressedPoint.y < 8) {

					// retrieve colour and make sure the current player turn is
					// clicking the  right colour of piece
					int checkersPieceColour = checkersModel.getPieceColour (pressedPoint.x, pressedPoint.y);

					if (checkersModel.getPiece (pressedPoint.x, pressedPoint.y) != ICheckersModel.EMPTY &&
						checkersPieceColour == getCurrentPlayerSeatNum ()
					) {
						boardComponent.setDragPoint (pressedPoint);

						boardComponent.repaint ();
						return;
					}
				}
			}
		}

		// reset mouse
		boardComponent.setPressedPoint(CheckersBoardComponent.OFF_SCREEN_POINT);
	}

	/**
	 * Implementation of the mouse released interface.
	 *
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (isGamePlaying ()) {
			// get mouse co-ordinates
			int mouseX = e.getX();
			int mouseY = e.getY();

			Point releasedPoint = null;

			int size = boardComponent.getWidth();
			int cellSpacing = boardComponent.getCellSpacing();

			if (mouseX >= cellSpacing && mouseX < size && mouseY >= cellSpacing && mouseY < size) {
				releasedPoint = boardComponent.getBoardCoords (mouseX, mouseY);
				// retrieve the pressed point on the board
				Point pressedPoint = boardComponent.getPressedPoint();

				// find out if this move is an attacking moves
				boolean moveIsAnAttack = checkersModel.getPieceMover().isAttackingMove (
					pressedPoint.x, pressedPoint.y, releasedPoint.x, releasedPoint.y);

				// execute the move
				boolean validMove = checkersModel.executeMove (
					pressedPoint.x, pressedPoint.y, releasedPoint.x, releasedPoint.y);

				// If this chess move was valid and there exists a connection to a
				// chess server then send the move down the connection
				if (conn != null && validMove) {
					int index1 = checkersModel.getBoardIndex (pressedPoint.x, pressedPoint.y);
					int index2 = checkersModel.getBoardIndex (releasedPoint.x, releasedPoint.y);

					// Create communications object for this move and send it
					CommCheckersMove commMove = new CommCheckersMove (index1, index2);
					sendObject (commMove);

					// Its this player has just killed another piece check to see
					// if the player can attack anymore.  Otherwise its the
					// other players turn
					CheckersPieceMover pieceMover = checkersModel.getPieceMover();
					int numOfAttackingMoves = pieceMover.countPossibleAttackingMoves(getSeatNum());
					if (!(moveIsAnAttack && numOfAttackingMoves != 0))
					{
						// if not game over then next players turn
						if (!checkGameOver ()) {
							// next players turn
							nextPlayer ();
						}
					}
				}
			}
		}

		checkersModel.resetAllowedMoves();

		// reset mouse
		boardComponent.resetPoints ();
		boardComponent.repaint ();
	}

	/**
	 * Receive data objects from other clients (checkers move).
	 *
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (XMLElement object) {
		if (object.getName().equals(CommCheckersMove.XML_NAME)) {
			CommCheckersMove move = new CommCheckersMove (object);
			checkersModel.executeMove (move);
		}
	}

	/**
	 * Check to see if the game is over.
	 */
	private boolean checkGameOver () {
		// check for game over or draw
		CheckersPieceMover pieceMover = checkersModel.getPieceMover();

		// Rule 1 - if opponents has no pieces left then its game over.
		int opponent = JogreUtils.invert (getSeatNum());
		int count = pieceMover.getPlayerCount (opponent);

		if (count == 0) {
			CommGameOver commGameOver = new CommGameOver (IGameOver.WIN);
			conn.send(commGameOver);
			return true;
		}
		return false;			// game still in progress
	}

	/**
	 * Returns the opponent player to the one who is currently playing.
	 *
	 * @return
	 */
	public int getCurrentOpponentPlayer () {
		return getCurrentPlayerSeatNum() == ICheckersModel.PLAYER_ONE ?
			ICheckersModel.PLAYER_TWO : ICheckersModel.PLAYER_ONE;
	}
}