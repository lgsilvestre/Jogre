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

import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import org.jogre.client.JogreController;

/**
 * <p>This class creates the view in the JOGRE MVC (model/view/controller)
 * architecture.  This class extends a JComponent and also implements the
 * Observer interface. By implementing the Observer interface a change in the
 * model (JogreModel) which calls the JogreModel.refreshObservers () method will
 * automatically repaint this class.</p>
 * <p>Mouse/keys events can listened on using a JogreController which is set
 * using the setController method of this class.</p>
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public abstract class JogreComponent extends JComponent implements Observer {

	/**
	 * Set the controller by adding a JogreController which implements a
	 * MouseListener, a MouseMotionListener and a KeyListener.
	 *
	 * @param controller   Specified controller e.g. ChessController
	 */
	public void setController (JogreController controller) {
		addMouseListener (controller);
		addMouseMotionListener (controller);
		addKeyListener (controller);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update (Observable observerable, Object args) {
		repaint ();
	}
}
