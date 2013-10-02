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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import nanoxml.XMLElement;

import org.jogre.client.IClient;
import org.jogre.common.IJogre;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommAdminGameMessage;
import org.jogre.common.comm.CommError;
import org.jogre.common.util.JogreLabels;
import org.jogre.server.ServerLabels;
import org.jogre.server.ServerProperties;

/**
 * Main panel of the Jogre Server administrator.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminPanel extends JPanel 
                              implements Observer, 
                                         IClient
{
	private static final int TAB_DIVIDE = 150;
	
    // Define menu options
    private JMenuBar menuBar;
    private JMenu fileMenu, treeMenu, serverMenu, helpMenu;
    private JMenuItem fileExit;
    private JMenuItem refreshTree, treeExpandAll, treeCollapseAll;
    private JCheckBoxMenuItem treeAutoExpand;
    private JMenuItem serverProperties;
    private JMenuItem helpAbout;
    
    // link for labels
    private ServerLabels labels;

    // Declare tree, console & message panels and status bar
    private AdminTreePanel    treePanel;
    private AdminMessagePanel messageRecievePanel, messageSentPanel;
	private AdminConsolePanel consolePanel;
	private AdminDataPanel dataPanel;
    private AdminStatusBar statusBar;

    // Fields
    private JFrame owner;
    private AdminClientConnectionThread conn = null;
    
    // Link to server properties
    private AdminServerPropertiesDialog serverPropertiesDialog = null;
    
    /**
     * Constructor of this frame.
     */
    public AdminPanel (JFrame owner, AdminClientConnectionThread conn) {
    	this.owner = owner; 
    	this.conn = conn;
    	
        // Create link to labels resource bundles
		labels = ServerLabels.getInstance();

        // Set up GUI
		setUpGUI ();

		// Add listeners
		addListeners ();
    }

    /**
	 * Set up the standard GUI elements
	 */
	private void setUpGUI () {
		setLayout (new BorderLayout ());
		
		// Create various panel
	    treePanel = new AdminTreePanel ();
		messageRecievePanel = new AdminMessagePanel ();
		messageSentPanel = new AdminMessagePanel ();
		consolePanel = new AdminConsolePanel ();
		dataPanel = new AdminDataPanel (JogreServerAdministrator.getInstance(), conn);

		// Create status bar
		statusBar = new AdminStatusBar ();

		// Set up a tabbed pane
	    JTabbedPane tabPane = new JTabbedPane ();
	    tabPane.addTab (labels.get ("console"), AdminGraphics.CONSOLE_ICON, consolePanel);
	    tabPane.addTab (labels.get ("recieved.messages"), AdminGraphics.ARROW_LEFT_ICON, messageRecievePanel);
	    tabPane.addTab (labels.get ("sent.messages"), AdminGraphics.ARROW_RIGHT_ICON, messageSentPanel);
	    tabPane.addTab (labels.get ("data"), AdminGraphics.DATA_ICON, dataPanel);

	    // Create split pane
	    JSplitPane splitPane = new JSplitPane (
			JSplitPane.HORIZONTAL_SPLIT, false, treePanel, tabPane);
        splitPane.setDividerLocation (TAB_DIVIDE);
        splitPane.setOneTouchExpandable (true);

        // Add sub panels to main panel and menu bar
        add (splitPane, BorderLayout.CENTER);
        add (statusBar, BorderLayout.SOUTH);
		
        setupMenu();
	}

	/**
	 * Called on the action script if required.
	 */
	public void autoExpandTree () {
	    if (treeAutoExpand.isSelected()) {
	        treePanel.expandAll();
	    }
	}

	/**
	 * Refresh the status bar.
	 */
	public void refreshStatusBar () {
	    statusBar.refresh();
	}
	
	/**
	 * Add listeners to the GUI items.
	 */
	private void addListeners () {
	    // Listener on the file exit
	    fileExit.addActionListener (
	        new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	                System.exit (0);
	            }
	        }
	    );

	    // Listener on the tree - collapse everything
	    treeCollapseAll.addActionListener (
	        new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	                treePanel.collapseAll();
	            }
	        }
	    );

	    // Listener on the tree - collapse everything
	    treeExpandAll.addActionListener (
	        new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	                treePanel.expandAll();
	            }
	        }
	    );

	    // Listener on the server - force refresh
	    refreshTree.addActionListener (
	        new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	                treePanel.refresh ();
	            }
	        }
	    );

	    // Listener on the help about dialog - keep it simple
	    helpAbout.addActionListener (
	        new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	                displayHelpAbout ();
	            }
	        }
	    );

	    // If clicked true then expand it all.
	    treeAutoExpand.addActionListener (
	        new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	                if (treeAutoExpand.isSelected())
	                    treePanel.expandAll();
	            }
	        }
	    );
	    
	    // Add action listener on server properties
	    serverProperties.addActionListener (
    		new ActionListener () {
	            public void actionPerformed (ActionEvent event) {
	            	serverPropertiesDialog ();
	            }
	        }
	    );
	}
	
	/**
	 * Set up the menu.
	 */
	private void setupMenu () {
		// Create menu
		menuBar = new JMenuBar ();

		// File
		fileMenu = new JMenu (labels.get("file"));
		fileMenu.setMnemonic('F');
		fileExit = new JMenuItem (labels.get("exit"));
		fileExit.setMnemonic('X');
		fileMenu.add (fileExit);

		// Tree	
		treeMenu = new JMenu (labels.get ("tree"));
		treeMenu.setMnemonic('T');
		treeAutoExpand = new JCheckBoxMenuItem (labels.get ("auto.expand.tree"), true);
		treeAutoExpand.setMnemonic('A');
		refreshTree = new JMenuItem (labels.get("force.refresh"));
		refreshTree.setMnemonic('R');
		treeExpandAll = new JMenuItem (labels.get("expand.all"));
		treeExpandAll.setMnemonic('E');
		treeCollapseAll = new JMenuItem (labels.get("collapse.all"));
		treeCollapseAll.setMnemonic('C');
		treeMenu.add (treeAutoExpand);
		treeMenu.add (refreshTree);
		treeMenu.addSeparator();
		treeMenu.add (treeExpandAll);
		treeMenu.add (treeCollapseAll);		
		treePanel.expandAll();

	    // Server
	    serverMenu = new JMenu (labels.get ("server"));
	    treeMenu.setMnemonic('S');
	    serverProperties = new JMenuItem (labels.get("server.properties"));
		serverProperties.setMnemonic('P');
		serverMenu.add (serverProperties);

	    // Help
	    helpMenu = new JMenu (labels.get ("help"));
	    helpMenu.setMnemonic('H');
	    helpAbout = new JMenuItem (labels.get ("about"));
	    helpAbout.setMnemonic('A');
	    helpMenu.add (helpAbout);

	    // Add menus to menu bar.
	    menuBar.add (fileMenu);
	    menuBar.add (treeMenu);
	    menuBar.add (serverMenu);
	    menuBar.add (helpMenu);
	}
	
	/**
	 * Close the client down.
	 */
	public void closeClient () {
		this.serverPropertiesDialog = null;
	}
	
	/**
	 * Server properties dialog.
	 */
	private void serverPropertiesDialog () {
		// Initilise the server properties
	    serverPropertiesDialog = new AdminServerPropertiesDialog (JogreServerAdministrator.getInstance(), conn);
	}
	
	/**
	 * Display help about box.
	 * 
	 * FIXME - !!! i18n this !!!
	 */
	private final void displayHelpAbout () {
	    String title =
	        ServerLabels.getServerTitle () + " " +
	        IJogre.VERSION;
	    String message =
	        "<html><head></head><body>" +
	        "<p>" + title + "</p><br>" +
	        "<p>JOGRE Project (GNU General Public Licence)</p>" +
	        "<p>http://jogre.sourceforge.net</p>" +
	        "<p><br>Programmed by: -<br>Bob Marks (project manager)" +
            "<br>Garrett Lehman, Richard Walter, Starsinthesky," +
            "<br>Ugnich Anton, Java Red, Alex Torkhov.</p>" +
	        "<p><br>Copyright 2004-2005</p>" +
	        "</body></html>";

	    // Show about box
	    JOptionPane.showMessageDialog (
            this,
            message,
            labels.get ("about") + " " + title,
            JOptionPane.INFORMATION_MESSAGE,
            AdminGraphics.SERVER_ICON
        );
	}

	/**
	 * Return the menu bar.
	 * 
	 * @return
	 */
	public JMenuBar getMenuBar () {
		return menuBar;
	}
    
    /** 
     * Delegate to receiveMessage method.
     * 
     * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
     */
    public void receiveGameMessage (XMLElement message) {
    	String messageType = message.getName();

    	// All messages to the administrator are wrapped in a CommAdminGameMessage object
    	if (messageType.equals (Comm.ADMIN_GAME_MESSAGE)) {
    		// Create admin message
    		CommAdminGameMessage adminMessage = new CommAdminGameMessage (message);
    		System.out.println (adminMessage);
    		
    		// Game message is a sub message 
    		XMLElement subMessage = adminMessage.getMessage();
    		String type     = subMessage.getName();
    		String gameID   = adminMessage.getGameID();
    		String username = adminMessage.getUsername();
    		
    		// Parse sub message.
    		if (!adminMessage.isReceivingMessage()) {
	    		
	    		if (type.equals(Comm.ADMIN_TEST_CONNECTION)) { 
	    			if (serverPropertiesDialog != null)
	    				serverPropertiesDialog.receiveGameMessage(subMessage);
	    		}
	    		if (type.equals(Comm.ADMIN_CLIENT_DATA)) { 
	    			if (dataPanel != null)
	    				dataPanel.receiveGameMessage(subMessage);
	    		}
	    		// check for errors
	    		if (type.equals(Comm.ERROR)) {
	    			displayError (new CommError (subMessage));
	    		}
    		}

    		// Forward all messages to correct message frame
    		if (adminMessage.isReceivingMessage())
    			messageRecievePanel.addCommMessage (gameID, username, subMessage);
    		else
    			messageSentPanel.addCommMessage (gameID, username, subMessage);
    	}
    }
    
    /**
     * Display error message.
     * 
     * @param commError
     */
    private void displayError(CommError commError) {
    	String errorMessage = JogreLabels.getError(commError.getStatus());
    	if (commError.getDescription() != null)
    		errorMessage = errorMessage + ":\n\n" + commError.getDescription();
    	String error = ServerLabels.getInstance().get("error");
		JOptionPane.showMessageDialog (owner, errorMessage, error, JOptionPane.ERROR_MESSAGE);
	}

	/**
     * Delegate to receiveMessage method.
     * 
     * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
     */
    public void receiveTableMessage (XMLElement message, int tableNum) {}
    
    public void update(Observable o, Object arg) {
		repaint();		
	}
}