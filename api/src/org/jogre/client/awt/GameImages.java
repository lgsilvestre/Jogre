/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.client.awt;

import java.awt.Image;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

/**
 * Contains a list of images for a particular game.  These images
 * should work on both applications and applets.
 *
 * Since beta 0.3 they are stored in a hash map for a nicer method 
 * of returning the images back.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class GameImages {

	private boolean finishedLoading = false;
	private HashMap imageHash = new HashMap ();

	private static GameImages instance = null;

	/**
	 * Constructor for the singleton GameImages.
     */
	private GameImages () {
        // Retrieve vector of image information from "game_labels.properties".
    	final Properties imageProps = GameProperties.getImageProperties();
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
            	Iterator it = imageProps.keySet().iterator();
                while (it.hasNext()) {
                	String key = (String)it.next();
                    GameImages.addImage(key, imageProps.getProperty(key));
                }
                GameImages.finishedLoaded();
                return null;
            }
        };
        worker.start();
	}
	
	/**
	 * Method to load the images (simply calls singleton constructor).
	 */
	public static void loadImages () {
		getInstance();		// call constructor
	}

	/**
	 * Return an instance of the GameImages class.
	 *
	 * @return
	 */
	public static synchronized GameImages getInstance () {
		if (instance == null) {
			instance = new GameImages ();
			return instance;
		}
		else
			return instance;
	}

    public Object clone() throws CloneNotSupportedException   {
     throw new CloneNotSupportedException();
    }

    /**
	 * Method to add images.
	 *
	 * @param images
	 */
	public static void addImage (String key, String path) {
		if (JogreUtils.isApplet())
	        getInstance().imageHash.put (key, getInstance().loadImageFromJar (path));
		else
			getInstance().imageHash.put (key, new ImageIcon (path));
	}

	/**
	 * Load image from the jar file.
	 *
	 * @param imageNum
	 * @return
	 */
	protected ImageIcon loadImageFromJar (String imagePath) {
	    try {
		    return new ImageIcon (getClass().getResource("/" + imagePath));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return a image from a specified index.
	 *
	 * @param index   Index of image to return.
	 * @return        Returns image or null if still loading images.
	 */
	public static Image getImage (int index) {
        ImageIcon imageIcon = getImageIcon(index);
        if (imageIcon != null) {
            return imageIcon.getImage();
        }
		return null;
	}
	
	/**
	 * Return a image from a specified index.
	 *
	 * @param index   Index of image to return.
	 * @return        Returns image or null if still loading images.
	 */
	public static Image getImage (String key) {
        ImageIcon imageIcon = getImageIcon(key);
        if (imageIcon != null) {
            return imageIcon.getImage();
        }
		return null;
	}

    /**
     * Return an image icon from a specified index.
     *
     * @param index   Index of image to return.
     * @return        Returns image or null if still loading images.
     */
    public static ImageIcon getImageIcon (int index) {
        return GameImages.getImageIcon(String.valueOf(index));
    }
    
    /**
     * Return an image from key in "game.properties" file.  
     * 
     * NOTE: There is no need to specify the "image." part of the image key.
     * 
     * @param key
     * @return
     */
    public static ImageIcon getImageIcon (String key) {
    	if (isFinishedLoading ()) {
            return (ImageIcon)getInstance().imageHash.get("image." + key);
        }
        else
            return null;
    }

	/**
	 * Inform if the images have finished loading.
	 *
	 * @return  Returns state of finishedLoading variable.
	 */
	public static boolean isFinishedLoading () {
		return getInstance().finishedLoading;
	}

	/**
	 * Set true that this class has finished loading.
	 */
	public static void finishedLoaded () {
		getInstance().finishedLoading = true;
	}
}
