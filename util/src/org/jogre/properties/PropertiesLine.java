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

/**
 * Property line class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesLine {
	
	public static final int TYPE_COMMENT = 0;
	public static final int TYPE_BLANK_LINE = 1;
	public static final int TYPE_PROPERTY = 2;	
	public static final int TYPE_NOT_SUPPORTED = 3;
	
	// Fields
	private PropertiesFile file;			// link to file
	
	private String key = null;
	private String value = null;
	private boolean dirty = false;
	
	/**
	 * Create a new properties line which is of type PropertiesLine.TYPE_BLANK_LINE.
	 * 
	 * @param file
	 */
	public PropertiesLine (PropertiesFile file) {
		this.file = file;
	}
	
	/**
	 * Constructor which takes an unparsed line and parses it into fields.
	 * 
	 * @param line
	 */
	public PropertiesLine (PropertiesFile file, String line) {
		this.file = file;				// link to file.
		
		if (line != null) {
			
			// Parse line into value / key, value pair
			if (line.startsWith("#") || line.trim().length() == 0) {	// blank line / comment	
				this.value = line;
			}			
			else if (line.indexOf ('=') != -1) {							// property					
				int index = line.indexOf('=');
				this.key  = line.substring (0, index);
				this.value = line.substring (index + 1);
			}			
			else
				value = "";	// blank
		}
		else
			value = "";	// blank
		
		this.dirty = false;
	}

	/**
	 * Return type
	 * 
	 * @return
	 */
	public int getType() {
		if (value.startsWith("#"))
			return TYPE_COMMENT;
		else if (key != null)
			return TYPE_PROPERTY;	
		else if (value.trim().equals(""))
			return TYPE_BLANK_LINE;
		
		return TYPE_NOT_SUPPORTED; 
	}

	/**
	 * Return key.
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Return value.
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Return true/false if this line has been changed recently.
	 * 
	 * @return
	 */
	public boolean isDirty () {
		return this.dirty;
	}
	
	/**
	 * Set key of properties line.
	 * 
	 * @param value
	 */
	public void setKey (String key) {
		this.key = key;
	}
	
	/**
	 * Set value of properties line.
	 * 
	 * @param value
	 */
	public void setValue (String value) {
		this.value = value;
	}
	
	/**
	 * Set dirty flag.
	 */
	public void setDirty () {
		this.dirty = true;
		this.file.setDirty ();
	}
	
	// Boolean accessors on type
	public boolean isComment ()   { return getType() == TYPE_COMMENT;    }
	public boolean isBlankLine () { return getType() == TYPE_BLANK_LINE; }
	public boolean isProperty ()  { return getType() == TYPE_PROPERTY;   }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		int type = getType();
		if (type == TYPE_COMMENT || type == TYPE_BLANK_LINE)
			return value;
		else if (type == TYPE_PROPERTY)
			return key + "=" + value;
		else 
			return "!!! Not Set !!!";
	}
}