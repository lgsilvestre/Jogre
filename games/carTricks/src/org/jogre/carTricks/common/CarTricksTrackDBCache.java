/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
 * http//jogre.sourceforge.org
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

import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import java.util.Vector;
import java.util.ListIterator;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A manager of a local cache of tracks for Car Tricks.
 * The Car Tricks server can only play tracks that it has on the server.
 * When a client starts a new game or connects to a game, it could be
 * that the client already knows about this track or it may not.  If
 * it knows the track already (because there is a local file that matches
 * it), then the local copy is used.  If it doesn't know the track then
 * it will ask the server to send the full database over to it.
 *
 * This is done so that the server doesn't have to send lots of data
 * to the clients all of the time, but only has to send track databases
 * for tracks that the clients don't know about.  Ideally, the clients
 * will then save the databases locally so that the next time they
 * connect, they will have the info available.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class CarTricksTrackDBCache {

	// Static instance provided by the static factory method getInstance()
	private static CarTricksTrackDBCache myInstance = null;

/////	private Vector<CarTricksTrackDB> validTracks;
	private Vector validTracks;

	/**
	 * Filter that only lists directories that start with "track"
	 */
	private FilenameFilter trackDBCacheFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.startsWith("track");
		}
	};

	/**
	 * Constructor which creates the cache given a baseDirectory of where
	 * to look for tracks.
	 */
	private CarTricksTrackDBCache(String baseDirectory) {

		// Create the vector of valid tracks for playing on.
		validTracks = new Vector();

		if (JogreUtils.isApplet()) {
			// Can't dynamically search a .jar file, so don't load the
			// track database cache.  Applets will get all of their track
			// data sent by the server.
			return;
		}

		// Scan the baseDirectory for all tracks...
		File trackdir = new File (baseDirectory);

		String [] children = trackdir.list(trackDBCacheFilter);
		if (children != null) {
			for (int i=0; i<children.length; i++) {
				String childFullName = baseDirectory + File.separator + children[i];
				File f = new File (childFullName);
				if (f.isDirectory()) {
					CarTricksTrackDB track = new CarTricksTrackDB(childFullName);
					if (track.isValid()) {
						validTracks.add(track);
					}
				}
			}
		}
	}

	/**
	 * A static factory for returning a cache instance.
	 *
	 * @param	baseDirectory		The directory that holds the track databases
	 */
	public static CarTricksTrackDBCache getInstance(String baseDirectory) {
		if (myInstance == null) {
			myInstance = new CarTricksTrackDBCache(baseDirectory);
		}

		return myInstance;
	}

	/**
	 * Return the vector of valid tracks.
	 */
	public Vector getValidTracks() {
		return validTracks;
	}

	/**
	 * Add a new track to the cache
	 *
	 * @param	newTrack		A new track database to add.
	 */
	public void addNewTrack(CarTricksTrackDB newTrack) {
		if (newTrack.isValid()) {
			validTracks.add(newTrack);
		}
	}

	/**
	 * Search through the validTracks list looking for the track with the
	 * given name and fingerprint.
	 *
	 * @param	trackName		The name of the track that is being searched for
	 * @param	fingerprint		The fingerprint of the track that is being searched for.
	 *							If 0, then fingerprint isn't matched.
	 * @returns		The track searched for (or null if the given track doesn't exist)
	 */
	 public CarTricksTrackDB findTrack(String trackName, int fingerprint) {
		ListIterator iter = validTracks.listIterator();

		while (iter.hasNext()) {
			CarTricksTrackDB track = (CarTricksTrackDB) iter.next();
			if (trackName.equals(track.getTrackName())) {
				if ((fingerprint == 0) || (fingerprint == track.getFingerprint())) {
					return track;
				}
			}
		}
		return null;
	 }
	 
}
