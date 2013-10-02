/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
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
package org.jogre.spades.client;

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreClientFrame;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Spades Client Frame.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class SpadesClientFrame extends JogreClientFrame {

	/**
	 * Default constructor
	 */
	public SpadesClientFrame (String [] args) {
		super(args);
	}

	/**
	 * Return spades table frame.
	 *
	 * @see org.jogre.awt.JogreClientFrame#getJogreTableFrame(org.jogre.client.ClientConnectionThread,
	 *      int)
	 */
	public JogreTableFrame getJogreTableFrame(TableConnectionThread conn) {
		// Return spades table frame
		return new SpadesTableFrame(conn);
	}

	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		new SpadesClientFrame(args);
	}
}
