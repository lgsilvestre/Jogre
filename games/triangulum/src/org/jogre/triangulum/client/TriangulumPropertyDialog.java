/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.client;

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
 * Property Dialog for the Triangulum game
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Beta 0.3
 */
public class TriangulumPropertyDialog extends JogrePropertyDialog {

	// Names used for the parts of the property dialog
	private static final String FLAVOR_NAME = "flavor";

	// The choices for game type
	private static final String [] validFlavors = {"36", "60"};

	/**
	 * Constructor
	 */
	public TriangulumPropertyDialog(Frame owner, String title, ClientConnectionThread conn) {
		super(owner, title, true, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {

		// Get the setting for the board size from the dialog box
		int index = this.getJogreLabelIndexByName(labels, FLAVOR_NAME);
		int selected = 0;
		if (index > -1) {
			selected = ((JComboBox) components.get(index)).getSelectedIndex();
		}

		// Add the locations for that layout as a property to the table
		newTable.addProperty (FLAVOR_NAME, validFlavors[selected]);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {

		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();

		// Add board size selection to the property dialog
		JogreLabel flavorName = new JogreLabel(gameLabels.get("config.gametype") + " ");
		flavorName.setName(FLAVOR_NAME);
		labels.add(flavorName);

		// Create the array of text strings that are the options for the flavor box.
		String [] flavorText = new String [validFlavors.length];
		for (int i=0; i<flavorText.length; i++) {
			flavorText[i] = gameLabels.get("config.gametype." + validFlavors[i]);
		}

		// Create the combo box with the valid flavors
		JComboBox theBox = new JComboBox(flavorText);
		theBox.setSelectedIndex(0);

	    components.add(theBox);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}

}
