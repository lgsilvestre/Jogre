/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.dots.server;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommControllerObject;
import org.jogre.common.util.JogreLogger;
import org.jogre.common.util.JogreUtils;
import org.jogre.dots.common.CommDotsMove;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for dots.  This controller is necessary so that
 * when an observer enters the game in the middle of play, the user
 * will get a data snap shot from the server.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class DotsServerController extends ServerController {

	private JogreLogger logger = new JogreLogger(this.getClass());

	/**
	 * Constructor which takes a game key (read from directory).
	 *
	 * @param gameKey	gamekey (read from directory).
	 */
	public DotsServerController(String gameKey) {
		super(gameKey);
	}

	/**
	 * Start the game of dots
	 *
	 * @see org.jogre.server.ServerController#startGame(int)
	 */
	public void startGame (int tableNum) {
		setModel (tableNum, new DotsServerTableModel (getTable(tableNum)));
	}

	/**
	 * Called when a game over request is sent from the client.
	 *
	 * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
	 */
	public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
		// Retrieve seat number and model
		int currentSeat = getSeatNum (conn.getUsername(), tableNum);
		DotsServerTableModel model = (DotsServerTableModel)getModel (tableNum);

		int otherSeat = JogreUtils.invert(currentSeat);
		int score = model.cellsOwned (currentSeat);
		int otherScore = model.cellsOwned (otherSeat);

		int result = IGameOver.DRAW;
		int points = 0;
		if (score > otherScore)
			result = IGameOver.WIN;
		else if (score < otherScore)
			result = IGameOver.LOSE;

		if (result != -1) {
		    // Update the server data
            gameOver (conn, tableNum, conn.getUsername(), result);
		}
	}

	/* (non-Javadoc)
	 * @see org.jogre.server.ServerController#parseTableMessage(org.jogre.server.ServerConnectionThread, nanoxml.XMLElement, int)
	 */
	public void parseTableMessage(ServerConnectionThread conn, XMLElement message, int tableNum) {

		String messageType = message.getName();

		try
		{
			if (messageType.equals(Comm.CONTROLLER_OBJECT)) {
				XMLElement object = new CommControllerObject(message).getData();
				messageType = object.getName();

				if (messageType.equals(CommDotsMove.XML_NAME)) {

					// get move data and store it
					CommDotsMove move = new CommDotsMove(object);

					// get move data
					int col = move.getColumn();
					int row = move.getRow();
					int location = move.getLocation();
					int seatNum = this.getSeatNum (move.getUsername(), tableNum);

					// Get server table model by table number
					DotsServerTableModel model = (DotsServerTableModel)getModel(tableNum);

					// Set data in table model
					model.setLocation(col, row, location, seatNum);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}