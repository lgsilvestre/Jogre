/*
 * JOGRE (Java Online Gaming Real-time Engine) - TicTacToe
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
package org.jogre.tictactoe.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.tictactoe.common.CommTicTacToeMove;

// Controller for the  
public class TicTacToeController extends JogreController {
	
	// links to game data and the board component 
	protected TicTacToeModel model;
	protected TicTacToeBoardComponent boardComponent;
	
	// Constructor
	public TicTacToeController(TicTacToeModel model, TicTacToeBoardComponent boardComponent) {
		super(model, boardComponent);
		
		this.model = model;						// set fields
		this.boardComponent = boardComponent;
	}
	
	// Start method which calls reset method (resets the model).
	public void start() {
	    model.reset ();
	}
	
	// Overwrite the mousePressed method
	public void mousePressed (MouseEvent e) { 
		if (isGamePlaying() && isThisPlayersTurn ()) {			// ensure game has started
			// get board point from screen position of mouse click
			Point board = boardComponent.getBoardCoords (e.getX(), e.getY());
			
			// ensure board point is in range (0 to 2)
			if (board.x >= 0 && board.x < 3 && board.y >= 0 && board.y < 3) {

				// check model at this point is BLANK and it is this persons go
				if (model.getData(board.x, board.y) == TicTacToeModel.BLANK) {
					int value = getCurrentPlayerSeatNum();	
					move (board.x, board.y, value);	
				}
			}
		}
	}
		
	public void move (int x, int y, int value) {
		// update model and change player turn						
		model.setData (x, y, value);
		
		// next player turn.
		nextPlayer ();
				
		// send move to other user
		CommTicTacToeMove move = new CommTicTacToeMove 
		 	(x, y, value);
		conn.send (move);		
	}
}