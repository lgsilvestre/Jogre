/*
 * JOGRE (Java Online Gaming Real-time Engine) - TicTacToe
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
package org.jogre.tictactoe.client;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

// Model which holds the data for a game of TicTacToe 
public class TicTacToeModel extends JogreModel {
	
	// Declare constans to define model
	public static final int BLANK = -1;
	public static final int X     = 0;
	public static final int O     = 1;	
	
	// model (2 dimensional int array)
	private int [][] data = new int [3][3];
	
	// Constructor
	public TicTacToeModel() {
		super ();
		reset ();
	}
	
	/**
	 * Update the state of the model from the message.
	 * 
	 * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
	 */
	public void setState (XMLElement message) {
		int [] alldata = JogreUtils.convertToIntArray (message.getContent());
		data = JogreUtils.convertTo2DArray (alldata, 3, 3);
		refreshObservers();
	}
	
	// reset the model back to zeros
	public void reset () {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				data [x][y] = BLANK;
			}
		}
		refreshObservers();
	}
	
	// return data at a particular point
	public int getData (int x, int y) {
		return data [x][y];
	}
	
	// set data at a point
	public void setData (int x, int y, int value) {
		data [x][y] = value;
		refreshObservers();		// update any views on this model
	}
	
	// return true if the game is won by this player
	public boolean isGameWon (int player) {	
		// check y axis
		for (int x = 0; x < 3; x++) { 
			int count = 0;
			for (int y = 0; y < 3; y++) {
				if (data[x][y] == player)
					count++;
			}
			if (count == 3) return true;
		}
		// check x axis
		for (int y = 0; y < 3; y++) { 
			int count = 0;
			for (int x = 0; x < 3; x++) {
				if (data[x][y] == player)
					count++;
			}
			if (count == 3) return true;
		}		
		// check diagonals
		if (data[0][0] == player && data [1][1] == player && data [2][2] == player)
			return true;
		if (data[0][2] == player && data [1][1] == player && data [2][0] == player)
			return true;
		return false;		
	}
	
	// return true if all 9 spaces are taken
	public boolean isNoCellsLeft () {
		int count = 0;
		// check each cell
		for (int x = 0; x < 3; x++) { 
			for (int y = 0; y < 3; y++) {
				if (data[x][y] != BLANK)
					count++;
			}		
		}	
		return (count == 9);		// if all 9 spaces filled return true
	}
	
	/**
	 * Flatten tic-tac-toe data.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = new XMLElement (Comm.MODEL);
		
		// Flatten 2d data to a single array and then to a String
		int [] alldata = JogreUtils.convertTo1DArray (data);
		String allDataStr = JogreUtils.valueOf (alldata);
		message.setContent(allDataStr);
		
		return message;
	}
}
