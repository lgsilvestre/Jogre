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
package org.jogre.common.playerstate;

import org.jogre.common.Game;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.util.JogreLabels;

/**
 * State object for a viewing player state.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PlayerStateViewing extends PlayerState {

	/**
     * If a player sits the state changes to seated.
     *
	 * @see org.jogre.common.playerstate.PlayerState#sit()
	 */
	public PlayerState sit () {
		return new PlayerStateSeated();
	}

	/**
	 * A player can sit if the number of players already seated isn't greater
	 * than the maximum number of players allowed in this game.
	 *
	 * @see org.jogre.common.playerstate.PlayerState#canSit(org.jogre.common.PlayerList)
	 */
	public boolean canSit (Table table) {
		int numOfPlayers = table.getNumOfPlayers();
		// Count up the num of people seated (i.e. not simply viewing).
		PlayerList players = table.getPlayerList();
		int curPlayersSeated =
			players.getPlayerStateCount(SEATED) +
			players.getPlayerStateCount(GAME_STARTED) +
			players.getPlayerStateCount(READY_TO_START);

		return (curPlayersSeated < numOfPlayers);
	}

	// Defaults (do nothing)
	public PlayerState stand () { return this; }
	public PlayerState start () { return this; }
	public boolean canStand (Table table) { return false; }
	public boolean canStart (Table table, Game game) { return false; }
	public boolean canOfferDrawResign () { return false; }

	/**
	 * @see org.jogre.common.playerstate.PlayerState#intValue()
	 */
	public String stringValue() {
		return VIEWING;
	}

	public String toString () {
	    return JogreLabels.getInstance().get("viewing");
	}
}
