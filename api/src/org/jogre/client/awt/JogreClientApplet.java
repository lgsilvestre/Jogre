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

import java.awt.Frame;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import nanoxml.XMLElement;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.IClient;
import org.jogre.client.TableConnectionThread;
import org.jogre.common.IJogre;
import org.jogre.common.JogreGlobals;
import org.jogre.common.User;
import org.jogre.common.Table;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.JogreUtils;

/**
 * Abstract JOGRE applet class.  This must be extended to create a
 * Swing applet.
 *
 * @author starsinthesky
 * @version Beta 0.3
 */
public abstract class JogreClientApplet extends JApplet
                                        implements IClient,    // communication
                                                   Observer,   // Updates GUI
                                                   IJogreClientGUI, // GUI link
                                                   IJogre      // Constants
{

	private JogreClientPanel jogreClientPanel = null;

	private GameConnectionPanel  connectionPanel = null;

	// Indicates if this game should display extended information in the table list.
	private boolean hasExtendedInfo;

	/**
	 * This must be overwritten to get a handle on the correct table frame.
	 *
	 * @param conn    Connection to server.
	 * @return        Return the correct table frame.
	 */
	public abstract JogreTableFrame getJogreTableFrame (TableConnectionThread conn);

	/**
	 * Constructor for a client applet.
	 */
	public JogreClientApplet (boolean hasExtInfo) {
		getRootPane().putClientProperty("defeatSystemEventQueueCheck",
		                                Boolean.TRUE);

		// Save parameter
		this.hasExtendedInfo = hasExtInfo;

		// Take a note that this is an applet.
		JogreUtils.setApplet (true);
	}

	/**
	 * Constructor for a client applet.
	 */
	public JogreClientApplet () {
		this (false);
	}

	/**
	 * Initilise the applet.
	 *
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		// Load images (in own thread)
		GameImages.loadImages();

		// Set the locale for international language support
		String localeString = getParameter (APPLET_PARAM_LOCALE);
		if (localeString != null)
			JogreGlobals.setLocale (localeString);

		// Try and connect to the server (silent connection)
		String username = getParameter (APPLET_PARAM_USERNAME);
		String password = getParameter (APPLET_PARAM_PASSWORD);
		String server   = getParameter (APPLET_PARAM_SERVER_HOST);
		int    port     = Integer.parseInt (getParameter (APPLET_PARAM_SERVER_PORT));

		if (password == null) {
			password = "";
		}

		// Create the connection panel.
		connectionPanel = new GameConnectionPanel (this, server, port, username, password);

		// Applets connect immediately
		connectionPanel.connect();

		getContentPane().add (connectionPanel);

		this.setVisible (true);
	}

	/**
	 * Method which is called when a client connects
	 * successfully.
	 *
	 * @param conn   Client connection thread.
	 */
	public void connectOK (ClientConnectionThread conn) {
		// Update client
		conn.setClientInterface (this);

		// Create new jogre client frame
		jogreClientPanel = new JogreClientPanel (conn, this);
		jogreClientPanel.setUpGUI ();
        jogreClientPanel.addListeners();

		while (connectionPanel == null) {
			System.err.println(JogreLabels.getInstance().get("socket.thread.waits.for.gui"));
			try {
				Thread.sleep (100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Remove logon panel and add client panel.
		getContentPane().remove (connectionPanel);
		getContentPane().add    (jogreClientPanel);
		jogreClientPanel.revalidate();
	}

	/**
	 * Method for setting a title.  In an applet this is empty.
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#setUITitle(java.lang.String)
	 */
	public void setUITitle (String title) {}

	/**
	 * Method for returning the correct chat dialog.  An applet doesn't
	 * need a parent Frame.
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#getChatPrivateDialog(java.lang.String, org.jogre.client.ClientConnectionThread)
	 */
	public ChatPrivateDialog getChatPrivateDialog (String usernameTo, ClientConnectionThread conn) {
		return new ChatPrivateDialog (usernameTo, conn);
	}

	/**
	 * Delegate method for popping up a game property dialog box
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#getPropertyDialog(org.jogre.client.ClientConnectionThread, org.jogre.common.util.GameProperties)
	 */
	public void getPropertyDialog (ClientConnectionThread conn) {
		Frame frame = JOptionPane.getFrameForComponent(this);
		String label = JogreLabels.getInstance().get("game.properties");
		new JogrePropertyDialog(frame, label, true, conn);
	}

	/**
	 * Delegate method for create a user dialog.
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#getUserDialog(org.jogre.common.User)
	 */
	public void getUserDialog (User user) {
		new JUserInfoDialog (user);
	}

	/**
	 * Delegate method for creating the rules dialog.
	 *
	 * This first looks for the rules filename in the game_labels.properties file.
	 * If there isn't a rules= line there, then it will check in the game.properties file.
	 *
	 * This is done so that rules can be written in multiple languages and each
	 * correct rules file can be pointed to by the appropriate game_labels.properties
	 * file for that language.  In case there isn't a rules file for a language,
	 * the file listed in the game.properties file will be used.
	 */
	public void getRulesDialog () {
		String rulesFilename = GameLabels.getRulesFilename();
		if (rulesFilename == null) {
			rulesFilename = GameProperties.getRulesFilename();
		}
		new JRulesDialog(rulesFilename);
	}

	/**
	 * Delegate method for receiving a game message.
	 *
	 * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
	 */
	public void receiveGameMessage (XMLElement message) {
		if (jogreClientPanel != null)
			jogreClientPanel.receiveGameMessage (message);
	}

	/**
	 * Delegate method for receiving a table message.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveTableMessage (XMLElement message, int tableNum) {
		if (jogreClientPanel != null)
			jogreClientPanel.receiveTableMessage (message, tableNum);
	}

	/**
	 * Delegate method for updating the GUI applet.
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg){
		if (jogreClientPanel != null)
			jogreClientPanel.update(o, arg);
	}

	/**
	 * Return if this game has extended info to display in the table list.
	 */
	public boolean hasExtendedInfo() {
		return hasExtendedInfo;
	}

	/**
	 * Default method for returning the extended info string for the given
	 * table.
	 *
	 * This default method returns just the # of players.
	 *
	 * This method should be overridden in the {game}ClientFrame.java to
	 * provide a different string to be displayed.
	 */
	public String getExtendedTableInfoString(Table theTable) {
		return JogreLabels.getInstance().get("players") + ": " + theTable.getProperty(Comm.PROP_PLAYERS, "-");
	}

	/**
	 * Stop the applet properly.
	 *
	 * @see java.applet.Applet#stop()
	 */
	public void stop () {
		if (jogreClientPanel != null)
			jogreClientPanel.closeClient ();
	}
}
