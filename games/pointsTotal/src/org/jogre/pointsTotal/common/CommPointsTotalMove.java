/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
 * Copyright (C) 2003 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.pointsTotal.common;

import nanoxml.XMLElement;

import java.awt.Point;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a move for Points Total
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommPointsTotalMove extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "ptMove";
	public static final String XML_ATT_C = "c";
	public static final String XML_ATT_R = "r";
	public static final String XML_ATT_ROT = "t";
	public static final String XML_ATT_VALUE = "v";

	// Location to transmit
	private Point location;
	private PointsTotalPiece thePiece;

	/**
	 * Constructor that takes a username, location and playerId.
	 *
	 * @param username      Username
	 * @param thePiece      The piece to play
	 * @param location      The location to play
	 */
	public CommPointsTotalMove(String username, PointsTotalPiece thePiece, Point location) {
		super(username);
		this.thePiece = thePiece;
		this.location = location;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param   message     The XML message
	 */
	public CommPointsTotalMove(XMLElement message) {
		super(message);
		this.location = new Point (message.getIntAttribute(XML_ATT_C),
		                           message.getIntAttribute(XML_ATT_R));
		this.thePiece = new PointsTotalPiece(
		                           0,
		                           message.getIntAttribute(XML_ATT_VALUE),
		                           message.getIntAttribute(XML_ATT_ROT));
	}

	/**
	 * Get information from the move
	 *
	 * @return the location
	 */
	public Point            getLocation() { return this.location; }
	public PointsTotalPiece getPiece()    { return this.thePiece; }


	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_C, this.location.x);
		message.setIntAttribute(XML_ATT_R, this.location.y);
		message.setIntAttribute(XML_ATT_ROT, this.thePiece.rotation);
		message.setIntAttribute(XML_ATT_VALUE, this.thePiece.value);
		return message;
	}
}
