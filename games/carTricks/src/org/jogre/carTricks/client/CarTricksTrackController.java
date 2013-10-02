/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter
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

import org.jogre.client.JogreController;

import org.jogre.carTricks.common.CommCarTricksMoveCar;
import org.jogre.carTricks.common.CommCarTricksScore;
import org.jogre.carTricks.common.CarTricksPath;

// Controller for the track component of the Car Tricks game
public class CarTricksTrackController extends JogreController {

	// links to game data and the board component
	private CarTricksClientModel model;
	private CarTricksTrackComponent trackComponent;

	// The threshhold for selecting paths
	private static final int MOUSE_SELECT_THRESHHOLD = 60;

	// An empty path to use when the active car moves 0 spaces
	private CarTricksPath emptyPath = new CarTricksPath(0);

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param trackComponent		The track component
	 */
	public CarTricksTrackController(	CarTricksClientModel model,
										CarTricksTrackComponent trackComponent) {
		super(model, trackComponent);

		this.model = model;						// set fields
		this.trackComponent = trackComponent;
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
		if ( model.isMovingCar() && isThisPlayersTurn()) {
			// Find the closest ending space to the graphical (x,y) location of the mouse
			if (trackComponent.setMousePoint(mEv.getX(), mEv.getY(), MOUSE_SELECT_THRESHHOLD)) {
				// The user selected a new end space, so need to re-draw the track
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
		if ( model.isMovingCar() && isThisPlayersTurn()) {
			// Convert the graphical (x,y) location to an ending space
			trackComponent.setMousePoint(mEv.getX(), mEv.getY(), MOUSE_SELECT_THRESHHOLD);

			// Send that path to the server
			sendPath(trackComponent.getSelectedPath());
		}
	}

	/**
	 * Send a message to the server to move the given path.
	 *
	 * @param	path	The path to send
	 */
	public void sendPath(CarTricksPath path) {
		if (path != null) {
			// Send the server the message with the path we've just selected
			conn.send(new CommCarTricksMoveCar(conn.getUsername(), path));

			// Move the car
			model.moveCar(getSeatNum(), path);

			// Unselect the path
			trackComponent.unselectPath();

			// Redraw the track to remove the selected spaces
			trackComponent.repaint();
		}
	}
}
