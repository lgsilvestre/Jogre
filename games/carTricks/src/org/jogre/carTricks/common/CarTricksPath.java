/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.carTricks.common;

import java.util.Vector;
import java.util.ListIterator;

// A path is a list of locations that a car can drive along.
public class CarTricksPath {

	// This array holds the spaces that are traversed (in order) for this path.
	private int [] theLocs;

	// This holds the current length of the path
	private int length;

	// This holds the max length of the path
	private int max_length;

	/**
	 * Constructor for an empty path
	 */
	public CarTricksPath (int max_length) {
		int i;

		// Create a new array for the locations
		this.theLocs = new int [max_length];

		// Fill the array with empty locations
		for (i=0; i < max_length ; i++) {
			this.theLocs[i] = -1;
		}

		// The path length is currently 0.
		this.length = 0;
		this.max_length = max_length;
	}

	/**
	 * Constructor for a path given an existing path
	 *
	 * @param	oldPath		The existing path
	 */
	public CarTricksPath (CarTricksPath oldPath) {
		int length = oldPath.pathLength();
		int max_len = oldPath.maxLength();

		// Create an empty path;
		this.theLocs = new int [max_len];
		this.length = length;
		this.max_length = max_len;

		copyAndClear(oldPath, length, this);
	}

	/**
	 * Constructor for a path given an existing path and a new maximum length to copy.
	 * If the old path is shorter than newMaxLength, then extra spaces are set to -1.
	 * If the old path is longer than newMaxLength, then the new path is clipped at newMaxLength.
	 *
	 * @param	oldPath			The existing path
	 * @param	newMaxLength	The length of the new path.
	 */
	public CarTricksPath (CarTricksPath oldPath, int newMaxLength) {
		this.theLocs = new int [newMaxLength];
		this.max_length = newMaxLength;

		if (newMaxLength > oldPath.max_length) {
			// The new path is longer than the old one, so copy the old into the new
			this.length = oldPath.length;
			copyAndClear(oldPath, oldPath.length, this);
		} else {
			// The new path is shorted than the old one, so truncate
			this.length = Math.min(oldPath.length, newMaxLength);
			copyAndClear(oldPath, this.length, this);
		}
	}

	/**
	 * Copy some amount of a path from srcPath into destPath, and clear the rest
	 * of destPath.
	 *
	 * @param	srcPath		The source path
	 * @param	srcLen		The amount of data to copy from srcPath into destPath
	 * @param	destPath	The destination path
	 */
	private void copyAndClear(CarTricksPath srcPath, int srcLen, CarTricksPath destPath) {
		// Copy the data from the srcPath to the new one
		for (int i = 0; i < srcLen; i++) {
			destPath.theLocs[i] = srcPath.theLocs[i];
		}

		// If destPath is longer, clear the rest of it
		if (destPath.max_length > srcLen) {
			for (int i = srcLen; i < destPath.max_length; i++) {
				destPath.theLocs[i] = -1;
			}
		}
	}

	/**
	 * Constructor for a path given an array of locations
	 */
	public CarTricksPath (int [] locArray) {
		this.theLocs = locArray;
		this.length = locArray.length;
		this.max_length = locArray.length;
	}

	/**
	 * Return the location array.
	 */
	public int [] getLocationArray() {
		return theLocs;
	}

	/**
	 * Return the length of a path
	 */
	public int pathLength() {
		return length;
	}

	/**
	 * Return the max length of a path
	 */
	public int maxLength() {
		return max_length;
	}

	/**
	 * Return a location of the path
	 *
	 * @param	index		The index of the path to get
	 * @return the location at the given index
	 */
	public int getLoc(int index) {
		return theLocs[index];
	}

	/**
	 * Return the last location in the path
	 */
	public int getTerminal() {
		if (length == 0) {
			return (-1);
		}
		return (theLocs[length-1]);
	}

	/**
	 * Add a location to a path.
	 *
	 * @param	location	The location to add to the path
	 */
	public void addLoc(int location) {
		theLocs[length] = location;
		length += 1;
	}

	/**
	 * Remove the last location from a path.
	 */
	public void popLoc() {
		length -= 1;
		if (length >= 0) {
			theLocs[length] = -1;
		}
	}

	/**
	 * Determine if the path contains the given location in it
	 */
	public boolean contains(int location) {
		for (int i=0; i<length; i++) {
			if (theLocs[i] == location) {
				return true;
			}
		}
		return false;
	}

}
