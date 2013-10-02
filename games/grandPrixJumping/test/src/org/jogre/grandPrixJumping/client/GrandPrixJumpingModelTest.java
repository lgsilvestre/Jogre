/*
 * JOGRE (Java Online Gaming Real-time Engine) - GrandPrixJumping
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
package org.jogre.grandPrixJumping.client;

import junit.framework.TestCase;

import nanoxml.XMLElement;

import java.util.Vector;

import org.jogre.grandPrixJumping.server.JumpingServerModel;
import org.jogre.grandPrixJumping.common.JumpingCoreModel;
import org.jogre.grandPrixJumping.common.JumpingCard;
import org.jogre.grandPrixJumping.common.JumpingFence;


/**
 * Test case for game model for the grandPrixJumping game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class GrandPrixJumpingModelTest extends TestCase {

	/**
	 * Test the model.
	 */
	public void testBasic () throws Exception {
		// Make a model to test.
		JumpingServerModel model = new JumpingServerModel(false, false, "abc");

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
	private void saveRestoreTest (JumpingServerModel srcModel) throws Exception {
		boolean openHands = srcModel.isOpenHands();
		boolean allowEdits = srcModel.allowEdits();
		String initialTrack = srcModel.createCodeForTrack();

		// Flatten & reconstruct from the message.
		XMLElement elm = srcModel.flatten();
		JumpingClientModel model =
		           new JumpingClientModel (openHands, allowEdits, initialTrack);
		model.setState(elm);

		// Verify that player stuff is the same
		for (int p = 0; p < 2; p++) {
			assertEquals (srcModel.playerStillEditingTrack(p), model.playerStillEditingTrack(p));
			assertEquals (srcModel.getHorseLocation(p), model.getHorseLocation(p));
			assertEquals (srcModel.getQuarterFaults(p), model.getQuarterFaults(p));
			assertEquals (srcModel.getPlayableHand(p).size(), model.getPlayableHand(p).size());
			assertHandVectorEquals (srcModel.getImmediateHand(p), model.getImmediateHand(p));
			assertEquals (srcModel.getMaxHandSize(p), model.getMaxHandSize(p));
		}

		// Verify that the sorting areas area the same
		assertHandVectorEquals (srcModel.getSortCards(0), model.getSortCards(0));
		assertHandVectorEquals (srcModel.getSortCards(1), model.getSortCards(1));
		assertEquals (srcModel.getLastMovedSortCard(), model.getLastMovedSortCard());

		// Verify that the board layout is the same
		assertEquals (initialTrack, model.createCodeForTrack());
		for (int s = 0; s < JumpingCoreModel.TRACK_SPACES; s++) {
			assertEquals (srcModel.getWaterJumpFaults(s), model.getWaterJumpFaults(s));
			assertEquals (srcModel.getFenceAt(s), model.getFenceAt(s));
		}

		assertFenceVectorEquals (srcModel.getAllFences(), model.getAllFences());

		// Verify misc. stuff is the same
		assertEquals (srcModel.getDualRiderCard(0), model.getDualRiderCard(0));
		assertEquals (srcModel.getDualRiderCard(1), model.getDualRiderCard(1));
		assertEquals (srcModel.getGamePhase(), model.getGamePhase());
		assertEquals (srcModel.getTurnNumber(), model.getTurnNumber());
		assertEquals (srcModel.getSorterSeatNum(), model.getSorterSeatNum());
	}

	/**
	 * Compare two hands of cards to determine if they are equal.
	 * For this test, they must contain the same cards in the same order.
	 */
	private void assertHandVectorEquals (Vector h1, Vector h2) throws Exception {
		assertEquals (h1.size(), h2.size());

		for (int i=0; i<h1.size(); i++) {
			assertEquals ((JumpingCard) h1.get(i), (JumpingCard) h2.get(i));
		}
	}

	/**
	 * Compare two Grand Prix Jumping cards to determine if they are equal.
	 */
	private void assertEquals (JumpingCard c1, JumpingCard c2) throws Exception {
		if (c1 == null) {
			assertTrue (c2 == null);
		} else {
			assertTrue (c1.equals(c2));
		}
	}

	/**
	 * Compare two vectors of fences to determine if they are equal.
	 * For this test, they must contain the same fences in the same order.
	 */
	private void assertFenceVectorEquals (Vector h1, Vector h2) throws Exception {
		assertEquals (h1.size(), h2.size());

		for (int i=0; i<h1.size(); i++) {
			assertEquals ((JumpingFence) h1.get(i), (JumpingFence) h2.get(i));
		}
	}

	/**
	 * Compare two Grand Prix Jumping fences to determine if they are equal.
	 */
	private void assertEquals (JumpingFence f1, JumpingFence f2) throws Exception {
		if (f1 == null) {
			assertTrue (f2 == null);
		} else {
			assertEquals (f1.location(), f2.location());
			assertEquals (f1.height(),   f2.height());
			assertEquals (f1.type(),     f2.type());
		}
	}
}
