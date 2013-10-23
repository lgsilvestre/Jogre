/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.server.data.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jogre.common.GameOver;
import org.jogre.common.util.JogreLogger;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.EloRatingSystem;
import org.jogre.server.ServerProperties;
import org.jogre.server.data.AbstractServerData;
import org.jogre.server.data.GameInfo;
import org.jogre.server.data.GameSummary;
import org.jogre.server.data.ServerDataException;
import org.jogre.server.data.User;

/**
 * Implementation of the IServerData interface which connects
 * connects to the local file system.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ServerDataXML extends AbstractServerData {

	// Declare file names
	private static final String USER_FILENAME     = "users.xml";
	private static final String GAMES_FILENAME    = "games.xml";
	private static final String SNAPSHOT_FILENAME = "snapshot.xml";

	// Declare user XML
	private static final String XML_ELM_USERS    = "users";
	private static final String XML_ELM_USER     = "user";
	private static final String XML_ATT_USERNAME = "username";
	private static final String XML_ATT_PASSWORD = "password";

	// Declare game XML
	private static final String XML_ELM_GAMES = "games";
	private static final String XML_ELM_GAME  = "game";
	private static final String XML_ATT_ID    = "id";

	// Declare snapshot XML
	private static final String XML_ELM_SNAPSHOTS     = "snapshots";
	private static final String XML_ATT_NUM_OF_USERS  = "numOfUsers";
	private static final String XML_ATT_NUM_OF_TABLES = "numOfTables";

	// Declare fields
	private File userFile, gameFile, snapshotFile;

	// Declare user / game XML documents
	private Document userDoc = null;
	private Document gameDoc = null;
	private Document snapshotDoc = null;

	// Formatter
	private OutputFormat format;

	/**
	 * Constructor for a user connecting locally on a computer.
	 *
	 * Use dom4j instead of nanoXML as its much more powerful.  Size
	 * isn't really a concern on the server side.
	 */
	public ServerDataXML () {
		// Create formattor to be pretty printed
		format = OutputFormat.createPrettyPrint();

		// Load user / game documents
		loadDocuments ();
	}

	/**
	 * Implementation of logon method (no password).
	 *
	 * @see org.jogre.server.data.IServerData#containsUser(java.lang.String)
	 */
	public boolean containsUser (String user) {
		// if a password is required - we cant log on
		ServerProperties serverProperties = ServerProperties.getInstance();
        if (serverProperties.isUserValidationNotRequired()) {
            return true;
        } else if (serverProperties.isUserValidationPassword()) {
			return false;
        } else if (serverProperties.isUserValidationUser()) {
            if (userDoc != null) {
                String xpath = "//user[@username='" + user + "']";
                Node node = userDoc.selectSingleNode (xpath);
                if (node != null)
                    return true;
    		}
            // If we get to here return false
            return false;
        } else {
            (new JogreLogger (this.getClass())).error("containsUser", "User Validation value is bad.");
        }
        return false;
    }

	/**
	 * Implementation of logon method (with password).
	 *
	 * @see org.jogre.server.data.IServerData#containsUser(java.lang.String)
	 */
	public boolean containsUser (String user, String password) {
		ServerProperties serverProperties = ServerProperties.getInstance();
	    if (serverProperties.isUserValidationNotRequired()) {
	        return true;
	    } else if (serverProperties.isUserValidationUser()) {
	        return containsUser (user);
	    } else if (serverProperties.isUserValidationPassword()) {
	        
	        if (userDoc != null) {
	            String xpath =
	                "//user[@username='" + user +
	                "' and @password='" + password + "']";
	            Node node = userDoc.selectSingleNode (xpath);
	            if (node != null)
	                return true;
	        }
	    }   
    	
    	// If we get to here return false
    	return false;
	}

	/**
	 * Add game information to data and update user scores.
	 *
	 * @see org.jogre.server.data.IServerData#addGame(org.jogre.server.data.GameInfo, boolean)
	 */
	public GameOver addGame (GameInfo gameInfo, boolean eloRatings) {
		// Declare GameOver object
		GameOver gameOver = null;

		// Update the game file
		if (gameDoc != null) {

			// Retrieve gameID
			String gameKey = gameInfo.getGameKey();

			// Retrieve game element for this game.
			String xpath = "//game[@id='" + gameKey + "']";
	        Node node = gameDoc.selectSingleNode (xpath);
	        Element gameElm = null;
			if (node != null)
				gameElm = (Element)node;
			else {
				gameElm = DocumentHelper.createElement(XML_ELM_GAME);
				gameElm.addAttribute (XML_ATT_ID, gameKey);
				gameDoc.getRootElement().add (gameElm);
			}

			// Create game info as XML
			Element gameInfoXML = GameInfoXML.flatten (gameInfo);
			gameElm.add (gameInfoXML);
			saveXMLFile (gameFile, gameDoc);	// save to file

			// Retrieve players and results
			String [] players = JogreUtils.convertToStringArray(gameInfo.getPlayers ());
			int [] results = JogreUtils.convertToIntArray(gameInfo.getResults ());
			int numPlayers = players.length;

			// Loop through players at table and update them
			GameSummary [] gameSummary = new GameSummary [numPlayers];
			Element     [] gameSummaryXML = new Element [numPlayers];
			for (int i = 0; i < numPlayers; i++) {
				String curPlayer = players [i];

				// Retrieve game summary from XML (create new if not exists).
				gameSummaryXML [i] = getGameSummaryXML (gameKey, curPlayer);

				// inflate to a game summary object
				gameSummary [i] = GameSummaryXML.inflate
					(gameKey, curPlayer, gameSummaryXML [i]);
			}

            // Check if scores need updating
			// NOTE: (currently only support 2 player games)
			if (eloRatings && numPlayers == EloRatingSystem.SUPPORTED_PLAYERS) {
				// Retrieve old ratings
				int [] oldRatings = {gameSummary [0].getRating(),
						             gameSummary [1].getRating()};

				// Find new ratings
				EloRatingSystem elo = EloRatingSystem.getInstance (gameKey);
				int [] newRatings = {
					elo.getNewRating (oldRatings[0], oldRatings[1], results[0]),
					elo.getNewRating (oldRatings[1], oldRatings[0], results[1])
				};

				// Update game summaries,
				for (int i = 0; i < numPlayers; i++)
					gameSummary[i].update (results[i], newRatings[i]);

				// Create GameOver object with old/new ratings and return to users
				gameOver = new GameOver (players, results, oldRatings, newRatings);
			} else {
				// No elo ratings - so everyone keeps old ratings
				int [] oldRatings = new int [numPlayers];

				for (int i = 0; i < numPlayers; i++)
					oldRatings[i] = gameSummary [i].getRating();

				gameOver = new GameOver (players, results, oldRatings, oldRatings);
			}

			// Update XML attributes
			for (int i = 0; i < numPlayers; i++) {
				gameSummaryXML [i].setAttributes (
					GameSummaryXML.flatten (gameSummary[i]).attributes());
			}
		}

		// Save document to file.
		saveXMLFile (userFile, userDoc);	// save to file

		// Now return gameOver object.
		return gameOver;
	}

	/**
	 * Return a game summary.
	 *
	 * @see org.jogre.server.data.IServerData#getGameSummary(java.lang.String, java.lang.String)
	 */
	public GameSummary getGameSummary (String gameId, String username) {
		Element gameSummary = getGameSummaryXML (gameId, username);		// retrieve from XML document.
		return GameSummaryXML.inflate (gameId, username, gameSummary);	// Convert to Java object.
	}

	/**
	 * Update the server snapshot.
	 *
	 * @see org.jogre.server.data.IServerData#updateSnapshot(java.lang.String, int, int)
	 */
	public void updateSnapshot (String gameId, int numOfUsers, int numOfTables) {

		// if a password is required - we cant log on
		if (snapshotDoc != null) {
			Element gameSnapShot;
			String xpath = "//game[@id='" + gameId + "']";
	        Node node = snapshotDoc.selectSingleNode (xpath);
			if (node != null) {
				gameSnapShot = (Element)node;
				gameSnapShot.addAttribute (XML_ATT_NUM_OF_USERS,  String.valueOf (numOfUsers));
				gameSnapShot.addAttribute (XML_ATT_NUM_OF_TABLES, String.valueOf (numOfTables));
			}
			else {
				snapshotDoc.getRootElement().add (
					getGameSnapshotElm (gameId, numOfUsers, numOfTables));
			}

			saveXMLFile (snapshotFile, snapshotDoc);
		}
	}

	/**
	 * Reset the server snapshot.
	 *
	 * @see org.jogre.server.data.IServerData#resetSnapshot(java.util.Vector)
	 */
	public void resetSnapshot (Vector gameKeys) {
		// Create new document
		this.snapshotDoc = DocumentHelper.createDocument();
		Element snapshotElm = DocumentHelper.createElement(XML_ELM_SNAPSHOTS);
		snapshotDoc.add (snapshotElm);

		for (int i = 0; i < gameKeys.size(); i++) {
			String gameId = (String)gameKeys.get(i);
			snapshotElm.add (getGameSnapshotElm (gameId, 0, 0));
		}

		saveXMLFile (snapshotFile, snapshotDoc);
	}

	/**
	 * Return type XML.
	 *
	 * @see org.jogre.server.data.IServerData#getType()
	 */
	public String getType() {
		return XML;
	}
	
	/**
	 * Return all users.
	 * 
	 * @see org.jogre.server.data.IServerData#getUsers()
	 */
	public List getUsers() {
		List users = new ArrayList();
		List userElms = userDoc.getRootElement().elements();
		for (int i = 0; i < userElms.size(); i++) {
			Element userElm = (Element)userElms.get(i);
			User user = new User ();
			user.setUsername (userElm.attributeValue("username"));
			user.setPassword (userElm.attributeValue("password"));
			users.add(user);
		}
		
		return users;
	}
	
	/**
	 * Get game infos.
	 * 
	 * @see org.jogre.server.data.IServerData#getGameInfos()
	 */
	public List getGameInfos() {
		List games = new ArrayList ();
		
		Iterator it1 = gameDoc.getRootElement().elements().iterator();
		while (it1.hasNext()) {
			Element gameElm = (Element)it1.next();
			String gameKey = gameElm.attributeValue("id");
			
			Iterator it2 = gameElm.elements().iterator();
			while (it2.hasNext()) {
				Element gameInfoElm = (Element)it2.next();			
				games.add (GameInfoXML.inflate(gameKey, gameInfoElm));
			}			
		}
		
		return games;
	}

	/**
	 * Get game summarys.
	 * 
	 * @see org.jogre.server.data.IServerData#getGameSummarys()
	 */
	public List getGameSummarys() {
		List gameSummaries = new ArrayList();
		
		Iterator it1 = userDoc.getRootElement().elements().iterator();
		while (it1.hasNext()) {
			Element userElm = (Element)it1.next();
			String username = userElm.attributeValue("username");
			
			Iterator it2 = userElm.elements().iterator();
			while (it2.hasNext()) {
				Element gameSummaryElm = (Element)it2.next();
				String gameId = gameSummaryElm.attributeValue("game");
				gameSummaries.add (GameSummaryXML.inflate(gameId, username, gameSummaryElm));
			}			
		}
		
		return gameSummaries;
	}
	
	/**
	 * Add a new user.
	 * 
	 * @see org.jogre.server.data.IServerData#newUser(org.jogre.server.data.User)
	 */
	public void newUser (User user) throws ServerDataException {
		Element userElm = DocumentHelper.createElement(XML_ELM_USER);

		// Check user doesn't exist 
		if (userDoc.selectSingleNode ("users/user[@username='" + user.getUsername() + "']") == null) {		
			userElm.addAttribute("username", user.getUsername());
			userElm.addAttribute("password", user.getPassword());
			userDoc.getRootElement().add (userElm);

			saveXMLFile (userFile, userDoc);
			/* MANDAR NOTIFICACION AL SERVIDOR */
		}
		else 
			throw new ServerDataException ("User already exists: " + user.getUsername());
	}
	
	/**
	 * Delete user.
	 * 
	 * @see org.jogre.server.data.IServerData#deleteUser(org.jogre.server.data.User)
	 */
	public void deleteUser(User user) throws ServerDataException {
		Object userObj = userDoc.selectSingleNode ("users/user[@username='" + user.getUsername() + "']");
		if (userObj != null)
			userDoc.getRootElement().remove((Element)userObj);
		saveXMLFile (userFile, userDoc);
	}

	/**
	 * Update user.
	 * 
	 * @see org.jogre.server.data.IServerData#updateUser(org.jogre.server.data.User)
	 */
	public void updateUser(User user) throws ServerDataException {
		Object userObj = userDoc.selectSingleNode ("users/user[@username='" + user.getUsername() + "']");
		if (userObj != null) {
			Element userElm = (Element)userObj;
			userElm.addAttribute("username", user.getUsername());
			userElm.addAttribute("password", user.getPassword());
			
			saveXMLFile (userFile, userDoc);
		}
	}
	
	/**
	 * Return XML data location relative to JOGRE instance.
	 * 
	 * @return
	 */
	public String getXMLLocation () {
		String xmlLocation = ServerProperties.getInstance().getXMLLocation ();
		if (!xmlLocation.endsWith("/"))
			xmlLocation = xmlLocation  + "/";
		
		return xmlLocation;
	}

	/**
	 * Load the XML documents up.  If they dont exist this method
	 * will create them.
	 */
	private void loadDocuments () {
		try {
			// Load SAX Reader
			SAXReader reader = new SAXReader ();

			// Load user file - if it doesn't exist create new one
			this.userFile = new File (getXMLLocation() + USER_FILENAME);
			if (userFile.exists())
				this.userDoc = reader.read(new FileInputStream (userFile));
			else {
				this.userDoc = DocumentHelper.createDocument();
		        Element users = userDoc.addElement (XML_ELM_USERS);
		        Element user  = users.addElement (XML_ELM_USER);
		        user.addAttribute (XML_ATT_USERNAME, XML_ELM_USER);
		        user.addAttribute (XML_ATT_PASSWORD, XML_ELM_USER);
		        saveXMLFile (userFile, userDoc);
			}
 
			// Load game file - if it doesn't exist create new one
			this.gameFile = new File (getXMLLocation() + GAMES_FILENAME);
			if (gameFile.exists())
				this.gameDoc = reader.read(new FileInputStream (gameFile));
			else {
				// Create new file
				this.gameDoc = DocumentHelper.createDocument();
		        gameDoc.addElement (XML_ELM_GAMES);
		        saveXMLFile (gameFile, gameDoc);
			}

			// Load snapshot file - if it doesn't exist then create new one
			snapshotFile = new File (getXMLLocation() + SNAPSHOT_FILENAME);
			if (snapshotFile.exists())
				this.snapshotDoc = reader.read(new FileInputStream (snapshotFile));
			else {
				// Create new file
				this.snapshotDoc = DocumentHelper.createDocument();
				snapshotDoc.addElement (XML_ELM_SNAPSHOTS);
		        saveXMLFile (snapshotFile, snapshotDoc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return a game
	 *
	 * @param gameId
	 * @param numOfUsers
	 * @param numOfTables
	 * @return
	 */
	private Element getGameSnapshotElm (String gameId, int numOfUsers, int numOfTables) {
		Element gameSnapShot = DocumentHelper.createElement(XML_ELM_GAME);

		gameSnapShot.addAttribute (XML_ATT_ID,            gameId);
		gameSnapShot.addAttribute (XML_ATT_NUM_OF_USERS,  String.valueOf (numOfUsers));
		gameSnapShot.addAttribute (XML_ATT_NUM_OF_TABLES, String.valueOf (numOfTables));

		return gameSnapShot;
	}

	/**
	 * Return a game summary XML from a game / username.  If it doesn't
	 * exist then create it.
	 *
	 * @param gameId    Game id.
	 * @param username  Username
	 * @return          Game summary XML.
	 */
	private Element getGameSummaryXML (String gameId, String username) {
		// Retrieve game element for this game.
		String xpath = "//user[@username='" + username + "']" +
		               "/game_summary[@game='" + gameId + "']";
        Node node = userDoc.selectSingleNode (xpath);
        Element gameSummaryElm = null;

        // If it exists then cast to an Element otherwise create new.
		if (node != null)
			gameSummaryElm = (Element)node;
		else {
			// Create new game summary in XML
			int startRating = ServerProperties.getInstance().getStartRating (gameId);
			gameSummaryElm = GameSummaryXML.flatten (
				new GameSummary (gameId, username, startRating));

			// and add to user object
			xpath = "//user[@username='" + username + "']";
			node = userDoc.selectSingleNode (xpath);
			if (node != null)
			   ((Element)node).add (gameSummaryElm);
			else {
   			}
		}

		return gameSummaryElm;
	}

	/**
	 * Method for saving an XML file.
	 *
	 * @param file      File to save to.
	 * @param document
	 */
	private void saveXMLFile (File file, Document document) {
		// write to a file
		try {
	        XMLWriter writer = new XMLWriter (
	            new FileWriter (file), format);
	        writer.write (document );
	        writer.close ();
		}
		catch (IOException ioe) {}
	}
}
