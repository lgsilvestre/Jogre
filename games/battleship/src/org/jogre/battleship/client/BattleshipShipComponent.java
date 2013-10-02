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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.Player;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.JogreUtils;

/**
 * Battleships visual ship component.  This component shows the
 * ships that are hit and are still left of each player.
 * 
 * @author Gman
 * @version Alpha 0.2.3
 */

public class BattleshipShipComponent extends JogreComponent {

	private static final long serialVersionUID = 1L;
	
	// Declare constants which define what the board looks like
	private static final int WIDTH = 125;
	private static final int HEIGHT = 161;
	private static final int SHIP_NAME_ROW_HEIGHT = 14;
	private static final int SHIP_ROW_HEIGHT = 18;
	private static final int LEFT_PADDING = 10;
	private static final int LEFT_PADDING_STATUS = 90;

	// Model
	private BattleshipModel model = null;
	private boolean myShips = false;

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public BattleshipShipComponent(BattleshipModel model, boolean myShips) {

		// Set model
		this.model = model;
		this.myShips = myShips;
		
		// Set size
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		// Repaint the component
		repaint();
	}

	/**
	 * Draw component
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		
		// Since this is a simple component, everything is within this method
		g.setColor(Color.gray);
		g.fillRect(0, 0, WIDTH - 1, HEIGHT - 1);
		g.setColor(Color.black);
		g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
		
		// Draw ships
		int seatNum = this.model.getSeatNum();
		if (seatNum != Player.NOT_SEATED) {
			
			if (!this.myShips)
				seatNum = JogreUtils.invert(seatNum);
		
			for (int i = 0; i < BattleshipModel.SHIP_NAMES.length; i++) {
				int y = i * (SHIP_NAME_ROW_HEIGHT + SHIP_ROW_HEIGHT);
				
				// Draw ship name
				g.setColor(Color.white);
				String shipName = GameLabels.getInstance().get("boat." + i);
				drawLeftAlignedText(g, shipName, LEFT_PADDING, y, WIDTH - LEFT_PADDING - 1, SHIP_NAME_ROW_HEIGHT, new Font("SansSerif", Font.PLAIN, 10));

				// Draw ship status
				int hitsOnShip = this.model.hitsOnShip(seatNum, BattleshipModel.SHIP_NAMES[i]);
				int shipSize = BattleshipModel.SHIP_SIZES[i];
				String shipStatus = hitsOnShip + "/" + shipSize;
				if (shipSize == hitsOnShip) {
					shipStatus = "Sunk";
				}
				drawLeftAlignedText(g, shipStatus, LEFT_PADDING_STATUS, y, WIDTH - LEFT_PADDING - 1, SHIP_NAME_ROW_HEIGHT, new Font("SansSerif", Font.PLAIN, 10));
				
				// Draw ship
				g.drawImage(GameImages.getImage(BattleshipModel.SHIP_NAMES[i]), LEFT_PADDING, y + SHIP_NAME_ROW_HEIGHT, null);
                
                // Draw explositions on ships
                for (int j = 0; j < hitsOnShip; j++) {
                	if (myShips)
                		g.drawImage(GameImages.getImage(BattleshipImages.BATTLESHIP_HIT), LEFT_PADDING + (j * 16), y + SHIP_NAME_ROW_HEIGHT, null);
                }
			}
		}
	}
	
	/**
	 * Draw left aligned text.
	 * 
	 * @param g
	 * @param text
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param font
	 */
	private void drawLeftAlignedText(Graphics g, String text, int x, int y, int w, int h, Font font) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(font);
		FontRenderContext frc = g2d.getFontRenderContext();
		LineMetrics metrics = font.getLineMetrics(text, frc);
		float lineheight = metrics.getHeight();
		float ascent = metrics.getAscent();
		float y0 = (float) ((h - lineheight) / 2 + ascent);
		g2d.drawString(text, x, y0 + y);		
	}
}