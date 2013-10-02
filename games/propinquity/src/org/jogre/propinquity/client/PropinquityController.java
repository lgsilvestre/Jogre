/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.propinquity.common.CommPropinquityAttackNum;
import org.jogre.propinquity.common.CommPropinquityMove;

/**
 * Propinquity controller.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PropinquityController extends JogreController {

	// Declare model/chess data.
	private PropinquityModel propinquityModel;
	private PropinquityComponent propinquityComponent;


	/**
	 * Constructor to the propinquity controller.
	 *
	 * @param model
	 * @param component
	 */
	public PropinquityController (PropinquityModel model, PropinquityComponent component) {
		super(model, component);

		// Set local variables
		this.propinquityModel = model;
		this.propinquityComponent = component;
	}

	/**
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		propinquityModel.reset ();
		
		// retrieve a new attack number
		if (isGamePlaying() && isThisPlayersTurn()) {
			CommPropinquityAttackNum request = new CommPropinquityAttackNum ();
			conn.send (request);
		}
	}

	/**
	 * @param move
	 */
	public void receiveMove (CommPropinquityMove move) {
		int seatNum = getSeatNum (move.getUsername());
		propinquityModel.playerMove (seatNum, move.getMove());
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent mouseEvent) {
	    if (isGamePlaying () && isThisPlayersTurn ()) {
			Point point = new Point (mouseEvent.getX(), mouseEvent.getY());
			int newPoint = propinquityComponent.getIndex (point);

			// If user has moved to a new hexagon
			if (newPoint != propinquityComponent.getCurMousePoint()) {
				propinquityComponent.setCurMousePoint (newPoint);
				propinquityComponent.repaint();
			}
		}
	}

	/**
	 * Returns the opponent player to the one who is currently playing.
	 *
	 * @return
	 */
	public int getCurrentOpponentPlayer () {
		return getCurrentPlayerSeatNum() == 0 ? 1 : 0;
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent e) {
		if (isGamePlaying () && isThisPlayersTurn ()) {
			Point point = new Point (e.getX(), e.getY());
			int index = propinquityComponent.getIndex (point);

			// check index is in range
			if (propinquityModel.indexIsInRange (index)) {

				// retrieve cell for this mouse press
				Cell cell = propinquityModel.getGridData(index);

				if (cell.getState() == Cell.CELL_BLANK) {

					// Make the move on the local game data
					propinquityModel.playerMove (getSeatNum(), index);
					propinquityComponent.setCurMousePoint (-1);

					// Check to see if there is a connection to the server
					if (conn != null) {
						// Create communcations move object
						CommPropinquityMove propinquityMove = new CommPropinquityMove (index);

						// and send to server
						conn.send (propinquityMove);

						// Check to see if the games over - otherwise next player
						if (!checkGameOver())
							nextPlayer ();
					}
				}
			}
		}
	}

	/**
	 * Check to see if the game is over or not.  If the game is over then send
	 * a game over to the server.
	 *
	 * @return  Returns true if the game is over.
	 */
	private boolean checkGameOver () {
		int blankSpaces = propinquityModel.getNumOfCells(Cell.CELL_BLANK);

		if (blankSpaces == 0) {			// no spaces left
			// Status is either -1, DRAW or WIN
			int opponentPlayer = getCurrentOpponentPlayer();
			int status = -1;
			String winner = "NO_WINNER";

			int winningPlayer = propinquityModel.getPlayerWithTopScore();
			String name = getPlayer(winningPlayer);
			int score = propinquityModel.getTerritories(winningPlayer);
			//int playerScore =
			int otherPlayersScore = propinquityModel.getTerritories(opponentPlayer);

			if (winningPlayer == -1)		// same score
				status = IGameOver.DRAW;
			else {
				status = IGameOver.WIN;
				winner = getPlayer(winningPlayer);
			}

			// Create game over object if a win or draw
			if (status != -1 && conn != null) {
				CommGameOver gameOver = new CommGameOver (status);
				conn.send (gameOver);
				return true;
			}
		}

		return false;
	}
}
