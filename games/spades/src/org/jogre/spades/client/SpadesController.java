/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
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
package org.jogre.spades.client;

import java.awt.event.MouseEvent;

import javax.swing.table.DefaultTableModel;

import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.games.Card;
import org.jogre.common.util.JogreLogger;
import org.jogre.spades.common.CommSpadesMakeBid;
import org.jogre.spades.common.CommSpadesPlayCard;
import org.jogre.spades.common.CommSpadesRequestHand;

/**
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 *
 * Controller for the spades game.
 */
public class SpadesController extends JogreController {

	// Spades model
	protected SpadesModel model;

	// Spades player component
	protected SpadesPlayerHandComponent playerHandComponent;

	// Spades board component
	protected SpadesBoardComponent boardComponent;

	// Spades score and bags table model
	protected DefaultTableModel scoreAndBagsModel;

	/**
	 * Contructor for initializing the controller
	 *
	 * @param gameModel
	 * @param boardComponent
	 */
	public SpadesController(SpadesModel model,
			SpadesPlayerHandComponent playerComponent,
			SpadesBoardComponent boardComponent,
			DefaultTableModel scoreAndBagsModel) {
		super(model, (JogreComponent) playerComponent);
		this.model = model;
		this.playerHandComponent = playerComponent;
		this.boardComponent = boardComponent;
		this.scoreAndBagsModel = scoreAndBagsModel;
	}

	/**
	 * Start the controller
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start() {

		// reset spades model
		model.reset();

		// immediately request hand at beginning of game
		requestHand();
	}

	/**
	 * Record mouse presses for selecting cards
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {

		// if playing and current turn, record mouse press
		if (isGamePlaying() && isThisPlayersTurn()
				&& this.model.getGameState() == SpadesModel.GAME_STATE_PLAYING) {
			int mouseX = e.getX();
			int mouseY = e.getY();

			// record which are the mouse was pressed on
			int cardPressed = this.playerHandComponent.getCard(mouseX, mouseY);
			if (cardPressed > -1 && model.validCard(cardPressed))
				this.playerHandComponent.setCardPressed(cardPressed);
			else
				this.playerHandComponent.setCardPressed(-1);
		} else
			this.playerHandComponent.setCardPressed(-1);
	}

	/**
	 * Record mouse clicks for selecting cards
	 *
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {

		// if playing and current turn, record mouse click
		if (isGamePlaying() && isThisPlayersTurn()
				&& this.model.getGameState() == SpadesModel.GAME_STATE_PLAYING
				&& !this.model.cardAlreadyPlayedInCurrentHand())

		{
			// get mouse co-ordinates
			int mouseX = e.getX();
			int mouseY = e.getY();

			// get card mouse was released on
			int cardReleased = this.playerHandComponent.getCard(mouseX, mouseY);

			// if card released on was the same as card mouse was pressed on ...
			if (cardReleased > -1
					&& cardReleased == this.playerHandComponent
							.getCardPressed()) {
				this.playerHandComponent.setCardClicked(cardReleased);

				if (cardReleased == this.playerHandComponent
						.getLastCardClicked()) {
					Card card = model.getCardFromHand(cardReleased);
					model.playCard(cardReleased, this.getSeatNum());

					// if double clicked, then play card
					CommSpadesPlayCard playCard = new CommSpadesPlayCard(conn
							.getUsername(), card);
					conn.send(playCard);
				} else {
					// unselect all cards, then select another card
					model.unselectAllCards();
					model.selectCard(cardReleased);
				}

				this.playerHandComponent.repaint();
			}
		}
	}

	/**
	 * Send bid to server as well sending next player message
	 *
	 * @param bid Bid
	 */
	public void makeBid(int bid) {
		this.model.setGameState(SpadesModel.GAME_STATE_PLAYING);
		CommSpadesMakeBid makeBid = new CommSpadesMakeBid(this.conn
				.getUsername(), bid);
		conn.send(makeBid);
		this.nextPlayer();
	}

	/**
	 * Request hand from server
	 */
	public void requestHand() {
		CommSpadesRequestHand requestHand = new CommSpadesRequestHand(conn
				.getUsername());
		conn.send(requestHand);
	}

	/**
	 * Set score and bags from server response
	 *
	 * @param scores Array of scores per team
	 * @param bags Array of bags per team
	 */
	public void setRoundScoreAndBags(int[] scores, int[] bags) {
		Object[] data = { scores[0] + " (" + bags[0] + ")",
				scores[1] + " (" + bags[1] + ")" };
		this.scoreAndBagsModel.addRow(data);

		this.model.setScore(scores);
		this.model.setBags(bags);
		this.model.resetRound();
	}

	/**
	 * Check to see if the game is over.
	 */
	private boolean checkGameOver() {
		/*
		 * // check for game over or draw CheckersPieceMover pieceMover =
		 * checkersModel.getPieceMover();
		 *  // Rule 1 - if opponents has no pieces left then its game over. int
		 * count = pieceMover.getPlayerCount(getCurrentOpponentPlayer ());
		 *
		 * if (count == 0) { CommGameOver commGameOver = new CommGameOver (
		 * CommGameOver.WIN, 20); conn.send(commGameOver); return true; }
		 */
		return false; // game still in progress
	}

	/**
	 * Get player from table by username
	 *
	 * @param username Username
	 * @return player from table by username
	 */
	public Player getPlayerFromTable(String username) {
		PlayerList playerList = this.getTable().getPlayerList();
		return playerList.getPlayer(username);
	}

	/**
	 * Face all cards in hand upright for current player
	 *
	 * @param faceUp
	 *            If true, then face all cards up, else face all cards down.
	 */
	public void turnHandOver(boolean faceUp) {
		this.model.turnOverHand(faceUp);
	}
}