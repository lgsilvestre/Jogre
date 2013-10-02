/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.client;

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

import org.jogre.carTricks.common.CarTricksCustomGameProperties;

import nanoxml.XMLElement;

/**
 * Car Tricks Property Dialog
 *
 * @author Richard Walter (rwalter42)
 * @version Alpha 0.2.3
 */
public class CarTricksPropertyDialog extends JogrePropertyDialog {

	// The custom game properties provided by the server when connecting
	private CarTricksCustomGameProperties customGameProperties = null;

	// Names used for the parts of the property dialog
	private static final String TRACKNAME_NAME = "track_name";
	private static final String ENABLE_EVENT_NAME = "enable_event_checkbox";

	/**
	 * Constructor
	 */
	public CarTricksPropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super(owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {

		// Get the setting for the selected track from the dialog box
		int index = this.getJogreLabelIndexByName(labels, TRACKNAME_NAME);
		int selected = 0;
		if (index > -1) {
			selected = ((JComboBox) components.get(index)).getSelectedIndex();
		}

		// Add that name as a property to the table
		newTable.addProperty ("trackName", customGameProperties.getTrackName(selected));
		newTable.addProperty ("fp", Integer.toString(customGameProperties.getFingerprint(selected)));

		// Get the setting for the enabled event cards from the dialog box
		index = this.getJogreLabelIndexByName(labels, ENABLE_EVENT_NAME);
		String setting = ((JCheckBox) components.get(index)).isSelected() ? "t" : "f";
		newTable.addProperty ("enableEventCards", setting);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {
		String [] validTrackNames;

		// Get the custom game properties which has the list of tracks (if we haven't already)
		if (customGameProperties == null) {
			customGameProperties = new CarTricksCustomGameProperties(customGamePropertiesTree);
		}

		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();

		// Add Track selection to the property dialog
		JogreLabel trackName = new JogreLabel(gameLabels.get("properties.track") + " ");
		trackName.setName(TRACKNAME_NAME);
		labels.add(trackName);

		// Get the list of tracks from the server
		validTrackNames = customGameProperties.getTrackList();
		if (validTrackNames.length == 0) {
			validTrackNames = new String [1];
			validTrackNames[0] = gameLabels.get("properties.noTracks");
		}
		
	    components.add(new JComboBox(validTrackNames));

		// Add the "Enable Event Cards" checkbox to the property dialog
		JogreLabel enableEventCards = new JogreLabel("");
		enableEventCards.setName(ENABLE_EVENT_NAME);
		labels.add(enableEventCards);

		components.add(new JCheckBox(gameLabels.get("properties.enableEventCards"), true));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}
}
