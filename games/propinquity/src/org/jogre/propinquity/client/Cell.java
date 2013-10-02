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

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Immutable class Cell.
 */
public class Cell {

	public static final int CELL_BLANK     = -1;
	public static final int CELL_PLAYER_1  = 0;
	public static final int CELL_PLAYER_2  = 1;

	public static final int MAX_AMOUNT = 16;
	public static final int EMPTY_AMOUNT = -1;
	
	public static final String [] CELL_STR = {"g", "r"};

	// Fields
	private int state, armies;

	/**
	 * The default constructor creates a blank cell with no amount on it.
	 */
	public Cell () {
		this.state = CELL_BLANK;
		this.armies = EMPTY_AMOUNT;
	}

	/**
	 * This constructor is used for creating a cell with a different state but
	 * contains no amount.
	 *
	 * @param state
	 */
	public Cell (int state) {
		this.state = state;
		this.armies = EMPTY_AMOUNT;
	}

	/**
	 * This constructor is used when a player clicks on a cell.
	 *
	 * @param state
	 * @param armies
	 */
	public Cell (int state, int armies) {
		this.state = state;
		this.armies = armies;
	}
	
	/**
	 * Create cell.
	 * 
	 * @param flatten
	 */
	public Cell (String flatten) {
		if (flatten.startsWith("-")) {
			this.state = CELL_BLANK;
			this.armies = EMPTY_AMOUNT;
		}
		else {
			this.state = flatten.startsWith ("g") ? CELL_PLAYER_1 : CELL_PLAYER_2;
			this.armies = Integer.parseInt(flatten.substring(1));
		}
	}

	/**
	 * Increment the cell by one.
	 *
	 * @return
	 */
	public Cell incrementArmy () {
		if (getArmies() != MAX_AMOUNT)
			return new Cell (getState(), getArmies() + 1);
		return this;
	}

	/**
	 * Take over this cell.
	 *
	 * @param newPlayer
	 * @return
	 */
	public Cell takeOver (int newPlayer) {
		return new Cell (newPlayer, getArmies());
	}

	/**
	 * Return the state of the cell.
	 *
	 * @return
	 */
	public int getState () {
		return state;
	}

	/**
	 * Return the amount of the cell.
	 *
	 * @return
	 */
	public int getArmies () {
		return armies;
	}

	public String toString () {
		return ("State: " + state + " amount: " + armies);
	}
	
	public String flatten () {
		if (state == CELL_BLANK)
			return "-";
		return CELL_STR[state] + getArmies(); 
	}
}
