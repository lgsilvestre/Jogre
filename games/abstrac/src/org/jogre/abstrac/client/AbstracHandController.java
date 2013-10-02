/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.abstrac.client;

import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

import org.jogre.abstrac.common.CommAbstracMove;

/*
	Controller for the hand of cards for Abstrac.  This is a special controller,
	because Abstrac requires that the card selection mechanism allow for up to
	3 cards selected at once.
*/
public class AbstracHandController extends JogreController {

	// The model
	private AbstracModel model;

	// The component we're controlling
	private AbstracHandComponent handComponent;

	// The taken component we're telling to redraw
	private AbstracTakenComponent takenComponent;

	/**
	 * Constructor which creates the controller
	 * @param model					The model
	 * @param handComponent			The hand component
	 * @param takenComponent		The component that shows the selected cards
	 */
	public AbstracHandController(AbstracModel model,
							     AbstracHandComponent handComponent,
								 AbstracTakenComponent takenComponent) {
		super(model, handComponent);

		// Save parameters
		this.model = model;
		this.handComponent = handComponent;
		this.takenComponent = takenComponent;
	}

	/**
	 * Need to override this as part of JogreController, but it does nothing
	 */
	public void start () {}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical (x,y) location to a card
			handComponent.selectCardsAt(mEv.getX(), mEv.getY());

			int numSelectedCards = handComponent.getNumSelectedCards();
			if (numSelectedCards > 0) {
				// Some number of cards was selected, so make the move
				int playerId = getCurrentPlayerSeatNum();
				model.downgradeJustTaken(playerId);
				model.takeCards(playerId, numSelectedCards);

				// Tell the server
				conn.send(new CommAbstracMove (conn.getUsername(), numSelectedCards));

				if (checkGameOver() == false) {
					// Advance to the next player
					nextPlayer();
				}

				// Unselect the cards in the hand
				handComponent.unselectAllCards();
				handComponent.repaint();
				takenComponent.repaint();
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		if (handComponent.unselectAllCards()) {
			// We've unselected the cards, so need to re-draw the hand
			handComponent.repaint();
			takenComponent.repaint();
		}
	}

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical (x,y) location to a card
			if (handComponent.selectCardsAt(mEv.getX(), mEv.getY())) {
				// The selection changed, so need to re-draw the hand
				handComponent.repaint();
				takenComponent.repaint();
			}
		}
	}

	/**
	 * Check to see if the game is over or not.
	 * If the game is over, this will send a game over message to the server.
	 *
	 * @return		true = Game over
	 * @return		false = Game not over
	 */
	private boolean checkGameOver () {

		int winner = model.getWinner();
		if (winner == AbstracModel.NOT_OVER) {
			return false;
		}

		// Create a GameOver object.  It doesn't matter whether I say I
		// won or lost, since the server will determine the winner.
		conn.send (new CommGameOver (IGameOver.WIN));

		return true;
	}

}
