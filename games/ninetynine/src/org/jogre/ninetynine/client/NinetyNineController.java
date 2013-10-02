/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
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
package org.jogre.ninetynine.client;

import info.clearthought.layout.TableLayout;
import nanoxml.XMLElement;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.Integer;

import java.text.MessageFormat;

import org.jogre.client.JogreController;
import org.jogre.common.IGameOver;
import org.jogre.common.comm.CommGameOver;

import org.jogre.common.util.GameLabels;

import org.jogre.client.awt.JogreButton;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.ChatGameComponent;

import org.jogre.ninetynine.common.CommNinetyNineMakeBid;
import org.jogre.ninetynine.common.CommNinetyNineRequestHand;
import org.jogre.ninetynine.common.CommNinetyNineSendHand;
import org.jogre.ninetynine.common.CommNinetyNineAskUpgrade;
import org.jogre.ninetynine.common.CommNinetyNineStartRound;
import org.jogre.ninetynine.common.CommNinetyNinePlayCard;
import org.jogre.ninetynine.common.CommNinetyNineRoundScore;
import org.jogre.ninetynine.common.CommNinetyNineTrumpSuit;
import org.jogre.ninetynine.common.NinetyNineCoreModel;

import org.jogre.ninetynine.std.Card;
import org.jogre.ninetynine.std.Hand;
import org.jogre.ninetynine.std.DrawableHand;
import org.jogre.ninetynine.std.CardHandComponent;
import org.jogre.ninetynine.std.CardHandController;
import org.jogre.ninetynine.std.ICardHandControllerAlertee;

