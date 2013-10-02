/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.checkers.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Test client for running a game of checkers offline.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreCheckersOffline extends JFrame {

	private CheckersBoardComponent board;
	private CheckersModel checkersModel;

	public JogreCheckersOffline () {
		super ("JOGRE Checkers - Version 0.1");
		// set up data object for the game
		checkersModel = new CheckersModel ();

		// set up GUI objects
		board = new CheckersBoardComponent (checkersModel);

		// Add observers on the data
		checkersModel.addObserver(board);

		// Create a controller for user input
		CheckersController controller =
			new CheckersController (checkersModel, board);
		board.setController (controller);

		JPanel panel = new JPanel ();
		panel.add (board);
		this.setContentPane(panel);
	}

	public static void main (String [] args) {
		JogreCheckersOffline client = new JogreCheckersOffline ();

		client.setSize(380, 480);
		client.setLocation(0, 380);

		client.setVisible(true);
		client.addWindowListener (
            new WindowAdapter () {
                public void windowClosing (WindowEvent e)
                {
                    System.exit (0);
                }
            }
        );
	}
}

