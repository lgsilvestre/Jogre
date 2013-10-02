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

import java.util.Enumeration;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;
import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.games.Card;
import org.jogre.common.util.JogreLogger;

/**
 * @author Garrett Lehman
 * @version Alpha 0.2.3
 *
 * Dots Cell
 */
public class DotsCell extends CommTableMessage {

	// Xml constants
	public static final String XML_NAME = "dots_cell";
	public static final String XML_ATT_COL = "col";
	public static final String XML_ATT_ROW = "row";
	public static final String XML_ATT_LOCATION_BOTTOM = "bottom";
	public static final String XML_ATT_LOCATION_LEFT = "left";
	public static final String XML_ATT_LOCATION_TOP = "top";
	public static final String XML_ATT_LOCATION_RIGHT = "right";
	public static final String XML_ATT_OWNED_BY = "owned_by";

	// Locations for cell
	public static final int LOCATION_BOTTOM = 0;
	public static final int LOCATION_LEFT = 1;
	public static final int LOCATION_TOP = 2;
	public static final int LOCATION_RIGHT = 3;

	// A cell that is not owned yet
	public static final int NOT_OWNED = -1;

	// Cell data
	private int col = 0;
	private int row = 0;
	private boolean[] filled = null;
	private int ownedBy = NOT_OWNED;

	/**
	 * Default contructor
	 */
	public DotsCell(int col, int row) {
		this.col = col;
		this.row = row;
		this.filled = new boolean[4];
	}

	/**
	 * Contructor that takes a xml message
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public DotsCell(XMLElement message) {
		this.col = message.getIntAttribute(XML_ATT_COL);
		this.row = message.getIntAttribute(XML_ATT_ROW);
		this.filled = new boolean[4];

		this.filled[LOCATION_BOTTOM] = message.getBooleanAttribute(XML_ATT_LOCATION_BOTTOM, "true", "false", false);
		this.filled[LOCATION_LEFT] = message.getBooleanAttribute(XML_ATT_LOCATION_LEFT, "true", "false", false);
		this.filled[LOCATION_TOP] = message.getBooleanAttribute(XML_ATT_LOCATION_TOP, "true", "false", false);
		this.filled[LOCATION_RIGHT] = message.getBooleanAttribute(XML_ATT_LOCATION_RIGHT, "true", "false", false);

		this.ownedBy = message.getIntAttribute(XML_ATT_OWNED_BY);
	}

	/**
	 * Returns if location is filled
	 *
	 * @param location
	 * @return true if location is filled, false otherwise
	 */
	public boolean isFilled(int location) {
		return this.filled[location];
	}

	/**
	 * Fill location with boolean
	 *
	 * @param location
	 * @param fill
	 */
	public void fill(int location, boolean fill) {
		this.filled[location] = fill;
	}

	/**
	 * Check if cell has been filled
	 *
	 * @return true if all locations are filled, false otherwise
	 */
	public boolean isFilled() {
		for (int i = 0; i < 4; i++) {
			if (!this.filled[i])
				return false;
		}
		return true;
	}

	/**
	 * Check if cell is owned by someone
	 *
	 * @param ownedBy
	 */
	public void own(int ownedBy) {
		this.ownedBy = ownedBy;
	}

	/**
	 * Get owner
	 *
	 * @return owner seat number
	 */
	public int getOwnedBy() {
		return this.ownedBy;
	}

	/**
	 * Check if cell is owned
	 *
	 * @return true if cell is owned, false otherwise
	 */
	public boolean isOwned() {
		return this.ownedBy != NOT_OWNED;
	}

	/**
	 * Gets number of lines filled for this cell
	 *
	 * @return number of lines filled for this cell
	 */
	public int filledLocations() {
		int count = 0;
		for (int i = 0; i < 4; i++) {
			if (this.filled[i])
				count++;
		}
		return count;
	}

	/**
	 * Get first missing location
	 *
	 * @return gets first missing location (default is bottom)
	 */
	public int firstMissingLocation() {
		for (int i = 0; i < 4; i++) {
			if (!this.filled[i])
				return i;
		}

		return LOCATION_BOTTOM;
	}

	/**
	 * Get column
	 *
	 * @return column
	 */
	public int getColumn() {
		return this.col;
	}

	/**
	 * Get row
	 *
	 * @return row
	 */
	public int getRow() {
		return this.row;
	}

	/**
	 * Flatten this object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_COL, this.col);
		message.setIntAttribute(XML_ATT_ROW, this.row);
		if (this.filled[LOCATION_BOTTOM])
			message.setAttribute(XML_ATT_LOCATION_BOTTOM, "true");
		if (this.filled[LOCATION_LEFT])
			message.setAttribute(XML_ATT_LOCATION_LEFT, "true");
		if (this.filled[LOCATION_TOP])
			message.setAttribute(XML_ATT_LOCATION_TOP, "true");
		if (this.filled[LOCATION_RIGHT])
			message.setAttribute(XML_ATT_LOCATION_RIGHT, "true");
		message.setIntAttribute(XML_ATT_OWNED_BY, this.ownedBy);
		return message;
	}
}