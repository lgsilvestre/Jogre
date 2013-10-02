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

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreClientFrame;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.GameLabels;
import org.jogre.common.Table;

/**
 * Game client frame for a game of Triangulum.  This is the
 * entry point to the game as an application.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumClientFrame extends JogreClientFrame {

	/**
	 * Constructor which takes command arguments.
	 *
	 * @param args     Command arguments from dos.
	 */
	public TriangulumClientFrame (String [] args) {
		super (args, true);
	}

	/**
	 * Return the correct table frame.
	 *
	 * @see org.jogre.client.awt.JogreClientFrame#getJogreTableFrame(org.jogre.client.TableConnectionThread)
	 */
	public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
		return new TriangulumTableFrame (conn);
	}

	/**
	 * Override the getPropertyDialog so that we can show the dialog
	 * which includes our special items.
	 *
	 * @see org.jogre.client.awt.JogreClientFrame#getPropertyDialog()
	 */
	public void getPropertyDialog(ClientConnectionThread conn) {
		String label = JogreLabels.getInstance().get("game.properties");
		new TriangulumPropertyDialog(this, label, conn);
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
			theTable.getProperty("flavor")
		};

		// Create the extended info string
		return gameLabels.get("ext.info", properties);
	}

	/**
	 * Main method which executes a game of triangulum.
	 *
	 * @param args
	 */
	public static void main (String [] args) {
		TriangulumClientFrame client = new TriangulumClientFrame (args);
	}
}
