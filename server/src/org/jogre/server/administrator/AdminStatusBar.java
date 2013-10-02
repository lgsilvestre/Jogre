/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.server.administrator;

import info.clearthought.layout.TableLayout;

import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jogre.common.Game;
import org.jogre.common.GameList;
import org.jogre.common.util.JogreLabels;
import org.jogre.server.JogreServer;

/**
 * Declare a little status bar.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminStatusBar extends JPanel {

    private JLabel userLabel, tableLabel;

    private JogreLabels labels = JogreLabels.getInstance();
    private String usersStr, tablesStr;
    private JogreServer server;

    /**
     * Constructor.
     */
    public AdminStatusBar () {

        // Set layout
        double pref = TableLayout.PREFERRED;
        double [][] layout = {{pref, 5, pref}, {pref}};
        setLayout(new TableLayout (layout));

        usersStr = labels.get ("users");
        tablesStr = labels.get ("tables");

        // Declare labels
        userLabel = new JLabel (" ");
        tableLabel = new JLabel (" ");

        // Update labels
        refresh ();

        // Set fonts of labels
        Font font = userLabel.getFont();
        Font plainFont = new Font (userLabel.getFont().getName(), Font.PLAIN, font.getSize());
        userLabel.setFont(plainFont);
        tableLabel.setFont(plainFont);

        // Set borders
        userLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        tableLabel.setBorder(BorderFactory.createLoweredBevelBorder());

        // Add labels to panel
        add (userLabel, "0,0");
        add (tableLabel, "2,0");
    }

    /**
     * Refresh the labels.
     */
    public void refresh () {
        // Number of users / tables.
        int numOfUsers = 0;
        int numOfTables = 0;

        // Retrieve number of users and tables
        GameList gameList = JogreServerAdministrator.getInstance().getGameList();
        if (gameList != null) {
	        Vector keys = gameList.getGameKeys();
	        for (int i = 0; i < gameList.size(); i++) {
	            Game g = gameList.getGame((String)keys.get(i));
	
	            numOfUsers += g.getUserList().size();
	            numOfTables += g.getTableList().size();
	        }
        }

        // Update labels
        userLabel.setText ("  " + usersStr + " " + numOfUsers + "  ");
        tableLabel.setText ("  " + tablesStr + " " + numOfTables + "  ");
    }
}
