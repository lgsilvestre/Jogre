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

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreClientFrame;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.GameLabels;
import org.jogre.common.Table;

// CarTricks client frame.
public class CarTricksClientFrame extends JogreClientFrame {

	// Constructor
	public CarTricksClientFrame (String [] args) {
		super (args, true);
	}

	// Return the correct table frame
	public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
		return new CarTricksTableFrame (conn);
	}

	/*
	 * Override the getPropertyDialog so that we can show the CarTricks dialog
	 * which includes the list of tracks from the server to select from.
	 *
	 * @see org.jogre.client.awt.JogreClientFrame#getPropertyDialog()
	 */
	public void getPropertyDialog(ClientConnectionThread conn) {
		String label = JogreLabels.getInstance().get("game.properties");
		new CarTricksPropertyDialog(this, label, true, conn);
	}

	/**
	 * Override the getExtendedTableInfoString method to provide more detailed
	 * information in the table list.
	 *
	 * @see org.jogre.client.awt.JogreClientFrame#getExtendedTableInfoString()
	 */
	public String getExtendedTableInfoString(Table theTable) {
		// We need game labels
		GameLabels gameLabels = GameLabels.getInstance();

		// Create the substitution array for the info string
		Object [] properties = {
			new Integer (theTable.getNumOfPlayers()),
			theTable.getProperty("trackName", "-"),
			gameLabels.get("t".equals(theTable.getProperty("enableEventCards")) ?
			                           "ext.info.yes" :
			                           "ext.info.no")
		};

		// Create the extended info string
		return gameLabels.get("ext.info", properties);
	}

	// Main method where this class gets run
	public static void main (String [] args) {
		CarTricksClientFrame frame = new CarTricksClientFrame (args);
	}
}
