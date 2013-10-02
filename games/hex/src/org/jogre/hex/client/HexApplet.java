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

import javax.swing.JOptionPane;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogreClientApplet;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.GameLabels;
import org.jogre.common.Table;

/**
 * Hex applet.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class HexApplet extends JogreClientApplet {

	/**
	 * Constructor for the applet
	 */
	public HexApplet () {
		super (true);
	}

	/**
	 * Return the correct table frame
	 */
	public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
		return new HexTableFrame (conn);
	}

	/**
	 * Override the getPropertyDialog so that we can show the dialog
	 * which includes our special items.
	 *
	 * @see org.jogre.client.awt.JogreClientFrame#getPropertyDialog()
	 */
	public void getPropertyDialog(ClientConnectionThread conn) {
		Frame frame = JOptionPane.getFrameForComponent(this);
		String label = JogreLabels.getInstance().get("game.properties");
		new HexPropertyDialog(frame, label, conn);
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
			theTable.getProperty("boardSize")
		};

		// Create the extended info string
		return gameLabels.get("ext.info", properties);
	}
}
