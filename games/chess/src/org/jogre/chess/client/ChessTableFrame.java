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

import info.clearthought.layout.TableLayout;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Chess table frame
 */
public class ChessTableFrame extends JogreTableFrame {

	// Declare the game data.
	private ChessModel chessModel;

	// Declare components
	private ChessBoardComponent chessBoard;
	private CapturedPiecesComponent whiteCapturedPieces, blackCapturedPieces;

	// Declare the game controller
	private ChessController chessController;

	/**
	 * @param conn
	 * @param tableNum
	 */
	public ChessTableFrame (TableConnectionThread conn)
	{
		super (conn);

		// Create game data and chess board and add observers
		this.chessModel = new ChessModel ();
		this.chessBoard = new ChessBoardComponent (chessModel);
		this.whiteCapturedPieces = new CapturedPiecesComponent
			(chessModel, ChessModel.PLAYER_ONE);
		this.blackCapturedPieces = new CapturedPiecesComponent
			(chessModel, ChessModel.PLAYER_TWO);

		// Add component observers to the game data.
		chessModel.addObserver (chessBoard);
		chessModel.addObserver (whiteCapturedPieces);
		chessModel.addObserver (blackCapturedPieces);

		// Set up game controller
		this.chessController = new ChessController (chessModel, chessBoard);
		chessController.setConnection (conn);
		chessBoard.setController (chessController);   // set controller on board

		// Set game data and controller (constructor must always call these)
		setupMVC (chessModel, chessBoard, chessController);

		// Create game panel and add components to it.
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{10, pref, 10},
		                     {10, pref, 10, pref, 10}};
		JogrePanel gamePanel = new JogrePanel (sizes);
		JogrePanel playerPanel = new JogrePanel (new double [][] {{pref, TableLayout.FILL},{pref}});
		JogrePanel chessPanel = new JogrePanel (new double [][] {{pref, 5, pref, 5, pref},{pref}});
		gamePanel.add (playerPanel, "1,1");
		gamePanel.add (chessPanel, "1,3");		
		playerPanel.add (new PlayerComponent (conn, 0, true), "0,0,l,c");
		playerPanel.add (new PlayerComponent (conn, 1, false),  "1,0,r,c");
		chessPanel.add (whiteCapturedPieces, "0,0,l,t");
		chessPanel.add (chessBoard,          "2,0,c,t");
		chessPanel.add (blackCapturedPieces, "4,0,l,t");
		
		// Set game panel
		setGamePanel (gamePanel);

		pack();
	}

	/**
	 * Override to ensure that the player is seated in the correct position.
	 *
	 * @param tableAction
	 */
	public void startGame () {
		super.startGame ();

		// Ensure board is correctly reversed.
		chessBoard.setReversed (gameController.getSeatNum() == IChessModel.PLAYER_TWO);
		chessBoard.repaint();
	}
}