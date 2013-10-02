/*
 * JOGRE (Java Online Gaming Real-time Engine) - Tetris
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
package org.jogre.tetris.client;

import java.awt.BorderLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreGlassPane;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Tetris table frame.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class TetrisTableFrame extends JogreTableFrame {

	// Add MVC & server connection
	private TetrisModel      tetrisModel;
	private TetrisComponent  tetrisComponent;
	private TetrisController tetrisController;

	/**
	 * Constructor for a tetris table frame.
	 * 
	 * @param conn     Connection to the server.
	 */
	public TetrisTableFrame (TableConnectionThread conn) {
		super (conn);

		// set up game data
		tetrisModel = new TetrisModel ();

		// set up main game area component
		tetrisComponent = new TetrisComponent (tetrisModel, conn);

		// Add a data observer on the data structure
		tetrisModel.addObserver (tetrisComponent);

		// Create controller
		tetrisController = new TetrisController (tetrisModel, tetrisComponent);
		tetrisController.setConnection (conn);
		tetrisComponent.setController (tetrisController);
		
		// Add tetris  component to the left hand side
		JogrePanel gamePanel = new JogrePanel (new BorderLayout ());
		gamePanel.add (tetrisComponent, BorderLayout.CENTER);
		setGamePanel (gamePanel);

		// Set up the model / view / controller
		setupMVC (tetrisModel, tetrisComponent, tetrisController);
		pack ();
	}
}