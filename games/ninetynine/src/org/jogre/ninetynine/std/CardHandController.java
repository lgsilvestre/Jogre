/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.std;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*
	Generic Controller for a hand of cards

	This is a generic controller.  It is *NOT* a Jogre controller.  It requires
	an object that implements the interface IGenericJogreControllerAlerts to be
	provided so that it can tell the specific Jogre application what is going
	on.
*/
public class CardHandController implements MouseListener, MouseMotionListener {

	// The pointer to the IGenericJogreControllerAlert object that will receive
	// the interface alerts from this.
	private ICardHandControllerAlertee jogController;

	// The component we're controlling
	private CardHandComponent handComponent;

	/**
	 * Constructor which creates the generic controller
	 * @param jogController			The controller to receive alerts
	 * @param handComponent			The hand component
	 */
	public CardHandController(ICardHandControllerAlertee jogController,
							  CardHandComponent handComponent) {
		// Save parameters
		this.jogController = jogController;
		this.handComponent = handComponent;
	}


	//==========================================================================
	// Methods for "MouseListener"
    //==========================================================================

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (handComponent.isEnabled()) {
			// Convert the graphical (x,y) location to a card
			DrawableHand hand = handComponent.getDrawableHand();
			hand.selectCardAt(mEv.getX(), mEv.getY());

			// Get the selected card
			Card selectedCard = hand.getSelectedCard();
			if (!selectedCard.equals(hand.noCard)) {
				// This is a real card that was selected, so tell the JOGRE controller
				jogController.signalCardClicked(handComponent, selectedCard);
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		DrawableHand hand = handComponent.getDrawableHand();

		if (hand.setSelectedCard(hand.noCard)) {
			// We've unselected a card, so need to re-draw the hand
			handComponent.repaint();
		}
	}

	/**
	 * Invoked when the mouse enters a JogreJogreComponent.
	 *
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * Invoked when the mouse has been clicked on a JogreComponent.
	 *
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {}

	/**
	 * Invoked when a mouse button has been released on a JogreComponent.
	 *
	 * @param e
	 */
	public void mouseReleased (MouseEvent e) {}

	//==========================================================================
	// Methods for "MouseMotionListener"
    //==========================================================================

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
		if (handComponent.isEnabled()) {
			// Convert the graphical (x,y) location to a card
			DrawableHand hand = handComponent.getDrawableHand();
			if (hand.selectCardAt(mEv.getX(), mEv.getY())) {
				if (!jogController.isValidCardSelection(handComponent)) {
					// But this is not a valid card to select right now, so unselect it.
					hand.unselectCard();
				}

				// We've selected a new card, so need to re-draw the hand
				handComponent.repaint();
			}
		}
	}

	/**
	 * Invoked when a mouse button is pressed on a JogreComponent and
	 * then dragged.
	 *
	 * @param e
	 */
	public void mouseDragged (MouseEvent e) {}

}
