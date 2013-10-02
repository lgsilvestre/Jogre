/*
 * JOGRE (Java Online Gaming Real-time Engine) - Battleship
 * Copyright (C) 2005  StarsInTheSky
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
package org.jogre.battleship.client;

/**
 * Battleship image interface
 *
 * @author Gman, Bob Marks
 * @version Alpha 0.2.3
 */
public interface BattleshipImages {
    
	// battleship icon
	public static final int BATTLESHIPS_ICON = 0;
	
	// ships
	public static final int BATTLESHIP_CARRIER = 1;
	public static final int BATTLESHIP_BATTLESHIP = 2;
	public static final int BATTLESHIP_DESTROYER = 3;
	public static final int BATTLESHIP_CRUISER = 4;
	public static final int BATTLESHIP_PTBOAT = 5;
	
	public static final int BATTLESHIP_HIT = 6;
	public static final int BATTLESHIP_MISS = 7;
}