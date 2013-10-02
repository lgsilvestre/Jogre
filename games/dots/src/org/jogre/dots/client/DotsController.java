/*
 * JOGRE (Java Online Gaming Real-time Engine) - Dots
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
package org.jogre.dots.client;

import java.awt.Point;
import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.util.JogreLogger;
import org.jogre.dots.common.CommDotsMove;
import org.jogre.dots.common.CommDotsSnapShot;

/**
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 *
 * Controller for the dots game.
 */
public class DotsController extends JogreController {

	private JogreLogger logger = new JogreLogger(this.getClass());

	// links to game data and the board component
	protected DotsModel model;
	protected DotsBoardComponent board;

	/**
	 * Default constructor
	 *
	 * @param gameModel
	 * @param boardComponent
	 */
	public DotsController(DotsModel model, DotsBoardComponent board)
	{
		// Call super class
		super (model, (JogreComponent) board);

		// Set fields
		this.model = model;
		this.board = board;
	}

	/**
	 * Start game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.reset ();
	}

	/**
	 * Implementation of the mouse moved interface.
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			// get mouse co-ordinates
			int mouseX = e.getX();
			int mouseY = e.getY();

			int col = 0;
			int row = 1;
			int location = 2;
			int[] info = this.model.getCellAndLocation(new Point(mouseX, mouseY));
			this.model.setMouseLocation(info[col], info[row], info[location]);
		} else {
			this.model.setMouseLocation(-1, -1, -1);
		}
	}

	/**
	 * Implementation of the mouse pressed interface.
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			int col = 0;
			int row = 1;
			int location = 2;
			int[] info = this.model.getCellAndLocation(new Point(mouseX, mouseY));
			this.model.setPressedLine(info[col], info[row], info[location]);
		}
	}

	/**
	 * Implementation of the mouse released interface.
	 *
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (isGamePlaying() && isThisPlayersTurn()) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			int col = 0;
			int row = 1;
			int location = 2;
			int[] info = this.model.getCellAndLocation(new Point(mouseX, mouseY));
			if (this.model.setClickedLine(info[col], info[row], info[location]))
				move(info[col], info[row], info[location]);
		}
	}

	public void move(int col, int row, int location) {
		if (this.model.setLocation(col, row, location, getSeatNum())) {
			CommDotsMove commMove = new CommDotsMove (this.conn.getUsername(), col, row, location);
			sendObject (commMove);

			if (!this.model.causedFill(col, row, location)) {
				this.nextPlayer();
			} else {
				if (this.model.checkGameOver()) {
					int currentSeat = this.getSeatNum();
					int otherSeat = 0;
					if (currentSeat == otherSeat)
						otherSeat = 1;
					int score = this.model.cellsOwned(currentSeat);
					int otherScore = this.model.cellsOwned(otherSeat);
					int outcome = IGameOver.DRAW;
					int points = 0;
					if (score > otherScore) {
						outcome = IGameOver.WIN;
						points = 20;
					} else if (score < otherScore) {
						outcome = IGameOver.LOSE;
						points = -20;
					}
					CommGameOver commGameOver = new CommGameOver (outcome);
					conn.send(commGameOver);
				}
			}
		}
	}

	/**
	 * Receive data objects from other clients (dots move).
	 *
	 * @see org.jogre.client.JogreController#receiveObject(nanoxml.XMLElement)
	 */
	public void receiveObject (XMLElement object) {
		logger.debug("receiveObject", "object: " + object);
		if (object.getName().equals(CommDotsMove.XML_NAME)) {
			CommDotsMove commMove = new CommDotsMove(object);
			String username = commMove.getUsername();
			int seatNum = this.getSeatNum(username);
			this.model.setLocation(commMove.getColumn(), commMove.getRow(), commMove.getLocation(), seatNum);
		}
		else if (object.getName().equals(CommDotsSnapShot.XML_NAME)) {
			if (this.isGamePlaying()) {
				CommDotsSnapShot snapShot = new CommDotsSnapShot(this.model.getCols(), this.model.getRows(), object);
				this.model.setLastMove(snapShot.getLastMoveColumn(), snapShot.getLastMoveRow(), snapShot.getLastMoveLocation());
				this.model.setData(snapShot.getData());
			}
		}
	}
}
