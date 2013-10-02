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
package org.jogre.server;

import java.util.Locale;

import org.jogre.common.util.AbstractProperties;

/**
 * This class provides an easy and effective point for accessing to each game
 * resource bundle (server_labels_*_*.properties).
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ServerLabels extends AbstractProperties {

    // name of the lookandfeel properties file
    private static final String DEFAULT_FILENAME = "server_labels";
    private static final String SERVER_TITLE     = "jogre.server.title";
    
    private static ServerLabels instance;
    
    /**
     * Singleton constructor for the server labels.  Looks for a 
     * server_labels_locale.properites file.
     * 
     * @param filename
     */
    private ServerLabels () {
        super (DEFAULT_FILENAME);
    }
    
    /**
     * Accessor to singleton instance of this class.
     *
     * @return
     */
    public static ServerLabels getInstance() {
        if (instance == null)
            instance = new ServerLabels();

        return instance;
    }
        
    /**
     * Return the server title.
     *
     * @return    Server title.
     */
    public static String getServerTitle () {
        return getInstance().get (SERVER_TITLE);
    }
}
