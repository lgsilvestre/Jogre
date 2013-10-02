/*
 * JOGRE (Java Online Gaming Real-time Engine) - TicTacToe
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
package org.jogre.tictactoe.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

// Immutable communication object for a tic tac toe move.
public class CommTicTacToeMove extends CommTableMessage {

  public  static final String XML_NAME      = "tic_tac_toe";
  private static final String XML_ATT_X     = "x";
  private static final String XML_ATT_Y     = "y";
  private static final String XML_ATT_VALUE = "value";

  private int x, y, value;   // position of move

  // Constructor which takes a move from a player.
  public CommTicTacToeMove (int x, int y, int value) {
    super ();
    // set x and y
    this.x = x;
    this.y = y;
    this.value = value;
  }

  // Constructor which takes a String from a flatten () method.
  public CommTicTacToeMove (XMLElement message) {
    super (message);

    this.x     = message.getIntAttribute (XML_ATT_X);
    this.y     = message.getIntAttribute (XML_ATT_Y);
    this.value = message.getIntAttribute (XML_ATT_VALUE);
  }

  // Flatten the fields of this object into a String
  public XMLElement flatten () {
    XMLElement message = super.flatten (XML_NAME);
    message.setIntAttribute (XML_ATT_X, x);
    message.setIntAttribute (XML_ATT_Y, y);
    message.setIntAttribute (XML_ATT_VALUE, value);
    
    return message;
  }

  // get x
  public int getX () { return x; }

  // get y
  public int getY () { return y; }

  // get value
  public int getValue () { return value; }
}
