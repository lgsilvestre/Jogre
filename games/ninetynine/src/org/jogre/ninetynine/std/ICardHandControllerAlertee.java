/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.std;

import java.awt.Graphics;

// An generic interface for an object that can receive alerts from
// a CardHandController
public interface ICardHandControllerAlertee {

	/**
	 * Signal that the user has clicked on a card.
	 *
	 * @param	theComponent		The CardHandComponent which the card came from
	 * @param	selectedCard		The card that was selected
	 */
	public void signalCardClicked(CardHandComponent theComponent, Card clickedCard);

	/**
	 * Test to see if the current selected card is a valid card to select.
	 * The Alertee should return true to tell the hand controller that it is
	 * ok to indicate this card is valid.  Or return false to indicate that
	 * the card is not ok to play right now and the card should not be indicated
	 * to be valid.
	 *
	 * @param	theComponent		The CardHandComponent which the active card
	 *								to be tested is from.
	 */
	public boolean isValidCardSelection(CardHandComponent theComponent);
}
