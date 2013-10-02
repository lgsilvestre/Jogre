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

import java.awt.event.MouseEvent;

import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.client.JogreController;

/**
 * Controller for the selection component of the Points Total game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalSelectionController extends JogreController {

    // links to game data and the board component
    protected PointsTotalModel              model;
    protected PointsTotalSelectionComponent selectionComponent;
    protected PointsTotalComponent          component;

    /**
     * Default constructor for the pointsTotal controller which takes a
     * model and a view.
     *
     * @param model               PointsTotal model class.
     * @param selectionComponent  PointsTotal view class.
     * @param component           The board view component.
     * @param conn
     */
    public PointsTotalSelectionController (
        PointsTotalModel              model,                // Players game data
        PointsTotalSelectionComponent selectionComponent,   // Our to game view
        PointsTotalComponent          component             // Main view of the game
    ) {
        super (model, selectionComponent);

        this.model     = model;
        this.selectionComponent = selectionComponent;
        this.component = component;
    }

    /**
     * Start method needed, as this is a JogreController, but this start()
     * routine does nothing, as the one in PointsTotalController does the
     * real start.
     */
    public void start () {}

	/**
	 * Handle mouse movement events
	 *
	 * @param  mEv        The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// Convert the (x,y) to which space is pointed at.
			int space = selectionComponent.decodeSpace(mEv.getX(), mEv.getY());

			// If we've selected nothing or if the selected space is still
			// available to play, then we can mouse over it.
			if ((space < 0) || model.isAvailToPlay(getSeatNum(), space)) {
				if (selectionComponent.setMouseSpace(space)) {
					selectionComponent.repaint();
				}
			}
		}
	}

    /**
     * Implementation of the mouse pressed interface.
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent mEv) {
        if (isGamePlaying() && isThisPlayersTurn()) {
	        // Convert the (x,y) to which space is pointed at.
			int space = selectionComponent.decodeSpace(mEv.getX(), mEv.getY());

			if ((space >= 0) && model.isAvailToPlay(getSeatNum(), space)) {
				if (selectionComponent.setSelectedSpace(space)) {
					// Tell the board component what the current piece should
					// now be when placing
					component.setCurrentPieceValue(space);

					selectionComponent.repaint();
					component.repaint();
				}
			}
        }
    }

}
