/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import java.util.StringTokenizer;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;

/**
 * Propinquity model.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PropinquityModel extends JogreModel {

	// Declare 2 constants for the players
	public static final int PLAYER_ONE = 0;
	public static final int PLAYER_TWO = 1;

	// declare some constants to show that piece is being used
	public static final int EMPTY = 0;			// empty space
	public static final int NOT_EXIST = -1;		// does not exist at all

	public static final int DEFAULT_NUM_OF_COLS = 9;
	public static final int DEFAULT_NUM_OF_ROWS = 11;
	
	private static final String XML_ATT_DATA = "data";

	private int numOfCols, numOfRows;
	private Vector gridData;
	private int attackNum;

	/**
	 * Constructor for this game data.
	 */
	public PropinquityModel () {
		super (JogreModel.GAME_TYPE_TURN_BASED);

		numOfCols = DEFAULT_NUM_OF_COLS;
		numOfRows = DEFAULT_NUM_OF_ROWS;

		reset ();
	}

	/**
	 * Reset all the board pieces.
	 *
	 * @see org.jogre.common.JogreModel#reset()
	 */
	public void reset() {
		int numOfCells = numOfCols * numOfRows;
		gridData = new Vector (numOfCells);
		for (int i = 0; i < numOfCells; i++) {
			gridData.add (new Cell ());
		}
	}

    /**
     * Player clicks on the grid.
     */
	public void playerMove (int player, int index) {
		// Check that the index is in range
		if (indexIsInRange(index)) {
			Cell cell = (Cell)gridData.get(index);
			// Retrieve the data
			if (cell.getState() == Cell.CELL_BLANK) {
				cell = new Cell (player, attackNum);
				gridData.set (index, cell);	// update vector

				// Update the neighbours of this cell
				updateNeighbours (index);

				refreshObservers();
			}
		}
	}

	/**
	 * Update the neighbours of this particular cell.
	 *
	 * @param index
	 */
	private void updateNeighbours (int index) {
		// Retrieve the cell for this index
		Cell cell = (Cell)gridData.get (index);

		// retrieve neighbours indexes and insert checks to ensure that no
		// wrapping occurs around the various edges
		int c = numOfCols;			//
		int add = (index / numOfCols) % 2;
		int west      = (index % c != 0)                 ? index - 1           : -1;
		int east      = (index % c != c - 1)             ? index + 1           : -1;
		int northwest = (index % c != 0 || add == 1)     ? index - c - 1 + add : -1;
		int northeast = (index % c != c - 1 || add == 0) ? index - c + add     : -1;
		int southwest = (index % c != 0 || add == 1)     ? index + c - 1 + add : -1;
		int southeast = (index % c != c - 1 || add == 0) ? index + c + add     : -1;

		int [] indexes = {west, east, northwest, northeast, southwest, southeast};

		// Loop through each of the neighbouring cells
		for (int i = 0; i < indexes.length; i++) {
			int nIndex = indexes [i];

			// ensure index is in range
			if (nIndex >= 0 && nIndex < gridData.size()) {
				// Retrieve the cell for this neighbour
				Cell nCell = (Cell) gridData.get (nIndex);

				// Does the cell belong to someone?
				if (nCell.getState() > Cell.CELL_BLANK) {

					// Is this cell belong to the same player as before?
					if (cell.getState() == nCell.getState()) {
						nCell = nCell.incrementArmy();
					}
					// Is this cell belong to a different player.  If so is the
					// value of the new cell greater that it.  If so capture it.
					else if (cell.getArmies() > nCell.getArmies()) {
						nCell = nCell.takeOver(cell.getState());
					}

					// Update this new cell.
					gridData.set(nIndex, nCell);
				}
			}
		}
	}

	/**
	 * Return the number of columns.
	 *
	 * @return
	 */
	public int getNumOfCols () {
		return numOfCols;
	}

	/**
	 * Return the number of rows.
	 *
	 * @return
	 */
	public int getNumOfRows () {
		return numOfRows;
	}

	/**
	 * Return the number of total cells.
	 *
	 * @return
	 */
	public int getNumOfCells () {
		return numOfCols * numOfRows;
	}

	/**
	 * Return the number of cells in a particular state.
	 *
	 * @param   State
	 * @return
	 */
	public int getNumOfCells (int state) {
		int count = 0;
		for (int i = 0; i < getNumOfCells (); i++) {
			Cell cell = getGridData (i);
			if (cell.getState() == state)
				count ++;
		}
		return count;
	}

	/**
	 * Return the cell at this point.
	 *
	 * @param index
	 * @return
	 */
	public Cell getGridData (int index) {
		return (Cell)gridData.get (index);
	}

	/**
	 * Set an attack number.
	 *
	 * @param attackNum
	 */
	public void setAttackNum (int attackNum) {
		this.attackNum = attackNum;
		refreshObservers();
	}

	/**
     * Return the number to the user.
     */
	public int getAttackNum () {
		return attackNum;
	}

	/**
	 * Return the number of armies.
	 *
	 * @param player   Player number
	 * @return
	 */
	public int getArmies (int player) {
		int armiesCount = 0;
		for (int i = 0; i < getNumOfCells (); i++) {
			Cell cell = getGridData (i);
			if (cell.getState() == player)
				armiesCount += cell.getArmies();
		}

		return armiesCount;
	}

	/**
	 * Return the number of territories.
	 *
	 * @param player    Player number
	 * @return
	 */
	public int getTerritories (int player) {
		int armiesCount = 0;
		for (int i = 0; i < gridData.size(); i++) {
			Cell cell = getGridData (i);
			if (cell.getState() == player)
				armiesCount += 1;
		}

		return armiesCount;
	}

	/**
	 * @param index
	 * @return
	 */
	public boolean indexIsInRange(int index) {
		return (index >= 0 && index < numOfCols * numOfRows);
	}

	/**
	 * Return the player with the highest score.
	 *
	 * @return
	 */
	public int getPlayerWithTopScore() {
		int score0 = getTerritories(0);
		int score1 = getTerritories(1);
		if (score0 == score1)
			return -1;				// draw
		if (score0 > score1)
			return 0;
		else return 1;
	}

	/* (non-Javadoc)
	 * @see org.jogre.common.JogreModel#flatten()
	 */
	public XMLElement flatten() {
		XMLElement state = new XMLElement (Comm.MODEL);
		StringBuffer sb = new StringBuffer ();
		for (int i = 0; i < getNumOfCells (); i++) {
			sb.append(getGridData(i).flatten() + " ");
		}
		state.setAttribute(XML_ATT_DATA, sb.toString());
		
		return state;
	}

	/* (non-Javadoc)
	 * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
	 */
	public void setState(XMLElement message) {
		reset();
		StringTokenizer st = new StringTokenizer (message.getStringAttribute(XML_ATT_DATA), " ");
		int index = 0;
		while (st.hasMoreElements())
			gridData.set (index++, new Cell (st.nextToken()));
		
		refreshObservers();
	}
	
	/**
	 * String representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		for (int i = 0; i < gridData.size(); i++) {
			Cell cell = getGridData (i);
			sb.append(getGridData(i).flatten() + " ");
			if (i % numOfCols == numOfCols -1)
				sb.append("\n");
		}
		return sb.toString();
	}
}