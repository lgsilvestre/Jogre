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
import org.jogre.common.Table;
import org.jogre.common.util.JogreLabels;

/**
 * State object for a game started player state.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PlayerStateGameStarted extends PlayerState {

	public PlayerState sit () { return this; }
	public PlayerState stand () { return new PlayerStateViewing(); }
	public PlayerState start () { return this; }

	public boolean canSit (Table table) { return false; }
	public boolean canStand (Table table) { return false; }
	public boolean canStart (Table table, Game game) { return false; }
	public boolean canOfferDrawResign () { return true; }

	/**
	 * @see org.jogre.common.playerstate.PlayerState#intValue()
	 */
	public String stringValue() {
		return GAME_STARTED;
	}

	public String toString () {
		return JogreLabels.getInstance().get("game.started");
	}
}