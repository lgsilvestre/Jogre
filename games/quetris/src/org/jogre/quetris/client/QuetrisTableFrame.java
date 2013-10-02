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

import java.awt.BorderLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Game table for a Quetris.  This class holds the MVC class for a
 * game of quetris.  The MVC classes are QuetrisModel, QuetrisComponent
 * and QuetrisController respectively.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class QuetrisTableFrame extends JogreTableFrame {

    // Declare MVC classes
    private QuetrisModel      quetrisModel;      // model
    private QuetrisComponent  quetrisComponent;  // view
    private QuetrisController quetrisController; // controller

    /**
     * Constructor which sets up the MVC classes and takes
     * table connection.
     *
     * @param conn
     * @param tableNum
     */
    public QuetrisTableFrame (TableConnectionThread conn)
    {
    	super (conn);
		
    	// set up game data
		quetrisModel = new QuetrisModel ();

		// set up main game area component
		quetrisComponent = new QuetrisComponent (quetrisModel, conn);

		// Add a data observer on the data structure
		quetrisModel.addObserver (quetrisComponent);

		// Create controller
		quetrisController = new QuetrisController (quetrisModel, quetrisComponent);
		quetrisController.setConnection (conn);
		quetrisComponent.setController (quetrisController);
		
		// Add quetris  component to the left hand side
		JogrePanel gamePanel = new JogrePanel (new BorderLayout ());
		gamePanel.add (quetrisComponent, BorderLayout.CENTER);
		setGamePanel (gamePanel);

		// Set up the model / view / controller
		setupMVC (quetrisModel, quetrisComponent, quetrisController);
		pack ();
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
