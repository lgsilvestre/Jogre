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
package org.jogre.battleship.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Battleship communication object for placing ships on board
 * 
 * @author Gman
 * @version Alpha 0.2.3
 */
public class CommBattleshipPlaceShip extends CommTableMessage {

	// Xml constants
	public static final String XML_NAME = "battleship_place_ship";
	private static final String XML_ATT_X = "x";
	private static final String XML_ATT_Y = "y";
	private static final String XML_ATT_SIZE = "size";
	private static final String XML_ATT_SHIP = "ship";
	private static final String XML_ATT_HORIZONTAL = "horizontal";
	private static final String XML_ATT_SEAT_NUM = "seatNum";
	
	// Battleship ship placement variables
	private int seatNum = 0;
	private int x = 0;
	private int y = 0;
	private int size = 0;
	private int ship = 0;
	private boolean horizontal = true;
	
	/**
	 * Contructor
	 * 
	 * @param x
	 * @param y
	 * @param size
	 * @param ship
	 * @param horizontal
	 */
	public CommBattleshipPlaceShip(int seatNum, int x, int y, int size, int ship, boolean horizontal) {
		super();
		this.seatNum = seatNum;
		this.x = x;
		this.y = y;
		this.size = size;
		this.ship = ship;
		this.horizontal = horizontal;
	}

	/**
	 * Contstructor
	 * 
	 * @param message
	 */
	public CommBattleshipPlaceShip(XMLElement message) {
		super(message);

		this.seatNum = message.getIntAttribute(XML_ATT_SEAT_NUM);
		this.x = message.getIntAttribute(XML_ATT_X);
		this.y = message.getIntAttribute(XML_ATT_Y);
		this.size = message.getIntAttribute(XML_ATT_SIZE);
		this.ship = message.getIntAttribute(XML_ATT_SHIP);
		this.horizontal = message.getBooleanAttribute(XML_ATT_HORIZONTAL, "true", "false", true);
	}

	/**
	 * Flatten this object
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_SEAT_NUM, seatNum);
		message.setIntAttribute(XML_ATT_X, x);
		message.setIntAttribute(XML_ATT_Y, y);
		message.setIntAttribute(XML_ATT_SIZE, size);
		message.setIntAttribute(XML_ATT_SHIP, ship);
		if (horizontal)
			message.setAttribute(XML_ATT_HORIZONTAL, "true");
		else
			message.setAttribute(XML_ATT_HORIZONTAL, "false");
		return message;
	}

	/**
	 * @return Returns the horizontal.
	 */
	public boolean isHorizontal() {
		return this.horizontal;
	}

	/**
	 * @param horizontal The horizontal to set.
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	/**
	 * @return Returns the ship.
	 */
	public int getShip() {
		return this.ship;
	}

	/**
	 * @param ship The ship to set.
	 */
	public void setShip(int ship) {
		this.ship = ship;
	}

	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return this.size;
	}

	
	/**
	 * @param size The size to set.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return Returns the x.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @param x The x to set.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return Returns the y.
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @param y The y to set.
	 */
	public void setY(int y) {
		this.y = y;
	}

	public int getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}
}