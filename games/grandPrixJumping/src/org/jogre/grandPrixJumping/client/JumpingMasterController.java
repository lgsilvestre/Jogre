/*
 * JOGRE (Java Online Gaming Real-time Engine) - Grand Prix Jumping
 * Copyright (C) 2006-2007  Richard Walter (rwalter42@yahoo.com)
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

import nanoxml.XMLElement;

import org.jogre.client.JogreController;
import org.jogre.client.awt.JogreButton;
import org.jogre.client.awt.ChatGameComponent;

import org.jogre.common.util.GameLabels;
import org.jogre.common.comm.Comm;

import java.util.Vector;
import java.util.ListIterator;

import org.jogre.grandPrixJumping.common.CommJumpingActivate;
import org.jogre.grandPrixJumping.common.CommJumpingChooseSort;
import org.jogre.grandPrixJumping.common.CommJumpingDone;
import org.jogre.grandPrixJumping.common.CommJumpingModifyFence;
import org.jogre.grandPrixJumping.common.CommJumpingMoveCards;
import org.jogre.grandPrixJumping.common.CommJumpingSetShadowHorse;
import org.jogre.grandPrixJumping.common.JumpingCard;
import org.jogre.grandPrixJumping.common.JumpingFaultHistoryElement;

/**
 * Master controller for the Grand Prix Jumping game
 *
 * @author Richard Walter
 * @version Beta 0.3
 */
public class JumpingMasterController extends JogreController {

	// Links to game data and components & controllers
	private JumpingClientModel model;
	private JumpingSortAreaComponent sortComponent;
	private JumpingPlayerHandComponent [] handComponents;
	private JumpingImmCardComponent [] immCardComponents;
	private JumpingTrackComponent trackComponent;
	private JumpingFaultIndicatorComponent faultComponent;
	private ChatGameComponent chatComponent;

	// The "done" button
	private JogreButton doneButton;

	// Labels used for setting the "done" button text and the chat messages
	// the game generates.
	GameLabels labels;

	// Name that is used as the source of chat messages from the game.
	private String chatGameName;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param trackController		The track controller
	 */
	public JumpingMasterController(	JumpingClientModel model,
									JogreButton doneButton,
									JumpingSortAreaComponent sortComponent,
									ChatGameComponent chatComponent,
									JumpingPlayerHandComponent [] handComponents,
									JumpingImmCardComponent [] immCardComponents,
									JumpingTrackComponent trackComponent,
									JumpingFaultIndicatorComponent faultComponent) {
		super(model, null);

		this.model = model;
		this.doneButton = doneButton;
		this.sortComponent = sortComponent;
		this.handComponents = handComponents;
		this.immCardComponents = immCardComponents;
		this.trackComponent = trackComponent;
		this.faultComponent = faultComponent;
		this.chatComponent = chatComponent;

		// Setup for using test labels
		labels = GameLabels.getInstance();
		chatGameName = labels.get("chat.gameName");
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		// Reset the model
		model.resetGame();

		// If this table doesn't allow edits, then click the "done" button to
		// skip the editing phase.
		if (!model.allowEdits()) {
			doneButtonClicked();
			dumpTrackCode();
		}
	}

	
	/**
	 * Handle a table message from the server.
	 *
	 * @param	message		The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();
		if (messageType.equals(CommJumpingMoveCards.XML_NAME)) {
			handleMoveCards(new CommJumpingMoveCards(message));
		} else if (messageType.equals(CommJumpingChooseSort.XML_NAME)) {
			handleChooseSort(new CommJumpingChooseSort(message));
		} else if (messageType.equals(CommJumpingDone.XML_NAME)) {
			handleDone(new CommJumpingDone(message));
		} else if (messageType.equals(CommJumpingActivate.XML_NAME)) {
			handleActivate(new CommJumpingActivate(message));
		} else if (messageType.equals(CommJumpingModifyFence.XML_NAME)) {
			handleModifyFence(new CommJumpingModifyFence(message));
		} else if (messageType.equals(CommJumpingSetShadowHorse.XML_NAME)) {
			handleSetShadowHorse(new CommJumpingSetShadowHorse(message));
		} else if (messageType.equals(Comm.GAME_OVER)) {
			// If we get an unexpected game over message, then force the model
			// to game over...
			if (!model.isGameOver()) {
				model.setGameOver();
			}
		}

		updatePlayingFlags();
	}

	/**
	 * Update the button text and the playable flags for the current player
	 */
	public void updatePlayingFlags() {
		setDoneButtonText();
		model.setPlayableFlags(model.getCurrentPlayer());
	}

