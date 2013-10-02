/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.common;

import nanoxml.XMLElement;

import java.awt.Point;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting a move for the Triangulum game.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommTriangulumMakeMove extends CommTableMessage {

	// XML information
	public static final String XML_NAME = "specMove";
	public static final String XML_ATT_COL = "c";
	public static final String XML_ATT_ROW = "r";
	public static final String XML_ATT_INDEX = "i";

	// Information about the move to transmit
	private TriangulumPiece piece;
	private int handIndex;
	private Point location;

	/**
	 * Constructor that takes a username, piece, hand index & location.
	 *
	 * @param username      The player making the move
	 * @param piece         The piece to place
	 * @param handIndex     The index in the player's hand that the piece is
	 *                       moving from/to
	 * @param location      The place on the board the piece is moving to, or
	 *                       null if this piece is being put into the player's
	 *                       hand.
	 */
	public CommTriangulumMakeMove (String username, TriangulumPiece thePiece, int handIndex, Point location) {
		super(username);

		this.piece = thePiece;
		this.location = location;
		this.handIndex = handIndex;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param    message     The XML element that is the message.
	 */
	public CommTriangulumMakeMove (XMLElement message) {
		super(message);

		// Get the piece out of the message.
		int val = message.getIntAttribute(TriangulumPiece.XML_ATT_VALUE);
		if (val <= 0) {
			this.piece = null;
		} else {
			this.piece = new TriangulumPiece (message);
		}

		// Get the location out of the message.
		int col = message.getIntAttribute(XML_ATT_COL);
		if (col <= 0) {
			this.location = null;
		} else {
			this.location = new Point (col, message.getIntAttribute(XML_ATT_ROW));
		}

		// Get other info out of the message.
		this.handIndex = message.getIntAttribute(XML_ATT_INDEX);
	}

	/**
	 * Get info from the message.
	 */
	public TriangulumPiece getPiece ()     { return this.piece; }
	public Point            getLocation ()  { return this.location; }
	public int              getHandIndex () { return this.handIndex; }

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = super.flatten(XML_NAME);

		// Add the piece information to the message
		if (piece == null) {
			message.setIntAttribute(TriangulumPiece.XML_ATT_VALUE, -1);
		} else {
			piece.addToXML(message);
		}

		// Add other attributes
		if (location == null) {
			message.setIntAttribute(XML_ATT_COL, -1);
		} else {
			message.setIntAttribute(XML_ATT_COL, location.x);
			message.setIntAttribute(XML_ATT_ROW, location.y);
		}

		message.setIntAttribute(XML_ATT_INDEX, handIndex);

		return message;
	}

}