// Controller for Ninety Nine game
public class NinetyNineController extends JogreController
	implements ICardHandControllerAlertee, IUpgradeDialogAlertee,
	           IGameStateAlertee {

	// links to client model and the board components
	private NinetyNineClientModel model;
	private CardHandComponent myHandComponent, myBidComponent;
	private CardHandComponent [] playComponents;

	// Unknown hands.  These are constants used displaying opponents unknown hands & bids.
	private Hand unknownHand;			// a hand of 12 unknown cards
	private Hand unknownBid;			// a hand of 3 unknown cards
	private Card unknownCard;			// an unknown card

	private Hand playHand, bidHand;

	// Buttons used for bidding
	private JogreButton bidButton;
	private JogreButton declareButton;
	private JogreButton revealButton;
	private JogreButton p0LeadButton;
	private JogreButton p1LeadButton;
	private JogreButton p2LeadButton;

	// Panel for holding the bid buttons & played card Hand
	private JogrePanel bidButtonPanel;
	private TableLayout	bidButtonLayout;

	// The format to use for the bid button.
	private MessageFormat bidButtonFormat;

	// The columns that are multplexed for the buttons
	private static final int bidButtonColumn = 0;
	private static final int leadButtonColumn = 1;
	private static final int playedCardColumn = 2;

	// The table frame.  This is used when creating the upgrade dialog
	private Frame myTableFrame;

	// The chat component.  This is used to inject game messages in the chat window.
	private ChatGameComponent theChatBox;

	// These are strings that are read from the properties file for use in the chat window.
	private String chatGameName;
	private MessageFormat chatUpgradeNoticeFormat1;
	private MessageFormat chatUpgradeNoticeFormat2;
	private MessageFormat chatRevealBidNoticeFormat;

	/**
	 * Constructor which creates the controller
	 *
	 * @param model					The game model
	 * @param handComponent			The component of my card hand
	 * @param bidComponent			The component of my bid
	 */
	public NinetyNineController(NinetyNineClientModel model,
								CardHandComponent myHandComponent,
								CardHandComponent bidComponent,
								CardHandComponent [] playComponents,
								JogreTableFrame myTableFrame) {
		super(model, myHandComponent);

		// Tell the model to alert us when game state is sent by the server
		model.setGameStateAlertee(this);

		// Save parameters (by definition, I am local player 0)
		this.model = model;
		this.myHandComponent = myHandComponent;
		this.myBidComponent = bidComponent;
		this.playComponents = playComponents;
		this.myTableFrame = myTableFrame;
		this.theChatBox = myTableFrame.getMessageComponent();

		this.playHand = myHandComponent.getDrawableHand().getHand();
		this.bidHand  = bidComponent.getDrawableHand().getHand();

		// Do local initialization
		unknownCard = new Card(Card.UNKNOWN, 0);

		unknownHand = new Hand();
		for (int i=0; i<12; i++) {
			unknownHand.addCard(unknownCard);
		}

		unknownBid = new Hand();
		for (int i=0; i<3; i++) {
			unknownBid.addCard(unknownCard);
		}

		// Read text strings for use with the chat
		GameLabels labels = GameLabels.getInstance();
		chatGameName = labels.get("chat.gameName");
		chatUpgradeNoticeFormat1 = new MessageFormat (labels.get("chat.upgrade.notice.format.1"));
		chatUpgradeNoticeFormat2 = new MessageFormat (labels.get("chat.upgrade.notice.format.2"));
		chatRevealBidNoticeFormat = new MessageFormat (labels.get("chat.reveal.bid.notice.format"));

		// Read text format for the bid button
		bidButtonFormat = new MessageFormat (labels.get("bid.only"));
	}

	/**
	 * Start a new game
	 *
	 * @see org.jogre.common.JogreModel#start()
	 */
	public void start () {
		model.resetGame();

		// Request our hand from the server.  (If we're a spectator,
		// the server will send back the trump suit, not a real hand.)
		conn.send(new CommNinetyNineRequestHand(conn.getUsername()));
		setLeadButtonText(getSeatNum());
	}


	/**
	 * Handle a table message from the server.
	 *
	 * @param	message		The message from the server
	 */
	public void receiveTableMessage (XMLElement message) {
		String messageType = message.getName();

		if (messageType.equals(CommNinetyNineSendHand.XML_NAME)) {
			handleSendHand(new CommNinetyNineSendHand(message));
		} else if (messageType.equals(CommNinetyNineMakeBid.XML_NAME)) {
			handleMakeBid(new CommNinetyNineMakeBid(message));
		}  else if (messageType.equals(CommNinetyNineStartRound.XML_NAME)) {
			handleStartRound(new CommNinetyNineStartRound(message));
		} else if (messageType.equals(CommNinetyNineAskUpgrade.XML_NAME)) {
			handleAskUpgrade(new CommNinetyNineAskUpgrade(message));
		} else if (messageType.equals(CommNinetyNinePlayCard.XML_NAME)) {
			handlePlayCard(new CommNinetyNinePlayCard(message));
		} else if (messageType.equals(CommNinetyNineRoundScore.XML_NAME)) {
			handleRoundScore(new CommNinetyNineRoundScore(message));
		} else if (messageType.equals(CommNinetyNineTrumpSuit.XML_NAME)) {
			handleTrumpSuit(new CommNinetyNineTrumpSuit(message));
		}
	}

	/**
	 * Handle a message that has sent our hand of cards to us.
	 */
	private void handleSendHand(CommNinetyNineSendHand theHandMsg) {
		int playerId = globalToLocalSeatNum(getSeatNum(theHandMsg.getUsername()));

		if (playerId == 0) {
			// This hand is for us (we are always local player 0)
			model.giveHand(0, theHandMsg.getHand());

			// Give our opponents unknown hands
			model.giveHand(1, unknownHand);
			model.giveHand(2, unknownHand);

			// Set the trump suit
			model.setTrumpSuit(theHandMsg.getTrumpSuit());

			// We are now setting the bid
			model.goToBidPhase();

			// Enable hand components so that bidding can happen
			myHandComponent.setEnable(true);

			// Disable the bid buttons, and show them.
			setBidButtonsState(false);
			setBidButtonText();
			showBidButtons(bidButtonColumn);

			// Set ourselves as the current player
			setLocalCurrentPlayer(0);
		} else {
			// This hand is for someone else, so he must be bidding Reveal and
			// so we are being given his hand.
			model.giveHand(playerId, theHandMsg.getHand());
		}
	}

	/**
	 * Handle a message that has only the trump suit in it.
	 * (This only happens when we are an observer and we've asked for a hand.)
	 */
	private void handleTrumpSuit(CommNinetyNineTrumpSuit trumpMsg) {
		// We're a spectator, so just set all 3 players up with unknown hands
		model.giveHand(0, unknownHand);
		model.giveHand(1, unknownHand);
		model.giveHand(2, unknownHand);

		model.setTrumpSuit(trumpMsg.getTrumpSuit());
		setLocalCurrentPlayer(0);
	}

	/**
	 * Handle a message that tells us a player's bid
	 */
	private void handleMakeBid(CommNinetyNineMakeBid bidMsg) {
		int playerId = globalToLocalSeatNum(getSeatNum(bidMsg.getUsername()));
		Hand theBidHand = bidMsg.getBidHand();

		if (theBidHand == null) {
			// The player made a bid, but we don't know what it is.
			model.giveBid(playerId, unknownBid, NinetyNineCoreModel.BID_UNKNOWN, -1);
			model.removeThreeHandCards(playerId);
		} else {
			// The player made a premium bid
			if ((playerId != 0) ||					// We're a player and someone else bid
				(getSeatNum() < 0)) {				// We're a spectator
				model.giveBid(playerId, theBidHand, bidMsg.getBidType(), bidMsg.getLeadPlayer());
			}

			if (bidMsg.getBidType() == NinetyNineCoreModel.BID_REVEAL) {
				// A reveal bid was made, so report it in the chat window
				Object [] args = {getPlayerName(playerId), getPlayerName(bidMsg.getLeadPlayer())};
				theChatBox.receiveMessage(chatGameName,
					chatRevealBidNoticeFormat.format(args));
			}

			startRound(globalToLocalSeatNum(bidMsg.getLeadPlayer()));
		}
	}

	/**
	 * Handle a message that tells us the round is over and it's time to
	 * get the scores.
	 */
	private void handleRoundScore(CommNinetyNineRoundScore scoreMsg) {

		// Report the scores to the model
		for (int i=0; i<3; i++) {
			int localSeatNum = globalToLocalSeatNum(i);
			model.setScore(localSeatNum, scoreMsg.getScore(i));
		}

		model.readyForNextRound();
		NinetyNineGraphics.getInstance().advanceToNextCardBack();

		if (!model.isGameOver()) {
			// Ask for our next hand
			conn.send(new CommNinetyNineRequestHand(conn.getUsername()));
		}
	}

	/**
	 * Handle a message that tells us to start the round.
	 */
	private void handleStartRound(CommNinetyNineStartRound startMsg) {
		startRound(globalToLocalSeatNum(startMsg.getFirstPlayerId()));
	}

	/**
	 * Handle a message that tells us that some player needs to decide to upgrade
	 * bid from declare to reveal.
	 */
	private void handleAskUpgrade(CommNinetyNineAskUpgrade upgradeMsg) {
		int declarer = upgradeMsg.getDeclarer();
		int revealer = upgradeMsg.getRevealer();

		if (declarer == getSeatNum()) {
			// We are the player being asked the question...
			NinetyNineUpgradeDialog upD = NinetyNineUpgradeDialog.newDialog (
					myTableFrame,
					getPlayerName(revealer),
					this,
					declarer);
		} else {
			// Some other player is being asked the question, so report it in the chat
			// window.
			Object [] args = {getPlayerName(declarer), getPlayerName(revealer)};
			theChatBox.receiveMessage(chatGameName,
					chatUpgradeNoticeFormat1.format(args) + "  " +
					chatUpgradeNoticeFormat2.format(args));
		}
	}

	/**
	 * Handle a message that tells us that some other player played a card
	 */
	private void handlePlayCard(CommNinetyNinePlayCard playMsg) {
		int playerId = globalToLocalSeatNum(getSeatNum(playMsg.getUsername()));
		Card thePlayedCard = playMsg.getCard();

		model.playCard(playerId, thePlayedCard);
		model.evaluateTrick();
		setLocalCurrentPlayer();
	}

	/**
	 * Start a round with the given first player.
	 *
	 * @param firstPlayerId		The seat number of the player who is to start.
	 */
	private void startRound(int firstPlayerId) {
		model.setCurrentPlayer(firstPlayerId);
		model.goToPlayPhase();
		setLocalCurrentPlayer();
		myHandComponent.setEnable(true);
	}

	/**
	 * Convert a global seat number into a local seat number.
	 */
	private int globalToLocalSeatNum(int globalSeatNum) {
		int mySeatNum = getSeatNum();
		if (mySeatNum > 0) {
			int localSeatNum = globalSeatNum - mySeatNum;
			if (localSeatNum < 0) {
				localSeatNum += 3;
			}
			return localSeatNum;
		} else {
			// No translation needed
			return globalSeatNum;
		}
	}

	/**
	 * Convert a local seat number into a global seat number.
	 */
	private int localToGlobalSeatNum(int localSeatNum) {
		int mySeatNum = getSeatNum();
		if (mySeatNum > 0) {
			int globalSeatNum = localSeatNum + mySeatNum;
			if (globalSeatNum > 2) {
				globalSeatNum -= 3;
			}
			return globalSeatNum;
		} else {
			// No translation needed
			return localSeatNum;
		}
	}

	/**
	 * Set our current playerID to the current player in the model.
	 * This also sets the prompt for the card to play.
	 */
	private void setLocalCurrentPlayer() {
		// Turn off prompts, if is one.
		playComponents[0].setPromptEnable(false);
		playComponents[1].setPromptEnable(false);
		playComponents[2].setPromptEnable(false);

		setLocalCurrentPlayer(model.getCurrentPlayerId());

		// If the game is playing, then enable prompt for the active player
		if (model.isPlaying()) {
			playComponents[model.getCurrentPlayerId()].setPromptEnable(true);
		}
	}

	/**
	 * Set our current playerID to the given localPlayerId
	 */
	private void setLocalCurrentPlayer(int localPlayerId) {
		int newPlayerId = localToGlobalSeatNum(localPlayerId);
		conn.getTable().getPlayerList().setCurrentPlayer(
			conn.getTable().getPlayerList().getPlayer(newPlayerId).getPlayerName());
	}

	/**
	 * Get the name for the given seat number
	 *
	 * @param	seatNum		The seat number to get the name for
	 */
	private String getPlayerName(int seatNum) {
		return conn.getTable().getPlayerList().getPlayer(seatNum).getPlayerName();
	}	


	//==========================================================================
	// Methods for "ICardHandControllerAlertee"
    //==========================================================================

	/**
	 * Signal that the user has clicked on a card.
	 *
	 * @param	theComponent		The CardHandComponent which the card came from
	 * @param	selectedCard		The card that was selected
	 */
	 public void signalCardClicked(CardHandComponent theComponent, Card selectedCard) {
	 	if (theComponent == myBidComponent) {
			// A bid card was selected, so move it back to the hand
			moveBidCardToHand(selectedCard);
		} else if (theComponent == myHandComponent) {
			// A card in the hand was selected
			if (model.isBidding()) {
				moveHandCardToBid(selectedCard);
			} else {
				// The player wants to play this card
				if (model.isValidPlay(0, selectedCard)) {
					model.playCard(0, selectedCard);
					model.evaluateTrick();
					setLocalCurrentPlayer();
					conn.send(new CommNinetyNinePlayCard(conn.getUsername(), selectedCard));
				}
			}
		}
	 }

	/**
	 * Test to see if the current selected card is a valid card to select.
	 * The Alertee should return true to tell the hand controller that it is
	 * ok to indicate this card is valid.  Or return false to indicate that
	 * the card is not ok to play right now and the card should not be indicated
	 * to be valid.
	 *
	 * @param	theComponent		The CardHandComponent which the active card
	 *								to be tested is from.
	 */
	public boolean isValidCardSelection(CardHandComponent theComponent) {
		Card potentialCard = theComponent.getDrawableHand().getSelectedCard();

		if (model.isBidding()) {
			// During bidding, any card is valid
			return true;
		}

		if (model.isPlaying() && isThisPlayersTurn()) {
			return model.isValidPlay(0, potentialCard);
		}

		// Any contingency not covered above results in not being able to play the card
		return false;
	}

	//==========================================================================

	/**
	 * Move a card from the playing hand to the bid hand.
	 *
	 * @param	selectedCard		The card to move from the playing hand to the bid hand
	 */
	private void moveHandCardToBid(Card selectedCard) {
		// Set the card invisible in our hand
		playHand.invisiblizeCard(selectedCard);

		// Add it to the bid hand
		int newLength = bidHand.appendCard(selectedCard);
		myBidComponent.setEnable(true);

		// Set the value of the bid in the bid button
		setBidButtonText();

		// If our bid is full, disable the hand component and enable the bid buttons
		if (newLength == 3) {
			myHandComponent.setEnable(false);
			setBidButtonsState(true);
		}
	}

	/**
	 * Move a card from the bid hand back to the playing hand.
	 *
	 * @param	selectedCard		The card to move from the bid hand to the playing hand
	 */
	private void moveBidCardToHand(Card selectedCard) {
		// Set the card visible in our hand
		playHand.visiblizeCard(selectedCard);
		myHandComponent.setEnable(true);
		setBidButtonsState(false);

		// Remove it from the bid hand
		int newLength = bidHand.removeCard(selectedCard);
		if (newLength == 0) {
			myBidComponent.setEnable(false);
		}

		// Set the value of the bid in the bid button
		setBidButtonText();
	}

	/**
	 * Calculate the current bid value of the bidHand
	 */
	private int calculateBidValue() {
		int value = 0;
		int numCards = bidHand.length();

		while (numCards > 0) {
			numCards -= 1;
			value += model.bidValueOfCard(bidHand.getNthCard(numCards));
		}

		return value;
	}

	/**
	 * Set the text of the bid button to read "Bid <value>" where value
	 * is the value of the current bidHand.
	 */
	private void setBidButtonText() {
		Object [] args = {new Integer(calculateBidValue())};
		bidButton.setText(bidButtonFormat.format(args));
	}

	//==========================================================================
	// Methods to support the IUpgradeDialogAlertee interface
    //==========================================================================

	/**
	 * Signal that the user clicked on the don't upgrade button
	 *
	 */
	public void noUpgradeButtonClicked () {
		// Send a message to the server changing our bid to a normal one
		conn.send(new CommNinetyNineMakeBid(conn.getUsername(),
											bidHand,
											NinetyNineCoreModel.BID_NORMAL));
	}


	/**
	 * Signal that the user clicked on one of the upgrade buttons
	 *
	 * @param	leadPlayer		PlayerID who should be the lead player
	 */
	public void upgradeButtonClicked (int leadPlayer) {
		// Send a message to the server changing our bid to an Reveal one
		conn.send(new CommNinetyNineMakeBid(conn.getUsername(),
											bidHand,
											NinetyNineCoreModel.BID_REVEAL,
											localToGlobalSeatNum(leadPlayer)));
	}

	//==========================================================================
	// Methods to support the bid & lead buttons
    //==========================================================================

	/**
	 * Create the panel with the bid buttons in it.
	 *
	 * @param	playedCardComponent		The hand component that will share the
	 *									space on the screen with the bid buttons.
	 */
	public JogrePanel makeBidButtonPanel (CardHandComponent playedCardComponent) {

		// Get a GameLabels object to use for the text of the components
		GameLabels labels = GameLabels.getInstance();

		// Create the layout
		double pref = TableLayout.PREFERRED;
		double[][] table_params = {
			// Initial panel shows only the bid buttons
			{pref, 0, 0},
			{pref, 3, pref, 3, pref} };

		bidButtonLayout = new TableLayout (table_params);
		bidButtonPanel = new JogrePanel (bidButtonLayout);

		// Create the buttons
		bidButton = new JogreButton (".");
		declareButton = new JogreButton (labels.get("bid.declare"));
		revealButton = new JogreButton (labels.get("bid.reveal"));
		setBidButtonText();

		p0LeadButton = new JogreButton ("1");
		p1LeadButton = new JogreButton ("2");
		p2LeadButton = new JogreButton ("3");
		setLeadButtonText(0);

		// Add buttons to the panel
		bidButtonPanel.add(bidButton, bidButtonColumn + ",0,c,c");
		bidButtonPanel.add(declareButton, bidButtonColumn + ",2,c,c");
		bidButtonPanel.add(revealButton, bidButtonColumn + ",4,c,c");
		bidButtonPanel.add(p0LeadButton, leadButtonColumn + ",0,c,c");
		bidButtonPanel.add(p1LeadButton, leadButtonColumn + ",2,c,c");
		bidButtonPanel.add(p2LeadButton, leadButtonColumn + ",4,c,c");

		// Add the provided playedCardComponent to the panel
		bidButtonPanel.add(playedCardComponent, playedCardColumn + ",0," + playedCardColumn + ",4,c,c");

		// Bid buttons start out disabled
		setBidButtonsState(false);

		// Lead buttons are always enabled
		p0LeadButton.setEnabled(true);
		p1LeadButton.setEnabled(true);
		p2LeadButton.setEnabled(true);

		// Create action listeners for the bid buttons
		createBidButtonsActionListeners();

		return bidButtonPanel;
	}

	/**
	 * Set the text of the lead buttons so that button 0 indicates the firstPlayer,
	 * button 1 indicates the player after that and button 2 indicates the last player.
	 * This is to keep the text of the buttons in sync with the local player viewpoint.
	 *
	 * @param	firstPlayer		The player to make the lead button 0 indicate.
	 */
	private void setLeadButtonText(int firstPlayer) {
		if (firstPlayer < 0) {
			firstPlayer = 0;
		}

		int p1 = (firstPlayer + 1) % 3;
		int p2 = (firstPlayer + 2) % 3;

		// Get a GameLabels object to use for the text of the components
		GameLabels labels = GameLabels.getInstance();

		String leadString = labels.get("lead");
		p0LeadButton.setText(labels.get("player.label." + firstPlayer) + " " + leadString);
		p1LeadButton.setText(labels.get("player.label." + p1) + " " + leadString);
		p2LeadButton.setText(labels.get("player.label." + p2) + " " + leadString);
	}

	/**
	 * Adds action listeners to each of the buttons
	 */
	private void createBidButtonsActionListeners() {
		bidButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				bidButtonClicked (NinetyNineCoreModel.BID_NORMAL);
			}
		});

		declareButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				bidButtonClicked (NinetyNineCoreModel.BID_DECLARE);
			}
		});

		revealButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				revealButtonClicked ();
			}
		});

		p0LeadButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				leadButtonClicked (0);
			}
		});

		p1LeadButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				leadButtonClicked (1);
			}
		});

		p2LeadButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				leadButtonClicked (2);
			}
		});

	}

	/**
	 * This routine is called when either the normal or declare bid buttons is clicked.
	 *
	 * @param	bidType		Indicates which bid button was clicked.
	 */
	private void bidButtonClicked (int bidType) {
		model.goToPostBidPhase();
		showBidButtons(playedCardColumn);
		setBidButtonsState(false);
		myBidComponent.setEnable(false);
		conn.send(new CommNinetyNineMakeBid(conn.getUsername(), bidHand, bidType));
	}

	/**
	 * This routine is called when the reveal bid buttons is clicked.
	 */
	private void revealButtonClicked () {
		model.goToPostBidPhase();
		showBidButtons(leadButtonColumn);
		setBidButtonsState(false);
		myBidComponent.setEnable(false);
	}

	/**
	 * This routine is called when one of the lead buttons is clicked.
	 *
	 * @param	leadPlayer		Indicates which lead button was clicked.
	 */
	private void leadButtonClicked (int leadPlayer) {
		showBidButtons(playedCardColumn);
		conn.send(new CommNinetyNineMakeBid(conn.getUsername(),
											bidHand,
											NinetyNineCoreModel.BID_REVEAL,
											localToGlobalSeatNum(leadPlayer)));
	}

	/**
	 * This routine will set the display to either show the bid buttons,
	 * the lead buttons, or the player's played card.
	 *
	 * @param	whichColumn		The column to show.
	 */
	private void showBidButtons(int whichColumn) {
		bidButtonLayout.setColumn(bidButtonColumn, 0);
		bidButtonLayout.setColumn(leadButtonColumn, 0);
		bidButtonLayout.setColumn(playedCardColumn, 0);
		bidButtonLayout.setColumn(whichColumn, TableLayout.PREFERRED);
		bidButtonPanel.invalidate();
		bidButtonPanel.validate();
	}

	/**
	 * Enable/Disable the bid buttons.
	 *
	 * @param	state			The state to put the bid buttons into
	 */
	private void setBidButtonsState(boolean state) {
		bidButton.setEnabled(state);
		declareButton.setEnabled(state);
		revealButton.setEnabled(state);
	}

	// =====================================================================

	/**
	 * The client model is telling us that the state has been changed without us knowing.
	 * This happens if we're an observer and we've just gotten a dump of the game state
	 * of a game in progress from the server.
	 */
	public void setState () {
		// Since we know that we are a observer, we can setup the gui correctly.

		// Turn off the bid buttons and always show the played card 0
		showBidButtons(playedCardColumn);
		setLocalCurrentPlayer();
	}
}
