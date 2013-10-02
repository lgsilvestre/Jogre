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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import nanoxml.XMLElement;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.IClient;
import org.jogre.client.TableConnectionThread;
import org.jogre.common.JogreGlobals;
import org.jogre.common.Table;
import org.jogre.common.User;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.JogreUtils;

/**
 * <p>This abstract class declares the main game frame where a user can see
 * users of the left, tables on the top right and a broadcasting style message
 * chat box on the bottom right. To create a new client frame for a game, such
 * as Chess for example, a class called e.g. ChessClientFrame extends this class
 * and implements the following abstract methods:</p>
 *
 * <p><code><pre>
 *	 public abstract JogreTableFrame getJogreTableFrame (ClientConnectionThread conn, Table table);</li>
 * </pre></code></p>
 *
 * <p>For the chess example this would look something like the following:</p>
 *
 * <code><pre>
 * public JogreTableFrame getJogreTableFrame (TableConnectionThread conn)
 * {
 *     // return a "Chess" table frame when join/creating a new table.
 *     return new ChessTableFrame (conn, table);
 * }
 * </pre></code>
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class JogreClientFrame extends JFrame
                                       implements IClient, Observer, IJogreClientGUI {
	private static final int FRAME_HEIGHT = 435;
	private static final int FRAME_WIDTH = 590;

	// Main GUI panel
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
	 * Constructor for a client frame.
	 *
	 * @param args         Command line arguments
	 * @param hasExtInfo   Indicates if this game will display extended info
	 */
	public JogreClientFrame (String [] args, boolean hasExtInfo) {
		// Class super constructor on JFrame
		super ();
		
		// Save parameter
		this.hasExtendedInfo = hasExtInfo;

		// parse the command line arguments
        parseCommandLineArguments (args);
        
        // Load images (in seperate thread)
    	GameImages.loadImages();

		// Set the title of the client window.
		this.setTitle(GameLabels.getClientTitle());

		// Inform game properties this isn't an applet (i.e. is an application)
		JogreUtils.setApplet (false);

		// Set size, move frame to center and make visible
		Dimension size = JogreAwt.getMinimumSize(this, FRAME_WIDTH, FRAME_HEIGHT);
		this.setSize (size);
		Point location = JogreAwt.getCentredLocation(this, size);
		this.setLocation(location.x, location.y);

		// Display connection panel
		connectionPanel = new GameConnectionPanel (this);
		getContentPane().add (connectionPanel);

		// Set background colour of frame
		getContentPane().setBackground (GameProperties.getBackgroundColour());

	    while (!GameImages.isFinishedLoading()) {}
	    setIconImage(GameImages.getImage("game.icon"));
		
	    // Make panel visible
		setVisible (true);
		validate();
	}

	/**
	 * Constructor for a client frame when no extended info.
	 *
	 * @param args         Command line arguments
	 */
	public JogreClientFrame (String [] args) {
		this (args, false);
	}

    /**
     * Constructor which takes no parameters which cannot be overwritten (so is final)
     *
     * @param args
     */
    private JogreClientFrame () {}

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
	 * Method which is called when a client connects
	 * successfully.
	 *
	 * @param conn   Client connection thread.
	 */
	public void connectOK (ClientConnectionThread conn) {
		// Update client
		conn.setClientInterface(this);

		// Create new jogre client frame
		jogreClientPanel = new JogreClientPanel (conn, this);
		jogreClientPanel.setUpGUI ();
		jogreClientPanel.addListeners();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				jogreClientPanel.closeClient ();
				System.exit (0);
			}
		});

		// Remove logon panel and add client panel.
		getContentPane().remove (connectionPanel);
		getContentPane().add    (jogreClientPanel);
		jogreClientPanel.revalidate();
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
	 * Delegate method for receiving a private chat message.
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#getChatPrivateDialog(java.lang.String, org.jogre.client.ClientConnectionThread)
	 */
	public ChatPrivateDialog getChatPrivateDialog (String usernameTo, ClientConnectionThread conn) {
		return new ChatPrivateDialog (this, usernameTo, conn);
	}

	/**
	 * Delegate method for popping up a game property dialog box
	 *
	 * @see org.jogre.client.awt.IJogreClientGUI#getPropertyDialog(org.jogre.client.ClientConnectionThread, org.jogre.common.util.GameProperties)
	 */
	public void getPropertyDialog(ClientConnectionThread conn) {
		String label = JogreLabels.getInstance().get("game.properties");
		new JogrePropertyDialog(this, label, true, conn);
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
	 * This first looks for the rules filename in the game_labels.properties file.
	 * If there isn't a rules= line there, then it will check in the game.properties file.
	 *
	 * This is done so that rules can be written in multiple languages and each
	 * correct rules file can be pointed to by the appropriate game_labels.properties
	 * file for that language.  In case there isn't a rules file for a language,
	 * the file listed in the game.properties file will be used.
	 *
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
		jogreClientPanel.receiveGameMessage (message);
	}

	/**
	 * Delegate method for receiving a table message.
	 *
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveTableMessage (XMLElement message, int tableNum) {
		jogreClientPanel.receiveTableMessage (message, tableNum);
	}

	/**
	 * Delegate method for updating the panel.
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg) {
		jogreClientPanel.update(o, arg);
	}

	/**
	 * Return if this game has extended info to display in the table list.
	 */
	public boolean hasExtendedInfo () {
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
	public String getExtendedTableInfoString (Table theTable) {
		return JogreLabels.getInstance().get("players") + ": " + theTable.getProperty(Comm.PROP_PLAYERS, "-");
	}
}
