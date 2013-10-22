filename=%game_id%/src/org/jogre/%game_id%/client/%Game_id%Component.java
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

import java.awt.Graphics;

import org.jogre.%game_id%.common.%Game_id%Model;
import org.jogre.client.awt.JogreComponent;

/**
 * Main visual view for a game of %game_id% which should show a
 * graphical representation of the %Game_id%Model.
 *
 * @author  %Author%
 * @version %Version%
 */
public class %Game_id%Component extends JogreComponent {

    // Link to the model
    protected %Game_id%Model model;

    // Constructor which creates the board
    public %Game_id%Component (%Game_id%Model model) {
        super ();

        this.model = model;         // link to model
    }

    // Update the graphics depending on the model
    public void paintComponent (Graphics g) {
        super.paintComponent (g);
    }
}
