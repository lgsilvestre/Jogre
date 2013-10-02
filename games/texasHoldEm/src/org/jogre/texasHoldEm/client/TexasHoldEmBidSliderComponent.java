/*
 * JOGRE (Java Online Gaming Real-time Engine) - TexasHoldEm
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com) and
 *      Bob Marks (marksie531@yahoo.com)
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
package org.jogre.texasHoldEm.client;

import org.jogre.client.awt.JogreComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * TexasHoldEm Bid Slider component.
 * This displays the bid slider component.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmBidSliderComponent extends JogreComponent {

	// Link to the model, graphics helper & text labels
	private TexasHoldEmClientModel model;
	TexasHoldEmGraphics thGraphics;

	// Metrics for the game font.
	// (The font is created and kept in TexasHoldEmGraphics.java)
	FontMetrics gameFontMetrics;

	// Max. value of top slider
	private int absoluteMaxValue;

	// Current low value of bottom slider
	private int currentLowValue;

	// The minimum & maximum value that the slider can be set to
	private int thumbMinValue = 0;
	private int thumbMaxValue = 0;

	// The current slider value
	private int currentThumbValue;

	// The current range location & size;
	private int rangeWidth;
	private int rangeX;

	// The offset into the dragged object from where the mouse has grabbed
	// the object for dragging.
	private int dragOffsetX;

	// Colors used to draw the sliders
	private Color sliderBackgroundColor = new Color (192, 192, 192);
	private Color minColor = new Color (0, 200, 0);
	private Color maxColor = new Color (64, 64, 64);
	private Color rangeColor = null;
	private Color thumbLineColor = new Color (255, 0, 0);

	// Points where various items are drawn within the component.
	private Point zeroTextAnchor;
	private Point absoluteMaxTextAnchor;
	private Point detailMinTextAnchor;
	private Point detailMidTextAnchor;
	private Point detailMaxTextAnchor;
	private Point topSliderAnchor;
	private Point bottomSliderAnchor;
	private int thumbY, thumbX;
	private int movingTextY;
	private int thumbLineX;

	// Strings used to draw the zero & absolute max for the top slider.
	// (These are fixed, so are only created once.)
	private String zeroString;
	private String absoluteMaxString;
	private String currLowValueString;
	private String currMidValueString;
	private String currHighValueString;
	private String currMovingString;

	// Parameters for drawing the slider component
	private static final int SLIDER_WIDTH = 300;
	private static final int SLIDER_HEIGHT = 30;
	private static final int SLIDER_SPACING = 10;
	private static final int TEXT_BORDER = 2;

	/**
	 * Constructor which creates the component
	 *
	 * @param model					The game model
	 * @param absoluteMaxValue		The maximum value that can be bet
	 */
	public TexasHoldEmBidSliderComponent (
		TexasHoldEmClientModel model,
		int absoluteMaxValue
	) {

		// Save parameters provided
		this.model = model;
		thGraphics = TexasHoldEmGraphics.getInstance();
		this.absoluteMaxValue = absoluteMaxValue;

		// Get the Font Metrics for the font we're using
		gameFontMetrics = getFontMetrics(thGraphics.gameFont);

		// Set the preferred dimension of the component
		setPreferredSize ( createComponentGeometry() );

		// Set initial values for things.
		zeroString = thGraphics.currencyFormatter.format(0);
		absoluteMaxString = thGraphics.currencyFormatter.format(absoluteMaxValue);
		setRangeStart(0);
		setThumbValue(0);

		rangeWidth = (SLIDER_WIDTH * 1000) / absoluteMaxValue;
	}

	/*
	 * Create the locations of various items in the component.
	 *
	 * @return the size of the component.
	 */
	private Dimension createComponentGeometry() {
		// Get the size of the box required to display currency.
		Dimension currencyDim = thGraphics.getLargestCurrencyDimension();

		// Calculate anchor points for top text & slider
		int leftSide = currencyDim.width / 2;
		int rightSide = leftSide + SLIDER_WIDTH;
		int y = gameFontMetrics.getMaxAscent();
		zeroTextAnchor = new Point (leftSide, y);
		absoluteMaxTextAnchor = new Point (rightSide, y);
		y += gameFontMetrics.getMaxDescent() + TEXT_BORDER;
		topSliderAnchor = new Point (leftSide, y);

		// Calculate anchor points for bottom text & slider
		y += SLIDER_HEIGHT + SLIDER_SPACING + gameFontMetrics.getMaxAscent();
		detailMinTextAnchor = new Point (leftSide, y);
		detailMidTextAnchor = new Point ((leftSide + rightSide) / 2, y);
		detailMaxTextAnchor = new Point (rightSide, y);
		y += gameFontMetrics.getMaxDescent() + TEXT_BORDER;
		bottomSliderAnchor = new Point (leftSide, y);

		// Calculate anchor points for the slider knob & text
		thumbY = y + (SLIDER_HEIGHT / 2);
		y += SLIDER_HEIGHT + TEXT_BORDER + gameFontMetrics.getMaxAscent();
		movingTextY = y;

		// Return the size of the whole component
		return new Dimension (
			SLIDER_WIDTH + currencyDim.width,
			y + gameFontMetrics.getMaxDescent() + TEXT_BORDER
		);
	}

	/**
	 * Set the thumb position to the given value.
	 *
	 * @param newValue		The new value to place the thumb at.
	 */
	public void setThumbValue(int newValue) {
		// Save the new value.
		currentThumbValue = pinToRange(newValue, thumbMinValue, thumbMaxValue);

		// Quantise to only be on $10 increments
		currentThumbValue = quantiseTo(currentThumbValue, 10);

		// Create text for under the thumb
		currMovingString = thGraphics.currencyFormatter.format(currentThumbValue);

		// Calculate the position of the slider thumb, and pin it to within the
		// slider Rectangle
		thumbX = pinToRange(
			((currentThumbValue - currentLowValue) * 3) / 10,
			0, SLIDER_WIDTH);
		thumbX += bottomSliderAnchor.x;

		// Set the position of the line in the upper slider
		thumbLineX = (currentThumbValue * SLIDER_WIDTH) / absoluteMaxValue;
		thumbLineX += topSliderAnchor.x;

	}

	/**
	 * Set a new value for the range to start in the upper slider.
	 * This also sets the currentLowValue of the lower slider.
	 */
	public void setRangeStart(int newValue) {
		// Save the new value
		currentLowValue = pinToRange(newValue, 0, absoluteMaxValue - 1000);

		// Quantise the range to start on $500 increments
		currentLowValue = quantiseTo(currentLowValue, 500);

		// Create the text for the lower slider
		currLowValueString = thGraphics.currencyFormatter.format(currentLowValue);
		currMidValueString = thGraphics.currencyFormatter.format(currentLowValue + 500);
		currHighValueString = thGraphics.currencyFormatter.format(currentLowValue + 1000);

		// Calculate the starting location of the range in the upper slider
		rangeX = (currentLowValue * SLIDER_WIDTH) / absoluteMaxValue;
		rangeX += topSliderAnchor.x;

		// Recalculate the thumb position given the new value.
		setThumbValue(currentThumbValue);
	}

	/**
	 * Decide if the given point is within the thumb of the lower slider.
	 * This will also set the dragOffsetX variable for dragging the thumb.
	 *
	 * @param (x,y)		The point to test.
	 *
	 * @return if the point is within the thumb or not.
	 */
	public boolean isPointInThumb(int x, int y) {
		dragOffsetX = x - thumbX;
		return thGraphics.pointWithinImage(x, y, thumbX, thumbY, TexasHoldEmImages.BID_SLIDER);
	}

	/**
	 * Drag the thumb to the given mouse coordinates within the slider.
	 * This will pin the coordinates to within the slider rectangle and also
	 * quantise the thumb location to one of the points.
	 *
	 * @param (x,y)		The point to drag to.
	 */
	public void dragThumbTo(int x, int y) {
		// Convert x to the position within the slider
		x = pinToRange((x - bottomSliderAnchor.x - dragOffsetX), 0, SLIDER_WIDTH);

		// Convert to money and move the slider there.
		setThumbValue(((10 * x) / 3) + currentLowValue);
	}

	/**
	 * Decide if the given point is within the range of the top slider.
	 * This will also set the dragOffsetX variable for dragging the range.
	 *
	 * @param (x,y)		The point to test.
	 *
	 * @return if the point is within the range or not.
	 */
	public boolean isPointInRange(int x, int y) {
		dragOffsetX = x - rangeX;
		return ((x >= rangeX) && (x <= (rangeX + rangeWidth)) &&
		        (y >= topSliderAnchor.y) && (y <= (topSliderAnchor.y + SLIDER_HEIGHT)));
	}

	/**
	 * Drag the range to the given mouse coordinates within the slider.
	 * This will pin the coordinates to within the slider rectangle and also
	 * quantise the range location to $500 incrments
	 *
	 * @param (x,y)		The point to drag to.
	 */
	public void dragRangeTo(int x, int y) {
		// Convert x to position within the slider
		x = pinToRange((x - topSliderAnchor.x - dragOffsetX), 0, SLIDER_WIDTH);

		// Convert to a money value & set the range to start there
		setRangeStart((x * absoluteMaxValue) / SLIDER_WIDTH);
	}

	/**
	 * Set the minimum value that the thumb can have.
	 * If the thumb is currently sitting below this value, then the thumb will
	 * be moved to this new minimum value.
	 *
	 * @param minValue   The minimum value that the thumb can have.
	 */
	public void setThumbMinValue(int minValue) {
		// Set the thumb min value.
		thumbMinValue = minValue;

		// If the new min is greater than than old max, then move max up...
		if (thumbMaxValue < minValue) {
			thumbMaxValue = minValue;
		}

		// If the thumb is below the min, move the thumb.
		if (currentThumbValue < minValue) {
			setThumbValue(minValue);
		}
	}

	/**
	 * Set the maximum value that the thumb can have.
	 * If the thumb is currently sitting above this value, then the thumb will
	 * be moved to this new maximum value.
	 *
	 * @param minValue   The maximum value that the thumb can have.
	 */
	public void setThumbMaxValue(int maxValue) {
		// Set the thumb max value.
		thumbMaxValue = maxValue;

		// If the new max is less than than old min, then move min down...
		if (thumbMinValue > maxValue) {
			thumbMinValue = maxValue;
		}

		// If the thumb is above the max, move the thumb.
		if (currentThumbValue > maxValue) {
			setThumbValue(maxValue);
		}
	}

	/**
	 * Set the minimum & maximum values to the values given in the array.
	 *
	 * @param limits   An array whose first element is the minimum value and
	 *                 second element is the maximum value.
	 */
	public void setThumbLimits(int [] limits) {
///		System.out.println("Setting thumb limits to " + limits[0] + " - " + limits[1]);
		setThumbMinValue(limits[0]);
		setThumbMaxValue(limits[1]);
	}


	/**
	 * Return the current value of the thumb.
	 *
	 * @return the current value of the thumb.
	 */
	public int getCurrentThumbValue() {
		return currentThumbValue;
	}

	/**
	 * Return the maximum legal value of the thumb.
	 *
	 * @return the maximum legal value of the thumb.
	 */
	public int getMaxThumbValue() {
		return thumbMaxValue;
	}

	/**
	 * Return the string that represents the current location of the thumb.
	 *
	 * @return the string that represents the current location of the thumb.
	 */
	public String getCurrentBidString() {
		return currMovingString;
	}

	/**
	 * Paint the component
	 *
	 * @param g		The graphics context to draw on.
	 */
	public void paintComponent (Graphics g) {
		if (rangeColor == null) {
			rangeColor = new Color (200, 200, 0, 128);
		}

		int currBid = model.getCurrentBid();
		thGraphics.setGameFont(g);
		drawTopSlider(g, currBid);
		drawConnectingPoly(g);
		drawBottomSlider(g, currBid);
	}

	/*
	 * This method will draw the top slider.
	 *
	 * @param g			The graphics context to draw on.
	 * @param currBid	The current bid.
	 */
	private void drawTopSlider(Graphics g, int currBid) {
		// Draw the background.
		g.setColor(sliderBackgroundColor);
		g.fillRect(topSliderAnchor.x, topSliderAnchor.y, SLIDER_WIDTH, SLIDER_HEIGHT);

		// Draw from 0 to currentBid with minColor &&
		// Draw from currentBid to thumbMinValue with maxColor
		if (currBid < thumbMinValue) {
			g.setColor(maxColor);
			g.fillRect(topSliderAnchor.x, topSliderAnchor.y, ((SLIDER_WIDTH * thumbMinValue) / absoluteMaxValue), SLIDER_HEIGHT);
		}
		g.setColor(minColor);
		g.fillRect(topSliderAnchor.x, topSliderAnchor.y, ((SLIDER_WIDTH * currBid) / absoluteMaxValue), SLIDER_HEIGHT);

/*
		// Draw from 0 to minimum with minColor
		g.setColor(minColor);
		g.fillRect(topSliderAnchor.x, topSliderAnchor.y, ((SLIDER_WIDTH * thumbMinValue) / absoluteMaxValue), SLIDER_HEIGHT);
*/

		// Draw from max to absolute max with maxColor
		g.setColor(maxColor);
		int startX = ((SLIDER_WIDTH * thumbMaxValue) / absoluteMaxValue);
		g.fillRect(topSliderAnchor.x + startX, topSliderAnchor.y,
		           (SLIDER_WIDTH - startX), SLIDER_HEIGHT);

		// Draw the highlight that indicates the range of the lower slider.
		g.setColor(rangeColor);
		g.fillRect(rangeX, topSliderAnchor.y, rangeWidth, SLIDER_HEIGHT);
		g.setColor(thumbLineColor);
		g.fillRect(thumbLineX, topSliderAnchor.y, 1, SLIDER_HEIGHT);

		// Draw black outline around the slider
		g.setColor(thGraphics.blackColor);
		drawOutline(g, topSliderAnchor);

		// Draw the gradiations every $1000
		for (int v=0; v < absoluteMaxValue; v+=1000) {
			int x = topSliderAnchor.x + ((v * SLIDER_WIDTH) / absoluteMaxValue);
			g.fillRect(x, topSliderAnchor.y, 1, SLIDER_HEIGHT);
		}

		// Draw the text for the top slider
		thGraphics.drawCenterJustifiedText(g, zeroString, zeroTextAnchor);
		thGraphics.drawCenterJustifiedText(g, absoluteMaxString, absoluteMaxTextAnchor);
	}

	/*
	 * This method will draw the polygon that connects the top & bottom sliders.
	 *
	 * @param g		The graphics context to draw on.
	 */
	private void drawConnectingPoly(Graphics g) {
		int [] xPoints = {bottomSliderAnchor.x,
		                  rangeX,
		                  rangeX + rangeWidth,
		                  bottomSliderAnchor.x + SLIDER_WIDTH};
		int [] yPoints = {bottomSliderAnchor.y - 2,
		                  topSliderAnchor.y + SLIDER_HEIGHT + 2,
		                  topSliderAnchor.y + SLIDER_HEIGHT + 2,
		                  bottomSliderAnchor.y - 2};

		g.setColor(rangeColor);
		g.fillPolygon(xPoints, yPoints, 4);

		g.setColor(thumbLineColor);
		g.drawLine(thumbLineX, topSliderAnchor.y + SLIDER_HEIGHT + 2, thumbX, bottomSliderAnchor.y - 2);
	}

	/*
	 * This method will draw the bottom slider.
	 *
	 * @param g		The graphics context to draw on.
	 * @param currBid	The current bid.
	 */
	private void drawBottomSlider(Graphics g, int currBid) {
		// Draw the background.
		g.setColor(sliderBackgroundColor);
		g.fillRect(bottomSliderAnchor.x, bottomSliderAnchor.y, SLIDER_WIDTH, SLIDER_HEIGHT);

		// Draw from currentLowValue to currentBid with minColor &&
		// Draw from currentBid to thumbMinValue with maxColor
		int width = thumbMinValue - currentLowValue;
		if (width > 0) {
			if (currBid < thumbMinValue) {
				g.setColor(maxColor);
				width = pinToRange((width * 3) / 10, 0, SLIDER_WIDTH);
				g.fillRect(bottomSliderAnchor.x, bottomSliderAnchor.y, width, SLIDER_HEIGHT);
			}

			width = currBid - currentLowValue;
			if (width > 0) {
				width = pinToRange((width * 3) / 10, 0, SLIDER_WIDTH);
				g.setColor(minColor);
				g.fillRect(bottomSliderAnchor.x, bottomSliderAnchor.y, width, SLIDER_HEIGHT);
			}
		}
/*
		// Draw from currentLowValue to minimum with minColor
		int width = thumbMinValue - currentLowValue;
		if (width > 0) {
			width = pinToRange((width * 3) / 10, 0, SLIDER_WIDTH);
			g.setColor(minColor);
			g.fillRect(bottomSliderAnchor.x, bottomSliderAnchor.y, width, SLIDER_HEIGHT);
		}
*/
		// Draw from max to absolute max with maxColor
		width = currentLowValue + 1000 - thumbMaxValue;
		if (width > 0) {
			width = pinToRange((width * 3) / 10, 0, SLIDER_WIDTH);
			g.setColor(maxColor);
			g.fillRect(bottomSliderAnchor.x + SLIDER_WIDTH - width, bottomSliderAnchor.y, width, SLIDER_HEIGHT);
		}

		// Draw black outline around the slider
		g.setColor(thGraphics.blackColor);
		drawOutline(g, bottomSliderAnchor);

		// Draw the gradiations every 3 pixels
		int height10 = SLIDER_HEIGHT;
		int height5 = SLIDER_HEIGHT / 2;
		int height1 = SLIDER_HEIGHT / 4;
		for (int i=0; i < 100; i++) {
			int h = ((i % 5) == 0) ? ( ((i % 10) == 0) ? height10 : height5 ) : height1;
			g.fillRect(bottomSliderAnchor.x + 3*i, bottomSliderAnchor.y, 1, h);
		}

		// Draw the thumb
		thGraphics.paintImage(g, thumbX, thumbY, TexasHoldEmImages.BID_SLIDER, 0, 0);

		// Draw the text for the bottom slider
		thGraphics.drawCenterJustifiedText(g, currLowValueString, detailMinTextAnchor);
		thGraphics.drawCenterJustifiedText(g, currMidValueString, detailMidTextAnchor);
		thGraphics.drawCenterJustifiedText(g, currHighValueString, detailMaxTextAnchor);
		thGraphics.drawCenterJustifiedText(g, currMovingString, thumbX, movingTextY);
	}

	/*
	 * This will draw a 2-pixel outline around a slider rectangle at the given
	 * offset into the component.
	 */
	private void drawOutline(Graphics g, Point offset) {
		g.fillRect(offset.x-1, offset.y-1, SLIDER_WIDTH+3, 2);
		g.fillRect(offset.x-1, offset.y-1, 2, SLIDER_HEIGHT+2);
		g.fillRect(offset.x+SLIDER_WIDTH, offset.y-1, 2, SLIDER_HEIGHT+2);
		g.fillRect(offset.x-1, offset.y+SLIDER_HEIGHT-1, SLIDER_WIDTH+2, 2);
	}

	/*
	 * This pins a number to the range of lowest to highest.
	 */
	private int pinToRange(int num, int lowest, int highest) {
		if (num < lowest) {
			return lowest;
		} else if (num > highest) {
			return highest;
		}
		return num;
	}

	/*
	 * Round the given number to the nearest multiple of a quantization target.
	 *
	 * @param origValue		The value to quantise.
	 * @param quantization	The multiple to quantize to.
	 */
	private int quantiseTo(int origValue, int quantization) {
		origValue += (quantization / 2);
		return origValue - (origValue % quantization);
	}

}
