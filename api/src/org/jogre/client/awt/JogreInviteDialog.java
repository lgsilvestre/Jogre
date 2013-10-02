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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jogre.client.TableConnectionThread;
import org.jogre.common.User;
import org.jogre.common.comm.CommInvite;
import org.jogre.common.util.JogreLabels;

/**
 * Jogre invite dialog.  This invite frame is a dialog so that
 * it will stay on top of running applications.  It contains a user
 * list to select and a message box to enter a message to the user
 * whom you are inviting to play.
 *
 * @author Garrett Lehman (gman)
 * @version Alpha 0.2.3
 */
public class JogreInviteDialog extends JogreDialog {
	// spacing between properties
	private final int SPACING = 5; 
	    
	// sizes for table layout
	private double pref = TableLayout.PREFERRED;
    
    // buttons that will always exist on properties dialog
    private JogreButton inviteButton = null;
    private JogreButton cancelButton = null;
    private JUserList userListBox = null;
    
    // connection used to create a table
    private TableConnectionThread conn = null;
	
	/**
	 * Constructor
	 */
	public JogreInviteDialog(Frame owner, String title, boolean modal, TableConnectionThread conn) {
		super (owner, title, modal);
		
		this.conn = conn;
		
	    setUpGUI ();
	}

	/**
	 * Sets up the graphical user interface.
	 */
	private void setUpGUI () {
		
		double[][] sizes = {{SPACING, 0.5, 0.5, SPACING}, {SPACING, pref, SPACING, pref, pref, SPACING}};
		this.getContentPane().setLayout(new TableLayout(sizes));
		
		User user = conn.getUserList().getUser(conn.getUsername());
		this.userListBox = new JUserList (conn.getGame(), user);
		JogreScrollPane userListScroll = new JogreScrollPane (userListBox);
		
		this.inviteButton = new JogreButton (JogreLabels.getInstance().get("invite"));
		this.cancelButton = new JogreButton (JogreLabels.getInstance().get("cancel"));
		
		this.getContentPane().add (userListScroll, "1,1,2,1,c,c");
		this.getContentPane().add (inviteButton, "1,3,r,c");
		this.getContentPane().add (cancelButton, "2,3,l,c");
		
		addListeners();
		
		updateButtonStates ();
		
        // Pack the window
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(this.getOwner());
		this.setVisible (true);
	}
	
	/**
	 *  Add listeners to the various buttons and lists
	 */
	public void addListeners () {
		// Listener on the user list
	    this.userListBox.addListSelectionListener (
			new ListSelectionListener () {
				public void valueChanged(ListSelectionEvent listselectionevent) {				
					updateButtonStates ();
				}
			}
		);
	    
		// Listener for the invite button
	    this.inviteButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {

					String usernameTo = userListBox.getSelectedUsername ();
					int tableNum = conn.getTableNum();

					CommInvite commInvite = new CommInvite (
						CommInvite.REQUEST, tableNum, usernameTo);

					// Send to server
					conn.send (commInvite);
					
					close();
				}
			}
		);
	    
		// Listener for the invite button
	    this.cancelButton.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					close();
				}
			}
		);
	}
	
	/**
	 * Update button states
	 */
	private void updateButtonStates() {
		User selectedUser = (User) userListBox.getSelectedValue();
		this.inviteButton.setEnabled(selectedUser != null);
	}
	
	/**
	 * Close dialog
	 */
	private void close() {
	    setVisible (false);
        dispose ();
	}
}
