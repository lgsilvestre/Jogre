/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
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
package org.jogre.client.awt;

import java.awt.*;
import java.util.Observer;

/**
 * <p>This graphical component can be extended to create a number of graphical
 * boards such as chess boards, etc.  Various attributes can be set such as the
 * number of cells, the cell size and cell spacing (in pixels).  Also the width
 * of the border can be specified, and the option of drawing letters/text on
 * the border. Also all the colours can be set of the board.</p>
 *
 * <p>To use
 * and override the
 * <code>paintComponent (Graphics g)</code> method as follows:</p>
 * <pre>
 * paintComponent (Graphics g) {
 *   // call paintComponent in AbstractBoardComponent which will draw an empty board
 *   super (g);
 *
 *   // Insert specifiec custom graphic drawing here
 * }
 * </pre>
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class AbstractBoardComponent extends JogreComponent implements Observer {

	/** Constant to show a cell point is off the board (not selected). */
	public static final Point OFF_SCREEN_POINT = new Point (-1, -1);

	/** Default cell size (36 pixels). */
	public static final int DEFAULT_CELL_SIZE = 36;

	/** Default cell spacing (1 pixel). */
	public static final int DEFAULT_CELL_SPACING = 1;

	/** Default border width (15 pixels). */
	public static final int DEFAULT_BORDER_WIDTH = 15;

	/** Default mouse border width (2 pixels).*/
	public static final int DEFAULT_MOUSE_BORDER_WIDTH = 2;

	// Board letters (a, b, ... , z).
	private final String [] BORDER_LETTERS =
		{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
		 "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	// Declare font and font metrics for this font
	private final Font letterFont = new Font ("SansSerif", Font.BOLD, 12);
	private FontMetrics fontMetrics = null;

	/** Number of rows in the board. */
	protected int numOfRows;

	/** Number of columns in the board. */
	protected int numOfCols;

	/** Size of cell in pixels. */
	protected int cellSize;

	/** Spacing between cells in pixels. */
	protected int cellSpacing;

	/** Width of the border in pixels. */
	protected int borderWidth;

	/** Width of the mouse border. */
	protected int mouseBorderWidth;

	/** If true letters/numbers are drawn on the left and bottom of the board. */
	protected boolean isDrawLetters;

	/** Boolean to indicate alternate style background. i.e. chess style board. */
	protected boolean isAlternateBackground;

	/** If this variable is true the board will be flipped. */
	protected boolean isReversed;

	/** Colour of cell one. */
	protected Color cellColour1;

	/** Colour of cell two. */
	protected Color cellColour2;

	/** Colour of spacing colour. */
	protected Color spacingColour;

	/** Colour of the mouse border. */
	protected Color mouseBorderColour;

	/** Stores the value of a mouse down. **/
	protected Point pressedPoint = OFF_SCREEN_POINT;

	/** Stores the value of where the point is being dragged. */
	protected Point dragPoint = OFF_SCREEN_POINT;

	/**
	 * Abstract board - but doesn't have a defined number of rows / columns. 
	 * Use this constructor when a board is heavily customised.
	 */
	public AbstractBoardComponent () {
		this (1, 1);
	}
	
	/**
	 * Default constructor which sets the number of rows and the number of
	 * columns and everything else uses it default value.
	 *
	 * @param numOfRows    Number of rows in the board.
	 * @param numOfCols    Number of columns in the board.
	 */
	public AbstractBoardComponent (int numOfRows, int numOfCols) {
		// Call detailed constructor
		this (numOfRows, numOfCols,	DEFAULT_CELL_SIZE, DEFAULT_CELL_SPACING,
			DEFAULT_BORDER_WIDTH, DEFAULT_MOUSE_BORDER_WIDTH, false, true, true
		);
	}

	/**
	 * This constructor sets all the various things of a board such as its
	 * number of rows/columns, cell size, spacing between cells, border width,
	 * mouse border width and the option to draw letters down the side.  Also
	 * a boolean can be set to indicate if the board is reversed.
	 *
	 * @param numOfRows           Number of rows in the board.
	 * @param numOfCols           Number of columns in the board.
	 * @param cellSize            Size of each cell in pixels.
	 * @param cellSpacing         Spacing between each cell in pixels.
	 * @param borderWidth         Size of the border in pixels.
	 * @param mouseBorderWidth    Size of the mouse border in pixels.
	 * @param isReversed          Boolean to indicate if the board is displayed
	 *                            in reverse.
	 * @param alternateBackground Alternate style background e.g. chess style.
	 * @param drawLetters         If true draw letters horizontally and numbers
	 *                            vertically.
	 */
	public AbstractBoardComponent (
		int numOfRows,
		int numOfCols,
		int cellSize,
		int cellSpacing,
		int borderWidth,
		int mouseBorderWidth,
		boolean isReversed,
		boolean alternateBackground,
		boolean drawLetters
	) {
		this.numOfRows = numOfRows;
		this.numOfCols = numOfCols;
		this.cellSize = cellSize;
		this.cellSpacing = cellSpacing;
		this.borderWidth = borderWidth;
		this.mouseBorderWidth = mouseBorderWidth;
		this.isReversed = isReversed;
		this.isAlternateBackground = alternateBackground;
		this.isDrawLetters = drawLetters;

		// Determine the size of the board and set the component to that size.
		int boardWidth =
			(numOfCols * (cellSize + cellSpacing)) +
			(borderWidth * 2) +
			cellSpacing;
		int boardHeight =
			(numOfRows * (cellSize + cellSpacing)) +
			(borderWidth * 2) +
			cellSpacing;
		setPreferredSize (new Dimension (boardWidth, boardHeight));

		// Set up colours to default colours
		setColours (
			new Color (255, 244, 200),
			new Color (200, 170, 100),
			new Color (64, 64, 64),
			new Color (64, 255, 0)
		);

		// now render it
		repaint ();
	}

	/**
	 * Draws the board.  This method calls the drawBorder (Graphics g) and
	 * drawBoardBackground (Graphics g) methods.  The drawBoardLetters
	 * (Graphics g) method is also called if the drawLetters boolean is set to
	 * true.  To customise this board then each of these methods can be
	 * over written in the sub class.
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g) {
		// draw the  Border
		drawBorder (g);

		// draw the background
		drawBoardBackground(g);

		// draw the letters on the board if the users requires this
		if (this.isDrawLetters)
			drawBoardLetters (g);
	}

	/**
	 * Set up colours for this board.
	 *
 	 * @param cellColour1            Colour for the first cell colour.
	 * @param cellColour2            Colour for the second cell colour.
	 * @param spacingColour          Colour of the spacing between the cells.
	 * @param mouseBorderColour      Colour of the mouse border colour.
	 */
	public final void setColours (Color cellColour1, Color cellColour2, Color spacingColour, Color mouseBorderColour) {
		this.cellColour1 = cellColour1;
		this.cellColour2 = cellColour2;
		this.spacingColour = spacingColour;
		this.mouseBorderColour = mouseBorderColour;
	}

	/**
	 * Draws the board background (all the various cells).  This can be
	 * overwritten to draw images for example.
	 *
	 * @param g  Graphics object.
	 */
	protected void drawBoardBackground (Graphics g) {
		// Draw chess style board
		if (this.isAlternateBackground) {

			for (int x = 0; x < this.numOfCols; x++) {
				for (int y = 0; y < this.numOfRows; y++) {
					// retrieve screen co-ordinates
					int screenX = x * (this.cellSize + this.cellSpacing) +
								  this.cellSpacing +
					              this.borderWidth;
					int screenY = y * (this.cellSize + this.cellSpacing) +
					              this.cellSpacing +
					              this.borderWidth;

					if ((x+y) % 2 == 0)
						g.setColor (this.cellColour1);
					else
						g.setColor (this.cellColour2);

					g.fillRect (screenX, screenY, this.cellSize, this.cellSize);
				}
			}
		}
	    else {			// normal style board (all cells the same colour).
	    	g.setColor (this.cellColour1);

			for (int x = 0; x < this.numOfCols; x++) {
				for (int y = 0; y < this.numOfRows; y++) {
					// retrieve screen co-ordinates
					int screenX = x * (this.cellSize + this.cellSpacing) +
								  this.cellSpacing +
					              this.borderWidth;
					int screenY = y * (this.cellSize + this.cellSpacing) +
					              this.cellSpacing +
					              this.borderWidth;

					g.fillRect (screenX, screenY, this.cellSize, this.cellSize);
				}
			}
	    }
	}

	/**
	 * This method wipes the board and draws the border.  This can be
	 * overwritten in a sub class to draw custom borders.
	 *
	 * @param g
	 */
	protected void drawBorder (Graphics g) {
		// wipe the background (spacing colour)
		g.setColor (this.spacingColour);
		g.fillRect (0, 0, getWidth(), getHeight());

		g.setColor (Color.black);
		int size = getWidth();
		g.drawRect (0, 0, size - 1, size - 1);
		g.drawRect (this.borderWidth,
		            this.borderWidth,
			        size - 1 - (this.borderWidth * 2),
			        size - 1 - (this.borderWidth * 2));
	}

	/**
	 * Draws the letters on the left and bottom of the board.  This can be
	 * overwritten in a sub class to draw custom board letters.
	 *
	 * @param g  Graphics object
	 */
	private void drawBoardLetters(Graphics g) {
		// Create font metrics
		g.setColor (Color.white);
		g.setFont  (this.letterFont);
		// Only create font metrics once (for performance)
		if (this.fontMetrics == null)
		    this.fontMetrics = g.getFontMetrics();

		// Compute indent of text (using borders
		int totalCellSize = this.cellSize + this.cellSpacing;
		int middleX = (totalCellSize / 2) - (this.fontMetrics.charWidth('a') / 2);
		int middleY = (totalCellSize + this.fontMetrics.getAscent()) / 2;

		// Draw numbers down the left
		for (int i = 0; i < numOfCols; i++) {
			int x = (this.borderWidth / 2) - (this.fontMetrics.charWidth('a') / 2) + this.cellSpacing;
			int y = this.borderWidth + (i * totalCellSize) + middleY;
			int number = this.isReversed ? (i + 1) : numOfCols - i;
			if (number > 9)
			    x -= ((this.fontMetrics.charWidth('a') / 2) + 1);
			g.drawString (String.valueOf(number), x, y);
		}

		// Draw letters across the board
		for (int i = 0; i < numOfRows; i++) {
			int x = this.borderWidth + (i * totalCellSize) + middleX;
			int y = getHeight() - (this.borderWidth / 2) +
				this.fontMetrics.getDescent();
			int letter = this.isReversed ? numOfRows - 1 - i : i;
			g.drawString (this.BORDER_LETTERS [letter], x, y);
		}
	}

	/**
	 * This methods returns a screen co-ordinate (in the top-left) from a board
	 * point.
	 *
	 * @param boardX    0 < boardX < numOfColumns
	 * @param boardY    0 < boardY < numOfRows
	 * @return          Screen co-ordinates fromm a board co-ordinate.
	 */
	public Point getScreenCoords (int boardX, int boardY) {
		int totalCellSize = this.cellSize + this.cellSpacing;

		// compute screen X and Y
		int screenX = (boardX * totalCellSize) + this.borderWidth + this.cellSpacing;
		int screenY = (boardY * totalCellSize) + this.borderWidth + this.cellSpacing;

		if (this.isReversed) {
			screenX = ((this.numOfCols - 1) * totalCellSize) -
				(boardX * totalCellSize) + this.borderWidth + this.cellSpacing;
			screenY = ((this.numOfRows - 1) * totalCellSize) -
				(boardY * totalCellSize) + this.borderWidth + this.cellSpacing;
		}

		return new Point (screenX, screenY);
	}

	/**
	 * This methods returns a board co-ordinate (in the top-left) from a screen
	 * point.
	 *
	 * @param screenX   X co-ordinate in pixels.
	 * @param screenY   Y co-ordinate in pixels.
	 * @return          Return board co-ordinates
	 */
	public Point getBoardCoords (int screenX, int screenY) {
		int totalCellWidth = this.cellSize + this.cellSpacing;

		// compute new board X and Y
		int boardX = ((screenX - this.borderWidth) / totalCellWidth);
		int boardY = ((screenY - this.borderWidth) / totalCellWidth);

		// note if the board is reversed then we have to allow for this also
		if (this.isReversed) {
			boardX = (this.numOfCols - 1) - boardX;
			boardY = (this.numOfRows - 1) - boardY;
		}

		return new Point (boardX, boardY);
	}

	/**
	 * Returns true if a board is reversed.
	 *
	 * @return   If board is reversed (e.g. black side in chess).
	 */
	public boolean isReversed () {
		return this.isReversed;
	}

	/**
	 * Return the width of the border in pixels.
	 *
	 * @return  Pixel width of border.
	 */
	public int getBorderWidth() {
		return this.borderWidth;
	}

	/**
	 * Return the size of each cell in pixels.
	 *
	 * @return  Pixel size of cell.
	 */
	public int getCellSize() {
		return this.cellSize;
	}

	/**
	 * Return the spacing between each cell in pixels.
	 *
	 * @return  Pixel size of cell spacing.
	 */
	public int getCellSpacing() {
		return this.cellSpacing;
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
	 * Sets the drag point of a mouse.
	 *
	 * @return  Point where user has dragged.
	 */
	public Point getDragPoint() {
		return this.dragPoint;
	}

	/**
	 * Sets if a board is reversed or not.
	 *
	 * @param isReversed   True if reversed (e.g. black side in chess).
	 */
	public void setReversed (boolean isReversed) {
		this.isReversed = isReversed;
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
	 * Reset the dragPoint and pressedPoint variables.
	 */
	public void resetPoints () {
		this.dragPoint = OFF_SCREEN_POINT;
		this.pressedPoint = OFF_SCREEN_POINT;
	}
}