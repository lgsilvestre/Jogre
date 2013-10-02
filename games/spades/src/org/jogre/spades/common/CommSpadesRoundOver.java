/*
 * JOGRE (Java Online Gaming Real-time Engine) - TicTacToe
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
package org.jogre.spades.common;

import java.util.Enumeration;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommTableMessage;

/**
 * Communications object for transmitting end of round protocol.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class CommSpadesRoundOver extends CommTableMessage {

	// xml name for communication object
	public static final String XML_NAME = "spades_round_over";
	public static final String XML_ATT_ROUND = "round";
	public static final String XML_ATT_TEAM_1_POINTS = "team_1_points";
	public static final String XML_ATT_TEAM_2_POINTS = "team_2_points";
	public static final String XML_ATT_TEAM_1_BAGS = "team_1_bags";
	public static final String XML_ATT_TEAM_2_BAGS = "team_2_bags";

	// round to transmit
	private int round = 0;

	// team points to transmit
	private int team1Points = 0;
	private int team2Points = 0;

	// team points to transmit
	private int team1Bags = 0;
	private int team2Bags = 0;

	private CommGameOver gameOver = null;

	/**
	 * Constructor that takes a username, round and trick.
	 *
	 * @param username
	 *            Username
	 * @param bid
	 *            Bid
	 */
	public CommSpadesRoundOver(int round, int team1Points, int team2Points,
			int team1Bags, int team2Bags) {
		super();
		this.round = round;
		this.team1Points = team1Points;
		this.team2Points = team2Points;
		this.team1Bags = team1Bags;
		this.team2Bags = team2Bags;
	}

	/**
	 * Constuctor that takes an xml element and creates an object from that xml
	 * element.
	 *
	 * @param message
	 *            Xml element
	 */
	public CommSpadesRoundOver(XMLElement message) {
		super(message);
		this.round = message.getIntAttribute(XML_ATT_ROUND);
		this.team1Points = message.getIntAttribute(XML_ATT_TEAM_1_POINTS);
		this.team2Points = message.getIntAttribute(XML_ATT_TEAM_2_POINTS);
		this.team1Bags = message.getIntAttribute(XML_ATT_TEAM_1_BAGS);
		this.team2Bags = message.getIntAttribute(XML_ATT_TEAM_2_BAGS);

		// Read child elements - game over objects
		Enumeration e = message.enumerateChildren();
		while (e != null && e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement) e.nextElement();

			if (childMessage.getName().equals(Comm.GAME_OVER)) {
				this.gameOver = new CommGameOver (childMessage);
				break;
			}
		}
	}

	/**
	 * Get round
	 *
	 * @return a round
	 */
	public int getRound() {
		return this.round;
	}

	/**
	 * Get team 1 points (seat 0 and 2)
	 *
	 * @return team 1's points for the round
	 */
	public int getTeam1Points() {
		return this.team1Points;
	}

	/**
	 * Get team 2 points (seat 1 and 3)
	 *
	 * @return team 2's points for the round
	 */
	public int getTeam2Points() {
		return this.team2Points;
	}

	/**
	 * Get team 1 bags (seat 0 and 2)
	 *
	 * @return team 1's bags for the round
	 */
	public int getTeam1Bags() {
		return this.team1Bags;
	}

	/**
	 * Get team 2 bags (seat 1 and 3)
	 *
	 * @return team 2's bags for the round
	 */
	public int getTeam2Bags() {
		return this.team2Bags;
	}

	/**
	 * Check whether it is game over or not
	 *
	 * @return true if game over, false otherwise
	 */
	public boolean isGameOver() {
		return this.gameOver != null;
	}

	/**
	 * Sets game over with CommGameOver object
	 *
	 * @param gameOver
	 *            Game over communication object
	 */
	public void setGameOver(CommGameOver gameOver) {
		this.gameOver = gameOver;
	}

	/**
	 * Get game over communication object
	 *
	 * @return game over communication object
	 */
	public CommGameOver getGameOver() {
		return this.gameOver;
	}

	/**
	 * Flattens this object into xml.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(XML_NAME);
		message.setIntAttribute(XML_ATT_ROUND, this.round);
		message.setIntAttribute(XML_ATT_TEAM_1_POINTS, this.team1Points);
		message.setIntAttribute(XML_ATT_TEAM_2_POINTS, this.team2Points);
		message.setIntAttribute(XML_ATT_TEAM_1_BAGS, this.team1Bags);
		message.setIntAttribute(XML_ATT_TEAM_2_BAGS, this.team2Bags);
		if (isGameOver())
			message.addChild(this.gameOver.flatten());
		return message;
	}
}