	/**
	 * Handle a message that moves some cards around.
	 */
	private void handleMoveCards(CommJumpingMoveCards theMoveMsg) {
		int moveCode = theMoveMsg.getMoveCode();
		int playerId = getSeatNum(theMoveMsg.getUsername());

		if (moveCode == CommJumpingMoveCards.DECK_TO_SORT) {
			// New cards are being put into the sorting area
			model.clearImmediateHand(0);
			model.clearImmediateHand(1);
			model.setNewSortCards(theMoveMsg.getCards());
		} else if (moveCode == CommJumpingMoveCards.SORT_LEFT_TO_RIGHT) {
			model.moveSortCard(model.LEFT_TO_RIGHT, (JumpingCard) theMoveMsg.getCards().firstElement());
			sortComponent.repaint();
		} else if (moveCode == CommJumpingMoveCards.SORT_RIGHT_TO_LEFT) {
			model.moveSortCard(model.RIGHT_TO_LEFT, (JumpingCard) theMoveMsg.getCards().firstElement());
			sortComponent.repaint();
		} else if (moveCode == CommJumpingMoveCards.HAND_TO_TRACK) {
			playCards(theMoveMsg.getCards());
			doCommitHorseMovement(playerId);
		} else if (moveCode == CommJumpingMoveCards.DECK_TO_HAND) {
			JumpingCard theCard = (JumpingCard) theMoveMsg.getCards().firstElement();
			String playerName = theMoveMsg.getUsername();

			model.addCardToHand(playerId, theCard, false);

			putChatMsg("chat.receivedCardForRibbon", playerName, theCard);
		} else if (moveCode == CommJumpingMoveCards.DECK_TO_DUAL_RIDER) {
			model.setDualRiderCards(theMoveMsg.getCards());
		} else if (moveCode == CommJumpingMoveCards.DUAL_RIDER_TO_HAND) {
			// Move the cards into the hands
			JumpingCard hisCard = (JumpingCard) theMoveMsg.getCards().get(0);
			JumpingCard myCard = (JumpingCard) theMoveMsg.getCards().get(1);

			doDualRiderSelect(playerId, hisCard, myCard, false);

			putChatMsg("chat.tookCardForDualRider", getPlayer(playerId), hisCard);
			putChatMsg("chat.givenCardForDualRider", getPlayer(1-playerId), myCard);
		} else if (moveCode == CommJumpingMoveCards.HAND_TO_DISCARD) {
			model.removeCardsFromHand(theMoveMsg.getCards(), playerId);
		}

		if (model.setNewDeckSize(theMoveMsg.getDeckSize())) {
			putChatMsg("chat.reshuffleOccurred");
		}
	}

	/**
	 * Handle the ChooseSort message from other client
	 *
	 * @param theChooseMsg	The message indicating the choice.
	 */
	private void handleChooseSort(CommJumpingChooseSort theChooseMsg) {
		boolean choice = theChooseMsg.getChoice();
		int playerId = getSeatNum(theChooseMsg.getUsername());
		if (getSeatNum() == -1) {
			// I'm a spectator, so both of my card areas disappear
			doChooseSort(playerId, choice, true, true);
		} else {
			// I'm a player, so don't turn over the cards that are given to me
			doChooseSort(playerId, choice, true, false);
		}

	}

