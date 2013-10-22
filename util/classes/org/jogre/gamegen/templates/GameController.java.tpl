filename=%game_id%/src/org/jogre/%game_id%/client/%Game_id%Controller.java
/*
 * JOGRE (Java Online Gaming Real-time Engine) - %Game_id%
 * Copyright (C) 2003 - %year%  %Author% (%email%)
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
package org.jogre.%game_id%.client;

import nanoxml.XMLElement;

import java.awt.event.MouseEvent;

import org.jogre.%game_id%.common.%Game_id%Model;
import org.jogre.client.JogreController;

/**
 * Controller for the %game_id% game.
 *
 * @author  %Author%
 * @version %Version%
 */
public class %Game_id%Controller extends JogreController {

    // links to game data and the board component
    protected %Game_id%Model     %game_id%Model;
    protected %Game_id%Component %game_id%Component;

    /**
     * Default constructor for the %game_id% controller which takes a
     * model and a view.
     *
     * @param %game_id%Model      %Game_id% model.
     * @param %game_id%Component  %Game_id% view.
     */
    public %Game_id%Controller (
        %Game_id%Model     %game_id%Model,     // link to players game data
        %Game_id%Component %game_id%Component      // Link to game view
    ) {
        super (%game_id%Model, %game_id%Component);

        this.%game_id%Model     = %game_id%Model;
        this.%game_id%Component = %game_id%Component;
    }

    /**
     * Start method which restarts the model.
     *
     * @see org.jogre.common.JogreModel#start()
     */
    public void start () {
        %game_id%Model.reset ();
    }

    /**
     * Implementation of the mouse pressed interface.
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if (isGamePlaying() && isThisPlayersTurn ()) {
            // get mouse co-ordinates
            int x = e.getX();
            int y = e.getY();

            System.out.println ("Mouse pressed x: " + x + " y:" + y);
        }
    }

    /**
     * Implementation of the mouse released interface.
     *
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased (MouseEvent e) {
        if (isGamePlaying() && isThisPlayersTurn ()) {
            // get mouse co-ordinates
            int mouseX = e.getX();
            int mouseY = e.getY();

            // if game still in progress then continue on...
            nextPlayer ();
        }
    }
}