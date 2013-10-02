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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.ITransmittable;
import org.jogre.common.util.JogrePropertyHash;

/**
 * Contains a HashMap of all the Table objects currently in play.  This class
 * also implements the ITransmittable interface so this object can be
 * transferred across the network.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class TableList extends Observable implements ITransmittable {

	/**
	 * HashMap containing a number of Table objects.  They key is an Integer
	 * object of the table number.
	 */
	protected HashMap tablelist;

	/**
	 * A vector of unused table numbers.
	 */
	protected Vector unusedTableNumbers;

	/**
	 * The highest #'d table that is either in use or in the unusedTableNumber
	 * vector.
	 */
	protected int highestTableNum;

	/**
	 * Default constructor which sets up a brand new TableList object with no
	 * Tables in its HashMap.
	 */
	public TableList () {
	    // Create new hash map for tables
		tablelist = new HashMap ();
		unusedTableNumbers = new Vector();
		highestTableNum = 0;
	}

	/**
	 * Constructor which recreates a Table object using a String from the
	 * ITransmittable.flatten() method from another TableList object.
	 *
	 * @param message            String representation of a TableList object
	 * @throws TransmissionException
	 */
	public TableList (XMLElement message) throws TransmissionException {
	    this ();

		if (!message.getName().equals(Comm.TABLE_LIST))
			throw new TransmissionException ("Error parsing table list");

		// Create new table from string and add to Hash
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement childElement = (XMLElement)e.nextElement();

			if (childElement.getName().equals(Comm.TABLE)) {
				Table table = new Table (childElement);
				tablelist.put (new Integer(table.getTableNum()), table);

				highestTableNum = Math.max(highestTableNum, table.getTableNum());
			}
		}

		// Fill in the unusedTableNumbers vector with tables that are less than
		// highestTablenum, but not in the table.
		for (int i = 0; i < highestTableNum; i++) {
			Integer keyI = new Integer(i);
			if (!tablelist.containsKey(keyI)) {
				unusedTableNumbers.add(keyI);
			}
		}
	}

	/**
	 * Add a new Table object to the HashMap.  The Table object is created using
	 * the parameters creator, isPublic and a computed table number which is
	 * generated using the computeNewTableNum() method.  This Table object is
	 * then stored in a HashMap with this computed table number (Integer) as its
	 * key.
	 *
	 * @param creator     The user that is creating this table.
	 * @param isPublic    True / false if public / private table.
	 * @param properties  Properties of this table.
	 * @return            The new table.
	 */
	public Table addTable (User creator, boolean isPublic, JogrePropertyHash properties) {
	    // Generate new table number.
		Integer newTableNum = computeNewTableNum();

		// Create new Table object.
		Table table = new Table (newTableNum.intValue(), creator, isPublic, properties);

		// Store in HashMap using the table number as its key.
		tablelist.put (newTableNum, table);

	    // Notify listeners
	    setChanged ();
		notifyObservers ("+T " + newTableNum);

		// Return the table number
		return table;
	}

	/**
	 * Remove table from the list.  This is usually called when all the users
	 * leave a particular table for example.
	 *
	 * @param tableNum  Number of the table to be removed.
	 */
	public void removeTable (int tableNum) {
		Integer iTableNum = new Integer (tableNum);
		tablelist.remove (iTableNum);

		// Reclaim this table number for reuse.
		if (tableNum == highestTableNum) {
			highestTableNum -= 1;
		} else {
			unusedTableNumbers.add(iTableNum);
		}

	    // Notify listeners
	    setChanged ();
		notifyObservers ("-T " + tableNum);
	}

	/**
	 * Remove a player from a table.  If the player is the last player to
	 * leave the table then the table will also be removed from the HashMap.
	 *
	 * @param tableNum  Table number.
	 * @param player    The name of the player to remove.
	 * @return true if the player was removed succesfully.
	 */
	public boolean removePlayer (int tableNum, String player) {
		Table table = getTable (tableNum);

		// Verify that table & player exist
		if ((table == null) || (player == null)) {
			return false;
		}

		// Verify that the player is removed from the player list.
		PlayerList players = table.getPlayerList();
		if (!players.removePlayer (player)) {
			return false;
		}

		// See if the table is now empty, and if so, remove it.
		if (players.size() == 0) {
			removeTable (tableNum);
		}

		// Notify listeners
		setChanged ();
		notifyObservers ("-P " + tableNum + " " + player);

		return true;
	}

	/**
	 * Remove a user from tables.
	 *
	 * @param player
	 */
	public void removeUserFromTables (String player) {
		Vector keys = new Vector (tablelist.keySet());

		// Loop through each table and remove the player
		for (int i = 0; i < keys.size(); i++) {
			int tableNum = ((Integer)keys.get(i)).intValue();
			removePlayer (tableNum, player);
		}

	    // Notify listeners
	    setChanged ();
		notifyObservers ("-U " + player);
	}

	/**
	 * Return a Table object from a specified table number.
	 *
	 * @param tableNum   Table number.
	 * @return           Table object if exists.
	 */
	public Table getTable (int tableNum) {
		return (Table)tablelist.get(new Integer(tableNum));
	}

	/**
	 * Returns a list of all the table numbers.
	 *
	 * @return   Array of integer table numbers.
	 */
	public int [] getTablesNumbers () {
		Vector keys = new Vector (tablelist.keySet());
		Collections.sort(keys);

		// Create integer and store key values it
		int [] intKeys = new int [tablelist.size()];
		for (int i = 0; i < keys.size(); i++) {
			int keyNum = ((Integer)keys.get(i)).intValue();
			intKeys [i] = keyNum;
		}

		return intKeys;
	}

	/**
	 * Update a table in the table list.
	 *
	 * @param tableNum
	 * @param table
	 */
	public void updateTable (int tableNum, Table table) {
		int tableCount = tablelist.size();
		tablelist.put (new Integer(tableNum), table);

		String notify = "";
		if (tablelist.size() > tableCount)
			notify = "+T " + tableNum;

		// Notify listeners
	    setChanged ();
		notifyObservers (notify);
	}

	/**
	 * Return the size of the table list.
	 *
	 * @return   Number of Table objects in TableList.
	 */
	public int size () {
		return tablelist.size();
	}

	/**
	 * Return a list of table numbers of a user.
	 *
	 * @param username
	 * @return
	 */
	public int [] getTableNumsForUser (String username) {

		int [] tableNums = getTablesNumbers();
		int [] userTableNumsTemp = new int [tableNums.length];

		int c = 0;
		for (int i = 0; i < tableNums.length; i++) {
			Table table = getTable(tableNums[i]);
			if (table.containsPlayer(username))
				userTableNumsTemp [c++] = tableNums[i];
		}

		// Create new integer array with populated values
		int [] userTableNums = new int [c];
		for (int i = 0; i < c; i++)
			userTableNums [i] = userTableNumsTemp [i];
		return userTableNums;
	}

	/**
	 * Return the number of tables that a specified user has created
	 * i.e. that the user is the owner of.
	 *
	 * @param username   Username to check.
	 * @return           Number of tables this user is owner of.
	 */
	public int getNumOfTablesUserOwns (String username) {
		int [] tableNums = getTablesNumbers();

		int count = 0;
		for (int i = 0; i < tableNums.length; i++) {
			Table table = getTable (tableNums[i]);
			if (table.getOwner().equals (username))
				count++;
		}

		return count;
	}

	/**
	 * Computes a new table number.
	 *
	 * @return  Unique table number.
	 */
	public Integer computeNewTableNum () {
		if (unusedTableNumbers.size() == 0) {
			// No table numbers are available to recycle, so all tables from
			// 1 to highestTableNum are being used.  Therefore, our new table
			// number is (highestTableNum + 1)
			highestTableNum += 1;
			return new Integer (highestTableNum);
		} else {
			// Pull the first number out of unusedTableNums and recycle it.
			return ((Integer) unusedTableNumbers.remove(0));
		}
	}

	/**
	 * Flatten the current list of tables in use.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten ()  {
		XMLElement message = new XMLElement (Comm.TABLE_LIST);

		int [] tableNums = getTablesNumbers();
		for (int i = 0; i < tableNums.length; i++) {
			Table table = getTable(tableNums[i]);
			message.addChild (table.flatten());
		}

		return message;
	}

	/**
	 * Refresh observers - calls the setChanged() and notifyObservers ()
	 * methods in the Observable class.
	 */
	public void refreshObservers () {
		setChanged();
		notifyObservers();	 // notify any class which observes this class
	}
}
