/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;

/**
 * A GameList contains a number of Game objects.  This should be stored
 * only on the server as individual games do not really care about this.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GameList implements ITransmittable {

	/**
	 * HashMap containing a number of Game objects.  They key is a
	 * game title and version seperated with a dash.
	 */
	protected HashMap gamelist;

	/**
	 * Constructor to a game list.
	 */
	public GameList () {
		gamelist = new HashMap ();
	}

	/**
	 * Constructor which takes an XMLElement to reload the GameList object.
	 * 
	 * @param message
	 * @throws TransmissionException
	 */
	public GameList (XMLElement message) throws TransmissionException {
		this ();

		if (!message.getName().equals (Comm.GAME_LIST))
			throw new TransmissionException ("Error parsing GameList");

		// Create new table from string and add to Hash
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement)e.nextElement();

			if (childMessage.getName().equals(Comm.GAME)) {
				Game game = new Game (childMessage);
				gamelist.put (game.getKey(), game);	// insert in hash
			}
		}
	}
	
	/**
	 * Add a game to the game list.  This should be done when the server
	 * is created for the first time.
	 *
	 * @param game		Specified game.
	 */
	public void addGame (Game game) {
		gamelist.put (game.getKey(), game);
	}

	/**
	 * Return a Game object from a game key.
	 *
	 * @param gameKey	game title and version seperated with a dash -.
	 *                  e.g. chess-0.1
	 * @return          Game Object stored in cache.
	 */
	public Game getGame (String gameKey) {
		return (Game)gamelist.get (gameKey);
	}

	/**
	 * Return true / false if server contains game key.
	 *
	 * @param gameKey   Game title and version seperated with a dash -.
	 * @return          True if the server supports this game.
	 */
	public boolean containsGame (String gameKey) {
	    return gamelist.containsKey (gameKey);
	}

	/**
	 * Return the game keys.
	 *
	 * @return    Vector of game keys.
	 */
	public Vector getGameKeys () {
		return new Vector (gamelist.keySet());
	}

	/**
	 * Return the number of games currently loaded up.
	 *
	 * @return
	 */
	public int size () {
		return gamelist.size();
	}

	/**
	 * Flatten object so it can be transmitted.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = new XMLElement (Comm.GAME_LIST);

		Set usernames = gamelist.keySet();		// get all usernames
		Iterator i = usernames.iterator();		// iterate through them
		while (i.hasNext()) {
			Game game = (Game)gamelist.get (i.next());
			message.addChild (game.flatten());
		}

		return message;
	}
}