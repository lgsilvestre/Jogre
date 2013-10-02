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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.jogre.client.awt.IJogreClientGUI;
import org.jogre.server.data.SnapShot;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.IJogreWeb;
import org.jogre.webapp.data.OnlineGame;
import org.jogre.webapp.forms.MainForm;
import org.jogre.webapp.utility.WebAppProperties;

/**
 * Main page action.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class MainAction extends AbstractJogreAction {

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
		
		// populate form
		populateForm (mapping, request, form);
		
		// Forward to JSP
		return mapping.findForward (IJogreWeb.FORWARD_MAIN);
	}

	/**
	 * Populate form.
	 * 
	 * @param mapping
	 * @param request
	 * @param form
	 */
	public void populateForm (ActionMapping mapping, HttpServletRequest request, ActionForm form) {
		
		// Retrieve form
		MainForm mainForm = (MainForm)form;
		List topGames = new ArrayList ();
		List newGames = new ArrayList ();

		try {			
			// Check to see if user is requesing a language change
			checkLanguageChange(request, props);
						
			// Select snapshots ordered by users 
			SnapShot parameter = new SnapShot ();
			parameter.setOrderBy("NUM_OF_USERS DESC");    // order by number of users
			List snapshots = ibatis.getList(IDatabase.ST_SELECT_ALL_SNAP_SHOTS, parameter);
			
			// Populate top / new games using snapshot info
			int numOfTopGames = props.getInt (IJogreWeb.PROP_NUM_OF_TOP_GAMES);
			
			// Iterate through snapshots
			for (int i = 0; i < snapshots.size(); i++) {
				SnapShot snapshot = (SnapShot)snapshots.get(i);
				String gameKey = snapshot.getGameKey();
				if (props.isSupportedGame(gameKey)) {
					OnlineGame game = new OnlineGame (snapshot, resources);
					
					// Update "top" games
					if (topGames.size() < numOfTopGames)
						topGames.add (game);
					
					// Update "new" games
					if (props.isNewGame(gameKey))
						newGames.add(game);
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
		
		// Update form
		mainForm.setTopGames(topGames);
		mainForm.setNewGames(newGames);
		mainForm.setGenre(IJogreWeb.GENRE_HOME);
	}

	/**
	 * Checkt to see if the user is requesting a language change.
	 * 
	 * @param request
	 * @param props
	 */
	private void checkLanguageChange(HttpServletRequest request, WebAppProperties props) {
		// Check language change
		String lang = request.getParameter("lang");
		if (lang != null && props.isSupportedLang(lang)) {
			HttpSession session = request.getSession();
			Locale locale = new Locale(lang);
			Locale.setDefault (locale);
			
			// Update request and session object.
			session.setAttribute("org.apache.struts.action.LOCALE", locale);
			request.setAttribute("org.apache.struts.action.LOCALE", locale);
			
			resources = getResources(request);
		}
	}
}