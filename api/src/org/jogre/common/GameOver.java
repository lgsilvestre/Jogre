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
package org.jogre.common;

/**
 * Game over class which describes what has happened when a game
 * was over.  This includes the players who were playing, results, old
 * ratings, new ratings etc.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class GameOver implements IGameOver {
	
	private String [] players;
	private int [] results;
	private int [] oldRatings;
	private int [] newRatings;
	
	/**
	 * Constructor which takes a list of players, results, old
	 * and new ratings (the ratings arrays will return an empty 
	 * integer array of size 0).
	 * 
	 * @param players     List of players.
	 * @param results     List of results.
	 */
	public GameOver (String [] players, int [] results) {
		this (players, results, new int [] {}, new int [] {});
	}	
	
	/**
	 * Constructor which takes a list of players, results, old
	 * and new ratings.
	 * 
	 * @param players     List of players.
	 * @param results     List of results.
	 * @param oldRatings  List of ratings before game.
	 * @param newRatings  List of ratings after game.
	 */
	public GameOver (String [] players, 
			         int [] results, 
			         int [] oldRatings, 
			         int [] newRatings) 
	{
		// Set fields
		this.players = players;
		this.results = results;
		this.oldRatings = oldRatings;
		this.newRatings = newRatings;
	}

	/**
	 * Return an integer array of new ratings.
	 * 
	 * @return    New ratings.
	 */
	public int [] getNewRatings () {
		return newRatings;
	}

	/**
	 * Return an integer array of old ratings.
	 * 
	 * @return    Old ratings.
	 */
	public int [] getOldRatings () {
		return oldRatings;
	}

	/**
	 * Return a String array of players who were playing.
	 * 
	 * @return    Players.
	 */
	public String [] getPlayers () {
		return players;
	}

	/**
	 * Return an integer array of results.
	 * 
	 * @return    Results.
	 */
	public int [] getResults () {
		return results;
	}
}
