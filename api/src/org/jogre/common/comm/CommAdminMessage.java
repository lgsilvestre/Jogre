package org.jogre.common.comm;

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

import java.util.Enumeration;

import nanoxml.XMLElement;

/**
 * Message designed to go the admin logged in at the server administrator client.  
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommAdminMessage extends CommGameMessage {

	// Private data objects
	private String     gameID;
	private XMLElement message;
	
	public static final String XML_ATT_GAME_ID     = "game_id";
	
	/**
	 * Constructor which takes a gameID, username and a message.
	 * 
	 * @param gameID
	 * @param username
	 * @param message
	 */
	public CommAdminMessage (String gameID, String username, XMLElement message) {
		super (username);		
		
		this.gameID             = gameID;
		this.message            = message;
	}
	
	/**
	 * Constructor which takes a XMLElement and creates a Java object.
	 * 
	 * @param message   Message in XML format.
	 */
	public CommAdminMessage (XMLElement message) {
		super (message);
		
		this.gameID             = message.getStringAttribute(XML_ATT_GAME_ID);		
		
		// Read children into message
		Enumeration e = message.enumerateChildren();
		if (e.hasMoreElements()) 
			this.message = (XMLElement)e.nextElement();
	}
	
	/**
	 * Return the return the game ID.
	 * 
	 * @return   Game ID.
	 */
	public String getGameID () {
		return this.gameID;
	}
	
	/**
	 * Return the message as an XML message.
	 * 
	 * @return  Message in XML.
	 */
	public XMLElement getMessage () {
		return message;
	}
	
	/**
	 * Flatten method to an XMLMessage.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Flatten and make the message be a child
		XMLElement flatMessage = super.flatten (Comm.ADMIN_DATA_MESSAGE);
		if (gameID != null)
			flatMessage.setAttribute (XML_ATT_GAME_ID, gameID);
		
		flatMessage.addChild (message);
		
		return flatMessage;
	}
}