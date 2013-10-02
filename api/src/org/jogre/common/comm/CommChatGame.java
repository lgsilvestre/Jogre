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
 * Communication class which is used to broadcast a chat message to all users
 * at a particular game.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommChatGame extends CommGameMessage {

	/** Chat String. */
	protected String chat;

	/**
	 * Constructer for a broadcast message which is sent to everyone.
	 *
	 * @param chat    Chat message from user.
	 */
	public CommChatGame (String chat) {
	    super ();

		this.chat = chat;
	}

	/**
	 * Constructor which creates a CommChatBroadcast object from the flatten ()
	 * method of another CommChatBroadcast object.
	 *
	 * @param message                     XML element version of object.
	 * @throws TransmissionException      Thrown if problem in transmission
	 */
	public CommChatGame (XMLElement message) throws TransmissionException {
	    super (message);

		this.chat = message.getContent();
	}

	/**
	 * Transmittable String representation of this object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = super.flatten (Comm.CHAT_GAME);
		message.setContent (chat);

		return message;
	}

	/**
	 * Return the content of the message.
	 *
	 * @return  Chat message.
	 */
	public String getChat() {
		return chat;
	}
}