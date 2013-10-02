/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006  Richard Walter
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
package org.jogre.grandPrixJumping.client;

import org.jogre.grandPrixJumping.common.JumpingCard;

import java.awt.geom.QuadCurve2D;

/**
 * Structure to hold information about a track ornament for Grand Prix Jumping.
 * Ornaments include the icons, lines and dots.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingTrackOrnament {

	// The id of the icon (or diameter of a dot)
	public int		id;

	// The image that the icon comes from.
	public int		imageId;

	// The (x,y) location of the icon or ends of the line
	public int		p1x, p1y;
	public int		p2x, p2y;

	// The curve (for curved lines)
	public QuadCurve2D	curve;

	// The card that this icon corresponds to
	public JumpingCard	theCard = null;

	// flags about the ornaments
	public boolean isLine = false;
	public boolean isCurve = false;
	public boolean isHorse = false;
	public boolean isCaution = false;
	public boolean isPlus = false;
	public boolean selected = false;

	// Constants for the layout of the icon image
	private static final int ICON_CANTER_OFFSET = 0;
	private static final int ICON_HEIGHT_OFFSET = 5;
	private static final int ICON_LENGTH_OFFSET = 10;
	private static final int ICON_SADDLE_INDEX = 15;
	private static final int ICON_FAKE_SADDLE_INDEX = 16;

	private static final int ICON_PLUS_INDEX = 0;
	private static final int ICON_SELECTED_PLUS_INDEX = 1;

	/**
	 * Constructor for an icon
	 */
	public JumpingTrackOrnament(JumpingCard theCard, int x, int y) {
		this.p1x = x;
		this.p1y = y;
		this.theCard = theCard;
		this.imageId = JumpingImages.TRACK_ICONS;

		if (theCard.isCanter()) {
			this.id = ICON_CANTER_OFFSET - 1 + theCard.cardValue();
		} else if (theCard.isHeight()) {
			this.id = ICON_HEIGHT_OFFSET - 1 + theCard.cardValue();
		} else if (theCard.isLength()) {
			this.id = ICON_LENGTH_OFFSET - 1 + theCard.cardValue();
		} else {
			if (theCard.isFakeSaddle()) {
				this.id = ICON_FAKE_SADDLE_INDEX;
			} else {
				this.id = ICON_SADDLE_INDEX;
			}
		}
	}

	/**
	 * Another constructor for an icon
	 */
	public JumpingTrackOrnament(JumpingCard theCard, int [] theLocation) {
		this (theCard, theLocation[0], theLocation[1]);
	}

	/**
	 * Constructor for a fake-saddle "plus" icon
	 */
	public JumpingTrackOrnament(int x, int y) {
		this.p1x = x;
		this.p1y = y;
		this.id = ICON_PLUS_INDEX;
		this.imageId = JumpingImages.PLUS;
		this.isPlus = true;
	}

	/**
	 * Constructor for a horse
	 */
	public JumpingTrackOrnament(int horseId, int space, boolean horseOffset) {
		this.p1x = horseId;
		this.p1y = space;
		this.isLine = horseOffset;
		this.isHorse = true;
	}

	/**
	 * Constructor for a line.
	 */
	public JumpingTrackOrnament(int [] p1, int [] p2) {
		this.p1x = p1[0];
		this.p1y = p1[1];
		this.p2x = p2[0];
		this.p2y = p2[1];
		this.isLine = true;
	}

	/**
	 * Constructor for a curve.
	 */
	public JumpingTrackOrnament(QuadCurve2D	curve) {
		this.curve = curve;
		this.isCurve = true;
	}

	/**
	 * Constructor for a dot.
	 */
	public JumpingTrackOrnament(int [] p) {
		this.p1x = p[0]-4;
		this.p1y = p[1]-4;
		this.id = 8;
		this.isLine = false;
	}

	/**
	 * Constructor for a caution sign
	 */
	public JumpingTrackOrnament(int value, int [] p) {
		this.p1x = p[0];
		this.p1y = p[1];
		this.id = value - 1;
		this.imageId = JumpingImages.CAUTION_SIGNS;
		this.isCaution = true;
	}

	public boolean isLine()     { return isLine; }
	public boolean isHorse()    { return isHorse; }
	public boolean isCaution()  { return isCaution; }
	public boolean isSelected() { return selected; }
	public boolean isPlus()     { return isPlus; }
	public boolean isCurve()	{ return isCurve; }

	/**
	 * Get fields for a horse ornament (since they are stored in a strange way)
	 */
	public int getHorseId() { return this.p1x; }
	public int getHorseSpace() { return this.p1y; }
	public boolean getHorseOffset() {return this.isLine; }

	/**
	 * Get the curve for a curve
	 */
	public QuadCurve2D getCurve() { return this.curve; }

	/**
	 * Set the selected flag to the given value.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
