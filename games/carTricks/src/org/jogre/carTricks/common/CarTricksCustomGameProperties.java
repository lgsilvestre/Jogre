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

import nanoxml.XMLElement;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Object to hold the custom game properties for Car Tricks.
 * The custom Game properties is currently the list of valid tracks.
 * 
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class CarTricksCustomGameProperties {

	// xml information
	public static final String XML_NAME = "car_tricks_custom_game_properties";
	public static final String XML_TRACK_NAME = "track";
	public static final String XML_ATT_TRACKNAME = "name";
	public static final String XML_ATT_FINGERPRINT = "fp";

	private String [] trackList;
	private int [] fingerprints;

	/**
	 * Constructor of a track list given a vector of CarTricksTrackDB's
	 */
	public CarTricksCustomGameProperties(Vector trackDBs) {
		// Convert the Vector of databases into an array of strings
		this.trackList = new String [trackDBs.size()];
		this.fingerprints = new int [trackDBs.size()];

		for (int i=0; i<trackDBs.size(); i++) {
			CarTricksTrackDB tr = (CarTricksTrackDB) trackDBs.elementAt(i); 
			this.trackList[i] = tr.getTrackName();
			this.fingerprints[i] = tr.getFingerprint();
		}
	}

	/**
	 * Constructor of a track list given the XML tree
	 */
	public CarTricksCustomGameProperties(XMLElement tree) {
		if (tree == null) {
			trackList = new String [0];
			fingerprints = new int [0];
		} else {
			// Create a new array for the names
			trackList = new String [tree.countChildren()];
			fingerprints = new int [tree.countChildren()];

			// Pull all of the names out of the tree
			Enumeration treeEnum = tree.enumerateChildren();
			XMLElement childEl;
			int i = 0;
			while (treeEnum.hasMoreElements()) {
				childEl = (XMLElement) treeEnum.nextElement();
				trackList[i] = childEl.getStringAttribute(XML_ATT_TRACKNAME);
				fingerprints[i] = childEl.getIntAttribute(XML_ATT_FINGERPRINT);
				i += 1;
			}
		}
	}

	/**
	 * Flattens this object for transmittal.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = new XMLElement(XML_NAME);

		for (int i=0; i<trackList.length; i++) {
			// Create a child for the next track name
			XMLElement childEl = new XMLElement(XML_TRACK_NAME);
			childEl.setAttribute(XML_ATT_TRACKNAME, trackList[i]);
			childEl.setIntAttribute(XML_ATT_FINGERPRINT, fingerprints[i]);

			// Add the child to the tree
			message.addChild(childEl);
		}

		return message;
	}

	/**
	 * Return the track name for the Nth element of the list of tracks
	 */
	public String getTrackName(int item) {
		return trackList[item];
	}

	/**
	 * Return the fingerprint for the Nth element of the list of tracks
	 */
	public int getFingerprint(int item) {
		return fingerprints[item];
	}

	/**
	 * Return the list of tracks
	 */
	public String [] getTrackList() {
		return trackList;
	}
}
