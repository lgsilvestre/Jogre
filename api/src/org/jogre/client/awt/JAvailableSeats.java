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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jogre.common.IJogre;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;

/**
 * Alternative component to JAvailableSeats.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JAvailableSeats extends JPanel implements Observer {

	// Declare data fields
	private PlayerList players;
	private Player     player;
	private int numPlayers = IJogre.DEFAULT_NUM_OF_PLAYERS;
	
	// Declare GUI objects
	private ColourSquare [] colourSquares;
	
	// Declare other fields
	private int selectedSeat = -1;
	
	/**
	 * Constructor which takes a username and a players object.
	 * 
	 * @param username  Username of player.
	 * @param players   Players list.
	 */
	public JAvailableSeats (Player player, PlayerList players, Table table) {
		// Set fields
		this.player   = player;
		this.players  = players;
		this.players.addObserver (this);
		this.selectedSeat = -1;
		this.numPlayers = table.getNumOfPlayers();

		// Set GUI objects		
		setUpGUI ();
	}
	
	/**
	 * Set up the GUI elements.
	 */
	private void setUpGUI () {
		// Set layout of panel
		double pref = TableLayout.PREFERRED;
		double [][] sizes = new double [2][];
		sizes [0] = new double [numPlayers];
		sizes [1] = new double [1];
		
		// Set all columns and rows to pref
		for (int i = 0; i < numPlayers; i++)
			sizes [0][i] = pref;			
		sizes [1][0] = pref;
		
		TableLayout layout = new TableLayout (sizes);
		this.setLayout (layout);
		
		// Set up coloured square components
		colourSquares = new ColourSquare [numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			// Create component and set colour
			colourSquares [i] = new ColourSquare (i, GameProperties.getPlayerColour(i));
			colourSquares [i].setCursor (new Cursor (Cursor.HAND_CURSOR));
			
			// Add to this panel
			add (colourSquares [i], "" + i + ",0");
		}
		
		refresh ();
	}
	
	/**
	 * @return
	 */
	public int getSelectedSeat () {
		return selectedSeat;
	}

    /**
     * Little coloured square beside text.
     * 
     * TODO - Can this extend the ColouredSquare class???
     */
    class ColourSquare extends JComponent implements MouseListener {
    	
    	public static final int SIZE = 24;
    	private int index;
    	
    	/**
    	 * Constructor which takes an inner colour.
    	 * 
    	 * @param innerColour
    	 */
    	public ColourSquare (int index, Color innerColour) {
    		this.index = index;
    		
    		// Add a mouse listener
    		addMouseListener (this);
    		
    		// Add a tool tip
    		setToolTipText (GameLabels.getPlayerLabel(index));
    		
    		// Set innert colour, size and then repaint.
    		setPreferredSize (new Dimension (SIZE, SIZE)); 
    	}
    	
    	/* (non-Javadoc)
    	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    	 */
    	public void paintComponent (Graphics g) {
    		g.setColor (GameProperties.getBackgroundColour()); 
    		g.fillRect (0, 0, SIZE, SIZE);
    		
    		boolean drawBox = players.isSeatFree(index);
    		if (!drawBox)
    			drawBox = player.getSeatNum() == index;
    				
    		if (drawBox) {
	    		g.setColor (Color.black); 
	    		g.drawRect (1, 1, SIZE - 3, SIZE - 3);
	    		
	    		g.setColor (GameProperties.getPlayerColour(index)); 
	    		g.fillRect (3, 3, SIZE - 6, SIZE - 6);
    		}
    		
    		// Either draw a big box or an outline only
    		g.setColor (GameProperties.getBackgroundColour());
    		if (player.getSeatNum() == index) {
    			// Reset seat 
    			selectedSeat = -1;
    			g.fill3DRect (8, 8, 8, 8, false);
    			g.setColor (GameProperties.getPlayerColour(index));
    			g.fill3DRect (10, 10, 4, 4, true);
    		}
    		else if (index == selectedSeat) {
    			g.drawRect(8, 8, 7, 7);
    		}
    	}
    	
    	/**
    	 * Respond to a mouseClick event.
    	 * 
    	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
    	 */
    	public void mouseClicked (MouseEvent e) {
   			if (!player.isSeated()) {
	    		if (players.isSeatFree(index)) {
	        		selectedSeat = index;
	        	}
	        	refresh();			// Refresh all components
    		}
    	}    	
    	public void mouseEntered (MouseEvent e) {}
    	public void mouseExited (MouseEvent e) {}
    	public void mousePressed (MouseEvent e) {}
    	public void mouseReleased (MouseEvent e) {}    	
    }
	
    /**
     * Refresh the boxes.
     */
    private void refresh () {
    	// If a seat isn't selected or a seat isn't free AND a user has sat down yet
    	if ((selectedSeat == -1 || !players.isSeatFree(selectedSeat)) && 
    	    player.getSeatNum() == Player.NOT_SEATED) {
    		for (int i = 0; i < numPlayers; i++) {
    			if (players.isSeatFree(i)) {
    				selectedSeat = i;
    				break;
    			}
    		}
    	}
    	
    	// Refresh the components
    	for (int i = 0; i < numPlayers; i++) {
			colourSquares [i].repaint ();				
		}
    }
    
    /**
     * Refresh the available seats component.
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    	refresh ();
    }
}
