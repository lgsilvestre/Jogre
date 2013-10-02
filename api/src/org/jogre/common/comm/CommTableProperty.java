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
 * This class is used for sending a table property from a client to a server.
 * The server then informs the other clients that a table property
 * has been sent.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommTableProperty extends CommTableMessage {

    private String key, value;
    
	private static final String XML_ATT_KEY   = "key";
	private static final String XML_ATT_VALUE = "value";
    
    /**
     * Constructor which takes a key and a value.
     * 
     * @param key     Property key.
     * @param value   Property value.
     */
    public CommTableProperty (String key, String value) {
        super ();
        
        // Set fields
        this.key   = key;
        this.value = value;
    }

    /**
     * Constructor which takes an element and reads it key and value.
     * 
     * @param message
     */
    public CommTableProperty (XMLElement message) {
        super (message);
        
		this.key   = message.getStringAttribute (XML_ATT_KEY);
		this.value = message.getStringAttribute (XML_ATT_VALUE);        
    }

    /**
     * Return the property key.
     * 
     * @return
     */
    public String getKey () {
        return this.key;
    }
    
    /**
     * Return the property value.
     * 
     * @return
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     * Flatten to an object and set its key and value.
     * 
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten() {
		XMLElement message = flatten (Comm.TABLE_PROPERTY);
		message.setAttribute (XML_ATT_KEY, key);
		message.setAttribute (XML_ATT_VALUE, value);

		return message;
    }
}