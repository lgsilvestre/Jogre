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
package org.jogre.client;

/**
 * Interface containing some constants which are used with the JogreModel.
 * Currently these include game types.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public interface IJogreModel {

	//==========================================================================
    // Game type constants
    //==========================================================================

	/**
	 * Type based game type i.e. each player takes their turn in a game e.g.
	 * board/card games.
	 */
	public static final int GAME_TYPE_TURN_BASED = 1;

	/**
	 * Real time game type i.e. everyone is playing at the same time e.g.
	 * arcade style game
	 */
	public static final int GAME_TYPE_REAL_TIME = 2;
}
