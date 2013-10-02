/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.chineseCheckers.common;

import nanoxml.XMLElement;

import java.awt.Point;

import java.util.Vector;
import java.util.ListIterator;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for transmitting a move for Chinese Checkers
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommChineseCheckersMakeMove extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "cc_move";
	public static final String XML_ATT_C = "c";
	public static final String XML_ATT_R = "r";
	public static final String XML_ATT_PLAYERID =  "p";

	// Information about the move to transmit
	private Vector theMove;
	private int playerId;

	/**
	 * Constructor that takes a username, move and playerId.
	 *
	 * @param username      Username
	 * @param theMove       The move to make
	 * @param playerId      The player ID
	 */
	public CommChineseCheckersMakeMove(String username, Vector theMove, int playerId) {
		super(username);
		this.theMove = theMove;
		this.playerId = playerId;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommChineseCheckersMakeMove(XMLElement message) {
		super(message);
		this.theMove = makeMoveVectorFromStrings(
		    message.getStringAttribute(XML_ATT_C),
		    message.getStringAttribute(XML_ATT_R));
		this.playerId = message.getIntAttribute(XML_ATT_PLAYERID);
	}

	/**
	 * Get the move
	 *
	 * @return the move
	 */
	public Vector getMoveVector() { return this.theMove; }

	/**
	 * Get the player id
	 *
	 * @return the Id
	 */
	public int getPlayerSeat() {
		return this.playerId;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		String [] vectStrings = makeVectorStrings(this.theMove);
		message.setAttribute(XML_ATT_C, vectStrings[0]);
		message.setAttribute(XML_ATT_R, vectStrings[1]);
		message.setIntAttribute(XML_ATT_PLAYERID, this.playerId);
		return message;
	}

	/*
	 * Convert two strings into a vector of points.  The two strings represent
	 * the columns and rows of the points to be put into the vector.
	 */
	private Vector makeMoveVectorFromStrings(String colString, String rowString) {
		Vector moveVect = new Vector();

		int [] cols = JogreUtils.convertToIntArray(colString);
		int [] rows = JogreUtils.convertToIntArray(rowString);

		for (int i=0; i<cols.length; i++) {
			moveVect.add(new Point (cols[i], rows[i]));
		}

		return moveVect;
	}

	/*
	 * Convert a Vector of points into two strings.  The first string has the
	 * x-coordinates and the second string has the y-coordinates of the points.
	 *
	 * @param moveVect    The vector of Points.
	 * @return an array of two strings.
	 */
	private String [] makeVectorStrings(Vector moveVect) {
		String [] result = new String [2];
		result[0] = "";
		result[1] = "";

		ListIterator iter = moveVect.listIterator();
		while (iter.hasNext()) {
			Point nextPoint = (Point) iter.next();
			result[0] += nextPoint.x + " ";
			result[1] += nextPoint.y + " ";
		}
		return result;
	}
}
