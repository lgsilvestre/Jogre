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

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.User;
import org.jogre.common.util.GameProperties;

import info.clearthought.layout.TableLayout;

/**
 * List which shows the players at a particular table.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JTablePlayers extends JList implements Observer {

	// The list of players at the table
	private PlayerList players;

	// Colors used to draw the current player info
	protected Color highlightBackgroundColor;
	protected Color highlightTextColor;

	// Default colors to use if the game doesn't have other values defined
	// in the game.properties file.
	private final static String DEFAULT_BACKGROUND_COLOR = "230,230,230";
	private final static String DEFAULT_TEXT_COLOR = "0, 0, 0";

	/**
	 * Constructor which takes a list of players
	 *
	 * @param table    Table object.
	 */
	public JTablePlayers (PlayerList players) {
		// set fields
		this.players = players;
		setOpaque (true);

		// Set the highlight color
		highlightBackgroundColor =
		   GameProperties.getTableCurrPlayerHighlightColor (DEFAULT_BACKGROUND_COLOR);
		highlightTextColor =
		   GameProperties.getTableCurrPlayerTextColor (DEFAULT_TEXT_COLOR);

		// Set the renderer of this player list
		setCellRenderer (new TablePlayersCellRenderer());

		// Refresh screen
		refresh ();
		setVisibleRowCount(5);

		// Refresh when a player has been updated
		this.players.addObserver (this);
	}

	/**
	 * Update method which updates the visual user list.
	 */
	public void refresh () {
		setListData (players.getPlayersSortedBySeat());
	}

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
        // Refresh screen
        refresh ();
    }

	// Cell renderer class used to display the rows of the TablePlayers
	class TablePlayersCellRenderer implements ListCellRenderer {

		// The labels & rating square used to display the player's info
		JLabel playerNameLabel;
		RatingSquare theRatingSquare;

		// The panel that holds the player info
		JogrePanel fullPlayerPanel;

		/**
		 * Constructor for the cell renderer for the table players.
		 */
		public TablePlayersCellRenderer () {
			// Create the panel used to show the info
			double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
			double [][] sizes = {{4, 16, pref},{pref}};
			fullPlayerPanel = new JogrePanel(sizes);
			fullPlayerPanel.setOpaque (true);

			// Create the label that will hold the player's name
			playerNameLabel = new JLabel ();
			playerNameLabel.setFont (JogreAwt.LIST_FONT);

			// Create the rating square that will hold the player's rating
			theRatingSquare = new RatingSquare(null);

			// Put the name & rating square labels into the panel
			fullPlayerPanel.add (theRatingSquare, "1,0,c,c");
			fullPlayerPanel.add (playerNameLabel, "2,0,c,c");
		}


		/**
		 * Given a player object, fill in the component to display and
		 * return it.
		 *
		 * This is the standard method of the ListCellRenderer interface.
		 */
		public Component getListCellRendererComponent(JList list,
		                                              Object value,
		                                              int index,
		                                              boolean isSelected,
		                                              boolean cellHasFocus)
		{
			// Get the player & user that we're supposed to display
			Player thisPlayer = (Player) value;
			User thisUser = thisPlayer.getUser();

			// Set the correct component: either name only, or name & rating.
			theRatingSquare.refresh (thisUser);

			// Set the highlighting if this player is the current player.
			if (players.isCurrentPlayer(thisPlayer)) {
				// Highlight the current player
				fullPlayerPanel.setBackground (highlightBackgroundColor);
				playerNameLabel.setForeground (highlightTextColor);
				playerNameLabel.setText(thisPlayer.toString() + " <>");
			} else {
				// Don't highlight this player
				fullPlayerPanel.setBackground (Color.white);
				playerNameLabel.setForeground (Color.black);
				playerNameLabel.setText(thisPlayer.toString());
			}

			return fullPlayerPanel;
		}
	}

}