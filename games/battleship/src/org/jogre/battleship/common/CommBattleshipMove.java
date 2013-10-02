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
public class CommBattleshipMove extends CommTableMessage {

	// Xml constants
	public static final String XML_NAME = "battleship_move";
	private static final String XML_ATT_X = "x";
	private static final String XML_ATT_Y = "y";
	private static final String XML_ATT_SEAT_NUM = "seat";
	
	// Battleship ship placement variables
	private int x = 0;
	private int y = 0;
	private int seatNum = -1;
	
	/**
	 * Contructor
	 * 
	 * @param x
	 * @param y
	 */
	public CommBattleshipMove(int seatNum, int x, int y) {
		super();
		this.seatNum = seatNum;
		this.x = x;
		this.y = y;
	}

	/**
	 * Contstructor
	 * 
	 * @param message
	 */
	public CommBattleshipMove(XMLElement message) {
		super(message);

		this.seatNum = message.getIntAttribute(XML_ATT_SEAT_NUM);
		this.x = message.getIntAttribute(XML_ATT_X);
		this.y = message.getIntAttribute(XML_ATT_Y);
	}

	/**
	 * Flatten this object
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_X, x);
		message.setIntAttribute(XML_ATT_Y, y);
		message.setIntAttribute(XML_ATT_SEAT_NUM, seatNum);
		return message;
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

	/**
	 * Get seat num.
	 * 
	 * @return
	 */
	public int getSeatNum() {
		return seatNum;
	}

	/**
	 * Set seat num
	 * 
	 * @param seatNum
	 */
	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}	
}