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
package org.jogre.common.comm;

import java.util.Iterator;
import java.util.List;

import nanoxml.XMLElement;

import org.jogre.common.PlayerList;
import org.jogre.common.Table;

/**
 * Small communication object for joining an existing table.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommJoinTable extends CommTableMessage {

    private PlayerList playerList = null;

    private XMLElement jogreModelXML = null;

    /**
     * Constructor for client requesting joining an existing table.
     */
    public CommJoinTable (int tableNum) {
        super ();

        // Call set table num manually as this is called from JogreClientFrame
        setTableNum (tableNum);
    }

    /**
     * Constructor from the server to the joining client.
     *
     * @param username   Name of username who has just joined.
     * @param table      Table object that the user is joining.
     */
    public CommJoinTable (String username, Table table) {
        super (username);

        // Set table number manually
        setTableNum (table.getTableNum());

        // Set player list
        this.playerList    = table.getPlayerList();
        if (table.getModel() != null)
        	this.jogreModelXML = table.getModel().flatten();
    }

    /**
     * Constructor for a server responding to other clients.
     *
     * @param username   Name of username who has just joined.
     * @param tableNum      Table number of table user is joining.
     */
    public CommJoinTable (String username, int tableNum) {
        super (username);

        // Call set table number manually
        setTableNum (tableNum);
    }

    /**
     * Constructor which takes an XMLElement.
     *
     * @param message
     */
    public CommJoinTable (XMLElement message) {
        super (message);

        List children = message.getChildren();
        for (Iterator i = children.iterator(); i.hasNext(); ) {
            XMLElement e = (XMLElement)i.next();
            String name = e.getName ();
            if (name.equals (Comm.PLAYER_LIST))
                this.playerList = new PlayerList (e);
            else if (name.equals (Comm.MODEL)) {
            	this.jogreModelXML = e;
            }
        }
    }

    /**
     * Return the player list.
     *
     * @return   Player list.
     */
    public PlayerList getPlayerList () {
        return playerList;
    }

    /**
     * Return the model state.
     *
     * @return
     */
    public XMLElement getModelState () {
    	return jogreModelXML;
    }

    /**
     * Return true if this message contains a model.
     *
     * @return
     */
    public boolean containsModel () {
    	return (jogreModelXML != null);
    }

	/**
	 * Flatten the String for transmission purposes.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		XMLElement message = super.flatten (Comm.JOIN_TABLE);

		if (playerList != null)
		    message.addChild (playerList.flatten());
		if (jogreModelXML != null)
			message.addChild (jogreModelXML);

		return message;
	}
}