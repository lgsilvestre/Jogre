/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.grandPrixJumping.client;

import java.lang.Integer;

import info.clearthought.layout.TableLayout;

import java.awt.Container;
import java.awt.Frame;

import java.awt.TextArea;

import java.util.ListIterator;
import java.util.Vector;

import org.jogre.common.util.GameLabels;
import org.jogre.client.awt.JogreDialog;

import org.jogre.grandPrixJumping.common.JumpingFaultHistoryElement;

/**
 * Dialog that displays the history of fault points scored in grandPrixJumping
 *
 * @author Richard Walter (rwalter42)
 * @version Alpha 0.2.3
 */
public class JumpingFaultHistoryDialog extends JogreDialog {

	/**
	 * Constructor
	 *
	 * @param	owner			The table frame that is the owner of this dialog
	 * @param	title			The title to use for the dialog box
	 * @param	history			The vectors of histories to be shown.
	 */
	public JumpingFaultHistoryDialog(
		Frame owner,
		Vector [] history,
		String [] headerNames
	) {
		super (owner, GameLabels.getInstance().get("history.title"), false);

		// Create the parts of the window
	    setUpGUI (history, headerNames);

	    // Show it.
		pack();
		setLocationRelativeTo(getOwner());
		setVisible (true);
	}

	/**
	 * Sets up the graphical user interface.
	 */
	private void setUpGUI (Vector [] history, String [] headerNames) {
	    // Get localized strings
		GameLabels labels = GameLabels.getInstance();

		// Set the sizing for the display
		double pref = TableLayout.PREFERRED;
		double SPACING = 5.0;
		double [] columns = {SPACING, pref, SPACING, pref, SPACING};
		double [] rows = {SPACING, pref, SPACING};

		// Set the layout for the content pane to the size arrays created above
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new TableLayout(columns, rows));

		// Add the objects to the pane
		contentPane.add(makeHistoryArea(labels, headerNames[0], history[0]), "1,1");
		contentPane.add(makeHistoryArea(labels, headerNames[1], history[1]), "3,1");
	}

	/*
	 * Create and fill in the TextArea for a player and return it.
	 *
	 * @param labels		Game labels object for converting the history events
	 *							into localized text.
	 * @param playerName	The string used for the heading line of the area.
	 * @param history		The history vector for this player.
	 * @return a TextArea with the history converted into localized text.
	 */
	private TextArea makeHistoryArea(GameLabels labels, String playerName, Vector history) {
		Object [] nameArray = {playerName};
		TextArea area = new TextArea(labels.get("history.heading", nameArray) + "\n", 10, 40);

		ListIterator iter = history.listIterator();
		while (iter.hasNext()) {
			JumpingFaultHistoryElement el = (JumpingFaultHistoryElement) iter.next();
			area.append(el.toString(labels) + "\n");
		}

		return area;
	}

}
