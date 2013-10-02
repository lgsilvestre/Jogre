/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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

import nanoxml.XMLElement;

import org.jogre.common.GameOver;
import org.jogre.common.IGameOver;
import org.jogre.common.util.JogreUtils;

/**
 * Communication object which describes a game over. 
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommGameOver extends CommTableMessage 
                          implements IGameOver 
{
	// Fields
	private GameOver gameOver = null;
	
	private static final String XML_ATT_PLAYERS     = "players";
	private static final String XML_ATT_RESULTS     = "results";
	private static final String XML_ATT_OLD_RATINGS = "oldRatings";
	private static final String XML_ATT_NEW_RATINGS = "newRatings";

	/**
	 * Constructor which takes a table number and a game over message.
	 * This is sent from the server to a client.
	 * 
	 * @param tableNum   Table number.
	 * @param gameOver   Game over message.
	 */
	public CommGameOver (int tableNum, GameOver gameOver) {
		super ();
		setTableNum (tableNum);

		this.gameOver = gameOver;
	}

	/**
	 * Message which is sent a client to the server to inform that a game is
	 * over.  The server generally does its own check to ensure that this
	 * is correct.
	 * 
	 * @param resultType    Result type (see IGameOver).
	 */
	public CommGameOver (int resultType) {
		super (resultType); 
	}
	
	/**
	 * Constructor which reads the game over object from an XML element.
	 *
	 * @param message                     XML element version of object.
	 */
	public CommGameOver (XMLElement message) {
		super (message);

		// Retrieve info from XML.
		String playersAtt = message.getStringAttribute (XML_ATT_PLAYERS);
		String resultsAtt = message.getStringAttribute (XML_ATT_RESULTS); 
		String oldRtngAtt = message.getStringAttribute (XML_ATT_OLD_RATINGS);
		String newRtngAtt = message.getStringAttribute (XML_ATT_NEW_RATINGS);
		
		// If these attributes are OK then create a game over object.
		if (playersAtt != null && resultsAtt != null && oldRtngAtt != null && newRtngAtt != null) {
			String [] players = JogreUtils.convertToStringArray (playersAtt);
			int [] results    = JogreUtils.convertToIntArray (resultsAtt);
			int [] oldRatings = JogreUtils.convertToIntArray (oldRtngAtt);
			int [] newRatings = JogreUtils.convertToIntArray (newRtngAtt);
			
			// Create game over object
			this.gameOver = 
				new GameOver (players, results, oldRatings, newRatings);
		}
	}

	/**
	 * Return the score.  If its a draw this will probobly be 0, a win a
	 * positive number and a negative number depending on the game.
	 *
	 * @return score increment.
	 */
	public GameOver getGameOver () {
		return this.gameOver;
	}

	/**
	 * Flatten communciation object.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = flatten (Comm.GAME_OVER);
		
		// Populate message
		if (gameOver != null) {
			message.setAttribute (XML_ATT_PLAYERS, 
				JogreUtils.valueOf(gameOver.getPlayers()));
			message.setAttribute (XML_ATT_RESULTS, 
				JogreUtils.valueOf(gameOver.getResults()));
			message.setAttribute (XML_ATT_OLD_RATINGS, 
				JogreUtils.valueOf(gameOver.getOldRatings()));
			message.setAttribute (XML_ATT_NEW_RATINGS, 
				JogreUtils.valueOf(gameOver.getNewRatings()));
		}
		
		return message;		// flatten XML to a String
	}
}
