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

// Camelot location class
public class CamelotLoc {

	// A location is a set of (i,j) indexes
	private int i, j;

	/**
	 * Constructor that creates an empty location
	 */
	public CamelotLoc () {
		this.i = -1;
		this.j = -1;
	}

	/**
	 * Constructor that creates a location given values
	 *
	 * @param	(i,j)		Indexes of the location
	 */
	public CamelotLoc (int i, int j) {
		this.i = i;
		this.j = j;
	}

	/**
	 * Constructor that creates a location given an existing location
	 *
	 * @param	loc			The location to copy
	 */
	public CamelotLoc (CamelotLoc loc) {
		this.i = loc.get_i();
		this.j = loc.get_j();
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
	 * Compare a location to another and determine if they are the same
	 *
	 * @params	otherLoc	The other location to compare against
	 * @return				true = locations are equal
	 * @return				false = locations are not equal
	 */
	public boolean equals (CamelotLoc otherLoc) {
		if ((this.i == otherLoc.get_i()) &&
			(this.j == otherLoc.get_j()) ) {
			return true;
		}
		return false;
	}

}
