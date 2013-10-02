/*
 * JOGRE (Java Online Gaming Real-time Engine) - Abstrac
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
package org.jogre.abstrac.client;

import java.awt.Graphics;

// An generic interface for an object that can draw numbers
public interface INumberRenderer {

	/**
	 * Return the width of a digit, in pixels.
	 *
	 * @return	the width of a digit, in pixels.
	 */
	public int getDigitWidth();

	/**
	 * Return the height of a digit, in pixels.
	 *
	 * @return	the height of a digit, in pixels.
	 */
	public int getDigitHeight();

	/**
	 * Paint a single digit number at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theNum		The Number to draw (0..9)
	 */
	public void paintSingleDigit (Graphics g, int x, int y, int num);

	/**
	 * Paint a double digit number at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theNum		The Number to draw (0..99)
	 * @param	spacing		The number of pixels to put between the digits
	 */
	public void paintDoubleDigit (Graphics g, int x, int y, int num, int spacing);

	/**
	 * Paint a triple digit number at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theNum		The Number to draw (0..999)
	 * @param	spacing		The number of pixels to put between the digits
	 */
	public void paintTripleDigit (Graphics g, int x, int y, int num, int spacing);
}
