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

import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jogre.common.IJogre;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.data.GameInfo;

/**
 * Class for inflatting / deflatting between XML and POJO.
 *
 * FIXME - PUSH UP??? OR USE REFLECTION???
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class GameInfoXML {
		
	// XML element / attribute names
	private static final String XML_ELM_GAME_INFO     = "gameinfo";
	private static final String XML_ATT_PLAYERS       = "players";
	private static final String XML_ATT_RESULTS       = "results";
	private static final String XML_ATT_START_TIME    = "st";
	private static final String XML_ATT_END_TIME      = "et";	
	private static final String XML_ATT_SCORE         = "score";
	private static final String XML_ATT_HISTORY       = "history";
		
	/**
	 * inflate method which return 
	 * 
	 * @param game    Game type.
	 * @param element Element.
	 */
	public static GameInfo inflate (String game, Element element) {
		// Check this is the correct element
		if (!element.getName().equals (XML_ELM_GAME_INFO))
			return null;
		
		// Read values from element into java primitives
		String players = element.attributeValue (XML_ATT_PLAYERS); 
		String results = element.attributeValue (XML_ATT_RESULTS);
		Date startTime = JogreUtils.readDate (
			element.attributeValue (XML_ATT_START_TIME), IJogre.DATE_FORMAT_FULL);
		Date endTime = JogreUtils.readDate (
			element.attributeValue (XML_ATT_END_TIME), IJogre.DATE_FORMAT_FULL);		
        String score = element.attributeValue (XML_ATT_SCORE);
        String history = element.attributeValue (XML_ATT_HISTORY);
		
        // Create game information object.
		return new GameInfo (
			game, players, results, startTime, endTime, score, history);
	} 
	
	/**
	 * Method for flattening a GameInfo object into an XML element.
	 * 
	 * @param gameInfo   Game information object.
	 * @return           XML (dom4j) version of Game info object. 
	 */
	public static Element flatten (GameInfo gameInfo) {
		Element elm = DocumentHelper.createElement (XML_ELM_GAME_INFO);
		
		elm.addAttribute (XML_ATT_PLAYERS, gameInfo.getPlayers());
		elm.addAttribute (XML_ATT_RESULTS, gameInfo.getResults());
		elm.addAttribute (XML_ATT_START_TIME, JogreUtils.valueOf (gameInfo.getStartTime(), IJogre.DATE_FORMAT_FULL));
		elm.addAttribute (XML_ATT_END_TIME, JogreUtils.valueOf (gameInfo.getEndTime(), IJogre.DATE_FORMAT_FULL));
		
		elm.addAttribute (XML_ATT_SCORE, gameInfo.getGameScore());
		elm.addAttribute (XML_ATT_HISTORY, gameInfo.getGameHistory());
		
		return elm;
	}
}