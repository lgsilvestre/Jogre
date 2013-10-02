/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.common;

import java.util.Observable;

import nanoxml.XMLElement;

import org.jogre.client.IJogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;

/**
 * <p>Abstract class which holds the state/model of a particular game when
 * extended. This class extends the Observable class so that JogreComponent
 * classes can update themselfs depending on changes made in this classes.
 * </p>
 *
 * <p>For a very simple example, to create the model for a game of
 * tic-tac-toe a class TicTacToeModel should extend this class and add a 3x3
 * two dimensional int array i.e.</p>
 * 
 * <pre>int [][] = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}.</pre>
 *
 * <p>A model can also store itself on a JogreServer.  To set the state of a 
 * JogreModel the setState (XMLElement message) method must be overwritten.  
 * The constructor wasn't used (like the constructor of Comm objects) as 
 * this reference must be kept.</p>
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public abstract class JogreModel extends Observable implements IJogreModel, ITransmittable {

	/** Game type from IJogreModel e.g. GAME_TYPE_TURN_BASED or GAME_TYPE_REAL_TIME. */
	protected int gameType;

	/**
	 * <p>Set the state of the model (set values of the model equal to the
	 * values of this model by copying its values.  The inString is created
	 * from the flatten () method of JogreModel.  The implementation of this
	 * method in the subclass should call the refreshObservers() method so that
	 * the screen gets updated correctly.</p>
	 *
	 * @param message   XMLElement created from flatten () method of this object.
	 * @throws TransmissionException
	 */
	public abstract void setState (XMLElement message);

	/**
	 * <p>Returns the state/snapshot of a particular game.</p>
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public abstract XMLElement flatten ();
	
	/**
	 * Blank constructor which can be called by a sub class.
	 */
	protected JogreModel () {
	    // Must be overwritten in sub class if used.
	}

	/**
	 * Create a new jogre model which sets the game type.
	 *
	 * @param gameType
	 */
	public JogreModel (int gameType) {
		super();			// to Observable class

		this.gameType = gameType;
	}

	/**
	 * Refresh observers - calls the setChanged() and notifyObservers ()
	 * methods in the Observable class.
	 */
	public void refreshObservers () {
		setChanged();
		notifyObservers();	// notify any class which observe this class
	}
}