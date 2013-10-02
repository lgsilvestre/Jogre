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
package org.jogre.server.data.db;

import java.util.List;

import org.jogre.server.data.User;

/**
 * Jogre database test case for the USER table.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class UserTest extends JogreDatabaseTestCase {
	
	private static final String TEST_DATA = "test_data_user.xml";
	private static final String TABLE = "USER";
	
	/**
	 * Test adding a user: - IDatabase.ST_ADD_USER
	 */
	public void testAddUser () throws Exception {
		super.emptyTable (TABLE);
		assertEquals (0, dbunitConn.getRowCount(TABLE));
		
		// Add user
		User paramUser = new User ();
		paramUser.setUsername("test_username");
		paramUser.setPassword("test_password");
		paramUser.setSecurityQuestion(1);
		paramUser.setSecurityAnswer("dogs");
		paramUser.setYearOfBirth("1957");
		paramUser.setEmail("dude@test.com");
		paramUser.setReceiveNewsletter(true);
		iBatis.update(IDatabase.ST_ADD_USER, paramUser);
		
		// Check user exists
		assertEquals (1, dbunitConn.getRowCount(TABLE));		
	}

	/**
	 * Test selecting a user: - IDatabase.ST_SELECT_USER
	 */
	public void testSelectUser () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Add user
		User resultUser = (User)iBatis.getObject(IDatabase.ST_SELECT_USER, new User ("bob"));
		assertEquals ("bob",           resultUser.getUsername());
		assertEquals ("bob123",        resultUser.getPassword());
		assertEquals (1,               resultUser.getSecurityQuestion());
		assertEquals ("pigs",          resultUser.getSecurityAnswer());
		assertEquals ("1974",          resultUser.getYearOfBirth());
		assertEquals ("bob@bob.com",   resultUser.getEmail());
		assertEquals (true,            resultUser.isReceiveNewsletter());
		
		User paramUser = new User ("bob");
		paramUser.setPassword("invalid");
		assertNull (iBatis.getObject(IDatabase.ST_SELECT_USER, paramUser));	
	}
	
	/**
	 * Test selecting all user: - IDatabase.ST_SELECT_ALL_USERS
	 */
	public void testSelectAllUsers () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Try getting list from sql map
		List users = iBatis.getList(IDatabase.ST_SELECT_ALL_USERS);
		assertEquals (4, users.size());
	}
	
	/**
	 * Test updating a user: - IDatabase.ST_UPDATE_USER
	 */
	public void testUpdateUser () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Add user
		User paramUser = new User ("dave");
		paramUser.setPassword("test_password");
		paramUser.setSecurityQuestion(5);
		paramUser.setSecurityAnswer("test_answer");
		paramUser.setYearOfBirth("1999");
		paramUser.setEmail("test@test.com");
		paramUser.setReceiveNewsletter(false);
		iBatis.update(IDatabase.ST_UPDATE_USER, paramUser);
		
		// Check to see if user is updated
		User resultUser = (User)iBatis.getObject(IDatabase.ST_SELECT_USER, new User ("dave"));
		assertEquals ("test_password", resultUser.getPassword());	
		assertEquals (5,               resultUser.getSecurityQuestion());
		assertEquals ("test_answer",   resultUser.getSecurityAnswer());
		assertEquals ("1999",          resultUser.getYearOfBirth());
		assertEquals ("test@test.com", resultUser.getEmail());
		assertEquals (false,           resultUser.isReceiveNewsletter());
	}
	
	/**
	 * Test deleting a user: - IDatabase.ST_DELETE_USER
	 */
	public void testDeleteUser () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Add user
		iBatis.update(IDatabase.ST_DELETE_USER, new User ("dave"));
		
		assertEquals (3, dbunitConn.getRowCount(TABLE));
	}
}
