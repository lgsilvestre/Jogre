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

import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.server.ServerLabels;

/**
 * Node class for a table.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminTableTreeNode extends DefaultMutableTreeNode implements Observer {

    /** Link to table list object. */
    private Table table;

    /**
     * Constructor which takes a table
     */
    public AdminTableTreeNode (Table table) {
        super (table);

        this.table = table;
        this.table.addObserver (this);
        this.table.getPlayerList().addObserver (this);

        createNodes ();
    }

    /**
     * Create nodes for a table
     */
    private void createNodes () {
        // Create the various tables
        PlayerList playerList = table.getPlayerList();
        Vector players = playerList.getPlayersSortedBySeat();

        Player player;
        for (int i = 0; i < players.size(); i++) {
            player = (Player)players.get(i);
            add (new AdminPlayerTreeNode (player));
        }
    }

    /**
     * Return the table back.
     *
     * @return
     */
    public Table getTable () {
        return table;
    }

    /**
     * Return's table number
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
        return "#" + table.getTableNum() + " - " +
               ServerLabels.getInstance().get("game.started.at") +
               " " + table.getTimeFormatted();
    }

    /**
     * Listen to changes on the Table object.
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update (Observable obs, Object obj) {
        if (obj != null) {
            String update = (String)obj;

            // Add a player
            if (update.startsWith ("+P")) {
	            String username = update.substring(3);
	            Player player = table.getPlayerList().getPlayer(username);
	            add (new AdminPlayerTreeNode (player));
	            AdminTreePanel.refreshNodeStructure (this);
            }
            // Remove a player
            else if (update.startsWith ("-P")) {
	            String username = update.substring(3);

	            AdminPlayerTreeNode node = getPlayerNode (username);

	            if (node != null) {
	                remove (node);

	                AdminTreePanel.refreshNodeStructure (this);
	            }
            }
        }

        // Refresh everything
        JogreServerAdministrator serverFrame = JogreServerAdministrator.getInstance();
        serverFrame.autoExpandTree ();
        serverFrame.refreshStatusBar();
    }

    /**
     * Loop through the various tree nodes until you find the correct
     * player node.
     *
     * @param playerName  Name of the player
     * @return
     */
    private AdminPlayerTreeNode getPlayerNode (String playerName) {
        for (int i = 0; i < getChildCount(); i++) {
            AdminPlayerTreeNode node = (AdminPlayerTreeNode)getChildAt (i);
            Player player = (Player)node.getUserObject();

            if (player.getPlayerName().equals(playerName))
                return node;
        }

        return null;
    }
}