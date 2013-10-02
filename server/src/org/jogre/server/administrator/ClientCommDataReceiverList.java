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
package org.jogre.server.administrator;

import java.util.HashMap;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.Game;
import org.jogre.common.ClientCommDataReceiver;
import org.jogre.common.GameList;

/**
 * Wrapper class for receiving data for multiple games and works on a 
 * GameList class as opposed to an individual Game class.  
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ClientCommDataReceiverList {

	private HashMap dataReceiverHash;
	
	/**
	 * Take the game list as a constructor.
	 * 
	 * @param gameList   GameList object.
	 */
	public ClientCommDataReceiverList (GameList gamelist) {
		// Create fields		
		this.dataReceiverHash = new HashMap ();
		
		// Initlise and fill hash map
		dataReceiverHash = new HashMap ();
		Vector gameIDs = gamelist.getGameKeys();
		for (int i = 0; i < gameIDs.size(); i++) {
			String gameId = (String)gameIDs.get(i);
			Game   game    = gamelist.getGame(gameId);
			
			// Create new GameDataCommReceiver
			ClientCommDataReceiver dataReceiver = new ClientCommDataReceiver (game);
			dataReceiverHash.put (gameId, dataReceiver);
		}
	}
	
	/**
	 * GameDataCommReceiver wrapper method for reading game messages
	 * which takes an additional gameID method.
	 *  
	 * @param message   Message to read. 
	 * @param gameID    Game ID.
	 * @param username  Username of user.
	 */
	public void receiveGameMessage (XMLElement message, String gameID, String username) {
		ClientCommDataReceiver dataReceiver = getGameDataReceiver (gameID); 
		if (dataReceiver != null)
			dataReceiver.receiveGameMessage(message, username);
	}
	
	/**
	 * GameDataCommReceiver wrapper method for reading table message 
	 * which takes an additional gameID method.
	 * 
	 * @param message   Message to read.
	 * @param gameID    Game ID.
	 * @param username  Username of user.
	 * @param tableNum  Table number.
	 */
	public void receiveTableMessage (XMLElement message, String gameID, String username, int tableNum) {
		ClientCommDataReceiver dataReceiver = getGameDataReceiver (gameID); 
		if (dataReceiver != null)
			dataReceiver.receiveTableMessage (message, username, tableNum);
	}
	
	/**
	 * Return the game data receiver for a specified gameID.
	 * 
	 * @param gameID  Specified gameID.
	 * @return        GameDataCommReceiver instance.
	 */
	private ClientCommDataReceiver getGameDataReceiver (String gameID) {
		return (ClientCommDataReceiver)dataReceiverHash.get (gameID);
	}
}