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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nanoxml.XMLElement;

import org.jogre.common.AbstractConnectionThread;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.ITransmittable;

/**
 * Test connection for connecting to a JogreServer.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class TestConnectionThread extends AbstractConnectionThread {

    private static final String SERVER = "localhost";
    private static final int    PORT   = 1790;

    private String username;

    private Socket socket;
    private BufferedReader in;
    private PrintStream out;

    private boolean loop = true;

    private List messageQueue;

    /**
     * @param username
     */
    public TestConnectionThread () throws UnknownHostException, IOException {
        super(null);
        setSocket (new Socket (SERVER, PORT));
        messageQueue = Collections.synchronizedList(new LinkedList());
    }

    /**
     * change "send (ITransmittable message)" method visibility to public.
     *
     * @param message
     */
    public void send (ITransmittable message) {
        super.send(message);
    }

    /**
     * Recieve a XML element back to the user.
     *
     * @return
     */
    public XMLElement receive () throws IOException {
        if (messageQueue.size() > 0) {
            return (XMLElement) messageQueue.remove(0);
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.jogre.common.AbstractConnectionThread#parse(nanoxml.XMLElement)
     */
    public void parse (XMLElement message) throws TransmissionException {
        messageQueue.add(message);
    }

    /* (non-Javadoc)
     * @see org.jogre.common.AbstractConnectionThread#cleanup()
     */
    public void cleanup() {

    }
}
