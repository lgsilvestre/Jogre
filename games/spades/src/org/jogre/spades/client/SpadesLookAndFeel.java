/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
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
package org.jogre.spades.client;

import java.awt.Color;
import java.awt.Font;

import org.jogre.common.util.GameProperties;

/**
 * @author Garrett Lehman
 * @version Alpha 0.2.3
 *
 * Local spades look-and-feel class which reads various look and feel
 * properties.
 */
public class SpadesLookAndFeel {
	// Main colours
	public static Color BG_COLOUR = GameProperties.getBackgroundColour();
	public static Color TABLE_BG_COLOUR = new Color(0, 128, 0);
	public static Color BORDER_COLOUR = new Color(0, 0, 0);
	public static Color PLAYER_BG_COLOUR = new Color(200, 200, 200);
	public static Color ACTIVE_PLAYER_BG_COLOUR = new Color(255, 227, 69);

	// Text colours
	public static Color TEXT_COLOUR = new Color(0, 0, 0);
	public static Color TEXT_SHADOW_COLOUR = new Color(150, 150, 150);

	// Text fonts
	public static Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 12);
	public static Font TEXT_BUTTON_FONT = new Font("SansSerif", Font.BOLD, 10);
}