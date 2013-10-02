/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import java.awt.Color;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Local propinquity look-and-feel class which reads various look and feel
 * properties from the bulid file.
 */
public class PropinquityLookAndFeel {
	// Main colours
	public static Color BG_COLOUR_1 = new Color (110, 165, 165);
	public static Color BG_COLOUR_2 = new Color (240, 240, 240);
	public static Color BORDER_COLOUR = new Color (0, 0, 0);
	public static Color MOUSE_COLOUR = new Color (255, 255, 64);

	// Board colours
	public static Color EMPTY_BG_COLOUR = new Color (90, 135, 135);
	public static Color LINE_COLOUR = new Color (0, 0, 0);
	public static Color PLAYER1_BG_COLOUR = new Color (75, 148, 75);
	public static Color PLAYER2_BG_COLOUR = new Color (228, 79, 46);
	// Text colours
	public static Color TEXT_COLOUR = new Color (255, 255, 255);
	public static Color TEXT_SHADOW_COLOUR = new Color (0, 0, 0);

}
