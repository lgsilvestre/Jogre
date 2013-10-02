/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.client;

import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JTextArea;
import javax.swing.JSeparator;

import java.awt.Component;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.PlayerComponent;

import org.jogre.carTricks.common.CarTricksTrackDB;
import org.jogre.carTricks.common.CarTricksTrackDBCache;
import org.jogre.carTricks.common.CommCarTricksGetTrackDB;
import org.jogre.carTricks.common.CommCarTricksTrackDB;
import org.jogre.carTricks.common.CommCarTricksTileRequest;
import org.jogre.carTricks.common.CommCarTricksTrackTileData;

import info.clearthought.layout.TableLayout;

// Jogre table frame
public class CarTricksTableFrame extends JogreTableFrame {

	// The game model
	private CarTricksClientModel clientModel;

	// The components in the table frame and their controllers
	private CarTricksTrackComponent trackComponent;
	private CarTricksTrackController trackController;
	private CarTricksPlayerHandComponent handComponent;
	private CarTricksPlayerHandController handController;
	private CarTricksPlayedCardComponent [] playedCardComponents;
	private CarTricksPlayedCardController playedCardController;
	private CarTricksPositionComponent actualComponent;
	private CarTricksPositionComponent bidComponent;
	private CarTricksPositionController bidController;
	private CarTricksGameStateComponent gameStateComponent;

	// The master controller for the game as a whole
	private CarTricksMasterController masterController;

	// The number of players
	private int numPlayers;

	// The titled borders around the played card components
	private TitledBorder [] playedCardBorders;

	// The main gamePanel.
	private JogrePanel gamePanel;

	// Get access to the lables from the game_labels.properties file
	private GameLabels labels;

	// Get access to the game.properties file
	private GameProperties props;

	// The cache of tracks known locally
	private CarTricksTrackDBCache trackCache;

	// Whether or not to save new tracks retrieved from the server or not.
	private boolean saveNewTracks;

	// Constructor which passes a client connection thread
	public CarTricksTableFrame (TableConnectionThread conn) {
		super (conn);

		// Get properties from the table
		numPlayers = table.getNumOfPlayers();
		boolean enableEventCards = ("t".equals(table.getProperty("enableEventCards")));
		String trackName = table.getProperty("trackName");
		int fingerprint = Integer.parseInt(table.getProperty("fp", "0"));

		// Get the database from our local cache, if it is there.
		// If we don't have a copy locally, then we'll initialize with what
		// we know and we'll ask the server to send us the full database.
		props = GameProperties.getInstance();
		String dataDir = props.get("local.trackDB.cache.dir");
		trackCache = CarTricksTrackDBCache.getInstance(dataDir);
		CarTricksTrackDB trackDB = trackCache.findTrack(trackName, fingerprint);

		// Create components
		playedCardBorders = new TitledBorder [numPlayers];
		playedCardComponents = new CarTricksPlayedCardComponent [numPlayers];

		// Create model and views
		clientModel = new CarTricksClientModel (trackDB, numPlayers, enableEventCards);
		trackComponent = new CarTricksTrackComponent (clientModel, trackDB);
		handComponent = new CarTricksPlayerHandComponent (clientModel);
		for (int i=0; i<numPlayers; i++) {
			playedCardComponents[i] = new CarTricksPlayedCardComponent(clientModel, i);
			clientModel.addObserver(playedCardComponents[i]);
		}
		actualComponent = new CarTricksPositionComponent (clientModel, CarTricksClientModel.CAR_POSITION_CODE);
		bidComponent = new CarTricksPositionComponent (clientModel, CarTricksClientModel.CAR_BID_CODE);
		gameStateComponent = new CarTricksGameStateComponent (clientModel, table);

		// Add the components as observers of the model
		clientModel.addObserver(trackComponent);
		clientModel.addObserver(handComponent);
		clientModel.addObserver(actualComponent);
		clientModel.addObserver(bidComponent);
		clientModel.addObserver(gameStateComponent);

		// Create controllers for the components
		trackController = new CarTricksTrackController (clientModel, trackComponent);
		handController = new CarTricksPlayerHandController(clientModel, handComponent);
		bidController = new CarTricksPositionController(clientModel, bidComponent);
		playedCardController = new CarTricksPlayedCardController(clientModel, playedCardComponents[0]);

		// Set the connections for the controllers
		handController.setConnection(conn);
		trackController.setConnection(conn);
		playedCardController.setConnection(conn);

		// Connect the components and the controllers together
		trackComponent.setController(trackController);
		handComponent.setController(handController);
		bidComponent.setController(bidController);
		playedCardComponents[0].setController(playedCardController);

		// Create the master controller that runs the game
		masterController = new CarTricksMasterController (clientModel, trackController, handController, gameStateComponent, this);
		masterController.setConnection(conn);

		// Get a GameLabels object to use for the text of the components
		labels = GameLabels.getInstance();

		// Create a game panel and add all of the components to it.
		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
			{5, pref, pref, 5, pref, 5, pref, 5},
			{5, pref, 5, pref, 5, pref, 5, pref, pref, pref, pref, 5, pref, 5} };

