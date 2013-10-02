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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.jogre.common.Game;
import org.jogre.common.Player;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.User;
import org.jogre.common.UserList;

/**
 * Visual list of all the tables currently being played.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JTableList extends JList implements Observer {

	/** Data (table list). */
	private Game game = null;
	
	/** ClientGui used to get extended info about a table. */
	protected IJogreClientGUI jogreClientGui;

	/**
	 * Constructor for the JTableList.
	 */
	public JTableList (Game game, IJogreClientGUI jogreClientGui) {
		super ();

		this.game = game;
		this.jogreClientGui = jogreClientGui;

	    // Add observer on changes to the table list.
		this.game.getTableList().addObserver (this);
		
		setCellRenderer(new TableListRenderer ());
		
		// Refresh the GUI
	    refresh ();
	}

	/**
	 * Refresh method which updates the visual userlist.
	 */
	public void refresh () {
	    if (game != null) {
	    	TableList tables = game.getTableList();
			int [] tableNums = tables.getTablesNumbers();
			Vector listData = new Vector ();
			for (int i = 0; i < tableNums.length; i++) {
				Table table = tables.getTable(tableNums[i]);

				listData.add (table);
			}

			//convert to a table at some stage
			setListData (listData);
		}
	}

	/**
	 * Return the selected table number.
	 *
	 * @return  Selected table number. -1 if nothing selected.
	 */
	public int getSelectedTableNum () {
		int index = getSelectedIndex();		
		if (game != null && index != -1) {
			TableList tableList = game.getTableList();
			
			// Retrieve User object
			int [] tableNums = tableList.getTablesNumbers();
			
			if (index < tableNums.length)			
			    return tableNums [index];
		}
		return -1;
	}

	/**
	 * Return a Table object of the current selected table.
	 *
	 * @return  Table from position in table.
	 */
	public Table getSelectedTable () {		
		if (game != null) {
			TableList tableList = game.getTableList();
			int tableNum = getSelectedTableNum();
			if (tableNum != -1) {
				// Retrieve User object
				return tableList.getTable(tableNum);
			}
		}
		return null;
	}
	
    /**
     * Refresh when the data has been updated on the userlist.
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update (Observable obs, Object str) {
        refresh ();
    }	
    
    /**
     * ===========================================================
     * Custom renderer for the pulldown.
     * ===========================================================
     */
    class TableListRenderer extends JPanel implements ListCellRenderer { 
        
    	private static final int PLAYER_LABEL_WIDTH = 50;
    	
    	// use a rating square to
    	private VisibilitySquare visibilitySquare;		 
    	private JogreLabel tableNumLabel, timeStartedLabel;
    	private JogrePanel playerPanel;
    	private JogreLabel extendedInfoLabel;

        /**
         * Constructor for this renderer.
         */
        public TableListRenderer () {
        	// Create layout
        	double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
        	double [][] sizes = {{16, 16, 60, fill},{pref, pref}};
        	setLayout (new TableLayout (sizes));

            // Create components and add.
        	visibilitySquare = new VisibilitySquare ();
        	tableNumLabel = new JogreLabel ();
        	timeStartedLabel = new JogreLabel ();
        	extendedInfoLabel = new JogreLabel ();

        	// Set fonts
        	tableNumLabel.setFont (JogreAwt.LIST_FONT_BOLD);
        	timeStartedLabel.setFont (JogreAwt.LIST_FONT);
        	extendedInfoLabel.setFont (JogreAwt.LIST_FONT);

        	// Create player panel
        	// RAW: Hmmm, does this mean that there is a limit of 7 players at a table?
        	sizes = new double [][]{{pref, pref, pref, pref, pref, pref, pref},{pref}};
        	playerPanel = new JogrePanel (sizes);

        	// Add components
        	add (visibilitySquare,  "0,0,c,c");
        	add (tableNumLabel,     "1,0,c,c");
        	add (timeStartedLabel,  "2,0,c,c");
        	add (playerPanel,       "3,0,l,c");

        	if (jogreClientGui.hasExtendedInfo()) {
        		add (extendedInfoLabel, "2,1,3,1,l,c");
        	}
        }

        /* (non-Javadoc)
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public Component getListCellRendererComponent(JList list, 
        											  Object value,
        											  int index, 
        											  boolean isSelected, 
        											  boolean cellHasFocus) 
        {
        	// Set colours up        	
            if (isSelected) {             	
                setBackground (new Color (240, 240, 240)); 
                setForeground (Color.black); 
            } else { 
            	setBackground (Color.white);
                setForeground (Color.black); 
            } 

            // Set label text and square colour.
            Table table = (Table)value;
                
            // Update values of components
            if (table != null) {
            	visibilitySquare.setPublic (table.isPublic());
		        tableNumLabel.setText (String.valueOf(table.getTableNum()));
		        timeStartedLabel.setText (table.getTimeFormatted());
				extendedInfoLabel.setText (jogreClientGui.getExtendedTableInfoString(table));

		        refreshPlayerPanel (table);
            }

            return this;
        }

        /**
         * Refresh the player panel.
         */
        private void refreshPlayerPanel (Table table) {
        	playerPanel.removeAll();
        	Vector players = table.getPlayerList().getPlayers();
        	String owner   = table.getPlayerList().getOwner();
        	
        	// Add each player to the player panel
        	UserList users = game.getUserList();
        	for (int i = 0; i < players.size(); i++) {
        		Player player = (Player)players.get(i);
        		String username = player.getPlayerName();
        		User user = users.getUser (username);
        		if (user != null) {
        			int rating = user.getRating();
        			playerPanel.add (new UserLabel (user, PLAYER_LABEL_WIDTH), i + ",0,l,c");
        		}
        	}
        }
    }    
    
    
    /**
     * Declare a little box for the visibility square.
     */
    private class VisibilitySquare extends JogrePanel {    	
    	
    	private final int BOX_SIZE  = 12;
    	private final Color COLOR_PUB  = new Color (0, 200, 0);
    	private final Color COLOR_PRIV = new Color (200, 0, 0);
    	private ColouredSquare square;
    	
    	public VisibilitySquare () {
    		super (new double [][] {{TableLayout.FILL},{TableLayout.FILL}});
    		setPreferredSize (new Dimension (16, 16));    		
    		square = new ColouredSquare (COLOR_PUB, BOX_SIZE, BOX_SIZE, true);
    		add (square, "0,0,c,c");
    	}
    	
    	public void setPublic (boolean isPublic) {
    		square.setColor (isPublic ? COLOR_PUB : COLOR_PRIV);
    	}
    }
}
