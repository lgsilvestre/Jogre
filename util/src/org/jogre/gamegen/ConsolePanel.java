/*
 * JOGRE (Java Online Gaming Real-time Engine) - Generator
 * Copyright (C) 2004 - 2007  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.gamegen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Panel for the administrator console for the server.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class ConsolePanel extends JPanel {

    private Color bgColor = new Color (255, 255, 255);

    private JTextArea textArea;
    
    /**
     * Constructor.
     */
    public ConsolePanel () {
        super (new BorderLayout ());

        setBackground (bgColor);

        textArea = new JTextArea();
        textArea.setFont (
            new Font ("Courier New", java.awt.Font.PLAIN, 11));
		JScrollPane scrollPane = new JScrollPane (textArea);
		add (scrollPane, BorderLayout.CENTER);

		System.setOut( new PrintStream(
			new ConsoleOutputStream (textArea.getDocument (), System.out), true));
		System.setErr( new PrintStream(
			new ConsoleOutputStream (textArea.getDocument (), null), true));
    }    
    
    public void clear () {
    	textArea.setText("");
    }
}

/**
 * Output stream which is associated with the consoel panel.
 */
class ConsoleOutputStream extends OutputStream
{
	private Document document = null;
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
	private PrintStream ps = null;

	public ConsoleOutputStream(Document document, PrintStream ps)
	{
		this.document = document;
		this.ps = ps;
	}

	public void write(int b)
	{
		outputStream.write (b);
	}

	public void flush() throws IOException
	{
		super.flush();

		try
		{
			if (document != null)
			{
				document.insertString (document.getLength (),
					new String (outputStream.toByteArray ()), null);
			}

			if (ps != null)
			{
				ps.write (outputStream.toByteArray ());
			}

			outputStream.reset ();
		}
		catch(Exception e) {}
	}

}