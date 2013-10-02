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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.jogre.server.data.db.IBatis;
import org.jogre.server.data.db.IDatabase;
import org.jogre.webapp.utility.WebAppProperties;

/**
 * Game action class.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public abstract class AbstractJogreAction extends Action {

	// Fields of abstract jogre action
	protected WebAppProperties props = null;
	protected IBatis           ibatis = null;
	protected MessageResources resources = null;
	protected ActionMessages   errors = null;

	/**
	 * Initialise web properties, database connection and resources.
	 */
	public void init(HttpServletRequest request) {
		// Create action message which will contain errors
		this.errors = new ActionMessages ();
		
		// Initiliase fields
		try {
			// Load properties / database connection / resources
			this.props = WebAppProperties.getInstance();
			
			// Init ibatis wrapper and test connection by selecting all snapshots
			this.ibatis = IBatis.getInstance (props.getDatabaseProperties());
			this.ibatis.getList (IDatabase.ST_SELECT_ALL_SNAP_SHOTS);
			
			// Retrieve resources
			this.resources = getResources(request);
		}
		catch (IOException ioEx) {
			errors.add("io.exception", new ActionMessage ("error.exception.io", ioEx.getMessage()));
		}
		catch (SQLException sqlEx) {
			errors.add("sql.exception", new ActionMessage ("error.exception.sql", "No database connection - check database execution status and connection details in \"webapp.properties\" properties file."));
		}
		
		if (errors.size() > 0)
			saveErrors(request, errors);
	}
}