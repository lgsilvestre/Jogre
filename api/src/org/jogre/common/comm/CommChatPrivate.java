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

import org.jogre.common.TransmissionException;

import nanoxml.XMLElement;

/**
 * Communication class which is used to send a private message between users.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommChatPrivate extends CommChatClient {

	/**
	 * Constructer for a private message which takes a message and a usernameTo.
	 *
	 * @param chat          Chat message from user.
	 * @param usernameTo    Name of user the chat is going to.
	 */
	public CommChatPrivate (String chat, String usernameTo) {
		super (chat);

		setUsernameTo (usernameTo);
	}

	/**
	 * Constructor which takes an XMLElement.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public CommChatPrivate (XMLElement message) throws TransmissionException {
	    super (message);
	}

    /**
     * Flatten this object class to an XML message.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten() {
        XMLElement element = super.flatten ();
        element.setName (Comm.CHAT_PRIVATE);

        return element;
    }
}
