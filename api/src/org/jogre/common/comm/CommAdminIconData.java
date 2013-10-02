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
package org.jogre.common.comm;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.jogre.common.util.JogreUtils;

import nanoxml.XMLElement;

/**
 * Communications message for transporting icon graphics. 
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommAdminIconData extends CommGameMessage {

	// Game icons
	private static final String XML_ICON_DATA = "icon_data";
	private static final String XML_ATT_GAME = "game";
	private static final String XML_ATT_ICON_DATA = "icon";
	
	private HashMap iconData;
	
	/**
	 * Constructor which takes a hash map.
	 * 
	 * @param iconData
	 */
	public CommAdminIconData (HashMap iconData) {
		this.iconData = iconData;
	}
	
	/**
	 * Constructor which takes an XMLElement.
	 * 
	 * @param message
	 */
	public CommAdminIconData (XMLElement message) {
		iconData = new HashMap ();
		
		Enumeration e = message.enumerateChildren();
		while (e.hasMoreElements()) {
			XMLElement elm = (XMLElement)e.nextElement();
			String gameId = elm.getStringAttribute(XML_ATT_GAME);
			byte [] bytes = JogreUtils.Jogre64Decode(elm.getStringAttribute(XML_ATT_ICON_DATA));
			
			if (bytes != null)
				iconData.put(gameId, bytes);
			else
				iconData.put(gameId, new byte [] {});
		}
	}
	
	/**
	 * Return icon data.
	 * 
	 * @return
	 */
	public HashMap getIconData () {
		return this.iconData;
	}
	
	/* (non-Javadoc)
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(Comm.ADMIN_ICON_DATA);
		
		Iterator i = iconData.keySet().iterator();
		while (i.hasNext()) {
			String gameId = (String)i.next();
			byte [] bytes = (byte [])iconData.get(gameId);
			
			XMLElement newIconDataElm = new XMLElement (XML_ICON_DATA);
			newIconDataElm.setAttribute(XML_ATT_GAME, gameId);
			newIconDataElm.setAttribute(XML_ATT_ICON_DATA, JogreUtils.Jogre64Encode(bytes));
			message.addChild(newIconDataElm);
		}

		return message;
	}	
}