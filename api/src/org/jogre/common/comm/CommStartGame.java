/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.common.comm;

import nanoxml.XMLElement;

/**
 * Small communication object for starting a game
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommStartGame extends CommTableMessage {

    private static final String XML_ATT_CUR_PLAYER = "curPlayer";

    private String curPlayer = null;

    /**
     * Constructor for requesting joining an existing table.
     *
     * @param tableNum  Table number of table where game is starting
     */
    public CommStartGame (String currentPlayer, int tableNum) {
        super ();

        this.curPlayer = currentPlayer;

        // Is sent from server so set table number manually
        setTableNum(tableNum);
    }

    /**
     * Constructor which takes an XMLElement.
     *
     * @param message
     */
    public CommStartGame (XMLElement message) {
        super (message);

        this.curPlayer = message.getStringAttribute (XML_ATT_CUR_PLAYER);
    }

    /**
     * Return the current player.
     *
     * @return   Current player.
     */
    public String getCurrentPlayer () {
        return this.curPlayer;
    }

    /**
	 * Flatten the String for transmission purposes.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
	    XMLElement message = super.flatten (Comm.START_GAME);
	    message.setAttribute (XML_ATT_CUR_PLAYER, curPlayer);

		return message;
	}
}