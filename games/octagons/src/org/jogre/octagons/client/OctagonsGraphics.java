/*
 * JOGRE (Java Online Gaming Real-time Engine) - Octagons
 * Copyright (C) 2005-2006  Richard Walter
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
package org.jogre.octagons.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class OctagonsGraphics {

	/**
	 * Constructor
	 */
	public OctagonsGraphics() {}

	/**
	 * Create a polygon for the left half of an octagon.
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 */
	private static Polygon createOctagonLeftHalf (int size) {
		int [] x = {size, (3*size)/2, (3*size)/2, size, 0, 0};
		int [] y = {0, 0, 3*size, 3*size, 2*size, size};

		return new Polygon (x, y, 6);
	}
	
	/**
	 * Create a polygon for the right half of an octagon.
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 */
	private static Polygon createOctagonRightHalf (int size) {
		int [] x = {(3*size)/2, 2*size, 3*size, 3*size, 2*size, (3*size)/2};
		int [] y = {0, 0, size, 2*size, 3*size, 3*size};

		return new Polygon (x, y, 6);
	}

	/**
	 * Create a polygon for the top half of an octagon.
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 */
	private static Polygon createOctagonTopHalf (int size) {
		int [] x = {0, 0, size, 2*size, 3*size, 3*size};
		int [] y = {(3*size)/2, size, 0, 0, size, (3*size)/2};

		return new Polygon (x, y, 6);
	}

	/**
	 * Create a polygon for the bottom half of an octagon.
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 */
	 private static Polygon createOctagonBottomHalf (int size) {
		int [] x = {0, 3*size, 3*size, 2*size, size, 0};
		int [] y = {(3*size)/2, (3*size)/2, 2*size, 3*size, 3*size, 2*size};

		return new Polygon (x, y, 6);
	}

	/**
	 * Create a polygon for a square.
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 */
	private static Polygon createSquare (int size) {
		int [] x = {size, 0, -size, 0};
		int [] y = {0, size, 0, -size};

		return new Polygon (x, y, 4);
	}

	/**
	 * Draw a filled octagon.  The outline around the octagon is one pixel in size.
	 *
	 * @param		g				The graphics context
	 * @param		(x,y)			The pixel location of where to place the square
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		direction		Orientation of the octagon
	 *									0 = left/right octagon
	 *									1 = up/down octagon
	 * @param		color_1			The color to use for the interior of the first part of the octagon
	 * @param		color_2			The color to use for the interior of the second part of the octagon
	 * @param		line_color		The color to use for the outline
	 */
	public static void drawOctagon (
		Graphics g, int x, int y, int size,
		int direction,
		Color color_1, Color color_2, Color line_color) {

		Polygon part_1, part_2;

		/* Create the two octagon halves */
		if (direction == 0) {
			part_1 = createOctagonLeftHalf (size);
			part_2 = createOctagonRightHalf (size);
		} else {
			part_1 = createOctagonTopHalf (size);
			part_2 = createOctagonBottomHalf (size);
		}

		/* Translate them to the correct position */
		part_1.translate (x, y);
		part_2.translate (x, y);

		/* Draw the filled halfs */
		g.setColor (color_1);
		g.fillPolygon (part_1);
		g.setColor (color_2);
		g.fillPolygon (part_2);

		/* Draw an outline around them. */
		g.setColor (line_color);
		g.drawPolygon (part_1);
		g.drawPolygon (part_2);
	}

	/**
	 * Draw a filled square.  The square is not outlined.
	 *
	 * @param		g				The graphics context
	 * @param		(x,y)			The pixel location of where to place the square
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		sq_color		The color to use for the interior of the square
	 */
	public static void drawSquare (
		Graphics g, int x, int y, int size,
		Color sq_color) {

		Polygon sq_poly = createSquare(size);

		sq_poly.translate (x, y);
		g.setColor (sq_color);
		g.fillPolygon (sq_poly);
	}

	/**
	 * Draw the outline of half an octagon.  The outline is 3-pixels wide
	 *
	 * @param		g				The graphics context
	 * @param		(x,y)			The pixel location of where to place the octagon
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		direction		Orientation of the octagon
	 *									0 = left/right octagon
	 *									1 = up/down octagon
	 * @param		half			Which half of the octagon to draw
	 *									OCT_1 = left or up
	 *									OCT_2 = right or down
	 * @param		line_color		The color to use for the outline
	 */
	private static void outlineOctagon (
		Graphics g, int x, int y, int size,
		int direction, int half,
		Color line_color) {

		Polygon poly;
		Graphics2D g2d = (Graphics2D) g;

		if (direction == 0) {
			if (half == OctLoc.OCT_1) {
				poly = createOctagonLeftHalf(size);
			} else {
				poly = createOctagonRightHalf(size);
			}
		} else {
			if (half == OctLoc.OCT_1) {
				poly = createOctagonTopHalf(size);
			} else {
				poly = createOctagonBottomHalf(size);
			}
		}

		poly.translate(x, y);
		g.setColor (line_color);
		g2d.setStroke(new BasicStroke(3));
		g.drawPolygon(poly);
		g2d.setStroke(new BasicStroke(1));
	}

	/**
	 * Draw the outline of a square.  The outline is 3-pixels wide
	 *
	 * @param		g				The graphics context
	 * @param		(x,y)			The pixel location of where to place the square
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		line_color		The color to use for the outline
	 */
	private static void outlineSquare (
		Graphics g, int x, int y, int size,
		Color line_color) {

		Polygon sq_poly = createSquare(size);
		Graphics2D g2d = (Graphics2D) g;

		sq_poly.translate (x, y);

		g.setColor (line_color);
		g2d.setStroke(new BasicStroke(3));
		g.drawPolygon (sq_poly);
		g2d.setStroke(new BasicStroke(1));
	}

	/**
	 * Draw the outline of a location.  The outline is 3-pixels wide
	 *
	 * @param		g				The graphics context
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		theLocation		The location to outline
	 * @param		line_color		The color to use for the outline
	 */
	public static void outlineLocation (
		Graphics g, int size, OctLoc theLocation, Color line_color) {

		int i = theLocation.get_i();
		int j = theLocation.get_j();
		int element = theLocation.get_element();

		switch (element) {
			case OctLoc.OCT_1 :
			case OctLoc.OCT_2 :
				outlineOctagon (
					g,								// graphics
					(size * 3 * i) + size,			// x
					(size * 3 * j) + size,			// y
					size,							// size
					(i+j) & 0x01,					// direction (up/down or left/right)
					element,						// half
					line_color						// line_color
				);
				break;
			case OctLoc.SQUARE :
				outlineSquare(
					g,								// graphics
					(size * 3 * (i+1)) + size,		// x
					(size * 3 * (j+1)) + size,		// y
					size,							// size
					line_color						// line_color
				);
				break;
		}
	}


	/**
	 * Draw two polygons
	 *
	 * @param		g				The graphics context
	 * @param		poly_color		The color to use to fill the polygons
	 * @param		line_color		The color to use for the outline of the polygons
	 * @param		poly_1			The first polygon to draw
	 * @param		poly_2			The second polygon to draw
	 */
	public static void drawPolys (
		Graphics g,
		Color poly_color, Color line_color,
		Polygon poly_1, Polygon poly_2) {

		g.setColor(poly_color);
		g.fillPolygon(poly_1);
		g.fillPolygon(poly_2);
		g.setColor(line_color);
		g.drawPolygon(poly_1);
		g.drawPolygon(poly_2);
	}


	/**
	 * Create the polygon for the top border of the board
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		num_octs		The number of octagons across the board
	 * @returns						A polygon for the top edge of the board
	 */
	public static Polygon createTopBorderPoly (int size, int num_octs) {
		Polygon poly = new Polygon();
		int i;
		int size2 = 2*size;
		int size3 = 3*size;
		int tx = size2;

		// Add the first few points
		poly.addPoint(0,0);
		poly.addPoint((size*3)/2, (size*3)/2);

		// Add humps over the octagons
		for (i=1; i < num_octs; i++) {
			poly.addPoint(tx, size);
			poly.addPoint(tx + size, size);
			poly.addPoint(tx + size2, size2);
			tx = tx + size3;
		}

		// Add the final points
		poly.addPoint(tx, size);
		poly.addPoint(tx + size, size);
		poly.addPoint(tx + (size * 3) / 2, (size*3)/2);
		poly.addPoint(tx + size3, 0);

		return poly;
	}

	/**
	 * Create the polygon for the bottom border of the board
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		num_octs		The number of octagons across the board
	 * @returns						A polygon for the bottom edge of the board
	 */
	public static Polygon createBottomBorderPoly (int size, int num_octs) {
		Polygon poly = new Polygon();
		int i;
		int size2 = 2*size;
		int size3 = 3*size;
		int tx = size2;

		// Add the first few points
		poly.addPoint(0,0);
		poly.addPoint((size*3)/2, -(size*3)/2);

		// Add humps over the octagons
		for (i=1; i < num_octs; i++) {
			poly.addPoint(tx, -size);
			poly.addPoint(tx + size, -size);
			poly.addPoint(tx + size2, -size2);
			tx = tx + size3;
		}

		// Add the final points
		poly.addPoint(tx, -size);
		poly.addPoint(tx + size, -size);
		poly.addPoint(tx + (size * 3) / 2, -(size*3)/2);
		poly.addPoint(tx + size3, 0);

		// Move it to the bottom of the board
		poly.translate(0, size3 * num_octs + size2);
		return poly;
	}

	/**
	 * Create the polygon for the left border of the board
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		num_octs		The number of octagons across the board
	 * @returns						A polygon for the left edge of the board
	 */
	public static Polygon createLeftBorderPoly (int size, int num_octs) {
		Polygon poly = new Polygon();
		int i;
		int size2 = 2*size;
		int size3 = 3*size;
		int ty = size2;

		// Add the first few points
		poly.addPoint(0,0);
		poly.addPoint((size*3)/2, (size*3)/2);

		// Add humps over the octagons
		for (i=1; i < num_octs; i++) {
			poly.addPoint(size, ty);
			poly.addPoint(size, ty + size);
			poly.addPoint(size2, ty + size2);
			ty = ty + size3;
		}

		// Add the final points
		poly.addPoint(size, ty);
		poly.addPoint(size, ty + size);
		poly.addPoint((size*3)/2, ty + (size*3)/2);
		poly.addPoint(0, ty + size3);

		return poly;
	}

	/**
	 * Create the polygon for the right border of the board
	 *
	 * @param		size			The length of one side of an octagon (in pixels)
	 * @param		num_octs		The number of octagons across the board
	 * @returns						A polygon for the right edge of the board
	 */
	public static Polygon createRightBorderPoly (int size, int num_octs) {
		Polygon poly = new Polygon();
		int i;
		int size2 = 2*size;
		int size3 = 3*size;
		int ty = size2;

		// Add the first few points
		poly.addPoint(0,0);
		poly.addPoint(-(size*3)/2, (size*3)/2);

		// Add humps over the octagons
		for (i=1; i < num_octs; i++) {
			poly.addPoint(-size, ty);
			poly.addPoint(-size, ty + size);
			poly.addPoint(-size2, ty + size2);
			ty = ty + size3;
		}

		// Add the final points
		poly.addPoint(-size, ty);
		poly.addPoint(-size, ty + size);
		poly.addPoint(-(size*3)/2, ty + (size*3)/2);
		poly.addPoint(0, ty + size3);

		// Move it to the right of the board
		poly.translate(size3 * num_octs + size2, 0);
		return poly;
	}
}
