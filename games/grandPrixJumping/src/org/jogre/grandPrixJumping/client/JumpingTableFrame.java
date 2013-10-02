/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.client;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreButton;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import info.clearthought.layout.TableLayout;


/**
 * Table Frame for Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingTableFrame extends JogreTableFrame {

	// The game model
	private JumpingClientModel clientModel;

	// The components in the table frame and their controllers
	private JumpingTrackComponent trackComponent;
	private JumpingTrackController trackController;
	private JumpingPlayerHandComponent [] handComponents = new JumpingPlayerHandComponent [2];
	private JumpingImmCardComponent [] immCardComponents = new JumpingImmCardComponent [2];
	private JumpingPlayerHandController handController;
	private JumpingImmCardController immCardController;
	private JumpingFaultController faultController;
	private JumpingSortAreaComponent sortComponent;
	private JumpingSortAreaController sortController;
	private JumpingFaultIndicatorComponent faultComponent;
	private JumpingGameStateComponent gameStateComponent;

	// The master controller for the game as a whole
	private JumpingMasterController masterController;

	// The main gamePanel.
	private JogrePanel gamePanel;

	// Get access to the labels from the game_labels.properties file
	GameLabels labels;

	// Get access to the game.properties file
	GameProperties props;

	// The done button
	private JogreButton doneButton;

	// The titled borders around the player card components
	private TitledBorder [] playerCardBorders = new TitledBorder [2];

	// Declare constants which define what the layout looks like
	private static final int DEFAULT_HAND_CARD_SPACING = 18;
	private static final int DEFAULT_HAND_SELECTED_Y_OFFSET = 20;

	// Constructor which passes a client connection thread
	public JumpingTableFrame (TableConnectionThread conn) {
		super (conn);

		// Get properties from the table
		boolean openHands = ("t".equals(table.getProperty("openHands")));
		boolean allowEdits = ("t".equals(table.getProperty("allowEdits")));
		String initialLayout = table.getProperty("initialLayout");

		// Get a GameLabels object to use for the text of the components and
		// a GameProperties object to get access to the properties.
		labels = GameLabels.getInstance();
		props = GameProperties.getInstance();

		// Create the done button
		doneButton = createDoneButton(labels.get("done.button"));

		// Get some properties from the game.properties file
		int handCardSpacing = props.getInt("hand.card.spacing", DEFAULT_HAND_CARD_SPACING);
		int handSelectedVerticalOffset = props.getInt("hand.selected.vertical.offset", DEFAULT_HAND_SELECTED_Y_OFFSET);
		int handHighlightImageIndex = props.getInt("turn.highlight.index", 0);

		// Create model and views
		clientModel = new JumpingClientModel(openHands, allowEdits, initialLayout);
		trackComponent = new JumpingTrackComponent(clientModel);
		handComponents[0] = new JumpingPlayerHandComponent(clientModel, 0, handCardSpacing, handSelectedVerticalOffset, handHighlightImageIndex);
		handComponents[1] = new JumpingPlayerHandComponent(clientModel, 0, handCardSpacing, 5, handHighlightImageIndex);
		immCardComponents[0] = new JumpingImmCardComponent(clientModel, 0, handCardSpacing, handSelectedVerticalOffset);
		immCardComponents[1] = new JumpingImmCardComponent(clientModel, 0, handCardSpacing, 5);
		sortComponent = new JumpingSortAreaComponent(clientModel);
		faultComponent = new JumpingFaultIndicatorComponent (clientModel);
		gameStateComponent = new JumpingGameStateComponent (clientModel, table);

		// Add the components as observers of the model
		clientModel.addObserver(trackComponent);
		clientModel.addObserver(handComponents[0]);
		clientModel.addObserver(handComponents[1]);
		clientModel.addObserver(immCardComponents[0]);
		clientModel.addObserver(immCardComponents[1]);
		clientModel.addObserver(sortComponent);
		clientModel.addObserver(faultComponent);
		clientModel.addObserver(gameStateComponent);

		// Create controllers for the components
		trackController = new JumpingTrackController(clientModel, trackComponent);
		handController = new JumpingPlayerHandController(clientModel, handComponents[0]);
		immCardController = new JumpingImmCardController(clientModel, immCardComponents[0]);
		sortController = new JumpingSortAreaController(clientModel, sortComponent);
		faultController = new JumpingFaultController(clientModel, faultComponent, this);

		// Set the connections for the controllers
		trackController.setConnection(conn);
		handController.setConnection(conn);
		immCardController.setConnection(conn);
		sortController.setConnection(conn);
		faultController.setConnection(conn);

		// Connect the components and the controllers together
		trackComponent.setController(trackController);
		handComponents[0].setController(handController);
		immCardComponents[0].setController(immCardController);
		sortComponent.setController(sortController);
		faultComponent.setController(faultController);

		// Create the master controller that runs the game
		masterController = new JumpingMasterController(
		                        clientModel, doneButton, sortComponent,
		                        getMessageComponent(), handComponents,
		                        immCardComponents, trackComponent,
		                        faultComponent);
		masterController.setConnection(conn);

		// Attach the master controller to the other controllers.
		sortController.setMasterController(masterController);
		handController.setMasterController(masterController);
		trackController.setMasterController(masterController);
		immCardController.setMasterController(masterController);

		// Create a game panel and add all of the components to it.
		createGamePanel();

		// Update the PlayerCardComponents to reflect seated players
		updatePlayerCardComponents();

		// Set up MVC classes in super class
		// (Note: The masterController doesn't have a component, so I'm using
		//         the trackComponent just to have something to provide.)
		setupMVC (clientModel, trackComponent, masterController);

		// Add the panel to the table frame and pack the panel
		setGamePanel(gamePanel);
		pack();
	}

	/**
	 * Create a Box with a titled border and place the given component inside of that
	 * box.
	 *
	 * @param   c       The component to place in the box
	 * @param   title   The title of the border
	 * @return The box component created.
	 */
	private Box createTitledBoxAround(Component c, String title) {
		Box b = new Box (BoxLayout.X_AXIS);
		b.add(c);
		b.setBorder(BorderFactory.createTitledBorder (
		                BorderFactory.createEtchedBorder(),
		                title
		           ));
		return (b);
	}

	/**
	 * Create a Box with a titled border and place the given component inside of that
	 * box.  This uses a default title of the horse name for player i.
	 * The titles are actually set in updatePlayerCardComponents().
	 * This is used for the playerCardBorders where we need to keep the handles around
	 * to change the title text.
	 *
	 * @param   c      The component to place in the box
	 * @param   i      The index of playedCardBorders[] to use to store the Border into
	 * @return The box component created.
	 */
	private Box createTitledBoxAround(Component c, int i) {
		Box b = new Box (BoxLayout.X_AXIS);
		b.add(c);
		playerCardBorders[i] = BorderFactory.createTitledBorder (
		                        BorderFactory.createEtchedBorder(),
		                        labels.get("player.label." + i)
		                       );
		b.setBorder(playerCardBorders[i]);
		return (b);
	}

	private final static int SPACER_SIZE = 3;

	private void createGamePanel() {
		double pref = TableLayout.PREFERRED;
		double[][] outer_table_params = {
		    {SPACER_SIZE, pref, SPACER_SIZE, pref, SPACER_SIZE},
		    {SPACER_SIZE, pref, SPACER_SIZE, pref, SPACER_SIZE}
		};

		gamePanel = new JogrePanel (outer_table_params);
		gamePanel.add(createLeftColumn(), "1,3");
		gamePanel.add(createRightColumn(), "3,3");

		gamePanel.add (new PlayerComponent (conn, 0, true), "1,1,l,c");
		gamePanel.add (new PlayerComponent (conn, 1, true), "3,1,l,c");
	}

	private JogrePanel createLeftColumn() {
		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
		    {pref}, {pref, SPACER_SIZE, pref, SPACER_SIZE, pref, SPACER_SIZE, .99}
		};

		JogrePanel leftColumn = new JogrePanel (table_params);
		leftColumn.add(createTitledBoxAround(faultComponent, labels.get("tableframe.faults")), "0,0");
		leftColumn.add(doneButton, "0,2,c,c");
		leftColumn.add(createTitledBoxAround(sortComponent, labels.get("tableframe.sort")), "0,4");
		leftColumn.add(createTitledBoxAround(gameStateComponent.theTextArea, labels.get("tableframe.state")), "0,6");
		leftColumn.add(gameStateComponent, "0,1");

		return leftColumn;
	}

	private JogrePanel createRightColumn() {
		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
		    {pref}, {pref, SPACER_SIZE, pref, SPACER_SIZE, pref}
		};

		JogrePanel rightColumn = new JogrePanel (table_params);
		rightColumn.add(createTitledBoxAround(createHandPanel(0), 0), "0,0");
		rightColumn.add(trackComponent, "0,2");
		rightColumn.add(createTitledBoxAround(createHandPanel(1), 1), "0,4");

		return rightColumn;
	}

	private JogrePanel createHandPanel(int who) {
		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
		    {pref, SPACER_SIZE, pref}, {pref}
		};

		JogrePanel handPanel = new JogrePanel (table_params);
		handPanel.add(handComponents[who], "0,0");
		handPanel.add(immCardComponents[who], "2,0");

		return handPanel;
	}

	/**
	 * Create & configure the "Done" Button.
	 */
	public JogreButton createDoneButton(String buttonText) {
		doneButton = new JogreButton (buttonText);

		doneButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				// Disable the button
				doneButton.setEnabled(false);
				// Call the routine in the master controller to handle the button click
				masterController.doneButtonClicked();
			}
		});

		doneButton.setEnabled(false);

		return doneButton;
	}


	/**
	 * Recieve the table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage(XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(Comm.PLAYER_STATE)) {
			updatePlayerCardComponents();
		} else if (messageType.equals(Comm.EXIT_TABLE)) {
			updatePlayerCardComponents();
		} else {
			// Send all other Table messages to the master controller
			masterController.receiveTableMessage(message);
		}
	}

	/**
	 * Update the titles and the player assignments of the played card components.
	 *
	 * This takes into account changes as players sit down and stand up.
	 *
	 * A player that is sitting always sees himself at the top, with the other
	 * player at the bottom.
	 *
	 * A player not seated always sees player 0 at the top and player 1 at the bottom.
	 *
	 */
	public void updatePlayerCardComponents() {

		// Determine our seat number so that we can put ourselves at the top.
		int mySeatNum = masterController.getSeatNum();
		int visualSeatNum = (mySeatNum < 0) ? 0 : mySeatNum;

		// Set the border titles of the areas
		setBorderTitle(0, visualSeatNum);
		setBorderTitle(1, 1-visualSeatNum);

		// Set the seat numbers for the components correctly.
		handComponents[0].setPlayerId(visualSeatNum);
		immCardComponents[0].setPlayerId(visualSeatNum);
		handComponents[1].setPlayerId(1-visualSeatNum);
		immCardComponents[1].setPlayerId(1-visualSeatNum);
		trackComponent.setPlayerId(mySeatNum);
		faultComponent.setTopPlayerId(visualSeatNum);

		// Repaint the gamePanel to show the new titles
		gamePanel.repaint();
	}

	private Object [] borderNameObjList = null;

	/**
	 * This will set the title text for one of the player's card areas
	 * the approprate text, given the current configuration of the game.
	 *
	 * @param   borderId    The Id number of the border to set. (0=top, 1=bottom)
	 * @param   seatNum     The seat number of the player to put in that border.
	 */
	private void setBorderTitle(int borderId, int seatNum) {
		String theTitle;

		Player player = table.getPlayerList().getPlayer(seatNum);

		// Determine the title to use
		if (player == null) {
			theTitle = labels.get("player.label." + seatNum);
		} else {
			// Create the object list once and then keep it around...
			if (borderNameObjList == null) {
				borderNameObjList = new Object [3];
				borderNameObjList[1] = labels.get("player.label.0");
				borderNameObjList[2] = labels.get("player.label.1");
			}

			// Need to change the player's name each time.
			borderNameObjList[0] = player.getPlayerName();

			theTitle = labels.get("frame.text." + seatNum, borderNameObjList);
		}

		// Set the title of the border
		playerCardBorders[borderId].setTitle(theTitle);

		// Set the heading of the fault history window
		faultController.setPlayerName(seatNum, theTitle);
	}
}
