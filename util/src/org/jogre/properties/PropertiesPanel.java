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

import info.clearthought.layout.TableLayout;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * Content panel
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesPanel extends JPanel implements Observer {
	
	private static final double FILL = TableLayout.FILL;
	private static final double PREF = TableLayout.PREFERRED;
	
	// Fields
	private String app = null, propertyFile = null;
	private JogrePropertiesEditor editor;
	private PropertiesParser parser;	
		
	// GUI items
	private PropertiesTable propTable;
	private ToolbarPanel toolbarPanel;
	
	/**
	 * Constructor to a content panel.
	 * 
	 * @param editor   Link to editor
	 * @param parser   Parser
	 */
	public PropertiesPanel (JogrePropertiesEditor editor, PropertiesParser parser) throws IOException {
		// Set fields
		this.editor = editor;
		this.parser = parser;
		this.parser.addObserver(this);
		
		setUpGui ();
	}
	
	/**
	 * Setup GUI.
	 */
	private void setUpGui () throws IOException {
		double [][] sizes = {{FILL},{PREF,FILL}};
		setLayout(new TableLayout (sizes));
		
		// Create GUI fields
		this.toolbarPanel = new ToolbarPanel (this, parser);
		this.propTable    = new PropertiesTable (this, parser);	
		
		add (toolbarPanel, "0,0,l,c");
		add (propTable.getScrolledTable(), "0,1");
	}
	
	/**
	 * Set property file.
	 * 
	 * @param app
	 * @param propFilename
	 */
	public void setPropertyFile (String app, String propertyFile) {
		this.app = app;
		this.propertyFile = propertyFile;
		refresh ();
	}
	
	/**
	 * Refresh method.
	 */
	private void refresh () {
		if (app != null &&  propertyFile != null) {
			propTable.getPropTableModel ().refresh (app, 
					                                propertyFile, 
					                                toolbarPanel.isFormatSelected(), 
					                                toolbarPanel.getLangSelections());
		}
	}
	
	// Accessors	
	public String getPropertyFile() {
		return propertyFile;
	}

	public PropertiesTable getPropTable() {
		return propTable;
	}

	/**
	 * Refresh panels.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg) {
		refresh ();
	}
}