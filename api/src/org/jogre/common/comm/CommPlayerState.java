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

import org.jogre.common.playerstate.PlayerState;
import org.jogre.common.playerstate.PlayerStateFactory;
import org.jogre.common.playerstate.PlayerStateSeated;

import nanoxml.XMLElement;

/**
 * Communication object for informing the server that the state of a
 * player has been updated.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommPlayerState extends CommTableMessage {

    public static final int NO_SEAT = -1;

    /** Seat number of where the user wishes to sit down at. */
    private int seatNum = -1;		// defaults to -1 (no seat)
    private PlayerState state;

    public static final String XML_ATT_STATE = "state";
    public static final String XML_ATT_SEAT  = "seat";

    /**
     * Constructor which takes a table number, a player's name and
     * a state.
     *
     * @param table   Table number of player.
     * @param player  Name of player.
     * @param state   State of the player.
     */
    public CommPlayerState (int table, String player, PlayerState state) {
        // Set up fields
        super (player);
        setTableNum (table);

        // Set player state
        this.state = state;
    }

    /**
     * Constructor which takes a table number, a player's name and
     * a seat number (player state is seated).
     *
     * @param table    Table number of player.
     * @param player   Name of player.
     * @param seatNum  Seat which player has sat at.
     */
    public CommPlayerState (int table, String player, int seatNum) {
        // Set up fields
        super (player);
        setTableNum (table);

        // Set player state
        this.state = new PlayerStateSeated ();
        this.seatNum = seatNum;
    }

    /**
     * Constructor which takes an XMLElement.
     *
     * @param message  XML version of this method.
     */
    public CommPlayerState (XMLElement message) {
        super(message);

        // Set player
        this.state = PlayerStateFactory.getState(
            message.getStringAttribute (XML_ATT_STATE));
        this.seatNum = message.getIntAttribute(XML_ATT_SEAT, NO_SEAT);
    }

    /**
     * Return this state.
     *
     * @return    State of the player.
     */
    public PlayerState getState () {
        return this.state;
    }

    /**
     * Return the seat number.
     *
     * @return    Seat number of the player.
     */
    public int getSeatNum () {
        return this.seatNum;
    }

    /**
     * Flatten this to an XMLElement.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        XMLElement message = super.flatten (Comm.PLAYER_STATE);
        message.setAttribute (XML_ATT_STATE, state.stringValue());
        if (seatNum != -1)
            message.setIntAttribute (XML_ATT_SEAT, seatNum);

        return message;
    }
}