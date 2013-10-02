/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
 * Copyright (C) 2003 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.pointsTotal.client;

import nanoxml.XMLElement;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import java.awt.Point;

import org.jogre.pointsTotal.common.CommPointsTotalMove;
import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.pointsTotal.common.PointsTotalPiece;
import org.jogre.client.JogreController;

/**
 * Controller for the pointsTotal game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalController extends JogreController {

	// links to game data and the board component
	protected PointsTotalModel     model;
	protected PointsTotalComponent component;
	protected PointsTotalSelectionComponent selectionComponent;

	/**
	 * Default constructor for the pointsTotal controller which takes a
	 * model and a view.
	 *
	 * @param model               PointsTotal model.
	 * @param component           PointsTotal board view.
	 * @param selectionComponent  PointsTotal selection component.
	 */
	public PointsTotalController (
		PointsTotalModel     model,       // link to players game data
		PointsTotalComponent component,   // Link to game view
		PointsTotalSelectionComponent selectionComponent
	) {
		super (model, component);

		this.model              = model;
		this.component          = component;
		this.selectionComponent = selectionComponent;
	}

	/**
	 * Start method which restarts the model.
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.reset ();

		// Tell the main component to have the keyboard focus.
		// (This needs to be done after the component is realized,
		//  and since the only keyboard input goes to the main component,
		//  this is a good place to do this and just leave the focus set.
		component.requestFocusInWindow();

		if (getSeatNum() == 0) {
			// I'm the first player, so need select piece 0 as the first selection.
			selectionComponent.setSelectedSpace(0);
			component.setCurrentPieceValue(0);
			component.setCurrentPieceRotation(0);
		}
	}

	/**
	 * Handle mouse movement events
	 *
	 * @param  mEv        The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical point to a board location
			Point boardLocation = component.decodeSpace(mEv.getX(), mEv.getY());

			// If this is not a valid move, then we've moved off board
			if (!model.validMove(boardLocation)) {
				boardLocation = component.OFF_BOARD_POINT;
			}

			// Set the selected space to the location and, if this has changed
			//   the selected location, repaint the board.
			if (component.setSelectedSpace(boardLocation)) {
				component.repaint();
			}
		}
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param  mEv        The mouse event
	 */
	public void mousePressed(MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the graphical point to a board location
			Point boardLocation = component.decodeSpace(mEv.getX(), mEv.getY());

			// Try to make the move locally
			PointsTotalPiece currPiece = component.getCurrentPiece();
			int playerNum = getSeatNum();
			if (model.makeMove(playerNum, currPiece, boardLocation)) {
				// If valid, then tell the server that this is our move
				conn.send(new CommPointsTotalMove(conn.getUsername(), currPiece, boardLocation));

				// Turn off the selections
				component.setSelectedSpace(component.OFF_BOARD_POINT);
				selectionComponent.unselectSpaces();

				// Indicate the next player
				setNextPlayer();

				// Repaint the components
				component.repaint();
				selectionComponent.repaint();
			}
		}
	}

	/**
	 * Handle mouse exited events
	 *
	 * @param  mEv        The mouse event
	 */
	public void mouseExited(MouseEvent mEv) {
		// Unselect the space, and redraw if needed
		if (component.setSelectedSpace(component.OFF_BOARD_POINT)) {
			component.repaint();
		}
	}

	/**
	 * Handle key events.
	 * Note: In order for key events to work, this component must have the
	 *       input focus.   In PointsTotalTableFrame, the requestFocusInWindow()
	 *       routine is called on on the component.
	 *
	 * @param kEv  The keyboard event.
	 */
	public void keyPressed (KeyEvent kEv) {
		if (isGamePlaying() &&
			isThisPlayersTurn()) {

			int keyCode = kEv.getKeyCode();
			int newRot;

			if ((keyCode == KeyEvent.VK_KP_LEFT) ||
			    (keyCode == KeyEvent.VK_LEFT) ||
			    (keyCode == KeyEvent.VK_Z) ||
			    (keyCode == KeyEvent.VK_NUMPAD4)) {
			        // Rotate counter-clockwise
				component.rotateCurrentPiece(-1);
				component.repaint();
			} else
			if ((keyCode == KeyEvent.VK_KP_RIGHT) ||
			    (keyCode == KeyEvent.VK_RIGHT) ||
			    (keyCode == KeyEvent.VK_X) ||
			    (keyCode == KeyEvent.VK_NUMPAD6)) {
			        // Rotate clockwise
				component.rotateCurrentPiece(1);
				component.repaint();
			}
		}
	}


	/**
	 * Handle receving messages from the server
	 *
	 * @param   message      The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(CommPointsTotalMove.XML_NAME)) {
			handleMove(new CommPointsTotalMove(message));
		}

	}

	/**
	 * Handle a message that indicates a move by another player
	 */
	private void handleMove(CommPointsTotalMove theMoveMsg) {
		// Make the move
		model.makeMove(getSeatNum(theMoveMsg.getUsername()),
		               theMoveMsg.getPiece(),
		               theMoveMsg.getLocation());

		setNextPlayer();
	}

	/**
	 * Set the next player
	 */
	private void setNextPlayer() {
		// Tell the table who the next player is
		int nextPlayerSeat = model.getCurrentPlayer();
		if (nextPlayerSeat >= 0) {
			conn.getTable().nextPlayer(nextPlayerSeat);
		}

		// If I'm the next player, then select the lowest valued piece
		// that I still haven't played yet.
		int mySeat = getSeatNum();
		if (nextPlayerSeat == mySeat) {
			int v;
			for (v=0; !model.isAvailToPlay(mySeat, v); v++);

			selectionComponent.setSelectedSpace(v);
			component.setCurrentPieceValue(v);
			component.setCurrentPieceRotation(0);
			selectionComponent.repaint();
		}
	}
}
