/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.client;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JComboBox;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePropertyDialog;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.GameLabels;
import org.jogre.common.Table;

import nanoxml.XMLElement;

import org.jogre.texasHoldEm.common.TexasHoldEmCoreModel;

/**
 * Property Dialog for the TexasHoldEm game
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Beta 0.3
 */
public class TexasHoldEmPropertyDialog extends JogrePropertyDialog {

	// Names used for the parts of the property dialog
	private static final String INITIAL_BANKROLL_NAME = "initialBankroll";
	private static final String INITIAL_BLIND_SCHEDULE_NAME = "initialBlindSched";
	private static final String BLIND_ADVANCE_NAME = "BlindAdvance";
	private static final String RAISE_LIMIT_NAME = "RaiseLimit";

	// Values for initial bankroll
	private static final int [] initialBankrollValues = {500, 1000, 1500, 2000};
	private String [] initialBankrollStrings;
	private int defaultInitialBankrollIndex = 0;

	// Values for the initial blind schedule
	private String [] initialBlindScheduleStrings;
	private int defaultInitialBlindScheduleIndex;

	// Values for the blind advance time
	private static final int [] blindTimeValues = {0, 10*60, 20*60, 30*60, 45*60, 60*60};
	private String [] blindAdvanceStrings;
	private int defaultBlindAdvanceIndex;

	// Values for the raise limit
	private String [] raiseLimitStrings;
	private int defaultRaiseLimitIndex = 0;

	/**
	 * Constructor
	 */
	public TexasHoldEmPropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super(owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {
		int initialBankrollIndex      = getIndexOf(labels, components, INITIAL_BANKROLL_NAME);
		int initialBlindScheduleIndex = getIndexOf(labels, components, INITIAL_BLIND_SCHEDULE_NAME);
		int blindAdvanceIndex         = getIndexOf(labels, components, BLIND_ADVANCE_NAME);
		int raiseLimitIndex           = getIndexOf(labels, components, RAISE_LIMIT_NAME);

		newTable.addProperty (INITIAL_BANKROLL_NAME, Integer.toString(initialBankrollValues[initialBankrollIndex]));
		newTable.addProperty (INITIAL_BLIND_SCHEDULE_NAME, Integer.toString(initialBlindScheduleIndex));
		newTable.addProperty (BLIND_ADVANCE_NAME, Integer.toString(blindTimeValues[blindAdvanceIndex]));
		newTable.addProperty (RAISE_LIMIT_NAME, Integer.toString(raiseLimitIndex));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.Game)
	 */
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertiesTree) {
		// Get an instance of the game labels for using to create the gui
		GameLabels gameLabels = GameLabels.getInstance();
		createLocalizedStrings(gameLabels);

		// Add initial bankroll to the property dialog
		addComboBox(labels, components,
		            gameLabels.get("properties.InitialBankroll"),
		            INITIAL_BANKROLL_NAME,
		            initialBankrollStrings,
		            defaultInitialBankrollIndex);

		// Add initial blind schedule to the property dialog
		addComboBox(labels, components,
		            gameLabels.get("properties.InitialBlindSchedule"),
		            INITIAL_BLIND_SCHEDULE_NAME,
		            initialBlindScheduleStrings,
		            defaultInitialBlindScheduleIndex);

		// Add time between blind changes to the property dialog
		addComboBox(labels, components,
		            gameLabels.get("properties.blindAdvanceTime"),
		            BLIND_ADVANCE_NAME,
		            blindAdvanceStrings,
		            defaultBlindAdvanceIndex);

		// Add Limit to the property dialog
		addComboBox(labels, components,
		            gameLabels.get("properties.RaiseLimit"),
		            RAISE_LIMIT_NAME,
		            raiseLimitStrings,
		            defaultRaiseLimitIndex);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}


	/*
	 * Helper function to add a comboBox to the dialog
	 */
	private void addComboBox(
		Vector labels, Vector components,
		String labelString, String nameString,
		String [] choiceStrings,
		int initialIndex)
	{
		JogreLabel theLabel = new JogreLabel(labelString);
		theLabel.setName(nameString);
		labels.add(theLabel);
		JComboBox theBox = new JComboBox(choiceStrings);

		if ((initialIndex < 0) || (initialIndex >= choiceStrings.length)) {
			initialIndex = 0;
		}
		theBox.setSelectedIndex(initialIndex);

		components.add(theBox);
	}

	/*
	 * Helper function to get the index of the combo box that has the given name
	 */
	private int getIndexOf(Vector labels, Vector components, String labelName) {
		int index = this.getJogreLabelIndexByName(labels, labelName);
		if (index > -1) {
			return ((JComboBox) components.get(index)).getSelectedIndex();
		} else {
			return 0;
		}
	}

