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

/**
 * Jogre database test case for the GAME_INFO table.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameInfoTest extends JogreDatabaseTestCase {

	private static final String TEST_DATA = "test_data_game_info.xml";
	private static final String TABLE = "GAME_INFO";
	
	/**
	 * Test adding a game_info: - IDatabase.ST_ADD_GAME_INFO
	 */
	public void testAddGameInfo () throws Exception {
		super.emptyTable (TABLE);
		assertEquals (0, dbunitConn.getRowCount(TABLE));
		
		// Create game info
		GameInfo gameInfo = new GameInfo ("chess", "dave bob", "0 1",
			JogreUtils.readDate("10/04/2008-10:42:30", IJogre.DATE_FORMAT_FULL),
			JogreUtils.readDate("10/04/2008-11:12:04", IJogre.DATE_FORMAT_FULL),
			"default score", "default history");
		
		// Add a game		
		iBatis.update(IDatabase.ST_ADD_GAME_INFO, gameInfo);
		assertEquals (1, dbunitConn.getRowCount(TABLE));
		
		// Add another game		
		iBatis.update(IDatabase.ST_ADD_GAME_INFO, gameInfo);
		assertEquals (2, dbunitConn.getRowCount(TABLE));
	}
	
	/**
	 * Test selecting game_info: - IDatabase.ST_SELECT_GAME_INFO
	 */
	public void testSelectGameInfo () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
			
		GameInfo gameInfo = (GameInfo)iBatis.getObject(IDatabase.ST_SELECT_GAME_INFO, new GameInfo(3));
		assertEquals ("checkers", gameInfo.getGameKey());
	}
	
	/**
	 * Test selecting all game infos: - IDatabase.ST_SELECT_ALL_GAME_INFOS
	 */
	public void testSelectAllGameInfo () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Try getting list from sql map
		List gameInfos = iBatis.getList(IDatabase.ST_SELECT_ALL_GAME_INFOS);
		assertEquals (4, gameInfos.size());
	}
	
	/**
	 * Test updating a game info: - IDatabase.ST_UPDATE_GAME_INFO
	 */
	public void testUpdateGameInfo () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Retrieve game info and change its history
		GameInfo paramGameInfo = (GameInfo)iBatis.getObject(IDatabase.ST_SELECT_GAME_INFO, new GameInfo(3));
		paramGameInfo.setGameHistory("new game history");
		iBatis.update (IDatabase.ST_UPDATE_GAME_INFO, paramGameInfo);
		
		// Check to see if game info is updated
		GameInfo gameInfo = (GameInfo)iBatis.getObject(IDatabase.ST_SELECT_GAME_INFO, new GameInfo(3));
		assertEquals ("new game history", gameInfo.getGameHistory());	
	}
	
	/**
	 * Test deleting a game info: - IDatabase.ST_DELETE_GAME_INFO
	 */
	public void testDeleteGameInfo () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
		
		// Delete game info
		iBatis.update(IDatabase.ST_DELETE_GAME_INFO, new GameInfo (1));
		
		assertEquals (3, dbunitConn.getRowCount(TABLE));
	}
}