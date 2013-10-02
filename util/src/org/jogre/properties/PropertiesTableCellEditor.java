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

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * Properties table editor.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PropertiesTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	// This is the component that will handle the editing of the cell value
    JComponent component = new JTextField();
    
    private boolean keyColumn = false;
    private PropertiesLine line, defaultLine;
    private PropertiesTableModel tableModel;
    
    /**
     * Constructor to a properties table cell editor.
     * 
     * @param parser
     */
    public PropertiesTableCellEditor (PropertiesTableModel tableModel) {
    	this.tableModel = tableModel;
    }
    
    /**
     * This method is called when a cell value is edited by the user.
     * 
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent (JTable table, Object value,
            boolean isSelected, int rowIndex, int columnIndex) {
    	
        // Configure the component with the specified value
        ((JTextField)component).setText((String)value);
        this.line = null;

        // Update property file model
        PropertiesFile defaultLangPropFile = tableModel.getDefaultLangPropFile();        
        if (defaultLangPropFile != null) {
        	this.keyColumn = columnIndex == PropertiesTableModel.COLUMN_KEYS;
        	if (keyColumn) {
        		this.defaultLine = tableModel.getDefaultLangLine (rowIndex);		// key column so default lang
        		this.line = null;
        	}
			else {
				this.defaultLine = tableModel.getDefaultLangLine (rowIndex);
				this.line = tableModel.getPropertiesLine (rowIndex, columnIndex);	// non key column so language specific
			}
		}
        
        // Return the configured component
        return component;
    }

    /**
     * Return cell editor value.
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
    	String text = ((JTextField)component).getText();

    	// Update 
    	if (keyColumn) {    	
    		if (defaultLine.isProperty()) {
    			defaultLine.setKey(text);
    			line.setDirty ();
    		}
    	}
    	else {
    		if (line == null) 
    			line.setValue (text);    			
    		line.setDirty ();
    	}

    	return text;
    }
}
