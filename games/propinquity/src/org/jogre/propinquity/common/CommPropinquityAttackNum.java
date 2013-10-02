/*
 * JOGRE (Java Online Gaming Real-time Engine) - Propinquity
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
package org.jogre.propinquity.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;
import org.jogre.propinquity.client.Cell;

/**
 * Communications object for a propinquity attack number which is generated on 
 * the server.
 *
 * @author  Bob Marks
 * @version Alpha 0.1
 */
public class CommPropinquityAttackNum extends CommTableMessage {

	// Used to actually send a move.
	public  static final String XML_NAME = "propinquity_attack_num";
	private static final String XML_ATT_ATTACK_NUM = "num";
	
	private int attackNum; 
	
	/**
	 * Constructor to an attack number which creates a new attack number
	 * between a specified range which is held as a static variable in 
	 * PropinquityModel.
	 */
	public CommPropinquityAttackNum () {
	    super ();
		attackNum = (int)(Math.random() * Cell.MAX_AMOUNT) + 1;
	}
	
	/**
	 * @param username
	 * @param tableNum
	 */
	public CommPropinquityAttackNum (XMLElement message) {
		super (message);
	    this.attackNum = message.getIntAttribute(XML_ATT_ATTACK_NUM);	
	}
	
	/**
	 * Return the attack num.
	 * 
	 * @return
	 */
	public int getAttackNum () {
		return attackNum;
	}
	
	/**
	 * Flatten the String.
	 * 
	 * @see org.jogre.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten () {
	    XMLElement message = flatten (XML_NAME);
		message.setIntAttribute (XML_ATT_ATTACK_NUM, attackNum);
		
		return message;
	}
}
