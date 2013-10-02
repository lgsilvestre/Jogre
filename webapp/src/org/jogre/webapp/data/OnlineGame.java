/*
 * JOGRE (Java Online Gaming Real-time Engine) - Webapp
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
package org.jogre.webapp.data;

import org.apache.struts.util.MessageResources;
import org.jogre.server.data.SnapShot;

/**
 * Immutable online game data object used in the JOGRE web system.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class OnlineGame {

	// Fields
	private String gameKey;
	private int numOfUsers; 
	private int numOfTables;
	private String gameName;
	private String gameSynopsis;
	private String gameRules;
	private String gameGenre;
	
	/**
	 * Constructor which takes a snapshot and message resources.
	 * 
	 * @param snapshot
	 * @param resources
	 */
	public OnlineGame (SnapShot snapshot, MessageResources resources) {
		this.gameKey = snapshot.getGameKey();
		this.numOfUsers = snapshot.getNumOfUsers();
		this.numOfTables = snapshot.getNumOfTables();
		this.gameName = resources.getMessage(gameKey);
		this.gameSynopsis = resources.getMessage(gameKey + ".synopsis");
		this.gameRules = resources.getMessage(gameKey + ".rules");
		this.gameGenre = resources.getMessage(gameKey + ".genre");
	}

	public String getGameKey() {
		return gameKey;
	}

	public int getNumOfUsers() {
		return numOfUsers;
	}

	public int getNumOfTables() {
		return numOfTables;
	}

	public String getGameName() {
		return gameName;
	}

	public String getGameSynopsis() {
		return gameSynopsis;
	}
	
	public String getGameRules() {
		return gameRules;
	}
	
	public String getGameGenre() {
		return gameGenre;
	}
}