	/**
	 * Handle the Done message from other client
	 *
	 * @param theDoneMsg	The message indicating the choice.
	 */
	private void handleDone(CommJumpingDone theDoneMsg) {

		if (model.isOfficialDiscardActive()) {
			model.setOfficialDiscards(-1);
			return;
		}

		// Clear the new flags on cards played.
		model.clearNewFlags(model.getCurrentPlayer());

		if (model.isSortingCards()) {
			model.changePhase(model.CHOOSING_SORTED);
		} else if (model.isChooserPlayingCards()) {
			if (model.numExcessCards(model.getChooserSeatNum()) > 0) {
				model.changePhase(model.CHOOSER_DISCARDING);
			} else {
				model.changePhase(model.PLAYING_CARDS_SORTER);
				trackComponent.archiveOrnaments();
				model.clearCurrentJumps(model.getSorterSeatNum());
			}
		} else if (model.isSorterPlayingCards()) {
			if (model.numExcessCards(model.getSorterSeatNum()) > 0) {
				model.changePhase(model.SORTER_DISCARDING);
			} else {
				checkForEndOfTurn(model.chooserHasImmediateCards(), model.PLAY_IMM_CARDS_CHOOSER);
				trackComponent.archiveOrnaments();
				model.clearCurrentJumps(model.getChooserSeatNum());
			}
		} else if (model.isChooserPlayingImmediate()) {
			checkForEndOfTurn(model.sorterHasImmediateCards(), model.PLAY_IMM_CARDS_SORTER);
		} else if (model.isSorterPlayingImmediate()) {
			checkForEndOfTurn(model.chooserHasImmediateCards(), model.PLAY_IMM_CARDS_CHOOSER);
			trackComponent.archiveOrnaments();
			model.clearCurrentJumps(model.getChooserSeatNum());
		} else if (model.isChooserDiscarding()) {
			model.changePhase(model.PLAYING_CARDS_SORTER);
			trackComponent.archiveOrnaments();
			model.clearCurrentJumps(model.getSorterSeatNum());
		} else if (model.isSorterDiscarding()) {
			checkForEndOfTurn(model.chooserHasImmediateCards(), model.PLAY_IMM_CARDS_CHOOSER);
			trackComponent.archiveOrnaments();
			model.clearCurrentJumps(model.getChooserSeatNum());
		} else if (model.isCreatingTrack()) {
			model.setPlayerEditing(1-getSeatNum(), false);
			checkForCreatingDone();
		}
	}

	/**
	 * Handle the Activate message from other client
	 *
	 * @param theActMsg		The message indicating the activation.
	 */
	private void handleActivate(CommJumpingActivate theActMsg) {
		String playerName = theActMsg.getUsername();
		int playerId = getSeatNum(playerName);
		JumpingCard msgCard = theActMsg.getCard();
		int half = theActMsg.getHalf();

		if (msgCard.isRibbon()) {
			doActivateHandCard(playerId, model.getActualHandCard(playerId, msgCard), half, false);
			putChatMsg((half == 0) ? "chat.playedRibbonForPoints" : "chat.playedRibbonForCard", playerName);
		} else {
			doActivateImmCard(playerId, model.getActualImmCard(playerId, msgCard), half, false);
			if (msgCard.isOfficial()) {
				putChatMsg((half == 0) ? "chat.playedOfficialForPoints" : "chat.playedOfficialForDiscard", playerName);
			}
		}
	}

	/**
	 * Handle the Modify Fence message
	 *
	 * Note: This message could originate either from the other player *or* us.
	 * See handleModifyFence() in JumpingServerController.java for details on why.
	 *
	 * @param theModifyMsg		The message indicating the modification.
	 */
	private void handleModifyFence(CommJumpingModifyFence theModifyMsg) {
		String playerName = theModifyMsg.getUsername();
		int playerId = getSeatNum(playerName);

		// Do the modification
		if (model.modifyFence(theModifyMsg.getLocation(), theModifyMsg.getDirection())) {

			// Since one player changed the state of the board, put the other back in editing
			// mode again.
			model.setPlayerEditing(1-playerId, true);
		}
	}

