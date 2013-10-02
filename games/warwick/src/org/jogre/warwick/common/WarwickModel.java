/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
 * Copyright (C) 2004 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.warwick.common;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Game model for the warwick game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickModel extends JogreModel {

	// The number of players in the game
	protected int numPlayers;

	// The number of pieces that each player still has to place on the board
	protected int [] piecesToPlace;

	// The score of each player
	protected int [] score;

	// The owner numbers of the pieces placed on the board
	// Organized as [region][space]
	protected int [][] owners = new int [4][15];

	// The allegience of each player for this turn
	protected int [] allegience;
	public static final int ALLEGIENCE_UNKNOWN = 5;

	// Indication of whether this player has chosen his allegience yet.
	protected boolean [] allegienceChosen;

	// The current active player
	protected int activePlayerSeatNum;

	// The phase of the game
	protected int phase;
	public static final int PRE_GAME = 0;
	public static final int CHOOSE_ALLEGIENCE = 1;
	public static final int POST_ALLEGIENCE = 2;
	public static final int PLACE_PIECE = 3;
	public static final int SLIDE_PIECE = 4;
	public static final int AWAIT_SCORING = 5;
	public static final int GAME_OVER = 6;

	// The current round number.
	// The game consists of one round per player in the game.
	protected int currentRound;

	// The two highest valued pieces for each player.
	// (Used during scoring)
	protected int [][] bestPiece;

	// The points gained in the last round for a player's own pieces and his
	// ally's pieces, and who his ally was.
	protected int [] ownScore;
	protected int [] allyScore;
	protected int [] lastAlly;

	// The location of the piece that was last placed on the board.
	protected int lastPlayedRegion, lastPlayedSpace;

	// The location of the slide that was last done on the board.
	protected int lastSlideFromRegion, lastSlideFromSpace;
	protected int lastSlideToRegion, lastSlideToSpace;

	/**
	 * Constructor which creates the model.
	 *
	 * @param numPlayers    The number of players in this game.
	 */
	public WarwickModel (int numPlayers) {
		super (GAME_TYPE_TURN_BASED);

		this.numPlayers = numPlayers;
		this.piecesToPlace    = new int [numPlayers];
		this.score            = new int [numPlayers];
		this.ownScore         = new int [numPlayers];
		this.allyScore        = new int [numPlayers];
		this.allegience       = new int [numPlayers];
		this.lastAlly         = new int [numPlayers];
		this.allegienceChosen = new boolean [numPlayers];
		this.bestPiece        = new int [numPlayers][2];

		reset (PRE_GAME);
	}

	/**
	 * Reset the game back to the initial conditions.
	 */
	public void reset () {
		reset (CHOOSE_ALLEGIENCE);
	}

	/**
	 * Reset the game back to the initial conditions.
	 *
	 * @param initialPhase   The initial phase to put the game into.
	 */
	private void reset (int initialPhase) {
		// Clear player info.
		for (int i=0; i < numPlayers; i++) {
			score[i] = 0;
			ownScore[i] = 0;
			allyScore[i] = 0;
			allegience[i] = ALLEGIENCE_UNKNOWN;
			lastAlly[i] = ALLEGIENCE_UNKNOWN;
			allegienceChosen[i] = false;
			piecesToPlace[i] = 3;
		}

		//              __
		// for (int i=\/-1; i <numPlayers; i++)

		clearBoard ();

		// Reset various stuff
		currentRound = 0;
		activePlayerSeatNum = 0;
		phase = initialPhase;
		refreshObservers();
	}

	/**
	 * Return the number of players in the game.
	 */
	public int getNumPlayers () {
		return numPlayers;
	}

	/**
	 * Return the current round number.
	 */
	public int getCurrentRoundNumber () {
		return currentRound;
	}

	/**
	 * Return the score for a given player.
	 *
	 * @param seatNum    The seat number of the player whose score is desired.
	 * @return the current score for the given player.
	 */
	public int getScore (int seatNum) {
		return score[seatNum];
	}

	/**
	 * Return the score gained in the last round for a player's own pieces.
	 *
	 * @param seatNum    The seat number of the player whose score is desired.
	 * @return the score gained in the last round for a player's own pieces.
	 */
	public int getLastOwnScore (int seatNum) {
		return ownScore[seatNum];
	}

	/**
	 * Return the score gained in the last round for a player's ally's pieces.
	 *
	 * @param seatNum    The seat number of the player whose score is desired.
	 * @return the score gained in the last round for a player's ally's pieces.
	 */
	public int getLastAllyScore (int seatNum) {
		return allyScore[seatNum];
	}

	/**
	 * Return the allegience that a player had chosen in the last round.
	 *
	 * @param seatNum    The seat number of the player whose allegience is desired.
	 * @return the allegience of that player chosen in the last round.
	 */
	public int getLastAllegience (int seatNum) {
		return lastAlly[seatNum];
	}

	/**
	 * Return the number of pieces a player has yet to place on the board.
	 *
	 * @param seatNum    The seat number of the player whose number of pieces
	 *                   yet to be played is desired.
	 * @return the number of pieces to be played by the given player.
	 */
	public int getPiecesToPlace (int seatNum) {
		return piecesToPlace[seatNum];
	}

	/**
	 * Return the allegiece of a player.
	 *
	 * @param seatNum    The seat number of the player whose allegience is desired.
	 * @return the allegience of the given player.
	 */
	public int getAllegience (int seatNum) {
		return allegience[seatNum];
	}

	/**
	 * Return if the player has chosen an allegience.
	 *
	 * @param seatNum    The seat number of the player whose allegience is wanted
	 *                   to be determined.
	 * @return whether this player has chosen his allegience yet or not.
	 */
	public boolean isAllegienceChosen (int seatNum) {
		return allegienceChosen[seatNum];
	}

	/**
	 * Return the seat number of the current active player.
	 *
	 * @return the seat number of the current active player, or ALL_PLAYERS
	 *         if it is allegience selection time.
	 */
	public int getActivePlayerSeatNum () {
		return activePlayerSeatNum;
	}

	/**
	 * Return information about the phase of the game.
	 */
	public int getGamePhase ()           { return phase; }
	public boolean isPreGame ()          { return (phase == PRE_GAME); }
	public boolean isChooseAllegience () { return (phase == CHOOSE_ALLEGIENCE); }
	public boolean isPlacePiece ()       { return (phase == PLACE_PIECE); }
	public boolean isSlidePiece ()       { return (phase == SLIDE_PIECE); }
	public boolean isAwaitScoring ()     { return (phase == AWAIT_SCORING); }
	public boolean isGameOver ()         { return (phase == GAME_OVER); }

	/**
	 * Change the phase of the game to the given one.
	 *
	 * @param newPhase    The new phase to set the game in.
	 */
	public void setGamePhase (int newPhase) {
		phase = newPhase;
		refreshObservers();
	}

	/**
	 * Return the owner of a given space on the board.
	 */
	public int getOwnerAt (int region, int space) {
		return owners[region][space];
	}

	/**
	 * Return if a space on the board is empty or not.
	 */
	public boolean spaceEmpty (int region, int space) {
		return owners[region][space] < 0;
	}

	/**
	 * Clear all of the pieces off of the board and put them back into the
	 * player's "to be played" stack.
	 */
	public void clearBoard () {
		// Clear the board of pieces
		for (int region=0; region < 4; region++) {
			for (int space=0; space < 15; space++) {
				owners[region][space] = -1;
			}
		}

		// Put the pieces back in the "to be played" stack
		for (int p = 0; p < numPlayers; p++) {
			piecesToPlace[p] = 3;
		}

		// Clear the "last played" locations
		lastPlayedRegion = -1;
		lastPlayedSpace = -1;
		lastSlideFromRegion = -1;
		lastSlideFromSpace = -1;
		lastSlideToRegion = -1;
		lastSlideToSpace = -1;

		refreshObservers();
	}

	/**
	 * Place a new piece on the board
	 *
	 * @param owner    The player whose piece this belongs to.
	 * @param region   The region to place the piece at.
	 * @param space    The space within the region to place the piece.
	 * @return if this is a valid move or not.
	 */
	public boolean addPiece (int owner, int region, int space) {
		// Make sure the space is empty that we're moving to and that the
		// player has pieces to place and is the active player
		if ((owner != activePlayerSeatNum) || // Not this player's turn
		    (region < 0) || (region > 3)   || // Not a valid board space
		    (space < 0) || (space > 14)    ||
		    (owners[region][space] >= 0)   || // Space is not empty
		    (piecesToPlace[owner] <= 0)    || // Player has no pieces left to play
		    (phase != PLACE_PIECE)) {         // Not time to place piece
			return false;
		}

		// Place the piece
		owners[region][space] = owner;

		lastPlayedRegion = region;
		lastPlayedSpace = space;

		piecesToPlace[owner] -= 1;

		refreshObservers();
		return true;
	}

	/**
	 * Return the location of the last piece that was played on the board.
	 */
	public int getLastPlacedRegion () { return lastPlayedRegion; }
	public int getLastPlacedSpace ()  { return lastPlayedSpace; }

	/**
	 * Return the location of the last piece that was slid on the board.
	 */
	public int getLastSlideRegion () { return lastSlideToRegion; }
	public int getLastSlideSpace ()  { return lastSlideToSpace; }

	/**
	 * Have a player choose an allegience and change the value of the
	 * allegience, if it is known.
	 *
	 * @param seatNum        The player who is chosing the allegience.
	 * @param newAllegience  The allegience being chosen.
	 */
	public void chooseAllegience (int seatNum, int newAllegience) {
		allegienceChosen[seatNum] = true;

		if (newAllegience != ALLEGIENCE_UNKNOWN) {
			allegience[seatNum] = newAllegience;
		}

		refreshObservers();
	}

	/**
	 * Have a player choose an allegience and set the allegience of everyone
	 * else to unknown.
	 *
	 * @param seatNum        The player who is chosing the allegience.
	 * @param newAllegience  The allegience being chosen.
	 */
	public void chooseAllegienceClearOthers (int seatNum, int newAllegience) {
		// Clear everyone's allegience
		for (int i = 0; i < allegience.length; i++) {
			allegience[i] = ALLEGIENCE_UNKNOWN;
		}

		// Set the one given to us.
		allegience[seatNum] = newAllegience;
		allegienceChosen[seatNum] = true;

		refreshObservers();
	}

	/**
	 * Determine if all players have chosen their allegience yet.
	 */
	public boolean allChosenAllegience () {
		for (int i = 0; i < allegienceChosen.length; i++) {
			if (!allegienceChosen[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reset the allegienceChosen array to false, except for the one given
	 * player, who has had his allegience set.
	 *
	 * @param exceptPlayer    The player whose allegience is not to be cleared
	 */
	public void resetAllegiences (int exceptPlayer) {
		for (int i = 0; i < allegienceChosen.length; i++) {
			allegienceChosen[i] = (i == exceptPlayer);
		}
	}

	// The map of which region is next to which other region on the board.
	private int [] nextRegion = {2, 3, 1, 0};

	/**
	 * Slide a piece on the board.
	 *
	 * @param fromRegion, fromSpace   The place where the piece is sliding from.
	 * @param toRegion, toSpace       The place where the piece is sliding to.
	 * @return if this is a legal move or not.
	 */
	public boolean slidePiece (int fromRegion, int fromSpace, int toRegion, int toSpace) {
		// Verify this is a valid move
		if ((fromRegion < 0) || (fromRegion > 3) || // Not a valid board space
		    (fromSpace < 0)  || (fromSpace > 14) ||
		    (toRegion < 0)   || (toRegion > 3)   ||
		    (toRegion < 0)   || (toSpace > 14)   ||
		    (owners[fromRegion][fromSpace] < 0)  || // No piece moving from
		    (owners[toRegion][toSpace] >= 0)     || // Not empty space moving to
		    (toRegion != nextRegion[fromRegion]) || // Not sliding one region clockwise
		    (phase != SLIDE_PIECE)) {               // Not time to slide piece
			return false;
		}

		// Slide the piece
		owners[toRegion][toSpace] = owners[fromRegion][fromSpace];
		owners[fromRegion][fromSpace] = -1;

		// RAW: May not need lastSlideFromRegion... (Delete if not used.)
		lastSlideFromRegion = fromRegion;
		lastSlideFromSpace = fromSpace;
		lastSlideToRegion = toRegion;
		lastSlideToSpace = toSpace;

		// If the piece that was just slid was the same piece that was just
		// played on the board, then clear the last played region so that
		// the display doesn't highlight an empty space where the played
		// piece used to be.
		if ((lastSlideFromRegion == lastPlayedRegion) &&
		    (lastSlideFromSpace == lastPlayedSpace)) {
			lastPlayedRegion = -1;
			lastPlayedSpace = -1;
		}

		refreshObservers();
		return true;
	}

	/**
	 * Advance to the next player as the active player.
	 *
	 * @return the seat number of the new player
	 */
	public int setNextPlayer () {
		return setNextPlayer (activePlayerSeatNum + 1);
	}

	/**
	 * Set the active player to the given one.
	 *
	 * @param nextPlayer    The seat number of the player to be the next player.
	 * @return the seat number of the new player
	 */
	public int setNextPlayer (int nextPlayer) {
		activePlayerSeatNum = nextPlayer % numPlayers;
		refreshObservers();
		return activePlayerSeatNum;
	}

	/**
	 * Score the current state of the board.
	 *
	 * @return if the game is over or not.
	 */
	public boolean updateScores () {
		findBestPieces();

		// Add to each player's score
		for (int p = 0; p < numPlayers; p++) {
			ownScore[p]  = bestPiece[p][0] + bestPiece[p][1];
			allyScore[p] = bestPiece[allegience[p]][0] * bestPiece[allegience[p]][1];
			score[p] += ownScore[p] + allyScore[p];
			lastAlly[p] = allegience[p];
		}

		// Clear everyone's allegience state
		resetAllegiences(-1);

		currentRound += 1;
		return (currentRound == numPlayers);
	}

	/*
	 * Find each player's top two pieces on the board.
	 */
	private void findBestPieces () {
		// Look through the board finding pieces, and when one is found, shift
		// it's value in.  Since we go through the regions from least value to
		// best, we will end with the two best in the array.
		for (int r = 0; r < 4; r++) {
			for (int s = 0; s < 15; s++) {
				int owner = owners[r][s];
				if (owner >= 0) {
					bestPiece[owner][1] = bestPiece[owner][0];
					bestPiece[owner][0] = r + 1;
				}
			}
		}
	}

/**************************************************************************/
/* Save/restore game state methods */

	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_BOARD_PIECES = "boardPieces";
	private static final String XML_ATT_SCORES = "scores";
	private static final String XML_ATT_ALLY_CHOSEN = "allyChosen";
	private static final String XML_ATT_ACTIVE_PLAYER = "activePlayer";
	private static final String XML_ATT_PHASE = "phase";
	private static final String XML_ATT_ROUND = "round";
	private static final String XML_ATT_OWN_SCORE = "ownScore";
	private static final String XML_ATT_ALLY_SCORE = "allyScore";
	private static final String XML_ATT_LAST_ALLY = "lastAlly";

	/**
	 * Method which reads in XMLElement and sets the state of the models fields.
	 * This method is necessary for other users to join a game and have the
	 * state of the game set properly.
	 *
	 * @param message    Data stored in message.
	 */
	public void setState (XMLElement message) {
		// Wipe everything
		reset();

		// Pull items out of the message
		owners = JogreUtils.convertTo2DArray(
		                  JogreUtils.convertToIntArray(
		                     message.getStringAttribute(XML_ATT_BOARD_PIECES)),
		                  4, 15);
		score            = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_SCORES));
		allegienceChosen = JogreUtils.convertToBoolArray(message.getStringAttribute(XML_ATT_ALLY_CHOSEN));
		activePlayerSeatNum = message.getIntAttribute(XML_ATT_ACTIVE_PLAYER);
		phase               = message.getIntAttribute(XML_ATT_PHASE);
		currentRound        = message.getIntAttribute(XML_ATT_ROUND);
		ownScore  = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_OWN_SCORE));
		allyScore = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_ALLY_SCORE));
		lastAlly  = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_LAST_ALLY));

		// Compute things that depend on the items given in the message
		for (int r = 0; r < 4; r++) {
			for (int s = 0; s < 15; s++) {
				int owner = owners[r][s];
				if (owner >= 0) {
					piecesToPlace[owner] -= 1;
				}
			}
		}

		// If everything is read sucessfully then refresh observers
		refreshObservers();
	}

	/**
	 * Implementation of a warwick game - This is stored on the server and is used
	 * when a player visits a game and a game is in progress.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		state.setAttribute(XML_ATT_BOARD_PIECES,
		                      JogreUtils.valueOf(JogreUtils.convertTo1DArray(owners)));
		state.setAttribute(XML_ATT_SCORES, JogreUtils.valueOf(score));
		state.setAttribute(XML_ATT_ALLY_CHOSEN, JogreUtils.valueOf(allegienceChosen));
		state.setIntAttribute(XML_ATT_ACTIVE_PLAYER, activePlayerSeatNum);
		state.setIntAttribute(XML_ATT_PHASE, phase);
		state.setIntAttribute(XML_ATT_ROUND, currentRound);
		state.setAttribute(XML_ATT_OWN_SCORE, JogreUtils.valueOf(ownScore));
		state.setAttribute(XML_ATT_ALLY_SCORE, JogreUtils.valueOf(allyScore));
		state.setAttribute(XML_ATT_LAST_ALLY, JogreUtils.valueOf(lastAlly));

		return state;
	}
}
