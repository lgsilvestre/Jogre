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

import org.jogre.common.JogreModel;

import org.jogre.carTricks.common.CarTricksCoreModel;
import org.jogre.carTricks.common.CarTricksCard;
import org.jogre.carTricks.common.CarTricksTrackDB;
import org.jogre.carTricks.common.CarTricksPath;
import org.jogre.carTricks.common.CarTricksConnection;

import java.util.Vector;
import java.util.ListIterator;

// Model which holds the data for a game of Car Tricks
// This is the client model which only knows what a client sees
public class CarTricksClientModel extends CarTricksCoreModel {

	// The hand for this player
	private CarTricksDrawableHand my_hand = new CarTricksDrawableHand();

	// This array is used during the calculatePaths() method to trim the search tree.
	private int [] track_distance = new int [1];
	private final int CAR_IN_WAY = -2;

	// A list of all of the valid paths for the current car that is moving
	private Vector allPaths = null;

	// Codes for call to getCarArray()
	public static final int CAR_POSITION_CODE = 0;
	public static final int CAR_LOCATIONS_CODE = 1;
	public static final int CAR_BID_CODE = 2;

	// Keep my bid
	private int [] myBid;

	// This keeps track of whether or not I have bidded yet.
	private boolean notBiddedYet;

	// Keep track of things to allow the badges to be placed on the played cards.
	private int choseCarPlayer;
	private int currDriverValue, willDrivePlayer;
	private int currLeaderValue, willLeadPlayer;

	// Constant cards used to compare against.
	private CarTricksCard driverCard, leaderCard;
	
	/**
	 * Constructor for the client model
	 *
	 * @param	numPlayers		The number of players playing this game
	 * @param	enableEvents	Indicates if event cards should be used or not.
	 */
	public CarTricksClientModel(CarTricksTrackDB trackDatabase, int numPlayers, boolean enableEventCards) {
		// Construct the core model
		super(numPlayers, enableEventCards);

		// If we have a database, then add it now.
		if (trackDatabase != null) {
			addDatabase(trackDatabase);
		}

		// Use the resetGame routine to initialize most things
		this.resetGame();

		// Initialize things.
		driverCard = new CarTricksCard(CarTricksCard.EVENT, CarTricksCard.DRIVER);
		leaderCard = new CarTricksCard(CarTricksCard.EVENT, CarTricksCard.LEADER);
		clearBadgeInfo();

		// We're all set to start waiting for all players to join
		super.changePhase(super.WAITING_FOR_ALL_PLAYERS);
	}

	/**
	 * Attach the track database to the model.
	 *
	 * @param	trackDataBase	The database to attach to the model.
	 */
	public void addDatabase(CarTricksTrackDB trackDatabase) {
		super.addDatabase(trackDatabase);

		track_distance = new int [trackDatabase.getNumSpaces()];

		DB_dependent_init();

		// If the game state is moving the car, then calculate paths
		// (This only occurs if we are attaching to a game-in-progress
		// and we did't have the database and the game state arrived
		// before we got the database.)
		if (isMovingCar()) {
			calculatePaths(getActiveCar(), getSpacesToMove());
			refreshObservers();
		}
	}

	/**
	 * Reset the model back to the initial state
	 */
	public void resetGame () {
		// Reset the core model
		super.resetGame();

		DB_dependent_init();
		my_hand.empty();
		notBiddedYet = true;

		refreshObservers();
	}

	/**
	 * Do initialization that changes after the database has been provided.
	 * Note: This routine is called when games are created, reset, and
	 *       after a DB has been provided.  It should initialize things
	 *       appropriately for both before & after DB attachment.
	 */
	private void DB_dependent_init() {

		if (getTrackDatabase() == null) {
			// We don't yet have a database, so initialize things for
			// pre-database operation.
			myBid = new int [1];
		} else {
			// We have a database, so initialize things now that we
			// have all the data we need.
			myBid = (int []) getCarLocations().clone();
		}

		// Initialize my bid to all empty locations
		for (int i=0; i<myBid.length; i++) {
			myBid[i] = -1;
		}
	}

