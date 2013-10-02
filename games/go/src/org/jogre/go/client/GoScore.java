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
package org.jogre.go.client;

import nanoxml.XMLElement;

import org.jogre.common.TransmissionException;
import org.jogre.common.comm.ITransmittable;
import org.jogre.common.util.JogreUtils;

/**
 * Go score object.
 * 
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoScore implements ITransmittable {

	// public constants
	public static final int SCORE_METHOD_AREA = 0;		// Chinese-style  
	public static final int SCORE_METHOD_TERRITORY = 1; // Japanese-style 
	    
	// Declare fields
	private double komi; 
	private int scoringMethod;
	private int [] areas;
	private int [] territorys;
	private int [] prisoners;
	    
    // Declare XML fields
    private static final String XML_NAME = "go_score";
    private static final String XML_ATT_KOMI         = "komi";
    private static final String XML_ATT_SCORE_METHOD = "score_method";
    private static final String XML_ATT_AREAS        = "area";
    private static final String XML_ATT_TERRITORYS   = "territorys";
    private static final String XML_ATT_PRISONERS    = "prisoners";
        
    /**
     * Go score constructor.
     * 
     * @param komi             Komi e.g. 6.5
     * @param scoringMethod    Scoring method (area / terrority).
     * @param area             Amount of area.
     * @param areaPlayer       Player who owns area.
     * @param territory        Amount of territory.
     * @param terrorityPlayer  Player who owns player.
     */
    public GoScore (double komi, 
    		        int scoringMethod, 
    		        int [] areas, 
    		        int [] territorys,
    		        int [] prisoners) 
    {
		super();
		
		this.komi          = komi;
		this.scoringMethod = scoringMethod;
		this.areas         = areas;
		this.territorys    = territorys;
		this.prisoners     = prisoners;
	}
    
    /**
     * Constructor which takes an element.
     * 
     * @param element
     */
    public GoScore (XMLElement element) throws TransmissionException {    	
    	this.komi          = element.getDoubleAttribute (XML_ATT_KOMI);
		this.scoringMethod = element.getIntAttribute    (XML_ATT_SCORE_METHOD); 
		this.areas         = JogreUtils.convertToIntArray (element.getStringAttribute(XML_ATT_AREAS));
		this.territorys    = JogreUtils.convertToIntArray (element.getStringAttribute(XML_ATT_TERRITORYS));
		this.prisoners     = JogreUtils.convertToIntArray (element.getStringAttribute(XML_ATT_PRISONERS));
    }
    
    // Accessors	
	public double getKomi ()             { return komi; }
	public int getScoringMethod ()       { return scoringMethod; }
	public int getArea      (int player) { return areas      [player]; }
	public int getTerritory (int player) { return territorys [player]; }
	public int getPrisoner  (int player) { return prisoners  [player]; }
	
	/**
	 * Return the player who won this game.
	 * 
	 * @return
	 */
	public int getWinningPlayer () {
		int blackScore = getScore (GoModel.BLACK, scoringMethod);
		int whiteScore = getScore (GoModel.WHITE, scoringMethod);
		
		return (blackScore > whiteScore) ? GoModel.BLACK : GoModel.WHITE;
	}
	
	/**
	 * Return the winning score.
	 * 
	 * @return
	 */
	public int getWinningScore () { 
		int blackScore = getScore (GoModel.BLACK, scoringMethod);
		int whiteScore = getScore (GoModel.WHITE, scoringMethod);
		
		return (blackScore > whiteScore) ? blackScore : whiteScore;
	}
		
	/**
	 * Return score for player and score type.
	 * 
	 * @param player
	 * @param scoreType
	 * @return
	 */
	public int getScore (int player, int scoreType) {
		
		int opponent = player == GoModel.BLACK ? GoModel.WHITE : GoModel.BLACK;
		int score = -1;
		
		if (scoreType == SCORE_METHOD_AREA) {
			score =
				areas [player]     - areas [opponent];
		}
		else if (scoreType == SCORE_METHOD_TERRITORY) {
			score = 
				territorys [player] - territorys [opponent] + 
				prisoners [player]  - prisoners [opponent];
		}
		
		// return score
		return score;		
	}
	
	/**
	 * Flatten object.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = new XMLElement (XML_NAME);
				
		message.setDoubleAttribute (XML_ATT_KOMI,         komi);
		message.setIntAttribute    (XML_ATT_SCORE_METHOD, scoringMethod);
		message.setAttribute       (XML_ATT_AREAS,        JogreUtils.valueOf (areas));
		message.setAttribute       (XML_ATT_TERRITORYS,   JogreUtils.valueOf (territorys));
		message.setAttribute       (XML_ATT_PRISONERS,    JogreUtils.valueOf (prisoners));
		
		// Return object as XML element
		return message;				
	}
}
