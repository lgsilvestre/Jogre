/*
 * JOGRE (Java Online Gaming Real-time Engine) - Hex
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
package org.jogre.hex.client;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePropertyDialog;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.GameLabels;

import nanoxml.XMLElement;

/**
 * Property Dialog for the Hex game
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Beta 0.3
 */
public class HexPropertyDialog extends JogrePropertyDialog {

	// Names used for the parts of the property dialog
	private static final String BOARD_SIZE_NAME = "boardSize";

	// The board sizes
	private static final String [] validBoardSizes = {"8", "9", "10", "11", "12", "13", "14"};
	private static final int DEFAULT_CHOICE_INDEX = 3;	// default to "11"

	/**
	 * Constructor
	 */
	public HexPropertyDialog(Frame owner, String title, ClientConnectionThread conn) {
		super(owner, title, true, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {

		// Get the setting for the board size from the dialog box
		int index = this.getJogreLabelIndexByName(labels, BOARD_SIZE_NAME);
		int selected = 0;
		if (index > -1) {
			selected = ((JComboBox) components.get(index)).getSelectedIndex();
		}

		// Add the locations for that layout as a property to the table
		newTable.addProperty ("boardSize", validBoardSizes[selected]);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {

		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();

		// Add board size selection to the property dialog
		JogreLabel boardSizeName = new JogreLabel(gameLabels.get("properties.boardSize") + " ");
		boardSizeName.setName(BOARD_SIZE_NAME);
		labels.add(boardSizeName);

		// Make the initial size 11
		JComboBox theBox = new JComboBox(validBoardSizes);
		theBox.setSelectedIndex(DEFAULT_CHOICE_INDEX);

	    components.add(theBox);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}

}