	/**
	 * Overriding the isSettingBid() from the core model so that from my perspective,
	 * I am only setting bids until I actually set a bid.  The core considers the
	 * game in SETTING_BID mode until all clients have submitted bids.  This keeps
	 * clients from being able to change their bid after they've submitted it but
	 * before all clients have.
	 *
	 * @return	true => client hasn't submitted a bid yet.
	 */
	public boolean isSettingBid() {
		return super.isSettingBid() && notBiddedYet;
	}

	public boolean hasSetBidPhase() {
		return super.isSettingBid() && !notBiddedYet;
	}

	/**
	 * Implement the setActivePlayer method of the ICarTricksSetActivePlayer interface.
	 * This should be overridden by someone to really set the active player, so this does
	 * nothing.  (See CarTricksMasterController.java for the real setActivePlayer)
	 */
	public void setActivePlayer (int seatNum) {}

	/**
	 * Initializes the bid to a default value.  This is not done in resetGame()
	 * because only real players get a bid.  Clients who are merely watching a
	 * game don't see one.  All clients will call resetGame() above as part
	 * of instantiation, but only real players will call initializeBid() to
	 * give them an initial bid.
	 */
	public void initializeBid() {
		for (int i=0; i<myBid.length; i++) {
			myBid[i] = i;
		}
		refreshObservers();
	}

	/**
	 * Retrieve the bid
	 */
	public int [] getBid() {
		return myBid;
	}

	/**
	 * Indicate that I've submitted my bid to the server.
	 */
	public void hasSubmittedBid() {
		notBiddedYet = false;
	}

	/**
	 * Change the phase of the game.
	 *
	 * @param	newPhase	The new game phase to set it to.
	 */
	public void changePhase(int newPhase) {
		super.changePhase(newPhase);
		refreshObservers();
	}

	/**
	 * Retrieve the hand of cards
	 *
	 * @return the hand
	 */
	public CarTricksDrawableHand getHand() {
		return my_hand;
	}

	/**
	 * Set a new hand of cards.
	 *
	 * @param	newCards	The new set of cards
	 */
	public void setHand(CarTricksCard [] newCards) {
		my_hand.setCards(newCards);
		refreshObservers();
	}

	/**
	 * Retrieve either car positions, car locations, or current bid, depending on
	 * the code
	 *
	 * @param	code		Code that indicates which array to return
	 * @return the car locations
	 */
	public int [] getCarArray(int code) {
		if (code == CAR_POSITION_CODE) {
			return getCarPositions();
		} else if (code == CAR_LOCATIONS_CODE) {
			return getCarLocations();
		} else if (code == CAR_BID_CODE) {
			return myBid;
		}
		return null;
	}

	/**
	 * Set the played card to the given one.
	 *
	 * @param	whichPlayer		The player whose played card we are setting
	 * @param	theCard			The card to be played.
	 * @param	endCondition	If true, then the game will end after the next car movement
	 *
	 * @return whether this card is a valid play or not.
	 */
	public boolean playCard(int whichPlayer, CarTricksCard theCard, boolean endCondition) {

		// Determine if this is still the first trick before playing the card.
		// (This is needed to determine when to evaluate badges.)
		boolean wasFirstTrick = isFirstTrick();

		// Attempt to play the card in the core model.
		boolean validPlay = super.playCard(whichPlayer, theCard);

		if (validPlay) {
			if (wasFirstTrick && !isFirstTrick()) {
				// The last card of the first trick was just played, so the
				// player that is now the active player is known to get all
				// three badges.
				setBadgeInfo(getActivePlayerId());
			} else if (!wasFirstTrick) {
				// This wasn't the first trick, so we can update the badges
				// as each card is played.
				updateBadgeInfo(whichPlayer, theCard);
			}

			// This card was a valid play, so see if the trick is complete.
			if (isMovingCar()) {
				// Yes, the trick is over, so calculate the spaces the car can move to
				calculatePaths(getActiveCar(), getSpacesToMove());
			}

			// If this is the last trick of the game, then remember that.
			if (endCondition) {
				signalEndOfGameCondition();
			}

			refreshObservers();
		}

		return validPlay;
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
		// Attempt to move the car in the core model.
		boolean validMove = super.moveCar(playerId, thePath);

		if (validMove) {
			// Kill the paths, since they don't exist anymore.
			allPaths = null;

			// Remove badge info
			clearBadgeInfo();

			refreshObservers();
		}

		return validMove;
	}

