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
package org.jogre.common.util;


/**
 * This class provides an easy and effective point for accessing the main JOGRE
 * resource bundle (system_*_*.properties).
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreLabels extends AbstractProperties {

    // name of the lookandfeel properties file
	private static final String DEFAULT_FILENAME = "labels";
	private static JogreLabels instance = null;

	/**
	 * Private constructor (Can only be called by the getInstance() method.
	 */
	private JogreLabels () {
		super (DEFAULT_FILENAME);
	}

	/**
	 * Accessor to singleton instance of this class.
	 *
	 * @return  Instance of this class.
	 */
	public static JogreLabels getInstance() {
		if (instance == null)
			instance = new JogreLabels ();

		return instance;
	}

	/**
	 * Return an error string from an error number.
	 *
	 * @param errorCode   Error number.
	 * @return            Error description.
	 */
	public static String getError (int errorCode) {
		return getInstance().get ("error." + errorCode);
	}
}