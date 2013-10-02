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
import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jogre.client.ClientConnectionThread;
import org.jogre.common.comm.CommChatClient;
import org.jogre.common.comm.CommChatPrivate;
import org.jogre.common.util.JogreLabels;

/**
 * Little message component which can be used in several ways.  These include
 * boardcast message (all clients recieve messages), room messages (only users
 * at a room/table) will recieve it and private message between 2 people.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ChatGameComponent extends JogrePanel {
	// Declare GUI elements

    /** Main message ouput box. */
	protected JTextArea messageOutput;

	/** Small message input box. */
	protected JTextField messageInput;

	private JogreScrollPane scrolledMessageOutput; // Scroll pane
	private JogreButton sendButton;				   // send button

	// Declare connection
	private ClientConnectionThread conn;

	/** Sending username. */
	protected String usernameFrom = null;

	/** Receiving username. */
	protected String usernameTo = null;

	/**
	 * Constructor for a message component.
	 *
	 * @param height   Hieght of the message component in rows.
	 */
	public ChatGameComponent (ClientConnectionThread conn, int height) {
        this.conn = conn;
        this.usernameFrom = conn.getUsername();

		// Create message output, input and send button components
		this.messageOutput = new JTextArea (height, height);
		this.messageOutput.setEditable(false);
		this.messageOutput.setLineWrap(true);
		this.messageOutput.setWrapStyleWord (true);
		this.scrolledMessageOutput = new JogreScrollPane (this.messageOutput);

		this.messageInput = new JTextField ();
		this.sendButton = new JogreButton (JogreLabels.getInstance().get("send"));

		// Create a panel for the button components
		double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
		double [][] sizes = {{fill}, {fill, pref}};
		setLayout (new TableLayout (sizes));
		sizes = new double [][] {{TableLayout.FILL, pref},{pref}};
		JPanel bottomPanel = new JPanel (new TableLayout (sizes));

		// Add components to this panel
		bottomPanel.add (this.messageInput, "0,0");
		bottomPanel.add (this.sendButton, "1,0");
		add (this.scrolledMessageOutput, "0,0");
		add (bottomPanel, "0,1");

		// Add title to the component
		setBorder(
			BorderFactory.createTitledBorder (
				BorderFactory.createEtchedBorder(),
				JogreLabels.getInstance().get("chat")
			)
		);

		// Add listener to the send event
		this.sendButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					sendMessage ();
				}
			}
		);

		this.messageInput.addKeyListener(
			new KeyAdapter () {
				// respond to the enter key
			    public void keyPressed(KeyEvent keyevent) {
			    	if (keyevent.getKeyCode() == 10) {
			    		sendMessage ();
			    	}
			    }
			}
		);
	}

	/**
	 * Sets the enabled status of component and message input box.
	 *
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
	    this.sendButton.setEnabled (enabled);
	    this.messageInput.setEditable (enabled);
	}

	/**
	 * Set the username to of this component (not required if the component
	 * is in broadcast mode).
	 *
	 * @param usernameTo   Username to send message to.
	 */
	public void setUsernameTo (String usernameTo) {
		this.usernameTo = usernameTo;
	}

	/**
	 * Send a message.  Message can be a private message or a braodcast message.
	 */
	protected void sendMessage () {
		String messageText = this.messageInput.getText();

        boolean a = !messageText.equals("");
		if (this.conn != null && !messageText.equals("")) {
		    this.messageOutput.append (this.usernameFrom + ": " + messageText + "\n");

			// Send to a user
			CommChatClient chatMessage = null;
			if (this.usernameTo != null) {
			    chatMessage = new CommChatPrivate (messageText, this.usernameTo);
			}
			// Else broad cast to everyone!
			else {
			    chatMessage = new CommChatClient (messageText);
			}

			// Send message and blank the message input box.
			this.conn.send (chatMessage);
			this.messageInput.setText("");
		}
	}

	/**
	 * Receives a message from the server.
	 *
	 * @param messageFrom   Who the message is from.
	 * @param message       Text of the message.
	 */
	public void receiveMessage (String messageFrom, String message) {
	    this.messageOutput.append (messageFrom + ": " + message + "\n");
	    this.messageOutput.setCaretPosition (this.messageOutput.getText().length());
	}
}