		gamePanel = new JogrePanel (table_params);
		gamePanel.add(trackComponent, "4,7,4,10");
		gamePanel.add(handComponent, "4,5");
		for (int i=1; i<numPlayers; i++) {
			gamePanel.add(createTitledBoxAround(playedCardComponents[i], i),
			             "6,"+(6+i));
		}
		gamePanel.add(createTitledBoxAround(playedCardComponents[0], 0), "6,5");

		gamePanel.add(gameStateComponent, "0,4");
		gamePanel.add(createTitledBoxAround(gameStateComponent.theTextArea, labels.get("tableframe.state")), "1,5,2,5");

		gamePanel.add(createTitledBoxAround(bidComponent, labels.get("tableframe.bid")), "1,7,1,10");
		gamePanel.add(createTitledBoxAround(actualComponent, labels.get("tableframe.actual")), "2,7,2,10");

		gamePanel.add(new JSeparator (), "1,3,6,3");
		gamePanel.add(createPlayerComponentStrip(conn), "1,1,6,1");

		// Update the PlayedCardComponents to reflect seated players
		updatePlayedCardComponents();

		// Set up MVC classes in super class
		// (Note: The masterController doesn't have a component, so I'm using the trackComponent
		//       just to have something to provide.)
		setupMVC (clientModel, trackComponent, masterController);

		// Add the panel to the table frame and pack the panel
		setGamePanel (gamePanel);
		pack();

		// Determine if we are supposed to save new tracks or not
		saveNewTracks = (props.getInt("local.trackDB.cache.saveNewTracks", 0) == 1)
		                 && !JogreUtils.isApplet();

		// Finally, if we don't have a database, then we need to request it from the server
		if (trackDB == null) {
			conn.send(new CommCarTricksGetTrackDB(username));
		} else {
			// Even if we do have the database, we might need some tiles... (Well, this is
			// **highly** unlikely, but we might as well check for it anyway...)
			checkForNeededTile(false);
		}
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
	 * box.  This uses a default title of "Player i".  The titles are actually set in
	 * updatePlayedCardComponents().  This is used for the playedCardAreas where we
	 * need to keep the handles around to change the title text.
	 *
	 * @param   c      The component to place in the box
	 * @param   i      The index of playedCardBorders[] to use to store the Border into
	 * @return The box component created.
	 */
	private Box createTitledBoxAround(Component c, int i) {
		Box b = new Box (BoxLayout.X_AXIS);
		b.add(c);
		playedCardBorders[i] = BorderFactory.createTitledBorder (
		                        BorderFactory.createEtchedBorder(),
		                        labels.get("tableframe.player") + " " + (i+1)
		                     );
		b.setBorder(playedCardBorders[i]);
		return (b);
	}

	/**
	 * Create the strip of player components that will be placed along the top
	 * of the table frame.
	 *
	 * @param  conn    The connection to the server
	 * @return the panel.
	 */
	private JogrePanel createPlayerComponentStrip(TableConnectionThread conn) {
		double pref = TableLayout.PREFERRED;
		double [][] stripSizes = {
			{5, 0.2, 0.2, 0.2, 0.2, 0.2, 5},
			{pref}
		};

		JogrePanel stripPanel = new JogrePanel (stripSizes);

		for (int i=0; i<5; i++) {
			stripPanel.add (new PlayerComponent (conn, i), (i+1) + ",0,l,c");
		}

		return stripPanel;
	}

