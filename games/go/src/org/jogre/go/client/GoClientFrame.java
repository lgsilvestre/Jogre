/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.go.client;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreClientFrame;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.common.Table;
import org.jogre.common.util.GameLabels;

/**
 * Go client frame.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoClientFrame extends JogreClientFrame {
    
    /**
     * Constructor.
     * 
     * @param args
     */
    public GoClientFrame (String [] args) {
        super (args);
    }
    
    /**
     * Return the correct table frame.
     * 
     * @see org.jogre.client.awt.JogreClientFrame#getJogreTableFrame(org.jogre.client.TableConnectionThread)
     */
    public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
        return new GoTableFrame (conn);
    }

    /**
	 * Override the getPropertyDialog so that we can show the dialog
	 * which includes our special items.
	 *
	 * @see org.jogre.client.awt.JogreClientFrame#getPropertyDialog()
	 */
	public void getPropertyDialog(ClientConnectionThread conn) {
		new GoPropertyDialog (this, "Go Game Properties", true, conn);
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
			theTable.getProperty("size")
		};

		// Create the extended info string
		return gameLabels.get("ext.info", properties);
	}
    
    /**
     * Main method where game gets run.
     */
    public static void main (String [] args) {
        GoClientFrame frame = new GoClientFrame (args);
    }
}
