/*
 * JOGRE (Java Online Gaming Real-time Engine) - Generator
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
package org.jogre.gamegen;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jogre.common.FileUtils;

/**
 * GUI front end to the game generator.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameGeneratorGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Declare constants
	private static final double PAD  = 10;
	private static final double PREF = TableLayout.PREFERRED;
	private static final double FILL = TableLayout.FILL;
	
	private static final int TAB_PARMETER = 0;
	private static final int TAB_CONSOLE = 1;
	
	// Declare fields
	private GameGeneratorProperties generatorProperties;
	
	// Declare GUI items
	private JMenuItem menuQuit, menuGenerate, menuUsage, menuAbout;
	private JButton generateButton, quitButton, browseButton;
	private JTextField dirTextBox;
	private HashMap paramTextBoxMap;
	private ConsolePanel consolePanel;
	private JTabbedPane tabbedPane;
	
	/**
	 * Generator GUI constructor.
	 */
	public GameGeneratorGUI () throws IOException {
		generatorProperties = new GameGeneratorProperties ();
		
		// Create GUI items
		createGUI ();
		
		// Add listeners
		addListeners ();
	}

	/**
	 * Create GUI items.
	 */
	private void createGUI () {
		// Set title
		setTitle ("JOGRE Game Generator");
		
		// Set up HashMap to store parameter text boxes in.
		paramTextBoxMap = new HashMap ();
		
		// Create new panel
		JPanel panel = getMainPanel ();
		getContentPane().add (panel, BorderLayout.CENTER);
		
		// Create menu
		setJMenuBar(getMenu());
		
		pack();
		
		Dimension size = getSize();
		Point p = GameGeneratorUtils.getCentredLocation (this, size);
		setLocation (p.x, p.y);
		setVisible (true);
	}
	
	/**
	 * Return menu bar.
	 * 
	 * @return
	 */
	private JMenuBar getMenu () {
		// New menu bar
		JMenuBar menuBar = new JMenuBar ();
		JMenu fileMenuBar = new JMenu ("File"); 
		fileMenuBar.setMnemonic('F');
		JMenu generateMenuBar = new JMenu ("Generate"); 
		generateMenuBar.setMnemonic('G');
		JMenu helpMenuBar = new JMenu ("Help"); 
		helpMenuBar.setMnemonic('H');
		
		menuBar.add (fileMenuBar);
		menuBar.add (generateMenuBar);
		menuBar.add (helpMenuBar);
		
		// Create menu items
		menuQuit     = new JMenuItem ("Quit", 'Q');
		menuGenerate = new JMenuItem ("Generate", 'G');
		menuUsage    = new JMenuItem ("Usage", 'U');
		menuAbout    = new JMenuItem ("About", 'A');
		
		fileMenuBar.add (menuQuit);
		generateMenuBar.add (menuGenerate);
		helpMenuBar.add (menuUsage);
		helpMenuBar.add (menuAbout);
		
		return menuBar;
	}
	
	/**
	 * Return main panel.
	 * 
	 * @return
	 */
	private JPanel getMainPanel () {
		double [][] sizes = {{PAD, FILL, PAD}, {PAD, FILL, PAD, PREF, PAD}};
		JPanel panel = new JPanel (new TableLayout (sizes));
			
		// Create scrolled parameter panel
		JScrollPane parameterScrollPane = new JScrollPane (getPametersPanel());
		parameterScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		parameterScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		// Create scrolled console panel
		this.consolePanel = new ConsolePanel();
		JScrollPane consoleScrollPane = new JScrollPane (consolePanel);
		
		// Create tabbed pane
		tabbedPane = new JTabbedPane (JTabbedPane.TOP);
		tabbedPane.add ("Parameters", parameterScrollPane);
		tabbedPane.add ("Console",    consoleScrollPane);
			
		// Update main panel and return
		panel.add (tabbedPane,       "1,1");
		panel.add (getButtonPanel(), "1,3,c,c");
				
		return panel; 
	}

	/**
	 * Create panel layout.
	 * 
	 * @return
	 */
	private JPanel getPametersPanel () {
		double [][] sizes = {{PAD, FILL, PAD},{PAD, PREF, PAD, PREF, PAD, PREF, PAD}};
		JPanel panel = new JPanel (new TableLayout (sizes));
		panel.setBorder (BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Parameters"));
		
		// Create mandatory and optional panes		
		panel.add (createMandatoryPametersPanel (), "1,1");
		panel.add (createDirPametersPanel (),  "1,3");
		panel.add (createOptionalPametersPanel (),  "1,5");
		
		// Create panel
		return panel;
	}
	
	/**
	 * Create directory parameter panel.
	 * 
	 * @return
	 */
	private Component createDirPametersPanel() {
		double [][] sizes = {{PAD, FILL, PAD, PREF, PAD},{PAD, PREF, PAD}};
		JPanel panel = new JPanel (new TableLayout (sizes));
		panel.setBorder (BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Directory (e.g. <jogre install dir>/games)"));
		
		dirTextBox = new JTextField (FileUtils.getExecutionDir ());
		browseButton = new JButton ("Browse");
		
		paramTextBoxMap.put (GameGeneratorProperties.DIR, dirTextBox);
		
		// Add items to panel
		panel.add (dirTextBox, "1,1");
		panel.add (browseButton, "3,1");
		
		// Create panel
		return panel;
	}

	/**
	 * Return mandatory properties panel.
	 * 
	 * @return
	 */
	private JPanel createMandatoryPametersPanel () {
		// Retrieve mandatory params		
		ArrayList mandatoryParams = generatorProperties.getParameters (false);
		int numOfParms = mandatoryParams.size();
		
		// Create panel
		JPanel panel = getParameterPanel (numOfParms);
		panel.setBorder (BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Mandatory Parameters"));
		
		// Add items to panel
		for (int i = 0; i < numOfParms; i++) {
			String param = (String)mandatoryParams.get(i);
			
			String defaultValue = generatorProperties.getDefaultValue(param);
			JTextField textField = new JTextField (defaultValue, 40);
			paramTextBoxMap.put (param, textField);
			
			// Add label / textbox			
			int y = 1 + (i * 2);
			panel.add (new JLabel (param), "1," + y + ",r,c");
			panel.add (textField, "3," + y + ",l,c");
		}
		
		return panel;
	}
	
	/**
	 * Return mandatory properties panel.
	 * 
	 * @return
	 */
	private JPanel createOptionalPametersPanel () {
		// Retrieve mandatory params		
		ArrayList optionalParams = generatorProperties.getParameters (true);
		optionalParams.remove("dir");		// dir is special case
		int numOfParms = optionalParams.size();
		
		// Create panel
		JPanel panel = getParameterPanel (numOfParms);
		panel.setBorder (BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Optional Parameters (click checkbox to use)"));
		
		// Add items to panel
		for (int i = 0; i < numOfParms; i++) {
			String param = (String)optionalParams.get(i);
			String defaultValue = generatorProperties.getDefaultValue(param);
			
			// Create GUI items
			JTextField textField = new JTextField (defaultValue, 40);
			JCheckBox paramCheckbox = new JCheckBox (param, false);
			paramCheckbox.setName(param);
			paramTextBoxMap.put (param, textField);
			paramCheckbox.addChangeListener(new ChangeListener () { //addActionListener(new ActionListener() {		
				public void stateChanged(ChangeEvent event) {
					JCheckBox paramCheckbox = (JCheckBox)event.getSource();
					String param = paramCheckbox.getName();
					JTextField textField = (JTextField)paramTextBoxMap.get (param);
					textField.setEnabled(paramCheckbox.isSelected());
				}				
			});	
			
			// Set initial state
			paramCheckbox.setSelected(false);
			textField.setEnabled(paramCheckbox.isSelected());
				
			// Add label / textbox
			int y = 1 + (i * 2);
			panel.add (paramCheckbox, "1," + y + ",r,c");
			panel.add (textField, "3," + y + ",l,c");
		}
		
		return panel;
	}
	
	/**
	 * Return a parameter panel.
	 * 
	 * @param numOfParams
	 * @return
	 */
	private JPanel getParameterPanel (int numOfParams) {
		// Create layout based on number of parameters		
		int rowCount = (numOfParams * 2) + 1;
		double [] columns = {PAD, 100, PAD, PREF, PAD};
		double [] rows = new double [rowCount];
		rows[0] = PAD; 
		for (int i = 0; i < numOfParams; i++) { 
			rows [1 + (i * 2)] = PREF;
			rows [2 + (i * 2)] = PAD;
		}
		
		// Create panel and return
		JPanel panel = new JPanel (new TableLayout (new double [][] {columns, rows}));
		return panel;
	}

	/**
	 * Return button panel.
	 * 
	 * @return
	 */
	private Component getButtonPanel() {
		double [][] sizes = {{PREF, PAD, PREF},{PREF}};
		JPanel panel = new JPanel (new TableLayout (sizes));
		
		this.generateButton = new JButton ("Generate");
		this.quitButton     = new JButton ("Quit");

		panel.add (generateButton, "0,0");
		panel.add (quitButton, "2,0");
		
		return panel;
	}
	
	/**
	 * Add listeners to items
	 */
	private void addListeners() {
		// Quit (button option and menu item)
		quitButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				quit ();
			}			
		});	
		menuQuit.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				quit ();
			}			
		});	
		
		// Browse button
		browseButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				browseDir ();
			}			
		});
		
		// Generate (button and menu option)
		generateButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				generate ();
			}			
		});
		menuGenerate.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				generate ();
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
		
		addWindowListener(new WindowAdapter () {
			public void windowClosed(WindowEvent event) {
				quit();
			}
		});
		
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
	}
	
	/**
	 * Browse to a directory using the browse button. 
	 */
	private void browseDir () {
		JFileChooser fc = new JFileChooser(new File (dirTextBox.getText()));				
		fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
		int returnValue = fc.showDialog (GameGeneratorGUI.this, "OK");
		if (returnValue == JFileChooser.APPROVE_OPTION) 
			dirTextBox.setText(fc.getSelectedFile().toString() + File.separator);
	}
	
	/**
	 * Quit application.
	 */
	private void quit () {
		System.exit (0);
	}
	
	/**
	 * Generate game.  This includes converting input in text items to the
	 * format which the main generator class can understand e.g. -game_id=x
	 */
	private void generate () {
		if (validateTextBoxes()) {
			
			// Create argument list from textfields
			ArrayList argList = new ArrayList ();
			ArrayList params = generatorProperties.getParameters ();
			for (int i = 0; i < params.size(); i++) {
				String param = (String)params.get(i);
				JTextField textField = (JTextField)paramTextBoxMap.get(param);
				
				// Only check enabled text boxes
				if (textField.isEnabled()) {
					String text = textField.getText().trim();
					argList.add ("-" + param + "=" + text);
				}
			}
			
			// Convert to String array
			String [] args = new String [argList.size()];
			argList.toArray(args);
			
			// Actually generate game with supplied parameters
			generate (args);
		}
	}
	
	/**
	 * Create game.
	 * 
	 * @param args
	 */
	private void generate (String [] args) {
		try {			
			// Wipe console and set tab pane to console pane.
			tabbedPane.setSelectedIndex(TAB_CONSOLE);
			consolePanel.clear();
			
			// Create generator class and generate content
			GameGenerator gen = new GameGenerator (args);
			gen.generate();
			
			// Display successful message to user.
			infoMessage ("Game successfully created");
		}
		catch (GameGeneratorException genEx) {
			errorMessage ("Generator error: " + genEx.getMessage());
		}
		catch (IOException ioEx) {
			errorMessage ("IO error: " + ioEx.getMessage());
		}				
		catch (Exception genEx) {
			errorMessage ("\nGeneral error" + genEx.getMessage());
		}
	}
	
	/**
	 * Show usage screen.
	 */
	private void usage () {
		try {
			// Wipe console and set tab pane to console pane.
			tabbedPane.setSelectedIndex(TAB_CONSOLE);
			consolePanel.clear();
			
			// Display usage
			GameGenerator.usage();
		}
		catch (IOException ioEx) {
			errorMessage ("IO error: " + ioEx.getMessage());
		} 
	}
	
	/**
	 * About application.
	 */
	private void about () {
		String message =
	        "<html><head></head><body>" +
	        "<p>JOGRE Generator</p><br>" +
	        "<p>JOGRE Project (GNU General Public Licence)</p>" +
	        "<p>http://jogre.sourceforge.net</p>" +
	        "<p><br>Programmed by: -<br>Bob Marks (project manager)</p>" +
	        "<p><br>Copyright 2004-2007</p>" +
	        "</body></html>";

	    // Show about box
	    JOptionPane.showMessageDialog (
            this,
            message,
            "About JOGRE Generator",
            JOptionPane.INFORMATION_MESSAGE
        );
	}
	
	/**
	 * Validate the various text boxes and ensure they contain something sensible.
	 * 
	 * @return
	 */
	private boolean validateTextBoxes () {
		// Ensure  all the enabled textboxes are filled in properly
		ArrayList params = generatorProperties.getParameters ();
		for (int i = 0; i < params.size(); i++) {
			String param = (String)params.get(i);
			JTextField textField = (JTextField)paramTextBoxMap.get(param);
			
			// Only check enabled text boxes
			if (textField.isEnabled()) {
				String text = textField.getText().trim();
				if (text.equals("")) {
					errorMessage ("Please fill in parameter: [ " + param + " ]");
					return false;
				}
			}
		}
		
		// Check dir is actually a directory
		String dirText = ((JTextField)paramTextBoxMap.get(GameGeneratorProperties.DIR)).getText();
		File dirFile = new File (dirText);
		if (!dirFile.isDirectory()) {
			errorMessage ("Invalid directory");
			return false;
		}
		
		return true;		// everything ok
	}
	
	/**
	 * Show error message.
	 * 
	 * @param message
	 */
	private void errorMessage (String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display an information message.
	 * 
	 * @param message
	 */
	private void infoMessage (String message) {
		JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Create new instance of generator GUI.
	 * 
	 * @param args
	 */
	public static void main (String [] args) throws IOException {
		GameGeneratorGUI generatorGUI = new GameGeneratorGUI();
	}	
}