	/**
	 * Handle the Set Shadow horse message
	 *
	 * @param theSetMsg		The message setting the shadow horse location.
	 */
	private void handleSetShadowHorse(CommJumpingSetShadowHorse theSetMsg) {
		model.setShadowHorseLoc(theSetMsg.getSpace());
	}

	/**
	 * Play a bunch of cards into the current jump
	 */
	private boolean playCards (Vector cards) {
		ListIterator iter = cards.listIterator();
		while (iter.hasNext()) {
			JumpingCard card = (JumpingCard) iter.next();
			if (!model.playCard(card)) {
				System.out.println("Error: Model says can't play card " + card + " !");
				return false;
			}
		}
		return true;
	}

	/**
	 * Put a message into the chat window.
	 *
	 * @param	formatLabelKeyname		The key name in the game_labels.properties file to use
	 *									to get the format string.
	 * @param	playerName				The name to substitute in the format string.
	 */
	private void putChatMsg(String formatLabelKeyName, String playerName) {
		Object [] args = {playerName};
		chatComponent.receiveMessage(chatGameName, labels.get(formatLabelKeyName, args));
	}

	/**
	 * Put a card message into the chat window.
	 *
	 * @param	formatLabelKeyname		The key name in the game_labels.properties
	 *                                  file to use to get the format string.
	 * @param	playerName				The name to substitute in the format string.
	 * @param	theCard					The card to substitute in the format string.
	 */
	private void putChatMsg(String formatLabelKeyName, String playerName, JumpingCard theCard) {
		Object [] args = {playerName, labels.get(theCard.getPropertiesKeyname())};
		chatComponent.receiveMessage(chatGameName, labels.get(formatLabelKeyName, args));
	}

	/**
	 * Put a plain message in the chat window.
	 *
	 * @param	labelKeyname			The key name in the game_labels.properties file to use
	 */
	private void putChatMsg(String labelKeyName) {
		chatComponent.receiveMessage(chatGameName, labels.get(labelKeyName));
	}

	/**
	 * Put a message into the chat window.
	 *
	 * @param	formatLabelKeyname		The key name in the game_labels.properties
	 *                                  file to use to get the format string.
	 * @param	args					The substitute arguements.
	 */
	private void putChatMsg(String formatLabelKeyName, Object [] args) {
		chatComponent.receiveMessage(chatGameName, labels.get(formatLabelKeyName, args));
	}

	/**
	 * This will print the code for the current layout to the chat window.
	 */
	private void dumpTrackCode() {
		putChatMsg("chat.trackCode", model.createCodeForTrack());
	}

/************************************************************************************/
/* Methods to actually do stuff in the client model.  These are common between
   local clicks doing things and messages from the other client doing things...
*/

	/**
	 * A player is choosing one of the sort card areas.
	 *
	 * @param	seatNum		The player that is choosing
	 * @param	chooseLeft	True => Player chose left
	 *						False => Player chose right
	 * @param	turnChosen	True => Turn the chosen cards in the sort area invisible
	 *						False => Leave the chosen cards in the sort area visible
	 * @param	turnNotChosen	True => Turn the not-chosen cards in the sort area invisible
	 *							False => Leave the not-chosen cards in the sort area visible
	 */
	public void doChooseSort(int seatNum, boolean chooseLeft, boolean turnChosen, boolean turnNotChosen) {

		boolean chooserUnknown = (seatNum != getSeatNum()) && (!model.isOpenHands());
		boolean nonChooserUnknown = ((1-seatNum) != getSeatNum()) && (!model.isOpenHands());

		int side = (chooseLeft ? 0 : 1);
		model.moveSortCardsToHand(side, seatNum, turnChosen, chooserUnknown);
		model.moveSortCardsToHand(1-side, (1-seatNum), turnNotChosen, nonChooserUnknown);

		// Get ready for the chooser to play cards
		int chooser = model.getChooserSeatNum();
		model.clearCurrentJumps(chooser);

		// Move to the playing card phase
		model.changePhase(JumpingClientModel.PLAYING_CARDS_CHOOSER);
	}

