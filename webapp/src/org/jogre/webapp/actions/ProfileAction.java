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

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.jogre.server.data.User;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.forms.ProfileForm;

/**
 * Profile action class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class ProfileAction extends AbstractJogreAction {

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
		
		// populate form
		populateForm (mapping, request, form);
		
		return mapping.findForward (IJogreWeb.FORWARD_PROFILE);
	}
	
	/**
	 * Populate form
	 * 
	 * @param mapping
	 * @param request
	 * @param form
	 */
	public void populateForm (ActionMapping mapping, HttpServletRequest request, ActionForm form) {

		// Retrieve form
		ProfileForm profileForm = (ProfileForm)form;		
		String action = profileForm.getAction();
		String username = profileForm.getUsername();

		try {	
			// Update games summaries
			profileForm.setGameSummaries(ibatis.getList(IDatabase.ST_SELECT_ALL_GAME_SUMMARYS, new User (username)));
						
			// Check action
			if (null == action) {		// first time visited page
				
				// Retrieve user details
				User user = (User)ibatis.getObject(IDatabase.ST_SELECT_USER, new User (username));
				if (user != null) {
					profileForm.setUser(user);
					profileForm.setPassword2(user.getPassword());
					profileForm.setSecurityAnswer2(user.getSecurityAnswer());					
				}
			}
			else if (IJogreWeb.ACTION_SUBMIT.equals(action)) {	// update user details
				// Update database				
				User user = profileForm.getUser();
				ibatis.update(IDatabase.ST_UPDATE_USER, user);
				
				// Update form with success and inform user.
				profileForm.setAction (IJogreWeb.ACTION_SUCCESS);
			}
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