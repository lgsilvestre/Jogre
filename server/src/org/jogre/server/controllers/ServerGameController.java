/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.server.controllers;

import java.util.List;

import nanoxml.XMLElement;

import org.jogre.common.Game;
import org.jogre.common.IError;
import org.jogre.common.IJogre;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.TransmissionException;
import org.jogre.common.User;
import org.jogre.common.UserList;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommAdminClientData;
import org.jogre.common.comm.CommAdminConnect;
import org.jogre.common.comm.CommAdminIconData;
import org.jogre.common.comm.CommAdminServerProperties;
import org.jogre.common.comm.CommAdminTestConnection;
import org.jogre.common.comm.CommChatClient;
import org.jogre.common.comm.CommChatPrivate;
import org.jogre.common.comm.CommDisconnect;
import org.jogre.common.comm.CommError;
import org.jogre.common.comm.CommGameConnect;
import org.jogre.common.comm.CommInvite;
import org.jogre.common.comm.CommJoinTable;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.comm.CommNewUserConnect;
import org.jogre.common.comm.CommRequestData;
import org.jogre.common.util.JogreLogger;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.JogreServer;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;
import org.jogre.server.ServerProperties;
import org.jogre.server.data.GameSummary;
import org.jogre.server.data.IServerData;
import org.jogre.server.data.ServerDataException;
import org.jogre.server.data.db.DBConnection;
import org.jogre.server.data.xml.ServerDataXML;

