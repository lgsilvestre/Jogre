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
package org.jogre.common.comm;

import java.util.Vector;

import nanoxml.XMLElement;

/**
 * Communication object which will send a XMLElement as a child
 * from a client to server (or or vice versa).  Used in the JogreController
 * to simplify the sending and receiving of more complex data.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class CommControllerObject extends CommTableMessage {

	/**
	 * XMLElement of data.
	 */
	protected XMLElement data;

	/**
	 * Empty Constructor.  This class must use the setData method (this constructor
	 * cannot take an XMLElement as this would conflict with the parse constructor).
	 */
	public CommControllerObject () {
		super ();
	}

	/**
	 * Constructor which takes an XMLElement.
	 *
	 * @param message
	 */
	public CommControllerObject (XMLElement message) {
		super (message);

		Vector children = message.getChildren();
		if (children.size() == 1) {	// should have one child as its data
			this.data = (XMLElement)children.get(0);
		}
	}

	/**
	 * Set data.
	 *
	 * @param data
	 */
	public void setData (XMLElement data) {
		this.data = data;				// set data
	}

	/**
	 * Return the data back to the user.
	 *
	 * @return   Data as XML object.
	 */
	public XMLElement getData () {
		return data;
	}

	/**
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = flatten (Comm.CONTROLLER_OBJECT);
		message.addChild (data);

		return message;
	}
}
