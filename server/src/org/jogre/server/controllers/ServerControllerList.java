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
package org.jogre.server.controllers;

import java.util.HashMap;

import org.jogre.server.ServerController;

/**
 * This class contains an instance of the various server controllers
 * (formally known as parsers). These include the standard controllers
 * and various custom parsers for each game.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class ServerControllerList {
	
	/** Standard game parser. */
	private ServerGameController serverGameController;

	/** Standard table parser. */
	private ServerTableController serverTableController;

	/** Custom server controllers for doing extra functionality. */
	private HashMap serverControllers;

	/**
	 * Constructor for the parser list.
	 */
	public ServerControllerList () {
		// Create instances of the standard parsers
		this.serverGameController = new ServerGameController ();
		this.serverTableController = new ServerTableController ();

		// Create new hashmaps to contain the objects
		this.serverControllers = new HashMap ();
	}

	/**
	 * Add a server table parser to the hash.
	 *
	 * @param gameKey     Name of the game
	 * @param tableParser  The actual game parser.
	 */
	public void addServerController (String gameKey, ServerController serverController) {
		this.serverControllers.put (gameKey, serverController);
	}

	/**
	 * Return an instance of the server game parser.
	 *
	 * @return Returns the serverGameParser.
	 */
	public ServerGameController getGameController () {
		return serverGameController;
	}

	/**
	 * Return an instance of the server table parser.
	 *
	 * @return Returns the serverTableParser.
	 */
	public ServerTableController getTableController () {
		return serverTableController;
	}

	/**
	 * Return a specified custom server controller.
	 *
	 * @param gameKey  Name of the game
	 * @return         Returns the serverGameParsers.
	 */
	public ServerController getCustomController (String gameKey) {
		return (ServerController)serverControllers.get (gameKey);
	}
}