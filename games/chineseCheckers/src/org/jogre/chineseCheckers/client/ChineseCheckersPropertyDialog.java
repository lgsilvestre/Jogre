/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
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
package org.jogre.chineseCheckers.client;

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
 * Property Dialog for the Chinese Checkers game
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Beta 0.3
 */
public class ChineseCheckersPropertyDialog extends JogrePropertyDialog {

	// Names used for the parts of the property dialog
	private static final String LONG_JUMP_NAME = "longJumps";

	/**
	 * Constructor
	 */
	public ChineseCheckersPropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super(owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {

		// Get the setting for long jumps from the dialog box
		int index = this.getJogreLabelIndexByName(labels, LONG_JUMP_NAME);
		String setting = ((JCheckBox) components.get(index)).isSelected() ? "t" : "f";
		newTable.addProperty ("longJumps", setting);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {
		// Get an instance of the game properties to read the default values for the dialog.
		GameProperties props = GameProperties.getInstance();
		boolean defaultLongJumps = props.getInt("preferences.defaultLongJumps", 0) == 1;
		int defaultNumPlayers = props.getInt("preferences.defaultNumPlayers", 2);

		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();

		// Add the "Long Jumps" checkbox to the property dialog
		JogreLabel openHands = new JogreLabel("");
		openHands.setName(LONG_JUMP_NAME);
		labels.add(openHands);

		components.add(new JCheckBox(gameLabels.get("properties.longJumps"), defaultLongJumps));

		// Set the default value of the numPlayers ComboBox (which is created in the superclass)
		int index = getJogreLabelIndexByName(labels, "players");
		((JComboBox) components.get(index)).setSelectedIndex(defaultNumPlayers - 2);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}

}
