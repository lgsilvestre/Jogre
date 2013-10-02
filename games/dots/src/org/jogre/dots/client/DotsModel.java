/*
 * JOGRE (Java Online Gaming Real-time Engine) - Dots
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
package org.jogre.dots.client;

import java.awt.Color;
import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.client.IJogreModel;
import org.jogre.common.JogreModel;
import org.jogre.common.util.GameProperties;

/**
 * Dots model
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class DotsModel extends JogreModel {

	// Cell constants
	public static final int NO_CELL = -1;
	public static final int NO_LOCATION = -1;

	// Player constants
	public static final int PLAYER_ONE = 0;
	public static final int PLAYER_TWO = 1;

	// Cell size constants
	public static final int CELL_SIZE = 15;			// FIXME - is this a view specific constant? 
	public static final int CELL_SPACING = 5;
	public static final int BORDER_WIDTH = 10;

	// Default board size
	public static final int DEFAULT_BOARD_SIZE = 5;

	// data
	private int cols = 0;
	private int rows = 0;
	private DotsCell[][] data = null;

	// mouse data
	private int mouseRow = -1;			// FIXME - is this a view specific constant?
	private int mouseCol = -1;
	private int mouseLocation = -1;

	// mouse pressed data				// FIXME - is this a view specific constant?
	private int pressedRow = 0;
	private int pressedCol = 0;
	private int pressedLocation = 0;
	
	// mouse clicked data				// FIXME - is this a view specific constant?
	private int clickedRow = 0;
	private int clickedCol = 0;
	private int clickedLocation = 0;

	// player (coded to support 2 - 4 players)
	private int players = 2;

	// last move data
	private int lastMoveRow = -1;
	private int lastMoveCol = -1;
	private int lastMoveLocation = -1;

	// player colors
	public static final Color [] seatColor = {
		GameProperties.getPlayerColour(0),
		GameProperties.getPlayerColour(1),
		GameProperties.getPlayerColour(2),
		GameProperties.getPlayerColour(3)
	};

	// last move color
	public static final Color lastMoveColor = Color.orange;

	/**
	 * Default dots model.
	 */
	public DotsModel () {
		this (DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
	}
	
	/**
	 * Default constructor
	 */
	public DotsModel (int cols, int rows) {
		super (IJogreModel.GAME_TYPE_TURN_BASED);
		this.cols = cols;
		this.rows = rows;
//		this.width = (BORDER_WIDTH * 2) + (cols * CELL_SIZE) + ((cols + 1) * CELL_SPACING);
//		this.height = (BORDER_WIDTH * 2) + (rows * CELL_SIZE) + ((rows + 1) * CELL_SPACING);

		reset();
	}

	/**
	 * Reset
	 *
	 * @see org.jogre.common.JogreModel#reset()
	 */
	public void reset () {

		// Clear data and reset cells
		this.data = new DotsCell[this.cols][this.rows];
		for (int c = 0; c < this.cols; c++) {
			for (int r = 0; r < this.rows; r++)
				this.data[c][r] = new DotsCell(c, r);
		}

		this.mouseCol = -1;
		this.mouseRow = -1;
		this.mouseLocation = -1;
		this.lastMoveCol = -1;
		this.lastMoveRow = -1;
		this.lastMoveLocation = -1;

		refreshObservers();
	}

	/**
	 * Get cell column, row and location based on mouse point
	 *
	 * @param p
	 * @return cell column, row and location in int[]
	 */
	public int[] getCellAndLocation(Point p) {
		int[] info = new int[3];
		int col = 0;
		int row = 1;
		int location = 2;
		int spacing = (CELL_SIZE + CELL_SPACING);

		// Get cell index
		info[col] = (p.x - BORDER_WIDTH) / spacing;
		info[row] = (p.y - BORDER_WIDTH) / spacing;
		if (info[row] > (this.rows - 1))
			info[row] = this.rows - 1;
		if (info[col] > (this.cols - 1))
			info[col] = this.cols - 1;

		// Get cell location
		int x = BORDER_WIDTH + (info[col] * spacing);
		int y = BORDER_WIDTH + (info[row] * spacing);
		Point topLeft = new Point(x, y);
		Point topRight = new Point(x + spacing + CELL_SPACING, y);
		Point bottomLeft = new Point(x, y + spacing + CELL_SPACING);
		Point bottomRight = new Point(x + spacing + CELL_SPACING, y + spacing + CELL_SPACING);

		info[location] = DotsCell.LOCATION_BOTTOM;
		double distance = bottomLeft.distance(p) + bottomRight.distance(p);

		if ((bottomLeft.distance(p) + topLeft.distance(p)) < distance) {
			distance = bottomLeft.distance(p) + topLeft.distance(p);
			info[location] = DotsCell.LOCATION_LEFT;
		}
		if ((topLeft.distance(p) + topRight.distance(p)) < distance) {
			distance = topLeft.distance(p) + topRight.distance(p);
			info[location] = DotsCell.LOCATION_TOP;
		}
		if ((topRight.distance(p) + bottomRight.distance(p)) < distance) {
			distance = topRight.distance(p) + bottomRight.distance(p);
			info[location] = DotsCell.LOCATION_RIGHT;
		}
		return info;
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
	public boolean setLocation(int col, int row, int location, int seatNum) {
		if (!this.data[col][row].isFilled(location)) {
			this.data[col][row].fill(location, true);
			this.setLastMove(col, row, location);

			if (this.data[col][row].isFilled())
				this.data[col][row].own(seatNum);

			if (location == DotsCell.LOCATION_BOTTOM && row < (this.rows - 1)) {
				this.data[col][row+1].fill(DotsCell.LOCATION_TOP, true);
				if (this.data[col][row+1].isFilled())
					this.data[col][row+1].own(seatNum);
			} else if (location == DotsCell.LOCATION_TOP && row > 0) {
				this.data[col][row-1].fill(DotsCell.LOCATION_BOTTOM, true);
				if (this.data[col][row-1].isFilled())
					this.data[col][row-1].own(seatNum);
			} else if (location == DotsCell.LOCATION_RIGHT && col < (this.cols - 1)) {
				this.data[col+1][row].fill(DotsCell.LOCATION_LEFT, true);
				if (this.data[col+1][row].isFilled())
					this.data[col+1][row].own(seatNum);
			} else if (location == DotsCell.LOCATION_LEFT && col > 0) {
				this.data[col-1][row].fill(DotsCell.LOCATION_RIGHT, true);
				if (this.data[col-1][row].isFilled())
					this.data[col-1][row].own(seatNum);
			}
			refreshObservers();
			return true;
		}
		return false;
	}

	/**
	 * Check if parameters equal last move.
	 *
	 * @param col
	 * @param row
	 * @param location
	 * @return true if parameters equal last move, false otherwise
	 */
	public boolean isLastMove(int col, int row, int location) {
		return (col == this.lastMoveCol && row == this.lastMoveRow && location == this.lastMoveLocation);
	}

	/**
	 * Set last move parameters
	 *
	 * @param col
	 * @param row
	 * @param location
	 */
	public void setLastMove(int col, int row, int location) {
		this.lastMoveCol = col;
		this.lastMoveRow = row;
		this.lastMoveLocation = location;
	}

	/**
	 * Check if move parameters cause a fill to be made
	 *
	 * @param col
	 * @param row
	 * @param location
	 * @return true if a fill has been caused by parameters, otherwise false
	 */
	public boolean causedFill(int col, int row, int location) {
		if (this.data[col][row].isOwned())
			return true;
		else {
			if (location == DotsCell.LOCATION_BOTTOM && row < (this.rows - 1)) {
				if (this.data[col][row+1].isOwned())
					return true;;
			} else if (location == DotsCell.LOCATION_TOP && row > 0) {
				if (this.data[col][row-1].isOwned())
					return true;
			} else if (location == DotsCell.LOCATION_RIGHT && col < (this.cols - 1)) {
				if (this.data[col+1][row].isOwned())
					return true;
			} else if (location == DotsCell.LOCATION_LEFT && col > 0) {
				if (this.data[col-1][row].isOwned())
					return true;
			}
		}
		return false;
	}

	/**
	 * Set mouse parameters while mouse is moving
	 *
	 * @param col
	 * @param row
	 * @param location
	 */
	public void setMouseLocation(int col, int row, int location) {
		if (col != this.mouseCol || row != this.mouseRow || location != this.mouseLocation) {
			this.mouseCol = col;
			this.mouseRow = row;
			this.mouseLocation = location;
			this.refreshObservers();
		}
	}

	/**
	 * Get data
	 *
	 * @return data
	 */
	public DotsCell[][] getData() {
		return this.data;
	}

	/**
	 * Get number of columns
	 *
	 * @return number of columns
	 */
	public int getCols() {
		return this.cols;
	}

	/**
	 * Get number of rows
	 *
	 * @return number of rows
	 */
	public int getRows() {
		return this.rows;
	}

	/**
	 * Get mouse column
	 *
	 * @return mouse column
	 */
	public int getMouseCol() {
		return this.mouseCol;
	}

	/**
	 * Get mouse row
	 *
	 * @return mouse row
	 */
	public int getMouseRow() {
		return this.mouseRow;
	}

	/**
	 * Get mouse location
	 *
	 * @return mouse location
	 */
	public int getMouseLocation() {
		return this.mouseLocation;
	}

	/**
	 * Set line pressed by mouse
	 *
	 * @param col
	 * @param row
	 * @param location
	 * @return true if line can be pressed, false otherwise
	 */
	public boolean setPressedLine(int col, int row, int location) {
		this.pressedCol = col;
		this.pressedRow = row;
		this.pressedLocation = location;
		return true;
	}

	/**
	 * Set line clicked by mouse
	 *
	 * @param col
	 * @param row
	 * @param location
	 * @return true if line can be clicked, false otherwise
	 */
	public boolean setClickedLine(int col, int row, int location) {
		if (col == pressedCol && row == pressedRow && location == pressedLocation) {
			this.clickedCol = col;
			this.clickedRow = row;
			this.clickedLocation = location;
			return true;
		}
		return false;
	}

	/**
	 * Get number of cells left that are empty
	 *
	 * @return number of cells left that are empty
	 */
	public int emptyCellsLeft() {
		int count = 0;
		for (int c = 0; c < this.cols; c++) {
			for (int r = 0; r < this.rows; r++) {
				if (!this.data[c][r].isOwned())
					count++;
			}
		}
		return count;
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

	/**
	 * Check if game is over
	 *
	 * @return true if game is over, false otherwise
	 */
	public boolean checkGameOver() {
		int emptyCellsLeft = emptyCellsLeft();

		// If no cells won
		if (emptyCellsLeft == 0)
			return true;

		/* This code checks to see if there are enough empty cells
		 * left for the losing player to be able to come back and win.
		 * It there aren't enough empty cells, then technically the game
		 * is over.  This is commented out because we want the players to
		 * fill in the rest of the board in case their ranking is calculated
		 * on how many blocks the player wins by as well as the win.
		 *
		 *
		int[] cellsOwned = new int[this.players];

		// Get seat with most cells owned
		int seatWithMostCells = 0;
		cellsOwned[0] = cellsOwned(0);
		for (int i = 1; i < this.players; i++) {
			cellsOwned[i] = cellsOwned(i);
			if (cellsOwned[i] > cellsOwned[seatWithMostCells])
				seatWithMostCells = i;
		}

		// Check if there are enough empty cells left to win for other players
		for (int i = 0; i < this.players; i++) {
			if (i != seatWithMostCells) {
				if ((cellsOwned[i] + emptyCellsLeft) >= cellsOwned[seatWithMostCells]) {
					System.out.println("cellsOwned[" + i + "]: " + cellsOwned[i]);
					System.out.println("emptyCellsLeft: " + emptyCellsLeft);
					System.out.println("mostCells[" + seatWithMostCells + "]: " + cellsOwned[seatWithMostCells]);
					return false;
				}
			}
		}
		*/

		return false;
	}

	public DotsCell getPossibleCell() {
		for (int c = 0; c < this.cols; c++) {
			for (int r = 0; r < this.rows; r++) {
				if (data[c][r].filledLocations() == 3) {
					return data[c][r];
				}
			}
		}
		return null;
	}

	public void setData(DotsCell[][] data) {
		this.data = data;
		refreshObservers();
	}

	public int getLastMoveColumn() {
		return this.lastMoveCol;
	}

	public int getLastMoveRow() {
		return this.lastMoveRow;
	}

	public int getLastMoveLocation() {
		return this.lastMoveLocation;
	}

	public void setNumOfPlayers(int players) {
		this.players = players;
	}

	public int getNumOfPlayers() {
		return this.players;
	}

	public XMLElement flatten() {
		// FIXME
		return null;
	}

	public void setState(XMLElement message) {
		// FIXME
	}
}
