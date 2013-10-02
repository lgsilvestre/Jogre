/*
 * JOGRE (Java Online Gaming Real-time Engine) - Battleship
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
package org.jogre.battleship.client;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.Player;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Battleship model
 * 
 * @author Gman, JavaRed
 * @version Alpha 0.2.3
 */
public class BattleshipModel extends JogreModel {

	// Cell constants
	public static final int BLANK = 0;
	public static final int MISS = 1;
	public static final int HIT = 2;

	// Ship constants
	public static final int CARRIER = 1;
	public static final int BATTLESHIP = 2;
	public static final int DESTROYER = 3;
	public static final int CRUISER = 4;
	public static final int PTBOAT = 5;
	public static final int GAME_STATE_PLACING_SHIPS = 0;
	public static final int GAME_STATE_PLAYING = 1;
	public static final Point OFFSCREEN_POINT = new Point(-1, -1);	
	public static final int [] SHIP_NAMES = {CARRIER, BATTLESHIP, DESTROYER, CRUISER, PTBOAT};
	public static final int [] SHIP_SIZES = {5, 4, 3, 3, 2};	
	public static final int BOARD_SIZE = 10;	// Board size will always be 10x10 for Battleship

	// XML constants
	private static final String XML_PLAYER = "player";
	private static final String XML_ATT_PLACED_SHIPS = "placed_ships";
	private static final String XML_ATT_HITS_MISSES  = "hits_misses";
	private static final String XML_ATT_SHIPS_PLACED = "ships_placed";
	private static final String XML_ATT_SHIPS_PLACED_HOR = "ships_places_hor";
	
	// Game data
	private int[][][] placedShips = new int[2][BOARD_SIZE][BOARD_SIZE];
	private int[][][] hitsAndMisses = new int[2][BOARD_SIZE][BOARD_SIZE];
	
	// Current ship placement data
	private int[] shipsPlaced = new int[2];
	private boolean[] shipPlacedHorizontally = new boolean[2];
	
	// Game state and seat information
	private int gameState = GAME_STATE_PLACING_SHIPS;
	private int seatNum = Player.NOT_SEATED;
	
	// Points to keep track of
	private Point boardPlacingPoint = null;
	private Point boardFiringPoint = null;
	private Point mousePressedPoint = null;
	
	/**
	 * Constructor
	 */
	public BattleshipModel() {
		super();
	}

	/**
	 * Start method which calls reset method (resets the model)
	 */
	public void start() {
		reset ();
	}

