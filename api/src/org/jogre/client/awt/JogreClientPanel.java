/* * JOGRE (Java Online Gaming Real-time Engine) - API
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

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.MissingResourceException;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nanoxml.XMLElement;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.IClient;
import org.jogre.client.TableConnectionThread;
import org.jogre.common.GameOver;
import org.jogre.common.IJogre;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.TransmissionException;
import org.jogre.common.User;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommChatClient;
import org.jogre.common.comm.CommChatPrivate;
import org.jogre.common.comm.CommDisconnect;
import org.jogre.common.comm.CommError;
import org.jogre.common.comm.CommExitTable;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommInvite;
import org.jogre.common.comm.CommJoinTable;
import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.JogreUtils;

/**
 * Jogre panel which exists in can be put within an applet
 * frame or a application frame.
 *
 * @author Bob Marks
 * @author starsinthesky
 * @author Gman
 * @version Alpha 0.2.3
 */
public class JogreClientPanel extends JPanel
                              implements IClient,
                                         Observer {

	// Frame variables
    public static final int FRAME_HEIGHT = 435;
    public static final int FRAME_WIDTH = 590;
    public static final int SPACING = 5;

	// Connection to server
	private ClientConnectionThread conn = null;

	// Data class for receiving data specific Comm messages


	// GUI stuff
	private JTableList tableListBox;
	private JUserList  userListBox;

	// buttons
	private JogreButton infoButton, messageButton;
	private JogreButton newGameButton, joinButton;
	private ChatGameComponent messageBox;
	private JogreButton rulesButton;

	// Declare a Hash of JogreTableFrames and private messages
	private HashMap tableFramesHash;
	private HashMap privateMessagesHash;

	// Resources classes
	private JogreLabels     jogreLabels;	// API resources

	private IJogreClientGUI jogreClientGui = null;

	/**
	 * Constructor for a client frame.
	 *
	 * @param conn            Connection to server.
	 * @param jogreClientGui  Connection to frame / applet.
	 */
	public JogreClientPanel (ClientConnectionThread conn,
			                 IJogreClientGUI jogreClientGui) {
	    // Set up fields
		this.conn = conn;
		this.conn.getTableList().addObserver (this);
	    this.conn.getUserList().addObserver (this);

	    this.jogreClientGui = jogreClientGui;

	    this.jogreLabels = JogreLabels.getInstance();
	    this.tableFramesHash = new HashMap ();
	    this.privateMessagesHash = new HashMap ();
 	}

	/**
	 * Sets up the graphical user interface.
	 */
	public void setUpGUI () {

	    // Create Tablelayout variables
	    double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
	    double [][] sizes;

		// Create user and table panels
	    sizes = new double [][] {{fill}, {pref, SPACING, fill, SPACING, pref}};
		JogrePanel userPanel  = new JogrePanel (sizes);

		// Set background colour
		setBackground (GameProperties.getBackgroundColour());

		// Create table panel
		JogrePanel tablePanel = new JogrePanel (sizes);
		this.userListBox = new JUserList (conn.getGame());
		JogreScrollPane userListScroll = new JogreScrollPane (userListBox);

		// Create user buttons panel and add buttons to it
		sizes = new double [][] {{pref, SPACING, pref}, {pref}};
		JogrePanel userButtonPanel = new JogrePanel (sizes);
		this.infoButton = new JogreButton (this.jogreLabels.get("info"));
		this.messageButton = new JogreButton (this.jogreLabels.get("message"));
		userButtonPanel.add (this.infoButton,    "0, 0");
		userButtonPanel.add (this.messageButton, "2, 0");

		// Add user panel stuff.
		userPanel.add (userButtonPanel,    "0, 0");
		userPanel.add (userListScroll,     "0, 2");
		userPanel.add (new RatingsPanel(), "0, 4");
		userPanel.setBorder(BorderFactory.createTitledBorder
            (BorderFactory.createEtchedBorder(), this.jogreLabels.get("user.list")));

		// Create table panel
		sizes = new double [][] {{pref, SPACING, pref, SPACING, pref}, {pref}};
		JogrePanel tableButtonPanel = new JogrePanel (sizes);

		// radio buttons
		this.newGameButton = new JogreButton (this.jogreLabels.get("new.game"));
		this.joinButton = new JogreButton (this.jogreLabels.get("join"));
		this.rulesButton = new JogreButton (this.jogreLabels.get("rules"));

		// Add items to main panel
		tableButtonPanel.add (this.newGameButton, "0,0");
		tableButtonPanel.add (this.joinButton, "2,0");
		tableButtonPanel.add (this.rulesButton, "4,0");

		// Create table list and add components to panel
		this.tableListBox = new JTableList (conn.getGame(), this.jogreClientGui);
		JogreScrollPane tableListScroll = new JogreScrollPane (this.tableListBox);
		tablePanel.setBorder (BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), this.jogreLabels.get("current.games.being.played")));
		tablePanel.add (tableButtonPanel, "0,0");
		tablePanel.add (tableListScroll,  "0,2");

		// Create bottom right panel
		this.messageBox = new ChatGameComponent (conn, 0);
		this.messageBox.receiveMessage (JogreLabels.getInstance().get("labels"), GameLabels.getWelcomeMessage());

		// Create main panel
		sizes = new double [][] {{0.3, 0.7}, {50, TableLayout.FILL}};
		setLayout (new TableLayout (sizes));
		setBackground(GameProperties.getBackgroundColour());

		// Create right panel
		sizes = new double [][] {{TableLayout.FILL}, {0.5, 0.5}};
		JogrePanel rightPanel = new JogrePanel (sizes);

		// Add panels
		rightPanel.add (tablePanel, "0,0");
		rightPanel.add (messageBox, "0,1");

		// Add to this panel
		add (new JogreTitlePanel(), "0,0,1,0");
		add (userPanel, "0,1");
		add (rightPanel, "1,1");

	    // Set the title
		String title = GameLabels.getClientTitle() + " - " +
		               jogreLabels.get("connected.as");
		jogreClientGui.setUITitle (title + " [" + this.conn.getUsername() + "]");

		// Update button states
		updateButtonStates ();
		invalidate();
		validate();
	}

	/**
	 * Update the button states depending on how many users are logged on and
	 * what components are selected.
	 */
	private void updateButtonStates () {
		// assume default state (no users or tables exist)
		boolean messageButtonEnabled = false;
		boolean infoButtonEnabled = false;
		boolean newGameButtonEnabled = false;
		boolean joinButtonEnabled = false;

		if (this.conn != null) {
			// Retrieve username
			String username = conn.getUsername();
			TableList tables = conn.getTableList();

			// Read this from server eventually
		    newGameButtonEnabled =
		    	this.conn.getTableList().size() < GameProperties.getMaxNumOfTables() &&
		    	tables.getNumOfTablesUserOwns(username) < GameProperties.getMaxNumOfTablesPerUser();

			// Rule 1: At least two users exist (yourself and someone else)
			//         and a row in the user table is selected
			boolean rule1 = this.userListBox.getSelectedIndex() != -1;

			if (rule1) {
				User selectedUser = (User)userListBox.getSelectedValue();
				messageButtonEnabled = !selectedUser.getUsername().equals (username);
			}

			// Rule 2: At least one table exists and a row in the table is selected
			boolean rule2 = this.conn.getTableList().size() > 0 &&
			                this.tableListBox.getSelectedIndex() != -1;

			// Rule 3: Rule 2 AND user isn't joining a game where he is already sitting
			//         AND the game is a public game.
			boolean rule3 = rule2;
			if (rule3) {		// ensure rule 2 other no table exists
				Table table = this.tableListBox.getSelectedTable();
				rule3 = (!table.containsPlayer(conn.getUsername()) &&
				          table.isPublic());
			}

			// Update enabled booleans depending on rules
			if (rule1) {
				infoButtonEnabled = true;
			}
			if (rule3) {
				joinButtonEnabled = true;
			}
		}
		// Update button states
		this.infoButton.setEnabled (infoButtonEnabled);
		this.messageButton.setEnabled (messageButtonEnabled);
		this.newGameButton.setEnabled (newGameButtonEnabled);
		this.joinButton.setEnabled (joinButtonEnabled);

		// The rules button is enabled if there is a rules.file property and is
		// disabled if there isn't.
		boolean rulesButtonEnabled =
		    (GameLabels.getRulesFilename() != null) ||
		    (GameProperties.getRulesFilename() != null);
		this.rulesButton.setEnabled (rulesButtonEnabled);
	}

	/**
	 *  Add listeners to the various buttons and lists
	 */
	public void addListeners () {
	    // Listener for the info button.
	    this.infoButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					jogreClientGui.getUserDialog (userListBox.getSelectedUser());
				}
			}
		);

		// Declare listener for the message button.
	    this.messageButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					// Create a new private message component
					getPrivateMessageComponent (userListBox.getSelectedUsername());
				}
			}
		);

		// Declare listener for the new game button.
	    this.newGameButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
                    jogreClientGui.getPropertyDialog (conn);
				}
			}
		);

		// Listener for the join button
	    this.joinButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					doJoinButtonAction();
				}
			}
		);

		// Listener for the rules button
		this.rulesButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					jogreClientGui.getRulesDialog();
				}
			}
		);

		// Listener on the table list
	    this.tableListBox.addListSelectionListener (
			new ListSelectionListener () {
				public void valueChanged(ListSelectionEvent listselectionevent) {
					updateButtonStates ();
				}
			}
		);

		// Double-click listener on the table list
		this.tableListBox.addMouseListener (
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					// If this is a double click and the joinButton is enabled...
					if ((e.getClickCount() == 2) &&
					     joinButton.isEnabled()) {
						// ... then do the same action as clicking the join button
						doJoinButtonAction();
					}
				}
			}
		);

		// Listener on the user list
	    this.userListBox.addListSelectionListener (
			new ListSelectionListener () {
				public void valueChanged(ListSelectionEvent listselectionevent) {
					updateButtonStates ();
				}
			}
		);
	}

	/**
	 * This is the action performed when the "Join Table" button is pressed.
	 *
	 * This is a separate function because the join table action can be caused
	 * by either clicking the "Join Table" button, or by double clicking a
	 * table in the table list.
	 */
	private void doJoinButtonAction() {
		// Retrieve the current selected table number
		int selectedTable = tableListBox.getSelectedTableNum ();

		// Create join table request and send to server.
		CommJoinTable joinTable = new CommJoinTable (selectedTable);
		conn.send (joinTable);
	}

    /**
     * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
     */
    public void receiveGameMessage (XMLElement message) {
		try {
	        String messageType = message.getName();

			if (messageType.equals(Comm.INVITE)) {
			    CommInvite commInvite = new CommInvite (message);
				receiveInvite (commInvite);
			}
			else if (messageType.equals(Comm.CHAT_GAME)) {
			    CommChatClient commMessage = new CommChatClient (message);
			    receiveGameChat (commMessage);
			}
			else if (messageType.equals(Comm.CHAT_PRIVATE)) {
			    CommChatPrivate commMessage = new CommChatPrivate (message);
			    receivePrivateChat (commMessage);
			}
			else if (messageType.equals(Comm.ERROR)) {
				CommError commError = new CommError (message);
				receiveError (commError);
			}

			// Also pass message into adapter method which all JOGRE clients
			// can recieve to do additional processing if this is required.
			receiveMessage (message);
		}
		catch (TransmissionException transEx) {}
    }

    /**
     * Adapter method for receiving a custom message.
     *
     * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
     */
    protected void receiveMessage (XMLElement message) {}

    /**
     * Recieve a table message and delegate it to the correct frame.
     *
     * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
     */
    public void receiveTableMessage (XMLElement message, int tableNum) {
 		// Redirect to correct frame depending on table number
		JogreTableFrame frame = getTableFrame (tableNum);
        if (frame != null)
            frame.receiveTableMessage(message, tableNum);
    }

	/**
     * Close the server back down again.
	 */
	public void closeClient () {
		try {
			// Try to close connection to server
			CommDisconnect commDisconnect = new CommDisconnect ();
			conn.send (commDisconnect);

			// Close down various Tables
			Vector keys = new Vector (tableFramesHash.keySet());

			// Loop through each table and remove the player
			for (int i = 0; i < keys.size(); i++) {
				int tableNum = ((Integer)keys.get(i)).intValue();
				getTableFrame (tableNum).dispose();
			}
		}
		catch (Exception ex) {}
		if (!JogreUtils.isApplet())
			System.exit (0);
	}

	/**
	 * Method which receives a message from the server (or user)
	 *
	 * @param chatMessage  Chat message from user
	 */
	public void receiveGameChat (CommChatClient chatMessage) {
	    String usernameFrom = chatMessage.getUsername();
	    String messageText = chatMessage.getChat();

	    messageBox.receiveMessage (usernameFrom, messageText);
	}

	/**
	 * Receive private chat message.
	 *
	 * @param chatMessage
	 */
	public void receivePrivateChat (CommChatPrivate chatMessage) {
	    String usernameFrom = chatMessage.getUsername();
	    String messageText = chatMessage.getChat();

	    getPrivateMessageComponent (usernameFrom).recieveMessage (usernameFrom, messageText);
	}

	/**
	 * Receive an error message.
	 *
	 * @param errorMessage   The error message from the server
	 */
	private void receiveError (CommError errorMessage) {
		String errorStr = JogreLabels.getError (errorMessage.getStatus());
		JOptionPane.showMessageDialog (
			this, errorStr, jogreLabels.get ("error"),
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	/**
	 * Return the correct JogreTableFrame.  This method also adds a listener
	 * so that when it closes down it does so cleanly.
	 *
	 * @param table  Specified table.
	 * @return       Return a JogreTableFrame which has been stored in cauche
	 */
	protected JogreTableFrame getTableFrame (final Table table) {
		JogreTableFrame newTableFrame = null;
		Integer iTableNum = new Integer (table.getTableNum());

		// Check hash to see if the frame already exists
		if (tableFramesHash.containsKey(iTableNum)) {
			// retrieve existing message component
			newTableFrame = (JogreTableFrame)tableFramesHash.get(iTableNum);
		}
		else {		// otherwise create a new frame
			// retrieve the table
		    TableConnectionThread tableConn = new TableConnectionThread (conn, table.getTableNum());
			newTableFrame = jogreClientGui.getJogreTableFrame (tableConn);
			newTableFrame.setTableFramesHash (tableFramesHash);

			tableFramesHash.put(iTableNum, newTableFrame);
		}

		// return table frame
		return newTableFrame;
	}

	/**
	 * Overloaded version when you are sure that the table exists (other wise
	 * returns null).
	 *
	 * @param tableNum  Specified table number.
	 * @return          Correct JogreTableFrame e.g. ChessTableFrame
	 */
	protected JogreTableFrame getTableFrame (int tableNum) {
		Table table = conn.getTableList().getTable (tableNum);

		// If player no longer is within table, don't open table
		if (table != null && !table.containsPlayer(this.conn.getUsername()))
			return null;

		// If table exists, retrieve frame and send the message to it
		if (table != null)
		    return getTableFrame(table);
		return null;
	}

	/**
	 * Close the table frame.
     *
	 * @param table
	 */
	private void closeTableFrame (Table table) {
	    int tableNum = table.getTableNum();

	    // Remove frame from hash
	    tableFramesHash.remove (new Integer (tableNum));

	    // Remove table from data
	    conn.getTableList().removePlayer (tableNum, conn.getUsername());
	}

	/**
	 * Recieve invite communications object.
	 *
	 * @param invite
	 */
	public void receiveInvite (CommInvite invite) {
	    // Retreive table number from message
	    int tableNum = invite.getTableNum();
	    Table table = conn.getTableList().getTable(tableNum);

		// Check to see if this is an invite or an invite reply
		if (invite.getStatus() == CommInvite.REQUEST) {
			// Compose string for user

			String tableVisibility = table.isPublic() ?
			                                jogreLabels.get ("public") :
			                                jogreLabels.get ("private");

			String messageTitle;
			try {
				// Try the old style message composition
				messageTitle =
				    jogreLabels.get ("player") + " " + invite.getUsername() + " " +
				    jogreLabels.get ("requests.a.game.playing.at") + " " +
				    jogreLabels.get ("table") + " " + invite.getTableNum() +
				    " (" + tableVisibility + "). " + jogreLabels.get ("accept") + "?";
			} catch (MissingResourceException mrEx) {
				// That didn't work, so try try new style message composition
				Object [] textArgs = {
				    invite.getUsername(),
				    new Integer (invite.getTableNum()),
				    tableVisibility
				};

				messageTitle = jogreLabels.get ("table.invite", textArgs);
			}

			int answer = JOptionPane.showConfirmDialog (
				this,
				messageTitle,
				jogreLabels.get("game.request"),
				JOptionPane.YES_NO_OPTION
			);

			// Convert to a number the CommInvite object understands
			int yesNo = (answer == JOptionPane.YES_OPTION) ?
				CommInvite.ACCEPT : CommInvite.DECLINE;

			// Create invite comm object to reply to the users
			CommInvite inviteReply = new CommInvite (
				yesNo, invite.getTableNum());
			inviteReply.setUsername (invite.getUsername());

			// Transmit message back to the server
			conn.send (inviteReply);
		}
		// User has declined invite
		else if (invite.getStatus() == CommInvite.DECLINE) {

			// report message that user has declined the offer
			String messageTitle;
			try {
				// Try the old style message composition
				messageTitle =
				    jogreLabels.get("user") + ": " +
				    invite.getUsernameTo() + " " +
				    jogreLabels.get ("has.rejected.offer.at.table") + " " +
				    invite.getTableNum();
			} catch (MissingResourceException mrEx) {
				// That didn't work, so try try new style message composition
				Object [] textArgs = {
					invite.getUsernameTo(),
					new Integer (invite.getTableNum())
				};

				messageTitle = jogreLabels.get ("rejected.invite", textArgs);
			}

			JOptionPane.showMessageDialog (
				this, messageTitle, jogreLabels.get ("game.request"),
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}

	/**
	 * Returns a private message for a particular user and creates one if it
	 * doesn't already exist.
	 *
	 * @param usernameTo  Username of person being chatted to.
	 * @return            Return a chat message component.
	 */
	protected ChatPrivateDialog getPrivateMessageComponent (final String usernameTo) {
		ChatPrivateDialog privMessage = null;

		if (privateMessagesHash.containsKey (usernameTo)) {
			// retrieve existing message component
			privMessage = (ChatPrivateDialog)privateMessagesHash.get(usernameTo);
		}
		else {
			// Create new private message component and add to the hash
			privMessage = jogreClientGui.getChatPrivateDialog (usernameTo, conn);

			// Add a listener for this message
			privMessage.addWindowListener(
				new WindowAdapter () {
					public void windowClosing (WindowEvent e) {
						privateMessagesHash.remove (usernameTo);
					}
				}
			);

			privateMessagesHash.put (usernameTo, privMessage);
		}

		// return message component
		return privMessage;
	}

	/**
	 * Update method on observer when the data changes.  This updates
	 * the state of the buttons and opens / closes any game frames.
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable obs, Object obj) {
	    updateButtonStates ();

	    // Loop through the tables and see if the user has created a new game
		// or joined an existing one
	    String username = conn.getUsername();
		TableList tableList = conn.getTableList ();
		int [] tableNumbers = tableList.getTablesNumbers();
		for (int i = 0; i < tableNumbers.length; i++) {
			int tableNum = tableNumbers [i];

			Table table = tableList.getTable(tableNum);

			// Do a check to see if this user is at a table and the frame for
			// this particular table is created (in the hash)
			if (table.containsPlayer(username) &&
				!tableFramesHash.containsKey(new Integer (table.getTableNum()))) {

				// Frame doesn't exist so create it
				getTableFrame (table);
			}
		}
	}
}
