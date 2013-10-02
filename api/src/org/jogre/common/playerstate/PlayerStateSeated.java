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
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreLabels;

/**
 * State object for a seated player state.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PlayerStateSeated extends PlayerState {

	/**
	 * If a player stands his state goes back to viewing.
	 *
	 * @see org.jogre.common.playerstate.PlayerState#stand()
	 */
	public PlayerState stand () {
		return new PlayerStateViewing ();
	}

	/**
	 * Player state goes to ready to start.
	 *
	 * @see org.jogre.common.playerstate.PlayerState#start()
	 */
	public PlayerState start () {
		return new PlayerStateReady ();
	}

	/**
	 * Once seated a player can stand back up again.
	 *
	 * @see org.jogre.common.playerstate.PlayerState#canStand(org.jogre.common.PlayerList)
	 */
	public boolean canStand (Table table) { return true; }

	/**
	 * A player can start if the minimum number of players are seated or are
	 * ready to start.
	 *
	 * @see org.jogre.common.playerstate.PlayerState#canStart(org.jogre.common.PlayerList)
	 */
	public boolean canStart (Table table, Game game) {
		int minPlayers = game.getMinNumOfPlayers();

		PlayerList players = table.getPlayerList();
		int curPlayersSeatedAndReady =
			players.getPlayerStateCount (SEATED) +
			players.getPlayerStateCount (READY_TO_START);
		return (curPlayersSeatedAndReady >= minPlayers);
	}

	// Defaults
	public PlayerState sit () { return this; }
	public boolean canSit (Table table) { return false; }
	public boolean canOfferDrawResign () { return false; }

	/**
	 * @see org.jogre.common.playerstate.PlayerState#intValue()
	 */
	public String stringValue() {
		return SEATED;
	}

	public String toString () {
	    return JogreLabels.getInstance().get("seated");
	}
}