	/**
	 * A player is playing a card
	 *
	 * @param	seatNum		Which player is playing the card
	 * @param	theCard		The card he's playing
	 */
	public void doPlayCard(int seatNum, JumpingCard theCard) {
		if (theCard.isMarked()) {
			// The card was already played, so the player is "un-playing" it.
			model.unplayCard(theCard);
			theCard.setMarked(false);
		} else {
			// Play it
			theCard.setMarked(model.playCard(theCard));
		}

		int newHorseLoc = model.getCurrJumpHorseLoc();
		if (model.setShadowHorseLoc(newHorseLoc)) {
			// The shadow horse location has changed, so send a message
			// to the server updating it there.
			conn.send(new CommJumpingSetShadowHorse(conn.getUsername(), newHorseLoc));
		}
	}

	/**
	 * A player is committing the current set of cards to horse movement.
	 *
	 * @param	seatNum		Which player is playing the cards
	 */
	public void doCommitHorseMovement(int seatNum) {
		model.commitHorseMovement(seatNum);
	}

	/**
	 * A player is activating a card in his hand
	 * Note: For hand cards, the only card that can be activated is the ribbon
	 *       card, so this assumes it is the ribbon card.
	 *
	 * @param	seatNum		Which player is activating the card
	 * @param	theCard		The card being activated
	 * @param	half		Which half of the card is being activated
	 * @param	tellServer	If true, then this will tell the server what we're doing.
	 */
	public void doActivateHandCard(int seatNum, JumpingCard theCard, int half, boolean tellServer) {
		if (tellServer) {
			conn.send(new CommJumpingActivate(conn.getUsername(), theCard, half));
		}

		if (half == 0) {
			// player is choosing to reduce fault points by 1/4.
			model.addToFaults(seatNum, -1, JumpingFaultHistoryElement.RIBBON_CARD);
			model.removeCardFromHand(seatNum, theCard);
		} else if (half == 1) {
			// player is choosing to pick a new card to replace this one
			model.removeCardFromHand(seatNum, theCard);
			// Note: The new card will be given by the server in another message
		}

		// In all cases, the card is no longer activated in the model
		theCard.setMarked(false);
		model.setHandCardActivated(false);
	}

	/**
	 * A player is activating a card in his immediate area
	 *
	 * @param	seatNum		Which player is activating the card
	 * @param	theCard		The card being activated
	 * @param	half		Which half of the card is being activated
	 * @param	tellServer	If true, then this will tell the server what we're doing.
	 */
	public void doActivateImmCard(int seatNum, JumpingCard theCard, int half, boolean tellServer) {
		if (tellServer) {
			conn.send(new CommJumpingActivate(conn.getUsername(), theCard, half));
		}

		if (theCard.isOfficial()) {
			if (half == 0) {
				// player is choosing to increase fault points by a full point.
				model.addToFaults(seatNum, 4, JumpingFaultHistoryElement.OFFICIAL_CARD);
			} else if (half == 1) {
				// player is choosing to discard half his hand
				model.removeUncommittedJumps();
				model.setOfficialDiscards(seatNum);
			} else if (half == -1) {
				// player is de-activating card without playing it
			}
		} else if (theCard.isDualRider()) {
			// Nothing to do here.  The server will send 2 cards back as a
			// response to the activation, so at that time things happen.
		}

		// In all cases, the card is no longer activated in the model
		model.setImmCardActivated(false);

		// If the card was actually played, then leave it selected.
		theCard.setMarked(half >= 0);
	}