	/**
	 * Reset model
	 */
	public void reset() {
		
		// Reset "placed ships" and "hits and misses"
		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
				for (int p = 0; p < 2; p++) {
					this.placedShips[p][x][y] = BLANK;
					this.hitsAndMisses[p][x][y] = BLANK;
				}
			}
		}
		
		// Reset game state and placing ship index
		this.gameState = GAME_STATE_PLACING_SHIPS;
		this.shipPlacedHorizontally = new boolean[2];
		this.shipsPlaced = new int[2];
		
		// Refresh observers after reset
		refreshObservers();
	}
	
	/**
	 * Check if coordinates are a valid move
	 * 
	 * @param x
	 * @param y
	 * @return true if valid move, false otherwise
	 */
	public boolean isValidMove(int seatNum, int x, int y) {
		if (x > -1 && x < BOARD_SIZE && y > -1 && y < BOARD_SIZE)
			return hitsAndMisses[seatNum][x][y] == BLANK;
		return false;
	}
	
	/**
	 * Set move in battleship
	 * 
	 * @param seatNum
	 * @param x
	 * @param y
	 */
	public void setMove(int seatNum, int x, int y) {
		int opponent = JogreUtils.invert(seatNum);
		if (this.placedShips[opponent][x][y] != BLANK) {
			this.hitsAndMisses[seatNum][x][y] = HIT;
		} else {
			this.hitsAndMisses[seatNum][x][y] = MISS;
		}
		
		refreshObservers();
	}
	
	/**
	 * Check if game has been won
	 * 
	 * @param seatNum
	 * @return true if game has been won by seat num provided, false otherwise
	 */
	public boolean isGameWon(int seatNum) {
		int opponentSeatNum = JogreUtils.invert(seatNum);
		
		for (int i = 0; i < BattleshipModel.SHIP_NAMES.length; i++) {
			if (!this.sunkShip(opponentSeatNum, BattleshipModel.SHIP_NAMES[i]))
				return false;
		}
		return true;
	}
	
	/**
	 * Set game state
	 * 
	 * @param gameState
	 */
	public void setGameState(int gameState) {
		this.gameState = gameState;
	}
	
	/**
	 * Get game state
	 * 
	 * @return game state
	 */
	public int getGameState() {
		return this.gameState;
	}
	
	/**
	 * Get board point based off of mouse x and y
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return board point
	 */
	public Point getBoardPoint(int mouseX, int mouseY) {
		int x = mouseX / (BattleshipBoardComponent.CELL_SIZE + BattleshipBoardComponent.CELL_SPACING);
		int y = mouseY / (BattleshipBoardComponent.CELL_SIZE + BattleshipBoardComponent.CELL_SPACING);
		return new Point(x, y);
	}
	
	/**
	 * Check if placement is valid for a ship
	 * 
	 * @param x
	 * @param y
	 * @param ship
	 * @param horizontal
	 * @return true if ship can be placed, otherwise false
	 */
	public boolean validPlacementForShip(int seatNum, Point p) {

		// Get ship properties
		p = getShipIndexPoint(seatNum, p, isShipPlacedHorizontally(seatNum));
		int x = p.x;
		int y = p.y;
		int size = BattleshipModel.SHIP_SIZES[this.shipsPlaced[seatNum]];
		
		// Check horizontally
		if (this.shipPlacedHorizontally[seatNum]) {

			// Check bounds
			if (x < 0 || y < 0 || (x + size) > BattleshipModel.BOARD_SIZE || y >= BattleshipModel.BOARD_SIZE)
				return false;
			
			// Check if a ship is already placed within the path
			for (int xx = x; xx < x+size; xx++) {
				if (this.placedShips[seatNum][xx][y] != BLANK)
					return false;
			}
		
		// Check vertically
		} else {
			
			// Check bounds
			if (x < 0 || y < 0 || x >= BattleshipModel.BOARD_SIZE || (y + size) > BattleshipModel.BOARD_SIZE)
				return false;
			
			// Check if a ship is already placed within the path
			for (int yy = y; yy < y+size; yy++) {
				if (placedShips[seatNum][x][yy] != BLANK)
					return false;
			}
		}
		
		// Ship placement is valid
		return true;
	}
	
	/**
	 * Place ship
	 * 
	 * @param seatNum
	 * @param p
	 */
	public void placeShip(int seatNum, Point p) {
		int size = BattleshipModel.SHIP_SIZES[this.shipsPlaced[seatNum]];
		int ship = BattleshipModel.SHIP_NAMES[this.shipsPlaced[seatNum]];
		boolean horizontal = this.shipPlacedHorizontally[seatNum];
		placeShip(seatNum, p, size, ship, horizontal);
	}
	
	/**
	 * Place ship
	 * 
	 * @param seatNum
	 * @param p
	 * @param size
	 * @param ship
	 * @param horizontal
	 */
	public void placeShip(int seatNum, Point p, int size, int ship, boolean horizontal) {

		// Get ship properties
		p = getShipIndexPoint(seatNum, p, horizontal);
		int x = p.x;
		int y = p.y;
		
		// Fill in placedShips array with ship name
		if (horizontal) {
			for (int xx = x; xx < x+size; xx++)
				this.placedShips[seatNum][xx][y] = ship;
		} else {
			for (int yy = y; yy < y+size; yy++)
				this.placedShips[seatNum][x][yy] = ship;
		}
		
		// Increment ships placed
		this.shipsPlaced[seatNum]++;		
		this.refreshObservers();
	}
	
	/**
	 * Set mouse board point when placing ships
	 * 
	 * @param boardPlacingPoint
	 */
	public void setBoardPlacingPoint(Point boardPlacingPoint) {
		int seatNum = getSeatNum();
		if (boardPlacingPoint != null && seatNum != Player.NOT_SEATED)
			this.boardPlacingPoint = getShipIndexPoint(seatNum, boardPlacingPoint, isShipPlacedHorizontally(seatNum));
		else
			this.boardPlacingPoint = null;
	}
	
	/**
	 * Set mouse board point when firing at ships
	 * 
	 * @param boardFiringPoint
	 */
	public void setBoardFiringPoint(Point p) {
		if (p != null) {
			if (p.x < (BattleshipModel.BOARD_SIZE + 1))
				p.setLocation(BattleshipModel.BOARD_SIZE + 1, p.y);
			if (p.y < 0)
				p.setLocation(p.x, 0);
			if (p.x > (BattleshipModel.BOARD_SIZE * 2))
				p.setLocation((BattleshipModel.BOARD_SIZE * 2), p.y);
			if (p.y > BattleshipModel.BOARD_SIZE)
				p.setLocation(p.x, BattleshipModel.BOARD_SIZE - 1);
		}
		this.boardFiringPoint = p;
	}
	
	/**
	 * Get ship index point (top left point of ship)
	 * 
	 * @param p
	 * @param shipSize
	 * @return ship index point (top left point of ship)
	 */
	public Point getShipIndexPoint(int seatNum, Point p, boolean horizontal) {
	
		int shipSize = BattleshipModel.SHIP_SIZES[this.shipsPlaced[seatNum]];
		
		if (p.x < 0)
			p.setLocation(0, p.y);
		if (p.y < 0)
			p.setLocation(p.x, 0);
		if (horizontal) {
			if ((p.x + shipSize) >= BOARD_SIZE)
				p.setLocation(BOARD_SIZE - shipSize, p.y);
			if (p.y >= BOARD_SIZE)
				p.setLocation(p.x, BOARD_SIZE - 1);
		} else {
			if (p.x >= BOARD_SIZE)
				p.setLocation(BOARD_SIZE - 1, p.y);
			if ((p.y + shipSize) >= BOARD_SIZE)
				p.setLocation(p.x, BOARD_SIZE - shipSize);
		}
		
		return p;
	}
	
	/**
	 * Get mouse point when placing ships
	 * 
	 * @return mouse placing point
	 */
	public Point getBoardPlacingPoint() {
		return this.boardPlacingPoint;
	}
	
	/**
	 * Get mouse point when firing at ships
	 * 
	 * @return mouse firing point
	 */
	public Point getBoardFiringPoint() {
		return this.boardFiringPoint;
	}
	
	/**
	 * Flip ship while placing the ship on the board
	 */
	public void flipShipPlacing(int seatNum) {
		this.shipPlacedHorizontally[seatNum] = !this.shipPlacedHorizontally[seatNum];
	}
	
	/**
	 * Get boolean telling if ship is horizontal or vertical
	 * while placing the ship on the board
	 * 
	 * @return true if placing horizontal, false otherwise
	 */
	public boolean isShipPlacedHorizontally(int seatNum) {
		return this.shipPlacedHorizontally[seatNum];
	}
	
	/**
	 * Get ship name by index
	 * 
	 * @param shipIndex
	 * @return ship name by index
	 */
	public int getShipName(int shipIndex) {
		return BattleshipModel.SHIP_NAMES[shipIndex];
	}
	
	/**
	 * Get ship index by name
	 * 
	 * @param shipName
	 * @return ship index by name
	 */
	public int getShipIndex(int shipName) {
		for (int i = 0; i < BattleshipModel.SHIP_NAMES.length; i++) {
			if (shipName == BattleshipModel.SHIP_NAMES[i])
				return i;
		}
		return BLANK;
	}
	
	/**
	 * Get current placing ship's name
	 * 
	 * @return current placing ship's name
	 */
	public int getPlacingShipName(int seatNum) {
		return getShipName(this.shipsPlaced[seatNum]);
	}
	
	/**
	 * Get current placing ship's size
	 * 
	 * @return current placing ship's size
	 */
	public int getPlacingShipSize(int seatNum) {
		return BattleshipModel.SHIP_SIZES[this.shipsPlaced[seatNum]];
	}
	
	/**
	 * Set point pressed by mouse
	 * 
	 * @param mousePressedPoint
	 */
	public void setMousePressedPoint(Point mousePressedPoint) {
		if (getSeatNum() != Player.NOT_SEATED)
			this.mousePressedPoint = mousePressedPoint;
		else
			this.mousePressedPoint = null;
	}
	
	/**
	 * Get mouse pressed point
	 * 
	 * @return mouse pressed point
	 */
	public Point getMousePressedPoint() {
		return this.mousePressedPoint;
	}
	
	/**
	 * Get placed ships
	 * 
	 * @return placed ships
	 */
	public int[][][] getPlacedShips() {
		return this.placedShips;
	}
	
	/**
	 * Get hits and misses
	 * 
	 * @return hits and misses
	 */
	public int[][][] getHitsAndMisses() {
		return this.hitsAndMisses;
	}
	
	/**
	 * Set seat number of client player.  This is only used
	 * for client.
	 * 
	 * @param seatNum
	 */
	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}
	
	/**
	 * Get seat number for current client.  This is only used
	 * for client.
	 * 
	 * @return client's seat number
	 */
	public int getSeatNum() {
		return this.seatNum;
	}
	
	/**
	 * Get whether or not ships are still being placed
	 * 
	 * @return true if ships are still being placed, false otherwise
	 */
	public boolean stillPlacingShips() {
		return (stillPlacingShips(0) || stillPlacingShips(1));
	}
	
	/**
	 * Get whether or not ships are still being placed by seat number
	 * 
	 * @param seatNum
	 * @return true if ships are still begin placed by seat number, false otherwise
	 */
	public boolean stillPlacingShips(int seatNum) {
		return this.shipsPlaced[seatNum] < 5;
	}
	
	/**
	 * Get whether or not ship has been sunk
	 * 
	 * @param seatNumOfShipOwner
	 * @param ship
	 * @return true if ship has been sunk, false otherwise
	 */
	public boolean sunkShip(int seatNumOfShipOwner, int ship) {
		int seatNumOfAttacker = JogreUtils.invert(seatNumOfShipOwner);
		
		int size = BattleshipModel.SHIP_SIZES[this.getShipIndex(ship)];
		int count = 0;
		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
				if (this.placedShips[seatNumOfShipOwner][x][y] == ship &&
						this.hitsAndMisses[seatNumOfAttacker][x][y] == HIT) {
					count++;
				}
			}
		}
		
		return (count == size);
	}
	
	/**
	 * Get number of hits on certain ship
	 * 
	 * @param seatNumOfShipOwner
	 * @param ship
	 * @return number of hits on ship
	 */
	public int hitsOnShip(int seatNumOfShipOwner, int ship) {
		int seatNumOfAttacker = JogreUtils.invert(seatNumOfShipOwner);
		
		int count = 0;
		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
				if (this.placedShips[seatNumOfShipOwner][x][y] == ship &&
						this.hitsAndMisses[seatNumOfAttacker][x][y] == HIT) {
					count++;
				}
			}
		}
		
		return count;
	}

	/**
	 * Check if current player has placed a ship by ship number.
	 * 
	 * @param ship
	 * @return the point at which the ship is placed, otherwise false
	 */
	public Point isShipPlaced(int ship) {
		int seatNum = this.getSeatNum();
		if (seatNum == Player.NOT_SEATED)
			return null;
		
		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
				if (this.placedShips[seatNum][x][y] == ship)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	/**
	 * Check if ship placed by current player, given the index,
	 * is horizontal
	 * 
	 * @param ship
	 * @param indexPoint
	 * @return true if given ship at index is horizontal, otherwise false (true = detault)
	 */
	public boolean isHorizontal(int ship, Point indexPoint) {
		int seatNum = this.getSeatNum();
		if (seatNum == Player.NOT_SEATED)
			return false;
		
		int x = indexPoint.x;
		int y = indexPoint.y;
		
		if ((x+1) < BOARD_SIZE && placedShips[seatNum][x+1][y] == ship)
			return true;
		else if ((y+1) < BOARD_SIZE && placedShips[seatNum][x][y+1] == ship)
			return false;
		
		return true;
	}

	/**
	 * Set state.
	 * 
	 * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
	 */
	public void setState(XMLElement message) {
		// Wipe everything
		reset ();
		
		for (int i = 0; i < 2; i++) {
			XMLElement subMessage = (XMLElement)message.getChildren().get(i);
			this.placedShips[i] = JogreUtils.convertTo2DArray(JogreUtils.convertToIntArray(subMessage.getStringAttribute(XML_ATT_PLACED_SHIPS)), BOARD_SIZE, BOARD_SIZE);
			this.hitsAndMisses[i] = JogreUtils.convertTo2DArray(JogreUtils.convertToIntArray(subMessage.getStringAttribute(XML_ATT_HITS_MISSES)), BOARD_SIZE, BOARD_SIZE);			
			this.shipsPlaced = JogreUtils.convertToIntArray(subMessage.getStringAttribute(XML_ATT_SHIPS_PLACED));
			this.shipPlacedHorizontally = JogreUtils.convertToBoolArray(subMessage.getStringAttribute(XML_ATT_SHIPS_PLACED_HOR));
		}			
	}
	
	/**
	 * Flatten model.
	 * 
	 * @see org.jogre.common.JogreModel#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = new XMLElement (Comm.MODEL);
		
		for (int i = 0; i < 2; i++) {
			XMLElement subMessage = new XMLElement (XML_PLAYER);
			
			subMessage.setAttribute(XML_ATT_PLACED_SHIPS,     JogreUtils.valueOf (JogreUtils.convertTo1DArray(placedShips[i])));
			subMessage.setAttribute(XML_ATT_HITS_MISSES,      JogreUtils.valueOf (JogreUtils.convertTo1DArray(hitsAndMisses[i])));
			subMessage.setAttribute(XML_ATT_SHIPS_PLACED,     JogreUtils.valueOf (shipsPlaced));
			subMessage.setAttribute(XML_ATT_SHIPS_PLACED_HOR, JogreUtils.valueOf (shipPlacedHorizontally));
			
			message.addChild(subMessage);
		}
		
		return message;
	}
	
	/**
	 * Return string representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int i = 0; i < 2; i++) {
				for (int x = 0; x < BOARD_SIZE; x++) {
					int hit = hitsAndMisses[i][x][y];
					if (hit == HIT)
						sb.append("x");
					else if (hit == MISS)
						sb.append("o");
					else {
						int ship = placedShips[i][x][y];
						if (ship > 0)
							sb.append(ship);
						else 
							sb.append("-");
					}
				}	
				sb.append("\t");
			}			
			sb.append("\n");
		}
		
		return sb.toString();
	}
}