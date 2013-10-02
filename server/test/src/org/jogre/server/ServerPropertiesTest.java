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
package org.jogre.server;

import junit.framework.TestCase;

/**
 * Test case for the server properties class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ServerPropertiesTest extends TestCase {
	
	private ServerProperties serverProps;
	
	/**
	 * Set up server properties.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		ServerProperties.setUpFromFile();
		serverProps = ServerProperties.getInstance();
	}
	
	/**
	 * Test configuration for server.
	 */
	public void testConfigurationServer () throws Exception {
		serverProps.setServerPort(1234); assertEquals (1234, serverProps.getServerPort());
		serverProps.setMaxNumOfTables(7); assertEquals (7, serverProps.getMaxNumOfTables());
		serverProps.setMaxNumOfUsers(9); assertEquals (9, serverProps.getMaxNumOfUsers());
		serverProps.setMaxNumOfTablesPerUser(4); assertEquals (4, serverProps.getMaxNumOfTablesPerUser());
		serverProps.setUserValidation("password"); assertEquals ("password", serverProps.getUserValidation());
	}
	
	/**
	 * Test configuration for administrator.
	 * 
	 * @throws Exception
	 */
	public void testConfigurationAdmin () throws Exception {
		serverProps.setAdminUsername("adminUsername"); assertEquals("adminUsername", serverProps.getAdminUsername());		
		serverProps.setAdminPassword("adminPassword"); assertEquals("adminPassword", serverProps.getAdminPassword());		
		serverProps.setReceiveMessages (false); assertEquals (false, serverProps.isReceiveMessages());
		serverProps.setReceiveMessages (true);  assertEquals (true, serverProps.isReceiveMessages());
	}
	
	/**
	 * Test the supported games section of the server properties.
	 * 
	 * @throws Exception
	 */
	public void testSupportedGames () throws Exception {
		assertNotNull (serverProps.getSupportedGames ());
		assertNotNull (serverProps.getCurrentlyHostedGames());
		assertEquals ("chess", serverProps.getGameElm("chess").attributeValue("id"));
		assertNotNull (serverProps.getELOElm("checkers"));
		serverProps.addELOElm("abstrac"); assertNotNull (serverProps.getELOElm("abstrac"));
		serverProps.deleteELOElm("abstrac"); assertNull (serverProps.getELOElm("abstrac"));		
		serverProps.setMinPlayers("connect4", 5); assertEquals (5, serverProps.getMinPlayers("connect4"));
		serverProps.setMaxPlayers("camelot", 3); assertEquals (3, serverProps.getMaxPlayers("camelot"));
		serverProps.setGameHosted("dots", false); assertEquals (false, serverProps.isGameHosted("dots"));
		serverProps.setGameHosted("dots", true); assertEquals (true, serverProps.isGameHosted("dots"));
		serverProps.addNewCustomElm("chess"); assertNotNull (serverProps.getCustomElms("chess"));
		serverProps.deleteCustomElm("carTricks", "dataRootDir"); assertEquals (0, serverProps.getCustomElms("carTricks").size());
		serverProps.addELOElm("hex"); assertNotNull (serverProps.getELOElm("hex")); 
		serverProps.setStartRating("hex", 1234); assertEquals (1234, serverProps.getStartRating("hex"));
		serverProps.setKFactor("hex", "0-2099=31,2100-2399=23,2490-3000=15"); assertEquals ("0-2099=31,2100-2399=23,2490-3000=15", serverProps.getKFactor("hex"));		
	}
		
	/**
	 * Test the supported games section of the server properties.
	 * 
	 * @throws Exception
	 */
	public void testData () throws Exception {
		serverProps.setCurrentServerData("database"); assertEquals ("database", serverProps.getCurrentServerData());
		serverProps.setCurrentDatabaseConnection("jogre_test"); assertEquals ("jogre_test", serverProps.getCurrentDatabaseConnection());
		assertNotNull (serverProps.getDatabaseElement ());
		
		serverProps.addDatabaseConnElm("jogre_test"); assertNotNull (serverProps.getConnectionElm ("jogre_test"));
		serverProps.setConnectionDriver ("jogre_test", "driver_test"); assertEquals ("driver_test", serverProps.getConnectionDriver("jogre_test"));
		serverProps.setConnectionURL("jogre_test", "url_test"); assertEquals ("url_test", serverProps.getConnectionURL("jogre_test"));
		serverProps.setConnectionUsername("jogre_test", "username_test"); assertEquals ("username_test", serverProps.getConnectionUsername("jogre_test"));
		serverProps.setConnectionPassword("jogre_test", "password_test"); assertEquals ("password_test", serverProps.getConnectionPassword("jogre_test"));
		serverProps.setXMLLocation("xml_test"); assertEquals ("xml_test", serverProps.getXMLLocation());
		assertNotNull (serverProps.getCurrentDatabaseConnElm ());
		assertNotNull (serverProps.getDatabaseType());
		assertTrue (serverProps.getConnectionIDs ().size() > 0);
		assertNotNull (serverProps.getDBConnURL());
		assertNotNull (serverProps.getDBDriver());
		assertNotNull (serverProps.getDBUsername());
		assertNotNull (serverProps.getDBPassword());
	}
}