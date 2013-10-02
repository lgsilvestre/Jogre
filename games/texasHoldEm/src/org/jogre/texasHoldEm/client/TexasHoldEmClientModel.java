/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.client;

import org.jogre.texasHoldEm.common.TexasHoldEmCoreModel;
import org.jogre.texasHoldEm.common.Card;

/**
 * Client Model for the TexasHoldEm game
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmClientModel extends TexasHoldEmCoreModel {

	// The history of the last hand:
	//   Each player's cards & the 5 common cards
	private Card [][] historyPlayerCards = new Card [8][2];
	private Card []   historyCommonCards = new Card [5];

	//   The type of hand
	private int historyHandType;

	// Highlight indication for the players
	private boolean [] historyPlayerHighlight = new boolean [8];

	// The time (local & absolute) until the next change of blind schedule.
	private long changeTime = -1;

	/**
	 * Constructor for the model
	 */
	public TexasHoldEmClientModel(
		int numPlayers,
		int initialBankroll,
		int initialBlindSchedule,
		int blindAdvanceTime,
		int raiseLimit
	) {
		super(numPlayers,
		      initialBankroll,
		      initialBlindSchedule,
		      blindAdvanceTime,
		      raiseLimit);

		clearHistory();
	}

	/**
	 * Clear the hand history data.
	 */
	public void clearHistory() {
		// Initialize the history data
		Card invisibleCard = new Card (0, 0, false);
		for (int i=0; i<8; i++) {
			historyPlayerCards[i][0] = invisibleCard;
			historyPlayerCards[i][1] = invisibleCard;
			historyPlayerHighlight[i] = false;
		}
		for (int i=0; i<5; i++) {
			historyCommonCards[i] = invisibleCard;
		}

		historyHandType = HAND_TYPE_BLANK;
	}

	/**
	 * Return a player's card from the hand history
	 */
	public Card getHistoryPlayerCard(int seatNum, int whichCard) {
		return historyPlayerCards[seatNum][whichCard];
	}

	/**
	 * Return a common card from the hand history
	 */
	public Card getHistoryCommonCard(int whichCard) {
		return historyCommonCards[whichCard];
	}

	/**
	 * Return the type of hand that won from the history
	 */
	public int getHistoryHandType() {
		return historyHandType;
	}

	/**
	 * Return whether to highlight the given player or not.
	 */
	public boolean getHistoryPlayerHighlight(int seatNum) {
		return historyPlayerHighlight[seatNum];
	}

	/**
	 * Save the current hand information in the hand history
	 */
	public void saveHandHistory() {
		for (int i=0; i<numPlayers; i++) {
			historyPlayerCards[i][0] = getPlayerCard(i, 0);
			historyPlayerCards[i][1] = getPlayerCard(i, 1);
		}
		for (int i=0; i<5; i++) {
			historyCommonCards[i] = getCommonCard(i);
		}
	}

	/**
	 * Set the history of the winner.
	 */
	public void setHistoryWinner(int winningValue, int [] allValues) {
		// Pull out the winning type to use to display the text string
		historyHandType = (winningValue >> 20);

		// Royal flushes are reported by the server as simply straight flushes
		// with a high card of 14 (ace).  So, this changes it back to a royal
		// flush for display to the players.
		if ((historyHandType == HAND_TYPE_STRAIGHT_FLUSH) &&
		    (winningValue & 0xF0000) == (14 << 16)) {
			    historyHandType = HAND_TYPE_ROYAL_FLUSH;
		}

		// Determine which players should be highlighted.
		for (int i=0; i<numPlayers; i++) {
			historyPlayerHighlight[i] = (allValues[i] == winningValue);
		}
	}

	/**
	 * Set the local client's view of the blind stage.
	 *
	 * @param blindStage        The new blind stage for the game.
	 * @param timeToNextStage   The time, in seconds, until the next stage change.
	 */
	public void setLocalTime(int blindStage, int timeToNextStage) {
		// Set the current blind stage.
		setCurrentBlindScheduleStage(blindStage);

		// Resynchronize our local time to match the server's time
		changeTime = System.currentTimeMillis() + (timeToNextStage * 1000);

		refreshObservers();
	}

	/**
	 * Return the number of minutes until the next change of blind values.
	 *
	 * 0 => less than 1 minute
	 * -1 => never
	 *
	 * @return the number of minutes until the next change of blinds.
	 */
	public int minutesToNextBlindChange() {
		// If we're not advancing or we've reached the last stage, then
		// there is infinite time to the next change.
		if ((currentBlindScheduleStage == 8) ||
		    (blindAdvanceTime == 0)) {
			return -1;
		}

		// If we're not playing the game yet, then the time to next stage is
		// the configured time
		if (changeTime < 0) {
			return blindAdvanceTime / 60;
		}

		// Compute the time, in seconds until the next change
		long delta = changeTime - System.currentTimeMillis();

		if (delta < 60000) {
			// Less than 1 minute to go
			return 0;
		}

		return (int) ((delta + 30000) / 60000);
	}

	/*
	 * Determine if the given player is going all in with the given bet.
	 *
	 * @param   seatNum    The seat number to check.
	 * @param   bidValue   The value of the bid to check.
	 *
	 * @return true  => This bid will cause an all-in
	 *         false => This bid will not cause an all-in
	 */
	public boolean isAllInAt(int seatNum, int bidValue) {
		return (bidValue == (holdings[seatNum] + potEquity[seatNum]));
	}

	/*
	 * Return the bid that this player would have to make to call
	 */
	public int getCallBid(int seatNum) {
		int playerMax = (holdings[seatNum] + potEquity[seatNum]);
		return Math.min (playerMax, currentBid);
	}
}
