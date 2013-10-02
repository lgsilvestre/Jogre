/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.client;

import java.util.Collections;
import java.util.Vector;
import java.util.Enumeration;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;

import java.lang.StringBuilder;

import org.jogre.common.util.JogreUtils;

import org.jogre.grandPrixJumping.common.JumpingCoreModel;
import org.jogre.grandPrixJumping.common.JumpingFence;

/**
 * Grand Prix Jumping Initial Layout structure
 *
 * @author Richard Walter (rwalter42@yahoo.com)
 * @version Beta 0.3
 */
public class JumpingLayouts {

	// Static instance provided by the static factory method getInstance()
	private static JumpingLayouts myInstance = null;

	private Vector titles = new Vector();
	private Vector locations = new Vector();

	// Default values for when the file can't be read.
	private static final String DEFAULT_NAME = "Default";
	private static final String DEFAULT_LOCS = "-1 11 32 -2 3 20 -3 7 42 -4 38 -5 17 -6 26";

	/**
	 * Constructor for the layout database.
	 *
	 * @param	filename	The filename of the xml file that contains
	 *						the initial layouts.
	 */
	private JumpingLayouts(String filename) {
		// Try to load the file contents
		parseLayouts(loadFile(filename));

		// If that didn't work, then create the default layout
		if (titles.size() == 0) {
			titles.add(DEFAULT_NAME);
			locations.add(DEFAULT_LOCS);
		}
	}

