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
package org.jogre.webapp;

/**
 * Interface which holds important JOGRE constants.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public interface IJogreWeb {
	
	// Environment variable - used to read property file name
	public static final String ENV_JOGRE_WEBAPP = "JOGRE_WEBAPP";
	public static final String PROPERTY_FILENAME = "webapp.properties";
	
	// Declare "webapp.properties" keys
	public static final String PROP_SUPPORTED_GAMES   = "supported.games";
	public static final String PROP_NEW_GAMES_LIST    = "new.games.list";
	public static final String PROP_NUM_OF_TOP_GAMES  = "num.of.top.games";
	public static final String PROP_SUPPORTED_LANGS   = "supported.langs";
	public static final String PROP_DATABASE_DRIVER   = "database.driver";
	public static final String PROP_DATABASE_URL      = "database.url";
	public static final String PROP_DATABASE_USERNAME = "database.username";
	public static final String PROP_DATABASE_PASSWORD = "database.password";
	public static final String PROP_JOGRE_SERVER_HOST = "jogre.server.host";
	public static final String PROP_JOGRE_SERVER_PORT = "jogre.server.port";
	
	// Declare forwards	
	public static final String FORWARD_ERROR              = "error";
	public static final String FORWARD_GAME               = "game";
	public static final String FORWARD_GAME_LIST          = "game_list";
	public static final String FORWARD_HELP               = "help";
	public static final String FORWARD_LOGON              = "logon";
	public static final String FORWARD_MAIN               = "main";
	public static final String FORWARD_PLAY_GAME          = "play_game";
	public static final String FORWARD_PLAY_GAME_INTERNAL = "play_game_internal";
	public static final String FORWARD_PROFILE            = "profile";
	public static final String FORWARD_REGISTER           = "register";	
	
	// Declare actions
	public static final String ACTION_LOGON      = "logon";
	public static final String ACTION_LOGOFF     = "logoff";
	public static final String ACTION_SUBMIT     = "submit";
	public static final String ACTION_SUCCESS    = "success";
	public static final String ACTION_NEW_WINDOW = "nw";
	
	// Declare genres
	public static final String GENRE_HOME      = "home";
	public static final String GENRE_ALL_GAMES = "all_games";
	public static final String GENRE_ARCADE    = "arcade";
	public static final String GENRE_CARD      = "card";
	public static final String GENRE_BOARD     = "board";
	public static final String GENRE_OTHERS    = "others";
	public static final String GENRE_DEFAULT   = "default";
}