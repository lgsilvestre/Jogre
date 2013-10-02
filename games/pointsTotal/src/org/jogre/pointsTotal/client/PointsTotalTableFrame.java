/*
 * JOGRE (Java Online Gaming Real-time Engine) - PointsTotal
 * Copyright (C) 2004 - 2007  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.pointsTotal.client;

import nanoxml.XMLElement;

import info.clearthought.layout.TableLayout;

import org.jogre.pointsTotal.common.PointsTotalModel;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;

import javax.swing.BorderFactory;
import java.awt.Color;

/**
 * Game table for a PointsTotal.  This class holds the MVC class for a
 * game of pointsTotal.  The MVC classes are PointsTotalModel, PointsTotalComponent
 * and PointsTotalController respectively.
 *
 * @author  Richard Walter
 * @version Beta 0.3
 */
public class PointsTotalTableFrame extends JogreTableFrame {

    // Declare MVC classes
    private PointsTotalModel      pointsTotalModel;      // model
    private PointsTotalComponent  pointsTotalComponent;  // view
    private PointsTotalSelectionComponent pointsTotalSelectionComponent;
    private PointsTotalController pointsTotalController; // controller
    private PointsTotalSelectionController pointsTotalSelectionController;

    /**
     * Constructor which sets up the MVC classes and takes
     * table connection.
     *
     * @param conn
     * @param tableNum
     */
    public PointsTotalTableFrame (TableConnectionThread conn)
    {
        super (conn);

        // Get parameters from the table properties
        int numPlayers = table.getNumOfPlayers();

        // Initialise MVC classes
        this.pointsTotalModel = new PointsTotalModel (numPlayers);
        this.pointsTotalComponent = new PointsTotalComponent (pointsTotalModel);
        this.pointsTotalSelectionComponent = new PointsTotalSelectionComponent (pointsTotalModel);
        this.pointsTotalController = new PointsTotalController (pointsTotalModel, pointsTotalComponent, pointsTotalSelectionComponent);
        this.pointsTotalSelectionController = new PointsTotalSelectionController (pointsTotalModel, pointsTotalSelectionComponent, pointsTotalComponent);

        // The components observe the model
        this.pointsTotalModel.addObserver(this.pointsTotalComponent);
        this.pointsTotalModel.addObserver(this.pointsTotalSelectionComponent);

        // Set client/server connection on controller
        this.pointsTotalController.setConnection (conn);
        this.pointsTotalSelectionController.setConnection (conn);

        // Enable main view to recieve user input (e.g. mouse clicks) by setting controller
        this.pointsTotalComponent.setController (pointsTotalController);
        this.pointsTotalSelectionComponent.setController (pointsTotalSelectionController);

        // Set game data and controller (constructor must always call these)
        setupMVC (pointsTotalModel, pointsTotalComponent, pointsTotalController);

        // Create game panel and add main view to it
        double pref = TableLayout.PREFERRED;
        double [][] gameSizes = {{pref, pref}, {pref}};
        JogrePanel gamePanel = new JogrePanel (gameSizes);
        gamePanel.add (pointsTotalComponent, "0,0,l,c");
        gamePanel.add (pointsTotalSelectionComponent, "1,0,r,c");
        gamePanel.setBorder (BorderFactory.createLineBorder (Color.black, 3));

        // Create overall panel with the game panel & the player components.
        double [][] sizes = {{10, pref, 10}, {10, pref, 5, pref, 5, pref, 10}};
        JogrePanel panel = new JogrePanel (sizes);
        panel.add (new PlayerComponent (conn, 0, true), "1,1,l,c");
        panel.add (new PlayerComponent (conn, 1, false), "1,1,r,c");
        panel.add (new PlayerComponent (conn, 2, true), "1,5,l,c");
        panel.add (new PlayerComponent (conn, 3, false), "1,5,r,c");
        panel.add (gamePanel, "1,3,c,c");

        // Set game panel
        setGamePanel (panel);

        pack();
    }

    /**
     * Override to ensure that the player is seated in the correct position.
     *
     * @param tableAction
     */
    public void startGame () {
        super.startGame ();

        // Tell the piece selection component what our seat number is so that
        // it can draw the columns in the right order.
        int mySeatNum = pointsTotalController.getSeatNum();
        pointsTotalComponent.setMySeatNumber(mySeatNum);
        pointsTotalSelectionComponent.setMySeatNumber(mySeatNum);
    }

    /**
     * Override receiveMessage so that we can send messages to the controller.
     */
    protected void receiveMessage (XMLElement message) {
	    pointsTotalController.receiveTableMessage(message);
    }
}
