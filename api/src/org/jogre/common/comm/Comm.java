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
package org.jogre.common.comm;

/**
 * Interface for the variour standard communcations object which are available
 * in the JOGRE api.  The various Strings of this interface is the header of a
 * ITransmittable object (1st token of the flattened String).
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 * @see org.jogre.common.comm.ITransmittable
 */
public interface Comm {

    //========================================================================
    // Declare DATA structures
    //========================================================================

	/** Header token of the data class GameList. */
	public static final String GAME_LIST = "game_list";
	
    /** Header token of the data class Game. */
    public static final String GAME = "game";

    /** Header token of the data class UserList. */
    public static final String USER_LIST = "user_list";

    /** Header token of the data class User. */
    public static final String USER = "user";

    /** Header token of the data class TableList. */
    public static final String TABLE_LIST = "table_list";

    /** Header token of the data class Table. */
    public static final String TABLE = "table";

    /** Header token of the data class Table. */
    public static final String MODEL = "model";

    /** Header token of the data class PlayerList. */
    public static final String PLAYER_LIST = "player_list";

    /** Header token of the data class Player. */
    public static final String PLAYER = "player";
    
    //========================================================================
    // Declare Commands
    //========================================================================

    /** Header token of a CommAdminConnect communications object. */
	public static final String ADMIN_CONNECT = "admin_connect";
	
	/** Header token of a CommAdminGameMessage communications object. */
	public static final String ADMIN_GAME_MESSAGE = "admin_game_message";
	
	/** Header token of a CommAdminDataMessage communications object. */
	public static final String ADMIN_DATA_MESSAGE = "admin_data_message";
	
	/** Header token of a CommAdminServerProperties communications object. */
	public static final String ADMIN_SERVER_PROPERTIES = "admin_server_properties";

	/** Header token of a CommAdminIconData communications object. */
	public static final String ADMIN_ICON_DATA = "admin_icon_data";
	
	/** Header token of a CommAdminClientData communications object. */
	public static final String ADMIN_CLIENT_DATA = "admin_client_data";
    
	/** Header token of a CommChatGame communications object. */
	public static final String CHAT_GAME = "chat_game";

	/** Header token of a CommChatPrivate communications object. */
	public static final String CHAT_PRIVATE = "chat_private";

	/** Header token of a CommChatTable communications object. */
	public static final String CHAT_TABLE = "chat_table";

	/** Header token of a CommConnect communications object. */
	public static final String GAME_CONNECT = "game_connect";

	/** Header token of a CommMasterServerConnect communications object. */
	public static final String MASTER_SERVER_CONNECT = "master_server_connect";

	/** Header token of a CommMasterServerMesssage communications object. */
	public static final String MASTER_SERVER_MESSAGE = "master_server_message";

	/** Header token of a CommControllerProperty communications object. */
	public static final String CONTROLLER_PROPERTY = "controller_prop";

	/** Header token of a CommControllerProperty communications object. */
	public static final String CONTROLLER_OBJECT = "controller_obj";

	/** Header token of a CommDisconnect communications object. */
	public static final String DISCONNECT = "disconnect";

	/** Header token of a CommError communications object. */
	public static final String ERROR = "error";

	/** Header token of a CommExitTable communications object. */
	public static final String EXIT_TABLE = "exit_table";

	/** Header token of a CommGameOver communications object. */
	public static final String GAME_OVER = "game_over";
	
	/** Header token of a CommInvite communications object. */
	public static final String INVITE = "invite";

	public static final String JOIN_TABLE = "join_table";

	/** Header token of a CommNewTable object. */
	public static final String NEW_TABLE = "new_table";

	/** Header token of a CommNextPlayer object. */
	public static final String NEXT_PLAYER = "next_player";

	/** Header token of a CommOfferDraw communications object. */
	public static final String OFFER_DRAW = "offer_draw";

	/** Header token of a CommPlayerState communications object. */
	public static final String PLAYER_STATE = "player_state";

	/** Header token of a CommReadyToStart communications object. */
	public static final String READY_TO_START = "ready_to_start";

	/** Header token of a CommReadyToStart communications object. */
	public static final String REQUEST_DATA = "request_data";

	/** Header token of a CommServerProperties communications object. */
	public static final String SERVER_PROPERTIES = "server_properties";
	
	/** Header token of a CommSitDown communications object. */
	public static final String SIT_DOWN = "sit_down";

	/** Header token of a CommStandUp communications object. */
	public static final String STAND_UP = "stand_up";

	/** Header token of a CommStartGame communications object. */
	public static final String START_GAME = "start_game";

	/** Header token of a CommTableProperty communications object. */
	public static final String TABLE_PROPERTY = "table_property";
	
	/** Header token of a CommTestConnection communication object. */
	public static final String ADMIN_TEST_CONNECTION = "test_connection";

	//========================================================================
	// Delcare some properties
	//========================================================================

	/** Minimum number of user. */
	public static final String PROP_PLAYERS = "players";
}