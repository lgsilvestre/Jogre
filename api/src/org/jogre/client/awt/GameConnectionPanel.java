/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.client.awt;

import java.net.Socket;

import nanoxml.XMLElement;

import org.jogre.client.ClientConnectionThread;
import org.jogre.common.Game;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommError;
import org.jogre.common.comm.CommGameConnect;
import org.jogre.common.comm.CommNewUserConnect;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreLabels;

/**
 * Connection panel for games to connect.
 * 
 * @author Bob
 * @version Beta 0.3
 */
public class GameConnectionPanel extends ConnectionPanel {

	/** Link the GUI. */
	private IJogreGUI client;
	/** Link to the client connection thread for the game. */
	private ClientConnectionThread conn = null;
	
	/**
     * Constructor for an application which doesn't take a
     * username / password.
     *
     * @param client   Link to frame / applet.
     */
    public GameConnectionPanel (IJogreGUI client) {
        super ();
        
        this.client = client;
    }

    /**
     * Silent connect which takes a server, port, username and password.
     *
     * @param client    Link to frame / applet.
     * @param server    Name of the server.
     * @param port      Server port.
     * @param username  Username of person trying to connect.
     * @param password  Password of the person trying to connect.
     */
    public GameConnectionPanel (IJogreGUI client,
                                String server,
                                int    port,
                                String username,
                                String password) 
    {
        super (server, port, username, password);
        
        this.client = client;
    }
	/**
	 * Override the connect method for handling game traffic.
	 * 
	 * @see org.jogre.client.awt.ConnectionPanel#connect(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	protected void connect (Socket socket, 
			                String username,
			                String password) 
	{
		// Create a new connection thread to handle communication
        this.conn = new ClientConnectionThread (socket, username, this);
        conn.start();       // Start the thread

        // Create a connect message.
        CommGameConnect commConnect;
        if (password.equals (""))
            commConnect = new CommGameConnect
                (username, GameProperties.getGameID());
        else
            commConnect = new CommGameConnect
                (username, password, GameProperties.getGameID());
                    
        // Send connect message to the server.
        conn.send (commConnect);

	}
//	// Connect with Integer (NewUser) a=0 => no newUser
//	// with a=0 does similar to connect() but with a CommNewUserConnect Object
//	protected void connect (int a,Socket socket, 
//	            String username,
//	            String password) 
//	{
//		if (a==0) connect(socket,username,password);
//		else{
//			// Create a new connection thread to handle communication
//	        this.conn = new ClientConnectionThread (socket, username, this);
//	        conn.start();       // Start the thread
//
//	        // Create a connect message.
//	        CommNewUserConnect commConnect;
//	        if (password.equals (""))
//	            commConnect = new CommNewUserConnect
//	                (username, GameProperties.getGameID());
//	        else
//	            commConnect = new CommNewUserConnect
//	                (username, password, GameProperties.getGameID());
//	                    
//	        // Send connect message to the server.
//	        conn.send (commConnect);
//		}
//	}
    /**
     * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
     */
    public void receiveGameMessage (XMLElement message) {
        String messageType = message.getName();

        // Message could be
        //     <error> - Error message.
        //     <game>  - Game message (success)
        if (messageType.equals (Comm.ERROR)) {
            // Create comm object and create error description
            CommError commError = new CommError (message);
            String errorStr = JogreLabels.getError (commError.getStatus());

            // Update label
            statusLabel.setText (errorStr);
        }
        else if (messageType.equals (Comm.GAME)) {
            try {
                // Create game object on connection
                conn.setGame (new Game (message));

                // User is sucessfully connected so set game object and return object.
                client.connectOK (conn);
            }
            catch (TransmissionException tEx) {}
        }
    }

    public void receiveTableMessage (XMLElement message, int tableNum) {}	// does nothing
}
