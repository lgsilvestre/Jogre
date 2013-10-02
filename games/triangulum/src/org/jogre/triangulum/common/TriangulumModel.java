/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
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
package org.jogre.triangulum.common;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

import java.awt.Point;

import java.lang.StringBuilder;

import java.util.Vector;
import java.util.Enumeration;


/**
 * Game model for the Triangulum game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumModel extends JogreModel {

	// Constants for the game
	public final static int WILD_COLOR = 6;

	// The size of the board
	protected int numRows;
	protected int numCols;

	// The type of game this is (36 or 64)
	protected int flavor;

	// Multipliers for the spaces on the board for 36 & 60 variations
	// Note: the multipliers array is [row][col], which is different from pretty
	//       much all the rest of this code (which is [col][row])
	private final static int [][] multipliers_60 = {
		{0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 1, 1, 4, 1, 1, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
		{0, 0, 0, 0, 1, 2, 3, 1, 0, 1, 3, 2, 1, 0, 0, 0},
		{0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0},
		{0, 0, 2, 1, 4, 1, 1, 1, 3, 1, 1, 1, 4, 1, 2, 0},
		{0, 3, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 3}
	};

	private final static int [][] multipliers_36 = {
		{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0},
		{0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
		{0, 0, 0, 1, 2, 4, 1, 4, 2, 1, 0, 0},
		{0, 0, 1, 1, 1, 1, 4, 1, 1, 1, 1, 0},
		{0, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1},
	};

	// Multipliers for the spaces on the board.
	// Note: the multipliers array is [row][col], which is different from pretty
	//       much all the rest of this code (which is [col][row])
	protected int [][] multipliers;

	// The pieces on the board.
	protected TriangulumPiece [][] boardPieces;

	// The pieces in the player's hands.
	protected TriangulumPiece [][] playerPieces;

	// The list of valid moves for each player's piece.
	protected Vector [][] playerPieceMoves;

	// The current player scores
	protected int [] playerScores;

	// The location of each player's most recent move.
	protected Point [] lastMove;

	// The number of players in the game.
	protected int numPlayers;

	// The number of pieces each player has.
	protected int piecesPerPlayer;

	// Indicates if this is the first move of the game or not.
	protected boolean firstMove = true;

	// The number of tiles still in the bag.
	protected int tilesToGo;

	/**
	 * Constructor which creates the model.
	 *
	 * @param flavor   The type of game to be played.
	 *                 36 = 36 piece game
	 *                 60 = 60 piece game
	 */
	public TriangulumModel (int flavor, int numPlayers) {
		super (GAME_TYPE_TURN_BASED);

		// Save parameters
		this.flavor = flavor;

		// Create items that depend on the number of players
		this.numPlayers = numPlayers;
		piecesPerPlayer = (flavor == 36) ? 4 : 5;
		playerPieces = new TriangulumPiece [numPlayers][piecesPerPlayer];
		playerPieceMoves = new Vector [numPlayers][piecesPerPlayer];
		playerScores = new int [numPlayers];
		lastMove = new Point [numPlayers];

		// Determine size of board from flavor
		multipliers = (flavor == 36) ? multipliers_36 : multipliers_60;

		// Create items that depend on the board size
		numRows = multipliers.length;
		numCols = multipliers[0].length;
		boardPieces = new TriangulumPiece[numCols][numRows];

		// Reset the game
		reset();
	}

	/**
	 * Reset the game to the starting configuration.
	 */
	public void reset () {
		// Clear the board of all pieces
		for (int c=0; c<boardPieces.length; c++) {
			for (int r=0; r<boardPieces[0].length; r++) {
				boardPieces[c][r] = null;
			}
		}

		// Remove all pieces from player's hands
		for (int p=0; p<playerPieces.length; p++) {
			for (int i=0; i<playerPieces[0].length; i++) {
				playerPieces[p][i] = null;
				playerPieceMoves[p][i] = new Vector();
			}
		}

		// Clear the player scores & last moves
		for (int p=0; p<playerScores.length; p++) {
			playerScores[p] = 0;
			lastMove[p] = null;
		}

		// Reset the number of tiles to go.
		tilesToGo = flavor;

		// This is now the first move
		firstMove = true;

		// Inform any graphical observers
		refreshObservers();
	}

	/**
	 * Return information about the game.
	 */
	public int getNumRows ()    { return numRows; }
	public int getNumCols ()    { return numCols; }
	public int getNumPlayers () { return numPlayers; }
	public int getFlavor ()     { return flavor; }
	public int getNumPiecesPerPlayer () { return piecesPerPlayer; }

	/**
	 * Return the multiplier to use when playing a piece on the given space
	 * of the board.
	 */
	public int getMultiplier (int c, int r) { return multipliers[r][c]; }

	/**
	 * Return the piece on the board at the given space.
	 */
	public TriangulumPiece getPieceAt (int c, int r) { return boardPieces[c][r]; }

	/**
	 * Return the requested player's piece.
	 */
	public TriangulumPiece getPlayerPiece (int seatNum, int pieceIndex) {
		try {
			return playerPieces[seatNum][pieceIndex];
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the score for a player.
	 */
	public int getScore (int seatNum) { return playerScores[seatNum]; }
	public int [] getScores ()        { return playerScores; }

	/**
	 * Return the last move for a player.
	 */
	public Point getLastMove (int seatNum) { return lastMove[seatNum]; }

	/**
	 * Return the number of valid moves for the given player piece.
	 * Note: On the first turn of the game, every piece is valid to play, but
	 *       the playerPieceMoves vectors aren't setup (since they all would
	 *       have every possible empty space on the board in them), so the
	 *       first move is handled special.
	 */
	public int numValidMovesForPiece (int seatNum, int pieceIndex) {
		try {
			return firstMove ? 1 : playerPieceMoves[seatNum][pieceIndex].size();
		} catch (Exception e) {
			return 0;
		}
	}

	/*
	 * Determine if it is valid for the given play to be discarded.
	 *
	 * @param seatNum     The seat number of the player
	 * @param pieceIndex  The piece number within the hand which is desired to check.
	 * @return true if the given piece can be discarded.
	 */
	public boolean canDiscard (int seatNum, int pieceIndex) {
		try {
			return !hasValidMove(seatNum) &&
			        (playerPieces[seatNum][pieceIndex] != null);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return the array of valid orientations for the given player's piece
	 * that go in the given logical spot on the board.
	 *
	 * @param seatNum     The player number whose hand is desired.
	 * @param pieceIndex  The piece number within the hand whose orientations is desired.
	 * @param loc         The location on the board for which orientations are desired.
	 * @return an array of pieces that have been flipped/rotated into the valid
	 *         orientations, or null if this move is not valid.
	 */
	public TriangulumPiece [] getValidOrientationsForPiece (int seatNum, int pieceIndex, Point loc) {
		if (firstMove) {
			// Handle the first move special.
			return (getMultiplier(loc.x, loc.y) == 1) ?
			           makePieceRotations(playerPieces[seatNum][pieceIndex]) :
			           null;
		}

		// Search the valid move vector to find the one that corresponds to this
		// location on the board.
		Vector moves = playerPieceMoves[seatNum][pieceIndex];
		for (int i=0; i<moves.size(); i++) {
			TriangulumValidMoveElement el = (TriangulumValidMoveElement) moves.get(i);
			if (el.isSameSpace(loc)) {
				return el.getOrientations();
			}
		}

		return null;
	}

	/**
	 * Return if the given player has a valid move or not.
	 * TODO: Perhaps cache this info in an array rather than computing it every
	 *       time this is called?
	 */
	public boolean hasValidMove (int seatNum) {
		if (firstMove) {
			// Handle the first move special.
			return true;
		}

		for (int i=0; i<piecesPerPlayer; i++) {
			if (playerPieceMoves[seatNum][i].size() != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Give a piece to a player for use in his hand.
	 *
	 * @param seatNum     The seat number of the player who is getting the piece.
	 * @param piece       The piece being given.
	 * @param pieceIndex  The location in the player's hand where the piece is to
	 *                    be placed.
	 */
	public void givePiece (int seatNum, TriangulumPiece piece, int pieceIndex) {
		// Put the piece in the player's hand
		playerPieces[seatNum][pieceIndex] = piece;

		// Clear the valid moves for this piece.
		playerPieceMoves[seatNum][pieceIndex].clear();

		// Reduce the number of tiles still in the bag.
		if (piece != null) {
			tilesToGo -= 1;
		}

		// If this is not the first move of the game, then scan for valid moves
		if (!firstMove) {
			// Set up all of the valid moves for this piece.
			for (int col=0; col < numCols; col++) {
				for (int row=0; row < numRows; row++) {
					// If this is a space on the board and it's empty, then try to
					// see if the piece fits here.
					if ((multipliers[row][col] > 0) &&
					    (boardPieces[col][row] == null)) {
						// Check this space for a valid move.
						Point loc = new Point (col, row);
						TriangulumPiece [] orientations = getAllPlaceOrientations(piece, loc);
						if (orientations != null) {
							playerPieceMoves[seatNum][pieceIndex].add(
						            new TriangulumValidMoveElement(loc, orientations));
						}
					}
				}
			}
		}

		// Update anyone watching...
		refreshObservers();
	}

	/**
	 * Make a move on the board.
	 *
	 * @param seatNum     The player making the move
	 * @param thePiece    The piece being placed
	 * @param loc         The board location where the piece is being placed
	 * @return true if this is a valid move and has been made
	 *         false if this is not a valid move and has not been made
	 */
	public boolean makeMove (int seatNum, TriangulumPiece piece, Point loc) {
		int col = loc.x;
		int row = loc.y;
		int neighbors;

		// Verify that this is a legal move.
		if (firstMove) {
			// For the first move, we need to verify that the multiplier for
			// the space is 1.  That is the only constraint.
			if (multipliers[row][col] != 1) {
				return false;
			}
			neighbors = 1;
		} else {
			// Verify that the piece given goes on the location given.
			neighbors = canPlacePiece(piece, loc);
			if ((neighbors <= 0) || (multipliers[row][col] == 0)) {
				return false;
			}
		}

		// Put the piece on the board
		boardPieces[col][row] = piece;

		// Add to the score for the player
		playerScores[seatNum] += (piece.getValue() * neighbors * multipliers[row][col]);

		// Remove this space from all other player's valid move arrays (since
		// it's just been taken)
		removeFromValidMoves(loc);

		// For the three neighboring spaces, if any are empty, then we need to
		// remove & re-check if each player's pieces can fit there.
		recheckValidMoves(getLeftLocation(col, row));
		recheckValidMoves(getRightLocation(col, row));
		recheckValidMoves(getBottomLocation(col, row));

		// Save the last move information
		lastMove[seatNum] = loc;

		// It is no longer the first move of the game.
		firstMove = false;

		// Update anyone watching...
		refreshObservers();

		return true;
	}

	/*
	 * This routine will check the given location and remove it from valid moves
	 * and then see which player's pieces may be validly placed there and with
	 * what orientation.
	 */
	private void recheckValidMoves (Point newLoc) {
		if (newLoc != null) {
			int col = newLoc.x;
			int row = newLoc.y;
			if (boardPieces[col][row] == null) {
				// Empty spot, so need to revalidate it.
				removeFromValidMoves(newLoc);
				checkAddToAllValidMoves(newLoc);
			}
		}
	}

	/*
	 * This routine will check to see if the given space is a valid move for
	 * all pieces in all player's hands and, if so, will add the location to
	 * the vector of valid moves for that piece.
	 */
	private void checkAddToAllValidMoves (Point newLoc) {
		for (int p=0; p<numPlayers; p++) {
			for (int i=0; i<piecesPerPlayer; i++) {
				TriangulumPiece [] orientations = 
				          getAllPlaceOrientations(playerPieces[p][i], newLoc);
				if (orientations != null) {
					playerPieceMoves[p][i].add(
					            new TriangulumValidMoveElement(newLoc, orientations));
				}
			}
		}
	}
	
	/*
	 * This routine will remove the given space from all valid move vectors
	 * of all player pieces.
	 */
	private void removeFromValidMoves (Point oldLoc) {
		for (int p=0; p<numPlayers; p++) {
			for (int i=0; i<piecesPerPlayer; i++) {
				removeMove(playerPieceMoves[p][i], oldLoc);
			}
		}
	}

	/*
	 * This routine will remove a valid move element from the given vector that
	 * corresponds to the given board location.
	 */
	private void removeMove (Vector validMoves, Point oldLoc) {
		for (int i=0; i<validMoves.size(); i++) {
			TriangulumValidMoveElement el = (TriangulumValidMoveElement) validMoves.get(i);
			if (el.isSameSpace(oldLoc)) {
				validMoves.remove(i);
				return;
			}
		}
	}

	/**
	 * Get an array of the valid orientations for the given piece in the given
	 * spot.
	 *
	 * @param thePiece  The piece to place.
	 * @param loc       The logical location on the board to test.
	 * @return an array of pieces that have been fliped/rotated.  If a piece
	 *  cannot be place validly on the given board space, then that entry in
	 *  the array is nulled out.
	 *  If the piece cannot be placed there at all, then null is returned.
	 */
	private TriangulumPiece [] getAllPlaceOrientations (TriangulumPiece thePiece, Point loc) {
		if (thePiece == null) {
			// Nothing to do
			return null;
		}

		// Create the 6 rotation/reflections of the piece.
		TriangulumPiece [] pieces = makePieceRotations(thePiece);
		int numPieces = pieces.length;

		// For each one, if the piece doesn't fit, then null out it's spot.
		for (int i=0; i < pieces.length; i++) {
			if (canPlacePiece(pieces[i], loc) <= 0) {
				pieces[i] = null;
				numPieces -= 1;
			}
		}

		// Return the array of pieces (or null, if the array is empty)
		return (numPieces == 0) ? null : pieces;
	}

	/*
	 * This routine will generate the possible rotations/flips of the given
	 * piece.  The array returned will include only distinct pieces, so in the
	 * case that 2 or 3 segments are the same color there will be fewer entries
	 * in the array returned.
	 * A copy of the given piece will be returned as element 0 of the array.
	 *
	 * @param thePiece   The piece to make rotations from.
	 * @return an array of the valid orientations.
	 */
	private TriangulumPiece [] makePieceRotations (TriangulumPiece thePiece) {
		TriangulumPiece [] rots;

		int val = thePiece.getValue();
		int cl = thePiece.getColor(TriangulumPiece.LEFT_SECTION  );
		int cr = thePiece.getColor(TriangulumPiece.RIGHT_SECTION );
		int cb = thePiece.getColor(TriangulumPiece.BOTTOM_SECTION);

		if (cl != cr) {
			// All 3 colors are different, so 6 orientations
			rots = new TriangulumPiece [6];

			rots[0] = new TriangulumPiece(val, cb, cl, cr);
			rots[1] = new TriangulumPiece(val, cb, cr, cl);
			rots[2] = new TriangulumPiece(val, cl, cr, cb);
			rots[3] = new TriangulumPiece(val, cr, cl, cb);
			rots[4] = new TriangulumPiece(val, cr, cb, cl);
			rots[5] = new TriangulumPiece(val, cl, cb, cr);
		} else {
			if (cl == cb) {
				// All 3 colors are the same, so only 1 orientation
				rots = new TriangulumPiece [1];

				rots[0] = new TriangulumPiece(val, cb, cl, cr);
			} else {
				// Left & right are same color, so 3 orientations
				rots = new TriangulumPiece [3];

				rots[0] = new TriangulumPiece(val, cb, cl, cr);
				rots[1] = new TriangulumPiece(val, cr, cb, cl);
				rots[2] = new TriangulumPiece(val, cl, cr, cb);
			}
		}

		return rots;
	}
	
	/**
	 * Determine if the give piece can be placed in the given location.
	 * This will *not* do any rotation/reflections on the piece; it uses
	 * the orientation provided.
	 *
	 * @param thePiece   The piece to be placed.
	 * @param loc        The location to place the piece.
	 * @return the number of neighbors that the piece will have if placed at the
	 *         given location, or -1 if the piece cannot be placed here.
	 */
	public int canPlacePiece (TriangulumPiece thePiece, Point loc) {
		int neighborCount = 0;
		int col = loc.x;
		int row = loc.y;

		// Check left neighbor.
		TriangulumPiece leftNeighbor = getLeftNeighbor(col, row);
		if (leftNeighbor != null) {
			if (!colorMatch(leftNeighbor.getColor(TriangulumPiece.RIGHT_SECTION),
			                thePiece.getColor(TriangulumPiece.LEFT_SECTION))) {
				return -1;
			}
			neighborCount += 1;
		}

		// Check right neighbor.
		TriangulumPiece rightNeighbor = getRightNeighbor(col, row);
		if (rightNeighbor != null) {
			if (!colorMatch(rightNeighbor.getColor(TriangulumPiece.LEFT_SECTION),
			                thePiece.getColor(TriangulumPiece.RIGHT_SECTION))) {
				return -1;
			}
			neighborCount += 1;
		}

		// Check bottom neighbor.
		TriangulumPiece bottomNeighbor = getBottomNeighbor(col, row);
		if (bottomNeighbor != null) {
			if (!colorMatch(bottomNeighbor.getColor(TriangulumPiece.BOTTOM_SECTION),
			                thePiece.getColor(TriangulumPiece.BOTTOM_SECTION))) {
				return -1;
			}
			neighborCount += 1;
		}

		return neighborCount;
	}

	/**
	 * Determine if the two colors provided match.
	 */
	public boolean colorMatch (int c1, int c2) {
		return ((c1 == c2) || (c1 == WILD_COLOR) || (c2 == WILD_COLOR));
	}

	/**
	 * Return the left neighbor piece to the space given.
	 */
	public TriangulumPiece getLeftNeighbor (int c, int r) {
		if (c != 0) {
			return boardPieces[c-1][r];
		}
		return null;
	}

	/**
	 * Return the right neighbor piece to the space given.
	 */
	public TriangulumPiece getRightNeighbor (int c, int r) {
		if (c != (boardPieces.length - 1)) {
			return boardPieces[c+1][r];
		}
		return null;
	}

	/**
	 * Return the bottom neighbor piece to the space given.
	 */
	public TriangulumPiece getBottomNeighbor (int c, int r) {
		if (isUpwardTriangle(c, r)) {
			if (r != (boardPieces[0].length - 1)) {
				return boardPieces[c][r+1];
			}
		} else {
			if (r != 0) {
				return boardPieces[c][r-1];
			}
		}
		return null;
	}

	/**
	 * Return the point on the board that is to the left of the given location.
	 * or null, if there is no point on the board to the left.
	 */
	public Point getLeftLocation (int c, int r) {
		try {
			if (multipliers[r][c-1] != 0)
				return new Point (c-1, r);
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * Return the point on the board that is to the right of the given location.
	 * or null, if there is no point on the board to the right.
	 */
	public Point getRightLocation (int c, int r) {
		try {
			if (multipliers[r][c+1] != 0)
				return new Point (c+1, r);
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * Return the point on the board that is next to the bottom of the given location.
	 * or null, if there is no point on the board to the bottom.
	 */
	public Point getBottomLocation (int c, int r) {
		try {
			r += (isUpwardTriangle(c, r) ? 1 : -1);
			if (multipliers[r][c] != 0)
				return new Point (c, r);
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * Determine if the game is over or not.
	 */
	public boolean isGameOver () {
		// The game is over when all players have no moves and the number of
		// tiles still in the bag is zero.

		if (tilesToGo > 0) {
			return false;
		}

		for (int i=0; i<numPlayers; i++) {
			if (hasValidMove(i)) {
				return false;
			}
		}

		return true;
	}


	/**
	 * This method will determine if the given triangle is an "up" triangle or
	 * not.  The up-ness computed by this is a logical property of the triangle
	 * not a physical one.  That means that rotation & flipping is not taken
	 * into account.  This should be used for logical calculations such as
	 * determining neighbors.
	 *
	 * @param col          The column of the triangle to draw.
	 * @param row          The row of the triangle to draw.
	 * @return   true  => The triangle is "up"
	 *           false => The triangle is "down"
	 */
	private boolean isUpwardTriangle (int col, int row) {
		return (((col + row) & 0x01) == 0);
	}

/**********************************************************************/
/* Save/restore game state methods */

	// XML attributes used for sending/receiving board state
	private static final String XML_NAME_BOARD_PIECES = "boardPieces";
	private static final String XML_NAME_PLAYER_PIECES = "playerPieces";
	private static final String XML_NAME_PIECE = "p";
	private static final String XML_ATT_SCORES = "scores";
	private static final String XML_ATT_LAST_MOVES = "lastMoves";
	private static final String XML_ATT_TILES_TO_GO = "tilesToGo";
	private static final String XML_ATT_I = "i";
	private static final String XML_ATT_J = "j";

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

		// Set values by pulling things out of the message
		playerScores = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_SCORES));
		tilesToGo = message.getIntAttribute(XML_ATT_TILES_TO_GO);
		decodeLastMoveString(message.getStringAttribute(XML_ATT_LAST_MOVES));

		// Pull out the sub-trees
		Enumeration subTreeEnum = message.enumerateChildren();
		while (subTreeEnum.hasMoreElements()) {
			XMLElement subTree = (XMLElement) subTreeEnum.nextElement();
			if (XML_NAME_BOARD_PIECES.equals(subTree.getName())) {
				decodePieceTree(subTree, boardPieces);
			} else if (XML_NAME_PLAYER_PIECES.equals(subTree.getName())) {
				decodePieceTree(subTree, playerPieces);
			}
		}

		// Compute things based on the restored values
		firstMove = (tilesToGo == flavor);

		// If everything is read sucessfully then refresh observers
		refreshObservers();
	}

	/**
	 * Implementation of a triangulum game - This is stored on the server and is used
	 * when a player visits a game and a game is in progress.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		state.addChild(createPieceXMLTree(boardPieces, XML_NAME_BOARD_PIECES));
		state.addChild(createPieceXMLTree(playerPieces, XML_NAME_PLAYER_PIECES));
		state.setAttribute(XML_ATT_SCORES, JogreUtils.valueOf(playerScores));
		state.setAttribute(XML_ATT_LAST_MOVES, createLastMoveString());
		state.setIntAttribute(XML_ATT_TILES_TO_GO, tilesToGo);

		return state;
	}

	/*
	 * Create an XML representation of the pieces in a two dimensional array.
	 *
	 * @param pieces    The pieces to encode.
	 * @param treeName  The name to give to the tree.
	 */
	private XMLElement createPieceXMLTree (TriangulumPiece [][] pieces, String treeName) {
		XMLElement newTree = new XMLElement (treeName);

		for (int i=0; i<pieces.length; i++) {
			for (int j=0; j<pieces[0].length; j++) {
				if (pieces[i][j] != null) {
					// Create a new child element for this piece
					XMLElement childEl = new XMLElement (XML_NAME_PIECE);

					// Add the attributes to the child
					childEl.setIntAttribute(XML_ATT_I, i);
					childEl.setIntAttribute(XML_ATT_J, j);
					pieces[i][j].addToXML(childEl);

					// Add the child to the board pieces tree
					newTree.addChild(childEl);
				}
			}
		}

		return newTree;
	}

	/*
	 * Decode an XML tree representation of pieces in a two-dimensional array
	 * and place them into the given array.
	 *
	 * @param pieceTree    The XML representation of the pieces.
	 * @param destArray    The array to place the pieces into.
	 */
	private void decodePieceTree (XMLElement pieceTree, TriangulumPiece [][] destArray) {
		Enumeration childrenEnum = pieceTree.enumerateChildren();

		while (childrenEnum.hasMoreElements()) {
			XMLElement childEl = (XMLElement) childrenEnum.nextElement();

			// Get the parameters out of the child.
			int i = childEl.getIntAttribute(XML_ATT_I);
			int j = childEl.getIntAttribute(XML_ATT_J);
			TriangulumPiece piece = new TriangulumPiece (childEl);

			// Put the piece in the array
			destArray[i][j] = piece;
		}
	}

	/*
	 * Create a string that represents the last moves of the players.
	 */
	private String createLastMoveString () {
		StringBuilder sb = new StringBuilder ();

		for (int i=0; i<lastMove.length; i++) {
			if (lastMove[i] == null) {
				sb.append("-1 -1 ");
			} else {
				sb.append(lastMove[i].x);
				sb.append(" ");
				sb.append(lastMove[i].y);
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	/*
	 * Decode the string that represents the last moves of the players.
	 */
	private void decodeLastMoveString (String msgString) {
		int [] indexes = JogreUtils.convertToIntArray(msgString);

		for (int i=0; i<lastMove.length; i++) {
			int x = indexes[i*2];
			int y = indexes[i*2+1];
			if (x >= 0) {
				lastMove[i] = new Point (x, y);
			}
		}
	}
}
