/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.server.administrator;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import nanoxml.XMLElement;

import org.jogre.common.util.JogreLabels;

/**
 * Panel for displaying the contents of somewhere on the tree.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminMessagePanel extends JPanel {

    private Color bgColor = new Color (255, 255, 255);

    private JTextPane textPane;
    private SimpleAttributeSet xmlTextSet, gameIDSet;
    private SimpleAttributeSet [] usernameSets;
    private Color [] usernameColours = {
        Color.blue, Color.red, Color.green, Color.cyan, Color.magenta
    };
    private int numOfColours;
    private Document doc;

    private boolean isShowingUser, isShowingGameID;

    private Vector users;

    /**
     * Consturctor.
     */
    public AdminMessagePanel () {
        super (new BorderLayout ());

        this.isShowingUser = true;
        this.isShowingGameID = false;
        this.users = new Vector ();

        // Set up simple attribute sets
        xmlTextSet = new SimpleAttributeSet ();
        StyleConstants.setForeground (xmlTextSet, Color.black);
        gameIDSet = new SimpleAttributeSet ();
        StyleConstants.setForeground (gameIDSet, Color.gray);

        // Set up username sets
        numOfColours = usernameColours.length;
        usernameSets = new SimpleAttributeSet [numOfColours];
        for (int i = 0; i < numOfColours; i++) {
			usernameSets [i] = new SimpleAttributeSet ();
            StyleConstants.setForeground (usernameSets[i], usernameColours[i]);
        }

        // Set background
        setBackground (bgColor);

        // Add a text area and disabled line wrapping.
        textPane = new JTextPane () {
            public void setSize (Dimension d) {
                if (d.width < getParent().getSize().width)
                    d.width = getParent().getSize().width;

                super.setSize(d);
            }

            public boolean getScrollableTracksViewportWidth(){
                return false;
            }
        };

        textPane.setFont (new Font ("Courier New", java.awt.Font.PLAIN, 11));
        textPane.setEditable(false);
        doc = textPane.getStyledDocument();

        // Add text are to a scroll pane
		JScrollPane scrollPane = new JScrollPane (textPane);
		add (scrollPane, BorderLayout.CENTER);
		add (getButtonPanel (), BorderLayout.NORTH);
    }

    /**
     * Return the button panel.
     *
     * @return
     */
    private JPanel getButtonPanel () {
        // Create a new panel for holding the buttons / options
        double pref = TableLayout.PREFERRED;
        double [][] sizes = {{pref, 5, pref, 5, pref, pref}, {pref}};
        JPanel panel = new JPanel (new TableLayout (sizes));

        JogreLabels labels = JogreLabels.getInstance();

        // Declare clear button
        JButton clearButton = new JButton (labels.get("clear"));
        clearButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event) {
                textPane.setText("");
            }
        });

        JLabel showLabel = new JLabel (labels.get("show")+ ": ");

        // Declare "show user" checkbox.
        final JCheckBox showUserCB = new JCheckBox (
            labels.get("user"), this.isShowingUser);
        showUserCB.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event) {
                isShowingUser = showUserCB.isSelected();
            }
        });

        // Declare show gameID button
        final JCheckBox showGameIDCB = new JCheckBox (
            labels.get("game.id"), this.isShowingGameID);
        showGameIDCB.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event) {
                isShowingGameID = showGameIDCB.isSelected();
            }
        });

        panel.add (clearButton,  "0,0");
        panel.add (showLabel,    "2,0");
        panel.add (showUserCB,   "4,0");
        panel.add (showGameIDCB, "5,0");

        return panel;
    }

    /**
     * Method for recieving any communcation from the server.
     *
     * @see org.jogre.server.ICommListener#getCommMessage(nanoxml.XMLElement, boolean)
     */
    public void addCommMessage (String gameID, String username, XMLElement message) {

        try {
        	if (username != null) {
	            // Retrieve index of users
	            if (!users.contains(username)) {
	                if (users.size() > 100)
	                    users = new Vector ();		// reclaim memory
	                users.add (username);
	            }
	            int index = users.indexOf(username) % numOfColours;  //*/
	           
	            doc.insertString (doc.getLength(), username, usernameSets[index]);
	
	            // Insert XML
	            doc.insertString (doc.getLength(), ": " + message.toString(true) + "\n", xmlTextSet);
        	}
        	else
        		doc.insertString (doc.getLength(), message.toString(true) + "\n", xmlTextSet);
        }
        catch (BadLocationException blEx) {} // dont' insert text
    }
}