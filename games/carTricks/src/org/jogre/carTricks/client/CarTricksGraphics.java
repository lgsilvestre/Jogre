/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
 * Copyright (C) 2006  Richard Walter
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

import org.jogre.carTricks.common.CarTricksCard;

import java.lang.Math;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import java.util.StringTokenizer;

import org.jogre.client.awt.GameImages;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

// Car Tricks graphics - Support functions for drawing things
public class CarTricksGraphics {

	// Static instance provided by the static factory method getInstance()
	private static CarTricksGraphics myInstance = null;

	// The faces of the common cards
	private ImageIcon cardImagesIcon;
	private Image cardImages;
	public int cardWidth, cardHeight;

	// The card back (the CarTricks logo)
	private Image cardBackImage;

	// The small event card markers
	private ImageIcon smallEventIcon;
	private Image smallEventImage;
	public int smallWidth, smallHeight;

	// The large event cards
	private ImageIcon largeEventIcon;
	private Image largeEventImage;
	public int largeWidth, largeHeight;

	// The image of the cars
	private ImageIcon carsImageIcon;
	private Image carsImage;
	public int carWidth, carHeight;

	// The image of the large cars (used for positions)
	private ImageIcon largeCarsImageIcon;
	private Image largeCarsImage;
	public int largeCarWidth, largeCarHeight;

	// The image of the card prompt
	private ImageIcon cardPromptImageIcon;
	private Image cardPromptImage;
	private int cardPromptX, cardPromptY;

	// The image of the badges in the played card area
	private ImageIcon checkImageIcon;
	private Image checkImage;
	public int checkWidth, checkHeight;

	private ImageIcon steeringImageIcon;
	private Image steeringImage;
	public int steeringWidth, steeringHeight;

	// Colors used for highlighting a rectangle.
	// This is not used.  See game.properties for discussion.
	public final static int NUM_HILIGHT_STRIPES = 5;
	private Color [] highlightColors = new Color [NUM_HILIGHT_STRIPES];
	private BasicStroke thinStroke = new BasicStroke(1);

	// Colors of the cars
	public Color [] carColors;

	// Halos used to highlight the active car on the track
	private Color [] haloColor;
	private int haloSize, haloDensity;

