/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.camelot.client;

import java.util.Vector;

// Camelot move family class
public class CamelotMoveFamily {

	// A move family is a set of sequence of hops that ends at a common board
	// space, but takes different paths to get there.

	// This is the location that the family ends at
	private CamelotLoc end;

	// The list of moves that end at <end>
	private Vector moves;

	// The current index move
	private int curr_index;

	/**
	 * Constructor for an empty family that has no moves
	 */
	public CamelotMoveFamily (CamelotLoc end) {
		this.end = end;
		this.moves = new Vector();
		this.curr_index = 0;
	}

	/**
	 * Constructor for a new family that will have the given move
	 */
	public CamelotMoveFamily (CamelotLoc end, Vector move) {
		this(end);
		addMove(move);
	}

	/**
	 * Return the location that the family ends at
	 *
	 */
	public CamelotLoc getEnd() {
		return (end);
	}

	/**
	 * Determine if the family ends at the given location
	 *
	 * @param	endLoc		The ending location
	 * @return	true/false
	 */
	public boolean endsAt(CamelotLoc endLoc) {
		return (endLoc.equals(this.end));
	}

	/**
	 * Add the given move to the family.  This clones the <move> Vector
	 *
	 * @param	move		The move to add
	 */
	public void addMove(Vector move) {
		this.moves.add(move.clone());
	}

	/**
	 * Return a move in the family.
	 *
	 * @param	advance		Indicates if the current move should be
	 *						advanced before returning a move.
	 * @return	The next move in the family
	 */
	public Vector nextMove(boolean advance) {
		if (advance) {
			curr_index += 1;
			if (curr_index == moves.size()) {
				curr_index = 0;
			}
		}

		return ( (Vector) moves.elementAt(curr_index));
	}
}