	/**
	 * A player is selecting one of the Dual Rider cards.
	 *
	 * @param	seatNum		The seat number of the choosing player
	 * @param	myCard		The card chosen by the <seatNum> player
	 * @param	hisCard		The card given to the other player
	 * @param	tellServer	If true, then this will tell the server what we're doing
	 */
	public void doDualRiderSelect(int seatNum, JumpingCard myCard, JumpingCard hisCard, boolean tellServer) {
		if (tellServer) {
			Vector c = new Vector();
			c.add(myCard);
			c.add(hisCard);
			conn.send(new CommJumpingMoveCards(conn.getUsername(), c, CommJumpingMoveCards.DUAL_RIDER_TO_HAND));
		}

		boolean selectorUnknown = (seatNum != getSeatNum()) && (!model.isOpenHands());
		boolean nonSelectorUnknown = ((1-seatNum) != getSeatNum()) && (!model.isOpenHands());

		model.addCardToHand(seatNum, myCard, selectorUnknown);
		model.addCardToHand(1-seatNum, hisCard, nonSelectorUnknown);

		model.clearDualRiderCards();
	}

/************************************************************************************/

	/**
	 * This routine is called when the done button is clicked.
	 */
	public void doneButtonClicked () {

		boolean doNextPlayer = true;		// Assume other player will play
		int seatNum = getSeatNum();

		if (model.isOfficialDiscardActive()) {
			discardMarkedCards(seatNum);
			model.setOfficialDiscards(-1);
			doNextPlayer = false;
		} else {

			model.clearNewFlags(seatNum);
			model.clearCurrentJumps(1-seatNum);
			trackComponent.clearArchivedOrnaments();

			if (model.isSortingCards()) {
				sortComponent.setSelectedArea(JumpingSortAreaComponent.NO_AREA);
				model.changePhase(model.CHOOSING_SORTED);
			} else if (model.isChooserPlayingCards()) {
				if (model.numExcessCards(model.getChooserSeatNum()) > 0) {
					model.changePhase(model.CHOOSER_DISCARDING);
					doNextPlayer = false;
				} else {
					model.changePhase(model.PLAYING_CARDS_SORTER);
				}
			} else if (model.isSorterPlayingCards()) {
				if (model.numExcessCards(model.getSorterSeatNum()) > 0) {
					model.changePhase(model.SORTER_DISCARDING);
					doNextPlayer = false;
				} else {
					checkForEndOfTurn(model.chooserHasImmediateCards(), model.PLAY_IMM_CARDS_CHOOSER);
				}
			} else if (model.isChooserPlayingImmediate()) {
				checkForEndOfTurn(model.sorterHasImmediateCards(), model.PLAY_IMM_CARDS_SORTER);
				// If we're now sorting cards, then we don't need to advance to the next player
				// since the chooser will become the sorter for the next round.
				doNextPlayer = !model.isSortingCards();
			} else if (model.isSorterPlayingImmediate()) {
				checkForEndOfTurn(model.chooserHasImmediateCards(), model.PLAY_IMM_CARDS_CHOOSER);
			} else if (model.isChooserDiscarding()) {
				discardMarkedCards(model.getChooserSeatNum());
				model.changePhase(model.PLAYING_CARDS_SORTER);
			} else if (model.isSorterDiscarding()) {
				discardMarkedCards(model.getSorterSeatNum());
				checkForEndOfTurn(model.chooserHasImmediateCards(), model.PLAY_IMM_CARDS_CHOOSER);
			} else if (model.isCreatingTrack()) {
				model.setPlayerEditing(seatNum, false);
				checkForCreatingDone();
				doNextPlayer = false;
			}
		}

		// If we're switching players, then do so.
		if (doNextPlayer) {
			nextPlayer();
		}

		// Finally, send the "done" message to the server
		conn.send(new CommJumpingDone(conn.getUsername()));

		// Update the various playing flags to match the new situation
		updatePlayingFlags();

		// Check for game over
		// If we are now sorting, then it's time to check for the end of the game.
		if (model.isSortingCards()) {
			if (model.checkGameOver()) {
				putChatMsg("chat.finalScore");
				putChatMsg("chat.finalScore.player", makeChatScoreArgs(0));
				putChatMsg("chat.finalScore.player", makeChatScoreArgs(1));
			}
		}
	}

	/**
	 * Check to see if both players have finished creating the board.  If so,
	 * then prepare to begin the real game playing.
	 */
	private void checkForCreatingDone() {
		if (!model.playerStillEditingTrack(0) && !model.playerStillEditingTrack(1)) {
			model.changePhase(model.SORTING_CARDS);
			trackComponent.setSelectedTrackSpace(-1);
			model.setCurrentSorter(0);
			dumpTrackCode();
		}
	}

