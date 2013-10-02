/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.carTricks.client;

import java.lang.Integer;

import info.clearthought.layout.TableLayout;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.jogre.common.util.GameLabels;
import org.jogre.common.PlayerList;
import org.jogre.client.awt.JogreDialog;
import org.jogre.client.awt.JogreButton;
import org.jogre.client.awt.JogreLabel;

/**
 * Dialog that displays the results of a game of carTricks
 *
 * @author Richard Walter (rwalter42)
 * @version Alpha 0.2.3
 */
public class CarTricksScoreDialog extends JogreDialog {
	// spacing between properties
	private final int SPACING = 5;

    // button on the score dialog
    private JogreButton closeButton = null;

	// The actual finishing positions
	private int [] actualFinishPositions;

	// Keep track of the number of scores needed until we can show the dialog
	private int numberOfScoresToGo;

	/**
	 * Constructor
	 *
	 * @param	owner					The table frame that is the owner of this dialog
	 * @param	title					The title to use for the dialog box
	 * @param	numPlayers				The number of real players in the game
	 * @param	players					The Players in the game
	 * @param	actualFinishPositions	An array with the actual finish positions for the cars.
	 *										array[0] is the car is first place
	 *										array[1] is the car in second place
	 *										etc...
	 *
	 *	Note: numPlayers != players.size(), because if there are observers then
	 *	they will show up in players.size(), but should *NOT* be included in numPlayers
	 */
	public CarTricksScoreDialog(
		Frame owner,
		String title,
		int numPlayers,
		PlayerList players,
		int [] actualFinishPositions)
	{
		super (owner, title, false);

		this.actualFinishPositions = actualFinishPositions;
	    setUpGUI (actualFinishPositions.length, numPlayers, players);

		numberOfScoresToGo = numPlayers;
	}

	/**
	 * Sets up the graphical user interface.
	 */
	private void setUpGUI (int numCars, int numPlayers, PlayerList players) {
	    // Retrieve resource bundle
		GameLabels labels = GameLabels.getInstance();

		// Create the size array for the items
		int numRows = (2 * numCars) + 7;
		int numColumns = (4 * numPlayers) + 5;
		double [] columns = createTableArray(numColumns, SPACING);
		double [] rows = createTableArray(numRows, SPACING);

		// Set the layout for the content pane to the size array created above
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new TableLayout(columns, rows));

		// Add the objects to the pane
		contentPane.add(new JogreLabel(labels.get("score.dialog.cars")), "1,3,c,c");
		contentPane.add(new JogreLabel(labels.get("score.dialog.place")), "3,3,c,c");
		contentPane.add(new JogreLabel(labels.get("score.dialog.total")), "1," + (numRows-2) + ",c,c");

		// Add the player's names, bid & points labels
		for (int i = 0; i < numPlayers; i++) {
			int h = (4*i + 5);
			contentPane.add(new JogreLabel(players.getPlayer(i).getPlayerName()), h + ",1," + (h+2) + ",1,c,c");
			contentPane.add(new JogreLabel(labels.get("score.dialog.bid")), h + ",3,c,c");
			contentPane.add(new JogreLabel(labels.get("score.dialog.points")), (h+2) + ",3,c,c");
		}

		// Add the car's pictures & finish positions
		for (int i = 0; i < numCars; i++) {
			int v = (2*i + 5);
			contentPane.add(new CarTricksLargeCarComponent(actualFinishPositions[i]), "1," + v + ",c,c" );
			contentPane.add(new JogreLabel(Integer.toString(i+1)), "3," + v + ",c,c");
		}

		// Add the lines to the pane
		// Note: The lines are *NOT* centered; they are full-justified so that they get pulled to the entire extent
		contentPane.add(new LineComponent(true), "0,4," + (numColumns-1) + ",4");
		contentPane.add(new LineComponent(true), "0," + (numRows-3) + "," + (numColumns-1) + "," + (numRows-3));
		for (int i = 0; i < numPlayers; i++) {
			int h = (4*i + 4);
			contentPane.add(new LineComponent(false), h + ",0," + h + "," + (numRows-1));
			contentPane.add(new LineComponent(false, 1, 3), (h+2) + ",4," + (h+2) + "," + (numRows-3));
		}
	}

	/**
	 * Create an array to be used for table layout.  This creates an array of size
	 * num_entries that alternates between spacing_value & preferred.
	 *
	 * @param	num_entries		The number of entries in the array
	 * @param	spacing			The size of the spacing elements
	 * @return		An array of doubles filled with the right values.
	 */
	private double [] createTableArray ( int num_entries, double spacing) {
		double [] theArray = new double [num_entries];
		int i = 0;

		// Since we do two at a time, we need to make sure that we don't fall
		// off the end if num_entries is odd...
		num_entries -= 1;
		while (i < num_entries) {
			theArray[i] = spacing;
			theArray[i+1] = TableLayout.PREFERRED;
			i += 2;
		}

		// If num_entries was odd, then we need to end with a last entry
		// of <spacing>.
		if (i == num_entries) {
			theArray[i] = spacing;
		}

		return theArray;
	}

	/**
	 * Add the information for the given player to the dialog box.
	 * Return value indicates if we've got all of the scores and are now
	 * shown or not.
	 *
	 * @param	seatNum			The seat number for the player reported
	 * @param	bid				The bid made by the player
	 * @return boolean value that indicates if we've shown the dialog or not.
	 */
	public boolean addScore(int seatNum, int [] bid) {
		int totalScore = 0;

		// Get the content pane for the dialog
		Container contentPane = this.getContentPane();

		// Add this player's bid & score for each car
		int h = 4*seatNum + 5;
		int v = 5;
		for (int i = 0; i < bid.length; i++) {
			int bidPlace = findInArray(bid, actualFinishPositions[i])+1;
			int thisScore = (bidPlace) * (i+1);
			totalScore += thisScore;

			contentPane.add(new JogreLabel(Integer.toString(bidPlace)), h + "," + v + ",c,c");
			contentPane.add(new JogreLabel(Integer.toString(thisScore)), (h+2) + "," + v + ",c,c");

			v += 2;
		}

		// Add this player's total score
		contentPane.add(new JogreLabel(Integer.toString(totalScore)), (h+2) + "," + v + ",c,c");

		// Subtract this from the number of scores to go.
		numberOfScoresToGo -= 1;

		// If we've got all of the scores, then we can show the dialog
		if (numberOfScoresToGo == 0) {
			pack();
			setResizable(false);
			setLocationRelativeTo(getOwner());
			setVisible (true);
			return true;
		}
		return false;
	}

	/**
	 * This finds the given item in the given array.  It returns the index of that
	 * item.  If the item isn't in the array, then it returns -1.
	 *
	 * @param	theArray		The array to search
	 * @param	item			The item to search for
	 * @return			The index of item, or -1 if not found
	 */
	private int findInArray(int [] theArray, int item) {
		for (int i = 0; i < theArray.length; i++) {
			if (theArray[i] == item) {
				return i;
			}
		}
		return -1;
	}
}
