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

import org.jogre.grandPrixJumping.common.CommJumpingModifyFence;
import org.jogre.grandPrixJumping.common.CommJumpingMoveCards;
import org.jogre.grandPrixJumping.common.JumpingCard;

/**
 * Controller for the track component of the Grand Prix Jumping game.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingTrackController extends JogreController {

	// links to game data and the board component
	private JumpingClientModel model;
	private JumpingTrackComponent trackComponent;
	private JumpingMasterController masterController;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param trackComponent		The track component
	 */
	public JumpingTrackController(	JumpingClientModel model,
									JumpingTrackComponent trackComponent) {
		super(model, trackComponent);

		this.model = model;
		this.trackComponent = trackComponent;
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
		if (isThisPlayersTurn() && model.isPlayingCards()) {
			// Convert the graphical (x,y) location to an icon
			if (trackComponent.selectIconAt(mEv.getX(), mEv.getY())) {
				// We've selected a new icon, so need to redraw the track
				trackComponent.repaint();
			}
		} else if (model.isCreatingTrack() && model.playerStillEditingTrack(getSeatNum())) {
			// User is modifying the fences on the track...
			if (trackComponent.selectTrackSpaceAt(mEv.getX(), mEv.getY())) {
				// We've selected a new track space for editing, so need
				// to redraw the track
				trackComponent.repaint();
			}
		}
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isThisPlayersTurn() && model.isPlayingCards()) {
			// Convert the graphical (x,y) location to a card
			trackComponent.selectIconAt(mEv.getX(), mEv.getY());

			// Get the selected card
			JumpingTrackOrnament orn = trackComponent.getSelectedIcon();

			// If it is a real card, then unplay it and then redraw the track
			if (orn != null) {
				if (orn.isHorse()) {
					// The horse was selected, so commit the play of the
					// current played cards.
					masterController.doCommitHorseMovement(getSeatNum());

					// Tell the server (and other players) that the cards have been played
					Vector cards = model.getCurrentPlayedCards();
					conn.send(new CommJumpingMoveCards(	conn.getUsername(),
														cards,
														CommJumpingMoveCards.HAND_TO_TRACK));

					// Clear the current played cards
					model.clearCurrentJumps();
				} else {
					// A card was selected, so play it.
					JumpingCard playedCard = orn.isPlus() ? JumpingCard.makeFakeSaddle() : orn.theCard;

					masterController.doPlayCard(getSeatNum(), playedCard);
				}

				// Update the "done" button and playable flags given the new situation
				masterController.updatePlayingFlags();

				// Unselect the icon and redraw the track
				trackComponent.setSelectedIcon(null);
				trackComponent.repaint();
			}
		} else if (model.isCreatingTrack() && model.playerStillEditingTrack(getSeatNum())) {
			// User is modifying the fences on the track.
			int space = trackComponent.getSelectedTrackSpace();
			int direction = mEv.isShiftDown() ? -1 : 1;

			if ((space > 0) && (space < JumpingClientModel.LAST_SPACE)) {
				// Tell the server that we want to modify the fence
				conn.send(new CommJumpingModifyFence(conn.getUsername(), space, direction));
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseExited (MouseEvent e) {
		boolean deselectIcon = trackComponent.selectIconAt(-1, -1);
		boolean deselectSpace = trackComponent.setSelectedTrackSpace(-1);
		if (deselectIcon || deselectSpace) {
			trackComponent.repaint();
		}
	}
}