/**
 * Class which parses game specific messages on the server (used to
 * be called ServerGameParser).
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class ServerGameController {

	/** Logging. */
	JogreLogger logger = new JogreLogger (this.getClass());

	/** Convience link to the server. */
	protected JogreServer server;

	/**
	 * Constructor.
	 */
	public ServerGameController () {
		// Set up fields
		this.server = JogreServer.getInstance();
	}

	/**
	 * Parse this game specific message.
	 *
	 * @param conn         Connection back to client
	 * @param message      Message to parse
	 */
	public synchronized void parseGameMessage (ServerConnectionThread conn, XMLElement message) {
		try {
			// 1) Implementation of Comm commands
			String messageType = message.getName();

			// Check for a connect message first of all
			if (messageType.equals (Comm.GAME_CONNECT)) {
				gameConnect (conn, message);
			}
			if (messageType.equals (Comm.GAME_NEWUSER)) {
				newUserConnect (conn, message);
			}
			else if (messageType.equals (Comm.ADMIN_CONNECT)) {
				adminConnect (conn, message);
			}

			else {
				// Ensure username / gameID is set.  If not then send
				// an error message back to the client and quit.
				if (conn.getUsername() == null || conn.getGameID() == null) {
					conn.send (new CommError (CommError.USER_NOT_CONNECTED));
					conn.stopLoop();
				}

				// Check if message for administrator thread.
				if (messageType.equals (Comm.ADMIN_SERVER_PROPERTIES)) {
					setServerProperties (conn, message);
				}
				else if (messageType.equals (Comm.ADMIN_TEST_CONNECTION)) {
					testConnection (conn, message);
				}
				else if (messageType.equals (Comm.ADMIN_CLIENT_DATA)) {
					adminClientData (conn, message);
				}
				// Other messages
				else {
					if (messageType.equals (Comm.DISCONNECT)) {
						disconnect (conn, new CommDisconnect (message));
					}
					else if (messageType.equals (Comm.INVITE)) {
						invite (conn, new CommInvite (message));
					}
					else if (messageType.equals (Comm.CHAT_GAME)) {
						chatGame (conn, new CommChatClient (message));
					}
					else if (messageType.equals (Comm.CHAT_PRIVATE)) {
						chatPrivate (conn, new CommChatPrivate (message));
					}
					else if (messageType.equals (Comm.REQUEST_DATA)) {
						requestData (conn, new CommRequestData (message));
					}
					else if (messageType.equals (Comm.NEW_TABLE)) {
						createTable (conn, new CommNewTable (message));
					}

					// Delegate to a custom controller to do extra functionality
					String gameID = conn.getGameID();
					ServerController controller = server.getControllers().getCustomController (gameID);
					if (controller != null)
						controller.parseGameMessage (conn, message);
				}
			}
		}
		catch (TransmissionException transEx) {
			transEx.printStackTrace();
		} 
		catch (ServerDataException sdEx) {
			conn.send (new CommError (IError.SERVER_DATA_ERROR, sdEx.getMessage()));
		}
	}

	/**
	 * Connect a user to a server.
	 *
	 * @param conn
	 * @param message                  Connection object.
	 * @throws TransmissionException   Thrown if there is a transmission error.
	 */
	private void gameConnect (ServerConnectionThread conn, XMLElement message) throws TransmissionException, ServerDataException {
		ServerProperties serverProperties = ServerProperties.getInstance();

		// Unpack game connect message
		CommGameConnect commConnect = new CommGameConnect (message);
		String username = commConnect.getUsername();
		String gameID = commConnect.getGameID();

		// Check server supports this particular game
		if (!server.getGameList().containsGame (gameID)) {

			// Send "game not supported" error to client and kill thread
			conn.send (new CommError (CommError.GAME_NOT_SUPPORTED));
			conn.stopLoop();
		}
		else {		// game is supported so continue
			// Try and retrieve game and its user list
			Game game = server.getGameList().getGame (gameID);
			UserList userList = game.getUserList();

			// Check now that enough users are still available for this game
			if (game.getUserList().size() < serverProperties.getMaxNumOfUsers()) {

				// Check user isn't already logged on in this game.
				if (!userList.containsUser (username)) {

					boolean userDetailsCorrect = false;

					// Ensure that the user is on the user list
					String password = commConnect.getPassword();
					try {
						IServerData userConn = server.getServerData();

						if (password != null)
							userDetailsCorrect = userConn.containsUser(username, password);
						else
							userDetailsCorrect = userConn.containsUser(username);
					} 
					catch (ServerDataException sdEx) {
						conn.send (new CommError (IError.SERVER_DATA_ERROR, sdEx.getMessage()));
					}

					// If everything is OK - initialise this thread, add user
					// and reply back to client
					if (userDetailsCorrect) {
						addNewUser (conn, gameID, username);
					}
					else {
						// Logon error
						if (password == null && serverProperties.isUserValidationPassword())
							conn.send (new CommError (CommError.SERVER_REQUIRES_PASSWORD));
						else
							conn.send (new CommError (CommError.USER_LOGON_INCORRECT));

						conn.stopLoop();
					}
				}
				else {
					// Send "user already connected" error to client and kill thread
					conn.send (new CommError (CommError.USER_ALREADY_CONNECTED));
					conn.stopLoop();
				}
			}
			else {
				// Send "server full" error to client and kill thread
				conn.send (new CommError (CommError.SERVER_FULL));
				conn.stopLoop ();
			}
		}
	}
	/**
	 * Connect a user to a server.
	 *
	 * @param conn
	 * @param message                  Connection object.
	 * @throws TransmissionException   Thrown if there is a transmission error.
	 */
	private void newUserConnect (ServerConnectionThread conn, XMLElement message) throws TransmissionException, ServerDataException {
		ServerDataXML xmlcool = new ServerDataXML();
		// Unpack game connect message
		CommNewUserConnect commConnect = new CommNewUserConnect (message);
		String username = commConnect.getUsername();
		String gameID = commConnect.getGameID();
		String password = commConnect.getPassword();
		// Check server supports this particular game
		if (!server.getGameList().containsGame (gameID)) {

			// Send "game not supported" error to client and kill thread
			conn.send (new CommError (CommError.GAME_NOT_SUPPORTED));
			conn.stopLoop();
		}
		else {	// Everything ok?
			org.jogre.server.data.User Nuevo = new org.jogre.server.data.User(username,password);
			xmlcool.newUser(Nuevo);
		}
	}

	/**
	 * Administrator connection.
	 *
	 * @param conn     Connection to the administrator thread.
	 * @param message  Message to parse.
	 * @throws TransmissionException
	 */
	private void adminConnect (ServerConnectionThread conn, XMLElement message) throws TransmissionException {
		// Unpack game connect message
		CommAdminConnect commConnect = new CommAdminConnect (message);
		String username = commConnect.getUsername();
		String password = commConnect.getPassword();

		// Check an admin isn't already connected (possibly support multiple in future?)
		if (server.getConnections().getAdminConnection() != null) {

			// Send "game not supported" error to client and kill thread
			conn.send (new CommError (CommError.USER_ALREADY_CONNECTED));
			conn.stopLoop ();
		}
		else {
			// Check if the user is an administrator or not
			if (ServerProperties.getInstance().isAdministrator (username, password)) {
				// Initilise thread
				conn.init (username);

				// Set admin connection
				server.getConnections().setAdminConnection (conn);

				// Reply back to the client and send server properties, icons and game state
				conn.transmit (server.getGameList());
				conn.transmit (new CommAdminIconData (server.getGameLoader().getIconDataHash()));
				conn.transmit (ServerProperties.getInstance());				
			}
			else {
				conn.send (new CommError (CommError.USER_LOGON_INCORRECT));
				conn.stopLoop ();
			}
		}
	}

	/**
	 * Set the server properties.
	 * 
	 * This may need updated at some time to do soft reboots etc.
	 *
	 * @param XMLElement
	 */
	private void setServerProperties (ServerConnectionThread conn, XMLElement message) {
		if (conn.isAdministrator()) {
			// Create communication admin properties object
			CommAdminServerProperties commServerProps = new CommAdminServerProperties (message);
			String serverPropsStr = commServerProps.getServerPropertiesStr();

			ServerProperties.setUpFromString (serverPropsStr);
			ServerProperties.getInstance().saveXMLFile ();
		}
	}

	/**
	 * Test the connection.
	 *
	 * @param conn
	 * @param message
	 */
	private void testConnection(ServerConnectionThread conn, XMLElement message) {
		if (conn.isAdministrator()) {
			// Create message and retrieve attributes
			CommAdminTestConnection commTestConn = new CommAdminTestConnection (message);
			String driver   = commTestConn.getDriver();
			String url      = commTestConn.getUrl();
			String username = commTestConn.getUsername();
			String password = commTestConn.getPassword();

			// Test connection
			int status = DBConnection.testConnection (driver, url, username, password, false);

			// Send reply back to user
			CommAdminTestConnection commTestConnReply = new CommAdminTestConnection (status);
			conn.send (commTestConnReply);
		}
	}

	/**
	 * The administrator client is requesting data.
	 * 
	 * This uses String arrays at minute - will be made better in future if required.
	 * 
	 * @param conn
	 * @param message
	 */
	private void adminClientData(ServerConnectionThread conn, XMLElement message) throws ServerDataException {
		if (conn.isAdministrator()) {
			// Create message and retrieve attributes
			CommAdminClientData adminDataReq = new CommAdminClientData (message);
			String [][] data = null;
			String [] params = adminDataReq.getParamData();
			String dataType  = adminDataReq.getDataType();		
			char requestType = adminDataReq.getRequestType();
			
			// Check data type
			if (IServerData.DATA_USERS.equals(dataType)) {			// User

				// Check to see if user is trying to update data first of all (only support for users at min).
				if (params != null) {					
					org.jogre.server.data.User user = new org.jogre.server.data.User ();
					user.setUsername(params[0]);

					if (requestType == CommAdminClientData.NEW) {
						user.setPassword(params[1]);
						server.getServerData().newUser (user);
					}
					else if (requestType == CommAdminClientData.UPDATE) {
						user.setPassword(params[1]);
						server.getServerData().updateUser (user);
					}
					else if (requestType == CommAdminClientData.DELETE) {
						server.getServerData().deleteUser (user);
					}
				}

				// Send list back to user.
				List users = server.getServerData().getUsers();
				data = new String [users.size()][2];
				for (int i = 0; i < users.size(); i++) {
					org.jogre.server.data.User user = (org.jogre.server.data.User)users.get(i);

					// Must be a better way of doing this ... but is it worth it?
					data [i][0] = user.getUsername();
					data [i][1] = user.getPassword();
				}		
				// Create item and return to user.
				CommAdminClientData adminDataReply = new CommAdminClientData (data, dataType);
				conn.send (adminDataReply);
			}			
			else if (IServerData.DATA_GAME_INFO.equals(dataType)) {			// Game info
				List gameInfos;
				gameInfos = server.getServerData().getGameInfos();

				data = new String [gameInfos.size()][7];
				for (int i = 0; i < gameInfos.size(); i++) {
					org.jogre.server.data.GameInfo gameInfo = (org.jogre.server.data.GameInfo)gameInfos.get(i);

					// Populate 2D array
					data [i][0] = gameInfo.getGameKey();
					data [i][1] = gameInfo.getPlayers();
					data [i][2] = gameInfo.getResults();
					data [i][3] = JogreUtils.valueOf (gameInfo.getStartTime(), IJogre.DATE_FORMAT_FULL);
					data [i][4] = JogreUtils.valueOf (gameInfo.getEndTime(), IJogre.DATE_FORMAT_FULL);
					data [i][5] = gameInfo.getGameScore();
					data [i][6] = gameInfo.getGameHistory();
				}
				// Create item and return to user.
				CommAdminClientData adminDataReply = new CommAdminClientData (data, dataType);
				conn.send (adminDataReply);
			}
			else if (IServerData.DATA_GAME_SUMMARY.equals(dataType)) {			// Game summary
				List gameSummaries;
				gameSummaries = server.getServerData().getGameSummarys();

				data = new String [gameSummaries.size()][6];
				for (int i = 0; i < gameSummaries.size(); i++) {
					org.jogre.server.data.GameSummary gameSummary = (org.jogre.server.data.GameSummary)gameSummaries.get(i);

					// Populate 2D array
					data [i][0] = gameSummary.getGameKey();
					data [i][1] = gameSummary.getUsername();
					data [i][2] = String.valueOf(gameSummary.getRating());
					data [i][3] = String.valueOf(gameSummary.getWins());
					data [i][4] = String.valueOf(gameSummary.getLoses());
					data [i][5] = String.valueOf(gameSummary.getDraws());
				}		
				// Create item and return to user.
				CommAdminClientData adminDataReply = new CommAdminClientData (data, dataType);
				conn.send (adminDataReply);	
			}			
		}
	}

	/**
	 * Add a new user to the userlist on the server.  Also create a CommGameConnect
	 * back to the user and broad cast the userlist and table list to ALL users.
	 *
	 * @param conn      Link to the client.
	 * @param gameID    GameID
	 * @param username
	 * @throws TransmissionException
	 */
	private void addNewUser (ServerConnectionThread conn, String gameID, String username) throws TransmissionException, ServerDataException {
		// Initilise thread
		conn.init (username, gameID);

		// Add this thread to the hashmap
		// (enables commuication to multiple clients)
		UserList userList = conn.getUserList ();

		// Add connection for user
		server.getConnections().addConnection (gameID, username, conn);

		// Retrieve game summary details about this user
		GameSummary gs =
			server.getServerData().getGameSummary (gameID, username);
		User user = new User (username, gs.getRating(), gs.getWins(),
				gs.getLoses(), gs.getDraws(), gs.getStreak());
		userList.addUser (user);		// Add user

		// Reply back to the client and send game state
		conn.transmit (conn.getGame());

		// Broadcast this connection to other users at this game
		conn.broadcast (username, user);

		// Send message to administration client if logged on
		conn.sendDataMessageToAdmin (user);

		// Output the message to the console
		logger.log ("Jogre Games Server: client [" + username + "] logged on");

		// Update snapshot
		server.getServerData().updateSnapshot (
				conn.getGameID(), conn.getUserList().size(), conn.getTableList().size());
	}

	/**
	 * Disconnect a user completely from the server (remove from user list and
	 * any instance of the user in the table list).
	 *
	 * @param conn                     Connection back to client
	 * @param commDisconnect           Unparsed disconnect String.
	 * @throws TransmissionException   Thrown if there is a transmission error.
	 */
	private void disconnect (ServerConnectionThread conn, CommDisconnect commDisconnect) throws TransmissionException {
		conn.stopLoop ();
	}

	/**
	 * A client is inviting another user.
	 *
	 * @param conn        Connection to the client
	 * @param commInvite  Invite communication object.
	 * @throws TransmissionException Thrown if there is a transmission error.
	 */
	private void invite (ServerConnectionThread conn, CommInvite commInvite) throws TransmissionException {
		// Make sure that the table is valid.
		Table table = conn.getTableList().getTable(commInvite.getTableNum());
		if (table != null) {
			switch (commInvite.getStatus()) {
			case CommInvite.REQUEST:
				doInvite(conn, commInvite, table);
				break;
			case CommInvite.ACCEPT:
				doAccept(conn, table);
				break;
			case CommInvite.DECLINE:
				doDecline(conn, commInvite, table);
				break;
			}
		}
	}

	/**
	 * A user is inviting another user to a table.
	 *
	 * @param conn        Connection to the client that is sending the invite
	 * @param commInvite  The invitation message from the sender.
	 * @param table       The table the host is inviting the guest to.
	 */
	private void doInvite (ServerConnectionThread conn, CommInvite commInvite, Table table) throws TransmissionException {
		// Get the name of the inviting player and the invited player
		String host = conn.getUsername();
		String guest = commInvite.getUsernameTo();

		// Make sure that the table that is being invited to exists and that
		// the host is a member of it and that the guest is not.
		if ( table.containsPlayer (host) &&
				!table.containsPlayer (guest)) {
			// Send the message to the invitee
			commInvite.setUsername (host);
			conn.transmit (guest, commInvite);

			// Attach an invitation to the table
			table.addInvite(guest);
		}
	}

	/**
	 * A user is accepting an invitation to join a table.
	 *
	 * @param conn        Connection to the client that is accepting.
	 * @param table       The table the guest is accepting to.
	 */
	private void doAccept (ServerConnectionThread conn, Table table) throws TransmissionException {
		// Get the name of the new guest
		String newPlayerName = conn.getUsername();
		User newPlayerUser = conn.getUserList ().getUser (newPlayerName);

		// Make sure that the player had an invite to the table, and that
		// the player was successfully added to the table.
		if ( table.removeInvite (newPlayerName) &&
				table.addPlayer (newPlayerUser)) {

			// Create a response for the new player that includes the entire
			// table state.
			CommJoinTable commJoinTable = new CommJoinTable (newPlayerName, table);
			conn.transmit (newPlayerName, commJoinTable);

			// Send everyone else a notice that the new player joined the table.
			commJoinTable = new CommJoinTable (newPlayerName, table.getTableNum());
			conn.broadcast (newPlayerName, commJoinTable);
			conn.sendDataMessageToAdmin (commJoinTable);
		}
	}

	/**
	 * A user is declining an invitation to join a table.
	 *
	 * @param conn        Connection to the client that is declining.
	 * @param commInvite  The invitation message from the guest.
	 * @param table       The table the guest is declining to join.
	 */
	private void doDecline (ServerConnectionThread conn, CommInvite commInvite, Table table) throws TransmissionException {
		// Get the name of the inviting (host) player and the declining (guest) player
		String host = commInvite.getUsername();
		String guest = conn.getUsername();

		// Make sure that the player had an invite to the table
		if (table.removeInvite (guest)) {
			// Send the message to the host indicating declanation
			commInvite.setUsernameTo (guest);
			conn.transmit (host, commInvite);
		}
	}

	/**
	 * Transmit a message.
	 *
	 * @param conn          Connection to the client.
	 * @param commMessage   Chat communication message.
	 * @throws TransmissionException
	 */
	private void chatGame (ServerConnectionThread conn, CommChatClient commMessage) throws TransmissionException {
		commMessage.setUsername(conn.getUsername());

		conn.broadcast (conn.getUsername(), commMessage);
	}

	/**
	 * Transmit a private chat message.
	 *
	 * @param conn                      Connection to the client.
	 * @param commChatMessage           Chat message.
	 * @throws TransmissionException
	 */
	private void chatPrivate (ServerConnectionThread conn, CommChatPrivate commChatMessage) throws TransmissionException {
		String usernameTo = commChatMessage.getUsernameTo();
		commChatMessage.setUsername(conn.getUsername());	// Client needs to know who sent it
		commChatMessage.setUsernameTo (null);				// Don't need username to

		// Send to client
		conn.transmit (usernameTo, commChatMessage);
	}

	/**
	 * Client is requesting data from the server
	 *
	 * @param conn                      Connection to the client.
	 * @param commRequestData           Request data communications object.
	 * @throws TransmissionException
	 */
	private void requestData (ServerConnectionThread conn, CommRequestData commRequestData) throws TransmissionException {
		// User is requesting data - transmit either Game, TableList or UserList
		String dataType = commRequestData.getDataType();
		if (dataType.equals(Comm.GAME_LIST))
			conn.transmit (server.getGameList());
		else if (dataType.equals(Comm.GAME))
			conn.transmit (conn.getGame());
		else if (dataType.equals(Comm.TABLE_LIST))
			conn.transmit (conn.getTableList());
		else if (dataType.equals(Comm.USER_LIST))
			conn.transmit (conn.getUserList ());
	}

	/**
	 * Transmit a create table message.
	 *
	 * @param conn            Connection to client
	 * @param commNewTable    New table communications object.
	 * @throws TransmissionException
	 */
	private void createTable (ServerConnectionThread conn, CommNewTable commNewTable) throws TransmissionException, ServerDataException {
		TableList tableList = conn.getTableList ();

		// Make sure that this player is allowed to start a new table.
		String userName = conn.getUsername();
		ServerProperties sProps = ServerProperties.getInstance();

		if (tableList.size() >= sProps.getMaxNumOfTables()) {
			conn.send (new CommError (CommError.SERVER_TABLE_LIMIT_EXCEEDED));
			return;
		}

		if (tableList.getNumOfTablesUserOwns (userName) >=
			sProps.getMaxNumOfTablesPerUser()) {
			conn.send (new CommError (CommError.PLAYER_TABLE_LIMIT_EXCEEDED));
			return;
		}

		User ownerUser = conn.getUserList().getUser (userName);
		// Create the new table
		Table table = tableList.addTable (ownerUser,
				commNewTable.isPublic(),
				commNewTable.getProperties());

		// Send this new table to all connected users and admin client if logged on
		conn.broadcast (table);
		conn.sendDataMessageToAdmin (table);

		// Update snapshot
		server.getServerData().updateSnapshot (conn.getGameID(),
				conn.getUserList().size(),
				tableList.size());
	}
}