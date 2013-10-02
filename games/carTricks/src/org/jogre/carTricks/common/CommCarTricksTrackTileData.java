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

import org.jogre.common.comm.CommTableMessage;
import org.jogre.common.util.JogreUtils;

/**
 * Communications object for transmitting a Tile Data message for Car Tricks.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CommCarTricksTrackTileData extends CommTableMessage {

	// xml information
	public static final String XML_NAME = "car_tricks_tile_data";
	public static final String XML_ATT_TILENAME = "name";
	public static final String XML_ATT_DATA = "data";

	// The name of this tile
	private String tileName;

	// The data of this tile.
	private byte [] rawData;

	/**
	 * Constructor
	 *
	 * @param tileName      The name of the tile that this data corresponds to
	 * @param rawData       The raw data to be sent
	 */
	public CommCarTricksTrackTileData(String tileName, byte [] rawData) {
		super();
		this.tileName = tileName;
		this.rawData = rawData;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message       XML element
	 */
	public CommCarTricksTrackTileData(XMLElement message) {
		super(message);
		this.tileName = message.getStringAttribute(XML_ATT_TILENAME);

		// Need to decode the string data back into binary data.
		rawData = JogreUtils.Jogre64Decode(message.getStringAttribute(XML_ATT_DATA));
	}

	/**
	 * Return the tilename
	 */
	public String getTileName() {
		return tileName;
	}

	/**
	 * Return the tile data
	 */
	public byte [] getTileData() {
		return rawData;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setAttribute(XML_ATT_TILENAME, tileName);

		// Encode the binary data into ascii data before sending
		message.setAttribute(XML_ATT_DATA, JogreUtils.Jogre64Encode(rawData));

		return message;
	}
}
