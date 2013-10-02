/*
 * JOGRE (Java Online Gaming Real-time Engine) - Generator
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
package org.jogre.gamegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jogre.common.FileUtils;

/**
 * Jogre game generator class for creating games quickly and easily 
 * to speed up game development of new games.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameGenerator {
	
	// Where template files are stored
	public static final String TEMPMLATE_LOCATION = "/org/jogre/gamegen/templates/";
	public static final String TEMPMLATE_EXT = ".tpl";
	
	private static final String FILENAME = "filename";
	
	// Declare fields
	private Map parameterValues;	
	private GameGeneratorProperties properties;
	
	/**
	 * <p>Constructor to Jogre Games generator which takes a number of command arguments.
	 * These command arguments must match what is in the parameters defined in
	 * the "generator.properties" file.</p>
	 * 
	 * <p>For example, if it contains parameters ...
	 * 
	 * <code><pre>
	 *     parameter.0=dir
	 *     parameter.1=game_id
	 *     parameter.2=author
	 *     parameter.3=version
	 * </pre></code>
	 * 
	 * ... then the command arguments for creating "bingo" should look like something like the 
	 * following ...</p>
	 * 
	 * <code><pre>
	 *     -dir=c:\jogre\games -game_id=bingo -author=Jimmy Jones -version=Beta 0.3
	 * </pre></code>
	 * 
	 * @param commandArgs
	 * @throws GameGeneratorException
	 * @throws IOException
	 */
	public GameGenerator (String [] args) throws IOException, GameGeneratorException  {
		// Set up fields		
		this.parameterValues = new HashMap ();
		this.properties = new GameGeneratorProperties ();
			
		// Parse command line arguments
		parseCommandLineArguments (args);
	}
	
	/**
	 * Generate game structure.
	 */
	public void generate () throws IOException, GameGeneratorException {
		// Make directories
		createDirectories ();
		
		// Create files
		createFiles ();
	}	

	/**
	 * Parse command line arguments into 
	 * 
	 * @param args
	 */
	private void parseCommandLineArguments (String [] args) throws GameGeneratorException {
		System.out.println ("Using Arguments...\n");
		
		// Parse the various arguments into  
	    if (args != null) {
			for (int i = 0; i < args.length; i++) {
				readArgument (args[i]);		             
			}
	    }

		// Ensure all mandatory arguments are read in
	    ArrayList params = properties.getParameters();
	    for (int i = 0; i < params.size(); i++) {
	    	String key   = (String)params.get(i);
	    	Object value = parameterValues.get(key);
	    	if (!properties.isOptional(key)) {
	    		if (value == null) 
	    			throw new GameGeneratorException ("Mantdatory option not supplied: -" + key);
	    	}
	    	else if (value == null) {	// optinal but still null
	    		// Get default value and use it instead
	    		Object defaultValue = properties.getDefaultValue(key);
	    		if (defaultValue != null) {
	    			parameterValues.put (key, (String)defaultValue);
	    		}
	    		else if (!key.equals (GameGeneratorProperties.DIR)) // Special optional attribute which can be null
	    			throw new GameGeneratorException ("No default value in properties file for optional parameter: -" + key);
	    	}
	    }
	}
	
	/**
	 * Read a single argument and do something with it.
	 * 
	 * @param argument
	 */
	private void readArgument (String argument) throws GameGeneratorException {
		// Read the parameter
		if (argument.startsWith("-") && argument.indexOf("-") != -1) {
			int pos      = argument.indexOf ("=");
			String key   = argument.substring (1, pos);
			String value = argument.substring (pos + 1);
			
			ArrayList params = properties.getParameters();
			if (params.contains(key)) {
				if (value != null && value.length() > 0) {
					System.out.println ("\t-" + key + "=" + value);
					parameterValues.put (key, value);
				}
				else
					throw new GameGeneratorException ("Invalid argument [ " + argument + " ]");
			}
			else
				throw new GameGeneratorException ("Invalid argument [ " + argument + " ]");
		} 
	}
	
	/**
	 * Create directories for new game.
	 */
	private void createDirectories () throws IOException {
		System.out.println ("\nGenerating directories...\n");
		
		String gameDir = getGamesDir ();
		
		// Create directories for new game
		ArrayList mkdirs = properties.getMkdirs ();
		
		for (int i = 0; i < mkdirs.size(); i++) {
			String mkdir = (String)mkdirs.get(i);
			
			// mkdirs contains %game_id%'s
			mkdir = GameGeneratorUtils.replace (mkdir, GameGeneratorProperties.GAME_ID, getParamValue(GameGeneratorProperties.GAME_ID));
			
			String mkdirFull = gameDir + mkdir;
			
			// Create directory
			File newDir = new File (mkdirFull);
			if (newDir.exists())
				System.out.println ("\t" + newDir.getAbsolutePath() + " (already exists)");
			else if (newDir.mkdirs()) 
				System.out.println ("\t" + newDir.getAbsolutePath());
			else 
				throw new IOException ("Couldn't make directory: " + newDir.getAbsolutePath());
		}
	}
	
	/**
	 * Create files. 
	 */
	private void createFiles () throws IOException, GameGeneratorException {
		System.out.println ("\nGenerating files...\n");
		
		ArrayList files = properties.getFiles();
		for (int i = 0; i < files.size(); i++) {
			String templateFilename = (String)files.get(i);
			if (templateFilename.endsWith(TEMPMLATE_EXT)) {
				System.out.print ("\t" + templateFilename + " ");
				System.out.flush();
				
				// Load file
				InputStream bufferedStream = this.getClass().getResourceAsStream (TEMPMLATE_LOCATION + templateFilename);
				InputStreamReader inputStreamReader = new InputStreamReader (bufferedStream);	
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	
				// Read first line of file.  This must contain the "location" property
				String firstLine = bufferedReader.readLine();
				if (!firstLine.startsWith(FILENAME + "="))
					throw new GameGeneratorException ("Template file [ " + templateFilename + " ] must contain \"location=X\" property");			
				
				String filename = firstLine.substring((FILENAME + "=").length());
				filename = GameGeneratorUtils.replace (filename, GameGeneratorProperties.GAME_ID, getParamValue(GameGeneratorProperties.GAME_ID));				
				String fullFilename = getGamesDir () + filename;
				
				// Create template file
				createFile (bufferedReader, fullFilename);
				
				// Close streams
				bufferedStream.close(); inputStreamReader.close(); bufferedReader.close();
			}
		}		
		
		System.out.println ("\nSuccess: " + files.size() + " files created!");
	}
	
	/**
	 * Create file using contents of the reader and store in correct place.
	 * 
	 * @param bufferedReader
	 * @param location2
	 */
	private void createFile (BufferedReader bufferedReader, String fullFilename) throws IOException {
		// Update filename
		fullFilename = GameGeneratorUtils.replaceAllCaseTypes (fullFilename, GameGeneratorProperties.GAME_ID, getParamValue(GameGeneratorProperties.GAME_ID));
		File createFile = new File (fullFilename);
		
		// Create output buffer
		BufferedWriter writer = new BufferedWriter (new FileWriter (createFile));
		boolean isLinux = fullFilename.endsWith(".sh"); 	// update if needs be in future
		
		String input = "";
		StringBuffer sb = new StringBuffer ();
		while (input != null) {						
			input = bufferedReader.readLine();
			if (input != null) {
				if (isLinux)
					sb.append(input + "\n");	// linux new line
				else
					sb.append(input + "\r\n");	// windows new line
			}
		}

		// Update file String using parameter values
		input = sb.toString();
		for (Iterator it = parameterValues.keySet().iterator(); it.hasNext(); ) {
			String param = (String)it.next(); 
			String value = (String)parameterValues.get(param);
			
			input = GameGeneratorUtils.replaceAllCaseTypes(input, param, value); 
		}
		
		writer.write (input);
		writer.close();
		System.out.println ("--> " + createFile.getAbsolutePath() + " [OK]");
	}

	/**
	 * Return the game dir.  Check if set as argument, other wise return
	 * execution directory.
	 * 
	 * @return
	 */
	private String getGamesDir () {
		// See if user has set default dir
		String dir = (String)parameterValues.get (GameGeneratorProperties.DIR);
		
		// If not use current dir.
		if (dir == null) 
			dir = FileUtils.getExecutionDir ();
		else if (dir != null && !dir.endsWith(File.separator))
			dir = dir + File.separator;
		
		return dir;
	}
	
	/**
	 * Return parameter value
	 * 
	 * @param key
	 * @return
	 */
	private String getParamValue (String key) {
		Object obj = parameterValues.get (key);
		if (obj != null)
			return (String)obj;
		return null;
	}
	
	/**
	 * Proper usage of Generator.
	 */
	public static void usage () throws IOException {
		GameGeneratorProperties properties = new GameGeneratorProperties ();
		System.out.println ("Usage:    java -jar gamegen.jar [parameters]\n");
		
		ArrayList params = properties.getParameters();
		
		// Display mandatory paramaters
		System.out.println ("Mandatory parameters include: -");		
		for (int i = 0; i < params.size(); i++) {
			String param = (String)params.get(i);
			if (!properties.isOptional(param)) 
				System.out.println ("    -" + properties.getDescription(param));
		}
		
		// Display optional parameters
		System.out.println ("\nOptional parameters include: -");
		for (int i = 0; i < params.size(); i++) {
			String param = (String)params.get(i);
			if (properties.isOptional(param)) 
				System.out.println ("    -" + properties.getDescription(param));
		}
	}
	
	/**
	 * Main method to Game Generator.
	 * 
	 * @param args
	 */
	public static void main (String [] args) {
		try {
			System.out.println ("-----------------------------------------------------------------------");
			System.out.println ("Jogre Game Generator");
			System.out.println ("-----------------------------------------------------------------------\n");
			
			// Read arguments 
			if (args.length == 0)
				usage ();
			else {		
				GameGenerator gen = new GameGenerator (args);
				gen.generate();
			}
		}
		catch (GameGeneratorException genEx) {
			System.out.println ("\nGenerator error: " + genEx.getMessage());
		}
		catch (IOException ioEx) {
			System.out.println ("\nIO error: " + ioEx.getMessage());
			ioEx.printStackTrace();
		}				
		catch (Exception genEx) {
			System.out.println ("\nGeneral error");
			genEx.printStackTrace();
		}
	}
}