/*
 * JOGRE (Java Online Gaming Real-time Engine) - Reversi
 * Copyright (C) 2005  Ugnich Anton (anton@portall.zp.ua)
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
package org.jogre.reversi.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

public class ReversiController extends JogreController {

	// links to game data and the board component
	protected ReversiModel model;
	protected ReversiBoardComponent boardComponent;

	// Constructor
	public ReversiController(ReversiModel model,
			ReversiBoardComponent boardComponent) {
		super(model, boardComponent);

		this.model = model; // set fields
		this.boardComponent = boardComponent;
	}

	/**
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start() {
		model.reset();
	}
	
	public void mouseMoved(MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn()) { // ensure game has

			// get board point from screen position of mouse click
			Point board = boardComponent.getBoardCoords(e.getX(), e.getY());

			// ensure board point is in range (0 to 2)
			if (board.x >= 0 && board.x < ReversiModel.COLS && board.y >= 0
					&& board.y < ReversiModel.ROWS) {
				if (model.piecesThatCanBeCaptured(board.x, board.y, getSeatNum()) > 0) {
					boardComponent.setMousePoint(board);
					return;
				}
			}
		}
		boardComponent.setMousePoint(null);
	}

	// Overwrite the mousePressed method
	public void mousePressed(MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn()) { // ensure game has
														// started
			// get board point from screen position of mouse click
			Point board = boardComponent.getBoardCoords(e.getX(), e.getY());

			// ensure board point is in range (0 to 2)
			if (board.x >= 0 && board.x < ReversiModel.COLS && board.y >= 0
					&& board.y < ReversiModel.ROWS) {

				// check model at this point is BLANK and it is this persons go
				if (model.getData(board.x, board.y) == ReversiModel.BLANK) {
					int value = getCurrentPlayerSeatNum();
					move(board.x, board.y, value); // move

					checkGameOver(); // check to see if the games over
				}
			}
		}
	}

	// Update the model
	public void move(int x, int y, int value) {
		// update model and change player turn
		if (model.setData(x, y, value)) {
			
			this.boardComponent.setMousePoint(null);

			// next player turn.
			int opponentSeat = this.model.invert(this.getCurrentPlayerSeatNum());
			boolean hasMovesLeftOpponent = model.hasMovesLeft(opponentSeat);
			if (hasMovesLeftOpponent)
				nextPlayer();
			
			// send move to other user
			sendProperty("move", x * ReversiModel.COLS + y, value);
		}
	}

	public void receiveProperty(String key, int xy, int value) {
		model.setData((int) (xy / ReversiModel.COLS), xy % ReversiModel.COLS,
				value);
	}

	/**
	 * Check if game is over.  After making a move, this method is called
	 * which checks if the other player has a move left or all spaces on
	 * the board have already been taken.
	 */
	private void checkGameOver() {
		// We just made a move, check and see if all spaces
		// are taken up or other player does not have a move.
		int status = -1;
		int opponentSeat = this.model.invert(this.getCurrentPlayerSeatNum());
		int winner = model.getWinner(opponentSeat);
		
		if (winner == getCurrentPlayerSeatNum())
			status = IGameOver.WIN;
		else if (winner == this.model.invert(getCurrentPlayerSeatNum()))
			status = IGameOver.LOSE;
		else if (winner == -1)
			status = IGameOver.DRAW;

		if (winner > -2) {
			CommGameOver gameOver = new CommGameOver (status);
			conn.send(gameOver);
		}
	}
}