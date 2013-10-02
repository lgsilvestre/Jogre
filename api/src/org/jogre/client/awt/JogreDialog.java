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

import java.awt.Frame;

import javax.swing.JDialog;

import org.jogre.common.util.GameProperties;

/**
 * Jogre dialog with extends a JDialog.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreDialog extends JDialog {

	/**
	 * A jogre dialog has a background of the game.
	 */
	public JogreDialog () {
		super ();
		
		setUpDialog ();
	}
		
	/**
	 * A jogre dialog has a background of the game.
	 * 
	 * @param owner  Parent dialog.  
	 */
	public JogreDialog (Frame owner) {
		super (owner);	
		
		setUpDialog ();
	}

	/**
	 * A jogre dialog with an owner, title and model.
	 * 
	 * @param owner   Owner panel
	 * @param title   Title
	 * @param modal   Modal or not.
	 */
	public JogreDialog (Frame owner, String title, boolean modal) {
		super (owner, title, modal);
		
		setUpDialog ();
	}
	
	/**
	 * Set up the dialog.
	 */
	protected void setUpDialog () {
		getContentPane().setBackground (GameProperties.getBackgroundColour());
	}
}
