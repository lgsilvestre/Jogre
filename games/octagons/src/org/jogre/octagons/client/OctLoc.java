/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
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
package org.jogre.octagons.client;

// Octagon location class
public class OctLoc {

	// Declare constants for elements
	public static final int OCT_1 = 0;
	public static final int OCT_2 = 1;
	public static final int SQUARE = 2;
	public static final int NOWHERE = 3;

	// A location is a set of (i,j) indexes and an element setting
	private int i, j, element;

	/**
	 * Constructor that creates an empty location
	 */
	public OctLoc () {
		this.i = -1;
		this.j = -1;
		this.element = NOWHERE;
	}

	/**
	 * Constructor that creates a location given values
	 *
	 * @param	(i,j)		Indexes of the location
	 * @param	element		The element of the location
	 */
	public OctLoc (int i, int j, int element) {
		this.i = i;
		this.j = j;

			/* Make sure the element is within bounds */
		if ((element < OCT_1) || (element > SQUARE)) {
			this.element = NOWHERE;
		} else {
			this.element = element;
		}
	}

	/**
	 * Constructor that creates a location given an existing location
	 *
	 * @param	loc			The location to copy
	 */
	public OctLoc (OctLoc loc) {
		this.i = loc.get_i();
		this.j = loc.get_j();
		this.element = loc.get_element();
	}

	/**
	 * Method to return the i index of a location
	 *
	 * @return				The i index
	 */
	public int get_i () {
		return i;
	}

	/**
	 * Method to return the j index of a location
	 *
	 * @return				The j index
	 */
	public int get_j () {
		return j;
	}

	/**
	 * Method to return the element of a location
	 *
	 * @return				The element
	 */
	public int get_element () {
		return element;
	}

	/**
	 * Compare a location to another and determine if they are the same
	 *
	 * @params	otherLoc	The other location to compare against
	 * @return				true = locations are equal
	 * @return				false = locations are not equal
	 */
	public boolean equals (OctLoc otherLoc) {
		if ((this.i == otherLoc.get_i()) &&
			(this.j == otherLoc.get_j()) &&
			(this.element == otherLoc.get_element())) {
			return true;
		}
		return false;
	}
}
