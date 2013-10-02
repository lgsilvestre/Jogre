/*
 * JOGRE (Java Online Gaming Real-time Engine) - Chinese Checkers
 * Copyright (C) 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.chineseCheckers.client;

import org.jogre.client.awt.JogreAwt;
import org.jogre.client.awt.AbstractHexBoards.AbstractStarHexBoardComponent;

import org.jogre.common.util.HexBoardUtils;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreUtils;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.QuadCurve2D.Float;

import java.util.ListIterator;
import java.util.Vector;

/**
 * Chinese Checkers visual board component (view of the model)
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class ChineseCheckersBoardComponent extends AbstractStarHexBoardComponent {

	// Link to the model
	private ChineseCheckersModel model;

	// The controlLineLength for each hexagon on the board.
	private final static int HEXAGON_SIZE = 20;

	// Link to the graphics helper functions
	private ChineseCheckersGraphics ccGraphics;

	// The active marble that is moving.
	private Point activeMarbleHex;

	// The highlighted move.
	private Vector highlightedMoveCurves;

	// The color to use to draw the highlighted move.
	private Color highlightedMoveColor;

	// The colors for each player.
	// These are used to draw the lines that indicate the highlighted move.
	private Color [] playerColors = new Color [6];
	private Color [] darkColors = new Color [6];

	// The colors for each point of the board.
	private Color neutralColor;
	private Color [] pointColors = new Color [6];

	// Strokes used to draw the move line.
	private BasicStroke bigStroke = new BasicStroke(7);
	private BasicStroke moveStroke = new BasicStroke(3);
	private BasicStroke normalStroke = new BasicStroke(1);

	// Color & stroke used to outline the point triangles.
	private Color blackColor = new Color (0,0,0);
	private BasicStroke outlineStroke = new BasicStroke(2);

	// Polygons used to draw the point triangles
	private Polygon [] pointTriangles = new Polygon [6];
	private Polygon [] bigTriangles = new Polygon [2];

	/**
	 * Constructor which creates the boardcomponent
	 *
	 * @param model					The game model
	 */
	public ChineseCheckersBoardComponent (ChineseCheckersModel model) {

		// Create the board.
		super (
			model.BOARD_SIZE,
			HexBoardUtils.makeRegularBoundingDim(HEXAGON_SIZE),
			HEXAGON_SIZE
		);

		// Save parameters provided
		this.model = model;
		this.ccGraphics = ChineseCheckersGraphics.getInstance();

		// Get the colors from the game.properties file
		GameProperties props = GameProperties.getInstance();
		loadColors(props);

		// Set the color of each point
		boolean colorHome = (props.getInt("preferences.colorHome", 1) == 1);
		for (int pt = 0; pt < 6; pt++) {
			int ptColor = colorHome ? model.getPointOwner(pt)
			                        : model.getPointTarget(pt);

			if (ptColor == -1) {
				pointColors[pt] = neutralColor;
			} else {
				pointColors[pt] = darkColors[ptColor];
			}
		}

		// Initialize other things
		this.activeMarbleHex = OFF_SCREEN_POINT;
		setHighlightMove(null, 0);
		Dimension neededOffset = createPointTriangles();

		// Add extra space around the board.
		setInset (neededOffset);
		setOutset (neededOffset);

		// Recompute the preferred dimension now that we've changed the geometry.
		Dimension boardDim = getBoardComponentDim ();
		setPreferredSize ( boardDim );
	}

	/*
	 * This will load the colors from the game.properties file.
	 * It creates two colors for each player.  A real color that
	 * equals the value in the file and a dark color that is half-way
	 * to black for the color.
	 */
	private void loadColors(GameProperties props) {
		neutralColor = JogreUtils.getColour (props.get("neutral.colour"));

		for (int i=0; i<6; i++) {
			playerColors[i] = JogreUtils.getColour (props.get("player.colour." + i));
			darkColors[i] = new Color (
				playerColors[i].getRed() / 2,
				playerColors[i].getGreen() / 2,
				playerColors[i].getBlue() / 2
			);
		}
	}

	/*
	 * This mess computes the boundaries of the triangles for the 6 player
	 * points that are colored to indicate which player start at which place
	 * on the board.
	 *
	 * It evolved from many other failed attempts and I'm not going to bother
	 * fixing it up and explaining it.  (For example, corner7 is called that
	 * because at one point there were 12 corners defined, but now only #7
	 * is still needed.)
	 *
	 * Consider yourself extra-cool if you can figure out what the xi & yi
	 * arrays do!
	 */
	private Dimension createPointTriangles() {
		int boardSize = model.BOARD_SIZE;

		int [] xs = new int [5];
		int [] ys = new int [7];

		Point corner7 = nthInColumn(2*boardSize, 0);
		Point screenp1 = getScreenAnchorFor(new Point (boardSize, 1));
		Point screenp2 = getScreenAnchorFor(new Point (boardSize+1, 1));
		Point screenp3 = getScreenAnchorFor(new Point (boardSize, 2));

		int xdiff = (screenp2.x - screenp1.x) * (boardSize * 2 + 1);
		int ydiff = (screenp3.y - screenp1.y) * (boardSize * 2 + 1);
		ydiff = ydiff / 2;

		xs[2] = getScreenCenterFor(corner7).x;
		xs[1] = xs[2] - (xdiff / 2);
		xs[0] = xs[2] - xdiff;
		xs[3] = xs[2] + (xdiff / 2);
		xs[4] = xs[2] + xdiff;

		ys[1] = getScreenAnchorFor(corner7).y;
		ys[2] = ys[1] + (ydiff / 2);
		ys[3] = ys[1] + ydiff;
		ys[4] = ys[3] + (ydiff / 2);
		ys[5] = ys[3] + ydiff;
		ys[6] = ys[5] + (ydiff / 2);
		ys[0] = ys[1] - (ydiff / 2);

		int [][] xi = {{0, 1, 1}, {1, 1, 2}, {3, 2, 3}, {3, 4, 3}, {3, 3, 2}, {1, 1, 2}};
		int [][] yi = {{3, 2, 4}, {0, 2, 1}, {0, 1, 2}, {2, 3, 4}, {4, 6, 5}, {4, 6, 5}};
		int [] xPoints = new int [3];
		int [] yPoints = new int [3];

		// Add some extra space around the board
		int xlateX = -xs[0] + 3;
		int xlateY = -ys[0] + 3;

		// Make polygons for the triangular points of the board
		for (int pt = 0; pt < 6; pt++) {
			xPoints[0] = xs[xi[pt][0]];		yPoints[0] = ys[yi[pt][0]];
			xPoints[1] = xs[xi[pt][1]];		yPoints[1] = ys[yi[pt][1]];
			xPoints[2] = xs[xi[pt][2]];		yPoints[2] = ys[yi[pt][2]];
			pointTriangles[pt] = new Polygon (xPoints, yPoints, 3);
			pointTriangles[pt].translate(xlateX, xlateY);
		}

		// Make the big triangles that are used to draw the black outlines
		xPoints[0] = xs[1];		yPoints[0] = ys[0];
		xPoints[1] = xs[1];		yPoints[1] = ys[6];
		xPoints[2] = xs[4];		yPoints[2] = ys[3];
		bigTriangles[0] = new Polygon (xPoints, yPoints, 3);
		bigTriangles[0].translate(xlateX, xlateY);

		xPoints[0] = xs[3];		yPoints[0] = ys[0];
		xPoints[1] = xs[3];		yPoints[1] = ys[6];
		xPoints[2] = xs[0];		yPoints[2] = ys[3];
		bigTriangles[1] = new Polygon (xPoints, yPoints, 3);
		bigTriangles[1].translate(xlateX, xlateY);

		return new Dimension (xlateX, xlateY);
	}

	/*
	 * This will determine the logical coordinates of the nth space that
	 * exists on the board in the given column.
	 */
	private Point nthInColumn(int col, int n) {
		int row = 0;
		while (!existsOnBoard(col, row)) {
			row += 1;
		}
		return new Point (col, row + n);
	}

	/**
	 * Overriding the paintComponent method for customizing the view of
	 * the board.
	 */
	public void paintComponent (Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
		// Draw the point colors
		drawPoints(g2);

		// Draw the board
		for (int r=0; r < numOfLogicalRows; r++) {
			for (int c=0; c < numOfLogicalCols; c++) {
				if (existsOnBoard(c, r)) {
					drawASpace(g2, c, r, getScreenCenterFor(c, r));
				}
			}
		}

		// Highlight the active marble (if there is one)
		if (existsOnBoard(activeMarbleHex)) {
			ccGraphics.paintImage(g2,
			                      getScreenCenterFor(activeMarbleHex),
			                      ChineseCheckersImages.SELECTED_MARBLE,
			                      0, 0);
		}

		// Draw the mouse highlighted hex (if there is one)
		if (existsOnBoard(highlightPoint)) {
			ccGraphics.paintImage(g2,
			                      getScreenCenterFor(highlightPoint),
			                      ChineseCheckersImages.SELECTED_SPACE,
			                      0, 0);
		}

		g.setColor(blackColor);
		g2.setStroke(bigStroke);
		drawMove(g2, highlightedMoveCurves);

		g.setColor(highlightedMoveColor);
		g2.setStroke(moveStroke);
		drawMove(g2, highlightedMoveCurves);

		g2.setStroke(normalStroke);
	}

	/**
	 * Draw a space on the board
	 *
	 * @param g            The grahics context to draw on.
	 * @param (col, row)   The local (col, row) of the hex to draw.
	 * @param centerPoint  The center point, in pixel coordinates, of where to
	 *                     draw the hex at.
	 */
	public void drawASpace(Graphics g, int col, int row, Point centerPoint) {
		// Draw the base piece depending on the owner
		int owner = model.getOwner(col, row);
		if (owner < 0) {
			ccGraphics.paintImage(g,
			                      centerPoint,
			                      ChineseCheckersImages.SPACES,
			                      0, 0);
		} else {
			ccGraphics.paintImage(g,
		                          centerPoint,
		                          ChineseCheckersImages.PLAYER_MARBLES,
		                          owner, 0);
		}

		// If this hex is a legal move, then draw the legal move icon
		if (model.getMoveVector(col, row) != null) {
			ccGraphics.paintImage(g,
		                          centerPoint,
		                          ChineseCheckersImages.VALID_MOVE,
		                          0, 0);
		}
	}

	/*
	 * Draw a point of the board.  A point is the triangle that a player
	 * start and ends at.
	 *
	 * @param g            The grahics context to draw on.
	 */
	private void drawPoints (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		for (int pt = 0; pt < 6; pt++) {
			g.setColor(pointColors[pt]);
			g.fillPolygon(pointTriangles[pt]);
		}

		g2d.setStroke(outlineStroke);
		g.setColor(blackColor);
		g.drawPolygon(bigTriangles[0]);
		g.drawPolygon(bigTriangles[1]);
	}

	/*
	 * Draw a move.  This will draw connected lines beween each of the points
	 * of the move.
	 *
 	 * @param g            The grahics context to draw on.
	 * @param theMove      The move to draw.
	 * @param moveColor    The color of the move to draw.
	 */
	private void drawMove(Graphics2D g2d, Vector theMove) {
		ListIterator iter = theMove.listIterator();
		while (iter.hasNext()) {
			g2d.draw((QuadCurve2D) iter.next());
		}
	}

	/**
	 * Set the active marble to the given location.  The active marble is
	 * the one that has been selected as the marble to move.
	 */
	public void setActiveMarbleHex(Point activePoint) {
		activeMarbleHex = activePoint;
	}

	/**
	 * Get the space where the active marble is located.
	 */
	public Point getActiveMarbleHex() {
		return activeMarbleHex;
	}

	/**
	 * Set the highlighted move to the given one.
	 *
	 * @param move      The move to make the highlighted one.
	 * @param playerId  The player whose move is being shown.  This is used
	 *                  to set the color of the line.
	 */
	public void setHighlightMove(Vector move, int playerId) {
		highlightedMoveCurves = new Vector();
		highlightedMoveColor = playerColors[playerId];

		if (move != null) {
			// Make the Quad Curves for the move

			ListIterator iter = move.listIterator();
			Point p2 = (Point) iter.next();
			while (iter.hasNext()) {
				// Get the logical end points of the move
				Point p1 = p2;
				p2 = (Point) iter.next();

				// Convert them to screen points
				Point sp1 = getScreenCenterFor(p1);
				Point sp2 = getScreenCenterFor(p2);

				// Compute the control point location for this curve.
				int dx = (sp2.x - sp1.x) / 2;
				int dy = (sp2.y - sp1.y) / 2;
				Point cp = new Point (sp1.x + dx, sp1.y + dy);
				double halfLength = Math.sqrt((dx * dx) + (dy * dy));
				double ratio = (HEXAGON_SIZE / halfLength);

				if (dx > 0) {
					cp.translate((int) (dy * ratio), (int) (- dx * ratio));
				} else {
					cp.translate((int) (- dy * ratio), (int) (dx * ratio));
				}

				// Generate the Quad curve connecting those points
				QuadCurve2D q = new QuadCurve2D.Float();
				q.setCurve(sp1, cp, sp2);

				// Add the curve to the vectors of curves
				highlightedMoveCurves.add(q);
			}
		}
	}

}
