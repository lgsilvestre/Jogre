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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Create files table.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesTable extends JTable {
	
	// Fields
	private PropertiesParser parser;
	private PropertiesPanel panel; 
	private PropertiesProperties props;
	
	// GUI items
	private PropertiesTableModel model;	
	private JScrollPane scrolledTable;
	private PropertiesTableCellEditor editor;
	
	/**
	 * Files panel.
	 * 
	 * @param parser
	 */
	public PropertiesTable (PropertiesPanel panel, PropertiesParser parser) throws IOException {
		// Fields
		this.panel  = panel;
		this.props  = PropertiesProperties.getInstance();
		this.parser = parser;
		
		// Set up GUI.	
		this.model = new PropertiesTableModel (parser);
		this.editor = new PropertiesTableCellEditor (model);
		setModel(model);
		setDefaultEditor(Object.class, editor);
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.scrolledTable = new JScrollPane (this);
		
		setDefaultRenderer(Object.class, new PropertiesTableCellRenderer(props, parser, model));
	}
	
	/**
	 * Reset cell editor
	 */
	public void resetCellEditor () {
		this.editor = new PropertiesTableCellEditor (model);
		setCellEditor(editor);
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
	 * return table model.
	 * 
	 * @return
	 */
	public PropertiesTableModel getPropTableModel () {
		return this.model;
	}
	
	/**
	 * Default cell renderer.
	 */
	class PropertiesTableCellRenderer extends DefaultTableCellRenderer {
		
		private final Color BG_COLOR = new Color (230, 230, 230);
		private final Color KEY_COLOR = new Color (0, 0, 128);
		private final Color COMMENT_COLOR = new Color (0, 128, 0);
		
		private PropertiesParser parser; 
		private PropertiesProperties props;
		private PropertiesTableModel tableModel;
		
		/**
		 * Constructor
		 * 
		 * @param parser
		 */
		public PropertiesTableCellRenderer (PropertiesProperties props, PropertiesParser parser, PropertiesTableModel tableModel) {
			// Fields
			this.props = props;
			this.parser = parser;
			this.tableModel = model;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent (JTable table, Object value,
				boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) 
		{					
			ColouredBackgroundCell label = new ColouredBackgroundCell ((String)value, null, table.getFont(), isSelected, hasFocus);
			PropertiesLine line = tableModel.getPropertiesLine(rowIndex, columnIndex);
			
			if (line != null) {
				Font font = label.getFont();
				if (line.isProperty()) {					
					if (columnIndex == 0) {
						String [] langs = this.parser.getLangs();
						if (langs != null) {
			
							Color [] colors = new Color [langs.length];							
							for (int i = 0; i < langs.length; i++) {					
								String lang = langs[i];
								
								// See if this language is supported.
								colors[i] = null;
								PropertiesLine  curLine = tableModel.getPropertiesLine (rowIndex, lang);
								if (curLine != null && curLine.getValue() != null && !"".equals(curLine.getValue().trim()))
									colors[i]=props.getColor(lang);
							}
							
							return new ColouredBackgroundCell ((String)value, colors, table.getFont(), isSelected, hasFocus);
						}
					}
					else if (line.isDirty()) 
						label.setFont (new Font (font.getName(), Font.BOLD, font.getSize()));
				}
				if (line.isComment()) 
					label.setForeground (COMMENT_COLOR); 
			}
			
			return label;
		}	
	}
}