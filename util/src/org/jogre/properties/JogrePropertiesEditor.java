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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jogre.common.FileUtils;

/**
 * Properties editor.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class JogrePropertiesEditor extends JFrame {

	// Declare constants
	private static final double PAD  = 10;
	private static final double PREF = TableLayout.PREFERRED;
	private static final double FILL = TableLayout.FILL;
	private static final int TAB_DIVIDE = 190;
	
	private static final String TITLE = "Properties Editor";

	// Declare GUI items
	private JTextField jogreHomeDir;
	private JButton    browseButton, refreshButton;
	private JMenuItem  menuSave, menuQuit;
	private JMenuItem  menuCopy, menuCut, menuPaste;
	private JMenuItem  menuUsage, menuAbout;

	private PropertiesParser parser;
	private PropertiesPanel propertyPanel;
	private FilesTable filesTable;
	
	/**
	 * GUI constructor.
	 */
	public JogrePropertiesEditor () throws IOException {

		// Create fields
		this.parser = new PropertiesParser ();
		
		// Create GUI items
		createGUI ();

		// Add listeners
		addListeners ();
	}

	/**
	 * Create GUI items.
	 */
	private void createGUI () throws IOException {
		// Set title
		setTitle (TITLE);
		
		// Create menu
		setJMenuBar(getMenu());
		
		// Create main panel
		getContentPane().add (getMainPanel (), BorderLayout.CENTER);
		
		pack();
		
		setSize(800, 300);
		Dimension size = getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		Point p = new Point (
			(int)(screenSize.getWidth() / 2 - size.getWidth() / 2), 
			(int)(screenSize.getHeight() / 2 - size.getHeight() / 2));
		setLocation (p.x, p.y);
		setVisible (true);
	}
	
	/**
	 * Create main panel
	 * 
	 * @return
	 */
	private JPanel getMainPanel () throws IOException {
		// Create main panel
		JPanel panel = new JPanel (new TableLayout (new double [][] {{FILL}, {PREF, FILL}}));
		
		// Create jogre home panel
		double [][] sizes = {{PAD, PREF, PAD, FILL, 5, PREF, PAD}, {PAD, PREF, PAD}};
		JPanel locationPanel = new JPanel (new TableLayout (sizes)); 
		JLabel label = new JLabel ("JOGRE Home: ");			
		this.jogreHomeDir = new JTextField ();
		this.browseButton = new JButton ("Browse");
		File file = new File (FileUtils.getExecutionDir ());
		if (file.exists() && file.getParentFile().exists())
			this.jogreHomeDir.setText(file.getParent());
		locationPanel.add (label,        "1,1");
		locationPanel.add (jogreHomeDir, "3,1");
		locationPanel.add (browseButton, "5,1");
		
		// Create split pane
		JPanel filesPanel = new JPanel (new TableLayout (new double [][] {{FILL}, {PREF, 5, FILL}}));
		this.refreshButton = new JButton ("Refresh");		
		this.filesTable = new FilesTable (parser);
		
		filesPanel.add (refreshButton, "0,0");
		filesPanel.add (filesTable.getScrolledTable(), "0,2");
		
		// Create properties panel
		this.propertyPanel = new PropertiesPanel (this, parser);	
		sizes = new double [][] {{FILL}, {FILL}};
		JPanel rightPanel = new JPanel (new TableLayout (sizes));				
		rightPanel.add (propertyPanel,   "0,0");
		
		JSplitPane splitPane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT, filesPanel, rightPanel);
		splitPane.setDividerLocation (TAB_DIVIDE);
        splitPane.setOneTouchExpandable (true);
				
		// Add to main panel
		panel.add (locationPanel, "0,0");
		panel.add (splitPane,     "0,1");
		
		// Return
		return panel;
	}

	/**
	 * Add listeners.
	 */
	private void addListeners () {
		// Quit
		menuQuit.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				quit ();
			}			
		});			
		// Usage menu option
		menuUsage.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				usage ();
			}			
		});
		
		// Usage menu option
		menuAbout.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				about ();
			}			
		});				

		// Browse button
		browseButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				browseDir ();
			}			
		});
		
		// Browse button
		refreshButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				refreshFiles ();
			}			
		});
		
		addWindowListener(new WindowAdapter () {
			public void windowClosed(WindowEvent event) {
				quit();
			}
		});		
		
		filesTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener () {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) return;
					ListSelectionModel lsm = (ListSelectionModel)e.getSource();
					if (!lsm.isSelectionEmpty()) {
						int row = filesTable.getSelectedRow();
						String app = (String)filesTable.getValueAt(row, 0);
						String file = (String)filesTable.getValueAt(row, 1);
						refreshContents (app, file);
						
						if (row == 0)
							setTitle (TITLE + " - All");
						else
							setTitle (TITLE + " - [" + app + "] " + file + ".properties");
					}			
				}					
			}
		);
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
	}
	
	/**
	 * Refresh files.
	 */
	public void refreshFiles () {
		// Retrieve JOGRE home from text directory.
		try {
			File jogreHome = new File (jogreHomeDir.getText());

			if (jogreHome.exists()) {
				parser.refreshFiles (jogreHome.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Refresh contents of panel.
	 * 
	 * @param app
	 * @param propFilename
	 */
	public void refreshContents (String app, String propFilename) {
		if (parser.getPropertyFile (app, propFilename) != null) {
			propertyPanel.setPropertyFile (app, propFilename);
		}
	}
	
	/**
	 * Return menu bar.
	 * 
	 * @return
	 */
	private JMenuBar getMenu () {
		// New menu bar
		JMenuBar menuBar = new JMenuBar ();
		JMenu fileMenuBar = new JMenu ("File"); fileMenuBar.setMnemonic('F');
		JMenu editMenuBar = new JMenu ("Edit"); editMenuBar.setMnemonic('E');
		JMenu helpMenuBar = new JMenu ("Help"); helpMenuBar.setMnemonic('H');
		
		menuBar.add (fileMenuBar);
		menuBar.add (editMenuBar);
		menuBar.add (helpMenuBar);
		
		// Create menu items
		menuSave  = new JMenuItem ("Save",  'S');  fileMenuBar.add (menuSave);
		menuQuit  = new JMenuItem ("Quit",  'Q');  fileMenuBar.add (menuQuit);
		menuCut   = new JMenuItem ("Cut",   'T');  editMenuBar.add (menuCut);
		menuCopy  = new JMenuItem ("Copy",  'Q');  editMenuBar.add (menuCopy);		
		menuPaste = new JMenuItem ("Paste", 'p');  editMenuBar.add (menuPaste);
		menuUsage = new JMenuItem ("Usage", 'U');  helpMenuBar.add (menuUsage);
		menuAbout = new JMenuItem ("About", 'A');  helpMenuBar.add (menuAbout);
		
		return menuBar;
	}
	
	/**
	 * Browse to a directory using the browse button. 
	 */
	private void browseDir () {
		JFileChooser fc = new JFileChooser(new File (jogreHomeDir.getText()));				
		fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fc.showDialog (JogrePropertiesEditor.this, "OK");
		if (returnValue == JFileChooser.APPROVE_OPTION) 
			jogreHomeDir.setText(fc.getSelectedFile().toString() + File.separator);
	}
	
	/**
	 * Quit. 
	 */
	private void quit () {
		System.exit (0);
	}
	
	/**
	 * Usage.
	 */
	private void usage () {
		String message =
	        "<html><head></head><body>" +
	        "<p>JOGRE Properties Editor</p><br>" +
	        "<p>JOGRE Project (GNU General Public Licence)</p>" +
	        "<p>http://jogre.sourceforge.net</p>" +
	        "<p><br>todo</p>" +
	        "</body></html>";

	    // Show about box
	    JOptionPane.showMessageDialog (
            this,
            message,
            "JOGRE Properties Editor",
            JOptionPane.INFORMATION_MESSAGE
        );
	}
	
	/**
	 * About application.
	 */
	private void about () {
		String message =
	        "<html><head></head><body>" +
	        "<p>JOGRE Properties Editor</p><br>" +
	        "<p>JOGRE Project (GNU General Public Licence)</p>" +
	        "<p>http://jogre.sourceforge.net</p>" +
	        "<p><br>Programmed by: -<br>Bob Marks (project manager)</p>" +
	        "<p><br>Copyright 2004-2007</p>" +
	        "</body></html>";

	    // Show about box
	    JOptionPane.showMessageDialog (
            this,
            message,
            "JOGRE Properties Editor - About",
            JOptionPane.INFORMATION_MESSAGE
        );
	}
	
	/**
	 * Main method to properties editor.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main (String [] args) throws IOException {
		JogrePropertiesEditor editor = new JogrePropertiesEditor ();
	}	
}
