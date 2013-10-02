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

/**
 * <p>This interfaces denotates that the ITransmittable object contains
 * a JogrePropertiesHash instance as a field.  The class must also supply an
 * <i>addProperty (String key, String value) </i> and
 * <i>getProperty (String key)</i> methods from this interface.  </p>
 *
 * <p>The properties should be stored in a "properties"
 * attribute in the ITransmittable.flatten () method when is read again
 * using the object's constructor (String) method.</p>
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public interface ITransmittableWithProps extends ITransmittable {

    /** Attribute name of properties. */
    public static final String XML_ATT_PROPERTIES = "properties";

    /**
     * Return the properties hash.
     *
     * @return
     */
    public JogrePropertyHash getProperties ();

	/**
	 * Add a property to a class. i.e.
	 * properties.put (key, value);
	 *
	 * @param key    Key of the property
	 * @param value  Value of the property
	 */
	public void addProperty (String key, String value);
}
