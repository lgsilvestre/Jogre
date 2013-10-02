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

/*	A step is a set of source & destination locations along with a step type
	and direction.

	A move is a list of steps.
*/

// Camelot step class
public class CamelotStep {

	// A step is a set of source & destination locations along with
	// a step type and direction
	private CamelotLoc from, to;
	private int type;
	private int direction;
	private int captured_piece;

	// Definitions for type
	public static final int WALK = 1;
	public static final int JUMP = 2;
	public static final int CAPTURE = 3;

	/**
	 * Constructor
	 */
	public CamelotStep (CamelotLoc from, CamelotLoc to, int type, int direction) {
		this.from = from;
		this.to = to;
		this.type = type;
		this.direction = direction;
		this.captured_piece = CamelotModel.PLAYER_NONE;
	}

	/**
	 * Constructor
	 */
	public CamelotStep (CamelotLoc from, CamelotLoc to, int type, int direction, int captured_piece) {
		this.from = from;
		this.to = to;
		this.type = type;
		this.direction = direction;
		this.captured_piece = captured_piece;
	}

	/**
	 * Constructor that creates a move given coordianates
	 */
	public CamelotStep (int from_i, int from_j, int to_i, int to_j, int type, int direction, int captured_piece) {
		this (new CamelotLoc(from_i, from_j), new CamelotLoc(to_i, to_j), type, direction, captured_piece);
	}

	/**
	 * Constructor that creates a move given an existing move
	 *
	 * @param	step		The step to copy
	 */
	public CamelotStep (CamelotStep step) {
		this.from = step.get_from();
		this.to = step.get_to();
		this.type = step.get_type();
		this.direction = step.get_direction();
		this.captured_piece = step.get_captured_piece();
	}

	/**
	 * Methods to return fields of the step
	 */
	public CamelotLoc	get_from () {return (from);}
	public CamelotLoc	get_to ()	{return (to);}
	public int			get_type () {return (type);}
	public int			get_direction() {return (direction);}
	public int			get_captured_piece() {return (captured_piece);}
}
