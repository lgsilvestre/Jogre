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

/**
 * Class to hold the server administrator preferences.
 * 
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminPreferences {

	public static final int DEFAULT_MESSAGE_BUFFER_SIZE = 100;
	
	private int messageBufferSize;
	
	/**
	 * Constructor.
	 */
	public AdminPreferences () {
		
	}
	
	/**
	 * Set the message buffer size.
	 * 
	 * @param value   Message buffer size.
	 */
	public void setMessageBufferSize (int value) {
		this.messageBufferSize = value;
	}
	
	/**
	 * Return the message buffer size.
	 * 
	 * @return  Message buffer size.
	 */
	public int getMessageBufferSize () {
		return this.messageBufferSize;
	}
}