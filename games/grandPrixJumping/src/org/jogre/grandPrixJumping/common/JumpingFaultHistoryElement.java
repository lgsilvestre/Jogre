/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2007  Richard Walter
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
package org.jogre.grandPrixJumping.common;

import org.jogre.common.util.GameLabels;

/**
 * Structure to hold info about fault points during a game of Grand Prix Jumping.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public final class JumpingFaultHistoryElement {

	// Information about the fault
	private int turnNumber;
	private int quarterFaultsScored;
	private int reasonCode;

	// Values for reasonCode
	public final static int MISSED_JUMP = 0;
	public final static int FAULT_CARD = 1;
	public final static int OFFICIAL_CARD = 2;
	public final static int RIBBON_CARD = 3;
	public final static int BEHIND_AT_END_OF_GAME = 4;

	/**
	 * Constructor for a fault history element.
	 *
	 * @param	turnNumber				The turn number that the faults were scored during.
	 * @param	quarterFaultsScored		The number of quarter faults scored.
	 * @param	reasonCode				The reason for the faults points scored.
	 */
	public JumpingFaultHistoryElement(int turnNumber, int quarterFaultsScored, int reasonCode) {
		this.turnNumber = turnNumber;
		this.quarterFaultsScored = quarterFaultsScored;
		this.reasonCode = reasonCode;
	}

	/**
	 * Retrieve info about the history element
	 */
	public int turnNumber()          { return turnNumber; }
	public int quarterFaultsScored() { return quarterFaultsScored; }
	public int reasonCode()          { return reasonCode; }

	/**
	 * Convert this element to a string
	 */
	public String toString(GameLabels labels) {
		Object [] replacementStrings = new Object [4];

		replacementStrings[0] = "   ";
		replacementStrings[1] = Integer.toString(turnNumber);
		replacementStrings[3] = labels.get("history.reason." + reasonCode);

		if (quarterFaultsScored >= 4) {
			// Full # of fault points
			replacementStrings[2] = Integer.toString(quarterFaultsScored / 4);
		} else if (quarterFaultsScored == -1) {
			replacementStrings[2] = labels.get("history.faultString.0");
		} else {
			replacementStrings[2] = labels.get("history.faultString." + quarterFaultsScored);
		}

		return labels.get("history.line", replacementStrings);
	}



}
