/*
 * JOGRE (Java Online Gaming Real-time Engine) - Connect4
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
package org.jogre.connect4.client;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Connect 4 model class.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class Connect4Model extends JogreModel {

	// Declare constants to define model
	public static final int COLS = 7;
	public static final int ROWS = 6;
	
	public static final int BLANK = -1;
	public static final int RED   = 0;
	public static final int YELLOW  = 1;
	
	private Point lastMove = null;

	// model (2 dimensional int array)
	private int [][] data = new int [COLS][ROWS];
    
    private static final String XML_ATT_PIECES = "pieces";

	/**
	 * Connect 4 model class.
	 */
	public Connect4Model() {
		super();
		reset ();
	}

	/**
	 * Reset the model back to zeros.
	 */
	public void reset () {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				data [x][y] = BLANK;
			}
		}
		refreshObservers();
	}

	/**
	 * Return data at a particular point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getData (int x, int y) {
		return data [x][y];
	}

	/**
	 * set data at a specified point.
	 * 
	 * @param x
	 * @param value
	 * @return
	 */
	public Point setData (int x, int value) {
		// Find the next available empty space
		for (int y = ROWS - 1; y >= 0; y--) {
			if (getData(x, y) == BLANK) {
				data [x][y] = value;
				this.lastMove = new Point(x, y);
				refreshObservers();		// update any views on this model
				return this.lastMove;
			}
		}
		return null;
	}
	
	/**
	 * Return true / false if the game is won.
	 * 
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isGameWon (int player, int x, int y) {
		// vertical count
		int vCount = 1;
		int hCount = 1;
		int d1Count = 1;
		int d2Count = 1;
		
		// Vertical Count
		for (int c = 1; y-c > -1 && data[x][y-c] == player; c++)
			vCount++;
		for (int c = 1; y+c < ROWS && data[x][y+c] == player; c++)
			vCount++;
		if (vCount == 4)
			return true;
		
		// Horizontal Count
		for (int c = 1; x-c > -1 && data[x-c][y] == player; c++)
			hCount++;
		for (int c = 1; x+c < COLS && data[x+c][y] == player; c++)
			hCount++;
		if (hCount == 4)
			return true;
		
		// Diagonal 1 (Left Slant) Count
		for (int c = 1; x-c > -1 && y-c > -1 && data[x-c][y-c] == player; c++)
			d1Count++;
		for (int c = 1; x+c < COLS && y+c < ROWS && data[x+c][y+c] == player; c++)
			d1Count++;
		if (d1Count == 4)
			return true;
		
		// Diagonal 2 (Right Slant) Count
		for (int c = 1; x+c < COLS && y-c > -1 && data[x+c][y-c] == player; c++)
			d2Count++;
		for (int c = 1; x-c > -1 && y+c < ROWS && data[x-c][y+c] == player; c++)
			d2Count++;
		if (d2Count == 4)
			return true;
		
		return false;
	}

	/**
	 * Return true if all spaces are taken.
	 * 
	 * @return
	 */
	public boolean isNoCellsLeft () {
		int count = 0;
		// check each cell
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (data[x][y] != BLANK)
					count++;
			}
		}
		
		//if all 9 spaces filled return true
		return (count == ROWS * COLS);		
	}
	
	/**
	 * Return the last move.
	 * 
	 * @return
	 */
	public Point getLastMove () {
		return this.lastMove;
	}
    
    /** 
     * Set the state.
     * 
     * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
     */
    public void setState (XMLElement message) {        
        // Retrieve "pieces" attribute and convert from 1d int array to 2d int array
        String pieces = message.getStringAttribute(XML_ATT_PIECES);
        int [] data1D = JogreUtils.convertToIntArray (pieces);
        this.data = JogreUtils.convertTo2DArray(data1D, COLS, ROWS);
        
        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }
    
    /**
     * Flatten the state of the model into an XML object.  
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        // Retrieve empty state message from super class
    	XMLElement state = new XMLElement (Comm.MODEL);
                
        // Flatten 2d data to a 1d array 
        int [] data1D = JogreUtils.convertTo1DArray(data);
        // Flatten 1d array into a space delimited string and set attribute
        state.setAttribute (XML_ATT_PIECES, JogreUtils.valueOf (data1D));

        return state;
    }    
}