	/**
	 * Recieve the table message from the server.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage(XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(Comm.PLAYER_STATE)) {
			updatePlayedCardComponents();
		} else if (messageType.equals(Comm.EXIT_TABLE)) {
			updatePlayedCardComponents();
		} else if (messageType.equals(CommCarTricksTrackDB.XML_NAME)) {
			// We're being given our database, so add it to the model &
			// track component.
			CommCarTricksTrackDB DBMsg = new CommCarTricksTrackDB(message);
			CarTricksTrackDB theDB = DBMsg.getDatabase();
			clientModel.addDatabase(theDB);
			trackComponent.addDatabase(theDB);
			trackCache.addNewTrack(theDB);
			masterController.clearSavedMessages();

			// If we have been configured to save track information, then
			// we need to create a directory for this track and put it there.
			if (saveNewTracks) {
				try {
					theDB.createDataDirectory(props.get("local.trackDB.cache.dir"));
					theDB.writeDB();
				} catch (Exception e) {
					// Something didn't work, so we didn't save the database locally...
					// oh, well.
				}
			}

			// Check to see if we need to ask for any tile images from the server
			checkForNeededTile(false);

			// Need to resize the panel, since the image of the track may be
			// a different size than the "download" image...
			pack();
		} else if (messageType.equals(CommCarTricksTrackTileData.XML_NAME)) {
			// We're being given some tile data
			CommCarTricksTrackTileData TileMsg = new CommCarTricksTrackTileData(message);
			clientModel.getTrackDatabase().setTileData(TileMsg.getTileName(), TileMsg.getTileData());
			checkForNeededTile(true);
		} else if (messageType.equals(Comm.NEXT_PLAYER)) {
			// Intercept next player messages to see if it's time to move
			// the car, and if so and the car only has 1 legal move, then
			// the client program will automatically make the move for the
			// human.
			masterController.checkForZeroLengthCarMove(message);
		} else {
			// Send all other Table messages to the master controller
			masterController.receiveTableMessage(message);
		}
	}

	/**
	 * Determine if we still need to get tiledata from the server, and send
	 * off a message if we do.
	 */
	private void checkForNeededTile(boolean redraw) {
		String neededTileName = trackComponent.checkForAllTiles();
		if (neededTileName != null) {
			// We need to ask the server for some more tile data
			conn.send(new CommCarTricksTileRequest(username, neededTileName));
		} else {
			if (redraw) {
				trackComponent.repaint();
			}
		}
	}


	/**
	 * Update the titles and the player assignments of the played card components.
	 * This takes into account changes as players sit down and stand up.  A player
	 * that is sitting always sees himself in the top spot, with the other players
	 * in order going down.  A player not seated always sees the players in order
	 * from top to bottom, with player 0 at the top.
	 *
	 * A player only enables the card prompts for himself when seated
	 */
	public void updatePlayedCardComponents() {
		String name;
		int index, mySeatNum;

		// Get the list of all players
		PlayerList players = table.getPlayerList();

		// Determine our seat number so that we can put ourselves at the top.
		mySeatNum = masterController.getSeatNum();
		if (mySeatNum < 0) {
			// If we're not sitting down, then display the game as if we were
			// player 0.
			mySeatNum = 0;
		}

		for (int i=0; i < numPlayers; i++) {
			// Determine the logical player number for this space.
			index = (i+mySeatNum) % numPlayers;

			// Get that player from the list
			Player player = players.getPlayer(index);
			name = ((player == null)
			            ? labels.get("tableframe.player") + " " + (index+1)
			            : player.getPlayerName());

			// Set the title of the border and tell the component who it is watching
			playedCardBorders[i].setTitle(name);
			playedCardComponents[i].setWhichPlayer(index);
		}

		// Repaint the gamePanel to show the new titles
		gamePanel.repaint();
	}
}
