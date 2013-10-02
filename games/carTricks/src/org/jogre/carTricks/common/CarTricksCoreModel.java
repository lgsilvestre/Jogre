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
package org.jogre.carTricks.common;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.common.util.JogreUtils;
import org.jogre.common.TransmissionException;


// Core model which holds the data for a game of Car Tricks
// This model is common for the client & the server.  It only holds
// data and methods that are common to the two.
public class CarTricksCoreModel extends JogreModel {

	// The number of players in this game.
	private int numPlayers;

	// Keeps track of whether this instance uses the Event cards or not.
	private boolean enableEvents;

	// The track database (this holds the information about the next spaces
	// for each space on the track).
	private CarTricksTrackDB trackDatabase = null;

	// The locations of the cars on the track
	private int [] car_locations;

	// The ordinal position (1st, 2nd, 3rd, ...) of each car on the track
	private int [] car_positions;

	// An array of track spaces that is used to verify that car movement doesn't drive
	// over another car
	protected boolean [] trackSpaceEmpty = new boolean [1];

	// The list of cards that have been played by each player
	private CarTricksCard [] playedCard;
	private CarTricksCard clearCard;

	// Flags for keeping track of the event cards played by the players
	protected boolean [][] eventCardFlags;
	protected int minPlayedEvents;
	protected int [] numPlayedEvents;
	protected boolean [] eventCardCanPlayThisTrick = new boolean [4];
	protected boolean someEventPlayedThisTrick;

	public static final int EVENT_ALREADY_PLAYED = 0;
	public static final int EVENT_NOT_AVAILABLE = 1;
	public static final int EVENT_AVAILABLE = 2;

	public static final int LEADER_ID     = (CarTricksCard.LEADER - CarTricksCard.MIN_VALUE);
	public static final int WRECK_ID      = (CarTricksCard.WRECK - CarTricksCard.MIN_VALUE);
	public static final int DRIVER_ID     = (CarTricksCard.DRIVER - CarTricksCard.MIN_VALUE);
	public static final int SLIPSTREAM_ID = (CarTricksCard.SLIPSTREAM - CarTricksCard.MIN_VALUE);

	// Values used to keep track of the reason why the car is not moving this round.
	protected int noMoveReason;
	public static final int CAR_MOVED = 0;
	public static final int WRECK_CARD_PLAYED = 1;
	public static final int ALL_EVENT_CARDS_PLAYED = 2;

	// Game Phases
	public static final int WAITING_FOR_ALL_PLAYERS = 0;
	public static final int SETTING_BID = 1;
	public static final int SELECTING_CARD = 2;
	public static final int MOVING_CAR = 3;
	public static final int GAME_OVER = 4;
	public static final int SETTING_BID_SPECTATOR = 5;
	private int gamePhase;
	protected boolean gameOverAfterNextCarMove;

	// Details on the car movement
	private int activePlayerId;
	private int carToMove;
	private int spacesToMove;

	// Keeps track of if this is the first trick or not.
	private boolean firstTrick;

	// The player who is to lead the next trick.
	protected int nextLeader;

	// The player who played the wreck card.
	private int wreckPlayerId;

	// Keep track of whether we have the state of the game or not.
	// This is needed because if we are attaching to a game after it has started (so that
	// we are an observer) and we don't have the database for this track, we could receive
	// the game state before we've gotten the database.  In this case, we need to remember
	// that when we do get the database, that we should *not* reset the game, but just
	// load the database.
	private boolean haveState = false;

	/**
	 * Constructor for the core model
	 *
	 * @param	numPlayers		The number of players playing this game
	 * @param	enableEvents	Indicates if event cards should be used or not.
	 */
	public CarTricksCoreModel(int numPlayers, boolean enableEvents) {
		super();

		// Initialize some stuff
		this.numPlayers = numPlayers;
		this.enableEvents = enableEvents;

		// Create various things
		playedCard = new CarTricksCard [numPlayers];
		eventCardFlags = new boolean [numPlayers][4];
		numPlayedEvents = new int [numPlayers];
		clearCard = CarTricksCard.makeInvisibleCard(new CarTricksCard());

		// Use the resetGame routine to initialize most things
		private_ResetGame();
	}

