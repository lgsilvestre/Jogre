/*
* JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.client.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Jogre glass pane - useful for drawing status strings on top of a game.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class JogreGlassPane extends JComponent {

	// Constants
	private static final int CORNER = 40;	
	private static final int PADDING = 10;
	private static final Font DEFAULT_FONT = new Font ("Arial", Font.BOLD, 18);
	
	private static final Color DEFAULT_FONT_COLOUR = new Color (64, 128, 64);
	private static final Color BACKGROUND_COLOUR = new Color (255, 255, 255, 180);
	private static final Color BACKGROUND_STROKE_COLOUR = new Color (100, 100, 100);
	
	// Fields
	private Font font = null;
	private Color color = null;
	private String text = null;
	private FontMetrics fontMetrics = null;
	
	/**
	 * Constructor. 
	 */
	public JogreGlassPane () {
		setOpaque(false);
	}
	
	/**
	 * Display using the default font / colour.
	 * 
	 * @param text
	 */
	public void display (String text) {
		display (DEFAULT_FONT, DEFAULT_FONT_COLOUR, text);
	}
	
	/**
	 * Display using supplied font, colour and text.
	 * 
	 * @param font   Selected font.
	 * @param color  Selected colour.
	 * @param text   Text
	 */
	public void display (Font font, Color color, String text) {
		this.font = font;
		this.color = color;
		this.text = text;
		this.fontMetrics = getFontMetrics(font);
		
		// Set visible
		setPaneVisible (true);
	}
	
	/**
	 * Set visible or not.
	 * 
	 * @param visible
	 */
	public void setPaneVisible (boolean visible) {
		getRootPane().getGlassPane().setVisible(visible);
		repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent (g);
		
		Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
		
        // Set font
        g2.setFont(font);
        
        // Draw background
        Rectangle2D rect = fontMetrics.getStringBounds(text, g2);
        int x = (int)(((getWidth() - rect.getWidth()) / 2) - PADDING);
        int y = (int)(((getHeight() - rect.getHeight()) / 2) - PADDING);
        int w = (int)(rect.getWidth() + (PADDING * 2));
        int h = (int)(rect.getHeight() + (PADDING * 2));        
		g2.setColor(BACKGROUND_COLOUR);
		g2.fillRoundRect(x, y, w, h, CORNER, CORNER);
		g2.setColor(BACKGROUND_STROKE_COLOUR);
		g2.drawRoundRect(x, y, w, h, CORNER, CORNER);
		
		// Draw text
		g2.setColor (color);
		int tx = (int)(((getWidth() - rect.getWidth()) / 2));
		int ty = (int)(((getHeight() - rect.getHeight()) / 2)) + fontMetrics.getAscent();
		g2.drawString(text, tx, ty);
	}
}