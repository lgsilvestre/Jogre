/*
 * JOGRE (Java Online Gaming Real-time Engine) - TODO
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

import org.jogre.common.IGameOver;

/**
 * Game summary class which tells how many games a user has played,
 * won, lost and drawn.  It also holds the users current score for
 * that particular game. 
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class GameSummary implements IGameOver {

	private String gameKey = null;
	private String username = null;
	private int wins;
	private int loses;
	private int draws;
	private int streak;
	private int rating;
	
	/**
	 * Empty constructor.
	 */
	public GameSummary () {}
	
	/**
	 * Game summary object.
	 * 
	 * @param gameKey
	 * @param username
	 */
	public GameSummary (String gameKey, String username) {
		this (gameKey, username, 0);
	}
	
	/**
	 * Create a default game summary object.
	 * 
	 * @param gameKey   Game key.
	 * @param username  Username of user.
	 * @param rating    Rating.
	 */
	public GameSummary (String gameKey, String username, int rating) {
		this (gameKey, username, rating, 0, 0, 0, 0);
	}
	
	/**
	 * Constructor for a game summary.
	 * 
	 * @param gameKey  Key of game that user has won e.g. chess.
	 * @param username Username of player,
	 * @param rating   Rating of user.
	 * @param wins     Number of wins that a user has had.
	 * @param loses    Number of losses that user has had.
	 * @param draws    Number of draws that a user has had.
	 * @param streak   Streak this user has had.
	 */
	public GameSummary (String gameKey, String username, int rating, int wins, int loses, int draws, int streak) {
		super();
		
		this.gameKey  = gameKey;
		this.username = username;
		this.rating   = rating;
		this.wins     = wins;
		this.loses   = loses;
		this.draws    = draws;		
		this.streak   = streak; 
	}
	
	/**
	 * Update the user summary with the result type and the user's new score.
	 * 
	 * @param resultType  Result type - e.g. win, lose or draw
	 * @param newRating   New rating
	 */
	public void update (int resultType, int newRating) {
		// Update wins/losses/draws depending on result type.
		switch (resultType) {
			case WIN:
				wins++;
				streak = streak > 0 ? streak + 1 : 1; 
				break;
			case LOSE:
				loses++;
				streak = streak < 0 ? streak - 1 : -1; 
				break;
			case DRAW:
				draws++;
				streak = 0;
				break;				
		}
		
		// Update rating
		this.rating = newRating;
	}

	public String getGameKey() {
		return gameKey;
	}

	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLoses() {
		return loses;
	}

	public void setLoses(int losses) {
		this.loses = losses;
	}

	public int getDraws() {
		return draws;
	}

	public void setDraws(int draws) {
		this.draws = draws;
	}

	public int getStreak() {
		return streak;
	}

	public void setStreak(int streak) {
		this.streak = streak;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
}