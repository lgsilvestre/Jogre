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

/**
 * Data class for the snapshot object.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class SnapShot {
	
	private String gameKey;
	private int numOfUsers; 
	private int numOfTables;
	
	// Order by string 
	private String orderBy = null;
	
	/**
	 * Blank constructor.
	 */
	public SnapShot() {}
	
	/**
	 * Snapshot which takes a single gameKey.
	 * 
	 * @param gameKey
	 */
	public SnapShot(String gameKey) {
		this.gameKey = gameKey;
	}
	
	/**
	 * Constructor which takes the gameId, number of users and tables.
	 * 
	 * @param gameKey
	 * @param numOfUsers
	 * @param numOfTables
	 */
	public SnapShot(String gameKey, int numOfUsers, int numOfTables) {
		this.gameKey = gameKey;
		this.numOfUsers = numOfUsers;
		this.numOfTables = numOfTables;
	}
	
	public String getGameKey() {
		return gameKey;
	}
	
	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}
	
	public int getNumOfUsers() {
		return numOfUsers;
	}
	
	public void setNumOfUsers(int numOfUsers) {
		this.numOfUsers = numOfUsers;
	}
	
	public int getNumOfTables() {
		return numOfTables;
	}
	
	public void setNumOfTables(int numOfTables) {
		this.numOfTables = numOfTables;
	}

	public String getOrderBy () {
		return orderBy;
	}

	public void setOrderBy (String orderBy) {
		this.orderBy = orderBy;
	}
}