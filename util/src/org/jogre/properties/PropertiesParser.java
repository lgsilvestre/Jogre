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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.SortedMap;

/**
 * Property parser class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesParser extends Observable {

	// Fields
	private PropertiesProperties props;
	private Map defaultPropMap;
	private Map propMap;	
	private ArrayList langsList;
	
	/**
	 * Blank constructor.
	 */
	public PropertiesParser () throws IOException {
		this.props = PropertiesProperties.getInstance();
		this.propMap = new LinkedHashMap ();
		this.defaultPropMap = new LinkedHashMap ();
		this.langsList = new ArrayList ();
	}
	
	/**
	 * Parse directory.
	 * 
	 * Bit messy at minute - will improve if required i.e. to make it more generic for a use
	 * in a project other that JOGRE.
	 * 
	 * @param directory
	 */
	public void refreshFiles (String rootDirectory) throws IOException {
		loadFiles (rootDirectory);
		
		parseFiles ();
		
		// Update observers
		refreshObservers();
	}
	
	/**
	 * Load files.
	 * 
	 * @param rootDirectory
	 * @throws IOException
	 */
	private void loadFiles (String rootDirectory) throws IOException {
		// Read property file locations
		String [] dirLocations = props.getArray("dir.location");
		for (int i = 0; i < dirLocations.length; i++) {
			String dir = dirLocations [i];
			File fileDir = new File (rootDirectory + File.separator + dir);
			
			// Check to see if directory exists
			if (fileDir.exists() && fileDir.isDirectory()) {
				
				// Iterate through include files for this directory
				String fileInclude = props.getValue("file.include." + dir);
				if (fileInclude.startsWith("/*/")) {		// iterate sub dirs
					fileInclude = fileInclude.substring(3);
					// Iterate through files sub directories
					File [] files = fileDir.listFiles();
					for (int j = 0; j < files.length; j++) {
						if (files[j].isDirectory()) {
							dir = files[j].getName();
							File [] subFiles = files[j].listFiles();
							for (int k = 0; k < subFiles.length; k++) {
								String file = subFiles[k].getName();
								if (file.startsWith(fileInclude) && file.endsWith(".properties")) {
									addFile (dir, fileInclude, subFiles[k]);
								}						
							}
						}
					}
				}
				else if (fileInclude.startsWith("/")) {		// move to location
					int index = fileInclude.lastIndexOf("/");
					File subDir = new File (fileDir.getAbsolutePath() + fileInclude.substring(0, index));
					
					if (subDir.exists() && fileDir.isDirectory()) {
						fileInclude = fileInclude.substring(index + 1);
						
						File [] files = subDir.listFiles();
						for (int j = 0; j < files.length; j++) {
							String file = files[j].getName();
							if (file.startsWith(fileInclude) && file.endsWith(".properties")) {
								addFile (dir, fileInclude, files[j]);
							}						
						}
					}
				}
				else {	// Simply iterate through files						
					File [] files = fileDir.listFiles();
					for (int j = 0; j < files.length; j++) {
						String file = files[j].getName();
						if (file.startsWith(fileInclude) && file.endsWith(".properties")) {
							addFile (dir, fileInclude, files[j]);
						}						
					}
				}					
			}
		}
	}
	
	/**
	 * Parse files.
	 * 
	 * @throws IOException
	 */
	private void parseFiles () throws IOException {
		Iterator it = defaultPropMap.values().iterator();
		while (it.hasNext()) {
			PropertiesFile file = (PropertiesFile)it.next();
			file.loadFile();
			
		}
	}
	
	/**
	 * Return sorted map.
	 * 
	 * @return
	 */
	public Map getPropertyMap () {
		return propMap;
	}
	
	/**
	 * Return the support languages of this app and property file.
	 * 
	 * @param app
	 * @return
	 */
	public String [] getLangs (String app, String propertyFile) {
		List langs = new ArrayList ();
		Iterator it = propMap.values().iterator();
		while (it.hasNext()) {
			PropertiesFile propFile = (PropertiesFile)it.next();
			if (propFile.getApp().equals(app) && propFile.getPropertyFile().equals(propertyFile))
				langs.add(propFile.getLang());
		}
		return (String [])langs.toArray(new String [langs.size()]);
	}

	/**
	 * Return global langs supported.
	 * 
	 * @return
	 */
	public String [] getLangs () {
		return (String [])langsList.toArray(new String [langsList.size()]);
	}
	
	/**
	 * Return key from app, property file and lang.
	 * 
	 * @param app
	 * @param propertyFile
	 * @param lang
	 * @return
	 */
	public String getKey (String app, String propertyFile, String lang) {
		return app + "/" + propertyFile + "/" + lang;
	}
	
	/**
	 * Return key for app and property file.
	 * 
	 * @param app
	 * @param propertyFile
	 * @return
	 */
	public String getKey (String app, String propertyFile) {
		return app + "/" + propertyFile;
	}
	
	/**
	 * Return property file.
	 * 
	 * @param app
	 * @param propertyFile
	 * @return
	 */
	public PropertiesFile getPropertyFile (String app, String propertyFile) {
		String defaultLang = props.getValue("default.lang");
		return getPropertyFile (app, propertyFile, defaultLang);
	}
	
	/**
	 * Return property file.
	 * 
	 * @param app
	 * @param propertyFile
	 * @param lang
	 * @return
	 */
	public PropertiesFile getPropertyFile (String app, String propertyFile, String lang) {
		if (lang == null)
			lang = props.getValue("default.lang");
		String key = getKey (app, propertyFile, lang);
		Object obj = propMap.get (key);
		if (obj != null)
			return (PropertiesFile)obj;
		
		return null;
	}
	
	/**
	 * Refresh observers. 
	 */
	public void refreshObservers() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Add a property file.
	 * 
	 * @param dir
	 * @param propertyFile
	 * @param file
	 * @throws IOException 
	 */
	private void addFile (String app, String propertyFile, File fileObject) throws IOException {
		// Extract language of this file.
		String filename = fileObject.getName();
		String lang = filename.substring(propertyFile.length(), filename.lastIndexOf("."));
		if (lang.length() == 0)
			lang = props.getValue("default.lang");
		else if (lang.startsWith("_"))
			lang = lang.substring(1);
		
		// Add to lang array if not already exists
		if (!langsList.contains(lang))
			langsList.add(lang);
		
		// Create property file object and add to hash
		boolean defaultLang = (lang == props.getValue("default.lang"));
		PropertiesFile propFile = new PropertiesFile (app, propertyFile, lang, defaultLang, fileObject);
		propMap.put (getKey(app, propertyFile, lang), propFile);
		
		if (defaultLang)
			defaultPropMap.put (getKey(app, propertyFile), propFile);
	}
}