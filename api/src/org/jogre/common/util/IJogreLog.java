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

/**
 * Interface which compliments the JogreLog class.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public interface IJogreLog {

	/** No logging will be created. */
	public static final int NONE = 0;

	/** Level 1 priority (high). */
	public static final int ERROR = 1;

	/** Level 2 priority (medium). */
	public static final int INFO = 2;

	/** Level 3 priority (low). */
	public static final int DEBUG = 3;

	/** String values of priorities */
	public static final String [] PRIORITY_STRS = {"NONE", "ERROR", "INFO ", "DEBUG"};

	// Declare some default values

	/** Default priority (if problems reading from the properties file. */
	public static final int DEFAULT_CONSOLE_PRIORITY = INFO;

	/** Default value for file output. */
	public static final int DEFAULT_FILE_PRIORITY = DEBUG;

	/** Default for showing a priority. */
	public static final boolean DEFAULT_SHOW_PRIORITY = false;
}
