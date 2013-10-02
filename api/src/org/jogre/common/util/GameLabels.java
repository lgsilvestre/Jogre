/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.common.util;

/**
 * This class provides an easy and effective point for accessing to each game
 * resource bundle (game_labels_*_*.properties).
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class GameLabels extends AbstractProperties {

    // name of the lookandfeel properties file
	private static final String DEFAULT_FILENAME   = "game_labels";
	private static final String CLIENT_TITLE       = "jogre.client.title";
	private static final String TABLE_TITLE        = "jogre.table.title";
	private static final String WELCOME_MESSAGE    = "jogre.client.welcome.message";
	private static final String PLAYER_LABEL       = "player.label.";
	private static final String KEY_RULES_FILENAME = "rules.file";	
	private static final String GAME_LABEL         = "game.label";

	private static GameLabels instance = null;

	/**
	 * Private constructor (Can only be called by the getInstance() method.
	 */
	private GameLabels () {
		super (DEFAULT_FILENAME);
	}

	/**
	 * Accessor to singleton instance of this class.
	 *
	 * @return  instance of GameLabels
	 */
	public static GameLabels getInstance() {
		if (instance == null)
			instance = new GameLabels ();

		return instance;
	}

	/**
	 * Return the color of a player (each player has a specific colour which
     * is used throughout the API).
	 *
	 * @param playerNum
	 * @return   PlayerLabel from a player number.
	 */
	public static String getPlayerLabel (int playerNum) {
		return getInstance().get (PLAYER_LABEL + playerNum);
	}

	/**
	 * Return the client title.
	 *
	 * @return    Client title
	 */
	public static String getClientTitle () {
		return getInstance().get (CLIENT_TITLE);
	}

	/**
	 * Return the title of this table.
	 *
	 * @return    Table title.
	 */
	public static String getTableTitle () {
		return getInstance(). get (TABLE_TITLE);
	}

	/**
	 * Return a welcome message.
	 *
	 * @return     Welcome message.
	 */
	public static String getWelcomeMessage () {
		return getInstance().get (WELCOME_MESSAGE);
	}

	/**
	 * Return the name of the rules file
	 *
	 * @return   Rules filename
	 */
	public static String getRulesFilename () {
	    return getInstance().get (KEY_RULES_FILENAME, (String) null);
	}

	/**
	 * Return the game label. 
	 * 
	 * @return
	 */
	public static String getGameLabel() {
		return getInstance().get (GAME_LABEL, "!!! NOT SPECIFIED !!!");
	}
}