	/**
	 * A static factory for creating the single layout instance.
	 */
	public static JumpingLayouts createInstance(String filename) {
		if (myInstance == null) {
			myInstance = new JumpingLayouts(filename);
		}

		return myInstance;
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static JumpingLayouts getInstance() {
		return myInstance;
	}

	/**
	 * Return the titles as an array of strings.
	 *
	 * Note: This does the conversion from Vector to String [] one element at
	 * a time because doing { return (String []) titles.toArray(); } results
	 * in a run-time cast exception for trying to convert the Object[] to String [].
	 *
	 * @param preTitles   These titles are added to the front of the returned array
	 */
	public String [] getTitles(String [] preTitles) {
		// Create the new array
		int preTitleLength = (preTitles == null) ? 0 : preTitles.length;
		int numTitles = titles.size() + preTitleLength;
		String [] newArray = new String [numTitles];

		// First copy the preTitles over.
		for (int i=0; i<preTitleLength; i++) {
			newArray[i] = preTitles[i];
		}

		// Now copy the titles from the titles Vector.
		Object [] titlesArray = titles.toArray();
		numTitles = titles.size();
		for (int i=0; i<numTitles; i++) {
			newArray[i+preTitleLength] = (String) titlesArray[i];
		}

		return newArray;
	}

	/**
	 * For a given title, return the locations string associated with that title.
	 *
	 * If the given title isn't found, then return the default locations
	 */
	public String getLocationsFor(String title) {
		int index = titles.indexOf(title);
		if (index >= 0) {
			return (String) locations.elementAt(index);
		} else {
			return DEFAULT_LOCS;
		}
	}

	/**
	 * Return the locations at the given index.
	 */
	public String getLocationsFor(int index) {
		return (String) locations.elementAt(index);
	}

	/**
	 * Read the initialLayouts file to create the starting layouts
	 *
	 * @param	filename	The filename of the file to parse.
	 */
	private XMLElement loadFile(String filename) {
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
				done = fullTree.getName().equals("layouts");
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
	 * Parse a layouts component from an XML tree and place the
	 * info for the spaces into the data structure of this item.
	 *
	 * @param	comp		The layouts component of the XML tree
	 */
	private void parseLayouts(XMLElement tree) {
		// If the tree is null, then there is nothing to do...
		if (tree == null) {
			return;
		}

		// Parse all of the children nodes to find the layouts
		Enumeration layoutsEnum = tree.enumerateChildren();
		while (layoutsEnum.hasMoreElements()) {
			XMLElement el = (XMLElement) layoutsEnum.nextElement();

			if (el.getName().equals("layout")) {
				// Get the title & layout info
				String theTitle = el.getStringAttribute("title");
				String theLocations = el.getStringAttribute("locations");

				// Do validity check...
				if (validLayout(theTitle, theLocations)) {
					// ... and if passed, then add to the vector of layouts
					titles.add(theTitle);
					locations.add(theLocations);
				}
			}
		}
	}

	/**
	 * Do simple validity checking of a given layout.
	 *
	 * @param theTitle		The title of the layout
	 * @param theLocations	The string on locations of the fences
	 */
	private boolean validLayout(String theTitle, String theLocations) {
		// The title must be something
		if (theTitle.equals("")) {
			return false;
		}

		if (theLocations.startsWith("-")) {
			// Old style location string
			// The locations must be an array of integers
			int [] locs;
			try {
				locs = JogreUtils.convertToIntArray(theLocations);
			} catch (Exception e) {
				// Can't translate into an array, so it's not good
				return false;
			}

			if (locs.length == 0) {
				return false;
			}

			// All of the integers have to be between -6 and <TRACK_SPACES>-1
			for (int i=0; i<locs.length; i++) {
				if ((locs[i] < -6) || (locs[i] >= JumpingCoreModel.TRACK_SPACES)) {
					return false;
				}
			}
		} else {
			// New style location string
			// Any string is valid for this.
		}

		// Passed the test.
		return true;
	}

	/*
	 * Generate a random layout of fences on the board.
	 *
	 * The random tracks that this produces have the following characteristics:
	 *   - There are always at least 1 space between fences.  (ie: there is
	 *     never an "oxer" jump)
	 *   - There are always two fences of height 1, 2 & 3 and a single fence
	 *     of heights 4 & 5.
	 *   - There is always a single water jump and it is located on the 2nd
	 *     or 3rd row of the board.
	 *   - There is always an empty space before and after the water jump.
	 */
	private static int [] fenceHeightList = {1,1,2,2,3,3,4,5};
	public static String getRandomLayoutCode() {
		// Create a random list of fences to be put on the board
		Vector fenceHeights = new Vector ();
		for (int i=0; i<fenceHeightList.length; i++ ) {
			fenceHeights.add(new Integer(fenceHeightList[i]));
		}
		Collections.shuffle(fenceHeights);

		// Pick a random location for the water jump and add it to the track
		int waterStart = 11 + (int) (Math.random() * 18.0);
		if (waterStart >= 20) {
			waterStart += 3;
		}

		// Create the list of possible places to put additional spaces.
		// This vector contains 4 each of 1..7 and 5 at 0 & 8.
		Vector spaces = new Vector ();
		for (int i=0; i < 9; i++) {
			spaces.add(new Integer(i));
			spaces.add(new Integer(i));
			spaces.add(new Integer(i));
			spaces.add(new Integer(i));
		}
		spaces.add(new Integer(0));
		spaces.add(new Integer(8));
		Collections.shuffle(spaces);

		// Pick 25 spaces out of the 38 available for spacing
		int [] distances = {1, 2, 2, 2, 2, 2, 2, 2, 1};
		for (int i=0; i < 25; i++) {
			distances[((Integer) spaces.get(i)).intValue()] += 1;
		}

		// Create a string builder to build the string and seed it with the
		// water jump
		StringBuilder sb = new StringBuilder();
		sb.append("-6 " + (waterStart + 1));

		// Now, go through the board moving the random distance generating the
		// board code.
		int currSpace = 0;
		for (int f = 0; f < 8; f++) {
			currSpace += distances[f];

			// If we've reached the water jump, then skip it before continuing
			if (currSpace >= waterStart) {
				currSpace += 6;
				waterStart = JumpingCoreModel.TRACK_SPACES + 1;
			}
			sb.append(" " + -((Integer) fenceHeights.get(f)).intValue() + " " + currSpace);
		}

		return sb.toString();
	}
}
