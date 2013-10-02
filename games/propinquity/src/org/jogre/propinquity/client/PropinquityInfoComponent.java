/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import org.jogre.client.awt.JogreComponent;

/**
 * Information component which suppliments the main propinquity grid.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class PropinquityInfoComponent extends JogreComponent {

	// Declare width and height of the component
	private final int HEIGHT = 220;
	private final int WIDTH  = 80;

	private final int ATTACK_CELL_SIZE = 58;

	private final int TERRITORIES_CELL_SIZE = 42;

	// Link to game data
	private PropinquityModel propinquityModel;
	private PropinquityController propinquityController;

	// Declare font and font metrics for this font
	protected final Font numbersFont = new Font ("SansSerif", Font.BOLD, 24);
	protected final Font scoreFont = new Font ("SansSerif", Font.BOLD, 12);

	/**
	 * Constructor.
	 */
	public PropinquityInfoComponent (
		PropinquityModel propinquityModel,
		PropinquityController propinquityController
	) {
		this.propinquityModel = propinquityModel;
		this.propinquityController = propinquityController;
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		repaint ();
	}

	/**
	 * Method for updating the GUI.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the background
		drawBackGround(g);

		// Draw the attack number
		drawAttackNumber(g);

		// Draw the current scores of the two players
		drawScores (g);
	}

	/**
	 * @param g
	 */
	private void drawBackGround(Graphics g) {		
		g.setColor(Color.white);
		g.fillRect (0, ((HEIGHT / 2) - (ATTACK_CELL_SIZE / 2)) - 9, getWidth() - 1, ATTACK_CELL_SIZE + 18);
		
		g.setColor(Color.black);
		g.drawRect (0, ((HEIGHT / 2) - (ATTACK_CELL_SIZE / 2)) - 9, getWidth() - 1, ATTACK_CELL_SIZE + 18);
	}

	/**
	 * @param g
	 */
	private void drawAttackNumber(Graphics g) {
		g.setFont (numbersFont);

		// Set correct background colour and draw the hexagon with the attack
		// number.
		int seatNum = propinquityController.getCurrentPlayerSeatNum();
		Color bgColor = seatNum == Cell.CELL_PLAYER_1 ?
			            PropinquityLookAndFeel.PLAYER1_BG_COLOUR :
			            PropinquityLookAndFeel.PLAYER2_BG_COLOUR;

		int x = (getWidth() / 2) - (ATTACK_CELL_SIZE / 2);
		int y = (getHeight() / 2) - (ATTACK_CELL_SIZE / 2);

		int attackNum = propinquityModel.getAttackNum();
		if (attackNum != 0) {
			PropinquityGraphics.drawHexagonWithText (
				g, x, y, ATTACK_CELL_SIZE, true,
				bgColor, attackNum);
		}
	}

	/**
	 * Draw the scores of each player.
	 *
	 * @param g
	 */
	private void drawScores (Graphics g) {
		g.setFont (scoreFont);

		int x = (getWidth() / 2) - (ATTACK_CELL_SIZE / 2);

		// Red territories
		PropinquityGraphics.drawHexagonWithText (
			g, (WIDTH / 2) - (TERRITORIES_CELL_SIZE / 2), 0,
			TERRITORIES_CELL_SIZE, true,
			PropinquityLookAndFeel.PLAYER1_BG_COLOUR,
			propinquityModel.getTerritories (PropinquityModel.PLAYER_ONE));

		// Blue territories
		PropinquityGraphics.drawHexagonWithText (
				g, (WIDTH / 2) - (TERRITORIES_CELL_SIZE / 2), HEIGHT - TERRITORIES_CELL_SIZE,
			TERRITORIES_CELL_SIZE, true,
			PropinquityLookAndFeel.PLAYER2_BG_COLOUR,
			propinquityModel.getTerritories (PropinquityModel.PLAYER_TWO));
	}
	
	
}
