/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.client.awt;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

/**
 * An element used for displaying XML entries in a JTree
 *
 * @author Richard Walter
 * @version Alpha 0.2.3
 */
public class JTreeXMLElement {

	public XMLElement xmlElement;
	private String propertyName;
	public int caretPos;

	/**
	 * Constructor
	 */
	public JTreeXMLElement (XMLElement xmlElement, String propertyName, int caretPos) {
		this.xmlElement = xmlElement;
		this.propertyName = propertyName;
		this.caretPos = caretPos;
	}

	/**
	 * Transform the XML element into a string.
	 * The mapping is the attribute with the given propertyName.
	 */
	public String toString() {
		return (xmlElement.getStringAttribute(propertyName));
	}

}
