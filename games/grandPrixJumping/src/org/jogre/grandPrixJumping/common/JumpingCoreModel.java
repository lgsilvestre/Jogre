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
package org.jogre.grandPrixJumping.common;

import java.lang.StringBuilder;

import java.util.Collections;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;
import org.jogre.common.TransmissionException;
import org.jogre.common.IGameOver;

/**
 * Core model which holds the data for a game of Grand Prix Jumping
 * This model is common for the client & the server.  It only holds
 * data and methods that are common to the two.
 *
 * @author Richard Walter
 * @version Beta 0.3
 *
 * Note: This code was started with the thought that the currJumps
 *       would need to be kept for both players, so some routines
 *       take a vector of jumps as a parameter.  Later, I realized
 *       that only a single currJumps needs to be kept since only
 *       one player is playing cards at a time.  At that point,
 *       the routines started using the single currJumps.  So,
 *       this file is currently in a state where some methods take
 *       a vector of jumps as a parameter and some use the global
 *       currJumps.  It really ought to be cleaned up at some point...
 */
public class JumpingCoreModel extends JogreModel {

	// Size of the track.
	public static final int TRACK_SPACES = 48;
	public static final int LAST_SPACE = (TRACK_SPACES - 1);

	// Indications of which players are still editing the track
	// at the start of the game.
	boolean [] stillEditing = new boolean [2];

	// The locations of the horses on the track
	private int [] horse_locations = new int [2];

	// The fences on the track
	// Sometimes a vector is more efficient, and sometimes an array
	// is more efficient, so I keep both data structures.  They contain
	// identical information (or, at least, they *should*. )
	private Vector fences;
	private JumpingFence [] fenceArray = new JumpingFence [TRACK_SPACES];
	private int [] waterJumpFaults = new int [TRACK_SPACES];

	// The cards that are in the sorting areas
	private Vector [] sortCards = new Vector [2];

	// The cards in each player's hand
	private Vector [] playableHand = new Vector [2];
	private Vector [] immediateHand = new Vector [2];

	// The faults that each player has accumulated
	// (The faults are kept in units of 1/4 fault points)
	private int [] quarterFaults = new int [2];
	private int faultPointsScored;

	// The history of faults taken by each player
	private Vector [] faultHistory = new Vector [2];

	// The max number of cards the player may keep in his hand
	private int [] maxHandSize = new int [2];

	// Where the horse ends up after a move.
	private int finalHorseLoc;

	// An invisible card & unknown card.
	private JumpingCard clearCard;
	private JumpingCard unknownCard;

	// The last card that was moved between the sort areas.
	private JumpingCard lastMovedSortCard;

	// The vector of jumps that are in progress
	private Vector currJumps;
	private int currJumpOwner;

	// States of activated cards in either the hand or immediate areas
	private boolean handCardActivated;
	private boolean immCardActivated;

	// The two cards that are in the "dual rider" choosing slots
	private JumpingCard [] dualRiderCards = new JumpingCard [2];

	// The number of cards that are needed to discard as a result of an official card
	private int officialDiscardNumber;

	// Codes for moving the sort cards.
	public static final int LEFT_TO_RIGHT = 0;
	public static final int RIGHT_TO_LEFT = 1;

	// Game Phases
	public static final int INTERMEDIATE_PHASE = -1;
	public static final int CREATING_TRACK = 0;
	public static final int SORTING_CARDS = 1;
	public static final int CHOOSING_SORTED = 2;
	public static final int PLAYING_CARDS_CHOOSER = 3;
	public static final int PLAYING_CARDS_SORTER = 4;
	public static final int PLAY_IMM_CARDS_CHOOSER = 5;
	public static final int PLAY_IMM_CARDS_SORTER = 6;
	public static final int CHOOSER_DISCARDING = 7;
	public static final int SORTER_DISCARDING = 8;
	public static final int GAME_OVER = 10;

	private int gamePhase;

	// Current player
	private int currentSorter;

	// The current turn number
	private int turnNumber;

	// The location where the "shadow horse" that tracks the current jump owner's play
	// progress.
	private int shadowHorseLoc;

	// Remember if the hands will be open or not.
	private boolean openHands;

	// Remember if editing of the board is allowed or not.
	private boolean allowEdits;

	// Entries for the final results (WIN, LOSE, TIE) and score at the end of the game.
	private int [] resultArray = new int [2];
	private String scoreString;

	/**
	 * Constructor for the core model
	 *
	 * @param	openHands		Indicates if the hands will be open or not
	 * @param	allowEdits		Indicates if board editing is allowed or not
	 * @param	initialLayout	The initial fence layout string
	 */
	public JumpingCoreModel(boolean openHands, boolean allowEdits, String initialLayout) {
		super();

		// Create various things
		clearCard = JumpingCard.makeInvisibleCard();
		unknownCard = new JumpingCard();

		playableHand[0] = new Vector();
		playableHand[1] = new Vector();
		immediateHand[0] = new Vector();
		immediateHand[1] = new Vector();
		faultHistory[0] = new Vector();
		faultHistory[1] = new Vector();
		sortCards[0] = new Vector();
		sortCards[1] = new Vector();

		// Remember parameters
		this.openHands = openHands;
		this.allowEdits = allowEdits;

		// Setup the initial board
		initBoard(initialLayout);

		// Use the resetGame routine to initialize most things
		private_ResetGame();
	}

	/**
	 * Reset the model back to the initial state
	 */
	private void private_ResetGame() {
		// Reset the players' state
		for (int i=0; i<2; i++) {
			horse_locations[i] = 0;
			playableHand[i].clear();
			immediateHand[i].clear();
			faultHistory[i].clear();
			quarterFaults[i] = 0;
			maxHandSize[i] = 8;
		}

		// Reset misc. stuff
		gamePhase = CREATING_TRACK;
		currentSorter = -1;
		handCardActivated = false;
		immCardActivated = false;
		turnNumber = 0;
		shadowHorseLoc = -1;

		dualRiderCards[0] = null;
		dualRiderCards[1] = null;
		officialDiscardNumber = -1;

		stillEditing[0] = true;
		stillEditing[1] = true;

		clearCurrentJumps();
	}

	public void resetGame() {
		private_ResetGame();

		refreshObservers();
	}

	/**
	 * Routines for getting the game phase
	 */
	public int getGamePhase()                  { return (gamePhase); }
	public boolean isCreatingTrack()           { return (gamePhase == CREATING_TRACK); }
	public boolean isSortingCards()            { return (gamePhase == SORTING_CARDS); }
	public boolean isChoosingSorted()          { return (gamePhase == CHOOSING_SORTED); }
	public boolean isChooserPlayingCards()     { return (gamePhase == PLAYING_CARDS_CHOOSER); }
	public boolean isSorterPlayingCards()      { return (gamePhase == PLAYING_CARDS_SORTER); }
	public boolean isChooserPlayingImmediate() { return (gamePhase == PLAY_IMM_CARDS_CHOOSER); }
	public boolean isSorterPlayingImmediate()  { return (gamePhase == PLAY_IMM_CARDS_SORTER); }
	public boolean isPlayingCards()            { return ((gamePhase == PLAYING_CARDS_CHOOSER) ||
	                                                     (gamePhase == PLAYING_CARDS_SORTER)); }
	public boolean isPlayingImmediate()        { return ((gamePhase == PLAY_IMM_CARDS_CHOOSER) ||
	                                                     (gamePhase == PLAY_IMM_CARDS_SORTER)); }
	public boolean isChooserPlaying()          { return ((gamePhase == PLAYING_CARDS_CHOOSER) ||
	                                                     (gamePhase == PLAY_IMM_CARDS_CHOOSER)); }
	public boolean isSorterPlaying()           { return ((gamePhase == PLAYING_CARDS_SORTER) ||
	                                                     (gamePhase == PLAY_IMM_CARDS_SORTER)); }
	public boolean isChooserDiscarding()       { return (gamePhase == CHOOSER_DISCARDING); }
	public boolean isSorterDiscarding()        { return (gamePhase == SORTER_DISCARDING); }
	public boolean isDiscarding()              { return ((gamePhase == CHOOSER_DISCARDING) ||
	                                                     (gamePhase == SORTER_DISCARDING)); }
	public boolean isGameOver()                { return (gamePhase == GAME_OVER); }

