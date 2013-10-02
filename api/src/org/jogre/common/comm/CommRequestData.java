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
 * Communication class for a client requesting data.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommRequestData extends CommGameMessage {

    private String dataType = null;

    public static final String XML_ATT_DATA_TYPE = "dataType";

    /**
     * Constructor for a requesting data.  This can be either
     * "game" (Game), "table_list" (TableList) or "user_list"
     * (UserList).
     */
    public CommRequestData (String dataType) {
        super ();

        // Set data type
        if (dataType.equals (Comm.GAME) ||
            dataType.equals (Comm.TABLE_LIST) ||
            dataType.equals (Comm.USER_LIST)) {

            // Set data type
            this.dataType = dataType;
        }
    }

	/**
	 * Constructor which takes an XMLElement.
	 *
	 * @param message
	 * @throws TransmissionException
	 */
	public CommRequestData (XMLElement message) throws TransmissionException {
	    super (message);

	    this.dataType = message.getStringAttribute (XML_ATT_DATA_TYPE);
	}

    /**
     * Return the data type that the user wishes to receive.
     *
     * @return   Data type.
     */
    public String getDataType () {
        return dataType;
    }

    /**
     * Flatten the object into an XMLElement.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten() {
        XMLElement message = super.flatten (Comm.REQUEST_DATA);
        message.setAttribute (XML_ATT_DATA_TYPE, dataType);

        return message;
    }

}
