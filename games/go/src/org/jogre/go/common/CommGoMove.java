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
package org.jogre.go.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communication class for executing a go move.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommGoMove extends CommTableMessage {

	public static final int TYPE_MOVE = 1;
	public static final int TYPE_PASS = 2;
	public static final int TYPE_MARK = 3;
	public static final int TYPE_HAPPY = 4;
	public static final int TYPE_UNHAPPY = 5;
	
	// Constants
	public static final String XML_NAME = "go_move";
	public static final String XML_ATT_X = "x";
	public static final String XML_ATT_Y = "y";

	// xy corindates
	private int x = -1, y = -1;
	
	/**
	 * Constructor which takes a status (type)
	 * 
	 * @param type      Type e.g. TYPE_PASS
	 * @param username  Username of player.
	 */
	public CommGoMove (int type, String username) {
		super (type, username);	
	}
	
	/**
	 * Constructor which takes an x and y.
	 * 
	 * @param type      Type e.g. TYPE_PASS
	 * @param username  Username of player.
	 * @param x         X co-ordinate
	 * @param y         y co-ordinate
	 */
	public CommGoMove (int type, String username, int x, int y) {
		super (type, username);
		
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor for a chess execute move which takes a String.
	 * 
	 * @param message   Go move as XML message.
	 */
	public CommGoMove (XMLElement message) {
		super (message);

		this.x = message.getIntAttribute("x");
		this.y = message.getIntAttribute("y");
	}

	/**
	 * Return x co-ordinate.
	 * 
	 * @return
	 */
	public int getX () {
		return this.x;
	}
	
	/**
	 * Return y co-ordinate.
	 * 
	 * @return
	 */
	public int getY () {
		return this.y;
	}

	/**
	 * Flatten the String.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = flatten (XML_NAME);
		message.setIntAttribute ("x", this.x);
		message.setIntAttribute ("y", this.y);

		return message;
	}
}
