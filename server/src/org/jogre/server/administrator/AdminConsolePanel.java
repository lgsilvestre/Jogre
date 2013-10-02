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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Panel for the administrator console for the server.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminConsolePanel extends JPanel {

    private Color bgColor = new Color (255, 255, 255);

    /**
     * Consturctor.
     */
    public AdminConsolePanel () {
        super (new BorderLayout ());

        setBackground (bgColor);

        JTextArea textArea = new JTextArea();
        textArea.setFont (
            new Font ("Courier New", java.awt.Font.PLAIN, 11));
		JScrollPane scrollPane = new JScrollPane (textArea);
		add (scrollPane, BorderLayout.CENTER);
		/* FIXME 
		System.setOut( new PrintStream(
			new ConsoleOutputStream (textArea.getDocument (), System.out), true));
		System.setErr( new PrintStream(
			new ConsoleOutputStream (textArea.getDocument (), null), true));
			*/
    }
}