	/**
	 * Change the phase of the game to the new one provided
	 *
	 * @param	newPhase	The new phase to move to.
	 *
	 */
	public void changePhase(int newPhase) {
		gamePhase = newPhase;
		shadowHorseLoc = -1;	// When changing phase, turn the shadow horse off

		refreshObservers();
	}

	/*
	 * Retrieve various values from the model
	 */
	public int getHorseLocation(int whichHorse)			{ return horse_locations[whichHorse]; }
	public Vector getAllFences()                        { return fences; }
	public int getWaterJumpFaults(int location)			{ return waterJumpFaults[location]; }
	public Vector getSortCards(int side)				{ return sortCards[side]; }
	public JumpingCard getLastMovedSortCard()			{ return lastMovedSortCard; }
	public Vector getPlayableHand(int seat)				{ return playableHand[seat]; }
	public Vector getImmediateHand(int seat)			{ return immediateHand[seat]; }
	public int getSorterSeatNum()						{ return currentSorter; }
	public int getChooserSeatNum()						{ return (1-currentSorter); }
	public int getMaxHandSize(int seat)					{ return maxHandSize[seat]; }
	public int numExcessCards(int seat)					{ return Math.max(0, playableHand[seat].size() - maxHandSize[seat]); }
	public int getQuarterFaults(int who)				{ return quarterFaults[who]; }
	public JumpingCard getDualRiderCard(int which)		{ return dualRiderCards[which]; }
	public int getOfficialDiscardNumber()				{ return officialDiscardNumber; }
	public int [] getResultArray()						{ return resultArray; }
	public String getScoreString()						{ return scoreString; }
	public Vector [] getFaultHistory()					{ return faultHistory; }
	public int getTurnNumber()                          { return turnNumber; }

	public boolean sorterHasImmediateCards()  { return hasUnmarkedImmediateCards(currentSorter); }
	public boolean chooserHasImmediateCards() { return hasUnmarkedImmediateCards(1-currentSorter); }

	public int getCurrentPlayer()	{
		if (isSortingCards() || isSorterPlayingCards() || isSorterPlayingImmediate() || isSorterDiscarding()) {
			return getSorterSeatNum();
		} else {
			return getChooserSeatNum();
		}
	}

	public boolean isHandCardActivated()	 { return handCardActivated; }
	public boolean isImmCardActivated()		 { return immCardActivated; }
	public boolean isDualRiderActive()		 { return dualRiderCards[0] != null; }
	public boolean isCardActivated()		 { return handCardActivated || immCardActivated || (dualRiderCards[0] != null); }
	public boolean isOfficialDiscardActive() { return officialDiscardNumber > 0; }

	public boolean isOpenHands() { return openHands; }
	public boolean allowEdits()  { return allowEdits; }

	/*
	 * Put the fences on the board.
	 *
	 * @param	initialLayout		The initial fence layout string
	 */
	public final static int WATER_FENCE = 6;
	private void initBoard(String initialLayout) {

		// Initialize the waterJump & fenceArrays arrays
		fences = new Vector();
		for (int i=0; i < TRACK_SPACES; i++) {
			waterJumpFaults[i] = 0;
			fenceArray[i] = null;
		}

		if (initialLayout.startsWith("-")) {
			// Layouts starting with a negative number use the older code
			int [] spaceIds = JogreUtils.convertToIntArray(initialLayout);
			int currentFenceHeight = 1;

			// Scan the initial layout array.
			for (int i=0; i < spaceIds.length; i++) {
				int num = spaceIds[i];
				if ((num < 0) && (num >= -WATER_FENCE)) {
					// Negative numbers set a new fence height
					currentFenceHeight = -num;
				} else if ((num >= 0) && (num < TRACK_SPACES)) {
					// Positive numbers put fences on the track.
					addFenceToBoard(currentFenceHeight, num);
				}
			}
		} else {
			// Layouts that don't start with a negative number use the new code
			byte [] bytes = initialLayout.toLowerCase().getBytes();

			int currSpace = 0;
			int i = 0;
			while ((currSpace < LAST_SPACE) && (i < bytes.length)) {
				byte b = bytes[i];
				if (b == 'z') {
					// Skip 5 spaces
					currSpace += 5;
				} else if ((b >= 'a') && (b <= 'y')) {
					currSpace += ((b - 'a') % 5 + 1);
					addFenceToBoard(((b - 'a') / 5) + 1, currSpace);
				} else if ((b >= '1') && (b <= '5')) {
					currSpace += (b - '1' + 1);
					addFenceToBoard(WATER_FENCE, currSpace);
					// Advance over the water jump
					currSpace += 3;
				}
				i += 1;
			}
		}
	}

	/**
	 * This routine will create the code string for the current track
	 * configuration.
	 */
	public String createCodeForTrack() {
		StringBuilder sb = new StringBuilder();

		int dist = 0;
		for (int s=1; s < TRACK_SPACES; s++) {
			JumpingFence fence = getFenceAt(s);
			if (fence == null) {
				dist += 1;
				if (dist == 5) {
					sb.append('z');
					dist = 0;
				}
			} else {
				if (fence.isWaterJump()) {
					sb.append((char) ('1' + dist) );
					s += 3;
				} else {
					sb.append((char) ('a' + dist + ((fence.type()-1) * 5)));
				}
				dist = 0;
			}
		}

		return sb.toString();
	}

	/*
	 * Add a fence of the given type to the board at the given space.
	 */
	private void addFenceToBoard(int type, int space) {
		if (isValidForFenceAt(type, space)) {
			JumpingFence newFence = new JumpingFence(space, type);
			fences.add(newFence);
			fenceArray[space] = newFence;

			if (type == WATER_FENCE) {
				addWaterJumpFaults(space);
			}
		}
	}

	/*
	 * Remove a fence from the board at the given space.
	 */
	private void removeFenceFromBoard(int space) {
		JumpingFence oldFence = getFenceAt(space);
		if (oldFence != null) {
			fences.remove(oldFence);
			fenceArray[space] = null;

			if (oldFence.type() == WATER_FENCE) {
				removeWaterJumpFaults(space);
			}
		}
	}

	/*
	 * Determine if it is valid to place a fence of the given height at the
	 * given space on the board.
	 */
	private boolean isValidForFenceAt(int height, int space) {
		// Can't put a fence on a space not on the board
		if ((space < 0) || (space >= TRACK_SPACES)) {
			return false;
		}

		// Can't put a fence on top of an existing fence
		if (isFenceOn(space)) {
			return false;
		}

		// Can't place a fence on top of a water jump
		if (waterJumpFaults[space] != 0) {
			return false;
		}

		// Need more tests for water jumps
		if (height == WATER_FENCE) {
			return isValidForWaterJump(space);
		}

		// Passed all of the tests
		return true;
	}

	/*
	 * Extra checks on the validity of water jumps.
	 *
	 * @param	space		The space where a water jump might be started
	 * @return if it is legal to put a water jump there.
	 */
	private boolean isValidForWaterJump(int space) {
		// Water jumps can't start less than 4 spaces away from a board turn
		if ( ((space >=  9) && (space <= 11)) ||
			 ((space >= 21) && (space <= 23)) ||
			 ((space >= 33) && (space <= 35)) ||
			 (space >= 44) ) {
			return false;
		}

		// Water jumps can't overlap
		if (waterJumpFaults[space+3] != 0) {
			return false;
		}

		// Water jumps can't be put where they cover regular jumps
		if ((getFenceAt(space+1) != null) ||
			(getFenceAt(space+2) != null) ||
			(getFenceAt(space+3) != null)) {
			return false;
		}

		// Passed all of the tests
		return true;
	}

