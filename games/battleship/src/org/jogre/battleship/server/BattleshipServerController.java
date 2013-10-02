/*
 * JOGRE (Java Online Gaming Real-time Engine) - Battleship
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
package org.jogre.battleship.server;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.battleship.client.BattleshipModel;
import org.jogre.battleship.common.CommBattleshipMove;
import org.jogre.battleship.common.CommBattleshipPlaceShip;
import org.jogre.common.IGameOver;
import org.jogre.common.Player;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * This is the server table parser for a game of battleship.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class BattleshipServerController extends ServerController {

    /**
     * Constructor for a game of tictactoe.
     *
     * @param gameKey
     */
    public BattleshipServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	setModel (tableNum, new BattleshipModel());
    }

	/**
	 * Parse a checkers message.
	 *
	 * @see org.jogre.server.ITableParser#parseTableMessage(org.jogre.server.ServerConnectionThread, nanoxml.XMLElement)
	 */
	public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
		String messageType = message.getName();

		if (messageType.equals (CommBattleshipPlaceShip.XML_NAME)) {
			CommBattleshipPlaceShip placeShip = new CommBattleshipPlaceShip (message);
			placeShip(conn, tableNum, placeShip);
		} else if (messageType.equals (CommBattleshipMove.XML_NAME)) {
			CommBattleshipMove move = new CommBattleshipMove (message);
			move(conn, tableNum, move);
		}
	}

	/**
	 * Move in battleship.
	 *
	 * @param conn      Connection to server.
	 * @param tableNum  Table number.
	 * @param placeShip Battleship place ship.
	 */
	private void placeShip (ServerConnectionThread conn, int tableNum, CommBattleshipPlaceShip placeShip) {
		// retrieve table num and username
		String username = conn.getUsername();

		// Update server model
		BattleshipModel model = (BattleshipModel) getModel(tableNum);
		int seatNum = getSeatNum (username, tableNum);
		if (seatNum != Player.NOT_SEATED) {
			Point p = new Point(placeShip.getX(), placeShip.getY());
        	int size = placeShip.getSize();
        	int ship = placeShip.getShip();
        	boolean horizontal = placeShip.isHorizontal();
        	model.placeShip (seatNum, p, size, ship, horizontal);

			// Trasmit to other players
			conn.transmitToTablePlayers (username, tableNum, placeShip);
		}
	}

	/**
	 * Move in battleship.
	 *
	 * @param conn      Connection to server.
	 * @param tableNum  Table number.
	 * @param move      Battleship move.
	 */
	private void move (ServerConnectionThread conn, int tableNum, CommBattleshipMove move) {
		// retrieve table num and username
		String username = conn.getUsername();

		// Update server model
		BattleshipModel model = (BattleshipModel) getModel(tableNum);
		int seatNum = getSeatNum(username, tableNum);
		model.setMove(seatNum, move.getX(), move.getY());

		// Trasmit to other players
		conn.transmitToTablePlayers (username, tableNum, move);
	}

	/**
     * Ensure that the game is over.
     *
	 * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
	 */
	public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
	    // Status is either -1, DRAW or WIN
        int result = -1;
        int curSeatNum = getSeatNum (conn.getUsername(), tableNum);
        BattleshipModel model = (BattleshipModel) getModel(tableNum);
        boolean isGameOver = model.isGameWon(curSeatNum);
        if (isGameOver)
            result = IGameOver.WIN;

        // Create game over object if a win or draw
        if (result != -1 && conn != null) {
            // Update the server data
            gameOver (conn, tableNum, conn.getUsername(), resultType);
        }
	}
}