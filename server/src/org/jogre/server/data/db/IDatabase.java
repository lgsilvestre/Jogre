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
package org.jogre.server.data.db;

/**
 * Interface holding some constants used with creating server database connections.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public interface IDatabase {

	//=============================================================================
	// List of statement ID's in iBatis SQL map files
	//=============================================================================
	
	// "sqlmap_user.xml"
	public static final String ST_ADD_USER = "addUser";
	public static final String ST_SELECT_USER = "selectUser";
	public static final String ST_SELECT_ALL_USERS = "selectAllUsers";
	public static final String ST_UPDATE_USER = "updateUser";
	public static final String ST_DELETE_USER = "deleteUser";
	
	// "sqlmap_game_info.xml"
	public static final String ST_ADD_GAME_INFO = "addGameInfo";
	public static final String ST_GET_GAME_INFO_ID = "getGameInfoId";
	public static final String ST_SELECT_GAME_INFO = "selectGameInfo";
	public static final String ST_SELECT_ALL_GAME_INFOS = "selectAllGameInfo";
	public static final String ST_UPDATE_GAME_INFO = "updateGameInfo";
	public static final String ST_DELETE_GAME_INFO = "deleteGameInfo";
	
	// "sqlmap_game_summary.xml"
	public static final String ST_ADD_GAME_SUMMARY = "addGameSummary";
	public static final String ST_SELECT_GAME_SUMMARY = "selectGameSummary";
	public static final String ST_SELECT_ALL_GAME_SUMMARYS = "selectAllGameSummary";
	public static final String ST_UPDATE_GAME_SUMMARY = "updateGameSummary";
	public static final String ST_DELETE_GAME_SUMMARY = "deleteGameSummary";
	
	// "sqlmap_snap_shot.xml"	
	public static final String ST_ADD_SNAP_SHOT = "addSnapShot";
	public static final String ST_SELECT_SNAP_SHOT = "selectSnapShot";
	public static final String ST_SELECT_ALL_SNAP_SHOTS = "selectAllSnapShot";
	public static final String ST_UPDATE_SNAP_SHOT = "updateSnapShot";
	public static final String ST_DELETE_SNAP_SHOT = "deleteSnapShot";
	public static final String ST_DELETE_ALL_SNAP_SHOT = "deleteAllSnapShot";
	
	//=============================================================================
	// Other constants
	//=============================================================================
	
	/** Default driver - used when creating a blank database connection. */
	public static final String DEFAULT_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
	
	/** Default database URL - used when creating a blank database connection. */
	public static final String DEFAULT_URL = "jdbc:odbc:";
	
}