/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.chess.client;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JScrollBar;


/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Component which displays the history of the moves which have been taken.
 */
public class ChessHistoryComponent extends JComponent implements Observer {

	private static final int WIDTH = 150;
	private static final int HEIGHT = 400;

	// link to current game data structure
	protected ChessModel chessGameData;
	protected GameHistory gameHistory;

	// Declare GUI elements
	// !!!! NOTE THESE SHOULD CHANGE TO MY OWN CUSTOM GUI CLASSES !!!
	//BobButton startButton, endButton, backButton, forwardButton;
	JScrollBar scrollBar = new JScrollBar ();

	/** Constructor to the chess history component
	 * @param gameData
	 *
	public ChessHistoryComponent (ChessGameData chessGameData) {
		// Create link to the game data
		this.chessGameData = chessGameData;
		this.gameHistory = chessGameData.getGameHistory();
		this.setLayout(new FlowLayout());

		// Set up the size of this component
		setPreferredSize(new Dimension (WIDTH, HEIGHT));

		// Creat main panel
		JPanel panel = new JPanel (new BorderLayout());

		// Create button panel and add the 4 buttons to it
		JPanel buttonPanel = new JPanel (new GridLayout (1,4));
		startButton = new BobButton ("<<", WIDTH / 4 - 1, 25);
		endButton = new BobButton ("<", WIDTH / 4 - 1, 25);
		backButton = new BobButton (">", WIDTH / 4 - 1, 25);
		forwardButton = new BobButton (">>", WIDTH / 4 - 1, 25);
		buttonPanel.add (startButton);
		buttonPanel.add (endButton);
		buttonPanel.add (backButton);
		buttonPanel.add (forwardButton);

		// Add various components to the main component
		panel.add (buttonPanel, BorderLayout.NORTH);
		panel.add (new JTextArea (4, 4), BorderLayout.CENTER);
		panel.add (new JScrollBar (), BorderLayout.EAST);

		// Add 4 buttons and a table
		add (startButton);
	}*/

	/** This method is called from the Observer (Game Data)
	*/
	public void update(Observable observable, Object obj) {
		repaint ();
	}

	// This class is for the actual list of compents
}