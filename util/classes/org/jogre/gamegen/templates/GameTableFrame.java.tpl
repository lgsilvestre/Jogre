filename=%game_id%/src/org/jogre/%game_id%/client/%Game_id%TableFrame.java
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
package org.jogre.%game_id%.client;

import info.clearthought.layout.TableLayout;

import org.jogre.%game_id%.common.%Game_id%Model;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Game table for a %Game_id%.  This class holds the MVC class for a
 * game of %game_id%.  The MVC classes are %Game_id%Model, %Game_id%Component
 * and %Game_id%Controller respectively.
 *
 * @author  %Author%
 * @version %Version%
 */
public class %Game_id%TableFrame extends JogreTableFrame {

    // Declare MVC classes
    private %Game_id%Model      %game_id%Model;      // model
    private %Game_id%Component  %game_id%Component;  // view
    private %Game_id%Controller %game_id%Controller; // controller

    /**
     * Constructor which sets up the MVC classes and takes
     * table connection.
     *
     * @param conn
     * @param tableNum
     */
    public %Game_id%TableFrame (TableConnectionThread conn)
    {
        super (conn);

        // Initialise MVC classes
        this.%game_id%Model = new %Game_id%Model ();
        this.%game_id%Component = new %Game_id%Component (%game_id%Model);
        this.%game_id%Controller = new %Game_id%Controller (%game_id%Model, %game_id%Component);

        // The component observes the model
        this.%game_id%Model.addObserver(this.%game_id%Component);

        // Set client/server connection on controller FIXME - constructor?
        this.%game_id%Controller.setConnection (conn);

        // Enable main view to recieve user input (e.g. mouse clicks) by setting controller
        this.%game_id%Component.setController (%game_id%Controller);

        // Set game data and controller (constructor must always call these)
        setupMVC (%game_id%Model, %game_id%Component, %game_id%Controller);

        // Create game panel and add main view to it
        double pref = TableLayout.PREFERRED;
        double [][] sizes = {{pref}, {pref}};       // simple 1x1 table
        JogrePanel panel = new JogrePanel (sizes);
        panel.add (%game_id%Component, "0,0");

        // Set game panel
        setGamePanel (panel);

        pack();
    }

    /**
     * Override to ensure that the player is seated in the correct position.
     *
     * @param tableAction
     */
    public void startGame () {
        super.startGame ();
    }
}