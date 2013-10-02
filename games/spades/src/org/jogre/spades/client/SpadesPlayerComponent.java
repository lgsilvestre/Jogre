/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
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
package org.jogre.spades.client;

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
 * Component for making a bid for a spades hand
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class SpadesPlayerComponent extends JogreComponent {

	// Spades model
	private SpadesModel model = null;

	// Spades controller
	private SpadesController controller = null;

	// Dimensions
	private final static int NAME_WIDTH = 150;
	private final static int NAME_HEIGHT = 25;
	private final static int SPACER = 5;
	private final static int BID_WIDTH = 75;
	private final static int BID_HEIGHT = 15;
	private final static int TRICK_WIDTH = 75;
	private final static int TRICK_HEIGHT = 15;

	// Player associated to this component
	private Player player = null;

	// boolean telling if it's this component's turn
	private boolean turn = false;

	/**
	 * Default constructor which takes a spades model
	 *
	 * @param spadesModel
	 *            Link to the main spades model
	 */
	public SpadesPlayerComponent(SpadesModel model) {
		this.model = model;
		this.controller = controller;

		int width = NAME_WIDTH;
		int height = NAME_HEIGHT + SPACER + BID_HEIGHT;

		setPreferredSize(new Dimension(width, height));
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

		// draw cards on table
		drawUsername(g);

		// draw bid
		drawBid(g);

		// draw tricks
		drawTricks(g);
	}

	/**
	 * Draw background
	 *
	 * @param g
	 *            Graphics
	 */
	public void drawBackground(Graphics g) {
		g.setColor(SpadesLookAndFeel.BG_COLOUR);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * Draws username with team in ()
	 *
	 * @param g
	 *            Graphics
	 */
	public void drawUsername(Graphics g) {
		String text = "Waiting ...";
		if (this.player != null) {
			text = this.player.getPlayerName();
			PlayerState playerState = this.player.getState();
			int seatNum = this.player.getSeatNum();
			if (seatNum == 0 || seatNum == 2)
				text += " (1)";
			else if (seatNum == 1 || seatNum == 3)
				text += " (2)";
			else
				text += " (" + playerState.stringValue() + ")";
		}

		Font letterFont = SpadesLookAndFeel.TEXT_FONT;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(letterFont);
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D bounds = letterFont.getStringBounds(text, frc);
		LineMetrics metrics = letterFont.getLineMetrics(text, frc);
		float width = (float) bounds.getWidth(); // The width of our text
		float lineheight = metrics.getHeight(); // Total line height
		float ascent = metrics.getAscent(); // Top of text to baseline
		float x0 = (float) ((NAME_WIDTH - width) / 2);
		float y0 = (float) ((NAME_HEIGHT - lineheight) / 2 + ascent);

		if (this.turn)
			g2d.setColor(SpadesLookAndFeel.ACTIVE_PLAYER_BG_COLOUR);
		else
			g2d.setColor(SpadesLookAndFeel.PLAYER_BG_COLOUR);
		g2d.fillRect(0, 0, NAME_WIDTH - 1, NAME_HEIGHT - 1);

		g2d.setColor(SpadesLookAndFeel.TEXT_COLOUR);
		g2d.drawRect(0, 0, NAME_WIDTH - 1, NAME_HEIGHT - 1);

		g2d.setColor(SpadesLookAndFeel.TEXT_SHADOW_COLOUR);
		g2d.drawString(text, x0 + 1, y0 + 1);

		g2d.setColor(SpadesLookAndFeel.TEXT_COLOUR);
		g2d.drawString(text, x0, y0);
	}

	/**
	 * Draw bid
	 *
	 * @param g
	 *            Graphics
	 */
	public void drawBid(Graphics g) {

		if (!this.model.seatIndexesSet())
			return;

		if (this.player == null)
			return;

		int bid = this.model.getBid(this.player.getSeatNum());

		String text = "";
		if (bid == SpadesBiddingComponent.BID_BLIND_NIL)
			text = "Blind Nil";
		else if (bid == SpadesBiddingComponent.BID_NIL)
			text = "Nil";
		else if (bid == SpadesBiddingComponent.BID_NO_BID)
			return;
		else
			text = String.valueOf(bid);

		text = "Bid: " + text;

		Font letterFont = SpadesLookAndFeel.TEXT_FONT;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(letterFont);
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D bounds = letterFont.getStringBounds(text, frc);
		LineMetrics metrics = letterFont.getLineMetrics(text, frc);
		float width = (float) bounds.getWidth(); // The width of our text
		float lineheight = metrics.getHeight(); // Total line height
		float ascent = metrics.getAscent(); // Top of text to baseline
		float x0 = (float) ((BID_WIDTH - width) / 2);
		float y0 = (float) ((BID_HEIGHT - lineheight) / 2 + ascent);

		g2d.setColor(SpadesLookAndFeel.TEXT_SHADOW_COLOUR);
		g2d.drawString(text, x0 + 1, y0 + NAME_HEIGHT + SPACER + 1);

		g2d.setColor(SpadesLookAndFeel.TEXT_COLOUR);
		g2d.drawString(text, x0, y0 + NAME_HEIGHT + SPACER);
	}

	/**
	 * Draw tricks
	 *
	 * @param g
	 *            Graphics
	 */
	public void drawTricks(Graphics g) {

		if (!this.model.seatIndexesSet())
			return;

		if (this.player == null)
			return;

		int tricks = this.model.getTricks(this.player.getSeatNum());

		if (tricks == 0)
			return;

		String text = "Tricks: " + String.valueOf(tricks);

		Font letterFont = SpadesLookAndFeel.TEXT_FONT;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(letterFont);
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D bounds = letterFont.getStringBounds(text, frc);
		LineMetrics metrics = letterFont.getLineMetrics(text, frc);
		float width = (float) bounds.getWidth(); // The width of our text
		float lineheight = metrics.getHeight(); // Total line height
		float ascent = metrics.getAscent(); // Top of text to baseline
		float x0 = (float) ((BID_WIDTH - width) / 2);
		float y0 = (float) ((BID_HEIGHT - lineheight) / 2 + ascent);

		g2d.setColor(SpadesLookAndFeel.TEXT_SHADOW_COLOUR);
		g2d.drawString(text, x0 + 1 + BID_WIDTH, y0 + NAME_HEIGHT + SPACER + 1);

		g2d.setColor(SpadesLookAndFeel.TEXT_COLOUR);
		g2d.drawString(text, x0 + BID_WIDTH, y0 + NAME_HEIGHT + SPACER);
	}

	/**
	 * Set player and redraw component
	 *
	 * @param player
	 *            Player
	 */
	public void setPlayer(Player player) {
		this.player = player;
		repaint();
	}

	/**
	 * Get player associated to this component
	 *
	 * @return player associated to this component
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Set turn for component
	 *
	 * @param turn Turn of play
	 */
	public void setTurn(boolean turn) {
		this.turn = turn;
		repaint();
	}

	public void setController (SpadesController controller) {
		this.controller = controller;
	}
}