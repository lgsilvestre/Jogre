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

import java.awt.event.MouseEvent;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.common.util.GameLabels;

import org.jogre.texasHoldEm.common.CommTexasHoldEmBidAction;
import org.jogre.texasHoldEm.common.CommTexasHoldEmCommonCard;
import org.jogre.texasHoldEm.common.CommTexasHoldEmGiveHand;
import org.jogre.texasHoldEm.common.CommTexasHoldEmHandOver;
import org.jogre.texasHoldEm.common.CommTexasHoldEmHandRequest;
import org.jogre.texasHoldEm.common.CommTexasHoldEmOfficialTime;
import org.jogre.texasHoldEm.std.TexasHoldEmButton;

/**
 * Controller for the TexasHoldEm game.
 *
 * This controller handles both network messages from the server and mouse
 * movement in the slider component.  The slider component is the only component
 * that has mouse input.
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class TexasHoldEmController extends JogreController {

	// links to game data
	private TexasHoldEmClientModel model;
	private TexasHoldEmGraphics thGraphics;
	private TexasHoldEmBidSliderComponent bidSliderComponent;
	private TexasHoldEmButton foldButton, callButton, bidButton;

	// Keep track of status of dragging the thumb or the range
	private boolean draggingThumb = false;
	private boolean draggingRange = false;

	// Seat number of the player here
	private int mySeatNum = -1;

	// Game labels object for getting the button text.
	private GameLabels labels;

	// Array to hold arguments when setting the text of the bid & call buttons
	private Object [] textArgs = new Object [1];

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param bidSliderComponent	The bid slider component
	 * @param foldButton			The fold button
	 * @param bidButton				The bid button
	 */
	public TexasHoldEmController (	TexasHoldEmClientModel model,
									TexasHoldEmBidSliderComponent bidSliderComponent,
									TexasHoldEmButton foldButton,
									TexasHoldEmButton callButton,
									TexasHoldEmButton bidButton ) {
		super(model, bidSliderComponent);

		// Save parameters.
		this.model = model;
		this.bidSliderComponent = bidSliderComponent;
		this.foldButton = foldButton;
		this.callButton = callButton;
		this.bidButton = bidButton;

		thGraphics = TexasHoldEmGraphics.getInstance();
		labels = GameLabels.getInstance();
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.resetGame();

		mySeatNum = getSeatNum();

		draggingThumb = false;
		draggingRange = false;

		// Ask the sever for our cards
		conn.send (new CommTexasHoldEmHandRequest (conn.getUsername()));
	}

	/**
	 * Handle mouse pressed events
	 *
	 * @param mEv				The mouse event
	 */
	public void mousePressed (MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {

			// Assume mouse is not dragging
			draggingThumb = false;
			draggingRange = false;

			if (bidSliderComponent.isPointInThumb(mEv.getX(), mEv.getY())) {
				draggingThumb = true;
			} else if (bidSliderComponent.isPointInRange(mEv.getX(), mEv.getY())) {
				draggingRange = true;
			}
		}
	}

	/**
	 * Handle mouse dragged events
	 *
	 * @param mEv				The mouse event
	 */
	public void mouseDragged (MouseEvent mEv) {
		if (isGamePlaying() && isThisPlayersTurn()) {

			if (draggingThumb) {
				bidSliderComponent.dragThumbTo(mEv.getX(), mEv.getY());
				setBidButtonText();
				bidSliderComponent.repaint();
			} else if (draggingRange) {
				bidSliderComponent.dragRangeTo(mEv.getX(), mEv.getY());
				bidSliderComponent.repaint();
			}
		}
	}

	/**
	 * Handle a fold button click event
	 */
	public void foldButtonClicked() {
		sendBidAction (-1);
	}

	/**
	 * Handle a call button click event
	 */
	public void callButtonClicked() {
		sendBidAction (model.getCallBid(mySeatNum));
	}

	/**
	 * Handle a bid button click event
	 */
	public void bidButtonClicked() {
		sendBidAction (bidSliderComponent.getCurrentThumbValue());
	}

	/**
	 * Send a bid action message to the server for the given amount of bid.
	 *
	 * @param bidAmount		The amount to put into the message to the server.
	 */
	private void sendBidAction (int bidAmount) {
		conn.send (new CommTexasHoldEmBidAction (conn.getUsername(),
					                            mySeatNum,
					                            bidAmount));
	}

	/*
	 * Set the text of the bid button depending on the current value of
	 * the slider.
	 */
	private void setBidButtonText() {
		if (mySeatNum < 0) {
			return;
		}

		textArgs[0] = bidSliderComponent.getCurrentBidString();

		bidButton.setText (labels.get(getBidButtonKey(), textArgs));
	}

	/*
	 * Return the label key string that should be used to set the text for
	 * the bid button.
	 */
	private String getBidButtonKey() {
		int currThumbValue = bidSliderComponent.getCurrentThumbValue();

		if (currThumbValue == model.getCurrentBid()) {
			return "button.call";
		}

		if ((currThumbValue == bidSliderComponent.getMaxThumbValue()) &&
		    (model.isAllInAt(mySeatNum, currThumbValue)) ) {
			return "button.call.allIn";
		}

		return "button.bid";
	}

	/*
	 * Set the text of the call button depending on the current value of the bid
	 */
	private void setCallButtonText() {
		if (mySeatNum < 0) {
			return;
		}

		int callBid = model.getCallBid(mySeatNum);
		textArgs[0] = thGraphics.currencyFormatter.format(callBid);
		if (model.isAllInAt(mySeatNum, callBid)) {
			callButton.setText (labels.get("button.call.allIn", textArgs));
		} else {
			callButton.setText (labels.get("button.call", textArgs));
		}
	}

	/**
	 * Receive a message from the server.
	 *
	 * @param	message			The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals (CommTexasHoldEmGiveHand.XML_NAME)) {
			handleGiveHandMessage (new CommTexasHoldEmGiveHand(message));
		} else if (messageType.equals (CommTexasHoldEmBidAction.XML_NAME)) {
			handleBidActionMessage (new CommTexasHoldEmBidAction(message));
		} else if (messageType.equals (CommTexasHoldEmCommonCard.XML_NAME)) {
			handleCommonCardMessage (new CommTexasHoldEmCommonCard(message));
		} else if (messageType.equals (CommTexasHoldEmHandOver.XML_NAME)) {
			handleHandOverMessage (new CommTexasHoldEmHandOver(message));
		} else if (messageType.equals (CommTexasHoldEmOfficialTime.XML_NAME)) {
			handleOfficialTimeMessage (new CommTexasHoldEmOfficialTime(message));
		}
	}

	/**
	 * Handle a give hand message from the server.
	 *
	 * @param	theMsg			The message from the server
	 */
	private void handleGiveHandMessage (CommTexasHoldEmGiveHand theMsg) {
		int destPlayer = theMsg.getDestPlayerSeat();

		// If the cards are for me, then this must be the start of a new hand
		if (destPlayer == mySeatNum) {
			// Update the bid slider to reflect the current legal bid range and move
			// the thumb to the minimum bid.
			bidSliderComponent.setThumbLimits(model.getLegalBidRange(mySeatNum));
			bidSliderComponent.setThumbValue(0);

			// Set the bid buttons correctly
			handleNextPlayerMessage();
		}

		// Give the cards to the player
		model.giveCardToPlayer(destPlayer, 0, theMsg.getCard(0));
		model.giveCardToPlayer(destPlayer, 1, theMsg.getCard(1));
	}

	/**
	 * Handle a bid action message from the server.
	 *
	 * @param	theBidMsg			The message from the server
	 */
	private void handleBidActionMessage (CommTexasHoldEmBidAction theBidMsg) {
		model.makeBid(theBidMsg.getPlayerSeat(), theBidMsg.getAmount());
		bidSliderComponent.setThumbLimits(model.getLegalBidRange(mySeatNum));
		setBidButtonText();
		setCallButtonText();
	}

	/**
	 * Handle a common card message from the server.
	 *
	 * @param	theMsg			The message from the server
	 */
	private void handleCommonCardMessage (CommTexasHoldEmCommonCard theMsg) {
		model.giveCommonCard(theMsg.getIndex(), theMsg.getCard());
		if (theMsg.getNextRound()) {
			model.clearLastAction();
			bidSliderComponent.setThumbLimits(model.getLegalBidRange(mySeatNum));
			setBidButtonText();
			setCallButtonText();
		}
	}

	/*
	 * Handle a hand over message from the server.
	 *
	 * @param	theMsg			The message from the server
	 */
	private void handleHandOverMessage(CommTexasHoldEmHandOver theMsg) {
		model.saveHandHistory();
		model.setHistoryWinner(theMsg.getWinningHandValue(), theMsg.getHandValues());
		model.updateHoldings(theMsg.getHoldings());

		// Start the next hand
		model.startNextHand();

		// If I'm still in the game, then ask for my hand of cards.
		if ((mySeatNum >= 0) && model.playerIsAlive(mySeatNum)) {
			conn.send (new CommTexasHoldEmHandRequest (conn.getUsername()));
		}
	}

	/**
	 * We've gotten a "NextPlayer" message from the server, so if it's now
	 * our turn to play, we need to enable the bid buttons.
	 */
	public void handleNextPlayerMessage () {
		model.setActivePlayer(getCurrentPlayerSeatNum());

		// If it's our turn, enable the bid buttons
		if (isThisPlayersTurn()) {
			setBidButtonText();
			setCallButtonText();
			foldButton.setEnabled(true);
			callButton.setEnabled(true);
			bidButton.setEnabled(true);
		}
	}

	/*
	 * Handle an official time message from the server.
	 *
	 * @param	theMsg		The official time message.
	 */
	private void handleOfficialTimeMessage(CommTexasHoldEmOfficialTime theMsg) {
		model.setLocalTime(theMsg.getStage(), theMsg.getTime());
	}
}