	/*
	 * Set the value for waterJumpFaults for a water jump starting at
	 * the given space.
	 */
	private void addWaterJumpFaults(int space) {
		waterJumpFaults[space  ] = 4;
		waterJumpFaults[space+1] = 3;
		waterJumpFaults[space+2] = 2;
		waterJumpFaults[space+3] = 1;
	}

	/*
	 * Remove the value for waterJumpFaults for a water jump starting at
	 * the given space.
	 */
	private void removeWaterJumpFaults(int space) {
		waterJumpFaults[space  ] = 0;
		waterJumpFaults[space+1] = 0;
		waterJumpFaults[space+2] = 0;
		waterJumpFaults[space+3] = 0;
	}

	/**
	 * Modify a fence on the track.
	 *
	 * @param	space		The space whose fence is to be modified.
	 * @param	dir			The direction (+1 or -1) of modification.
	 * @return if the modification was valid or not.
	 */
	public boolean modifyFence(int space, int dir) {
		boolean valid = true;	// Assume true
		JumpingFence oldFence = getFenceAt(space);

		if (oldFence == null) {
			// We're putting a new fence onto the board.
			int newHeight = (dir == 1) ? 1 : (isValidForFenceAt(WATER_FENCE, space) ? WATER_FENCE : 5);

			valid = isValidForFenceAt(newHeight, space);
			if (valid) {
				addFenceToBoard(newHeight, space);
			}
		} else {
			// We're modifying a fence already on the board.
			int newHeight = (oldFence.type() + dir) % (WATER_FENCE + 1);

			if (newHeight == WATER_FENCE) {
				// We're upgrading a size 5 fence to a water jump.
				// Need to make extra checks before doing this.
				if (isValidForWaterJump(space)) {
					oldFence.setType(newHeight);
					addWaterJumpFaults(space);
				} else {
					// Can't put a water jump here, so skip it and remove
					// the fence entirely.
					newHeight = 0;
				}
			}

			if (newHeight == 0) {
				// We're removing a fence from the board.
				removeFenceFromBoard(space);
			} else if (newHeight != WATER_FENCE) {
				if (oldFence.isWaterJump()) {
					// We're downgrading a water jump to a size 5 fence
					// so, we need to remove the water faults
					removeWaterJumpFaults(space);
				}

				// Change the height of the existing fence.
				oldFence.setType(newHeight);
			}
		}

		// If this actually changed something, then refresh
		if (valid) {
			refreshObservers();
		}

		return valid;
	}

	/**
	 * Return the number of water jump faults that result from hopping from
	 * the given starting location and going for the given length.
	 * Note that faults are only accrued for the hop that crosses the
	 * first space of the water jump.  Hopping within the tail of the water
	 * jump does *not* accrue any water points.
	 *
	 * @param	from		The space to start hopping at.
	 * @param	length		The number of spaces to hop.
	 * @return the number of water jump faults accrued.
	 */
	public int getWaterJumpFaults(int from, int length) {
		int to = Math.min((from + length), LAST_SPACE);
		int potentialFaults = waterJumpFaults[to];

		// If we didn't jump over the first space of the water
		// jump, then faults is 0.
		if ((potentialFaults + length) <= 4) {
			potentialFaults = 0;
		}

		return potentialFaults;
	}

	/**
	 * Return if the given player is still editing the track.
	 *
	 * @param	seatNum		The seat number of the player.
	 * @return if that player is still editing the track.
	 */
	public boolean playerStillEditingTrack(int seatNum) {
		return stillEditing[seatNum];
	}

	/**
	 * Set the value of the still editing flag for the given player to the
	 * given value.
	 *
	 * @param	seatNum		The seat number of the player.
	 * @param	value		The new value to set the flag to
	 */
	public void setPlayerEditing(int seatNum, boolean value) {
		stillEditing[seatNum] = value;
	}

	/**
	 * Swap the chooser & sorter seat numbers.
	 */
	public void swapChooserAndSorter() {
		currentSorter = 1-currentSorter;

		// If we're changing the chooser & sorter, then this is a new turn.
		turnNumber += 1;
	}

	/**
	 * Set the current sorter.
	 */
	public void setCurrentSorter(int seatNum) {
		currentSorter = seatNum;
	}

	/**
	 * Clear the immediate hand
	 */
	public void clearImmediateHand(int seatNum) {
		immediateHand[seatNum].clear();
	}

	/**
	 * Make the vector of cards provided the new cards in the left side of the sort
	 * cards area and clear the right side.
	 *
	 * @param	newSortCards		The new batch of sort cards.
	 */
	public void setNewSortCards(Vector newSortCards) {
		// Put the given cards in the left side.
		sortCards[0] = newSortCards;

		// Make the right side 7 invisible cards.
		sortCards[1] = new Vector();
		for (int i=0; i<7; i++) {
			sortCards[1].add(clearCard);
		}

		lastMovedSortCard = null;

		refreshObservers();
	}

	/**
	 * Move a card from one of the sort card lists to the other.
	 *
	 * @param	index		The index of the card to move
	 * @return 	a direction code: LEFT_TO_RIGHT or RIGHT_TO_LEFT
	 */
	public int moveSortCard(int index) {
		int dirCode;

		JumpingCard leftCard = (JumpingCard) sortCards[0].get(index);
		JumpingCard rightCard = (JumpingCard) sortCards[1].get(index);
		sortCards[0].set(index, rightCard);
		sortCards[1].set(index, leftCard);

		if (leftCard.isVisible()) {
			lastMovedSortCard = leftCard;
			return LEFT_TO_RIGHT;
		} else {
			lastMovedSortCard = rightCard;
			return RIGHT_TO_LEFT;
		}
	}

	/**
	 * Move a card from one of the sort card lists to the other.
	 *
	 * @param	direction	Direction code: LEFT_TO_RIGHT or RIGHT_TO_LEFT
	 * @param	theCard		The card to move.
	 */
	public void moveSortCard(int direction, JumpingCard theCard) {
		Vector srcPile = (direction == LEFT_TO_RIGHT) ? sortCards[0] : sortCards[1];

		// Find the first card in the srcPile that matches <theCard>
		for (int i=0; i<srcPile.size(); i++) {
			JumpingCard pCard = (JumpingCard) srcPile.get(i);
			if (pCard.isVisible() && pCard.equals(theCard)) {
				moveSortCard(i);
				return;
			}
		}

		// Shouldn't get here
		System.out.print("Error: Asked to move sort card " + theCard + " ");
		System.out.print((direction == LEFT_TO_RIGHT) ? "LEFT_TO_RIGHT" : "RIGHT_TO_LEFT");
		System.out.println(" that doesn't exist.");
	}

	/**
	 * Determine if the current configuration of sort cards is valid or not.
	 * The config is valid if there is at least one visible and one invisible
	 * card in the left area.  (Becuase that means that there is at least one
	 * card in each sorting area.)
	 */
	public boolean isValidSorting() {
		boolean visibleSeen = false;
		boolean invisibleSeen = false;

		try {
			for (int i=0; i<sortCards[0].size(); i++) {
				JumpingCard c = (JumpingCard) sortCards[0].get(i);
				if (c.isVisible()) {
					visibleSeen = true;
				} else {
					invisibleSeen = true;
				}

				if (visibleSeen && invisibleSeen) {
					return true;
				}
			}
		} catch (NullPointerException e) {
			// There were no sortCards at all, so invalid sorting
		}

		return false;
	}

