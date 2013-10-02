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
package org.jogre.chess.client;

import java.util.Enumeration;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.ITransmittable;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * This class holds the history of a chess game
 */
public class GameHistory implements ITransmittable {

	private static final String XML_HISTORY = "history";
	
	private Vector moves = null;

	/** Constructor for the game history
	 */
	public GameHistory () {
		moves = new Vector ();
	}
	
	/**
	 * Constructor which takes a message.
	 * 
	 * @param message
	 */
	public GameHistory (XMLElement message) {
		moves = new Vector ();
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) 
			moves.add (new ChessMove ((XMLElement)e.nextElement()));
	}

	/** Add a move to the history
	 * @param move
	 */
	public void addMove (ChessMove move) {
		moves.add (move);
	}

	/** 
	 * Retrieve a move from the history.
	 * 
	 * @param index
	 * @return
	 */
	public ChessMove getMove (int index) {
		return (ChessMove)moves.get (index);
	}
	
	/**
	 * Return number of moves in history.
	 * 
	 * @return
	 */
	public int size () {
		return moves.size();
	}

	/* (non-Javadoc)
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = new XMLElement (XML_HISTORY);
		for (int i = 0; i < moves.size(); i++) {
			ChessMove move = (ChessMove)moves.get(i);
			message.addChild(move.flatten());
		}
		
		return message;
	}
}
