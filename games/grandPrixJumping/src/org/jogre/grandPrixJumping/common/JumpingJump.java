/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter
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
package org.jogre.grandPrixJumping.common;

import java.util.Vector;
import java.util.ListIterator;

/**
 * Structure to hold info about a Jump for Grand Prix Jumping.
 * A jump is a set of cards that starts with a saddle and contains
 * cantor and/or jump cards.
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public final class JumpingJump {

	// The cards that make up the jump
	private JumpingCard saddleCard;
	private Vector canterCards = new Vector();
	private Vector heightCards = new Vector();
	private Vector lengthCards = new Vector();

	// Features of the jump
	private boolean canterFirst = false;
	private int startSpace;					// The space where the entire jump starts
	private int endSpace;					// The ending space of the entire jump
	private int jumpLength = 0;				// The total of length cards played
	private int jumpHeight = 0;				// The total of height cards played
	private int canterLength = 0;			// The total of canter cards played
	private int jumpingStart= 0;			// The space where the height/length cards start
	private int faultPoints = -1;
	private boolean committed = false;		// Indicates if this jump has been committed by the player or not.

	// Used to keep track of whether canter cards are prohibited or not.
	private boolean canterProhibited = false;

	// Used during assignation of height cards to fences
	private int [] bestArray;

	// A link to the core model.  This is needed to get the layout of the fences.
	JumpingCoreModel model;

	/**
	 * Constructor for a Jump.
	 *
	 * @param	saddleCard		The saddle card that starts the jump
	 * @param	startSpace		The space on the board that the jump starts.
	 * @param	model			The core model that will provide fence info.
	 * @param	canterProhibited   Indicates if canter cards are prohibited.
	 */
	public JumpingJump(JumpingCard saddleCard, int startSpace, JumpingCoreModel model, boolean canterProhibited) {
		this.saddleCard = saddleCard;
		this.startSpace = startSpace;
		this.endSpace = startSpace;
		this.model = model;

		saddleCard.setEffectiveSpace(startSpace);
		saddleCard.setVisualSpace(startSpace);

		this.canterProhibited = saddleCard.isFakeSaddle() ? canterProhibited : false;
	}

	/**
	 * Retrieve info about the Jump
	 */
	public boolean isValid()     { return (faultPoints >= 0); }
	public boolean isCommitted() { return committed; }
	public int startSpace()      { return startSpace; }
	public int endSpace()        { return endSpace; }
	public int faultPoints()     { return faultPoints; }
	public int getJumpingStart() { return jumpingStart; }
	public int getJumpingEnd()   { return pinToTrackEnd(jumpingStart + jumpLength); }

	public JumpingCard getSaddleCard() { return saddleCard; }
	public Vector getCanterCards()     { return canterCards; }
	public Vector getLengthCards()     { return lengthCards; }
	public Vector getHeightCards()     { return heightCards; }

	/**
	 * Return a vector of the fences that this jump jumps over.
	 */
	public Vector getFences() {
		return model.getFencesInRange(jumpingStart, jumpLength);
	}

	/**
	 * Determine if the given card is the same as the saddle card that
	 * starts this jump.
	 */
	public boolean isSameSaddleCard(JumpingCard card) {
		return (saddleCard.equals(card) && (saddleCard.getEffectiveSpace() == card.getEffectiveSpace()));
	}

	/**
	 * Determine if the given card is valid to be played.
	 */
	public boolean okToPlay(JumpingCard newCard) {
		if (newCard.isCanter()) {
			return !canterProhibited;
		} else if (newCard.isHeight() || newCard.isLength()) {
			return ((newCard.cardValue() + jumpLength + jumpHeight) <= 7);
		} else if (newCard.isSpecial()) {
			if (newCard.cardValue() == JumpingCard.RIBBON) {
				return true;
			}
			if ((newCard.cardValue() == JumpingCard.SADDLE) ||
			    (newCard.cardValue() == JumpingCard.FAKE_SADDLE)) {
				boolean empty = ((lengthCards.size() == 0) &&
				                 (heightCards.size() == 0) &&
				                 (canterCards.size() == 0));
				return (!empty && (faultPoints >= 0));
			}
		}

		return false;
	}

	/**
	 * Determine if it is valid to place a fake sadde at the end of this
	 * jump.
	 */
	public boolean canPlaceFakeSaddle() {
		// Can't place a fake saddle if this jump isn't valid.
		if (faultPoints < 0) {
			return false;
		}

		// A fake saddle card can be placed if we've ended with a jump.
		return ( (jumpLength != 0) &&
		         ((canterLength == 0) || canterFirst) );
	}

	/**
	 * Determine if the next jump will have prohibited canter cards.
	 */
	public boolean areFollowingCantersProhibited() {
		// If I'm prohibited, then my followers will be also.
		// If I'm not prohibited, but I have canter cards, then my followers are prohibited.
		return canterProhibited || (canterLength != 0);
	}

	/**
	 * Add a card to a jump
	 */
	public boolean addCard(JumpingCard newCard) {
		if (newCard.isCanter()) {
			canterCards.add(newCard);
			canterLength += newCard.cardValue();
			if (canterCards.size() == 1) {
				// This is the first canter card, so we need to determine if canter
				// cards are going first or not.  If this isn't the first canter
				// card played, then canterFirst has already been set to the right
				// value.
				canterFirst = ( (heightCards.size() == 0) && (lengthCards.size() == 0) &&
				                 !saddleCard.isFakeSaddle() );
			}
		} else if (newCard.isHeight()) {
			heightCards.add(newCard);
			jumpHeight += newCard.cardValue();
		} else if (newCard.isLength()) {
			lengthCards.add(newCard);
			jumpLength += newCard.cardValue();
		} else {
			// Invalid card added to jump
			return false;
		}

		setEffectiveLocations();
		return true;
	}

	/**
	 * Remove a card from a jump
	 */
	public boolean removeCard(JumpingCard oldCard) {
		boolean removed = false;

		// Get the right deck
		if (oldCard.isCanter()) {
			removed = removeCardFromDeck(canterCards.listIterator(), oldCard);
			if (removed) {
				canterLength -= oldCard.cardValue();
			}
		} else if (oldCard.isHeight()) {
			removed = removeCardFromDeck(heightCards.listIterator(), oldCard);
			if (removed) {
				jumpHeight -= oldCard.cardValue();
			}
		} else if (oldCard.isLength()) {
			removed = removeCardFromDeck(lengthCards.listIterator(), oldCard);
			if (removed) {
				jumpLength -= oldCard.cardValue();
			}
		}

		if (removed) {
			setEffectiveLocations();
		}
		return removed;
	}

	/*
	 * Remove a card from the given iterator.
	 *
	 * @param	iter		An iterator for the deck to remove the card from.
	 * @param	oldCard		The card to remove.
	 * @return if the card was found or not.
	 */
	private boolean removeCardFromDeck(ListIterator iter, JumpingCard oldCard) {
		// Now, look for the old card
		while (iter.hasNext()) {
			JumpingCard c = (JumpingCard) iter.next();
			if (c.equals(oldCard) && (c.getEffectiveSpace() == oldCard.getEffectiveSpace())) {
				// Found it, so remove it
				iter.remove();
				return true;
			}
		}

		return false;
	}

	/**
	 * Change the starting space.
	 */
	public void setStartingSpace(int newStartSpace) {
		if (this.startSpace != newStartSpace) {
			this.startSpace = newStartSpace;
			saddleCard.setEffectiveSpace(startSpace);
			saddleCard.setVisualSpace(startSpace);
			setEffectiveLocations();
		}
	}

	/**
	 * Return a vector of all of the cards in this jump
	 */
	public Vector getAllCards() {
		Vector v = new Vector();
		addCardsToVector(v);
		return v;
	}

	/**
	 * Add all of the cards in this jump to the given vector
	 */
	public void addCardsToVector(Vector v) {
		v.add(saddleCard);
		if (canterFirst) {
			v.addAll(canterCards);
			v.addAll(lengthCards);
			v.addAll(heightCards);
		} else {
			v.addAll(lengthCards);
			v.addAll(heightCards);
			v.addAll(canterCards);
		}
	}

	/**
	 * Set the value of committed to the one given
	 *
	 * @param	committed		The value to set the committed flag to.
	 */
	public void setCommitted(boolean committed) {
		this.committed = committed;
	}

	/**
	 * Determine the effective locations for all of the cards in the
	 * jump and also determine the number of fault points that will
	 * be assigned if this jump is made.  If the jump is not legal,
	 * then the fault points are set negative.
	 */
	private void setEffectiveLocations() {

		jumpingStart = pinToTrackEnd(canterFirst ? startSpace + canterLength : startSpace);
		int canterStart = pinToTrackEnd(canterFirst ? startSpace : startSpace + jumpLength);
		endSpace = pinToTrackEnd(startSpace + jumpLength + canterLength);

		// Set spaces
		setSpacesFor(canterCards.listIterator(), canterStart, true, true);
		setSpacesFor(lengthCards.listIterator(), jumpingStart, true, false);
		setHeightSpaces(jumpingStart);

		// Determine if this is a legal move or not.
		boolean empty = ((lengthCards.size() == 0) &&
		                 (heightCards.size() == 0) &&
		                 (canterCards.size() == 0));
		boolean halfJump = ((jumpHeight != 0) && (jumpLength == 0));
		boolean canterThroughFence = (model.countFencesInRange(canterStart, canterLength) != 0);
		boolean landOnFence = model.isFenceOn(endSpace);
		boolean jumpOverSpace = ((jumpLength != 0) && (model.countFencesInRange(jumpingStart, jumpLength) == 0));
		if (canterThroughFence || jumpOverSpace || landOnFence || empty || halfJump) {
			faultPoints = -1;
		}
	}

	/**
	 * Pin the given location to the end of the track
	 */
	private int pinToTrackEnd(int space) {
		return Math.min(space, JumpingCoreModel.LAST_SPACE);
	}

	/**
	 * Set the effective locations for all of the cards in the given iterator
	 *
	 * @param	iter		The iterator that has the cards
	 * @param	startSpace	The space to start the move on
	 * @param	advance		If true, each the space of each card is moved by it's value
	 *						If false, all cards are placed on <startSpace>
	 */
	private void setSpacesFor(ListIterator iter, int startSpace, boolean effAdvance, boolean visAdvance) {
		int effSpace = startSpace;
		int visualSpace = startSpace;
		while (iter.hasNext()) {
			JumpingCard c = (JumpingCard) iter.next();
			c.setEffectiveSpace(effSpace);
			c.setVisualSpace(visualSpace);
			if (effAdvance) {
				effSpace = pinToTrackEnd(effSpace + c.cardValue());
			}
			if (visAdvance) {
				visualSpace = pinToTrackEnd(visualSpace + c.cardValue());
			}
		}
	}

	/**
	 * Set the effective locations for the height cards and calculate
	 * the number of fault points created by this jump.
	 */
	private void setHeightSpaces(int startSpace) {
		Vector fences = model.getFencesInRange(startSpace, jumpLength);

		if (fences.size() == 0) {
			// No fences to jump over, so, all cards' effective space is
			// the jump start space
			setSpacesFor(heightCards.listIterator(), startSpace, false, false);
			faultPoints = ((jumpHeight == 0) && (jumpLength == 0)) ? 0 : -1;
			return;
		} else if (fences.size() == 1) {
			// One fence to jump over, so assign all cards to that fence.
			JumpingFence f = (JumpingFence) fences.firstElement();
			setSpacesFor(heightCards.listIterator(), f.location(), false, false);

			// Calculate fault points for jumping too low over fence
			f.resetFaultPoints();
			f.reduceFaultPoints(jumpHeight);
			faultPoints = f.faultPoints();
		} else {
			// Multiple fences to assign cards to.
			assignCardsToFences(heightCards, fences);
		}

		// Add fault points for landing too short over water jump
		int jumpEnd = pinToTrackEnd(jumpingStart + jumpLength);
		int waterFaults = model.getWaterJumpFaults(jumpingStart, jumpLength);
		faultPoints += waterFaults;

		// If we are short on the water jump, then we need to amend the
		// caution sign on the water jump.
		if (waterFaults > 0) {
			JumpingFence f = model.getFenceAt(jumpEnd - 4 + waterFaults);
			if (f.faultPoints() == 0) {
				f.setFaultPoints(waterFaults);
			}
		}
	}

	/**
	 * Find the best way to assign height cards to the fences
	 */
	private void assignCardsToFences(Vector cards, Vector fences) {
		// Calculate the lowest fault points and create <bestArray>
		faultPoints = makeBestApplyArray(cards, fences);

		// Reset the fence to maximum faults.
		resetFenceFaults(fences.listIterator());

		// Now, go through bestArray and assign spots to the cards.
		// And reduce the faults for each fence.
		for (int i=0; i<bestArray.length; i++) {
			JumpingCard c = (JumpingCard) cards.get(i);
			JumpingFence f = (JumpingFence) fences.get(bestArray[i]);
			c.setEffectiveSpace(f.location());
			c.setVisualSpace(f.location());
			f.reduceFaultPoints(c.cardValue());
		}
	}

	/**
	 * Reset all of the fence fault values of the given vector to their
	 * maximum value.
	 */
	private void resetFenceFaults(ListIterator iter) {
		while (iter.hasNext()) {
			((JumpingFence) iter.next()).resetFaultPoints();
		}
	}

	/**
	 * Find the best way to assign height cards to the fences
	 */
	private int makeBestApplyArray(Vector cards, Vector fences) {
		int [] apply = new int [cards.size()];
		int [] cardValues = makeCardValuesArray(cards);
		boolean [] waterFences = makeWaterFenceArray(fences);
		int base = fences.size();

		clearArray(apply);

		int bestFaultPoints = 100;	// Start with something really big!
		do {
			int faults = evalFenceFaults(apply, cardValues, fences, waterFences);
			if (faults < bestFaultPoints) {
				bestArray = (int []) apply.clone();
				bestFaultPoints = faults;
				if (faults == 0) {
					// If we find a config with 0 fault points, then stop there.
					return 0;
				}
			}

		} while (incArray(apply, base) == 0);

		// Didn't find a zero solution, so return the best found
		return bestFaultPoints;
	}

	/**
	 * Evaluate the fault points that result from applying cards to fences.
	 *
	 * @param cardApply     An array that indicates which fence each card should
	 *                      be applied to.
	 * @param cardValues    An array of the height values of each card.
	 * @param fences        A vector of the fences to be jumped.
	 * @param waterFence    An array that indicates whether each fence is a water
	 *                      jump or not.
	 */
	private int evalFenceFaults(int [] cardApply, int [] cardValues, Vector fences, boolean [] waterFence) {
		int [] faultsPerFence = makeFenceValuesArray(fences);

		// Apply each card to the fence it's assigned to
		for (int i=0; i<cardApply.length; i++) {
			int fenceToApply = cardApply[i];
			faultsPerFence[fenceToApply] = Math.max(0, (faultsPerFence[fenceToApply] - cardValues[i]));
		}

		// Add up all of the faults
		int faults = 0;
		for (int i=0; i<faultsPerFence.length; i++) {
			// If there is a water jump that hasn't had any card applied to it,
			// then add 4 fault points
			if (waterFence[i] && (faultsPerFence[i] != 0)) {
				faults += 5;
			} else {
				faults += faultsPerFence[i];
			}
		}

		return faults;
	}

	/*
	 * Given a vector fences, create and return an array of integers whose
	 * values are the height of the corresponding fence.
	 */
	private int [] makeFenceValuesArray(Vector fences) {
		int [] a = new int [fences.size()];
		for (int i=0; i<fences.size(); i++) {
			JumpingFence f = (JumpingFence) fences.get(i);
			a[i] = f.height();
		}
		return a;
	}

	/*
	 * Given a vector of cards, create and return an array of integers whose
	 * values are the values of the corresponding card.
	 */
	private int [] makeCardValuesArray(Vector cards) {
		int [] a = new int [cards.size()];
		for (int i=0; i<cards.size(); i++) {
			JumpingCard c = (JumpingCard) cards.get(i);
			a[i] = c.cardValue();
		}
		return a;
	}

	/*
	 * Given a vector of fences, create and return a boolean array that
	 * indicates whether each fence is a water fence or not.
	 */
	private boolean [] makeWaterFenceArray(Vector fences) {
		boolean [] a = new boolean [fences.size()];
		for (int i=0; i<fences.size(); i++) {
			JumpingFence f = (JumpingFence) fences.get(i);
			a[i] = f.isWaterJump();
		}
		return a;
	}

	/*
	 * Increment the given array by one in the given base.
	 * Return the carry out of the MSD.
	 */
	private int incArray(int [] a, int base) {
		for (int i=0; i<a.length; i++) {
			a[i] = a[i] + 1;
			if (a[i] == base) {
				a[i] = 0;
			} else {
				return 0;
			}
		}
		return 1;
	}

	/* Clear an array to all zeros */
	private void clearArray(int [] a) {
		for (int i=0; i<a.length; i++) {
			a[i] = 0;
		}
	}
}
