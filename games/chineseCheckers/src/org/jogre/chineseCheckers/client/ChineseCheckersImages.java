/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.chineseCheckers.client;

/**
 * Interface for knowing what the various images are.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public interface ChineseCheckersImages {

	public static final int GAME_ICON = 0;
	public static final int PLAYER_MARBLES = 1;
	public static final int SPACES = 2;
	public static final int SELECTED_SPACE = 3;
	public static final int SELECTED_MARBLE = 4;
	public static final int VALID_MOVE = 5;

	public static final int NUM_IMAGES = 6;
}