	/**
	 * Attach the track database to the model.
	 *
	 * @param	trackDatabase	The database to attach to the model.
	 */
	public void addDatabase(CarTricksTrackDB trackDatabase) {
		this.trackDatabase = trackDatabase;

		// Create and initialize the trackSpaceEmpty array
		trackSpaceEmpty = new boolean [trackDatabase.getNumSpaces()];

		this.DB_dependent_init();
	}

	/**
	 * Reset the model back to the initial state
	 */
	private void private_ResetGame() {
		// Do database-dependent initialization
		this.DB_dependent_init();

		// Reset player states
		for (int i=0; i<numPlayers; i++) {
			playedCard[i] = clearCard;
			for (int j=0; j<4; j++) {
				eventCardFlags[i][j] = enableEvents;
			}
			numPlayedEvents[i] = (enableEvents ? 0 : 4);
		}

		// Reset misc. stuff
		for (int i=0; i<4; i++) {
			eventCardCanPlayThisTrick[i] = true;
		}
		someEventPlayedThisTrick = false;
		minPlayedEvents = (enableEvents ? 0 : 4);
		gamePhase = SETTING_BID;
		activePlayerId = -1;
		carToMove = -1;
		spacesToMove = 0;
		firstTrick = true;
		nextLeader = 0;
		gameOverAfterNextCarMove = false;
		wreckPlayerId = 0;
	}

	public void resetGame() {
		private_ResetGame();
	}


	/**
	 * Do initialization that changes after the database has been provided.
	 * Note: This routine is called when games are created, reset, and
	 *       after a DB has been provided.  It should initialize things
	 *       appropriately for both before & after DB attachment.
	 */
	private void DB_dependent_init() {

		if (trackDatabase == null) {
			// We don't yet have a database, so initialize things for
			// pre-database operation.
			car_locations = new int [1];
			car_positions = new int [1];
			car_positions[0] = -1;
		} else {
			// We have a database, so initialize things now that we
			// have all the data we need.

			if (!haveState) {
				// Set the cars back to their starting locations
				car_locations = (int []) trackDatabase.startSpaces.clone();
				car_positions = (int []) car_locations.clone();
			}

			calculate_car_positions();
			placeCarsOnTrack();
		}

	}

	/**
	 * Routines for getting the game phase
	 */
	public boolean isWaitingForPlayers()   {return (gamePhase == WAITING_FOR_ALL_PLAYERS);}
	public boolean isSettingBid()          {return (gamePhase == SETTING_BID);}
	public boolean isSpectatorSettingBid() {return (gamePhase == SETTING_BID_SPECTATOR);}
	public boolean isSelectingCard()       {return (gamePhase == SELECTING_CARD);}
	public boolean isMovingCar()           {return (gamePhase == MOVING_CAR);}
	public boolean isGameOver()            {return (gamePhase == GAME_OVER);}

	/**
	 * Change the phase of the game to the new one provided
	 *
	 * @param	newPhase	The new phase to move to.
	 *
	 */
	public void changePhase(int newPhase) {
		gamePhase = newPhase;
	}

	/**
	 * Indication that the game will end after the next car move
	 */
	protected void signalEndOfGameCondition() {
		gameOverAfterNextCarMove = true;
	}

	/**
	 * Return a list of cars ordered by car color, where the nth entry
	 * is the place of the nth car.
	 * ie: if array[n] = 1, then that car is in first place.
	 *     if array[n] = 2, then that car is in second place.
	 *
	 * @return	the array of positions
	 */
	public int [] getPositionsByCar() {
		int [] pos = new int [numCarsInRace()];
		for (int i=0; i<pos.length; i++) {
			pos[i] = carsAheadOf(getCarLocations()[i] + 1);
		}
		return pos;
	}

	/**
	 * Given the current locations of the cars on the track, determine what their
	 * ordinal positions (1st, 2nd, 3rd) are.  Place the id of the car that is
	 * in the nth position in the (n-1)th element of the car_positions array.
	 */
	private void calculate_car_positions() {
		for (int i=0; i<car_locations.length; i++) {
			// A car's position is the number of cars ahead of it
			car_positions[carsAheadOf(car_locations[i])] = i;
		}
	}

	/**
	 * Count the number of cars that are ahead of the given location of the track.
	 *
	 * @param	loc			The location to count cars ahead of
	 * @return the number of cars ahead of that location
	 */
	protected int carsAheadOf(int loc) {
		int count = 0;
		for (int i=0; i<car_locations.length; i++) {
			if (car_locations[i] < loc) {
				count += 1;
			}
		}
		return count ;
	}

