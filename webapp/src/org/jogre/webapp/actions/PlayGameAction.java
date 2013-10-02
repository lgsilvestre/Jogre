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
package org.jogre.webapp.actions;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.jogre.server.data.User;
import org.jogre.server.data.db.IBatis;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.forms.PlayGameForm;
import org.jogre.webapp.utility.WebAppProperties;

/**
 * Play game action class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class PlayGameAction extends AbstractJogreAction {
	
	/**
	 * Execute method.
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{		
		// Initialise
		super.init(request);
		if (errors.size() > 0)
			return mapping.findForward (IJogreWeb.FORWARD_ERROR);
		
		// Check to see if username is null or not
		HttpSession session = request.getSession();
		if (session != null) {
			Object userObj = session.getAttribute("username");
			if (userObj == null)
				return mapping.findForward (IJogreWeb.FORWARD_LOGON);
			else 
				populateForm (form, request, (String)userObj);
		}	
			
		// Figure out which view to return depending on button pressed.
		PlayGameForm gameForm = (PlayGameForm)form;
		if (IJogreWeb.ACTION_NEW_WINDOW.equals(gameForm.getAction()))
			return mapping.findForward (IJogreWeb.FORWARD_PLAY_GAME);			
		return mapping.findForward (IJogreWeb.FORWARD_PLAY_GAME_INTERNAL);
	}
	
	/**
	 * Populate form.
	 * 
	 * @param form
	 * @param request
	 * @param username
	 */
	private void populateForm (ActionForm form, HttpServletRequest request, String username) {
		// Retrieve form
		PlayGameForm gameForm = (PlayGameForm)form;
		ActionMessages errors = new ActionMessages ();
		
		try {			
			// Retrieve attributes
			String gameKey = request.getParameter("gameKey");
			String gameKeySC = gameKey.substring(0, 1).toUpperCase() + gameKey.substring(1);
			String password = ((User)ibatis.getObject(IDatabase.ST_SELECT_USER, new User (username))).getPassword();
			
			// Update form
			gameForm.setGameKey(gameKey);
			gameForm.setGameName(resources.getMessage(gameKey));
			gameForm.setApplet("org.jogre." + gameKey + ".client." + gameKeySC + "Applet.class");
			gameForm.setUsername(username);
			gameForm.setPassword(password);
			gameForm.setServerHost (props.get(IJogreWeb.PROP_JOGRE_SERVER_HOST));
			gameForm.setServerPort (props.get(IJogreWeb.PROP_JOGRE_SERVER_PORT));
			gameForm.setLanguage("en");		// FIXME - retrieve from session
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			errors.add("sql.exception", new ActionMessage ("error.exception.sql", sqlEx.getMessage()));
		}	
		catch (Exception genEx) {
			genEx.printStackTrace();
			errors.add("gen.exception", new ActionMessage ("error.exception.general", genEx.getMessage()));
		}
		
		if (errors.size() > 0)
			saveErrors(request, errors);
	}	
}