	/**
	 * Update the information used to draw the badges.  This assumes
	 * that the given card has already been played in the core model.
	 * (to set the activeCar, for instance).
	 *
	 * @param	whichPlayer		The player playing the card
	 * @param	theCard			The card the player is playing.
	 */
	private void updateBadgeInfo(int whichPlayer, CarTricksCard theCard) {

		int activeCar = super.getActiveCar();

		// If noone has played a card to set the active car color yet,
		// then this play might.
		if ((choseCarPlayer == -1) && (activeCar != -1)) {
			choseCarPlayer = whichPlayer;
		}

		// If there is an active car, and this card is the same color
		// and is higher than the highest value of that color seen so
		// far, then this player will be the driver.
		if ((activeCar != -1) &&
			(theCard.cardColor() == activeCar) &&
			(theCard.cardValue() > currDriverValue)) {
				currDriverValue = theCard.cardValue();
				willDrivePlayer = whichPlayer;
		}

		// If this card is the driver card, then this player automatically
		// becomes the driver.
		if (theCard.equals(driverCard)) {
			currDriverValue = CarTricksCard.MAX_VALUE+1;
			willDrivePlayer = whichPlayer;
		}

		// If this is the highest value card played so far, then
		// this player will be the next leader.
		if ((theCard.cardColor() >= 0) &&
			(theCard.cardValue() > currLeaderValue)) {
				currLeaderValue = theCard.cardValue();
				willLeadPlayer = whichPlayer;
		}

		// If this card is the leader card, then this player automatically
		// becomes the leader.
		if (theCard.equals(leaderCard)) {
			currLeaderValue = CarTricksCard.MAX_VALUE+1;
			willLeadPlayer = whichPlayer;
		}
	}

	/**
	 * Clear the badge info.  This will remove the badges from all of the
	 * cards.
	 */
	private void clearBadgeInfo() {
		choseCarPlayer = -1;
		willDrivePlayer = -1;
		currDriverValue = 0;
		willLeadPlayer = -1;
		currLeaderValue = 0;
	}

	/**
	 * Sets the badge info so that the given player gets all three badges.
	 *
	 * @param	playerId		The player to get all 3 badges.
	 */
	private void setBadgeInfo(int playerId) {
		choseCarPlayer = playerId;
		willDrivePlayer = playerId;
		willLeadPlayer = playerId;
	}

	/**
	 * Determine if whichPlayer was the one that played the card which
	 * set the active Car.
	 *
	 * @param	whichPlayer		The player number to check.
	 * @return	true => Yes, this player set the active car.
	 * @return	flase => No, this player did not set the active car.
	 */
	public boolean choseActiveCar(int whichPlayer) {
		return (whichPlayer >= 0) && (whichPlayer == choseCarPlayer);
	}

	/**
	 * Determine if whichPlayer will be driving the car.
	 *
	 * @param	whichPlayer		The player number to check.
	 * @return	true => Yes, this player will be driving the car.
	 * @return	flase => No, this player did not be driving the car.
	 */
	public boolean willDriveCar(int whichPlayer) {
		return (whichPlayer >= 0) && (whichPlayer == willDrivePlayer);
	}

	/**
	 * Determine if whichPlayer will be leading the next trick.
	 *
	 * @param	whichPlayer		The player number to check.
	 * @return	true => Yes, this player will lead the next trick.
	 * @return	flase => No, this player did not lead the next trick.
	 */
	public boolean willLeadNext(int whichPlayer) {
		return (whichPlayer >= 0) && (whichPlayer == willLeadPlayer);
	}

/*****************************************************************************/

