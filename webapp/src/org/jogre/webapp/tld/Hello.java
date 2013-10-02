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
package org.jogre.webapp.tld;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * This is a simple tag example to show how content is added to the
 * output stream when a tag is encountered in a JSP page. 
 * 
 * @author  Bob Marks
 * @version Beta 0.3 
 */
public class Hello extends TagSupport {
	
	private String name = null;
	
	/**
	 * Getter/Setter for the attribute name as defined in the tld file 
	 * for this tag
	 */
	public void setName(String value){
		name = value;
	}

	public String getName(){
		return(name);
	}
	
	/**
	 * doStartTag is called by the JSP container when the tag is encountered
	 */
	public int doStartTag() {
		try {
			JspWriter out = pageContext.getOut();
			out.println ("<table border=\"1\">");
			if (name != null)
				out.println ("<tr><td> Hello " + name + " </td></tr>");
			else
				out.println ("<tr><td> Hello World </td></tr>");
		} catch (Exception ex) {
			throw new Error ("All is not well in the world.");
		}
		// Must return SKIP_BODY because we are not supporting a body for this 
		// tag.
		return SKIP_BODY;
	}
	
	/**
	 * doEndTag is called by the JSP container when the tag is closed
	 */
	public int doEndTag(){
		try {
			JspWriter out = pageContext.getOut();
			out.println ("</table>");
		} catch (Exception ex) {
			throw new Error ("All is not well in the world.");
		}
		return 1;
	}
}