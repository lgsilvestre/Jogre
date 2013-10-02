/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
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
package org.jogre.quetris.common;

import nanoxml.XMLElement;

import org.jogre.common.comm.CommTableMessage;

/**
 * Communication object for transmitting a quetris move.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class CommQuetrisMove extends CommTableMessage {

    // Declare some fields
    private int seatNum;
    private int curShapeNum, curShapePos; 
    private int curShapeX, curShapeY;
    private int nextShapeNum;
    
    // Declare attribute names of XML
    public  static final String XML_NAME           = "quetris_move";
    private static final String XML_ATT_SEAT       = "seat";
    private static final String XML_ATT_SHAPE_NUM  = "num";
    private static final String XML_ATT_SHAPE_POS  = "pos";
    private static final String XML_ATT_SHAPE_X    = "x";
    private static final String XML_ATT_SHAPE_Y    = "y";
    private static final String XML_ATT_NEXT_SHAPE = "next"; 

    /**
     * Constructor which takes fields.
     * 
     * @param seatNum        Seat number of user.
     * @param curShapeNum    Current shape number.
     * @param curShapePos    Current shape position.
     * @param curShapeX      Current shape X position.
     * @param curShapeY      Current shape X position.
     * @param nextShapeNum   Next shape number.
     */
    public CommQuetrisMove(int seatNum,
                          int curShapeNum, 
                          int curShapePos, 
                          int curShapeX, 
                          int curShapeY, 
                          int nextShapeNum) 
    {
        // Set fields.
        this.seatNum      = seatNum;
        this.curShapeNum  = curShapeNum; 
        this.curShapePos  = curShapePos; 
        this.curShapeX    = curShapeX; 
        this.curShapeY    = curShapeY; 
        this.nextShapeNum = nextShapeNum; 
    }
    
    /**
     * Constructor which takes an XML element.
     * 
     * @param message
     */
    public CommQuetrisMove (XMLElement message) {
        super (message);
        
        // Read fields from XML
        this.seatNum      = message.getIntAttribute(XML_ATT_SEAT);
        this.curShapeNum  = message.getIntAttribute(XML_ATT_SHAPE_NUM);
        this.curShapePos  = message.getIntAttribute(XML_ATT_SHAPE_POS);
        this.curShapeX    = message.getIntAttribute(XML_ATT_SHAPE_X);
        this.curShapeY    = message.getIntAttribute(XML_ATT_SHAPE_Y);
        this.nextShapeNum = message.getIntAttribute(XML_ATT_NEXT_SHAPE);    
    }

    /**
     * Return seat number.
     * 
     * @return  Current seat number.
     */
    public int getSeatNum() {
        return this.seatNum;
    }
    
    /**
     * Return current shape number.
     * 
     * @return  Current shape number.
     */
    public int getCurShapeNum() {
        return curShapeNum;
    }
    
    /**
     * Return current shape position.
     * 
     * @return   Current shape position.
     */
    public int getCurShapePos() {
        return curShapePos;
    }
    
    /**
     * Return current shape X position.
     * 
     * @return  current shape X position.
     */
    public int getCurShapeX() {
        return curShapeX;
    }
    
    /**
     * Return current shape Y position.
     * 
     * @return  Current shape Y position.
     */ 
    public int getCurShapeY() {
        return curShapeY;
    }
    
    /**
     * Return the next shape number.
     * 
     * @return  Next shape number.
     */
    public int getNextShapeNum() {
        return nextShapeNum;
    }
    
    /**
     * Flatten to an XMLElement.
     * 
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        XMLElement message = super.flatten (XML_NAME);
        message.setIntAttribute (XML_ATT_SEAT,       seatNum);
        message.setIntAttribute (XML_ATT_SHAPE_NUM,  curShapeNum);
        message.setIntAttribute (XML_ATT_SHAPE_POS,  curShapePos);
        message.setIntAttribute (XML_ATT_SHAPE_X,    curShapeX);
        message.setIntAttribute (XML_ATT_SHAPE_Y,    curShapeY);
        message.setIntAttribute (XML_ATT_NEXT_SHAPE, nextShapeNum);
        
        return message;
    }
}
