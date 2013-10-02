/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
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
package org.jogre.server.administrator;

import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * Class holding admin client graphics information.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class AdminGraphics {

	public static final ImageIcon SERVER_ICON = new ImageIcon ("images/server_icon.gif");
    public static final ImageIcon CONSOLE_ICON = new ImageIcon ("images/console_icon.gif");
    public static final ImageIcon ARROW_LEFT_ICON = new ImageIcon ("images/arrow_left_icon.gif");
    public static final ImageIcon ARROW_RIGHT_ICON = new ImageIcon ("images/arrow_right_icon.gif");
    public static final ImageIcon DATA_ICON = new ImageIcon ("images/data.gif");
    
    protected static HashMap gameIcons = new HashMap ();
    
    /**
     * Return a game icon depending on its gameId.
     * 
     * @param gameId
     * @return
     */
    public static ImageIcon getGameIcon (String gameId) {
    	// Retrieve the icon for the game
	    Object obj = gameIcons.get (gameId);
	    if (obj != null) {
	    	return (ImageIcon)obj; 
	    }
	    else  {
	        // Wasn't in the map, so try to get the data 
	    	ImageIcon icon = null;
	        try {
	        	byte [] iconData = (byte [])JogreServerAdministrator.getInstance().getIconData().get(gameId);
	        	icon = new ImageIcon (iconData);
	        } catch (NullPointerException e) {
	        }

	        // If we now have the icon, then add it to the hashmap for next time.
	        if (icon != null) {
	            gameIcons.put (gameId, icon);
	        }
	        return icon;
	    }	    
    }
}
