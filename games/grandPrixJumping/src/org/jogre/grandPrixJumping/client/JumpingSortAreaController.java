
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

import java.util.Vector;

import org.jogre.client.JogreController;
import org.jogre.grandPrixJumping.common.CommJumpingChooseSort;
import org.jogre.grandPrixJumping.common.CommJumpingMoveCards;


/**
 * Controller for the sorting area for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingSortAreaController extends JogreController {

	// links to game data and the component
	private JumpingClientModel model;
	private JumpingSortAreaComponent sortComponent;
	private JumpingGraphics Jgraphics;
	private JumpingMasterController masterController;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param sortComponent			The hand component
	 * @param doneButton			The "done" button
	 */
	public JumpingSortAreaController(	JumpingClientModel model,
										JumpingSortAreaComponent sortComponent) {
		super(model, sortComponent);

		this.model = model;
		this.sortComponent = sortComponent;
		this.Jgraphics = JumpingGraphics.getInstance();
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
		if (isThisPlayersTurn()) {
			if (model.isSortingCards()) {
				handleMouseMovedSorting(mEv);
			} else if (model.isChoosingSorted()) {
				handleMouseMovedChoosing(mEv);
			}
		}
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed(MouseEvent mEv) {
		if (isThisPlayersTurn()) {
			if (model.isSortingCards()) {
				handleMousePressedSorting(mEv);
			} else if (model.isChoosingSorted()) {
				handleMousePressedChoosing(mEv);
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		boolean redrawNeeded = false;

		if (model.isSortingCards()) {
			redrawNeeded = sortComponent.setSelectedIndex(-1);
		} else if (model.isChoosingSorted()) {
			redrawNeeded = sortComponent.setSelectedArea(JumpingSortAreaComponent.NO_AREA);
		}

		// Determine if we need to redraw the component
		if (redrawNeeded) {
			sortComponent.repaint();
		}
	}

/*************************************************************/
/* Handle mouse events while the player is sorting the cards */

	/**
	 * Handle mouse movement while the player is sorting the cards
	 *
	 * @param mEv				The mouse event
	 */
	private void handleMouseMovedSorting(MouseEvent mEv) {
		// Convert the graphical (x,y) location to a card
		if (sortComponent.selectCardAt(mEv.getX(), mEv.getY())) {
			// We've selected a new card, so need to redraw the sorting area
			sortComponent.repaint();
		}
	}

	/**
	 * Handle mouse pressed events while the player is sorting the cards
	 *
	 * @param mEv				The mouse event
	 */
	public void handleMousePressedSorting(MouseEvent mEv) {
		// Convert the graphical (x,y) location to a card
		sortComponent.selectCardAt(mEv.getX(), mEv.getY());

		// Get the selected card
		int selectedCardIndex = sortComponent.getSelectedIndex();
		if (selectedCardIndex != -1) {

			// A real card was selected, so move it from one side to the other
			int direction = model.moveSortCard(selectedCardIndex);
			int moveCode = (direction == model.LEFT_TO_RIGHT) ?
								CommJumpingMoveCards.SORT_LEFT_TO_RIGHT :
								CommJumpingMoveCards.SORT_RIGHT_TO_LEFT ;

			// Tell the server (and other players) that the card moved
			Vector cardVector = new Vector();
			cardVector.add(model.getLastMovedSortCard());
			conn.send(new CommJumpingMoveCards(	conn.getUsername(),
												cardVector,
												moveCode));

			// Reselect a new card given that the card just moved
			sortComponent.selectCardAt(mEv.getX(), mEv.getY());

			// Redraw the sorting area
			sortComponent.repaint();

			// Enable the "done" button depending on whether this is a
			// valid sorting configuration or not.
			masterController.setDoneButtonText();
		}
	}


/**************************************************************/
/* Handle mouse events while the player is choosing the cards */


	/**
	 * Handle mouse movement while the player is choosing the cards
	 *
	 * @param mEv				The mouse event
	 */
	private void handleMouseMovedChoosing(MouseEvent mEv) {
		// Convert the graphical (x,y) location to a card
		if (sortComponent.selectAreaAt(mEv.getX(), mEv.getY())) {
			// We've selected a new area, so need to redraw the sorting area
			sortComponent.repaint();
		}
	}

	/**
	 * Handle mouse pressed events while the player is choosing the cards
	 *
	 * @param mEv				The mouse event
	 */
	public void handleMousePressedChoosing(MouseEvent mEv) {
		// Convert the graphical (x,y) location to an area
		sortComponent.selectAreaAt(mEv.getX(), mEv.getY());

		// Get the selected card
		int selectedArea = sortComponent.getSelectedArea();
		if (selectedArea != JumpingSortAreaComponent.NO_AREA) {
			// Make our selection in the model
			boolean choseLeft = (selectedArea == JumpingSortAreaComponent.LEFT_AREA);
			masterController.doChooseSort(getSeatNum(), choseLeft, false, true);

			// Tell the server (and other players) what our selection was
			conn.send(new CommJumpingChooseSort(conn.getUsername(), choseLeft));

			// Unselect the area
			sortComponent.setSelectedArea(JumpingSortAreaComponent.NO_AREA);

			// Enable/Disable the done button depending on whether the chooser
			// (which is us), can end playing right now without playing any cards.
			masterController.updatePlayingFlags();
		}
	}

}
