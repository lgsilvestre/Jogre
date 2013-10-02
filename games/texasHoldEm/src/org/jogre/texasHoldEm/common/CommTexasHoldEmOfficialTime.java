/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for sending the clients the official blind stage and
 * time until the next blind change for TexasHoldEm.
 *
 * @author Richard Walter
 * @version Alpha 0.1
 */
public class CommTexasHoldEmOfficialTime extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "time";
	public static final String XML_ATT_STAGE = "s";
	public static final String XML_ATT_TIME = "t";

	// Information
	private int stage;
	private int time;

	/**
	 * Constructor that takes the parts of the message.
	 *
	 * @param username      Username sending the message
	 * @param stage         The blind schedule stage.
	 * @param time          The time (in seconds) until the next change to stage.
	 */
	public CommTexasHoldEmOfficialTime (int stage, int time) {
		super("theGhost");

		this.stage = stage;
		this.time = time;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param	message		XML element
	 */
	public CommTexasHoldEmOfficialTime (XMLElement message) {
		super(message);

		stage = message.getIntAttribute(XML_ATT_STAGE);
		time = message.getIntAttribute(XML_ATT_TIME);
	}

	/**
	 * Return the blind stage
	 */
	public int getStage() {
		return this.stage;
	}

	/**
	 * Return the time until stage change
	 */
	public int getTime () {
		return this.time;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);

		message.setIntAttribute(XML_ATT_STAGE, stage);
		message.setIntAttribute(XML_ATT_TIME, time);

		return message;
	}

}
