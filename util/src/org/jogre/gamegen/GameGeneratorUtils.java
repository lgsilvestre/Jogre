/*
 * JOGRE (Java Online Gaming Real-time Engine) - Generator
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.gamegen;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.io.File;

/**
 * Bunch of useful methods used in generator.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameGeneratorUtils {
		
	/**
	 * <p>Replace text using a search parameter which is surrouned by percentages % 
	 * and replace with value. Parameters will be searched and replaced in 3 stages.</p>  
	 * 
	 * <p>
	 * <ol>
	 *   <li>Search input String where search is supplied value (1st letter lowercase) e.g. %author%</li>
	 *   <li>Search input String where search is lowercase e.g. %author%</li>
	 *   <li>Search input String where search is UPPERCASE e.g. %AUTHOR%</li>
	 *   <li>Search input String where search is 1st letter Uppercase e.g. %Author%</li>
	 * </ol>
	 * 
	 * <p>Each time a search is found it is replaced with replace in the correct case.</p>
	 * 
	 * <p>For example if method parameters are as follows:</p>
	 * <ol>
	 *   <li><b>input</b> = "%Author% and %author% shouted "MY NAME IS %AUTHOR%"!!!"</li>
	 *   <li><b>search</b> = "author"</li>
	 *   <li><b>replace</b> = "bob"</li>
	 * </ol>
	 * </p>
	 * 
	 * <p>The output would be: "Bob and bob shouted "MY NAME IS BOB"!!!"</p>  
	 * 
	 * <p>This method is used extensively when generating source code.</p>
	 * 
	 * @param input     Input strings containing params surrounded by percentages e.g. %id%
	 * @param search    Parameter to search for (note this starts with lower case letter).
	 * @param replace   Value to change (note this starts with lower case letter).
	 * @return          New replaced string in supplied case, lowercase, upper and 1st letter case.
	 */
	public static String replaceAllCaseTypes (String input, String search, String replace) {
		// Create searches
		String searchNormalCase  = "%" + search + "%";
		String searchLowerCase   = "%" + search.toLowerCase() + "%";
		String searchUpperCase   = "%" + search.toUpperCase() + "%";		
		String searchFirstLetter = "%" + search.substring(0, 1).toUpperCase() + 
		                           search.substring(1) + "%";
		
		// Create replaces
		String replaceNormalCase  = replace;
		String replaceLowerCase   = replace.toLowerCase();
		String replaceUpperCase   = replace.toUpperCase();		
		String replaceFirstLetter = replace.substring(0, 1).toUpperCase() + 
		                            replace.substring(1);
		
		// Replace String
		String replacedString = input;
		replacedString = replacedString.replaceAll (searchNormalCase,  replaceNormalCase);
		replacedString = replacedString.replaceAll (searchLowerCase,   replaceLowerCase);
		replacedString = replacedString.replaceAll (searchUpperCase,   replaceUpperCase);
		replacedString = replacedString.replaceAll (searchFirstLetter, replaceFirstLetter);
		
		return replacedString;
	}
	
	/**
	 * Simple search and replace.  The search String has percentages at start and end.
	 * 
	 * @param input    Input text to search e.g. "public class %Game_id {}" 
	 * @param search   Text to search for which gets wrapped in percentages e.g. "game_id" => "%game_id%"
	 * @param replace  Text to replace it with e.g. "chess".
	 * @return         New String
	 */
	public static String replace (String input, String search, String replace) {
		return input.replaceAll("%" + search + "%", replace);
	}
	
	/**
	 * Return the location of a centred component on screen.
	 * 
	 * @param window       Window e.g. frame that component is called from.
	 * @param screenSize   Screen size.
	 * @return             Dimension contain location.
	 */
	public static Point getCentredLocation (Window window, Dimension componentSize) {
		Dimension screenSize = window.getToolkit().getScreenSize();
		Point p = new Point (
			(int)(screenSize.getWidth() / 2 - componentSize.getWidth() / 2), 
			(int)(screenSize.getHeight() / 2 - componentSize.getHeight() / 2));
		
		return p;
	}
}