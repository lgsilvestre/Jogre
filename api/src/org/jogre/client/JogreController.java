/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import nanoxml.XMLElement;

import org.jogre.client.awt.JogreComponent;
import org.jogre.common.JogreModel;
import org.jogre.common.Player;
import org.jogre.common.PlayerList;
import org.jogre.common.Table;
import org.jogre.common.comm.CommControllerObject;
import org.jogre.common.comm.CommControllerProperty;
import org.jogre.common.comm.CommNextPlayer;
import org.jogre.common.comm.ITransmittable;

/**
 * <p>Adapter game controller class which creates empty methods for the
 * MouseListener, MouseMotionListener and KeyListener event interfaces.
 * This class also contains a link to the JogreModel, a connection thread to the
 * server and also a JogreComponent where the mouse movements/key presses are taking
 * place on.</p>
 *
 * <p>To use this controller a class must extend it e.g. ChessController and
 * then override one of its event methods, update the correct model and send
 * communication to other users.  To use the controller on a JogreComponent
 * a JogreComponent calls its <code>public void setController (JogreController
 * controller)</code> method.</p>
 *
 * <p>Any of the following methods can be overwritten to do something:</p>
 * <code>
 * <ul>
 *   <li>public void mouseEntered (MouseEvent e)</li>
 *   <li>public void mouseExited (MouseEvent e)</li>
 *   <li>public void mouseClicked (MouseEvent e)</li>
 *   <li>public void mouseReleased (MouseEvent e)</li>
 *   <li>public void mousePressed (MouseEvent e)</li>
 *   <li>public void mouseMoved (MouseEvent e)</li>
 *   <li>public void mouseDragged (MouseEvent e)</li>
 *   <li>public void keyPressed (KeyEvent e)</li>
 *   <li>public void keyReleased (KeyEvent e)</li>
 *   <li>public void keyTyped (KeyEvent e)</li>
 * </ul>
 * </code>
 *
 * <p>This class also contains a link to the ClientConnectionThread.  This
 * allows access to the UserList and TableList object which allow access to what
 * is happening on the server.  This class also provides various useful methods
 * such as returning the table number, current player, current player's seat
 * number, this players seat number, etc.</p>
 *
 * <p>Since Alpha 0.2, the controller contains convience methods for sending
 * and receving properties and XML objects easily and quickly to other players
 * at a game using the following send methods:</p>
 *
 * <p>
 * <code>
 * <ul>
 *   <li>public void sendProperty (String key, String value)</li>
 *   <li>public void sendProperty (String key, int value)</li>
 *   <li>public void sendProperty (String key, int x, int y)</li>
 *   <li>public void sendObject   (ITransmittable object)</li>
 *   <li>public void sendObject   (XMLElement element)</li>
 * </ul>
 * </code>
 * </p>
 *
 * <p>All of these methods send something to the server.  If a send method
 * is used then its corresponding adapter receive method must also be fillin.
 * e.g. if a method calls:</p>
 * <p><blockquote>sendProperty ("move", 5, 5");</blockquote></p>
 * <p>then the following recieve method must be filled in to do as the
 * controllers for the other players will receive this property.</p>
 * <p><blockquote>recieveProperty (String key, int x, int y) { ...</blockquote></p>
 *
 * <p><b>Note:</b> Each sub class of JogreController most implement the
 * abstract <i>start()</i> method.  This is used for things like resetting
 * the model / controller timers etc.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class JogreController implements MouseListener, MouseMotionListener, KeyListener {

	/** Link to the game model. */
	protected JogreModel model;

	/** Link to the JogreComponent this is controlling . */
	protected JogreComponent JogreComponent;

	/** Link to the client's connection thread. */
	protected TableConnectionThread conn;

	/** Table number which this controller is controlling. */
	protected int tableNum;

	/**
	 * Reset method which is called every time a new game is created.  Each
	 * sub class of this must have this method.  This method is called when the
	 * game starts for the first time.
	 */
	public abstract void start ();

	/**
	 * Default constructor class which doesn't set up the network connection.
	 *
	 * @param model           Model which the controller can update depending on
	 *                        what the user input is.
	 * @param JogreComponent  The JogreComponent which the controller is
	 *                        listening on.
	 */
	public JogreController (JogreModel model, JogreComponent JogreComponent) {
		this.model = model;
		this.JogreComponent = JogreComponent;
	}

	/**
	 * Set up a network connection for this controller.
     *
	 * @param conn      Link to the TableConnectionThread.
	 */
	public void setConnection (TableConnectionThread conn) {
		this.conn = conn;
		this.tableNum = conn.getTableNum();
	}

	/**
	 * Return the current player.
	 *
	 * @return  Current player's username.
	 */
	public String getCurrentPlayer () {
		// if connection is not equal to null then there is access to the
		// current player etc.
		if (this.conn != null) {
			return getTable().getPlayerList().getCurrentPlayerName();
		}
		return null;
	}

	/**
	 * Return the player num (between 0 and the number of players).
	 *
	 * @return   Seat number of current player.
	 */
	public int getCurrentPlayerSeatNum () {
		// Check connection has been set
		if (this.conn != null) {
			Player currentPlayer = getTable().getPlayerList().getCurrentPlayer();
			if (currentPlayer != null)
				return currentPlayer.getSeatNum();
		}
		return 0;
	}

	/**
	 * Return the table number.
	 *
	 * @return   table number.
	 */
	public int getTableNum () {
		return this.tableNum;
	}

	/**
	 * Convience method for returning a table.
	 *
	 * @return   Current table i.e. tableList.getTable (current table).
	 */
	public Table getTable () {
	    return this.conn.getTable();
	}

	/**
	 * Return the seat number of a user.
	 *
	 * @return   If -1 user is standing, otherwise seatNum &gt=0 and &lt number
	 * of seats available.
	 */
	public int getSeatNum () {
		if (this.conn != null) {
			PlayerList playerList = getTable().getPlayerList();
			Player player = playerList.getPlayer (this.conn.getUsername());

			if (player != null)
				return player.getSeatNum();
		}
		return Player.NOT_SEATED;
	}

	/**
	 * Return the player name from a seat num.
	 *
	 * @param  seatNum  Seat number of the user.
	 * @return   If -1 user is standing, otherwise seatNum &gt=0 and &lt number
	 * of seats available.
	 */
	public String getPlayer (int seatNum) {
		if (this.conn != null) {
			Table table = this.conn.getTableList().getTable (this.tableNum);
			return table.getPlayerList().getPlayer (seatNum).getPlayerName();
		}
		return null;
	}

	/**
	 * Return the seat number of a specified player.
	 *
	 * @param player	Name of the player.
	 * @return          Number of specified players seat.
	 */
	public int getSeatNum (String player) {
		if (this.conn != null) {
			Table table = this.conn.getTableList ().getTable (this.tableNum);
			return table.getPlayerList ().getPlayer (player).getSeatNum();
		}
		return Player.NOT_SEATED;
	}

	/**
	 * Return true/false if the game is started or not.
	 * 
	 * NOTE: This will return true even if a player is not seated at a table.
	 *
	 * @return 	Returns true if a game is underway.
	 */
	public boolean isGamePlaying () {
		if (this.conn != null) {
			Table table = this.conn.getTableList().getTable (this.tableNum);
			if (table != null)
				return table.isGamePlaying();
		}
		return false;
	}
	
	/**
	 * Returns true if game is playing AND player is also seated - useful
	 * for non-turned based style games.
	 * 
	 * @return
	 */
	public boolean isPlayerPlaying () {
		if (this.conn != null) {
			Table table = this.conn.getTableList().getTable (this.tableNum);
			if (table != null) {
				return table.isGamePlaying() && getSeatNum() != Player.NOT_SEATED;
			}
		}
		return false;
	}

	/**
	 * Send a more complex object (any object which implements the ITransmittable
	 * interface). The recieve (XMLElement element) method must be overwritten
	 * to recieve it.
	 *
	 * @param object   A more complex piece of data encoded in XML.
	 */
	public void sendObject (ITransmittable object) {
		sendObject (object.flatten());
	}

	/**
	 * Send a more complex object (any object which implements the ITransmittable
	 * interface).  The recieve (XMLElement element) method must be overwritten
	 * to recieve it.
	 *
	 * @param message   A more complex piece of data encoded in XML.
	 */
	public void sendObject (XMLElement message) {
		// Create property communications object, add data and send to server
		CommControllerObject commObject = new CommControllerObject ();
		commObject.setData (message);
		this.conn.send (commObject);
	}

	/**
	 * Send a normal String valued property.
	 *
	 * @param key
	 * @param value
	 */
	public void sendProperty (String key, String value) {
		// Create property communications object and send to server
		CommControllerProperty commContProp = new CommControllerProperty (
			CommControllerProperty.TYPE_STRING, key, value);

		this.conn.send(commContProp);
	}

	/**
	 * Send a single integer property.
	 *
	 * @param key
	 * @param value
	 */
	public void sendProperty (String key, int value) {
		// Create property communications object and send to server
		String valueStr = String.valueOf(value);
		CommControllerProperty commContProp = new CommControllerProperty (
			CommControllerProperty.TYPE_INT, key, valueStr);
		this.conn.send(commContProp);
	}

	/**
	 * Send a co-ordinate property (x and y integer values).
	 *
	 * @param key
	 * @param x
	 * @param y
	 */
	public void sendProperty (String key, int x, int y) {
		// Create property communications object and send to server
		CommControllerProperty commContProp = new CommControllerProperty (
			CommControllerProperty.TYPE_INT_TWO, key, x + " " + y);
		this.conn.send(commContProp);
	}

	/**
	 * Adapter method for receiving property as a key and value from another client.
	 *
	 * @param key    Key of property to be read from server.
	 * @param value  String value of property to be read from server.
	 */
	public void receiveProperty (String key, String value) {
	    // over written in sub class
	}

	/**
	 * Recieve property as a String key and integer value.
	 *
	 * @param key    Key of property to be read from server.
	 * @param value  Integer value of property to be read from server.
	 */
	public void receiveProperty (String key, int value) {
	    // over written in sub class
	}

	/**
	 * Recieve property from another client as a co-ordinate (x and y integer).
	 *
	 * @param key    Key of property to be read from server.
	 * @param x      Integer x co-ordinate value property to be read from server.
	 * @param y      Integer y co-ordinate value property to be read from server.
	 */
	public void receiveProperty (String key, int x, int y) {
	    // over written in sub class
	}

	/**
	 * Recieve object as XML from a client or server (use this when more advanced
	 * data is required from the server or a client).
	 *
	 * @param message   Receive a more complex XML object from a server.
	 */
	public void receiveObject (XMLElement message) {}

	/**
	 * Return new if it is this players turn.
	 *
	 * @return  True if this current players turn.
	 */
	public boolean isThisPlayersTurn () {
		if (PlayerList.NO_PLAYER.equals (getCurrentPlayer()))
			return false;
		else
			return (getSeatNum() == getCurrentPlayerSeatNum());
	}

	/**
	 * Method which tells the server that it is next players turn.
	 */
	public void nextPlayer () {
		// Create communcations table action of type "Next Player".
		CommNextPlayer nextPlayer = new CommNextPlayer ();

		// Fix for bug 1176951 - "Possibility of consequent moves"
		conn.getTable().getPlayerList().setCurrentPlayer (PlayerList.NO_PLAYER);

		// send to server from client
		this.conn.send (nextPlayer);
	}

	//==========================================================================
	// Empty implementations of "MouseListener"
    //==========================================================================

	/**
	 * Invoked when the mouse enters a JogreComponent.
	 *
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * Invoked when the mouse exits a JogreComponent.
	 *
	 * @param e
	 */
	public void mouseExited(MouseEvent e) {}

	/**
	 * Invoked when the mouse has been clicked on a JogreComponent.
	 *
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {}

	/**
	 * Invoked when a mouse button has been released on a JogreComponent.
	 *
	 * @param e
	 */
	public void mouseReleased (MouseEvent e) {}

	/**
	 * Invoked when a mouse button has been pressed on a JogreComponent.
	 *
	 * @param e
	 */
	public void mousePressed (MouseEvent e) {}

	//==========================================================================
	// Empty implementations of "MouseMotionListener"
    //==========================================================================

	/**
	 * Invoked when the mouse button has been moved on a JogreComponent
	 * (with no buttons no down).
	 *
	 * @param e
	 */
	public void mouseMoved(MouseEvent e) {}

	/**
	 * Invoked when a mouse button is pressed on a JogreComponent and
	 * then dragged.
	 *
	 * @param e
	 */
	public void mouseDragged (MouseEvent e) {}

	//==========================================================================
	// Empty implementations of "KeyListener"
    //==========================================================================

	/**
	 * Invoked when a key has been pressed.
	 *
	 * @param e
	 */
	public void keyPressed(KeyEvent e) {}

	/**
	 * Invoked when a key has been released.
	 *
	 * @param e
	 */
	public void keyReleased(KeyEvent e) {}

	/**
	 * Invoked when a key has been typed.
	 *
	 * @param e
	 */
	public void keyTyped(KeyEvent e) {}
}
