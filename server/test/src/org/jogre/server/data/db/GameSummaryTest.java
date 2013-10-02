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

import org.jogre.server.data.GameSummary;
import org.jogre.server.data.User;

/**
 * Jogre database test case for the GAME_SUMMARY table.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameSummaryTest extends JogreDatabaseTestCase {

	private static final String TEST_DATA = "test_data_game_summary.xml";
	private static final String TABLE = "GAME_SUMMARY";
	
	/**
	 * Test adding a game summary: - IDatabase.ST_ADD_GAME_SUMMARY
	 */
	public void testAddGameInfo () throws Exception {
		super.emptyTable (TABLE);
		assertEquals (0, dbunitConn.getRowCount(TABLE));
		
		// Add user
		GameSummary gameSummary = new GameSummary ("chess", "bob", 1200, 10, 5, 3, 2);
		
		// Add a game		
		iBatis.update(IDatabase.ST_ADD_GAME_SUMMARY, gameSummary);
		assertEquals (1, dbunitConn.getRowCount(TABLE));
		
		// Add another game	
		gameSummary.setGameKey("checkers");
		iBatis.update(IDatabase.ST_ADD_GAME_SUMMARY, gameSummary);
		assertEquals (2, dbunitConn.getRowCount(TABLE));
	}
	
	/**
	 * Test selecting game summary: - IDatabase.ST_SELECT_GAME_SUMMARY
	 */
	public void testSelectGameSummary () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (5, dbunitConn.getRowCount(TABLE));
			
		GameSummary param = new GameSummary ("chess", "bob");
		GameSummary gameSummary = (GameSummary)iBatis.getObject(IDatabase.ST_SELECT_GAME_SUMMARY, param);
		assertEquals (1240, gameSummary.getRating());
	}
	
	/**
	 * Test selecting all game summaries: - IDatabase.ST_SELECT_ALL_GAME_SUMMARYS
	 */
	public void testSelectAllGameSummary () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (5, dbunitConn.getRowCount(TABLE));
		
		// Try getting list from sql map
		List users = iBatis.getList(IDatabase.ST_SELECT_ALL_GAME_SUMMARYS);
		assertEquals (5, users.size());
		
		List users2 = iBatis.getList(IDatabase.ST_SELECT_ALL_GAME_SUMMARYS, new User("bob"));
		assertEquals (3, users2.size());
	}
	
	/**
	 * Test updating a game summary: - IDatabase.ST_UPDATE_GAME_SUMMARY
	 */
	public void testUpdateGameSummary () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (5, dbunitConn.getRowCount(TABLE));
		
		// Retrieve game info and change its history
		GameSummary paramGameSummary = (GameSummary)iBatis.getObject(IDatabase.ST_SELECT_GAME_SUMMARY, new GameSummary("chess", "dave"));
		paramGameSummary.setRating(1215);
		iBatis.update (IDatabase.ST_UPDATE_GAME_SUMMARY, paramGameSummary);
		
		// Check to see if user is updated
		GameSummary gameSummary = (GameSummary)iBatis.getObject(IDatabase.ST_SELECT_GAME_SUMMARY, new GameSummary("chess", "dave"));
		assertEquals (1215, gameSummary.getRating());	
	}
	
	/**
	 * Test deleting a user: - IDatabase.ST_DELETE_GAME_SUMMARY
	 */
	public void testDeleteGameSummary () throws Exception {
		insertFileIntoDb(TEST_DATA);		
		assertEquals (5, dbunitConn.getRowCount(TABLE));
		
		// Add user
		iBatis.update(IDatabase.ST_DELETE_GAME_SUMMARY, new GameSummary ("connect4", "bob"));		
		assertEquals (4, dbunitConn.getRowCount(TABLE));
	}
}