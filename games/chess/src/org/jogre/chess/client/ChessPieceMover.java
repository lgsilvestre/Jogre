/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chess
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
package org.jogre.chess.client;

/**
 * This class determines how a chess piece can actually move.  This
 * class has a contructor, 1 method "checkMove (x1, y1, x2, y2)" and
 * a method called "executeMove (x1, y1, x2, y2)".
 *
 * This class contains a number of inner classes which describe how a piece
 * moves.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ChessPieceMover implements IChessModel {
	// reference to main game data and an internal copy
	private ChessModel mainChessModel;

	// Create instances of various checking types
	private WhitePawn whitePawn = new WhitePawn ();
	private BlackPawn blackPawn = new BlackPawn ();
	private Knight knight = new Knight ();
	private Bishop bishop = new Bishop ();
	private Queen queen = new Queen ();
	private WhiteKing whiteKing = new WhiteKing ();
	private BlackKing blackKing = new BlackKing ();
	private Rook rook = new Rook ();

	/**
	 * Constructor for a move.  Must provde a link to the main ChessModel.
	 *
	 * @param chessModel
	 */
	public ChessPieceMover (ChessModel chessModel) {
		this.mainChessModel = chessModel;
	}

	/**
	 * This is the all important check move function which checks to see if a
	 * particular move is achievable or not.
	 */
	public boolean checkMove (int player, int x1, int y1, int x2, int y2) {
		// The trick here is create a copy of the current game model each time
		// and actually make the move on the copy.  This is necessary as you
		// have to know if the move puts the king into a check position (which
		// of course is disallowed).
		ChessModel gameModelCopy = new ChessModel ();
		deepCopy (mainChessModel, gameModelCopy);

		// presume current players king isn't in check
		boolean isOwnKingInCheck = false;

		// if the move is valid so far then make the move and check to see if
		// that move leaves the players OWN king in check.  Also
		if (isPossibleMove (mainChessModel, x1, y1, x2, y2)) {
			// Actually make the move on the game copy
			executeMove (gameModelCopy, x1, y1, x2, y2);

			// Ensure that this move doesn't put the player in check
			isOwnKingInCheck = isPlayerInCheck (gameModelCopy, player);

			// if king isn't in check then return as normal
			return (!isOwnKingInCheck);
		}

		// other wise this is an invalid move
		return false;
	}

	/**
	 * Actually execute the move.
	 */
	public void executeMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
		// Check to see if any of the rooks have moved
		if (chessModel.getPiece(0, 7) != W_ROOK)
			chessModel.setFlag (IChessModel.FLAG_W_L_ROOK_HASNT_MOVED, false);
		if (chessModel.getPiece(7, 7) != W_ROOK)
			chessModel.setFlag (IChessModel.FLAG_W_R_ROOK_HASNT_MOVED, false);
		if (chessModel.getPiece(0, 0) != B_ROOK)
			chessModel.setFlag (IChessModel.FLAG_B_L_ROOK_HASNT_MOVED, false);
		if (chessModel.getPiece(7, 0) != B_ROOK)
			chessModel.setFlag (IChessModel.FLAG_B_R_ROOK_HASNT_MOVED, false);

		// Retrieve the pieces
		int piece1 = chessModel.getPiece(x1, y1);
		int piece2 = chessModel.getPiece(x2, y2);

		// check white castling queen side
		if (x1 ==  4 && y1 == 7 && x2 == 2 && y2 == 7 && piece1 == W_KING) {
			chessModel.setPiece(2, 7, W_KING); chessModel.setPiece(3, 7, W_ROOK);
			chessModel.setPiece(4, 7, EMPTY); chessModel.setPiece(0, 7, EMPTY);
		}
		// check white castling queen side
		else if (x1 ==  4 && y1 == 7 && x2 == 6 && y2 == 7 && piece1 == W_KING) {
			chessModel.setPiece(6, 7, W_KING); chessModel.setPiece(5, 7, W_ROOK);
			chessModel.setPiece(4, 7, EMPTY); chessModel.setPiece(7, 7, EMPTY);
		}
		// check black castling queen side
		else if (x1 ==  4 && y1 == 0 && x2 == 2 && y2 == 0 && piece1 == B_KING) {
			chessModel.setPiece(2, 0, B_KING); chessModel.setPiece(3, 0, B_ROOK);
			chessModel.setPiece(4, 0, EMPTY); chessModel.setPiece(0, 0, EMPTY);
		}
		// check black castling queen side
		else if (x1 ==  4 && y1 == 0 && x2 == 6 && y2 == 0 && piece1 == B_KING) {
			chessModel.setPiece(6, 0, B_KING); chessModel.setPiece(5, 0, B_ROOK);
			chessModel.setPiece(4, 0, EMPTY); chessModel.setPiece(7, 0, EMPTY);
		}
        // white pawn en passant
        else if (piece1 == W_PAWN && (new WhitePawn()).isEnPassant(chessModel, x1, y1, x2, y2)) {
		    piece2 = chessModel.getPiece(x2, y2 + 1);
			int pieceColour = chessModel.getPieceColour(x2, y2 + 1);
			if (pieceColour == ChessModel.PLAYER_ONE) {
				chessModel.addCapturedPiece (piece2);
			}
			else if (pieceColour == ChessModel.PLAYER_TWO) {
				chessModel.addCapturedPiece (piece2);
            }

			// Move piece from one square to another
			chessModel.setPiece(x2, y2, piece1);
			chessModel.setPiece(x1, y1, EMPTY);
            // en passant capture
			chessModel.setPiece(x2, y2 + 1, EMPTY);
        }
        // black pawn en passant
        else if (piece1 == B_PAWN && (new BlackPawn()).isEnPassant(chessModel, x1, y1, x2, y2)) {
		    piece2 = chessModel.getPiece(x2, y2 - 1);
			int pieceColour = chessModel.getPieceColour(x2, y2 - 1);
			if (pieceColour == ChessModel.PLAYER_ONE) {
				chessModel.addCapturedPiece (piece2);
			}
			else if (pieceColour == ChessModel.PLAYER_TWO) {
				chessModel.addCapturedPiece (piece2);
            }

			// Move piece from one square to another
			chessModel.setPiece(x2, y2, piece1);
			chessModel.setPiece(x1, y1, EMPTY);
            // en passant capture
			chessModel.setPiece(x2, y2 - 1, EMPTY);
        }
        // normal move
		else {
			// check to see if a piece is being captured
			int pieceColour = chessModel.getPieceColour(x2, y2);
			if (pieceColour == ChessModel.PLAYER_ONE) {
				chessModel.addCapturedPiece (piece2);
			}
			else if (pieceColour == ChessModel.PLAYER_TWO)
				chessModel.addCapturedPiece (piece2);

			// Move piece from one square to another
			chessModel.setPiece(x2, y2, piece1);
			chessModel.setPiece(x1, y1, EMPTY);

			if (piece1 == W_PAWN && y2 == 0)
				chessModel.setPiece(x2, y2, W_QUEEN);
			if (piece1 == B_PAWN && y2 == 7)
				chessModel.setPiece(x2, y2, B_QUEEN);
		}

        // Clear two square flags for pawns. Should be done after en passant checked
        for (int i = 0; i < 8; i++)
        {
			chessModel.setFlag (IChessModel.FLAG_W_PAWN_TWO_SQUARE_MOVED + i, false);
			chessModel.setFlag (IChessModel.FLAG_B_PAWN_TWO_SQUARE_MOVED + i, false);
        }

        // Set two square flags for pawns to check en passant on next turn
        if (piece1 == W_PAWN && y1 - y2 == 2 && x1 == x2)
        {
			chessModel.setFlag (IChessModel.FLAG_W_PAWN_TWO_SQUARE_MOVED + x1, true);
        }
        if (piece1 == B_PAWN && y2 - y1 == 2 && x1 == x2)
        {
			chessModel.setFlag (IChessModel.FLAG_B_PAWN_TWO_SQUARE_MOVED + x1, true);
        }
    }

	/** Important method to show if the white king is in check
	 */
	public boolean isPlayerInCheck (int player) {
		return isPlayerInCheck (mainChessModel, player);		// use main game Data
	}

	/** Overloaded version which can be used on tempoary data.
	 */
	private boolean isPlayerInCheck (ChessModel chessModel, int player) {
		// Must locate where the various kings are
		int kingX = -1, kingY = -1;
		int opponentPlayer = player == ChessModel.PLAYER_ONE ?
				ChessModel.PLAYER_TWO : ChessModel.PLAYER_ONE;

		int currentKing =
			player == ChessModel.PLAYER_ONE ? W_KING : B_KING;
		// find the king
		for (int x = 0; x < 8; x++)
        	for (int y = 0; y < 8; y++)
        		if (chessModel.getPiece(x, y) == currentKing) {
        			kingX = x; kingY = y; break;
        		}
		return (isAttackedBy (chessModel, kingX, kingY, opponentPlayer));
	}

	/** Important method to show if the white king is in check mate
	 */
	public boolean isPlayerInCheckMate (int player) {
		// First check that you have the opponent in check position
		if (isPlayerInCheck(player)) {

			// Then find the opponents pieces ...
			for (int x1 = 0; x1 < 8; x1++) {
	        	for (int y1 = 0; y1 < 8; y1++) {

	        		// Check to see if this is an opponent
	        		if (mainChessModel.getPieceColour(x1, y1) == player) {

	        			// and check to see if he can move
	        			for (int x2 = 0; x2 < 8; x2++) {
	        	        	for (int y2 = 0; y2 < 8; y2++) {
	        	        		// It isn't check mate if the other player can move
	        	        		if (checkMove(player, x1, y1, x2, y2)) {
	        	        			return false;
	        	        		}
	        	        	}
	        			}
	        		}
	        	}
			}

			return true;
		}

		return false;
	}

	/** Check to see if the game is a draw.
	 */
	public boolean isGameADraw (int player) {
		// First check the player is NOT in check position
		if (!isPlayerInCheck(player)) {

			// Then find the player pieces ...
			for (int x1 = 0; x1 < 8; x1++) {
	        	for (int y1 = 0; y1 < 8; y1++) {

	        		// Check to see if this is an player
	        		if (mainChessModel.getPieceColour(x1, y1) == player) {

	        			// and check to see if he can move
	        			for (int x2 = 0; x2 < 8; x2++) {
	        	        	for (int y2 = 0; y2 < 8; y2++) {
	        	        		// It isn't check mate if the player can move
	        	        		if (checkMove(player, x1, y1, x2, y2))
	        	        			return false;
	        	        	}
	        			}
	        		}
	        	}
			}

			return true;
		}

		return false;
	}

	/**
	 * Deep copy one game data to another.
     *
	 * @param curChessModel
	 * @param copyChessModel
	 */
	private void deepCopy (ChessModel curChessModel, ChessModel copyChessModel) {
		// Reset all the data in the copy
		copyChessModel.reset ();
		//	 Copy across the pieces
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++)
				copyChessModel.setPiece(x, y, curChessModel.getPiece(x, y));

		// Set final King and Rook changes
		for (int i = 0; i < IChessModel.NUM_OF_FLAGS; i++)
			copyChessModel.setFlag(i, curChessModel.flag(i));

		// Set last move
		copyChessModel.setLastMove(curChessModel.getLastMove());
	}

	/**
	 * This is the method which checks if a piece can move or not.
	 */
	private boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
		// Depending on what piece has been moved
		switch (chessModel.getPiece(x1, y1)) {
			case W_PAWN: return whitePawn.isPossibleMove(chessModel, x1, y1, x2, y2);
			case W_KNIGHT: return knight.isPossibleMove(chessModel, x1, y1, x2, y2);
			case W_BISHOP: return bishop.isPossibleMove(chessModel, x1, y1, x2, y2);
			case W_ROOK: return rook.isPossibleMove(chessModel, x1, y1, x2, y2);
			case W_QUEEN: return queen.isPossibleMove(chessModel, x1, y1, x2, y2);
			case W_KING: return whiteKing.isPossibleMove(chessModel, x1, y1, x2, y2);
			case B_PAWN: return blackPawn.isPossibleMove(chessModel, x1, y1, x2, y2);
			case B_KNIGHT: return knight.isPossibleMove(chessModel, x1, y1, x2, y2);
			case B_BISHOP: return bishop.isPossibleMove(chessModel, x1, y1, x2, y2);
			case B_ROOK: return rook.isPossibleMove(chessModel, x1, y1, x2, y2);
			case B_QUEEN: return queen.isPossibleMove(chessModel, x1, y1, x2, y2);
			case B_KING: return blackKing.isPossibleMove(chessModel, x1, y1, x2, y2);
		}

		return false;
	}

	/** Checks the line of sight from one chess piece to another
	 */
	private boolean checkLineOfSight (ChessModel chessModel, int x1, int y1, int x2, int y2) {
		// check it can go in a particular direction
		if (x1 != x2 && y1 != y2 && Math.abs(x2 - x1) != Math.abs(y2 - y1))
			return false;

		// find difference
		int rdiff = (x2 >= x1) ? ((x2 > x1) ? 1 : 0) : -1;
		int fdiff = (y2 >= y1) ? ((y2 > y1) ? 1 : 0) : -1;
		int rank = x1 + rdiff;
		int file = y1 + fdiff;

		while (rank != x2 || file != y2) {
			if (chessModel.getPiece(rank, file) != EMPTY)
				return false;
			rank += rdiff;
			file += fdiff;
		}
		return true;
	}

	/**
	 * Does some basic checks.
	 */
	private boolean checkBasicMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
		if (x1 < 0 || x2 < 0 || x1 > 7 || x2 > 7 || y1 < 0 || y2 < 0 || y1 > 7 || y2 > 7)
			return false;

		int piece1 = chessModel.getPiece(x1, y1);
		int piece2 = chessModel.getPiece(x2, y2);
		boolean isWhite = piece1 < B_PAWN;

		return
		(	!(   // ensure that white isn't landing on white and black on black
				(isWhite && piece2 < B_PAWN && piece2 != EMPTY) ||
				(!isWhite && piece2 > W_KING)
			)
		);
	}

	/** Method to show if a piece is being attacked by the other player
	 */
	private boolean isAttackedBy (ChessModel chessModel, int x1, int y1, int colour) {
        for (int x2 = 0; x2 < 8; x2++) {
        	for (int y2 = 0; y2 < 8; y2++) {
        		if (chessModel.getPieceColour(x2, y2) == colour) {
					// don't forget a pawn movement isn't always an attack
        			if (chessModel.getPiece(x2, y2) == W_PAWN) {
        				if (whitePawn.isAttackingMove(chessModel, x2, y2, x1, y1))
        					return true;
        			}
        			else if (chessModel.getPiece(x2, y2) == B_PAWN) {
        				if (blackPawn.isAttackingMove(chessModel, x2, y2, x1, y1))
        					return true;
        			}
        			else if (isPossibleMove(chessModel, x2, y2, x1, y1)) {
	        			return true;
        			}
        		}
        	}
        }
        return false;
    }

	/**
	 * Declare abstract class for holding data common to all chess pieces.
	 */
	public abstract class AbstractChessPiece {
		// declare an integer array to dermine what directions the
		// piece can move in
		int [][] possibleMoves = null;

		/**
		 * @param type
		 * @param directions
		 * @param canMoveVariable
		 */
		public AbstractChessPiece (int [][] possibleMoves) {
			this.possibleMoves = possibleMoves;
		}

		/**
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 * @return
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			// Perform simple checking
			for (int i = 0; i < possibleMoves.length; i++) {
				if (possibleMoves[i][0] == x2 - x1 && possibleMoves[i][1] == y2 - y1) {
					if (checkBasicMove (chessModel, x1, y1, x2, y2))
						return true;
				}
			}

			return false;
		}
	}

	/**
	 * White pawn (can only move forward 1 or 2 places).
	 */
	public class WhitePawn extends AbstractChessPiece {
		public WhitePawn () {
			super (null);
		}

		/** Pawns have more complex moving/attacking so overwrite the
		 * movePiece function.
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			// simple move forward (2 spaces or 1)
			// PAWN CAN KILL BY MOVING FORWARD 2
			if (checkBasicMove(chessModel, x1, y1, x2, y2) && (
					(y1 == 6 && x1 == x2 && y2 == 4 && chessModel.getPiece(x1, 5) == EMPTY) ||	// more forward 2 spaces
					(x1 == x2 && y2 == y1 - 1) // simple move forward one
				) &&
				chessModel.getPiece(x2, y2) == EMPTY
			) return true;

			// check if pawn is trying to capture an enemy piece
			else if (isAttackingMove(chessModel, x1, y1, x2, y2)) return true;

			return false;
		}

		/**
		 * A pawn is unique as its attacking moves are seperate from actually
		 * normal movements.  It is necessary to split this into 2 seperate
		 * methods as only the isAttackingMove method for a pawn is required
		 * when checking if a peice puts a King in check or not.
		 *
		 * @param x1
		 * @return
		 */
		public boolean isAttackingMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			return (
				y2 == y1 - 1 && (x2 == x1 - 1 || x2 == x1 + 1) &&
				chessModel.getPieceColour(x2, y2) == ChessModel.PLAYER_TWO
			) || isEnPassant(chessModel, x1, y1, x2, y2);
		}

        /**
         * Checks for en passant capture. The en passant rule applies when a
         * player moves a pawn two squares forward from its starting position,
         * and an opposing pawn could have captured it if it had only moved one
         * square forward. The rule states that the opposing pawn may then
         * capture the pawn as if it had only moved one square forward. The
         * resulting position is the same as if the pawn had only moved one
         * square forward and then the opposing pawn had captured as normal.
         * En passant must be done on the very next turn, or the right to do
         * so is lost. (http://en.wikipedia.org/wiki/En_passant)
         */
        public boolean isEnPassant (ChessModel chessModel, int x1, int y1, int x2, int y2) {
            return
                // Check enemy move pawn two squares
				chessModel.flag(FLAG_B_PAWN_TWO_SQUARE_MOVED + x2) &&
                // attacking at right row (could be removed)
                y2 == 2 &&
                // is attack?
				y2 == y1 - 1 && (x2 == x1 - 1 || x2 == x1 + 1) &&
                // backward row we attacking there are pawn
				chessModel.getPiece(x2, y2 + 1) == ChessModel.B_PAWN &&
                // and additional check of possible pawns at 5th and 7th rows
                // (inserted for safety, could be removed)
				chessModel.getPiece(x2, 1) != ChessModel.B_PAWN
                ;
        }
	}

	/**
	 * Black pawn (can only move forward 1 or 2 places).
	 */
	public class BlackPawn extends AbstractChessPiece {
		boolean firstMoveTaken = false;

		public BlackPawn () {
			super (new int [][] {{0, 1}, {0, 2}});
		}

		/**
		 * Pawns have more complex moving/attacking so overwrite the
		 * movePiece function.
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			// simple move forward (2 spaces or 1)
			if (checkBasicMove (chessModel, x1, y1, x2, y2) && (
					(y1 == 1 && x1 == x2 && y2 == 3 && chessModel.getPiece(x1, 2) == EMPTY ) ||	// more forward 2 spaces
					(x1 == x2 && y2 == y1 + 1)
				) &&	// simple move forward one
				chessModel.getPiece(x2, y2) == EMPTY
			) return true;

			// check if pawn is trying to capture an enemy piece
			else if (isAttackingMove (chessModel, x1, y1, x2, y2)) return true;

			return false;
		}

		/**
		 */
		public boolean isAttackingMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			return (
				y2 == y1 + 1 && (x2 == x1 - 1 || x2 == x1 + 1) &&
				chessModel.getPieceColour(x2, y2) == ChessModel.PLAYER_ONE
			) || isEnPassant(chessModel, x1, y1, x2, y2);
		}

        public boolean isEnPassant (ChessModel chessModel, int x1, int y1, int x2, int y2) {
            return
				chessModel.flag(FLAG_W_PAWN_TWO_SQUARE_MOVED + x2) &&
                y2 == 5 &&
				y2 == y1 + 1 && (x2 == x1 - 1 || x2 == x1 + 1) &&
				chessModel.getPiece(x2, y2 - 1) == ChessModel.W_PAWN &&
				chessModel.getPiece(x2, 6) != ChessModel.W_PAWN
                ;
        }
	}

	/** Knight
	 */
	public class Knight extends AbstractChessPiece {
		public Knight () {
			super (new int [][] {{1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}});
		}
	}

	/** Bishop
	 */
	public class Bishop extends AbstractChessPiece {
		public Bishop () {super (null);	}

		/** This needs overwritten a ...
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			if (checkBasicMove(chessModel, x1, y1, x2, y2)) {
				if (Math.abs(x2 - x1) == Math.abs(y2 - y1)) {
					if (checkLineOfSight (chessModel, x1, y1, x2, y2))
						return true;
				}
			}
			return false;
		}
	}

	/** Rook
	 */
	public class Rook extends AbstractChessPiece {
		public Rook () {super (null);}

		/** This needs overwritten a ...
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			if (checkBasicMove(chessModel, x1, y1, x2, y2)) {
				if (x1 == x2 || y1 == y2) {
					if (checkLineOfSight (chessModel, x1, y1, x2, y2))
						return true;
				}
			}

			return false;
		}
	}

	/** Queen
	 */
	public class Queen extends AbstractChessPiece {
		public Queen () {super (null);}

		/** This needs overwritten a ...
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			if (checkBasicMove(chessModel, x1, y1, x2, y2)) {
				if (checkLineOfSight (chessModel, x1, y1, x2, y2)) {
					return true;
				}
			}

			return false;
		}
	}

	// note a king cannot be in check
	public abstract class King extends AbstractChessPiece {
		public King () {
			super (new int [][] {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}});
		}
	}

	/** White king - is there a link between the king and the rook
	 */
	public class WhiteKing extends King {
		public WhiteKing () {}

		/** This needs overwritten a ...
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			// check to see if king isn't in starting position (he's moved)
			if (chessModel.getPiece(4, 7) != W_KING)
				chessModel.setFlag(IChessModel.FLAG_W_KING_HASNT_MOVED, false);

			// check castling first of all
			if (chessModel.flag (IChessModel.FLAG_W_KING_HASNT_MOVED) &&
				checkBasicMove(chessModel, x1, y1, x2, y2) &&
				y2 == 7 &&
				!isAttackedBy (chessModel, x1, y1, ChessModel.PLAYER_TWO)
			) {

				if (chessModel.flag (IChessModel.FLAG_W_L_ROOK_HASNT_MOVED) && x2 == 2) {
					if (chessModel.getPiece(1,7) == EMPTY &&
						chessModel.getPiece(2,7) == EMPTY &&
						chessModel.getPiece(3,7) == EMPTY &&
						!isAttackedBy (chessModel, 2, 7, ChessModel.PLAYER_TWO) &&
						!isAttackedBy (chessModel, 3, 7, ChessModel.PLAYER_TWO)
					)
						return true;		// castle queen side
				}
				if (chessModel.flag (IChessModel.FLAG_W_R_ROOK_HASNT_MOVED) && x2 == 6) {
					if (chessModel.getPiece(5,7) == EMPTY &&
						chessModel.getPiece(6,7) == EMPTY &&
						!isAttackedBy (chessModel, 5, 7, ChessModel.PLAYER_TWO) &&
						!isAttackedBy (chessModel, 6, 7, ChessModel.PLAYER_TWO)
					)
					return true;			// castle king side
				}
			}

			// Check that the piece can move and that it doesn't move into
			if (super.isPossibleMove (chessModel, x1, y1, x2, y2) == false)
				return false;
			if (isAttackedBy (chessModel, x2, y2, ChessModel.PLAYER_TWO))
				return false;

			return true;
		}
	}

	/** Black king
	 */
	public class BlackKing extends King {
		/** This needs overwritten a ...
		 */
		public boolean isPossibleMove (ChessModel chessModel, int x1, int y1, int x2, int y2) {
			// check to see if king isn't in starting position (he's moved)
			if (chessModel.getPiece(4, 0) != B_KING)
				chessModel.setFlag(IChessModel.FLAG_B_KING_HASNT_MOVED, false);

			// check castling first of all
			if (chessModel.flag (IChessModel.FLAG_B_KING_HASNT_MOVED) &&
				checkBasicMove(chessModel, x1, y1, x2, y2) &&
				y2 == 0
				&& !isAttackedBy (chessModel, x1, y1, ChessModel.PLAYER_ONE)
			) {
				if (chessModel.flag (IChessModel.FLAG_B_L_ROOK_HASNT_MOVED) && x2 == 2) {
					if (chessModel.getPiece(1,0) == EMPTY &&
						chessModel.getPiece(2,0) == EMPTY &&
						chessModel.getPiece(3,0) == EMPTY &&
						!isAttackedBy (chessModel, 2, 0, ChessModel.PLAYER_ONE) &&
						!isAttackedBy (chessModel, 3, 0, ChessModel.PLAYER_ONE)
					)
						return true;
				}
				if (chessModel.flag (IChessModel.FLAG_B_R_ROOK_HASNT_MOVED) && x2 == 6) {
					if (chessModel.getPiece(5,0) == EMPTY &&
						chessModel.getPiece(6,0) == EMPTY &&
						!isAttackedBy (chessModel, 5, 0, ChessModel.PLAYER_ONE) &&
						!isAttackedBy (chessModel, 6, 0, ChessModel.PLAYER_ONE)
					)
					return true;
				}
			}

			// Check that the piece can move and that it doesn't move into a check position
			if (super.isPossibleMove (chessModel, x1, y1, x2, y2) == false)
				return false;
			if (isAttackedBy (chessModel, x2, y2, ChessModel.PLAYER_ONE))
				return false;

			return true;
		}
	}
}
