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
package org.jogre.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Vector;

import org.jogre.common.Game;
import org.jogre.common.GameList;
import org.jogre.common.util.JogreLogger;
import org.jogre.server.controllers.ServerControllerList;

/**
 * This class loads up a game using reflection.  The classes
 * must be in the "jogre/server/games directory" either as classes
 * or as JAR files.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class GameLoader {

    /** Logging */
	JogreLogger logger = new JogreLogger (this.getClass());

    /** Link to the parser list. */
    private ServerControllerList parserList;

    /** Description of load up. */
    private StringBuffer verboseSB;
    
    private HashMap iconDataHash;

	/**
	 * Load the various games up.
	 *
	 * @param gameList    Game list which this class populates.
	 * @param parserList  Server parser list which this
	 *                    class will add to with each game found.
	 */
	public GameLoader (GameList gameList, ServerControllerList parserList) {
	    this.parserList = parserList;
	    this.verboseSB = new StringBuffer ();
	    this.iconDataHash = new HashMap ();

	    ServerProperties serverProperties = ServerProperties.getInstance();

	    Vector games = serverProperties.getCurrentlyHostedGames ();

	    // Check that at least 1 game exists
	    if (games != null) {
	        String curGameKey = null;
	        Game curGame = null;

            int numOfGames = games.size();
            URL [] urls = new URL [numOfGames];

            String gamesURL = "file://" + serverProperties.getJogreHomeDir() + "games/";

            for (int i = 0; i < numOfGames; i++) {
                try {
                    urls [i] = new URL (gamesURL + games.get(i) + "/classes/");
                } catch (MalformedURLException murlEx) {
                    murlEx.printStackTrace();
                }
            }
            URLClassLoader classLoader = new URLClassLoader (urls);

	        // Loop througth the various games
	        for (int i = 0; i < numOfGames; i++)
	        {
	        	// Read game id
	            curGameKey = (String)games.get (i);
	            int minNumOfPlayers = serverProperties.getMinPlayers (curGameKey);
	            int maxNumOfPlayers = serverProperties.getMaxPlayers (curGameKey);
	            curGame = new Game (curGameKey, minNumOfPlayers, maxNumOfPlayers);
	            gameList.addGame (curGame);

	            loadParser (classLoader, curGame);

	            // Load the icon for the game.
	            byte [] iconData = loadIconData(curGameKey);
	            if (iconData != null) {
	            	iconDataHash.put (curGameKey, iconData);
	            }

	            verboseSB.append (curGameKey + "\n");
	        }
	    }
	}
	
	/**
	 * Return the icon data hash.
	 * 
	 * @return
	 */
	public HashMap getIconDataHash () {
		return this.iconDataHash;
	}

	/**
	 * Load a game's icon data from it's directory.
	 *
	 * @param gameKey  The name of the game
	 */
	private byte [] loadIconData (String gameKey) {
	    byte [] theData = null;

	    // Create the base name of the icon file.
	    String baseName = ServerProperties.getInstance().getJogreHomeDir() +
            "games/" + gameKey + "/images/" + gameKey + "_icon";

        // Try to find .gif or .png icons
        File iconFile = new File (baseName + ".gif");
        if (!iconFile.canRead()) {
	        iconFile = new File (baseName + ".png");
	        if (!iconFile.canRead()) {
	            return null;
	        }
	    }

        // Read the data into a byte array
        FileInputStream inputStream = null;
        int fileSize = (int) iconFile.length();
        try {
	        inputStream = new FileInputStream(iconFile);
	        theData = new byte [fileSize];
	        int actualLength = inputStream.read(theData);
	        if (actualLength != fileSize) {
	            // If can't read all of the data, then pretend that we didn't
	            // read any of it.
	            theData = null;
	        }
        } catch (IOException e) {
        } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close();
	            } catch (IOException e) {
	            }
	        }
        }
        return theData;
    }

	/**
	 * Load a game parser.
	 *
	 * @param curGame
	 */
	private void loadParser (URLClassLoader classLoader, Game curGame) {
	    // Load for server controllers in the game directory
        File serverControllerDir = new File (
            ServerProperties.getInstance().getJogreHomeDir() +
            "games/" + curGame.getKey() +
            "/classes/org/jogre/" + curGame.getKey() + "/server");
        File [] files = serverControllerDir.listFiles();

        // Look for files which end in "ServerController.class".
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String curFileName = files [i].getName();

                if (curFileName.endsWith("ServerController.class")) {
                    curFileName = curFileName.substring(0, curFileName.indexOf(".class"));  // remove .class extention

            	    // Create location
                    String directory = "org.jogre." + curGame.getKey() + ".server.";
                    String controllerName = directory + curFileName;

                    // Load the controllers
                    Object serverController = loadServerController (classLoader, curGame.getKey(), controllerName);

                    // Add tableParser to parserList if not null
                    if (serverController != null) {
                        if (serverController instanceof ServerController) {
                            parserList.addServerController (curGame.getKey(), (ServerController)serverController);

                            curGame.setCustomGameProperties(((ServerController) serverController).getCustomGameProperties());

                            verboseSB.append ("\t[controller]\t");
                            return;
                        }
                        else {
                            logger.log ("Class instance created but of incorrect type - should be of type ServerController");
                        }
                    }
                }
            }
        }
        verboseSB.append ("\t\t\t");
	}

	/**
     * Load the server controller.
     *
	 * @param classLoader
	 * @param gameKey
	 * @param className
	 * @return
	 */
	private Object loadServerController (URLClassLoader classLoader, String gameKey, String className) {
	    try {
            //Class instance = Class.forName (className);
            Class instance = classLoader.loadClass(className);

            // Create constructor
            Class  [] classParams = new Class  [] {String.class};
            Object [] params      = new Object [] {gameKey};

            // Create a new constructor and return the presentation item
            Constructor constructor = instance.getConstructor (classParams);
            return constructor.newInstance (params);
        } catch (ClassNotFoundException e) {

        } catch (InstantiationException ie) {

        } catch (IllegalAccessException iae) {

        } catch (Exception e) {

        }

	    return null;
	}

	/**
	 * Return the String description ot the games loading.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
	    return verboseSB.toString ();
	}
}