	/**
	 * Calculate the paths that the given car can take of the given distance.
	 *
	 * Note: This may create two paths that end on the same spot if there is re-use
	 * of a space (since the track_distance array is used to hold only the latest
	 * position).  But that is ok, when drawing the path, one will be shown and it
	 * doesn't really matter which one is chosen.  The use of track_distance to
	 * trim the search tree is really to make the search faster and to not take up
	 * too much memory holding lots of paths that end on the same space.  It makes
	 * sure that the common case is taken care of, but is not perfectly exhaustive.
	 *
	 * @param	car_id		The car that is moving
	 * @param	distance	The number of spaces that the car must move
	 */
	private void calculatePaths(int car_id, int distance) {
		int i, nextLoc;
		allPaths = new Vector();	// Wipe out the old paths (if there were any)

		// If there is not a car moving (ie: all cards played were event cards)
		// then, pretend we're moving car 0 for a distance of 0.
		if (car_id < 0) {
			car_id = 0;
			distance = 0;
		}

		// Create a temporary path & connection list for searching the move tree
		CarTricksPath tempPath = new CarTricksPath (distance+1);
		CarTricksConnection [] connList = new CarTricksConnection [distance+1];

		// Seed the path with our car's starting location
		tempPath.addLoc(getCarLocations()[car_id]);

		// If the distance is 0, then there is only 1 valid move, and that is to
		// not move anywhere.
		if (distance == 0) {
			allPaths.add(tempPath);
			return;
		}

		// First, wipe out the distance values for all of the spaces on the board
		for (i=0; i<track_distance.length; i++) {
			track_distance[i] = -1;
		}

		// Add the other cars to the track_distance array so that they are in our way
		for (i=0; i<getCarLocations().length; i++) {
			if (i != car_id) {
				track_distance[getCarLocations()[i]] = CAR_IN_WAY;
			}
		}

		// Seed the connection list with the location that the given car is
		// sitting at.  (A clone is used because connList may have multiple
		// copies of the same location.)
		connList[0] = new CarTricksConnection (getTrackDatabase().getConnectionsForSpace(getCarLocations()[car_id]));

		// Start the index at 0.
		int curr_index = 0;
		int max_index = 0;

		while (curr_index >= 0) {
			nextLoc = connList[curr_index].nextLink();
			if (nextLoc != -1) {
				// There is a place to go after this one.
				if (track_distance[nextLoc] != CAR_IN_WAY) {
					if (track_distance[nextLoc] != curr_index) {
						// We haven't been here before, and it is empty, so need to save this.
						tempPath.addLoc(nextLoc);

						// Remember we've been here before to trim the search tree
						track_distance[nextLoc] = curr_index;

						// Advance to the next index
						curr_index += 1;
						max_index = curr_index;
						if (curr_index == distance) {
							// We've gone as far as we can, so we need to save this path
							allPaths.add(new CarTricksPath (tempPath));

							// Remove this space from the path
							tempPath.popLoc();
							curr_index -= 1;
						} else {
							// We still have spaces to go, so add this to the end of the connection list
							// before continuing
							connList[curr_index] = new CarTricksConnection (getTrackDatabase().getConnectionsForSpace(nextLoc));
						}
					} else {
						// We've been to the nextLoc through some other path, so we don't want to
						// continue searching down this path, but we need to set max_index larger
						// so that we don't think that we dead-ended here.
						max_index = curr_index + 1;
					}
				}
			} else {
				// If all paths out of here were blocked but we haven't used up all movement points, then
				// we want to add this path to the valid paths anyway.
				if (curr_index == max_index) {
					allPaths.add(new CarTricksPath (tempPath, max_index+1));
				}

				// There isn't a place to go after this one, so need to back up one space
				tempPath.popLoc();
				curr_index -= 1;
			}
		}
	}

	/**
	 * Return the vector of all current paths
	 */
	public Vector getAllPaths() {
		return allPaths;
	}


	/**
	 * Override this method in the core model.  This is called whenever the client attaches in the
	 * middle of a game and the new state has been sent.  This needs to update state outside of the
	 * core model.
	 */
	protected void setClientModelState() {
		// If we have the database and the car is moving, then calculate the paths
		if ((getTrackDatabase() != null) && (isMovingCar())) {
			calculatePaths(getActiveCar(), getSpacesToMove());
		}
	}

}
