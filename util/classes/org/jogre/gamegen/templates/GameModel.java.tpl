filename=%game_id%/src/org/jogre/%game_id%/common/%Game_id%Model.java
/*
 * JOGRE (Java Online Gaming Real-time Engine) - %Game_id%
 * Copyright (C) 2004 - %year%  %Author% (%email%)
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
package org.jogre.%game_id%.common;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;

/**
 * Game model for the %game_id% game.
 *
 * @author  %Author%
 * @version %Version%
 */
public class %Game_id%Model extends JogreModel {

    /**
     * Constructor which creates the model.
     */
    public %Game_id%Model () {
        super (GAME_TYPE_TURN_BASED);
    }

    /**
     * Method which reads in XMLElement and sets the state of the models fields.
     * This method is necessary for other users to join a game and have the
     * state of the game set properly.
     *
     * @param message    Data stored in message.
     */
    public void setState (XMLElement message) {
        // Wipe everything
        reset ();

        // TODO - Fill in

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

    /**
     * Reset the pieces.
     */
    public void reset () {
        // TODO - Fill in


        // inform any graphical observers
        refreshObservers();
    }

    /**
     * Implementation of a %game_id% game - This is stored on the server and is used
     * when a player visits a game and a game is in progress.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        // Create empty model XML to populate
        XMLElement state = new XMLElement (Comm.MODEL);

        // TODO - Fill in

        return state;
    }
}