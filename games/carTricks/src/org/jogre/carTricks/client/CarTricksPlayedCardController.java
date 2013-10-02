/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.client;

import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.comm.CommGameOver;
import org.jogre.carTricks.common.CarTricksCard;
import org.jogre.carTricks.common.CommCarTricksSetBid;
import org.jogre.carTricks.common.CommCarTricksPlayCard;


// Controller for the played card area of the Car Tricks game.
public class CarTricksPlayedCardController extends JogreController {

	// links to game data and the component
	private CarTricksClientModel model;
	private CarTricksPlayedCardComponent cardComponent;
	private CarTricksGraphics CT_graphics;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param cardComponent			The component
	 */
	public CarTricksPlayedCardController(CarTricksClientModel model,
										 CarTricksPlayedCardComponent cardComponent) {
		super(model, cardComponent);

		this.model = model;						// set fields
		this.cardComponent = cardComponent;
		this.CT_graphics = CarTricksGraphics.getInstance();
	}

	/**
	 * Need to override this as part of JogreController, but it does nothing
	 */
	public void start () {}

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
		if ( model.isSettingBid() ||
			(model.isSelectingCard() && isThisPlayersTurn())) {
			// Convert the graphical (x,y) location to a card
			if (cardComponent.selectEventCardAt(mEv.getX(), mEv.getY())) {
				// We've selected a new card, so need to re-draw the card
				cardComponent.repaint();
			}
		}
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if ( model.isSettingBid() ||
			(model.isSelectingCard() && isThisPlayersTurn())) {
			// Convert the graphical (x,y) location to an event card
			cardComponent.selectEventCardAt(mEv.getX(), mEv.getY());

			// Get the selected card
			CarTricksCard selectedCard = cardComponent.getSelectedCard();
			if (selectedCard.isEvent()) {
				// This is a real card that was selected.

				if (model.isSettingBid()) {
					// We need to send a bid message to the server
					conn.send(new CommCarTricksSetBid(conn.getUsername(), model.getBid(), selectedCard));
					model.hasSubmittedBid();
				} else {
					// We need to send a played card message to the server
					conn.send(new CommCarTricksPlayCard(conn.getUsername(), selectedCard, false));
				}

				// Put the card into the played card area
				model.playCard(getSeatNum(), selectedCard, false);

				// Unselect the card
				cardComponent.unSelectCard();
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		if (cardComponent.unSelectCard()) {
			// We've unselected a card, so need to re-draw the hand
			cardComponent.repaint();
		}
	}
}
