/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.octagons.client;

/**
 * Test model for the octagons game.
 *
 * This test model provides access to protected variables that aren't used by
 * the regular game to allow for more complete testing.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class OctagonsTestModel extends OctagonsModel {

	/**
	 * Provide access to protected variables
	 */

	public int get_island_id (int i, int j, int element) {
		return (board_island_id[i][j][element]);
	}

	public boolean alreadyPlayedSquare () {
		return already_played_square;
	}

	public boolean pieRuleInForce () {
		return pie_rule_in_force;
	}

	public int getCurrentMoveNum () {
		return current_move_num;
	}

	public int getCurrentTurnNum () {
		return current_turn_num;
	}

	public int getNumUnclamiedSquares () {
		return squares_unclaimed;
	}

}
