/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

/**
 * Custom JOGRE Scroll bar which overrides the standard
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreScrollBar extends JScrollBar {

	/**
	 * Constructor which takes a component.
	 * 
	 * @param view  View that we are scrolling about.
	 */
	public JogreScrollBar (Component view) {
		setUI(new JogreScrollBarUI ());
	}
	
	/**
	 * Override the default JOGRE scrollbar UI delegate.
	 */
	private class JogreScrollBarUI extends BasicScrollBarUI {
		
		private final Color BG_COLOR    = GameProperties.getBackgroundColour();
		private final Color ARROW_COLOR = new Color (40, 40, 40);
		private final Color THUMB_COLOR = JogreUtils.getColorDelta (BG_COLOR, 8);
		
		public JogreScrollBarUI () {}
		
		public void configureScrollBarColors() {
			super.configureScrollBarColors();

			thumbColor            = BG_COLOR;
			trackColor            = BG_COLOR;
			trackHighlightColor   = BG_COLOR;
		}
		
		protected void paintDecreaseHighlight(Graphics g) {}
		
		protected void paintIncreaseHighlight(Graphics g) {}
		
	    protected void paintThumb (Graphics g, JComponent c, Rectangle thumbBounds)  
	    {
	    	if(thumbBounds.isEmpty() || !scrollbar.isEnabled())
	    		return;
	    	
	    	int w = thumbBounds.width;
	    	int h = thumbBounds.height;		
	    	
	    	g.translate (thumbBounds.x, thumbBounds.y);
	    	
	    	g.setColor (THUMB_COLOR);
	    	g.fillRect(0, 0, w + 1, h + 1);
			g.fill3DRect(1, 1, w - 2, h - 2, true);
	    	g.translate (-thumbBounds.x, -thumbBounds.y);
	    }
		
	    // TODO - this is only programmed for vertical scroll bars.
		protected JButton createDecreaseButton (int orientation)
		{
			return new JogreButton ("", 16, 16) {
				public void paintComponent (Graphics g) {
					super.paintComponent (g);
					g.setColor(ARROW_COLOR);
					for (int i = 0; i < 3; i++)
						g.drawLine(7 - i, 6 + i, 7 + i, 6 + i);
				}
			};
		}
		
		// TODO - this is only programmed for vertical scroll bars.
		protected JButton createIncreaseButton (int orientation)
		{
			return new JogreButton ("", 16, 16) {
				public void paintComponent (Graphics g) {
					super.paintComponent (g);
					g.setColor(ARROW_COLOR);
					for (int i = 0; i < 3; i++)
						g.drawLine(7 - i, 9 - i, 7 + i, 9 - i);
				}
			};
		}
	}
}