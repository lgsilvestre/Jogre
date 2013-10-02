/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.grandPrixJumping.client;

/**
 * Interface for knowing what the various images are.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public interface JumpingImages {

	public static final int JUMPING_ICON = 0;
	public static final int TRACK = 1;
	public static final int FENCES = 2;
	public static final int CARD_BACK = 3;
	public static final int CARD_OUTLINE = 4;
	public static final int CARDS = 5;
	public static final int CARDS_IMMEDIATE = 6;
	public static final int CARDS_SPECIAL = 7;
	public static final int HORSE0 = 8;
	public static final int HORSE1 = 9;
	public static final int TRACK_ICONS = 10;
	public static final int HORSE_HIGHLIGHT = 11;
	public static final int WATER_JUMP = 12;
	public static final int CARDS_ACTIVATED_SPECIALS = 13;
	public static final int CAUTION_SIGNS = 14;
	public static final int TRACK_ICONS_HIGHLIGHT = 15;
	public static final int PLUS = 16;
	public static final int DARK_CORNERS = 17;
	public static final int TURN_HIGHLIGHT = 18;
	public static final int TRACK_HIGHLIGHT = 19;
	public static final int PADLOCK = 20;
	public static final int HAND_SIZE_CORNERS = 21;

	public static final int NUM_IMAGES = 22;

	// Index of images within the activated card image
	public static final int ACTIVATED_CARDS_OFFICIAL_INDEX = 0;
	public static final int ACTIVATED_CARDS_RIBBON_INDEX = 1;
	public static final int ACTIVATED_CARDS_HIGHLIGHT_UPPER_LEFT_INDEX = 2;
	public static final int ACTIVATED_CARDS_HIGHLIGHT_LOWER_RIGHT_INDEX = 3;
	public static final int ACTIVATED_CARDS_HIGHLIGHT_CARD_INDEX = 4;
}
