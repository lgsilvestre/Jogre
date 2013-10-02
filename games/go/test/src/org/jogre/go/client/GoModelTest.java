package org.jogre.go.client;

import nanoxml.XMLElement;
import junit.framework.TestCase;

/**
 * Test case which test the score object.
 * 
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GoModelTest extends TestCase {

	/**
	 * Test basic functionality of score object.
	 */
	public void testBasic () throws Exception {
		
		// Create go model and create random data
		GoModel goModel = new GoModel ();
		for (int i = 0; i < goModel.getTotalCellCount(); i++) {
			int randomVal = (int)(Math.random() * 3) - 1;
			goModel.setData (i, randomVal);
		}
		
		// Flatten and reconstrut from XML
		XMLElement elm = goModel.flatten();		
		GoModel goModelFromElm = new GoModel ();
		goModelFromElm.setState(elm);
		
		// Check integrity of object created from element
		assertEquals (goModel.getTotalCellCount(), goModelFromElm.getTotalCellCount());
		assertTrue (goModel.getKomi() == goModelFromElm.getKomi());
		 
		for (int i = 0; i < goModel.getTotalCellCount(); i++) {
			assertEquals ("i = " + i, goModel.getData(i), goModelFromElm.getData(i));
			assertTrue (goModel.getPrevData(i) == goModelFromElm.getPrevData(i));
		}
	}
}