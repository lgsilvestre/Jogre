/*
 * JOGRE (Java Online Gaming Real-time Engine) - Connect4
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
package org.jogre.connect4.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

/**
 * Controller for a game of connect 4.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class Connect4Controller extends JogreController {

	// links to game data and the board component
	protected Connect4Model model;
	protected Connect4BoardComponent boardComponent;

	/**
	 * Connect 4 controller.
	 * 
	 * @param model           Connect 4 model.
	 * @param boardComponent  Connect 4 visual board component.
	 */
	public Connect4Controller(Connect4Model model, Connect4BoardComponent boardComponent) {
		super(model, boardComponent);

		this.model = model;						// set fields
		this.boardComponent = boardComponent;
	}
	
	/**
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.reset ();
	}

	// Overwrite the mousePressed method
	public void mousePressed (MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn ()) {			// ensure game has started
			// get board point from screen position of mouse click
			Point board = boardComponent.getBoardCoords (e.getX(), e.getY());

			// ensure board point is in range (0 to 2)
			if (board.x >= 0 && board.x < Connect4Model.COLS &&
				board.y >= 0 && board.y < Connect4Model.ROWS) {

				// check model at this point is BLANK and it is this persons go
				if (model.getData(board.x, 0) == Connect4Model.BLANK) {
					int value = getCurrentPlayerSeatNum();
					move (board.x, value);		// move
					
					checkGameOver();
				}
			}
		}
	}
	
	/** 
	 * Show the column that the mouse is on.
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent e) {
		if (isGamePlaying () && isThisPlayersTurn ()) {
			Point point = boardComponent.getBoardCoords (e.getX(), e.getY());
			
			// If user has moved to a new column
			if (point.x != boardComponent.getCurMousePoint()) {
				// Check to see if there is room left
				if (model.getData(point.x, 0) != Connect4Model.BLANK)
					boardComponent.setCurMousePoint (-1);
				else
					boardComponent.setCurMousePoint (point.x);
				
				// update the board
				boardComponent.repaint();
			}
		}
	}

	// Update the model
	public void move (int x, int value) {
		boardComponent.setCurMousePoint (-1);
		
		// update model and change player turn
		Point p = model.setData (x, value);
		
		// next player turn.
		nextPlayer ();
		
		// send move to other user
		sendProperty ("move", x, value);
	}
	
	/**
	 * Override the receive property method.
	 * 
	 * @see org.jogre.client.JogreController#receiveProperty(java.lang.String, int, int)
	 */
	public void receiveProperty (String key, int x, int value) {
		model.setData(x, value);
	}

	/**
	 * Check to see if the game is over or not.
	 */
	private void checkGameOver () {
		
		Point lastMove = this.model.getLastMove();
		
		// Status is either -1, DRAW or WIN
		int status = -1;

		if (model.isGameWon(getSeatNum(), lastMove.x, lastMove.y))
			status = IGameOver.WIN;
		else if (model.isNoCellsLeft ())
			status = IGameOver.DRAW;

		// Create game over object if a win or draw
		if (status != -1 && conn != null) {
			CommGameOver gameOver = new CommGameOver (status);
			conn.send (gameOver);
		}
	}
}