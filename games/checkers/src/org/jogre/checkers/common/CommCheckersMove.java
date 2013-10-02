/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
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
package org.jogre.checkers.common;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;
import org.jogre.common.comm.CommTableMessage;

/**
 * Checkers communication object for executing a move.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommCheckersMove extends CommTableMessage {

	// XML attribute names
	public static final String XML_NAME = "checkers_move";
	private static final String XML_ATT_START = "start";
	private static final String XML_ATT_END = "end";

	// data fields
	private int start, end;

	/**
	 * Constructor which takes a start index and end index
	 *
	 * @param start
	 * @param end
	 */
	public CommCheckersMove (int start, int end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Constructor which takes a String
	 *
	 * @param inString
	 * @throws TransmissionException
	 */
	public CommCheckersMove (XMLElement message) {
		this.start = message.getIntAttribute(XML_ATT_START);
		this.end = message.getIntAttribute(XML_ATT_END);
	}

	/**
	 * Return index 1
	 *
	 * @return
	 */
	public int getStart () {
		return start;
	}

	/**
	 * Return index 2
	 *
	 * @return
	 */
	public int getEnd () {
		return end;
	}

	/**
	 * Flatten the String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten (XML_NAME);
		message.setIntAttribute(XML_ATT_START, start);
		message.setIntAttribute(XML_ATT_END, end);

		return message;
	}
}