	/**
	 * Create the Object array to use for subsitution in the final score chat
	 * string for the given player.
	 *
	 */
	private Object [] makeChatScoreArgs(int playerId) {
		Object [] args = new Object[3];
		int quarterFaults = model.getQuarterFaults(playerId);

		args[0] = getPlayer(playerId);
		args[1] = Integer.toString(quarterFaults / 4);
		args[2] = labels.get("chat.scoreQuarter." + (quarterFaults % 4));

		return args;
	}

	/**
	 * This routine will discard the marked cards in the given player's hand
	 */
	private void discardMarkedCards(int seatNum) {
		Vector cards = model.getAndRemoveMarkedCards(model.getPlayableHand(seatNum));

		// Tell the server (and other players) that the cards have been discarded
		conn.send(new CommJumpingMoveCards(	conn.getUsername(),
											cards,
											CommJumpingMoveCards.HAND_TO_DISCARD));
	}

	/**
	 * Check to see if the end of turn is ok, and handle it.
	 *
	 * @param hasImmCards		Indicates if the "other" player has immediate cards.
	 * @param immCardNextState	If <hasImmCards> is true, then this is the state
	 *							that the model will be changed to rather than
	 *							SORTING_CARDS state.
	 */
	private void checkForEndOfTurn(boolean hasImmCards, int immCardNextState) {
		if (hasImmCards) {
			model.changePhase(immCardNextState);
		} else {
			model.swapChooserAndSorter();
			model.changePhase(model.SORTING_CARDS);
		}
	}

	/**
	 * Set the text & state of the "done" button.
	 */
	public void setDoneButtonText() {
		int seatNum = getSeatNum();
		boolean enabled = false;
		String keyText = "done.button";
		Object [] textArgs = {""};

		// From the state of the model, determine the correct text & state of the button
		if (model.isSortingCards()) {
			enabled = ((model.getSorterSeatNum() == seatNum) && model.isValidSorting());
		} else if ((model.isChooserDiscarding() && (model.getChooserSeatNum() == seatNum)) ||
		           (model.isSorterDiscarding()  && (model.getSorterSeatNum()  == seatNum)) ||
				    model.isOfficialDiscardActive()) {
			int cardsToDiscard = model.getOfficialDiscardNumber();
			if (cardsToDiscard <= 0) {
				cardsToDiscard = model.numExcessCards(seatNum);
			}
			int markedCards = model.numMarkedCards(seatNum);

			enabled = (markedCards == cardsToDiscard);
			textArgs[0] = new Integer(cardsToDiscard);
			keyText = "discard.button";
		} else if ((model.isChooserPlaying() && (model.getChooserSeatNum() == seatNum)) ||
		           (model.isSorterPlaying()  && (model.getSorterSeatNum()  == seatNum))) {

			if (model.isDualRiderActive()) {
				keyText = "dual.rider.select";
			} else if (model.isCardActivated()) {
				keyText = "activated.button";
			} else if (model.getCurrJumps().size() != 0) {
				// There are cards played in the board
				keyText = "cards.on.board";
			} else {
				enabled = !model.hasUnmarkedImmediateCards(seatNum);

				// Note: The "isPlayingImmediate()" is there because excess cards
				// don't have to be discarded during the immediate playing phase.
				int cardsToDiscard = model.numExcessCards(seatNum);
				if ((cardsToDiscard > 0) && (!model.isPlayingImmediate())) {
					textArgs[0] = new Integer(cardsToDiscard);
					keyText = "discard.button.select";
				}
			}
		} else if (model.isCreatingTrack()) {
			keyText = "done.setup";
			enabled = model.playerStillEditingTrack(seatNum);
		}

		// Set the button correctly.
		doneButton.setEnabled(enabled);
		doneButton.setText(labels.get(keyText, textArgs));
	}
}
