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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.quetris.common.CommQuetrisMove;
import org.jogre.quetris.common.CommQuetrisPlayerState;

/**
 * Controller which controls a game of quetris.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class QuetrisController extends JogreController {

    // Add timers and delays
	private static final int DOWN_DELAY = 1000;
	
	private static final String MOVE_LEFT      = "MOVE_LEFT";
	private static final String MOVE_RIGHT     = "MOVE_RIGHT";
	private static final String MOVE_DOWN      = "MOVE_DOWN";
	private static final String CLOCKWISE      = "CLOCKWISE";
	private static final String ANTI_CLOCKWISE = "ANTI_CLOCKWISE";
	private static final String FLIP_VERTICALLY = "FLIP_VERTICALLY";
	private static final String FLIP_HORIZONTALLY = "FLIP_HORIZONTALLY";
	
	private Timer timer;
	private long timeStarted;
	
	// Add link to model and component
	private QuetrisModel     quetrisModel;
	private QuetrisComponent quetrisComponent;
    
    /**
     * Constructor for a quetris controller.
     * 
     * @param model
     * @param JogreComponent
     */
    public QuetrisController (QuetrisModel quetrisModel, QuetrisComponent quetrisComponent) {
        super (quetrisModel, quetrisComponent);
        
        // Set fields
        this.quetrisModel     = quetrisModel;
        this.quetrisComponent = quetrisComponent;
        
        // Create timer
        timer = new Timer (
            DOWN_DELAY,
    		new ActionListener () {
				public void actionPerformed (ActionEvent evt) {
					if (isGamePlaying()) {
					    moveShapeDown ();
					}
					else
					    timer.stop();
				}
            }
		);
        
        InputMap im = quetrisComponent.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW);   
        
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), MOVE_LEFT);
        quetrisComponent.getActionMap().put(MOVE_LEFT, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                moveShapeLeft ();
            }
        });
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), MOVE_RIGHT);
        quetrisComponent.getActionMap().put(MOVE_RIGHT, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                moveShapeRight ();
            }
        });
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), MOVE_DOWN);
        quetrisComponent.getActionMap().put(MOVE_DOWN, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                moveShapeDown ();
            }
        });
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_V, 0), ANTI_CLOCKWISE);
        quetrisComponent.getActionMap().put(CLOCKWISE, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                moveShapeACW ();
            }
        });     
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), CLOCKWISE);
        quetrisComponent.getActionMap().put(ANTI_CLOCKWISE, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                moveShapeCW();
            }
        });
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), FLIP_VERTICALLY);
        quetrisComponent.getActionMap().put(FLIP_VERTICALLY, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                flipShapeVertically();
            }
        });     
        im.put (KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), FLIP_HORIZONTALLY);
        quetrisComponent.getActionMap().put(FLIP_HORIZONTALLY, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
            	flipShapeHorizontally();
            }
        });
    }
    
    /**
     * Move the shape left is the game is currently playing.
     */
    private void moveShapeLeft () {
        if (isGamePlaying()) 
            quetrisModel.moveShapeLeft();
    }
    
    /**
     * Move the shape left is the game is currently playing.
     */
    private void moveShapeRight () {
        if (isGamePlaying()) 
            quetrisModel.moveShapeRight ();
    }    

    /**
     * Move the shape anti-clockwise.
     */
    private void moveShapeACW () {
        if (isGamePlaying()) 
            quetrisModel.moveShapeACW();
    }
    
    /**
     * Move the shape clockwise.
     */
    private void moveShapeCW () {
        if (isGamePlaying()) 
            quetrisModel.moveShapeCW();
    }    
    
    /**
     * Move the shape anti-clockwise.
     */
    private void flipShapeVertically () {
        if (isGamePlaying()) 
            quetrisModel.flipShapeVertically();
    }
    
    /**
     * Move the shape clockwise.
     */
    private void flipShapeHorizontally () {
        if (isGamePlaying()) 
            quetrisModel.flipShapeHorizontally();
    }   
    
    /**
     * @see org.jogre.client.JogreController#start()
     */
    public void start () {
        // Reset the quetris model (pass in the seat number of player).
        quetrisModel.start (getSeatNum());
        
        // Start the timer
        timer.start();
        timeStarted = System.currentTimeMillis();
    }

    /**
     * Move the shape down.  This is the most complex method in JOGRE Quetris
     * as we need to be able to send a move to the other player if the shape
     * falls on another shape.  We also need to inform the other player if 
     * lines have been created and to create dummy lines on the other player.
     */
    private void moveShapeDown () {
        if (isGamePlaying()) {
            QuetrisPlayerModel curModel = getCurModel();
            
            // Remove shape from model.
            curModel.removeCurrentShape();
            curModel.setCurShapeY (curModel.getCurShapeY() + 1);
            
            // Check that shape fits on grid
            if (!curModel.shapeFits()) {			
                curModel.setCurShapeY (curModel.getCurShapeY() - 1);
                curModel.addCurrentShape();		// add shape to grid 
                
                // See how many lines this has created
                int numOfLines = curModel.checkForFullLines ();
                
                // Inform other user of move
                if (numOfLines == 0) {
	                CommQuetrisMove commQuetrisMove = new CommQuetrisMove (
	                    getSeatNum(),
	                    curModel.getCurShapeNum(),
	                    curModel.getCurShapePos(),            
	                    curModel.getCurShapeX(),
	                    curModel.getCurShapeY(),
	                    curModel.getNextShapeNum());			
	                sendObject (commQuetrisMove);
                }
                else {
                	CommQuetrisPlayerState commState = 
                        new CommQuetrisPlayerState (getSeatNum(), getCurModel().getGameData());
                	sendObject(commState);
                	int opponent = getSeatNum() == 0 ? 1 : 0;
                	sendProperty("lines", opponent, numOfLines);
                }
                
                // Create new shape.
                curModel.newShape ();			
                
                // If shape doesn't fit then game over
                if (!curModel.shapeFits()) {		
                    gameOver ();			    
                    return;
                }
            }
            
            // Add shape back to data structure and 
            curModel.addCurrentShape();			// add shape to grid and return true
        }
    }
    
    /**
     * Receive the object again. 
     * 
     * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
     */
    public void receiveObject (XMLElement message) {
        String name = message.getName();
        
        // Check what the message is
        if (name.equals (CommQuetrisMove.XML_NAME)) {
            CommQuetrisMove commMove = new CommQuetrisMove (message);
            
            // Retrieve player model from seat in message
            QuetrisPlayerModel playerModel = 
                quetrisModel.getPlayerModel (commMove.getSeatNum());
            
            // Add shape to move.
            playerModel.addShape (
                commMove.getCurShapeNum(),
                commMove.getCurShapePos(),
                commMove.getCurShapeX(),
                commMove.getCurShapeY(),
                commMove.getNextShapeNum());
        }
        else if (name.equals (CommQuetrisPlayerState.XML_NAME)) {
            // Update the model with the extra lines
            CommQuetrisPlayerState commState = new CommQuetrisPlayerState (message);
            
            QuetrisPlayerModel playerModel = 
                quetrisModel.getPlayerModel (commState.getSeatNum());
            playerModel.setGameData (commState.getGridData());
        }
    }
    
    /* (non-Javadoc)
     * @see org.jogre.client.JogreController#receiveProperty(java.lang.String, int)
     */
    public void receiveProperty (String key, int seat, int value) {
    	// Add extra lines to this player
    	if ("lines".equals (key)) {
		    if (value > 1) {
		    	if (getSeatNum() == seat)
		    		addExtraLines (value);
		    }
    	}
    }
    
    /**
     * Add lines.
     */
    private void addExtraLines (int numOfLines) {
        int [] extraLines = new int [numOfLines];
        
        // Populate with random numbers (space to be left out)
        for (int i = 0; i < numOfLines; i++) 
            extraLines [i] = (int)(Math.random() * QuetrisPlayerModel.NUM_OF_COLS);
                
        // Update the model with the extra lines
        getCurModel().addExtraLines (extraLines);
        
        // Transfer the information across (without the current shape).
        getCurModel().removeCurrentShape();
        CommQuetrisPlayerState commState = 
            new CommQuetrisPlayerState (getSeatNum(), getCurModel().getGameData());
        getCurModel().addCurrentShape();
        
        sendObject (commState);
    }
    
    /**
     * Quetris game over.
     */
    private void gameOver () {
        CommGameOver commGameOver = new CommGameOver (IGameOver.LOSE);
        conn.send (commGameOver);
    }
    
    /**
     * Return the current player model.
     * 
     * @return
     */
    public QuetrisPlayerModel getCurModel () {
        int seatNum = getSeatNum();
        return quetrisModel.getPlayerModel (seatNum);
    }
}