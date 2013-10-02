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

import java.util.Iterator;
import java.util.Vector;

import nanoxml.XMLElement;

import org.jogre.common.util.JogreUtils;

/**
 * Communications message for transport data to the an administrator
 * client data panel. 
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class CommAdminClientData extends CommGameMessage {
	
	// XML fields
	private static final String XML_DATA = "data";
	
	private static final String XML_ATT_DATA_TYPE = "data_type";	
	private static final String XML_ATT_REQUEST_TYPE = "requestType";
	private static final String XML_ATT_PARAM_DATA = "param_data";
	
	public static final char RETRIEVE = 'R';
	public static final char NEW = 'N';
	public static final char UPDATE = 'U';
	public static final char DELETE = 'D';
	
	// Fields
	private String dataType;
	private char requestType;
	private String [][] data = null;
	private String [] paramData = null;	
	
	/**
	 * Constructor which takes a 2D array of String data which will be displayed in table format at the other end.
	 * 
	 * @param data
	 * @param dataType
	 */
	public CommAdminClientData (String [][] data, String dataType) {
		super ();
		
		this.dataType = dataType;
		this.data = data;
		this.requestType = RETRIEVE;
	}
	
	/**
	 * Constructor which is used to request a 2D array of data.
	 * 
	 * @param dataType
	 */
	public CommAdminClientData (String dataType) {
		this (null, dataType);
	}
	
	/**
	 * Constructor which is used for modifying data.
	 * 
	 * @param paramData     Parameter data to be used on a table of data.
	 * @param dataType      Data type e.g. users
	 * @param requestType   Request type - i.e. new, update, delete
	 */
	public CommAdminClientData (String [] paramData, String dataType, char requestType) {
		this.paramData = paramData;
		this.dataType = dataType;
		this.requestType = requestType;
	}
	
	/**
	 * Constructor which takes an XMLElement.
	 * 
	 * @param message
	 */
	public CommAdminClientData (XMLElement message) {
		this.dataType     = message.getStringAttribute (XML_ATT_DATA_TYPE);
		
		// Read request items
		this.requestType = (message.getStringAttribute (XML_ATT_REQUEST_TYPE).charAt(0));
		String reqDataStr = message.getStringAttribute (XML_ATT_PARAM_DATA);
		if (reqDataStr != null)
			this.paramData      = JogreUtils.convertToStringArray(message.getStringAttribute (XML_ATT_PARAM_DATA));
		
		// Read data from child row and column attributes into 2D string array
		Vector children = message.getChildren();
		Iterator it = children.iterator();
		int row = 0;
		while (it.hasNext()) {
			XMLElement rowElm = (XMLElement)it.next();
			int numAtts = rowElm.countAttributes();
			if (data == null)
				this.data = new String [children.size()][numAtts]; 
						
			for (int i = 0; i < numAtts; i++) {
				data [row][i] = rowElm.getStringAttribute("C" + i);
			}
			row ++;
		}
	}
	
	/**
	 * Return data type.
	 * 
	 * @return
	 */
	public String getDataType () {
		return this.dataType;
	}

	/**
	 * Return data.
	 * 
	 * @return
	 */
	public String [][] getData () {
		return this.data;
	}
	
	/**
	 * Return the request type.
	 * 
	 * @return
	 */
	public char getRequestType () {
		return this.requestType;
	}
	
	/**
	 * Return parameter data.
	 * 
	 * @return
	 */
	public String [] getParamData () {
		return this.paramData;
	}

	/* (non-Javadoc)
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten (Comm.ADMIN_CLIENT_DATA);
		message.setAttribute    (XML_ATT_DATA_TYPE,      dataType);
		message.setAttribute    (XML_ATT_REQUEST_TYPE, String.valueOf(requestType));
		
		// Add data if applicable
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				XMLElement dataElm = new XMLElement (XML_DATA);
				String [] row = data [i];
				for (int j = 0; j < row.length; j++) {
					String dataItem = row [j] == null ? "" : row [j];
					dataElm.setAttribute("C" + j, dataItem);
				}
				message.addChild (dataElm);
			}
		}
		
		// Add parameter data if applicable
		if (paramData != null) 
			message.setAttribute(XML_ATT_PARAM_DATA, JogreUtils.valueOf(paramData));
		 
		return message;
	}
}