/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.common;

/**
 * Interface which holds constants for a game over.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public interface IGameOver {
	
	/** Player wins a game. */
	public static final String [] RESULTS = {"W", "L", "D", "UR", "AD"};
	
	/** Player wins a game. */
	public static final int WIN = 1;

	/** Player losses a game. */
	public static final int LOSE = 2;

	/** Player draws with another player. */
	public static final int DRAW = 3;	
	
	/** A user resigns. */
	public static final int USER_RESIGNS = 4;
	
	/** All users agree on a draw. */
	public static final int AGREED_DRAW = 5;	
}