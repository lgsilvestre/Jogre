/*
 * JOGRE (Java Online Gaming Real-time Engine) - Car Tricks
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
package org.jogre.carTricks.client;

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.ChatGameComponent;
import org.jogre.common.util.GameLabels;

import org.jogre.common.comm.CommGameOver;
import org.jogre.carTricks.common.CommCarTricksSetBid;
import org.jogre.carTricks.common.CommCarTricksPlayCard;
import org.jogre.carTricks.common.CommCarTricksRequestHand;
import org.jogre.carTricks.common.CommCarTricksSendHand;
import org.jogre.carTricks.common.CommCarTricksMoveCar;
import org.jogre.carTricks.common.CommCarTricksScore;
import org.jogre.carTricks.common.CarTricksPath;

import java.util.Vector;
import java.text.MessageFormat;

// Controller for the Car Tricks game
public class CarTricksMasterController extends JogreController {

	// Links to game data and components & controllers
	private CarTricksClientModel model;
	private CarTricksTrackController trackController;
	private CarTricksPlayerHandController handController;
	private CarTricksGameStateComponent gameStateComponent;
	private JogreTableFrame tableFrame;
	private ChatGameComponent theChatBox;
	private CarTricksScoreDialog scoreDialog = null;

	// A place to keep our queue of messages if we can't handle them immediately
	private Vector savedMessages = new Vector();

	// These are strings that are read from the properties file for use in the chat window.
	private GameLabels labels;
	private String chatGameName;
	private MessageFormat chatNoMovementWreckFormat;
	private MessageFormat chatNoMovementAllEventFormat;
	private MessageFormat chatNoMovementBlockedFormat;
	private MessageFormat chatSingleMoveFormat;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param trackController		The track controller
	 * @param handController		The hand controller
	 */
	public CarTricksMasterController(	CarTricksClientModel model,
										CarTricksTrackController trackController,
										CarTricksPlayerHandController handController,
										CarTricksGameStateComponent gameStateComponent,
										JogreTableFrame tableFrame) {
		super(model, null);

		this.model = model;
		this.trackController = trackController;
		this.handController = handController;
		this.gameStateComponent = gameStateComponent;
		this.tableFrame = tableFrame;
		theChatBox = tableFrame.getMessageComponent();

		// Read text strings for use with the chat
		labels = GameLabels.getInstance();
		chatGameName = labels.get("chat.gameName");
		chatNoMovementWreckFormat = new MessageFormat (labels.get("chat.NoMovementWreck.format"));
		chatNoMovementAllEventFormat = new MessageFormat (labels.get("chat.NoMovementAllEvent.format"));
		chatNoMovementBlockedFormat = new MessageFormat (labels.get("chat.NoMovementBlocked.format"));
		chatSingleMoveFormat = new MessageFormat (labels.get("chat.SingleMove.format"));
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		// Reset the model
		model.resetGame();

		if (getSeatNum() >= 0) {
			// We are a real player, so request our hand of cards from the server
			// Request our hand of cards from the server
			conn.send(new CommCarTricksRequestHand(conn.getUsername()));
		} else {
			// We are just a spectator, so move into Setting Bid phase as a spectator
			model.changePhase(CarTricksClientModel.SETTING_BID_SPECTATOR);
		}
	}

	/**
	 * Handle receving messages from the server
	 *
	 * @param	message		The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		if (model.getTrackDatabase() == null) {
			// We don't have a model, but we got a table message.  This means
			// that we must have attached in the middle of a game for which
			// we didn't have the database, and it is being sent by the
			// server, but hasn't arrived yet.  Therefore, we don't know how
			// to deal with table messages yet.  So, we'll just put this
			// message into a FIFO and deal with it after we get the
			// database.
			savedMessages.add(message);
		} else {
			// Handle the message
			handleTableMessage (message);
		}
	}

	/**
	 * If there are any savedMessages, now is the time to handle them.
	 */
	public void clearSavedMessages() {
		while (!savedMessages.isEmpty()) {
			handleTableMessage((XMLElement) savedMessages.remove(0));
		}
	}

	/**
	 * Handle a table message from the server.
	 *
	 * @param	message		The message from the server
	 */
	private void handleTableMessage (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(CommCarTricksSendHand.XML_NAME)) {
			handleSendHand(new CommCarTricksSendHand(message));
		} else if (messageType.equals(CommCarTricksPlayCard.XML_NAME)) {
			handlePlayCard(new CommCarTricksPlayCard(message));
		} else if (messageType.equals(CommCarTricksMoveCar.XML_NAME)) {
			handleMoveCar(new CommCarTricksMoveCar(message));
		} else if (messageType.equals(CommCarTricksScore.XML_NAME)) {
			handleScore(new CommCarTricksScore(message));
		}

	}

	/**
	 * Handle a message that has sent our hand of cards to us.
	 */
	private void handleSendHand(CommCarTricksSendHand theHandMsg) {
		model.initializeBid();
		model.setHand(theHandMsg.getHand());
		model.changePhase(CarTricksClientModel.SETTING_BID);
	}

	/**
	 * Handle a message that tells that a card has been played
	 */
	private void handlePlayCard(CommCarTricksPlayCard thePlayCardMsg) {
		int userId = getSeatNum(thePlayCardMsg.getUsername());
		model.playCard(userId, thePlayCardMsg.getCard(), thePlayCardMsg.isFinalCard());
	}

	/**
	 * Handle a message that tells us that a car has moved.
	 */
	private void handleMoveCar(CommCarTricksMoveCar theMoveCarMsg) {
		int userId = getSeatNum(theMoveCarMsg.getUsername());
		model.moveCar(userId, theMoveCarMsg.getPath());
	}

	/**
	 * Handle a message that tells us the game is over and what the score is.
	 */
	private void handleScore(CommCarTricksScore theScoreMsg) {
		// Tell the model that the game is over, since we don't know that.
		// (Note: Each client *could* determine how many cards each player
		// was dealt and keep track of them during the game and know when one
		// player ran out of them, but it's easier to just let the server
		// do that (which it has to anyway) and tell us when the game is over.)
		model.changePhase(model.GAME_OVER);

		// If there isn't a score dialog yet, then create one
		if (scoreDialog == null) {
			scoreDialog = new CarTricksScoreDialog(
					tableFrame,												// owner
					GameLabels.getInstance().get("score.dialog.title"),		// title
					model.getNumPlayers(),									// numPlayers
					conn.getTable().getPlayerList(),						// PlayerList
					model.getCarPositions()									// The car positions
					);
		}

		// Add this score to the dialog
		if (scoreDialog.addScore(getSeatNum(theScoreMsg.getUsername()), theScoreMsg.getBid())) {
			// The dialog has been shown, so we don't need to keep the handle to it.
			scoreDialog = null;
		}
	}

	/**
	 * This is called after every next_player message from the server.
	 * It will check to see if it is now time for the car to move, and if
	 * so and the car has only a single legal move, then this will submit
	 * the move message back to the server on behalf of the human player.
	 * It will also issue a chat message indicating that it has done so.
	 */
	public void checkForZeroLengthCarMove(XMLElement message) {
		Vector allPaths = model.getAllPaths();
		if (allPaths != null) {
			// A trick has just been finished.
			if (allPaths.size() == 1) {
				// And there is only a single move that the car can make.
				CarTricksPath thePath = (CarTricksPath) allPaths.firstElement();

				// Put up a chat message explaining what just happened
				Object [] args = {
					labels.get("gamestate.movingCar." + model.getActiveCar(), ""),
					getPlayer(model.getWreckPlayerId())
				};

				theChatBox.receiveMessage(
					chatGameName,
					getCorrectMessageFormat(thePath).format(args));

				// If we are the player moving the car, then go ahead and send
				// this move to the server on behalf of the human.
				if (isThisPlayersTurn()) {
					trackController.sendPath(thePath);
				}
			}
		}
	}

	/**
	 * This routine will determine the reason for no car movement this turn
	 * and return the correct MessageFormat to be used in putting the chat
	 * message in the chat window.
	 */
	private MessageFormat getCorrectMessageFormat(CarTricksPath thePath) {
		if (model.wreckPlayed()) {
			return chatNoMovementWreckFormat;
		}

		if (model.onlyEventsPlayed()) {
			return chatNoMovementAllEventFormat;
		}

		if (thePath.pathLength() == 1) {
			return chatNoMovementBlockedFormat;
		}

		return chatSingleMoveFormat;
	}

}
