/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
 * Copyright (C) 2003 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.triangulum.server;

import org.jogre.triangulum.common.TriangulumModel;
import org.jogre.triangulum.common.TriangulumPiece;

import java.util.Collections;
import java.util.Vector;

/**
 * Server model for the Triangulum game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumServerModel extends TriangulumModel {

	// The tiles that haven't been used yet in the game.
	Vector tileBag = new Vector();

	// The index in the tileBag where the next tile will be drawn from.
	int nextTileIndex = 0;

	/**
	 * Constructor for the Triangulum server model.
	 * Note: a new server model is created when each game starts.
	 *
	 * @param flavor      The type of game to be played.
	 *                    0 = 36 piece game
	 *                    1 = 60 piece game
	 * @param numPlayers  The number of players in the game.
	 */
	public TriangulumServerModel (int flavor, int numPlayers) {
		super(flavor, numPlayers);

		// Do server initialization
		createAllTiles();

		Collections.shuffle(tileBag);
		nextTileIndex = 0;

		// Deal out the initial hand of tiles for each player.
		for (int p=0; p<numPlayers; p++) {
			for (int i=0; i<piecesPerPlayer; i++) {
				givePiece(p, getNextPieceFromBag(), i);
			}
		}

	}

	/**
	 * Get the next tile from the tileBag.
	 */
	public TriangulumPiece getNextPieceFromBag () {
		if (nextTileIndex < tileBag.size()) {
			TriangulumPiece nextTile = (TriangulumPiece) tileBag.get(nextTileIndex);
			nextTileIndex += 1;
			return nextTile;
		}
		return null;
	}

	/**
	 * Put all tiles into the tileBag.
	 */
	private void createAllTiles () {
		// Get the correct list of tiles
		int [][] tileList = (flavor == 36) ? tileList_36 : tileList_60;

		// Put all of the tiles into the bag.
		for (int i=0; i<tileList.length; i++) {
			tileBag.add(new TriangulumPiece(
			                         tileList[i][0],   // Value
			                         tileList[i][3],   // Bottom
			                         tileList[i][1],   // Left
			                         tileList[i][2])); // Right
		}
	}

	// The list of all tiles in the 36 tile game
	private final static int [][] tileList_36 = {
	// {value, left, right, bottom}
		{1, 6, 6, 6},
		{1, 2, 1, 4},
		{1, 3, 1, 2},
		{1, 3, 4, 0},
		{2, 0, 2, 4},
		{2, 3, 2, 0},
		{2, 4, 0, 1},
		{3, 0, 2, 1},
		{3, 1, 0, 3},
		{3, 4, 2, 3},
		{3, 4, 3, 1},
		{4, 0, 0, 1},
		{4, 0, 0, 4},
		{4, 1, 1, 2},
		{4, 1, 1, 3},
		{4, 2, 2, 0},
		{4, 2, 2, 4},
		{4, 3, 3, 0},
		{4, 3, 3, 2},
		{4, 4, 4, 1},
		{4, 4, 4, 3},
		{5, 0, 0, 2},
		{5, 0, 0, 3},
		{5, 1, 1, 0},
		{5, 1, 1, 4},
		{5, 2, 2, 1},
		{5, 2, 2, 3},
		{5, 3, 3, 1},
		{5, 3, 3, 4},
		{5, 4, 4, 0},
		{5, 4, 4, 2},
		{6, 0, 0, 0},
		{6, 1, 1, 1},
		{6, 2, 2, 2},
		{6, 3, 3, 3},
		{6, 4, 4, 4}
	};

	// The list of all tiles in the 60 tile game
	private final static int [][] tileList_60 = {
	// {value, left, right, bottom}
		{1, 5, 1, 4},
		{1, 4, 1, 3},
		{1, 3, 1, 0},
		{1, 1, 2, 0},
		{1, 1, 2, 4},
		{1, 2, 0, 5},
		{1, 2, 4, 5},
		{1, 2, 1, 5},
		{1, 2, 4, 3},
		{1, 2, 0, 4},
		{2, 1, 5, 3},
		{2, 0, 2, 3},
		{2, 1, 2, 3},
		{2, 6, 6, 5},
		{2, 6, 6, 4},
		{2, 5, 1, 0},
		{2, 4, 1, 0},
		{2, 5, 4, 0},
		{2, 5, 3, 2},
		{2, 3, 4, 0},
		{2, 5, 0, 3},
		{2, 5, 4, 3},
		{3, 1, 1, 0},
		{3, 2, 2, 3},
		{3, 0, 0, 5},
		{3, 4, 4, 1},
		{3, 5, 5, 4},
		{3, 3, 3, 1},
		{3, 0, 0, 2},
		{4, 4, 4, 0},
		{4, 4, 4, 2},
		{4, 5, 5, 3},
		{4, 0, 0, 3},
		{4, 5, 5, 1},
		{4, 3, 3, 4},
		{4, 1, 1, 2},
		{4, 2, 2, 5},
		{4, 6, 2, 1},
		{4, 6, 3, 0},
		{5, 2, 2, 1},
		{5, 1, 1, 4},
		{5, 4, 4, 3},
		{5, 0, 0, 4},
		{5, 3, 3, 0},
		{5, 5, 5, 0},
		{5, 1, 1, 5},
		{5, 5, 5, 2},
		{5, 2, 2, 0},
		{6, 2, 2, 4},
		{6, 3, 3, 2},
		{6, 0, 0, 1},
		{6, 3, 3, 5},
		{6, 4, 4, 5},
		{6, 1, 1, 3},
		{7, 4, 4, 4},
		{7, 5, 5, 5},
		{7, 0, 0, 0},
		{7, 3, 3, 3},
		{7, 2, 2, 2},
		{7, 1, 1, 1}
	};
}
