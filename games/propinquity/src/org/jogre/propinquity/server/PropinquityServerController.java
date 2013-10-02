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
package org.jogre.propinquity.server;

import nanoxml.XMLElement;

import org.jogre.common.Table;
import org.jogre.propinquity.client.PropinquityModel;
import org.jogre.propinquity.common.CommPropinquityAttackNum;
import org.jogre.propinquity.common.CommPropinquityMove;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Sever table parser for propinquity.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class PropinquityServerController extends ServerController {
    
    /**
     * Constructor which takes a game key (read from directory).
     * 
     * @param gameKey   gamekey (read from directory).
     */
    public PropinquityServerController (String gameKey) {
        super (gameKey);
    }
    
    /**
     * Start a game (create a new table).
     * 
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	setModel (tableNum, new PropinquityModel ());
	}

	/**
	 * Implement the game over functionality.
	 * 
	 * @see org.jogre.server.ServerController#gameOver(org.jogre.server.ServerConnectionThread, int, int)
	 */
	public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
		
	}    
    
    /**
     * Parse a propinquity message.
     *
     * @see org.jogre.server.ITableParser#parseTableMessage(org.jogre.server.ServerConnectionThread, nanoxml.XMLElement)
     */
    public void parseTableMessage (ServerConnectionThread conn, XMLElement message, int tableNum) {
        // Retrieve the message type
        String messageType = message.getName();
        Table table = getTable(tableNum);

        // Parse the message
        if (messageType.equals (CommPropinquityAttackNum.XML_NAME)) {
            CommPropinquityAttackNum clientRequest = new CommPropinquityAttackNum (message);

           	CommPropinquityAttackNum attackNum = new CommPropinquityAttackNum ();

          	conn.transmitToTablePlayers (tableNum, attackNum);
        }
        else if (messageType.equals (CommPropinquityMove.XML_NAME)) {
            CommPropinquityMove move = new CommPropinquityMove (message);
            String sender = conn.getUsername();
            move.setUsername(sender);	

            // Update server model
            PropinquityModel model = (PropinquityModel)getModel (tableNum);
            model.playerMove (getSeatNum(sender, tableNum), move.getMove());
            
            // send move to players EXCEPT to the person who sent it
            conn.transmitToTablePlayers (sender, tableNum, move);

            // transmit new attack number to ALL players
            CommPropinquityAttackNum attackNum = new CommPropinquityAttackNum ();
            model.setAttackNum(attackNum.getAttackNum());
            conn.transmitToTablePlayers (tableNum, attackNum);
        }
    }
}