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
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.common.JogreModel;
import org.jogre.go.common.CommGoMove;

/**
 * Go controller.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoController extends JogreController {
    
    // links to game data and the board component
    protected GoModel          model;
    protected GoPieceMover     pieceMover;
    protected GoBoardComponent boardComponent;
    
    /**
     * Go controller.
     * 
     * @param model
     * @param boardComponent
     */
    public GoController (GoModel model, GoBoardComponent boardComponent) {
        super(model, boardComponent);
        
        this.model = model;						// set fields
        this.pieceMover = new GoPieceMover (model);
        this.boardComponent = boardComponent;
    }
    
    /**
     * @see org.jogre.common.JogreModel#start()
     */
    public void start () {
        model.reset ();
    }
    
    /**
     * Return model.
     * 
     * @return
     */
    public GoModel getModel () {
    	return this.model;
    }
    
    /**
     * Return the piece mover.
     * 
     * @return
     */
    public GoPieceMover getPieceMover () {
    	return this.pieceMover;
    }
    
    /**
     * Response to a mouse click.
     * 
     * @see org.jogre.client.JogreController#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed (MouseEvent e) {
    	int player = getCurrentPlayerSeatNum();
    	
    	// Ensure game is playing and its this players turn
        if (isGamePlaying()) {			
        	
        	// get board point from screen position of mouse click
            Point board = boardComponent.getBoardCoords (e.getX(), e.getY());
            
            // ensure board point is in range
            int numOfCells = model.getNumOfCells();
            if (board.x >= 0 && board.x < numOfCells && board.y >= 0 && board.y < numOfCells) {

            	// Depending on state, either move or mark
            	if (model.canMark()) {
            		mark (board.x, board.y);
            	}
            	else if (isThisPlayersTurn ()) {	// try and move 
            		// Try and make a move
            		move (board.x, board.y, player);	
            	}
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.jogre.client.JogreController#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
    	if (isGamePlaying () && isThisPlayersTurn ()) {
			Point point = boardComponent.getBoardCoords (e.getX(), e.getY());
			int x = point.x, y = point.y;
			
			if (x >= 0 && y >= 0 && x < model.getNumOfCells() && y < model.getNumOfCells()) {
				if (pieceMover.isValidMove(point.x, point.y, getCurrentPlayerSeatNum())) {
					boardComponent.setDragPoint(point);
					boardComponent.repaint();
					return;
				}
			}
		}    	
    	
    	// update the board
    	boardComponent.setDragPoint (AbstractBoardComponent.OFF_SCREEN_POINT);
		boardComponent.repaint();
	}
    
    /**
     * Update the model if a move is acceptable.
     * 
     * @param x       X co-ordinate of the move.
     * @param y       Y co-ordinate of the move.
     * @param player  Player who is making move.
     */
    public void move (int x, int y, int player) {
        // update model and change player turn
        if (pieceMover.isValidMove(x, y, player))
        {
        	pieceMover.move(x, y, player);
        	model.refreshObservers();		// update views
        	
        	// send move to server
			CommGoMove commMove = new CommGoMove (CommGoMove.TYPE_MOVE, conn.getUsername(), x, y);
			sendObject(commMove);
        	
            // next player turn.
            nextPlayer ();
        }
    }

	/**
	 * Player makes a pass.
	 */
	public void pass () {
		int player = getCurrentPlayerSeatNum();		
		if (isGamePlaying() && isThisPlayersTurn() && model.canPass(player)) {
			pieceMover.setPass (player);

			// send move to server
			CommGoMove commMove = new CommGoMove (CommGoMove.TYPE_PASS,  conn.getUsername());
			sendObject(commMove);
			
			// next player turn.
			nextPlayer();		
		}
	}

	/**
	 * Mark the board where group is dead.
	 * 
	 * @param x  X board position.
	 * @param y  Y 
	 */
	public void mark (int x, int y) {
		if (isGamePlaying() && model.canMark()) {
			pieceMover.mark (x, y);
			
			// send move to server
			CommGoMove commMove = new CommGoMove (CommGoMove.TYPE_MARK,  conn.getUsername(), x, y);
			sendObject(commMove);
			
			// Refresh observers
			model.refreshObservers();
		}
	}
	
	/**
	 * User is happy with selected
	 */
	public void happy () {
		int player = getSeatNum();
		if (isGamePlaying()) {
			pieceMover.setHappy(player);

			// send move to server
			CommGoMove commMove = new CommGoMove (CommGoMove.TYPE_HAPPY, conn.getUsername());
			sendObject(commMove);
		}
	}

	/**
	 * User is unhappy with selected 
	 */
	public void notHappy() {
		int player = getSeatNum();	
		if (isGamePlaying()) {
			pieceMover.setUnhappy(player);

			// send move to server 
			CommGoMove commMove = new CommGoMove (CommGoMove.TYPE_UNHAPPY, conn.getUsername());
			sendObject(commMove);		
		}
	}
	
	/**
     * Update territories to show
     */
    public void updateTerritories() {
    	pieceMover.updateTerritories();
    }
    
    /**
     * Receive object. 
     * 
     * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
     */
    public void receiveObject (XMLElement object) { 
	    if (object.getName().equals(CommGoMove.XML_NAME)) {
			CommGoMove move = new CommGoMove (object);
			GoModel goModel = ((GoModel)model);
			GoPieceMover pieceMover = new GoPieceMover (goModel);
			
			int player = getSeatNum(move.getUsername());
			switch (move.getStatus()) {
				case CommGoMove.TYPE_MOVE:
					pieceMover.move(move.getX(), move.getY(), player);
					break;
				case CommGoMove.TYPE_MARK:
					pieceMover.mark(move.getX(), move.getY());
					break;
				case CommGoMove.TYPE_PASS:
					pieceMover.setPass(player);
					break;
				case CommGoMove.TYPE_HAPPY:
					pieceMover.setHappy(player);
					break;
				case CommGoMove.TYPE_UNHAPPY:
					pieceMover.setUnhappy(player);
					break;
			}
			model.refreshObservers();
		}
    }

}