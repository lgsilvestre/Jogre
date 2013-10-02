/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
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
package org.jogre.go.client;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;

/**
 * Model which holds the data for a game of Go.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoModel extends JogreModel {
	
	private final static String XML_ATT_NUM_OF_CELLS = "numOfCells";
	private final static String XML_ATT_CURRENT_DATA = "currentData";
	private final static String XML_ATT_PREVIOUS_DATA = "previousData";
	private final static String XML_ATT_TERRITORIES = "territories";
	private final static String XML_ATT_GAME_STATE = "gameState";
	private final static String XML_ATT_CAPTURED_STONES = "capturedStones";
	private final static String XML_ATT_KOMI = "komi";
	private final static String XML_ATT_SCORE_METHOD = "scoreMethod";
    
	public static final int DEFAULT_BOARD_SIZE = 19;
	
    // Declare constants
    public static final int [] BOARD_SIZES = {9, 13, 19};
        
    public static final int MARKED = -2;
    public static final int BLANK = -1;
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int BLACK_MARKED_DEAD = 2;
    public static final int WHITE_MARKED_DEAD = 3;
    
    // States that a game can get
    public static final int STATE_MOVE = 0;
    public static final int STATE_WHITE_PASS = 1;
    public static final int STATE_BLACK_PASS = 2;
    public static final int STATE_MARK = 3;
    public static final int STATE_WHITE_HAPPY = 4;
    public static final int STATE_BLACK_HAPPY = 5;
    public static final int STATE_BOTH_HAPPY = 6;
    
    // Game properties
    private static final int    DEFAULT_NUM_OF_CELLS = 9;
    private static final int    DEFAULT_SCORE_METHOD = GoScore.SCORE_METHOD_AREA;
    private static final double DEFAULT_KOMI = 6.5;
    
    private static final String [] PIECES_STR_ABRV = 
    	{" ", "B", "W", "b", "w", "-", "-"};	// debugging b=Black, w=White
    
    // model (2 dimensional int array)
    private int numOfCells = BLANK;
    private int [] currentData, previousData, territories;
    private int gameState;
    private int [] capturedStones;
    private double komi = BLANK;
    private int scoreMethod = BLANK;
     
    /**
     * Blank constructor.
     */
    public GoModel () {
    	this (DEFAULT_NUM_OF_CELLS, DEFAULT_SCORE_METHOD, DEFAULT_KOMI);
    	
    	refreshObservers();
    }
    
    /**
     * Constructor which takes the number of cells.
     * 
     * @param numOfCells
     */
    public GoModel (int numOfCells) {
    	this (numOfCells, DEFAULT_SCORE_METHOD, DEFAULT_KOMI);
    	
    	refreshObservers();
    }
    
    /**
     * Constructor.
     */
    public GoModel (int numOfCells, int scoreMethod, double komi) {        
        reset (numOfCells, scoreMethod, komi);
    }
    
    /**
     * Reset model using number of cells.
     * 
     * @param numOfCells   Number of cells.
     */
    public void reset () {
    	if (numOfCells == BLANK)
    		numOfCells = DEFAULT_NUM_OF_CELLS;
    	if (scoreMethod == BLANK)
    		scoreMethod = DEFAULT_SCORE_METHOD;
    	if (komi == BLANK)
    		komi = DEFAULT_KOMI;
    	
    	reset (this.numOfCells, scoreMethod, komi);
    }
    
    /**
     * Reset the model back to zeros.
     * 
     * @param numOfCells   Number of cells.
     * @param scoreMethod  Score method = area / territory.
     * @param komi         Komi.
     */
    public void reset (int numOfCells, int scoreMethod, double komi) {
        setNumOfCells  (numOfCells);
        setScoreMethod (scoreMethod);
        setKomi        (komi);
        setGameState   (STATE_MOVE);
        
        // Create new data integer array and reset all its values to zero
        this.currentData  = new int [numOfCells * numOfCells];
        this.previousData = new int [numOfCells * numOfCells];
        this.territories  = new int [numOfCells * numOfCells];
        for (int i = 0; i < numOfCells * numOfCells; i++) {
        	currentData [i] = BLANK;
        	previousData [i] = BLANK;
        	territories [i] = BLANK;
        }
        
        // Reset capture counts
        capturedStones = new int [2];
        capturedStones [GoModel.BLACK] = 0; 
        capturedStones [GoModel.WHITE] = 0;
        
        refreshObservers();
    }
    
    /**
     * Set data at a point.
     * 
     * @param x       X co-ordinate.
     * @param y       Y co-ordinate.
     * @param value   Value of the data.
     * @return        True if successful go
     */
    public void setData (int x, int y, int value) {
    	setData (y * numOfCells + x, value);
    }
    
    /**
     * Return data at a particular point using a co-ordinate.
     * 
     * @param x
     * @param y
     * @return
     */
    public int getData (int x, int y) {
        return getData (y * numOfCells + x);
    }
    
    /**
     * Set data on model using index and value.
     * 
     * @param index
     * @param value
     * @return
     */
    public void setData (int index, int value) {
    	currentData [index] = value;
    }
    
    /**
     * Return data at a particular point using an index.
     * 
     * @param index
     * @return
     */
    public int getData (int index) {
    	return currentData [index];
    }
    
    /**
     * Return full data.
     * 
     * @return
     */
    public int [] getData () {
    	return this.currentData;
    }
    
    /**
     * Set data at a point.
     * 
     * @param x       X co-ordinate.
     * @param y       Y co-ordinate.
     * @param value   Value of the data.
     * @return        True if successful go
     */
    public void setPrevData (int x, int y, int value) {
    	setPrevData (y * numOfCells + x, value);
    }
    
    /**
     * Set data on model using index and value.
     * 
     * @param index
     * @param value
     * @return
     */
    public void setPrevData (int index, int value) {
    	this.previousData [index] = value;
            
        refreshObservers();		// update any views on this model
    }
    
    /**
     * Return previous data at a given index.
     * 
     * @param index
     * @return
     */
    public int getPrevData (int index) {
    	return this.previousData [index];
    } 
    
    /**
     * Return data of previous position.
     * 
     * @return
     */
    public int [] getPrevData () {
    	return this.previousData;
    }
        
    /**
     * Set number of cells.
     * 
     * @param numOfCells  Value is 9, 13, 19;
     */
    public void setNumOfCells (int numOfCells) {
        this.numOfCells = numOfCells;
        
        // Update screen.
        refreshObservers();
    }
    
    /**
     * Return the number of columns.
     * 
     * @return
     */
    public int getNumOfCells () {
        return this.numOfCells;
    }
    
    /**
	 * Set the territory.
	 * 
	 * @param index  Index of territory.
	 * @param piece  Game piece.
	 */
	public void setTerritory (int index, int piece) {
		this.territories[index] = piece;
	}
	
	/**
	 * Set the territory.
	 * 
	 * @param x      X co-orindate.
	 * @param y      Y co-ordinate.
	 * @param piece  Value of piece.
	 */
	public void setTerritory (int x, int y, int piece) {
		setTerritory (y * numOfCells + x, piece);
	}
		
	/**
	 * Return the territory piece.
	 * 
	 * @param index
	 * @param piece
	 * @return
	 */
	public int getTerritory (int index) {
		return this.territories[index];
	}
	
	/**
	 * Return territory and a particular x/y co-ordinate.
	 * 
	 * @param x   X-coordinate.
	 * @param y   Y-Cordinate.
	 * @return    
	 */
	public int getTerritory (int x, int y) {
		return getTerritory (y * numOfCells + x);
	}
    
    /**
     * Set the scoring method.
     * 
     * @param scoreMethod
     */
    public void setScoreMethod (int scoreMethod) {
    	this.scoreMethod = scoreMethod;
    }
    
    /**
     * Return the scoring method e.g. area / territory.
     * 
     * @return
     */
    public int getScoreMethod () {
    	return this.scoreMethod;
    }
    
    /**
     * Set komi.
     * 
     * @param Komi.
     */
    public void setKomi (double komi) {
    	this.komi = komi;
    }
    
    /**
     * Return the komi.
     * 
     * @return Komi.
     */
    public double getKomi () {
    	return this.komi;
    }
    
    /**
     * Return the total cell count (num of cells x num of cells). 
     * 
     * @return
     */
    public int getTotalCellCount () {
    	return numOfCells * numOfCells;
    }    
    
    /**
     * Set the game state.
     * 
     * @param gameState   Game state.
     */
    public void setGameState (int gameState) {
    	this.gameState = gameState;
    }
    
    /**
	 * Return true/false if a player can player i.e. its in normal, or either player has just passed.
	 * 
	 * @param player
	 * @return
	 */
	public boolean canMove (int player) {
		return (gameState == STATE_MOVE || gameState == STATE_WHITE_PASS || gameState == STATE_BLACK_PASS);
	}
    
    /**
     * Return true/false if a player can pass.
     * 
     * @param player
     * @return
     */
    public boolean canPass (int player) {
		if (gameState == STATE_MOVE)
			return true;
		else if (gameState == STATE_WHITE_PASS && player == GoModel.BLACK)
			return true;
		else if (gameState == STATE_BLACK_PASS && player == GoModel.WHITE)
			return true;
		
		// Otherwise player cannot pass.
		return false;
	}
    
    /**
     * Return true if a player can be happy / unhappy with choices.
     * 
     * @param player
     * @return
     */
    public boolean canHappyOrUnhappy (int player) {

    	if (player == GoModel.BLACK) {
    		if (gameState == GoModel.STATE_MARK)		
    			return true;
    		else if (gameState == GoModel.STATE_WHITE_HAPPY)
    			return true;
    	}
    	else if (player == GoModel.WHITE) {
    		if (gameState == GoModel.STATE_MARK)		
    			return true;
    		else if (gameState == GoModel.STATE_BLACK_HAPPY)
    			return true;
    	}
		
		// Otherwise player cant be happy / unhappy
    	return false;
    }

	/**
	 * Return the number of captures.
	 * 
	 * @param player
	 * @return
	 */
	public int getCapturedStones (int player) {
		return capturedStones [player];
	}
	
	/**
	 * Return prisoner array.
	 * 
	 * @return
	 */
	public int [] getCapturedStones () {
		return this.capturedStones;
	}
	
	/**
	 * Set the number of captures for a specified player.
	 * 
	 * @param player
	 * @param numOfCaptures
	 */
	public void setCapturedStones (int player, int numOfCaptures) {
		capturedStones [player] = numOfCaptures;
		refreshObservers();
	}
	
	/**
	 * Return a point from an index.
	 * 
	 * @param index
	 * @return
	 */
	public Point getPoint (int index) {
		int x = index % numOfCells;
		int y = index / numOfCells;
		return new Point (x, y);
	}
	
	/**
	 * Add the capture count.
	 * 
	 * @param player
	 * @param numOfCaptures
	 */
	public void addCapturedStoneCount (int player, int numOfCaptures) {
		capturedStones [player] += numOfCaptures;
	}
	
	/**
	 * Return the game state.
	 * 
	 * @return   
	 */
	public int getGameState () {
		return this.gameState;
	}
	
	/**
	 * Signifys that this has been a normal move.
	 */
	public void normalMove() {
		this.gameState = STATE_MOVE;
	}

	/**
	 * Return true / false if this player can mark or not.
	 * 
	 * @return
	 */
	public boolean canMark () {
		return gameState == STATE_MARK;
	}
	
	/**
	 * Return deep copy of GoModel.
	 * 
	 * @return
	 */
	public GoModel deepCopy () {
		GoModel copy = new GoModel (getNumOfCells(), getScoreMethod(), getKomi());
		for (int i = 0; i < numOfCells * numOfCells; i++) {
			copy.setData (i, getData(i));
		}
		copy.setGameState (getGameState());
		copy.setCapturedStones(BLACK, getCapturedStones(BLACK));
		copy.setCapturedStones(WHITE, getCapturedStones(WHITE));
		
		return copy;
	}

	/**
	 * String representation of model for debugging purposes.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer str = new StringBuffer ();
		str.append("Data\n");
		str.append("-------------------\n");
		for (int y = 0; y < numOfCells; y++) {
			for (int x = 0; x < numOfCells; x++) {
				str.append(PIECES_STR_ABRV[getData(x, y) + 1]);
			}
			str.append ("\n");
		}
		str.append("Territory\n");
		str.append("-------------------\n");
		for (int y = 0; y < numOfCells; y++) {
			for (int x = 0; x < numOfCells; x++) {
				str.append(PIECES_STR_ABRV[getTerritory(x, y) + 1]);
			}
			str.append ("\n");
		}
		return str.toString();
	}
	
	/**
	 * Flatten to an XML string.
	 * 
	 * @see org.jogre.common.JogreModel#flatten()
	 */
	public XMLElement flatten() {
		XMLElement state = new XMLElement (Comm.MODEL);

		// Flatten object into XML element
		state.setIntAttribute (XML_ATT_NUM_OF_CELLS, this.numOfCells);
		state.setAttribute (XML_ATT_CURRENT_DATA,    JogreUtils.valueOf (this.currentData));
		state.setAttribute (XML_ATT_PREVIOUS_DATA,   JogreUtils.valueOf (this.previousData));
		state.setAttribute (XML_ATT_TERRITORIES,     JogreUtils.valueOf (this.territories));
		state.setIntAttribute (XML_ATT_GAME_STATE,   this.gameState);
		state.setAttribute (XML_ATT_CAPTURED_STONES, JogreUtils.valueOf (this.capturedStones));
		state.setDoubleAttribute (XML_ATT_KOMI,      this.komi);
		state.setIntAttribute (XML_ATT_SCORE_METHOD, this.scoreMethod);

		return state;
	}
	
	/* (non-Javadoc)
     * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
     */
    public void setState (XMLElement state) {
    	this.numOfCells = state.getIntAttribute(XML_ATT_NUM_OF_CELLS);
    	this.currentData = JogreUtils.convertToIntArray(state.getStringAttribute(XML_ATT_CURRENT_DATA));
	    this.previousData = JogreUtils.convertToIntArray(state.getStringAttribute(XML_ATT_PREVIOUS_DATA));
	    this.territories = JogreUtils.convertToIntArray(state.getStringAttribute(XML_ATT_TERRITORIES));
	    this.gameState = state.getIntAttribute(XML_ATT_GAME_STATE);
	    this.capturedStones = JogreUtils.convertToIntArray(state.getStringAttribute(XML_ATT_CAPTURED_STONES));
	    this.komi = state.getDoubleAttribute(XML_ATT_KOMI);
	    this.scoreMethod = state.getIntAttribute(XML_ATT_SCORE_METHOD);
    }
}