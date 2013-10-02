/*
 * JOGRE (Java Online Gaming Real-time Engine) - Properties
 * Copyright (C) 2004 - 2007   Bob Marks (marksie531@yahoo.com)
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
package org.jogre.properties;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Generator properties file.  This class is an interface into the 
 * "generator.properties" file.
 *  
 * @author Bob Marks
 */
public class PropertiesProperties {

	// Declare constants	
	
	// Filename
	private static final String FILENAME = "properties.properties";
	private static final String PROPERTY_FILE_LOCATION = "/org/jogre/properties/";
	private static Map colorMap = new HashMap ();
	
	// Fields
	private Properties props;	
	
	private static PropertiesProperties instance = null;
	
	/**
	 * Singleton constructor to generator properties.
	 * 
	 * @throws IOException
	 */
	private PropertiesProperties () throws IOException {
		loadProperties ();		// load file 
	}
	
	/**
	 * Return properties singleton instance.
	 * 
	 * @return
	 */
	public static PropertiesProperties getInstance () throws IOException {
		if (instance == null)
			instance = new PropertiesProperties ();
		return instance;
	}
	
	
	/**
	 * Load properties file up.
	 * 
	 * @throws IOException
	 */
	private void loadProperties () throws IOException {
		// Create stream and load properties
		InputStream is = this.getClass().getResourceAsStream (PROPERTY_FILE_LOCATION + FILENAME);
		this.props = new Properties ();
		props.load (is);
	}
	
	/**
	 * Return value from a key.
	 * 
	 * @param key
	 * @return
	 */
	public String getValue (String key) {
		return (String)props.getProperty(key);
	}
	
	/**
	 * Convert a space delimited value into an array of Strings.
	 * 
	 * @param key
	 * @return
	 */
	public String [] getArray (String key) {
		String value = getValue(key);
		if (value != null) {
			// Create Str
			StringTokenizer st = new StringTokenizer (value, " ");
			String [] values = new String [st.countTokens()];

			// Poopulate
			int i = 0;
			while (st.hasMoreTokens())
				values [i++] = st.nextToken();
			
			return values;
		}
		return null;
	}

	/**
	 * Return colour from lang.
	 * 
	 * @param lang
	 * @return
	 */
	public Color getColor (String lang) {
		String value = getValue("color." + lang);
		if (value != null) {
			StringTokenizer st = new StringTokenizer (value, ",");
			try {
				int r = Integer.parseInt(st.nextToken().trim());
				int g = Integer.parseInt(st.nextToken().trim());
				int b = Integer.parseInt(st.nextToken().trim());
				
				// Lazy load color
				Color color = null;
				if (colorMap.containsKey(lang))
					color = (Color)colorMap.get(lang);
				else { 
					color = new Color (r, g, b);
					colorMap.put(lang, color);
				}
				return color;
			} catch (NumberFormatException nfEx) {
				nfEx.printStackTrace();
			}
		}
		else
			System.out.println ("No colour in property file for lang: " + lang);
		
		return Color.white;
	}
}
