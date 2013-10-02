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

import java.io.File;

/**
 * Bundle of useful file operations such as creating directories if they dont
 * exist etc.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class FileUtils {

	/**
	 * Helper method for quickly creating a directory.
	 *
	 * @param directory   Directory to create.
	 */
	public static void createDirectory(String directory) {
	    File logDirectory = new File (directory);
	    if (!logDirectory.isDirectory())      // creates new directory if it doesn't exist
	      logDirectory.mkdir();
	}

}
