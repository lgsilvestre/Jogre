/*
 * JOGRE (Java Online Gaming Real-time Engine) - Generator
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
package org.jogre.gamegen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Generator properties file.  This class is an interface into the 
 * "generator.properties" file.
 *  
 * @author Bob Marks
 */
public class GameGeneratorProperties {

	// Declare constants
	public static final String GAME_ID = "game_id";
	public static final String DIR     = "dir";
	
	private static final String PARAMETER   = "parameter";
	private static final String MKDIR       = "mkdir";
	private static final String FILE        = "file";
	private static final String DESCRIPTION = "description";
	private static final String OPTIONAL    = "optional";
	private static final String DEFAULT     = "default";
	
	// Filename
	private static final String FILENAME = "generator.properties";
	private static final String PROPERTY_FILE_LOCATION = "/org/jogre/gamegen/";
	
	// Fields
	private Properties props;	
	private ArrayList parameters, mkdirs, files;
	
	/**
	 * Constructor to generator properties.
	 * 
	 * @throws IOException
	 */
	public GameGeneratorProperties () throws IOException {
		loadProperties ();		// load file 
	}
	
	/**
	 * Return the parameters array.
	 * 
	 * @return
	 */
	public ArrayList getParameters () {
		return this.parameters;
	}
	
	/**
	 * Return sub list of parameters, but only parameters which match
	 * the madatory boolean.
	 * 
	 * @param optional   If true returns optional parameters, 
	 *                   mandatory parameters if false. 
	 * @return           Parameter list.
	 */
	public ArrayList getParameters (boolean optional) {
		ArrayList allParameters = getParameters();
		ArrayList subParameters = new ArrayList ();
		for (int i = 0; i < parameters.size(); i++) {
			String param = (String)allParameters.get(i);
			if (optional && isOptional (param))			
				subParameters.add (param);
			else if (!optional && !isOptional(param))
				subParameters.add (param);
		}
		
		return subParameters;
	}
	
	/**
	 * Return the mkdirs array.
	 * 
	 * @return
	 */
	public ArrayList getMkdirs () {
		return this.mkdirs;
	}
	
	/**
	 * Return the files to create.
	 * 
	 * @return
	 */
	public ArrayList getFiles () {
		return this.files;
	}
	
	/**
	 * Return true/false if parameter is optional or not.
	 * 
	 * @param parameter
	 * @return
	 */
	public boolean isOptional (String parameter) {
		String value = getValue (parameter + "." + OPTIONAL);
		if (value != null) {
			value = value.toLowerCase();
			return (value.equals("yes"));
		}	
		
		return false;		// assume not optional if problem
	}
	
	/**
	 * Return default value of optional parameter.
	 * 
	 * @param parameter   Name of property.
	 * @return
	 */
	public String getDefaultValue (String parameter) {
		return getValue (parameter + "." + DEFAULT);
	}
	
	/**
	 * Return property description.
	 * 
	 * @param parameter
	 * @return
	 */
	public String getDescription (String parameter) {
		return getValue (parameter + "." + DESCRIPTION);
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
		
		// Set parameters and make dirs
		this.parameters = getPropertyList (PARAMETER);
		this.mkdirs     = getPropertyList (MKDIR);
		this.files      = getPropertyList (FILE);
	}
	
	/**
	 * Return value from a key.
	 * 
	 * @param key
	 * @return
	 */
	private String getValue (String key) {
		return (String)props.getProperty(key);
	}
	
	/**
	 * Return an array of properties from an key.  Uses a loop until no 
	 * more are found. e.g. if key is "mkdir" it will look for 
	 * mkdir.0, mkdir.1, mkdir.2, etc.
	 *
	 * @param key   Key, this method adds a dot "." and a number e.g. "mkdir"
	 *              becomes "mkdir.0".
	 * @return
	 */
	private ArrayList getPropertyList (String key) {
		ArrayList propsList = new ArrayList ();
		try {		
			int i = 0;
			String value = "";
			while (value != null) {	// loop until no more properties
				value = getValue(key + "." + i++);
				if (value != null)
					propsList.add (value);
			}
		} catch (Exception e) {} // do nothing
		
		return propsList;
	}
}