	/**
	 * Constructor which creates the graphics.
	 *
	 * This is private because the graphics helper should only be retrieved
	 * via the getInstance() factory method.
	 *
	 * Note: Cards are loaded here because there are multiple containers that
	 *		 draw cards (the hand & the player areas) and so this puts all
	 *		 of that in one place.  The track, on the other hand, is only
	 *		 ever drawn by the track container, so that picture is loaded
	 *		 and drawn by the TrackComponent.
	 */
	private CarTricksGraphics() {

		// Load the picture of the cards
		cardImagesIcon = GameImages.getImageIcon (CarTricksImages.CARDS);
		cardImages = cardImagesIcon.getImage();
		cardWidth = cardImagesIcon.getIconWidth() / 10;
		cardHeight = cardImagesIcon.getIconHeight() / 6;

		cardBackImage = GameImages.getImage(CarTricksImages.CARDBACK);

		smallEventIcon = GameImages.getImageIcon(CarTricksImages.SMALL_EVENTS);
		smallEventImage = smallEventIcon.getImage();
		smallWidth = smallEventIcon.getIconWidth() / 2;
		smallHeight = smallEventIcon.getIconHeight() / 4;

		largeEventIcon = GameImages.getImageIcon(CarTricksImages.LARGE_EVENTS);
		largeEventImage = largeEventIcon.getImage();
		largeWidth = largeEventIcon.getIconWidth() / 2;
		largeHeight = largeEventIcon.getIconHeight() / 4;

		// Load the picture of the cars
		carsImageIcon = GameImages.getImageIcon (CarTricksImages.CARS);
		carsImage = carsImageIcon.getImage();
		carWidth = carsImageIcon.getIconWidth() / 16;
		carHeight = carsImageIcon.getIconHeight() / 6;

		// Load the picture of the large cars
		largeCarsImageIcon = GameImages.getImageIcon (CarTricksImages.LARGE_CARS);
		largeCarsImage = largeCarsImageIcon.getImage();
		largeCarWidth = largeCarsImageIcon.getIconWidth() / 6;
		largeCarHeight = largeCarsImageIcon.getIconHeight();

		// Load the pictures of the steering wheel and check used for badges.
		checkImageIcon = GameImages.getImageIcon (CarTricksImages.CHECK_BADGE);
		checkImage = checkImageIcon.getImage();
		checkWidth = checkImageIcon.getIconWidth();
		checkHeight = checkImageIcon.getIconHeight();

		steeringImageIcon = GameImages.getImageIcon (CarTricksImages.STEERING_WHEEL_BADGE);
		steeringImage = steeringImageIcon.getImage();
		steeringWidth = steeringImageIcon.getIconWidth();
		steeringHeight = steeringImageIcon.getIconHeight();

		// Load the pictures of the card prompt
		cardPromptImageIcon = GameImages.getImageIcon (CarTricksImages.CARD_PROMPT);
		cardPromptImage = cardPromptImageIcon.getImage();
		int cpWidth = cardPromptImageIcon.getIconWidth();
		int cpHeight = cardPromptImageIcon.getIconHeight();
		cardPromptX = (cardWidth - cpWidth) / 2;
		cardPromptY = (cardHeight - cpHeight) / 2;

		// Generate the highlight colors
		GameProperties props = GameProperties.getInstance();
		Color baseHLColor = JogreUtils.getColour (props.get("focus.highlight.color"));

		for (int i=0; i<NUM_HILIGHT_STRIPES; i++) {
			highlightColors[i] = new Color (
				baseHLColor.getRed(),
				baseHLColor.getGreen(),
				baseHLColor.getBlue(),
				255 - i * (255 / (NUM_HILIGHT_STRIPES - 1))
			);
		}

		// Read in the car colors from the properties file
		carColors = new Color [6];
		for (int i=0; i < 6; i++) {
			carColors[i] = JogreUtils.getColour (props.get("car.color." + i));
		}

		// Read halo params from the game.properties file and calculate
		// some items ahead of time.
		int size_param = props.getInt("halo.size", 100);
		haloSize = Math.max(4, (Math.max(carWidth, carHeight) * size_param) / 100);

		haloDensity = Math.max(0, Math.min(255,
			(props.getInt("halo.density", 38) * 255) / 100));

		haloColor = new Color [4];
		haloColor[0] = carTricksGetColor(props.get("halo.color.0"));
		haloColor[1] = carTricksGetColor(props.get("halo.color.1"));
		// haloColor[2..3] are used when drawing the halo
	}

	/**
	 * A static factory for returning the single graphics helper instance.
	 */
	public static CarTricksGraphics getInstance() {
		if (myInstance == null) {
			myInstance = new CarTricksGraphics();
		}

		return myInstance;
	}

