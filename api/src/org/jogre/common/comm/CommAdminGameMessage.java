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
 * Message designed to go the admin logged in at the server administrator client.  
 * This message shows all "game" traffic which the JogreServer sends out.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommAdminGameMessage extends CommAdminMessage {

	private boolean isReceivingMessage;		// if false - then "sending" message
	
	public static final String XML_ATT_IS_RECV = "is_recv";
	
	/**
	 * Constructor which takes a gameID, username and a message.
	 * 
	 * @param gameID
	 * @param username
	 * @param message
	 * @param sent 
	 */
	public CommAdminGameMessage (boolean isReceivingMessage, String gameID, String username, XMLElement message) {
		super (gameID, username, message);		
		
		this.isReceivingMessage = isReceivingMessage;
	}
	
	/**
	 * Constructor which takes a XMLElement and creates a Java object.
	 * 
	 * @param message   Message in XML format.
	 */
	public CommAdminGameMessage (XMLElement message) {
		super (message);
		
		this.isReceivingMessage = message.getAttribute(XML_ATT_IS_RECV).equals("true");
	}
	
	/**
	 * Return true/false if this is a receiving message.
	 * 
	 * @return   True if receiving message / false if sending message.
	 */
	public boolean isReceivingMessage () {
		return this.isReceivingMessage;
	}
	
	/**
	 * Flatten method to an XMLMessage.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Flatten and make the message be a child
		XMLElement flatMessage = super.flatten ();
		flatMessage.setName (Comm.ADMIN_GAME_MESSAGE);
		flatMessage.setAttribute (XML_ATT_IS_RECV, String.valueOf(isReceivingMessage));
		
		return flatMessage;
	}
}