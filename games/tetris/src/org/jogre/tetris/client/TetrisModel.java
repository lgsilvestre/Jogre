/*
 * JOGRE (Java Online Gaming Real-time Engine) - Tetris
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
package org.jogre.tetris.client;

import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;

/**
 * The tetris model which contains 2 TetrisPlayerModel's and accessors
 * for each object.  
 * 
 * @author  Bob Marks0036
 * @version Alpha 0.2.3
 */
public class TetrisModel extends JogreModel {

    public static final int MAX_PLAYERS = 2;
    public static final int NO_PLAYER = -1;
    
    private TetrisPlayerModel [] playerModels;
    private int seatNum = NO_PLAYER;
    
    /**
	 * Default constructor for the game
	 */
	public TetrisModel () {
	    // Create player models
	    playerModels = new TetrisPlayerModel [MAX_PLAYERS];
	    for (int i = 0; i < MAX_PLAYERS; i++) {
	        playerModels [i] = new TetrisPlayerModel (i);
	    }
	}
	
	/**
	 * Starts the game.  The model must be passed in the player
	 * number.
	 * 
	 * @param player
	 */
	public void start (int player) {
	    this.seatNum = player;
	    
	    // Start the players model
	    for (int i = 0; i < MAX_PLAYERS; i++) {
	        if (player == i)
	            playerModels [i].start ();
	        else
	            playerModels [i].wipeAll ();
	    }
	}
	
	/**
	 * Return the player model
	 * 
	 * @param index   Player index.
	 * @return        Main grid data.
	 */ 
	public TetrisPlayerModel getPlayerModel (int index) {
	    return playerModels [index];
	}
	
    /**
     * Return the player num.
     * 
     * @return
     */
    public int getSeatNum () {
        return seatNum;
    }
    
    /**
     * Set the player num.
     * 
     * @param player
     */
    public void setSeatNum (int player) {
        this.seatNum = player;
    }

	/** 
	 * This method returns true if the current shape will fit on the screen.
	 * 
	 * @return
	 */
	public boolean shapeFits () {
		return true;
	}

	/** 
	 * This method moves a shape left.
	 */
	public void moveShapeLeft () {
	    getPlayerModel(seatNum).moveShapeLeft();
	}

	/** 
	 * This method moves a shape right.
	 * 
	 * @return Returns false if the shape can't move
	 */
	public void moveShapeRight () {
	    getPlayerModel(seatNum).moveShapeRight();
	}

	/** 
	 * Move shape clockwise.
	 */
	public void moveShapeCW () {
	    getPlayerModel(seatNum).moveShapeCW();
	}

	/** 
	 * Move shape anti clockwise.
	 */
	public void moveShapeACW () {
	    getPlayerModel(seatNum).moveShapeACW();
	}

	/* (non-Javadoc)
	 * @see org.jogre.common.JogreModel#flatten()
	 */
	public XMLElement flatten() {
		XMLElement state = new XMLElement (Comm.MODEL);
		
		for (int i = 0; i < 2; i++) 
			state.addChild(getPlayerModel(i).flatten());
		
		return state;
	}

	/* (non-Javadoc)
	 * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
	 */
	public void setState (XMLElement state) {
		Vector childElms = state.getChildren();
		for (int i = 0; i < childElms.size(); i++) {
			getPlayerModel(i).setState((XMLElement)childElms.get(i));
			getPlayerModel(i).refreshObservers();
		}
	}
}