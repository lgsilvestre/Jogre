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
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

/**
 * File list model
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class FilesListTableModel extends AbstractTableModel implements Observer {

	private static final String [] COLUMNS = {"App", "File"};
	
	private static final FileListRow ALL = new FileListRow ("All", "-", new String [] {"-"});

	private PropertiesParser parser;

	private List appProps;			// for visual purposes
	
	/**
	 * Constructor.
	 */
	public FilesListTableModel (PropertiesParser parser) {
		this.parser = parser;
		this.parser.addObserver (this);
		
		this.appProps = new ArrayList ();
	}

	// Overridden methods.
	
	/**
	 * Get column count.
	 *
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return COLUMNS.length;
	}

	/**
	 * Return row count.
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return appProps.size();
	}

	/**
	 * Get value at a specific cell.
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt (int rowIndex, int columnIndex) {
		FileListRow row = (FileListRow)appProps.get(rowIndex);
		if (columnIndex == 0)
			return row.getApp();
		else 
			return row.getPropertyFile();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName (int columnIndex) {
		return COLUMNS [columnIndex];
	}
	
	/**
	 * Return default properties file. 
	 * 
	 * @param rowIndex
	 * @return
	 */
	public PropertiesFile getDefaultPropertiesFile (int rowIndex) {
		FileListRow row = (FileListRow)appProps.get(rowIndex);
		return parser.getPropertyFile(row.getApp(), row.getPropertyFile());
	}

	/**
	 * Update view.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg) {
		appProps.clear();
		appProps.add(ALL);
		
		Iterator it = parser.getPropertyMap().keySet().iterator();
		List dups = new ArrayList ();	
		while (it.hasNext()) {
			String key = (String)it.next();
			
			PropertiesFile pFile = (PropertiesFile)parser.getPropertyMap().get(key);
			String app = pFile.getApp();
			String propertyFile = pFile.getPropertyFile();
			String [] supportedLangs = parser.getLangs(app, propertyFile);
			
			if (!dups.contains(app + "/" + propertyFile)) {
				appProps.add (new FileListRow (app, propertyFile, supportedLangs));
				dups.add(app + "/" + propertyFile);
			}
		}
		
		fireTableDataChanged();
	}
	
	/**
	 * Return supported langs.
	 * 
	 * @param rowIndex
	 * @return
	 */
	public String [] getSupportedLangs (int rowIndex) {
		FileListRow row = (FileListRow)appProps.get(rowIndex);
		return row.getSupportedLangs();
	}
	
	/**
	 * Little immutable data class describing a row.
	 */
	private static class FileListRow {
		
		private String app, propertyFile;
		private String [] supportedLangs;
		
		/**
		 * Constructor.
		 */
		public FileListRow (String app, String propertyFile, String [] supportedLangs) {
			this.app = app;
			this.propertyFile = propertyFile;
			this.supportedLangs = supportedLangs;
		}
		
		// Accessors
		public String getApp() { return app; }
		public String getPropertyFile() {return propertyFile; }
		public String[] getSupportedLangs() { return supportedLangs; }
	}
}