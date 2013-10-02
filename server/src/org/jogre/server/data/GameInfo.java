/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.server.data;

import java.util.Date;

import nanoxml.XMLElement;

import org.jogre.common.IGameOver;
import org.jogre.common.comm.ITransmittable;

/**
 * Game information object which describes a game which has been played.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GameInfo implements IGameOver  {
	
	// Required Fields
	private long id = 0;
	private String gameKey;
	private String players;		// Array of players at table
	private String results;		// Array of results of players (wins, loses, draws)
	private Date   startTime = null, endTime = null;
		
	// Optional fields (null if not set)
	private String gameScore = null;
	private String gameHistory = null;
	
	/**
	 * Empty constructor.
	 */
	public GameInfo () {}
	
	/**
	 * Constructor which takes a id.
	 */
	public GameInfo (long id) {
		this.id = id;
	}
	
	/**
	 * Constructor for a Game info object.
	 * 
	 * @param gameKey       Game key that has just been played e.g. chess. 
	 * @param players       List of players.
	 * @param results       Result type of game for players (W, L, D).
	 * @param startTime     Start time of game.
	 * @param endTime       End time of game.
	 * @param gameScore     Score (optional).
	 * @param gameHistory   History of game.
	 */
	public GameInfo (String gameKey,
			         String players, 
			         String results,
			         Date   startTime, 
			         Date   endTime, 
			         String gameScore, 
			         String gameHistory) 
	{
		this.gameKey     = gameKey;
		this.players     = players;
		this.results     = results;
		this.startTime   = startTime;
		this.endTime     = endTime;
		this.gameScore   = gameScore;
		this.gameHistory = gameHistory;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGameKey() {
		return gameKey;
	}

	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}

	public String getPlayers() {
		return players;
	}

	public void setPlayers(String players) {
		this.players = players;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getGameScore() {
		return gameScore;
	}

	public void setGameScore(String gameScore) {
		this.gameScore = gameScore;
	}

	public String getGameHistory() {
		return gameHistory;
	}

	public void setGameHistory(String gameHistory) {
		this.gameHistory = gameHistory;
	}
}