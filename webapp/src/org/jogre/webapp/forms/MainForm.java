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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.jogre.server.data.User;
import org.jogre.server.data.db.IBatis;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.utility.WebAppProperties;

/**
 * Main JOGRE form object.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class MainForm extends AbstractJogreForm {

	// used for logging in
	private String username;
	private String password;
	private String action;
	
	private List topGames;		// list of the top games (based on number of logged in players)
	private List newGames;		// list of new games
	
	// Info vars
	private int numOfUsers;
	
	private String state = null;
	
	/**
	 * Blank constructor.
	 */
	public MainForm () {}
	
	// Accessors / mutators
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public int getNumOfUsers() {
		return numOfUsers;
	}	

	public void setNumOfUsers(int numOfUsers) {
		this.numOfUsers = numOfUsers;
	}

	public List getTopGames() {
		return topGames;
	}

	public void setTopGames(List topGames) {
		this.topGames = topGames;
	}

	public List getNewGames() {
		return newGames;
	}

	public void setNewGames(List newGames) {
		this.newGames = newGames;
	}

	/**
	 * Validate method - used for logging on purposes.
	 * 
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
    {
		// Create errors object
        ActionErrors errors = new ActionErrors();
        
        if (IJogreWeb.ACTION_LOGON.equals(action)) {			// validate user logging on	        
	        if(username == null || username.equals(""))
	            errors.add("empty.username", new ActionMessage ("error.empty.username"));
	        if(password == null || password.equals(""))
	            errors.add("empty.password", new ActionMessage ("error.empty.password"));
	        if (errors.size() > 0) 
	        	return errors;		// dont access database if simple validation errors
	        
	        try {
				IBatis ibatis = IBatis.getInstance(WebAppProperties.getInstance().getDatabaseProperties());
				User parameterUser = new User (username, password);
				Object user = ibatis.getObject(IDatabase.ST_SELECT_USER, parameterUser);
				if (user == null) 
					errors.add("invalid.logon.details", new ActionMessage ("error.invalid.logon.details"));
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
				errors.add("io.exception", new ActionMessage ("error.exception.io", ioEx.getMessage()));
			} catch (SQLException sqlEx) {
				sqlEx.printStackTrace();
				errors.add("sql.exception", new ActionMessage ("error.exception.sql", sqlEx.getMessage()));
			}
        }

        return errors;
    }
}