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
package org.jogre.client;

import org.jogre.common.Game;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.UserList;
import org.jogre.common.comm.CommGameMessage;
import org.jogre.common.comm.CommTableMessage;

/**
 * <p>Convience class for sending message from a table client frame in JOGRE.</p>
 *
 * <p>This class also has links to several methods in the ClientConnectionThread
 * class through delegation.</p>
 *
 * <p>This class holds a link to the ClientConnectionThread (as there should
 * only be one instance per client) and a tableNum which it appends to messages
 * automatically when a send method is called.</p>
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TableConnectionThread {

    /**
     * Link to the server.
     */
    private ClientConnectionThread conn;

    /**
     * Table number.
     */
    private int tableNum;

    /**
     * Constructor which takes a ClientConnectionThread and a table number.
     *
     * @param conn      Connection to server.
     * @param tableNum  Current table number.
     */
    public TableConnectionThread (ClientConnectionThread conn, int tableNum) {
        this.conn = conn;
        this.tableNum = tableNum;
    }

	/**
	 * @param message
	 */
	public void send (CommTableMessage message) {
		message.setTableNum (this.tableNum);

		this.conn.send (message);
	}

	/**
     * Return the table number.
     *
     * @return  Table number
     */
    public int getTableNum () { return this.tableNum; }

    /**
     * Return the table associated with this table connection class.
     *
     * @param message
     */
    public Table getTable () {
        return this.conn.getTableList().getTable (this.tableNum);
    }

    /**
     * Return the game object on this connection.
     *
     * @return
     */
    public Game getGame () {
    	return conn.getGame();
    }

	// Delegate methods from ClientConnectionThread

	/**
	 * Send a game message.
	 *
	 * @param message   Game message.
	 */
	public void send (CommGameMessage message) { this.conn.send (message); }

	/**
	 * Delegate method for returning a username;
	 *
	 * @return   Username.
	 */
	public String getUsername () { return this.conn.getUsername(); }

	/**
	 * Delegate method for returning the table list.
	 *
	 * @return  List of tables.
	 */
	public TableList getTableList () { return this.conn.getTableList(); }

	/**
	 * Delegate method for returning the user list.
	 *
	 * @return  List of users.
	 */
	public UserList getUserList () { return this.conn.getUserList(); }

	/**
	 * Return thue if this user is the owner of the table.
	 *
	 * @return
	 */
	public boolean isOwnerOfTable () {
	    return this.conn.getUsername().equals (
	        getTable().getOwner()
	    );
	}

	/**
	 * Return the client connection thread which is a field.
	 *
	 * @return  Current connection to server.
	 */
	public ClientConnectionThread getClientConnectionThread () {
		return this.conn;
	}
}
