/*
 * JOGRE (Java Online Gaming Real-time Engine) - Camelot
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.camelot.client;

import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.camelot.common.CommCamelotMakeMove;

import java.util.Vector;
import java.util.ListIterator;

// Controller for the Camelot game
public class CamelotController extends JogreController {

	// links to game data and the board component
	protected CamelotModel model;
	protected CamelotBoardComponent boardComponent;

	// A location that is initialized to be "nowhere" and never changed.
	private CamelotLoc nowhereLocation;

	// Current mouse location
	private CamelotLoc currMouseLocation;
	private boolean showingOpponentMove;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param boardComponent		The board component
	 */
	public CamelotController(CamelotModel model, CamelotBoardComponent boardComponent) {
		super(model, boardComponent);

		this.model = model;
		this.boardComponent = boardComponent;

		this.nowhereLocation = new CamelotLoc();
		this.currMouseLocation = new CamelotLoc();
		this.showingOpponentMove = false;
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.resetGame ();
		boardComponent.resetGame();

		showingOpponentMove = false;
		currMouseLocation = nowhereLocation;
	}

	/**
	 * Handle mouse movement events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseMoved(MouseEvent mEv) {
	   	if (isGamePlaying () && isThisPlayersTurn ()) {
			// Convert the graphical (x,y) location to a CamelotLoc
			CamelotLoc newLocation = boardComponent.convertPointToLocation(mEv.getX(), mEv.getY());

			// If the location has moved, then need to figure out what
			// changes need to be made to the display
			if (newLocation.equals(currMouseLocation) == false) {
				// This is our new location
				currMouseLocation = newLocation;

				if (showingOpponentMove) {
					// If we're still showing the opponent's move, then we can only
					// highlight valid start locations
					if (model.validStartLocation(newLocation)) {
						boardComponent.setOutlineLocation(currMouseLocation);
					} else {
						boardComponent.setOutlineLocation(null);
					}
				} else {
					// We're not showing the opponent's move, so need to look at both
					// valid start and end locations

					Vector theMove = findMoveForLocation(newLocation, true);
					if (theMove != null) {
						// This space is an ending space for a valid move.
						boardComponent.setOutlineLocation(null);
						boardComponent.setActiveMove(theMove);
					} else if (model.validStartLocation(newLocation)) {
						// This space contains a piece that the player could move.
						boardComponent.setOutlineLocation(currMouseLocation);
						boardComponent.setActiveMove(null);
					} else {
						// This space has nothing to do
						boardComponent.setOutlineLocation(null);
						boardComponent.setActiveMove(null);
					}
				}

				// repaint the screen now
				boardComponent.repaint();
			}
		}

	}

	/**
	 * Handle mouse Clicked events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseClicked (MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {

			if (showingOpponentMove) {
				// If we're showing the opponent's move at this point, then we
				// need to make the move, then erase it before continuing with
				// other processing of the click.
				model.makeMove(boardComponent.getActiveMove());
				boardComponent.setActiveMove(null);
				showingOpponentMove = false;
			}

			// Convert the graphical (x,y) location to an OctLoc
			CamelotLoc theLocation = boardComponent.convertPointToLocation(mEv.getX(), mEv.getY());

			Vector theMove = findMoveForLocation(theLocation, false);
			if (theMove != null) {
				// This is a valid end location, so clicking on it means that
				// want want to make this move
				model.makeMove(theMove);
				CommCamelotMakeMove moveObj =
						new CommCamelotMakeMove(
							conn.getUsername(),
							theMove,
							getCurrentPlayerSeatNum());
				sendObject (moveObj.flatten());

				// If the game is over, then send gameOver message.
				// (Note: This does *NOT* check for a player not having a valid move.
				//	That check is done in receiveObject when it becomes that other
				//	player's turn)
				if (sendGameOver(model.getWinner()) == false) {
					nextPlayer();
				}

				// Kill the current locations so that they aren't highlighted anymore
				currMouseLocation = nowhereLocation;
				boardComponent.setOutlineLocation(null);
				boardComponent.setActiveMove(null);
				boardComponent.setActiveMoveTree(null);
			} else if (model.validStartLocation(theLocation)) {
				// This is a valid starting location, so clicking on it means that
				// we want to create the new move tree starting at this location and
				// use that to display the valid ending spots
				boardComponent.setActiveMoveTree(model.buildMoveTree(theLocation));
				boardComponent.setActiveMove(null);
			} else {
				// Player clicked on something that is neither a valid start nor end.
				// So, clear the active move & move tree
				boardComponent.setActiveMove(null);
				boardComponent.setActiveMoveTree(null);
			}

			boardComponent.repaint();
		}
	}


	/**
	 * Handle receiving objects from the server
	 *
	 * @param	message			The message from the server
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals (CommCamelotMakeMove.XML_NAME)) {
			// Decode the message into a move
			CommCamelotMakeMove theMoveMsg = new CommCamelotMakeMove (message);
			// Show this move and remember that it's the opponent's move
			boardComponent.setActiveMove(theMoveMsg.getMove());
			showingOpponentMove = true;

			// Update the valid move list for the next player
			// In order to enumerate valid starts, the move has to be made on the
			// board.  However, we want to show the move graphically until the player
			// clicks once.  So, we make the move, update the valid starts and then
			// un-make the move.  When the user clicks, the move is again re-made.
			// (Note: Updating the valid starts returns an end-game code if there are
			//  no valid starts for me.)
			model.makeMove(theMoveMsg.getMove());
			if (getSeatNum() >= 0) {
				sendGameOver(model.updateValidStart(getSeatNum()));
				model.unmakeMove(theMoveMsg.getMove());
			}

			boardComponent.repaint();

		}
	}


	/**
	 * Check to see if the game is over or not.  If the game is over, this will send a game
	 *	over message to the server.
	 *
	 * @param	winnerCode		Code that indicates the winner.
	 *								PLAYER_NONE = no one has won yet.
	 *								PLAYER_ONE_FLAG = Player 1 won.
	 *								PLAYER_TWO_FLAG = Player 2 won.
	 *								PLAYER_NEVER_FLAG = Draw.
	 * @return		true = Game over
	 * @return		false = Game not over
	 */
	private boolean sendGameOver (int winnerCode) {
		int overCode = IGameOver.DRAW;		// Assume draw as default

		// Game not really over
		if (winnerCode == CamelotModel.PLAYER_NONE) {
			return (false);
		}

		// Create a GameOver object with the correct winning code
		if (conn != null) {
			switch (winnerCode) {
				case CamelotModel.PLAYER_ONE_FLAG:
					overCode = ((getSeatNum() == 0) ? IGameOver.WIN : IGameOver.LOSE);
					break;
				case CamelotModel.PLAYER_TWO_FLAG:
					overCode = ((getSeatNum() == 1) ? IGameOver.WIN : IGameOver.LOSE);
					break;
			}

			CommGameOver gameOver = new CommGameOver (overCode);
			conn.send (gameOver);
		}

		return (true);
	}

	/**
	 * Find a move that ends at the given location.
	 *
	 * @param	theLoc		The location to be ending at.
	 * @param	advance		If true, then should find the next move in the family.
	 *						If false, then should find the current move in the family
	 * @return	The move that gets there (if there is one)
	 *			or null, if there is no move that gets there.
	 */
	private Vector findMoveForLocation(CamelotLoc theLoc, boolean advance)
	{
		Vector tree = boardComponent.getActiveMoveTree();
		ListIterator iter;
		CamelotMoveFamily fam;

		if (tree != null) {
			// Search the move tree for the family that ends at currMouseLocation.
			iter = tree.listIterator();
			while (iter.hasNext()) {
				fam = (CamelotMoveFamily) iter.next();
				if (fam.endsAt(theLoc)) {
				// When found, return the next move of that family
					return (fam.nextMove(advance));
				}
			}
		}

		// We didn't find a family with the given end location
		return null;
	}
}
