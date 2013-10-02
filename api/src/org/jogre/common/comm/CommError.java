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

import org.jogre.common.IError;

import nanoxml.XMLElement;

/**
 * Communications class which is used to send an error from the server to a
 * client or vice-versa.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommError extends CommGameMessage implements IError {

	private String description = null;		// not always needed
	
	private static final String XML_ATT_DESCRIPTION = "description";
	
	/**
	 * Constructor to an error message.
	 *
	 * @param errorCode
	 */
	public CommError (int errorCode) {
		super (errorCode);
	}
	
	/**
	 * Construct an error message which also takes a description
	 * 
	 * @param errorCode
	 * @param description
	 */
	public CommError (int errorCode, String description) {
		this (errorCode);
		this.description = description;
	}

	/**
	 * Constructor which creates a CommError object from the flatten ()
	 * method of another CommError object.
	 *
	 * @param message   Communication message as XML.
	 */
	public CommError (XMLElement message) {
		super (message);
		this.description = message.getStringAttribute(XML_ATT_DESCRIPTION);
	}
	
	/**
	 * Return description if it exists.
	 * 
	 * @return
	 */
	public String getDescription () {
		return this.description;
	}

	/**
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = flatten (Comm.ERROR);
		if (description != null)
			message.setAttribute(XML_ATT_DESCRIPTION, description);
		return message; 
	}
}