	/*
	 * This method creates the localized strings for putting into the selection
	 * boxes for things like initial bankroll and initial blind progression.
	 */
	private void createLocalizedStrings(GameLabels gameLabels) {

		// Read new game preferences from the game.properties file
		GameProperties props = GameProperties.getInstance();
		int defaultInitialBankroll = props.getInt("preferences.newgame.defaultInitialBankroll", 0);
		defaultInitialBlindScheduleIndex = props.getInt("preferences.newgame.defaultInitialBlindScheduleIndex", 0);
		defaultRaiseLimitIndex = props.getInt("preferences.newgame.defaultRaiseLimitIndex", 0);
		defaultBlindAdvanceIndex = props.getInt("preferences.newgame.defaultBlindAdvanceIndex", 0);

		// Set the text for the initial bankrolls
		initialBankrollStrings = new String [initialBankrollValues.length];
		TexasHoldEmGraphics thGraphics = TexasHoldEmGraphics.getInstance();
		for (int i=0; i<initialBankrollValues.length; i++) {
			// Set the text to the localization value
			initialBankrollStrings[i] = thGraphics.currencyFormatter.format(initialBankrollValues[i]);

			// If this matches the initial bankroll preference, then set it as the default index.
			if (initialBankrollValues[i] == defaultInitialBankroll) {
				defaultInitialBankrollIndex = i;
			}
		}

		// Set the text for the blind schedule
		initialBlindScheduleStrings = new String [TexasHoldEmCoreModel.ante.length];
		Object [] replaceText = new Object [3];
		for (int i=0; i<TexasHoldEmCoreModel.ante.length; i++) {
			replaceText[0] = thGraphics.currencyFormatter.format(TexasHoldEmCoreModel.smallBlind[i]);
			replaceText[1] = thGraphics.currencyFormatter.format(TexasHoldEmCoreModel.bigBlind[i]);
			replaceText[2] = thGraphics.currencyFormatter.format(TexasHoldEmCoreModel.ante[i]);
			initialBlindScheduleStrings[i] = gameLabels.get("properties.blindScheduleTemplate", replaceText);
		}

		// Set the text for the raise limit selection
		raiseLimitStrings = new String [3];
		raiseLimitStrings[0] = gameLabels.get("properties.RaiseLimit.none");
		raiseLimitStrings[1] = gameLabels.get("properties.RaiseLimit.pot");
		raiseLimitStrings[2] = gameLabels.get("properties.RaiseLimit.blind");

		// Set the text for the blind advance time
		blindAdvanceStrings = new String [6];
		blindAdvanceStrings[0] = gameLabels.get("properties.blindAdvanceTime.0");
		blindAdvanceStrings[1] = gameLabels.get("properties.blindAdvanceTime.1");
		blindAdvanceStrings[2] = gameLabels.get("properties.blindAdvanceTime.2");
		blindAdvanceStrings[3] = gameLabels.get("properties.blindAdvanceTime.3");
		blindAdvanceStrings[4] = gameLabels.get("properties.blindAdvanceTime.4");
		blindAdvanceStrings[5] = gameLabels.get("properties.blindAdvanceTime.5");
	}

	/**
	 * Function to provide the extended info string for a table.
	 * This is located in this file because there is alot of extra string
	 * manipulation to do.
	 *
	 * @param theTable   The table to create extended info for.
	 * @return the extended info string for this table.
	 */
	public static String getExtendedTableInfoString(Table theTable) {
		String limitString;
		String advanceString;

		// We need the currency formatter in the graphics helper
		TexasHoldEmGraphics thGraphics = TexasHoldEmGraphics.getInstance();

		// We need game labels
		GameLabels gameLabels = GameLabels.getInstance();

		// Create the substitution array for the info string
		int initialBankroll     = Integer.parseInt(theTable.getProperty(INITIAL_BANKROLL_NAME));
		int initBlindSchedIndex = Integer.parseInt(theTable.getProperty(INITIAL_BLIND_SCHEDULE_NAME));
		int advanceTime         = Integer.parseInt(theTable.getProperty(BLIND_ADVANCE_NAME)) / 60;
		int limitIndex          = Integer.parseInt(theTable.getProperty(RAISE_LIMIT_NAME));

		// Create the Blind schedule text
		Object [] blindText = new Object [3];
		blindText[0] = thGraphics.currencyFormatter.format(TexasHoldEmCoreModel.smallBlind[initBlindSchedIndex]);
		blindText[1] = thGraphics.currencyFormatter.format(TexasHoldEmCoreModel.bigBlind[initBlindSchedIndex]);
		blindText[2] = thGraphics.currencyFormatter.format(TexasHoldEmCoreModel.ante[initBlindSchedIndex]);

		if (advanceTime == 0) {
			advanceString = gameLabels.get("properties.blindAdvanceTime.0");
		} else {
			Object [] xText = {Integer.toString(advanceTime)};
			advanceString = gameLabels.get("ext.info.blindSchedule.minutes", xText);
		}

		// Create the limit String
		if (limitIndex == 0) {
			limitString = gameLabels.get("properties.RaiseLimit.none");
		} else if (limitIndex == 1) {
			limitString = gameLabels.get("properties.RaiseLimit.pot");
		} else {
			limitString = gameLabels.get("properties.RaiseLimit.blind");
		}

		// Create the substitution array for the info string
		Object [] properties = {
			new Integer (theTable.getNumOfPlayers()),
			thGraphics.currencyFormatter.format(initialBankroll),
			gameLabels.get("properties.blindScheduleTemplate", blindText),
			advanceString,
			limitString
		};

		// Create the extended info string
		return gameLabels.get("ext.info", properties);
	}
}
