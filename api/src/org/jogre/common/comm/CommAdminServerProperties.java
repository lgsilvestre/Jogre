/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
 * Communications message for transporting a server properties. 
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommAdminServerProperties extends CommGameMessage {

	// String object hold parsed / formatted XML describing the server properties
	// It is held as a String to preserve its formatting (i.e. a user may wish to 
	// simply use a text editor to update the  server properties and not use the
	// Jogre administrator.
	public String serverPropertiesStr;
	
	/**
	 * Constructor which describes the server properties.
	 * 
	 * @param serverPropertiesElm
	 */
	public CommAdminServerProperties (XMLElement message) {
		this.serverPropertiesStr = message.getContent();
	}
	
	/**
	 * Return the server properties elm.
	 * 
	 * @return
	 */
	public String getServerPropertiesStr () {
		return this.serverPropertiesStr;
	}
	
	/**
	 * Flatten the object to an XML object.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = new XMLElement (Comm.ADMIN_SERVER_PROPERTIES);
		message.setContent(serverPropertiesStr);
		
		return message;
	}
}