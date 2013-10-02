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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jogre.common.User;
import org.jogre.common.util.JogreLabels;

/**
 * Ratings panel which contains.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class RatingsPanel extends JPanel {
	
	/**
	 * Constructor.
	 */
	public RatingsPanel () {
		double pref = TableLayout.PREFERRED;
		double [][] sizes = {{pref, 0.5, pref, 0.5}, 
				{pref, pref, pref}};
		setLayout(new TableLayout (sizes));
		setOpaque(true);
		setBackground (Color.white);
		setBorder (BorderFactory.createLoweredBevelBorder());	
		
		// Declare labels
		JLabel l1 = new JLabel ("2100+");       
		JLabel l2 = new JLabel ("1800-2099");   
		JLabel l3 = new JLabel ("1500-1799");   
		JLabel l4 = new JLabel ("1200-1499");   
		JLabel l5 = new JLabel ("0-1199");      
		JLabel l6 = new JLabel (JogreLabels.getInstance().get("provisional")); 
		
		Font f = l1.getFont();
		f = new Font (f.getFontName(), Font.PLAIN, 10);
		l1.setFont(f); l2.setFont(f); l3.setFont(f);
		l4.setFont(f); l5.setFont(f); l6.setFont(f);
		
		// Add items to panel
		add (new RatingSquare (new User ("", 2200, 20, 0, 0, 0)), "0,0,c,c");
		add (new RatingSquare (new User ("", 2000, 20, 0, 0, 0)), "2,0,c,c");
		add (new RatingSquare (new User ("", 1700, 20, 0, 0, 0)), "0,1,c,c");
		add (new RatingSquare (new User ("", 1400, 20, 0, 0, 0)), "2,1,c,c");
		add (new RatingSquare (new User ("", 1100, 20, 0, 0, 0)), "0,2,c,c");
		add (new RatingSquare (new User ("", 1000, 0, 0, 0, 0)), "2,2,c,c");
		add (l1, "1,0,"); add (l2, "3,0");
		add (l3, "1,1"); add (l4, "3,1");
		add (l5, "1,2"); add (l6, "3,2");
	}
}
