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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.JFrame;

import nanoxml.XMLElement;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.awt.ChatPrivateDialog;
import org.jogre.client.awt.JRulesDialog;
import org.jogre.client.awt.JUserInfoDialog;
import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.JogrePropertyDialog;
import org.jogre.common.GameList;
import org.jogre.common.JogreGlobals;
import org.jogre.common.User;
import org.jogre.common.util.GameProperties;
import org.jogre.server.ServerLabels;

/**
 * Administrator which can log onto a JogreServer in much the same was as a
 * game can but can see the full game tree and send messages to the JogreServer.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class JogreServerAdministrator extends JFrame {
	
	private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

	// Main GUI panel
	private AdminPanel mainPanel       = null;
	private AdminConnectionPanel  connectionPanel = null;
	private AdminClientConnectionThread conn = null;

	private static JogreServerAdministrator instance = null;
	
	private GameList gameList = null;	
	private HashMap iconData = null;
	
	/**
	 * Constructor for a client frame.
	 */
	private JogreServerAdministrator (String [] args) {
		// Class super constructor on JFrame
		super ();

		// parse the command line arguments
        parseCommandLineArguments (args);

		// Set the title of the client window.
		this.setTitle (ServerLabels.getInstance().get("jogre.server.administrator"));

		// Set icon
		this.setIconImage(AdminGraphics.SERVER_ICON.getImage());

		// Set size, move frame to center and make visible
		Dimension size = JogreAwt.getMinimumSize(this, WIDTH, HEIGHT);
		Point location = JogreAwt.getCentredLocation(this, size);
		this.setSize (size);
		this.setLocation(location.x, location.y);

		// Display connection panel
		connectionPanel = new AdminConnectionPanel (this); 
		getContentPane().add (connectionPanel);

		// Set background colour of frame
		getContentPane().setBackground (GameProperties.getBackgroundColour());

		// Make panel visible
		setVisible (true);
		validate();
	}
	
	/**
	 * Singleton method to return new jogre server manager.
	 * 
	 * @return       Singleton instance of JogreServerManager.
	 */
	public static JogreServerAdministrator getInstance (String [] args) {
		if (instance == null)
			instance = new JogreServerAdministrator (args);
		
		return instance;
	}
	
	/**
	 * Convience method - assumes instance is already created.
	 * 
	 * @return      Singleton instance of JogreServerManager.
	 */
	public static JogreServerAdministrator getInstance () {
		return getInstance (null);
	}

    /**
     * <p>This method parses the commands handed in from the command prompt.
     * For example:</p>
     *
     * <code>-lang=fr</code>
     *
     * @param args     Additional arguments from the command line.
     */
    public void parseCommandLineArguments (String [] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String argument = args [i];

                // Read the port if specified
                if (argument.startsWith("-lang=")) {
                    int pos = argument.indexOf ("=");
                    JogreGlobals.setLocale (argument.substring(pos + 1));
                }
            }
        }
    }
	
	/**
	 * Setup main screen.
	 */
	private void setupGUI (AdminClientConnectionThread conn) {
		getContentPane().remove (connectionPanel);		// remove connection panel first of all
		
		// Create new jogre client frame
		mainPanel = new AdminPanel (this, conn);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mainPanel.closeClient ();
				System.exit (0);
			}
		});
		
		// Update client
		conn.setClientInterface (mainPanel);
		
		setJMenuBar (mainPanel.getMenuBar());

		// Add the main panel to the content panel
		addWindowListener (
		    new WindowAdapter () {
		        public void windowClosing (WindowEvent e) {
		            System.exit (0);
		        }
		    }
		);
	    
	    // Add the main panel
		getContentPane().add (mainPanel);
	}

	/**
	 * Set the title of this frame.
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#setUITitle(java.lang.String)
	 */
	public void setUITitle (String title){
		this.setTitle (title);
	}
	
	/**
	 * Return the game list.
	 * 
	 * @return  GameList.
	 */
	public GameList getGameList () {
		return this.gameList;
	}
	
	/**
	 * Set the game list.
	 * 
	 * @param gamelist   Game list.
	 */
	public void setGameList (GameList gamelist) {
		this.gameList = gamelist;
	}
	
	/**
	 * Set icon data.
	 * 
	 * @param iconData
	 */
	public void setIconData (HashMap iconData) {
		this.iconData = iconData;
	}
	
	/**
	 * Return icon data.
	 * 
	 * @return
	 */
	public HashMap getIconData () {
		return this.iconData;
	}

	/**
	 * Delegate method for create a user dialog.
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#getUserDialog(org.jogre.common.User)
	 */
	public void getUserDialog (User user) {
        new JUserInfoDialog (this, user);
	}

	/**
	 * Delegate method for creating the rules dialog.
	 *
	 */
	public void getRulesDialog () {
		new JRulesDialog(GameProperties.getRulesFilename());
	}
	
	/**
	 * Return connection thread.
	 * 
	 * @return
	 */
	public AdminClientConnectionThread getAdminConnectionThread () {
		return this.conn;
	}

	/**
	 * Delegate method for receiving a game message.
	 *
	 * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
	 */
	public void receiveGameMessage (XMLElement message) {
		mainPanel.receiveGameMessage (message);
	}

	/**
	 * Delegate method for receiving a table message.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveTableMessage (XMLElement message, int tableNum) {
		mainPanel.receiveTableMessage (message, tableNum);
	}

	/**
	 * Delegate method for updating the panel.
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg) {
		mainPanel.update (o, arg);
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main (String [] args) {
		JogreServerAdministrator admin = JogreServerAdministrator.getInstance ();	
	}

	/**
	 * Delegate to main panel.
	 */
	public void autoExpandTree() {
		if (mainPanel != null)
			mainPanel.autoExpandTree();
	}

	/**
	 * Delegate to status bar.
	 */
	public void refreshStatusBar() {
		if (mainPanel != null)
			mainPanel.refreshStatusBar();
	}

	/**
	 * When connected OK - create JogreServerPanel which is capable to 
	 * recieving server data (it implements IClient)
	 * 
	 * @param conn
	 */
	public void connectOK (AdminClientConnectionThread conn) {
		this.conn = conn;		// set connection to server.

		// Remove logon panel and add client panel.
		setupGUI (conn);

		invalidate ();
		validate ();
	}
}