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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.server;

import java.util.HashMap;

import org.jogre.common.IJogre;

/**
 * List of Connection objects.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ConnectionList {

	/** Hashmap of connections. */
	private HashMap connections;

	/**
	 * Constructor which sets up a Hash to store the various Connection
	 * objects in.
	 */
	public ConnectionList () {
		this.connections = new HashMap ();
	}

	/**
	 * Create a new Connection object using a ServerConnectionThread and add
	 * to the connections HashMap using the username as the key.
	 *
	 * @param gameId 	       Game id e.g. "chess"
	 * @param username         Username of person.
	 * @param connectionThread ServerConnectionThread.
	 */
	public void addConnection (String gameId, String username, ServerConnectionThread connectionThread) {
		Connection conn = new Connection (connectionThread);
		
		connections.put (getKey (gameId, username), conn);
	}
	
	/**
	 * Set the administrator thread.
	 *
	 * @param gameId 	       Game id e.g. "chess"
	 * @param username         Username of person.
	 * @param connectionThread ServerConnectionThread.
	 */
	public void setAdminConnection (ServerConnectionThread connectionThread) {
		addConnection (IJogre.ADMINISTRATOR, IJogre.ADMINISTRATOR, connectionThread);
	}
	
	/**
	 * Return a serverconnection thread for a specified user (which is a field
	 * of the Connection object).
	 *
	 * @param username Username of user.
	 * @return
	 */
	public ServerConnectionThread getServerConnectionThread (String gameId, String username) {
		if (gameId != null) {
			if (gameId.equals(IJogre.ADMINISTRATOR))
				return getConnection (IJogre.ADMINISTRATOR, IJogre.ADMINISTRATOR).getServerConnectionThread();
			else 
				return getConnection (gameId, username).getServerConnectionThread();
		}
		
		return null;
	}
	
	/**
	 * Return a Connection object of specified user.
	 *
	 * @param username
	 * @return
	 */
	public Connection getConnection (String gameId, String username) {
		return (Connection)connections.get (getKey (gameId, username));
	}

	/**
	 * Remove a connection.
	 *
	 * @param username
	 */
	public void removeConnection (String gameId, String username) {
		connections.remove (getKey (gameId, username));
	}
	
	/**
	 * Remove the admin connection.
	 */
	public void removeAdminConnection () {
		connections.remove (getKey (IJogre.ADMINISTRATOR, IJogre.ADMINISTRATOR));
	}

	/**
	 * Return number of connections.
	 *
	 * @return
	 */
	public int size () {
		return connections.size();
	}

	/**
	 * Return hash key from a gameId and a username.
	 *
	 * @param gameId   GameID
	 * @param username Username
	 * @return
	 */
	private String getKey (String gameId, String username) {
	    return gameId + "-" + username;
	}

	/**
	 * Return admin connection.
	 * 
	 * @return
	 */
	public ServerConnectionThread getAdminConnection() {
		Connection conn = getConnection(IJogre.ADMINISTRATOR, IJogre.ADMINISTRATOR); 
		
		if (conn != null)
			return conn.getServerConnectionThread();
		 
		return null;
	}
}
