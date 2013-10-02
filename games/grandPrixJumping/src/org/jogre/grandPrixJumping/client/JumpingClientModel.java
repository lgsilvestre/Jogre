/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter (rwalter42@yahoo.com)
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

import org.jogre.grandPrixJumping.common.JumpingCard;
import org.jogre.grandPrixJumping.common.JumpingCoreModel;
import org.jogre.grandPrixJumping.common.JumpingJump;

import java.util.Vector;
import java.util.ListIterator;

import nanoxml.XMLElement;

/**
 * Model which holds the data for a game of Grand Prix Jumping
 * This is the client model which only knows what a client sees
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingClientModel extends JumpingCoreModel {

	// Indicates if the current state of playedCards has changed since someone last
	// looked at them.
	private boolean playedCardsChanged;

	// The number of cards in the deck.
	private int deckSize;

	/**
	 * Constructor for the client model
	 *
	 * @param	openHands		Indicates if the hands will be open or not
	 * @param	allowEdits		Indicates if board editing is allowed or not
	 * @param	initialLayout	The initial fence layout string
	 */
	public JumpingClientModel(boolean openHands, boolean allowEdits, String initialLayout) {
		// Construct the core model
		super(openHands, allowEdits, initialLayout);

		// Use the resetGame routine to initialize most things
		this.resetGame();
		
		// Client model is created in GAME_OVER state and moves to CREATING_TRACK
		// only when the game is started.
		changePhase(GAME_OVER);
	}

	/**
	 * Reset the game.
	 */
	public void resetGame() {
		super.resetGame();

		deckSize = 80;
	}

/****************************************************************/

	/**
	 * Return the size of the current deck.
	 *
	 * @return the current deck size.
	 */
	public int getDeckSize() {
		return deckSize;
	}

	/**
	 * Set the size of the current deck.  If this has required a reshuffle, then
	 * true is returned.
	 *
	 * @param	newDeckSize		The new size to set the deckSize to.
	 *							Negative numbers won't change the value.
	 * @return true => A reshuffle occurred
	 * @return false => A reshuffle did not occur.
	 */
	public boolean setNewDeckSize(int newDeckSize) {
		boolean result = false;
		if (newDeckSize >= 0) {
			result = (deckSize - newDeckSize) < 0;
			deckSize = newDeckSize;
			refreshObservers();
		}
		return result;
	}

	/**
	 * Determine if the played cards have changed since the last time
	 * this was called.
	 */
	public boolean playedCardsChanged() {
		if (playedCardsChanged) {
			playedCardsChanged = false;
			return true;
		}
		return false;
	}

	/**
	 * Go through all of the cards in a player's hand and set the playable
	 * flag to the correct value, given the current played cards.
	 *
	 * @param	playerSeat		The player whose cards should be marked
	 */
	public void setPlayableFlags(int playerSeat) {
		if ((playerSeat < 0) || (playerSeat > 1)) {
			return;
		}

		Vector hand = getPlayableHand(playerSeat);

		if (isDiscarding() || isOfficialDiscardActive()) {
			// Update flags for when discarding - All cards are valid selections
			ListIterator iter = hand.listIterator();
			while (iter.hasNext()) {
				JumpingCard card = (JumpingCard) iter.next();
				card.setPlayable( true );
			}
		} else if (isPlayingCards()) {
			// Update flags for when playing
			ListIterator iter = hand.listIterator();
			while (iter.hasNext()) {
				JumpingCard card = (JumpingCard) iter.next();
				card.setPlayable( card.isMarked() || isPlayableNow(card) );
			}
		}
	}

	/**
	 * Determine if the given card is playable in the current position,
	 * and if so, add it to the played cards vector and update if this
	 * is now a valid complete move.
	 */
	public boolean playCard(JumpingCard card) {
		boolean played = addCardToCurrentJumps(card);

		if (played) {
			// Update the display, since things have changed
			playedCardsChanged = true;
			refreshObservers();
		}

		return played;
	}

	/**
	 * Unplay the given card.  Move it back to the player's hand (along with
	 * any other side effects of un-playing this card), and update state.
	 */
	public boolean unplayCard(JumpingCard card) {

		boolean removed = removeCardFromCurrentJumps(card);

		if (removed) {
			// Update the display since things have changed
			playedCardsChanged = true;
			refreshObservers();
		}

		return removed;
	}

	/**
	 * Remove all un-committed jumps from current jumps.
	 */
	public void removeUncommittedJumps() {
		playedCardsChanged = true;
		super.removeUncommittedJumps();
	}

	/**
	 * Commit a horse movement.  This will play the current set of jumps, removing
	 * the cards the player's hand, and move the associated horse.  It will
	 * also assign fault points appropriately.
	 *
	 * @param	playerId		The player trying to make the move
	 * @returns true => Move was good.
	 *          false => Move was not good.
	 */
	public boolean commitHorseMovement(int playerId) {

		boolean playOk = super.commitHorseMovement(playerId, getCurrJumps());
		playedCardsChanged = true;

		refreshObservers();

		return playOk;
	}

	/**
	 * Indicate if the played cards are pre-commit or not.
	 */
	public boolean playedCardsArePrecommit() {
		try {
			return !((JumpingJump) getCurrJumps().lastElement()).isCommitted();
		} catch (Exception e) {
			return false;
		}
	}

/****************************************************************/

	/**
	 * Override this method in the core model.  This is called whenever the
	 * client attaches in the middle of a game and the new state has been sent.
	 * This needs to update state outside of the core model.
	 */
	protected void setClientModelState(XMLElement message) {
		deckSize = message.getIntAttribute("deckSize");
	}

}

