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

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nanoxml.XMLElement;

import org.dom4j.Element;
import org.jogre.client.IClient;
import org.jogre.client.awt.JogreAwt;
import org.jogre.common.IError;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommAdminServerProperties;
import org.jogre.common.comm.CommAdminTestConnection;
import org.jogre.common.util.JogreLabels;
import org.jogre.server.ServerLabels;
import org.jogre.server.ServerProperties;

/**
 * Server properties dialog.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class AdminServerPropertiesDialog extends JDialog implements IClient{
	
	private static final double PREF = TableLayout.PREFERRED;	
	private static final double FILL = TableLayout.FILL;	
	private static final double PAD  = 5;	// pixel padding
	
	// Global GUI items
	private JLabel  validationLabel;
	private JButton okButton, cancelButton;
	
	// 3 Main panels
	private ConfigPanel configPanel;
	private GamePanel   gamePanel;
	private DataPanel   dataPanel;
	
	// Link to server properties
	private ServerProperties serverProperties;
	
	// Connection to Jogre Server
	private AdminClientConnectionThread conn;
	
	/**
	 * Constructor for the server properties dialog.
	 */
	public AdminServerPropertiesDialog (JFrame frame, 
								        AdminClientConnectionThread conn) {
		super (frame, false);
		
		this.conn = conn;
		
		// Set title of this dialog
		setTitle ("Server Properties");
		
		// Get a handle to the properties
		serverProperties = ServerProperties.getInstance();
			
		// Create GUI items
		createGUI ();
		
		// Add listeners
		addListeners ();
		
		// Set visible
		setVisible(true);
	}
	
	/**
	 * Create GUI items.
	 */
	private void createGUI () {
		
		// Set layout of main panel
		double [][] sizes = {{FILL}, {FILL, 20, PREF}};
		JPanel mainPanel = new JPanel (new TableLayout (sizes));
				
		// Create status label
		validationLabel = new JLabel ("");
		
		// Create the 3 main GUI panels
		configPanel = new ConfigPanel (serverProperties, validationLabel);
		gamePanel   = new GamePanel   (serverProperties, validationLabel);
		dataPanel   = new DataPanel   (serverProperties, validationLabel, conn);
		
		// Create tabbed pane		
		JTabbedPane tabbedPane = new JTabbedPane ();		
		tabbedPane.add("Configuration", configPanel);
		tabbedPane.add("Games",         gamePanel);
		tabbedPane.add("Server Data",   dataPanel);
		
		// Add tabbed pane and buttons to main panel		
		mainPanel.add (tabbedPane,         "0,0");
		mainPanel.add (validationLabel,    "0,1");
		mainPanel.add (getOKCancelPanel(), "0,2,c,c");
		
		getContentPane().add (mainPanel);		
		pack ();
		
		// Set size, move frame to center and make visible
		Dimension size = getSize();
		Point location = JogreAwt.getCentredLocation (this, size);
		this.setSize (size);
		this.setLocation(location.x, location.y);		
	}
	
	/**
	 * Add listeners.
	 */
	private void addListeners () {
		// OK cancel button
		okButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				okButtonClicked ();
			};
		});
		cancelButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				close();
			};
		});
	}
	
	/**
	 * Close dialog
	 */
	private void close() {
	    setVisible (false);
	    dispose();
	}
	
	/**
	 * Return the OK / Cancel button panel.
	 * 
	 * @return
	 */
	private JPanel getOKCancelPanel () {
		double [][] sizes = {{PAD, PREF, PAD, PREF, PAD}, 
				             {PAD, PREF, PAD}};
		JPanel panel = new JPanel (new TableLayout (sizes));
		
		// Create buttons
		okButton     = new JButton ("OK");
		cancelButton = new JButton ("Cancel");
		panel.add (okButton,     "1,1");		
		panel.add (cancelButton, "3,1");
		
		return panel;
	}
	
	/**
	 * Used when a user clicks the OK button. 
	 */
	private void okButtonClicked () {
		// Create new admin properties class
		CommAdminServerProperties adminProperties = 
			new CommAdminServerProperties (serverProperties.flatten());
		conn.send (adminProperties);
		close();
	}
	
	/**
	 * Message received from server.
	 * 
	 * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
	 */
	public void receiveGameMessage (XMLElement message) {
		// Retrieve the type of the message
		String type = message.getName();
		
		if (type.equals(Comm.ADMIN_TEST_CONNECTION)) {
			 
			CommAdminTestConnection commTestConn = new CommAdminTestConnection (message); 
			int status = commTestConn.getStatus();
			
			String label = ServerLabels.getInstance().get("database.connection.successful");
			if (status != IError.NO_ERROR)
				label = JogreLabels.getError(status);
			
			validationLabel.setText(label);
		}		
	}
	public void receiveTableMessage(XMLElement message, int tableNum) {}
	
	/**
	 * ========================================================================
	 * Custom panel containing configuration specifiec server properties.
	 * ========================================================================
	 */
	private static class ConfigPanel extends JPanel 
	                                 implements ActionListener, CaretListener {
		
		private ServerProperties serverProperties;
		private static final String [] COMBO_BOX = {"Guest", "Username required", "Username and Password"};
		
		private JLabel     verifyPasswordLabel;
		private JTextField adminUsernameTextBox;
		private JPasswordField adminPasswordTextBox; 
		private JTextField serverPortTextField, maxUsersTextField, maxTablesTextField, maxTablesPerUserTextField;
		private JComboBox  userLogonComboBox;
		private JLabel validationLabel;
		private JCheckBox receiveMessagesCB;
		
		/**
		 * Constructor.
		 * 
		 * @param serverProperties
		 * @param validationLabel
		 */
		public ConfigPanel (ServerProperties serverProperties, JLabel validationLabel) {
			super ();
			this.serverProperties = serverProperties;
			this.validationLabel  = validationLabel;	// link to validation label
			
			createGUI ();
			
			addListeners();
		}
		
		/**
		 * Create GUI.
		 */
		private void createGUI () {
			double [][] sizes = {{PAD, FILL, PAD}, 
					{PAD, PREF, PAD, PREF, PAD}};
			setLayout (new TableLayout (sizes));
			
			// admin sub panel
			sizes = new double [][] {{PAD, 0.5, PAD, 0.5, PAD}, 
					{PAD, PREF, PAD, PREF, PAD, PREF, PAD}};
			JPanel adminPanel = new JPanel (new TableLayout (sizes));
			adminPanel.setBorder (BorderFactory.createTitledBorder("Administrator Details"));
			
			//Create and add components
			JLabel adminUsernameLabel = new JLabel ("Admin Username: ");
			JLabel adminPasswordLabel = new JLabel ("Admin Password: ");
			JLabel receiveMessagesLabel = new JLabel ("Receive Client Messages: ");
			verifyPasswordLabel = new JLabel ("Verify Password: ");
			verifyPasswordLabel.setVisible(false);
			adminUsernameTextBox = new JTextField (serverProperties.getAdminUsername(), 20);
			adminPasswordTextBox = new JPasswordField (serverProperties.getAdminPassword(), 20);
			receiveMessagesCB = new JCheckBox ("Click to receive messages");
			receiveMessagesCB.setSelected(serverProperties.isReceiveMessages());
			
			adminPanel.add (adminUsernameLabel,   "1,1");
			adminPanel.add (adminUsernameTextBox, "3,1");		
			adminPanel.add (adminPasswordLabel,   "1,3");		
			adminPanel.add (adminPasswordTextBox, "3,3");
			adminPanel.add (receiveMessagesLabel, "1,5");
			adminPanel.add (receiveMessagesCB,    "3,5");
			
			// Server details sub panel
			sizes = new double [][] {{PAD, 0.5, PAD, 0.5, PAD}, 
					{PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD}};
			JPanel serverDetailsPanel = new JPanel (new TableLayout (sizes));
			serverDetailsPanel.setBorder (BorderFactory.createTitledBorder("Server Details"));
			
			// Create labels / text fields
			JLabel serverPortLabel  = new JLabel ("Server port: ");
			JLabel userLogonLabel   = new JLabel ("User logon: ");
			JLabel maxUsersLabel    = new JLabel ("Max users: ");
			JLabel maxTablesLabel   = new JLabel ("Max tables: ");
			JLabel maxTablesPerUser = new JLabel ("Max tables per user: ");
			serverDetailsPanel.add (serverPortLabel,  "1,1");
			serverDetailsPanel.add (userLogonLabel,   "1,3");
			serverDetailsPanel.add (maxUsersLabel,    "1,5");
			serverDetailsPanel.add (maxTablesLabel,   "1,7");
			serverDetailsPanel.add (maxTablesPerUser, "1,9");
			
			// Create combo-box and set initial value
			userLogonComboBox = new JComboBox (COMBO_BOX);
			String userValidation = serverProperties.getUserValidation();
			int index = 0;
			if (userValidation.equals(ServerProperties.XML_ATT_VAL_USER_VALIATION_USER))
				index = 1;
			else if (userValidation.equals(ServerProperties.XML_ATT_VAL_USER_VALIATION_PASS))
				index = 2;
			userLogonComboBox.setSelectedIndex(index);
			
			// Create text fields
			serverPortTextField       = new JTextField (String.valueOf(serverProperties.getServerPort()), 10);
			maxUsersTextField         = new JTextField (String.valueOf(serverProperties.getMaxNumOfUsers()), 8);
			maxTablesTextField        = new JTextField (String.valueOf(serverProperties.getMaxNumOfTables()), 8);
			maxTablesPerUserTextField = new JTextField (String.valueOf(serverProperties.getMaxNumOfTablesPerUser()), 8);
			serverDetailsPanel.add (serverPortTextField,       "3,1");
			serverDetailsPanel.add (userLogonComboBox,         "3,3");
			serverDetailsPanel.add (maxUsersTextField,         "3,5");
			serverDetailsPanel.add (maxTablesTextField,        "3,7");
			serverDetailsPanel.add (maxTablesPerUserTextField, "3,9");
			
			// Add sub panels to main panel
			add (adminPanel,         "1,1");
			add (serverDetailsPanel, "1,3");
		}
		
		/**
		 * Add listeners.
		 */
		private void addListeners () {
			// Set names for ID purposes
			adminUsernameTextBox.setName      ("adminUsernameTextBox");
			adminPasswordTextBox.setName      ("adminPasswordTextBox");
			serverPortTextField.setName       ("serverPortTextField");
			userLogonComboBox.setName         ("userLogonComboBox");
			maxUsersTextField.setName         ("maxUsersTextField");
			maxTablesTextField.setName        ("maxTablesTextField");
			maxTablesPerUserTextField.setName ("maxTablesPerUserTextField");
			receiveMessagesCB.setName         ("receiveMessagesCB"); 
			
			// Add carat listeners
			adminUsernameTextBox.addCaretListener (this);
			adminPasswordTextBox.addCaretListener (this);
			serverPortTextField.addCaretListener (this);
			maxUsersTextField.addCaretListener (this);
			maxTablesTextField.addCaretListener (this);
			maxTablesPerUserTextField.addCaretListener (this);
			
			// Add action listener (combo box)
			receiveMessagesCB.addActionListener(this);
			userLogonComboBox.addActionListener (this);
		}
		
		/**
		 * Update server properties based on combo box changes.
		 */
		public void actionPerformed (ActionEvent event) {
			JComponent comp = (JComponent)event.getSource();
			String name = comp.getName();
			
			if ("receiveMessagesCB".equals(name)) {
				serverProperties.setReceiveMessages(receiveMessagesCB.isSelected());
			}
			if ("userLogonComboBox".equals (name)) {
				int index = userLogonComboBox.getSelectedIndex();
				String userValidation = ServerProperties.VALIDATION_ARRAY[index];
				serverProperties.setUserValidation (userValidation);
			}
		}

		/**
		 * Update server properties based on key stroke changes.
		 */
		public void caretUpdate (CaretEvent event) {
			JComponent comp = (JComponent)event.getSource();
			String name = comp.getName();
			
			// Admin Username
			if ("adminUsernameTextBox".equals (name)) {
				String adminUsername = adminUsernameTextBox.getText().trim();
				
				if (adminUsername.length() != 0) {
					serverProperties.setAdminUsername (adminUsername);
					validationLabel.setText ("");
				}
				else
					validationLabel.setText ("Please specify an admin username");
			}
			// Admin password
			else if ("adminPasswordTextBox".equals (name)) {
				String password = new String (adminPasswordTextBox.getPassword());
				
				if (password.length() != 0) {
					serverProperties.setAdminPassword (password);
					validationLabel.setText ("");
				}
				else 
					validationLabel.setText ("Please specify an admin password");
			}
			// Server port
			else if ("serverPortTextField".equals (name)) {
				try {
					String portStr = serverPortTextField.getText().trim();					
					
					if (portStr.length() != 0) {	// only try and save if something is typed in.
						int port = Integer.parseInt (portStr);
						serverProperties.setServerPort (port);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText ("Invalid port number");
				}
			}
			// Maximum users
			else if ("maxUsersTextField".equals (name)) {
				try {
					String maxUsersStr = maxUsersTextField.getText().trim();					
					
					if (maxUsersStr.length() != 0) {	// only try and save if something is typed in.
						int maxUsers = Integer.parseInt (maxUsersStr);
						serverProperties.setMaxNumOfUsers (maxUsers);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText("Invalid maximum number of users");
				}
			}
			// Maximum tables
			else if ("maxTablesTextField".equals (name)) {
				try {
					String maxTablesStr = maxTablesTextField.getText().trim();					
					
					if (maxTablesStr.length() != 0) {	// only try and save if something is typed in.
						int maxTables = Integer.parseInt (maxTablesStr);
						serverProperties.setMaxNumOfTables (maxTables);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText("Invalid maximum number of tables");
				}
			}
			// Maximum tables per user
			else if ("maxTablesPerUserTextField".equals (name)) {
				try {
					String maxTablesPerUserStr = maxTablesPerUserTextField.getText().trim();
					
					if (maxTablesPerUserStr.length() != 0) {	// only try and save if something is typed in.
						int maxTablesPerUser = Integer.parseInt (maxTablesPerUserStr);
						serverProperties.setMaxNumOfTablesPerUser (maxTablesPerUser);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText("Invalid maximum number of tables per user");
				}
			}
		}
	}
	
	
	/**
	 * ========================================================================
	 * Custom panel containing game specifiec server properties.
	 * ========================================================================
	 */
	private static class GamePanel extends JPanel 
	                               implements ActionListener, CaretListener {
		
		private ServerProperties serverProperties;	// link to server properties
				
		// Declare GUI items
		private GamesListPanel gamesListPanel;
		private JList gamesList;
		private GameCustomTagsPanel gamesCustomTagsPanel;
		
		private JLabel     gameIDLabelValue;
		private JTextField minPlayersTextField, maxPlayersTextField;
		private JCheckBox  useCustomELOCheckBox;
		private JTextField startRatingTextField, kFactorTextField;
		private JRadioButton hostCheckBoxYes, hostCheckBoxNo;
		
		private JLabel validationLabel;
		
		/**
		 * Constructor.
		 * 
		 * @param serverProperties
		 * @param validationLabel
		 */
		public GamePanel (ServerProperties serverProperties, JLabel validationLabel) {
			this.serverProperties = serverProperties;
			this.validationLabel  = validationLabel;
			
			// Create panels
			double [][] sizes = {{PAD, PREF, PAD, FILL, PAD}, {PAD, PREF, PAD}};
			setLayout (new TableLayout (sizes));
			
			// Add left and right panel to main panel
			gamesListPanel = new GamesListPanel (serverProperties);
			this.gamesList = gamesListPanel.getList();
			
			add (gamesListPanel,     "1,1");
			add (getGameInfoPanel(), "3,1");
			setBorder(BorderFactory.createEtchedBorder());
			
			addListeners ();
			
			refresh ();		// Update items enable / disable state etc.
		}	
		
		/**
		 * Add listeners.
		 */
		private void addListeners () {
			
			gamesList.addListSelectionListener (new ListSelectionListener () {
				public void valueChanged (ListSelectionEvent event) {
					gameSelected ();
				}
			});
			
			minPlayersTextField.setName  ("minPlayersTextField");			
			maxPlayersTextField.setName  ("maxPlayersTextField");
			useCustomELOCheckBox.setName ("useCustomELOCheckBox");
			startRatingTextField.setName ("startRatingTextField");
			kFactorTextField.setName     ("kFactorTextField");
			hostCheckBoxYes.setName      ("hostCheckBoxYes");
			hostCheckBoxNo.setName       ("hostCheckBoxNo");
			
			minPlayersTextField.addCaretListener  (this);
			maxPlayersTextField.addCaretListener  (this);
			startRatingTextField.addCaretListener (this);
			kFactorTextField.addCaretListener     (this);
			
			useCustomELOCheckBox.addActionListener (this);
			hostCheckBoxYes.addActionListener      (this);
			hostCheckBoxNo.addActionListener       (this);
		}
		
		/**
		 * Game selected.
		 */
		private void gameSelected () {
			String gameID = getCurGame ();
			refresh ();
			
			// Update host radio button
			boolean isGameHosted = serverProperties.isGameHosted (gameID);
			if (isGameHosted)
				hostCheckBoxYes.setSelected (true);
			else hostCheckBoxNo.setSelected (true);
				
			gameIDLabelValue.setText (gameID);
			minPlayersTextField.setText (
				String.valueOf (serverProperties.getMinPlayers(gameID)));
			maxPlayersTextField.setText (
				String.valueOf (serverProperties.getMaxPlayers(gameID)));
			
			if (serverProperties.getELOElm(gameID) != null) {
				startRatingTextField.setText (
					String.valueOf (serverProperties.getStartRating(gameID)));
				kFactorTextField.setText (
					String.valueOf (serverProperties.getKFactor(gameID)));
				useCustomELOCheckBox.setSelected (true);
			}
			else {
				useCustomELOCheckBox.setSelected (false);
				startRatingTextField.setText ("");
				kFactorTextField.setText ("");
			}
		
			// Update custom tags
			gamesCustomTagsPanel.setCurrentGame(gameID);
		}
		
		/**
		 * Return the current game.
		 * 
		 * @return
		 */
		private String getCurGame () {
			return (String)gamesList.getSelectedValue();
		}
		
		/**
		 * Return the game info panel (made of 3 sub panels).
		 * 
		 * @return
		 */
		private JPanel getGameInfoPanel () {
			// Create right panels
			double [][] sizes = {{FILL}, {PREF, PAD, PREF, PAD, PREF}};
			JPanel panel = new JPanel (new TableLayout (sizes));

			// 1) Create game info panel
			sizes = new double [][] {{PAD, 0.5, PAD, 0.5, PAD}, 
	                {PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD}};
			JPanel gameInfoPanel = new JPanel (new TableLayout (sizes));
			
			// Create host radio button and panel
			hostCheckBoxYes = new JRadioButton ("Yes");
			hostCheckBoxNo  = new JRadioButton ("No");			
			JPanel radioButtonPanel = new JPanel (
				new TableLayout (new double [][] {{PREF, PREF},{PREF}}));
			radioButtonPanel.add (hostCheckBoxYes, "0,0");
			radioButtonPanel.add (hostCheckBoxNo,  "1,0");
			ButtonGroup bg = new ButtonGroup ();
			bg.add (hostCheckBoxYes); bg.add (hostCheckBoxNo);
				
			// Create text fields / labels
			minPlayersTextField = new JTextField ();
			maxPlayersTextField = new JTextField ();			
			JLabel gameIDLabel  = new JLabel ("Game ID: ");
			gameIDLabelValue    = new JLabel ();
			gameIDLabelValue.setBorder(BorderFactory.createLoweredBevelBorder());
			JLabel hostLabel   = new JLabel ("Host: ");
			
			JLabel minPlayers  = new JLabel ("Min players: ");
			JLabel maxPlayers  = new JLabel ("Max players: ");
			
			// Add items to panel			
			gameInfoPanel.add (gameIDLabel, "1,1");
			gameInfoPanel.add (hostLabel,   "1,3");
			gameInfoPanel.add (minPlayers,   "1,5");
			gameInfoPanel.add (maxPlayers,  "1,7");
			gameInfoPanel.add (gameIDLabelValue, "3,1");
			gameInfoPanel.add (radioButtonPanel, "3,3");
			gameInfoPanel.add (minPlayersTextField, "3,5");
			gameInfoPanel.add (maxPlayersTextField, "3,7");
			gameInfoPanel.setBorder (BorderFactory.createTitledBorder("Game Info"));
			
			// 2) Create ELO panel		
			sizes = new double [][] {{PAD, PREF, PAD, 40, PAD, PREF, PAD, FILL, PAD}, 
					                 {PAD, PREF, PAD, PREF, PAD}};
			JPanel eloRatingPanel   = new JPanel (new TableLayout (sizes));
			
			useCustomELOCheckBox    = new JCheckBox ("Use custom ELO", false);
			JLabel startRatingLabel = new JLabel ("Start Rating");
			JLabel kFactorLabel     = new JLabel ("kFactor");
			startRatingTextField    = new JTextField (4);
			kFactorTextField        = new JTextField (10);
			
			eloRatingPanel.add (useCustomELOCheckBox, "1,1,7,1");
			eloRatingPanel.add (startRatingLabel,     "1,3");
			eloRatingPanel.add (startRatingLabel,     "1,3");
			eloRatingPanel.add (startRatingTextField, "3,3");
			eloRatingPanel.add (kFactorLabel,         "5,3");
			eloRatingPanel.add (kFactorTextField,     "7,3");
			eloRatingPanel.setBorder(BorderFactory.createTitledBorder("Elo Rating Settings"));
					
			// 3) Create custom tags panel
			gamesCustomTagsPanel = new GameCustomTagsPanel (serverProperties);
			
			// Add sub panels to main game panel
			panel.add (gameInfoPanel,        "0,0");
			panel.add (eloRatingPanel,       "0,2");
			panel.add (gamesCustomTagsPanel, "0,4");
			
			return panel;
		}
		
		/**
		 * Set all items in the right panel enabled / disabled.
		 * 
		 * @param enabled
		 */
		private void refresh () {
			boolean enabled = gamesList.getSelectedIndex() != -1; 
			
			gameIDLabelValue.setEnabled(enabled);
			minPlayersTextField.setEnabled(enabled);
			maxPlayersTextField.setEnabled(enabled);			
			hostCheckBoxYes.setEnabled(enabled);
			hostCheckBoxNo.setEnabled(enabled);
			useCustomELOCheckBox.setEnabled(enabled);
			
			gamesListPanel.refresh();
			
			setELOItemsEnabled (enabled);
		}
		
		/**
		 * Set just the ELO items enabled / disabled.
		 * 
		 * @param enabled
		 */
		private void setELOItemsEnabled (boolean enabled) {
			if (enabled = false) {
				startRatingTextField.setEnabled(false);
				kFactorTextField.setEnabled(false);
			}
			else {
				boolean isEnabled = serverProperties.getELOElm(getCurGame()) != null;
				startRatingTextField.setEnabled(isEnabled);
				kFactorTextField.setEnabled(isEnabled);
			}
		}
		
		/**
		 * Update server properties based on combo box changes.
		 */
		public void actionPerformed (ActionEvent event) {
			JComponent comp = (JComponent)event.getSource();
			String name = comp.getName();
			String gameId = getCurGame();			
			
			// User logon
			if ("useCustomELOCheckBox".equals (name)) {
				if (useCustomELOCheckBox.isSelected())
					serverProperties.addELOElm(gameId); 
				else serverProperties.deleteELOElm(gameId);
				
				gameSelected ();
			}
			else if ("hostCheckBoxYes".equals (name)) {
				serverProperties.setGameHosted(gameId, true);
			}
			else if ("hostCheckBoxNo".equals (name)) {
				serverProperties.setGameHosted(gameId, false);
			}
		}

		/**
		 * Update server properties based on key stroke changes.
		 */
		public void caretUpdate (CaretEvent event) {
			JComponent comp = (JComponent)event.getSource();
			String name = comp.getName();
						
			// Minimum players
			if ("minPlayersTextField".equals (name)) {								
				try {
					String minPlayersStr = minPlayersTextField.getText().trim();					
					
					if (minPlayersStr.length() != 0 && minPlayersTextField.isEnabled()) {	// only try and save is something is typed in.
						int minPlayers = Integer.parseInt (minPlayersStr);
						serverProperties.setMinPlayers (getCurGame(), minPlayers);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText ("Invalid minimum number of players");
				}
			}
			else if ("maxPlayersTextField".equals (name)) {								
				try {
					String maxPlayersStr = maxPlayersTextField.getText().trim();					
					
					if (maxPlayersStr.length() != 0 && maxPlayersTextField.isEnabled()) {	// only try and save is something is typed in.
						int maxPlayers = Integer.parseInt (maxPlayersStr);
						serverProperties.setMaxPlayers (getCurGame(), maxPlayers);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText ("Invalid maximum number of players");
				}
			}
			else if ("startRatingTextField".equals (name)) {								
				try {
					String startRatingStr = startRatingTextField.getText().trim();					
					
					if (startRatingStr.length() != 0 && startRatingTextField.isEnabled()) {	// only try and save is something is typed in.
						int startRating = Integer.parseInt (startRatingStr);
						serverProperties.setStartRating (getCurGame(), startRating);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText ("Invalid maximum number of players");
				}
			}
			else if ("kFactorTextField".equals (name)) {								
				try {
					String kFactorStr = kFactorTextField.getText().trim();					
					
					if (kFactorStr.length() != 0 && kFactorTextField.isEnabled()) {	// only try and save is something is typed in.						
						serverProperties.setKFactor (getCurGame(), kFactorStr);
						validationLabel.setText ("");
					}
				}
				catch (NumberFormatException nfe) {
					validationLabel.setText ("Invalid maximum number of players");
				}
			}
		}			
	}
	
	
	/**
	 * ========================================================================
	 * Custom panel containing data specifiec server properties.
	 * ========================================================================
	 */
	private static class DataPanel extends JPanel 
	                               implements ActionListener, CaretListener 
	{ 
		private static final List DATA_TYPES = Arrays.asList (new String []{"xml", "database"});
		
		private ServerProperties serverProperties;	// link to server properties	
		private JLabel validationLabel;
		private AdminClientConnectionThread conn;
		
		// Gui items
		private ConnectionListPanel connectionListPanel; 
		private JList               connectionList;
		private JComboBox           currentDatabaseComboBox, currentDataTypeComboBox;
		private JButton             testDatabaseButton;
		private JLabel              driverIDLabelValue;
		private JTextField          locationTextField, driverTextField, urlTextField, usernameTextField;
		private JPasswordField      passwordField;
		
		/**
		 * Constructor.
		 * 
		 * @param serverProperties
		 * @param validationLabel
		 */
		public DataPanel (ServerProperties serverProperties, 
				          JLabel validationLabel,
				          AdminClientConnectionThread conn) 
		{
			this.serverProperties = serverProperties;
			this.validationLabel  = validationLabel;
			this.conn             = conn;

			// Create GUI items
			createGUI ();		
			
			// Add listeners
			addListeners ();
		}		
		
		/**
		 * Create GUI.
		 */
		private void createGUI () {
			double [][] sizes = {{PAD, FILL, PAD}, 
		             {PAD, PREF, PAD, PREF, PAD}};
			setLayout (new TableLayout (sizes));

			// Create data types section
			JLabel currentDataTypeLabel = new JLabel ("Current Persistent Data Type");
			currentDataTypeComboBox = new JComboBox (DATA_TYPES.toArray());
					
			sizes = new double [][] {{PAD, 0.5, PAD, 0.5, PAD}, 
		                             {PAD, PREF, PAD}};
			JPanel dataTypesPanel = new JPanel (new TableLayout (sizes));
			dataTypesPanel.add (currentDataTypeLabel,    "1,1");
			dataTypesPanel.add (currentDataTypeComboBox, "3,1");
			
			// Create tabbed pane
			JTabbedPane tabbedPane = new JTabbedPane ();		
			tabbedPane.add("XML",      getXMLPanel ());
			tabbedPane.add("Database", getDatabasePanel ());
			tabbedPane.setSelectedIndex(1);
			
			// Add sub panels to main panel
			add (dataTypesPanel, "1,1");
			add (tabbedPane,     "1,3");
			
			refresh();
		}
		
		/**
		 * Return the database panel.
		 * 
		 * @return
		 */
		private JPanel getDatabasePanel () {
			// Create current database connection
			JLabel currentDataTypeLabel = new JLabel ("Current Database Connection");
			
			currentDatabaseComboBox = new JComboBox (serverProperties.getConnectionIDs());
					
			double [][] sizes = {{PAD, 200, PAD, FILL, PAD}, 
		                         {PAD, PREF, PAD}};
			JPanel curDatabasePanel = new JPanel (new TableLayout (sizes));
			curDatabasePanel.add (currentDataTypeLabel,    "1,1");
			curDatabasePanel.add (currentDatabaseComboBox, "3,1");
							
			sizes = new double [][] {{PAD, FILL, PAD}, 
					                 {PAD, PREF, PAD, PREF, PAD, PREF, PAD}};
			JPanel panel = new JPanel (new TableLayout (sizes));
						
			// Create button panel
			sizes = new double [][] {{PAD, PREF, PAD, PREF, PAD}, {PAD, PREF, PAD}};
			JPanel buttonPanel = new JPanel (new TableLayout (sizes));
			JButton newButton    = new JButton ("New");
			JButton deleteButton = new JButton ("Delete");
			
			buttonPanel.add (newButton,    "1,1");		
			buttonPanel.add (deleteButton, "3,1");
			
			// Create database panel
			panel.add (curDatabasePanel,      "1,1");
			panel.add (getDatabaseSubPanel(), "1,3");
			
			return panel;
		}
		
		/**
		 * Return the XML panel.
		 * 
		 * @return
		 */
		private JPanel getXMLPanel () {
			double [][] sizes = {{PAD, 0.5, PAD, 0.5, PAD}, {PAD, PREF, PAD, PREF, PAD}};
			JPanel panel = new JPanel (new TableLayout (sizes));
			
			// Create label and textbox
			JLabel locationLabel = new JLabel ("Data Location");
			locationTextField = new JTextField (serverProperties.getXMLLocation());
			JPanel buttonPanel = new JPanel (
					new TableLayout (new double [][]{{PREF},{PREF}}));			
			
			panel.add (locationLabel,     "1,1");
			panel.add (locationTextField, "3,1");
			panel.add (buttonPanel,       "1,3");		
			
			return panel;
		}
		
		/**
		 * Return the database sub panel.
		 * 
		 * @return
		 */
		private JPanel getDatabaseSubPanel () {
			
			// Create panel		
			double [][] sizes = {{PAD, PREF, PAD, FILL, PAD}, {PAD, PREF, PAD}};
			JPanel panel = new JPanel (new TableLayout (sizes));
			
			// Create detail panel
			sizes = new double [][]{{PAD, 150, PAD, FILL, PAD},
					                {PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD}};
			JPanel detailPanel = new JPanel (new TableLayout (sizes));
			detailPanel.setBorder (BorderFactory.createTitledBorder("Details"));
			
			// Create text fields
			JLabel idLabel       = new JLabel ("ID");
			JLabel driverLabel   = new JLabel ("Driver");
			JLabel urlLabel      = new JLabel ("URL");
			JLabel usernameLabel = new JLabel ("Username");
			JLabel passwordLabel = new JLabel ("Password");
			
			// Text field
			driverIDLabelValue = new JLabel ();
			driverTextField    = new JTextField (10);
			urlTextField       = new JTextField (10);
			usernameTextField  = new JTextField (10);
			passwordField      = new JPasswordField (10);
			detailPanel.add (idLabel,            "1,1");
			detailPanel.add (driverIDLabelValue, "3,1");
			detailPanel.add (driverLabel,        "1,3");
			detailPanel.add (driverTextField,    "3,3");
			detailPanel.add (urlLabel,           "1,5");
			detailPanel.add (urlTextField,       "3,5");
			detailPanel.add (usernameLabel,      "1,7");
			detailPanel.add (usernameTextField,  "3,7");
			detailPanel.add (passwordLabel,      "1,9");
			detailPanel.add (passwordField,      "3,9");
			driverIDLabelValue.setBorder(BorderFactory.createLoweredBevelBorder());
			
			// Add test button
			testDatabaseButton = new JButton ("Test");
			detailPanel.add (testDatabaseButton, "1,11");
			
			// Add database list panel
			connectionListPanel = new ConnectionListPanel (serverProperties);
			this.connectionList = connectionListPanel.getList();			// extract list 
			
			// Add list panel to the left and detail panel to the right
			panel.add (connectionListPanel, "1,1");
			panel.add (detailPanel,       "3,1");
			
			return panel;
		}
		
		/**
		 * Called when a database connection is selected.
		 */
		private void connectionSelected () {
			String connectionID = getCurConnection ();
			refresh ();
				
			// Set text fields
			driverIDLabelValue.setText (connectionID);
			driverTextField.setText    (serverProperties.getConnectionDriver (connectionID));
			urlTextField.setText       (serverProperties.getConnectionURL (connectionID));
			usernameTextField.setText  (serverProperties.getConnectionUsername (connectionID));
			passwordField.setText      (serverProperties.getConnectionPassword (connectionID));
		}
		
		/**
		 * Return the current game.
		 * 
		 * @return
		 */
		private String getCurConnection () {
			return (String)connectionList.getSelectedValue();
		}
		
		/**
		 * Refresh.
		 */
		private void refresh () {
			boolean enabled = connectionList.getSelectedIndex() != -1;
			
			currentDataTypeComboBox.setSelectedIndex(DATA_TYPES.indexOf(serverProperties.getCurrentServerData()));
			currentDatabaseComboBox.setSelectedIndex(serverProperties.getConnectionIDs().indexOf(serverProperties.getCurrentDatabaseConnection()));
			
			driverIDLabelValue.setEnabled (enabled);
			driverTextField.setEnabled (enabled);
			urlTextField.setEnabled (enabled);
			usernameTextField.setEnabled (enabled);
			passwordField.setEnabled (enabled);
			
			connectionListPanel.refresh();
		}
		
		/**
		 * Add listeners
		 */
		private void addListeners () {
			connectionList.addListSelectionListener (new ListSelectionListener () {
				public void valueChanged (ListSelectionEvent event) {
					connectionSelected ();
				}
			});
			
			// Set names to items which can be selected / changed
			locationTextField.setName       ("locationTextField");
			currentDatabaseComboBox.setName ("currentDatabaseComboBox");
			currentDataTypeComboBox.setName ("currentDataTypeComboBox");
			testDatabaseButton.setName      ("testDatabaseButton");			
			driverTextField.setName         ("driverTextField");
			urlTextField.setName            ("urlTextField");
			usernameTextField.setName       ("usernameTextField");
			passwordField.setName           ("passwordField");
			
			// Add listeners		
			currentDatabaseComboBox.addActionListener (this);
			currentDataTypeComboBox.addActionListener (this);
			testDatabaseButton.addActionListener (this);
						
			locationTextField.addCaretListener (this);
			driverTextField.addCaretListener (this);
			urlTextField.addCaretListener (this);
			usernameTextField.addCaretListener (this);
			passwordField.addCaretListener (this);
		}

		/**
		 * Update the server properties when events occurs.
		 */
		public void actionPerformed(ActionEvent event) {
			JComponent comp = (JComponent)event.getSource();
			String name = comp.getName();
						
			if ("testDatabaseButton".equals (name)) {
				testDatabaseConnection ();
			}
			else if ("currentDataTypeComboBox".equals(name)) {
				serverProperties.setCurrentServerData(String.valueOf (currentDataTypeComboBox.getSelectedItem()));
			}
			else if ("currentDatabaseComboBox".equals(name)) {
				serverProperties.setCurrentDatabaseConnection(String.valueOf (currentDatabaseComboBox.getSelectedItem()));
			}
		}

		/**
		 * Test database connection.
		 */
		private void testDatabaseConnection () {			 
			String driver   = driverTextField.getText().trim();
			String url      = urlTextField.getText().trim();
			String username = usernameTextField.getText().trim();
			String password = new String (passwordField.getPassword()).trim();
			
			CommAdminTestConnection commTestConn = new CommAdminTestConnection (driver, url, username, password);
			conn.send(commTestConn);
		}
		
		/**
		 * Caret update.
		 * 
		 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
		 */
		public void caretUpdate (CaretEvent event) {
			JComponent comp = (JComponent)event.getSource();
			String name = comp.getName();
			String connectionID = getCurConnection();
						
			if ("locationTextField".equals (name)) {
				String location = locationTextField.getText().trim();
				
				if (location.length() != 0 && locationTextField.isEnabled()) {
					serverProperties.setXMLLocation(location);
					validationLabel.setText ("");
				}
				else if (locationTextField.isEnabled())
					validationLabel.setText ("Please specify a valid XML location");
			}
			else if ("driverTextField".equals (name)) {
				String driver = driverTextField.getText().trim();
				
				if (driver.length() != 0 && driverTextField.isEnabled()) {
					serverProperties.setConnectionDriver(connectionID, driver);
					validationLabel.setText ("");
				}
				else if (driverTextField.isEnabled())
					validationLabel.setText ("Please specify valid connection driver");
			}
			else if ("urlTextField".equals (name)) {
				String url = urlTextField.getText().trim();
				
				if (url.length() != 0 && urlTextField.isEnabled()) {
					serverProperties.setConnectionURL(connectionID, url);
					validationLabel.setText ("");
				}
				else if (urlTextField.isEnabled())
					validationLabel.setText ("Please specify a valid connection URL");
			}
			else if ("usernameTextField".equals (name)) {
				String username = usernameTextField.getText().trim();
				
				serverProperties.setConnectionUsername(connectionID, username);
				validationLabel.setText ("");
			}
			else if ("passwordField".equals (name)) {
				String password = new String (passwordField.getPassword());
				
				serverProperties.setConnectionPassword (connectionID, password);
				validationLabel.setText ("");			
			}
		}
	}
	
	
	/**
	 * Database list. 
	 */
	private static class ConnectionListPanel extends DeleteNewButtonComponentPanel {
		private JList list;
		
		// Constructor
		public ConnectionListPanel (ServerProperties serverProperties) {
			super (serverProperties);
		}

		// Create list.
		public JComponent createListComponent() {
			list = new JList ();
			JScrollPane scrolledList = new JScrollPane (list);
			scrolledList.setPreferredSize(new Dimension (100, 200));
			refreshData ();
			
			return scrolledList;
		}
		
		private void refreshData () {
			list.setListData (serverProperties.getConnectionIDs());
			
			refresh ();
		}
		
		public void newButtonClicked () {
			String connectionID = JOptionPane.showInputDialog (this, 
				"Please enter unique database connection ID", 
				"New Database Connection", 
				JOptionPane.QUESTION_MESSAGE);
			
			if (connectionID != null) {		// user hits cancel
				if (serverProperties.getConnectionElm(connectionID) != null) {
					JOptionPane.showMessageDialog (this, 
						"Database connection ID already in use!", 
						"New Database Connection", 
						JOptionPane.ERROR_MESSAGE);
				}
				else if (connectionID.trim().length() == 0) {
					JOptionPane.showMessageDialog (this, 
						"Please supply a valid database connection!", 
						"New Database Connection", 
						JOptionPane.ERROR_MESSAGE);
				}
				else {
					serverProperties.addDatabaseConnElm (connectionID);
				}
			}
			
			refreshData();
		}
		
		public void deleteButtonClicked () {
			if (JOptionPane.showConfirmDialog(this, 
					"Are you sure you want to delete this database connection?", 
					"Delete Database Connection", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				serverProperties.deleteDatabaseConnElm ((String)list.getSelectedValue());
				
				refreshData();
			}
		}
		
		public JList getList() {
			return list;
		}

		public void refresh() {
			deleteButton.setEnabled (list.getSelectedIndex() != -1);
		}
	}
	
	
	/**
	 * Games list. 
	 */
	private static class GamesListPanel extends DeleteNewButtonComponentPanel {

		private JList list;
		
		public GamesListPanel (ServerProperties serverProperties) {
			super (serverProperties);
		}

		// Create list.
		public JComponent createListComponent() {
			this.list = new JList ();	
			this.list.setCellRenderer(new GameIconRenderer ());	// draws game icons.
			
			JScrollPane scrolledList = new JScrollPane (list);
			scrolledList.setPreferredSize(new Dimension (100, 350));
			refreshData ();
			
			return scrolledList;
		}
		
		private void refreshData () {
			List children = serverProperties.getGamesElm().elements ();
			
			Vector listData = new Vector ();
			for (int i = 0; i < children.size(); i++) {
				Element elm = (Element)children.get(i);
				listData.add (elm.attributeValue("id"));
			}
			
			list.setListData (listData);
			
			refresh ();
		}
		
		public void newButtonClicked () {
			String gameID = JOptionPane.showInputDialog (this, 
				"Please enter unique gameID", 
				"New Game", 
				JOptionPane.QUESTION_MESSAGE);
			
			if (gameID != null) {		// user hits cancel
				// Check to see if valid game Elm
				if (serverProperties.getGameElm(gameID) != null) {
					JOptionPane.showMessageDialog (this, 
						"Game ID already in use!", 
						"New Game", 
						JOptionPane.ERROR_MESSAGE);
				}
				else if (gameID.trim().length() == 0) {
					JOptionPane.showMessageDialog (this, 
						"Please supply a valid database connection!", 
						"New Database Connection", 
						JOptionPane.ERROR_MESSAGE);
				}
				else {
					serverProperties.addGamesElm (gameID);
				}
				refreshData();
			}
		}
		
		public JList getList () {
			return list;
		}
		
		public void deleteButtonClicked () {
			if (JOptionPane.showConfirmDialog(this, 
					"Are you sure you want to delete this game??", 
					"Delete Game", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				serverProperties.deleteGamesElm ((String)list.getSelectedValue());
				
				refreshData();
			}						
		}
		
		public void refresh () {
			deleteButton.setEnabled (list.getSelectedIndex() != -1);
		}
	}
	
	
	/**
	 * Games custom tags panel
	 */
	private static class GameCustomTagsPanel extends DeleteNewButtonComponentPanel {

		private GameCustomTagsModel customTagsModel;	// model
		private JTable gameCustomTagsTable;				// view / controller
		
		public GameCustomTagsPanel (ServerProperties serverProperties) {
			super (serverProperties);

			// Set border
			setBorder (BorderFactory.createTitledBorder("Custom tags"));
		}		
		
		// Create list.
		public JComponent createListComponent() {
			customTagsModel = new GameCustomTagsModel (serverProperties);
			gameCustomTagsTable = new JTable (customTagsModel);
			
			gameCustomTagsTable.setAutoResizeMode (JTable.AUTO_RESIZE_ALL_COLUMNS);
			JScrollPane scrolledTable = new JScrollPane (gameCustomTagsTable);		
			gameCustomTagsTable.setPreferredScrollableViewportSize (new Dimension(400, 50));
			gameCustomTagsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			// Add selection listener
			gameCustomTagsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener () {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) return;
				        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				        if (!lsm.isSelectionEmpty()) {
				            refresh ();
				        }			
					}					
				}
			);
			
			return scrolledTable;
		}
		
		public void setCurrentGame (String currentGame) {
			customTagsModel.setCurGame(currentGame);
			refresh ();
		}
		
		public void newButtonClicked () {
			serverProperties.addNewCustomElm (customTagsModel.getCurGame());
			customTagsModel.refreshData();
			refresh ();
		}
		
		public void deleteButtonClicked () {
			int selectedRow = gameCustomTagsTable.getSelectedRow();
			if (selectedRow != -1) {
				String type = (String)customTagsModel.getValueAt(selectedRow, 0);
				serverProperties.deleteCustomElm (customTagsModel.getCurGame(), type);
				customTagsModel.refreshData ();
				refresh ();
			}
		}

		public void refresh() {
			deleteButton.setEnabled(gameCustomTagsTable.getSelectedRow() != -1);
		}
	}
	
	
	/**
	 * Panel which includes a list and 2 buttons (new / delete).
	 */
	private static abstract class DeleteNewButtonComponentPanel extends JPanel {
	
		protected ServerProperties serverProperties;

		protected JButton newButton, deleteButton;
		protected JComponent listComponent;
		
		public abstract JComponent createListComponent ();	// Create list / table component	
		public abstract void newButtonClicked ();		// Called when the "new" button is clicked
		public abstract void deleteButtonClicked ();	// Called when the "delete" button is clicked
		public abstract void refresh ();				// Used to set enabled / disabled states etc
		
		/**
		 * Constructor which takes a link to the server properties, an XML element and 
		 * attribute which 
		 * 
		 * @param serverProperties
		 * @
		 */
		public DeleteNewButtonComponentPanel (ServerProperties serverProperties) {
			super (new TableLayout (new double [][] {{PREF}, {PREF, PAD, PREF}}));
			
			// Set fields
			this.serverProperties = serverProperties;
			
			// Create buttons and panel
			double [][] sizes = {{PREF, PAD, PREF}, {PREF}};
			JPanel buttonPanel = new JPanel (new TableLayout (sizes));
			newButton = new JButton ("New");
			deleteButton = new JButton ("Delete");
			deleteButton.setEnabled(false);
			buttonPanel.add (newButton, "0,0");
			buttonPanel.add (deleteButton, "2,0");
			
			// Create left panel
			this.listComponent = createListComponent(); 
			add (listComponent, "0,0");
			add (buttonPanel,   "0,2");
			
			// Add listeners to "new" / "delete" buttons
			newButton.addActionListener (new ActionListener () {
				public void actionPerformed (ActionEvent actionEvent) {
					newButtonClicked ();
				}				
			});
			deleteButton.addActionListener (new ActionListener () {
				public void actionPerformed (ActionEvent actionEvent) {
					deleteButtonClicked ();
				}				
			});
		}	 
		
		/**
		 * Return the list component.
		 * 
		 * @return
		 */
		public JComponent getListComponent () {
			return this.listComponent;
		}
	}
		
	
	/**
	 * Game custom tags model which uses XML as its model
	 */
	private static class GameCustomTagsModel extends AbstractTableModel {
		private static final String [] XML_ATTS       = {"type", "value"};
		private static final String [] COLUMN_HEADERS = {"Type", "Value"};
		
		private ServerProperties serverProperties;		
		private String curGame;
		private List customElmsXMLData = null;
						
		public GameCustomTagsModel (ServerProperties serverProperties) {
			this.serverProperties = serverProperties;
		}		
		public void setCurGame (String curGame) {
			this.curGame = curGame;
			refreshData ();
		}
		public void refreshData () {
			if (curGame != null) {
				this.customElmsXMLData = serverProperties.getCustomElms(curGame);
				fireTableDataChanged();
			}
		}
		public Object getValueAt (int rowIndex, int columnIndex) {
			if (customElmsXMLData != null)
				return ((Element)customElmsXMLData.get(rowIndex)).attributeValue (XML_ATTS[columnIndex]);
			else 
				return "";
		}
		public void setValueAt (Object value, int rowIndex, int columnIndex) {
			if (customElmsXMLData != null)
				((Element)customElmsXMLData.get(rowIndex)).addAttribute(XML_ATTS[columnIndex], String.valueOf (value)); 
		}
		public int getRowCount() {
			if (customElmsXMLData != null) 
				return (customElmsXMLData.size());			
			return 0;
		}
		public int getColumnCount()  { return XML_ATTS.length; }	
		public String getColumnName (int column) { return COLUMN_HEADERS [column]; }
		public boolean isCellEditable (int row, int column) { return true; }
		public String getCurGame () { return curGame; }
	}
	
	/**
     * Create a custom renderer to display the proper icon in the tree.
     */
    private static class GameIconRenderer extends DefaultListCellRenderer {
    	
        public Component getListCellRendererComponent(JList list, 
        		                                      Object value,
        											  int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            // allow original renderer to do its job
            JLabel label = (JLabel)super.getListCellRendererComponent
            	(list, value, index, isSelected, cellHasFocus);

            // Draw icon beside game text
            String gameId = label.getText();	
            if (gameId != null) {
            	ImageIcon icon = AdminGraphics.getGameIcon(gameId);

            	if (icon != null)
            	    label.setIcon (icon);
            }

            return label;
        }
    }
	
	/**
	 * Test main method.
	 * 
	 * @param args
	 */
	public static void main (String [] args) {
		ServerProperties.setUpFromFile();
		AdminServerPropertiesDialog dialog = new AdminServerPropertiesDialog (null, null);
		dialog.setVisible(true);
		dialog.setLocation(400, 700);		
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {				
				System.exit (0);
			}
		});
	}
}