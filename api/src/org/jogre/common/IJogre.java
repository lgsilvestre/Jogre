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
package org.jogre.common;

/**
 * Interface file to contain global properties such as default values 
 * for the number of players etc.  These should be set in game property files.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public interface IJogre {
		
	// Declare some  constants.
	public static final String DATE_FORMAT_FULL = "dd/MM/yyyy-hh:mm:ss";
	public static final String DATE_FORMAT_TIME  = "hh:mm:ss";
	
	/** Link to the current version of JOGRE. */
	public static final String VERSION = "beta 0.3";

	/** Key to the master server. */
	public static final String MASTER_SERVE_KEY = "master_server";
	
	/** Key to the administrator. */
	public static final String ADMINISTRATOR = "admin";

	/** Default port that JOGRE runs on. */
	public static final int DEFAULT_PORT = 1790;
	
	/** Default number of players (2). */
	public static final int DEFAULT_NUM_OF_PLAYERS = 2;

	/** Default minimum number of players (2). */
	public static final int DEFAULT_MIN_NUM_OF_PLAYERS = 2;

	/** Default maximum number of players (2). */
	public static final int DEFAULT_MAX_NUM_OF_PLAYERS = 2;

	/** Default required number of players (2). */
	public static final int DEFAULT_REQ_NUM_OF_PLAYERS = 2;

	/** Default maximum number of table (2). */
	public static final int DEFAULT_MAX_NUM_OF_TABLES = 50;
	
	/** Default number of tables per user */
	public static final int DEFAULT_MAX_NUM_OF_TABLES_PER_USER = 2;
	
	/** Default ELO starting rating for new users. */ 
	public static final int DEFAULT_ELO_START_RATING = 1200; 

	/** Default ELO k factor. */
	public static final double DEFAULT_ELO_K_FACTOR = 24.0;
	
	/** Number of games a user must complete not to be provisional. */
	public static final int PROVISIONAL_COUNT = 10;
	
	/** Default value if an admin receives all client messages". */
	public static final boolean ADMIN_RECEIVE_MESSAGES = true;
	
	/**
	 * Default maximum number of users which is multiplied by the number
	 * of games running.
	 */
	public static final int DEFAULT_MAX_NUM_OF_USERS = 100;
	
	/** Applet parameter "username". */
	public static final String APPLET_PARAM_USERNAME = "username";
	
	/** Applet parameter "password". */
	public static final String APPLET_PARAM_PASSWORD = "password";
	
	/** Applet parameter "serverhost". */
	public static final String APPLET_PARAM_SERVER_HOST = "serverhost";

	/** Applet parameter "serverport". */
	public static final String APPLET_PARAM_SERVER_PORT = "serverport";

	/** Applet parameter "language". */
	public static final String APPLET_PARAM_LOCALE = "language";
}
