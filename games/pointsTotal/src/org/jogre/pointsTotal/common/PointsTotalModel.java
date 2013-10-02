/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.pointsTotal.common;

import nanoxml.XMLElement;

import java.util.Enumeration;

import java.awt.Point;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Game model for the pointsTotal game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalModel extends JogreModel {

	// The board that is played on.
	// The board is 6x6 to allow a hidden ring around the real board.
	// This hidden ring is set to values of 0 and never changed.  This allows
	// the code that scores plays to not worry about going out-of-bounds
	// when adding the values of neighbor pieces.
	private PointsTotalPiece [][] board = new PointsTotalPiece [6][6];

    // The values of an empty board
    private static final int [][] initialBoardValues = {
	    {0,0,0,0,0,0},
	    {0,2,1,1,2,0},
	    {0,1,0,0,1,0},
	    {0,1,0,0,1,0},
	    {0,2,1,1,2,0},
	    {0,0,0,0,0,0}
	};

	// The owner of a board space with no pieces.
	public final static int OWNER_NONE = -1;

	// Which pieces each player still has available
	// (availToPlay[playerNum][pointvalue])
	private boolean availToPlay[][];

	// The score of all of the players
	private int [] score;

	// The maximum & minimum scores of all scores.
	private int maxScore;
	private int minScore;

	// The number of players in this game.
	private int numberOfPlayers;

	// The current player
	private int currPlayer = -1;

	// The player who played first this round
	private int firstPlayer = 0;

	// The # of turns left in the current round
	protected int turnsInRound = 0;

	// The place where the last play was.  This is used to highlight it on
	// the board.
	private Point lastMovePoint = null;

    /**
     * Constructor which creates the model.
     */
    public PointsTotalModel (int numPlayers) {
        super (GAME_TYPE_TURN_BASED);

        // Save parameters
        numberOfPlayers = numPlayers;

        // Create the pieces of the board.
        for (int i=0; i<board.length; i++) {
	        for (int j=0; j<board[0].length; j++) {
		        board[i][j] = new PointsTotalPiece(OWNER_NONE, 0, 0);
	        }
        }

        // Create various arrays
        availToPlay = new boolean [numPlayers][4];
        score = new int [numPlayers];

        reset();
    }


    /**
     * Reset the game to get ready for a new game.
     */
    public void reset () {
		// Reset the board to all empty
		resetBoardToEmpty();
		resetAvailToPlay();
		lastMovePoint = null;

		// Reset misc. stuff
		currPlayer = 0;
		firstPlayer = 0;
	    turnsInRound = 4 * numberOfPlayers;
	    maxScore = 0;
	    minScore = 0;

	    // Reset the score.
	    for (int p = 0; p < numberOfPlayers; p++) {
		    score[p] = 0;
	    }

        // inform any graphical observers
        refreshObservers();
    }

	/*
	 * Reset the spaces on the board to the initial configuration.
	 */
    private void resetBoardToEmpty() {
        for (int i = 0; i < board.length; i++) {
	        for (int j = 0; j < board[0].length; j++) {
		        board[i][j].setOwner(OWNER_NONE);
		        board[i][j].setValue(initialBoardValues[i][j]);
		        board[i][j].setRotation(0);
	        }
        }
    }

    /*
     * Reset the availToPlay array so that all pieces are available again.
     */
    private void resetAvailToPlay() {
	    for (int i = 0; i < availToPlay.length; i++) {
		    for (int j = 0; j < availToPlay[0].length; j++) {
			    availToPlay[i][j] = true;
		    }
	    }
    }

    /**
     * Return the piece at the given location on the board.
     *
     * @param (col, row)   The column & row of the piece.
     * @return the piece at that col, row.
     */
    public PointsTotalPiece getPiece(int col, int row) {
	    // Note: Add 1 to col & row to skip the ring around the real board.
	    return (board[col+1][row+1]);
    }

    /**
     * Determine if the given player has the given piece available.
     *
     * @param  playerSeat    The seat number of the requested player
     * @param  value         The value the piece to check.
     * @return if the given player can still play the given piece.
     */
    public boolean isAvailToPlay(int playerSeat, int value) {
	    return availToPlay[playerSeat][value];
    }

    /**
     * Return the number of players in the game.
     *
     * @return the number of players in the game.
     */
    public int getNumPlayers() {return numberOfPlayers;}

    /**
     * Return the place of the last move made in the game.
     *
     * @return the place of the last move made in the game.
     */
    public Point getLastMoveSpace() {return lastMovePoint;}

    /**
     * Return the score for the given player.
     */
    public int getScore(int seatNum) {return score[seatNum];}

    /**
     * Return the seat number of the current player.
     */
    public int getCurrentPlayer() {return currPlayer;}

    /**
     * Return the seat number of the first player that started this round.
     */
    public int getFirstPlayer() {return firstPlayer;}

    /**
     * Determine if the given move is valid or not.
     *
     * For PointsTotal, the only requirement is that the space be empty.
     *
     * @param location   The place where a player wants to play.
     * @return if the move is valid or not.
     */
    public boolean validMove(Point location) {
	    if ((location.x < 0) || (location.x > 3) ||
	        (location.y < 0) || (location.y > 3)) {
		        return false;
	        }

	    return (board[location.x+1][location.y+1].owner == OWNER_NONE);
    }

    // These arrays indicate how to move from a space on the board to the
    // adjacent spaces in each of the four directions.
    private final static int [] c_dir = {0, 1, 0, -1};
    private final static int [] r_dir = {-1, 0, 1, 0};

    /**
     * Make the move.
     *
     * @param  playerSeat   The player making the move
     * @param  thePiece   The piece to be placed
     * @param  location   The place to put the piece.
     * @return if the move is valid or not.
     */
    public boolean makeMove(int playerSeat, PointsTotalPiece thePiece, Point location) {
	    if (!validMove(location)) {
		    return false;
	    }

	    // Offset into the "real" array (to jump over the 1-entry border
	    int col = location.x+1;
	    int row = location.y+1;

	    // Place the piece on the board
	    PointsTotalPiece tgtPiece = board[col][row];
	    tgtPiece.setOwner(playerSeat);
	    tgtPiece.setValue(thePiece.value);
	    tgtPiece.setRotation(thePiece.rotation);

	    // Score the piece by adding the values of the pieces around it.
	    for (int dir = 0; dir < 4; dir++) {
		    if (thePiece.isPointing(dir)) {
			    score[playerSeat] += board[col+c_dir[dir]][row+r_dir[dir]].value;
		    }
	    }

	    // Add the value of the piece itself.
	    score[playerSeat] += thePiece.value;

	    // Remove this piece from the player's pool to use.
	    availToPlay[playerSeat][thePiece.value] = false;

	    // Save this move as the last one made.
	    lastMovePoint = location;

	    // Advance to the next player
	    advanceToNextPlayer();

        // Tell everyone that we've changed.
        refreshObservers();

        return true;
    }

    private void advanceToNextPlayer() {
	    if (turnsInRound != 1) {
		    // Advance one player
		    currPlayer = (currPlayer + 1) % numberOfPlayers;
		    turnsInRound -= 1;
		    return;
	    }

	    // It's the end of the round, so advance the first player to start
	    // the next round
	    firstPlayer += 1;
	    if (firstPlayer == numberOfPlayers) {
		    // Game is over.
		    currPlayer = -1;
		    determineWinners();
		    return;
	    }

	    // Reset the board ready for the next round.
	    resetBoardToEmpty();
	    resetAvailToPlay();
	    lastMovePoint = null;
	    turnsInRound = 4 * numberOfPlayers;
	    currPlayer = firstPlayer;
    }

    /*
     * Determine the maximum score of any of the players.  This score is
     * the winning score.  When determining if a player is the winner, it
     * can be checked to see if that player's score equals the winning
     * score.  This is needed because there can be a tie for winners.
     */
    private void determineWinners() {
	    maxScore = score[0];
	    minScore = score[0];
	    for (int p=1; p < numberOfPlayers; p++) {
		    if (score[p] > maxScore) {
			    maxScore = score[p];
		    }
		    if (score[p] < minScore) {
			    minScore = score[p];
		    }
	    }
    }

    /**
     * Determine if this game is a tie or not.
     */
    public boolean isTie() {
	    return (maxScore == minScore);
    }

    /**
     * Determine if the given player was a winner or not.
     *
     * @param seatNum  The seat number to check for winning.
     * @return true  => This player is a winner.
     *         false => This player is not a winner.
     */
    public boolean isWinner(int seatNum) {
	    return (score[seatNum] == maxScore);
    }

/*********************************************************************/
/* Routines to set/restore state. */

	// XML attributes used for sending/receiving board state
	private static final String XML_PIECE_NAME = "piece";
	  private static final String XML_ATT_PIECE_COL = "c";
	  private static final String XML_ATT_PIECE_ROW = "r";
	  private static final String XML_ATT_PIECE_VALUE = "v";
	  private static final String XML_ATT_PIECE_ROTATION = "t";
	  private static final String XML_ATT_PIECE_OWNER = "o";
	private static final String XML_ATT_SCORE = "score";
	private static final String XML_ATT_CURR_PLAYER = "currPlayer";
	private static final String XML_ATT_FIRST_PLAYER = "firstPlayer";

    /**
     * Method which reads in XMLElement and sets the state of the models fields.
     * This method is necessary for other users to join a game and have the
     * state of the game set properly.
     *
     * @param message    Data stored in message.
     */
    public void setState (XMLElement message) {
        // Wipe everything
        reset ();

		// Unpack the simple things
		currPlayer  = message.getIntAttribute(XML_ATT_CURR_PLAYER);
		firstPlayer = message.getIntAttribute(XML_ATT_FIRST_PLAYER);
		score = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_SCORE));

		// Pull out all of the pieces from the message
		Enumeration msgEnum = message.enumerateChildren();
		while (msgEnum.hasMoreElements()) {
			XMLElement msgEl = (XMLElement) msgEnum.nextElement();
			String elementName = msgEl.getName();
			if (elementName.equals(XML_PIECE_NAME)) {
				// We've got a piece
				int col   = msgEl.getIntAttribute(XML_ATT_PIECE_COL);
				int row   = msgEl.getIntAttribute(XML_ATT_PIECE_ROW);
				int owner = msgEl.getIntAttribute(XML_ATT_PIECE_OWNER);
				int value = msgEl.getIntAttribute(XML_ATT_PIECE_VALUE);
				int rot   = msgEl.getIntAttribute(XML_ATT_PIECE_ROTATION);

				// Put it on the board
				board[col][row].setOwner(owner);
				board[col][row].setValue(value);
				board[col][row].setRotation(rot);

				// Update other status as a result of this piece.
				availToPlay[owner][value] = false;
				turnsInRound -= 1;
			}
		}

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

    /**
     * Implementation of a pointsTotal game - This is stored on the server and is used
     * when a player visits a game and a game is in progress.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        // Retrieve empty state from super class
        XMLElement state = new XMLElement (Comm.MODEL);

        // Set simple things...
        state.setAttribute(XML_ATT_SCORE, JogreUtils.valueOf(score));
        state.setIntAttribute(XML_ATT_CURR_PLAYER,  currPlayer);
        state.setIntAttribute(XML_ATT_FIRST_PLAYER, firstPlayer);

        // Go through the board and create sub-elements for every piece on the
        // board.  (Only need to do the 16 real spaces on the board)
        for (int col = 1; col <= 4; col++) {
	        for (int row = 1; row <= 4; row++) {
		        if (board[col][row].owner != OWNER_NONE) {
			        // Create a child element for this piece.
			        XMLElement child = new XMLElement(XML_PIECE_NAME);
			        child.setIntAttribute(XML_ATT_PIECE_COL, col);
			        child.setIntAttribute(XML_ATT_PIECE_ROW, row);
			        child.setIntAttribute(XML_ATT_PIECE_OWNER, board[col][row].owner);
			        child.setIntAttribute(XML_ATT_PIECE_VALUE, board[col][row].value);
			        child.setIntAttribute(XML_ATT_PIECE_ROTATION, board[col][row].rotation);

			        // Add the child to the state
			        state.addChild(child);
		        }
	        }
        }

        return state;
    }
}
