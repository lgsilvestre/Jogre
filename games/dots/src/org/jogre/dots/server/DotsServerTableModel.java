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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.dots.server;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.Table;
import org.jogre.common.util.JogreLogger;
import org.jogre.dots.client.DotsCell;
import org.jogre.dots.common.CommDotsSnapShot;

/**
 * This table model keeps track of game data so when someone
 * joins the table in mid-play, the data can be sent to them.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */

public class DotsServerTableModel extends JogreModel {

	private JogreLogger logger = new JogreLogger(this.getClass());

	private int cols = 0;
	private int rows = 0;

    private int lastMoveRow = -1;
	private int lastMoveCol = -1;
	private int lastMoveLocation = -1;
	private DotsCell[][] data = null;

	/**
	 * Contructor that requires table number. Each spades table should contain
	 * one of these server objects in order to keep all variables in server
	 * memory.
	 */
	public DotsServerTableModel(Table table) {
	    int boardSize = Integer.parseInt(table.getProperty("size"));
	    this.cols = boardSize;
	    this.rows = boardSize;

		this.reset();
	}

	/**
	 * Reset all variables.
	 */
	public synchronized void reset() {
		this.lastMoveRow = -1;
		this.lastMoveCol = -1;
		this.lastMoveLocation = -1;
		this.data = null;

		// Clear data and reset cells
		this.data = new DotsCell[this.cols][this.rows];
		for (int c = 0; c < this.cols; c++) {
			for (int r = 0; r < this.rows; r++)
				this.data[c][r] = new DotsCell(c, r);
		}
	}

	/**
	 * Set location of cell
	 *
	 * @param col Column
	 * @param row Row
	 * @param location Location (bottom, left, top, right)
	 * @param seatNum Seat number
	 * @return true if location can be set, otherwise false
	 */
	public synchronized void setLocation(int col , int row, int location, int seatNum) {

		this.data[col][row].fill(location, true);
		this.setLastMove(col, row, location);

		if (this.data[col][row].isFilled())
			this.data[col][row].own(seatNum);

		if (location == DotsCell.LOCATION_BOTTOM && row < (this.rows - 1)) {
			this.data[col][row+1].fill(DotsCell.LOCATION_TOP, true);
			this.setLastMove(col, row+1, location);
			if (this.data[col][row+1].isFilled())
				this.data[col][row+1].own(seatNum);
		} else if (location == DotsCell.LOCATION_TOP && row > 0) {
			this.data[col][row-1].fill(DotsCell.LOCATION_BOTTOM, true);
			if (this.data[col][row-1].isFilled())
				this.data[col][row-1].own(seatNum);
		} else if (location == DotsCell.LOCATION_RIGHT && col < (this.cols - 1)) {
			this.data[col+1][row].fill(DotsCell.LOCATION_LEFT, true);
			this.setLastMove(col+1, row, location);
			if (this.data[col+1][row].isFilled())
				this.data[col+1][row].own(seatNum);
		} else if (location == DotsCell.LOCATION_LEFT && col > 0) {
			this.data[col-1][row].fill(DotsCell.LOCATION_RIGHT, true);
			if (this.data[col-1][row].isFilled())
				this.data[col-1][row].own(seatNum);
		}

		logger.debug("setLocation", "set col: " + col + ", row: " + row + ", loc: " + location + ", seat: " + seatNum);
	}

	private void setLastMove(int col, int row, int location) {
		this.lastMoveCol = col;
		this.lastMoveRow = row;
		this.lastMoveLocation = location;
	}

	public synchronized CommDotsSnapShot getSnapShot() {
		return new CommDotsSnapShot(this.cols, this.rows, lastMoveCol, lastMoveRow, lastMoveLocation, data);
	}

	/**
	 * Get number of cells owned by seat number
	 *
	 * @param seatNum Seat number
	 * @return number of cells owned by seat number
	 */
	public int cellsOwned(int seatNum) {
		int count = 0;
		for (int c = 0; c < this.cols; c++) {
			for (int r = 0; r < this.rows; r++) {
				if (this.data[c][r] != null && this.data[c][r].getOwnedBy() == seatNum)
					count++;
			}
		}
		return count;
	}

	public XMLElement flatten () {
		// FIXME FILL IN
		return null;
	}

	public void setState (XMLElement message) {
		// FIXME FILL IN
	}
}