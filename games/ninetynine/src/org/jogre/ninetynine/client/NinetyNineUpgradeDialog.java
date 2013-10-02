/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.client;

import java.lang.Integer;

import info.clearthought.layout.TableLayout;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.jogre.common.util.GameLabels;
import org.jogre.common.PlayerList;
import org.jogre.client.awt.JogreDialog;
import org.jogre.client.awt.JogreButton;
import org.jogre.client.awt.JogreLabel;

/**
 * Dialog that asks the user about upgrading from a declare bid
 * to a reveal bid .
 *
 * @author Richard Walter (rwalter42)
 * @version Alpha 0.1
 */
public class NinetyNineUpgradeDialog extends JogreDialog {

	// spacing between items
	private final int SPACING = 5;

    // buttons on the dialog
	private JogreButton dontUpgradeButton = null;
	private JogreButton upgradeP0LeadsButton = null;
	private JogreButton upgradeP1LeadsButton = null;
    private JogreButton upgradeP2LeadsButton = null;

	// The alertee object to call when our buttons are clicked
	private IUpgradeDialogAlertee buttonAlertee;

	/**
	 * Constructor
	 *
	 * @param	owner				The table frame that is the owner of this dialog
	 * @param	title				The title to use for the dialog box
	 * @param	revealPlayerName	The name of the player who bid reveal
	 * @param	buttonAlertee		The object we are suppossed to alert when a button is pressed.
	 * @param	mySeatNum			The seat number of this client.
	 */
	private NinetyNineUpgradeDialog(
		Frame owner,
		String title,
		String revealPlayerName,
		IUpgradeDialogAlertee buttonAlertee,
		int mySeatNum)
	{
		super (owner, title, true);
		this.buttonAlertee = buttonAlertee;

	    setUpGUI (revealPlayerName, mySeatNum);
	}

	/**
	 * Static factory for making dialogs.
	 * This is needed because a constructor can't have any
	 * code above super() when invoking the parent type's creation
	 * routine, but here we need to look up the game labels to
	 * get the title string before we can call the parent creation.
	 */
	public static NinetyNineUpgradeDialog newDialog(
		Frame owner,
		String revealPlayerName,
		IUpgradeDialogAlertee buttonAlertee,
		int mySeatNum)
	{
	    // Retrieve resource bundle
		GameLabels labels = GameLabels.getInstance();
		return new NinetyNineUpgradeDialog(	owner,
											labels.get("upgrade.dialog.title"),
											revealPlayerName,
											buttonAlertee,
											mySeatNum);
	}

	/**
	 * Sets up the graphical user interface.
	 *
	 * @param	revealPlayerName	The name of the player who bid reveal.
	 * @param	mySeatNum			The seat number of this client.
	 */
	private void setUpGUI (String revealPlayerName, int mySeatNum) {
	    // Retrieve resource bundle
		GameLabels labels = GameLabels.getInstance();

		// Create the array for the panel
		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
			{SPACING, pref, SPACING},
			{SPACING, pref, SPACING, pref, SPACING, pref, SPACING, pref, SPACING, pref, SPACING, pref, SPACING} };

		// Set the layout for the content pane to the size array created above
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new TableLayout(table_params));

		// Add the text to the pane
		MessageFormat dlgFormat = new MessageFormat(labels.get("upgrade.dialog.text.1.format"));
		Object [] args = {revealPlayerName};
		contentPane.add(new JogreLabel(dlgFormat.format(args)), "1,1,c,c");
		contentPane.add(new JogreLabel(labels.get("upgrade.dialog.text.2")), "1,3,c,c");

		// Create the buttons
		int p1 = (mySeatNum + 1) % 3;
		int p2 = (mySeatNum + 2) % 3;
		String upString = labels.get("upgrade.dialog.button.upgrade");
		String leadString = labels.get("lead");
		dontUpgradeButton = new JogreButton (labels.get("upgrade.dialog.button.pass"));
		upgradeP0LeadsButton = new JogreButton (upString + " " + labels.get("player.label." + mySeatNum) + " " + leadString);
		upgradeP1LeadsButton = new JogreButton (upString + " " + labels.get("player.label." + p1) + " " + leadString);
		upgradeP2LeadsButton = new JogreButton (upString + " " + labels.get("player.label." + p2) + " " + leadString);

		// Add the buttons to the pane
		contentPane.add(dontUpgradeButton, "1,5,c,c");
		contentPane.add(upgradeP0LeadsButton, "1,7,c,c");
		contentPane.add(upgradeP1LeadsButton, "1,9,c,c");
		contentPane.add(upgradeP2LeadsButton, "1,11,c,c");

		// Create action listeners for the bid buttons
		createDialogButtonsActionListeners();

        // Pack the window
		pack();
		setResizable(false);
		setLocationRelativeTo(this.getOwner());
		setVisible (true);
	}

	/**
	 * Adds action listeners to each of the buttons
	 */
	private void createDialogButtonsActionListeners() {
		dontUpgradeButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				buttonAlertee.noUpgradeButtonClicked ();
				setVisible (false);
				dispose ();
			}
		});

		upgradeP0LeadsButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				buttonAlertee.upgradeButtonClicked (0);
				setVisible (false);
				dispose ();
			}
		});

		upgradeP1LeadsButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				buttonAlertee.upgradeButtonClicked (1);
				setVisible (false);
				dispose ();
			}
		});

		upgradeP2LeadsButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				buttonAlertee.upgradeButtonClicked (2);
				setVisible (false);
				dispose ();
			}
		});
	}

}
