/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.client;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;
import javax.swing.JTextField;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePropertyDialog;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.GameLabels;

import nanoxml.XMLElement;

/**
 * Grand Prix Jumping Property Dialog
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Beta 0.3
 */
public class JumpingPropertyDialog extends JogrePropertyDialog {

	// Names used for the parts of the property dialog
	private static final String INITIAL_LAYOUT_NAME = "initial_layout";
	private static final String LAYOUT_CODE_NAME = "layout_code";
	private static final String OPEN_HANDS_NAME = "open_hands";
	private static final String ALLOW_EDITS_NAME = "allow_edits";

	/**
	 * Constructor
	 */
	public JumpingPropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super(owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {

		// Get the setting for open hands from the dialog box
		int index = this.getJogreLabelIndexByName(labels, OPEN_HANDS_NAME);
		String setting = ((JCheckBox) components.get(index)).isSelected() ? "t" : "f";
		newTable.addProperty ("openHands", setting);

		// Get the setting for allowing edits from the dialog box
		index = this.getJogreLabelIndexByName(labels, ALLOW_EDITS_NAME);
		setting = ((JCheckBox) components.get(index)).isSelected() ? "t" : "f";
		newTable.addProperty ("allowEdits", setting);

		// Get the setting for the initial layout from the dialog box
		index = this.getJogreLabelIndexByName(labels, INITIAL_LAYOUT_NAME);
		int selected = 0;
		if (index > -1) {
			selected = ((JComboBox) components.get(index)).getSelectedIndex();
		}

		String layoutCode;
		if (selected == 0) {
			// We want a random layout
			layoutCode = JumpingLayouts.getRandomLayoutCode();
		} else if (selected == 1) {
			// We want to use the code provided in the layout code dialog
			// Get the Initial Layout code
			index = this.getJogreLabelIndexByName(labels, LAYOUT_CODE_NAME);
			layoutCode = ((JTextComponent) components.get(index)).getText();
		} else {
			// We want to use one of the preset layouts
			layoutCode = JumpingLayouts.getInstance().getLocationsFor(selected-2);
		}
		newTable.addProperty ("initialLayout", layoutCode);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {
		// Get an instance of the game properties to read the default values for the
		// checkboxes.
		GameProperties props = GameProperties.getInstance();
		boolean defaultOpenHands = props.getInt("preferences.defaultOpenHands", 0) == 1;
		boolean defaultAllowEdits = props.getInt("preferences.defaultAllowEdits", 0) == 1;

		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();

		// Add the "Open Hands" checkbox to the property dialog
		JogreLabel openHands = new JogreLabel("");
		openHands.setName(OPEN_HANDS_NAME);
		labels.add(openHands);

		components.add(new JCheckBox(gameLabels.get("properties.openHands"), defaultOpenHands));

		// Add the "Allow Edits" checkbox to the property dialog
		JogreLabel allowEdits = new JogreLabel("");
		allowEdits.setName(ALLOW_EDITS_NAME);
		labels.add(allowEdits);

		components.add(new JCheckBox(gameLabels.get("properties.allowEdits"), defaultAllowEdits));

		// Add layout selection to the property dialog
		JogreLabel layoutName = new JogreLabel(gameLabels.get("properties.initialLayout") + " ");
		layoutName.setName(INITIAL_LAYOUT_NAME);
		labels.add(layoutName);

		String [] fixedTitles = new String[2];
		fixedTitles[0] = gameLabels.get("properties.initialLayout.random");
		fixedTitles[1] = gameLabels.get("properties.initialLayout.code");
		String [] validLayoutNames = JumpingLayouts.getInstance().getTitles(fixedTitles);

		components.add(new JComboBox(validLayoutNames));

		// Add the track code entry to the property dialog
		JogreLabel layoutCode = new JogreLabel(gameLabels.get("properties.initialLayout.enterCode"));
		layoutCode.setName(LAYOUT_CODE_NAME);
		labels.add(layoutCode);

		components.add(new JTextField(16));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}

}
