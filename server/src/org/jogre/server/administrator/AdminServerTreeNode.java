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

import java.util.Collections;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jogre.common.Game;
import org.jogre.common.GameList;
import org.jogre.common.IJogre;
import org.jogre.common.util.JogreLabels;
import org.jogre.server.JogreServer;
import org.jogre.server.ServerLabels;

/**
 * Node class for a table list.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminServerTreeNode extends DefaultMutableTreeNode {

    /**
     * Constructor which takes a TableList as an instance.
     *
     * @param tableList
     */
    public AdminServerTreeNode () {
        super ();

        // Create nodes
        createNodes ();
    }

    /**
     * Create nodes method from the top.
     *
     * @param topNode
     */
    public void createNodes () {
        // Retrieve game list from the server.
        GameList gameList = JogreServerAdministrator.getInstance().getGameList ();

        if (gameList != null) {
	        // Loop throug the various games in the game list and add these to the tree
	        Game game;
	        String gameKey;
	        Vector gameKeys = gameList.getGameKeys();
	        Collections.sort(gameKeys);
	        for (int i = 0; i < gameKeys.size(); i++) {
	            gameKey = (String)gameKeys.get(i);
	            game = gameList.getGame(gameKey);
	            add (new AdminGameTreeNode (game));
	        }
        }
    }

    /**
     * Return the number of JOGRE games currently being served. 
     * 
     * @return
     */
    private int getGameCount () {
    	return JogreServerAdministrator.getInstance().getGameList ().size();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString () {
        return 
            ServerLabels.getServerTitle() + 
            " (" + 
            JogreLabels.getInstance().get("games") + ": " + getGameCount() + 
            ")";
    }
}