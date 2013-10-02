/*
 * JOGRE (Java Online Gaming Real-time Engine) - Warwick
 * Copyright (C) 2003 - 2008  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.warwick.client;

import nanoxml.XMLElement;

import java.awt.event.MouseEvent;

import org.jogre.warwick.common.WarwickModel;
import org.jogre.client.JogreController;

import org.jogre.warwick.common.CommWarwickChooseAllegience;
import org.jogre.warwick.common.CommWarwickSlidePiece;

/**
 * Controller for the warwick game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class WarwickController extends JogreController {

	// links to game data and the board component
	protected WarwickModel          model;
	protected WarwickBoardComponent boardComponent;

	/**
	 * Default constructor for the warwick controller which takes a
	 * model and a view.
	 *
	 * @param model           Warwick model.
	 * @param boardComponent  Warwick view.
	 */
	public WarwickController (
		WarwickModel          model,
		WarwickBoardComponent boardComponent
	) {
		super (model, boardComponent);

		this.model = model;
		this.boardComponent = boardComponent;
	}

	/**
	 * Start method which restarts the model.
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.reset ();
	}

	/**
	 * Implementation of the mouse moved interface.
	 *
	 * @see java.awt.event.MouseListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved (MouseEvent mEv) {
		boolean redraw = false;

		// Always allow score markers to be moused over, even if the game is over.
		if (boardComponent.selectScoreMarkerAt(mEv.getX(), mEv.getY())) {
			boardComponent.repaint();
		} else if (isGamePlaying()) {
			if (model.isChooseAllegience()) {
				redraw = boardComponent.selectAllegienceCardAt(mEv.getX(), mEv.getY());
			} else if (isThisPlayersTurn()) {
				if (model.isPlacePiece()) {
					redraw = boardComponent.placePotentialPieceAt(mEv.getX(), mEv.getY());
				} else if (model.isSlidePiece()) {
					redraw = boardComponent.selectSlidablePieceAt(mEv.getX(), mEv.getY());
				}
			}

			if (redraw) {
				boardComponent.repaint();
			}
		}
	}

	/**
	 * Implementation of the mouse pressed interface.
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isGamePlaying()) {
			if (model.isChooseAllegience()) {
				boardComponent.selectAllegienceCardAt(mEv.getX(), mEv.getY());
				tryChooseAllegience();
			} else if (isThisPlayersTurn()) {
				if (model.isPlacePiece()) {
					boardComponent.placePotentialPieceAt(mEv.getX(), mEv.getY());
					boolean validMove = tryPlacePiece();
					if (validMove) {
						// If we've placed a piece, then immediately go into
						// sliding and select the current piece that we just
						// placed.
						boardComponent.selectSlidablePieceAt(mEv.getX(), mEv.getY());
					}
				} else if (model.isSlidePiece()) {
					boardComponent.selectSlidablePieceAt(mEv.getX(), mEv.getY());
					trySlidePiece();
				}
			}
		}
	}

	/*
	 * Try to select the allegience card being pointed at.
	 *
	 * @return if this is a valid move or not.
	 */
	private boolean tryChooseAllegience () {
		int selectedCard = boardComponent.getSelectedAllegience();

		// Verify valid selection.
		if ((selectedCard < 0) ||
		    (selectedCard >= model.getNumPlayers()) ||
		    (selectedCard == getSeatNum())) {
			return false;
		}

		// Make selection locally
		model.chooseAllegienceClearOthers(getSeatNum(), selectedCard);

		// Stop selecting allegience
		model.setGamePhase(WarwickModel.POST_ALLEGIENCE);
		boardComponent.clearAllegienceCardSelection();

		// See if we're ready to start the next round
		if (model.allChosenAllegience()) {
			model.clearBoard();
			model.setGamePhase(WarwickModel.PLACE_PIECE);
		}

		// Tell the server
		CommWarwickChooseAllegience theMsg = new CommWarwickChooseAllegience (
		   conn.getUsername(), selectedCard);
		conn.send(theMsg);

		return true;
	}

	/*
	 * Try to place a piece at the space being pointed at.
	 *
	 * @return if this is a valid move or not.
	 */
	private boolean tryPlacePiece () {
		int region = boardComponent.getPotentialPieceRegion();
		int space = boardComponent.getPotentialPieceSpace();

		// Place the piece locally
		if (!model.addPiece(getSeatNum(), region, space)) {
			// Not a valid move...
			return false;
		}

		// Stop placing pieces in the GUI
		boardComponent.clearPotentialPieceSelection();

		// Advance to sliding a piece
		model.setGamePhase(WarwickModel.SLIDE_PIECE);

		// Tell the server
		CommWarwickSlidePiece theMsg = new CommWarwickSlidePiece (
		    conn.getUsername(), -1, -1, region, space);
		conn.send(theMsg);

		return true;
	}

	/*
	 * Try to slide the piece being pointed at.
	 *
	 * @return if this is a valid move or not.
	 */
	private boolean trySlidePiece () {
		int fromRegion = boardComponent.getSlidePieceRegion();
		int fromSpace  = boardComponent.getSlidePieceSpace();
		int toRegion   = boardComponent.getTargetPieceRegion();
		int toSpace    = boardComponent.getTargetPieceSpace();

		// Slide the piece locally
		if (!model.slidePiece(fromRegion, fromSpace, toRegion, toSpace)) {
			// Not a valid move...
			return false;
		}

		// Stop sliding in the GUI
		boardComponent.clearSlideSelection();

		// Advance to the next player's turn
		advanceToNextPlayer();

		// Tell the server
		// Note: This must be done *after* all change of state to ensure that
		//       any response from the server comes after those changes.  If
		//       this is the last move before scoring, and the chooseAllegience
		//       messages arrived before we went to AWAIT_SCORING phase, then
		//       we would end up in the wrong phase.
		CommWarwickSlidePiece theMsg = new CommWarwickSlidePiece (
		    conn.getUsername(), fromRegion, fromSpace, toRegion, toSpace);
		conn.send(theMsg);
		return true;
	}

	/*
	 * Advance to the next player whose turn it will be.
	 */
	private void advanceToNextPlayer () {
		// Advance to the next player
		int nextPlayerSeat = model.setNextPlayer();

		if (model.getPiecesToPlace(nextPlayerSeat) > 0) {
			// It is the next player's turn to move.
			model.setGamePhase(WarwickModel.PLACE_PIECE);
		} else {
			// We need to now score.  We cannot score right now, as we don't
			// know the other players' chosen allegiences, so we need to wait
			// to be told that by the server before continuing play.
			model.setGamePhase(WarwickModel.AWAIT_SCORING);
			model.resetAllegiences(getSeatNum());
		}

		// Tell the table who the next player is.
		conn.getTable().nextPlayer(nextPlayerSeat);
	}

	/**
	 * Do scoring for the current board position and then check for the end
	 * of the game.
	 */
	private void doScoring () {
		if (model.updateScores()) {
			// The game is over.
			model.setGamePhase(WarwickModel.GAME_OVER);
		} else {
			// The game continues, with the first player of this round being the
			// same as the round number.
			conn.getTable().nextPlayer(model.setNextPlayer(model.getCurrentRoundNumber()));
			model.setGamePhase(WarwickModel.CHOOSE_ALLEGIENCE);
		}
	}

	/**
	 * Handle receving messages from the server
	 *
	 * @param   message      The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(CommWarwickChooseAllegience.XML_NAME)) {
			CommWarwickChooseAllegience theMsg = new CommWarwickChooseAllegience(message);

			model.chooseAllegience(getSeatNum(theMsg.getUsername()),
			                       theMsg.getAllegience());

			if (model.allChosenAllegience()) {
				if (model.isAwaitScoring()) {
					doScoring();
				} else {
					model.clearBoard();
					model.setGamePhase(WarwickModel.PLACE_PIECE);
				}
			}
		} else if (messageType.equals(CommWarwickSlidePiece.XML_NAME)) {
			CommWarwickSlidePiece theMsg = new CommWarwickSlidePiece(message);
			int fromRegion = theMsg.getFromRegion();
			if (fromRegion < 0) {
				// This is a piece being played from off the board.
				if (model.addPiece(
				       getSeatNum(theMsg.getUsername()),
				       theMsg.getToRegion(), theMsg.getToSpace())) {
					// Valid move, so advance to sliding phase.
					model.setGamePhase(WarwickModel.SLIDE_PIECE);
				}
			} else {
				// This is a piece being slid on the board from one place to another.
				if (model.slidePiece(
				       fromRegion, theMsg.getFromSpace(),
				       theMsg.getToRegion(), theMsg.getToSpace())) {
					// Valid move, so advance to the next player's turn
					advanceToNextPlayer();
				}
			}
		}
	}
}
