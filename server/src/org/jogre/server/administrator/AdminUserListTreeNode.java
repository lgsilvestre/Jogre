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

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jogre.common.User;
import org.jogre.common.UserList;
import org.jogre.common.util.JogreLabels;

/**
 * Tree node which represents a user list.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminUserListTreeNode extends DefaultMutableTreeNode implements Observer {

    /** User list class. */
    private UserList userList;

    /**
     * Constructor for a user list node.
     */
    public AdminUserListTreeNode (UserList userList) {
        super (userList);

        // Set field
        this.userList = userList;
        this.userList.addObserver (this);

        // create nodes
        createNodes ();
    }

    /**
     * Create nodes.
     */
    private void createNodes () {
        // Create the various tables
        User user;
        Vector users = userList.getUserObjects();
        for (int i = 0; i < users.size(); i++) {
            user = (User)users.get(i);
            add (new AdminUserTreeNode (user));
        }
    }

    /**
     * Return the userlist.
     *
     * @return
     */
    public UserList getUserList () {
        return userList;
    }

    /**
     * Returns message in tree.
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
        return userList.size () + " " + JogreLabels.getInstance().get("users");
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update (Observable obs, Object obj) {
        // Read update String
        String update = (String)obj;

        if (update != null) {
	        if (update.startsWith("+U")) {
	            User newUser = userList.getUser(update.substring(3));
	            add (new AdminUserTreeNode (newUser));
	            AdminTreePanel.refreshNodeStructure (this);
	        }
	        else if (update.startsWith("-U")) {
	            String username = update.substring (3);
	            for (int i = 0; i < getChildCount(); i++) {
	                DefaultMutableTreeNode node = (DefaultMutableTreeNode)getChildAt (i);
	                User user = (User)node.getUserObject();
	
	                if (username.equals (user.getUsername()))
	                    remove (i);
	            }
	            AdminTreePanel.refreshNodeStructure (this);
	        }
        }

        // Refresh everything
        JogreServerAdministrator serverFrame = JogreServerAdministrator.getInstance();
        serverFrame.autoExpandTree ();
        serverFrame.refreshStatusBar();
    }
}
