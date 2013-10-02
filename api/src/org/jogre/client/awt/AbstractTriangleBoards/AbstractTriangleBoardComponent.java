/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Abstract Triangular Board Component
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.client.awt.AbstractTriangleBoards;

import java.awt.*;

import org.jogre.client.awt.JogreComponent;

/**
 * <p>This graphical component is a base component for triangular boards.
 * Various attributes can be set such as the number of cells, the cell
 * size and color.</p>
 *
 * <p>This supports rectangular arrays of triangles</p>
 *
 * <p>There is a single logical board layout that this component uses.
 * However, there are parameters for allowing the view of the board to
 * be manipulated to provide multiple different views.  The board may be
 * rotated in 90-degree increments and it may be flipped around horizontally.</p>
 *
 * <p>The canonical arrangement of the board of triangles is this:
 * Triangles are referred to by a (col, row) coordinate.  Columns run up/down
 * and rows run across.  Therefore, a column # is the distance across the board
 * (similar to an x-coordinate) and a row # is the distance down the board
 * (similar to a y-coordinate).  So (col, row) can be thought of as (x, y).</p>
 *
 * <p>Here is the "One True Way" of laying out triangles (logically):
 *<pre>
 *
 * +   ------------------------
 *    /\ 1,0  /\ 3,0  /\ 5,0  /\        * The + indicates the upper-left corner
 *   /  \    /  \    /  \    /  \         of the component area.
 *  /    \  /    \  /    \  /    \ 
 * / 0,0  \/ 2,0  \/ 4,0  \/ 6,0  \     * This board has 3 rows & 7 columns
 * --------------------------------
 * \ 0,1  /\ 2,1  /\ 4,1  /\ 6,1  /     * The coordinates in each triangle are
 *  \    /  \    /  \    /  \    /        the (col, row) for that triangle.
 *   \  /    \  /    \  /    \  /  
 *    \/ 1,1  \/ 3,1  \/ 5,1  \/   
 *     ------------------------
 *    /\ 1,2  /\ 3,2  /\ 5,2  /\   
 *   /  \    /  \    /  \    /  \  
 *  /    \  /    \  /    \  /    \ 
 * / 0,2  \/ 2,2  \/ 4,2  \/ 6,2  \
 * --------------------------------
 *</pre>
 *
 * The triangles are layed out such that they run in horizontal rows, with
 * horizontal bases and angled sides.</p>
 *
 * <p>The size of the triangles is determined by a bounding rectangle that is
 * provided when the board is created.  The triangle looks like this:
 *<pre>
 *    +======--------     * The outer rectangle is the bounding box.  It can
 *    |     /\      |       be stretched out to make elongated triangles.
 *    |    /  \     |     * The + in the upper left corner is the "anchor"
 *    |   /    \    |       point for a given triangle.
 *    |  /      \   |     * The distance from the anchor point to the tip of
 *    | /        \  |       the triangle (denoted by = characters) is called
 *    |/          \ |       the control line.
 *    ---------------     * Triangles that point down are similar, but inverted.
 *</pre>
 * <p> When creating a triangular board, you provide a number of parameters
 * about the board.  These parameters are: </p>
 *
 * <p> * Number of columns and number of rows.</p>
 * <p> * The bounding box, in pixels, for the triangle.</p>
 * <p> * The distance, in pixels, from the anchor point to the tip of the
 *       triangle.  This is the control line length.</p>
 *
 * <p> The odd rows are vertical mirror-images of the even rows.</p>
 *
 * <p> After creation, the view of the board can be rotated & flipped by using
 *     the method setOrientation(int rotation, boolean Hflip).
 *     Rotation is in units of 90 degrees clockwise.
 *     Hflip flips the board horizontally *after* rotation.</p>
 *
 * <p> This component allows setting of an "inset" and "outset" parameters.
 *     The inset is the distance that the upper left corner of the actual
 *     triangular board array is offset from the upper left corner of the
 *     omponent.  The outset is the distance from the lower right corner of the
 *     actual triangular board array to the lower right corner of the component.
 *     Changing these allow the board to be placed inside a component and still
 *     allow room for other things around it (such as a border.)  Note that the
 *     default drawing routine draws a 1-pixel line for the triangles.  Because
 *     these pixels are drawn below and to the right of the coordinate, the
 *     outset must be at least (1,1) in order for these borders to not be
 *     chopped off along the right & bottom of the board.  The default inset is
 *     (0,0).  The default outset is (1,1).</p>
 *
 * <p> This module provides a default paintComponent() method that will draw the
 *     board.  A subclass can override this to do custom painting of the entire
 *     board.</p>
 *
 * <p> The default paintComponent() calls a drawATriangle() method to draw each
 *     triangle of the board.  A subclass can override just this method to
 *     provide a custom drawing of each triangle, but let this class paint the
 *     board.</p>
 *
 * <p> The default drawATriangle() method calls a getTriangleColor() method to
 *     get the color that it should use to fill in the triangle.  A subclass can
 *     override just this method to change the colors of the triangles, while
 *     still letting this class paint the triangle's.</p>
 *
 * <p> See the sample game "Triangulum" for example of a game that uses this
 *     board type.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractTriangleBoardComponent extends JogreComponent {

	/** Constant to show a cell point is off the board (not selected). */
	public static final Point OFF_SCREEN_POINT = new Point (-1, -1);

	/** Size of the bounding box around a triangle (in pixels). */
	protected Dimension cellBoundingDimension;

	/** The distance from the upper left corner of the bounds to the point
	    of the triangle (in pixels). */
	protected int controlLineLength;

	/** The distance from the upper left corner of the component to the upper left
		corner of the triangular array. */
	protected Dimension inset = new Dimension (0, 0);

	/** The distance from the lower right corner of the triangular array to the
	    lower right corner of the component. */
	protected Dimension outset = new Dimension (1, 1);

	/** Number of logical rows in the board. */
	protected int numOfLogicalRows;

	/** Number of logical columns in the board. */
	protected int numOfLogicalCols;

	/** Current orientation, both rotation & flip. */
	protected int rot = 0;
	protected boolean hFlip = false;

	/** Colour of a triangle. */
	protected Color cellColour;

	/** Colour of a cell's outline. */
	protected Color outlineColour;

	/** Colour to outline the highlighted cell with. */
	protected Color highlightColour;

	/** Stores the value of a mouse down. **/
	protected Point pressedPoint = OFF_SCREEN_POINT;

	/** Stores the value of where the point is being dragged. */
	protected Point dragPoint = OFF_SCREEN_POINT;

	/** Stores the value of which point should be highlighted on the board. */
	protected Point highlightPoint = OFF_SCREEN_POINT;

	/** Representative triangles used while drawing the board using the default
	    methods here. */
	protected Polygon [][] baseTriPoly = new Polygon[2][2];
	private final static int UP = 0;
	private final static int DOWN = 1;
	private final static int EVEN_ROW = 0;
	private final static int ODD_ROW = 1;

	/** The size (in pixels) of the triangle array on the screen */
	protected Dimension arrayDimension;

	/**
	 * Constructor for a triangular array.
	 *
	 * @param numOfCols           Number of columns in the board.
	 * @param numOfRows           Number of rows in the board.
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each triangle.
	 */
	public AbstractTriangleBoardComponent (
		int numOfCols,
		int numOfRows,
		Dimension cellDim,
		int controlLineLength
	) {
		super();

		// Save the parameters
		this.numOfLogicalRows = numOfRows;
		this.numOfLogicalCols = numOfCols;
		this.cellBoundingDimension = new Dimension (cellDim);
		this.controlLineLength = controlLineLength;

		// Calculate parameters
		setOrientation(0, false);

		// Determine the size of the board and set the component to that size.
		setPreferredSize ( getBoardComponentDim () );

		// Set up colours to default colours
		setColours (
			new Color (178, 178, 178),
			new Color (0, 0, 0),
			new Color (255, 255, 0)
		);
	}

	/**
	 * Given the current values of the board, this will return the size of
	 * the entire board in pixels.  This includes the inset & outset borders
	 * around the array.
	 */
	public Dimension getBoardComponentDim () {
		// Add the inset & outset dimensions to the array.
		return new Dimension (
		    arrayDimension.width  + inset.width + outset.width,
		    arrayDimension.height + inset.height + outset.height);
	}

	/*
	 * Calculate the dimensions, in pixels, of the triangle array.
	 * This is only the triangle array dimensions and does not include
	 * any additional space around the array.
	 *
	 * @return a dimension of the triangle array
	 */
	private Dimension calcTriangleArrayDimension () {
		// Calculate height of the board
		int height = cellBoundingDimension.height * numOfLogicalRows;

		// Calculate width of the board
		int width = ((numOfLogicalCols + 1) >> 1) * cellBoundingDimension.width;
		if ((numOfLogicalCols & 0x01) == 0) {
			// Even # of columns, so add the last triangle in the row
			width += controlLineLength;
		}

		// Return the correct dimension, depending on rotation of the board
		if ((rot == 1) || (rot == 3)) {
			return new Dimension (height, width);
		} else {
			return new Dimension (width, height);
		}
	}

	/**
	 * This will create the polygons that describe the 4 triangles for the
	 * board.  The (0,0) point of the polygon lies at the anchorPoint
	 * for the triangle at space (0,0) of the board.
	 */
	private void createBaseTriPolys () {
		int [] upXPoints;
		int [] upYPoints;
		int [] downXPoints;
		int [] downYPoints;
		int flipSize;

		switch (rot) {
			case 0:
				upXPoints   = new int [] {controlLineLength, cellBoundingDimension.width, 0};
				upYPoints   = new int [] {0, cellBoundingDimension.height, cellBoundingDimension.height};
				downXPoints = new int [] {0, cellBoundingDimension.width, cellBoundingDimension.width - controlLineLength};
				downYPoints = new int [] {0, 0, cellBoundingDimension.height};
				flipSize = cellBoundingDimension.width;
				break;
			case 1:
				upXPoints   = new int [] {0, 0, cellBoundingDimension.height};
				upYPoints   = new int [] {0, cellBoundingDimension.width, controlLineLength};
				downXPoints = new int [] {0, cellBoundingDimension.height, cellBoundingDimension.height};
				downYPoints = new int [] {cellBoundingDimension.width - controlLineLength, cellBoundingDimension.width, 0};
				flipSize = cellBoundingDimension.height;
				break;
			case 2:
				downXPoints = new int [] {controlLineLength, cellBoundingDimension.width, 0};
				downYPoints = new int [] {0, cellBoundingDimension.height, cellBoundingDimension.height};
				upXPoints   = new int [] {0, cellBoundingDimension.width, cellBoundingDimension.width - controlLineLength};
				upYPoints   = new int [] {0, 0, cellBoundingDimension.height};
				flipSize = cellBoundingDimension.width;
				break;
			case 3:
			default:
				downXPoints = new int [] {0, 0, cellBoundingDimension.height};
				downYPoints = new int [] {0, cellBoundingDimension.width, controlLineLength};
				upXPoints   = new int [] {0, cellBoundingDimension.height, cellBoundingDimension.height};
				upYPoints   = new int [] {cellBoundingDimension.width - controlLineLength, cellBoundingDimension.width, 0};
				flipSize = cellBoundingDimension.height;
				break;
		}

		if (hFlip) {
			for (int i=0; i<3; i++) {
				upXPoints[i]   = flipSize - upXPoints[i];
				downXPoints[i] = flipSize - downXPoints[i];
			}
		}

		// Generate even row'ed triangles
		baseTriPoly[UP  ][EVEN_ROW] = new Polygon (upXPoints,   upYPoints,   3);
		baseTriPoly[DOWN][EVEN_ROW] = new Polygon (downXPoints, downYPoints, 3);

		// Flip for odd rows
		if ((rot == 0) || (rot == 2)) {
			for (int i=0; i<3; i++) {
				upYPoints[i]   = cellBoundingDimension.height - upYPoints[i];
				downYPoints[i] = cellBoundingDimension.height - downYPoints[i];
			}
		} else {
			for (int i=0; i<3; i++) {
				upXPoints[i]   = cellBoundingDimension.height - upXPoints[i];
				downXPoints[i] = cellBoundingDimension.height - downXPoints[i];
			}
		}

		// Generate odd row'ed triangles
		baseTriPoly[DOWN][ODD_ROW] = new Polygon (upXPoints,   upYPoints,   3);
		baseTriPoly[UP  ][ODD_ROW] = new Polygon (downXPoints, downYPoints, 3);
	}

	/**
	 * Give a new inset dimension to the component.  The inset is the
	 * distance from the upper left corner of the component to the upper
	 * left corner of the triangular array.
	 *
	 * @param newInset   The new inset to use with the component.
	 */
	public void setInset (Dimension newInset) {
		this.inset = new Dimension (newInset);
	}

	/**
	 * Give a new outset dimension to the component.  The outset is the
	 * distance from the lower right corner of the triangular array to the lower
	 * right corner of the component.
	 *
	 * Outsets can be used to place the array within a larger component, or
	 * to increase the size of the component to allow space for drawing outlines
	 * of triangles.  The default outset is (1, 1) so that all triangles can
	 * be outlined with a width 1 stroke and still show up.
	 *
	 * @param newOutset   The new outset to use with the component.
	 */
	public void setOutset (Dimension newOutset) {
		this.outset = new Dimension (newOutset);
	}

	/**
	 * Sets the orientation of the visual view of the board to the
	 * given parameters.
	 *
	 * @param rotation    Number of 90-degree clockwise rotations to rotate
	 *                    the board.
	 * @param flip        If true, then the board will be flipped horizontally
	 *                    after the rotation.
	 * @return a value that indicates if the visual board size has changed
	 *         as a result of this orientation change.
	 */
	public boolean setOrientation (int rotation, boolean flip) {
		Dimension oldDim = arrayDimension;

		// Save the values provided.
		rot = (rotation & 0x03);    // Only values 0-3 matter for rotation.
		hFlip = flip;

		// Create the new base triangle polygons;
		createBaseTriPolys();

		// Calculate the new dimensions of the board
		arrayDimension = calcTriangleArrayDimension();

		// Tell if we've changed dimensions
		return (!arrayDimension.equals(oldDim));
	}


	/**
	 * Draws the board.
	 * This method calls the drawATriangle() method to draw each triangle.
	 *
	 * To customize the board drawing, the subclass can override
	 * either this entire method, or just the drawATriangle() method.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the board
		for (int r=0; r < numOfLogicalRows; r++) {
			for (int c=0; c < numOfLogicalCols; c++) {
				if (existsOnBoard (c, r)) {
					drawATriangle (g, c, r);
				}
			}
		}

		// Draw the highlighted triangle (if there is one)
		if (existsOnBoard(highlightPoint)) {
			Point highlightAnchor = getScreenAnchorFor (highlightPoint);
			int highlightRow = (highlightPoint.y) & 0x01;
			Polygon highlightPoly =
			     isUpwardTriangle (highlightPoint) ?
			          baseTriPoly[0][highlightRow] :
			          baseTriPoly[1][highlightRow];
			highlightPoly.translate (highlightAnchor.x, highlightAnchor.y);
			g.setColor (this.highlightColour);
			g.drawPolygon (highlightPoly);
			highlightPoly.translate (-highlightAnchor.x, -highlightAnchor.y);
		}
	}

	/**
	 * Draws a Triangle on the board.
	 * This calls the getTriangleColor() method to get the color to
	 * be used to fill in each triangle.
	 *
	 * To customize the board drawing, the subclass can override this
	 * method to draw board spaces however it would like to.  Or, the
	 * subclass can override the getTriangleColor() method to just change
	 * the color used to draw the triangle.
	 *
	 * @param g            The grahics context to draw on.
	 * @param col          The column of the triangle to draw.
	 * @param row          The row of the triangle to draw.
	 */
	public void drawATriangle (Graphics g, int col, int row) {
		// Get the anchor point for the triangle
		Point anchorPoint = getScreenAnchorFor (col, row);

		// Determine the correct base polygon to use
		Polygon basePoly = isUpwardTriangle (col, row) ?
		                         baseTriPoly[0][row & 0x01] :
		                         baseTriPoly[1][row & 0x01];

		// Move the base poly to the right point on the screen
		basePoly.translate (anchorPoint.x, anchorPoint.y);

		// Fill it with the cell colour
		g.setColor (getTriangleColor (col, row));
		g.fillPolygon (basePoly);

		// Outline it in the outline colour
		g.setColor (outlineColour);
		g.drawPolygon (basePoly);

		// Move the base polygon back to to the origin
		basePoly.translate (-anchorPoint.x, -anchorPoint.y);
	}

	/**
	 * This method will determine if the given triangle is an "up" triangle or
	 * not.  The up-ness computed by this is a logical property of the triangle
	 * not a physical one.  That means that rotation & flipping is not taken
	 * into account.  This should be used for logical calculations such as
	 * determining neighbors.
	 *
	 * @param col          The column of the triangle to draw.
	 * @param row          The row of the triangle to draw.
	 * @return   true  => The triangle is "up"
	 *           false => The triangle is "down"
	 */
	public boolean isUpwardTriangle (int col, int row) {
		return (((col + row) & 0x01) == 0);
	}

	/**
	 * This method will determine if the given triangle is an "up" triangle or
	 * not.  The up-ness computed by this is a logical property of the triangle
	 * not a physical one.  That means that rotation & flipping is not taken
	 * into account.  This should be used for logical calculations such as
	 * determining neighbors.
	 *
	 * @param boardPoint   The logical point on the board.
	 * @return   true  => The triangle is "up"
	 *           false => The triangle is "down"
	 */
	public boolean isUpwardTriangle (Point boardPoint) {
		return isUpwardTriangle (boardPoint.x, boardPoint.y);
	}

	/**
	 * This method returns the color that should be used to fill in the space
	 * at the given logical col, row.
	 *
	 * This standard method just returns the basic cellColour provided.
	 *
	 * To customize the board drawing, a subclass can override this method
	 * and use the col, row to determine what color the triangle should be filled
	 * with.
	 *
	 * @param (col, row)   The logical position of the triangle on the board,
	 *                     whose color is wanted.
	 * @return the color that the given triangle should be drawn with.
	 */
	public Color getTriangleColor (int col, int row) {
		return this.cellColour;
	}

	/**
	 * Determine if the triangle at the given (col, row) exists on the board.
	 *
	 * @param (col, row)    The location to check for existance.
	 * @return true => triangle is on the board.
	 */
	public boolean existsOnBoard (int col, int row) {
		if ((col < 0) || (col >= numOfLogicalCols) ||
		    (row < 0) || (row >= numOfLogicalRows)) {
			return false;
		}

		return true;
	}

	/**
	 * Determine if the triangle at the given logical board point exists
	 * on the board.
	 *
	 * @param boardPoint    The location to check for existance.
	 * @return true => triangle is on the board.
	 */
	public boolean existsOnBoard (Point boardPoint) {
		return existsOnBoard(boardPoint.x, boardPoint.y);
	}

	/**
	 * This method returns a screen coordinate (in the top-left) from a board
	 * point.
	 *
	 * @param boardPoint   Point on board (col, row) coordinates.
	 * @return             Screen coordinates for the anchor point of the hex.
	 */
	public Point getScreenAnchorFor (Point boardPoint) {
		return getScreenAnchorFor(boardPoint.x, boardPoint.y);
	}

	/**
	 * This method returns a screen coordinate (in the top-left) from a board
	 * point.
	 *
	 * @param col    0 < col < numOfColumns
	 * @param row    0 < row < numOfRows
	 * @return          Screen coordinates for the anchor point of the triangle.
	 */
	public Point getScreenAnchorFor (int col, int row) {
		Point anchorPoint;
		int flipSize;

		// Calculate point based on non-rotated / non-flipped orientation
		int x = (col >> 1) * cellBoundingDimension.width +
		        (col & 0x01) * controlLineLength;
		int y = row * cellBoundingDimension.height;

		// Apply rotation
		switch (rot) {
			case 0:
				anchorPoint = new Point (x, y);
				flipSize = cellBoundingDimension.width;
				break;
			case 1:
				anchorPoint = new Point (arrayDimension.width - y - cellBoundingDimension.height, x);
				flipSize = cellBoundingDimension.height;
				break;
			case 2:
				anchorPoint = new Point (arrayDimension.width - x - cellBoundingDimension.width, arrayDimension.height - y - cellBoundingDimension.height);
				flipSize = cellBoundingDimension.width;
				break;
			case 3:
			default:
				anchorPoint = new Point (y, arrayDimension.height - x - cellBoundingDimension.width);
				flipSize = cellBoundingDimension.height;
				break;
		}

		// Apply flip
		if (hFlip) {
			anchorPoint.setLocation(arrayDimension.width - anchorPoint.x - flipSize, anchorPoint.y);
		}

		// Finally, add the inset
		anchorPoint.translate(inset.width, inset.height);

		return anchorPoint;
	}

	/**
	 * This methods returns a board coordinate from a screen point.
	 *
	 * @param screenX   X coordinate in pixels.
	 * @param screenY   Y coordinate in pixels.
	 * @return          Return board coordinates
	 */
	public Point getBoardCoords (int screenX, int screenY) {
		int temp;
		int oX = screenX;
		int oY = screenY;

		// Remove the inset to get the coordinates in the array itself.
		screenX -= inset.width;
		screenY -= inset.height;

		// Undo flip
		if (hFlip) {
			screenX = arrayDimension.width - screenX;
		}

		// Undo rotation
		switch (rot) {
			case 0:
				break;
			case 1:
				temp = screenX;
				screenX = screenY;
				screenY = arrayDimension.width - temp;
				break;
			case 2:
				screenX = arrayDimension.width - screenX;
				screenY = arrayDimension.height - screenY;
				break;
			case 3:
			default:
				temp = screenX;
				screenX = arrayDimension.height - screenY;
				screenY = temp;
				break;
		}

		// Calculate the board position based on non-rotated / non-flipped orientation.
		int row = (screenY / cellBoundingDimension.height);
		int col = (screenX / cellBoundingDimension.width) * 2;

		int offx = (screenX % cellBoundingDimension.width);
		int offy = (screenY % cellBoundingDimension.height);

		// Flip over odd rows
		if ((row & 0x01) != 0) {
			offy = cellBoundingDimension.height - offy;
		}

		int param1 = (controlLineLength * (cellBoundingDimension.height - offy)) -
		             cellBoundingDimension.height * offx;
		if (param1 > 0) {
			// In upper-left corner of cell
			return new Point (col - 1, row);
		}

		int param2 = (cellBoundingDimension.height * (offx - controlLineLength)) -
		             (offy * (cellBoundingDimension.width - controlLineLength));
		if (param2 > 0) {
			// In upper-right corner of cell
			return new Point (col + 1, row);
		}

		// In main rectangle
		return new Point (col, row);
	}

	/**
	 * Return the size of each triangle bounding box in pixels.
	 *
	 * @return  bounding dimensions of the triangle.
	 */
	public Dimension getTriangleSize () {
		return this.cellBoundingDimension;
	}

	/**
	 * Return the point where a user has pressed on the board.
	 *
	 * @return    Logical point where user has pressed.
	 */
	public Point getPressedPoint () {
		return this.pressedPoint;
	}

	/**
	 * Return the drag point of a mouse.
	 *
	 * @return   Logical point where user has dragged.
	 */
	public Point getDragPoint () {
		return this.dragPoint;
	}

	/**
	 * Return the highlighted point.
	 *
	 * @param highlightPoint     Logical point on board that is highlighted
	 */
	public Point getHighlightPoint () {
		return this.highlightPoint;
	}

	/**
	 * Sets pressed point in the board.
	 *
	 * @param pressedPoint   Logical point on board where pressed.
	 */
	public void setPressedPoint (Point pressedPoint) {
		this.pressedPoint = pressedPoint;
	}

	/**
	 * Sets the drag point.
	 *
	 * @param dragPoint     Logical point on board where dragged.
	 */
	public void setDragPoint (Point dragPoint) {
		this.dragPoint = dragPoint;
	}

	/**
	 * Sets the highlighted point.
	 *
	 * @param highlightPoint    Logical point on board to be highlighted
	 * @return true if this point is different from the old highlight point.
	 *         false if this point is the same as the old highlighted point.
	 */
	public boolean setHighlightPoint (Point newHighlightPoint) {
		if (!newHighlightPoint.equals (this.highlightPoint)) {
			this.highlightPoint = newHighlightPoint;
			repaint();
			return true;
		}
		return false;
	}

	/**
	 * Reset the dragPoint and pressedPoint variables.
	 */
	public void resetPoints () {
		this.dragPoint = OFF_SCREEN_POINT;
		this.pressedPoint = OFF_SCREEN_POINT;
		setHighlightPoint (OFF_SCREEN_POINT);
	}

	/**
	 * Set up colours for this board.
	 *
 	 * @param cellColour             Colour for the triangles.
 	 * @param outlineColour          Colour to outline the triangles.
	 * @param highlightColour        Colour to highlight the triangles.
	 */
	public final void setColours (Color cellColour, Color outlineColour, Color highlightColour) {
		this.cellColour = cellColour;
		this.outlineColour = outlineColour;
		this.highlightColour = highlightColour;
	}

}