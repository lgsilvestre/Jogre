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

import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.util.JogreLabels;

/**
 * Node class for a table list.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminTableListTreeNode extends DefaultMutableTreeNode implements Observer {

    /** Link to table list object. */
    private TableList tableList;

    /**
     * Constructor which takes a TableList as an instance.
     *
     * @param tableList
     */
    public AdminTableListTreeNode (TableList tableList) {
        super (tableList);

        this.tableList = tableList;
        this.tableList.addObserver (this);

        createNodes ();
    }

    /**
     * Create nodes for a table
     */
    private void createNodes () {
        // Create the various tables
        Table table;
        int [] tableNums = tableList.getTablesNumbers();
        for (int i = 0; i < tableNums.length; i++) {
            table = tableList.getTable(tableNums[i]);
            add (new AdminTableTreeNode (table));
        }
    }

    /**
     * Return the table list.
     *
     * @return
     */
    public TableList getTableList () {
        return tableList;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update (Observable obs, Object obj) {
        if (obj == null)
            return;

        // Refresh tree depending on change to the data model.
        String update = (String)obj;

        // Add table: +T <table number>
        if (update.startsWith("+T")) {
            int tableNum = Integer.parseInt (update.substring(3));
            Table newTable = tableList.getTable (tableNum);
            add (new AdminTableTreeNode (newTable));
            AdminTreePanel.refreshNodeStructure (this);
        }
        // Remove a table: -T <table number>
        else if (update.startsWith("-T")) {
            // Retrieve table number
            int tableNum = Integer.parseInt (update.substring(3));
            AdminTableTreeNode node = getTableNode (tableNum);

            if (node != null)
                remove (node);

            AdminTreePanel.refreshNodeStructure (this);
        }

        // Refresh everything
        JogreServerAdministrator serverFrame = JogreServerAdministrator.getInstance();
        serverFrame.autoExpandTree ();
        serverFrame.refreshStatusBar();
    }

    /**
     * Loop through the various tree nodes until you find the correct
     * table node.
     *
     * @param tableNum
     * @return
     */
    private AdminTableTreeNode getTableNode (int tableNum) {
        for (int i = 0; i < getChildCount(); i++) {
            AdminTableTreeNode node = (AdminTableTreeNode)getChildAt (i);
            Table table = (Table)node.getUserObject();

            if (tableNum == table.getTableNum())
                return node;
        }

        return null;
    }

    /**
     * Displays number of trees.
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
        return tableList.size() + " " + JogreLabels.getInstance().get("tables");
    }
}