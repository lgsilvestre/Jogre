/*
 * JOGRE (Java Online Gaming Real-time Engine) - Dots
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can istribute it and/or
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreComponent;
import org.jogre.common.Player;
import org.jogre.common.playerstate.PlayerState;
import org.jogre.common.util.JogreLogger;

/**
 * Component for showing score based on color and seat number
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class DotsScoreComponent extends JogreComponent {

	// Spades model
	private DotsModel model = null;
	private int seat = -1;

	/**
	 * Default constructor
	 *
	 * @param model
	 * @param width
	 * @param height
	 */
	public DotsScoreComponent(DotsModel model, int width, int height) {
		this.model = model;

		setPreferredSize(new Dimension(width*model.getNumOfPlayers(), height));
		repaint();
	}

	/**
	 * Refresh the component.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		// draw background
		drawBackground(g);

		// draw scores
		drawScores(g);
	}

	/**
	 * Draw background
	 *
	 * @param g Graphics
	 */
	public void drawBackground(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * Draw scores
	 *
	 * @param g Graphics
	 */
	public void drawScores(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Font letterFont = new Font("SansSerif", Font.BOLD, 12);
		g2d.setFont(letterFont);
		FontRenderContext frc = g2d.getFontRenderContext();

		int w = (getWidth() / model.getNumOfPlayers());
		int h = getHeight();

		for (int i = 0; i < this.model.getNumOfPlayers(); i++) {

			int x = (i * w) + 1;
			int y = 1;

			g2d.setColor(this.model.seatColor[i]);
			g2d.fillRect(x, y, w-2, h-2);

			int score = this.model.cellsOwned(i);
			String scoreText = String.valueOf(score);
			Rectangle2D bounds = letterFont.getStringBounds(scoreText, frc);
			LineMetrics metrics = letterFont.getLineMetrics(scoreText, frc);
			float width = (float) bounds.getWidth();
			float lineheight = metrics.getHeight();
			float ascent = metrics.getAscent();
			float x0 = (float) (((w - width) / 2) + x);
			float y0 = (float) (((h - lineheight) / 2 + ascent) + y);

			g2d.setColor(Color.white);
			g2d.drawString(scoreText, x0, y0);

			if (seat == i) {
				g2d.setColor(Color.white);
				g2d.drawRect(x, y, w-3, h-3);
				g2d.drawRect(x + 1, y + 1, w-5, h-5);
				g2d.drawRect(x + 2, y + 2, w-7, h-7);
			}
		}
	}

	public void setSeat(int seat) {
		this.seat = seat;
		repaint();
	}
}