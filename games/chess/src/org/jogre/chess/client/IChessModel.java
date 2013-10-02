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

import org.jogre.client.IJogreModel;

/**
 * @author  Bob Marks
 * @version Alpha 0.2.3
 *
 * Interface to the chess game model which components can interface though.
 */
public interface IChessModel extends IJogreModel {

	public static final int PLAYER_ONE = 0;
	public static final int PLAYER_TWO = 1;

	// Constants to declare the positions of the flags in the boolean array
	public static final int NUM_OF_FLAGS = 6 + 16;
	public static final int FLAG_W_KING_HASNT_MOVED = 0;
	public static final int FLAG_W_L_ROOK_HASNT_MOVED = 1;
	public static final int FLAG_W_R_ROOK_HASNT_MOVED = 2;
	public static final int FLAG_B_KING_HASNT_MOVED = 3;
	public static final int FLAG_B_L_ROOK_HASNT_MOVED = 4;
	public static final int FLAG_B_R_ROOK_HASNT_MOVED = 5;
    public static final int FLAG_W_PAWN_TWO_SQUARE_MOVED = 6; // 6 - 13
    public static final int FLAG_B_PAWN_TWO_SQUARE_MOVED = 14; // 14 - 21

	// Declare constants for the various pieces
	public static final int EMPTY = 0;

	// Numbers of each of the pieces
	public static final int W_PAWN = 1;		// Declare the white pieces
	public static final int W_KNIGHT = 2;
	public static final int W_BISHOP = 3;
	public static final int W_ROOK = 4;
	public static final int W_QUEEN = 5;
	public static final int W_KING = 6;
	public static final int B_PAWN = 7;		// Declare the black pieces
	public static final int B_KNIGHT = 8;
	public static final int B_BISHOP = 9;
	public static final int B_ROOK = 10;
	public static final int B_QUEEN = 11;
	public static final int B_KING = 12;

	// String representation of the pieces
	public static final String [] PIECES_STR = {
		"Empty",
		"White Pawn", "White Knight", "White Bishop", "White Rook",
		"White Queen", "White King",
		"Black Pawn", "Black Knight", "Black Bishop", "Black Rook",
		"Black Queen", "Black King"
	};

	// Shorter string representation of the pieces
	public static final String [] PIECES_STR_ABRV = {
		"  ",
		"WP", "WK", "WB", "WR", "WQ", "WK",
		"BP", "BK", "BB", "BR", "BQ", "BK"
	};

	// Declare the starting positions of the pieces
	public static final int [] START_POSITIONS = {
		B_ROOK, B_KNIGHT, B_BISHOP, B_QUEEN, B_KING, B_BISHOP, B_KNIGHT, B_ROOK,
		B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN,
		EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
		EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN,
		W_ROOK, W_KNIGHT, W_BISHOP, W_QUEEN, W_KING, W_BISHOP, W_KNIGHT, W_ROOK
	};

	// Declare test start pieces to aid debugging
	public static final int [] TEST_START_POSITIONS = {
		EMPTY,  EMPTY,  EMPTY,  EMPTY, B_KING, EMPTY,  EMPTY,  EMPTY,
		W_ROOK, EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
		EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
		EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,
	    EMPTY, W_QUEEN, EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY,  EMPTY
	};
}
