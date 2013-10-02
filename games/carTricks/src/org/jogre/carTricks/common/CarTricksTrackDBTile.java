/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.carTricks.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.awt.Image;
import javax.swing.ImageIcon;

import org.jogre.common.util.JogreUtils;

// Structure to hold a tile for a track database
public class CarTricksTrackDBTile {

	// Info about this tile
	private int id;
	private String filename;
	private int off_x, off_y;

	// The image of this tile
	private ImageIcon theImageIcon = null;
	private Image theImage = null;

	// Location of the tile data
	private String baseDirectory;

	/**
	 * Constructor for a new tile
	 *
	 * @param	id				The tile ID
	 * @param	filename		The filename of the tile image
	 * @param	off_x, off_y	The (x,y) offsets for the tile
	 */
	public CarTricksTrackDBTile(int id, String baseDirectory, String filename, int off_x, int off_y) {
		this.id = id;
		this.filename = filename;
		this.off_x = off_x;
		this.off_y = off_y;
		this.baseDirectory = baseDirectory;
	}

	/**
	 * Accessor functions for the various fields
	 */
	public int getId() {return id;}
	public int getXOff() {return off_x;}
	public int getYOff() {return off_y;}
	public String getFilename() {return filename;}
	public ImageIcon getImageIcon() {return theImageIcon;}
	public Image getImage() {return theImage;}
	public int getTileWidth() {return theImage.getWidth(null);}
	public int getTileHeight() {return theImage.getHeight(null);}

	/**
	 * Load the imageIcon & Image into memory.
	 *
	 * @param 	baseDirectory	The directory that contains the file
	 */
	public boolean loadImage() {
		// If it's already loaded, then just return true.
		if (theImage != null) {
			return true;
		}
		// Create the file name to load
		String fullFileName = baseDirectory + File.separator + this.filename;

		// Load the file
		if (JogreUtils.isApplet()) {
			// Load from .jar file
			try {
				theImageIcon = new ImageIcon (getClass().getResource("/" + fullFileName));
				theImage = theImageIcon.getImage();
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			// Load from the file system
			File theFile = new File(fullFileName);

			if (theFile.exists()) {
				// Try to load it
				theImageIcon = new ImageIcon (fullFileName);
				theImage = theImageIcon.getImage();
				return true;
			} else {
				// The file doesn't exist
				return false;
			}
		}
	}

	/**
	 * Read the data in from the file and return the raw data
	 */
	public byte [] getRawData() {
		// Create the file name to load
		String fullFileName = baseDirectory + File.separator + this.filename;
		File theFile = new File(fullFileName);
		FileInputStream inputStream = null;
		byte [] theData = new byte [1];

		try {
			inputStream = new FileInputStream(theFile);
			theData = new byte [(int) theFile.length()];
			int actualLength = inputStream.read(theData);
			if (actualLength != theFile.length()) {
				// If can't read all of the data, then pretend that we didn't
				// read any of it.
				theData = null;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			return theData;
		}
	}

	/**
	 * Create the image given an array of data.
	 *
	 * @param imageData		The data of the image
	 * @param saveFile		The file to save the data into
	 */
	public void setData(byte [] imageData) {
		theImageIcon = new ImageIcon(imageData);
		theImage = theImageIcon.getImage();
	}

	/**
	 * Save the given data into a file
	 *
	 * @param dirName		The name of the directory to create the file in
	 * @param data			The data to write into the file
	 */
	 public void saveDataToFile(String dirName, byte [] data) {
	 	// If there is no directory, then just return
	 	if ((dirName == null) || JogreUtils.isApplet()) {
			return;
		}

		String fullFileName = dirName + File.separator + this.filename;
		File theFile = new File(fullFileName);
		FileOutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(theFile);
			outputStream.write(data);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
