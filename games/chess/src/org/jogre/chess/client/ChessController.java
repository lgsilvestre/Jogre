/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.chess.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.chess.common.CommChessMove;
import org.jogre.client.JogreController;
import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Controller for the chess game.
 */
public class ChessController extends JogreController {

	// links to game data and the board component
	protected ChessModel chessModel;
	protected ChessBoardComponent boardComponent;

	/**
	 * Default constructor for the chess controller.
	 *
	 * @param chessModel
	 * @param boardComponent
	 * @param conn
	 */
	public ChessController (
		ChessModel chessModel,					// link to players game data
		ChessBoardComponent boardComponent
	) {
		super (chessModel, boardComponent);

		this.chessModel = chessModel;
		this.boardComponent = boardComponent;
	}

	/**
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		chessModel.reset ();
	}

	/**
	 * @param releasedPoint
	 * @param pressedPoint
	 */
	public void executeMove (ChessMove opponentsMove) {
		// update this players data with the move
		chessModel.executeMove(opponentsMove);
		boardComponent.repaint ();
	}

	/**
	 * Returns the opponent player to the one who is currently playing.
	 *
	 * @return
	 */
	public int getCurrentOpponentPlayer () {
		return getCurrentPlayerSeatNum() == IChessModel.PLAYER_ONE ?
			IChessModel.PLAYER_TWO : IChessModel.PLAYER_ONE;
	}

	/**
	 * Implementation of the mouse pressed interface.  If the correct player is
	 * trying to lift the correct colour piece then take a note of its pressed
	 * board co-ordinate (0..7, 0..7).
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn ()) {
			// get mouse co-ordinates
			int x = e.getX();
			int y = e.getY();

			// reset mouse
			boardComponent.setPressedPoint(AbstractBoardComponent.OFF_SCREEN_POINT);

			int size = boardComponent.getWidth();
			int cellSpacing = boardComponent.getCellSpacing();

			if (x >= cellSpacing && x < size && y >= cellSpacing && y < size) {
				Point pressedPoint = boardComponent.getBoardCoords (x, y);
				boardComponent.setPressedPoint (pressedPoint);

				// Check user isn't trying to lift nothing
				if (pressedPoint.x >= 0 && pressedPoint.x < 8 && pressedPoint.y >= 0 && pressedPoint.y < 8) {

					// retrieve colour and make sure the current player turn is
					// clicking the  right colour of piece
					int chessPieceColour = chessModel.getPieceColour (pressedPoint.x, pressedPoint.y);

					if (chessModel.getPiece(pressedPoint.x, pressedPoint.y) != ChessModel.EMPTY &&
						chessPieceColour == getCurrentPlayerSeatNum ()
					) {
						boardComponent.setDragPoint (pressedPoint);

						chessModel.updateAllowedMoves (pressedPoint.x, pressedPoint.y);
						boardComponent.repaint ();
						return;
					}

				}
			}

			boardComponent.setPressedPoint (AbstractBoardComponent.OFF_SCREEN_POINT);
		}
	}

	/**
	 * Implementation of the mouse released interface.
	 *
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (isGamePlaying()) {
			// get mouse co-ordinates
			int mouseX = e.getX();
			int mouseY = e.getY();

			Point releasedPoint = null;

			chessModel.resetAllowedMoves();
			int size = boardComponent.getWidth();
			int cellSpacing = boardComponent.getCellSpacing();

			if (mouseX >= cellSpacing && mouseX < size && mouseY >= cellSpacing && mouseY < size) {
				releasedPoint = boardComponent.getBoardCoords (mouseX, mouseY);
				// retrieve the pressed point on the board
				Point pressedPoint = boardComponent.getPressedPoint();

				// Create chess move
				ChessMove move = new ChessMove
					(pressedPoint.x, pressedPoint.y, releasedPoint.x, releasedPoint.y);

				// Execute the chess move on local game data
				boolean validMove = chessModel.executeMove (move);

				// If this chess move was valid and there exists a connection to a
				// chess server then send the move down the connection
				if (conn != null && validMove) {
					CommChessMove commMove = new CommChessMove (move);
					sendObject (commMove);

					// Check to see if the game is over or not
					if (!checkGameOver ()) {
						// if game still in progress then continue on...
						nextPlayer ();
					}
				}
			}
		}

		// reset mouse
		boardComponent.resetPoints();
		boardComponent.repaint ();
	}

	/**
	 * Check to see if the game is over or not.  If the game is over then send
	 * a game over to the server.
	 *
	 * @return  Returns true if the game is over.
	 */
	private boolean checkGameOver () {
		// Retrieve piece mover from the chess model
		ChessPieceMover pieceMover = chessModel.getPieceMover();

		// Find out who the opponent is
		int opponentPlayer = getCurrentOpponentPlayer();

		// Status is either -1, DRAW or WIN
		int status = -1;
		if (pieceMover.isGameADraw(opponentPlayer))
			status = IGameOver.DRAW;
		else if (pieceMover.isPlayerInCheckMate(opponentPlayer))
			status = IGameOver.WIN;

		// Create game over object if a win or draw
		if (status != -1 && conn != null) {
			CommGameOver gameOver = new CommGameOver (status);
			conn.send (gameOver);
			return true;
		}

		return false;
	}

	/**
	 * Implementation of the mouse dragged interface.
	 *
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		// only do anything when the mouse is being dragged
		if (!boardComponent.getPressedPoint().equals(AbstractBoardComponent.OFF_SCREEN_POINT)) {
			int mouseX = e.getX();
			int mouseY = e.getY();

			int size = boardComponent.getWidth();
			int cellSpacing = boardComponent.getCellSpacing();
			Point previousDragPoint = boardComponent.getDragPoint();
			if (mouseX >= cellSpacing && mouseX < size && mouseY >= cellSpacing && mouseY < size) {
				// compute the new drag point
				Point newDragPoint = boardComponent.getBoardCoords (mouseX, mouseY);

				// if the user hasn't dragged into a previous square don't
				// bother repainting the component
				if (!newDragPoint.equals (previousDragPoint)) {
					boardComponent.setDragPoint(newDragPoint);
					boardComponent.repaint ();
				}
			}
		}
	}

	/**
	 * Receive data objects from other clients (chess move).
	 *
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (XMLElement object) {
		if (object.getName().equals(CommChessMove.XML_NAME)) {
			CommChessMove move = new CommChessMove (object);
			executeMove (move.getMove());
		}
	}
}