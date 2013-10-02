/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.client;

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
 * NinetyNine Property Dialog
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Alpha 0.2.3
 */
public class NinetyNinePropertyDialog extends JogrePropertyDialog {

	// Names used for the parts of the property dialog
	private static final String GAME_LENGTH_NAME = "game_length";

	// Array of lengths for a given index in the selection ComboBox
	private static final int [] lengthArray = new int [] {9, 6, 3};

	/**
	 * Constructor
	 */
	public NinetyNinePropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super(owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {

		// Get the setting for the selected track from the dialog box
		int index = this.getJogreLabelIndexByName(labels, GAME_LENGTH_NAME);
		int selected = 0;
		if (index > -1) {
			selected = ((JComboBox) components.get(index)).getSelectedIndex();
		}

		// Add that name as a property to the table
		newTable.addProperty ("rounds", Integer.toString(lengthArray[selected]));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {

		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();

		// Add game length selection to the property dialog
		JogreLabel lengthLabel = new JogreLabel(gameLabels.get("properties.length") + " ");
		lengthLabel.setName(GAME_LENGTH_NAME);
		labels.add(lengthLabel);

		// Create the combo box with length options
		String [] lengthSelections = new String [] {
			gameLabels.get("properties.len9"),
			gameLabels.get("properties.len6"),
			gameLabels.get("properties.len3")};
	    components.add(new JComboBox(lengthSelections));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}
}
