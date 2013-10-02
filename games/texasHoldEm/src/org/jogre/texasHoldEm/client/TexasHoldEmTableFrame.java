/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSeparator;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import org.jogre.common.util.GameLabels;

import org.jogre.texasHoldEm.std.TexasHoldEmButton;

import info.clearthought.layout.TableLayout;

/**
 * TexasHoldEm table frame.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmTableFrame extends JogreTableFrame {

	// Components of the game.
	private TexasHoldEmClientModel model;
	private TexasHoldEmBoardComponent boardComponent;
	private TexasHoldEmBidProgressionComponent bidProgressionComponent;
	private TexasHoldEmBidTransitionComponent bidTransitionComponent;
	private TexasHoldEmBidSliderComponent bidSliderComponent;
	private TexasHoldEmHistoryComponent historyComponent;
	private TexasHoldEmController controller;

	// Buttons in the game
	private TexasHoldEmButton foldButton;
	private TexasHoldEmButton callButton;
	private TexasHoldEmButton bidButton;
	
	// The main gamePanel.
	private JogrePanel gamePanel;

	// Constructor which passes a client connection thread
	public TexasHoldEmTableFrame (TableConnectionThread conn) {
		super (conn);

		// Get the game parameters from the table property
		int numPlayers = table.getNumOfPlayers();
		int initialBankroll = Integer.parseInt(table.getProperty("initialBankroll"));
		int initialBlindSchedule = Integer.parseInt(table.getProperty("initialBlindSched"));
		int blindAdvanceTime = Integer.parseInt(table.getProperty("BlindAdvance"));
		int raiseLimit = Integer.parseInt(table.getProperty("RaiseLimit"));

		// Create model, view and register view to model
		model = new TexasHoldEmClientModel (numPlayers,
		                                    initialBankroll,
		                                    initialBlindSchedule,
		                                    blindAdvanceTime,
		                                    raiseLimit);
		boardComponent = new TexasHoldEmBoardComponent (model);
		bidProgressionComponent = new TexasHoldEmBidProgressionComponent (model);
		bidTransitionComponent = new TexasHoldEmBidTransitionComponent (model);
		bidSliderComponent = new TexasHoldEmBidSliderComponent (model, numPlayers * initialBankroll);
		historyComponent = new TexasHoldEmHistoryComponent (model);

		// The components observe the model
		model.addObserver(boardComponent);
		model.addObserver(bidProgressionComponent);
		model.addObserver(bidSliderComponent);
		model.addObserver(historyComponent);
		// Note: The bid transition component doesn't observe the model.
		//       It is updated directly by the controller.

		// Create the fold & bid buttons
		createButtons();

		// Create controller which updates model and controls the view
		controller = new TexasHoldEmController (model,
		                                        bidSliderComponent,
		                                        foldButton,
		                                        callButton,
		                                        bidButton);
		controller.setConnection (conn);
		bidSliderComponent.setController(controller);

		// Set up MVC classes in super class
		setupMVC (model, bidSliderComponent, controller);

		// Create the game panel to place the pieces in.
		double pref = TableLayout.PREFERRED;
		int SPACER_SIZE = 3;
		double [][] leftColumnTableParams = {{pref}, {pref, SPACER_SIZE, pref, SPACER_SIZE, pref}};
		double [][] rightColumnTableParams = {{pref}, {pref, SPACER_SIZE, pref}};
		double [][] playerCompTableParams = {{SPACER_SIZE, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125}, {pref}};
		double [][] tableParams = {
			{SPACER_SIZE, pref, SPACER_SIZE, pref, SPACER_SIZE},
			{SPACER_SIZE, pref, SPACER_SIZE, pref, SPACER_SIZE, pref, SPACER_SIZE}
		};

		JogrePanel leftColumn = new JogrePanel (leftColumnTableParams);
		JogrePanel rightColumn = new JogrePanel (rightColumnTableParams);
		JogrePanel playerCompStrip = new JogrePanel (playerCompTableParams);
		gamePanel = new JogrePanel (tableParams);

		leftColumn.add (bidProgressionComponent, "0,0");
		leftColumn.add (bidTransitionComponent, "0,2");
		leftColumn.add (historyComponent, "0,4");
		rightColumn.add (boardComponent, "0,0");
		rightColumn.add (createSliderAndButtons(), "0,2");
		for (int i=0; i<8; i++) {
			playerCompStrip.add (new PlayerComponent (conn, i), (i+1) + ",0,l,c");
		}

		gamePanel.add (leftColumn, "1,1");
		gamePanel.add (rightColumn, "3,1");
		gamePanel.add (new JSeparator (), "1,3,3,3");
		gamePanel.add (playerCompStrip, "1,5,3,5");

		setGamePanel (gamePanel);
		pack();
	}

	/*
	 * Create the slider & buttons panel
	 */
	private JogrePanel createSliderAndButtons() {
		double pref = TableLayout.PREFERRED;
		int SPACER_SIZE = 3;
		double[][] tableParams = {
			{pref, SPACER_SIZE, 100},
			{0.33, SPACER_SIZE, 0.34, SPACER_SIZE, 0.33}
		};

		JogrePanel sliderPanel = new JogrePanel (tableParams);
		sliderPanel.add (bidSliderComponent, "0,0,0,4");
		sliderPanel.add (foldButton, "2,0");
		sliderPanel.add (callButton, "2,2");
		sliderPanel.add (bidButton, "2,4");

		return sliderPanel;
	}

	/*
	 * Create & configure the "Fold" & "Bid" Buttons.
	 */
	private void createButtons() {
		GameLabels labels = GameLabels.getInstance();
		foldButton = new TexasHoldEmButton (labels.get("button.fold"));
		callButton = new TexasHoldEmButton (labels.get("button.call1"));
		bidButton = new TexasHoldEmButton (labels.get("button.bid1"));

		foldButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				// Disable all buttons
				foldButton.setEnabled(false);
				callButton.setEnabled(false);
				bidButton.setEnabled(false);
				// Call the routine in the controller to handle the button click
				controller.foldButtonClicked();
			}
		});

		callButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				// Disable both buttons
				foldButton.setEnabled(false);
				callButton.setEnabled(false);
				bidButton.setEnabled(false);
				// Call the routine in the controller to handle the button click
				controller.callButtonClicked();
			}
		});

		bidButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				// Disable both buttons
				foldButton.setEnabled(false);
				callButton.setEnabled(false);
				bidButton.setEnabled(false);
				// Call the routine in the controller to handle the button click
				controller.bidButtonClicked();
			}
		});

		foldButton.setEnabled(false);
		callButton.setEnabled(false);
		bidButton.setEnabled(false);
	}

	/*
	 * Update the seating of the board & history components so that the player's
	 * position is in the lower middle spot.
	 */
	void updateSeats() {
		// Get the seat number from the controller
		int mySeatNum = controller.getSeatNum();

		// Set this seat as the zero-seat for the board & history components
		boardComponent.setSeatZeroPlayer(mySeatNum);
		historyComponent.setSeatZeroPlayer(mySeatNum);

		// Refresh the screen to display the new board.
		gamePanel.repaint();
	}

	/**
	 * Receive a table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage(XMLElement message) {
		String messageType = message.getName();

		if ((messageType.equals(Comm.PLAYER_STATE)) ||
		    (messageType.equals(Comm.EXIT_TABLE)) ) {
			updateSeats();
		} else if (messageType.equals(Comm.NEXT_PLAYER)) {
			controller.handleNextPlayerMessage();
		} else {
			// Send all other Table messages to the controller
			controller.receiveTableMessage(message);
		}
	}
}
