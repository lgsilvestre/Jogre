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

/**
 * Abstract base class for table messages.  This class is used for creating
 * communication objects between games e.g. CommExecuteChessMove.  Fields
 * which are most commonly used include username, status and table number.
 * 
 * Note: possible future enhancement - put seat number in here and update
 * TableConnectionThread???
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public abstract class CommTableMessage extends CommGameMessage {

	/** No table being used in the message. */
	public static final int NO_TABLE = -1;

	/** Table number. */
	protected int tableNum;

	/** XML attribute for table number. */
	public static final String XML_ATT_TABLE_NUM   = "table";

	/**
	 * Creates a communcation table object with no status.  Note: the table
	 * number must be set using the setTable () method.
	 */
	protected CommTableMessage () {
		super ();

		setTableNum(NO_TABLE);
	}

	/**
	 * Creates a table message with a status.  Note: the table
	 * number must be set using the setTable () method.
	 *
	 * @param status    Integer status code
	 */
	protected CommTableMessage (int status) {
		super (status);

		setTableNum (NO_TABLE);
	}

	/**
	 * Creates a table message with a username.  Note: the table
	 * number must be set using the setTable () method.
	 *
	 * @param status    Integer status code
	 */
	protected CommTableMessage (String username) {
		super (username);

		setTableNum (NO_TABLE);
	}

	/**
	 * Create a table message to go to a particular user.  Note: the table
	 * number must be set using the setTable () method.
	 *
	 * @param status      Integer status code
	 * @param username    Username
	 */
	protected CommTableMessage (int status, String username) {
		super (status, username);

		setTableNum(NO_TABLE);
	}

	/**
	 * Constructor which parses the XMLElement into class fields.
	 *
	 * @param message  Element to parse.
	 */
	protected CommTableMessage (XMLElement message) {
		super (message);			// super class reads username and status

		// read table numbers
		this.tableNum = message.getIntAttribute (XML_ATT_TABLE_NUM, NO_TABLE);
	}

	/**
	 * Set the table number - this is done in the API.
	 *
	 * @param tableNum Set the table number.
	 */
	public void setTableNum (int tableNum) {
		this.tableNum = tableNum;
	}

	/**
	 * Return the table number for this table.
	 *
	 * @return  Table number.
	 */
	public int getTableNum () {
		return tableNum;
	}

	/**
	 * Create an abstract XMLElement which sub classes can use. Note: this
	 * flatten (String name) method isn't part of the ITransmittable interface
	 * but is in here for convenience.
	 *
	 * @param name		Name of the commuication object.
	 * @return          Flat XML version of this object.
	 */
	public XMLElement flatten (String name) {
		// retrieve abstract element from super class
		XMLElement message = super.flatten (name);

		if (tableNum != NO_TABLE)
		    message.setIntAttribute (XML_ATT_TABLE_NUM, tableNum);

		return message;			// return element back to user
	}
}