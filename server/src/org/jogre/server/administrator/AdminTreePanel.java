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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PUsRPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.server.administrator;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Panel on the left hand side of the JogreServerFrame for displaying
 * the tree.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminTreePanel extends JPanel {
	private static final double PREF = TableLayout.PREFERRED;
	private static final double FILL = TableLayout.FILL;
	
    /** Tree for display the server tree. */
    private static AdminGameTree tree;			// FIXME !!! change from being static !!!

    /** Link to the root node.*/
    private AdminServerTreeNode root;

    /** Scroll panel where the tree exists on. */
    private JScrollPane scrollPanel;    
    private JButton collapseButton, expandButton; 

    /**
     * Constructor.
     */
    public AdminTreePanel () {
        super ();
        setLayout(new TableLayout (new double [][] {{FILL}, {FILL, PREF}}));
        
        createRootNode ();
    }
    
    /**
     * Create root node.
     */
    public void createRootNode () {
    	// Create the nodes.
        root = new AdminServerTreeNode ();

        // Create a tree / buttons
        this.tree = new AdminGameTree (root);
        this.collapseButton = new JButton ("-");
        this.expandButton = new JButton ("+");
        this.scrollPanel = new JScrollPane (tree);

        // Add to panel.
        JPanel buttonPanel = new JPanel (new TableLayout (new double [][] {{0.5, 0.5}, {20}}));
        buttonPanel.add(collapseButton, "0,0");
        buttonPanel.add(expandButton,   "1,0");
        buttonPanel.setBorder(BorderFactory.createLoweredBevelBorder());
                
        // Add        
        add (scrollPanel, "0,0");
        add (buttonPanel, "0,1");
        
        collapseButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				collapseAll();
			}        	
        });
        expandButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				expandAll();
			}        	
        });
    }

    /**
     * Refresh a single node.
     */
    public static void refreshNode (TreeNode node) {
        ((DefaultTreeModel)tree.getModel()).nodeChanged (node);
    }

    /**
     * Refresh a single node.
     */
    public static void refreshNodeStructure (TreeNode node) {
        ((DefaultTreeModel)tree.getModel()).nodeStructureChanged (node);
    }

    /**
     * Refresh all the nodes.
     */
    public void refresh () {
        ((DefaultTreeModel)tree.getModel()).nodeStructureChanged (root);
    }

    /**
     * Expand the tree.
     */
    public void expandAll () {
    	if (tree != null) {
	        int row = 0;
	        while (row < tree.getRowCount()) {
	            tree.expandRow(row);
	            row++;
	        }
    	}
    }

    /**
     * Collapse the tree.
     */
    public void collapseAll () {
    	if (tree != null) {
	        int row = tree.getRowCount() - 1;
	        while (row > 0) {
	            tree.collapseRow(row);
	            row--;
	        }
    	}
    }
}
