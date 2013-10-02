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
package org.jogre.abstrac.std;

import java.awt.Graphics;

// An generic interface for an object that can draw Cards.
public interface ICardRenderer {

	/**
	 * Return the width of a card, in pixels.
	 *
	 * @return	the width of a card, in pixels.
	 */
	public int getCardWidth();

	/**
	 * Return the height of a card, in pixels.
	 *
	 * @return	the height of a card, in pixels.
	 */
	public int getCardHeight();

	/**
	 * Return the width of a suit marker, in pixels.
	 *
	 * @return	the width of a suit marker, in pixels.
	 */
	public int getSuitWidth();

	/**
	 * Return the height of a suit marker, in pixels.
	 *
	 * @return	the height of a suit marker, in pixels.
	 */
	public int getSuitHeight();

	/**
	 * Paint a card at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theCard		The Card to draw
	 */
	public void paintCard (Graphics g, int x, int y, Card theCard);

	/**
	 * Paint a card prompt at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the prompt.
	 * @param	y			Y coordinate of where to draw the prompt.
	 */
	public void paintPrompt(Graphics g, int x, int y);

	/**
	 * Paint a suit marker at a given location.
	 *
	 * @param	g			The graphics context to draw on.
	 * @param	x			X coordinate of where to draw the card.
	 * @param	y			Y coordinate of where to draw the card.
	 * @param	theSuit		The suit to draw, with -1 being "noTrump" suit
	 */
	public void paintSuitMarker (Graphics g, int x, int y, int theSuit);
}
