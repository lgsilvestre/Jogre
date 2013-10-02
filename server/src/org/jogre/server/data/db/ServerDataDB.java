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
package org.jogre.server.data.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.jogre.common.GameOver;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.EloRatingSystem;
import org.jogre.server.ServerProperties;
import org.jogre.server.data.AbstractServerData;
import org.jogre.server.data.GameInfo;
import org.jogre.server.data.GameSummary;
import org.jogre.server.data.ServerDataException;
import org.jogre.server.data.SnapShot;
import org.jogre.server.data.User;

/**
 * Implementation of the IServerData to a local database.
 *
 * FIXME PUT ALL STACK TRACE PRINTS INTO SOME SORT OF LOGGING UTILITY
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class ServerDataDB extends AbstractServerData implements IDatabase {
	
	/**
	 * Return type as a database.
	 *
	 * @see org.jogre.server.data.IServerData#getType()
	 */
	public String getType () {
		return DATABASE;
	}

	/**
     * Run query to see if user is in the "users" table given a username.
     *
	 * @see org.jogre.server.data.IServerData#containsUser(java.lang.String)
	 */
	public boolean containsUser (String username) throws ServerDataException {		
		ServerProperties serverProperties = ServerProperties.getInstance();
        if (serverProperties.isUserValidationNotRequired()) {
            return true;
        } else if (serverProperties.isUserValidationPassword()) {
            return false;
        } else if (serverProperties.isUserValidationUser()) {
        	try {
        		IBatis iBatis = IBatis.getInstance();
        		
        		User parameterUser = new User ();
        		parameterUser.setUsername(username);
        		
        		User resultUser = (User)iBatis.getObject(ST_SELECT_USER, parameterUser);
        		
        		return resultUser != null;
        	}
        	catch (IOException ioEx) {
    			ioEx.printStackTrace();		// should use proper logging at some stage
    			throw new ServerDataException (ioEx.getMessage());
    		}
    		catch (SQLException sqlEx) {
    			sqlEx.printStackTrace();
    			throw new ServerDataException (sqlEx.getMessage());
    		}
        }
        
        return false;
	}

	/**
     * Run query to see if user is in the "users" table given a username
     * and a password.
     *
	 * @see org.jogre.server.data.IServerData#containsUser(java.lang.String, java.lang.String)
	 */
	public boolean containsUser (String username, String password) throws ServerDataException {
		ServerProperties serverProperties = ServerProperties.getInstance();
        if (serverProperties.isUserValidationNotRequired()) {
            return true;
        } else if (serverProperties.isUserValidationUser()) {
            return containsUser (username);
        } else if (serverProperties.isUserValidationPassword()) {
        	try {
        		IBatis iBatis = IBatis.getInstance();
        		
        		User parameterUser = new User ();
        		parameterUser.setUsername(username);
        		parameterUser.setPassword(password);
        		
        		User resultUser = (User)iBatis.getObject(ST_SELECT_USER, parameterUser);
        		
        		return resultUser != null;
        	}
        	catch (IOException ioEx) {
    			ioEx.printStackTrace();		// should use proper logging at some stage
    			throw new ServerDataException (ioEx.getMessage());
    		}
    		catch (SQLException sqlEx) {
    			sqlEx.printStackTrace();
    			throw new ServerDataException (sqlEx.getMessage());
    		} 
        }
        
        return false;
	}

	/**
     * Method for adding a game to persistent data.
     *
	 * @see org.jogre.server.data.IServerData#addGame(org.jogre.server.data.GameInfo, boolean)
	 */
	public GameOver addGame (GameInfo gameInfo, boolean eloRatings) throws ServerDataException {
		GameOver gameOver = null;
        try {
        	IBatis ibatis = IBatis.getInstance();
            
            // Insert the game info to the database        	
    		ibatis.update(ST_ADD_GAME_INFO, gameInfo);

            // Update the game summaries now on the database
            String    gameKey    = gameInfo.getGameKey();
            String [] players    = JogreUtils.convertToStringArray(gameInfo.getPlayers());
            int    [] results    = JogreUtils.convertToIntArray(gameInfo.getResults());
            int       numPlayers = players.length;

            // Loop through players at table and update them
            GameSummary [] gameSummary = new GameSummary [numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                String curPlayer = players [i];

                // Retrieve game summary from XML (create new if not exists).
                gameSummary [i] = getGameSummary (gameKey, curPlayer);
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

                // Update game summaries and database
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

            // Update database
            for (int i = 0; i < numPlayers; i++) {
                ibatis.update(ST_UPDATE_GAME_SUMMARY, gameSummary[i]);
            }
        }
        catch (IOException ioEx) {
			ioEx.printStackTrace();		// should use proper logging at some stage
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}

        return gameOver;
	}

	/**
     * Return the game summary for a specified user.
     *
	 * @see org.jogre.server.data.IServerData#getGameSummary(java.lang.String, java.lang.String)
	 */
	public GameSummary getGameSummary (String gameKey, String username) throws ServerDataException {
		try {
			IBatis ibatis = IBatis.getInstance();
			GameSummary param = new GameSummary (gameKey, username);		
			GameSummary gameSummary = (GameSummary)ibatis.getObject(ST_SELECT_GAME_SUMMARY, param);
			
			// Check to see if game summary is null
			if (gameSummary != null) {
				return gameSummary;				
			}
			else {		 // create new game summary and add to database
				param.setRating(ServerProperties.getInstance().getStartRating (gameKey));
				ibatis.update(ST_ADD_GAME_SUMMARY, param);
				
				return param;
			}
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();		// should use proper logging at some stage
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.jogre.server.data.IServerData#updateSnapshot(java.lang.String, int, int)
	 */
	public void updateSnapshot (String gameKey, int numOfUsers, int numOfTables) throws ServerDataException {
		try {
			IBatis ibatis = IBatis.getInstance();
			SnapShot snapShot = new SnapShot (gameKey, numOfUsers, numOfTables);
			ibatis.update(ST_UPDATE_SNAP_SHOT, snapShot);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();		// should use proper logging at some stage
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
    }

	/**
     * Reset the database snapshot.
     *
	 * @see org.jogre.server.data.IServerData#resetSnapshot(java.util.Vector)
	 */
	public void resetSnapshot (Vector gameKeys) throws ServerDataException {
		try {
			IBatis ibatis = IBatis.getInstance();
			ibatis.update(ST_DELETE_ALL_SNAP_SHOT);
			
			for (int i = 0; i < gameKeys.size(); i++) {
				String gameKey = (String)gameKeys.get(i);
				ibatis.update(ST_ADD_SNAP_SHOT, new SnapShot (gameKey, 0, 0));
			}
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();		// should use proper logging at some stage
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
    }

	/**
	 * Return list of all users.
	 * 
	 * @see org.jogre.server.data.IServerData#getUsers()
	 */
	public List getUsers() throws ServerDataException {
		try {
			return IBatis.getInstance().getList(ST_SELECT_ALL_USERS);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}

	/**
	 * Return the game infos.
	 * 
	 * @see org.jogre.server.data.IServerData#getGameInfos()
	 */
	public List getGameInfos() throws ServerDataException {
		try {
			return IBatis.getInstance().getList(ST_SELECT_ALL_GAME_INFOS);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}

	/**
	 * Return the game summaries.
	 * 
	 * @see org.jogre.server.data.IServerData#getGameSummarys()
	 */
	public List getGameSummarys() throws ServerDataException {
		try {
			return IBatis.getInstance().getList(ST_SELECT_ALL_GAME_SUMMARYS);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}

	/**
	 * Add a new user.
	 * 
	 * @see org.jogre.server.data.IServerData#newUser(org.jogre.server.data.User)
	 */
	public void newUser(User user) throws ServerDataException {
		try {
			IBatis.getInstance().update(ST_ADD_USER, user);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}

	/** 
	 * Delete a user.
	 * 
	 * @see org.jogre.server.data.IServerData#deleteUser(org.jogre.server.data.User)
	 */
	public void deleteUser(User user) throws ServerDataException {
		try {
			IBatis.getInstance().update(ST_DELETE_USER, user);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}

	/**
	 * Update a user.
	 * 
	 * @see org.jogre.server.data.IServerData#updateUser(org.jogre.server.data.User)
	 */
	public void updateUser(User user) throws ServerDataException {
		try {
			IBatis.getInstance().update(ST_UPDATE_USER, user);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw new ServerDataException (ioEx.getMessage());
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			throw new ServerDataException (sqlEx.getMessage());
		}
	}
}