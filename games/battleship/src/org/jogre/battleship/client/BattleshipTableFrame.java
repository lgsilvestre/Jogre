/*
 * JOGRE (Java Online Gaming Real-time Engine) - Battleship
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
package org.jogre.battleship.client;

import info.clearthought.layout.TableLayout;

import java.awt.Point;

import nanoxml.XMLElement;

import org.jogre.battleship.common.CommBattleshipMove;
import org.jogre.battleship.common.CommBattleshipPlaceShip;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.JogreUtils;

/**
 * This class holds the battleships MVC classes.
 * 
 * @author Gman, JavaRed
 * @version Alpha 0.2.3
 */
public class BattleshipTableFrame extends JogreTableFrame {
	
	// Declare model, boards and controller
	private BattleshipModel model = null;
	private BattleshipBoardComponent board = null;
	private BattleshipShipComponent myShips = null;
	private BattleshipShipComponent enemyShips = null;
	private BattleshipController controller = null;

	/**
	 * Constructor
	 * 
	 * @param conn
	 */
	public BattleshipTableFrame(TableConnectionThread conn) {
		super(conn);

		// Create model, board, and controller
		this.model = new BattleshipModel();
		this.board = new BattleshipBoardComponent(model);
		this.myShips = new BattleshipShipComponent(model, true);
		this.enemyShips = new BattleshipShipComponent(model, false);
		this.controller = new BattleshipController(model, board, messageComponent);
		
		// Observers
		model.addObserver(board);
		model.addObserver(myShips);
		model.addObserver(enemyShips);

		// Give connection to controller
		controller.setConnection(conn);

		// Give board controller
		board.setController(controller);

		// Set up game panel
		double pref = TableLayout.PREFERRED;
		double size[][] = { { 10, pref, 5, pref, 5, pref, 10 },
				{ 10, pref, 5, pref, pref, 5, pref, 10 } };
		JogrePanel gamePanel = new JogrePanel(size);		
		gamePanel.add (new PlayerComponent (conn, 0), "1,1,l,c");
		gamePanel.add (new PlayerComponent (conn, 1), "1,6,l,c");
		gamePanel.add (new JogreLabel("My Ships"), "1,3,c,c");
		gamePanel.add (new JogreLabel("Enemy Ships"), "5,3,c,c");
		gamePanel.add (myShips, "1,4,c,t");
		gamePanel.add (board, "3,4,c,t");
		gamePanel.add (enemyShips, "5,4,c,t");
		setGamePanel(gamePanel);

		// Set up MVC classes in super class (CONSTRUCTOR MUST CALL THIS)
		setupMVC(model, board, controller);
		
		pack();		
	}
	
	/**
	 * Recieve the table message.
	 * 
	 * NOTE: Older way of doing this - not using controller.
	 * 
	 * @see org.jogre.client.ITable#receiveTableMessage(nanoxml.XMLElement)
	 */
	public void receiveMessage (XMLElement message) {
        String messageType = message.getName();
        
        if (messageType.equals (CommBattleshipMove.XML_NAME)) {
        	CommBattleshipMove move = new CommBattleshipMove (message);
        	int seatNum = move.getSeatNum();
        	model.setMove(seatNum, move.getX(), move.getY());
        } else if (messageType.equals (CommBattleshipPlaceShip.XML_NAME)) {
        	CommBattleshipPlaceShip placeShip = new CommBattleshipPlaceShip (message);
        	int seatNum = placeShip.getSeatNum();
        	Point p = new Point(placeShip.getX(), placeShip.getY());
        	int size = placeShip.getSize();
        	int ship = placeShip.getShip();
        	boolean horizontal = placeShip.isHorizontal();
        	
            model.placeShip(seatNum, p, size, ship, horizontal);
            if (!model.stillPlacingShips() && messageComponent != null) {
                GameLabels labels = GameLabels.getInstance();
                this.messageComponent.receiveMessage(labels.get("game"), labels.get("begin.firing"));
            }       
        }
	}
}