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
import java.util.Observable;
import java.util.Observer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;import java.util.*;


import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.jogre.common.Game;
import org.jogre.common.User;

/**
 * Visual list of all the users logged on.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JUserFriendList extends JList implements Observer {

	// Data (user list)
	private Game game = null;
	
	// Excluded user
	private User excludedUser = null;
	
	protected Vector amigos = new Vector();
	protected HashMap friendlist =new HashMap();


	/**
	 * Empty Constructor.
	 */
	public JUserFriendList (Game game) {
		super ();
				
		// Sets fields
	    this.game = game;
	    
	    init();
	}
	
	/**
	 * Constructor with excluded user.
	 */
	public JUserFriendList (Game game, User excludedUser) {
		super ();
				
		// Sets fields
	    this.game = game;
	    this.excludedUser = excludedUser;
	    
	    init();
	}
	
	/**
	 * Init
	 */
	private void init() {
		
		User Amigo1 = new User("Maite",0,0,0,0,0);
		friendlist.put(Amigo1.getUsername(),Amigo1);
		
		User Amigo2 = new User("Sylvester",0,0,0,0,0);
		friendlist.put(Amigo2.getUsername(),Amigo2);
		
		User Amigo3 = new User("Tweety",0,0,0,0,0);
		friendlist.put(Amigo3.getUsername(),Amigo3);
		// Set the renderer of this user list
		setCellRenderer (new UserListRenderer());
		
		// Refresh the GUI
	    refresh ();
	}
	
	/**
	 * Return the selected user as an object.
	 *
	 * @return   Selected user object.
	 */
	public User getSelectedUser () {
		return (User)getSelectedValue();
	}
	
	/**
	 * Return the selected user's username.
	 *
	 * @return   Selected user's username.
	 */
	public String getSelectedUsername () {
		return ((User)getSelectedValue()).getUsername();
	}	

	/**
	 * Refresh the user list.
	 */
	public void refresh () {
	    if (game != null) {
	    	amigos=new Vector(friendlist.values());
	    	Vector userObjects = amigos;
	    	if (this.excludedUser != null)
	    		userObjects.remove(this.excludedUser);
	    	
			setListData (userObjects);
			repaint();
		}
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
    class UserListRenderer extends JPanel implements ListCellRenderer { 
        
    	private UserLabel  userLabel;
    	private JogreLabel ratingLabel, tablesLabel;
    	
    	// Declare colours
    	private Color bgColour1, bgColour2;        

        /**
         * Constructor for this renderer.
         */
        public UserListRenderer () {
        	// Set layout
        	double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
        	double [][] sizes = {{fill, pref, 36, 4},{pref}}; 
        	setLayout (new TableLayout (sizes));
        	        	
            // Create components and and labels
        	userLabel = new UserLabel ();
        	ratingLabel = new JogreLabel ();   
        	tablesLabel = new JogreLabel ();
        	
        	// Set font of labels
        	ratingLabel.setFont (JogreAwt.LIST_FONT);
        	tablesLabel.setFont (JogreAwt.LIST_FONT);
        	
        	add (userLabel,  "0,0,l,c");
        	add (tablesLabel,   "1,0,r,c");
        	add (ratingLabel,   "2,0,r,c");        	
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
            User user = (User)value;
                        
            if (user != null) {
            	// Extract username
		        String username = user.getUsername();
            	
		        // Create rating square, rating label and username
		        userLabel.refresh (user);
		        ratingLabel.setText(String.valueOf (user.getRating()));
		        
		        // Compute table numbers
		        int [] userTables = game.getTableList().getTableNumsForUser(username);
		        StringBuffer sb = new StringBuffer ();
		        for (int i = 0; i < userTables.length; i++) {
		        	sb.append (String.valueOf(userTables[i]));
		        	if (i < userTables.length - 1)
		        		sb.append (",");
		        }
		        tablesLabel.setText (sb.toString());		        		        
            }
                        
            return this; 
        } 
    }     
}
