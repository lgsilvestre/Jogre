/*
 * JOGRE (Java Online Gaming Real-time Engine) - Webapp
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
package org.jogre.webapp.forms;

import org.apache.struts.action.ActionForm;
import org.jogre.webapp.IJogreWeb;

/**
 * Abstract JOGRE action form which holds data common to all forms.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class AbstractJogreForm extends ActionForm {

	// Declare abstract fields
	protected String genre = null;	
	protected String action = null;
	protected String gameKey = null;
	
	/**
	 * Empty constructor.
	 */
	public AbstractJogreForm () {
		this.genre = IJogreWeb.GENRE_DEFAULT;
	}
	
	// Accessors / mutators
	public String getGenre() {
		return genre;
	}
	
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getGameKey() {
		return gameKey;
	}
	
	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}
}