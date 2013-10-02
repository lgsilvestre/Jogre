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
package org.jogre.client;

import java.net.Socket;

import nanoxml.XMLElement;

import org.jogre.common.AbstractConnectionThread;
import org.jogre.common.ClientCommDataReceiver;
import org.jogre.common.Game;
import org.jogre.common.TableList;
import org.jogre.common.UserList;
import org.jogre.common.comm.CommGameMessage;
import org.jogre.common.comm.CommTableMessage;

/**
 * <p>Client connection which is spawned from the client machine and receives/
 * sends communication to/from the server.  This class also holds a mirrored
 * copy of the TableList and UserList objects which are stored on the
 * JogreServer.</p>
 *
 * <p>The communication between this connection thread and the GUI (e.g.
 * an implementation of the JogreClientFrame) must goes through
 * an interface IClient to ensure maximum abstraction.</p>
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ClientConnectionThread extends AbstractConnectionThread {

	/** Interface betweem this thread and the Frame */
	protected IClient clientInterface;

	/** Current game ID. */
	protected String gameID = null;
	
	/** Game associated with this connection thread (will be 
	 *  mirrored to that on the server. */
	protected Game game = null;

	/** Link to data communication receiver. */
	private ClientCommDataReceiver commDataReceiver = null;
	
	/**
	 * Default constructor which takes a Socket connection to the server, a
	 * username and an IClient which sits between this class and the GUI.
	 *
	 * @param connection       Socket connection to server.
	 * @param username         Username
	 * @param clientInterface  Interface between this class and GUI.
	 */
	public ClientConnectionThread (Socket connection, String username, IClient clientInterface) {
		super (connection);

		this.username = username;
		this.clientInterface = clientInterface;	
		this.game = null; 
	}
	
	/**
	 * Set the client interface.
	 * 
	 * @param clientInterface  Client interface
	 */
	public void setClientInterface (IClient clientInterface) {
		this.clientInterface = clientInterface;		
	}

	/**
	 * Parse method which reads the first token of the message and delegate
	 * to the implementating client.
	 *
	 * @see org.jogre.common.AbstractConnectionThread#parse(nanoxml.XMLElement)
	 */
	public void parse (XMLElement message) {
		// Update data first of all		
		String sTableNum = message.getStringAttribute (CommTableMessage.XML_ATT_TABLE_NUM); 
			
		// If message contains a "table" attribute then delegate to a table message
		if (sTableNum == null) {		
			
			// Update data tree first
			if (commDataReceiver != null)
				commDataReceiver.receiveGameMessage (message, username);
			
			// Delegate to client interface for further processing
		    this.clientInterface.receiveGameMessage (message);
		}
		else {
			int tableNum = Integer.parseInt(sTableNum);
			
			// Update data tree first
			if (commDataReceiver != null)
				commDataReceiver.receiveTableMessage (message, username, tableNum);
				
		    this.clientInterface.receiveTableMessage (message, tableNum);
		}
	}

	/**
	 * Send a ITransmittable object from a client to the server.
	 *
	 * @param message
	 */
	public void send (CommGameMessage message) {
		super.send (message);
	}

	/**
	 * Return the user list (should be the same as the server user
	 * list object).
	 *
	 * @return   List of users.
	 */
	public UserList getUserList () {
		return this.game.getUserList();
	}

	/**
	 * Update the user list.  
	 * 
	 * @param userList   List of users
	 */
	public void setUserList (UserList userList) {
		this.game.setUserList (userList);
	}

	/**
	 * Return the gameID.
	 * 
	 * @return  Game ID.
	 */
	public String getGameID () {
		return this.gameID;
	}
	
	/**
	 * Return the game object which should be mirrored to that 
	 * on the server.
	 * 
	 * @return       Game object consisting of user and table lists.
	 */
	public Game getGame () {
	    return this.game;
	}
	
	/**
	 * Set the game object from the server.
	 * 
	 * @param game
	 */
	public void setGame (Game game) {
	   this.game = game;
	   
	   this.commDataReceiver = new ClientCommDataReceiver (game);
	}
	
	/**
	 * Return the table list object (should be the same as the server table
	 * list).
	 *
	 * @return  TableList object (contains number of Table objects).
	 */
	public TableList getTableList () {
		return this.game.getTableList();
	}

	/**
	 * Update the table list.
	 *
	 * @param tableList   TableList object containing Table objects.
	 */
	public void setTableList (TableList tableList) {
		this.game.setTableList (tableList);
	}

	/**
	 * Stop the thread.
	 */
	protected void disconnect () {
		super.loop = false;			// End thread
	}

	/**
	 * Client has exitted so clean everything up.
	 *
	 * @see org.jogre.common.AbstractConnectionThread#cleanup()
	 */
	public void cleanup () {
	    // Clean up
	}
}
