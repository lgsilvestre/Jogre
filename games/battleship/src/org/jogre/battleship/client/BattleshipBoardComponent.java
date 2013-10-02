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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import org.jogre.client.awt.AbstractBoardComponent;
import org.jogre.client.awt.GameImages;
import org.jogre.common.Player;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

/**
 * Battleships visual board component.
 * 
 * @author Gman, JavaRed
 * @version Alpha 0.2.3
 */

public class BattleshipBoardComponent extends AbstractBoardComponent {

	// Declare constants which define what the board looks like
	public static final int CELL_SIZE = 15;
	public static final int CELL_SPACING = 1;
	private BattleshipModel model = null;

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public BattleshipBoardComponent(BattleshipModel model) {
		// Call constructor in AbstractBoardComponent
		super(BattleshipModel.BOARD_SIZE, (BattleshipModel.BOARD_SIZE * 2) + 1, CELL_SIZE,
				CELL_SPACING, 0, 0, true, true, false);

		// Set model
		this.model = model;

		// Set colors
		Color boardColor1 = JogreUtils.getColour (GameProperties.getInstance().get("board.color.1"));
		Color boardColor2 = JogreUtils.getColour (GameProperties.getInstance().get("board.color.2"));
		
		this.setColours(boardColor1, boardColor2, Color.black, Color.white);
	}

	/**
	 * Draw board component
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		
		// Draw the board (AbstractBoardComponent)
		super.paintComponent(g);
		
		// Draw spacer between two boards with default bd color
		drawBoardSpacer(g);
		
		// Draw placed ships
		drawPlacedShips(g);
		
		// Draw hits and misses for both players
		drawHitsAndMisses(g);
		
		// Draw mouse cursor
		drawMousePlacingCursor(g);
		
		// Draw mouse cursor
		drawMouseFiringCursor(g);
	}
	
	/**
	 * Override the background images.
	 * 
	 * @see org.jogre.client.awt.AbstractBoardComponent#drawBoardBackground(java.awt.Graphics)
	 */
	protected void drawBoardBackground (Graphics g) {	
		for (int x = 0; x < this.numOfCols; x++) {
			for (int y = 0; y < this.numOfRows; y++) {
				// retrieve screen co-ordinates
				int screenX = x * (this.cellSize + this.cellSpacing) +
							  this.cellSpacing + this.borderWidth;
				int screenY = y * (this.cellSize + this.cellSpacing) +
				              this.cellSpacing + this.borderWidth;
				Image bgImg = null;
				if ((x+y) % 2 == 0)
					bgImg = GameImages.getImage(8);
				else
					bgImg = GameImages.getImage(9);

				g.drawImage (bgImg, screenX, screenY, null);
			}
		}
	}
	
	/**
	 * Draw middle spacer between boards
	 * 
	 * @param g
	 */
	public void drawBoardSpacer(Graphics g) {
		int x = 1 + (BattleshipModel.BOARD_SIZE * (CELL_SIZE + CELL_SPACING));
		int y = 0;
		
		g.setColor(GameProperties.getBackgroundColour());
		g.fillRect(x, y, CELL_SIZE, 1 + (BattleshipModel.BOARD_SIZE * (CELL_SIZE + CELL_SPACING)));
	}
	
	/**
	 * Draw placed ships
	 * 
	 * @param g
	 */
	public void drawPlacedShips(Graphics g) {
		int seatNum = this.model.getSeatNum();
		if (seatNum == Player.NOT_SEATED)
			return;
		int seatNumOpponent = 0;
		if (seatNumOpponent == seatNum)
			seatNumOpponent = 1;
		
		for (int i = 0; i < BattleshipModel.SHIP_NAMES.length; i++) {
			int ship = BattleshipModel.SHIP_NAMES[i];
			Point p = this.model.isShipPlaced(ship);
			if (p != null)			
				this.drawShip(g, ship, p.x, p.y, this.model.isHorizontal(ship, p));
		}
	}
	
