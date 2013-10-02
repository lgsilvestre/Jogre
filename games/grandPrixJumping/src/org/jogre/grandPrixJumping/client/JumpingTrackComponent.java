/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
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
package org.jogre.grandPrixJumping.client;

import java.lang.Math;

import java.util.Vector;
import java.util.ListIterator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.QuadCurve2D.Float;

import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogrePanel;
import org.jogre.common.util.JogreUtils;

import org.jogre.grandPrixJumping.common.JumpingCard;
import org.jogre.grandPrixJumping.common.JumpingFence;
import org.jogre.grandPrixJumping.common.JumpingJump;

/**
 * View of the track for Grand Prix Jumping
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingTrackComponent extends JogreComponent {

	// Link to the model & graphics
	private JumpingClientModel model;
	private JumpingGraphics Jgraphics;

	// Our seat number & opponent's seat number.
	// This is used to determine how to draw the horses.
	private int myPlayerSeatNum = 0;
	private int oppSeatNum = 1;

	// This is the "real" myPlayerSeatNum.
	// For spectators, this is -1, while the "myPlayerSeatNum" will be 0
	private int realMyPlayerSeatNum = -1;

	// Vectors that keep the icons & highlight lines on the track
	private Vector icons = null;
	private Vector lines = null;
	private Vector archivedIcons = null;
	private Vector archivedLines = null;

	// Array to keep track of the number of icons placed at each space of the board.
	private final static int ON_SPACE_TYPE = 0;
	private final static int BETWEEN_SPACE_TYPE = 1;
	int [][] iconsShownOnSpace = new int [2][JumpingClientModel.TRACK_SPACES];

	// The owner of the archived icons & lines
	private int archivedJumpOwner = 0;

	// The currently selected icon
	private JumpingTrackOrnament selectedIcon;

	// The currently selected track space while editing the fence heights
	private int selectedTrackSpace = -1;

	// The amount of space to move the track down because of horses and/or fences
	// on the top row sticking up above the track.
	private int overhang = 0;

	// Constant for how many pixels to offset the horses on the spaces
	private static final int HORSE_VERT_OFFSET = 12;

	// The center of the board image.  This is used to draw the lock icon.
	private int centerX, centerY;

	// For debug, setting this to true will cause the requested & actual bounds
	// of the component to be drawn.
	private boolean debugShowBounds = false;

	/**
	 * Constructor which creates the track component
	 *
	 * @param model					The game model
	 */
	public JumpingTrackComponent (JumpingClientModel model) {

		// link to model and graphics
		this.model = model;
		this.Jgraphics = JumpingGraphics.getInstance();

		// Compute various parameters of the track.
		overhang = Math.max ( horseOverhang(), fenceOverhang() );
		centerX = Jgraphics.imageWidths [JumpingImages.TRACK] / 2;
		centerY = (Jgraphics.imageHeights[JumpingImages.TRACK] + overhang) / 2;

		// Set the dimensions of the track component.
		Dimension dim = new Dimension (
							Jgraphics.imageWidths [JumpingImages.TRACK],
							Jgraphics.imageHeights[JumpingImages.TRACK] + overhang);
		setPreferredSize(dim);
		setMinimumSize(dim);
	}

	/**
	 * Change my seat number
	 */
	public void setPlayerId(int seatNum) {
		myPlayerSeatNum = (seatNum == 1) ? 1 : 0;
		oppSeatNum = (1 - myPlayerSeatNum);
		realMyPlayerSeatNum = seatNum;
	}

	/**
	 * Make the given icon the currently selected one.
	 * Return value indicates if this is a different icon than was selected.
	 *
	 * @param	icon			The icon to make currently selected.
	 * @return true => The new selected icon is different than the old one.
	 *         false => The new selected icon is the same as the old one.
	 */
	public boolean setSelectedIcon(JumpingTrackOrnament icon) {
		if (selectedIcon != icon) {
			if (selectedIcon != null) {
				selectedIcon.setSelected(false);
			}
			if (icon != null) {
				icon.setSelected(true);
			}
			selectedIcon = icon;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the currently selected icon.
	 *
	 * @return the current selected icon.
	 */
	public JumpingTrackOrnament getSelectedIcon() {
		return selectedIcon;
	}

	/**
	 * Select the icon pointed to at (x,y).
	 * Return value indicates if this is a different icon than what was selected.
	 */
	public boolean selectIconAt(int x, int y) {

		if (icons != null) {
			// Look throught the icons (backward) to find an icon at the correct location
			ListIterator iter = icons.listIterator(icons.size());
			while (iter.hasPrevious()) {
				boolean hit = false;
				JumpingTrackOrnament orn = (JumpingTrackOrnament) iter.previous();

				if (orn.isHorse()) {
					int [] pt = getHorsePoint(orn.getHorseSpace(), orn.getHorseOffset());
					hit = Jgraphics.pointWithinImage(x, y, pt[0], pt[1], JumpingImages.HORSE0);
				} else if (!orn.isCaution()) {
					hit = Jgraphics.pointWithinImage(x, y, orn.p1x, orn.p1y, JumpingImages.TRACK_ICONS);
				}

				if (hit) {
					return setSelectedIcon(orn);
				}
			}
		}

		// If we got through all of the icons and didn't find one that matched, then
		// select no icon
		return setSelectedIcon(null);
	}

	/**
	 * Make the given track space the currently selected one.
	 * Return value indicates if this is a different space than was selected.
	 *
	 * @param	newSpace		The space to make currently selected.
	 * @return true => The new selected space is different than the old one.
	 *         false => The new selected space is the same as the old one.
	 */
	public boolean setSelectedTrackSpace(int newSpace) {
		if (selectedTrackSpace != newSpace) {
			selectedTrackSpace = newSpace;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the currently selected track space.
	 *
	 * @return the current selected track space.
	 */
	public int getSelectedTrackSpace() {
		return selectedTrackSpace;
	}

	/**
	 * Select the track space point to at (x,y).
	 * Return value indicates if this is a different space than what was selected.
	 */
	public boolean selectTrackSpaceAt(int x, int y) {
		int theSpace = getSpaceForCoordinates(x, y);

		// Can't select the starting or ending space of the track.
		if ((theSpace == 0) || (theSpace == JumpingClientModel.LAST_SPACE)) {
			theSpace = -1;
		}

		return setSelectedTrackSpace(theSpace);
	}

	/**
	 * Compute the locations for the track ornaments.
	 */
	private void enumerateOrnaments() {
		// Get the jumps from the model.
		Vector jumps = (Vector) model.getCurrJumps();

		if (jumps == null) {
			// Nothing to do...
			lines = null;
			icons = null;
			return;
		}

		// Clone the vector of jumps to avoid "Concurrent Modification Exception"
		jumps = (Vector) jumps.clone();

		lines = new Vector();
		icons = new Vector();

		boolean canPlaceFakeSaddle = false;

		// Get info from the model
		int playerSeat = model.getCurrJumpOwner();
		boolean horseOffset = (playerSeat == myPlayerSeatNum);
		int horseLoc = model.getHorseLocation(playerSeat);

		// Clear the iconsShown array to 0's
		for (int i=0; i < JumpingClientModel.TRACK_SPACES; i++) {
			iconsShownOnSpace[ON_SPACE_TYPE][i] = 0;
			iconsShownOnSpace[BETWEEN_SPACE_TYPE][i] = 0;
		}

		ListIterator iter = jumps.listIterator();
		while (iter.hasNext()) {
			// Get the jump info
			JumpingJump jump = (JumpingJump) iter.next();

			// Add icons for the cards of the jump
			addSaddleIcon(jump.getSaddleCard(), horseOffset);
			addCardIcons(jump.getCanterCards(), horseOffset, BETWEEN_SPACE_TYPE);
			addCardIcons(jump.getLengthCards(), horseOffset, BETWEEN_SPACE_TYPE);
			addCardIcons(jump.getHeightCards(), horseOffset, ON_SPACE_TYPE);

			// Add icons for any fences that need caution signs on this jump
			addCautionSigns(jump.getFences());

			// Add lines for the canter & length cards
			addCanterLines(jump.getCanterCards(), horseOffset);
			addLengthLines(jump.getJumpingStart(), jump.getJumpingEnd(), horseOffset);

			// update horseLoc with where this jump ends
			horseLoc = jump.endSpace();
			canPlaceFakeSaddle = jump.canPlaceFakeSaddle();
		}

		// If we are showing a pre-commit path and this is a valid move and the horse has moved,
		// then add a horse icon to the icon list
		if (model.playedCardsArePrecommit() &&
			model.arePlayedCardsValid() &&
			(horseLoc != model.getHorseLocation(playerSeat))) {
			icons.insertElementAt(new JumpingTrackOrnament(playerSeat, horseLoc, horseOffset), 0);

			// If it is legal to play a fake saddle card at the end of this jump, then
			// add a plus icon to the screen
			if (canPlaceFakeSaddle) {
				int [] iconPt = getIconPoint(ON_SPACE_TYPE, horseLoc, horseOffset);
				icons.add(new JumpingTrackOrnament(iconPt[0]+4, iconPt[1]+5));
			}
		}

		// Since there are now real cards played, erase the archived icons & lines
		archivedIcons = null;
		archivedLines = null;
	}

	/*
	 * Add a saddle icon to the icon list.
	 *
	 * @param	saddleCard		The card that the icon is for
	 * @param	horseOffset		Direction to offset the horse on the space
	 */
	private void addSaddleIcon(JumpingCard saddleCard, boolean horseOffset) {
		int [] iconPt = getIconPoint(ON_SPACE_TYPE, saddleCard.getVisualSpace(), horseOffset);
		icons.add(new JumpingTrackOrnament(saddleCard, iconPt));
	}

	/*
	 * Add a bunch of card icons to the icon list.
	 *
	 * @param	cards			Vector of canter cards.
	 * @param	horseOffset		Direction to offset the horse on the space
	 * @param	spaceType		The type of location to place the icons
	 */
	private void addCardIcons(Vector cards, boolean horseOffset, int spaceType) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard c = (JumpingCard) iter.next();
			int [] iconPt = getIconPoint(spaceType, c.getVisualSpace(), horseOffset);
			icons.add(new JumpingTrackOrnament(c, iconPt));
		}
	}

	/*
	 * Add caution sign icons for any that result from the given fences.
	 *
	 * @param	fences		The vector of fences to place cautions for
	 */
	private void addCautionSigns(Vector fences) {
		ListIterator iter = fences.listIterator();
		while (iter.hasNext()) {
			JumpingFence fence = (JumpingFence) iter.next();
			int cautionValue = fence.faultPoints();
			if (cautionValue > 0) {
				int cautionSpace = (fence.isWaterJump() ? fence.location() + (5 - cautionValue) : fence.location());
				icons.add(new JumpingTrackOrnament(cautionValue, getCautionPoint(cautionSpace)));
			}
		}
	}

	/*
	 * Add lines to the line vector for canter cards.
	 */
	private void addCanterLines(Vector cards, boolean horseOffset) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard c = (JumpingCard) iter.next();
			addLine(c.getEffectiveSpace(), c.cardValue(), horseOffset);
		}
	}

	/*
	 * Add lines to the line vector for jump cards.
	 */
	private void addLengthLines(int startSpace, int endSpace, boolean horseOffset) {
		// If there is no jump to show, don't bother computing a jump.
		if (startSpace == endSpace) {
			return;
		}

		// Get end points of the curve
		int [] p1coords = getHorsePoint(startSpace, horseOffset);
		int [] p2coords = getHorsePoint(endSpace, horseOffset);
		Point p1 = new Point (p1coords[0], p1coords[1]);
		Point p2 = new Point (p2coords[0], p2coords[1]);

		// Compute control point location for the curve
		int dx = (p2coords[0] - p1coords[0]) / 2;
		int dy = (p2coords[1] - p1coords[1]) / 2;
		Point cp = new Point (p1coords[0] + dx, p1coords[1] + dy);
		double ratio = (HORSE_VERT_OFFSET / Math.sqrt((dx * dx) + (dy * dy)));
		if (dx > 0) {
			cp.translate((int) (dy * ratio), (int) (-dx * ratio));
		} else {
			cp.translate((int) (-dy * ratio), (int) (dx * ratio));
		}

		// Create the curve
		QuadCurve2D q = new QuadCurve2D.Float();
		q.setCurve(p1, cp, p2);

		// Add it to the lines
		lines.add(new JumpingTrackOrnament(q));

		// Add a dot at the end.
		lines.add(new JumpingTrackOrnament(p2coords));
	}

	/*
	 * Add a bunch of lines to the line vector with a dot at the end
	 *
	 * @param	start			The space to start at.
	 * @param	length			The number of spaces to link together.
	 * @param	horseOffset		Which direction to offset the horse.
	 *							true => up, false => down
	 */
	private void addLine(int start, int length, boolean horseOffset) {
		if (length > 0) {
			// Determine the end spot
			int end = Math.min((start + length), (JumpingClientModel.TRACK_SPACES - 1));

			// Add line segments from start to end
			for (int i=start; i<end; i++) {
				lines.add(new JumpingTrackOrnament( getHorsePoint(i, horseOffset),
													getHorsePoint(i+1, horseOffset)));
			}

			// Add the dot at the end.
			lines.add(new JumpingTrackOrnament(getHorsePoint(end, horseOffset)));
		}
	}

	/**
	 * Save the current ornaments in the "archived" version.  The archived ornaments
	 * are drawn, but cannot be selected.
	 */
	public void archiveOrnaments() {
		archivedIcons = icons;
		archivedLines = lines;
		icons = null;
		lines = null;
		archivedJumpOwner = model.getCurrJumpOwner();
	}

	/**
	 * Remove all of the archived ornaments from the display.
	 */
	public void clearArchivedOrnaments() {
		archivedIcons = null;
		archivedLines = null;
	}

	/**
	 * Update the graphics depending on the model
	 *
	 * @param	g				The graphics area to draw on
	 */
	public void paintComponent (Graphics g) {

		// Draw the track background
		Jgraphics.paintImage(g, 0, overhang, JumpingImages.TRACK, 0, 0);

		// Draw the fences
		paintFences(g, model.getAllFences());

		// If the played cards have changed since the last time we redrew the
		// track, then we need to re-enumerate the lines & icons.
		if (model.playedCardsChanged()) {
			enumerateOrnaments();
		}

		// Draw the track lines for the most recent played cards
		paintLines(g, lines, model.getCurrJumpOwner());
		paintLines(g, archivedLines, archivedJumpOwner);

		// Draw the horses
		// If my opponent is behind me, then draw him first, otherwise
		// draw me first.  (This guarantees that the horse that is lower
		// on the board will be drawn in front of the horse that is
		// higher on the board.)
		int myLoc = model.getHorseLocation(myPlayerSeatNum);
		int hisLoc = model.getHorseLocation(oppSeatNum);
		if (hisLoc < myLoc) {
			paintHorse(g, oppSeatNum, hisLoc, false, 0);
			paintHorse(g, myPlayerSeatNum, myLoc, true, 0);
		} else {
			paintHorse(g, myPlayerSeatNum, myLoc, true, 0);
			paintHorse(g, oppSeatNum, hisLoc, false, 0);
		}

		// Draw the track icons for the most recent played cards
		paintIcons(g, icons, true);
		paintIcons(g, archivedIcons, false);

		// If there is a shadow horse to draw, then draw it.
		int shadowLoc = model.getShadowHorseLoc();
		int shadowPlayer = model.getCurrJumpOwner();
		if ((shadowPlayer != realMyPlayerSeatNum) && (shadowLoc > 0)) {
			boolean horseOffset = (shadowPlayer == myPlayerSeatNum);
			paintHorse(g, shadowPlayer, shadowLoc, horseOffset, 1);
		}

		// If we're modifying the fences, then we need to outline the currently
		// selected space.
		if ((selectedTrackSpace >= 0) && model.isCreatingTrack()) {
			highlightSpace(g, selectedTrackSpace);
		}

		// If it is game over state and if editing is disabled, then draw the lock.
		if (model.isGameOver() && !model.allowEdits()) {
			Jgraphics.paintImage(g, centerX, centerY, JumpingImages.PADLOCK, 0, 0);
		}

		// For debug, show the requested & actual bounds.
		// And show the centers of all of the spaces.
		if (debugShowBounds) {
			Jgraphics.paintRects(g, getBounds(), getPreferredSize());

			for (int i = 0; i < JumpingClientModel.TRACK_SPACES; i++) {
				g.setColor(new Color(255, 0, 0));
				int [] loc = getGraphicalArrayForSpace(i);
				g.drawRect(loc[0], loc[1], 1, 1);
			}
		}
	}

	private void highlightSpace(Graphics g, int space) {
		int [] loc = getGraphicalArrayForSpace(space);
		Jgraphics.paintImage(g, loc[0], loc[1], JumpingImages.TRACK_HIGHLIGHT, 0, 0);
	}

	/**
	 * Draw all of the fences in the given vector.
	 *
	 * @param	g			The graphics area to draw on
	 * @param	fences		The list of fences to draw
	 */
	private void paintFences(Graphics g, Vector fences) {
		ListIterator iter = fences.listIterator();
		while (iter.hasNext()) {
			JumpingFence f = (JumpingFence) iter.next();
			paintFence(g, f.location(), f.type());
		}
	}

	/**
	 * Draw a fence on a space
	 *
	 * @param	g			The graphics area to draw on
	 * @param	space		The track space to paint on
	 * @param	fenceId		The number (1..6) of the fence to draw
	 */
	private void paintFence(Graphics g, int space, int fenceId) {

		if ((fenceId <= 0) || (fenceId > 6)) {
			// If this is an invalid fence height, then ignore it.
			return;
		}

		if (fenceId == JumpingClientModel.WATER_FENCE) {
			// Get the center point of the jump as betwen spaces 1 & 2 of the jump
			int [] p1 = getGraphicalArrayForSpace(space+1);
			int [] p2 = getGraphicalArrayForSpace(space+2);
			int [] location = pointAverage(p1, p2);

			Jgraphics.paintImage(g,
					location[0], location[1],
					JumpingImages.WATER_JUMP,
					0, getHorseFace(space));
		} else {
			// Look up the location (dx, dy) coordinates of the space
			int [] location = getGraphicalArrayForSpace(space);

			// Paint the fence
			Jgraphics.paintImage(g,
					location[0], location[1] - 3,		// x, y
					JumpingImages.FENCES,				// Image Id
					(fenceId - 1), 0);					// tx, ty
		}
	}

	/**
	 * Draw a horse on a space
	 *
	 * @param	g			The graphics area to draw on
	 * @param	horseId		The Id of the horse to draw
	 * @param	space		The space to draw the horse on
	 * @param	offset		Which direction to offset the horse.
	 *						true => up, false => down
	 * @param	shadowed	0 => Solid horse, 1 => Shadowed horse
	 */
	private void paintHorse(Graphics g, int horseId, int space, boolean offset, int shadowed) {
		if (space < 0) { return; }

		// Get the (x,y) graphical location of the horse
		int [] location = getHorsePoint(space, offset);

		// Paint the horse
		Jgraphics.paintImage(g,
					location[0], location[1],			// x, y
					(JumpingImages.HORSE0 + horseId),	// Image Id
					getHorseFace(space), shadowed);		// tx, ty
	}

	/**
	 * Draw a horse highlight on a space
	 *
	 * @param	g			The graphics area to draw on
	 * @param	space		The space to draw the horse outline on
	 * @param	offset		Which direction to offset the horse outline.
	 *						true => up, false => down
	 */
	private void paintHorseHighlight(Graphics g, int space, boolean offset) {
		if (space < 0) { return; }

		// Get the (x,y) graphical location of the horse
		int [] location = getHorsePoint(space, offset);

		// Paint the horse
		Jgraphics.paintImage(g,
					location[0], location[1],		// x, y
					JumpingImages.HORSE_HIGHLIGHT,	// Image Id
					getHorseFace(space), 0);		// tx, ty
	}

	/**
	 * Determine which direction the horse should face, given a space
	 *
	 * @param	space		The board space the horse is on.
	 * @return	0 => horse faces right
	 *			1 => horse faces left
	 */
	private int getHorseFace(int space) {
		// Determine which direction the horse is facing
		if (((space >= 12) && (space <= 23)) ||
			((space >= 36) && (space <= 47))) {
			return 1;
		}
		return 0;
	}

	/**
	 * Return the (x,y) graphical location where the given horse should be
	 * centered for any given space on the board.
	 *
	 * @param	space		The space to put the horse on
	 * @param	offset		Which direction to offset the horse.
	 *						true => up, false => down
	 * @return	a two dimensional array with entry 0 being the x-coord.
	 *			and entry 1 being the y-coord.
	 */
	private int [] getHorsePoint(int space, boolean offset) {
		// Look up the location (dx, dy) coordinates of the space
		int [] location = getGraphicalArrayForSpace(space);

		// Adjust the horse up or down, depending on the requested offset
		location[1] += ((offset ? -HORSE_VERT_OFFSET : HORSE_VERT_OFFSET) - 8);

		// return the location
		return location;
	}

	/**
	 * Return the (x,y) graphical location where a caution sign should be
	 * centered for any given space on the board.
	 *
	 * @param	space		The space to put the sign on
	 * @return	a two dimensional array with entry 0 being the x-coord.
	 *			and entry 1 being the y-coord.
	 */
	private int [] getCautionPoint(int space) {
		int [] location = getGraphicalArrayForSpace(space);
		location[0] -= 18;
		location[1] += HORSE_VERT_OFFSET;

		return location;
	}

	/**
	 * Paint the track icons given a vector of played cards
	 *
	 * @param	g				The graphics area to draw on
	 * @param	iconVec			The list of icons to draw
	 */
	private void paintIcons(Graphics g, Vector iconVec, boolean paintSelection) {

		if (iconVec == null) {
			return;
		}

		// Go through the list of icons and draw them
		ListIterator iter = iconVec.listIterator();
		while (iter.hasNext()) {
			// Get the icon info
			JumpingTrackOrnament orn = (JumpingTrackOrnament) iter.next();
			if (orn.isHorse()) {
				paintHorse(g, orn.getHorseId(), orn.getHorseSpace(), orn.getHorseOffset(), 1);
			} else {
				Jgraphics.paintImage(g, orn.p1x, orn.p1y, orn.imageId, orn.id, 0);
			}
		}

		// Paint the selected icon, if there is one.
		if (paintSelection && (selectedIcon != null)) {
			if (selectedIcon.isHorse()) {
				paintHorseHighlight(g, selectedIcon.getHorseSpace(), selectedIcon.getHorseOffset());
			} else if (selectedIcon.isPlus()) {
				Jgraphics.paintImage(g, selectedIcon.p1x, selectedIcon.p1y, JumpingImages.PLUS, 1, 0);
			} else {
				Jgraphics.paintImage(g, selectedIcon.p1x, selectedIcon.p1y,
				                     JumpingImages.TRACK_ICONS_HIGHLIGHT,
				                     (selectedIcon.id / 5), 0);
			}
		}
	}

	/**
	 * Return the graphical point where the icon for the given card should
	 * be placed.  This will add in the offset to ensure that icons are
	 * overlapped correctly.  This also updates the iconsShownOnSpace array
	 * to reflect this new icon that was added.
	 *
	 * @param	spaceType		The type of offset to add to the point for
	 *							overlapping icons.
	 * @param	space			The effective space for the card
	 * @param	horseOffset		Which direction to offset the horse.
	 *							true => up, false => down
	 */
	private int [] getIconPoint(int spaceType, int space, boolean horseOffset) {

		int [] basePt = getHorsePoint(space, horseOffset);

		// Move the icons down a bit.
		basePt[1] += 10;

		if (spaceType == BETWEEN_SPACE_TYPE) {
			int [] nextPt = getHorsePoint(space+1, horseOffset);
			nextPt[1] += 10;
			basePt = pointAverage(basePt, nextPt);
		}

		// Get the offset to add for overlapping icons on this space
		int iconOffset = 10 * iconsShownOnSpace[spaceType][space];
		iconsShownOnSpace[spaceType][space] += 1;

		// Add that offset to the points
		basePt[0] += iconOffset;
		basePt[1] += iconOffset;

		return basePt;
	}

	/*
	 * Average the two points provided
	 */
	private int [] pointAverage(int [] a, int [] b) {
		int [] c = new int [2];
		c[0] = (a[0] + b[0] ) / 2;
		c[1] = (a[1] + b[1] ) / 2;
		return c;
	}

	/**
	 * Paint the track lines
	 *
	 * @param	g				The graphics area to draw on
	 * @param	lineVec			The vector of lines to draw
	 * @param	lineColor		The seat # of the player whose color is to be used.
	 */
	private void paintLines(Graphics g, Vector lineVec, int lineColor) {

		if (lineVec == null) {
			return;
		}

		// Configure the lines to draw with
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));

		// Set the line color to the color of the horse that is moving.
		g.setColor(Jgraphics.horseColors[lineColor]);

		// Go through the list of lines and draw them
		ListIterator iter = lineVec.listIterator();
		while (iter.hasNext()) {
			// Get the line info
			JumpingTrackOrnament orn = (JumpingTrackOrnament) iter.next();
			if (orn.isLine()) {
				g.drawLine(orn.p1x, orn.p1y, orn.p2x, orn.p2y);
			} if (orn.isCurve()) {
				g2d.draw(orn.getCurve());
			} else {
				g.fillOval(orn.p1x, orn.p1y, orn.id, orn.id);
			}
		}
	}

	/**
	 * Given a space ID on the board, return an array of two elements
	 * which are the (x,y) coordinates of the center of that board space.
	 *
	 * @param	space		The space whose location is desired
	 * @return	a two dimensional array with entry 0 being the x-coord.
	 *			and entry 1 being the y-coord.
	 */
	private int [] getGraphicalArrayForSpace(int space) {
		int [] result = new int [2];

		// Pin space to be between 0 & (TRACK_SPACES-1)
		if (space < 0) {
			space = 0;
		} else if (space >= JumpingClientModel.TRACK_SPACES) {
			space = JumpingClientModel.TRACK_SPACES - 1;
		}

		int row = space / 12;
		int col = space % 12;

		result[1] = (row * 55) + 30 + overhang;

		if ((row & 0x01) == 0) {
			// Even rows run left to right
			result[0] = (col * 50) + 26;
		} else {
			// Odd rows run right to left
			result[0] = ((11 - col) * 50) + 26;
		}

		return result;
	}

	/**
	 * Given (x,y) coordinates in the board, return the space that the
	 * coordinates refer to.
	 *
	 * @param	(x,y)	Pixel coordinates on the board.
	 * @return the space on the board pointed to.  Will return -1 for
	 *         coordinates outside of the board.
	 */
	private int getSpaceForCoordinates(int x, int y) {
		int row = (y - overhang) / 55;
		int col = x / 50;

		if ((row >= 0) && (row <= 3) && (col >= 0) && (col <= 11)) {
			switch (row) {
				case 0:	return col;
				case 1: return 12 + (11 - col);
				case 2: return 24 + col;
				case 3: return 36 + (11 - col);
			}
		}

		return -1;
	}

	/*
	 * Compute the amount that a horse image on the top row will stick up above the track
	 */
	private int horseOverhang() {
		int [] tmp = getHorsePoint(0, true);
		return (-(tmp[1] - (Jgraphics.imageHeights[JumpingImages.HORSE0] / 2)));
	}

	/*
	 * Compute the amount that a fence image on the top row will stick up above the track
	 */
	private int fenceOverhang() {
		int [] tmp = getGraphicalArrayForSpace(0);
		return (-(tmp[1] - 3 - (Jgraphics.imageHeights[JumpingImages.FENCES] / 2)));
	}

}
