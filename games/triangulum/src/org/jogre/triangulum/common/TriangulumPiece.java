/*
 * JOGRE (Java Online Gaming Real-time Engine) - Triangulum
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
package org.jogre.triangulum.common;

import nanoxml.XMLElement;

/**
 * Game piece for the Triangulum game.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class TriangulumPiece {

	// Sections of the piece.  These should be used for the getColor() method.
	public static int BOTTOM_SECTION = 0;
	public static int LEFT_SECTION = 1;
	public static int RIGHT_SECTION = 2;

	// The colors of the three sections of the piece
	private int [] colors = new int [3];

	// The value of the piece
	private int value;

	// XML Attributes used when sending a piece in a message.
	public static final String XML_ATT_VALUE        = "val";
	public static final String XML_ATT_BOTTOM_COLOR = "bc";
	public static final String XML_ATT_LEFT_COLOR   = "lc";
	public static final String XML_ATT_RIGHT_COLOR  = "rc";

	/**
	 * Constructor which creates the piece.
	 *
	 * @param value        The value of the piece
	 * @param bottomColor  Color for the bottom
	 * @param leftColor    Color for the left
	 * @param rightColor   Color for the right
	 */
	public TriangulumPiece (int value, int bottomColor, int leftColor, int rightColor) {
		this.value = value;
		colors[BOTTOM_SECTION] = bottomColor;
		colors[LEFT_SECTION  ] = leftColor;
		colors[RIGHT_SECTION ] = rightColor;
	}

	/**
	 * Constructor for a piece given a piece.
	 *
	 * @param oldPiece     The piece to copy.
	 */
	public TriangulumPiece (TriangulumPiece oldPiece) {
		this.value = oldPiece.value;
		colors[BOTTOM_SECTION] = oldPiece.colors[BOTTOM_SECTION];
		colors[LEFT_SECTION  ] = oldPiece.colors[LEFT_SECTION  ];
		colors[RIGHT_SECTION ] = oldPiece.colors[RIGHT_SECTION ];
	}

	/**
	 * Constructor for a piece given an XML entry.
	 *
	 * @param theEl    The XML element with this piece information in it.
	 */
	public TriangulumPiece (XMLElement theEl) {
		value = theEl.getIntAttribute(XML_ATT_VALUE);
		colors[BOTTOM_SECTION] = theEl.getIntAttribute(XML_ATT_BOTTOM_COLOR);
		colors[LEFT_SECTION  ] = theEl.getIntAttribute(XML_ATT_LEFT_COLOR);
		colors[RIGHT_SECTION ] = theEl.getIntAttribute(XML_ATT_RIGHT_COLOR);
	}

	/**
	 * Return the color for the requested part of the piece.
	 *
	 * @param section    Which section to get the color of.
	 * @return the color code for the requested section.
	 */
	public int getColor(int section) {
		return colors[section];
	}

	/**
	 * Return the value of the piece.
	 *
	 * @return the value of the piece.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Determine if the piece given is the same piece.  A piece is the same
	 * irrespective of flips and/or rotates.
	 */
	public boolean isSamePiece(TriangulumPiece tgtPiece) {
		if (value != tgtPiece.value) {
			return false;
		}

		// Brute force compare
		int tcl = tgtPiece.colors[LEFT_SECTION  ];
		int tcr = tgtPiece.colors[RIGHT_SECTION ];
		int tcb = tgtPiece.colors[BOTTOM_SECTION];
		int cl = colors[LEFT_SECTION  ];
		int cr = colors[RIGHT_SECTION ];
		int cb = colors[BOTTOM_SECTION];

		if (cb == tcb) {
			return ((cl == tcl) && (cr == tcr)) ||
			       ((cl == tcr) && (cr == tcl));
		} else if (cb == tcl) {
			return ((cl == tcb) && (cr == tcr)) ||
			       ((cl == tcr) && (cr == tcb));
		} else if (cb == tcr) {
			return ((cl == tcl) && (cr == tcb)) ||
			       ((cl == tcb) && (cr == tcl));
		}
		return false;
	}

	/**
	 * Add the attributes for this piece to the given XML element.
	 *
	 * @param    theEl     The element to add the attributes to
	 */
	public void addToXML (XMLElement theEl) {
		theEl.setIntAttribute(XML_ATT_VALUE, value);
		theEl.setIntAttribute(XML_ATT_BOTTOM_COLOR, colors[BOTTOM_SECTION]);
		theEl.setIntAttribute(XML_ATT_LEFT_COLOR,   colors[LEFT_SECTION  ]);
		theEl.setIntAttribute(XML_ATT_RIGHT_COLOR,  colors[RIGHT_SECTION ]);
	}
}
