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

import org.jogre.common.util.JogrePropertyHash;

import nanoxml.XMLElement;

/**
 * Small communication object for requesting the creation of a new table.  Although
 * this message is responsible for table communication it is at the game level
 * as we dont know our table number (the server creates).
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommNewTable extends CommGameMessage implements ITransmittableWithProps {

	/** Boolean to show if a table is public or private. */
	private boolean isPublic;

	/** Extra properties which can be associated with this table action. */
	private JogrePropertyHash properties = new JogrePropertyHash ();

	// XML strings
	private static final String XML_ATT_IS_PUBLIC  = "isPublic";

    /**
     * Constructor for creating a new table.
     */
    public CommNewTable (boolean isPublic) {
        super();
        this.isPublic = isPublic;
    }

    /**
     * Consturctor for creating a new message.
     *
     * @param message
     */
    public CommNewTable (XMLElement message) {
        super (message);

        isPublic = message.getAttribute(XML_ATT_IS_PUBLIC).equals("true");

        // Read properties
		properties = new JogrePropertyHash (message.getStringAttribute (XML_ATT_PROPERTIES));
    }

    /**
     * Return true if this table is a public table or not.
     *
     * @return
     */
    public boolean isPublic () {
        return this.isPublic;
    }

	/**
	 * Add a property to the Table Action.
	 *
	 * @param key
	 * @param value
	 */
	public void addProperty (String key, String value) {
	    properties.put (key, value);
	}

	/**
     * @see org.jogre.common.comm.ITransmittableWithProps#getProperties()
     */
    public JogrePropertyHash getProperties() {
        return properties;
    }

	/**
	 * Flatten the String for transmission purposes.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = flatten (Comm.NEW_TABLE);
		message.setAttribute (XML_ATT_IS_PUBLIC,  String.valueOf(isPublic));
		message.setAttribute (XML_ATT_PROPERTIES, String.valueOf(properties));

		return message;			// return element back to user
	}
}