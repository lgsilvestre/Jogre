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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

/**
 * A custom Jogre button which responds to mouse overs.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class JogreButton extends JButton implements MouseListener
{
	private static final int PADDING = 10;
	private static final Color LINE_COLOR = new Color (192, 192, 192);	
	
	// Set width of the button
	protected int width, height, textX, textY;
	protected String      text;
	
	private Color bgColor, buttonBGColor, textColor, lineColor, bgColorHover;
	
	// Declare 2 fields to record the status of button and text of button
	private boolean     mouseUp = true;
	private boolean     mouseOver = false;
	
	private FontMetrics fontMetrics = null;
	
	/**
	 * Constructor to this button.
	 *
	 * @param text    
	 * @param width
	 * @param height
	 */
	public JogreButton (String text, int width, int height) {		
		// set text, widths and heights
		this.text = text;
		this.width = width;
		this.height = height;
		
		if (width != -1 && height != -1) {
			setPreferredSize (new Dimension (this.width, this.height));
		}
		
		// Set up GUI elements
		bgColor = GameProperties.getBackgroundColour();		
		textColor = Color.black;
		
		// Create background hover 
		buttonBGColor = JogreUtils.getColorDelta (bgColor, 8);		
		bgColorHover  = JogreUtils.getColorDelta (bgColor, 16);
				
		// Add listeners.
		addMouseListener(this);
		
		// Repaint
		repaint ();
	}
	
	/**
	 * Constructor which takes a String.
	 * 
	 * @param text
	 */
	public JogreButton (String text) {
		this (text, -1, -1);
	}
	
	/**
	 * Paint the button.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		
		if (text != null) {

			// Check to see if width / height need to be created
			if (width == -1 || height == -1) {
				FontMetrics fontMetrics = g.getFontMetrics();
				Rectangle2D rect = fontMetrics.getStringBounds(text, g);

				// Set width / height and textX & text Y;
				this.width  = (int)rect.getWidth() + (PADDING * 2);
				this.height = 24;
				this.textX  = PADDING;
				this.textY  = 17;

				setPreferredSize (new Dimension (this.width, this.height));
				revalidate();
			}		

			if (mouseOver && isEnabled()) 
				g.setColor (bgColorHover);		// Hover background
			else if (isEnabled())
				g.setColor (buttonBGColor);		// Button background
			else
				g.setColor(bgColor);			// Default background

			g.fillRect (0, 0, width + 1, height + 1);
			if (isEnabled()) {
				g.fill3DRect(1, 1, width - 2, height - 2, mouseUp);
				g.fill3DRect(3, 3, width - 6, height - 6, mouseUp);
			}	

			g.setColor (Color.black);
			g.drawString (text, textX, textY);
		}
	}
	
	/**
	 * Return the text of the button.
	 *
	 * @return
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Override the setEnabled call to change the border from an
	 * etched to a plain line when enabled / disabled.
	 * 
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled (boolean enable) {
		super.setEnabled (enable);
		if (isEnabled())
			setBorder (BorderFactory.createEtchedBorder());
		else
			setBorder (BorderFactory.createLineBorder(LINE_COLOR));
	}
	
	/**
	 * Set the Text of the button.
	 *
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		
		this.width = -1;
		this.height = -1;
		repaint ();
	}
	
	public void mousePressed (MouseEvent e) {
		if (isEnabled()) {
			mouseUp = false;
			repaint ();
		}
	}
	
	public void mouseReleased (MouseEvent e) {
		mouseUp = true;
		repaint ();
	}
	
	public void mouseExited (MouseEvent e) {
		mouseOver = false;
		repaint ();
	}
	
	public void mouseEntered (MouseEvent e) {
		if (isEnabled()) {
			mouseOver = true;
			repaint ();
		}		
	}    
	public void mouseClicked (MouseEvent e) {}
}