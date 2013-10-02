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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Create languages panel.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ToolbarPanel extends JPanel implements Observer, ItemListener {
	
	// Fields
	private PropertiesPanel panel;
	private PropertiesParser parser;
	
	private JButton tableButtonAdd, tableButtonDelete, tableButtonUp, tableButtonDown;
	private JButton selectAllButton, selectNoneButton, translateButton;
	private Map langCheckBoxsMap = null;
	
	private JCheckBox formatCB;
	private JPanel checkBoxPanel;
	
	/**
	 * Create languages panel.
	 * 
	 * @param props 
	 * @param parser
	 */
	public ToolbarPanel (PropertiesPanel panel, PropertiesParser parser) {
		// Set fields
		this.panel = panel;
		this.parser = parser;
		
		// Check box map
		this.parser.addObserver (this);
		
		setUpGui ();
		
		addListeners ();
	}

	/**
	 * Set up GUI.
	 */
	private void setUpGui () {
		double pref = TableLayout.PREFERRED;
		
		// Create checkbox panel		
		double [][] sizes = {{pref, pref, pref, pref, pref, pref, pref, pref, 
			pref, pref, pref, pref, pref, pref, pref, pref, pref, pref},{pref}};
		this.checkBoxPanel = new JPanel (new TableLayout (sizes));
		this.checkBoxPanel.setBorder(BorderFactory.createEtchedBorder());
		
		// Create buttons
		this.selectAllButton  = new JButton ("All");
		this.selectNoneButton = new JButton ("None");
		this.translateButton = new JButton ("Translate");
		
		this.tableButtonAdd = new JButton ("+");
		this.tableButtonDelete = new JButton ("-");
		this.tableButtonUp = new JButton ("/\\");
		this.tableButtonDown = new JButton ("\\/");
		
		this.formatCB = new JCheckBox ("Show Format");
		
		// Set this layout
		sizes = new double [][] {{5, pref, 5, pref, pref, 5, pref, pref, pref, pref, 5, pref, 5, pref, 5}, {5, pref, 5}};
		setLayout(new TableLayout (sizes));
		add (checkBoxPanel,     "1,1");
		add (selectAllButton,   "3,1");
		add (selectNoneButton,  "4,1");
		
		add (tableButtonAdd,    "6,1");
		add (tableButtonDelete, "7,1");
		add (tableButtonUp,     "8,1");
		add (tableButtonDown,   "9,1");
		
		add (translateButton,   "11,1");
		
		add (formatCB,          "13,1");
	}
	
	/**
	 * Add listeners.
	 */
	private void addListeners () {
		selectAllButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				selectCBs (true);
			}			
		});
		selectNoneButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				selectCBs (false);
			}			
		});
		formatCB.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				parser.refreshObservers();
			}			
		});
		translateButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				translate();
			}			
		});
		tableButtonAdd.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				addRow ();
			}			
		});
		tableButtonDelete.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				
			}			
		});
	}
	
	/**
	 * Select check boxes.
	 * 
	 * @param selected
	 */
	private void selectCBs (boolean selected) {
		Iterator it = langCheckBoxsMap.values().iterator();
		while (it.hasNext()) 
			((JCheckBox)it.next()).setSelected(selected);
		parser.refreshObservers();
	}
	
	private void addRow () {
		int row = panel.getPropTable().getSelectedRow();
		int newRow = row ++;
		panel.getPropTable().getPropTableModel ().addRow (newRow);
	}
	
	/**
	 * Update.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable o, Object arg) {
		try {
			PropertiesProperties props = PropertiesProperties.getInstance();
			
			// Reset
			if (langCheckBoxsMap == null) {			// first time only
				this.langCheckBoxsMap = new HashMap ();
				this.checkBoxPanel.removeAll();
				this.langCheckBoxsMap = new HashMap ();

				String [] langs = parser.getLangs();
				for (int i = 0; i < langs.length; i++) {
					JCheckBox cb = new JCheckBox (langs[i], true);
					langCheckBoxsMap.put (langs[i], cb);
					checkBoxPanel.add (i + ",0", cb);
					cb.setBackground(props.getColor (langs[i]));
					cb.addItemListener(this);
				}
			}
			getParent().validate ();
			getParent().invalidate ();
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
	}
	
	/**
	 * Return true/false if a lang is selected or not.
	 * 
	 * @param lang
	 * @return
	 */
	public boolean isLangSelected (String lang) {		
		if (langCheckBoxsMap.get(lang) != null) 
			return ((JCheckBox)langCheckBoxsMap.get(lang)).isSelected();		
		return false;
	}
	
	/**
	 * Return lang selections.
	 * 
	 * @return
	 */
	public List getLangSelections () {
		List langList = new ArrayList ();
		String [] langs = parser.getLangs();
		for (int i = 0; i < langs.length; i++) {
			if (isLangSelected(langs[i]))
				langList.add(langs[i]);
		}
		return langList;
	}
	
	/**
	 * Return true/false if format checkbox is selected.
	 * 
	 * @return
	 */
	public boolean isFormatSelected () {
		return this.formatCB.isSelected();
	}
	
	/**
	 * Update
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged (ItemEvent e) {
		parser.refreshObservers();
	}
	
	/**
	 * Translate word from english to specified language using google API.
	 */
	private void translate () {
		parser.refreshObservers();
	}
}