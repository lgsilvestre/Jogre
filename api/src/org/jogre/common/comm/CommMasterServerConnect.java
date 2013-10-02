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
 * Communication object for connecting to the master server.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommMasterServerConnect extends CommGameMessage {

	private String serverName;

	private static final String XML_ATT_SERVER_NAME = "serverName";

	/**
	 * Constructor for connecting to the server.
	 *
	 * @param serverName  Name of the server.
	 */
	public CommMasterServerConnect (String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @param message
	 */
	public CommMasterServerConnect (XMLElement message) {
		this.serverName = message.getStringAttribute (XML_ATT_SERVER_NAME);
	}

	/**
	 * Return the server name.
	 *
	 * @return  Name of the server.
	 */
	public String getServerName () {
		return serverName;
	}

	/**
	 * Flatten this object into an XML String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message  = super.flatten (Comm.MASTER_SERVER_CONNECT);
		message.setAttribute (XML_ATT_SERVER_NAME, serverName);

		return message;
	}
}
