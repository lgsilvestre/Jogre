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

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.jogre.client.ClientConnectionThread;
import org.jogre.common.Game;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.util.JogreLabels;
import org.jogre.common.util.GameProperties;

import nanoxml.XMLElement;

/**
 * Generic properties dialog that contains some public hooks
 * for future development of custom game properties.
 *
 * @author Garrett Lehman (gman)
 * @version Alpha 0.2.3
 */
public class JogrePropertyDialog extends JogreDialog {
	// spacing between properties
	private final int SPACING = 5;

	// sizes for table layout
	private double pref = TableLayout.PREFERRED;
    private double[][] sizes;

    // buttons that will always exist on properties dialog
    private JogreButton startGame = null;
    private JogreButton cancel = null;

    // vectors of labels and property components
    private Vector propertyLabels = new Vector();
    private Vector propertyComponents = new Vector();

    // connection used to create a table
    private ClientConnectionThread conn = null;

    private JogreLabels labels;

	/**
	 * Constructor
	 */
	public JogrePropertyDialog(Frame owner, String title, boolean modal, ClientConnectionThread conn) {
		super (owner, title, modal);

		this.conn = conn;

	    setUpGUI ();
	}

	/**
	 * Sets up the graphical user interface.
	 */
	private void setUpGUI () {
		// Retrieve resource bundle
		labels = JogreLabels.getInstance();
		GameProperties props = GameProperties.getInstance();

		// Add game type
		int defaultGameType = (props.get("preferences.newgame.gameType", "public").equals("private") ? 1 : 0);
		JogreLabel gameTypeLabel = new JogreLabel(labels.get("game.type") + ": ");
		gameTypeLabel.setName("game_type");
		this.propertyLabels.add(gameTypeLabel);
		String[] gameTypeComboBoxStrings = {labels.get("public"), labels.get("private")};
		JComboBox gameTypeComboBox = new JComboBox(gameTypeComboBoxStrings);
		gameTypeComboBox.setSelectedIndex(defaultGameType);
		this.propertyComponents.add(gameTypeComboBox);

		// Add number of players
		JogreLabel playersLabel = new JogreLabel(labels.get("players") + ": ");
		playersLabel.setName("players");
		this.propertyLabels.add(playersLabel);

		Game game = this.conn.getGame();
		int min = game.getMinNumOfPlayers();
		int max = game.getMaxNumOfPlayers();
		int options = max - min + 1;

		int numPlayersPreference = props.getInt("preferences.newgame.numPlayers", min);
		numPlayersPreference = Math.min(max, Math.max(min, numPlayersPreference));

		String[] playersComboBoxStrings =  new String[options];
		int count = 0;
		for (int i = min; i <= max; i++)
			playersComboBoxStrings[count++] = String.valueOf(i);
		JComboBox numPlayersComboBox = new JComboBox(playersComboBoxStrings);
		numPlayersComboBox.setSelectedIndex(numPlayersPreference - min);
		this.propertyComponents.add(numPlayersComboBox);

        // Add custom gui components
		addCustomProperties(this.propertyLabels, this.propertyComponents);
		addCustomProperties(this.propertyLabels, this.propertyComponents, game.getCustomGameProperties());

		// Set up columns and rows depending on labels and components vector
		double[] columns = {SPACING, pref, SPACING, pref, SPACING};
		double[] rows = new double[(this.propertyLabels.size()*2) + 2 + 1];
		rows[0] = SPACING;
		for (int r = 1; r < rows.length; r=r+2) {
			rows[r] = pref;
			rows[r+1] = SPACING;
		}
		double[][] sizes = {columns, rows};
		this.getContentPane().setLayout(new TableLayout(sizes));
		count = 0;
		for (int r = 1; r < rows.length-2; r=r+2) {
			this.getContentPane().add((JComponent) this.propertyLabels.get(count), "1," + r + ",l,c");
			this.getContentPane().add((JComponent) this.propertyComponents.get(count), "3," + r + ",l,c");;
			count++;
		}


		// Add buttons at the bottom of the table layout
        int y = sizes[1].length - 2;
		this.startGame = new JogreButton(labels.get("start"));
		this.cancel    = new JogreButton(labels.get("cancel"));
        double [][] sizes2 = {{pref, 5, pref}, {pref}};
        JogrePanel buttons = new JogrePanel (sizes2);
        buttons.add (startGame, "0,0"); buttons.add (cancel, "2,0");
        this.getContentPane().add(buttons, "1," + y + ",3," + y + ",c,c");

		// Set up listeners for start and cancel buttons
		this.startGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				startGame();
			}
		});
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				close();
			}
		});

	    // Add listeners to the buttons
	    addCustomListeners();

        // Pack the window
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(this.getOwner());
		this.setVisible (true);
	}

	/**
	 * Start game by creating a new table comm object
	 */
	private void startGame() {
	    JComboBox publicGameComboBox = (JComboBox) this.propertyComponents.get(0);
	    boolean isPublic = publicGameComboBox.getSelectedIndex() == 0;
	    CommNewTable newTable = new CommNewTable (isPublic);
	    addPropertiesToTable(newTable);
		this.conn.send (newTable);
	    close();
	}

	/**
	 * Close dialog
	 */
	private void close() {
	    setVisible (false);
        dispose ();
	}

	/**
	 * Add properties to comm new table object
	 *
	 * @param newTable
	 */
	private void addPropertiesToTable(CommNewTable newTable) {
		int index = this.getJogreLabelIndexByName(this.propertyLabels, "players");
		JComboBox players = (JComboBox) propertyComponents.get(index);
		newTable.addProperty (Comm.PROP_PLAYERS, (String) players.getSelectedItem());

		addCustomPropertiesToTable(this.propertyLabels, this.propertyComponents, newTable);
	}

	/**
	 * Get Jogre Label index from vector of labels
	 *
	 * @param labels
	 * @param name
	 * @return the index of the jogre label according to name provided
	 */
	public int getJogreLabelIndexByName(Vector labels, String name) {
		if (labels == null || name == null)
			return -1;
		int size = labels.size();
		JogreLabel jLabel = null;
		for (int i = 0; i < size; i++) {
			jLabel = (JogreLabel) labels.get(i);
			if (name.equals(jLabel.getName()))
				return i;
		}
		return -1;
	}

	/**
	 * Hook: Add custom properties to comm new table object
	 *
	 * @param labels
	 * @param components
	 * @param newTable
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {}

	/**
	 * Hook: Custom gui setup
	 */
	public void addCustomProperties(Vector labels, Vector components) {}
	public void addCustomProperties(Vector labels, Vector components, XMLElement customGamePropertyTree) {}

	/**
	 *  Hook: Add custom listeners
	 */
	public void addCustomListeners() {}
}
