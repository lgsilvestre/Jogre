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

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 * Custom scroll pane which currently only supports vertical scrollbars.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreScrollPane extends JScrollPane {

	/**
	 * Constructor which takes a component.
	 * 
	 * @param view   Component view.
	 */
	public JogreScrollPane (Component view) {
		super (view);
		
		setVerticalScrollBar (new JogreScrollBar (view));
		setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
	}
}