/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for a propinquity attack number which is generated on
 * the server.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommPropinquityMove extends CommTableMessage {

	public  static final String XML_NAME = "propinquity_move";
	private static final String XML_ATT_MOVE = "move";

	private int move; 		// move

	/**
	 * Constructor which creates a move on one of the cells.
	 *
	 * @param move
	 */
	public CommPropinquityMove (int move) {
		this.move = move;
	}

	/**
	 * @param inString
	 */
	public CommPropinquityMove (XMLElement message) {
	    super (message);

		move = message.getIntAttribute(XML_ATT_MOVE);
	}

	/**
	 * Return the move.
	 *
	 * @return
	 */
	public int getMove () {
		return move;
	}

	/**
	 * Flatten the String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
	    XMLElement message = flatten (XML_NAME);
		message.setIntAttribute (XML_ATT_MOVE, move);

		return message;
	}
}