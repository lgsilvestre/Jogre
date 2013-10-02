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

import org.jogre.client.TableConnectionThread;
import org.jogre.common.comm.CommChatTable;

/**
 * Component for a chat table component.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class ChatTableComponent extends ChatGameComponent {

    // This class uses a table connection thread.
    private TableConnectionThread conn;

    /**
     * Constructor for a chat table component
     *
     * @param height
     */
    public ChatTableComponent (TableConnectionThread conn, int height) {
        super(conn.getClientConnectionThread(), height);

        this.conn = conn;
        this.usernameFrom = conn.getUsername();
    }

    /**
	 * Overloaded version of the sendMessage method which uses a
	 * TableConnectionThread instead of a ClientConnectionThread.
	 */
	protected void sendMessage () {
		String messageText = this.messageInput.getText();

		if (this.conn != null && !messageText.equals("")) {
		    this.messageOutput.append (this.usernameFrom + ": " + messageText + "\n");
		    this.conn.send (new CommChatTable (messageText));

		    this.messageInput.setText("");
		}
	}
}
