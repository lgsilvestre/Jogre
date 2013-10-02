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
package org.jogre.common;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;

/**
 * Transmittable user list data object.  Contains the a HashMap of users.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class UserList extends Observable implements ITransmittable {

	/** List of users. */
	protected HashMap userlist;

	/**
	 * Default constructor of a UserList object.
	 */
	public UserList () {
		userlist = new HashMap ();
	}

	/**
	 * Constructor which reads a user list from a String.
	 *
	 * @param message                  XML element version of object.
	 * @throws TransmissionException   Thrown if there is a problem in transmission.
	 */
	public UserList (XMLElement message) throws TransmissionException {
		this ();

		if (!message.getName().equals (Comm.USER_LIST))
			throw new TransmissionException ("Error parsing UserList");

		// Create new table from string and add to Hash
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childMessage = (XMLElement)e.nextElement();

			if (childMessage.getName().equals(Comm.USER)) {
				User user = new User (childMessage);
				userlist.put (user.getUsername(), user);	// insert in hash
			}
		}
	}

	/**
	 * Creates a new User using a username and a rating and adds to the HashMap.
	 *
	 * @param username Username of User.
	 * @param rating   Rating of this user.
	 */
	public void addUser (User user) {
	    // Add to hash
	    String username = user.getUsername();
		userlist.put (user.getUsername(), user);

	    // Notify listeners
	    setChanged ();
		notifyObservers ("+U " + username);
	}

	/**
	 * Remove user from the list.
	 *
	 * @param username
	 */
	public void removeUser (String username) {
	    // Remove user from hash
		userlist.remove (username);

	    // Notify listeners
	    setChanged ();
		notifyObservers ("-U " + username);
	}

	/**
	 * Return true if the user list contains this user.
	 *
	 * @param username  Username to check for in the list.
	 * @return          True if list contains the username as a key.
	 */
	public boolean containsUser (String username) {
		return userlist.containsKey (username);
	}

	/**
	 * Returns a list of all the users.
	 *
	 * @return  List of users as Strings in a Vector.
	 */
	public Vector getUsers () {
		return new Vector (userlist.keySet());
	}

	/**
	 * Return the user objects.
	 *
	 * @return
	 */
	public Vector getUserObjects () {
	    return new Vector (userlist.values());
	}

	/**
     * Return a user object from a username.
     *
     * @param username  Username to search on
     * @return          User object if found.
     */
	public User getUser (String username) {
	    return (User)userlist.get(username);
	}

	/**
	 * Flatten the current list of logged on users.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten ()  {
		XMLElement message = new XMLElement (Comm.USER_LIST);

		Set usernames = userlist.keySet();		// get all usernames
		Iterator i = usernames.iterator();		// iterate through them
		while (i.hasNext()) {
			User user = (User)userlist.get(i.next());
			message.addChild (user.flatten());
		}

		return message;
	}

	/**
	 * Return the number of users.
	 *
	 * @return   Number of users (between 0 and GameProperties.getMaxNumOfUsers()).
	 */
	public int size () {
		return userlist.size();
	}

	/**
	 * Return String version of the userList.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
	    return userlist.keySet().toString();
	}

	/**
	 * Notify observers.
	 *
	 * @see java.util.Observable#notifyObservers()
	 */
	public void refreshObservers() {
	    setChanged();
	    super.notifyObservers();
	}
}
