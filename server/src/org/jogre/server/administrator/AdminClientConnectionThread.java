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
package org.jogre.server.administrator;

import java.net.Socket;

import nanoxml.XMLElement;

import org.jogre.client.IClient;
import org.jogre.common.AbstractConnectionThread;
import org.jogre.common.GameList;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommAdminMessage;
import org.jogre.common.comm.CommGameMessage;
import org.jogre.common.comm.CommTableMessage;

/**
 * Class for listening to data coming from the ServerConnectionThread 
 * to the Jogre Server Administrator.
 * 
 * @author Bob Marks 
 * @version Beta 0.3
 */
public class AdminClientConnectionThread extends AbstractConnectionThread {

	/** Interface betweem this thread and the Frame */
	protected IClient clientInterface;
	
	private ClientCommDataReceiverList dataReceiver = null;

	/**
	 * Default constructor which takes a Socket connection to the server, a
	 * username and an IClient which sits between this class and the GUI.
	 *
	 * @param connection       Socket connection to server.
	 * @param username         Username
	 * @param clientInterface  Interface between this class and GUI.
	 */
	public AdminClientConnectionThread (Socket connection, String username, IClient clientInterface) {
		super (connection);

		this.username = username;
		this.clientInterface = clientInterface;
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
		// Receive data messages and update tree 
		if (Comm.ADMIN_DATA_MESSAGE.equals(message.getName())) {
			CommAdminMessage adminMessage = new CommAdminMessage (message);
			
			if (dataReceiver != null) {
				String gameID   = adminMessage.getGameID();
				String username = adminMessage.getUsername();
				XMLElement subMessage = adminMessage.getMessage();
				
				String sTableNum = subMessage.getStringAttribute(CommTableMessage.XML_ATT_TABLE_NUM);
		    	if (sTableNum == null) 			
					this.dataReceiver.receiveGameMessage  (adminMessage.getMessage(), gameID, username);
				else
					this.dataReceiver.receiveTableMessage (adminMessage.getMessage(), gameID, username, Integer.parseInt(sTableNum));
			}
		} 
		
		// Delegate ALL message to GUI interface
		String sTableNum = message.getStringAttribute (CommTableMessage.XML_ATT_TABLE_NUM);
		if (sTableNum == null) 			
			this.clientInterface.receiveGameMessage (message);
		else
			this.clientInterface.receiveTableMessage (message, Integer.parseInt (sTableNum));
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
	 * Convience Delegate method to return the game list object which 
	 * should be mirrored to that on the jogre server.
	 * 
	 * @return       Game list object consisting of user and table lists.
	 */
	public GameList getGameList () {
		return JogreServerAdministrator.getInstance().getGameList();
	}

	/**
	 * Set the game list object from the server.
	 * 
	 * @param gamelist   Game list object.
	 */
	public void setGameList (GameList gamelist) {
		JogreServerAdministrator.getInstance().setGameList(gamelist);
		
		this.dataReceiver = new ClientCommDataReceiverList (gamelist); 
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