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
package org.jogre.server.data.jdo;

import java.util.List;
import java.util.Vector;

import org.jogre.common.GameOver;
import org.jogre.server.data.AbstractServerData;
import org.jogre.server.data.GameInfo;
import org.jogre.server.data.GameSummary;
import org.jogre.server.data.IServerData;
import org.jogre.server.data.ServerDataException;
import org.jogre.server.data.User;

/**
 * Implementation of the IServerData interface to the
 * master server on www.jogre.org.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ServerDataJogreDotOrg extends AbstractServerData {
	
	/**
	 * Return type as a www.jogre.org master server.
	 * 
	 * @see org.jogre.server.data.IServerData#getType()
	 */
	public String getType () {
		return IServerData.JOGRE_DOT_ORG;
	}
	
	public boolean logon (String user) {
		return false;
	}

	public boolean logon (String user, String password) {
		return false;
	}

	public boolean containsUser(String username) {
		return false;
	}

	public boolean containsUser(String username, String password) {
		return false;
	}

	public GameOver addGame(GameInfo gameInfo, boolean eloRatings) {
		return null;
	}

	public GameSummary getGameSummary(String gameId, String username) {
		return null;
	}
	
	public void updateSnapshot (String gameId, int numOfUsers, int numOfGames) {}
	
	public void resetSnapshot (Vector gameKeys) {}

	public List getUsers() {
		return null;
	}

	public List getGameInfos() {
		return null;
	}

	public List getGameSummarys() {
		return null;
	}

	public void newUser(User user) {}

	public void deleteUser(User user) throws ServerDataException {}

	public void updateUser(User user) throws ServerDataException {}
}