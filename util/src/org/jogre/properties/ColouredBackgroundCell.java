/*
 * JOGRE (Java Online Gaming Real-time Engine) - Properties
 * Copyright (C) 2004 - 2007   Bob Marks (marksie531@yahoo.com)
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
package org.jogre.properties;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * Declare coloured background cell.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
class ColouredBackgroundCell extends JLabel {
	
	private Border FOCUS_BORDER = BorderFactory.createLineBorder(Color.red);
	private Color  SELECTED_BG_COLOUR = new Color (200, 200, 255, 100);
	private Color [] bgColours;
	
	private boolean isSelected, hasFocus;
	
	/**
	 * Constructor which takes text and background colours.
	 * 
	 * @param text
	 * @param bgColours
	 */
	public ColouredBackgroundCell (String text, Color [] bgColours, Font font, boolean isSelected, boolean hasFocus) {
		super (text);
		setFont(font);
		this.bgColours = bgColours;
		
		this.isSelected = isSelected;
		this.hasFocus = hasFocus;	
		
		if (hasFocus)
			setBorder(FOCUS_BORDER);
	}
	
	/**
	 * Paint component.
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {		
		
		if (bgColours != null && bgColours.length > 0) {
			int width = getWidth() / bgColours.length;
			int height = getHeight();
			
			for (int i = 0; i < bgColours.length; i++) {
				Color color = bgColours[i];				
				if (color != null) {						
					g.setColor(color);							
					g.fillRect (i * width, 0, width, height);
				}
			}
		}
		if (isSelected) {
			g.setColor(SELECTED_BG_COLOUR);
			g.fillRect (0, 0, getWidth(), getHeight());
		}
		super.paintComponent(g);
	} 
}