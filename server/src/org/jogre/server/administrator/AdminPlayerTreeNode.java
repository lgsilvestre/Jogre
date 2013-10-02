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

import java.util.Observable;
import java.util.Observer;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jogre.common.Player;
import org.jogre.common.util.JogreLabels;

/**
 * Tree node for the player.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminPlayerTreeNode extends DefaultMutableTreeNode implements Observer  {

    /** Player instance. */
    private Player player;

    /**
     * Constructor for tree node which takes a player object.
     */
    public AdminPlayerTreeNode (Player player) {
        super (player);

        this.player = player;
        this.player.addObserver (this);	// listen to changes on data.
    }

    /**
     * Return the player object.
     *
     * @return
     */
    public Player getPlayer () {
        return player;
    }

    /**
     * To String method which displays player information.
     *
     * @see java.lang.Object#toString()
     */
    public String toString () {
        StringBuffer str = new StringBuffer (player.getPlayerName () + " ");
        JogreLabels labels = JogreLabels.getInstance();

        int seat = player.getSeatNum();
        if (player.isViewing())
	    	str.append (labels.get ("is.viewing"));
	    else if (player.isSeated())
	        str.append (labels.get ("is.seated.on.seat") + " " + seat);
	    else if (player.isReady())
	    	str.append (labels.get ("is.ready.to.start.on.seat") + " " + seat);
	    else if (player.isPlaying())
	    	str.append (labels.get ("is.playing.on.seat") + " " + seat);

        return  str.toString();
    }

    /**
     * Implementation of the update method on the
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update (Observable obs, Object obj) {
        AdminTreePanel.refreshNode (this);
    }
}