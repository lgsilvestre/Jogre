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
package org.jogre.webapp.forms;

import org.jogre.webapp.data.OnlineGame;

/**
 * Main JOGRE form object.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameForm extends AbstractJogreForm {
	
	private OnlineGame game;
	
	/**
	 * Blank constructor.
	 */
	public GameForm () {}
	
	public void setGameKey (String gameKey) {
		this.gameKey = gameKey;
	}
	
	public String getGameKey () {
		return this.gameKey;
	}
	
	public void setOnlineGame (OnlineGame game) {
		this.game = game;
	}

	public String getGameName() {
		return game.getGameName();
	}

	public String getGameSynopsis() {
		return game.getGameSynopsis();
	}
	
	public String getGameRules() {
		return game.getGameRules();
	}

	public int getNumOfTables() {
		return game.getNumOfTables();
	}

	public int getNumOfUsers() {
		return game.getNumOfUsers();
	}
}