/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.client.awt;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.TableConnectionThread;
import org.jogre.common.User;
import org.jogre.common.Table;

/**
 * Inteface between JOGRE application / applet and client panel.
 *
 * @author starsinthesky
 * @version Alpha 0.2.3
 */
public interface IJogreClientGUI extends IJogreGUI {

	/**
	 * Return the JOGRE table frame.
	 *
	 * @param conn   Table connection to server.
	 * @return       Returns a table frame.
	 */
	public JogreTableFrame getJogreTableFrame (TableConnectionThread conn);

	/**
	 * Sets the user interfaces title.
	 *
	 * @param title   Title to set.
	 */
	public void setUITitle (String title);

	/**
	 * Return a private chat dialog.
	 *
	 * @param usernameTo   Username to talk to.
	 * @param conn         Connection to the server.
	 * @return             Private chat dialog.
	 */
	public ChatPrivateDialog getChatPrivateDialog(String usernameTo, ClientConnectionThread conn);

	/**
	 * Return a property dialog.
	 *
	 * @param conn        Connection to server.
	 */
	public void getPropertyDialog (ClientConnectionThread conn);

	/**
	 * Return a user information dialog.
	 *
	 * @param conn        Connection to server.
	 */
	public void getUserDialog (User user);

	/**
	 * Return the rules dialog.
	 */
	public void getRulesDialog();

	/**
	 * Return if this game supports extended info in the tableList
	 */
	public boolean hasExtendedInfo();

	/**
	 * Return the extended info string for a given table
	 */
	public String getExtendedTableInfoString(Table theTable);
}

