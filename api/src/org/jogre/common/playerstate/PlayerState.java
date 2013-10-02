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

/**
 * Class which describes the state of the various player when they join a table.
 * This class employs the STATE design pattern from the GOF (gang of four).
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class PlayerState {

	// Integer constrants

	/** Player is simply viewing a table. */
	public static final String VIEWING = "viewing";

	/** Player is seated at a table. */
	public static final String SEATED = "seated";

	/** Player is ready to start a table. */
	public static final String READY_TO_START = "ready";

	/** Player is in the middle of a game at a table. */
	public static final String GAME_STARTED = "started";

	/**
	 * Abstract method which defines what a player can do when they sit.
	 *
	 * @return PlayerState object.
	 */
	public abstract PlayerState sit();

	/**
	 * Abstract method which defines what a player can do when they stand up.
	 *
	 * @return PlayerState object.
	 */
	public abstract PlayerState stand();

	/**
	 * Abstract method which defines what a player can do when they start.
	 *
	 * @return PlayerState object.
	 */
	public abstract PlayerState start();

	//	 The following 3 methods are not abstract and return false by default

	/**
	 * Returns true if a user can sit.
	 *
	 * @param table    Link to the table
	 * @return         True if player can sit down.
	 */
	public abstract boolean canSit (Table table);

	/**
	 * Returns true if a user can stand.
	 *
	 * @param table    Link to the table
	 * @return         True if player can stand up.
	 */
	public abstract boolean canStand (Table table);

	/**
	 * Returns true if a user can hit the start.
	 *
	 * @param table    Link to the table
	 * @param game     Link to the game.
	 * @return         True if player can start.
	 */
	public abstract boolean canStart (Table table, Game game);

	/**
	 * Returns true if a user can offer a draw or resign.
	 * @return         True if player offer a draw or resign.
	 */
	public abstract boolean canOfferDrawResign ();

	/**
	 * Integer label of this state (see constants of this class).
	 *
	 * @return          String value of this state.
	 */
	public abstract String stringValue ();
}
