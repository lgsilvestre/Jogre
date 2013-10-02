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
package org.jogre.server.data;

import java.util.List;
import java.util.Vector;

import org.jogre.common.GameOver;

/**
 * Interface between a JOGRE server and its persistent data.  This 
 * can be either XML, local database or a connection to the "jogre.org" 
 * server.
 *
 * The type of connection is set in the "server.xml" configuration file.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public interface IServerData {

	/** Property to load a simple data connection to XML. */
	public static final String XML = "xml";
	
	/** Property to load a more scalable database solution. */
	public static final String DATABASE = "database";
	
	/** Property to . */
	public static final String JOGRE_DOT_ORG = "jogre.org";
	
	/** Location of XML files */
	public static final String XML_FOLDER = "data/xml/";

	/** Location of database files */
	public static final String DATABASE_FOLDER = "data/database/";

	// Declare order of tabs in admin data panel
	/** Users data */
	public static final String DATA_USERS = "users";
	
	/** Game info */
	public static final String DATA_GAME_INFO = "game_info";
	
	/** Game summary */
	public static final String DATA_GAME_SUMMARY = "game_summary";
	
	/** Order of data items. */
	public static final String [] DATA_ORDER = {DATA_USERS, DATA_GAME_INFO, DATA_GAME_SUMMARY};
	
	/**
	 * Return the type of user connection.
	 * 
	 * @return   Type of user connection as a String.
	 */
	public String getType ();

    /**
	 * Return true / false if a user can log on without a password.
	 * 
	 * @param username   Username of client wishing to logon.
	 * @return           True if logon sucessful.
     * @throws ServerDataException 
	 */
	public boolean containsUser (String username) throws ServerDataException;
	
	/**
	 * Return true / false if a user can log on with username and password.
	 * 
	 * @param username   Username of client wishing to logon.
	 * @param password   Password of the client wishing to logon.
	 * @return           True if logon sucessful.
	 * @throws ServerDataException 
	 */
	public boolean containsUser (String username, String password) throws ServerDataException;	
	
	/**
	 * Method for add a game to persistent data.
	 * 
	 * @param gameInfo      Information about the game.
	 * @param eloRatings    Boolean to update the ratings (ELO).
	 * @return              Returns a GameOver object outling the new ratings.
	 * @throws ServerDataException 
	 */
	public GameOver addGame (GameInfo gameInfo, boolean eloRatings) throws ServerDataException;
	
	/**
	 * Return a game summary from a specified game and user.
	 * 
	 * @param gameId    Game id e.g. "chess".
	 * @param username  Username e.g. "bob".
	 * @return          GameSummary object
	 * @throws ServerDataException 
	 */
	public GameSummary getGameSummary (String gameId, String username) throws ServerDataException;
	
	/**
	 * Update the server snapshot.
	 * 
	 * @param gameId      Game id.
	 * @param numOfUsers  Number of users.
	 * @param numOfTables  Number of tables.
	 * @throws ServerDataException 
	 */
	public void updateSnapshot (String gameId, int numOfUsers, int numOfTables) throws ServerDataException;
	
	/**
	 * Resets the snapshot of the server.  This should be called when the server
	 * is loaded.
	 * 
	 * @param gameKeys  Vector of game keys.
	 * @throws ServerDataException 
	 */
	public void resetSnapshot (Vector gameKeys) throws ServerDataException;
	
	/**
	 * Return a list of users of type org.jogre.server.data.User.
	 * 
	 * @return
	 * @throws ServerDataException 
	 */
	public List getUsers () throws ServerDataException;
	
	/**
	 * Return a list of game summarys of type org.jogre.server.data.GameSummary.
	 * 
	 * @return
	 * @throws ServerDataException 
	 */
	public List getGameSummarys() throws ServerDataException;
	
	/**
	 * Return a list of game infos of type org.jogre.server.data.GameInfo.
	 * 
	 * @return
	 * @throws ServerDataException 
	 */
	public List getGameInfos() throws ServerDataException;

	/**
	 * Add a new user.  Throws an exception if a user already exists or if there is a
	 * problem.
	 * 
	 * @param user
	 * @throws ServerDataException 
	 */
	public void newUser(User user) throws ServerDataException;

	/**
	 * Delete a user.
	 * 
	 * @param user   User to delete.
	 */
	public void deleteUser (User user) throws ServerDataException;

	/**
	 * Update a user.
	 * 
	 * @param user
	 * @throws ServerDataException
	 */
	public void updateUser (User user) throws ServerDataException;
}