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
package org.jogre.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Convience class for storing key/values.  This class extends the
 * HashMap class from the Sun API but adds additional information
 * for packing / unpacking into a String of format
 * "key1=value1, key2=value2, ... ".
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogrePropertyHash extends HashMap {

    /**
     * Empty constructor which doesn't take any hash values.
     */
    public JogrePropertyHash () {
        super ();		// Call function in super class
    }

    /**
     * This constructor converts a String of format:
     *
     *     "key1=value1, key2=value2, ... "
     *
     * into a HashMap.
     *
     * @param properites
     */
    public JogrePropertyHash (String properties) {
        this ();
        if (properties != null) {
	        StringTokenizer st = new StringTokenizer (properties, ",");

		    while (st.hasMoreTokens()) {
		        String token = st.nextToken();

		        int commaPos = token.indexOf("=");
		        if (commaPos != -1) {
		            // extract key and value
		            String key = token.substring (0, commaPos);
		            String value = token.substring (commaPos + 1);

		            // and add to the hash
		            put (key, value);
		        }
		    }
        }
    }

    /**
     * Return the contents of the HashMap of format:
     *
     * 	   "key1=value1, key2=value2, ... "
     *
     * into a String.
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
	    StringBuffer propsSB = new StringBuffer ();
		Set keys = keySet();
		for (Iterator i = keys.iterator(); i.hasNext(); ) {
		    String key = (String)i.next();
		    propsSB.append (key + "=" + get(key));
		    if (i.hasNext())
		        propsSB.append(",");
		}

		return propsSB.toString();
    }
}
