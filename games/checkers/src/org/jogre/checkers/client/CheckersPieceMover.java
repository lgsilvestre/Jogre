/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.checkers.client;

import java.awt.Point;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Class for checking and executing valid checkers moves.
 */
public class CheckersPieceMover implements ICheckersModel {

	private CheckersModel checkersModel;

	/**
	 * Default constructor for checking and executing valid checkers moves.
	 * The constructor takes the checkers model for a game.
	 *
	 * @param checkersModel
	 */
	public CheckersPieceMover (CheckersModel checkersModel) {
		this.checkersModel = checkersModel;
	}

	/**
	 * Return if a possible move.
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public boolean isNormalMove (int x1, int y1, int x2, int y2) {
		int index1 = checkersModel.getBoardIndex (x1, y1);
		int index2 = checkersModel.getBoardIndex (x2, y2);

		if (index1 >= 0 && index1 < 32 && index2 >= 0 && index2 < 32) {
			int piece = checkersModel.getPiece (index1);

			// The square where the piece goes to must be empty
			if (checkersModel.getPiece (index2) != EMPTY)
				return false;

			// Check the different pieces
			if (piece == W_NORMAL) {				// White normal
				// normal move forward
				if (y1 - y2 == 1 && Math.abs(x2 - x1) == 1) {
					return true;
				}
			}
			else if (piece == W_KING) {				// White King
				if (Math.abs(y1 - y2) == 1 && Math.abs(x2 - x1) == 1) {
					return true;
				}
			}
			else if (piece == B_NORMAL) {			// Black normal
				// normal move forward
				if (y1 - y2 == -1 && Math.abs(x2 - x1) == 1) {
					return true;
				}
			}
			else if (piece == B_KING) {				// Black King
				if (Math.abs(y1 - y2) == 1 && Math.abs(x2 - x1) == 1) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Return true if this move is an attacking move.
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public boolean isAttackingMove (int x1, int y1, int x2, int y2) {
		int index1 = checkersModel.getBoardIndex (x1, y1);
		int index2 = checkersModel.getBoardIndex (x2, y2);

		if (index1 >= 0 && index1 < 32 && index2 >= 0 && index1 < 32) {
			int piece = checkersModel.getPiece (index1);

			// The square where the piece goes to must be empty
			if (checkersModel.getPiece (index2) != EMPTY)
				return false;

			// Check the different pieces
			if (piece == W_NORMAL) {				// White normal
				if (		// attacking another player
					y1 - y2 == 2 && 					// 2 rows away
					Math.abs (x2 - x1) == 2 && (
						checkersModel.getPieceColour (	// is piece colour in middle
							checkersModel.getBoardIndex (x1 - ((x1 - x2) / 2), y1 - 1)
						) == PLAYER_TWO					// equal to other player ?
					)
				) return true;
			}
			else if (piece == W_KING) {				// White King
				if (
					Math.abs (x2 - x1) == 2 && 					// 2 rows away
					Math.abs (y2 - y1) == 2 && (
						checkersModel.getPieceColour (	// is piece colour in middle
							checkersModel.getBoardIndex (
								x1 - ((x1 - x2) / 2), y1 - ((y1 - y2) / 2)
							)
						)
					) == PLAYER_TWO				// equal to other player ?
				) return true;
			}
			else if (piece == B_NORMAL) {			// Black normal
				if (		// attacking another player
					y1 - y2 == -2 && 					// 2 rows away
					Math.abs (x2 - x1) == 2 && (
						checkersModel.getPieceColour (	// is piece colour in middle
							checkersModel.getBoardIndex (x1 - ((x1 - x2) / 2), y1 + 1)
						) == PLAYER_ONE					// equal to other player ?
					)
				) return true;
			}
			else if (piece == B_KING) {				// Black King
				if (
					Math.abs (x2 - x1) == 2 && 					// 2 rows away
					Math.abs (y2 - y1) == 2 && (
						checkersModel.getPieceColour (	// is piece colour in middle
							checkersModel.getBoardIndex (
								x1 - ((x1 - x2) / 2), y1 - ((y1 - y2) / 2)
							)
						)
					) == PLAYER_ONE					// equal to other player ?
				) return true;
			}
		}
		return false;
	}

	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public boolean isPossibleMove (int x1, int y1, int x2, int y2) {
		int index1 = checkersModel.getBoardIndex(x1, y1);
		int player = checkersModel.getPieceColour(index1);
		int numOfAttacking = countPossibleAttackingMoves (player);

		// If the player can attack ensure that he is doing so.
		if (numOfAttacking > 0)
			return isAttackingMove(x1, y1, x2, y2);
		else
			return isNormalMove(x1, y1, x2, y2);
	}

	/**
	 * Return the number of pieces of a particular colour.
	 *
	 * @param player
	 */
	public int getPlayerCount (int player) {
		int count = 0;
		for (int i = 0; i < 32; i++) {
			if (checkersModel.getPieceColour(i) == player)
				count ++;
		}

		return count;
	}

	/**
	 * Return true if a player can attack
	 *
	 * @param player
	 */
	public boolean canPlayerMove (int player) {
		int numOfMoves =
			countPossibleAttackingMoves(player) +
			countPossibleAttackingMoves(player);
		return numOfMoves > 0;
	}

	/**
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public int countPossibleAttackingMoves (int player) {
		int count = 0;

		// retrieve index from co-ordinate
		for (int i = 0; i < 32; i++) {
			if (checkersModel.getPieceColour(i) == player) {
				Point p1 = checkersModel.getBoardPoint(i);
				for (int j = 0; j < 32; j++) {
					Point p2 = checkersModel.getBoardPoint(j);
					if (isAttackingMove(p1.x, p1.y, p2.x, p2.y))
						count ++;
				}
			}
		}

		return count;
	}

	/**
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public int countPossibleNormalMoves (int player) {
		int count = 0;

		// retrieve index from co-ordinate
		for (int i = 0; i < 32; i++) {
			if (checkersModel.getPieceColour(i) == player) {
				Point p1 = checkersModel.getBoardPoint(i);
				for (int j = 0; j < 32; j++) {
					Point p2 = checkersModel.getBoardPoint(i);
					if (isNormalMove(p1.x, p1.y, p2.x, p2.y))
						count ++;
				}
			}
		}

		return count;
	}
	/**
	 * Assumes isPossibleMove () method has already been called.
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public boolean executeMove (int x1, int y1, int x2, int y2) {
		int index1 = checkersModel.getBoardIndex (x1, y1);
		int index2 = checkersModel.getBoardIndex (x2, y2);

		int piece = checkersModel.getPiece(index1);
		int pieceColour = checkersModel.getPieceColour(index1);

		// Check to see if attacking move
		if (Math.abs (x2 - x1) == 2 && Math.abs (y2 - y1) == 2) {
			int mx = x1 - ((x1 - x2) / 2);
			int my = y1 - ((y1 - y2) / 2);
			int mi = checkersModel.getBoardIndex (mx, my);

			// remove opponents piece
			checkersModel.setPiece (mi, EMPTY);
		}

		// Check to see if a piece has reached the other side of the board
		if (y2 == 0 && pieceColour == PLAYER_ONE)
			piece = W_KING;		// upgrade to king
		if (y2 == 7 && pieceColour == PLAYER_TWO)
			piece = B_KING;		// upgrade to king

		// normal move
		checkersModel.setPiece (index1, EMPTY);
		checkersModel.setPiece (index2, piece);

		return true;
	}
}
