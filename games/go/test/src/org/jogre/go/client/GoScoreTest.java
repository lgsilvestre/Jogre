/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
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
package org.jogre.go.client;

import nanoxml.XMLElement;
import junit.framework.TestCase;

/**
 * Test case which test the score object.
 * 
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoScoreTest extends TestCase {

	/**
	 * Test basic functionality of score object.
	 */
	public void testBasic () throws Exception {
		double komi = 6.5;
		int scoreMethod = GoScore.SCORE_METHOD_AREA;
		int [] areas = {10, 15};
		int [] territorys = {9,  13};		
		int [] prisoners = {2,  3};
		
		// Create go score object
		GoScore goScore = new GoScore (
			komi, scoreMethod, areas, territorys, prisoners);
		XMLElement elm = goScore.flatten();
		GoScore goScoreFromElm = new GoScore (elm);
		
		// Check integrity of object created from element
		assertTrue(goScore.getKomi() == goScoreFromElm.getKomi()); 	// !! NOTE !! - IS THERE JUNIT ISSUE? - CANT USE assertEquals (double, double) ???
		assertEquals(goScore.getScoringMethod(), goScoreFromElm.getScoringMethod());
		
		for (int i = 0; i < 2; i++) {
			assertEquals(goScore.getArea(i),      goScoreFromElm.getArea(i));					
			assertEquals(goScore.getTerritory(i), goScoreFromElm.getTerritory(i));
			assertEquals(goScore.getPrisoner(i),  goScoreFromElm.getPrisoner(i));
		}
	}
}
