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
package org.jogre.ninetynine.client;

import org.jogre.ninetynine.common.NinetyNineCoreModel;
import org.jogre.ninetynine.std.DrawableHand;
import org.jogre.ninetynine.std.Hand;
import org.jogre.ninetynine.std.Card;

// Client model for game of NinetyNine
public class NinetyNineClientModel extends NinetyNineCoreModel {

	// Hand objects that hold the played card and the tricks taken.
	// These are created so that the drawable Hand component can
	// be used to draw them on the screen.
	protected Hand [] playedCardAsHand = new Hand[3];
	protected Hand [] tricksTakenAsHand = new Hand[3];

	// The tricksTakenHand of the winner of the most recent trick.
	protected Hand lastTrickWinnerHand;

	// The object to alert when the game state is updated
	protected IGameStateAlertee alertController;

	/**
	 * Constructor for the model
	 */
	public NinetyNineClientModel(int roundsInGame) {
		super(roundsInGame);

		// Do local initialization
		for (int i=0; i<3; i++) {
			playedCardAsHand[i] = new Hand();
			tricksTakenAsHand[i] = new Hand();
		}

		resetGame();
	}

	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame() {
		super.resetGame();

		lastTrickWinnerHand = null;
	}

	/**
	 * Set the object that we should alert when the setGameState occurs.
	 */
	public void setGameStateAlertee(IGameStateAlertee theAlertee) {
		alertController = theAlertee;
	}

	/**
	 * Override the core model routine so that we can do extra stuff.
	 *
	 * Get ready for the next round.
	 *
	 * @returns		true = Ready for next round
	 *				false = Game over
	 */
	public boolean readyForNextRound() {
		// Empty the trick's taken hands
		tricksTakenAsHand[0].empty();
		tricksTakenAsHand[1].empty();
		tricksTakenAsHand[2].empty();

		lastTrickWinnerHand = null;

		return super.readyForNextRound();
	}

	/**
	 * Return the hand object that reflects the played card
	 * for the given player.
	 *
	 * @param	playerId		The Id of the players hand to return.
	 *
	 * @return the hand that reflects the playerId's played card.
	 */
	public Hand getPlayedCardAsHand(int playerId) {
		return playedCardAsHand[playerId];
	}

	/**
	 * Return the hand object that reflects the tricks taken
	 * for the given player.
	 *
	 * @param	playerId		The Id of the players tricks taken to return.
	 *
	 * @return the hand that reflects the playerId's tricks taken.
	 */
	public Hand getTakenTricksHand(int playerId) {
		return tricksTakenAsHand[playerId];
	}


	/**
	 * Override the core model's routine so that we can do more stuff.
	 *
	 * Play the given card.
	 *
	 * @param	playerId		The player who is making the play
	 * @param	playedCard		The card to play
	 */
	public void playCard(int playerId, Card playedCard) {
		super.playCard(playerId, playedCard);

		// Add this card to the hand of the played cards so that it shows up on the screen.
		playedCardAsHand[playerId].addCard(playedCard);
	}

	/**
	 * Remove three hand cards from the given player's hand.  This is used to
	 * remove the three cards from a hand that were moved into the bid
	 * area when the bid is unknown type.
	 *
	 * @param	playerId	The player whose hand should lose three cards.
	 */
	public void removeThreeHandCards(int playerId) {
		hands[playerId].freeze();
		hands[playerId].removeLastCard();
		hands[playerId].removeLastCard();
		hands[playerId].removeLastCard();
		hands[playerId].thaw();
	}

	/**
	 * Override the core model's routine for evaluating the current trick so
	 * that we can do more stuff.
	 *
	 * Evaluate the current trick to determine who gets it and becomes the new leader.
	 * Set the currentPlayerId to the player who will be playing next.
	 *
	 * @return false = trick not over yet.
	 *         true = trick is over.  currentPlayerId is set to the winner.
	 */
	public boolean evaluateTrick() {
		boolean value = super.evaluateTrick();
		if (value) {
			// The trick is over, so let's do some clean-up and get ready for the next trick.
			if (lastTrickWinnerHand != null) {
				// Remove the last trick cards from the taken card hand for the winner of the
				// previous trick and convert them into an unknown card
				lastTrickWinnerHand.freeze();
				lastTrickWinnerHand.removeLastCard();
				lastTrickWinnerHand.removeLastCard();
				lastTrickWinnerHand.removeLastCard();
				lastTrickWinnerHand.appendCard(unknownCard);
				lastTrickWinnerHand.thaw();
			}

			// Add the trick to the taken cards hand of the winner
			lastTrickWinnerHand = tricksTakenAsHand[currentPlayerId];
			lastTrickWinnerHand.freeze();
			lastTrickWinnerHand.appendCard(playedCards[0]);
			lastTrickWinnerHand.appendCard(playedCards[1]);
			lastTrickWinnerHand.appendCard(playedCards[2]);
			lastTrickWinnerHand.thaw();

			// Remove the cards from the playedCardAsHand hands
			playedCardAsHand[0].empty();
			playedCardAsHand[1].empty();
			playedCardAsHand[2].empty();

			super.getReadyForNextTrick();
		}

		return value;
	}

	/**
 	 * Override this method from the core model so that we are notified when
	 * the game state is to be set and can make additional set ups.
	 */
	public void clientModelSetState() {

		if (isPlaying()) {
			Card unknownCard = new Card (Card.UNKNOWN, 0, true);

			for (int i=0; i<3; i++) {
				playedCardAsHand[i].empty();
				Card playedCard = getPlayedCard(i);
				if (playedCard.isVisible()) {
					playedCardAsHand[i].appendCard(playedCard);
				}

				tricksTakenAsHand[i].empty();
				int wonTricks = getWonTricks(i);
				while (wonTricks > 0) {
					tricksTakenAsHand[i].appendCard(unknownCard);
					wonTricks -= 1;
				}
			}
		}

		// Tell the controller that we've changed.
		alertController.setState();
	}
}
