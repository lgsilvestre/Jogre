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
package org.jogre.server;

/**
 * Connection class which holds a link to a ServerConnectionThread,
 * lastAccessTime etc.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class Connection {

	private ServerConnectionThread connectionThread;
	private long lastAccessTime;

	/**
	 * Constructor which takes a ServerConnectionThread as a parameter.
	 *
	 * @param connectionThread ServerConnectionThread object.
	 */
	public Connection (ServerConnectionThread connectionThread) {
		this.connectionThread = connectionThread;
		lastAccessTime = System.currentTimeMillis();
	}

	/**
	 * This method sets the lastAccessTime to the current time.  This method
	 * should be called whenever a user does some activity.
	 */
	public void updateLastAccessTime () {
		lastAccessTime = System.currentTimeMillis();
	}

	/**
	 * Return the server connection thread.
	 *
	 * @return
	 */
	public ServerConnectionThread getServerConnectionThread () {
		return connectionThread;
	}

	/**
	 * Return the last access time of this ServerConnectionThread.
	 *
	 * @return
	 */
	public long getLastAccessTime () {
		return lastAccessTime;
	}
}