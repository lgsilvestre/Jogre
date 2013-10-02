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
package org.jogre.abstrac.server;

import java.util.Collections;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.abstrac.client.AbstracModel;
import org.jogre.abstrac.common.CommAbstracMove;
import org.jogre.abstrac.common.CommAbstracSendHand;
import org.jogre.common.IGameOver;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for the game of Abstrac
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class AbstracServerController extends ServerController {

    /**
     * Constructor to create a Abstrac server controller.
     * 
     * @param gameKey  Game key.
     */
    public AbstracServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new model when the game starts.
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
	public void startGame (int tableNum) {
		AbstracModel model = new AbstracModel();
		model.giveCards(shuffleCards());
		setModel (tableNum, model);
	}

	/**
	 * Create an array of random ints for the deck.
	 */
	private int [] shuffleCards() {
		// Create the deck
		int numCardsInDeck = AbstracModel.NUM_VALUES * AbstracModel.NUM_SUITS;
		Vector cardList = new Vector ();
		for (int i=0; i<numCardsInDeck; i++) {
			cardList.add(new Integer(i));
		}

		Collections.shuffle(cardList);

		// Create an array from the list
		int [] cards = new int [numCardsInDeck];
		for (int i=0; i<numCardsInDeck; i++) {
			cards[i] = ((Integer) cardList.get(i)).intValue();
		}

		return cards;
	}

	/**
	 * Override this method to send the initial cards.
	 */
	public void sendInitialClientMessages (ServerConnectionThread conn, int tableNum) {
		AbstracModel model = (AbstracModel) getModel(tableNum);
		conn.transmitToTablePlayers(tableNum, new CommAbstracSendHand(model.getOrigHand()));
	}

	/**
	 * Handle receving messages from the clients.
	 *
	 * @param conn      Connection to a client.
	 * @param message   Message.
	 * @param tableNum  Table number of message.
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
		String messageType = message.getName();

		if (messageType.equals(CommAbstracMove.XML_NAME)) {
			handleMove(conn, tableNum, new CommAbstracMove(message));
		}
	}

	/**
	 * Handle the Move message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theMoveMsg	The message making the move
	 */
	private void handleMove(ServerConnectionThread conn, int tableNum, CommAbstracMove theMoveMsg) {
		String userName = theMoveMsg.getUsername();
		AbstracModel model = (AbstracModel) getModel(tableNum);

		model.takeCards(getSeatNum(userName, tableNum), theMoveMsg.getNumCards());
		conn.transmitToTablePlayers (userName, tableNum, theMoveMsg);
	}

	/**
	 * Handle the HandReq message from a client.
	 *
	 * @param conn			Connection to a client
	 * @param tableNum		The table the client is at.
	 * @param theReqMsg		The message making the request
	 *//*
	private void handleHandReq(ServerConnectionThread conn, int tableNum, CommAbstracHandReq theReqMsg) {
		String userName = theReqMsg.getUsername();
		AbstracModel model = (AbstracModel) getModel(tableNum);

		conn.transmitToTablePlayer(userName, tableNum, new CommAbstracSendHand(model.getOrigHand()));
	}*/

    /**
     * Verify that the game is over.
	 * (A client has just told us that the game is over, but we ought to verify it)
     *
     * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
		AbstracModel model = (AbstracModel) getModel(tableNum);
		int winner = model.getWinner();
		int clientId = getSeatNum(conn.getUsername(), tableNum);

		if (winner == clientId) {
			gameOver (conn, tableNum, conn.getUsername(), IGameOver.WIN);
		} else if (winner == (1-clientId)) {
			gameOver (conn, tableNum, conn.getUsername(), IGameOver.LOSE);
		} else if (winner == AbstracModel.DRAW_GAME) {
			gameOver (conn, tableNum, conn.getUsername(), IGameOver.DRAW);
		}
	}
	 
}
