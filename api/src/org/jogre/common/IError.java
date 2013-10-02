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
 * Interface which contains a number of error codes.
 * 
 * @author Bob
 */
public interface IError {
	
	/** No error. */
	public static final int NO_ERROR = -1;
	
	/** General error. */
	public static final int GENERAL_ERROR = 0;

	/** Server full. */
	public static final int SERVER_FULL = 1;

	/** Server doesn't support game. */
	public static final int GAME_NOT_SUPPORTED = 2;

	/** Status code for a user is already connected. */
	public static final int USER_ALREADY_CONNECTED = 3;

	/** Status code for a user who is not connected but is trying to
	 *  send a message. */
	public static final int USER_NOT_CONNECTED = 4;

	/** Status code for a user login is incorrect (username / password). */
	public static final int USER_LOGON_INCORRECT = 5;
    
    /** Status code for a server requires a password. */
    public static final int SERVER_REQUIRES_PASSWORD = 6;
    
    /** Status code for a server not able to load JDBC/ODBC driver. */
	public static final int JDBC_DRIVER_LOAD_ERROR = 7;
    
    /** Status code for a server being unable to connect to database.*/
    public static final int DATABASE_CONNECTION_ERROR = 8;

    /** Status code for too many tables already started on the server. */
    public static final int SERVER_TABLE_LIMIT_EXCEEDED = 9;

    /** Status code for too many tables started by this player on the server. */
    public static final int PLAYER_TABLE_LIMIT_EXCEEDED = 10;
    
    /** Status code for a server data error. */
    public static final int SERVER_DATA_ERROR = 11;
    
    /** Status code for a server data error. */
    public static final int SQL_ERROR = 12;

    /** Status code for database load error. */
	public static final int LOAD_ERROR = 13;
	
	/** Status code for IO error .*/
	public static final int IO_ERROR = 14;
	
}
