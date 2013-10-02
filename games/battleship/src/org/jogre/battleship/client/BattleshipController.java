/*
 * JOGRE (Java Online Gaming Real-time Engine) - Battleship
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
package org.jogre.battleship.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.jogre.battleship.common.CommBattleshipMove;
import org.jogre.battleship.common.CommBattleshipPlaceShip;
import org.jogre.client.JogreController;
import org.jogre.client.awt.ChatGameComponent;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.JogreUtils;

/**
 * Controller which updates the model and listens to user input on the visual
 * board.
 * 
 * @author Gman, JavaRed
 * @version Alpha 0.2.3
 */
public class BattleshipController extends JogreController {

	// Model and board component
	protected BattleshipModel model = null;
	protected BattleshipBoardComponent board = null;
	protected ChatGameComponent chatGameComponent = null;
    
	/**
	 * Constructor
	 * 
	 * @param model
	 * @param boardComponent
	 */
	public BattleshipController (BattleshipModel model, 
                                 BattleshipBoardComponent board,
                                 ChatGameComponent chatGameComponent) {
		super(model, board);

		this.model = model;
		this.board = board;
        this.chatGameComponent = chatGameComponent;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		if (isPlayerPlaying()) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			Point boardPoint = this.model.getBoardPoint(mouseX, mouseY);
			if (this.model.stillPlacingShips(getSeatNum()))
				this.model.setBoardPlacingPoint(boardPoint);
			else
				this.model.setBoardPlacingPoint(null);
			
			if (!this.model.stillPlacingShips() && isThisPlayersTurn())
				this.model.setBoardFiringPoint(boardPoint);
			else
				this.model.setBoardFiringPoint(null);

			this.model.refreshObservers();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent e) {
		if (isPlayerPlaying()) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			Point boardPoint = this.model.getBoardPoint(mouseX, mouseY);
			this.model.setMousePressedPoint(boardPoint);
		}
		else
			this.model.setMousePressedPoint(null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased (MouseEvent e) {
		if (isPlayerPlaying()) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			Point boardPoint = this.model.getBoardPoint(mouseX, mouseY);
			
			if (this.model.stillPlacingShips(getSeatNum())) {
				if (SwingUtilities.isRightMouseButton(e)) {
					this.model.flipShipPlacing(getSeatNum());
					this.model.setBoardPlacingPoint(boardPoint);
					this.model.refreshObservers();
				} else if (boardPoint.equals(this.model.getMousePressedPoint()) &&
						this.model.validPlacementForShip(getSeatNum(), boardPoint)) {
					placeShip(boardPoint.x, boardPoint.y);
				}
				return;
			}
		
			if (this.model.stillPlacingShips())
				return;
			
			if (isThisPlayersTurn()) {
				if (boardPoint.equals(this.model.getMousePressedPoint())) {

					boardPoint.setLocation(boardPoint.getX() - BattleshipModel.BOARD_SIZE - 1, boardPoint.getY());
					if (model.isValidMove(getSeatNum(), boardPoint.x, boardPoint.y)) {
						move(boardPoint.x, boardPoint.y);
					}
					this.model.setBoardFiringPoint(null);
				}
			}
		}
	}
	
	/**
	 * Place ship
	 * 
	 * @param x
	 * @param y
	 */
	public void placeShip(int x, int y) {

		// Get seat number
		int seatNum = getSeatNum();
		
		// Get ship placement information
		int size = model.getPlacingShipSize(seatNum);
		int ship = model.getPlacingShipName(seatNum);
		boolean horizontal = model.isShipPlacedHorizontally(seatNum);

		// Place ship in model
		this.model.placeShip(seatNum, new Point(x, y));
		
		// Send ship placement to server
		CommBattleshipPlaceShip placeShip = new CommBattleshipPlaceShip(seatNum, x, y, size, ship, horizontal);
		conn.send (placeShip);
	}

	/**
	 * Make a move
	 * 
	 * @param x
	 * @param y
	 */
	public void move(int x, int y) {
		
		// Set move in model
		model.setMove(this.getSeatNum(), x, y);
		int player   = getSeatNum();
        int opponent = JogreUtils.invert (player);
                
		// Next player turn.
		nextPlayer();

		// Send move to server
		CommBattleshipMove move = new CommBattleshipMove(getSeatNum(), x, y);
		conn.send (move);
        
		// Check game over
        checkGameOver ();
        
        // Update the message box
        int[][][] getPlacedShips = model.getPlacedShips();
        if (chatGameComponent != null) {
            StringBuffer label = new StringBuffer("ship.");
            if (model.getSeatNum() == player)
                label.append("enemy.");
            else
                label.append("mine.");
            if (model.sunkShip (opponent, getPlacedShips[opponent][x][y]))
                label.append("sunk.");
            else
                label.append("hit.");
            label.append (model.getShipIndex (getPlacedShips[opponent][x][y]));
            GameLabels labels = GameLabels.getInstance();
            chatGameComponent.receiveMessage (labels.get("game"), labels.get(label.toString()));
        }   
	}
    
    /**
     * Check game over.
     */
    private void checkGameOver () {          
        // Status is either -1, DRAW or WIN
        int resultType = -1;
        int curSeatNum = getSeatNum ();
        boolean isGameOver = model.isGameWon(curSeatNum); 
        if (isGameOver)
            resultType = IGameOver.WIN;

        // Create game over object if a win or draw
        if (resultType != -1 && conn != null) {
            
            // Inform the server
            CommGameOver commGameOver = new CommGameOver (resultType);
            conn.send (commGameOver);
        }
    }
	
	/**
	 * Start game by reseting model and assigning current client's
	 * seat number to the model.
	 * 
	 * @see org.jogre.client.JogreController#start()
	 */
	public void start() {
		model.setSeatNum(this.getSeatNum());
		model.start();
        
        if (this.chatGameComponent != null) {
            GameLabels labels = GameLabels.getInstance();
            chatGameComponent.receiveMessage(labels.get("game"), labels.get("place.ships"));;
        }
	}
}