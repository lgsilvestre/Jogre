/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.common.comm;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;

/**
 * Communication class which is used to invite a user to a table.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommInvite extends CommGameMessage {

	/** User requests invite to a user. */
	public static final int REQUEST = 1;

	/** User accepts invite from a user. */
	public static final int ACCEPT = 2;

	/** User declines invite from. */
	public static final int DECLINE = 3;

	/** Table number. */
	protected int tableNum;

	// Declare XML attributes
	private static final String XML_ATT_TABLE_NUM   = "tableNum";

	/**
	 * Constructor which takes a status and table number.
	 *
	 * @param status       Status of the message i.e. request, accept & decline.
	 * @param tableNum     Table number.
	 */
	public CommInvite (int status, int tableNum) {
	    this (status, tableNum, null);
	}

	/**
	 * Constructor for a CommInvite object which is used to invite a user to
	 * a particular table.
	 *
	 * @param status       Status of the message i.e. request, accept & decline.
	 * @param tableNum     Table number.
	 * @param usernameTo   The user that is being invited.
	 */
	public CommInvite (int status, int tableNum, String usernameTo) {
		super (status);
		setUsernameTo (usernameTo);

		// set fields
		this.tableNum = tableNum;
	}

	/**
	 * Constructor which creates a CommInvite object from the flatten () method of
	 * another CommInvite object.
	 *
	 * @param message                     XML element version of object.
	 * @throws TransmissionException      Thrown if problem in transmission
	 */
	public CommInvite (XMLElement message) throws TransmissionException {
		super (message);

		if (!message.getName().equals(Comm.INVITE))
			throw new TransmissionException ("Error parsing invite string.");

		this.tableNum = message.getIntAttribute (XML_ATT_TABLE_NUM);
	}

	/**
	 * Flatten this offer draw object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = flatten (Comm.INVITE);
		message.setIntAttribute (XML_ATT_TABLE_NUM, tableNum);

		return message;				// flatten XML to a String
	}

	/**
	 * Return the table number.
	 *
	 * @return   Table number.
	 */
	public int getTableNum () {
		return tableNum;
	}
}