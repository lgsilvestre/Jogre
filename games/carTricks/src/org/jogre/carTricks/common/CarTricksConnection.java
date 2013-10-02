/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter
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
import org.jogre.common.util.JogreUtils;

// A connection keeps information about which locations are accessable from
// a given location.
// Note: The internal data structure is mostly the same as a CarTricksPath,
// just with with different semantics.  So, this is implemented as a wrapper
// around a CarTricksPath.
public class CarTricksConnection {

	// The path that is the list of next locations
	private CarTricksPath thePath;

	// The current index in the connection;
	private int curr_index;

	/**
	 * Constructor for an empty connection
	 */
	public CarTricksConnection (int max_followers) {
		this.thePath = new CarTricksPath(max_followers);
		this.curr_index = 0;
	}

	/**
	 * Constructor for a connection given an existing connection
	 *
	 * @param	connection		The existing connection
	 */
	public CarTricksConnection (CarTricksConnection connection) {
		this.thePath = new CarTricksPath(connection.getPath());
		this.curr_index = 0;
	}

	/**
	 * Constructor for a connection given the max number of followers
	 * and a string with space-delimted location id's
	 *
	 * @param	max_followers	The max. # of followers
	 * @param	follower		String with follower id's
	 */
	public CarTricksConnection (int max_followers, String initial_ids) {
		this(max_followers);

		int [] initial_vals = JogreUtils.convertToIntArray (initial_ids);
		for (int i=0; i<initial_vals.length; i++) {
			this.thePath.addLoc(initial_vals[i]);
		}
	}

	/**
	 * Return the path that makes the Connection.
	 *
	 * @return	the path that makes up this connection
	 */
	private CarTricksPath getPath () {
		return thePath;
	}

	/**
	 * Return the location that is the next next-link.  This also advances to the next
	 * index;
	 *
	 * @return the next link
	 */
	public int nextLink() {
		int ret;

		if (curr_index < thePath.pathLength()) {
			// There are still links to return.
			ret = thePath.getLoc(curr_index);
			curr_index += 1;
		} else {
			// We're at the end
			ret = -1;
		}

		return ret;
	}

	/**
	 * Reset the index back to 0 so that nextLink() will start from the beginning again
	 *
	 */
	public void resetLinks() {
		curr_index = 0;
	}

	/**
	 * Add a location to a connection.
	 *
	 * @param	location	The location to add to the connection
	 */
	public void addLoc(int location) {
		thePath.addLoc(location);
	}

	/**
	 * Determine if the given location is in the connection.
	 *
	 * @param	location	The location to look for in the connection
	 */
	public boolean connectsTo(int location) {
		return thePath.contains(location);
	}
}
