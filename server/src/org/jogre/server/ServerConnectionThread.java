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
package org.jogre.server;

import java.net.Socket;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.AbstractConnectionThread;
import org.jogre.common.Game;
import org.jogre.common.IJogre;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.TransmissionException;
import org.jogre.common.UserList;
import org.jogre.common.comm.CommAdminMessage;
import org.jogre.common.comm.CommAdminGameMessage;
import org.jogre.common.comm.CommDisconnect;
import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.comm.ITransmittable;
import org.jogre.common.playerstate.PlayerStateGameStarted;
import org.jogre.common.util.JogreLogger;
import org.jogre.server.controllers.ServerControllerList;
import org.jogre.server.data.ServerDataException;

/**
 * <p>Every time a client connections to a JogreServer this class (which extends
 * Thread) is run.  This ensures that a JogreServer is multi-threaded.</p>
 *
 * @author  Bob Marks
 * @version Beta 0.3
 * @see org.jogre.server.JogreServer
 */
public class ServerConnectionThread extends AbstractConnectionThread 
                                    implements IJogre {

	/** Logging */
	JogreLogger logger = new JogreLogger (this.getClass());

	/** link to the JogreServer */
	protected JogreServer server;

	/** Link to the various parsers. */
	private ServerControllerList controllers = null;

	/** Game ID which is set to a particular game, master server or admin. */
	private String gameID = null;

	/** Convience link to the userlist. */
	private UserList userList = null;

	/** Convience link to the tablelist. */
	private TableList tableList = null;

	/** Convience link to the game. */
	private Game game = null;

	/** Connection list. */
	private ConnectionList connections = null;
	
	private boolean isAdministrator = false;
	
	/**
	 * Constructor which takes a socket connection and a link to the server.
	 *
	 * @param clientSocket      Client connection.
	 */
	public ServerConnectionThread (Socket clientSocket) {
		super (clientSocket);

		// Get link to the server (singleton) and the various parsers
		this.server       = JogreServer.getInstance();
		this.controllers  = server.getControllers();

		// and also set up links to server objects which are often used.
		this.connections = server.getConnections();	 // link to connections
	}

	/**
	 * Initilise this server connection thread with a username and a
	 * game key.
	 *
	 * @param username  Username of the client connecting to this thread.
	 * @param gameID    Gamekey of the client connecting.
	 */
	public void init (String username, String gameID) {
		this.username = username;
		this.gameID = gameID;

		// Set convience fields
		this.game = server.getGameList().getGame (gameID);
		this.userList = game.getUserList();	     // link to userlist
		this.tableList = game.getTableList();	 // link to table list
	}
	
	/**
	 * Initilise this server connection thread for an administrator user.
	 * 
	 * @param admin_username   Administrator username.
	 */
	public void init (String admin_username) {
		this.username        = admin_username;
		this.gameID          = ADMINISTRATOR;
		this.isAdministrator = true;
	}

	/**
	 * Implementation of the parse method on the server side.  This method
	 * recieves XML communication from a client.
	 *
	 * @see org.jogre.common.AbstractConnectionThread#parse (XMLElement element)
	 */
	public void parse (XMLElement message) throws TransmissionException {
	    if (message != null) {
	    	// Retrieve table number 
	    	String sTableNum = message.getStringAttribute (CommTableMessage.XML_ATT_TABLE_NUM); 
			
			// If message contains a "table" attribute then delegate to a table message
			if (sTableNum == null) {				
				// Parse game message
	            controllers.getGameController().parseGameMessage (this, message);
			}
	        else {
	            int tableNum = Integer.parseInt (sTableNum);
	            controllers.getTableController().parseTableMessage (this, message, tableNum);
	        }
	        
	        // Send a copy to the administrator thread if it is listening.
			sendGameMessageToAdmin (message, true);
	    }
	}

	/**
	 * Send a ITransmittable object to the output stream (could be server or
	 * client).
	 *
	 * @param transObject
	 * @param sendCopyToAdmin   If this is true then a copy is sent to the administrator.
	 */
	protected void send (ITransmittable transObject, boolean sendCopyToAdmin) {
		super.send (transObject);
		
		// Send to administrator if the administrator is listening.
		if (sendCopyToAdmin) 			
			sendGameMessageToAdmin (transObject.flatten(), false);
	}	
	
	/**
	 * Send game message to administration connection.  The game message will 
	 * be wrapped in in a CommAdminGameMessage.
	 * 
	 * @param message             Game message as an XML message.
	 * @param isReceivingMessage  If true then going to 
	 */
	public void sendGameMessageToAdmin (XMLElement message, boolean isReceivingMessage) {
		// Wrap the message inside an <admin_message> which details
		// additional info about the message such as which user thread has sent it
		// and what game.
		ServerConnectionThread conn = connections.getAdminConnection();
		
		if (conn != null) {		// ensure we're not sending same message twice
			CommAdminGameMessage adminMessage = new CommAdminGameMessage (isReceivingMessage, gameID, username, message); 
			conn.send (adminMessage, false);			
		}
	}
	
	/**
	 * Send data update message to administration connection.  The update message
	 * will be wrapped in a CommAdminDataMessage object.
	 * 
	 * @param message
	 * @param isReceivingMessage
	 */
	public void sendDataMessageToAdmin (ITransmittable message) {
		ServerConnectionThread conn = connections.getAdminConnection();
		
		if (conn != null) {		// ensure we're not sending same message twice
			CommAdminMessage adminMessage = new CommAdminMessage (gameID, username, message.flatten());
			conn.send (adminMessage, false);
		}
	}
	
	/**
	 * Convience method to send a message - assumes that message is also being
	 * sent to the admin client.
	 * 
	 * @see org.jogre.common.AbstractConnectionThread#send(org.jogre.common.comm.ITransmittable)
	 */
	public void send (ITransmittable transObject) {
		send (transObject, true);
	}
		
	/**
	 * Return the game object currently tied to this connection thread..
	 *
	 * @return   Game object (which contains the userlist and table list).
	 */
	public Game getGame () {
	    return game;
	}

	/**
	 * Return the gameID.
	 *
	 * @return    Game ID (e.g. chess-0.2).
	 */
	public String getGameID () {
		return this.gameID;
	}

	/**
	 * Return the user list for this game.
	 * 
	 * @return   Userlist.
	 */
	public UserList getUserList () {
		return this.userList;
	}

	/**
	 * Return the table list.
	 *
	 * @return
	 */
	public TableList getTableList () {
	    return this.tableList;
	}
	
	/**
	 * Return true/false if this user is administrator.
	 * 
	 * @return  True if user is an administrator.    
	 */
	public boolean isAdministrator () {
		return this.isAdministrator;
	}

	/**
	 * Client has exitted so clean everything up.
	 *
	 * @see org.jogre.common.AbstractConnectionThread#cleanup()
	 */
	public void cleanup () {
		logger.debug ("cleanup", "Cleaning up user details");

		// Remove connection from the connection list (game & admin users)
		if (isAdministrator ())
			server.getConnections().removeAdminConnection ();
		else 
			server.getConnections().removeConnection (gameID, username);
		
		// Game users only
		if (userList != null && tableList != null) {
		    logger.debug ("cleanup", "remove user");
		    userList.removeUser (username);

			// remove any instance of the user from the table list
			logger.debug ("cleanup", "removing table");
		    tableList.removeUserFromTables (username);

			// Inform all connected clients that user has disconnected from game
			CommDisconnect commDisconnect = new CommDisconnect (username);
			broadcast (commDisconnect);
			sendDataMessageToAdmin (commDisconnect);

			// Log message
			logger.log ("Jogre Games Server: client [" + username + "] has logged off");

		    // Update snapshot
		    try {
				server.getServerData().updateSnapshot (
					getGameID(), getUserList().size(), getTableList().size());
			} catch (ServerDataException e) {				
				e.printStackTrace();			// proper logging at some stage
			}
		}
	}

	/**
	 * Transmit a message to the specified table and specified player.
	 *
	 * @param usernameTo Username to transmit to
	 * @param tableNum Table number to transmit to
	 * @param transObject Transmittable object
	 */
	public void transmitToTablePlayer (String usernameTo, int tableNum, ITransmittable transObject) {
		if (transObject instanceof CommTableMessage)
		    ((CommTableMessage)transObject).setTableNum (tableNum);

		// Insure that that user is actually in this table
		Table table = tableList.getTable(tableNum);
		if (table != null) {
			PlayerList playerList = table.getPlayerList();
			Player player = playerList.getPlayer(usernameTo);
			if (player != null)
				transmit (usernameTo, transObject);
		}
	}

	/**
	 * Transmit a message to the specified table.
	 *
	 * @param tableNum
	 * @param transObject
	 */
	public void transmitToTablePlayers (int tableNum, ITransmittable transObject) {
		if (transObject instanceof CommTableMessage)
		    ((CommTableMessage)transObject).setTableNum (tableNum);

		Table table = tableList.getTable(tableNum);
		if (table != null) {
			Vector players = table.getPlayerList().getPlayers();

			for (int i = 0; i < players.size(); i++) {
				Player player = (Player)players.get(i);
				String username = player.getPlayerName();
				transmit (username, transObject);
			}
		}
	}

	/**
	 * Transmit a messgae to a table but omit a user (this user will generally
	 * be the player who has created the message in the first place).
	 *
	 * @param omitUser     Username to omit from the list (usually the sender).
	 * @param tableNum     Table number to transmit message to.
	 * @param transObject  Object which implements the ITransmittable interface.
	 */
	public void transmitToTablePlayers (String omitUser, int tableNum, ITransmittable transObject) {
	    transmitToTablePlayers (omitUser, tableNum, transObject, false);
	}

	/**
	 * Overloaded version which only transmits to players in a game in progress i.e. transmits
	 * to players whose state is "PlayerStateGameStarted".
	 *
	 * @param omitUser       Username to omit from the list (usually the sender).
	 * @param tableNum       Table number to transmit message to.
	 * @param transObject    Object which implements the ITransmittable interface.
	 * @param gameInProgress If true only transmits to players if they are playing.
	 */
	public void transmitToTablePlayers (String omitUser, int tableNum, ITransmittable transObject, boolean gameInProgress) {
		if (transObject instanceof CommTableMessage)
		    ((CommTableMessage)transObject).setTableNum (tableNum);

	    Table table = tableList.getTable(tableNum);
		if (table != null) {
		    Vector players = table.getPlayerList().getPlayers();

		    // Loop throught the various players and transmit the message
			for (int i = 0; i < players.size(); i++) {
				Player player = (Player)players.get(i);
				String username = player.getPlayerName();

				if (!username.equals(omitUser)) {
				    if (gameInProgress) {
				        if (player.getState() instanceof PlayerStateGameStarted)
				            transmit (username, transObject);
				    }
				    else
				        transmit (username, transObject);
				}
			}
		}
	}

	/**
	 * Broadcast a transmittable object to all the clients.
	 *
	 * @param transmittableObject
	 */
	public void broadcast (ITransmittable transmittableObject) {
		// retrieve all the clients
		Vector users = userList.getUsers();
		for (int i = 0; i < users.size(); i++) {
			transmit((String)users.get(i), transmittableObject);
		}
	}

	/**
	 * Broadcast a transmittable object to all the clients.
	 *
	 * @param omitUser             Omit this user (usually the person who sent the message).
	 * @param transmittableObject  Object to transmit.
	 */
	public void broadcast (String omitUser, ITransmittable transmittableObject) {
		// retrieve all the clients
		Vector users = userList.getUsers();
		for (int i = 0; i < users.size(); i++) {
			String currentUsername = (String)users.get(i);
			if (!omitUser.equals(currentUsername))
				transmit(currentUsername, transmittableObject);
		}
	}

	/**
	 * Overloaded version which takes a CommGameMessage object.
	 *
	 * @param username     Username to send message to.
	 * @param transObject  Transmittable object.
	 */
	public void transmit (String username, ITransmittable transObject) {
		ServerConnectionThread conn = connections.getServerConnectionThread (getGameID(), username);

		// Send the object to the user
		conn.send (transObject);
	}

	/**
	 * Send a ITransmittable object to user connected to this thread only.
	 *
	 * @param transObject   Transmittable object.
	 */
	public void transmit (ITransmittable transObject) {
	    // Send the object to the user
		send (transObject);
	}

	/**
	 * This metohd returns a server controller associated with this
	 * server connection thread.
	 *
	 * @return   Server connection.
	 */
	public ServerController getServerController () {
		return JogreServer.getInstance().getControllers().getCustomController (gameID);
	}
}