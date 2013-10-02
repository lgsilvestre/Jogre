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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.jogre.server.data.User;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.forms.RegisterForm;

/**
 * Game action class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class RegisterAction extends AbstractJogreAction {

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
		
		return mapping.findForward (IJogreWeb.FORWARD_REGISTER);
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
		RegisterForm registerForm = (RegisterForm)form; 
		String action = registerForm.getAction();

		try {		
			// Check action
			if (null == action) {		// first time visited page
				registerForm.setSecurityQuestion(0);
			}
			else if (IJogreWeb.ACTION_SUBMIT.equals(action)) {
				// register validation successful. Ensure user doesn't already exist.
				String username = registerForm.getUsername();
				
				if (ibatis.getObject(IDatabase.ST_SELECT_USER, new User (username)) == null) {		// OK to register
					
					// Retrieve user object from form and update database
					User newUser = registerForm.getUser();
					ibatis.update(IDatabase.ST_ADD_USER, newUser);
					
					// Update form with success and inform user.
					registerForm.setAction(IJogreWeb.ACTION_SUCCESS);
					registerForm.setSecurityQuestionText(resources.getMessage("security.question." + registerForm.getSecurityQuestion()));
				}
				else {
					errors.add ("username.already.registered", new ActionMessage ("error.username.already.registered", username));
				}
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