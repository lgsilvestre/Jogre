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
package org.jogre.protocol;

import junit.framework.TestCase;
import nanoxml.XMLElement;

import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommGameConnect;
import org.jogre.common.comm.CommGameMessage;
import org.jogre.common.comm.ITransmittable;

/**
 * <p>JUnit test case for testing JOGRE communication is robust.
 * Ensure that a server is running in the background first of all.</p>
 *
 * <p>It would be nice in the future if the server could be run in here
 * but there seems to be thread syncronisation problems.</p>
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class ProtocolTest extends TestCase {

    /**
     * Constructor for TestProtocol.
     *
     * @param arg0
     */
    public ProtocolTest(String name) {
        super(name);
    }

    /**
     * Test method for testing clients logging onto a Jogre Server.
     */
    public void testLogon() throws Throwable {
    	// THIS IS WORK IN PROGRESS - GOING TO CHANGE VERY SOON
        // Create 2 test client connections
       /*TestConnectionThread t1 = null, t2 = null;

        ITransmittable request;		// Request object
        XMLElement response;		// Response object (as XML)

        // Create 2 new connections to the JogreServer
        t1 = new TestConnectionThread();		// bob
        t2 = new TestConnectionThread();		// dave

        // Create CommGameConnect for "bob" and send to server
        t1.send(new CommGameConnect("bob",  "chess"));
        t2.send(new CommGameConnect("dave", "chess"));

        // Receive and check responses from both clients
        do {
            response = t1.receive();
            //System.out.println("response: " + response);
            Thread.sleep(100);
        } while (response == null);
        assertTrue("Response not correct: " + response, expectedXML(response, Comm.GAME));

        do {
            response = t2.receive();
            //System.out.println("response: " + response);
            Thread.sleep(100);
        } while (response == null);
        assertTrue("Response not correct: " + response, expectedXML(response, Comm.GAME));

        assertTrue("Response not correct: " + response,
                    expectedXML(response, Comm.GAME_CONNECT,
                                new String[] {CommGameMessage.XML_ATT_USERNAME},
                                new String[] {"bob"}));
*/
    }

    /**
     * Return true / false to ensure expected XML matches the specified message.
     *
     * @param message     Message to check.
     * @param name        Name to check against message.
     * @param attNames    Attribute name array to check against message.
     * @param attValues   Attribute values array to check against message.
     * @param content     Content to check against the message.
     * @return            True if all information is correct / false otherwise.
     */
    private boolean expectedXML(XMLElement message, String name, String[] attNames, String[] attValues, String content) {
        // Check name is correct
        if (!message.getName().equals(name))
            return false;

        /*
        // Check number of attributes
        if (attNames == null && message.countAttributes() != 0)	 // attributes not defined
            return false;
        else if (message.countAttributes() != attNames.length)	 // attributes defined
            return false;
        */

        // Check

        return true;
    }

    /**
     * Return true / false to ensure expected XML matches the specified message.
     *
     * @param message     Message to check.
     * @param name        Name to check against message.
     * @param content     Content to check against the message.
     * @return            True if all information is correct / false otherwise.
     */
    private boolean expectedXML (XMLElement message, String name, String content) {
        return expectedXML (message, name, null, null, content);
    }

    /**
     * Return true / false to ensure expected XML matches the specified message.
     *
     * @param message     Message to check.
     * @param name        Name to check against message.
     * @param attNames    Attribute name array to check against message.
     * @param attValues   Attribute values array to check against message.
     * @return            True if all information is correct / false otherwise.
     */
    private boolean expectedXML (XMLElement message, String name, String [] attNames, String [] attValues) {
        return expectedXML (message, name, attNames, attValues, null);
    }

    /**
     * Return true / false to ensure expected XML matches the specified message.
     *
     * @param message     Message to check.
     * @param name        Name to check against message.
     * @param attNames    Attribute name array to check against message.
     * @param attValues   Attribute values array to check against message.
     * @return            True if all information is correct / false otherwise.
     */
    private boolean expectedXML (XMLElement message, String name) {
        return expectedXML (message, name, null, null, null);
    }

    /**
     * Main method for running test case.
     *
     * @param args
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run (ProtocolTest.class);
    }
}
