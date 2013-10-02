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
package org.jogre.client.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.jogre.client.ClientConnectionThread;
import org.jogre.common.util.JogreLabels;

/**
 * This is used to send a private message between two users.  Assumes that a
 * socket to the server has already been established.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class ChatPrivateDialog extends JogreDialog {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 150;

	private ChatGameComponent messageComponent;

	/**
	 * Constructor for loading a private message in an application.
	 *
	 * @param owner         Parent frame of this chat dialog.
	 * @param usernameTo    Username to who will be receiving the chat.
	 * @param conn          Connection to the server.
	 */
	public ChatPrivateDialog (JFrame owner,
			                  String usernameTo,
			                  ClientConnectionThread conn)
	{
		super (owner);

		setUpGUI (usernameTo, conn);
	}

	/**
	 * Constructor for loading a private message in an applet.
	 *
	 * @param usernameTo   Name of the username user user is talking to.
	 * @param conn         Connection to server.
	 */
	public ChatPrivateDialog (String usernameTo,
			                  ClientConnectionThread conn)
	{
		super ();

		setUpGUI (usernameTo, conn);
	}

	/**
	 * Set up the GUI for the username and the connection.
	 *
	 * @param usernameTo   Name of the username user user is talking to.
	 * @param conn         Connection to server.
	 */
	private void setUpGUI (String usernameTo,
			               ClientConnectionThread conn) {
		setTitle (JogreLabels.getInstance().get("private.message") +
                  " (" + usernameTo + ")");

		// Create message component
		this.messageComponent = new ChatGameComponent (conn, 6);
		this.messageComponent.setUsernameTo (usernameTo);

		// Set up GUI
		JogrePanel panel = new JogrePanel (new BorderLayout ());
		panel.add (this.messageComponent, BorderLayout.CENTER);

		getContentPane().add (panel);
		setSize (new Dimension (DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setLocationRelativeTo (this);
		setVisible (true);
	}

	/**
	 * Recieve private message.
	 *
	 * @param usernameFrom   Username of other user.
	 * @param chatMessage    Chat of other user.
	 */
	public void recieveMessage (String usernameFrom, String chatMessage) {
	    this.messageComponent.receiveMessage (usernameFrom, chatMessage);
	}
}