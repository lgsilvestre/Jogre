/*
 * JOGRE (Java Online Gaming Real-time Engine) - Quetris
 * Copyright (C) 2007  Bob Marks (marksie531@yahoo.com)
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
import org.jogre.common.util.JogreUtils;
import org.jogre.quetris.client.QuetrisPlayerModel;

/**
 * Class for sending the state of a player to the various other
 * players at a table to keep everything in synch.
 * 
 * The grid is compressed to save communication data using a simple 
 * run-length encoding technique.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class CommQuetrisPlayerState extends CommTableMessage {

    // Constants
    private static final int NUM_OF_CELLS = QuetrisPlayerModel.NUM_OF_COLS * QuetrisPlayerModel.NUM_OF_ROWS;    
    private static final int RLE_START = 13;
        
    // Fields
    private int      length; 
    private int [][] gridData; 
    
    private int []   compressedGridData;
    private int      seatNum;
    
    // XML variables
    public static final String XML_NAME = "quetris_player_state";
    public static final String XML_ATT_SEAT = "seat";
    
    /**
     * Constructor for a quetris player state which compressed the grid data using 
     * RLE.  
     * 
     * @param gridData   Uncompressed grid data.
     */
    public CommQuetrisPlayerState (int seatNum, int [][] gridData) {
        this.seatNum  = seatNum;
        this.gridData = gridData;
        
        compress ();
    }
    
    /**
     * Constructor which takes 
     * 
     * @param message
     */
    public CommQuetrisPlayerState (XMLElement message) {
        this.seatNum = message.getIntAttribute (XML_ATT_SEAT);
        
        // Read content from message
        String content = message.getContent();
        
        // Set compressed data and length string        
        this.compressedGridData = JogreUtils.convertToIntArray (content);
        this.length = compressedGridData.length;
        
        // Now we can decompress again
        decompress ();		
    }
    
    /**
     * Compress the 2 dimensional data into a 1 dimensional data using RLE.
     */
    private void compress () {
        this.length = 0;
        int [] tempGrid = new int [NUM_OF_CELLS];
                
        // Loop through the various cells
        for (int i = 0; i < NUM_OF_CELLS - 1; i++) {
            int v1 = getGridData (i);
            int v2 = getGridData (i + 1);
            
            // If the next value in the grid is the same then compress it.
            // If value is e.g. 3 then it becomes 12 (3 + run length encoding offset)
            if (v1 == v2) {
                tempGrid [length++] = v1 + RLE_START + 1;
                
                // Compute "count"
                int count = 1;
                while (v1 == getGridData (i + 1) && 
                       i < NUM_OF_CELLS - 2) 
                {
                    count ++;
                    i++;
                    
                    if (i >= NUM_OF_CELLS - 1)
                        break;
                }
                tempGrid [length++] += count;
            }
            else 
                tempGrid [length++] += v1;
        }
        
        // Compute last variable  
        tempGrid [length++] = getGridData (NUM_OF_CELLS - 1);

        // Copy temporary data into compressed grid data field
        this.compressedGridData = new int [length];
        for (int i = 0; i < length; i++)
            compressedGridData [i] = tempGrid [i];
    }
    
    /**
     * Compress compressed data to decompressed data.
     * 
     * @param gridData
     */
    private void decompress () {
        this.gridData = new int [QuetrisPlayerModel.NUM_OF_COLS][QuetrisPlayerModel.NUM_OF_ROWS];

        // Loop through the various cells
        int index = 0;
        for (int i = 0; i < length; i++) {
            int cv = compressedGridData [i];
            
            // If the value is less than number of different quetris pieces then its uncompressed
            if (cv < RLE_START) { 
                setGridData (index++, cv);
            }
            // Otherwise its compressed using RLE
            else {
                // Find out what decompressed value is by subtracing the RLE offset 
                // from the compressed value
                int dv = cv - (RLE_START + 1);
                
                // count is the next token
				int count = compressedGridData [++i];	
	            
				// Set the next "count" number of items to the decompressed value
				for (int j = 0; j < count; j++) 
				    setGridData (index++, dv);
            }				
        }
    }
    
    /**
     * Return the value of a grid data at a particular point.
     * 
     * @param index
     * @return
     */
    private int getGridData (int index) {
        return this.gridData [index % QuetrisPlayerModel.NUM_OF_COLS]
                             [index / QuetrisPlayerModel.NUM_OF_COLS];
    }
    
    /**
     * Set the grid data
     * 
     * @param index
     * @param value
     */
    private void setGridData (int index, int value) {
        this.gridData [index % QuetrisPlayerModel.NUM_OF_COLS]
                      [index / QuetrisPlayerModel.NUM_OF_COLS] = value;
    }
    
    /**
     * Return the grid data back to the user.
     * 
     * @return
     */
    public int [][] getGridData() {
        return gridData;
    }
    
    /**
     * Return the seat number to the user.
     * 
     * @return
     */
    public int getSeatNum () {
        return this.seatNum;
    }
    
    /**
     * Flatten this object to an XML string which can be sent as a message.
     * 
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten() {
        XMLElement message = super.flatten (XML_NAME);
        
        // Set content as String representation of compressed data
        message.setContent (JogreUtils.valueOf(compressedGridData));
        message.setIntAttribute (XML_ATT_SEAT, seatNum);
        
        return message;
    }
}

