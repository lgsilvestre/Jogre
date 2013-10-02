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

import java.awt.event.MouseEvent;

import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Controller for the fault component of the Grand Prix Jumping game.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingFaultController extends JogreController {

	// links to game data and the board component
	private JumpingClientModel model;
	private JumpingFaultIndicatorComponent faultComponent;
	private JogreTableFrame tableFrame;

	// Strings to use as the headings of the history windows
	private String [] headingString = new String [2];

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param faultComponent		The fault component
	 */
	public JumpingFaultController(	JumpingClientModel model,
									JumpingFaultIndicatorComponent faultComponent,
									JogreTableFrame tableFrame) {
		super(model, faultComponent);

		this.model = model;
		this.faultComponent = faultComponent;
		this.tableFrame = tableFrame;
		headingString[0] = "";
		headingString[1] = "";
	}

	/**
	 * Need to override this as part of JogreController, but it does nothing
	 */
	public void start () {}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		JumpingFaultHistoryDialog d = new JumpingFaultHistoryDialog (
			tableFrame, model.getFaultHistory(), headingString
		);
	}

	/**
	 * Set the player name for use in creating the headings for the fault history
	 * dialog.
	 *
	 * @param seatNum			The seat # to set.
	 * @param headingString		The string to use for the heading
	 */
	public void setPlayerName(int seatNum, String headingString) {
		this.headingString[seatNum] = headingString;
	}
}
