/*
 * JOGRE (Java Online Gaming Real-time Engine) - Dots
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.dots.client;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JComboBox;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePropertyDialog;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.GameLabels;

/**
 * Dots Property Dialog
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class DotsPropertyDialog extends JogrePropertyDialog {

	/**
	 * Constructor
	 */
	public DotsPropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super(owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.util.GameProperties, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {
		GameProperties gameProperties = GameProperties.getInstance();
		int size = 10;
		int index = this.getJogreLabelIndexByName(labels, "board_size");
		if (index > -1) {
			JComboBox boardSize = (JComboBox) components.get(index);
			if (boardSize.getSelectedIndex() == 0)
				size = gameProperties.getInt("board.size.small");
			else if (boardSize.getSelectedIndex() == 1)
				size = gameProperties.getInt("board.size.medium");
			else
				size = gameProperties.getInt("board.size.large");
		}
		newTable.addProperty ("size", String.valueOf(size));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.util.GameProperties)
	 */
	public void addCustomProperties(Vector labels, Vector components) {
		// Add Board Size
		GameProperties gameProperties = GameProperties.getInstance();
		GameLabels gameLabels = GameLabels.getInstance();
		JogreLabel boardSize = new JogreLabel(gameLabels.get("properties.boardSize"));
		boardSize.setName("board_size");
		labels.add(boardSize);
	    String[] boardSizes = new String[3];
	    boardSizes[0] = "Small - " + gameProperties.getInt("board.size.small") + " x " + gameProperties.getInt("board.size.small");
	    boardSizes[1] = "Medium - " + gameProperties.getInt("board.size.medium") + " x " + gameProperties.getInt("board.size.medium");
	    boardSizes[2] = "Large - " + gameProperties.getInt("board.size.large") + " x " + gameProperties.getInt("board.size.large");
	    components.add(new JComboBox(boardSizes));
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}
}