	/**
	 * Draw ship
	 * 
	 * @param g
	 * @param ship
	 * @param x
	 * @param y
	 * @param horizontal
	 */
	private void drawShip(Graphics g, int ship, int x, int y, boolean horizontal) {
		int xx = x * (CELL_SIZE + CELL_SPACING) + 1;
		int yy = y * (CELL_SIZE + CELL_SPACING) + 1;
		
		if (horizontal)
			g.drawImage(GameImages.getImage(ship), xx, yy, null);
		else {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.rotate(Math.toRadians(90), xx, yy);
			g2d.translate(xx, yy - CELL_SIZE);
			g2d.drawImage(GameImages.getImage(ship), 0, 0, null);
		}
	}
	
	/**
	 * Draw hits and misses for both players playing
	 * 
	 * @param g
	 */
	public void drawHitsAndMisses(Graphics g) {
		int seatNum = this.model.getSeatNum();
		if (seatNum == Player.NOT_SEATED)
			seatNum = 0;			// someone viewing 

		int seatNumOpponent = JogreUtils.invert(seatNum);
		int[][][] hitsAndMisses = this.model.getHitsAndMisses();
		
		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
				if (hitsAndMisses[seatNumOpponent][x][y] == BattleshipModel.HIT) {
					int xx = x * (CELL_SIZE + CELL_SPACING) + 1;
					int yy = y * (CELL_SIZE + CELL_SPACING) + 1;
					g.drawImage(GameImages.getImage(BattleshipImages.BATTLESHIP_HIT), xx, yy, null);
				} else if (hitsAndMisses[seatNumOpponent][x][y] == BattleshipModel.MISS) {
					int xx = x * (CELL_SIZE + CELL_SPACING) + 1;
					int yy = y * (CELL_SIZE + CELL_SPACING) + 1;
					g.drawImage(GameImages.getImage(BattleshipImages.BATTLESHIP_MISS), xx, yy, null);
				}
			}
		}
		
		for (int x = 0; x < BattleshipModel.BOARD_SIZE; x++) {
			for (int y = 0; y < BattleshipModel.BOARD_SIZE; y++) {
				if (hitsAndMisses[seatNum][x][y] == BattleshipModel.HIT) {
					int xx = (x + BattleshipModel.BOARD_SIZE + 1) * (CELL_SIZE + CELL_SPACING) + 1;
					int yy = y * (CELL_SIZE + CELL_SPACING) + 1;
					g.drawImage(GameImages.getImage(BattleshipImages.BATTLESHIP_HIT), xx, yy, null);
				} else if (hitsAndMisses[seatNum][x][y] == BattleshipModel.MISS) {
					int xx = (x + BattleshipModel.BOARD_SIZE + 1) * (CELL_SIZE + CELL_SPACING) + 1;
					int yy = y * (CELL_SIZE + CELL_SPACING) + 1;
					g.drawImage(GameImages.getImage(BattleshipImages.BATTLESHIP_MISS), xx, yy, null);
				}
			}
		}
	}
	
	/**
	 * Draw mouse placing cursor
	 * 
	 * @param g
	 */
	public void drawMousePlacingCursor(Graphics g) {
		int seatNum = model.getSeatNum();
		if (seatNum == Player.NOT_SEATED)
			return;
		
		if (this.model.stillPlacingShips(seatNum)) {
			Point p = this.model.getBoardPlacingPoint();
			if (p != null)
				drawShip(g, model.getPlacingShipName(seatNum), p.x, p.y, model.isShipPlacedHorizontally(seatNum));
		}
	}
	
	/**
	 * Draw mouse firing cursor
	 * 
	 * @param g
	 */
	public void drawMouseFiringCursor(Graphics g) {
		Point p = this.model.getBoardFiringPoint();
		if (p != null) {
			g.setColor(Color.red);
			int xx = p.x * (CELL_SIZE + CELL_SPACING) + 1;
			int yy = p.y * (CELL_SIZE + CELL_SPACING) + 1;
			g.fillRect(xx, yy, CELL_SIZE, CELL_SIZE);
		}
	}
}