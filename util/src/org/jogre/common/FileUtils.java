/*
 * JOGRE (Java Online Gaming Real-time Engine) - Util
 * Copyright (C) 2004 - 2007   Bob Marks (marksie531@yahoo.com)
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bundle of useful file operations such as creating directories if they dont
 * exist etc.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class FileUtils {
	
	private static final String DEFAULT_LANG = "en";		// use properties

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

	/**
	 * Return the current execution directory.
	 * 
	 * @return
	 */
	public static String getExecutionDir () {
		String dir = new File (".").getAbsoluteFile().getAbsolutePath();
		dir = dir.substring(0, dir.length() - 1);
		return dir;
	}
	
	/**
	 * Return list of files which match an extension.
	 * 
	 * @param dir
	 * @param ext
	 * @return
	 */
	public static List getFiles (String dir, String ext) {
		List matchedFiles = new ArrayList ();
		
		File fileDir = new File (dir);
		if (fileDir.exists()) {
			String [] files = fileDir.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(ext))
					matchedFiles.add(files[i]);
			}
		}
		return matchedFiles;	
	}
	
	/**
	 * Read a file to a String object.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 *
	public static String readFile (File file) throws IOException {
		StringBuffer sb = new StringBuffer ();
		if (file.exists () && file.isFile()) {
			BufferedReader reader = new BufferedReader (new FileReader (file));
			
			// Read file into string buffer
			String str;
			while ((str = reader.readLine()) != null)
				sb.append (str + "\n");
		}
		
		return sb.toString();
	}*/
	/*
	public static Map readFile (File file) throws IOException {
		StringBuffer sb = new StringBuffer ();
		if (file.exists () && file.isFile()) {
			BufferedReader reader = new BufferedReader (new FileReader (file));
			
			// Read file into string buffer
			String str;
			while ((str = reader.readLine()) != null)
				sb.append (str + "\n");
		}
		
		return sb.toString();
	}*/
}