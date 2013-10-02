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

import java.awt.Color;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.jogre.common.IJogre;


/**
 * This class provides an easy and effective point for accessing to each game
 * resource bundle (game.properties).
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class GameProperties extends AbstractProperties implements IJogre {

	// Declare constants
	
	// Special JOGRE images (3 in total every game must have).
	public static final String IMAGE_JOGRE_TITLE = "image.jogre.title";
	public static final String IMAGE_GAME_TITLE = "image.game.title";
	public static final String IMAGE_GAME_ICON = "image.game.icon";

	// name of the lookandfeel properties file
	private static final String DEFAULT_FILENAME = "game";

	// Declare some keys
	private static final String KEY_PLAYER_COLOUR      = "player.colour.";
	private static final String KEY_GAME_ID            = "game.id";
	private static final String KEY_MAX_NUM_OF_TABLES  = "max.num.of.tables";
	private static final String KEY_BACKGROUND_COLOUR  = "background.colour";
	private static final String KEY_TITLE_COLOUR       = "title.colour";
	private static final String IMAGE_LABEL            = "image.";
	private static final String KEY_MAX_NUM_OF_TABLES_PER_USER = "tables.per.user";
	private static final String KEY_RULES_FILENAME     = "rules.file";
	private static final String KEY_TABLE_PLAYER_HIGHLIGHT_COLOR = "table.current.player.highlight.colour";
	private static final String KEY_TABLE_PLAYER_TEXT_COLOR = "table.current.player.text.colour";

	// Some defaults
	private static final String DEFAULT_BACKGROUND_COLOUR = "212,208,200";
	private static final String DEFAULT_TITLE_COLOUR = "100,100,100";

	private static GameProperties instance = null;

	/**
	 * Private constructor (Can only be called by the getInstance() method.
	 */
	private GameProperties () {
		super (DEFAULT_FILENAME);
	}

	/**
	 * Accessor to singleton instance of this class.
	 *
	 * @return  Instance of this class.
	 */
	public static GameProperties getInstance() {
		if (instance == null)
			instance = new GameProperties();

		return instance;
	}

	/**
	 * Return the minimum number of players in a game.
	 *
	 * @return    Max num of players.
	 */
	public static int getMaxNumOfTables () {
		return getInstance().getInt (KEY_MAX_NUM_OF_TABLES, DEFAULT_MAX_NUM_OF_TABLES);
	}

	/**
	 * Return the maximum number of tables that a user can create.
	 *
	 * @return
	 */
	public static int getMaxNumOfTablesPerUser () {
		return getInstance().getInt (KEY_MAX_NUM_OF_TABLES_PER_USER, DEFAULT_MAX_NUM_OF_TABLES_PER_USER);
	}

	/**
	 * Return the game ID.
	 *
	 * @return    GameID e.g. chess.
	 */
	public static String getGameID () {
	    return getInstance().get (KEY_GAME_ID);
	}

	/**
	 * Return the game label on its own.
	 *
	 * @return    Game label (no id) - e.g. chess.
	 */
	public static String getGameNoID () {
	    String gameId = getGameID();
	    int index = gameId.indexOf('-');
		return gameId.substring (0, index - 1);
	}

	/**
	 * Return the color of a player (each player has a specific colour which
     * is used throughout the API).
	 *
	 * @param playerNum      Supplied player number
	 * @return              Color
	 */
	public static Color getPlayerColour (int playerNum) {
		String key = KEY_PLAYER_COLOUR + playerNum;
		String colorStr = GameProperties.getInstance().get(key);
		return JogreUtils.getColour (colorStr);
	}

	/**
	 * Return the background colour of the JOGRE table frame.
	 *
	 * @return              Color
	 */
	public static Color getBackgroundColour () {
		String colorStr = GameProperties.getInstance().get(KEY_BACKGROUND_COLOUR, DEFAULT_BACKGROUND_COLOUR);
		return JogreUtils.getColour (colorStr);
	}
	
	/**
	 * Return the title colour.
	 * 
	 * @return
	 */
	public static Color getTitleColour () {
		String colorStr = GameProperties.getInstance().get(KEY_TITLE_COLOUR, DEFAULT_TITLE_COLOUR);
		return JogreUtils.getColour (colorStr);
	}

	/**
	 * <p>Return a vector of images.</p>
	 *
	 * <p>Each image info will consist of the image and its size
	 * in bytes:</p>
	 *
	 *    e.g. chess_icon
	 *
	 * @return   Vector of image strings.
	 */
	public static Properties getImageProperties () {
		Properties properties = new Properties();

		List keys = getInstance().getKeys ();
		for (int i = 0; i < keys.size(); i++) {
			String key = (String)keys.get(i);
			if (key.startsWith("image."))
				properties.put (key, getInstance().get(key));
		}

		// Add special images, if they aren't already defined in the game.properties file
		String gameId = getInstance().getGameID();
		if (!properties.containsKey (IMAGE_JOGRE_TITLE)) {
			properties.put (IMAGE_JOGRE_TITLE, "images/jogre_title.gif");
		}
		if (!properties.containsKey (IMAGE_GAME_TITLE)) {
			properties.put (IMAGE_GAME_TITLE,  "images/" + gameId + "_title.gif");
		}
		if (!properties.containsKey (IMAGE_GAME_ICON)) {
			properties.put (IMAGE_GAME_ICON,   "images/" + gameId + "_icon.gif");
		}

		return properties;
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
	 * Return the color to use to highlight the current player in the table list.
	 *
	 * If there is a table.current.player.highlight.colour in the game.properties
	 * file, then that one is used, otherwise the default color provided is used.
	 *
	 * @param defaultColorString   The color to be used if there isn't one
	 *                             defined in the game.properties file.
	 * @return the color to use to highlight the current player in the table
	 *         list display.
	 */
	public static Color getTableCurrPlayerHighlightColor (String defaultColorString) {
		String colorStr = GameProperties.getInstance().get (
		         KEY_TABLE_PLAYER_HIGHLIGHT_COLOR, defaultColorString);
		return JogreUtils.getColour (colorStr);
	}

	/**
	 * Return the color to use to display the text of the current player in the
	 * table list.
	 *
	 * If there is a table.current.player.text.colour in the game.properties
	 * file, then that one is used, otherwise the default color provided is used.
	 *
	 * @param defaultColorString   The color to be used if there isn't one
	 *                             defined in the game.properties file.
	 * @return the color to use to display the text of the current player in the
	 *         table list display.
	 */
	public static Color getTableCurrPlayerTextColor (String defaultColorString) {
		String colorStr = GameProperties.getInstance().get (
		         KEY_TABLE_PLAYER_TEXT_COLOR, defaultColorString);
		return JogreUtils.getColour (colorStr);
	}

}
