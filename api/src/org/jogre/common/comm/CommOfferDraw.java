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
 * Communications object which is used when one user offers a draw to another
 * user.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommOfferDraw extends CommTableMessage {

	// The serial number of this draw offer message
	int serialNumber = 0;

	public static final String XML_ATT_SERIAL_NUM = "sn";

	/**
	 * Constructor which is called from the server and the client who is
	 * offering the draw - they are unsure of who the draw is being offered to
	 * so the usernameTo (from CommInvite) isn't used.

	 * @param status
	 */
	public CommOfferDraw (int status) {
		super (status);
	}

	/**
	 * Constructor for a CommOfferDraw object.  This constructor is created
	 * from the client.  The additional field is the usernameTo.
	 *
	 * @param status       Status of the message i.e. request, accept & decline.
	 * @param usernameTo   Username that is being offered the draw to.
	 */
	public CommOfferDraw (int status, String username) {
		super (status, username);
	}

	/**
	 * Constructor which creates a CommOfferDraw object from the flatten ()
	 * method of another CommInvite object.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public CommOfferDraw (XMLElement message) throws TransmissionException {
		super (message);

		if (!message.getName().equals(Comm.OFFER_DRAW))
			throw new TransmissionException ("Error parsing CommOfferDraw");

		serialNumber = message.getIntAttribute(XML_ATT_SERIAL_NUM, 0);
	}

	/**
	 * Set the serial number of the offer message.
	 *
	 * @param newSerialNum    The new serial number of the message.
	 */
	public void setSerialNum (int newSerialNum) {
		serialNumber = newSerialNum;
	}

	/**
	 * Get the serial number of the offer message.
	 */
	public int getSerialNum () {
		return serialNumber;
	}

	/**
	 * Flatten the String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = super.flatten (Comm.OFFER_DRAW);

		message.setIntAttribute (XML_ATT_SERIAL_NUM, serialNumber);

		return message;
	}
}