	/**
	 * Make the track empty except for the current locations of the cars
	 */
	private void placeCarsOnTrack() {
		// Clear the track to empty...
		for (int i=0; i<trackSpaceEmpty.length; i++) {
			trackSpaceEmpty[i] = true;
		}

		// ... and make the spaces with the cars not empty.
		for (int i=0; i<car_locations.length; i++) {
			trackSpaceEmpty[car_locations[i]] = false;
		}
	}

	/*
	 * Retrieve various values from the model
	 */
	public int numCarsInRace()								{return car_locations.length;}
	public int [] getCarLocations()							{return car_locations;}
	public int [] getCarPositions()							{return car_positions;}
	public CarTricksTrackDB getTrackDatabase()				{return trackDatabase;}
	public int getNumPlayers()								{return numPlayers;}
	public CarTricksCard getPlayedCard(int whichPlayer)		{return playedCard[whichPlayer];}
	public int getActivePlayerId()							{return activePlayerId;}
	public int getActiveCar()								{return carToMove;}
	public int getSpacesToMove()							{return spacesToMove;}
	public boolean [] getEventCardFlags(int whichPlayer)	{return eventCardFlags[whichPlayer];}
	public boolean isFirstTrick()							{return firstTrick;}
	public int getWreckPlayerId()							{return wreckPlayerId;}

	public boolean wreckPlayed() {
		return (spacesToMove == 0) && (noMoveReason == WRECK_CARD_PLAYED);
	}

	public boolean onlyEventsPlayed() {
		return (spacesToMove == 0) && (noMoveReason == ALL_EVENT_CARDS_PLAYED);
	}

	/**
	 * Set the played card to the given one.
	 *
	 * @param	whichPlayer		The player whose played card we are setting
	 * @param	theCard			The card to be played.
	 */
	public boolean playCard(int whichPlayer, CarTricksCard theCard) {

		// Determine if the requested card is playable
		boolean playable = verifyCardPlayable(whichPlayer, theCard);

		if (playable) {
			// Remember this card
			playedCard[whichPlayer] = theCard;

			// Advance to the next active player
			if (activePlayerId != -1) {
				activePlayerId = (activePlayerId + 1) % numPlayers;
			}

			if (allPlayedCardsKnown()) {
				// All cards are known, so we should advance to the moving car phase
				gamePhase = MOVING_CAR;

				// Evaluate the trick to determine how far the car is to move.
				if (firstTrick) {
					evalFirstTrick();
				} else {
					evalTrick();
				}
			} else {
				// If all cards aren't known, then we check to see if this
				// is the first color card played, in which case it sets
				// the carToMove color, and the badges can now be put on
				// the cards (in the client view).
				if ((carToMove == -1) && (theCard.cardColor() >= 0)) {
					carToMove = theCard.cardColor();
				}
			}

		}

		return (playable);
	}

	/**
	 * Set all of the played cards to clear.
	 */
	private void clearPlayedCards() {
		for (int i=0; i<numPlayers; i++) {
			playedCard[i] = clearCard;
		}
	}

	/**
	 * Clear the state of the current trick and setup for the next trick.
	 */
	private void clearTrickState() {
		clearPlayedCards();
		spacesToMove = 0;
		carToMove = -1;
		someEventPlayedThisTrick = false;
		for (int i=0; i<4; i++) {
			eventCardCanPlayThisTrick[i] = true;
		}
	}

	/**
	 * Check to see if all played cards are known.  This is used to determine if the trick
	 * is complete or not.
	 *
	 * @return		true  => all played cards are known.
	 *				false => not all played cards are known.
	 */
	private boolean allPlayedCardsKnown() {
		for (int i=0; i<numPlayers; i++) {
			if (!playedCard[i].isKnown()) {
				return (false);
			}
		}
		return (true);
	}

