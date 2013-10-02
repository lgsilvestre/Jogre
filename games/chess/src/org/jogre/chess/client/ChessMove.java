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

import nanoxml.XMLElement;

import org.jogre.common.comm.ITransmittable;

/**
 * This immutable class holds a single move as 2 sets of co-ordinates and
 * additional information such as putting the king in check etc or has attacked
 * another piece.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class ChessMove implements ITransmittable {
	
	private static final String XML_MOVE = "chess_move";
	
	private int x1, y1, x2, y2;

	/**
	 * Constructor which takes an XML element.
	 * 
	 * @param message
	 */
	public ChessMove (XMLElement message) {
		this.x1 = message.getIntAttribute("x1");
		this.y1 = message.getIntAttribute("y1");
		this.x2 = message.getIntAttribute("x2");
		this.y2 = message.getIntAttribute("y2");
	}

	/** Constructor which creates a new Move.
	 *
	 * @param x1 Starting x co-ordinate
	 * @param y1 Starting x co-ordinate
	 * @param x2 Starting x co-ordinate
	 * @param y2
	 */
	public ChessMove (int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * @return Returns the starting x co-ordinate.
	 */
	public int getX1() {return x1; }

	/**
	 * @return Returns the starting y co-ordinate.
	 */
	public int getY1() {return y1; }

	/**
	 * @return Returns the ending x co-ordinate.
	 */
	public int getX2() {return x2; }

	/**
	 * @return Returns the ending y co-ordinate.
	 */
	public int getY2() {return y2; }

	/* (non-Javadoc)
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = new XMLElement (XML_MOVE);
		
		message.setIntAttribute ("x1", getX1());
		message.setIntAttribute ("y1", getY1());
		message.setIntAttribute ("x2", getX2());
		message.setIntAttribute ("y2", getY2());
		
		return message;
	}
	
	/**
	 * Check if equal to another move.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals (Object obj) {
		if (obj instanceof ChessMove) {
			ChessMove move = (ChessMove)obj;
			return (x1 == move.getX1() &&
			        x2 == move.getX2() &&
			        y1 == move.getY1() && 
			        y2 == move.getY2());
		}
		return super.equals(obj);
	}
}