	/**
	 * Clear all of the new flags for the cards in the hand of the given
	 * seat number.
	 */
	public void clearNewFlags(int seatNum) {
		try {
			ListIterator iter = playableHand[seatNum].listIterator();
			while (iter.hasNext()) {
				JumpingCard card = (JumpingCard) iter.next();
				card.setAdded(false);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// seatNum was invalid, so nothing to do.
		}
	}

	/**
	 * Move the visible cards from one of the sort areas to one of the player's hands
	 *
	 * @param	sortAreaNum		The sort area number to move cards from
	 * @param	handNum			The hand number to move cards to
	 * @param	turnOver		If true, set the cards in the sort area to invisible
	 * @param	makeUnknown		If true, then cards put into the player's hand are made unknown
	 */
	public void moveSortCardsToHand(int sortAreaNum, int handNum, boolean turnOver, boolean makeUnknown) {
		ListIterator iter = sortCards[sortAreaNum].listIterator();

		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			if (card.isVisible()) {
				if (card.isImmediate()) {
					// Immediate card, so move into the immediate hand
					immediateHand[handNum].add(card);

					// Apply the card's effect, and set the selected flag if it has been applied
					card.setMarked(applyCardEffect(card, handNum));
				} else {
					// Not immediate cards, so move into the player's hand area
					playableHand[handNum].add(makeUnknown ? new JumpingCard() : card);
				}

				card.setAdded(true);

				// If turning over, set the sort card to invisible
				if (turnOver) {
					sortCards[sortAreaNum].set(iter.previousIndex(), clearCard);
				}
			}
		}

		// Sort the hands now that they've been added to
		Collections.sort(immediateHand[handNum]);
		Collections.sort(playableHand[handNum]);

		refreshObservers();
	}

	/**
	 * Add the given card to the given player's hand.
	 *
	 * @param	handNum			The hand number to add the card to
	 * @param	theCard			The card to add to the hand
	 * @param	makeUnknown		If true, then the card put into the player's hand is made unknown
	 */
	public void addCardToHand(int handNum, JumpingCard theCard, boolean makeUnknown) {

		if (theCard.isImmediate()) {
			immediateHand[handNum].add(theCard);
			theCard.setMarked(applyCardEffect(theCard, handNum));
		} else {
			if (makeUnknown) {
				theCard = new JumpingCard();
			}
			playableHand[handNum].add(theCard);
			Collections.sort(playableHand[handNum]);
		}

		theCard.setAdded(true);

		refreshObservers();
	}

	/**
	 * Apply the effect of an immediate card, if possible.
	 *
	 * @param	card		The card to apply
	 * @param	playerNum	The player to apply the effect to
	 * @return true => effect has been made.
	 *         false => effect has not been made.
	 */
	private boolean applyCardEffect(JumpingCard card, int playerNum) {
		int value = card.cardValue();
		if (value == JumpingCard.FAULT_1_4) {
			addToFaults(playerNum, 1, JumpingFaultHistoryElement.FAULT_CARD);
		} else if (value == JumpingCard.FAULT_2_4) {
			addToFaults(playerNum, 2, JumpingFaultHistoryElement.FAULT_CARD);
		} else if (value == JumpingCard.FAULT_3_4) {
			addToFaults(playerNum, 3, JumpingFaultHistoryElement.FAULT_CARD);
		} else if (value == JumpingCard.STABLE) {
			maxHandSize[playerNum] += 1;
		} else {
			// Dual Rider & Official cards can't be applied automatically.
			return false;
		}
		return true;
	}

	/**
	 * Add the given number of quarter fault points to the given player
	 *
	 * @param	playerNum		The player to apply the points to
	 * @param	quarterPoints	The number of quarter points to add
	 * @param	reasonCode		The reasonCode for adding points.
	 *
	 * RAW: Continue adding support for the JumpingFaultHistory.  Make all
	 *      changes to fault point go through this routine!
	 */
	public void addToFaults(int playerNum, int quarterPoints, int reasonCode) {
		quarterFaults[playerNum] = Math.max(0, quarterFaults[playerNum] + quarterPoints);

		if (quarterPoints != 0) {
			faultHistory[playerNum].add (
				new JumpingFaultHistoryElement(turnNumber, quarterPoints, reasonCode)
			);
		}

		refreshObservers();
	}

