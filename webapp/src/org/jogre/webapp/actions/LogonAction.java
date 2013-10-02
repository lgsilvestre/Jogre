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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.forms.MainForm;

/**
 * Logon action - can also be used for logging off too.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class LogonAction extends MainAction implements IJogreWeb {

	/**
	 * Execute logging in.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward execute (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{		
		// Initialise
		super.init(request);
		if (errors.size() > 0)
			return mapping.findForward (IJogreWeb.FORWARD_ERROR);
		
		// Populate form
		super.populateForm (mapping, request, form);
		
		// Check to see if user is logging on / logging off
		MainForm jogreForm = (MainForm)form;
		HttpSession session = request.getSession();
		String action = jogreForm.getAction();		
		if (session != null) {
			
			// Logging on
			if (IJogreWeb.ACTION_LOGON.equals (action)) {
				session.setAttribute ("username", jogreForm.getUsername());
				return mapping.findForward (IJogreWeb.FORWARD_MAIN);
			} 
			
			// Logging off
			else if (IJogreWeb.ACTION_LOGOFF.equals (action)) {
				session.setAttribute ("username", null);
				jogreForm.setUsername(null);
				jogreForm.setPassword(null);
				return mapping.findForward (IJogreWeb.FORWARD_MAIN);
			}
		}
		
		return mapping.findForward (IJogreWeb.FORWARD_LOGON);
	}
}