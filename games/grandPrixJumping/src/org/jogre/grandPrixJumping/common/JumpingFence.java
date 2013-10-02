/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.common;

import org.jogre.common.util.JogreUtils;

/**
 * Structure to hold info about a fence for Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public final class JumpingFence {

	// Features of the fence
	private int location;
	private int height;
	private int type;

	private final static int WATER_FENCE_TYPE = 6;

	// Fault points used by this jump.  This is used for putting
	// the point caution signs on the board.
	private int faultPoints;

	/**
	 * Constructor for a fence
	 */
	public JumpingFence(int location, int type) {
		this.location = location;
		this.faultPoints = -1;
		setType(type);
	}

	/**
	 * Retrieve info about the fence
	 */
	public int location() { return location; }
	public int height()   { return height; }
	public int type()     { return type; }

	/**
	 * Set the type of jump.
	 *
	 * @param	newType		The new type of jump this is.
	 */
	public void setType(int newType) {
		this.type = newType;
		this.height = (newType == WATER_FENCE_TYPE) ? 1 : newType;
	}

	/**
	 * Return the number of fault points associated with this fence.
	 */
	public int faultPoints() { return faultPoints; }

	/**
	 * Set the number of fault points associated with this fence.
	 */
	public void setFaultPoints(int newPoints) {
		faultPoints = newPoints;
	}

	/**
	 * Clear the number of fault points to an invalid value.
	 */
	public void clearFaultPoints() {
		faultPoints = -1;
	}

	/**
	 * Reset the number of fault points to the height value.
	 */
	public void resetFaultPoints() {
		if (type == WATER_FENCE_TYPE) {
			// Water fences start with 5 fault points
			faultPoints = 5;
		} else {
			faultPoints = height;
		}
	}

	/**
	 * Reduce the fault points by the given amount.
	 *
	 * @param	amount		The amount to reduce the fault points by
	 */
	public void reduceFaultPoints(int amount) {
		if ((type == WATER_FENCE_TYPE) && (amount > 0)) {
			// Any reduction in fault points on a water fence reduce the result
			// to zero.
			faultPoints = 0;
		} else {
			faultPoints = Math.max(0, faultPoints - amount);
		}
	}

	/**
	 * Return if this fence is a water jump.
	 *
	 * @return if this fence is a water jump.
	 */
	public boolean isWaterJump() {
		return (type == WATER_FENCE_TYPE);
	}
}
