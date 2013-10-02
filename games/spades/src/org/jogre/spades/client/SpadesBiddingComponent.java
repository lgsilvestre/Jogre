/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
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
package org.jogre.spades.client;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jogre.common.util.JogreLogger;

/**
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 *
 * Component for making a bid for a spades hand
 */
public class SpadesBiddingComponent extends JPanel {

	// constants for bidding state
	public final static int BIDDING_STATE_BEHIND_BY_100_POINTS = 0;
	public final static int BIDDING_STATE_NORMAL = 1;
	public final static int BIDDING_STATE_NONE = 2;

	// constants for bids
	public final static int BID_NO_BID = -2;
	public final static int BID_BLIND_NIL = -1;
	public final static int BID_NIL = 0;
	public final static int BID_1 = 1;
	public final static int BID_2 = 2;
	public final static int BID_3 = 3;
	public final static int BID_4 = 4;
	public final static int BID_5 = 5;
	public final static int BID_6 = 6;
	public final static int BID_7 = 7;
	public final static int BID_8 = 8;
	public final static int BID_9 = 9;
	public final static int BID_10 = 10;
	public final static int BID_11 = 11;
	public final static int BID_12 = 12;
	public final static int BID_13 = 13;

	// Button values (bid values)
	private int[] buttonValues = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			0, -1 };

	// Button names
	private String[] buttonNames = { "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "10", "11", "12", "13", "Nil", "Blind Nil" };

	// Buttons for bidding
	private JButton[] buttons = new JButton[buttonNames.length];
	private JButton showCardsButton = null;

	// Spades controller
	private SpadesController controller = null;

	/**
	 * Default constructor which takes a spades model
	 *
	 * @param spadesModel
	 *            Link to the main spades model
	 */
	public SpadesBiddingComponent() {

		// Create panels which users can add to
		double pref = TableLayout.PREFERRED;
		double[][] sizes = new double[][] { { pref, pref, pref },
				{ pref, pref, pref, pref, pref, pref, pref } };

		this.setVisible(false);
		this.setLayout(new TableLayout(sizes));

		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 3; c++) {
				int i = (r * 3) + c;

				if (i < 15) {
					buttons[i] = new JButton(buttonNames[i]);
					buttons[i].setFont(SpadesLookAndFeel.TEXT_BUTTON_FONT);
					buttons[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							JButton button = (JButton) event.getSource();
							sendBid(button.getText());
						}
					});
					buttons[i].setEnabled(false);

					String param = c + "," + r;

					if (i == 13)
						param = "1,4,2,4";
					else if (i == 14)
						param = "0,5,2,5";

					this.add(buttons[i], param);
				}
			}
		}

		showCardsButton = new JButton("Show Cards");
		showCardsButton.setFont(SpadesLookAndFeel.TEXT_BUTTON_FONT);
		showCardsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				showCards();
			}
		});
		showCardsButton.setEnabled(false);

		this.add(showCardsButton, "0,6,2,6");
	}

	/**
	 * Set controller to this component
	 *
	 * @param controller
	 */
	public void setController(SpadesController controller) {
		this.controller = controller;
	}

	/**
	 * Toggle buttons for bidding on provided state situation
	 *
	 * @param state
	 *            State that tells the situation (BEHIND_BY_100_POINTS_BID,
	 *            NORMAL_BID, NO_BID)
	 */
	public void toggleButtons(int state)
	{
		if (state == BIDDING_STATE_BEHIND_BY_100_POINTS) {
			int length = this.buttons.length - 1;
			for (int i = 0; i < length; i++)
				this.buttons[i].setEnabled(false);
			this.buttons[length].setEnabled(true);
			this.showCardsButton.setEnabled(true);
			this.setVisible(true);
		}
		else if (state == BIDDING_STATE_NORMAL) {
			int length = this.buttons.length - 1;
			for (int i = 0; i < length; i++)
				this.buttons[i].setEnabled(true);
			this.buttons[length].setEnabled(false);
			this.showCardsButton.setEnabled(false);
			this.setVisible(true);
		}
		else if (state == BIDDING_STATE_NONE) {
			this.setVisible(false);
			int length = this.buttons.length;
			for (int i = 0; i < length; i++)
				this.buttons[i].setEnabled(false);
			this.showCardsButton.setEnabled(false);
		}
	}

	/**
	 * After a button is pressed, send the bid from the controller
	 *
	 * @param bidName Bid name from button
	 */
	private void sendBid(String bidName) {
		int index = -1;
		for (int i = 0; i < buttonNames.length; i++) {
			if (bidName.equals(buttonNames[i])) {
				index = i;
				break;
			}
		}
		if (index > -1) {
			int bid = this.buttonValues[index];
			this.toggleButtons(BIDDING_STATE_NONE);
			this.controller.makeBid(bid);

		}
	}

	/**
	 * Show cards instead of bidding Blind Nil
	 */
	private void showCards() {
		this.controller.turnHandOver(true);
		toggleButtons(BIDDING_STATE_NORMAL);
	}
}