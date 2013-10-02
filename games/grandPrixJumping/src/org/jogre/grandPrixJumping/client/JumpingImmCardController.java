/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.client;

import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.grandPrixJumping.common.JumpingCard;

import org.jogre.grandPrixJumping.common.CommJumpingActivate;

/**
 * Controller of a player's immediate cards for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingImmCardController extends JogreController {

	// links to game data and the component
	private JumpingClientModel model;
	private JumpingImmCardComponent cardComponent;
	private JumpingMasterController masterController;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param cardComponent			The card component
	 */
	public JumpingImmCardController(	JumpingClientModel model,
										JumpingImmCardComponent cardComponent) {
		super(model, cardComponent);

		this.model = model;
		this.cardComponent = cardComponent;
	}

	/**
	 * Need to override this as part of JogreController, but it does nothing
	 */
	public void start () {}

	/**
	 * Set the master controller.
	 */
	public void setMasterController(JumpingMasterController masterController) {
		this.masterController = masterController;
	}

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
		boolean redraw;

		if (isThisPlayersTurn() && okToSelect()) {
			// Convert the graphical (x,y) location to a card and determine
			// if we need to redraw.

			if (model.isDualRiderActive()) {
				redraw = cardComponent.selectDualRiderCardAt(mEv.getX(), mEv.getY());
			} else if (model.isImmCardActivated()) {
				redraw = cardComponent.selectActivatedCardAt(mEv.getX(), mEv.getY());
			} else {
				redraw = cardComponent.selectCardAt(mEv.getX(), mEv.getY());
			}

			if (redraw) {
				cardComponent.repaint();
			}
		}
	}

	/**
	 * Helper function to determine if it's ok to select a card at this time.
	 */
	private boolean okToSelect() {
		return (model.isPlayingCards() && !model.isHandCardActivated()) || model.isPlayingImmediate();
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isThisPlayersTurn() && okToSelect()) {
			if (model.isDualRiderActive()) {
				// Convert the graphical (x,y) location to a card
				cardComponent.selectDualRiderCardAt(mEv.getX(), mEv.getY());
				int half = cardComponent.getActivatedHalf();
				if ((half == 0) || (half == 1)) {
					cardComponent.deactivateCard();
					cardComponent.setSelectedIndex(-1);
					JumpingCard myCard = model.getDualRiderCard(half);
					JumpingCard hisCard = model.getDualRiderCard(1-half);
					masterController.doDualRiderSelect(getSeatNum(), myCard, hisCard, true);
					cardComponent.repaint();
				}
			} else if (model.isImmCardActivated()) {
				// Convert the graphical (x,y) location to a card half
				cardComponent.selectActivatedCardAt(mEv.getX(), mEv.getY());

				// Get which part of the card is selected
				int half = cardComponent.getActivatedHalf();
				JumpingCard activatedCard = cardComponent.getSelectedCard();

				// Deal with the activation
				masterController.doActivateImmCard(getSeatNum(), activatedCard, half, true);
				cardComponent.deactivateCard();
				cardComponent.selectCardAt(-1, -1);
				cardComponent.repaint();
			} else {
				// Convert the graphical (x,y) location to a card
				cardComponent.selectCardAt(mEv.getX(), mEv.getY());

				// Get the selected card
				JumpingCard selectedCard = cardComponent.getSelectedCard();
				if (selectedCard != null) {
					if (selectedCard.isOfficial()) {
						model.setImmCardActivated(true);
						selectedCard.setMarked(true);
						cardComponent.selectActivatedCardAt(mEv.getX(), mEv.getY());
						cardComponent.repaint();
					} else if (selectedCard.isDualRider()) {
						// Tell the server we've played the dual rider card so that he will
						// send us our two cards to choose from.
						conn.send(new CommJumpingActivate(conn.getUsername(), selectedCard, 0));
						selectedCard.setMarked(true);
						cardComponent.repaint();
					}
				}
			}

			// Update the "done" button and playable flags given the new situation
			masterController.updatePlayingFlags();
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		boolean redraw;

		if (model.isImmCardActivated() || model.isDualRiderActive()) {
			redraw = cardComponent.selectActivatedCardAt(-1, -1);
		} else {
			redraw = cardComponent.setSelectedIndex(-1);
		}

		if (redraw) {
			cardComponent.repaint();
		}
	}
}
