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

import java.awt.Font;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.jogre.common.User;
import org.jogre.common.util.JogreLabels;

/**
 * Panel which displays user information such as games played,
 * games won, lost, drawn and streak.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JUserInfoDialog extends JogreDialog {

	private User user;
	
	/**
	 * Constructor for applets.
	 */
	public JUserInfoDialog (User user) {
		super ();
		
		// Set user
		this.user = user;
		
		// Set up GUI
		setUpGUI ();		
	}
	
	/**
	 * Constructor for applications.
	 * 
	 * @param owner
	 */
	public JUserInfoDialog (Frame owner, User user) {
		super ();
		
		// Set user
		this.user = user;
		
		setUpGUI ();
	}
	
	/**
	 * Set up the GUI.
	 */
	private void setUpGUI () {		
        // Set labels
        JogreLabels labels = JogreLabels.getInstance();
        
		// Set layout and create a panel.
		double s1 = 20, s2 = 5, pref = TableLayout.PREFERRED;
		double [][] sizes = { 
			{s1, 0.5, s2, 0.5, s1},		// columns
			{s1, pref, s1, pref, s2, pref, s2, pref, s2, pref, s2, pref, s2, pref, s2, pref, s1}};
		JogrePanel panel = new JogrePanel (new TableLayout (sizes));
		
		// Create labels
		JLabel title = new JLabel (labels.get("user.information") + " (" + user.getUsername() + ")");
		JLabel ratingsL     = new JLabel (labels.get("rating") + ":");
		JLabel ratingsR     = new JLabel (String.valueOf (user.getRating()));
		JLabel gamesPlayedL = new JLabel (labels.get("games.played") + ":");
		JLabel gamesPlayedR = new JLabel (String.valueOf (user.getGamesPlayed()));
		JLabel gamesWonL    = new JLabel (labels.get("wins") + ":");
		JLabel gamesWonR    = new JLabel (String.valueOf (user.getWins()));
		JLabel gamesLosesL  = new JLabel (labels.get("losses") + ":");
		JLabel gamesLosesR  = new JLabel (String.valueOf (user.getLosses()));
		JLabel gamesDrawsL  = new JLabel (labels.get("Draws") + ":");
		JLabel gamesDrawsR  = new JLabel (String.valueOf (user.getDraws()));
		JLabel gamesStreakL = new JLabel (labels.get("streak") + ":");
		JLabel gamesStreakR = new JLabel (String.valueOf (user.getStreak()));
		ImageIcon userImage = new ImageIcon(""); //Aquí debería ir un getter de la imagen del objeto user
		JLabel ImageLabel = new JLabel("",userImage,JLabel.LEFT);
		
		JButton addFriendButton = new JButton ("hadsasadsasdadsola");
		addFriendButton.setText("Add Friend");
		
		// Set fonts
		Font pf = JogreAwt.LIST_FONT;
		title.setFont(JogreAwt.LIST_FONT_BOLD);
		ratingsL.setFont (pf);		ratingsR.setFont (pf);
		gamesPlayedL.setFont (pf);  gamesPlayedR.setFont (pf);
		gamesWonL.setFont (pf);		gamesWonR.setFont (pf);
		gamesLosesL.setFont (pf);	gamesLosesR.setFont (pf);  
		gamesDrawsL.setFont (pf);	gamesDrawsR.setFont (pf);
		gamesStreakL.setFont (pf);	gamesStreakR.setFont (pf);
		
		// Add labels to panel
		panel.add (addFriendButton, "1,2,3,1,c,c");
		panel.add (title, "1,1,3,1,c,c");
		panel.add (ratingsL,    " 1,5,r,c");  panel.add (ratingsR,     "3,5,l,c");
		panel.add (gamesPlayedL, "1,7,r,c");  panel.add (gamesPlayedR, "3,7,l,c");
		panel.add (gamesWonL,    "1,9,r,c");  panel.add (gamesWonR,    "3,9,l,c");
		panel.add (gamesLosesL,  "1,11,r,c");  panel.add (gamesLosesR,  "3,11,l,c");
		panel.add (gamesDrawsL,  "1,13,r,c"); panel.add (gamesDrawsR,  "3,13,l,c");
		panel.add (gamesStreakL, "1,15,r,c"); panel.add (gamesStreakR, "3,15,l,c");
		panel.add (ImageLabel, "1,3,3,1,c,c");
		
		// Set this panel to be visible
		getContentPane().add(panel);
		pack ();
		setLocationRelativeTo(this);
		setVisible (true);		
	}
}
