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

/**
 * A piece on the board for the Points Total game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalPiece {

	// The owner of the piece.
	public int owner;

	// The value of the piece.
	public int value;

	// The rotation of the piece, in 90-degree increments clockwise
	public int rotation;

	/**
	 * Constructor for a piece.
	 */
	public PointsTotalPiece (int owner, int value, int rotation) {
		this.owner = owner;
		this.value = value;
		this.rotation = rotation;
	}

	// Set values
	public void setOwner    (int newOwner) { owner    = newOwner;  }
	public void setValue    (int newValue) { value    = newValue;  }
	public void setRotation (int newRot)   { rotation = newRot; }

	/*
	 * Determine if the piece has an arrow pointing in the given
	 * direction.
	 *
	 * @param dir   The direction (0=Up, 1=Right, 2=Down, 3=Left)
	 * @return if the piece has an arrow pointing in that direction.
	 */
	public boolean isPointing(int dir) {
		if (value == 0) {
			// The 0 piece points all directions
			return true;
		}
			// un-rotate the piece
		int absDir = (dir - rotation) & 0x03;
		if (value == 1) {
			return (absDir != 2);
		}
		if (value == 3) {
			return (absDir == 0);
		}
		return ((absDir & 0x01) != 0);
	}
}