	/**
	 * Verify that a card is playable by the given player
	 *
	 * @param	player_id			The seat number of the player who is playing the card
	 * @param	theCard				The card being played
	 * @return			true  => The card is legal to play and has been played
	 *					false => The card is not legal to play
	 */
	private boolean verifyCardPlayable(int player_id, CarTricksCard theCard) {
		// A card can only be played by either:
		//    a) the Active player during SELECTING_CARD phase
		// or b) any player during the SETTING_BID phase
		if (!(gamePhase == SETTING_BID) &&
			!(gamePhase == SETTING_BID_SPECTATOR) &&
			!((gamePhase == SELECTING_CARD) && (activePlayerId == player_id))) {

			return (false);
		}

		// This player is valid to play, now see if the player can play the requested card.
		if (theCard.isEvent()) {
			// If the card is an event card, then make sure that it is legal to play
			// at this point.
			int evCardId = theCard.cardValue() - CarTricksCard.MIN_VALUE;

			if ((eventCardFlags[player_id][evCardId]) &&			// This player has this event card in his hand
				(numPlayedEvents[player_id] == minPlayedEvents) &&	// This player hasn't played too many event cards yet
				(eventCardCanPlayThisTrick[evCardId])) {			// This event card hasn't been played yet this trick
				// This is a legal card to play

				eventCardFlags[player_id][evCardId] = false;		// This card is not valid to play anymore
				numPlayedEvents[player_id] += 1;					// This player has now played an event card
				eventCardCanPlayThisTrick[evCardId] = false;		// It is now not valid to play the same event card in this trick

				// Once any event card has been played, then WRECK cards are not valid to play in this trick
				eventCardCanPlayThisTrick[CarTricksCard.WRECK - CarTricksCard.MIN_VALUE] = false;

				// Determine the new minPlayedEvents value
				calculate_minPlayedEvents();

				return (true);
			} else {
				// For some reason, this event card is not valid to play
				return (false);
			}
		} else {
			// The card is not an event card, so it is valid to play
			return (true);
		}
	}

	/**
	 * Indicate if the given player is allowed to play the requested event card
	 *
	 * @param	whichPlayer		The player who is being polled
	 * @param	card_id			Index of which special card
	 * @return					EVENT_ALREADY_PLAYED => Card has been played
	 *							EVENT_AVAILABLE      => Card can be played
	 *							EVENT_NOT_AVAILABLE  => Card has not been played, but
	 *							                          can't play right now.
	 */
	public int eventCardState(int whichPlayer, int card_id) {
		if (eventCardFlags[whichPlayer][card_id] == false) {
			// This player has already played this card
			return (EVENT_ALREADY_PLAYED);
		}

		if (numPlayedEvents[whichPlayer] > minPlayedEvents) {
			// This player has played more events that someone else, so
			// can't play any until that someone else plays one.
			return (EVENT_NOT_AVAILABLE);
		}

		if (eventCardCanPlayThisTrick[card_id] == false) {
			// Someone else has played this event this turn
			return (EVENT_NOT_AVAILABLE);
		}

		if ((card_id == WRECK_ID) && (someEventPlayedThisTrick == true)) {
			// Can only play a wreck if no other event cards have been played
			return (EVENT_NOT_AVAILABLE);
		}

		return (EVENT_AVAILABLE);
	}

	/**
	 * Evaluate the current trick as the first trick.  Since there is no lead player during the first
	 * trick, the highest value card wins.  In the case of ties, the color that is currently leading
	 * wins.  Since none of the cars has moved yet, we know that they are in the starting locations,
	 * so we can use the colors to determine order.  (Note: This requires that the starting locations
	 * are defined in color order...)
	 *
	 * This sets activePlayerId, carToMove, spacesToMove and nextLeader to valid values.
	 */
	private void evalFirstTrick() {
		CarTricksCard highestCard = new CarTricksCard(CarTricksCard.UNKNOWN, CarTricksCard.MIN_VALUE);

		// Initialize to noone moving the car.
		activePlayerId = -1;

		// Determine which card is the highest valued card played
		for (int i=0; i<numPlayers; i++) {
			if ((playedCard[i].cardValue() > highestCard.cardValue()) ||
				((playedCard[i].cardValue() == highestCard.cardValue()) &&
				 (playedCard[i].cardColor() < highestCard.cardColor()))) {
				highestCard = playedCard[i];
				activePlayerId = i;
			}
		}

		if (activePlayerId < 0) {
			// All cards played during the first trick were event cards, so ignore this trick
			// and pretend that the next trick is the first one...
			spacesToMove = 0;
			return;
		}

		// Now that a non-event card was played, this is no longer the first trick of the game
		firstTrick = false;

		// Seed nextLeader with the active player ID so that if multiple people played the
		// same valued card, that the next leader will be the same as the person who played
		// the card that won the position tie-breaker.
		nextLeader = activePlayerId;

		// Determine the number of spaces to move.
		carToMove = highestCard.cardColor();
		calcSpacesToMove();
	}

