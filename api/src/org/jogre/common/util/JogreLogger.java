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
package org.jogre.common.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>Custom logging was created for JOGRE so that is as flexible and as
 * lightweight as possible (e.g. log4j is too large at 345kb). This logging
 * stores a timestamp, followed by the class and then the message itself.</p>
 *
 * <p>To use this class you simply create a new instance at the top of a class
 * such as:</p>
 *
 * <p><code>JogreLogger logger = new JogreLog (this.getClass());</code></p>
 *
 * <p>To use this class in source code use the log (String message), error
 * (String method, String message), info (String method, String message) and
 * debug (String method, String message) methods. The error method should be
 * used when there is simply an error (used for speed), logInfo for logging
 * information (on a stable system) and logDebug (slower) for debugging an
 * application (logs everything).</p>
 *
 * <p>To see the priority of debug output at run time by setting the following
 * key/value properties in a "game.properties" file. All of these properties
 * are optional and if they don't exist then their default values are assumed
 * (which are in bold).</p>
 * <p>
 *   <ul>
 *     <li><code>log.priority.console</code> - must be equal to <i>one</i> of the following:</li>
 *     <ul>
 *       <li><code>1</code> - error (fast, but only logs errors.</li>
 *       <li><b><code>2</code> - info</b> - (medium speend, logs information strings).</li>
 *       <li><code>3</code> - debug - (slow, logs everything.</li>
 *     </ul>
 *     <li><code>log.priority.file</code> - must be equal to <i>one</i> of the following:</li>
 *     <ul>
 *       <li><code>1</code> - error (fast, but only logs errors.</li>
 *       <li><code>2</code> - info - (medium speend, logs information strings).</li>
 *       <li><b><code>3</code> - debug</b> - (slow, logs everything.</li>
 *     </ul>
 *     <li><code>log.show.priority</code> - <code>true</code> or <b><code>false</code></b> - show priority in log outputs.</li>
 *   </ul>
 * </p>
 *
 * <p>Logs which are saved by file are saved in the following location -
 * "logs/yyyy_MM/log_dd_MMM_yyyy.txt" where y = year, d = day and M = month e.g.
 * "logs/2004_04/log_29_April_2004.txt". This logger is not anywhere near as
 * efficient as others but this will be investigated soon.</p>
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreLogger implements IJogreLog {

	// declare keys are which are stored in the resources strings
	private static final String KEY_LOG_CONSOLE_PRIORITY = "log.priority.console";
	private static final String KEY_LOG_FILE_PRIORITY = "log.priority.file";
	private static final String KEY_LOG_SHOW_PRIORITY = "log.show.priority";

	// name of the log directory
	private static final String LOG_DIRECTORY = "log";

	// dont log constant.
	private static final String DONT_LOG = "-1";

	/** Name of this class. */
	protected String className;

	/** Priority of console logging. */
	protected int consolePriority;

	/** Priority of file logging. */
	protected int filePriority;

	/** If this is true then show the priority of the log as a String. */
	protected boolean showPriority;

	// Date formatters (declare once for performance)
	private static final String DATE_FORMAT_DIR  = "yyyy_MM";
	private static final String DATE_FORMAT_FILE = "dd_MMM_yyyy";
	private static final String DATE_FORMAT_LOG  = "yyyy/MM/dd hh:mm:ss:SSS";
	private SimpleDateFormat dateFormatDir  = null;
	private SimpleDateFormat dateFormatFile = null;
	private SimpleDateFormat dateFormatLog  = null;

	/**
	 * Constructor which takes the class of the logged Class.
	 * @param loggedClass
	 */
	public JogreLogger (Class loggedClass) {
		// Populate the class Name
		String fullClassName = loggedClass.getName();
		this.className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

		// Initilise simple date formatters
		this.dateFormatDir  = new SimpleDateFormat (DATE_FORMAT_DIR);
		this.dateFormatFile = new SimpleDateFormat (DATE_FORMAT_FILE);
		this.dateFormatLog  = new SimpleDateFormat (DATE_FORMAT_LOG);

		// Set up booleans
		initilise ();
	}

	/**
	 * Log an error message.
	 *
	 * @param method   Method in a class.
	 * @param message  Message to log.
	 */
	public void error (String method, String message) {
		log (ERROR, method, message);
	}

	/**
	 * Log an information message.
	 *
	 * @param method   Method in a class.
	 * @param message  Message to log.
	 *
	 */
	public void info (String method, String message) {
		log (INFO, method, message);
	}

	/**
	 * Log a debug message.
	 *
	 * @param method   Method in a class.
	 * @param message  Message to log.
	 */
	public void debug (String method, String message) {
		log (DEBUG, method, message);
	}

	/**
	 * Simple log which will go in at INFO level.
	 *
	 * @param message  Message to log
	 */
	public void log (String message) {
		log (INFO, DONT_LOG, message);
	}

	/**
	 * Logs a stacktrace.
	 *
	 * @param e  Exception object.
	 */
	public void stacktrace (Exception e) {
	    error ("Exception", e.getMessage());
	}

	/**
	 * Intilise the priority level and booleans of where to output the logs.
	 */
	private void initilise () {

		// Set defaults
		consolePriority = DEFAULT_CONSOLE_PRIORITY;
		filePriority    = NONE; // DEFAULT_FILE_PRIORITY; shutting this off for applets until better solution
		showPriority    = DEFAULT_SHOW_PRIORITY;

		// read in priorities for console and file logging
		/*  TODO - Change this as there is a seperate server and API now.
		GameProperties properties = GameProperties.getInstance();

		if (properties != null) {
			consolePriority = properties.getInt (KEY_LOG_CONSOLE_PRIORITY, DEFAULT_CONSOLE_PRIORITY);
			filePriority    = properties.getInt (KEY_LOG_FILE_PRIORITY, DEFAULT_FILE_PRIORITY);
			showPriority	= properties.getBoolean (KEY_LOG_SHOW_PRIORITY, DEFAULT_SHOW_PRIORITY);
		}*/

		// Create to see if top level "log" directory is created.
//		FileUtils.createDirectory (LOG_DIRECTORY);

	}

	/**
	 * Method which performs the debug.
	 *
	 * @param logPriority Priority of the log (should be between 1 and 3)
	 * @param method   Used to show class.method () in log. If this is equal
	 *                 to DONT_LOG then this isnt displayed.
	 * @param message  Message to log.
	 */
	private void log (int logPriority, String method, String message) {

		// Check priority
		// Create log message from priority, time stamp, classs & message
		Calendar calender = Calendar.getInstance();
		Date date         = calender.getTime();

		// Create log Strings
		String timeStamp  = dateFormatLog.format (date);
		String classMethodStr = "";
		String priorityStr = "";

		if (!method.equals (DONT_LOG))
			classMethodStr = className + "." +  method + "(): ";

		// Create the priority string if required
		if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals (DONT_LOG))
			priorityStr = PRIORITY_STRS [logPriority] + " ";

		String logMessage =
			"[" + timeStamp + "] " +    // timestamp
			priorityStr +				// error e.g. ERROR INFO DEBUG
			classMethodStr + 			// class and method
			message;					// actual message

		if (logPriority <= consolePriority)
			writeToConsole (logMessage);
		if (logPriority <= filePriority)
			writeToFile (logMessage, date);
	}

	/**
	 * Log to the console.
	 *
	 * @param logMessage   Log message which is going to the console
	 */
	private void writeToConsole (String logMessage) {
		System.out.println (logMessage);
	}

	/**
	 * Log to the console.
	 *
	 * @param logMessage    Message to log.
	 * @param date			Date reference for working out directory names etc.
	 */
	private void writeToFile (String logMessage, Date date) {
		// Create to see if top level "log" directory is created.
		FileUtils.createDirectory (LOG_DIRECTORY);

	    // Create the name of the directory and file
		char PS             = File.separatorChar;	 // path seperater
		String logDirectory = dateFormatDir.format (date);
		String logFilename  = "log_" + dateFormatFile.format (date) + ".txt";

		// Create actual log file
		String fullFileName = LOG_DIRECTORY + PS + logDirectory + PS + logFilename;
		File logFile = new File (fullFileName);

	    // Create the log directory if it doesn't exist
		FileUtils.createDirectory (LOG_DIRECTORY + PS + logDirectory);

		try {
			BufferedWriter fileout = null;            // declare

			if (logFile.exists()) {
				fileout = new BufferedWriter(new FileWriter(fullFileName, true));   // true = append
				fileout.newLine();
			}
			else                            // else create the file
				fileout = new BufferedWriter(new FileWriter(fullFileName));

			// write text to file.
			fileout.write (logMessage);
			fileout.close ();
		}
		catch (IOException ioe) {
		    // if error don't worry (do nothing)
		}
	}
}
