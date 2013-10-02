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

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;

/**
 * Create a jogre title panel which is shared between the client and the table 
 * frame.
 * 
 * The title panel is 50 pixels tall and has a gradient.  It contains the default
 * JOGRE image to the left and a game specific image to the right.  It also contains
 * a label of the game.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class JogreTitlePanel extends JogrePanel {
	
	private static final double PAD = 5;
	private static final double FILL = TableLayout.FILL;
	private static final double TITLE_WIDTH = 260;
	private static final double IMAGE_SIZE = 50;
	
	private static final Color GRADIANT_COLOR_TOP = Color.white;
	private static final Color GRADIANT_COLOR_BOTTOM = new Color (230, 230, 230);
	private static final Font  LABEL_FONT  = new Font ("Arial", Font.BOLD, 24);
	
	private static JogreLabel gameLabel;
	
	/**
	 * Create instance of title panel
	 * @param GRADIANT_COLOR_BOTTOM 
	 * @param GRADIANT_COLOR_TOP 
	 * @param sizes 
	 */
	public JogreTitlePanel () {
		super (GRADIANT_COLOR_TOP, GRADIANT_COLOR_BOTTOM);
		setBorder (BorderFactory.createEtchedBorder());
				
		double [][] sizes = {{TITLE_WIDTH, FILL, PAD, IMAGE_SIZE, PAD}, {IMAGE_SIZE}};
		setLayout(new TableLayout (sizes));
		
		gameLabel = new JogreLabel (GameLabels.getGameLabel().toUpperCase());
		gameLabel.setFont(LABEL_FONT);
		gameLabel.setForeground(GameProperties.getTitleColour());		
		
		add (gameLabel, "1,0,r,b");
	}
	
	/**
	 * Paint items
	 * 
	 * @param g
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		g.drawImage(GameImages.getImage("jogre.title"), 0, 0, null); 
		g.drawImage(GameImages.getImage("game.title"), getWidth() - 55, 0, null);
	}

}