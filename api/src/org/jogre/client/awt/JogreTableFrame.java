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
package org.jogre.client.awt;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import nanoxml.XMLElement;

import org.jogre.client.ITable;
import org.jogre.client.JogreController;
import org.jogre.client.TableConnectionThread;
import org.jogre.common.GameOver;
import org.jogre.common.IGameOver;
import org.jogre.common.JogreModel;
import org.jogre.common.Player;
import org.jogre.common.Table;
import org.jogre.common.TableList;
import org.jogre.common.TransmissionException;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommChatTable;
import org.jogre.common.comm.CommControllerObject;
import org.jogre.common.comm.CommControllerProperty;
import org.jogre.common.comm.CommExitTable;
import org.jogre.common.comm.CommGameOver;
import org.jogre.common.comm.CommInvite;
import org.jogre.common.comm.CommJoinTable;
import org.jogre.common.comm.CommOfferDraw;
import org.jogre.common.comm.CommReadyToStart;
import org.jogre.common.comm.CommSitDown;
import org.jogre.common.comm.CommStandUp;
import org.jogre.common.comm.CommStartGame;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;
import org.jogre.common.util.JogreLabels;

/**
 * <p>The important JogreTableFrame which is where each game is played.  Each time
 * a new table is created one of these frames appear.  This class creates the
 * buttons at the top of the screen, the player list at the bottom left and the
 * message chat box on the bottom right. The main game area is set in the sub
 * classes e.g. ChessTableFrame using the setGamePanel (JPanel) method.</p>
 *
 * <p>To use this class a class must extend it e.g. ChesTableFrame.  Its fields
 * must include a data model (JogreModel e.g. ChessModel), a view of the data
 * (JogreComponent e.g. ChessBoardComponent) and a controller (JogreController
 * e.g. ChessController) for understanding mouse movements etc on the view.</p>
 *
 * <p>The constructor in the sub class should initialise these MVC fields and
 * MUST call the setMVC (model, view, controller) method as this class
 * makes use of these.</p>
 *
 * @author  Bob Marks
 * @author  Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public abstract class JogreTableFrame extends JFrame 
                                      implements ITable,
                                                 Observer {
    
	// Declare constants
	private static final int WIDTH = 600;
	private static final int HEIGHT = 500;
	private static final int SPACING = 5;
	private static final Font  GAME_OVER_FONT = new Font ("Arial", Font.BOLD, 20);
	private static final Color GAME_OVER_COLOUR = new Color (200, 0, 0);

	private   String                 title;			        // title of this frame
	protected String                 username;		        // Username from conn.getUsername();
	protected TableConnectionThread  conn = null;           // Connection
	protected int 					 tableNum;			    // number of this table
	protected JogreModel 			 gameModel = null;		// Client gameModel
	protected JogreController        gameController = null; // GameController
	protected JogreComponent 		 gameMainView = null;	// main view
	protected Table                  table = null;
	protected Player                 player = null;
	
	// Link to table hash in JogreClientFrame
	private HashMap tableFramesHash = null;
	
	// Declare GUI components
	private JogreGlassPane        glassPane;    
	private JogrePanel 			  gamePanel, topPanel;
	protected ChatTableComponent  messageComponent;
	protected JogreButton         sitButton, standButton, startButton;
	protected JogreButton         offerDrawButton, resignButton, inviteButton;
	private JAvailableSeats       availablePlayers;
	private JTablePlayers         tablePlayers;

	/**
	 * Constructor to a standard JogreGameFrame.  NOTE: Every contructor of a
	 * sub class of this must call the setMVC (model, view, controller) method.
	 *
	 * @param conn	 Client connection thread which holds the link to the
	 *               server, user and table lists.
     * @param width  Width of the table frame.
     * @param height Height of the table frame.  
	 */
	public JogreTableFrame (TableConnectionThread conn, int width, int height) {
		// Set fields
		this.conn = conn;
		
		// Set convience fields
		this.tableNum = conn.getTableNum();
		this.table    = conn.getTable();
		this.username = conn.getUsername();
		this.player   = this.table.getPlayerList().getPlayer(username);

		// Set title
		this.title = GameLabels.getTableTitle () + " " +
		             tableNum + " - " +
		             JogreLabels.getInstance().get("player") + ": " +
		             conn.getUsername();

		// Set title of frame
		setTitle (title);

		// Set up GUI
		setUpGUI ();
		
		// Set image icon
		setIconImage(GameImages.getImage("game.icon"));

		// Add listeners
		addListeners ();
        
        // Set size of the frame
        if (width != -1 && height != -1)
            setSize (new Dimension (width, height));        
	}

    /**
     * Constructor which doesn't take a width and height.
     * 
     * @param conn   Table connection thread.
     */
    public JogreTableFrame (TableConnectionThread conn) {
        this (conn, -1, -1);
    }
    
	/**
	 * Set up the standard GUI elements
	 */
	private void setUpGUI () {		
		
		// Retrieve labels resources
		JogreLabels resources = JogreLabels.getInstance();
		
		// Set background colour
		getContentPane().setBackground (GameProperties.getBackgroundColour());
		
		// Create glass pane
		this.glassPane = new JogreGlassPane ();
		getRootPane().setGlassPane (glassPane);

		// Create main panel
		double pref = TableLayout.PREFERRED, fill = TableLayout.FILL;
		double [][]sizes = {{fill},{50, pref, fill, pref}};
		JogrePanel mainPanel = new JogrePanel (sizes);	
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		
		// Create top panel
		sizes = new double [][] {{pref, SPACING, pref},{pref}};
		topPanel = new JogrePanel (sizes);
		
		// Create game panel
		gamePanel = new JogrePanel (new BorderLayout());	

		// Create bottom panel (users cannot add to this). 
		sizes = new double [][] {{0.3, 0.7},{100}};
		JogrePanel bottomPanel = new JogrePanel (sizes);

		// Create buttons
		sizes = new double [][] {{SPACING, pref, SPACING, pref, SPACING, pref, SPACING, pref, SPACING}, {SPACING, pref, SPACING}};
		JogrePanel firstPanel = new JogrePanel (sizes);
		sizes = new double [][] {{SPACING, pref, SPACING, pref, SPACING, pref, SPACING, pref, SPACING}, {SPACING, pref, SPACING}};
		JogrePanel secondPanel = new JogrePanel (sizes);
		firstPanel.setBorder(BorderFactory.createEtchedBorder());
		secondPanel.setBorder(BorderFactory.createEtchedBorder());

		// Set up available players component
		availablePlayers = new JAvailableSeats (player, table.getPlayerList(), table);
		
		// Set up buttons
		sitButton = new JogreButton (resources.get("sit"));
		standButton = new JogreButton (resources.get("stand")); 
		startButton = new JogreButton (resources.get("start"));
		offerDrawButton = new JogreButton (resources.get("offer.draw"));
		resignButton = new JogreButton (resources.get("resign"));
		inviteButton = new JogreButton (resources.get("invite"));

		// Add to subPanels
		firstPanel.add (new JLabel (resources.get("playing.as")), "1,1");
		firstPanel.add (availablePlayers, "3,1");
		firstPanel.add (sitButton, "5,1");
		firstPanel.add (standButton, "7,1");
		secondPanel.add (startButton, "1,1");
		secondPanel.add (offerDrawButton, "3,1");
		secondPanel.add (resignButton, "5,1");
		secondPanel.add (inviteButton, "7,1");

		topPanel.add (firstPanel, "0,0");
		topPanel.add (secondPanel, "2,0");

		// Create message component
		messageComponent = new ChatTableComponent (conn, 3);

        // Create table players
		tablePlayers = new JTablePlayers (table.getPlayerList());
		JogrePanel tablePlayersPanel = new JogrePanel (new double [][] {{fill}, {fill}});
		tablePlayersPanel.setBorder(
			BorderFactory.createTitledBorder (
				BorderFactory.createEtchedBorder(), 
				JogreLabels.getInstance().get("user.list")
			)
		);
		JogreScrollPane tablePlayersScroll = new JogreScrollPane (tablePlayers);
		tablePlayersPanel.add (tablePlayersScroll, "0,0");
		        
		// Add these components to bottom panel
		bottomPanel.add (tablePlayersPanel, "0,0");
		bottomPanel.add (messageComponent, "1,0");

		// Add to main panel
		mainPanel.add ( new JogreTitlePanel (), "0,0");
		mainPanel.add (topPanel,                "0,1");
		mainPanel.add (gamePanel,               "0,2");
		mainPanel.add (bottomPanel,             "0,3");

		// Add the main panel to the content pane
		getContentPane().add(mainPanel);

		// Refresh state of buttons
		updateGuiStates ();
		
        Dimension minSize = this.getPreferredSize();
        Dimension size = new Dimension
        (
            Math.max((int)minSize.getWidth(), WIDTH),
            Math.max((int)minSize.getHeight(), HEIGHT)
        );
		setSize(size);
		Dimension screenSize = getToolkit().getScreenSize();
		this.setLocation ((int)screenSize.getWidth() / 2 - this.getWidth() / 2, (int)screenSize.getHeight()/2 - this.getHeight()/2);
		setVisible (true);
	}

	/**
	 * Add listeners
	 */
	private void addListeners () {
		//add close listener to frame
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {
				leaveTable();
			}
		});

		// Add listener for the sit button
		sitButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				sitDown ();
			}
		});

		// Add listener for the stand button
		standButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				standUp ();
			}
		});

		// Add listener for
		startButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				readyToStart();
			}
		});

		// Add listener for the offer draw button
		offerDrawButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				offerDraw ();
			}
		});

		// Add listener for resign button
		resignButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				resign ();
			}
		});
		
		// Add listener for invite button
		inviteButton.addActionListener ( new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				invite ();
			}
		});
		
		// Add listeners on data
		table.addObserver (this);					// 1) Table observer
		table.getPlayerList().addObserver(this);	// 2) Player list observer 
	}

	/**
	 * Set the link to the table frames hash in the JogreClientFrame (must
	 * be deleted when the client is removed).
	 * 
	 * @param tableFramesHash
	 */
	public void setTableFramesHash (HashMap tableFramesHash) {
	    this.tableFramesHash = tableFramesHash;
	}
	
	/**
	 * Player leaves the table.
	 */
	public void leaveTable () {
	    // Check that the controller isn't null
        if (gameController != null) {
            
            // Check to see if the game is playing or not
            if (gameController.isGamePlaying() && gameController.getSeatNum() != Player.NOT_SEATED) {        
			    if (JOptionPane.showConfirmDialog (
			            this, 
			            "You cannot leave a table in progress.  Do you wish to resign?", 
			            "Close Table", 
			            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
			    {		 			        
			        // Send resign method to server
					CommGameOver commGameOver = new CommGameOver (IGameOver.USER_RESIGNS);
					conn.send (commGameOver);		
                    
					// Now close the frame and removed any references to it
                    closeTableFrame ();
			    }
			    return;
			}
        }
        
        // Now close the frame and removed any references to it
        closeTableFrame ();
	}

	/**
	 * Close the table frame.
	 */
	private void closeTableFrame () {
	    int tableNum = table.getTableNum();
	    
	    // Remove table from table hash in JogreClientFrame
	    TableList tableList = conn.getTableList();
	    tableFramesHash.remove (new Integer (tableNum));
	    tableList.removePlayer (tableNum, username);
	    tableList.refreshObservers();
	    
	    // Inform other clients at table
	    CommExitTable commExitTable = new CommExitTable ();
		conn.send (commExitTable);
	    
	    // Set visible equal to false and dispose this frame
	    setVisible (false);
        dispose ();
	}
	
	/**
	 * Set the empty panel in the screen equal to the game panel.
	 *
	 * @param gamePanel
	 */
	public void setGamePanel (JogrePanel gamePanel) {
		// Set up game panel (user must create this) in the middle
		this.gamePanel.add (gamePanel, BorderLayout.CENTER);
		validate ();
		invalidate();
	}
	
	/**
	 * Method to add additional components (such as buttons etc) to the top
	 * right hand side of the screen.
	 *
	 * @param component
	 */
	public void addToTopPanel (Component component) {
		topPanel.add (component);
	}

	/**
	 * Accessor for the game controller.
	 * @return
	 */
	public JogreController getController () {
		return gameController;
	}

	/**
	 * Accessor for the game controller.
	 *
	 * @return
	 */
	public JogreModel getModel () {
		return gameModel;
	}

	/**
	 * Accessor for the small message box with each table.
	 *
	 * @return
	 */
	public ChatGameComponent getMessageComponent() {
		return messageComponent;
	}
	
	/**
	 * Get invite dialog
	 */
	public JogreInviteDialog getInviteDialog() {
        return new JogreInviteDialog(this, JogreLabels.getInstance().get("invite"), true, conn);
	}
	
	/**
	 * Return glass pane.
	 * 
	 * @return
	 */
	public JogreGlassPane getJogreGlassPane () {
		return this.glassPane;
	}
	
	/**
	 * Set the jogre glass pane.
	 * 
	 * @param glassPane
	 */
	public void setJogreGlassPane (JogreGlassPane glassPane) {
		this.glassPane = glassPane;
	}	

	/**
	 * Update the states of the buttons (done on a data change).
	 */
	private void updateGuiStates () {
	    if (table != null && player != null) {	        
			// Update the actual button
			sitButton.setEnabled (player.canSit(table));
			availablePlayers.setEnabled(player.canSit(table));
			standButton.setEnabled (player.canStand(table));
			startButton.setEnabled (player.canStart(table, conn.getGame()));
			offerDrawButton.setEnabled (player.canOfferDrawResign());
			resignButton.setEnabled (player.canOfferDrawResign());
			
			JogreLabels labels = JogreLabels.getInstance();
			if (player.isSeated() && !player.canStart(table, conn.getGame()))
				glassPane.display(labels.get("waiting.on.other.players.to.sit"));
			else if (player.canStart(table, conn.getGame()))
				glassPane.display(labels.get("click.start.to.begin.game"));
			else if (player.isReady() && !table.isGamePlaying())
				glassPane.display(labels.get("waiting.on.other.players.to.start"));
			else
				glassPane.setVisible(false);
	    }
	}

    /**
     * Implementation of the ITable interface.
     *
     * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
     */
    public void receiveTableMessage (XMLElement message, int tableNum) {
        try {
	        String messageType = message.getName();
	       
	        if (messageType.equals (Comm.START_GAME)) {
			    startGame (new CommStartGame (message));
			}
			else if (messageType.equals(Comm.GAME_OVER)) {
			    gameOver (new CommGameOver (message));
			}
			else if (messageType.equals(Comm.OFFER_DRAW)) {
			    receiveOfferDraw (new CommOfferDraw (message));
			}
			else if (messageType.equals(Comm.CONTROLLER_PROPERTY)) {
				receiveProperty (new CommControllerProperty (message));
			}
			else if (messageType.equals(Comm.CONTROLLER_OBJECT)) {
			    receiveObject (new CommControllerObject (message));
			}
			else if (messageType.equals(Comm.CHAT_TABLE)) {
			    CommChatTable commMessage = new CommChatTable (message);
			    receiveChatTableMessage (commMessage.getUsername(), commMessage.getTableNum(), commMessage.getChat());
			}
			else if (messageType.equals(Comm.JOIN_TABLE)) {
				CommJoinTable commJoinTable = new CommJoinTable (message);
				if (commJoinTable.containsModel())					
					gameModel.setState(commJoinTable.getModelState());
			}   

			// Also pass message into adapter method which all JOGRE clients
			// can receive to do additional processing if this is required.			
			receiveMessage (message);
        }
        catch (TransmissionException transEx) {}
    }

    /**
     * Adapter method for receiving a custom message.
     *
     * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
     */
    protected void receiveMessage (XMLElement message) {}
	
	/**
	 * Receive start game message from server.  Change status of players, reset current 
	 * player (for turn based games), call controller.start() and startGame() on 
	 * any sub class of this class.
	 * 
	 * @param commStartGame     
	 */
	private void startGame (CommStartGame commStartGame) {	    
	    // Start controller
		this.gameController.start ();		// Controller is also informed game is started
		
		// Delegate to custom startGame () method in client if required
		this.startGame ();
	}
	
	/**
	 * Start the game (Empty adapter method).
	 */
	public void startGame () {}
	
	/**
	 * This client wishes to sit down at a particular table.
	 */
	private void sitDown () {
		int selectedSeat = availablePlayers.getSelectedSeat();

		// Create table action communications object and send to server
		CommSitDown commSitDown = new CommSitDown (selectedSeat);
		conn.send (commSitDown);
	}
	
	/**
	 * Stand back up again (status of player becomes VIEWING).
	 */
	private void standUp () {
		// Set the status of the player back to VIEWING.
	    CommStandUp commStandUp = new CommStandUp ();

		conn.send (commStandUp);
	}
	
	/**
	 * Player clicks the start button.
	 */
	private void readyToStart () {
		CommReadyToStart commReadyToStart = new CommReadyToStart ();
		conn.send (commReadyToStart);
	}

	/**
	 * Offer the user a draw.
	 */
	private void offerDraw () {
		// Create offer draw communications object and send to server.
		CommOfferDraw commOfferDraw = new CommOfferDraw (
			CommInvite.REQUEST);
		conn.send (commOfferDraw);
	}

	/**
	 * Receive offer draw.
	 * 
	 * @param offerDraw
	 */
	private void receiveOfferDraw (CommOfferDraw offerDraw) {
		JogreLabels jogreLabels = JogreLabels.getInstance();

		// Check to see if this is an offer draw or an offer draw reply
		if (offerDraw.getStatus() == CommInvite.REQUEST) {

			String message =
				jogreLabels.get ("player") + " " + offerDraw.getUsername() + " " +
				jogreLabels.get ("offers.a.draw") + " " +
				jogreLabels.get ("accept") + "?";

			int answer = JOptionPane.showConfirmDialog (
				this,
				message,
				jogreLabels.get("table.message"),
				JOptionPane.YES_NO_OPTION
			);

			// Convert to a number the CommInvite object understands
			int yesNo = (answer == JOptionPane.YES_OPTION) ?
				CommInvite.ACCEPT : CommInvite.DECLINE;

			// Create invite comm object to reply to the users
			CommOfferDraw offerDrawReply = new CommOfferDraw (
				yesNo, offerDraw.getUsername());
			offerDrawReply.setSerialNum(offerDraw.getSerialNum());

			// Transmit message back to the server
			conn.send (offerDrawReply);
		}
		else {			// is an offer draw reply from a user
			if (offerDraw.getStatus() == CommInvite.DECLINE) {

				// report message that user has declined the offer
				String messageTitle =
					"User " + offerDraw.getUsername() + " " +
					jogreLabels.get ("has.rejected.draw.offer") + ".";

				JOptionPane.showMessageDialog (
					this,
					messageTitle,
					jogreLabels.get("table.message"),
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}
	
	/**
	 * Resign from this particular game.
	 */
	private void resign () {
		// Player resigns
		CommGameOver commGameOver = new CommGameOver
			(IGameOver.USER_RESIGNS);
		conn.send (commGameOver);
	}
	
	/**
	 * Pop up invite dialog and invite someone to the table
	 */
	private void invite () {
		this.getInviteDialog();
	}

	/**
	 * Sets up the MVC for this table.  This method <b>must</b> be called from
	 * the constructor of any sub class of this class as this class refers to
	 * the JogreModel and JogreController.
	 *
	 * @param model
	 * @param mainView
	 * @param controller
	 */
	public void setupMVC (JogreModel model, JogreComponent mainView, JogreController controller) {
		this.gameModel = model;
		this.gameMainView = mainView;
		this.gameController = controller;
	}

	/**
	 * Create game over message and add to chat box.
	 *  
	 * @param commGameOver
	 */
	private void gameOver (CommGameOver commGameOver) {
		// Create game over message
		JogreLabels labels = JogreLabels.getInstance();
		StringBuffer fromSB, ratingsSB;
		
		// Depending on the status create the game over message
		GameOver gameOver = commGameOver.getGameOver();
		String [] players = gameOver.getPlayers();
		int [] results    = gameOver.getResults(); 
	    int [] oldRatings = gameOver.getOldRatings();
	    int [] newRatings = gameOver.getNewRatings();
		
	    // Loop through the players
	    for (int i = 0; i < players.length; i++) {
			int result = results [i];

			// Create from message
			fromSB = new StringBuffer (
				labels.get ("player") + " " + players[i] + " ");			
			if (result == IGameOver.DRAW) 
				fromSB.append (labels.get ("draws"));
			else if (result == IGameOver.LOSE) 
				fromSB.append (labels.get ("loses"));
			else if (result == IGameOver.WIN) 
				fromSB.append (labels.get ("wins"));
			
			// Create old / new ratings
			ratingsSB = new StringBuffer (
				labels.get("old.rating") + " " + oldRatings[i] + " " +
				labels.get("new.rating") + " " + newRatings[i]);
			
			// Write to the message component
			messageComponent.receiveMessage (fromSB.toString(), ratingsSB.toString());
		}
	    	    
	    // Display "Game Over" text in glass pane
	    StringBuffer gameOverSB = new StringBuffer (labels.get("game.over"));
	    int seatNum = player.getSeatNum(); 
	    if (seatNum >= 0 && seatNum < players.length) {
	    	if (results[seatNum] == IGameOver.DRAW)
	    		gameOverSB.append(" - " + labels.get("draw"));
	    	else if (results[seatNum] == IGameOver.LOSE)
	    		gameOverSB.append(" - " + labels.get("you.lose"));
	    	else if (results[seatNum] == IGameOver.WIN)
	    		gameOverSB.append(" - " + labels.get("you.win"));
	    }
	    glassPane.display(GAME_OVER_FONT, GAME_OVER_COLOUR, gameOverSB.toString());
	}
    
    /**
	 * Receive a property.
	 *
	 * @param commProperty
	 */
	private void receiveProperty(CommControllerProperty commProperty) {
		// retrieve controller from the correct table.
		JogreController controller = getController();

		switch (commProperty.getStatus()) {
			case CommControllerProperty.TYPE_STRING:
				controller.receiveProperty (commProperty.getKey(), commProperty.getValue());
				return;

			case CommControllerProperty.TYPE_INT:
				int value = Integer.parseInt (commProperty.getValue());
				controller.receiveProperty (commProperty.getKey(), value);
				return;

			case CommControllerProperty.TYPE_INT_TWO:
				StringTokenizer st = new StringTokenizer (commProperty.getValue());
				int value1 = Integer.parseInt (st.nextToken());
				int value2 = Integer.parseInt (st.nextToken());
				controller.receiveProperty (commProperty.getKey(), value1, value2);
				return;
		}
	}

	/**
	 * Receive a message for a specificied table.
	 *
	 *
	 * @param usernameFrom
	 * @param tableNum
	 * @param messageText
	 */
	private void receiveChatTableMessage (String usernameFrom, int tableNum, String messageText) {
		getMessageComponent().receiveMessage(usernameFrom, messageText);
	}

	/**
	 * Retrieve controller object.
	 *
	 * @param commObject
	 */
	private void receiveObject (CommControllerObject commObject) {
		JogreController controller = getController();
		controller.receiveObject (commObject.getData());
	}
		
	/**
	 * Update and refresh the buttons when the data changes.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable obs, Object obj) {
	    updateGuiStates ();
	}
}
