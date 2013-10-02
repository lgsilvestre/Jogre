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
 * Small communication object which is used when a client sits down on
 * a particular seat at a table. If the seat is free the player list is
 * updated and notifications are sent to all players at this table.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommSitDown extends CommTableMessage {

    /** Seat number of where the user wishes to sit down at. */
    private int seatNum;

    private static final String XML_ATT_SEAT = "seat";

    /**
     * Constructor for requesting joining an existing table.
     */
    public CommSitDown (int seatNum) {
        super ();

        this.seatNum = seatNum;
    }

    /**
     * Constructor which takes an XMLElement.
     *
     * @param message
     */
    public CommSitDown (XMLElement message) {
        super (message);

        this.seatNum = message.getIntAttribute (XML_ATT_SEAT);
    }

    /**
     * Return the seat number.
     *
     * @return
     */
    public int getSeatNum () {
        return this.seatNum;
    }

	/**
	 * Flatten the String for transmission purposes.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
	    XMLElement message = super.flatten (Comm.SIT_DOWN);
	    message.setIntAttribute(XML_ATT_SEAT, seatNum);

		return message;
	}
}