	/**
	 * Look through the immediate cards for the given seat and determine if there are
	 * any unmarked cards left it in.
	 */
	public boolean hasUnmarkedImmediateCards(int seatNum) {
		ListIterator iter = immediateHand[seatNum].listIterator();
		while (iter.hasNext()) {
			JumpingCard c = (JumpingCard) iter.next();
			if (!c.isMarked()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Look through the playable hand cards for the given seat and determine
	 * the number of marked cards in it.
	 */
	public int numMarkedCards(int seatNum) {
		int count = 0;
		ListIterator iter = playableHand[seatNum].listIterator();
		while (iter.hasNext()) {
			JumpingCard c = (JumpingCard) iter.next();
			if (c.isMarked()) {
				count += 1;
			}
		}
		return count;
	}

	/**
	 * For a given range of spaces, return the number of fences within that range.
	 *
	 * @param	from		The space to start counting at.
	 * @param	length		The number of spaces to count to.
	 * @return the number of fences within that range.
	 */
	public int countFencesInRange(int from, int length) {
		int to = Math.min((from + length), LAST_SPACE);
		int count = 0;

		for (int i = from; i <= to; i++) {
			if (fenceArray[i] != null) {
				count += 1;
			}
		}

		return count;
	}

	/**
	 * For a given range of spaces, return a vector that contains the fences
	 * that are within that range.
	 *
	 * @param	from		The space to start counting at.
	 * @param	length		The number of spaces to count to.
	 * @return a new vector of fences within that range.
	 */
	public Vector getFencesInRange(int from, int length) {
		int to = Math.min((from + length), LAST_SPACE);
		Vector result = new Vector();

		for (int i = from ; i <= to; i++) {
			if (fenceArray[i] != null) {
				result.add(fenceArray[i]);
			}
		}

		return result;
	}

	/**
	 * For a given range of spaces, determine if it is legal to jump between
	 * the given spaces.  Jumps are legal if:
	 *   * There is at least one fence covered by the jump.
	 *   * The last space has no fence.
	 *
	 * @param	from		The space to start checking.
	 * @param	length		The number of spaces to check.
	 * @return if the jump is legal.
	 */
	public boolean canJumpBetween(int from, int length) {
		int to = Math.min((from + length), LAST_SPACE);
		if (fenceArray[to] == null) {
			for (int i = from; i < to; i++) {
				if (fenceArray[i] != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * For a given space, return the fence that is located on that space.
	 *
	 * @param	space		The space to return the fence for.
	 * @return the fence on that space or null, if there is no fence on that space.
	 */
	public JumpingFence getFenceAt(int space) {
		return fenceArray[space];
	}

	/**
	 * Determine if there is a fence on the given space.
	 *
	 * @param	space	The space to check for a fence.
	 * @return if there is a fence or not.
	 */
	public boolean isFenceOn(int space) {
		return (fenceArray[space] != null);
	}

	/**
	 * Determine if the vector of jumps played by the given player
	 * is a valid move.
	 *
	 * Also, if the move is valid, then:
	 *		- This computes the effective locations for the cards
	 *		- <finalHorseLoc> is set to the final location of the horse
	 *		- <faultPointsScored> is set to the number of fault points accrued by this move.
	 *
	 * If the move is not valid, then the above are trashed.
	 *
	 * @param	playerId		The player who is trying to make the move
	 * @param	jumps			The jumps, in order, that the player is trying to play
	 * @return whether this is a valid move or not
	 */
	public boolean checkValidPlay(int playerId, Vector jumps) {

		if (jumps.size() == 0) {
			// A zero-length set of cards is legal
			return true;
		}

		boolean valid = true;
		faultPointsScored = 0;

		int horseLoc = horse_locations[playerId];

		ListIterator iter = jumps.listIterator();
		while (iter.hasNext()) {
			JumpingJump jump = (JumpingJump) iter.next();
			if (!jump.isCommitted()) {
				jump.setStartingSpace(horseLoc);

				faultPointsScored += jump.faultPoints();
				valid = valid & jump.isValid();

				horseLoc = jump.endSpace();
			}
		}

		finalHorseLoc = horseLoc;
		return valid;
	}

	/**
	 * Advance the horse on the given spot by the number of spaces.
	 *
	 * @param	currSpace	The space the horse is starting
	 * @param	advance		The number of spaces to advance
	 * @return the new spot the horse ends on.
	 */
	public int advanceHorse(int currSpace, int advance) {
		return Math.min(currSpace + advance, LAST_SPACE);
	}

	/**
	 * Commit a horse movement.  This will play a set of cards, removing
	 * them from the player's hand, and move the associated horse.  It will
	 * also assign fault points appropriately.
	 *
	 * @param	playerId		The player trying to make the move
	 * @param	jumps			The jumps to be played
	 * @returns true => Move was good.
	 *          false => Move was not good.
	 */
	public boolean commitHorseMovement(int playerId, Vector jumps) {
		// Only the current jump owner can commit the jump
		if (playerId != currJumpOwner) {
			return false;
		}

		// Can't commit to a play that doesn't move the horse.
		if (jumps.size() == 0) {
			return false;
		}

		// Verify that the play is good.  (and as a side effect, determine
		// where the player's horse ends (stored in finalHorseLoc)).
		if (!checkValidPlay(playerId, jumps)) {
			return false;
		}

		// Play was valid, now verify that the player has all of the cards being played
		if (!checkHasCards(playableHand[playerId], getAllCards(jumps, false))) {
			return false;
		}

		addToFaults(playerId, faultPointsScored * 4, JumpingFaultHistoryElement.MISSED_JUMP);
		removeMarkedCards(playableHand[playerId]);
		commitAllJumps(jumps);
		horse_locations[playerId] = finalHorseLoc;

		shadowHorseLoc = -1;	// When a play is committed, turn the shadow horse off

		return true;
	}

	/**
	 * Set all of the jumps in the given vector to the committed state.
	 */
	private void commitAllJumps(Vector jumps) {
		ListIterator iter = jumps.listIterator();
		while (iter.hasNext()) {
			JumpingJump jump = (JumpingJump) iter.next();
			jump.setCommitted(true);
		}
	}

	/**
	 * Create a single vector of all of the cards played in the given jumps.
	 *
	 * @param	jumps				The jumps to return the cards from
	 * @param	includeCommitted	true => Include cards from committed jumps
	 *								false => Exclude cards from committed jumps
	 */
	private Vector getAllCards(Vector jumps, boolean includeCommitted) {
		ListIterator iter = jumps.listIterator();
		Vector cards = new Vector();

		while(iter.hasNext()) {
			JumpingJump jump = (JumpingJump) iter.next();
			if (!jump.isCommitted() || includeCommitted) {
				jump.addCardsToVector(cards);
			}
		}
		return cards;
	}

	/**
	 * Check that the hand contains all of the cards of subset.
	 * The cards within hand that are in subset are left marked
	 *
	 * @param	hand		A hand of cards
	 * @param	subset		A set of cards that will be verified.
	 * @return true => Yes, all cards of subset are in hand.
	 */
	private boolean checkHasCards(Vector hand, Vector subset) {
		unmarkAllCards(hand);

		ListIterator iter = subset.listIterator();
		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			if (card.isFakeSaddle()) {
				card.setMarked(true);
			} else {
				// Only check for cards that aren't fake saddle cards.
				if (!findAndMark(card, hand)) {
					// Didn't find this card in hand, so bad.
					unmarkAllCards(hand);
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Unselect all of the cards in the given list.
	 */
	private void unmarkAllCards(Vector cards) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			((JumpingCard) iter.next()).setMarked(false);
		}
	}

	/**
	 * Find the given card in the given list and, if found, return it.
	 *
	 * @return true => Card found and marked
	 *         false => Card not found.
	 */
	private JumpingCard findActualUnmarkedCard(JumpingCard tgtCard, Vector cards) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			// Unknown cards will match with anything here (since if a player has
			// unknown cards in hand, then all cards will be unknown, and therefore
			// any card will match.)
			if ((card.equals(tgtCard) || !card.isKnown()) && !card.isMarked()) {
				return card;
			}
		}
		return null;
	}

	/**
	 * Find the given card in the given list and, if found, select it.
	 *
	 * @param tgtCard		An example card to find
	 * @param cards			The list of cards
	 * @return true => Card found and marked
	 *         false => Card not found.
	 */
	public boolean findAndMark(JumpingCard tgtCard, Vector cards) {
		try {
			findActualUnmarkedCard(tgtCard, cards).setMarked(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Find the given card in the given list and, if found, remove it.
 	 *
	 * @return true => Card found and removed
	 *         false => Card not found.
	 */
	private boolean removeACard(JumpingCard oldCard, Vector cards) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			if (card.equals(oldCard)) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove the selected cards from the given hand
	 */
	private void removeMarkedCards(Vector cards) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			if (card.isMarked()) {
				iter.remove();
			}
		}
	}

	/**
	 * Create a new vector that is made up of only the marked cards in
	 * given hand, and remove those cards from the original vector.
	 */
	public Vector getAndRemoveMarkedCards(Vector cards) {
		Vector newVec = new Vector();
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			if (card.isMarked()) {
				newVec.add(card);
				iter.remove();
			}
		}
		refreshObservers();

		return newVec;
	}

	/**
	 * Remove a card from a player's hand
	 */
	public void removeCardFromHand(int playerId, JumpingCard oldCard) {
		removeACard(oldCard, playableHand[playerId]);
		refreshObservers();
	}

	/**
	 * Remove all of the cards of the to-be-removed Vector from the hand
	 * of the given player.
	 */
	public void removeCardsFromHand(Vector toBeRemoved, int seatNum) {
		Vector hand = playableHand[seatNum];
		ListIterator iter = toBeRemoved.listIterator();
		while (iter.hasNext()) {
			removeACard((JumpingCard) iter.next(), hand);
		}
		refreshObservers();
	}

	/**
	 * Change the state of the activated cards
	 */
	public void setHandCardActivated(boolean newValue) {
		handCardActivated = newValue;
		refreshObservers();
	}

	public void setImmCardActivated(boolean newValue) {
		immCardActivated = newValue;
		refreshObservers();
	}

	/**
	 * Put the given cards into the dual rider slots
	 */
	public void setDualRiderCards(Vector newCards) {
		if (newCards.size() != 2) {
			System.out.println("Error: Got " + newCards.size() + " dual rider cards!");
			return;
		}

		dualRiderCards[0] = (JumpingCard) newCards.elementAt(0);
		dualRiderCards[1] = (JumpingCard) newCards.elementAt(1);
		refreshObservers();
	}

	/**
	 * Clear the dual rider cards.
	 */
	public void clearDualRiderCards() {
		dualRiderCards[0] = null;
		dualRiderCards[1] = null;
		refreshObservers();
	}

	/**
	 * Verify that the given two cards are the two dual rider cards.
	 *
	 * @param cardA, cardB	Two cards to check.
	 * @return if they are the dual rider cards or not.
	 */
	public boolean verifyDualRiderCards(JumpingCard cardA, JumpingCard cardB) {
		return ((cardA.equals(dualRiderCards[0]) && cardB.equals(dualRiderCards[1])) ||
		        (cardB.equals(dualRiderCards[0]) && cardA.equals(dualRiderCards[1])));
	}

	/**
	 * Set the number of cards to discard as a result of the official card
	 * to half of the given player's hand (rounded up).
	 *
	 * @param seatNum		The seat number of the player to set value for.
	 *						Use -1 as a seat number to clear the official discard count.
	 */
	public void setOfficialDiscards(int seatNum) {
		if (seatNum < 0) {
			officialDiscardNumber = -1;
		} else {
			officialDiscardNumber = (playableHand[seatNum].size() + 1) / 2;
		}
	}

	/**
	 * Return the actual immediate card in the given hand that matches the one
	 * provided.
	 *
	 * @param	seatNum			The seat number to look for
	 * @param	theCard			The card to look for
	 * @return the card in the immediate hand of the requested player that matches
	 *			the card given.
	 */
	public JumpingCard getActualImmCard(int seatNum, JumpingCard theCard) {
		return findActualUnmarkedCard(theCard, immediateHand[seatNum]);
	}

	/**
	 * Return the actual hand card in the given hand that matches the one
	 * provided.
	 *
	 * @param	seatNum			The seat number to look for
	 * @param	theCard			The card to look for
	 * @return the card in the playable hand of the requested player that matches
	 *			the card given.
	 */
	public JumpingCard getActualHandCard(int seatNum, JumpingCard theCard) {
		return findActualUnmarkedCard(theCard, playableHand[seatNum]);
	}

/*****************************************************************************************/
/* Deal with current jumps */

	/**
	 * Clear the current jumps vector in preparation to starting a new one.
	 * Two versions allow setting the new jump owner or just keeping the current one.
	 */
	public void clearCurrentJumps(int owner) {
		currJumps = new Vector();
		currJumpOwner = owner;
	}

	public void clearCurrentJumps() {
		currJumps = new Vector();
	}

	/**
	 * Get the cards in the current jump as a single vector
	 */
	public Vector getCurrentPlayedCards() { return getAllCards(currJumps, true); }

	/**
	 * Extract the uncommitted jumps from current jumps.
	 */
	public Vector getUncommittedCurrentJumps() {
		ListIterator iter = currJumps.listIterator();
		Vector unJumps = new Vector();

		while(iter.hasNext()) {
			JumpingJump jump = (JumpingJump) iter.next();
			if (!jump.isCommitted()) {
				unJumps.add(jump);
			}
		}
		return unJumps;
	}

	/**
	 * Get info about the current jumps
	 */
	public Vector getCurrJumps()          { return currJumps; }
	public int getCurrJumpOwner()         { return currJumpOwner; }
	public boolean arePlayedCardsValid()  { return checkValidPlay(currJumpOwner, currJumps); }

	/**
	 * Determine if the given card is playable, given the current jumps.
	 */
	public boolean isPlayableNow(JumpingCard newCard) {
		if (currJumps.size() == 0) {
			// If there are no jumps yet, then the only valid cards to
			// play are saddle or ribbon, which are the special cards
			return newCard.isSpecial();
		}

		JumpingJump j = (JumpingJump) currJumps.lastElement();
		return j.okToPlay(newCard);
	}

	/**
	 * Add a card to the current jumps.
	 *
	 * @param	newCard			The card to add to the current jump
	 * @return	true  => The card was added.
	 *			false => The card was not added.
	 */
	public boolean addCardToCurrentJumps(JumpingCard newCard) {
		if (newCard.isRibbon()) {
			handCardActivated = true;
			return true;
		} else {
			return addCardToJumps(newCard, currJumpOwner, currJumps);
		}
	}

	/**
	 * Add a card to the given jumps.
	 *
	 * @param	newCard			The card to add to jumps
	 * @param	playerId		The player who will be making the move
	 * @param	jumps			Vector of jumps to add to
	 * @return	true  => The card was added.
	 *			false => The card was not added.
	 */
	public boolean addCardToJumps(JumpingCard newCard, int playerId, Vector jumps) {
		if (!isPlayableNow(newCard)) {
			return false;
		}

		boolean addOk;

		if (newCard.isSpecial()) {
			// Only saddle cards come here.  (Ribbon cards are handled separately)
			// Start a new jump with this saddle card
			JumpingJump newJump;
			if (jumps.size() == 0) {
				newJump = new JumpingJump(newCard, horse_locations[playerId], this, false);
			} else {
				JumpingJump lastJump = (JumpingJump) jumps.lastElement();
				newJump = new JumpingJump(newCard, lastJump.endSpace(), this,
				                          lastJump.areFollowingCantersProhibited());
			}

			jumps.add(newJump);
			addOk = true;
		} else {
			JumpingJump currLastJump = (JumpingJump) jumps.lastElement();
			addOk = currLastJump.addCard(newCard);
		}

		return addOk;
	}

	/**
	 * Remove a card from the current jumps
	 *
	 * @param	oldCard			The card to remove from the current jump
	 * @return	true  => The card was removed.
	 *			false => The card was not removed.
	 */
	public boolean removeCardFromCurrentJumps(JumpingCard oldCard) {
		if (oldCard.isSpecial()) {
			// If this is a real saddle card, then we need to remove the
			// fake saddles that follow it, too.  So, removeJumpsForSaddleCard()
			// will return a vector of all saddle cards (real & fake) that
			// are associated with the old card.
			ListIterator iter = removeJumpsForSaddleCard(oldCard).listIterator();
			while (iter.hasNext()) {
				JumpingJump j = (JumpingJump) iter.next();
				// We need to un-play all of the cards in the jump
				unmarkAllCards(j.getAllCards());
			}
			reEvaluateCurrJump();
		} else if (!removeCardFromCurrJumps(oldCard)) {
			return false;
		}

		return true;
	}

	/**
	 * Remove all un-committed jumps from current jumps.
	 */
	public void removeUncommittedJumps() {
		ListIterator iter = currJumps.listIterator();
		while (iter.hasNext()) {
			JumpingJump j = (JumpingJump) iter.next();
			if (!j.isCommitted()) {
				unmarkAllCards(j.getAllCards());
				iter.remove();
			}
		}
		refreshObservers();
	}

	/**
	 * Remove the Jumps that are anchored by the given saddle card from the current jumps.
	 *
	 * @param	saddleCard	The saddle card that we're going to remove.
	 * @return a vector of the jumps that were removed
	 */
	private Vector removeJumpsForSaddleCard(JumpingCard oldCard) {
		Vector jumps = new Vector();
		int index = getIndexForJumpAtSaddleCard(oldCard);
		if (index >= 0) {
			// Remove this jump
			jumps.add(currJumps.elementAt(index));
			currJumps.remove(index);

			if (oldCard.isSaddle()) {
				// If we're removing a real saddle card, then we need to
				// keep removing jumps until we either reach the end or
				// another real saddle card.  (If we're removing a fake
				// saddle card, then we only need to remove the one jump,
				// so we're done...)
				while (currJumps.size() > index) {
					JumpingJump nextJump = (JumpingJump) currJumps.elementAt(index);
					if (nextJump.getSaddleCard().isSaddle()) {
						// Found another real saddle card, so we can quit here.
						return jumps;
					}
					jumps.add(nextJump);
					currJumps.remove(index);
				}
			}
		}

		return jumps;
	}

	/**
	 * Return the index into currJumps that corresponds to the jump that
	 * is anchored by the given saddle card.
	 *
	 * @param	saddleCard	The saddle card that we're looking for.
	 * @return the index of the jump that is anchored by that saddle card.
	 */
	private int getIndexForJumpAtSaddleCard(JumpingCard saddleCard) {
		ListIterator iter = currJumps.listIterator();
		while (iter.hasNext()) {
			JumpingJump j = (JumpingJump) iter.next();
			if (j.isSameSaddleCard(saddleCard)) {
				return iter.previousIndex();
			}
		}
		return -1;
	}

	/**
	 * Remove a card from the current jumps
	 *
	 * @param	oldCard			The card to remove from the current jump
	 * @return	true  => The card was removed.
	 *			false => The card was not removed.
	 */
	private boolean removeCardFromCurrJumps(JumpingCard oldCard) {
		ListIterator iter = currJumps.listIterator();
		while (iter.hasNext()) {
			JumpingJump j = (JumpingJump) iter.next();
			if (j.removeCard(oldCard)) {
				// Removed, so can stop here
				return true;
			}
		}
		// Didn't find the card in any of the jumps
		return false;
	}

	/**
	 * Reevaluate all of the cards in the current jump
	 */
	private void reEvaluateCurrJump() {
		int currSpace = horse_locations[currJumpOwner];

		ListIterator iter = currJumps.listIterator();
		while (iter.hasNext()) {
			JumpingJump j = (JumpingJump) iter.next();
			if (!j.isCommitted()) {
				j.setStartingSpace(currSpace);
			}
			currSpace = j.endSpace();
		}
	}

	/**
	 * Return the location of the horse that would result from committing
	 * the current jump.
	 *
	 * @return the location of the last jump in current jumps, if valid,
	 *         or -1 if the current jumps are not valid.
	 */
	public int getCurrJumpHorseLoc() {
		try {
			JumpingJump j = (JumpingJump) (currJumps.lastElement());
			if (j.isValid()) {
				return j.endSpace();
			}
		} catch (NoSuchElementException e) {
			// No jump in currJumps, so fall through to return -1.
		}

		return -1;
	}

	/**
	 * Set the location of the shadow horse location.  If the location
	 * has changed, then return true and update observers.
	 *
	 * @param	newLoc		The new horse location.
	 * @return if the new location is different than the old one.
	 */
	public boolean setShadowHorseLoc(int newLoc) {
		// If the new location is where the real horse is, then turn the
		// shadow horse off.
		if (newLoc == horse_locations[currJumpOwner]) {
			newLoc = -1;
		}

		boolean different = (newLoc != shadowHorseLoc);
		if (different) {
			shadowHorseLoc = newLoc;
			refreshObservers();
		}
		return different;
	}

	/**
	 * Get the current shadow horse location.
	 */
	public int getShadowHorseLoc () {
		return shadowHorseLoc;
	}

/*****************************************************************************************/

	/**
	 * Determine if the game is over.  If it is over, then set the model to that
	 * state.
	 */
	public boolean checkGameOver() {

		if ((horse_locations[0] == LAST_SPACE) || (horse_locations[1] == LAST_SPACE)) {
			// If one of the horses is on the last track space, then the game is
			// over and the other player gets penalties for how far back he is.
			setGameOver();
			addToFaults(0, LAST_SPACE - horse_locations[0], JumpingFaultHistoryElement.BEHIND_AT_END_OF_GAME);
			addToFaults(1, LAST_SPACE - horse_locations[1], JumpingFaultHistoryElement.BEHIND_AT_END_OF_GAME);
		} else if ((quarterFaults[0] >= 16) || (quarterFaults[1] >= 16)) {
			// If one of the players has gone over 16 quarter faults (4 full faults)
			// then the game is over.
			setGameOver();
		}

		if (isGameOver()) {
			// Need to create resultArray and scoreString for the result of the game
			resultArray[0] = winnerCode(0);
			resultArray[1] = winnerCode(1);

			int maxScore = Math.max(quarterFaults[0], quarterFaults[1]);
			scoreString = (maxScore - quarterFaults[0]) + " " + (maxScore - quarterFaults[1]);

			refreshObservers();
		}

		return isGameOver();
	}

	/**
	 * This will set the model into the correct game over state.
	 */
	public void setGameOver() {
		changePhase(GAME_OVER);
		currentSorter = -1;
	}

	/*
	 * Return a winner code (IGameOver.LOSE, IGameOver.WIN or IGameOver.DRAW) for the
	 * player at the given seat.
	 */
	private int winnerCode(int seatNum) {
		int otherSeatNum = (1-seatNum);
		int diff = quarterFaults[seatNum] - quarterFaults[otherSeatNum];
		if (diff > 0) {
			return IGameOver.LOSE;
		} else if (diff < 0) {
			return IGameOver.WIN;
		} else {
			// In case of a tie of points, the winner is the one that got to the end
			if ((horse_locations[seatNum] == LAST_SPACE) &&
			    (horse_locations[otherSeatNum] != LAST_SPACE)) {
				return IGameOver.WIN;
			} else if ((horse_locations[seatNum] != LAST_SPACE) &&
			           (horse_locations[otherSeatNum] == LAST_SPACE)) {
				return IGameOver.LOSE;
			} else {
				return IGameOver.DRAW;
			}
		}
	}

/*****************************************************************************************/
/* Save/restore game state methods */

	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_TURN_NUMBER = "turn";
	private static final String XML_ATT_HORSE_LOCATIONS = "horses";
	private static final String XML_ATT_SORT0 = "sort0";
	private static final String XML_ATT_SORT1 = "sort1";
	private static final String XML_ATT_HAND0 = "hand0";
	private static final String XML_ATT_HAND1 = "hand1";
	private static final String XML_ATT_HAND0_MARK = "hand0_mark";
	private static final String XML_ATT_HAND1_MARK = "hand1_mark";
	private static final String XML_ATT_IMM_HAND0 = "imm0";
	private static final String XML_ATT_IMM_HAND1 = "imm1";
	private static final String XML_ATT_IMM_HAND0_MARK = "imm0_mark";
	private static final String XML_ATT_IMM_HAND1_MARK = "imm1_mark";
	private static final String XML_ATT_FAULTS = "faults";
	private static final String XML_ATT_MAX_HAND_SIZE = "handSize";
	private static final String XML_ATT_CURR_JUMP = "jump";
	private static final String XML_ATT_CURR_JUMP_OWNER = "jumpOwner";
	private static final String XML_ATT_DUAL_RIDER0 = "dualRider0";
	private static final String XML_ATT_DUAL_RIDER1 = "dualRider1";
	private static final String XML_ATT_OFFICIAL_DISCARD = "offDisc";
	private static final String XML_ATT_GAMEPHASE = "phase";
	private static final String XML_ATT_CURR_SORTER = "sorter";
	private static final String XML_ATT_SHADOW_LOC = "shadow";

	/**
	 * Set the model state from the contents of the message.  This is used to
	 * decode the message sent from the server when attaching so that the
	 * client gets the current state of the game, even if attaching in the middle
	 * of a game.
	 *
	 * @param message    Message from the server
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {
		// Reset the game back to the starting value before using the
		// message to fill it in.
		private_ResetGame();

		// Pull all of the bits out of the message
		horse_locations = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_HORSE_LOCATIONS));
		sortCards[0] = convertToCardVector(message.getStringAttribute(XML_ATT_SORT0));
		sortCards[1] = convertToCardVector(message.getStringAttribute(XML_ATT_SORT1));
		playableHand[0] = convertToCardVector(message.getStringAttribute(XML_ATT_HAND0));
		playableHand[1] = convertToCardVector(message.getStringAttribute(XML_ATT_HAND1));
		immediateHand[0] = convertToCardVector(message.getStringAttribute(XML_ATT_IMM_HAND0));
		immediateHand[1] = convertToCardVector(message.getStringAttribute(XML_ATT_IMM_HAND1));
		markCardsFromVector(immediateHand[0], message.getStringAttribute(XML_ATT_IMM_HAND0_MARK));
		markCardsFromVector(immediateHand[1], message.getStringAttribute(XML_ATT_IMM_HAND1_MARK));
		quarterFaults = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_FAULTS));
		maxHandSize = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_MAX_HAND_SIZE));
		currJumpOwner = message.getIntAttribute(XML_ATT_CURR_JUMP_OWNER);
		officialDiscardNumber = message.getIntAttribute(XML_ATT_OFFICIAL_DISCARD);
		gamePhase = message.getIntAttribute(XML_ATT_GAMEPHASE);
		currentSorter = message.getIntAttribute(XML_ATT_CURR_SORTER);
		shadowHorseLoc = message.getIntAttribute(XML_ATT_SHADOW_LOC);
		turnNumber = message.getIntAttribute(XML_ATT_TURN_NUMBER);

		createCurrentJumps(message.getStringAttribute(XML_ATT_CURR_JUMP));

		String potentialDualRider0 = message.getStringAttribute(XML_ATT_DUAL_RIDER0);
		if ("x".equals(potentialDualRider0)) {
			dualRiderCards[0] = null;
			dualRiderCards[1] = null;
		} else {
			dualRiderCards[0] = JumpingCard.fromString(potentialDualRider0);
			dualRiderCards[1] = JumpingCard.fromString(message.getStringAttribute(XML_ATT_DUAL_RIDER1));
		}

		// Calculate things that aren't explicitly sent in the message
		if (openHands) {
			markCardsFromVector(playableHand[0], message.getStringAttribute(XML_ATT_HAND0_MARK));
			markCardsFromVector(playableHand[1], message.getStringAttribute(XML_ATT_HAND1_MARK));
		}



		// Call out to the client model so that it can set it's state as well...
		setClientModelState(message);

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

	/**
	 * The client model that extends this class should override this method to
	 * get called whenever the core model is updated.  This allows the client
	 * model to update things that he knows about that the core model doesn't.
	 */
	protected void setClientModelState(XMLElement message) {}

	/**
	 * Used to bundle up the state of the model.  This is used so that when
	 * a client attaches, it gets the current state of the board from the
	 * server.  This allows an observer to attach to a game in progress and
	 * get the up-to-date values.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		// Add attributes for all of the elements of the game
		state.setIntAttribute(XML_ATT_TURN_NUMBER, turnNumber);
		state.setAttribute(XML_ATT_HORSE_LOCATIONS, JogreUtils.valueOf(horse_locations));
		state.setAttribute(XML_ATT_SORT0, cardVectorToString(sortCards[0]));
		state.setAttribute(XML_ATT_SORT1, cardVectorToString(sortCards[1]));
		state.setAttribute(XML_ATT_IMM_HAND0, cardVectorToString(immediateHand[0]));
		state.setAttribute(XML_ATT_IMM_HAND1, cardVectorToString(immediateHand[1]));
		state.setAttribute(XML_ATT_IMM_HAND0_MARK, cardVectorToMarkString(immediateHand[0]));
		state.setAttribute(XML_ATT_IMM_HAND1_MARK, cardVectorToMarkString(immediateHand[1]));
		state.setAttribute(XML_ATT_FAULTS, JogreUtils.valueOf(quarterFaults));
		state.setAttribute(XML_ATT_MAX_HAND_SIZE, JogreUtils.valueOf(maxHandSize));
		state.setAttribute(XML_ATT_CURR_JUMP, cardVectorToString(getAllCards(currJumps, false)));
		state.setIntAttribute(XML_ATT_CURR_JUMP_OWNER, currJumpOwner);
		state.setIntAttribute(XML_ATT_OFFICIAL_DISCARD, officialDiscardNumber);
		state.setIntAttribute(XML_ATT_GAMEPHASE, gamePhase);
		state.setIntAttribute(XML_ATT_CURR_SORTER, currentSorter);
		state.setIntAttribute(XML_ATT_SHADOW_LOC, shadowHorseLoc);

		if (openHands) {
			// If open hands, then send the real cards
			state.setAttribute(XML_ATT_HAND0, cardVectorToString(playableHand[0]));
			state.setAttribute(XML_ATT_HAND1, cardVectorToString(playableHand[1]));
			state.setAttribute(XML_ATT_HAND0_MARK, cardVectorToMarkString(playableHand[0]));
			state.setAttribute(XML_ATT_HAND1_MARK, cardVectorToMarkString(playableHand[1]));
			if (dualRiderCards[0] != null) {
				state.setAttribute(XML_ATT_DUAL_RIDER0, dualRiderCards[0].toString());
				state.setAttribute(XML_ATT_DUAL_RIDER1, dualRiderCards[1].toString());
			} else {
				state.setAttribute(XML_ATT_DUAL_RIDER0, "x");
				state.setAttribute(XML_ATT_DUAL_RIDER1, "x");
			}
		} else {
			// If not open hands, then send unknown cards
			state.setAttribute(XML_ATT_HAND0, makeNUnknownCards(playableHand[0].size()));
			state.setAttribute(XML_ATT_HAND1, makeNUnknownCards(playableHand[1].size()));
			if (dualRiderCards[0] != null) {
				state.setAttribute(XML_ATT_DUAL_RIDER0, unknownCard.toString());
				state.setAttribute(XML_ATT_DUAL_RIDER1, unknownCard.toString());
			} else {
				state.setAttribute(XML_ATT_DUAL_RIDER0, "x");
				state.setAttribute(XML_ATT_DUAL_RIDER1, "x");
			}
		}

		return state;
	}

	/**
	 * Make a string the represents N unknown cards.
	 *
	 * @param	n	The number of unknown cards to make.
	 * @return the string.
	 */
	private String makeNUnknownCards(int n) {
		String str = "";

		for (int i=0; i<n; i++) {
			str = str + " " + unknownCard.toString();
		}

		return str;
	}

	/**
	 * Convert a string of card items into a vector of cards
	 *
	 * @param	cardString	A string of space-separated card info
	 * @return a vector of cards from that string
	 */
	private Vector convertToCardVector (String cardString) {
		String [] items = JogreUtils.convertToStringArray(cardString);
		Vector newCards = new Vector();

		int i = 0;
		while (i < items.length) {
			newCards.add(JumpingCard.fromString(items[i], items[i+1]));
			i += 2;
		}

		return newCards;
	}

	/**
	 * Convert a vector of cards into a string that represents them
	 *
	 * @param	cards	A vector of cards
	 * @return a string to represent the cards in the vector
	 */
	private String cardVectorToString(Vector cards) {
		String str = "";

		for (int i=0; i<cards.size(); i++) {
			str = str + " " + ((JumpingCard) cards.get(i)).toString();
		}

		return str;
	}

	/**
	 * Create the current jumps from the given string representation.
	 */
	private void createCurrentJumps(String theString) {
		// Clear the current jumps vector;
		clearCurrentJumps();

		// Convert the string into a vector of cards...
		Vector cards = convertToCardVector(theString);

		// ... and play each card in turn to the current jumps
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard theCard = (JumpingCard) iter.next();
			addCardToCurrentJumps(theCard);
		}
	}

	/**
	 * Create a string that contains the index of all of the cards in the vector
	 * that are selected.
	 */
	private String cardVectorToMarkString(Vector cards) {
		String outStr = "";
		for (int i = 0; i < cards.size(); i++) {
			JumpingCard card = (JumpingCard) cards.get(i);
			if (card.isMarked()) {
				outStr = outStr + " " + i;
			}
		}

		return outStr;
	}

	/**
	 * Given a vector of cards and a string of numbers, this will
	 * read through the numbers and mark the card in the vector
	 * at the index of the entry number.
	 */
	private void markCardsFromVector(Vector cards, String indexString) {
		int [] indexArray = JogreUtils.convertToIntArray(indexString);

		for (int i = 0; i < indexArray.length; i++) {
			JumpingCard card = (JumpingCard) cards.get(indexArray[i]);
			card.setMarked(true);
		}
	}
}