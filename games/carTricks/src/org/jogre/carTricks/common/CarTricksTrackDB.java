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
import nanoxml.XMLParseException;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

import org.jogre.common.util.JogreUtils;

// Structure to hold a track database
public class CarTricksTrackDB {

	// This is the full XML tree for the database.  This is used when the server
	// needs to send the database to a client.
	private XMLElement theXMLTree;

	// The directory that this track database comes from.
	private String dirName = null;

	// This array holds the information on the tiles used to draw the track
	private CarTricksTrackDBTile [] tiles;

	// The size of the image (x,y)
	private int [] imageSize;

	// The tile ID of the tile to use for the background
	private int backgroundTileId;

	// This array holds the graphical information for the track.
	private int [][] graphical_info = null;
	public static final int GRAPH_X_COORD = 0;
	public static final int GRAPH_Y_COORD = 1;
	public static final int GRAPH_ROT = 2;

	// This array holds the logical connection information for the track.
	public CarTricksConnection [] connections = null;

	// This array holds the tile ID and tile layer for the tile at each space
	private int [] tileIds;
	private int [] tileLayers;

	// This array holds the starting spaces for each car.
	public int [] startSpaces = null;

	// The name of the track
	private String trackName;

	// The "fingerprint" of the track.
	// This is generated as a function of the connections of the track.
	// It is used for clients to verify that they probably have the right database
	// locally without needing to be told it from the server.
	private int fingerprint;

	/**
	 * Constructor for a new database given an XML tree
	 *
	 * @param	theTree		The XML tree of the database
	 */
	public CarTricksTrackDB(XMLElement theTree) {
		this.theXMLTree = parseTrack(theTree);
	}

	/**
	 * Constructor for a new database given a directory to read
	 *
	 * @param	dirname		The name of the directory to read from.
	 *						The file loaded is "<dirname>/db.xml"
	 */
	public CarTricksTrackDB(String dirName) {
		this.dirName = dirName;
		String fname = dirName + File.separator + "db.xml";
		this.theXMLTree = parseTrack(this.loadFile(fname));
	}

	/**
	 * Parse a file into the track data base
	 *
	 * @param	filename	The filename of the file to parse.
	 */
	public XMLElement loadFile(String filename) {
		// Read the file into an XML database tree
		XMLElement fullTree = new XMLElement();
		Reader reader;

		// Try to open the given file
		try {
			if (JogreUtils.isApplet()) {
				reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
			} else {
				reader = new FileReader(filename);
			}
		} catch (Exception e) {
			return null;
		}

		// Try to parse the XML elements in the file and find a track.
		boolean done = false;
		while (!done) {
			try {
				fullTree.parseFromReader(reader);
				done = fullTree.getName().equals("track");
			} catch (IOException e) {
				fullTree = null;
				done = true;
			} catch (XMLParseException e) {
				fullTree = null;
				done = true;
			}
		}

		// Close the file
		try {
			reader.close();
		} catch (IOException e) {
		}

		// Return the tree we've got
		return fullTree;
	}

	/**
	 * Parse a track component from an XML tree and place the
	 * info for the spaces into the data structure of this item.
	 *
	 * @param	comp		The track component of the XML tree
	 * @returns		If parse successfull, then this will return the same XML tree
	 *				as given.  If parse is not succesfull, then this will return
	 *				null.
	 */
	private XMLElement parseTrack(XMLElement comp) {
		// If the tree is null, then there is nothing to do...
		if (comp == null) {
			return null;
		}

		// Parse the attributes of the track
		int num_spaces = comp.getIntAttribute("num_spaces", -1);
		if (num_spaces <= 0) {
			return null;
		}
		graphical_info = new int [num_spaces][3];
		connections = new CarTricksConnection [num_spaces];
		tileIds = new int [num_spaces];
		tileLayers = new int [num_spaces];

		int num_tiles = comp.getIntAttribute("num_tiles", -1);
		if (num_tiles <= 0) {
			return null;
		}
		tiles = new CarTricksTrackDBTile [num_tiles];

		trackName = comp.getStringAttribute("name", "");
		if (trackName.equals("")) {
			return null;
		}

		startSpaces = JogreUtils.convertToIntArray(comp.getStringAttribute("start_spaces", ""));
		imageSize = JogreUtils.convertToIntArray(comp.getStringAttribute("image_size", ""));
		if (startSpaces.length == 0 || imageSize.length != 2) {
			return null;
		}

		backgroundTileId = comp.getIntAttribute("backgroundTile", -1);

		// Parse all of the children nodes
		Enumeration trackEnum = comp.enumerateChildren();
		boolean ok = true;
		while (trackEnum.hasMoreElements()) {
			XMLElement el = (XMLElement) trackEnum.nextElement();

			if (el.getName().equals("space")) {
				ok = parse_space_element(el);
			} else if (el.getName().equals("tile")) {
				ok = parse_tile_element(el);
			}

			if (!ok) {
				return null;
			}
		}

		// Calculate the fingerprint
		generateFingerprint();

		// Return the same tree as provided to indicate success
		return comp;
	}

