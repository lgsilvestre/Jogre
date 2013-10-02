/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.client.awt;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;

import org.jogre.common.User;
import org.jogre.common.util.JogreLabels;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Enumeration;

/**
 * Panel which displays rules.
 *
 * This reads the rules from an XML file and displays them in a dialog
 * box with two panes.  The left pane is a tree view of index marks and
 * the right pane is the text.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class JRulesDialog extends JogreDialog implements TreeSelectionListener {

	private JTree sectionTree;

	private JTextPane textPane;
	private String contentType;

	JogreLabels labels;

	/**
	 * Constructor for applets.
	 */
	public JRulesDialog (String rulesFilename) {
		super ();

		labels = JogreLabels.getInstance();
		setUpGUI(readRulesFile(getJarReader(rulesFilename), rulesFilename));
	}

	/**
	 * Constructor for applications.
	 *
	 * @param owner
	 */
	public JRulesDialog (Frame owner, String rulesFilename) {
		super ();

		labels = JogreLabels.getInstance();
		setUpGUI(readRulesFile(getFileReader(rulesFilename), rulesFilename));
	}

	/**
	 * This creates the dialog box and the tree view and the text pane and
	 * all of the nifty GUI stuff.
	 *
	 * @param docRoot		The XML tree of the document that we're to display
	 */
	private void setUpGUI (XMLElement docRoot) {
		// Create the text pane and put it in a scroll pane.
		textPane = new JTextPane();
		textPane.setContentType(contentType);

		JScrollPane textScrollPane = new JScrollPane(textPane);
		textScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textScrollPane.setPreferredSize(new Dimension(500, 350));
		textScrollPane.setMinimumSize(new Dimension(10, 10));

		// Build the logical tree (which also creates the document
		// for the text pane created above) and create the tree view
		// and put it into a scroll pane
		DefaultMutableTreeNode sectionTreeRoot = buildTree(docRoot);
		sectionTree = new JTree(sectionTreeRoot);
		sectionTree.setRootVisible(false);

		JScrollPane sectionTreeScrollPane = new JScrollPane(sectionTree);
		sectionTreeScrollPane.setPreferredSize(new Dimension(200, 350));
		sectionTreeScrollPane.setMinimumSize(new Dimension(10, 10));

		// Set this dialog to be a listener to selections of the tree view
		sectionTree.addTreeSelectionListener(this);
//		sectionTree.getSelectionModel().setSelectionMode
//						(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Lock the text pane since we've finished adding all of the text to it.
		textPane.setEditable(false);

		// Put the Caret back at the top so that the first line of the rules
		// is displayed upon creation.
		textPane.setCaretPosition(0);

		// Create the split plane that will hold the tree & text views
		JSplitPane splitPane = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			sectionTreeScrollPane,
			textScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		// Add the split pane to the window and show it
		getContentPane().add(splitPane);
		pack ();
		setLocationRelativeTo(this);

		// Set the title of the dialog to "Rules"
		this.setTitle(labels.get("rules"));

		// Show the dialog box
		setVisible (true);
	}

	/**
	 * Open a file in the file system for reading the rules.
	 *
	 * @param  filename   The name of the rules file
	 */
	private Reader getFileReader(String filename) {
		FileReader reader = null;

		// Try to open the given file
		try {
			reader = new FileReader(filename);
		} catch (Exception e) {
		}

		return reader;
	}

	/**
	 * Open a file in the Jar for reading the rules.
	 *
	 * @param  filename   The name of the rules file
	 */
	private Reader getJarReader(String filename) {
		BufferedReader reader = null;

		// Try to open the given file from the JAR
		try {
			reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
		} catch (Exception e) {
		}

		return reader;
	}

	/**
	 * Read the xml rules file into an XML database tree
	 *
	 * @param   reader    The reader to read the rules from
	 * @param   filename  The file name.
	 *                       Used for messages in case of errors
	 * @return the root of the XML tree
	 */
	private XMLElement readRulesFile(Reader reader, String filename) {
		XMLElement newTree = new XMLElement();

		if (reader == null) {
			contentType = "text";
			return (createErrorTree(labels.get("cannot.open.rules.file") + " " + filename));
		}

		// Try to parse it, looking for an element "rules"
		contentType = null;
		while (contentType == null) {
			try {
				newTree.parseFromReader(reader);
			} catch (IOException e) {
				newTree = createErrorTree(labels.get("cannot.read.rules.file") + " " + filename);
			} catch (XMLParseException e) {
				newTree = createErrorTree(labels.get("cannot.parse.rules.file") + " " + filename);
			}

			if (newTree.getName().equals("rules")) {
				// We found the rules, so check the content type
				contentType = checkContentType(newTree.getStringAttribute("content"));
			}
		}

		// Close the file
		try {
			reader.close();
		} catch (IOException e) {
		}

		// And return the tree just built
		return (newTree);
	}

	/**
	 * Create an XML tree with error text so that the user sees something when
	 * things go bad.
	 *
	 * @param	errString			The text to put into the text body
	 * @returns an XML tree
	 */
	private XMLElement createErrorTree(String errString) {
		XMLElement root = new XMLElement("rules");
		root.setAttribute("title", labels.get("rules"));

		XMLElement child = new XMLElement("section");
		child.setAttribute("title", labels.get("error"));
		child.setContent(errString);

		root.addChild(child);

		return(root);
	}

	/**
	 * Check the content type string to make sure it is a known type.  If it
	 * is, then return that type.  If it isn't a known type, then return plain
	 * text.
	 *
	 * @param		possibleType		The possible type to check against
	 * @return	a valid content type.
	 */
	private String checkContentType(String possibleType) {
		// If there is no type, then default to plain text
		if (possibleType == null) {
			return ("text");
		}

		// If it isn't HTML or RTF, then default to plain text
		if ( !(possibleType.equals("text/html")) &&
			 !(possibleType.equals("text/rtf"))) {
			return ("text");
		}

		return (possibleType);
	}

	/**
	 * Build a JTree structure from the given XML data.  This also
	 * creates the entire viewable document by adding all of sections'
	 * CDATA into the textPane's document.
	 *
	 * @param	theXmlData	The XML tree of the rules file
	 * @return		The top-level tree node of the JTree
	 */
	private DefaultMutableTreeNode buildTree(XMLElement theXmlData) {
		DefaultMutableTreeNode node = null;
		int curr_length = textPane.getStyledDocument().getLength();

		// If this segment has a title attribute, then create a new JTreeElement
		// for it.  (If it doesn't have a title attribute, then we'll just add
		// it's text to the document, but it doesn't get an index marker.)
		if (theXmlData.getStringAttribute("title") != null) {
			// Create a new JTreeXMLElement from the current XML data that will display
			// the "title" parameter.
			JTreeXMLElement jTreeEl = new JTreeXMLElement(theXmlData, "title", curr_length);

			// Create a new node for the Jtree with the jTreeElement
			node = new DefaultMutableTreeNode (jTreeEl);

			if (theXmlData.countChildren() != 0) {
				// Add the children of this element to the tree
				Enumeration theEnum = theXmlData.enumerateChildren();
				while (theEnum.hasMoreElements()) {
					DefaultMutableTreeNode childNode = buildTree((XMLElement)theEnum.nextElement());
					if (childNode != null) {
						node.add(childNode);
					}
				}
			} else {
				// Add the text of this leaf to the document
				addStringToContent(theXmlData.getContent(), curr_length);
			}
		} else {
			// Add the text of this un-titled segment to the document
			addStringToContent(theXmlData.getContent(), curr_length);
		}

		return (node);
	}

	/**
	 * Add new string data to the contents of the document in the textPane.
	 *
	 * @param	newContent		The string to add
	 * @param	insertionPoint	The index of where to begin adding the text.
	 */
	private void addStringToContent(String newContent, int insertionPoint) {
		StringReader sr = new StringReader(newContent);
		try {
			textPane.getEditorKit().read(sr, textPane.getStyledDocument(), insertionPoint);
		} catch (IOException e) {
		} catch (BadLocationException e) {
		}
	}

	/**
	 * This routine is called when the user selects a different entry of the tree.
	 *
	 * @param	e		The Tree selection event
	 */
	public void valueChanged(TreeSelectionEvent e) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
								sectionTree.getLastSelectedPathComponent();

		if (node != null) {
			scrollToPosition(((JTreeXMLElement) node.getUserObject()).caretPos);
		}
	}

	/**
	 * This routine will scroll the text pane such the requested logical
	 * index in the text is in the upper left corner of the pane.
	 *
	 * @param	e		The Tree selection event
	 */
	private void scrollToPosition(int index) {
		Rectangle r;

		// Convert the index to a screen point.
		try {
			r = textPane.modelToView(index);
		} catch (BadLocationException e) {
			// If we can't find the location (for some strange reason),
			// then scroll to the top of the document.
			r = new Rectangle (0, 0, 1, 1);
		}

		// Set the height of the rectange to the height of the window.
		// This will force our logical point to the top of the visible window
		r.height = textPane.getVisibleRect().height;

		// Scroll to that point
		textPane.scrollRectToVisible(r);
	}
}