	/**
	 * Return a Color object from a String of "r,g,b"
	 * Note: This copied from JogreUtils.java, except it won't print an error
	 *       message to System.err if the string isn't parseable.
	 *
	 * @param colourStr	Color as a comma delimited String.
	 * @return         	Return a Color object.
	 */
	public static Color carTricksGetColor(String colourStr) {
		try {
			StringTokenizer st = new StringTokenizer (colourStr, ",");
			int r = Integer.parseInt(st.nextToken().trim());
			int g = Integer.parseInt(st.nextToken().trim());
			int b = Integer.parseInt(st.nextToken().trim());
			return new Color (r, g, b);
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the color for a given car
	 *
	 * @param carId		The car # whose color is requested
	 */
	public Color getCarColor(int carId) {
		return carColors[carId];
	}

	/**
	 * Paint a card.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card
	 * @param	theCard			The card to draw
	 */
	public void paintCard(
		Graphics g,
		int x, int y,
		CarTricksCard theCard)
	{
		int sx, sy;
		int color = theCard.cardColor();

		// If clear, then nothing to draw
		if (!theCard.isVisible()) {
			return;
		}

		if (color == CarTricksCard.UNKNOWN) {
			// Draw a card back
			g.drawImage(cardBackImage,
						x, y,								// dx1, dy1
						x + cardWidth, y + cardHeight,		// dx2, dy2
						0, 0,								// sx1, sy1
						cardWidth, cardHeight,				// sx2, sy2
						null
						);
		} else if (color == CarTricksCard.EVENT) {
			// Draw one of the event cards
			// (Note: We move the corner (x,y) such that the center of the
			//		event lines up with the center of the normal card that
			//		would have been drawn here.)
			x += (cardWidth/2) - (largeWidth/2);
			y += (cardHeight/2) - (largeHeight/2);
			sx = 0;
			sy = ((theCard.cardValue() - 2) % 4) * largeHeight;
			g.drawImage(largeEventImage,
						x, y,								// dx1, dy1
						x + largeWidth, y + largeHeight,	// dx2, dy2
						sx, sy,								// sx1, sy1
						sx + largeWidth, sy + largeHeight,	// sx2, sy2
						null
						);
		} else if (color < CarTricksCard.MAX_COLOR) {
			// Draw a regular card
			sx = (theCard.cardValue() - 2) * cardWidth;
			sy = color * cardHeight;
			g.drawImage(cardImages,
						x, y,								// dx1, dy1
						x + cardWidth, y + cardHeight,		// dx2, dy2
						sx, sy,								// sx1, sy1
						sx + cardWidth, sy + cardHeight,	// sx2, sy2
						null);
		}
	}

	/**
	 * Paint the card prompt
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the prompt.
	 *								This is the upper left corner of where the card
	 *								would be if the card that the prompt is prompting
	 *								for would have been drawn.
	 */
	public void paintCardPrompt(Graphics g, int x, int y) {
		g.drawImage(cardPromptImage, x + cardPromptX, y + cardPromptY, null);
	}

	/**
	 * Paint a small event card
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the card.
	 *								This is the upper left corner where the card is drawn.
	 * @param	id				The id of the card to draw
	 * @param	valid			true => Draw card normal.
	 							false => Draw card greyed out
	 */
	public void paintSmallEvent(
		Graphics g,
		int x, int y,
		int id,
		boolean valid)
	{
		int sx, sy;

		sx = (valid ? 0 : smallWidth);
		sy = id * smallHeight;

		g.drawImage(smallEventImage,
					x, y,									// dx1, dy1
					x + smallWidth, y + smallHeight,		// dx2, dy2
					sx, sy,									// sx1, sy1
					sx + smallWidth, sy + smallHeight,		// sx2, sy2
					null);
	}

	/**
	 * Paint a car
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the car.
	 *							   The car is CENTERED on this spot.
	 * @param	car_color		The color of the car to draw
	 * @param	rot				The rotation of the car to draw
	 */
	public void paintCar(
		Graphics g,
		int x, int y,
		int car_color,
		int rot)
	{
		int sx, sy;

		// Center the car on the given location
		x -= (carWidth/2);
		y -= (carHeight/2);

		// The source is set by the car id & rotation
		sx = rot * carWidth;
		sy = car_color * carHeight;

		// Draw the car
		g.drawImage(carsImage,
					x, y,							// dx1, dy1
					x + carWidth, y + carHeight,	// dx2, dy2
					sx, sy,							// sx1, sy1
					sx + carWidth, sy + carHeight,	// sx2, sy2
					null);
	}

	/**
	 * Draw the halo around a car
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the halo.
	 *							   The halo is CENTERED on this spot.
	 * @param	carId			The car the halo is going to go around.
	 *							   This is used when a halo color is "car"
	 */
	public void paintHalo(
		Graphics g,
		int x, int y,
		int carId)
	{
		// Generate the correct halo colors to use
		haloColor[2] = genHaloColor(haloColor[0], carId, haloDensity);
		haloColor[3] = genHaloColor(haloColor[1], carId, haloDensity);

		int rad = haloSize / 2;
		int c = 2;
		while (rad > 0) {
			// Draw the next circle of the halo
			g.setColor(haloColor[c]);
			g.fillOval(x-rad, y-rad, rad*2, rad*2);

			// Swap the colors
			c = (c == 2) ? 3 : 2;

			// Move to the next part of the halo
			rad -= 1;
		}
	}

	/**
	 * Paint a row of cars
	 *
	 * @param	g				The gpositionImageraphics area to draw on
	 * @param	(x, y)			Offset into g to start the line.
	 *							   The first car is CENTERED on this spot.
	 * @param	car_colors		The array of car colors to draw
	 * @param	x_off, y_off	The offset to apply to each car as it is drawn
	 * @param	rot				The rotation of the cars to draw
	 */
	public void paintCarArray(
		Graphics g,
		int x, int y,
		int [] car_colors,
		int x_off, int y_off,
		int rot)
	{
		int i;
		if (car_colors != null) {
			for (i=0; i<car_colors.length; i++) {
				paintCar(g, x+i*x_off, y+i*y_off, car_colors[i], rot);
			}
		}
	}

	/**
	 * Paint a large car
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the car.
	 *							   The car is CENTERED on this spot.
	 * @param	car_color		The color of the car to draw
	 */
	public void paintLargeCar(
		Graphics g,
		int x, int y,
		int car_color)
	{
		int sx, sy;

		// Only draw the car if it's color makes sense
		if (car_color >= 0) {

			// Center the car on the given location
			x -= (largeCarWidth/2);
			y -= (largeCarHeight/2);

			// The source is set by the car id
			sx = car_color * largeCarWidth;

			// Draw the car
			g.drawImage(largeCarsImage,
					x, y,									// dx1, dy1
					x + largeCarWidth, y + largeCarHeight,	// dx2, dy2
					sx, 0,									// sx1, sy1
					sx + largeCarWidth, largeCarHeight,		// sx2, sy2
					null);
		}
	}

	/**
	 * Paint a row of large cars
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to start the line.
	 *							   The first car is CENTERED on this spot.
	 * @param	car_colors		The array of car colors to draw
	 * @param	x_off, y_off	The offset to apply to each car as it is drawn
	 */
	public void paintLargeCarArray(
		Graphics g,
		int x, int y,
		int [] car_colors,
		int x_off, int y_off)
	{
		int i;
		if (car_colors != null) {
			for (i=0; i<car_colors.length; i++) {
				paintLargeCar(g, x+i*x_off, y+i*y_off, car_colors[i]);
			}
		}
	}

	/**
	 * Draw an area highlight rectangle.
	 *
	 * @param	g				The graphics area to draw on
	 * @param	rect			The rectangle to highlight
	 */
	public void highlightArea(Graphics g, Rectangle rect) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(thinStroke);

		for (int i=0; i<NUM_HILIGHT_STRIPES; i++) {
			g.setColor(highlightColors[i]);
			g.drawRect(i, i, rect.width - 2*i - 1, rect.height - 2*i - 1);
		}
	}

	/**
	 * Paint a check badge
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the badge.
	 *							This is the upper left corner where the badge is drawn.
	 */
	public void paintCheckBadge(
		Graphics g,
		int x, int y)
	{
		g.drawImage(checkImage, x, y, null);
	}

	/**
	 * Paint a steering wheel badge
	 *
	 * @param	g				The graphics area to draw on
	 * @param	(x, y)			Offset into g to paint the badge.
	 *							This is the upper left corner where the badge is drawn.
	 */
	public void paintSteeringBadge(
		Graphics g,
		int x, int y)
	{
		g.drawImage(steeringImage, x, y, null);
	}

	/**
	 * Convert a base color into a halo color by adding an alpha channel.
	 *
	 * @param baseColor		The base color to convert.
	 * @param carId			if the base color is null, then use the color of this car
	 *							as the base.
	 * @param density		The alpha value
	 *
	 * @returns the new color with alpha channel.
	 */
	private Color genHaloColor(Color baseColor, int carId, int density) {
		if (baseColor == null) {
			baseColor = carColors[carId];
		}

		return new Color (baseColor.getRed(),
						 baseColor.getGreen(),
						 baseColor.getBlue(),
						 density);
	}
}
