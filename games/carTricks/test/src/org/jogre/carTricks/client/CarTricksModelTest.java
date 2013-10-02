/*
 * JOGRE (Java Online Gaming Real-time Engine) - CarTricks
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.carTricks.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

import org.jogre.carTricks.common.CarTricksTrackDB;

/**
 * Test case for game model for the carTricks game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class CarTricksModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		CarTricksTrackDB testTrack = new CarTricksTrackDB ("data/track_Standard");
		CarTricksServerTestModel model = new CarTricksServerTestModel(3, testTrack, true);

		// ToDo: ought to add some tests actually moving some cars on the track...

		// Test saving & restoring model state
		saveRestoreTest (model);
	}

	/**
	 * Test saving the state of the model and restoring it in another model.
	 * This is what happens when a client connects in the middle of a game.
	 *
	 * This will flatten the source model provided and restore it into a new
	 * model and then compare the two.
	 */
	private void saveRestoreTest (CarTricksServerTestModel srcModel) throws Exception {
		CarTricksTrackDB theTrack = srcModel.getTrackDatabase();
		int numPlayers = srcModel.getNumPlayers();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		CarTricksClientTestModel model = new CarTricksClientTestModel (theTrack, numPlayers, true);
		model.setState(elm);

		// Verify stuff is the same.
		assertEquals (srcModel.numCarsInRace(), model.numCarsInRace());
		assertEquals (srcModel.getCarLocations(), model.getCarLocations());
		assertEquals (srcModel.getCarPositions(), model.getCarPositions());
		assertEquals (srcModel.isFirstTrick(), model.isFirstTrick());
		assertEquals (srcModel.getActivePlayerId(), model.getActivePlayerId());
		assertEquals (srcModel.getActiveCar(), model.getActiveCar());
		assertEquals (srcModel.getSpacesToMove(), model.getSpacesToMove());
		assertEquals (srcModel.getEmptyTrackSpaces(), model.getEmptyTrackSpaces());
		assertEquals (srcModel.getEventCardCanPlayThisTrick(), model.getEventCardCanPlayThisTrick());
		assertEquals (srcModel.getSomeEventPlayedThisTrick(), model.getSomeEventPlayedThisTrick());
		assertEquals (srcModel.getMinPlayedEvents(), model.getMinPlayedEvents());
		assertEquals (srcModel.getNoMoveReason(), model.getNoMoveReason());
		assertEquals (srcModel.getGameOverAfterNextCarMove(), model.getGameOverAfterNextCarMove());
		assertEquals (srcModel.getNextLeader(), model.getNextLeader());
		assertEquals (srcModel.getWreckPlayerId(), model.getWreckPlayerId());

		// Verify that the game phase is the same.
		assertEquals (srcModel.isWaitingForPlayers(), model.isWaitingForPlayers());
		assertEquals (srcModel.isSettingBid(), model.isSettingBid());
		assertEquals (srcModel.isSpectatorSettingBid(), model.isSpectatorSettingBid());
		assertEquals (srcModel.isSelectingCard(), model.isSelectingCard());
		assertEquals (srcModel.isMovingCar(), model.isMovingCar());
		assertEquals (srcModel.isGameOver(), model.isGameOver());

		// Verify that played cards are the same.
		for (int p = 0; p < numPlayers; p++) {
			assertEquals (srcModel.getPlayedCard(p), model.getPlayedCard(p));
			assertEquals (srcModel.getEventCardFlags(p), model.getEventCardFlags(p));
			assertEquals (srcModel.getNumPlayedEvents(p), model.getNumPlayedEvents(p));
		}
	}

	/**
	 * Compare two arrays of ints to determine if they are equal.
	 */
	private void assertEquals (int [] a, int [] b) throws Exception {
		assertEquals (a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals (a[i], b[i]);
		}
	}

	/**
	 * Compare two arrays of boolean to determine if they are equal.
	 */
	private void assertEquals (boolean [] a, boolean [] b) throws Exception {
		assertEquals (a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals (a[i], b[i]);
		}
	}
}
