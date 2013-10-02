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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.jogre.server.data.SnapShot;
import org.jogre.server.data.db.IBatis;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.data.OnlineGame;
import org.jogre.webapp.forms.GameForm;
import org.jogre.webapp.utility.WebAppProperties;

/**
 * Game action class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GameAction extends AbstractJogreAction {

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
		
		// Populate form
		populateForm(form, request);
		
		return mapping.findForward (IJogreWeb.FORWARD_GAME);
	}

	/**
	 * Populate form.
	 * 
	 * @param form
	 * @param request
	 */
	private void populateForm (ActionForm form, HttpServletRequest request) {
		// Retrieve form
		GameForm gameForm = (GameForm)form;
		
		try {
			String gameKey = gameForm.getGameKey();
			SnapShot snapshot = (SnapShot)ibatis.getObject(IDatabase.ST_SELECT_SNAP_SHOT, new SnapShot(gameKey));	
			gameForm.setOnlineGame(new OnlineGame (snapshot, resources));
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