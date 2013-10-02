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

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.comm.CommGameOver;

// Controller for the position component for the Car Tricks game
public class CarTricksPositionController extends JogreController {

	// links to game data and the component
	private CarTricksClientModel model;
	private CarTricksPositionComponent posComponent;
	private CarTricksGraphics CT_graphics;

	private boolean dragging;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param boardComponent		The board component
	 */
	public CarTricksPositionController(CarTricksClientModel model,
										 CarTricksPositionComponent posComponent) {
		super(model, posComponent);

		// Link to model, component and graphics
		this.model = model;
		this.posComponent = posComponent;
		this.CT_graphics = CarTricksGraphics.getInstance();

		dragging = false;
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
		if (model.isSettingBid()) {
			// Start the drag at the given location
			dragging = posComponent.startDragAt(mEv.getX(), mEv.getY());
			posComponent.repaint();
		}
	}

	/**
	 * Handle mouse released events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseReleased (MouseEvent mEv) {
		if (dragging) {
			// Get the start and end indexes of the drag
			int drag_index = posComponent.dragging_index;
			int drop_index = posComponent.endDragAt(mEv.getX(), mEv.getY());
			dragging = false;

			// If both indexes are valid, then rearrange the cars
			if ((drag_index >= 0) && (drop_index >= 0) && (drag_index != drop_index)) {
				posComponent.rearrangeCars(drag_index, drop_index);
			}
			posComponent.repaint();
		}
	}

	/**
	 * Handle mouse dragged events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseDragged (MouseEvent mEv) {
		posComponent.setDragPoint(mEv.getX(), mEv.getY());
		posComponent.repaint();
	}

}
