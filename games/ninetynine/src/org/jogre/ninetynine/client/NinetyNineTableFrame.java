/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.ninetynine.client;

import javax.swing.BorderFactory;
import java.awt.Color;

import org.jogre.common.comm.Comm;
import org.jogre.common.util.GameProperties;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import org.jogre.ninetynine.std.CardHandComponent;
import org.jogre.ninetynine.std.CardHandController;
import org.jogre.ninetynine.std.ICardRenderer;

import nanoxml.XMLElement;

import info.clearthought.layout.TableLayout;

// Jogre table frame
public class NinetyNineTableFrame extends JogreTableFrame {

	// Main model & controller
	private NinetyNineClientModel model;
	private NinetyNineController masterController;
	
	// Components of the view
	private CardHandComponent [] handComp = new CardHandComponent [3];
	private CardHandComponent [] bidComp = new CardHandComponent [3];
	private CardHandComponent [] playedCardComp = new CardHandComponent [3];
	private CardHandComponent [] takenTricksComp = new CardHandComponent [3];
	private NinetyNineScoreComponent scoreComponent;

	// Controllers for my bid & hand components.  (Other components don't need controllers)
	private CardHandController myHandController;
	private CardHandController myBidController;

	// Constructor which passes a client connection thread
	public NinetyNineTableFrame (TableConnectionThread conn) {
		super (conn);

		// Get game paramters from the table
		int numRoundsInGame = Integer.parseInt(table.getProperty("rounds"));

		// Create the model
		model = new NinetyNineClientModel(numRoundsInGame);

		// Make the components that display the model
		makeComponents();

		// Create the master controller which updates the model and communicates with
		// the server
		masterController = new NinetyNineController (model, handComp[0], bidComp[0], playedCardComp, this);
		masterController.setConnection (conn);

		// Create the controllers for my hand & bid components and attach to components
		myHandController = new CardHandController(masterController, handComp[0]);
		myBidController  = new CardHandController(masterController, bidComp[0]);
		handComp[0].setController(myHandController);
		bidComp[0].setController(myBidController);

		// Set up MVC classes in super class
		setupMVC (model, handComp[0], masterController);

		// Create the panel and set it in the table frame
		JogrePanel panel = makeNinetyNinePanel();
		setGamePanel (panel);
		pack();
	}

	/**
	 * Recieve the table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage(XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(Comm.PLAYER_STATE)) {
			updateComponentColors();
		} else if (messageType.equals(Comm.EXIT_TABLE)) {
			updateComponentColors();
		} else {
			// Send all other Table messages to the master controller
			masterController.receiveTableMessage(message);
		}
	}

	/** 
	 * Update the colors of the components based upon the current player ID's.
	 */
	private void updateComponentColors() {
		int mySeatNum;

		// Determine our seat number so that we can put ourselves at the top.
		mySeatNum = masterController.getSeatNum();
		if (mySeatNum < 0) {
			// If we're not sitting down, then display the game as if we were
			// player 0.
			mySeatNum = 0;
		}

		// Change the colors of the player stripes in the score component to
		// match the new player ID
		scoreComponent.setFirstPlayer(mySeatNum);

		// Repaint to show the new stripes
		scoreComponent.repaint();
	}

	/**
	 * Create the panels of the table
	 */
	private JogrePanel makeNinetyNinePanel() {

		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
			{5, pref, 5, pref, 5, pref, 5, pref, 5},
			{5, pref, 5, pref, 5, pref, 5, pref, 5, pref, 5} };

		JogrePanel gamePanel = new JogrePanel (table_params);
		for (int i=0; i<3; i++) {
			int yoff = 3+(i*2);
			gamePanel.add(bidComp[i], "1,"+yoff);
			gamePanel.add(handComp[i], "3,"+yoff);
			if (i != 0) {
				gamePanel.add(playedCardComp[i], "5,"+yoff);
			}
			gamePanel.add(takenTricksComp[i], "7,"+yoff);
		}
		gamePanel.add(scoreComponent, "3,9,7,9");
		gamePanel.add(masterController.makeBidButtonPanel(playedCardComp[0]), "5,3");

