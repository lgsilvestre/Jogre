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
 * Chat communications object for chat at a table.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommChatTable extends CommTableMessage {

	/** Chat String. */
	protected String chat;

	/**
	 * Constructor which takes a message.
	 *
	 * @param chat
	 */
	public CommChatTable (String chat) {
		super ();		// omit person sending the message

		this.chat = chat;
	}

	/**
	 * Constructor which creates a CommChatRoom object from the flatten ()
	 * method of another CommChatRoom object.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public CommChatTable (XMLElement message) throws TransmissionException {
		super (message);

		this.chat = message.getContent();

		if (!message.getName().equals(Comm.CHAT_TABLE))
			throw new TransmissionException ("Error parsing Comm");
	}

	/**
	 * Return the text of the message.
	 *
	 * @return   Chat message from user.
	 */
	public String getChat() {
		return chat;
	}

	/**
	 * Transmittable String representation of this object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = super.flatten (Comm.CHAT_TABLE);
		message.setContent (chat);

		return message;
	}
}