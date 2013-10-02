/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
 * http//jogre.sourceforge.org
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
package org.jogre.carTricks.client;

import java.util.Vector;
import java.util.ListIterator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogrePanel;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import org.jogre.carTricks.common.CarTricksTrackDB;
import org.jogre.carTricks.common.CarTricksTrackDBTile;
import org.jogre.carTricks.common.CarTricksPath;
import org.jogre.carTricks.common.CommCarTricksTrackTileData;

// Car Tricks visual track component (view of the model)
public class CarTricksTrackComponent extends JogreComponent {

	// We use 2 layers for drawing the active path
	private static final int NUM_PATH_LAYERS = 2;

	// Link to the model & graphics
	private CarTricksClientModel model;
	private CarTricksGraphics CT_graphics;

	// The image for the track
	private Image trackImage;
	private int trackWidth, trackHeight;

	// Indicates if the track image has been drawn yet or not.
	private boolean trackDrawn;

	// The image for the path markers (used to indicate spaces to move the car to)
	private ImageIcon pathMarkersImageIcon;
	private Image pathMarkersImage;
	private int pathMarkersWidth, pathMarkersHeight;

	// The track database (this holds the information about the x,y coordinates
	// for each space on the track).
	private CarTricksTrackDB trackDatabase = null;

	// Colors to use
	private boolean [] pathLayerEnable = new boolean [NUM_PATH_LAYERS];
	private boolean [] pathLayerUseCarColor= new boolean [NUM_PATH_LAYERS];
	private BasicStroke [] pathLayerStroke= new BasicStroke [NUM_PATH_LAYERS];
	private Color [] pathLayerColor= new Color [NUM_PATH_LAYERS];

	// The currently selected path
	private CarTricksPath selectedPath;

	// Determine if the active path should be outlined in black or not.
	private boolean outlineActivePath;

	/**
	 * Constructor which creates the hand component
	 *
	 * @param model					The game model
	 * @param trackDatabase			The track database for this game
	 */
	public CarTricksTrackComponent (CarTricksClientModel model,
									CarTricksTrackDB trackDatabase) {

		// link to model, graphics and track data base
		this.model = model;
		this.CT_graphics = CarTricksGraphics.getInstance();

		selectedPath = null;
		trackDrawn = false;

		// Load the pictures of the track markers
		pathMarkersImageIcon = GameImages.getImageIcon(CarTricksImages.PATH_MARKERS);
		pathMarkersImage = pathMarkersImageIcon.getImage();
		pathMarkersWidth = pathMarkersImageIcon.getIconWidth() / 7;
		pathMarkersHeight = pathMarkersImageIcon.getIconHeight();

		// Read the settings from the properties file for how to draw the active path.
		GameProperties props = GameProperties.getInstance();
		readPathLayerSettings(props);

		addDatabase(trackDatabase);
	}

