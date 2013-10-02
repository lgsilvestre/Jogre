/*
 * JOGRE (Java Online Gaming Real-time Engine) - Webapp
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
package org.jogre.webapp.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jogre.webapp.IJogreWeb;

/**
 * Properties file for reading JOGRE web application properties.
 * 
 * This assumes there is a JOGRE_WEBAPP environment variable set before run.
 * 
 * It will then look under this folder for a "webapp.properties" file.
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class WebAppProperties implements IJogreWeb {
	
	// Singleton instance of web app properties.
	private static WebAppProperties instance = null;
	
	// fields
	private Properties properties;
	
	/**
	 * Private constructor - use getInstance() method to get singleton instance. 
	 */
	private WebAppProperties () throws IOException {
		String webAppDir = System.getProperty (ENV_JOGRE_WEBAPP);
		if (webAppDir == null)
			throw new IOException("\"JOGRE_WEBAPP\" system property is not set.  Ensure this is set to directory of \"webapp.properties\" and restart webserver");
		
        File propertiesFile = new File (webAppDir + "/" + PROPERTY_FILENAME);

        if (!propertiesFile.exists()) {
            throw new IOException("Web properties file defined using JOGRE_WEBAPP system property does not exist at location: " + propertiesFile.getAbsolutePath());
        }
        
        // Load properties
        this.properties = new Properties();
        FileInputStream propFile = new FileInputStream(propertiesFile);
        this.properties.load (propFile);
	}
	
	/**
	 * Singleton accessor to web app properties.
	 * 
	 * @return
	 */
	public static WebAppProperties getInstance () throws IOException {
		if (instance == null)
			instance = new WebAppProperties ();
		
		return instance;
	}
	
	/**
	 * Return property.
	 * 
	 * @param key
	 * @return
	 */
	public String get (String key) {		
		try {
			if (properties != null)
				return ((String)properties.get (key.trim())).trim();
		}
		catch (Exception mrEx) {}
		
		return null;
	}
	
	/**
	 * @param key
	 * @return
	 */
	public int getInt (String key) {
		return Integer.parseInt (get (key));
	}
	
	/**
	 * Return the database connection that the web application is pointing to.
	 * 
	 * @return
	 */
	public Properties getDatabaseProperties () {
		Properties dbProps = new Properties();
		dbProps.put ("driver",   properties.getProperty(PROP_DATABASE_DRIVER));
		dbProps.put ("url",      properties.getProperty(PROP_DATABASE_URL));
		dbProps.put ("username", properties.getProperty(PROP_DATABASE_USERNAME));
		dbProps.put ("password", properties.getProperty(PROP_DATABASE_PASSWORD));
		
		return dbProps;
	}
	
	/**
	 * Return true / false if this is a supported game.
	 * 
	 * @param gameKey
	 * @return
	 */
	public boolean isSupportedGame (String gameKey) {
		String supportedGames = get(PROP_SUPPORTED_GAMES);
		if (supportedGames != null)
			return (supportedGames.indexOf(gameKey) != -1);
		
		return false;
	}
	
	/**
	 * Return true / false if this is a new game or not.
	 * 
	 * @param gameKey
	 * @return
	 */
	public boolean isNewGame (String gameKey) {
		String newGames = get(PROP_NEW_GAMES_LIST);
		if (newGames != null)
			return (newGames.indexOf(gameKey) != -1);
		
		return false;
	}
	
	/**
	 * Return true / false if a language is supported or not.
	 * 
	 * @param lang
	 * @return
	 */
	public boolean isSupportedLang (String lang) {
		String supportedLangs = get(PROP_SUPPORTED_LANGS);
		if (supportedLangs != null)
			return (supportedLangs.indexOf(lang) != -1);
		return false;
	}
}