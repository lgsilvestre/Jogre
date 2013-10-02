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
package org.jogre.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

import org.jogre.common.comm.ITransmittable;
import org.jogre.common.util.JogreLogger;

/**
 * Abstract connection thread which is spawned with each client.  This extends a
 * thread and stores a Socket to the client, and a BufferedReader and PrintStream
 * which can read/write Strings to the user/server.  Also the username of the
 * client is stored in the username String.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class AbstractConnectionThread extends Thread {

	/** Logging */
	JogreLogger logger = new JogreLogger (this.getClass());

	/** Socket between the server and the user. */
	protected Socket socket;

	/** Buffered input. */
	protected BufferedReader in;

	/** PrintStream for the output. */
	private PrintStream out;

	/** Username of the client. */
	protected String username;

	/** When the boolean loop becomes false the Thread finishes. */
	protected boolean loop = true;

	/** All clients start initially with "connected" equal to false
	 *  (although they can still recieve/transfer logon information). */
	protected boolean connected = false;

	/**
	 * This abstract method must be overwritten by a child which extends this
	 * class.
	 *
	 * @param message       Communication as an XML object.
	 * @throws TransmissionException  This is thrown if there is a problem parsing the String.
	 */
	public abstract void parse (XMLElement message) throws TransmissionException;

	/**
	 * This method is called to properly clean up after a client.
	 */
	public abstract void cleanup ();

	/**
	 * Constructor for a connection which takes a Socket and sends up the input
	 * and output stream.
	 *
	 * @param socket   Socket connection to client / server.
	 */
	public AbstractConnectionThread (Socket socket) {
		try {
			logger.debug ("AbstractConnectionThread", "Creating new in/out streams.");

			setSocket (socket);
		}
		catch (IOException ioEx) {
			logger.error ("AbstractConnectionThread", "IO Exception.");
			logger.stacktrace (ioEx);
		}
	}

	/**
	 * @param socket
	 * @throws IOException
	 */
	protected void setSocket (Socket socket) throws IOException {
	    this.socket = socket;

	    if (socket != null) {
		    in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
		    out = new PrintStream (socket.getOutputStream());
		}
	}

	/**
	 * Run method - runs until an exception has occured or the loop variable
	 * becomes false.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run () {
		logger.debug("run", "Staring thread.");

		try {
			while (loop) {
				// listen for input from the user
				String inString = "";

				while (loop && inString!=null && inString.equals("")) {
					if (in == null) {
						cleanup ();
						return;
					}
					inString = in.readLine();
				}

				// Check input.
				if (in == null) {
					cleanup ();
					return;
				}

				// Ensure that the communication is XML (starts with a '<' character)
				// as any sort of client could send communication to the server.
				if (inString != null) {
					if (inString.startsWith("<")) {
	
						// Starts with an '<' so try and parse this XML
						XMLElement message = new XMLElement ();
	
						try {
						    message.parseString (inString);
	
						    // parse this element
							if (message != null)
							    parse (message);
						}
						catch (XMLParseException xmlParseEx) {
							logger.error ("run", "problem parsing: " + inString);
							logger.stacktrace (xmlParseEx);
						}
					}
				}
			}
		}
		catch (SocketException sEx) {
			logger.debug ("run", "Connection lost");
			logger.stacktrace (sEx);
		}
		catch (IOException ioEx) {
			logger.error ("run", "IO Exception: ");
			logger.stacktrace (ioEx);
		}
		catch (Exception genEx) {
			genEx.printStackTrace();
			logger.error ("run", "General Exception: ");
			logger.stacktrace (genEx);			
		}

		connected = false;
		cleanup ();
	}

	/**
	 * Stop the loop.
	 */
	public void stopLoop () {
		this.loop = false;
	}

	/**
	 * Set boolean to specify that this client has connected sucessfully.
	 */
	public void connect () {
		this.connected = true;
	}

	/**
	 * Send a ITransmittable object to the output stream (could be server or
	 * client).
	 *
	 * @param transObject
	 */
	protected void send (ITransmittable transObject) {
		// Retrieve XMLElement from the object and flatten to a String.
		String message = transObject.flatten().toString();

		// Send down the socket to the receiving end
		out.println (message);
	}

	/**
	 * Returns the username.
	 *
	 * @return  Username of client / server who created this thread.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set the username.
	 *
	 * @param username  Username of client / server who created this thread.
	 */
	public void setUsername (String username) {
		this.username = username;
	}
}