	/**
	 * Evaluate the current trick.
	 *
	 * This sets activePlayerId, carToMove, spacesToMove and nextLeader to valid values.
	 */
	private void evalTrick() {

		// Determine the color of the car that is going to move

		// Scan the cards starting with the player that lead this trick.
		int whoseCard = nextLeader;
		do {
			carToMove = playedCard[whoseCard].cardColor();
			if (carToMove < 0) {
				whoseCard = (whoseCard + 1) % numPlayers;
			}
		} while ((carToMove < 0) && (whoseCard != nextLeader));

		if (carToMove < 0) {
			// All cards played were events, so there is no car movement this turn
			spacesToMove = 0;

			// Let the leader have to click before starting the next trick
			activePlayerId = nextLeader;

			// Leave nextLeader alone, so that the same player leads the next trick.
			return;
		}

		// Now that we know the car that is to move, calculate the distance to move
		// and the player to move.
		calcSpacesToMove();
	}

	/**
	 * Calculate the number of spaces that the carToMove is to move, who the driver will be
	 * and who will lead the next trick, given all of the played cards.
	 *
	 * This sets activePlayerId, nextLeader & spacesToMove.
	 */
	private void calcSpacesToMove() {
		int multiplier = 1;

		// Start at 0 spaces to move
		spacesToMove = 0;

		// Assume that all of the cards are event cards.
		noMoveReason = ALL_EVENT_CARDS_PLAYED;

		// Initialize limits for overall highest card and highest card of the moving car color
		int maxCardValue = -1;
		int maxCardValueOfMovingCar = -1;

		// Start with the leader's card
		int player = nextLeader;
		nextLeader = -1;

		for (int i=0; i<numPlayers; i++) {
			if (playedCard[player].isEvent()) {
				// In the case of event cards, we need to take special action.
				switch (playedCard[player].cardValue()) {
					case CarTricksCard.SLIPSTREAM :
						multiplier *= 2;
						break;
					case CarTricksCard.WRECK :
						noMoveReason = WRECK_CARD_PLAYED;
						wreckPlayerId = player;
						multiplier = 0;
						break;
					case CarTricksCard.DRIVER :
						activePlayerId = player;
						maxCardValueOfMovingCar = CarTricksCard.MAX_VALUE + 1;
						break;
					case CarTricksCard.LEADER :
						nextLeader = player;
						maxCardValue = CarTricksCard.MAX_VALUE + 1;
				}
			} else {
				// This is a non-event card.
				if (playedCard[player].cardColor() == carToMove) {
					// If this card is the same color as the car to move, then add
					// it's value to the number of space to be moved
					spacesToMove += playedCard[player].cardValue();

					if (playedCard[player].cardValue() > maxCardValueOfMovingCar) {
						// This player played a higher card value of the moving car's
						// color, so he is now the driver.
						activePlayerId = player;
						maxCardValueOfMovingCar = playedCard[player].cardValue();
					}
				}

				if (playedCard[player].cardValue() > maxCardValue) {
					// This player played a higher card value, so his will lead the
					// next trick.
					maxCardValue = playedCard[player].cardValue();
					nextLeader = player;
				}
			}

			// Advance to the next player
			player = (player + 1) % numPlayers;
		}

		// Apply the multiplier to spacesToMove
		spacesToMove *= multiplier;
	}

	/**
	 * Calculate the minimum number of event cards played by all of the players.
	 * The answer is stored in minPlayedEvents.
	 */
	private void calculate_minPlayedEvents() {
		minPlayedEvents = 5;
		for (int i=0; i<numPlayers; i++) {
			minPlayedEvents = Math.min(minPlayedEvents, numPlayedEvents[i]);
		}
	}

