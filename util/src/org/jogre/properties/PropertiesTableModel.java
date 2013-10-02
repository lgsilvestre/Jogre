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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

/**
 * Properties table model. 
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesTableModel extends AbstractTableModel implements Observer {

	// Constants
	public static final int COLUMN_KEYS = 0;
	
	private PropertiesParser parser;
	private String app = null;
	private String propertyFile = null;
	private boolean showFormat;
	private List userSelectedLangs = null;
	private PropertiesFile defaultLangPropFile = null;
	
	/**
	 * Constructor which takes a parser object.
	 * 
	 * @param parser
	 */
	public PropertiesTableModel (PropertiesParser parser) {
		this.parser = parser;
		this.userSelectedLangs = new ArrayList ();
	}

	/**
	 * Set info.
	 * 
	 * @param app
	 * @param propertyFile
	 */
	public void refresh (String app, String propertyFile, boolean showFormat, List userSelectedLangs) {
		this.app = app;
		this.propertyFile = propertyFile;
		this.showFormat = showFormat;
		this.userSelectedLangs = userSelectedLangs;
		
		// Populate
		this.defaultLangPropFile = parser.getPropertyFile (app, propertyFile);
		
		// Update view
		fireTableDataChanged();
		fireTableStructureChanged();
	}
	
	/**
	 * Return column count.
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return userSelectedLangs.size() + 1;
	}

	/**
	 * Return row count.
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (defaultLangPropFile != null) {
			if (showFormat)
				return defaultLangPropFile.getPropertiesLines().size();
			else 
				return defaultLangPropFile.getPropertiesLines(PropertiesLine.TYPE_PROPERTY).size();
		}
		return 0;
	}

	/**
	 * Return property.
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt (int rowIndex, int columnIndex) {		
		if (defaultLangPropFile != null) {
			PropertiesLine line = getDefaultLangLine(rowIndex);
			
			if (line != null) {
				// Return default language key
				if (columnIndex == COLUMN_KEYS) {
					if (line.isComment())
						return line.getValue();
					else if (line.isProperty())
						return line.getKey();
				}
				else if (columnIndex > COLUMN_KEYS) {
					// Check properties file line.
					PropertiesLine propFileLine = getPropertiesLine(rowIndex, columnIndex);
					if (propFileLine != null && !propFileLine.isComment())
						return propFileLine.getValue();
				}
			}
		}
		return null;		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName (int columnIndex) {
		if (columnIndex == 0)
			return "Key";
		else {
			return getLang (columnIndex);
		}
	}
	
	/**
	 * Return true/false if cell is editable
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable (int rowIndex, int columnIndex) {
		return true;
	}
	
	/**
	 * Return the default language property file.
	 * 
	 * @return
	 */
	public PropertiesFile getDefaultLangPropFile () {
		return this.defaultLangPropFile;
	}
	
	/**
	 * Return language property file
	 * 
	 * @param columnIndex
	 * @return
	 */
	public PropertiesFile getLangPropFile (int columnIndex) {	
		Object obj = parser.getPropertyFile(app, propertyFile, getLang(columnIndex));
		if (obj != null)
			return (PropertiesFile)obj;
		return null;
	}
	
	/**
	 * Return the properties line for row / index.
	 * 
	 * FIXME - very similar to getPropertiesLine (int rowIndex, String lang).
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	public PropertiesLine getPropertiesLine (int rowIndex, int columnIndex) {
		PropertiesLine line = getDefaultLangLine (rowIndex);
		
		if (line.isProperty()) {
			String defaultLangKey = getDefaultLangKey(rowIndex);
		
			PropertiesFile propFile = getLangPropFile (columnIndex);		
			if (propFile != null && propFile.getPropertiesLine (defaultLangKey) != null)
				return ((PropertiesLine)propFile.getPropertiesLine (defaultLangKey));
		}
		else if (line.isComment())
			return line;
			
		return null;
	}
	
	/**
	 * Return properties line
	 * 
	 * @param rowIndex
	 * @param lang
	 * @return
	 */
	public PropertiesLine getPropertiesLine (int rowIndex, String lang) {
		PropertiesLine line = getDefaultLangLine (rowIndex);
		
		if (line.isProperty()) {
			String defaultLangKey = getDefaultLangKey(rowIndex);
			Object obj = parser.getPropertyFile (app, propertyFile, lang);
			if (obj != null) {
				PropertiesFile propFile = (PropertiesFile)obj;
				if (propFile != null && propFile.getPropertiesLine (defaultLangKey) != null)
					return ((PropertiesLine)propFile.getPropertiesLine (defaultLangKey));
			}
		}
		return null;
	}
	
	/**
	 * Add a row to this table model.
	 * 
	 * @param row
	 */
	public void addRow (int row) {
		defaultLangPropFile.getPropertiesLines().add(new PropertiesLine (defaultLangPropFile));
		// Update view
		fireTableDataChanged();
		//fireTableStructureChanged();
	}
	
	/**
	 * Return language from column index.
	 * 
	 * @param columnIndex
	 * @return
	 */
	public String getLang (int columnIndex) {
		if (columnIndex > 0)
			return (String)userSelectedLangs.get(columnIndex - 1);
		return null;
	}
	
	/**
	 * Return default language key for a specified row.
	 * 
	 * @param columnIndex
	 * @return
	 */
	public String getDefaultLangKey (int rowIndex) {		
		PropertiesLine line = getDefaultLangLine (rowIndex);
		return line.getKey();	
	}
	
	/**
	 * Return default language line.
	 * 
	 * @param rowIndex
	 * @return
	 */
	public PropertiesLine getDefaultLangLine (int rowIndex) {
		List lines;
		if (showFormat)
			lines = defaultLangPropFile.getPropertiesLines();
		else lines = defaultLangPropFile.getPropertiesLines(PropertiesLine.TYPE_PROPERTY);
		
		return (PropertiesLine)lines.get (rowIndex);
	}
	
	/**
	 * Update.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg) {
		System.out.println ("bla");
	}
}