/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.client;

import info.clearthought.layout.TableLayout;
import nanoxml.XMLElement;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.Integer;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

import org.jogre.abstrac.common.CommAbstracSendHand;
import org.jogre.abstrac.common.CommAbstracMove;

import org.jogre.abstrac.std.Card;
import org.jogre.abstrac.std.Hand;

// Controller for Abstrac game
public class AbstracController extends JogreController {

	// links to client model and the board components
	private AbstracModel model;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model      The game model
	 */
	public AbstracController(AbstracModel model) {
		super(model, null);

		// Save parameters
		this.model = model;
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.resetGame();
	}

	/**
	 * Handle a table message from the server.
	 *
	 * @param  message     The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(CommAbstracSendHand.XML_NAME)) {
			handleSendHand(new CommAbstracSendHand(message));
		} else if (messageType.equals(CommAbstracMove.XML_NAME)) {
			handleMove(new CommAbstracMove(message));
		}
	}

	/**
	 * Handle a message that has sent our hand of cards to us.
	 */
	private void handleSendHand(CommAbstracSendHand theHandMsg) {
		model.giveCards(theHandMsg.getCards());
	}

	/**
	 * Handle a message with a move in it
	 */
	private void handleMove(CommAbstracMove theMoveMsg) {
		int playerId = getSeatNum(theMoveMsg.getUsername());
		model.downgradeJustTaken(playerId);
		model.takeCards(playerId, theMoveMsg.getNumCards());
	}

}