	/**
	 * Parse a space element from a track database XML tree and place the
	 * info for the spaces into the data structure of this item.
	 *
	 * @param	space		The space element from the XML tree
	 * @returns		true, if parse successfull
	 */
	private boolean parse_space_element(XMLElement space) {
		int id = space.getIntAttribute("id", -1);
		try {
			stuffIntArray(graphical_info[id], space.getStringAttribute("screen"));

			connections[id] = new CarTricksConnection(4, space.getStringAttribute("next"));

			int [] tile_info = JogreUtils.convertToIntArray(space.getStringAttribute("tile", "-1 -1"));
			tileIds[id] = tile_info[0];
			tileLayers[id] = tile_info[1];

			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Parse a tile element from a track database XML tree and place the
	 * info for the spaces into the data structure of this item.
	 *
	 * @param	tile		The tile element from the XML tree
	 * @returns		true, if parse successfull
	 */
	private boolean parse_tile_element(XMLElement tile) {
		int id = tile.getIntAttribute("id", -1);
		try {
			String tile_filename = tile.getStringAttribute("file");
			int [] offset = JogreUtils.convertToIntArray(tile.getStringAttribute("offset"));
			tiles[id] = new CarTricksTrackDBTile(id, dirName, tile_filename, offset[0], offset[1]);

			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Flatten this database into an XML tree.
	 */
	public XMLElement flatten() {
		// Note: Since the database is created by an XMLTree, no real
		// work needs to be done to create an XML tree here... :)
		return this.theXMLTree;
	}

	/**
	 * Is this database valid?
	 */
	public boolean isValid() {
		return (this.theXMLTree != null);
	}

    /**
     * stuff the values of the given string into the given array.
     *
     * @param	array	The array to fill.
     * @param	text	Simple integer array as a spaced delimited String.
     */
    private void stuffIntArray (int [] array, String text) {
        StringTokenizer st = new StringTokenizer (text, " ");

        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(st.nextToken().trim());
        }
    }

    /**
     * Get the three element graphical array for the given space number
     *
     * @param	space_num	The space number
     * @return	the three-element array (x, y, rotation)
     */
	public int [] getGraphicalArrayForSpace (int space_num) {
		if ((space_num >= 0) && (space_num < graphical_info.length)) {
			return graphical_info[space_num];
		} else {
			return null;
		}
	}

	/**
     * Get the Connection list for the given board space
     *
     * @param	space_num		The space number
     * @return	the board following array
     */
	public CarTricksConnection getConnectionsForSpace (int space_num) {
		if ((space_num >= 0) && (space_num < connections.length)) {
			return connections[space_num];
		} else {
			return null;
		}
	}

	/**
	 * Get the tile for a given board space
	 *
     * @param	space_num		The space number
     * @return	the tile for that space, or null if there isn't one
	 */
	public CarTricksTrackDBTile getTileForSpace(int space_num) {
		try {
			return tiles[tileIds[space_num]];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Get the array of tiles
	 *
	 * @return the array of tiles
	 */
	public CarTricksTrackDBTile [] getTiles() {
		return tiles;
	}

	/**
	 * Find a tile by name
	 *
	 * @return the tile of the given name
	 */
	public CarTricksTrackDBTile getTileByName(String tilename) {
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i].getFilename().equals(tilename)) {
				return tiles[i];
			}
		}
		return null;
	}

	/**
	 * Get the tile layer for a given board space
	 *
     * @param	space_num		The space number
     * @return	the layer of the tile for that space
	 */
	public int getTileLayerForSpace(int space_num) {
		try {
			return tileLayers[space_num];
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	/**
	 * Get the tile to be used as the background
	 *
     * @return	the tile for the background, or null if there isn't a background tile
	 */
	public CarTricksTrackDBTile getBackgroundTile() {
		try {
			return tiles[backgroundTileId];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Set the data associated with a tile
	 */
	public void setTileData(String tileName, byte [] tileData) {
		CarTricksTrackDBTile theTile = getTileByName(tileName);
		if (theTile != null) {
			theTile.setData(tileData);
			theTile.saveDataToFile(dirName, tileData);
		}
	}

	/**
	 * Return the name of the track.
	 *
	 * @return	the name of the track
	 */
	public String getTrackName() {
		return trackName;
	}

	/**
     * Return the number of cars on the track
     *
     * @return	the number of cars
     */
	public int getNumCars() {
		return startSpaces.length;
	}

	/**
     * Return the number of spaces on the track
     *
     * @return	the number of spaces
     */
	public int getNumSpaces() {
		return connections.length;
	}

	/**
	 * Get the dimensions of the track image
	 *
	 * @return the dimensions of the image
	 */
	public int [] getImageDimensions() {
		return imageSize;
	}

	/**
	 * Return the fingerprint of the track
	 *
	 * @return the fingerprint
	 */
	public int getFingerprint() {
		return fingerprint;
	}

	/**
	 * Generate the fingerprint for a track.
	 */
	private void generateFingerprint() {
		// Initialize fingerprint to 0
		fingerprint = 0;

		int num_spaces = graphical_info.length;

		addToFingerprint(num_spaces);
		addToFingerprint(startSpaces);
		for (int i = 0; i < num_spaces; i++) {
			addToFingerprint(connections[i]);
		}
	}

	/* Helper functions to add various items to the fingerprint of a track */

	/* Add an integer to the fingerprint. */
	private void addToFingerprint(int a) {
		fingerprint = (fingerprint * 17) + a;
	}

	/* To add an array of ints, add each one in turn. */
	private void addToFingerprint(int [] a) {
		for (int i = 0; i < a.length; i++) {
			addToFingerprint(a[i]);
		}
	}

	/* To add a connection, add each link in turn. */
	private void addToFingerprint(CarTricksConnection c) {
		c.resetLinks();
		int a = c.nextLink();
		while (a > 0) {
			addToFingerprint(a);
			a = c.nextLink();
		}
	}

	/**
	 * Sanitize the track name to a form that can be used for the
	 * name of the directory to hold the data.
	 *
	 * This removes all non-[A-Z, a-z, 0-9] characters from the
	 * track name and converts them into _ characters.
	 *
	 * @return		The sanitized version of the track name
	 */
	private String sanitizeName() {
		StringBuffer newName = new StringBuffer(trackName);

		for (int i = 0; i < newName.length(); i++) {
			char c = newName.charAt(i);
			if (!( ((c >= 'A') && (c <= 'Z')) ||
				   ((c >= 'a') && (c <= 'z')) ||
				   ((c >= '0') && (c <= '9')))) {
				newName.setCharAt(i, '_');
			}
		}

		return newName.toString();
	}

	/**
	 * Create the directory name for this track.  The default
	 * directory name is the sanitized version of the track name.
	 * If that directory already exists (ie: there is a different
	 * track with the same name), then we append "_1" and try again.
	 * We keep trying up to "_99", at which point we give up.
	 *
	 * @param baseDir		The base directory name to use.
	 * @return		A path name with the directory name.
	 *				or null if no valid name could be found
	 */
	private String createTrackDirName(String baseDir) {
		String sName = "track_" + sanitizeName();
		int i = 0;

		// The initial try name is "baseDir/sName"
		String tryName = baseDir + File.separator + sName;

		// Only try 100 times.  If there are more tracks than that with
		// the same sanitized name, then something is probably wrong...
		while (i < 100) {
			if (!(new File (tryName)).exists()) {
				// This directory doesn't exist yet, so we can use it!
				return tryName;
			}

			// The next directory to try is "baseDir/sName_i"
			i += 1;
			tryName = baseDir + File.separator + sName + "_" + i;
		}

		return null;
	}

	/**
	 * Create a local directory for this database
	 *
	 * @param baseDir	The base directory where all track databases are to be kept.
	 */
	public void createDataDirectory(String baseDir) {
		// Create a name for the new directory
		dirName = createTrackDirName(baseDir);
		if (dirName == null) {
			return;
		}

		// Create the directory
		if (!(new File (dirName)).mkdir()) {
			// Didn't work, so kill the directory name so that we don't attempt
			// to write any data into it.
			dirName = null;
		}
	}

	/**
	 * Write the database out to a file.
	 */
	public void writeDB() {
		// If there is no directory for this, then nothing to do.
		if (dirName == null) {
			return;
		}

		FileWriter myWriter = null;

		try {
			myWriter = new FileWriter(dirName + File.separator + "db.xml");
			myWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
			myWriter.write(this.theXMLTree.toString(true));
			myWriter.write("\n");
			myWriter.flush();
		} catch (IOException e) {
		} finally {
			try {
				myWriter.close();
			} catch (IOException e) {
			} catch (NullPointerException e) {
			}
		}
	}

}
