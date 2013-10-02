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
package org.jogre.common.comm;

import junit.framework.TestCase;

/**
 * Collection of simple test on Comm objects.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommTest extends TestCase {
	
	/**
	 * Test CommAdminClientData comm object - Test 1.
	 * 
	 * @throws Exception
	 */
	public void testCommAdminClientData1 () throws Exception { 
		String [][] data = {{"bob", "bob123"}, {"sharon", "sharon123"}, {"dave", "dave123"}};
		CommAdminClientData comm1 = new CommAdminClientData (data, "users");
		String str1 = comm1.toString();
		CommAdminClientData comm2 = new CommAdminClientData (comm1.flatten());
		String str2 = comm2.toString();
		assertEquals (str1, str2);		// test flatten methods are same
	}
	
	/**
	 * Test CommAdminClientData comm object - Test 2.
	 * 
	 * @throws Exception
	 */
	public void testCommAdminClientData2 () throws Exception { 
		String [] reqData = {"jimmy", "jimmy123"};
		CommAdminClientData comm1 = new CommAdminClientData (reqData, "users", CommAdminClientData.NEW);
		String str1 = comm1.toString();
		CommAdminClientData comm2 = new CommAdminClientData (comm1.flatten());
		String str2 = comm2.toString();
		assertEquals (str1, str2);		// test flatten methods are same
	}
}
