/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.quetris.client;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreClientFrame;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Game client frame for a game of quetris.  This is the
 * entry point to the game as an application.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class QuetrisClientFrame extends JogreClientFrame {

    /**
     * Constructor which takes command arguments.
     *
     * @param args     Command arguments from dos.
     */
    public QuetrisClientFrame (String [] args) {
        super (args);
    }

    /**
     * Return the correct table frame.
     *
     * @see org.jogre.client.awt.JogreClientFrame#getJogreTableFrame(org.jogre.client.TableConnectionThread)
     */
    public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
        return new QuetrisTableFrame (conn);
    }

    /**
     * Main method which executes a game of quetris.
     *
     * @param args
     */
    public static void main (String [] args) {
        QuetrisClientFrame client = new QuetrisClientFrame (args);
    }
}
