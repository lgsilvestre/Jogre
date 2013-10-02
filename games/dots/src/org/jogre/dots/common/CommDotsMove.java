/*
 * JOGRE (Java Online Gaming Real-time Engine) - DOTS
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
package org.jogre.dots.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Dots communication move object
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class CommDotsMove extends CommTableMessage {

	// XML attribute names
	public static final String XML_NAME = "dots_move";
	private static final String XML_ATT_COL = "col";
	private static final String XML_ATT_ROW = "row";
	private static final String XML_ATT_LOCATION = "location";

	// data fields
	private int col = 0;
	private int row = 0;
	private int location = 0;

	/**
	 * Constructor that takes column, row and location
	 *
	 * @param col
	 * @param row
	 * @param location
	 */
	public CommDotsMove (String username, int col, int row, int location) {
		super(username);
		this.col = col;
		this.row = row;
		this.location = location;
	}

	/**
	 * Constructor that takes xml element
	 *
	 * @param message
	 */
	public CommDotsMove (XMLElement message) {
		super(message);
		this.col = message.getIntAttribute(XML_ATT_COL);
		this.row = message.getIntAttribute(XML_ATT_ROW);
		this.location = message.getIntAttribute(XML_ATT_LOCATION);
	}

	/**
	 * Get column
	 *
	 * @return column
	 */
	public int getColumn () {
		return this.col;
	}

	/**
	 * Get row
	 *
	 * @return row
	 */
	public int getRow () {
		return this.row;
	}

	/**
	 * Get location
	 *
	 * @return location
	 */
	public int getLocation () {
		return this.location;
	}

	/**
	 * Flatten the String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten (XML_NAME);
		message.setIntAttribute(XML_ATT_COL, this.col);
		message.setIntAttribute(XML_ATT_ROW, this.row);
		message.setIntAttribute(XML_ATT_LOCATION, this.location);

		return message;
	}
}