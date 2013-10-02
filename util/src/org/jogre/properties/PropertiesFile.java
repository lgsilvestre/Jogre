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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Property file object.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesFile {

	private String app, propertyFile, lang;
	private File fileObject;
	
	// File info
	private List lines = null;
	private boolean defaultLang, dirty;
	private Map propHash;
	
	/**
	 * Create new property file object.
	 * 
	 * @param app
	 * @param propertyFile
	 * @param fileObject
	 */
	public PropertiesFile (String app, String propertyFile, String lang, boolean defaultLang, File fileObject) throws IOException {
		this.app = app;
		this.propertyFile = propertyFile;
		this.lang = lang;
		this.defaultLang = defaultLang;
		this.fileObject = fileObject;
		this.dirty = false;
		
		// Read properties file
		if (defaultLang)		// default lang defines structure
			this.lines = new ArrayList ();
		this.propHash = new HashMap ();			
	}
	
	// Accessors / mutators
	
	public String getApp() {
		return app;
	}
	
	public String getPropertyFile() {
		return propertyFile;
	}
	
	public String getLang() {
		return lang;
	}

	public File getFileObject() {
		return fileObject;
	}
	
	public void setDirty() {
		this.dirty = true;
	}
	
	public boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * Return the file.
	 * 
	 * @return
	 */
	public String getFileContents () {
		StringBuffer sb = new StringBuffer ();
		Iterator it = lines.iterator();
		while (it.hasNext()) {
			sb.append ((PropertiesLine)it.next() + "\n");
		}
		return sb.toString();
	}
	
	/**
	 * Return list of property lines (list of PropertyLine objects).
	 * 
	 * @return
	 */
	public List getPropertiesLines () {
		return lines;
	}
	
	/**
	 * Return list of property lines.
	 * 
	 * @param type
	 * @return
	 */
	public List getPropertiesLines (int type) {
		List list = new ArrayList ();
		for (int i = 0; i < lines.size(); i++) {
			PropertiesLine line = (PropertiesLine)lines.get(i);
			if (line.getType() == type)
				list.add (line);
		}
		
		return list;
	}
	
	/**
	 * Return property line for specified key.
	 * 
	 * @param key
	 * @return
	 */
	public PropertiesLine getPropertiesLine (String key) {
		if (propHash.get (key) != null)
			return (PropertiesLine)propHash.get (key);
		return null;
	}
	
	/**
	 * Populate file contents.
	 * 
	 * @throws IOException 
	 */
	public void loadFile () throws IOException {
		
		if (fileObject.exists () && fileObject.isFile()) {
			BufferedReader reader = new BufferedReader (new FileReader (fileObject));
			
			// Read file into string buffer
			String str;
			while ((str = reader.readLine()) != null) {
				PropertiesLine propLine = new PropertiesLine (this, str);
					
				if (defaultLang)
					lines.add (propLine);
				
				if (propLine.isProperty())
					propHash.put (propLine.getKey(), propLine);
			}
		}
	}
}