	/**
	 * Move the car.
	 *
	 * @param	playerId		The player attempting to move the car.
	 * @param	thePath			The path to move the car along.
	 * @return 	true  => car moved succesfully.
	 *			false => car movement invalid.
	 */
	public boolean moveCar(int playerId, CarTricksPath thePath) {
		if (playerId != activePlayerId) {
			// Wrong player is attempting to move
			return false;
		}

		if (spacesToMove > 0) {
			// Only need to make more checks if the car is actually moving

			if (!canMoveAlongPath(thePath)) {
				// Can't move along that path, so replace the car
				trackSpaceEmpty[car_locations[carToMove]] = false;
				return false;
			}

			// Move the car from it's current position to the final position
			car_locations[carToMove] = thePath.getTerminal();
			trackSpaceEmpty[car_locations[carToMove]] = false;

			// Recalculate the car positions (1st, 2nd, 3rd, etc...)
			calculate_car_positions();
		}

		// Clear the data from the trick that just finished
		clearTrickState();

		// Advance to the next game phase.
		gamePhase = (gameOverAfterNextCarMove ? GAME_OVER : SELECTING_CARD);
		activePlayerId = nextLeader;

		return true;
	}

	/**
	 * Check to see if the moving car can move along the requested path
	 *
	 * @param	thePath			The path to move the car along.
	 * @return	true  => car can move along the path.
	 *			false => car cannot move along the path.
	 */
	private boolean canMoveAlongPath(CarTricksPath thePath) {
		if (thePath.pathLength() == 0) {
			// If the path is empty, then this shouldn't have been called,
			// so it is, by definition, invalid.
			return false;
		}

		int carLoc = thePath.getLoc(0);

		if (car_locations[carToMove] != carLoc) {
			// If the car that is to move isn't on the first space of the path,
			// then this path is invalid.
			return false;
		}

		// The space that the moving car is currently on will be empty.
		trackSpaceEmpty[carLoc] = true;

		// Make sure that it is valid to move from each space in the path
		// to the next space in the path.  (We only need to do this if the
		// path actually moves the car.)

		if (thePath.pathLength() > 1) {
			for (int i=1; i<thePath.pathLength(); i++) {
				int nextLoc = thePath.getLoc(i);
				if (!trackDatabase.getConnectionsForSpace(carLoc).connectsTo(nextLoc)) {
					// Can't move from the current location to the next in the list
					return false;
				}

				if (!trackSpaceEmpty[nextLoc]) {
					// The space is not empty
					return false;
				}
				carLoc = nextLoc;
			}
		}

		// If the path didn't use all of the movement points, then this path is
		// legal only if the space that it ended on has no legal following spaces.
		if (thePath.pathLength() != (spacesToMove+1)) {
			CarTricksConnection ctConn = trackDatabase.getConnectionsForSpace(thePath.getTerminal());
			ctConn.resetLinks();
			int index = ctConn.nextLink();
			do {
				if ((index > 0) && (trackSpaceEmpty[index])) {
					// Oops, here is an empty space that the player could have moved to
					return false;
				}
				index = ctConn.nextLink();
			} while (index > 0);
		}

		return true;
	}

/*****************************************************************************************/
/* Save/restore game state methods */

	// XML attributes used for sending/receiving board state
	private static final String XML_ATT_TRACK_NAME = "trackName";
	private static final String XML_ATT_CAR_LOCATIONS = "cars";
	private static final String XML_ATT_NUM_PLAYERS = "numPlayers";
	private static final String XML_ATT_GAMEPHASE = "phase";
	private static final String XML_ATT_ACTIVEPLAYER = "actPlayer";
	private static final String XML_ATT_ACTIVECAR = "actCar";
	private static final String XML_ATT_SPACESTOMOVE = "spacesToMove";
	private static final String XML_ATT_EVENTCARD_PLAYTRICK = "evtrck";
	private static final String XML_ATT_SOMEEVENT_PLAYTRICK = "somevtrck";
	private static final String XML_ATT_FIRSTTRICK = "firstTrick";
	private static final String XML_ATT_GAMEOVER = "gameOver";
	private static final String XML_ATT_NEXTLEADER = "nextLeader";

	private static final String XML_ATT_EVENTCARD_FLAGS = "ecf";
	private static final String XML_ATT_PLAYED_CARD = "card";

