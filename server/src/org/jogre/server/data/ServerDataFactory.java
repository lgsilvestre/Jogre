/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.server.data;

import org.jogre.server.ServerProperties;
import org.jogre.server.data.db.ServerDataDB;
import org.jogre.server.data.xml.ServerDataXML;

/**
 * Factory class which returns the correct instance of a user
 * connection.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ServerDataFactory {
	
	/**
	 * Static factory method which will return an instance of a 
	 * data connection depending on the server_data attribute 
	 * in the "server.xml" file.
	 * 
	 * @return       Server connection instance (local / master).
	 */
	public static IServerData getInstance ()  {
		String userConnProp = ServerProperties.getInstance().getCurrentServerData();
		
		// Depending on value - load correct instance.
		if (userConnProp.equals (IServerData.XML))
			return new ServerDataXML ();		// link to local file system
        else if (userConnProp.equals(IServerData.DATABASE))
            return new ServerDataDB ();     // link to master server
        else if (userConnProp.equals(IServerData.JOGRE_DOT_ORG))
			return new ServerDataDB ();		// link to master server
		else {
			System.err.println ("No user connection defined");
			System.exit (0);		// Exit
		}		
		
		return null;
	}
}