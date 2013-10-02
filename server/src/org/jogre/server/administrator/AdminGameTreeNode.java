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

import javax.swing.tree.DefaultMutableTreeNode;

import org.jogre.common.Game;
import org.jogre.common.TableList;
import org.jogre.common.UserList;
import org.jogre.common.util.JogreLabels;

/**
 * Node class for a specified game.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class AdminGameTreeNode extends DefaultMutableTreeNode implements Observer {

	private AdminTableListTreeNode tableListTreeNode;
	private AdminUserListTreeNode  userListTreeNode;
	
	/**
     * Game object associated with this class.
     */
    private Game game;
    private TableList tableList;
	private UserList userList;

    /**
     * Constructor for a "game" which takes a game object.
     *
     * @param arg0
     */
    public AdminGameTreeNode (Game game) {
        super (game);

        // Set reference of the data Game object.
        this.game = game;
        this.tableList = game.getTableList();
        this.userList  = game.getUserList();
  
        this.tableListTreeNode = new AdminTableListTreeNode (tableList);
    	this.userListTreeNode = new AdminUserListTreeNode (userList);    	
    	this.tableList.addObserver (this);
        this.userList.addObserver (this);   
        
        // Refresh the tree for 1st time loading up
        refreshTree ();
    }

    /**
     * Return game object associated with this class.
     *
     * @return
     */
    public Game getGame () {
        return game;
    }

    /**
     * Return
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
    	StringBuffer sb = new StringBuffer (game.getKey());
    	
    	// Add user / table count if applicable
    	if (tableList.size() > 0 || userList.size() > 0)
    		sb.append (" (");
    	if (tableList.size() > 0) {
    		sb.append (tableList.size() + " tables");
    		if (userList.size() > 0)
    			sb.append (", ");
    	}    	
    	if (userList.size() > 0)
    		sb.append (userList.size() + " users"); 
    	if (tableList.size() > 0 || userList.size() > 0)
    		sb.append (")");    	
    		
        return sb.toString();
    }
    
    /**
     * Refresh the tree.
     */
    private void refreshTree () {
    	if (tableList.size() == 0 && isNodeChild(tableListTreeNode))
    		remove (tableListTreeNode);
    	else if (tableList.size() > 0 && !isNodeChild(tableListTreeNode))
    		insert(tableListTreeNode, 0);

    	// User list
    	if (userList.size() == 0 && isNodeChild(userListTreeNode))
    		remove (userListTreeNode);
    	else if (userList.size() > 0 && !isNodeChild(userListTreeNode))
    		add(userListTreeNode);
    }

	/**
	 * Update method.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable obs, Object obj) {

        // Table list
    	refreshTree ();
    	
    	AdminTreePanel.refreshNodeStructure (this);
        JogreServerAdministrator serverFrame = JogreServerAdministrator.getInstance();
        serverFrame.autoExpandTree ();
        serverFrame.refreshStatusBar();
	}
}