	/**
	 * Set the model state from the contents of the message.  This is used to
	 * decode the message sent from the server when attaching so that the
	 * client gets the current state of the game, even if attaching in the middle
	 * of a game.
	 *
	 * @param message    Message from the server
	 * @throws TransmissionException
	 */
	public void setState (XMLElement message) {
		// We have been given the game state.
		haveState = true;

		// Reset the game back to the starting value before using the
		// message to fill it in.
		private_ResetGame();

		// Pull all of the bits out of the message
		car_locations = JogreUtils.convertToIntArray(message.getStringAttribute(XML_ATT_CAR_LOCATIONS));
		car_positions = (int []) car_locations.clone();
		numPlayers = message.getIntAttribute(XML_ATT_NUM_PLAYERS);
		gamePhase = message.getIntAttribute(XML_ATT_GAMEPHASE);
		activePlayerId = message.getIntAttribute(XML_ATT_ACTIVEPLAYER);
		carToMove = message.getIntAttribute(XML_ATT_ACTIVECAR);
		spacesToMove = message.getIntAttribute(XML_ATT_SPACESTOMOVE);
		eventCardCanPlayThisTrick = JogreUtils.convertToBoolArray(message.getStringAttribute(XML_ATT_EVENTCARD_PLAYTRICK));
		someEventPlayedThisTrick = message.getStringAttribute(XML_ATT_SOMEEVENT_PLAYTRICK).equals("t");
		firstTrick = message.getStringAttribute(XML_ATT_FIRSTTRICK).equals("t");
		gameOverAfterNextCarMove = message.getStringAttribute(XML_ATT_GAMEOVER).equals("t");
		nextLeader = message.getIntAttribute(XML_ATT_NEXTLEADER);

		for (int i = 0; i < numPlayers; i++) {
			eventCardFlags[i] = JogreUtils.convertToBoolArray(message.getStringAttribute(XML_ATT_EVENTCARD_FLAGS + i));
			playedCard[i] = CarTricksCard.fromString(message.getStringAttribute(XML_ATT_PLAYED_CARD + i));
		}

		// Calculate things that aren't explicitly sent in the message
		if (trackDatabase != null) {
			placeCarsOnTrack();
			calculate_car_positions();
		}
		calculate_numPlayedEvents();
		calculate_minPlayedEvents();

		// Call out to the client model so that it can set it's state as well...
		setClientModelState();

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

	/**
	 * The client model that extends this class should override this method to get called whenever
	 * the core model is updated.  This allows the client model to update things that he knows about
	 * that the core model doesn't.
	 */
	protected void setClientModelState() {}

	/**
	 * Calculate the number of event cards played by each player.
	 */
	private void calculate_numPlayedEvents() {
		for (int i=0; i<numPlayers; i++) {
			numPlayedEvents[i] = 0;
			for (int j=0; j<eventCardFlags[i].length; j++) {
				if (!eventCardFlags[i][j]) {
					numPlayedEvents[i] += 1;
				}
			}
		}
	}


	/**
	 * Used to bundle up the state of the model.  This is used so that when
	 * a client attaches, it gets the current state of the board from the
	 * server.  This allows an observer to attach to a game in progress and
	 * get the up-to-date values.
	 *
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
		// Retrieve empty state from super class
		XMLElement state = new XMLElement (Comm.MODEL);

		// Add attributes for all of the elements of the game
		state.setAttribute(XML_ATT_TRACK_NAME, trackDatabase.getTrackName());
		state.setAttribute(XML_ATT_CAR_LOCATIONS, JogreUtils.valueOf(car_locations));
		state.setIntAttribute(XML_ATT_NUM_PLAYERS, numPlayers);
		state.setIntAttribute(XML_ATT_GAMEPHASE, gamePhase);
		state.setIntAttribute(XML_ATT_ACTIVEPLAYER, activePlayerId);
		state.setIntAttribute(XML_ATT_ACTIVECAR, carToMove);
		state.setIntAttribute(XML_ATT_SPACESTOMOVE, spacesToMove);
		state.setAttribute(XML_ATT_EVENTCARD_PLAYTRICK, JogreUtils.valueOf(eventCardCanPlayThisTrick));
		state.setAttribute(XML_ATT_SOMEEVENT_PLAYTRICK, (someEventPlayedThisTrick ? "t" : "f"));
		state.setAttribute(XML_ATT_FIRSTTRICK, (firstTrick ? "t" : "f"));
		state.setAttribute(XML_ATT_GAMEOVER, (gameOverAfterNextCarMove ? "t" : "f"));
		state.setIntAttribute(XML_ATT_NEXTLEADER, nextLeader);

		for (int i = 0; i < numPlayers; i++) {
			state.setAttribute(XML_ATT_EVENTCARD_FLAGS + i, JogreUtils.valueOf(eventCardFlags[i]));
			state.setAttribute(XML_ATT_PLAYED_CARD + i, playedCard[i].toString());
		}

		return state;
	}

}
