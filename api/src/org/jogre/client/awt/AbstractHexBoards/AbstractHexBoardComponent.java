/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.client.awt.AbstractHexBoards;

import org.jogre.client.awt.*;

import java.awt.*;
import java.util.Observer;

/**
 * <p>This graphical component is a base component for hexagonal boards.
 * Various attributes can be set such as the number of cells, the cell
 * size and color.</p>
 *
 * <p>This supports rectangular arrays of hexagons</p>
 *
 * <p>There is a single logical board layout that this component uses.
 * However, there are parameters for allowing the view of the board to
 * be manipulated to provide multiple different views.  The board may be
 * rotated in 90-degree increments; it may be flipped around; it may have
 * half the top and/or bottom hexagons trimmed off.</p>
 *
 * <p>The canonical arrangement of the board of hexagons is this:
 * Hexagons are referred to by a (col, row) coordinate.  Columns run up/down
 * and rows run across.  Therefore, a column # is the distance across the board
 * (similar to an x-coordinate) and a row # is the distance down the board
 * (similar to a y-coordinate).  So (col, row) can be thought of as (x, y).</p>
 *
 * <p>Here is the "One True Way" of laying out hexagons (logically):
 *<pre>
 *  (0, 0)          (2, 0)          (4, 0)
 *          (1, 0)          (3, 0)          (5, 0)
 *  (0, 1)          (2, 1)          (4, 1)
 *          (1, 1)          (3, 1)          (5, 1)
 *  (0, 2)          (2, 2)          (4, 2)
 *          (1, 2)          (3, 2)          (5, 2)
 *</pre>
 *
 * The hexagons are layed out such that their top & bottom lines are horizontal
 * and the side lines are angled.  This is referred to as the "horizontal" configuration.
 * If the board is rotated by 90 or 270 degrees, then the hexagons will have vertical
 * sides and angled tops & bottoms.  This is referred to as the "vertical" configuration.</p>
 *
 * <p>The size of the hexagons is determined by a bounding rectangle that is
 * provided when the board is created.  The Hexagon looks like this:
 *<pre>
 *    +---=======----       * The outer rectangle is the bounding box.  It can
 *    |  /       \  |         be stretched out to make elongated hexagons.
 *    | /         \ |       * The length of the top & bottom sides (shown by
 *    |/           \|         the = characters) is provided.  It is centered
 *    |\           /|         along the top & bottom of the bounding box.
 *    | \         / |       * The center of the left & right sides are where the
 *    |  \       /  |         hexagon touches the bounding box.
 *    ----=======----       * The + in the upper left corner is the "anchor" point
 *                            for a given hexagon.
 *</pre>
 * <p>When creating a hex board, you provide a number of parameters about the
 * board.  These parameters are: </p>
 *
 * <p> * Number of columns and number of rows.</p>
 * <p> * A trimLow parameter, that, when true, will cause the hexagons in row 0
 *       in even columns to be removed from the board.   These are the hexagons
 *       at locations (0,0), (2,0), (4,0), etc...</p>
 * <p> * A trimHigh parameter, that, when true, will cause the hexagons in the
 *       last row in odd columns to be removed from the board.  For the above
 *       board, these are the hexagons at locations (1,2), (3,2), and (5,2).</p>
 * <p> * The bounding box & length of the top side.  These are in pixels and
 *       determine the screen size of the board.</p>
 *
 * <p>After creation, the view of the board can be rotated & flipped by using
 *     the method setOrientation(int rotation, boolean flip).  Rotation is in
 *     units of 90 degrees clockwise.  If flip is true, then the board will be
 *     flipped in the row direction.  (So, setOrientation(0, true) will result
 *     in space (0,2) being in the upper left corner.)</p>
 *
 * <p>This component allows setting of an "inset" and "outset" parameters.
 *     The inset is the distance that the upper left corner of the actual hexagonal
 *     board array is offset from the upper left corner of the component.
 *     The outset is the distance from the lower right corner of the actual hexagonal
 *     board array to the lower right corner of the component.
 *     Changing these allow the board to be placed inside a component and still
 *     allow room for other things around it (such as a border.)  Note that the
 *     default drawing routine draws a 1-pixel border around the hexagons.  Because
 *     these pixels are drawn below and to the right of the coordinate, the outset
 *     must be at least (1,1) in order for these borders to be not chopped off
 *     along the right & bottom of the board.  The default inset is (0,0).  The
 *     default outset is (1,1).</p>
 *
 * <p>This module provides a default paintComponent() method that will draw the
 *     board.  A subclass can override this to do custom painting of the entire
 *     board.</p>
 *
 * <p>The default paintComponent() calls a drawAHex() method to draw each
 *     hexagon of the board.  A subclass can override just this method to
 *     provide a custom drawing of each hex, but let this class paint the board.</p>
 *
 * <p>The default drawAHex() method calls a getHexColor() method to get the
 *     color that it should use to fill in the hex.  A subclass can override
 *     just this method to change the colors of the hex, but still letting
 *     this class paint the hex's.</p>
 *
 * <p>See the sample game "hex" for example of a game that uses this board type.</p>
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public abstract class AbstractHexBoardComponent extends JogreComponent implements Observer {

	/** Constant to show a cell point is off the board (not selected). */
	public static final Point OFF_SCREEN_POINT = new Point (-1, -1);

	/** Size of the bounding box around the hexagons (in pixels). */
	protected Dimension cellBoundingDimension;

	/** The length of the controlling line of the hex (in pixels).  */
	protected int controlLineLength;

	/** The distance from the upper left corner of the component to the upper left
		corner of the hex array. */
	protected Dimension inset = new Dimension (0, 0);

	/** The distance from the lower right corner of the hex array to the lower
	    right corner of the component. */
	protected Dimension outset = new Dimension (1, 1);

	/** Number of logical rows in the board. */
	protected int numOfLogicalRows;

	/** Number of logical columns in the board. */
	protected int numOfLogicalCols;

	/** Flag that indicates if the logical top hex's are trimmed. */
	protected boolean logicalTrimLow;

	/** Flag that indicates if the logical bottom hex's are trimmed. */
	protected boolean logicalTrimHigh;

	/** Current orientation, both rotation & flip. */
	protected int rot = 0;
	protected boolean userFlip = false;

	/** Colour of a cell. */
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

	/** A representative hexagon used while drawing the board using the default
	    methods here. */
	protected Polygon baseHexPoly;

	/** Horizontal & Vertical hexagons.
	   baseHexPoly is set to one of these depending on the orientation of the board. */
	protected Polygon HBaseHexPoly, VBaseHexPoly;

	/* The following fields are computed from above items and are not directly
	   set by the user. */

	/** The distance from the upper/left corner of the bounding box to the start
	    of the controlling line. */
	protected int boundInset;

	/** The width of the "superRect" that encloses two hexes.  It is used
	    to convert between board & screen coordinates. */
	protected int superWidth;

	/** Half of the height & width of the bounding box.
	    These are computed once and then saved rather than dividing by 2 alot. */
	protected int halfHeight;
	protected int halfWidth;

	/* i_hh is equal to (boundInset * halfHeight).
	   w_hh is equal to (boundWidth * halfHeight).
	   These are used to determine on which side of the sloping hexagon border
	   a pixel lies, but since they are constants that only depend on the
	   size of the hexagon, they are computed just once when the board is
	   created rather than everytime a screen point is translated. */
	private int i_hh;
	private int w_hh;

	/** Flag that indicates the orientation of each individual hexagon.
	    False => Hexagons have their controlling line running horizontally.
		True  => Hexagons have their controlling line running vertically.
	*/
	protected boolean verticalOrientation = false;

	/* Flag that indicates if the physical top (or left) hex's are trimmed. */
	private boolean physicalTrimLow;

	/* Flag that indicates if the physical bottom (or right) hex's are trimmed. */
	private boolean physicalTrimHigh;

	/* Flags that indicate if columns and/or rows should be inverted during the
	   logical to physical conversion process. */
	private boolean invertCols, invertRows;

	/* Amount to offset odd & even rows when doing the logical to physical
	   conversion. */
	private int oddRowOffset, evenRowOffset;

	/**
	 * Constructor for a hex array.
	 *
	 * @param numOfCols           Number of columns in the board.
	 * @param numOfRows           Number of rows in the board.
	 * @param cellDim             The bounding box for each cell.
	 * @param controlLineLength   The length of the control line of each hexagon.
	 * @param trimLow             If true, trim the top row of hex's.
	 * @param trimHigh            If true, trim the bottom row of hex's.
	 */
	public AbstractHexBoardComponent (
		int numOfCols,
		int numOfRows,
		Dimension cellDim,
		int controlLineLength,
		boolean trimLow,
		boolean trimHigh
	) {
		super();

		// Save the parameters
		this.numOfLogicalRows = numOfRows;
		this.numOfLogicalCols = numOfCols;
		this.cellBoundingDimension = new Dimension (cellDim);
		this.controlLineLength = controlLineLength;
		this.logicalTrimLow = trimLow;
		this.logicalTrimHigh = trimHigh;

		// Calculate parameters
		this.boundInset = (cellBoundingDimension.width - controlLineLength) / 2;
		this.superWidth =  cellBoundingDimension.width + controlLineLength;

		this.halfHeight = cellBoundingDimension.height / 2;
		this.halfWidth  = cellBoundingDimension.width  / 2;
		this.i_hh = this.boundInset * this.halfHeight;
		this.w_hh = cellBoundingDimension.width * this.halfHeight;


		// Create the hexagon polygons.
		createBaseHexPolys ();

		// Set the physical parameters from the logical ones.
		setPhysicalParamsFromLogical();

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
	 * This will create the polygons that describe a hexagon for the
	 * board.  The (0,0) point of the polygon lies at the basePoint
	 * for the hexagon at space (0,0) of the board.
	 */
	private void createBaseHexPolys() {
		int [] xPoints = {0, 0, 0, 0, 0, 0};
		int [] yPoints = {0, 0, 0, 0, 0, 0};

		int p2 = cellBoundingDimension.width - boundInset;

		// Set the points for the polygon
		xPoints[0] = xPoints[4] = boundInset;
		xPoints[1] = xPoints[3] = p2;
		xPoints[2] = cellBoundingDimension.width;

		yPoints[2] = yPoints[5] = halfHeight;
		yPoints[3] = yPoints[4] = cellBoundingDimension.height;

		HBaseHexPoly = new Polygon(xPoints, yPoints, 6);
		VBaseHexPoly = new Polygon(yPoints, xPoints, 6);
	}

	/**
	 * Given the current values of the board, this will return the size of
	 * the entire board in pixels.  This includes the inset & outset borders
	 * around the array.
	 *
	 * @return a dimension of the whole board
	 */
	public Dimension getBoardComponentDim() {
		Dimension hexArraySize = calcHexArrayDimension();

		if (verticalOrientation) {
			invertDimension(hexArraySize);
		}

		// Add the inset & outset dimensions to the array.
		hexArraySize.setSize(hexArraySize.width  + inset.width + outset.width,
		                     hexArraySize.height + inset.height + outset.height);

		return hexArraySize;
	}

	/*
	 * Calculate the dimensions, in pixels, of the hex array.
	 * This is only the hex array dimensions and does not include
	 * any additional space around the array.
	 *
	 * @return a dimension of the hex array
	 */
	private Dimension calcHexArrayDimension() {
		// Calculate height of the board
		int heightHalves = 2*numOfLogicalRows + 1;
		heightHalves -= (logicalTrimLow ? 1 : 0);
		heightHalves -= (logicalTrimHigh ? 1 : 0);

		int height = ((cellBoundingDimension.height * heightHalves) + 1) / 2;

		// Calculate width of the board
		int numWholeWidths = (numOfLogicalCols + 1) / 2;
		int numSmallWidth = numOfLogicalCols - numWholeWidths;

		int width = (numWholeWidths * cellBoundingDimension.width) + (numSmallWidth * controlLineLength);

		return new Dimension (width, height);
	}

	/**
	 * Give a new inset dimension to the component.  The inset is the
	 * distance from the upper left corner of the component to the upper
	 * left corner of the hex array.
	 *
	 * @param newInset   The new inset to use with the component.
	 */
	public void setInset(Dimension newInset) {
		this.inset = new Dimension (newInset);
	}

	/**
	 * Give a new outset dimension to the component.  The outset is the
	 * distance from the lower right corner of the hex array to the lower
	 * right corner of the component.
	 *
	 * Outsets can be used to place the array within a larger component, or
	 * to increase the size of the component to allow space for drawing outlines
	 * of hexagons.  The default outset is (1, 1) so that all hexagons can
	 * be outlined with a width 1 stroke and still show up.
	 *
	 * @param newOutset   The new outset to use with the component.
	 */
	public void setOutset(Dimension newOutset) {
		this.outset = new Dimension (newOutset);
	}

	/**
	 * Draws the board.
	 * This method calls the drawAHex() method to draw each hex.
	 *
	 * To customize the board drawing, the subclass can override
	 * either this entire method, or just the drawAHex() method.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// Draw the board
		for (int r=0; r < numOfLogicalRows; r++) {
			for (int c=0; c < numOfLogicalCols; c++) {
				if (existsOnBoard(c, r)) {
					drawAHex(g, c, r, getScreenAnchorFor(c, r));
				}
			}
		}

		// Draw the highlighted hex (if there is one)
		if (existsOnBoard(highlightPoint)) {
			Point highlightAnchor = getScreenAnchorFor (highlightPoint);
			this.baseHexPoly.translate(highlightAnchor.x, highlightAnchor.y);
			g.setColor (this.highlightColour);
			g.drawPolygon (this.baseHexPoly);
			this.baseHexPoly.translate(-highlightAnchor.x, -highlightAnchor.y);
		}
	}

	/**
	 * Draws a Hexagon on the board.
	 * This calls the getHexColor() method to get the color to
	 * be used to fill in each hex.
	 *
	 * To customize the board drawing, the subclass can override this
	 * method to draw board spaces however it would like to.  Or, the
	 * subclass can override the getHexColor() method to just change
	 * the color used to draw the hex.
	 *
	 * @param g            The grahics context to draw on.
	 * @param col          The column of the hex to draw.
	 * @param row          The row of the hex to draw.
	 * @param anchorPoint  The anchor point, in pixel coordinates, of where to
	 *                     draw the hex at.
	 */
	public void drawAHex(Graphics g, int col, int row, Point anchorPoint) {
		// Move the base hex to the right point on the screen
		this.baseHexPoly.translate(anchorPoint.x, anchorPoint.y);

		// Fill it with the cell colour
		g.setColor (getHexColor(col, row));
		g.fillPolygon (this.baseHexPoly);

		// Outline it in the outline colour
		g.setColor (this.outlineColour);
		g.drawPolygon (this.baseHexPoly);

		// Move the base hex back to to the origin
		this.baseHexPoly.translate(-anchorPoint.x, -anchorPoint.y);
	}

	/**
	 * This method returns the color that should be used to fill in the space
	 * at the given logical col, row.
	 *
	 * This standard method just returns the basic cellColour provided.
	 *
	 * To customize the board drawing, a subclass can override this method
	 * and use the col, row to determine what color the hex should be filled
	 * with.
	 *
	 * @param (col, row)   The logical position of the hex on the board, whose
	 *                     color is wanted.
	 * @return the color that the given hex should be drawn with.
	 */
	public Color getHexColor(int col, int row) {
		return this.cellColour;
	}

	/**
	 * Sets the orientation of the visual view of the board to the
	 * given parameters.
	 *
	 * @param rotation    Number of 90-degree clockwise rotations to rotate
	 *                    the board.
	 * @param flip        If true, then the board has rows flipped.
	 * @return a value that indicates if the visual board size has changed
	 *         as a result of this orientation change.
	 */
	public boolean setOrientation(int rotation, boolean flip) {
		// Save the values provided.
		this.rot = (rotation & 0x03);	// Only values 0-3 matter for rotation.
		this.userFlip = flip;

		return setPhysicalParamsFromLogical();
	}

	/* These are data tables that are used to look up the properties used
	 * to convert logical to physical coordinates for various combinations of
	 * trim, flip & rotation.
	 */
	private boolean [] vertOrientTable = {false, true, false, true};
	private boolean [] invertColsTable = {false, false, true, true};
	private boolean [][] invertRowsTable = {
		{false, true, true, false},
		{true, false, false, true}};

	private int [][][][] oddRowTable = {
		{ { {0,1,1,1}, {0,1,1,0} }, { {1,0,1,1}, {1,0,0,1} } },     // logicalTrim h,l = f,f
		{ { {0,1,1,0}, {0,1,1,0} }, { {1,0,0,1}, {1,0,0,1} } },     // logicalTrim h,l = f,t
		{ { {0,2,1,1}, {0,2,2,0} }, { {2,0,1,1}, {2,0,0,2} } },     // logicalTrim h,l = t,f
		{ { {0,2,1,0}, {0,2,2,0} }, { {2,0,0,1}, {2,0,0,2} } }      // logicalTrim h,l = t,t
	};
	private int [][][][] evenRowTable = {
		{ { {0,0,1, 0}, {0,0,0,0} }, { {0,0, 0,1}, {0,0,0,0} } },   // logicalTrim h,l = f,f
		{ { {0,0,1,-1}, {0,0,0,0} }, { {0,0,-1,1}, {0,0,0,0} } },   // logicalTrim h,l = f,t
		{ { {0,1,1, 0}, {0,1,1,0} }, { {1,0, 0,1}, {1,0,0,1} } },   // logicalTrim h,l = t,f
		{ { {0,1,1,-1}, {0,1,1,0} }, { {1,0,-1,1}, {1,0,0,1} } }    // logicalTrim h,l = t,t
	};

	private boolean [][][][] physicalTrimLowTable = {
		// logicalTrim h,l = f,f
		{ { {false, true, false, true}, {false, true, true, false} }, // userFlip = False; even, odd
		  { {true, false, true, false}, {true, false, false, true} }  // userFlip = True; even, odd
		},

		// logicalTrim h,l = f,t
		{ { {true, true, false, false}, {true, true, true, true} },   // userFlip = False; even, odd
		  { {true, true, false, false}, {true, true, true, true} }    // userFlip = True; even, odd
		},

		// logicalTrim h,l = t,f
		{ { {false, false, true, true}, {false, false, false, false} }, // userFlip = False; even, odd
		  { {false, false, true, true}, {false, false, false, false} }  // userFlip = True; even, odd
		},

		// logicalTrim h,l = t,t
		{ { {true, false, true, false}, {true, false, false, true} },	// userFlip = False; even, odd
		  { {false, true, false, true}, {false, true, true, false} }	// userFlip = True; even, odd
		}
	};

	private boolean [][][][] physicalTrimHighTable = {
		// logicalTrim h,l = f,f
		{ { {false, true, false, true}, {false, true, true, false} },	// userFlip = False; even, odd
		  { {true, false, true, false}, {true, false, false, true} }	// userFlip = True; even, odd
		},

		// logicalTrim h,l = f,t
		{ { {false, false, true, true}, {false, false, false, false} },	// userFlip = False; even, odd
		  { {false, false, true, true}, {false, false, false, false} }	// userFlip = True; even, odd
		},

		// logicalTrim h,l = t,f
		{ { {true, true, false, false}, {true, true, true, true} },	// userFlip = False; even, odd
		  { {true, true, false, false}, {true, true, true, true} }	// userFlip = True; even, odd
		},

		// logicalTrim h,l = t,t
		{ { {true, false, true, false}, {true, false, false, true} },	// userFlip = False; even, odd
		  { {false, true, false, true}, {false, true, true, false} }	// userFlip = True; even, odd
		}
	};

	/*
	 * This routine will convert the logical TrimLow, TrimHigh, userFlip & rotation
	 * values that are user visible into the various parameters that are used to
	 * do the conversion from logical board space to physical board space.
	 * This is mainly just table lookup, since the calculations are pretty obtuse
	 * and it's easier just to build the table once, since they're not *that* big.
	 *
	 * @return a boolean that indicates if the dimensions of the physical display
	 *         are changed by the new settings.
	 */
	private boolean setPhysicalParamsFromLogical() {
		// Save old orientation so we can tell the caller if the orientation has
		// changed.
		boolean oldVertOrient = verticalOrientation;

		// Calc. indexs into the tables
		int columnIndex = (numOfLogicalCols & 0x01);
		int userFlipIndex = (userFlip ? 1 : 0);
		int logicalTrimIndex = (logicalTrimLow ? 1 : 0) + (logicalTrimHigh ? 2 : 0);

		// Read the new parameters from the tables.
		verticalOrientation = vertOrientTable[rot];
		physicalTrimLow = physicalTrimLowTable[logicalTrimIndex][userFlipIndex][columnIndex][rot];
		physicalTrimHigh = physicalTrimHighTable[logicalTrimIndex][userFlipIndex][columnIndex][rot];
		invertCols = invertColsTable[rot];
		invertRows = invertRowsTable[userFlipIndex][rot];
		oddRowOffset = oddRowTable[logicalTrimIndex][userFlipIndex][columnIndex][rot];
		evenRowOffset = evenRowTable[logicalTrimIndex][userFlipIndex][columnIndex][rot];

		// Change the base Hex polygon to the correct orientation
		baseHexPoly = (verticalOrientation ? VBaseHexPoly : HBaseHexPoly);

		// Tell the caller if we've changed orientation...
		return (verticalOrientation != oldVertOrient);
	}

	/*
	 * Convert a logical coordinate to a physical coordinate.
	 *
	 * @param logicalCol   The logical column on the board.
	 * @param logicalRow   The logical row on the board.
	 * @return the physical screen point that corresponds to the given
	 *         logical point.
	 */
	private Point logicalToPhysical(int logicalCol, int logicalRow) {
		if (existsOnBoard(logicalCol, logicalRow)) {
			int physicalCol = invertCols ?
			                       numOfLogicalCols - logicalCol - 1 :
			                       logicalCol;

			int rowOffset = ((logicalCol & 0x01) == 0) ? evenRowOffset : oddRowOffset;
			int physicalRow = invertRows ?
			                       (numOfLogicalRows - logicalRow) - rowOffset :
			                       logicalRow + rowOffset;

			return new Point ( physicalCol, physicalRow );
		} else {
			return new Point ( OFF_SCREEN_POINT );
		}
	}

	/*
	 * Convert a physical coordinate to a logical coordinate.
	 *
	 * @param physicalCol   The physical column on the screen.
	 * @param physicalRow   The physical row on the screen.
	 * @return the logical board point that corresponds to the given
	 *         physical point.
	 */
	private Point physicalToLogical(int physicalCol, int physicalRow) {
		int logicalCol = invertCols ?
			                   numOfLogicalCols - physicalCol - 1 :
			                   physicalCol;

		int rowOffset = ((logicalCol & 0x01) == 0) ? evenRowOffset : oddRowOffset;
		int logicalRow = invertRows ?
			                   (numOfLogicalRows - physicalRow) - rowOffset :
			                   physicalRow - rowOffset;

		if (existsOnBoard(logicalCol, logicalRow)) {
			return new Point ( logicalCol, logicalRow );
		} else {
			return new Point ( OFF_SCREEN_POINT );
		}
	}

	/**
	 * This method returns a screen coordinate (in the top-left) from a board
	 * point.
	 *
	 * @param boardX    0 < boardX < numOfColumns
	 * @param boardY    0 < boardY < numOfRows
	 * @return          Screen coordinates for the anchor point of the hex.
	 */
	public Point getScreenAnchorFor (int boardX, int boardY) {

		Point anchorPoint = getHorizontalScreenAnchorFor(logicalToPhysical(boardX, boardY));

		if (verticalOrientation) {
			invertPoint(anchorPoint);
		}

		anchorPoint.translate(inset.width, inset.height);
		return anchorPoint;
	}

	/*
	 * This method returns a screen coordinate (from the top-left of the component)
	 * given a physical board point.  This assumes a horizontal hex orientation.
	 *
	 * @param physicalBoardPoint   The board point to translate, in physical board coordinates.
	 * @return the screen coordinates for the anchor point of the hex.
	 */
	private Point getHorizontalScreenAnchorFor(Point physicalBoardPoint) {
		int anchorX, anchorY;

		boolean physicalColOdd = ((physicalBoardPoint.x & 1) != 0);

		int extCols = physicalBoardPoint.x / 2;
		anchorX = (extCols * superWidth);
		anchorX += (physicalColOdd ? (controlLineLength + boundInset) : 0);

		int heightHalves = 2 * physicalBoardPoint.y;
		heightHalves -= (physicalTrimLow ? 1 : 0);
		heightHalves += (physicalColOdd ? 1 : 0);
		anchorY = ((cellBoundingDimension.height * heightHalves) + 1) / 2;

		return new Point (anchorX, anchorY);
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
	 * This method returns a screen coordinate in the center of a hex
	 * from a board point.
	 *
	 * @param boardX    0 < boardX < numOfColumns
	 * @param boardY    0 < boardY < numOfRows
	 * @return          Screen coordinates for the center point of the hex.
	 */
	public Point getScreenCenterFor (int boardX, int boardY) {
		Point thePoint = getScreenAnchorFor(boardX, boardY);
		thePoint.translate(halfWidth, halfHeight);
		return thePoint;
	}

	/**
	 * This method returns a screen coordinate in the center of a hex
	 * from a board point.
	 *
	 * @param boardPoint   Point on board (col, row) coordinates.
	 * @return             Screen coordinates for the anchor point of the hex.
	 */
	public Point getScreenCenterFor (Point boardPoint) {
		return getScreenCenterFor(boardPoint.x, boardPoint.y);
	}

	/**
	 * Determine if the hex at the given (col, row) exists on the board.
	 * Because of trimLow & trimHigh, some hex's within the array
	 * should not be drawn.
	 *
	 * @param (col, row)    The location to check for existance.
	 * @return true => hex is on the board.
	 */
	public boolean existsOnBoard (int col, int row) {
		if ((col < 0) || (col >= numOfLogicalCols) ||
		    (row < 0) || (row >= numOfLogicalRows)) {
			return false;
		}

		boolean colEven = ((col & 1) == 0);
		if ((logicalTrimLow  &  colEven && (row == 0)) ||
		    (logicalTrimHigh & !colEven && (row == (numOfLogicalRows - 1)))) {
			return false;
		}
		return true;
	}

	/**
	 * Determine if the hex at the given logical board point exists
	 * on the board.
	 *
	 * @param boardPoint    The location to check for existance.
	 * @return true => hex is on the board.
	 */
	public boolean existsOnBoard (Point boardPoint) {
		return existsOnBoard(boardPoint.x, boardPoint.y);
	}

	/**
	 * This method returns a board coordinate from a screen point.
	 *
	 * @param screenX   X coordinate in pixels.
	 * @param screenY   Y coordinate in pixels.
	 * @return          Return board coordinates
	 */
	public Point getBoardCoords (int screenX, int screenY) {

		// Remove the inset to get the coordinates in the array itself.
		screenX -= (inset.width + 1);
		screenY -= (inset.height + 1);

		if (verticalOrientation) {
			// Vertical orientation is computed as the inversion of the horizontal hex.
			return getHorizBoardCoords(screenY, screenX);
		} else {
			return getHorizBoardCoords(screenX, screenY);
		}
	}

	/*
	 * This method will invert the x & y coordinates of the given Point and
	 * return the modified Point back.
	 * Note: This modifies the Point provided; it does *not* create a new Point.
	 *
	 * @param p         The Point to invert.
	 * @return the same Point provided, but now inverted.
	 */
	private Point invertPoint(Point p) {
		p.setLocation(p.y, p.x);
		return p;
	}

	/*
	 * This method will invert the width & height of a given Dimenion and
	 * return the modified Dimension back.
	 * Note: This modifies the Dimension provided; it does *not* create a new Dimension.
	 *
	 * @param d         The Dimension to invert.
	 * @return the same Dimension provided, but now inverted.
	 */
	private Dimension invertDimension(Dimension d) {
		d.setSize(d.height, d.width);
		return d;
	}

	/*
	 * This method returns a board coordinate from a screen point.
	 * This assumes that the hex's are in a horizontal orientation.
	 *
	 * @param screenX   X coordinate in pixels.
	 * @param screenY   Y coordinate in pixels.
	 * @return          Return board coordinates
	 */
	private Point getHorizBoardCoords(int screenX, int screenY) {
		// If we've trimmed the low part of the board, then we need to
		// add an additional halfHeight to compensate.
		screenY += (physicalTrimLow ? halfHeight : 0);

		// Compute the base (col,row) of the "superCell" for the point.
		int col = (screenX / superWidth) * 2;
		int row = (screenY / cellBoundingDimension.height);

		// Compute the offset into the "superCell" for the point.
		int XinCell = screenX % superWidth;
		int YinCell = screenY % cellBoundingDimension.height;

		// Check which part of the "superCell" we're in to modify the base
		// (col,row) to get the real (col, row)
		if (XinCell >= cellBoundingDimension.width) {
			// In right side of "superCell", so simple check of Y-value
			// determines if in upper-right or lower-right portion.
			if (YinCell >= halfHeight) {
				// In lower-right corner of "superCell"
				return physicalToLogical(col+1, row);
			} else {
				// In upper-right corner of "superCell"
				return physicalToLogical(col+1, row-1);
			}
		}

		int f1 = boundInset * YinCell;
		int f2 = halfHeight * XinCell;

		if ((f1+f2) <= i_hh) {
			// In upper-left corner of "superCell"
			return physicalToLogical(col-1, row-1);
		}

		if ((f1-f2) >= i_hh) {
			// In lower-left corner of "superCell"
			return physicalToLogical(col-1, row);
		}

		if ((f1-f2) <= (i_hh - w_hh)) {
			// In upper-right corner of "superCell"
			return physicalToLogical(col+1, row-1);
		}
		if ((f1+f2) >= (i_hh + w_hh)) {
			// In lower-right corner of "superCell"
			return physicalToLogical(col+1, row);
		}

		// Only area left is the main Hex of the "superCell"
		return physicalToLogical(col, row);
	}

	/**
	 * Return the size of each hex in pixels.
	 *
	 * @return  bounding dimensions of the hex.
	 */
	public Dimension getHexSize() {
		return this.cellBoundingDimension;
	}

	/**
	 * Return the point where a user has pressed on the board.
	 *
	 * @return   Point where user has pressed.
	 */
	public Point getPressedPoint() {
		return this.pressedPoint;
	}

	/**
	 * Return the drag point of a mouse.
	 *
	 * @return  Point where user has dragged.
	 */
	public Point getDragPoint() {
		return this.dragPoint;
	}

	/**
	 * Return the highlighted point.
	 *
	 * @param highlightPoint    Point on board that is highlighted
	 */
	public Point getHighlightPoint () {
		return this.highlightPoint;
	}

	/**
	 * Sets pressed point in the board.
	 *
	 * @param pressedPoint  Point on board where pressed.
	 */
	public void setPressedPoint (Point pressedPoint) {
		this.pressedPoint = pressedPoint;
	}

	/**
	 * Sets the drag point.
	 *
	 * @param dragPoint    Point on board where dragged.
	 */
	public void setDragPoint (Point dragPoint) {
		this.dragPoint = dragPoint;
	}

	/**
	 * Sets the highlighted point.
	 *
	 * @param highlightPoint    Point on board to be highlighted
	 */
	public void setHighlightPoint (Point newHighlightPoint) {
		if (!newHighlightPoint.equals (this.highlightPoint)) {
			this.highlightPoint = newHighlightPoint;
			repaint();
		}
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
 	 * @param cellColour             Colour for the cells.
 	 * @param outlineColour          Colour to outline the cells.
	 * @param highlightColour        Colour to highlight the cell.
	 */
	public final void setColours (Color cellColour, Color outlineColour, Color highlightColour) {
		this.cellColour = cellColour;
		this.outlineColour = outlineColour;
		this.highlightColour = highlightColour;
	}
}
