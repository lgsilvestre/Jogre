/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
 * MERCHANTABILIRTY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import nanoxml.XMLElement;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jogre.common.IJogre;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;
import org.jogre.server.data.IServerData;
import org.jogre.server.data.db.IDatabase;

/**
 * This class provides an easy and effective point for accessing the server properties.
 *
 * Since Beta 0.3 it can also be transferred to a server administrator client.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ServerProperties implements IJogre, ITransmittable {

    // Declare constants
	private static final String DEFAULT_FILENAME = "server.xml";

	private static final String XML_CUSTOM           = "custom";
    private static final String XML_SUPPORTED_GAMES  = "supported_games";
	private static final String XML_GAME             = "game";
	private static final String XML_ELO              = "elo";

	private static final String XML_ATT_TYPE         = "type";
    private static final String XML_ATT_VALUE        = "value";
	private static final String XML_ATT_HOST         = "host";
    private static final String XML_ATT_MIN_PLAYERS  = "minPlayers";
    private static final String XML_ATT_MAX_PLAYERS  = "maxPlayers";
    private static final String XML_ATT_START_RATING = "startRating";
    private static final String XML_ATT_KFACTOR      = "kFactor";

    // Persistent data
    private static final String XML_SERVER_DATA   = "server_data";
	private static final String XML_DATABASE      = "database";
	private static final String XML_CONNECTION    = "connection";
	private static final String XML_ATT_ID        = "id";
    private static final String XML_ATT_DRIVER    = "driver";
    private static final String XML_ATT_URL       = "url";
    private static final String XML_ATT_USERNAME  = "username";
    private static final String XML_ATT_PASSWORD  = "password";

    // User validation
    public static final String XML_ATT_VAL_USER_VALIATION_NONE = "guest";
    public static final String XML_ATT_VAL_USER_VALIATION_USER = "user";
    public static final String XML_ATT_VAL_USER_VALIATION_PASS = "password";
    public static final String [] VALIDATION_ARRAY = {
    	XML_ATT_VAL_USER_VALIATION_NONE, XML_ATT_VAL_USER_VALIATION_USER, XML_ATT_VAL_USER_VALIATION_PASS};

    // Data selection
    public static final String XML_ATT_VAL_XML = "xml";
    public static final String XML_ATT_VAL_DATABASE = "database";

    private static final File SERVER_FILE = new File (DEFAULT_FILENAME);

	private static ServerProperties instance = null;

	// Document
	private Document doc;
	
	private String jogreHomeDir;
	
	private File serverFile;

	/**
	 * Private constructor to restrict access to this class.
	 */
	private ServerProperties () {
	    // Create file definition for reader.
        try {
            String path = SERVER_FILE.toURL().getPath();
            String pathWithoutServerFile = path.substring(0, path.lastIndexOf(DEFAULT_FILENAME));
            jogreHomeDir = path.substring(0, pathWithoutServerFile.lastIndexOf("server"));
        } catch (Exception e ) {
            jogreHomeDir = null;        // JOGRE home directory not created
        }

		try {
			// Load up the XML document
			SAXReader reader = new SAXReader ();
			this.serverFile = new File (DEFAULT_FILENAME);
			if (serverFile.exists())
				this.doc = reader.read(new FileInputStream (serverFile));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create server properties from a String.
	 * 
	 * @param string
	 */
	private ServerProperties (String string) {
		try {
			SAXReader reader = new SAXReader ();
			this.doc = reader.read (new StringReader (string));			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Accessor to singleton instance of this class.
	 *
	 * @return
	 */
	public static ServerProperties getClonedInstance () {
		if (instance == null)
			throw new NullPointerException ("Server properties not initialised");
		else {
			// Deep copy the server element by parsing from String.
			return new ServerProperties (instance.toString());
		}
	}

	/**
	 * Accessor to getting cloned instance.
	 *
	 * @return
	 */
	public static ServerProperties getInstance () {
		if (instance == null)
			throw new NullPointerException ("Server properties not initialised");

		return instance;
	}

	/**
	 * Accessor to server properties which takes an XMLElement.  Used in the
	 * server administrator client and JogreServer.
	 *
	 * @param elm   XMLElement containing server tree.
	 */
	public static void setUpFromString (String string) {
		instance = new ServerProperties (string);		// read from element
	}

	/**
	 * Setup server properties from the server file (used in JogreServer).
	 */
	public static void setUpFromFile () {
		instance = new ServerProperties ();
	}

	/**
	 * Save the server properties to a file.
	 */
	public void saveXMLFile () {
		try {
			OutputFormat format = new OutputFormat ("    ", true);
			XMLWriter writer = new XMLWriter (new FileWriter (DEFAULT_FILENAME), format);
			writer.write (doc);
			writer.close ();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();	 // FIXME - proper logging
		}
	}
	
	/**
	 * Return document.
	 * 
	 * @return
	 */
	public Document getDoc () {
		return this.doc;
	}
	
	/**
	 * Return the server port.
	 *
	 * @return
	 */
	public int getServerPort () {
		return getIntAttribute ("server_properties/configuration/server/@port", DEFAULT_PORT);
	}

	/**
	 * Set the server port.
	 *
	 * @param portNum        Port number to set.
	 */
	public void setServerPort(int portNum) {
		setAttribute ("server_properties/configuration/server/@port", portNum);
	}

	/**
	 * Return the maximum number of users.
	 *
	 * @return
	 */
	public int getMaxNumOfUsers () {
		return getIntAttribute ("server_properties/configuration/server/@max_users", DEFAULT_MAX_NUM_OF_USERS);
	}

	/**
	 * Set the maximum number of users.
	 *
	 * @param maxUsers
	 */
	public void setMaxNumOfUsers(int maxUsers) {
		setAttribute ("server_properties/configuration/server/@max_users", maxUsers);
	}

	/**
	 * Return the maximum number of tables.
	 *
	 * @return maximum number of tables.
	 */
	public int getMaxNumOfTables () {
		return getIntAttribute ("server_properties/configuration/server/@max_tables", DEFAULT_MAX_NUM_OF_TABLES);
	}

	/**
	 * Set the maximum number of tables.
	 *
	 * @param maxTables
	 */
	public void setMaxNumOfTables (int maxTables) {
		setAttribute ("server_properties/configuration/server/@max_tables", maxTables);
	}
	
	/**
	 * Return the maximum number of tables per user.
	 * 
	 * @return
	 */
	public int getMaxNumOfTablesPerUser() {
		return getIntAttribute ("server_properties/configuration/server/@max_tables_per_user", DEFAULT_MAX_NUM_OF_TABLES_PER_USER);
	}
	
	/**
	 * Set the number of tables per user.
	 * 
	 * @param maxNumOfTablesPerUser
	 */
	public void setMaxNumOfTablesPerUser (int maxNumOfTablesPerUser) {
		setAttribute ("server_properties/configuration/server/@max_tables_per_user", maxNumOfTablesPerUser);
	}
	
	/**
	 * Return the user validation type (guest, user or password).
	 *
	 * @return
	 */
	public String getUserValidation () {
		return getStringAttribute ("server_properties/configuration/server/@user_validation");
	}

	/**
	 * Set the user validation.
	 *
	 * @param userValidation
	 */
	public void setUserValidation (String userValidation) {
		setAttribute ("server_properties/configuration/server/@user_validation", userValidation);
	}

	/**
	 * Return administrator username.
	 *
	 * @return
	 */
	public String getAdminUsername () {
		return getStringAttribute ("server_properties/configuration/admin_client/@username");
	}
	
	/**
	 * Set the administrator username.
	 *
	 * @param adminUsername
	 */
	public void setAdminUsername(String adminUsername) {
		setAttribute ("server_properties/configuration/admin_client/@username", adminUsername);
	}

	/**
	 * Return the administrator password
	 *
	 * @return
	 */
	public String getAdminPassword () {
		return getStringAttribute ("server_properties/configuration/admin_client/@password");
	}

	/**
	 * Set the administrator password.
	 *
	 * @param adminPassword
	 */
	public void setAdminPassword (String adminPassword) {
		setAttribute ("server_properties/configuration/admin_client/@password", adminPassword);
	}

	/**
	 * Return true/false if the administrator username / password is correct.
	 *
	 * @param username   User specified username
	 * @param password   User specified password
	 * @return           True if this is indeed the administrator.
	 */
	public boolean isAdministrator (String username, String password) {
		if (username != null && password != null) {
	    	if (getAdminUsername().equalsIgnoreCase(username) &&
	    	    getAdminPassword().equals(password))

	    		return true;			// this is indeed the administrator
	    }

	    return false;
	}
	
	/**
	 * Return true/false if the administrator will receive messages.
	 * 
	 * @return
	 */
	public boolean isReceiveMessages() {
		return getBooleanAttribute ("server_properties/configuration/admin_client/@receive_messages", ADMIN_RECEIVE_MESSAGES);
	}
	
	/**
	 * Set true/false if the administrator will receive messages.
	 * 
	 * @param receive_messages
	 */
	public void setReceiveMessages(boolean receive_messages) {
		setAttribute ("server_properties/configuration/admin_client/@receive_messages", receive_messages);
	}
	
	/**
	 * Return the games that this server supports.
	 *
	 * @return
	 */
	public String [] getSupportedGames () {
	    // Check games element isn't null
		List supportedGamesElms = doc.selectNodes("/server_properties/supported_games");
	    if (supportedGamesElms != null) {
	        String [] games = new String [supportedGamesElms.size()];
	        for (int i = 0; i < supportedGamesElms.size(); i++) {
	        	Element elm = (Element)supportedGamesElms.get(i);
	        	games [i] = elm.attributeValue(XML_ATT_ID);
	    	}

	    	return games;
	    }

	    return null;
	}

    /**
     * Return the currently hosted games.
     *
     * @return
     */
    public Vector getCurrentlyHostedGames () {
    	List supportedGamesElms = doc.selectNodes("/server_properties/supported_games/game[@host='true']");
	    if (supportedGamesElms != null) {
	        Vector games = new Vector (supportedGamesElms.size());
	        for (int i = 0; i < supportedGamesElms.size(); i++) {
	        	Element elm = (Element)supportedGamesElms.get(i);
	        	games.add (elm.attributeValue(XML_ATT_ID));
	    	}

	    	return games;
	    }

	    return null;
    }

    /**
     * Return game element where ID = gameID.
     *
     * @param gameID
     * @return
     */
    public Element getGameElm (String gameID) {
    	return getElement ("/server_properties/supported_games/game[@id='" + gameID + "']");
    }

	/**
     * Return the ELO element for this specified game.
     *
     * @param gameId
     * @return
     */
    public Element getELOElm (String gameId) {
    	return getElement("/server_properties/supported_games/game[@id='" + gameId + "']/elo");
    }

    /**
     * Add ELO element.
     *
     * @param gameId
     */
    public void addELOElm (String gameId) {
    	Element gameElm = getGameElm (gameId);

    	if (gameElm != null) {
    		Element eloElm = DocumentHelper.createElement (XML_ELO);
    		eloElm.addAttribute(XML_ATT_START_RATING, "1200");
    		eloElm.addAttribute(XML_ATT_KFACTOR,      "0-3000=24");

    		// Add the eloElm
    		gameElm.add (eloElm);
    	}
    }

    /**
     * Delete the ELO element from the game.
     *
     * @param gameId
     */
    public void deleteELOElm (String gameId) {
    	Element gameElm = getGameElm (gameId);
    	Element gameEloElm = getELOElm(gameId);

    	if (gameElm != null && gameEloElm != null) 
    		gameElm.remove(gameEloElm);
    }

	/**
     * Return the minimum number of players of a game.
     *
	 * @param gameID
	 * @return
	 */
	public int getMinPlayers (String gameID) {
		return getIntAttribute("/server_properties/supported_games/game[@id='" + gameID + "']/@minPlayers", DEFAULT_MIN_NUM_OF_PLAYERS);
	}

	/**
	 * Set the min players of a game.
	 *
	 * @param gameId
	 * @param minPlayers
	 */
	public void setMinPlayers (String gameId, int minPlayers) {
		setAttribute("/server_properties/supported_games/game[@id='" + gameId + "']/@minPlayers", minPlayers);
	}

	/**
     * Return the maximum number of players of a game.
     *
	 * @param gameID
	 * @return
	 */
	public int getMaxPlayers (String gameID) {	    
		return getIntAttribute("/server_properties/supported_games/game[@id='" + gameID + "']/@maxPlayers", DEFAULT_MAX_NUM_OF_PLAYERS);
	}

	/**
	 * Set the max players of a game.
	 *
	 * @param gameId
	 * @param maxPlayers
	 */
	public void setMaxPlayers (String gameId, int maxPlayers) {
		setAttribute("/server_properties/supported_games/game[@id='" + gameId + "']/@maxPlayers", maxPlayers);
	}

	/**
	 * Return true/false if specified game is hosted or not.
	 *
	 * @param   gameID
	 * @return
	 */
	public boolean isGameHosted (String gameID) {
		return getBooleanAttribute("/server_properties/supported_games/game[@id='" + gameID + "']/@host", true);
	}

	/**
	 * Set if a game is hosted or not.
	 *
	 * @param gameId
	 * @param value
	 */
	public void setGameHosted (String gameId, boolean value) {
		setAttribute("/server_properties/supported_games/game[@id='" + gameId + "']/@host", value);
	}	

	/**
	 * Return a custom server property string from the server.xml file for a given game.
	 *
	 * @param gameID          The game whose property is sought.
	 * @param propertyName    The name of the property that is sought.
	 * @param defaultValue    The default value to return if the requested property
	 *                        can't be found.
	 * @returns The String for the property.
	*/
	public String getStringCustomProp (String gameID, String propertyName, String defaultValue) {
		try {
			Object obj = doc.selectSingleNode (
				"/server_properties/supported_games/game[@id='" + gameID + "']/custom[@type='" + propertyName + "']");			
			if (obj != null)
				return ((Element)obj).attributeValue(XML_ATT_VALUE);
		} catch (NullPointerException e) {}

		return defaultValue;
	}

	/**
	 * Create a Vector of custom elements.
	 *
	 * @param gameID
	 * @return
	 */
	public List getCustomElms (String gameID) {
		return doc.selectNodes("/server_properties/supported_games/game[@id='" + gameID + "']/custom");
	}

	/**
	 * Add a new custom row.
	 *
	 * @param curGame
	 */
	public void addNewCustomElm (String gameID) {
		// Create new custom element with blank type and values
		Element customElm = DocumentHelper.createElement (XML_CUSTOM);
		customElm.addAttribute (XML_ATT_TYPE,  "");
		customElm.addAttribute (XML_ATT_VALUE, "");

		// Add as child to this particular game
		Element gameElm = getGameElm (gameID);
		if (gameElm != null)
			gameElm.add (customElm);
	}

	/**
	 * Delet an existing custom elm using a gameID and a custom elm type.
	 *
	 * @param gameID
	 * @param type
	 */
	public void deleteCustomElm (String gameID, String type) {
		Element gameElm = getGameElm (gameID);
		Object customElm = doc.selectSingleNode (
				"/server_properties/supported_games/game[@id='" + gameID + "']/custom[@type='" + type + "']");
		
		if (gameElm != null && customElm != null)
			gameElm.remove((Element)customElm);
	}
	
	/**
	 * Return the start rating for a specified game.
	 *
	 * @param gameID   Game Id e.g. chess.
	 * @return         Starting rating.
	 */
	public int getStartRating (String gameID) {
		return getIntAttribute ("/server_properties/supported_games/game[@id='" + gameID + "']/elo/@startRating", DEFAULT_ELO_START_RATING);
	}

	/**
	 * Set the start rating.
	 *
	 * @param gameId
	 * @param startRating
	 */
	public void setStartRating (String gameId, int startRating) {
		setAttribute("/server_properties/supported_games/game[@id='" + gameId + "']/elo/@startRating", startRating);
	}

	/**
	 * Return the k factor range string for a specified game.
	 *
	 * @param gameID   Game Id e.g. chess.
	 * @return         KFactor String e.g.
	 *                 "0-2099=32,2100-2399=24,2490-3000=16"
	 */
	public String getKFactor (String gameID) {
		return getStringAttribute ("/server_properties/supported_games/game[@id='" + gameID + "']/elo/@kFactor");
	}

	/**
	 * Set the kFactor for a particular game.
	 *
	 * @param gameId
	 * @param kFactor
	 */
	public void setKFactor (String gameId, String kFactor) {
		setAttribute("/server_properties/supported_games/game[@id='" + gameId + "']/elo/@kFactor", kFactor);
	}

	/**
	 * Return the user connection.
	 *
	 * @return
	 */
	public String getCurrentServerData () {
		return getStringAttribute ("server_properties/server_data/@current");
	}

	/**
	 * Set the current server data.
	 *
	 * @param value
	 */
	public void setCurrentServerData (String value) {
		setAttribute ("server_properties/server_data/@current", value);		
	}

    /**
     * Return the current database connection.
     *
     * @return
     */
    public String getCurrentDatabaseConnection () {
    	return getStringAttribute ("server_properties/server_data/database/@current");
    }

    /**
     * Set the currenct database connection;
     *
     * @param value
     * @return
     */
    public void setCurrentDatabaseConnection (String value) {
    	setAttribute ("server_properties/server_data/database/@current", value);
    }

    /**
     * Return database element.
     *
     * @return
     */
    public Element getDatabaseElement () {
    	return getElement ("server_properties/server_data/database");
    }

    /**
     * Return the connection element.
     *
     * @param connectionID
     * @return
     */
    public Element getConnectionElm (String connectionID) {
    	return getElement ("server_properties/server_data/database/connection[@id='" + connectionID + "']");
    }

	/**
	 * Return the connection driver.
	 *
	 * @param connectionID  Specified connection ID.
	 * @return
	 */
	public String getConnectionDriver (String connectionID) {
		return getStringAttribute("server_properties/server_data/database/connection[@id='" + connectionID + "']/@driver");
	}
	
	/**
	 * Set the connection driver.
	 *
	 * @param connectionID
	 */
	public void setConnectionDriver (String connectionID, String driver) {
		setAttribute ("server_properties/server_data/database/connection[@id='" + connectionID + "']/@driver", driver);
	}

	/**
	 * Return the connection URL.
	 *
	 * @param connectionID  Specified connection ID.
	 * @return
	 */
	public String getConnectionURL(String connectionID) {
		return getStringAttribute("server_properties/server_data/database/connection[@id='" + connectionID + "']/@url");
	}

	/**
	 * Set the connection URL.
	 *
	 * @param connectionID
	 */
	public void setConnectionURL (String connectionID, String url) {
    	setAttribute ("server_properties/server_data/database/connection[@id='" + connectionID + "']/@url", url);
	}

	/**
	 * Return the connection username.
	 *
	 * @param connectionID  Specified connection ID.
	 * @return
	 */
	public String getConnectionUsername(String connectionID) {
		return getStringAttribute("server_properties/server_data/database/connection[@id='" + connectionID + "']/@username");
	}

	/**
	 * Set the connection username.
	 *
	 * @param connectionID
	 */
	public void setConnectionUsername (String connectionID, String username) {
		setAttribute ("server_properties/server_data/database/connection[@id='" + connectionID + "']/@username", username);
	}
	
	/**
	 * Return the connection password.
	 *
	 * @param connectionID  Specified connection ID.
	 * @return
	 */
	public String getConnectionPassword (String connectionID) {
		return getStringAttribute("server_properties/server_data/database/connection[@id='" + connectionID + "']/@password");
	}

	/**
	 * Set the connection password.
	 * 
	 * @param connectionID
	 */
	public void setConnectionPassword (String connectionID, String password) {
		setAttribute ("server_properties/server_data/database/connection[@id='" + connectionID + "']/@password", password);
	}	

	/**
     * Return the storage location of xml data.
     *
     * @return
     */
    public String getXMLLocation () {
    	return getStringAttribute ("server_properties/server_data/xml/@location");
    }
	
	/**
	 * Set the XML location.
	 *
	 * @param xmlLocation
	 */
	public void setXMLLocation (String xmlLocation) {
		setAttribute ("server_properties/server_data/xml/@location", xmlLocation);
	}

	/**
	 * Return the maximum number of tables.
	 *
	 * @return
	 */
	public boolean isUserValidationEqualTo (String string) {
	    if (getUserValidation () != null)
	    	return getUserValidation ().equals(string);

	    return false;
	}

    /**
     * Return true if no validation is required.
     *
     * @return
     */
    public boolean isUserValidationNotRequired () {
        return isUserValidationEqualTo (XML_ATT_VAL_USER_VALIATION_NONE);
    }

    /**
     * Return true if a valid username is required.
     *
     * @return
     */
    public boolean isUserValidationUser () {
        return isUserValidationEqualTo (XML_ATT_VAL_USER_VALIATION_USER);
    }

    /**
     * Return true if a valid username and a password is required.
     *
     * @return
     */
    public boolean isUserValidationPassword () {
        return isUserValidationEqualTo (XML_ATT_VAL_USER_VALIATION_PASS);
    }

    /**
     * Return the JOGRE home directory.
     *
     * @return
     */
    public String getJogreHomeDir () {
        return this.jogreHomeDir;
    }
    
    /**
     * Return the server file.
     * 
     * @return
     */
    public File getServerFile () {
    	return this.serverFile;
    }

    /**
     * Return the current database connection element.
     * 
     * @return
     */
    public Element getCurrentDatabaseConnElm () {
    	String connectionID = getCurrentDatabaseConnection();
    	return getElement ("server_properties/server_data/database/connection[@id='" + connectionID + "']");
    }
    
    /**
     * Return the user specified database connection e.g. Access, MySQL.
     * Dont worry about the syntax - this isn't used to create
     * an actual database connection but is displayed as text
     * in the Jogre server at start up to inform the user which
     * database is used to store server persistent data.
     *
     * @return      Database type.
     */
    public String getDatabaseType () {
        if (getCurrentDatabaseConnElm () != null)
            return getCurrentDatabaseConnElm ().attributeValue (XML_ATT_ID);
        return null;
    }
    
    /**
     * Return a list of connection ID's.
     * 
     * @return
     */
    public Vector getConnectionIDs () {
    	List list = doc.selectNodes("server_properties/server_data/database/connection/@id");
    	Vector v = new Vector ();
    	for (int i = 0; i < list.size(); i++)  {
    		Attribute att = (Attribute)list.get(i);
    		v.add(att.getValue());
    	}
    	return v;
    }

    /**
     * Return the user specified driver e.g. sun.jdbc.odbc.JdbcOdbcDriver.
     *
     * @return   Driver to database.
     */
    public String getDBDriver () {
        if (getCurrentDatabaseConnElm () != null)
            return getCurrentDatabaseConnElm ().attributeValue (XML_ATT_DRIVER);
        return null;
    }

    /**
     * Return the connection URL e.g. jdbc:odbc:.
     *
     * @return
     */
    public String getDBConnURL () {
        if (getCurrentDatabaseConnElm () != null)
            return getCurrentDatabaseConnElm ().attributeValue (XML_ATT_URL);
        return null;
    }

    /**
     * Return the database username (if required).
     *
     * @return    Optional username.
     */
    public String getDBUsername () {
        if (getCurrentDatabaseConnElm () != null)
            return getCurrentDatabaseConnElm ().attributeValue (XML_ATT_USERNAME);
        return null;
    }

    /**
     * Return the database password (if required).
     *
     * @return     Optional password
     */
    public String getDBPassword () {
        if (getCurrentDatabaseConnElm () != null)
            return getCurrentDatabaseConnElm ().attributeValue (XML_ATT_PASSWORD);
        return null;
    }

    /**
     * Return games element.
     *
     * @return
     */
    public Element getGamesElm() {
		return doc.getRootElement().element(XML_SUPPORTED_GAMES);
	}

	/**
	 * Add a new database element.
	 *
	 * @param   connectionID   New user specified database connection ID.
	 */
	public void addDatabaseConnElm (String connectionID) {
		Element newDatabaseConnElement = DocumentHelper.createElement (XML_CONNECTION);
		newDatabaseConnElement.addAttribute (XML_ATT_ID,     connectionID);
		newDatabaseConnElement.addAttribute (XML_ATT_DRIVER,   IDatabase.DEFAULT_DRIVER);
		newDatabaseConnElement.addAttribute (XML_ATT_URL,      IDatabase.DEFAULT_URL);
		newDatabaseConnElement.addAttribute (XML_ATT_USERNAME, "");
		newDatabaseConnElement.addAttribute (XML_ATT_PASSWORD, "");

		doc.getRootElement().element(XML_SERVER_DATA).element(XML_DATABASE).add (newDatabaseConnElement);
	}

	/**
	 * Remove database element.
	 *
	 * @param databaseID   Database element to be removed.
	 */
	public void deleteDatabaseConnElm (String databaseID) {
		Object dbElm = doc.selectSingleNode (
    		"server_properties/server_data/database/connection[@id='" + databaseID + "']");
		if (dbElm != null)
			((Element)dbElm).getParent().remove((Element)dbElm);
	}

	/**
	 * Add games element.
	 *
	 * @param gameID  New user specified gameID.
	 */
	public void addGamesElm (String gameID) {
		// Create default games Elm
		Element newGamesElm = DocumentHelper.createElement (XML_GAME);
		newGamesElm.addAttribute (XML_ATT_ID,          gameID);
		newGamesElm.addAttribute (XML_ATT_HOST,        "yes");
		newGamesElm.addAttribute (XML_ATT_MIN_PLAYERS, String.valueOf (DEFAULT_MIN_NUM_OF_PLAYERS));
		newGamesElm.addAttribute (XML_ATT_MAX_PLAYERS, String.valueOf (DEFAULT_MAX_NUM_OF_PLAYERS));

		doc.getRootElement().element(XML_SUPPORTED_GAMES).add (newGamesElm);
	}

	/**
	 * Remove games elm.
	 *
	 * @param gameID   Games element to be removed.
	 */
	public void deleteGamesElm (String gameID) {
		Element gameElm = getGameElm(gameID);
		gameElm.getParent().remove(gameElm);
	}

	/**
	 * Flatten element for transmission.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		
		// Create new element for transportation and set clone as text.
		XMLElement element = new XMLElement (Comm.ADMIN_SERVER_PROPERTIES);
		String serverConfig = toString();
		element.setContent(serverConfig);
		
		return element;
	}

	//========================================================================
	// Private methods
	//========================================================================
	
	/**
	 * Little helper method for setting an attribute.
	 * 
	 * @param xpath     Xpath to attribute
	 * @param attValue  Doesn't set anything if it doesn't exist
	 */
	private void setAttribute (String xpath, String attValue) {
		Object obj = doc.selectSingleNode(xpath);
		if (obj != null && obj instanceof Attribute)
			((Attribute)obj).setValue(attValue);
	}
	
	/**
	 * Little helper method for setting an attribute.
	 * 
	 * @param xpath     Xpath to attribute
	 * @param attValue  Attribute value
	 */
	private void setAttribute (String xpath, boolean attValue) {
		setAttribute(xpath, attValue ? "true" : "false");
	}
	
	/**
	 * Little helper method for setting an attribute.
	 * 
	 * @param xpath     Xpath to attribute
	 * @param attValue  Attribute value
	 */
	private void setAttribute (String xpath, int attValue) {
		setAttribute (xpath, String.valueOf(attValue));
	}
	
	/**
	 * Little helper method for returning an attribute as a String.
	 * 
	 * @param xpath  Xpath to attribute.
	 * @return       Value or xpath or null if it cant find it.
	 */
	private String getStringAttribute (String xpath) {
		Object obj = doc.selectSingleNode (xpath);
		if (obj != null)
			return ((Attribute)obj).getValue();
		return null;
	}
	
	/**
	 * Little helper method for returning a boolean attribute.
	 * 
	 * @param xpath  Xpath to attribute.
	 * @return       Value or xpath or null if it cant find it.
	 */
	private boolean getBooleanAttribute (String xpath, boolean defaultAtt) {
		Object obj = doc.selectSingleNode (xpath);
		if (obj != null)
			return "true".equals(((Attribute)obj).getValue());
		return defaultAtt;
	}
	
	/**
	 * Return integer attribute.
	 * 
	 * @param xpath
	 * @param defaultAtt
	 * @return
	 */
	private int getIntAttribute (String xpath, int defaultAtt) {
		Object obj = doc.selectSingleNode (xpath);
		if (obj != null)
			return Integer.parseInt (((Attribute)obj).getValue());
		return defaultAtt;
	}
	
	/**
	 * Little helper method to return an element from an xpath.
	 * 
	 * @param xpath
	 * @return
	 */
	private Element getElement (String xpath) {
		Object obj = doc.selectSingleNode (xpath);
		if (obj != null)
			return (Element)obj;
		return null;
	}
		
	/**
	 * Print XML version.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		try {
			OutputFormat format = new OutputFormat ();
			StringWriter sw = new StringWriter ();
			XMLWriter writer = new XMLWriter (sw, format);
			writer.write (doc);
			return sw.toString();
		} catch (Exception e) { return ""; }
	}

	
}