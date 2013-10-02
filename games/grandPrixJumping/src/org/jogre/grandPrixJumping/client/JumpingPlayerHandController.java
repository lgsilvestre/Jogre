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


/**
 * Controller of a player's hand for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingPlayerHandController extends JogreController {

	// links to game data and the component
	private JumpingClientModel model;
	private JumpingPlayerHandComponent handComponent;
	private JumpingMasterController masterController;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param handComponent			The hand component
	 */
	public JumpingPlayerHandController(	JumpingClientModel model,
										JumpingPlayerHandComponent handComponent) {
		super(model, handComponent);

		this.model = model;
		this.handComponent = handComponent;
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

			if (model.isHandCardActivated()) {
				redraw = handComponent.selectActivatedCardAt(mEv.getX(), mEv.getY());
			} else {
				redraw = handComponent.selectCardAt(mEv.getX(), mEv.getY());
			}

			if (redraw) {
				handComponent.repaint();
			}
		}
	}

	/**
	 * Helper function to determine if it's ok to select a card at this time.
	 */
	private boolean okToSelect() {
		return (model.isPlayingCards() &&
					!(model.isImmCardActivated() || model.isDualRiderActive()))
				|| model.isDiscarding();
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isThisPlayersTurn() && model.isPlayingCards() && !model.isImmCardActivated() && (!model.isOfficialDiscardActive())) {
			if (model.isHandCardActivated()) {
				// Convert the graphical (x,y) location to a card half
				handComponent.selectActivatedCardAt(mEv.getX(), mEv.getY());

				int half = handComponent.getActivatedHalf();
				JumpingCard activatedCard = handComponent.getSelectedCard();
				masterController.doActivateHandCard(getSeatNum(), activatedCard, half, true);
				handComponent.deactivateCard();
				handComponent.selectCardAt(mEv.getX(), mEv.getY());
				handComponent.repaint();
			} else {
				// Convert the graphical (x,y) location to a card
				handComponent.selectCardAt(mEv.getX(), mEv.getY());

				// Get the selected card
				JumpingCard selectedCard = handComponent.getSelectedCard();
				if (selectedCard != null) {
					masterController.doPlayCard(getSeatNum(), selectedCard);

					// If this is a ribbon card, then we've just activated it.
					if (selectedCard.isRibbon()) {
						handComponent.selectActivatedCardAt(mEv.getX(), mEv.getY());
					}
				}
			}

			// Update the "done" button and playable flags given the new situation
			masterController.updatePlayingFlags();
		} else if (isThisPlayersTurn() && (model.isDiscarding() || model.isOfficialDiscardActive())) {
			// Convert the graphical (x,y) location to a card
			handComponent.selectCardAt(mEv.getX(), mEv.getY());

			// Get the selected card
			JumpingCard selectedCard = handComponent.getSelectedCard();
			if (selectedCard != null) {
				selectedCard.invertMark();
				masterController.updatePlayingFlags();
				handComponent.repaint();
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		boolean redraw;

		if (model.isHandCardActivated()) {
			redraw = handComponent.selectActivatedCardAt(-1, -1);
		} else {
			redraw = handComponent.setSelectedIndex(-1);
		}

		if (redraw) {
			handComponent.repaint();
		}
	}
}
