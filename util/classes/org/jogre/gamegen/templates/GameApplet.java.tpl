filename=%game_id%/src/org/jogre/%game_id%/client/%Game_id%Applet.java
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

import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogreClientApplet;
import org.jogre.client.TableConnectionThread;

/**
 * Applet class for %Game_id%.
 *
 * @author  %Author%
 * @version %Version%
 */
public class %Game_id%Applet extends JogreClientApplet {

    /**
     * Applet constructor for a game of %game_id%.
     *
     * @param conn
     * @param tableNum
     */
    public %Game_id%Applet () {
        super ();
    }

    /**
     * Return the correct table frame
     *
     * @param conn
     * @param tableNum
     */
    public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
        return new %Game_id%TableFrame (conn);
    }
}
