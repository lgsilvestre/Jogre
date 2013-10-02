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
package org.jogre.server.administrator;

import java.net.Socket;

import nanoxml.XMLElement;

import org.jogre.client.awt.ConnectionPanel;
import org.jogre.common.GameList;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommAdminConnect;
import org.jogre.common.comm.CommAdminIconData;
import org.jogre.server.ServerProperties;

/**
 * Connection panel for the server manager to connect to a server.
 * 
 * @author Bob
 * @version Beta 0.3
 */
public class AdminConnectionPanel extends ConnectionPanel {

	/** Link the GUI. */
	private JogreServerAdministrator serverManager;
	
	/** Link to the client connection thread for the game. */
	private AdminClientConnectionThread conn = null;
	
	/**
     * Constructor for an application which doesn't take a
     * username / password.
     *
     * @param client   Link to frame / applet.
     */
    public AdminConnectionPanel (JogreServerAdministrator serverManager) {
    	super ();
    	
    	this.serverManager = serverManager;
    }

	/**
	 * Override the connect method for handling traffic.
	 * 
	 * @see org.jogre.client.awt.ConnectionPanel#connect(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	protected void connect (Socket socket, 
			                String username,
			                String password) 
	{
		// Create a new connection thread to handle communication
        this.conn = new AdminClientConnectionThread (socket, username, this);
        conn.start();       // Start the thread

        // Create a connect message.
        CommAdminConnect commConnect = new CommAdminConnect (username, password);
                    
        // Send connect message to the server.
        conn.send (commConnect);
	}

    /**
     * Receive game message.
     * 
     * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
     */
    public void receiveGameMessage (XMLElement message) {
        String messageType = message.getName();

        if (messageType.equals(Comm.GAME_LIST)) {
        	try {
		    	GameList gameList = new GameList (message);
		    	conn.setGameList (gameList);
        	}
            catch (TransmissionException transEx) {}
        } else if (messageType.equals(Comm.ADMIN_ICON_DATA)) {
        	CommAdminIconData commIconData = new CommAdminIconData (message);
        	serverManager.setIconData(commIconData.getIconData());
        }        
        else if (messageType.equals(Comm.ADMIN_SERVER_PROPERTIES)) {
        	// User is sucessfully connected so set game object and return object.
        	ServerProperties.setUpFromString(message.getContent());
            
        	// Administration client should be good to go
            serverManager.connectOK (conn);
        } else {
        	super.receiveGameMessage (message);
    	}
    }
    public void receiveTableMessage (XMLElement message, int tableNum) {}	// does nothing
}