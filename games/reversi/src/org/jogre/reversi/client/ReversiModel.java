/*
 * JOGRE (Java Online Gaming Real-time Engine) - Reversi
 * Copyright (C) 2005  Ugnich Anton (anton@portall.zp.ua)
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
package org.jogre.reversi.client;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

// Model which holds the data for a game of Reversi
public class ReversiModel extends JogreModel {

	// Declare constants to define model
	public static final int COLS = 8;
	public static final int ROWS = 8;

	public static final int BLANK = -1;
	public static final int BLACK = 0;
	public static final int WHITE = 1;

    private static final String XML_ATT_PIECES   = "pieces";
    
	// model (2 dimensional int array)
	private int[][] data = new int[COLS][ROWS];

	// Constructor
	public ReversiModel() {
		super();
		reset();
	}

	// reset the model back to zeros
	public void reset() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				data[x][y] = BLANK;
			}
		}
		data[3][3] = data[4][4] = WHITE;
		data[3][4] = data[4][3] = BLACK;
		refreshObservers();
	}

	// return data at a particular point
	public int getData(int x, int y) {
		return data[x][y];
	}

	protected int invert(int value) {
		return value == 1 ? 0 : 1;
	}

	// set data at a point
	public boolean setData(int x, int y, int value) {

		int changed = 0, i, n;

		// top
		if (y >= 2 && data[x][y - 1] == invert(value)) {
			n = y;
			for (i = y - 1; i >= 0; i--)
				if (data[x][i] == value) {
					n = i;
					break;
				} else if (data[x][i] == BLANK)
					break;
			for (i = y - 1; i >= n; i--) {
				data[x][i] = value;
				changed++;
			}
		}

		// top, right
		if (y >= 2 && x <= COLS - 3 && data[x + 1][y - 1] == invert(value)) {
			n = 0;
			for (i = 1; x + i < COLS && y - i >= 0; i++)
				if (data[x + i][y - i] == value) {
					n = i;
					break;
				} else if (data[x + i][y - i] == BLANK)
					break;
			for (i = 1; i <= n; i++) {
				data[x + i][y - i] = value;
				changed++;
			}
		}

		// right
		if (x <= COLS - 3 && data[x + 1][y] == invert(value)) {
			n = x;
			for (i = x + 1; i < COLS; i++)
				if (data[i][y] == value) {
					n = i;
					break;
				} else if (data[i][y] == BLANK)
					break;
			for (i = x + 1; i <= n; i++) {
				data[i][y] = value;
				changed++;
			}
		}

		// right, bottom
		if (x <= COLS - 3 && y <= ROWS - 3
				&& data[x + 1][y + 1] == invert(value)) {
			n = 0;
			for (i = 1; x + i < COLS && y + i < ROWS; i++)
				if (data[x + i][y + i] == value) {
					n = i;
					break;
				} else if (data[x + i][y + i] == BLANK)
					break;
			for (i = 1; i <= n; i++) {
				data[x + i][y + i] = value;
				changed++;
			}
		}

		// bottom
		if (y <= ROWS - 3 && data[x][y + 1] == invert(value)) {
			n = y;
			for (i = y + 1; i < ROWS; i++)
				if (data[x][i] == value) {
					n = i;
					break;
				} else if (data[x][i] == BLANK)
					break;
			for (i = y + 1; i <= n; i++) {
				data[x][i] = value;
				changed++;
			}
		}

		// bottom, left
		if (y <= ROWS - 3 && x >= 2 && data[x - 1][y + 1] == invert(value)) {
			n = 0;
			for (i = 1; x - i >= 0 && y + i < ROWS; i++)
				if (data[x - i][y + i] == value) {
					n = i;
					break;
				} else if (data[x - i][y + i] == BLANK)
					break;
			for (i = 1; i <= n; i++) {
				data[x - i][y + i] = value;
				changed++;
			}
		}

		// left
		if (x >= 2 && data[x - 1][y] == invert(value)) {
			n = x;
			for (i = x - 1; i >= 0; i--)
				if (data[i][y] == value) {
					n = i;
					break;
				} else if (data[i][y] == BLANK)
					break;
			for (i = x - 1; i >= n; i--) {
				data[i][y] = value;
				changed++;
			}
		}

		// left, top
		if (x >= 2 && y >= 2 && data[x - 1][y - 1] == invert(value)) {
			n = 0;
			for (i = 1; x - i >= 0 && y - i >= 0; i++)
				if (data[x - i][y - i] == value) {
					n = i;
					break;
				} else if (data[x - i][y - i] == BLANK)
					break;
			for (i = 1; i <= n; i++) {
				data[x - i][y - i] = value;
				changed++;
			}
		}

		if (changed > 0) {
			data[x][y] = value;
			refreshObservers(); // update any views on this model
			return true;
		} else
			return false;
	}
	
	/**
	 * Gets winner based on opposing player.  The method returns who is
	 * the winner of the game.  It first checks if there are any moves
	 * left for the opposing player.  If there isn't it 
	 * @param oppPlayer
	 * @return
	 */
	public int getWinner(int oppPlayer) {
		
		int DRAW = -1;
		int NO_WINNER_YET = -2;
		
		int player = invert(oppPlayer);
		
		int[] count = new int[2];
		boolean[] hasMove = new boolean[2];

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (piecesThatCanBeCaptured(c, r, oppPlayer) > 0)
					hasMove[oppPlayer] = true;
				if (piecesThatCanBeCaptured(c, r, player) > 0)
					hasMove[player] = true;

				if (data[c][r] == WHITE)
					count[WHITE]++;
				else if (data[c][r] == BLACK)
					count[BLACK]++;
				
				if (hasMove[oppPlayer] || hasMove[player])
					return NO_WINNER_YET;
			}
		}
		
		if (count[oppPlayer] > count[player])
			return oppPlayer;
		else if (count[oppPlayer] < count[player])
			return player;
		else
			return DRAW;
	}
	
	/**
	 * Checks to see if seat number has any moves left
	 * 
	 * @param player Player seat number
	 * @return true if seat number has moves left, otherwise false
	 */
	public boolean hasMovesLeft(int player) {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (piecesThatCanBeCaptured(c, r, player) > 0)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets pieces that can be captured from col, row index
	 * 
	 * @param col Column
	 * @param row Row
	 * @param player Player seat number
	 * @return number of pieces that can be captured from col, row index
	 */
	public int piecesThatCanBeCaptured(int col, int row, int player) {
		if (this.data[col][row] != BLANK)
			return 0;
		
		int totalCapture = 0;
		int capture = 0;
		int count = 0;
		boolean possible = false;
		
		// look to bottom right
		capture = 0;
		count = 1;
		possible = false;
		while ((col + count) < COLS && (row + count) < ROWS) {
			if (this.data[col + count][row + count] != BLANK && this.data[col + count][row + count] != player)
				capture++;
			else if (this.data[col + count][row + count] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to bottom
		capture = 0;
		count = 1;
		possible = false;
		while ((row + count) < ROWS) {
			if (this.data[col][row + count] != BLANK && this.data[col][row + count] != player)
				capture++;
			else if (this.data[col][row + count] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to bottom left
		capture = 0;
		count = 1;
		possible = false;
		while ((col - count) > -1 && (row + count) < ROWS) {
			if (this.data[col - count][row + count] != BLANK && this.data[col - count][row + count] != player)
				capture++;
			else if (this.data[col - count][row + count] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to left
		capture = 0;
		count = 1;
		possible = false;
		while ((col - count) > -1) {
			if (this.data[col - count][row] != BLANK && this.data[col - count][row] != player)
				capture++;
			else if (this.data[col - count][row] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to top left
		capture = 0;
		count = 1;
		possible = false;
		while ((col - count) > -1 && (row - count) > -1) {
			if (this.data[col - count][row - count] != BLANK && this.data[col - count][row - count] != player)
				capture++;
			else if (this.data[col - count][row - count] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to top
		capture = 0;
		count = 1;
		possible = false;
		while ((row - count) > -1) {
			if (this.data[col][row - count] != BLANK && this.data[col][row - count] != player)
				capture++;
			else if (this.data[col][row - count] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to top right
		capture = 0;
		count = 1;
		possible = false;
		while ((col + count) < COLS && (row - count) > -1) {
			if (this.data[col + count][row - count] != BLANK && this.data[col + count][row - count] != player)
				capture++;
			else if (this.data[col + count][row - count] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		// look to right
		capture = 0;
		count = 1;
		possible = false;
		while ((col + count) < COLS) {
			if (this.data[col + count][row] != BLANK && this.data[col + count][row] != player)
				capture++;
			else if (this.data[col + count][row] == player) {
				possible = true;
				break;
			}
			else
				break;
			count++;
		}
		if (possible)
			totalCapture += capture;
		
		return totalCapture;
	}

    /**
	 * Returns number of pieces owned by player
	 * 
	 * @param seat number of seat, BLACK or WHITE
     */
    public int piecesOwned(int seat) {
        int count = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
                if (data[r][c] == seat) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Set the checkers pieces from the contents of the message. 
     *
     * @param message    Data stored in message.
     * @throws TransmissionException
     */
    public void setState (XMLElement message) {        
        // Set chess pieces
        int [] data1D = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_PIECES)); 
        this.data = JogreUtils.convertTo2DArray(data1D, COLS, ROWS); 
        
        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }
    
    /**
     * Used for state control of a reversi model. 
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        // Retrieve empty state from super class
    	XMLElement message = new XMLElement (Comm.MODEL);
                
        // Flatten 2d data to a single array and then to a String
        int [] data1D = JogreUtils.convertTo1DArray(data);
        message.setAttribute (XML_ATT_PIECES,   JogreUtils.valueOf (data1D));

        return message;
    }       
}
