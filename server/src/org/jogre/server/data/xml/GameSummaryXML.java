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
package org.jogre.server.data.xml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jogre.server.data.GameSummary;

/**
 * Class for packing / unpacking a GameSummary class to and from XML.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class GameSummaryXML {

	// XML element / attribute names
	private static final String XML_ELM_GAME_SUMMARY = "game_summary";
	private static final String XML_ATT_GAME         = "game";
	private static final String XML_ATT_RATING       = "rating";
	private static final String XML_ATT_WINS         = "wins";
	private static final String XML_ATT_LOSES        = "loses";
	private static final String XML_ATT_DRAWS        = "draws";
	private static final String XML_ATT_STREAK       = "streak";
	
	/**
	 * Method for returning a game summary object from an 
	 * XML Element (dom4j).
	 * 
	 * @param element
	 * @return
	 */
	public static GameSummary inflate (String game, String username, Element element) {
		// Check this is the correct element
		if (!element.getName().equals (XML_ELM_GAME_SUMMARY))
			return null;
		
		// retrieve variables from element
		int rating = Integer.parseInt (element.attributeValue(XML_ATT_RATING));
		int wins   = Integer.parseInt (element.attributeValue(XML_ATT_WINS));
		int loses  = Integer.parseInt (element.attributeValue(XML_ATT_LOSES));
		int draws  = Integer.parseInt (element.attributeValue(XML_ATT_DRAWS));
		int streak = Integer.parseInt (element.attributeValue(XML_ATT_STREAK));
			
		// Create game summary object and add to UserInfo object
		return new GameSummary (game, username, rating, wins, loses, draws, streak);
	} 
	
	/**
	 * Method for converting a gameSummary java object into an 
	 * XML Element (dom4j).
	 * 
	 * @param gameSummary 		Game summary object.
	 * @return                  Game summary object as XML "game_summary".
	 */
	public static Element flatten (GameSummary gameSummary) {
		Element elm = DocumentHelper.createElement (XML_ELM_GAME_SUMMARY);
		
		elm.addAttribute (XML_ATT_GAME,   gameSummary.getGameKey());
		elm.addAttribute (XML_ATT_RATING, String.valueOf (gameSummary.getRating())); 
		elm.addAttribute (XML_ATT_WINS,   String.valueOf (gameSummary.getWins())); 
		elm.addAttribute (XML_ATT_LOSES,  String.valueOf (gameSummary.getLoses())); 
		elm.addAttribute (XML_ATT_DRAWS,  String.valueOf (gameSummary.getDraws())); 
		elm.addAttribute (XML_ATT_STREAK, String.valueOf (gameSummary.getStreak()));
				
		return elm; 
	}
}