	/**
	 * Attach the track database to the track component.
	 *
	 * @param	trackDataBase	The database to attach to the component.

	 */
	public void addDatabase(CarTricksTrackDB trackDatabase) {
		// Link to the database
		this.trackDatabase = trackDatabase;

		// Setup the picture of the track
		if (trackDatabase != null) {
			// Get the dimensions from the database to create the track image
			int [] dim = trackDatabase.getImageDimensions();
			trackWidth = dim[0];
			trackHeight = dim[1];
		} else {
			trackImage = GameImages.getImage(CarTricksImages.LOADING_PICT);
			trackWidth = trackImage.getWidth(null);
			trackHeight = trackImage.getHeight(null);
		}

		// Set the dimensions of the track component.
		Dimension dim = new Dimension (trackWidth, trackHeight);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Determine if we have all of the tile pieces needed to draw the track.
	 *
	 * @return	If all tile pieces are available, then return null.
	 *			If at least one tile piece is not known, then return the name of
	 *			the tile piece needed.
	 */
	public String checkForAllTiles() {
		// If there is no track database, then we don't have tile pieces to check...
		if (trackDatabase == null) {
			return null;
		}

		CarTricksTrackDBTile [] tiles = trackDatabase.getTiles();
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i].loadImage() == false) {
				return tiles[i].getFilename();
			}
		}

		// We've got images for all tiles...
		return null;
	}

	/**
	 * Attempt to draw the offscreen image of the track.
	 *
	 * @return	true : if the offscreen track is drawn and ready to use
	 *			false : if the offscreen track is not drawn.
	 */
	private boolean attemptToDrawOffscreenTrack() {
		// If the track has already been drawn, then nothing to do.
		if (trackDrawn) {
			return true;
		}

		if (trackDatabase == null) {
			return false;
		}

		if (checkForAllTiles() == null) {
			// Yea! We can now create the off-screen Image and draw the track into it

			trackImage = createImage(trackWidth, trackHeight);
			int numSpaces = trackDatabase.getNumSpaces();
			int next_layer = 0;
			Graphics g = trackImage.getGraphics();

			// First cover the background with the background tile
			CarTricksTrackDBTile t = trackDatabase.getBackgroundTile();
			if (t != null) {
				Image tileImage = t.getImage();
				int w = tileImage.getWidth(null);
				int h = tileImage.getHeight(null);

				int h_num = (trackWidth / w) + 1;
				int v_num = (trackHeight / h) + 1;
				for (int i = 0; i < h_num; i++) {
					for (int j = 0; j < v_num; j++) {
						g.drawImage(tileImage,
									i*w, j*h,				// dx1, dy1
									(i+1)*w, (j+1)*h,		// dx2, dy2
									0, 0,					// sx1, sx2
									w, h,					// sx2, sy2
									null);
					}
				}
			}

			// Now, draw all of the rest of the tiles of the track
			do {
				int curr_layer = next_layer;
				next_layer = 10000;
				for (int i = 0; i < numSpaces; i++) {
					int layer = trackDatabase.getTileLayerForSpace(i);
					if (layer == curr_layer) {
						// Time to draw this tile
						t = trackDatabase.getTileForSpace(i);
						int [] screen_info = trackDatabase.getGraphicalArrayForSpace(i);
						int x1 = screen_info[CarTricksTrackDB.GRAPH_X_COORD] - t.getXOff();
						int y1 = screen_info[CarTricksTrackDB.GRAPH_Y_COORD] - t.getYOff();
						Image tileImage = t.getImage();
						int w = tileImage.getWidth(null);
						int h = tileImage.getHeight(null);

						g.drawImage(tileImage,
									x1, y1,						// dx1, dy1
									x1 + w, y1 + h,				// dx2, dy2
									0, 0,						// sx1, sx2
									w, h,						// sx2, sy2
									null);
					} else if ((layer > curr_layer) && (layer < next_layer)) {
						// We have a new next layer to draw
						next_layer = layer;
					}
				}
			} while (next_layer < 10000);

			// Finally, draw a 2-pixel border around the whole thing
			g.setColor(new Color (0,0,0));
			g.drawRect(0, 0, trackWidth-1, trackHeight -1);
			g.drawRect(1, 1, trackWidth-3, trackHeight -3);

			trackDrawn = true;
		}

		return trackDrawn;
	}

	/**
	 * Read the settings for how to draw the active path from the game.properties
	 * file and set internal variables appropriately.
	 *
	 * @param	props		The game properties object
	 */
	private void readPathLayerSettings(GameProperties props) {
		for (int i = 0; i < NUM_PATH_LAYERS; i++) {
			// Assume the layer is enabled and a fixed color.
			// We will adjust this assumption if we find otherwise.
			pathLayerEnable[i] = true;
			pathLayerUseCarColor[i] = false;

			// Get the width of the line
			String propertyBaseName = "path.layer." + i;
			int width = props.getInt(propertyBaseName + ".width", 0);
			if (width <= 0) {
				pathLayerEnable[i] = false;
				pathLayerStroke[i] = null;
			} else {
				pathLayerStroke[i] = new BasicStroke(width);
			}

			// Get the color of the line
			String layerColorString = props.get(propertyBaseName + ".color", "none");
			// Note: I'm not using JogreUtils.getColour() here because that will print an
			// error message if the color isn't a r,g,b triplet, and I allow other text
			// as well...
			pathLayerColor[i] = CarTricksGraphics.carTricksGetColor(layerColorString);

			if (pathLayerColor[i] == null) {
				// Couldn't parse the color as a fixed triple, so check for special cases
				if ("car".equals(layerColorString)) {
					// This layer should be the same color as the car
					pathLayerUseCarColor[i] = true;
				} else {
					// We can't parse the color, so turn the layer off
					pathLayerEnable[i] = false;
				}
			}
		}
	}

	/**
	 * Return the current selected path
	 */
	public CarTricksPath getSelectedPath() {
		return selectedPath;
	}

	/**
	 * Unselect the current selected path
	 */
	public void unselectPath() {
		selectedPath = null;
	}

	/**
	 * Given (x,y) graphical coordinates, select the path that ends closest to
	 * that point.  This point must be within threshhold pixels to be selected,
	 * otherwise no path will be selected.
	 *
	 * @param	(mx,my)		The point to use to select a path
	 * @param	threshhold	The maximum distance that a point can be from the
	 *						space and still be considered close.
	 * @return	true if a new path was selected
	 *			false if the same path is still selected
	 */
	public boolean setMousePoint(int mx, int my, int threshhold) {
		// To save computation, we calculate distance squared, so we need to
		// square the threshhold as well.  And we initialize the current
		// minimum threshhold to that number so that spaces farther away
		// are automatically excluded.
		int curr_min_dist = (threshhold * threshhold) + 1;
		CarTricksPath newPath = null;

		// Scan all of the paths, determining the distance from the point
		// to the terminal of the path.
		Vector allPaths = model.getAllPaths();
		if (allPaths != null) {
			ListIterator iter = allPaths.listIterator();
			while (iter.hasNext()) {
				// Get the path
				CarTricksPath path = (CarTricksPath) iter.next();

				// Look up the location (dx, dy) coordinates of that space
				int [] graph_info = trackDatabase.getGraphicalArrayForSpace(path.getTerminal());
				int dx = graph_info[CarTricksTrackDB.GRAPH_X_COORD];
				int dy = graph_info[CarTricksTrackDB.GRAPH_Y_COORD];

				// Determine the distance from (mx,my) to (dx,dy)
				int dist_squared = (mx-dx)*(mx-dx) + (my-dy)*(my-dy);

				// If this is closer than the current minimum, then remember it
				if (dist_squared < curr_min_dist) {
					newPath = path;
					curr_min_dist = dist_squared;
				}
			}
		}

		if (newPath != selectedPath) {
			// We have a new selectedPath!
			selectedPath = newPath;
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {
		// If the active car can land on it's own location, then we need to draw it's
		// terminal after the cars are drawn.  So, we get the location of the
		// active car to check for that.
		int activeCar = model.getActiveCar();
		int activeCarLoc = (activeCar >= 0)
								? activeCarLoc = model.getCarLocations()[activeCar]
								: -1;
		int needExtraTerminal = -1;

		// If we can't draw the offscreen track image yet, then just draw the "downloading" image
		if (!attemptToDrawOffscreenTrack()) {
			// Center the image on the screen
			int x = (getBounds().width - trackImage.getWidth(null)) / 2;
			int y = (getBounds().height - trackImage.getHeight(null)) / 2;
			g.drawImage(trackImage, x, y, null);
			return;
		}

		// Draw the track background
		g.drawImage(trackImage,
					0, 0,							//	x, y,
					null);							// observer

		// If there are any paths to show, then put the terminal marker at their ends.
		Vector allPaths = model.getAllPaths();
		if (allPaths != null) {
			ListIterator iter = allPaths.listIterator();
			while (iter.hasNext()) {
				// Get the path
				CarTricksPath path = (CarTricksPath) iter.next();

				// Draw the marker at the terminal of that path
				paintMarker(g, path.getTerminal(), -1);

				if (path.getTerminal() == activeCarLoc) {
					needExtraTerminal = activeCarLoc;
				}
			}
		}

		// If there is a selected path, then draw the path that connects them.
		// RAW Note: Maybe the array of points should be created when a path is selected and
		// then this just goes through those points.  That would also allow for things like
		// offsetting some points for when the path loops back on itself...
		if (selectedPath != null) {
			Color carColor = CT_graphics.getCarColor(activeCar);

			// Start on the space where the active car currently is
			int [] p1_info = null;
			int [] p2_info = trackDatabase.getGraphicalArrayForSpace(selectedPath.getLoc(0));

			for (int i=1; i < selectedPath.pathLength(); i++) {
				// Get the (x,y) positions for the next position in the path
				int [] p3_info = trackDatabase.getGraphicalArrayForSpace(selectedPath.getLoc(i));

				if (pathLayerEnable[0]) {
					// Draw layer 0 line from p2 to p3
					drawCarTricksLine(
						g,
						pathLayerUseCarColor[0] ? carColor : pathLayerColor[0],
						pathLayerStroke[0],
						p2_info, p3_info);
				}

				if ((p1_info != null) && pathLayerEnable[1]) {
					// Draw layer 1 line from p1 to p2
					drawCarTricksLine(
						g,
						pathLayerUseCarColor[1] ? carColor : pathLayerColor[1],
						pathLayerStroke[1],
						p1_info, p2_info);
				}

				// Advance the points.
				p1_info = p2_info;
				p2_info = p3_info;
			}

			if (pathLayerEnable[1]) {
				// Draw final layer 1 line from p1 to p2
				drawCarTricksLine(
					g,
					pathLayerUseCarColor[1] ? carColor : pathLayerColor[1],
					pathLayerStroke[1],
					p1_info, p2_info);
			}
		}

		// Draw the cars.
		int [] car_locs = model.getCarLocations();
		for (int i=0; i<car_locs.length; i++) {
			// Look up location (dx, dy) coordinates & rotation of the space that the
			// car is on.
			int [] graph_info = trackDatabase.getGraphicalArrayForSpace(car_locs[i]);

			// If this is the active car, then draw the halo before the car
			if (i == activeCar) {
				CT_graphics.paintHalo(g,
					graph_info[CarTricksTrackDB.GRAPH_X_COORD],
					graph_info[CarTricksTrackDB.GRAPH_Y_COORD],
					i);
			}

			// Draw the car
			CT_graphics.paintCar(g,
				graph_info[CarTricksTrackDB.GRAPH_X_COORD],
				graph_info[CarTricksTrackDB.GRAPH_Y_COORD],
				i,
				graph_info[CarTricksTrackDB.GRAPH_ROT]);
		}

		// If we have a terminal on the active car, then we need to redraw the terminal,
		// since we just drew the car over top of it.
		if (needExtraTerminal > 0) {
			paintMarker(g, needExtraTerminal, -1);
		}

		// If there is a selected path, then draw the highlighted terminal marker on it.
		if (selectedPath != null) {
			paintMarker(g, selectedPath.getTerminal(), activeCar);
		}

/*
		// For testing, put cars on all of the spaces
		for (int i=0; i<trackDatabase.getNumSpaces(); i++) {
			int [] graph_info = trackDatabase.getGraphicalArrayForSpace(i);

			CT_graphics.paintCar(g,
				graph_info[CarTricksTrackDB.GRAPH_X_COORD],
				graph_info[CarTricksTrackDB.GRAPH_Y_COORD],
				(i%6),
				graph_info[CarTricksTrackDB.GRAPH_ROT]);
		}
*/
	}

	/**
	 * Draw a line on the track to show part of the path.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	lineColor		The color to use to draw the line with
	 * @param	stroke			The stroke to use to draw the line with
	 * @param	p1_info			Info about one end point of the line
	 * @param	p2_info			Info about the other end point of the line
	 */
	private void drawCarTricksLine(Graphics g, Color lineColor, BasicStroke stroke, int [] p1_info, int [] p2_info) {
		Graphics2D g2d = (Graphics2D) g;

		if ((p1_info != null) && (p2_info != null)) {
			// Only draw the line if the info's have data
			g2d.setStroke(stroke);
			g.setColor(lineColor);
			g.drawLine( p1_info[CarTricksTrackDB.GRAPH_X_COORD],
						p1_info[CarTricksTrackDB.GRAPH_Y_COORD],
						p2_info[CarTricksTrackDB.GRAPH_X_COORD],
						p2_info[CarTricksTrackDB.GRAPH_Y_COORD]);
		}
	}

	/**
	 * Draw a track marker on a track space.  The color in the middle of the marker
	 * is equal to the color of the car that is moving.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	space			The track space to paint on
	 * @param	carId			The number (0..5) of the car that is moving
	 *							or -1 for a blank marker.
	 */
	private void paintMarker(Graphics g, int space, int carId) {
		// Look up the location (dx, dy) coordinates of the space
		int [] graph_info = trackDatabase.getGraphicalArrayForSpace(space);
		int dx = graph_info[CarTricksTrackDB.GRAPH_X_COORD];
		int dy = graph_info[CarTricksTrackDB.GRAPH_Y_COORD];

		// Fix (dx,dy) so that the image is centered on that spot
		dx -= (pathMarkersWidth / 2);
		dy -= (pathMarkersHeight / 2);

		// Determine the source location
		int sx = pathMarkersWidth * (carId + 1);
		int sy = 0;

		// Draw the image
		g.drawImage(pathMarkersImage,
					dx, dy,											// dx1, dy1
					dx + pathMarkersWidth, dy + pathMarkersHeight,	// dx2, dy2
					sx, sy,											// sx1, sy1
					sx + pathMarkersWidth, sy + pathMarkersHeight,	// sx2, sy2
					null
					);
	}

}
