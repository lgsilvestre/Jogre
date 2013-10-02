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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jogre.common.JogreGlobals;

/**
 * Abstract class which provides various methods which are common to
 * GameProperties, GameLabels and JogreLabels.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class AbstractProperties {
    String filename;

	/** Resource bundle. */
	protected ResourceBundle rb = null; 				    // resource bundle

	/**
	 * Private constructor (Can only be called by the getInstance() method.
	 * @param filename
	 */
	protected AbstractProperties (String filename) {
		this.filename = filename;

		// Load resource bundles
        reload ();
	}

    /**
     * Reload all resource bundles (if there has been a
     */
    public void reload () {
        // Populate resource bundle
        try {
            rb = ResourceBundle.getBundle (filename, JogreGlobals.getLocale ());
        }
        catch (MissingResourceException mrEx) {}
    }

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as a
	 * String.
	 *
	 * @param key  Key to get value from.
	 * @return     Trimed value from a specified trimmed key.
	 */
	public String get (String key) {
		if (rb != null)
			return rb.getString (key.trim()).trim();
		return null;
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as a
	 * String.  If the value doesn't
	 *
	 * @param key           Key to get value from.
	 * @param defaultValue  Default value if key doesn't retrieve a value.
	 * @return              String value.
	 */
	public String get (String key, String defaultValue) {
		try {
			if (rb != null)
				return rb.getString (key.trim()).trim();
		}
		catch (Exception mrEx) {
		    // Return the default value.
		}

		return defaultValue;
	}
	
	/**
	 * Return an array list of Keys;
	 * 
	 * @return
	 */
	public List getKeys () {
		List keys = new ArrayList ();
		Enumeration e = rb.getKeys();
		while (e.hasMoreElements())
			keys.add(e.nextElement());
		
		return keys;
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as an
	 * int value.
	 *
	 * @param key     Key to get value from.
	 * @return        String value.
	 */
	public int getInt (String key) {
		return Integer.parseInt (get (key));
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as an
	 * int value.  If the value doesn't exist then return its defaultValue
	 * instead.
	 *
	 * @param key               Key to get value from.
	 * @param defaultValue      Default integer value if key isn't found.
	 * @return                  Integer value.
	 */
	public int getInt (String key, int defaultValue) {
		try {
			return Integer.parseInt (get (key));
		}
		catch (Exception mrEx) {
		    // Return the default value.
		}

		return defaultValue;
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as an
	 * String value, in case "value" contains "variables".  If the value doesn't exist then return null.
	 * instead. Example of use:
	 * property.with.five.variables = This property is the {1} and contains this variables: {2},{3},{4} and {5}
	 * In java code:
	 * ObjectArray[] = {"1",new Integer(2),"-23",new Double(2.34),0}
	 * String text = labels.get("property.with.five.variables",ObjectArray)
	 * and then text = This property is the 1 and contains this variables: 2,-23,2.34 and 0
	 *
	 * @param key               Key to get value from.
	 * @param arguments         Arguments
	 * @return                  String value.
	 */
	public String get (String key, Object[] arguments) {
        String pattern = get(key);
        MessageFormat mf = new MessageFormat (pattern);
        return mf.format(arguments, new StringBuffer(), null).toString();
	}

	/**
	 * Return a boolean value from a key.
	 *
	 * @param key     Key to get value from.
	 * @return        Returns true if value="true"
	 */
	public boolean getBoolean (String key) {
		return get (key).equals("true");
	}

	/**
	 * Return a boolean value from a key.  If the key doesn't exist then return
	 * the default value.
	 *
	 * @param key           Key to get value from.
	 * @param defaultValue  Default boolean value if key isn't found.
	 * @return              Returns true if value="true" or default value if not value exists.
	 */
	public boolean getBoolean (String key, boolean defaultValue) {
		try {
			return getBoolean (key);
		}
		catch (Exception mrEx) {
		    // Return the default value.
		}

		return defaultValue;
	}

    /**
     * Recreates ResourceBundle
     */
    public void reset() {
    	try {
    		rb = ResourceBundle.getBundle (filename);
    	}
    	catch (MissingResourceException mrEx) {}
    }

    public ResourceBundle getResourceBundle() {
        return rb;
    }
}
