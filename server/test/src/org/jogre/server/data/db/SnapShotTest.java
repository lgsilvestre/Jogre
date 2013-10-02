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

import org.jogre.common.IJogre;
import org.jogre.common.util.JogreUtils;
import org.jogre.server.data.GameInfo;
import org.jogre.server.data.SnapShot;

/**
 * Jogre database test case for the SNAP_SHOT table.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class SnapShotTest extends JogreDatabaseTestCase {

	private static final String TEST_DATA = "test_data_snapshot.xml";
	private static final String TABLE = "SNAP_SHOT";
	
	/**
	 * Test adding a snapshot: - IDatabase.ST_ADD_SNAP_SHOT
	 */
	public void testAddSnapShot () throws Exception {
		super.emptyTable (TABLE);
		assertEquals (0, dbunitConn.getRowCount(TABLE));
		
		// Add snapshot
		SnapShot snapshot = new SnapShot ("chess", 4, 5);		
		iBatis.update(IDatabase.ST_ADD_SNAP_SHOT, snapshot);
		assertEquals (1, dbunitConn.getRowCount(TABLE));
		
		// Add another game
		snapshot.setGameKey("checkers");
		iBatis.update(IDatabase.ST_ADD_SNAP_SHOT, snapshot);
		assertEquals (2, dbunitConn.getRowCount(TABLE));
	}
	
	/**
	 * Test selecting snapshot: - IDatabase.ST_SELECT_SNAP_SHOT
	 */
	public void testSelectSnapShot () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (22, dbunitConn.getRowCount(TABLE));
			
		SnapShot snapshot = (SnapShot)iBatis.getObject(IDatabase.ST_SELECT_SNAP_SHOT, new SnapShot("chess"));
		assertEquals (3, snapshot.getNumOfUsers());
		assertEquals (9, snapshot.getNumOfTables());		
	}
	
	/**
	 * Test selecting all snapshots: - IDatabase.ST_SELECT_ALL_SNAP_SHOTS
	 */
	public void testSelectAllSnapShot () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (22, dbunitConn.getRowCount(TABLE));
		
		// Try getting list from sql map
		List users = iBatis.getList(IDatabase.ST_SELECT_ALL_SNAP_SHOTS);
		assertEquals (22, users.size());
		
		// Ordered ascending
		SnapShot param = new SnapShot ();
		param.setOrderBy("GAME_KEY ASC");
		List usersSortedAsc = iBatis.getList(IDatabase.ST_SELECT_ALL_SNAP_SHOTS, param);
		assertEquals ("abstrac", ((SnapShot)usersSortedAsc.get(0)).getGameKey());		
		
		// Ordered desscending
		param.setOrderBy("GAME_KEY DESC");
		List usersSortedDesc = iBatis.getList(IDatabase.ST_SELECT_ALL_SNAP_SHOTS, param);		
		assertEquals ("tictactoe", ((SnapShot)usersSortedDesc.get(0)).getGameKey());
	}
	
	/**
	 * Test updating a snapshot: - IDatabase.ST_UPDATE_SNAP_SHOT
	 */
	public void testUpdateSnapShot () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (22, dbunitConn.getRowCount(TABLE));
		
		// Retrieve game info and change its history
		SnapShot snapshot = new SnapShot ("checkers");
		snapshot.setNumOfTables(4);
		iBatis.update (IDatabase.ST_UPDATE_SNAP_SHOT, snapshot);
		
		// Check to see if user is updated
		snapshot = (SnapShot)iBatis.getObject(IDatabase.ST_SELECT_SNAP_SHOT, new SnapShot ("checkers"));
		assertEquals (4, snapshot.getNumOfTables());	
	}
	
	/**
	 * Test deleting a snapshot: - IDatabase.ST_DELETE_SNAP_SHOT
	 */
	public void testDeleteSnapShot () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (22, dbunitConn.getRowCount(TABLE));
		
		// Add user
		iBatis.update(IDatabase.ST_DELETE_SNAP_SHOT, new SnapShot ("connect4"));
		
		assertEquals (21, dbunitConn.getRowCount(TABLE));
	}
	
	/**
	 * Test deleting all snapshots: - IDatabase.ST_DELETE_SNAP_SHOT
	 */
	public void testDeleteAllSnapShot() throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (22, dbunitConn.getRowCount(TABLE));
		
		// Add user
		iBatis.update(IDatabase.ST_DELETE_ALL_SNAP_SHOT);
		
		assertEquals (0, dbunitConn.getRowCount(TABLE));
	}
}