/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.chess.common;

import nanoxml.XMLElement;

import org.jogre.chess.client.ChessMove;
import org.jogre.common.comm.CommTableMessage;

/**
 * Communication class for executing a chess move.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommChessMove extends CommTableMessage {

	// Constants
	public static final String XML_NAME = "chess_move";

	// Fields
	private ChessMove move;

	/**
	 * Constructor which takes a username, table number and move (no next player).
	 * This is used by the client controller.
	 *
	 * @param username
	 * @param tableNum
	 */
	public CommChessMove (ChessMove move) {
		super ();

		this.move = move;
	}

	/**
	 * Constructor for a chess execute move which takes a String.
	 */
	public CommChessMove (XMLElement message) {
		super (message);

		move = new ChessMove (message);
	}

	/**
	 * Return the chess move.
	 *
	 * @return
	 */
	public ChessMove getMove() {
		return move;
	}

	/**
	 * Flatten the String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = move.flatten ();

		return message;
	}
}