		// Add the player components
		double[][] playerComponentSizes = {{5, pref, 5}, {pref, 5, pref, 5, pref}};
		JogrePanel playerComponentPanel = new JogrePanel (playerComponentSizes);
		playerComponentPanel.add (new PlayerComponent (conn, 0, true), "1,0,l,c");
		playerComponentPanel.add (new PlayerComponent (conn, 1, true), "1,2,l,c");
		playerComponentPanel.add (new PlayerComponent (conn, 2, true), "1,4,l,c");
		gamePanel.add(playerComponentPanel, "1,9,l,t");

		return gamePanel;
	}

	/**
	 * Create the components used for the NinetyNine game
	 */
	private void makeComponents() {
		NinetyNineGraphics NN_graphics = NinetyNineGraphics.getInstance();

		// Read settings from the game.properties file
		GameProperties props = GameProperties.getInstance();
		int cardSpacing = props.getInt("display.hand.cardSpacing", 19);
		int suitSpacing = props.getInt("display.hand.suitSpacing", 30);
		int selectYOffset = props.getInt("display.hand.selectYOffset", 20);
		int takenSpacing = props.getInt("display.taken.cardSpacing", 5);
		int scoreDigitSpacing = props.getInt("display.score.digitSpacing", 2);
		int scoreHSpacing = props.getInt("display.score.HSpacing", 5);
		int scoreVSpacing = props.getInt("display.score.VSpacing", 3);

		for (int i=0; i<3; i++) {
			// Only player 0 (us) has a selection offset
			int sYoff = (i == 0) ? selectYOffset : 0;

			bidComp[i] = new CardHandComponent (
				model.getBid(i),			// theHand
				NN_graphics,				// cardRenderer
				cardSpacing,				// cardSpacing
				cardSpacing,				// suitSpacing		Note: No special suit spacing
				sYoff,						// selectYOffset
				3,							// maxCardsInHand
				1,							// maxSuitsInHand	Note: Suits don't matter
				null						// bgColor
			);

			handComp[i] = new CardHandComponent (
				model.getHand(i),			// theHand
				NN_graphics,				// cardRenderer
				cardSpacing,				// cardSpacing
				suitSpacing,				// suitSpacing
				sYoff,						// selectYOffset
				12,							// maxCardsInHand
				4,							// maxSuitsInHand
				null						// bgColor
			);

			playedCardComp[i] = new CardHandComponent (
				model.getPlayedCardAsHand(i),	// theHand
				NN_graphics,					// cardRenderer
				0,								// cardSpacing		Note: Suits don't matter
				0,								// suitSpacing		Note: Suits don't matter
				sYoff,							// selectYOffset
				1,								// maxCardsInHand
				1,								// maxSuitsInHand	Note: Suits don't matter
				null							// bgColor
			);

			takenTricksComp[i] = new CardHandComponent (
				model.getTakenTricksHand(i),	// theHand
				NN_graphics,					// cardRenderer
				takenSpacing,					// cardSpacing
				takenSpacing,					// suitSpacing		Note: No special suit spacing
				sYoff,							// selectYOffset
				9,								// maxCardsInHand
				1,								// maxSuitsInHand	Note: Suits don't matter
				null							// bgColor
			);

		}

		// Create component for the score
		scoreComponent = new NinetyNineScoreComponent (
			model,							// model
			NN_graphics,					// numRenderer
			NN_graphics,					// suitRenderer
			scoreDigitSpacing,				// digitSpacing
			scoreHSpacing,					// HSpacing
			scoreVSpacing					// VSpacing
		);

		// ***RAW: Probably don't need this, as the scoreComponent has a addObserver(this) in it.
		// The score component observes the model
		model.addObserver(scoreComponent);
	}
}

