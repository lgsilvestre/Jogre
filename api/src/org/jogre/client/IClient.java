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

import nanoxml.XMLElement;

/**
 * Client interface between the ClientConnectionThread (which listens on network
 * calls) and a JogreClientFrame which implements this interface.
 *
 * Note that the extends ITable as a client which implements this interface must
 * also be able to recieve TableMessages and forward the message to the correct
 * table.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public interface IClient extends ITable {

	/**
	 * Recieve a message specific to a particular game.
	 *
	 * @param message  Game message from server
	 */
	public void receiveGameMessage (XMLElement message);
}
