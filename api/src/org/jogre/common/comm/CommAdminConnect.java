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
 * Sends a connect message to a JOGRE server for administrators.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommAdminConnect extends CommGameConnect {

	/**
	 * Constructor which takes a username, gameID but no password.
	 *
	 * @param username    Username of person logging on.
	 * @param gameID      Game ID i.e. game-version (e.g. chess-0.2).
	 */
	public CommAdminConnect (String username, String password) {
	    super (username);

	    this.password = password;
	}
	
	/**
	 * Constructor which creates a CommConnect object from the flatten ()
	 * method of another CommConnect object.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public CommAdminConnect (XMLElement message) throws TransmissionException {
		super (message);
	}

	/**
	 * Flatten the connect object into a XML communication object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
	    XMLElement message = super.flatten (Comm.ADMIN_CONNECT);
	    message.setAttribute (XML_ATT_PASSWORD, password);
	    
	    return message;
	}
}
