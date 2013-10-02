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
import java.util.Vector;

/**
 * Class responsible for determing if a piece is a valid move or not.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoPieceMover {

	private GoModel mainModel;
	
	/**
	 * Constructor which takes a go model.
	 * 
	 * @param goModel
	 */
	public GoPieceMover (GoModel goModel) {
		this.mainModel = goModel;
	}
	
	/**
	 * Important method which denotes if a move is valid or not.
	 * 
	 * @param x   X co-ordinate of move.
	 * @param y   Y co-ordinate of move.
	 * @return    True if valid move.
	 */
	public boolean isValidMove (int x, int y, int player) {
		
		// Create booleans
		boolean correctState = mainModel.canMove (player);
		boolean blankPiece   = isBlankPiece (x, y);		
		boolean isSuicide    = isSuicide (x, y, player);
		boolean isKORule     = isKORule  (x, y, player);

		// Create over all success boolean
		boolean validMove = correctState && blankPiece && !isSuicide && !isKORule;
		
		return validMove;
	}

	/**
	 * Take a move using main model.
	 * 
	 * @param x
	 * @param y
	 * @param player
	 */
	public void move (int x, int y, int player) {
		// Check if move is valid, then current state of model becomes previous state
		if (isValidMove (x, y, player)) {
			int numOfCells = mainModel.getNumOfCells();
			for (int i = 0; i < numOfCells * numOfCells; i++)
				mainModel.setPrevData (i, mainModel.getData(i));
			
			move (mainModel, player, x, y);
			if (mainModel.getGameState() != GoModel.STATE_MOVE)
				updateTerritories();
			
			mainModel.refreshObservers();
		}
	}
	
	/**
	 * Return player groups on main model.
	 * 
	 * @param piece
	 * @return
	 */
	public Vector getPlayerGroups (int piece) {
		return getPlayerGroups (mainModel, piece);
	}
	
	/**
	 * Return a single player group.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector getPlayerGroup (int x, int y) {
		// Create copy of model
		GoModel modelCopy = mainModel.deepCopy();
		int player = modelCopy.getData (x, y);
		
		Vector group = new Vector ();
		buildGroup (modelCopy, player, group, x, y); 
		
		return group;
	}
	
	/**
	 * Return score object.
	 * 
	 * @return
	 */
	public GoScore getScore() {
		// Update territories
		updateTerritories();
		
		double komi        = mainModel.getKomi();
		int scoreMethod    = mainModel.getScoreMethod();
		int [] areas       = new int [2];
		int [] territories = new int [2];
		int [] prisoners   = {mainModel.getCapturedStones (GoModel.WHITE), 
				              mainModel.getCapturedStones (GoModel.BLACK)};
		
		// Loop through each index and
		for (int i = 0; i < mainModel.getTotalCellCount(); i++) {
			
			int territory = mainModel.getTerritory(i);
			int piece     = mainModel.getData(i);
			
			// Update territory / areas
			if (territory == GoModel.BLACK) {
				areas [GoModel.BLACK] ++;
				if (piece != GoModel.BLACK)
					territories [GoModel.BLACK] ++;
			}
			else if (territory == GoModel.WHITE) {
				areas [GoModel.WHITE] ++;
				if (piece != GoModel.WHITE)
					territories [GoModel.WHITE] ++;
			}
		
			// Update prisoners depending on marked dead pieces
			if (piece == GoModel.BLACK_MARKED_DEAD) 
				prisoners [GoModel.WHITE] ++;
			else if (piece == GoModel.WHITE_MARKED_DEAD)
				prisoners [GoModel.BLACK] ++;
		}
		
		// Create score object
		GoScore score = new GoScore (komi, scoreMethod, areas, territories, prisoners);
		return score;
	}

	/**
	 * Mark a group as dead.  If a piece is already dead, then mark as normal again.
	 * 
	 * @param x
	 * @param y
	 */
	public void mark (int x, int y) {
		int piece = mainModel.getData (x, y);
		if (piece >= GoModel.BLACK && piece <= GoModel.WHITE_MARKED_DEAD) {
			piece = piece < GoModel.BLACK_MARKED_DEAD ? piece + 2 : piece - 2;
			
			Vector group = getPlayerGroup (x, y);
			updateGroup (group, piece);
			updateTerritories();
		}
		mainModel.refreshObservers();
	}
		
	/**
	 * Update the territories on the main go model.
	 * 
	 * @return
	 */
	public void updateTerritories () {
		
		// Create dummy model and populate only with white/black pieces (ignore marked pieces)
		int numOfCells = mainModel.getNumOfCells();
		GoModel modelCopy = mainModel.deepCopy();
		
		for (int i = 0; i < numOfCells * numOfCells; i++) {
			int piece = mainModel.getData(i);
			if (piece <= GoModel.WHITE) 
				modelCopy.setData (i, piece);
			else 
				modelCopy.setData (i, GoModel.BLANK);
		}		
		
		// Retrieve all blank groups
		Vector blankGroups = getPlayerGroups (modelCopy, GoModel.BLANK);
		
		// Declare variables
		int index;
		Point p;
		
		// Check each group to see if blank, black territory or white territory.
		for (int i = 0; i < blankGroups.size(); i++) {
			Vector blankGroup = (Vector)blankGroups.get(i);
			
			// Go through each piece and see if it piece is currently blank
			// then check to see if borderd by black or white piece.
			boolean blackNeighbour = false, whiteNeighbour = false;
			for (int j = 0; j < blankGroup.size(); j++) {
				index = ((Integer)blankGroup.get(j)).intValue();
				p = modelCopy.getPoint (index);		// retrieve point from index
				
				// Populate booleans if neighbour is equal to black / white
				if (p.x > 0 && modelCopy.getData (p.x - 1, p.y) == GoModel.WHITE) whiteNeighbour = true;
				if (p.x > 0 && modelCopy.getData (p.x - 1, p.y) == GoModel.BLACK) blackNeighbour = true;
				if (p.x < numOfCells - 1 && modelCopy.getData (p.x + 1, p.y) == GoModel.WHITE) whiteNeighbour = true;
				if (p.x < numOfCells - 1 && modelCopy.getData (p.x + 1, p.y) == GoModel.BLACK) blackNeighbour = true;
				if (p.y > 0 && modelCopy.getData (p.x, p.y - 1) == GoModel.WHITE) whiteNeighbour = true;
				if (p.y > 0 && modelCopy.getData (p.x, p.y - 1) == GoModel.BLACK) blackNeighbour = true;
				if (p.y < numOfCells - 1 && modelCopy.getData (p.x, p.y + 1) == GoModel.WHITE) whiteNeighbour = true;
				if (p.y < numOfCells - 1 && modelCopy.getData (p.x, p.y + 1) == GoModel.BLACK) blackNeighbour = true;
			}
			
			// Update model 
			if (blackNeighbour && !whiteNeighbour) updateGroup(modelCopy, blankGroup, GoModel.BLACK);
			if (!blackNeighbour && whiteNeighbour) updateGroup(modelCopy, blankGroup, GoModel.WHITE);
		} 
		
		// Update territories from model copy to main model
		for (int i = 0; i < mainModel.getTotalCellCount(); i++) {
			mainModel.setTerritory (i, modelCopy.getData(i));
		}
	}

	/**
	 * Reset a marked board to show
	 */
	public void resetMarkedBoard () {
		for (int i = 0; i < mainModel.getTotalCellCount(); i++) {
			int piece = mainModel.getData(i);
			if (piece == GoModel.BLACK_MARKED_DEAD)
				piece = GoModel.BLACK;
			else if (piece == GoModel.WHITE_MARKED_DEAD)
				piece = GoModel.WHITE;
		}
	}
	
	/**
	 * Update a group of values in model using an index using the main model.
	 * 
	 * @param indexes
	 * @param value
	 */
	private void updateGroup (Vector indexes, int player) {
		updateGroup(mainModel, indexes, player);
	}
	
	/**
	 * Update group using model, indexes and specified piece.
	 * 
	 * @param model
	 * @param indexes
	 * @param piece
	 */
	private void updateGroup (GoModel model, Vector indexes, int piece) {
		for (int i = 0; i < indexes.size(); i++) {
			model.setData (((Integer)indexes.get(i)).intValue(), piece);
		}
	}

	/**
	 * Return player groups.
	 * 
	 * @param piece
	 * @return
	 */
	private Vector getPlayerGroups (GoModel model, int piece) {
		// Create copy of model
		GoModel modelCopy = model.deepCopy (); 
		int numCells      = model.getNumOfCells ();
		
		// Create new groups vector 
		Vector groups = new Vector ();
		
		// Loop through the list
		int index;
		for (int y = 0; y < numCells; y++) {
			for (int x = 0; x < numCells; x++) {
				index = y * numCells + x;
				if (modelCopy.getData(index) == piece) {
					Vector group = new Vector ();
				
					// Find neighbours of this piece			
					buildGroup (modelCopy, piece, group, x, y);
					groups.add (group);
				}
			}
		}
		
		return groups;
	}
	
	/**
	 * Take a move using a specified model.
	 * 
	 * @param model
	 * @param player
	 * @param x
	 * @param y
	 */
	private void move (GoModel model, int player, int x, int y) {
		model.setData (x, y, player);
		model.normalMove ();		// change state so signify normal move (gets rid of pass states).
		
		// Check if capture
		checkCapture (model, player);
	}
	
	/**
	 * Return true / false if this move results in KO.
	 * 
	 * @param x
	 * @param y
	 * @param player
	 * 
	 * @return       If true KO rule applys.
	 */
	public boolean isKORule(int x, int y, int player) {
		// Check if move is valid, then take a copy of data, make a move on it
		// and see if state of model equals state of previous data of current model.		
		boolean blankPieceNoSuicide = isBlankPiece (x, y) && !isSuicide (x, y, player);		
		if (blankPieceNoSuicide) {
			
			GoModel copy = mainModel.deepCopy();
			move (copy, player, x, y);			// do move on data copy.
			
			// if ANY of previous data is not same as move on copy then 
			// return false i.e. KO rule doesn't not apply as model is not the same
			int [] prevData = mainModel.getPrevData();			
			for (int i = 0; i < prevData.length; i++) {
				if (prevData[i] != copy.getData(i))
					return false;
			}
				
		}
		
		return true;		// state of 
	}
	
	/**
	 * Build group using model.
	 * 
	 * @param model
	 * @param piece
	 * @param group
	 * @param x
	 * @param y
	 */
	private void buildGroup (GoModel model, int piece, Vector group, int x, int y) {
		int numCells = model.getNumOfCells();
		
		// Perform validation
		if (x < 0 || x >= numCells || y < 0 || y >= numCells)
			return;
		
		// Note note of index and value
		int index = y * numCells + x;
		int value = model.getData (index);
		
		// If value == player then add to array and recurse 
		if (value == piece) {
			model.setData (x, y, GoModel.MARKED);
			group.add (new Integer (index));		// take a note of index 
			
			buildGroup (model, piece, group, x - 1, y);	// Check left
			buildGroup (model, piece, group, x, y - 1);	// Check up
			buildGroup (model, piece, group, x + 1, y);	// Check right
			buildGroup (model, piece, group, x, y + 1);	// Check down
		}
	}
	
	/**
	 * Check if move results in a capture for a particular player.
	 * 
	 * @param model    Model to use (may be a copied model).
	 * @param player   Player to check capture of.
	 */
	private void checkCapture (GoModel model, int player) {
		int numCells  = model.getNumOfCells();
		int index = 0, x, y;
		
		// Get all opponent groups
		int opponentPlayer = getOpponent (player);
		Vector groups = getPlayerGroups (model, opponentPlayer);
			
		// Loop through each group and check for capture i.e. no liberties exist
		for (int g = 0; g < groups.size(); g++) {
			Vector group = (Vector)groups.get(g);
			
			// Go through each position in the group and ensure that 
			// if either its north, south, east or west position is 
			// a blank square then it is safe
			boolean libertyFound = false;		// assume none found
			for (int i = 0; i < group.size(); i++) {
				index = ((Integer)group.get(i)).intValue();
				x = index % numCells;
				y = (index / numCells);
				
				if (x - 1 >= 0 && model.getData (x - 1, y) == GoModel.BLANK) 
					libertyFound = true;
				if (x + 1 < numCells && model.getData (x + 1, y) == GoModel.BLANK) 
					libertyFound = true;
				if (y - 1 >= 0 && model.getData(x, y - 1) == GoModel.BLANK) 
					libertyFound = true;
				if (y + 1 < numCells && model.getData(x, y + 1) == GoModel.BLANK) 
					libertyFound = true;
			}
			
			// If no liberties found then capture piece and remove pieces from data 
			if (!libertyFound) {				
				model.addCapturedStoneCount (opponentPlayer, group.size());
				for (int i = 0; i < group.size(); i++) {
					index = ((Integer)group.get(i)).intValue();
					model.setData (index, GoModel.BLANK);
				}
			}
		}
	}
	
	/**
	 * Return true/false if current piece is blank or not.
	 * 
	 * @param x
	 * @param y
	 * @return   True if blank.
	 */
	public boolean isBlankPiece (int x, int y) {
		int curPiece = mainModel.getData(x, y);
		return curPiece == GoModel.BLANK;
	}
	
	/**
	 * Check if this move is a suicide move or not.
	 * @param moveX   Move X co-orindate.
	 * @param moveY   Move Y co-orindate.
	 * @param player  Player 
	 * 
	 * @return
	 */
	public boolean isSuicide (int moveX, int moveY, int player) {			
		// Create copy of current model, make move on copy 
		// and see if players group from x, y has any liberties.
		GoModel copy = mainModel.deepCopy();
		
		// Do move and if this move results in this group being removed then its obviously suicide
		move (copy, player, moveX, moveY);
		
		// Check if capture
		checkCapture (copy, getOpponent(player));
		
		return (copy.getData(moveX, moveY) == GoModel.BLANK);
	}
	
	/**
     * Player is trying to pass.  This can only happen if the state of the 
     * game is normal or the other player has just passed.
     * 
     * @param passCount
     */
    public void setPass (int player) {
    	int gameState = mainModel.getGameState();
    	
    	if (player == GoModel.BLACK) {
    		if (gameState == GoModel.STATE_MOVE)		
    			mainModel.setGameState (GoModel.STATE_BLACK_PASS);
    		else if (gameState == GoModel.STATE_WHITE_PASS)
    			mainModel.setGameState (GoModel.STATE_MARK);
    	}
    	else if (player == GoModel.WHITE) {
    		if (gameState == GoModel.STATE_MOVE)		
    			mainModel.setGameState (GoModel.STATE_WHITE_PASS);
    	else if (gameState == GoModel.STATE_BLACK_PASS)
    		mainModel.setGameState (GoModel.STATE_MARK);
    	}
    	
    	mainModel.refreshObservers();
    }
    
    /**
     * A player is happy with the marked pieces 
     * 
     * @param player
     */
    public void setHappy (int player) {
    	int gameState = mainModel.getGameState();
    	
    	if (player == GoModel.BLACK) {
    		if (gameState == GoModel.STATE_MARK)		
    			mainModel.setGameState (GoModel.STATE_BLACK_HAPPY);
    		else if (gameState == GoModel.STATE_WHITE_HAPPY)
    			mainModel.setGameState (GoModel.STATE_BOTH_HAPPY);
    	}
    	else if (player == GoModel.WHITE) {
    		if (gameState == GoModel.STATE_MARK)		
    			mainModel.setGameState (GoModel.STATE_WHITE_HAPPY);
    		else if (gameState == GoModel.STATE_BLACK_HAPPY)
    			mainModel.setGameState (GoModel.STATE_BOTH_HAPPY);
    	}
    	
    	mainModel.refreshObservers();
    }
    
    /**
     * Player is unhappy with the pieces - simply reset to STATE_MARK
     * 
     * @param player
     */
    public void setUnhappy (int player) {
    	mainModel.setGameState (GoModel.STATE_MARK);
    	mainModel.refreshObservers();
    }
	
	/**
	 * Return opponent player.
	 * 
	 * @param player
	 * @return
	 */
	private int getOpponent (int player) {
		return player == GoModel.BLACK ? GoModel.WHITE : GoModel.BLACK;
	}
}