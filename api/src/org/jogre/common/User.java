/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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

import java.util.Observable;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;

/**
 * Transmittable user data object.  Contains the username, score,
 * number of wins/loses and draws.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class User extends Observable implements ITransmittable {

	private static final String XML_ATT_USERNAME = "username";
	private static final String XML_ATT_RATING   = "rating";
	private static final String XML_ATT_WINS     = "wins";
	private static final String XML_ATT_LOSES    = "loses";
	private static final String XML_ATT_DRAWS    = "draws";
	private static final String XML_ATT_STREAK   = "streak";

	/** Username of User. */
	protected String username;

	/** Score rating of User. */
	protected int rating, wins, draws, loses, streak;

	/**
	 * Blank User.
	 */
	public User () {}

	/**
	 * User constructor which takes a username, rating, number of
	 * wins, loses, draws and a user streak.
	 *
	 * @param username  Username of user.
	 * @param rating    User rating.
	 * @param wins      Number of user wins.
	 * @param loses     Number of users loses.
	 * @param draws     Number of user draws.
	 * @param streak    Streak of user.
	 */
	public User (String username, int rating, int wins, int loses, int draws, int streak) {
		// Set fields
		this.username = username;
		this.rating   = rating;
		this.wins     = wins;
		this.loses    = loses;
		this.draws    = draws;
		this.streak   = streak;
	}

	/**
	 * Constructor which creates a User object from the flatten () method of
	 * another User object.
	 *
	 * @param message                 XMLElement communication object.
	 * @throws TransmissionException  Thrown if there is a problem in transmission.
	 */
	public User (XMLElement message) throws TransmissionException {
		if (!message.getName().equals(Comm.USER))
			throw new TransmissionException ("Error parsing User");

		this.username = message.getStringAttribute (XML_ATT_USERNAME);
		this.rating   = message.getIntAttribute    (XML_ATT_RATING);
		this.wins     = message.getIntAttribute    (XML_ATT_WINS);
		this.loses    = message.getIntAttribute    (XML_ATT_LOSES);
		this.draws    = message.getIntAttribute    (XML_ATT_DRAWS);
		this.streak   = message.getIntAttribute    (XML_ATT_STREAK);
	}

	/**
	 * Return the username of this user.
	 *
	 * @return username  Username as a String.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Returns the rating of this particular user (currently not implemented).
	 *
	 * @return rating  Rating of user (int)
	 */
	public int getRating() {
		return this.rating;
	}

	/**
	 * Return the number of draws a user has had.
	 *
	 * @return  Number of draws.
	 */
	public int getDraws () {
		return this.draws;
	}

	/**
	 * Return the number of losses a user has had.
	 *
	 * @return   Number of losses.
	 */
	public int getLosses () {
		return this.loses;
	}

	/**
	 * Return the number of wins a user has had.
	 *
	 * @return  Number of wins.
	 */
	public int getWins () {
		return this.wins;
	}

	/**
	 * Return the user streak.  For example, if a user has
	 * won 3 games in a row - this is 3.  If he has lost 4 then
	 * its -4.  If he won 2 and then lost 1 it is -1.
	 *
	 * @return   Streak of user.
	 */
	public int getStreak () {
		return this.streak;
	}

	/**
	 * Return the number of games this player has played.
	 *
	 * @return   Total number of games played (wins + loses + draws).
	 */
	public int getGamesPlayed () {
		return this.wins + this.loses + this.draws;
	}

	/**
	 * Return true if a user is still provisional.
	 *
	 * @return   True if user is still in provisional.
	 */
	public boolean isProvisional () {
		return getGamesPlayed() < IJogre.PROVISIONAL_COUNT;
	}

	/**
	 * Update a user depending on a result type and their new rating.
	 *
	 * @param resultType  Result type - e.g. win, lose or draw
	 * @param newRating   New rating
	 */
	public void update (int resultType, int newRating) {
		// Update wins/losses/draws depending on result type.
		switch (resultType) {
			case IGameOver.WIN:
				wins++;
				streak = streak > 0 ? streak + 1 : 1;
				break;
			case IGameOver.LOSE:
				loses++;
				streak = streak < 0 ? streak - 1 : -1;
				break;
			case IGameOver.DRAW:
				draws++;
				streak = 0;
				break;
		}

		// Update rating
		this.rating = newRating;

		// Update observers
		setChanged ();
		notifyObservers ();
	}

	/**
	 * Flatten the User object for transmission.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten ()  {
		XMLElement message = new XMLElement (Comm.USER);

		message.setAttribute (XML_ATT_USERNAME,  username);

		message.setIntAttribute (XML_ATT_RATING, rating);
		message.setIntAttribute (XML_ATT_WINS,   wins);
		message.setIntAttribute (XML_ATT_LOSES,  loses);
		message.setIntAttribute (XML_ATT_DRAWS,  draws);
		message.setIntAttribute (XML_ATT_STREAK, streak);

		return message;
	}

	/**
	 * Customer equals method to compare User objects
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals (Object obj) {
		if (obj == null)
			return false;

		if (obj instanceof User) {
			User user = (User) obj;
			return this.username.equals(user.getUsername());
		}
		return false;
	}
}
