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

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nanoxml.XMLElement;

import org.jogre.client.IClient;
import org.jogre.common.IJogre;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommError;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.JogreUtils;

/**
 * Connection panel which replaces the old ConnectionDialog
 * class.  
 * 
 * Since beta 0.3 this has become abstract.  It two implementing classes 
 * are GameConnectionPanel and ServerManagerConnectionPanel which 
 * send / receive slightly different messages and have different types of 
 * network connections.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public abstract class ConnectionPanel extends JogrePanel
                                      implements IClient
{
    // Used for validation
    private static final String INVALID_CHARS = " []{};:'@#+=<>?,./\"";

    // GUI fields
    protected JTextField usernameTextField;
    protected JTextField serverTextField, portTextField;
    protected JPasswordField passwordTextField;
    protected JButton okButton, cancelButton;    
    protected JLabel statusLabel;
    
    // Data fields
    private boolean silentConnect;
    private String  server, username, password;
    private int     port;
    
    // Other fields
    private JogreLabels labels;

    /**
     * Abstract method for connecting to a Jogre server which must be overwritten
     * in the implmentation.  This may be
     * 
     * @param socket    Socket object to connect to server with. 
     * @param username  Username to connect to server.
     * @param password  Password to connect to server.
     */
    protected abstract void connect (Socket socket, String username, String password);
        
    /**
     * Constructor for an application which doesn't take a
     * username / password.
     *
     * @param client   Link to frame / applet.
     */
    public ConnectionPanel () {
        super ();

        // Set up fields
        this.silentConnect = false;   // This is not a silent connect

        // Set up the GUI
        setUpGUI ();

        // Add listeners
        addListeners ();
    }

    /**
     * Silent connect which takes a server, port and a username.
     *
     * @param client    Link to frame / applet.
     * @param server    Name of the server.
     * @param port      Server port.
     * @param username  Username of person trying to connect.
     * @param password  Password of the person trying to connect.
     */
    public ConnectionPanel (String server,
                            int    port,
                            String username,
                            String password) {
        super();

        // Set fields
        this.server   = server;
        this.port     = port;
        this.username = username;
        this.password = password;

        silentConnect = true;		// This is a silent connect

        // Set up the GUI
        setUpGUI ();

        // Add listeners
        addListeners ();
    }
    
    /**
     * Set up GUI.
     */
    private void setUpGUI () {
        // Get labels
        labels = JogreLabels.getInstance();
        
        double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;

        // Set main layout.
        double [][] sizes = {{fill}, {fill}};
        setLayout (new TableLayout (sizes));

        // Create main panel
        sizes = new double [][] {{fill}, {pref, 25, pref, 50, pref}};
        JogrePanel mainPanel = new JogrePanel (sizes);
        statusLabel = new JLabel (labels.get("fill.in.details"));
        if (silentConnect) {
            statusLabel = new JLabel (labels.get("logging.in"));
        }
        statusLabel.setFont (JogreAwt.LIST_FONT);

        // If not a silent connect then the user must supply details
        if (!silentConnect)
            mainPanel.add (getLogonPanel (),  "0,0,c,c");
        mainPanel.add (getButtonPanel (), "0,2,c,c");
        mainPanel.add (statusLabel,       "0,4,c,c");

        // Add
        add (mainPanel, "0,0,c,c");
    }

    /**
     * Add listeners.
     */
    private void addListeners () {
        // Add button listeners
        okButton.addActionListener (
            new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    if (!silentConnect) {
                        if (validInput())	// validate if user supplies info
                            connect ();
                    }
                    else
                        connect ();			// dont' validate for applets

                }
            }
        );

        // Cancel button.
        cancelButton.addActionListener (
            new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    if (!JogreUtils.isApplet()) {
                        System.exit (0);
                    }
                }
            }
        );
    }

    /**
     * Try and connect this user.
     */
    public void connect () {
        // Retrieve server and port num
        if (!silentConnect) {
            port     = Integer.parseInt (portTextField.getText().trim());
            server   = serverTextField.getText();
            username = usernameTextField.getText();
            password = new String (passwordTextField.getPassword());
        }

        // Try and create a socket connection
        Socket socket = null;
        try {            
        	socket = new Socket (server, port);

        	// Let sub class handle the connection for here on...
        	connect (socket, username, password);
        }
        catch (ConnectException coEx) {
        	statusLabel.setText (labels.get("cannot.connect.to.server"));	
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
            statusLabel.setText (labels.get("cannot.connect.to.server"));            
        }
        catch (SecurityException secEx) {
            statusLabel.setText (labels.get("security.exception.has.occurred"));            
        }
        catch (Exception genEx) {
            genEx.printStackTrace ();
        }
    }

    /**
     * Perform some client side validation on the dialog.
     *
     * @return True if valid input.
     */
    private boolean validInput () {
        String sUsername = usernameTextField.getText().trim();
        String sServer = serverTextField.getText().trim();
        String sPort = portTextField.getText().trim();

        boolean nonEmptyUsername = !sUsername.equals ("");
        boolean nonEmptyServer = !sServer.equals ("");
        boolean nonEmptyPort = !sPort.equals ("");
        boolean validCharsUsername = validChars (sUsername);

        // assume valid port is initally OK
        boolean validPort = true;
        try {			// make sure its an integer
            int temp = Integer.parseInt(sPort);
        } catch (NumberFormatException nfEx) {
            validPort = false;
        }

        // Create over all sucess boolean
        boolean sucess = (nonEmptyUsername && nonEmptyServer && nonEmptyPort &&
            validCharsUsername && validPort
        );
                
        // If an error has occured then output the appropriate error message
        if (!sucess) {
            if (!nonEmptyUsername)
                statusLabel.setText(labels.get("please.fill.in.a.username"));
            else if (!nonEmptyServer)
                statusLabel.setText(labels.get("please.fill.in.a.server"));
            else if (!nonEmptyPort)
                statusLabel.setText(labels.get("please.fill.in.a.port"));
            else if (!validCharsUsername)
                statusLabel.setText(labels.get("invalid.characters.in.username"));
            else if (!validPort)
                statusLabel.setText(labels.get("port.is.not.a.valid.number"));
        }

        return sucess;
    }

    /**
     * @param str
     * @return
     */
    private boolean validChars (String str) {
        for (int i = 0; i < str.length(); i++) {
            if (INVALID_CHARS.indexOf(str.charAt(i)) != -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create the logon panel.
     */
    private JogrePanel getLogonPanel () {
        // Create login panel
        double pref = TableLayout.PREFERRED, space = 5;
        double [][] sizes = new double [][] {{space, pref, space, pref, space},
                                 {space, pref, space, pref, space, pref, space, pref, space, pref}};
        JogrePanel panel = new JogrePanel (sizes);

        // Create widgets
        JLabel usernameLabel = new JLabel (labels.get("username") + ":");
        JLabel passwordLabel = new JLabel (labels.get("password.leave.blank.if.not.required") + ":");
        JLabel serverLabel   = new JLabel (labels.get("server") + ":");
        JLabel portLabel     = new JLabel (labels.get("port") + ":");
        usernameTextField = new JTextField (12);
        passwordTextField = new JPasswordField (12);
        serverTextField   = new JTextField ("127.0.0.1", 8);
        portTextField     = new JTextField (String.valueOf (IJogre.DEFAULT_PORT), 4);

        // Add items to logon panel
        panel.add (usernameLabel,     "1,1,r,c");
        panel.add (passwordLabel,     "1,3,r,c");
        panel.add (serverLabel,       "1,5,r,c");
        panel.add (portLabel,         "1,7,r,c");
        panel.add (usernameTextField, "3,1,l,c");
        panel.add (passwordTextField, "3,3,l,c");
        panel.add (serverTextField,   "3,5,l,c");
        panel.add (portTextField,     "3,7,l,c");

        // Return logon panel
        return panel;
    }

    /**
     * Return the button panel.
     */
    private JogrePanel getButtonPanel () {
        // Create panel
        double pref = TableLayout.PREFERRED, space = 5;
        double [][] sizes = new double [][] {{pref, space, pref}, {pref}};
        JogrePanel buttonPanel = new JogrePanel (sizes);

        // Create buttons
        okButton = new JogreButton (labels.get("logon"));
        cancelButton = new JogreButton (labels.get("cancel"));
        if (!silentConnect) {
            buttonPanel.add (okButton, "0,0");
            buttonPanel.add (cancelButton, "2,0");
        }

        return buttonPanel;
    }
    
    /**
     * Sub class should override this and call super.receiveGameMessage (message)
     * after parsing specific sub class communication.
     * 
     * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
     */
    public void receiveGameMessage (XMLElement message) {
    	String messageType = message.getName();

    	if (messageType.equals (Comm.ERROR)) {
    		// Create comm object and create error description
    		CommError commError = new CommError (message);
    		String errorStr = JogreLabels.getError (commError.getStatus());

    		// Update label
    		statusLabel.setText (errorStr);
    	}
    }
}
