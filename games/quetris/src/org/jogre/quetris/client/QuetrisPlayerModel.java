/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
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
package org.jogre.quetris.client;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.util.JogreUtils;

/**
 * Model which is used for the quetris grid.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class QuetrisPlayerModel extends JogreModel {
        
	public static final int NUM_OF_COLS = 15;
	public static final int NUM_OF_ROWS = 24;
	public static final int NUM_OF_BLOCKS = 5;	// number of blocks which make up a shape
	public static final int NO_SHAPE    = -1;
	public static final int EXTRA_LINE  = 12;
	
	private int curShapeNum, nextShapeNum, nextShapeNum2;
	private int curShapeX, curShapeY, curPos;

	private int numOfLines, score;
	
	// This two-dimensional array holds the data for the game.
	private int [][] gameData = new int [NUM_OF_COLS][NUM_OF_ROWS]; 
	
	private int index;
	
	// XML attributes
    private static final String XML_PLAYER_MODEL = "player_model";
    private static final String XML_ATT_INDEX        = "index";
    private static final String XML_ATT_DATA         = "data";
    private static final String XML_ATT_CUR_SHAPE    = "cur_shape";
    private static final String XML_ATT_NEXT_SHAPE   = "next_shape";
    private static final String XML_ATT_NEXT_SHAPE_2 = "next_shape_2";
    private static final String XML_ATT_CUR_SHAPE_X  = "cur_shape_x";
    private static final String XML_ATT_CUR_SHAPE_Y  = "cur_shape_y";
    private static final String XML_ATT_CUR_POS      = "cur_pos";
    private static final String XML_ATT_NUM_OF_LINES = "lines";
    private static final String XML_ATT_SCORE        = "score";
		
	/**
	 * Default constructor for the game
	 */
	public QuetrisPlayerModel (int index) {
		// Set index
		this.index = index;
		
		// create new gameData and make all the values = -1 (no shape)
		numOfLines = 0;
		
		// Wipe everything
		wipeAll ();
	}
	
    /* (non-Javadoc)
     * @see org.jogre.common.JogreModel#start()
     */
    public void start() {
        // Wipe every thing		
		wipeAll ();
		
		// And compute shape numbers		// FIXME - Move to server?
		nextShapeNum  = (int)(Math.random() * QuetrisBlockInfo.NUMBER_OF_SHAPES);
		nextShapeNum2 = (int)(Math.random() * QuetrisBlockInfo.NUMBER_OF_SHAPES);
		newShape ();
		addCurrentShape();			// add the block to the data
    }
	
	/**
	 * Wipes the game grid
	 */
	public void wipeAll () {
	    // blank everything
	    numOfLines = 0;
	    score = 0;
	    nextShapeNum = NO_SHAPE;
	    curShapeNum = NO_SHAPE;
	    
	    // Wipe the grid
		for (int i = 0; i < NUM_OF_COLS; i++)
			for (int j = 0; j < NUM_OF_ROWS; j++)
				gameData[i][j] = NO_SHAPE;
		
		// Refresh observers
		refreshObservers();
	}
	
	/**
	 * Return the current shape num.
	 * 
	 * @return
	 */
	public int getCurShapeNum () {
		return curShapeNum;
	}	
	
	/**
	 * Return the current shape num.
	 * 
	 * @return
	 */
	public int getCurShapePos () {
		return curPos;
	}		
	
	/**
	 * Return the next shape num.
	 * 
	 * @return
	 */
	public int getNextShapeNum () {
		return nextShapeNum;
	}
	
	/**
	 * Return the following shape after this shape.
	 * 
	 * @return
	 */
	public int getNextShapeNum2 () {
		return nextShapeNum2;
	}
	
	/**
	 * Return a grid shape number.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getGridShapeNum (int x, int y) {
		return gameData [x][y];
	}
	
	/**
	 * Return the quetris game grid.
	 * 
	 * @return   Game grid as a 2D array.
	 */
	public int [][] getGameData () {
	    return gameData;
	}
	
	/**
	 * Return the number of lines.
	 * 
	 * @return
	 */
	public int getNumOfLines () {
		return numOfLines;
	}
	
	/**
	 * Set number of lines.
	 * 
	 * @param numOfLines
	 */
	public void setNumOfLines (int numOfLines) {
		this.numOfLines	= numOfLines;
	}

	/**
	 * Set score.
	 * 
	 * @param score
	 */
	public void setScore (int score) {
		this.score = score;
	}
	
	/**
	 * Return the score.
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * The current shape becomes the next shape and the next shape 
	 * becomes a random number.
	 */
	public void newShape () {		
		curShapeNum = nextShapeNum;
		nextShapeNum = nextShapeNum2;
		nextShapeNum2 = (int)(Math.random() * QuetrisBlockInfo.NUMBER_OF_SHAPES);
		
		// Set the default position of the current shape
		curShapeX = 5;
		curShapeY = 0;
		curPos = 0;		
		
		// Change and notify observers
		refreshObservers();
	}
	
	/**
	 * Adds the current shape to the grid
	 */
	public void removeCurrentShape () {
		for (int i = 0; i < NUM_OF_BLOCKS; i++) {
			int x = QuetrisBlockInfo.getShapeX(curShapeNum, curPos, i) + curShapeX;
			int y = QuetrisBlockInfo.getShapeY(curShapeNum, curPos, i) + curShapeY;
		
			gameData [x][y] = -1;			
		}
	}
	
	/**
	 * Adds the current shape to the grid
	 */
	public void addCurrentShape () {
		for (int i = 0; i < NUM_OF_BLOCKS; i++) {
			int x = QuetrisBlockInfo.getShapeX(curShapeNum, curPos, i) + curShapeX;
			int y = QuetrisBlockInfo.getShapeY(curShapeNum, curPos, i) + curShapeY;
		
			gameData [x][y] = curShapeNum;
		}
		
		// Change and refresh observers
		refreshObservers();
	}
	
	/**
	 * Adds the current shape to the grid
	 */
	public void addShape (int curShape, int pos, int x, int y, int nextShape) {
	    this.curShapeNum  = curShape;
	    this.curPos       = pos;
	    this.curShapeX    = x;
	    this.curShapeY    = y;
	    this.nextShapeNum = nextShape;
	    
	    // Add this shape to grid and refresh GUI observers
	    addCurrentShape ();
	}	
	
	/** 
	 * This method returns true if the current shape will fit on the screen.
	 * 
	 * @return
	 */
	public boolean shapeFits () {
		for (int i = 0; i < NUM_OF_BLOCKS; i++) {
			int x = QuetrisBlockInfo.getShapeX(curShapeNum, curPos, i) + curShapeX;
			int y = QuetrisBlockInfo.getShapeY(curShapeNum, curPos, i) + curShapeY;
			
			if (x < 0 || x >= NUM_OF_COLS || y < 0 || y >= NUM_OF_ROWS)
				return false;
			
			if (gameData[x][y] != -1)
				return false;
		}
		
		return true;
	}
	
	/** 
	 * This method moves a shape left.
	 * 
	 * @return Returns false if the shape can't move
	 */
	public boolean moveShapeLeft () {
		removeCurrentShape();		// remove shape from the data structure
		curShapeX -= 1;
			
		if (!shapeFits()) {			// check that shape fits on grid
			curShapeX += 1;
			addCurrentShape();			// add shape to grid and return true
			return false;
		}
		
		addCurrentShape();			// add shape to grid and return true
		
		// Update components
		refreshObservers();
		
		return true;
	}
	
	/** This method moves a shape right
	 * @return Returns false if the shape can't move
	 */
	public boolean moveShapeRight () {
		removeCurrentShape();		// remove shape from the data structure
		curShapeX += 1;
		
		if (!shapeFits()) {			// check that shape fits on grid
			curShapeX -= 1;
			addCurrentShape();			// add shape to grid and return true
			return false;
		}
		
		addCurrentShape();			// add shape to grid and return true
		
		// Update observers
		refreshObservers();
		
		return true;
	}
	
	/**
	 * Check for full lines created.
	 * 
	 * @return   Number of lines (between 0 and 4).
	 */
	public int checkForFullLines () {		
	    int lines = 0;
	    // loop from bottom of the grid to the top
		for (int y1 = NUM_OF_ROWS - 1; y1 > 0; y1--) {
			// reset the block count each row 
			int blockCount = 0;
			
			// loop until no move rows to fall down
			do {	
				blockCount = 0;
				for (int x = 0; x < NUM_OF_COLS; x++) {
					if (gameData[x][y1] != -1) 
						blockCount ++;
				}
				
				// if a full line of blocks is detected then move all 
				// the blocks above it down the screen
				if (blockCount == NUM_OF_COLS) {
					numOfLines ++;		// global num of lines
					lines ++;			// cur num of lines
										
					for (int y2 = y1; y2 > 0; y2--) {
						for (int x = 0; x < NUM_OF_COLS; x++) {
							gameData[x][y2] = gameData[x][y2 - 1];							
						}
					}
				}	
			}
			while (blockCount == NUM_OF_COLS);
		}
		
		// Update observers
		refreshObservers();
		
		return lines;
	}
	
	/** 
	 * Move shape clockwise.
	 * 
	 * @return Returns false if the shape can't move
	 */
	public boolean moveShapeCW () {
		removeCurrentShape();		// remove shape from the data structure
		movePositionCW ();
		
		if (!shapeFits()) {			// check that shape fits on grid
			movePositionACW();
			addCurrentShape();			// add shape to grid and return true
			return false;
		}
		
		addCurrentShape();			// add shape to grid and return true
		
		// Update observers
		refreshObservers();
		
		return true;
	}
	
	/** Move shape anti clockwise
	 * @return Returns false if the shape can't move
	 */
	public boolean moveShapeACW () {
		removeCurrentShape();		// remove shape from the data structure
		movePositionACW();
		
		if (!shapeFits()) {			// check that shape fits on grid
			movePositionCW ();
			addCurrentShape();			// add shape to grid and return true
			return false;
		}
		
		addCurrentShape();			// add shape to grid and return true
		
		// Update observers
		refreshObservers();
		
		return true;
	}
	
	
	/**
	 * Flip shape vertically.
	 */
	public boolean flipShapeVertically() {
		removeCurrentShape();		// remove shape from the data structure
		flipPositionVertically ();
		
		if (!shapeFits()) {			// check that shape fits on grid
			flipPositionVertically ();
			addCurrentShape();			// add shape to grid and return true
			return false;
		}
		
		addCurrentShape();			// add shape to grid and return true
		
		// Update observers
		refreshObservers();
		
		return true;
	}

	/**
	 * Flip shape horizontally.
	 */
	public boolean flipShapeHorizontally() {
		removeCurrentShape();		// remove shape from the data structure
		flipPositionHorizontally ();
		
		if (!shapeFits()) {			// check that shape fits on grid
			flipPositionHorizontally ();
			addCurrentShape();			// add shape to grid and return true
			return false;
		}
		
		addCurrentShape();			// add shape to grid and return true
		
		// Update observers
		refreshObservers();
		
		return true;
	}
	
	/**
	 * Set game data.
	 * 
	 * @param gameData
	 */
	public void setGameData (int [][] gameData) {
	    this.gameData = gameData;	    
	    
	    refreshObservers();
	}
	
	/**
	 * Return the position of the current shapes X co-ordinate.
	 * 
	 * @return   X position of current shape.  
	 */
	public int getCurShapeX () {
	    return this.curShapeX;
	}
	
	/**
	 * Return the position on the current shapes Y co-ordinate.
	 * 
	 * @return   Y position of current shape.
	 */
	public int getCurShapeY () {
	    return this.curShapeY;
	}
	
	/**
	 * Set the X position of the current shape. 
	 * 
	 * @param x   X position on screen (0 to num Of columns - 1)
	 */
	public void setCurShapeX (int x) {
	    this.curShapeY = x;
	}
	
	/**
	 * Set the Y position of the current shape. 
	 * 
	 * @param y  Y Position on the screen (0 to num Of rows - 1)
	 */
	public void setCurShapeY (int y) {
	    this.curShapeY = y;
	}	
	
	/**
	 * Set next shape.
	 * 
	 * @param shape
	 */
	public void setNextShape (int shape) {
		this.nextShapeNum = shape;
	}
	
	/**
	 * Set second shape (next shape 2).
	 * 
	 * @param shape
	 */
	public void setNextShape2 (int shape) {
		this.nextShapeNum2 = shape;		
	}
	
	/**
	 * To string (useful for debugging).
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
	    StringBuffer str = new StringBuffer ();
	    for (int y = 0; y < NUM_OF_ROWS; y++) {
	        for (int x = 0; x < NUM_OF_COLS; x++) {
	            str.append (gameData[x][y] + "\t");
	        }
	        str.append ("\n");
	    }
	    return str.toString();
	}

    /**
     * Add extra lines to the DOM.
     * 
     * @param extraLines   Lines to obmit.
     */
    public void addExtraLines (int [] extraLines) {
        
        // Find out how many lines to go down
        int numOfLines = extraLines.length;
        
        // Move rows up line at a time
        for (int i = 0; i < numOfLines; i++) {
            removeCurrentShape ();
            
	        // Move all rows up.
	        for (int y = 1; y < NUM_OF_ROWS; y++) {
	            for (int x = 0; x < NUM_OF_COLS; x++) {
	                gameData[x][y - 1] = gameData[x][y];
	            }
	        }

	        // Add new row	        
	        for (int x = 0; x < NUM_OF_COLS; x++) {
	            if (x != extraLines[i])
	                gameData [x][NUM_OF_ROWS - 1] = EXTRA_LINE;
	            else
	                gameData [x][NUM_OF_ROWS - 1] = NO_SHAPE;
	        }
	        
	        if (shapeFits())
	            addCurrentShape();
	        else if (curShapeY > 0) {
	            curShapeY --;
	            addCurrentShape();
	        }          
        }
        
        refreshObservers();
    }
    
    /**
     * Return the index.
     * 
     * @return
     */
    public int getIndex () {
    	return index;
    }
    
    /**
     * Move position clockwise.
     */
    private void movePositionCW () {
    	curPos ++;
		if (curPos == 4)		// wrap around
			curPos = 0;
		else if (curPos == 8)
			curPos = 4;
    }
    
    /**
     * Move position anti clock wise.
     */
    private void movePositionACW () {
    	curPos --;
		if (curPos == -1)
			curPos = 4;		// wrap around
		else if (curPos == 3)
			curPos = 7;
    }
    
    /**
     * Flip position vertically.
     */
    private void flipPositionVertically () {
    	if (curPos == 0)
			curPos = 4;
		else if (curPos == 4)
			curPos = 0;
		else
			curPos = 8 - curPos;
    }
    
    /**
     * Flip position horizontally.
     */
    private void flipPositionHorizontally () {
    	if (curPos == 7)
    		curPos = 4;
		else if (curPos == 3)
			curPos = 7;
		else
			curPos = 6 - curPos;
    }

	/**
	 * Flatten object.
	 * 
	 * @see org.jogre.common.JogreModel#flatten()
	 */
	public XMLElement flatten () {
		XMLElement state = new XMLElement (XML_PLAYER_MODEL);
		state.setIntAttribute (XML_ATT_INDEX, index);
		state.setIntAttribute (XML_ATT_CUR_SHAPE,    curShapeNum);
		state.setIntAttribute (XML_ATT_NEXT_SHAPE,   nextShapeNum);
		state.setIntAttribute (XML_ATT_NEXT_SHAPE_2, nextShapeNum2);
		state.setIntAttribute (XML_ATT_CUR_SHAPE_X,  curShapeX);
		state.setIntAttribute (XML_ATT_CUR_SHAPE_Y,  curShapeY);
		state.setIntAttribute (XML_ATT_CUR_POS,      curPos);
		state.setIntAttribute (XML_ATT_NUM_OF_LINES, numOfLines);
		state.setIntAttribute (XML_ATT_SCORE,        score);
		
		// Flatten 2d data to a 1d array
        state.setAttribute (XML_ATT_DATA, JogreUtils.valueOf (JogreUtils.convertTo1DArray(gameData)));
		
		return state;
	}

	/**
	 * Set state.
	 * 
	 * @see org.jogre.common.JogreModel#setState(nanoxml.XMLElement)
	 */
	public void setState (XMLElement state) {
		wipeAll();
		index = state.getIntAttribute(XML_ATT_INDEX);
			
        int [] data1D = JogreUtils.convertToIntArray (state.getStringAttribute(XML_ATT_DATA));
        this.gameData      = JogreUtils.convertTo2DArray(data1D, NUM_OF_COLS, NUM_OF_ROWS);
        this.curShapeNum   = state.getIntAttribute(XML_ATT_CUR_SHAPE);
        this.nextShapeNum  = state.getIntAttribute(XML_ATT_NEXT_SHAPE);
        this.nextShapeNum2 = state.getIntAttribute(XML_ATT_NEXT_SHAPE_2);
		this.curShapeX     = state.getIntAttribute(XML_ATT_CUR_SHAPE_X);
		this.curShapeY     = state.getIntAttribute(XML_ATT_CUR_SHAPE_Y);
		this.curPos        = state.getIntAttribute(XML_ATT_CUR_POS);
		this.numOfLines    = state.getIntAttribute(XML_ATT_NUM_OF_LINES);
		this.score         = state.getIntAttribute(XML_ATT_SCORE);
	}
}
