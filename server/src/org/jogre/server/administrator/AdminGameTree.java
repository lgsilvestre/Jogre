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

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.jogre.common.Player;
import org.jogre.common.Table;

/**
 * Extended version of the JTree for displaying trees.  Includes a renderer
 * for displaying icons on a tree item.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminGameTree extends JTree {

    // Load all icons at start up
    private static final ImageIcon PLAYER_STAND_ICON = new ImageIcon ("images/player_stand_icon.gif");
    private static final ImageIcon PLAYER_SIT_ICON = new ImageIcon ("images/player_sit_icon.gif");
    private static final ImageIcon PLAYER_READY_ICON = new ImageIcon ("images/player_ready_icon.gif");
    private static final ImageIcon PLAYER_STARTED_ICON = new ImageIcon ("images/player_started_icon.gif");
    private static final ImageIcon SERVER_ICON = new ImageIcon ("images/server_node_icon.gif");
    private static final ImageIcon TABLE_LIST_ICON = new ImageIcon ("images/tablelist_icon.gif");
    private static final ImageIcon USER_LIST_ICON = new ImageIcon ("images/userlist_icon.gif");
    private static final ImageIcon USER_ICON = new ImageIcon ("images/user_icon.gif");
    private static final ImageIcon TABLE_PRIV_ICON = new ImageIcon ("images/table_priv_icon.gif");
    private static final ImageIcon TABLE_PUB_ICON = new ImageIcon ("images/table_pub_icon.gif");

    /**
     * Constructor for the Jogre Tree.
     */
    public AdminGameTree (DefaultMutableTreeNode node) {
        super (node);

        // Use a custom renderer for displaying the icons
        TreeCellRenderer renderer = new TreeCellRenderer();
        setCellRenderer(renderer);

        // Set correct selection mode
        getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    /**
     * Create a custom renderer to display the proper icon in the tree.
     */
    class TreeCellRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            // allow original renderer to do its job
            Component c = super.getTreeCellRendererComponent
            	(tree, value, selected, expanded, leaf, row, hasFocus);

            // render the open course object as bold
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;

            	ImageIcon icon = null;

            	// Create the correct image for this node
            	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            	if (node instanceof AdminGameTreeNode) {
            	    // Retrieve the icon for the game
            	    String gameKey = ((AdminGameTreeNode)node).getGame().getKey();
            	    icon = AdminGraphics.getGameIcon(gameKey);
            	}
            	else if (node instanceof AdminPlayerTreeNode) {
            	    Player player = ((AdminPlayerTreeNode)node).getPlayer();
            	    if (player.isViewing())
            	        icon = PLAYER_STAND_ICON;
            	    else if (player.isSeated())
            	        icon = PLAYER_SIT_ICON;
            	    else if (player.isReady())
            	        icon = PLAYER_READY_ICON;
            	    else if (player.isPlaying())
            	        icon = PLAYER_STARTED_ICON;
            	}
            	else if (node instanceof AdminServerTreeNode)
            	    icon = SERVER_ICON;
            	else if (node instanceof AdminTableListTreeNode)
            	    icon = TABLE_LIST_ICON;
            	else if (node instanceof AdminUserListTreeNode)
            	    icon = USER_LIST_ICON;
            	else if (node instanceof AdminUserTreeNode)
            	    icon = USER_ICON;
            	else if (node instanceof AdminTableTreeNode) {
            	    Table table = ((AdminTableTreeNode)node).getTable();
            	    if (table.isPublic())
            	        icon = TABLE_PUB_ICON;
            	    else
            	        icon = TABLE_PRIV_ICON;
            	}

            	if (icon != null)
            	    label.setIcon (icon);
            	label.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        	}
            return c;
        }
    }
}