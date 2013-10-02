/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.camelot.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.camelot.client.CamelotLoc;
import org.jogre.camelot.client.CamelotStep;

import java.util.Vector;
import java.util.ListIterator;
import java.util.Enumeration;

/**
 * Communications object for transmitting a move for Camelot.
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommCamelotMakeMove extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "Camelot_move";
	public static final String XML_ATT_PLAYERID =  "player_id";
	public static final String XML_ATT_FROM_I = "from_i";
	public static final String XML_ATT_FROM_J = "from_j";
	public static final String XML_ATT_TO_I = "to_i";
	public static final String XML_ATT_TO_J = "to_j";
	public static final String XML_ATT_TYPE = "type";
	public static final String XML_ATT_DIRECTION = "direction";
	public static final String XML_ATT_CAPTURED_PIECE = "captured_piece";


	// Move to transmit
	private Vector theMove;
	private int playerId;

	/**
	 * Constructor that takes a username, move and playerId.
	 *
	 * @param username		Username
	 * @param move			The move
	 * @param playerId		The player ID
	 */
	public CommCamelotMakeMove(String username, Vector move, int playerId) {
		super(username);
		this.theMove = move;
		this.playerId = playerId;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommCamelotMakeMove(XMLElement message) {
		super(message);

		// Pull the Player ID out of the message
		this.playerId = message.getIntAttribute(XML_ATT_PLAYERID);

		// Create a new vector for the move
		this.theMove = new Vector();

		// Pull all of the steps out of the message
		Enumeration msgEnum = message.enumerateChildren();
		XMLElement stepEl;
		while (msgEnum.hasMoreElements()) {
			stepEl = (XMLElement) msgEnum.nextElement();
			this.theMove.add (new CamelotStep(
				stepEl.getIntAttribute(XML_ATT_FROM_I),
				stepEl.getIntAttribute(XML_ATT_FROM_J),
				stepEl.getIntAttribute(XML_ATT_TO_I),
				stepEl.getIntAttribute(XML_ATT_TO_J),
				stepEl.getIntAttribute(XML_ATT_TYPE),
				stepEl.getIntAttribute(XML_ATT_DIRECTION),
				stepEl.getIntAttribute(XML_ATT_CAPTURED_PIECE)));
		}
	}

	/**
	 * Get the Move
	 *
	 * @return a move
	 */
	public Vector getMove() {
		return this.theMove;
	}

	/**
	 * Get the player id
	 *
	 * @return the Id
	 */
	public int getPlayerId() {
		return this.playerId;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		XMLElement child;
		ListIterator iter = this.theMove.listIterator();
		CamelotStep step;

		// Set the player id of the move
		message.setIntAttribute(XML_ATT_PLAYERID, playerId);

		while(iter.hasNext()) {
			// Get the next step
			step = (CamelotStep) iter.next();

			// Create a child element for this step
			child = new XMLElement("Step");
			child.setIntAttribute(XML_ATT_FROM_I, step.get_from().get_i());
			child.setIntAttribute(XML_ATT_FROM_J, step.get_from().get_j());
			child.setIntAttribute(XML_ATT_TO_I, step.get_to().get_i());
			child.setIntAttribute(XML_ATT_TO_J, step.get_to().get_j());
			child.setIntAttribute(XML_ATT_TYPE, step.get_type());
			child.setIntAttribute(XML_ATT_DIRECTION, step.get_direction());
			child.setIntAttribute(XML_ATT_CAPTURED_PIECE, step.get_captured_piece());

			// Add the child to the message
			message.addChild(child);
		}

		return message;
	}
}
