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
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Create files table.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class FilesTable extends JTable {
	
	private PropertiesParser parser;
	private PropertiesProperties props;
	
	private FilesListTableModel listModel;	
	private JScrollPane scrolledTable;
	
	/**
	 * Files panel.
	 * 
	 * @param parser
	 */
	public FilesTable (PropertiesParser parser) throws IOException {
		// Fields
		this.props = PropertiesProperties.getInstance();
		this.parser = parser;
		
		// Set up GUI.	
		this.listModel = new FilesListTableModel (parser);
		setModel (listModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.scrolledTable = new JScrollPane (this);
		
		setDefaultRenderer(Object.class, new FilesTableCellRenderer(props, parser, listModel));
	}
	
	/**
	 * Return component in scroll pane.
	 * 
	 * @return
	 */
	public JScrollPane getScrolledTable () {
		return this.scrolledTable;
	}
	
	/**
	 * Default cell renderer.
	 */
	class FilesTableCellRenderer extends DefaultTableCellRenderer {
		
		private PropertiesParser parser; 
		private PropertiesProperties props;
		private FilesListTableModel tableModel;
		
		/**
		 * Constructor
		 * 
		 * @param parser
		 */
		public FilesTableCellRenderer (PropertiesProperties props, PropertiesParser parser, FilesListTableModel tableModel) {
			// Fields
			this.props = props;
			this.parser = parser;
			this.tableModel = tableModel;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent (JTable table, Object value,
				boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) 
		{		
			ColouredBackgroundCell cell = null;			
			PropertiesFile propFile = tableModel.getDefaultPropertiesFile(rowIndex);
			Font font = table.getFont();
			
			// Update
			if (vColIndex == 0) {				
				String [] langs = this.parser.getLangs();
				if (langs != null) {
	
					String [] supportedLangs = tableModel.getSupportedLangs(rowIndex);
					Color [] colors = new Color [langs.length];
					
					for (int i = 0; i < langs.length; i++) {					
						String lang = langs[i];
						
						// See if this language is supported.
						colors[i] = null;
						for (int j = 0; j < supportedLangs.length; j++) {
							if (lang.equals(supportedLangs[j])) {						
								colors[i]=props.getColor(lang);
							}
						}
					}
					
					cell = new ColouredBackgroundCell ((String)value, colors, font, isSelected, hasFocus);
					if (propFile != null && propFile.isDirty())
						cell.setFont (new Font (font.getName(), Font.BOLD, font.getSize()));
					return cell;
				}
			}			
			cell = new ColouredBackgroundCell ((String)value, null, font, isSelected, hasFocus);
			if (propFile != null && propFile.isDirty())
				cell.setFont (new Font (font.getName(), Font.BOLD, font.getSize()));
			
			return cell;			
		}		
		
	}
}