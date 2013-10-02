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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Bunch of utilities which are used in several classes through the API.
 *
 * @author  Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreUtils {

	/**
	 * Important static variable which is set to denote if Jogre is running as an
	 * application or an applet.
	 */
	private static boolean isApplet = false;

	/**
	 * Static method which return true if this is an applet.
	 *
	 * @return
	 */
	public static boolean isApplet () {
		return isApplet;
	}

	/**
	 * Static method for setting true/false if this is an applet.
	 *
	 * @param isApplet   If true this is an applet.
	 */
	public static void setApplet (boolean isApplet) {
		JogreUtils.isApplet = isApplet;
	}

	/**
	 * Return a Color object from a String of "r,g,b" or "r,g,b,a"
	 *
	 * @param colourStr  Colour as a comma delimited String.
	 * @return           Return a Color object.
	 */
	public static Color getColour (String colourStr) {
		try {
			StringTokenizer st = new StringTokenizer (colourStr, ",");
			int r = Integer.parseInt(st.nextToken().trim());
			int g = Integer.parseInt(st.nextToken().trim());
			int b = Integer.parseInt(st.nextToken().trim());
			if (st.hasMoreTokens()) {
				int a = Integer.parseInt(st.nextToken().trim());
				return new Color (r, g, b, a);
			} else {
				return new Color (r, g, b);
			}
		}
		catch (Exception e) {
			System.err.println ("Error converting colour from " + colourStr);
			return null;
		}
	}

	/**
	 * Return a new Color from an existing color by add each of its
	 * rgb values with an integer delta amount.  E.g. If a color
	 * rgb (100, 100, 100), delta = 10 => New Color (110, 110, 110).
	 *
	 * @param color   Original Colour.
	 * @param delta   Amount to add color to each channel.
	 * @return        New Color
	 */
	public static Color getColorDelta (Color color, int delta) {
		int r = color.getRed () + delta < 256 ? color.getRed () + delta : 255;
		int g = color.getGreen () + delta < 256 ? color.getGreen () + delta : 255;
		int b = color.getBlue () + delta < 256 ? color.getBlue () + delta : 255;
		return new Color (r, g, b);
	}

	/**
	 * Return a Point from a string of "x,y"
	 *
	 * @param pointStr  Point as a comma delimited String.
	 * @return          A Point at the given point.
	 */
	public static Point getPoint (String pointStr) {
		try {
			StringTokenizer st = new StringTokenizer (pointStr, ",");
			int x = Integer.parseInt(st.nextToken().trim());
			int y = Integer.parseInt(st.nextToken().trim());
			return new Point (x, y);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return a Dimension from a string of "w,h"
	 *
	 * @param dimStr    Dimension as a comma delimited String.
	 * @return          A Dimension with the given width, height.
	 */
	public static Dimension getDimension (String dimStr) {
		try {
			StringTokenizer st = new StringTokenizer (dimStr, ",");
			int w = Integer.parseInt(st.nextToken().trim());
			int h = Integer.parseInt(st.nextToken().trim());
			return new Dimension (w, h);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return a Rectangle from a string of "x,y,w,h".  Where (x,y) is the
	 * upper left hand corner, and w,h is the width & height.
	 *
	 * @param rectStr    Rectangle as a comma delimited String.
	 * @return           A Rectangle decoded from the String.
	 */
	public static Rectangle getRectangle (String rectStr) {
		try {
			StringTokenizer st = new StringTokenizer (rectStr, ",");
			int x = Integer.parseInt(st.nextToken().trim());
			int y = Integer.parseInt(st.nextToken().trim());
			int w = Integer.parseInt(st.nextToken().trim());
			int h = Integer.parseInt(st.nextToken().trim());
			return new Rectangle (x, y, w, h);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Little helper method which reads as a String and formats it using a
	 * specified format String.
	 *
	 * @param dateString      Date as a String
	 * @param formatString    Format of the String
	 * @return                Date object.
	 * @see java.text.SimpleDateFormat
	 */
	public static Date readDate (String dateString, String formatString) {
		Date tempDate = null;
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat (formatString);
			tempDate = dateFormatter.parse (dateString);
		}
		catch (ParseException parseException) {
		    // Simply return null.
		}
		return tempDate;
	}

    /**
     * Convert String to int array.
     *
     * @param text  Simple integer array as a spaced delimited String.
     */
    public static String [] convertToStringArray (String text) {
        StringTokenizer st = new StringTokenizer (text, " ");
        String [] values = new String [st.countTokens()];

        // Populate line info array
        for (int i = 0; i < values.length; i++) {
            values [i] = st.nextToken().trim();
        }

        return values;
    }
    
    /**
     * Convert a String in form "x y" (2 numbers seperate by space) to a Point object.
     * 
     * @param text
     * @return
     */
    public static Point convertToPoint (String text) {
    	StringTokenizer st = new StringTokenizer (text, " ");    	
    	return new Point (Integer.parseInt (st.nextToken()), Integer.parseInt (st.nextToken()));
    }

    /**
     * Convert String to int array.
     *
     * @param text  Simple integer array as a spaced delimited String.
     */
    public static int [] convertToIntArray (String text) {
        StringTokenizer st = new StringTokenizer (text, " ");
        int [] values = new int [st.countTokens()];

        // Populate line info array
        for (int i = 0; i < values.length; i++) {
            values [i] = Integer.parseInt (st.nextToken().trim());
        }

        return values;
    }

    /**
     * Convert String to boolean array.
     *
     * @param text  Simple integer array as a spaced delimited String.
     * @return      Returns a String of booleans of format "t f t".
     */
    public static boolean [] convertToBoolArray (String text) {
        StringTokenizer st = new StringTokenizer (text, " ");
        boolean [] values = new boolean [st.countTokens()];

        // Populate line info array
        for (int i = 0; i < values.length; i++)
            values [i] = st.nextToken().trim().equals("t");

        return values;
    }

	/**
	 * Helper method for converting a 2 dimensional array to a single
	 * array.
	 *
	 * @param array   2 dimensional array.
	 * @return        1 dimensional array with contents of 2d array.
	 */
	public static int [] convertTo1DArray (int [][] array) {
		int l1 = array.length;
		int l2 = array[0].length;
		int [] singleArray = new int [l1 * l2];
		int c = 0;
		for (int i = 0; i < l1; i++)
			for (int j = 0; j < l2; j++)
				singleArray [c++] = array [i][j];

		return singleArray;
	}

	/**
	 * Helper method for converting a 2 dimensional array to a single
	 * array.
	 *
	 * @param array  Single dimension array of data.
	 * @param l1     Length 1.
	 * @param l2     Length 2
	 * @return       Returns a 2d array of size [l1][l2] of type
	 */
	public static int [][] convertTo2DArray (int [] array, int l1, int l2) {
		int [][] doubleArray = new int [l1][l2];

		int c = 0;
		for (int i = 0; i < l1; i++)
			for (int j = 0; j < l2; j++)
				doubleArray [i][j] = array [c++];

		return doubleArray;
	}

    /**
     * Convert simple integer array to space delimited String.
     *
     * @param values  Array of integers.
     * @return        String value.
     */
    public static String valueOf (int [] values) {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0; i < values.length; i++) {
            sb.append (values[i]);
            if (i < values.length - 1)
                sb.append (" ");
        }

        return sb.toString();
    }

    /**
     * Convert simple boolean array to space delimited String.
     *
     * @param values  Array of integers.
     * @return        String value of syntax "t f t ..." where t = true / f = false
     */
    public static String valueOf (boolean [] values) {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0; i < values.length; i++) {
            sb.append (values[i] ? "t" : "f");
            if (i < values.length - 1)
                sb.append (" ");
        }

        return sb.toString();
    }

    /**
     * Convert simple String array to space delimited String.
     *
     * @param values   Array of strings.
     * @return         String with spaces.
     */
    public static String valueOf (String [] values) {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0; i < values.length; i++) {
            sb.append (values[i]);
            if (i < values.length - 1)
                sb.append (" ");
        }

        return sb.toString();
    }

    /**
     * Return the data as a String formatted with a specified String.
     *
	 * @param date          Specified data / time.
	 * @param formatString  Format of string.
	 * @return              String representation of date.
	 */
	public static String valueOf (Date date, String formatString) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat (formatString);
		return dateFormatter.format (date);
	}
	
	/**
	 * Return a point as 2 numbers.
	 * 
	 * @param point
	 * @return
	 */
	public static String valueOf (Point point) {
		return (int)point.getX() + " " + (int)point.getY();
	}

	/**
	 * Get invert of 0 or 1.  Especially useful when trying to get
	 * opposing player's seat number during a 2 player game.
	 *
	 * @param value
	 * @return invert of 0 or 1
	 */
	public static int invert(int value) {
		return value == 1 ? 0 : 1;
	}

/********************************************************************/
/*
 * Jogre64Encode & Jogre64Decode provide support for sending binary
 * data between the server & clients.
 *
 * Jogre64Encode will encode a byte array into a string that can be
 *    sent as part of a message.
 * Jogre64Decode will decode an encoded string back into a byte array.
 *
 * These routines use a form of base-64 encoding that uses XML-safe
 * encoding.  This encoding converts every 3 bytes of binary data into
 * 4 printable characters.  It also puts a single character at the front
 * of the string that indicates how many buffer 0's were added to pad the
 * binary data out to a multiple of 3 bytes.
 */

	/* This is the encode array that converts 6-bit binary numbers
	 * in the range of 0-63 into printable ASCII characters.  This
	 * isn't a simple offset into the ASCII array (by adding 0x30,
	 * for example) because some of the characters in that range are
	 * special to XML.
	 */
	private static final byte [] encodeArray = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';'
	};

	// This table holds the # of excess bytes as a character.
	private final static byte [] excessTable = {'0', '1', '2'};


	/**
	 * Decode an ASCII code into the 6-bit binary code
	 */
	private static int dec (int code) {
		if ((code >= 'A') && (code <= 'Z')) {
			return (code - 'A');
		} else if ((code >= 'a') && (code <= 'z')) {
			return (code - 'a' + 26);
		} else{
			return (code - '0' + 52);
		}
	}

	/**
	 * Decode a single group (4 encoded bytes converted into 3 binary bytes)
	 *
	 * @param dest          The array to put the decoded data into
	 * @param dest_index    The starting point in the destination data to put the decoded data
	 * @param s0 - s4       The 4 source bytes
	 */
	private static void decodeGroup(byte [] dest, int dest_index, byte s0, byte s1, byte s2, byte s3) {

		int num = (dec(s3) << 18) | (dec(s2) << 12) | (dec(s1) << 6) | dec(s0);

		dest[dest_index  ] = (byte) (num >> 0);
		dest[dest_index+1] = (byte) (num >> 8);
		dest[dest_index+2] = (byte) (num >> 16);
	}

	/**
	 * Decode an array of 6-bit ASCII data into an array of 8-bit binary data
	 *
	 * @param encodedString      The Jogre64 encoded string to decode.
	 * @return an array of 8-bit binary bytes
	 */
	public static byte [] Jogre64Decode(String encodedString) {
		byte [] encodedData = encodedString.getBytes();
		int excess = encodedData[0] - '0';
		int groups = (encodedData.length - 1) / 4;
		int dataLength = (groups * 3) - excess;

		// Create the array to hold the decoded data
		byte [] decodedData = new byte [dataLength];

		// Convert the 4-character groups into 3-byte decodings
		int src_index = 1;
		int dest_index = 0;
		for (int i = 1; i < groups; i++) {
			decodeGroup(decodedData, dest_index, encodedData[src_index], encodedData[src_index+1], encodedData[src_index+2], encodedData[src_index+3]);
			src_index += 4;
			dest_index += 3;
		}

		// For the last group, decode into a special array and then copy out
		// only the data that is needed
		byte [] residue = new byte [3];
		decodeGroup(residue, 0, encodedData[src_index], encodedData[src_index+1], encodedData[src_index+2], encodedData[src_index+3]);
		decodedData[dest_index] = residue[0];
		if (excess == 1) {
			decodedData[dest_index+1] = residue[1];
		} else if (excess == 0) {
			decodedData[dest_index+1] = residue[1];
			decodedData[dest_index+2] = residue[2];
		}

		return decodedData;
	}

	/**
	 * Encode a single group (3 source bytes converted into 4 encoded bytes)
	 *
	 * @param dest          The array to put the encoded data into
	 * @param dest_index    The starting point in the destination data to put the encoded data
	 * @param s0, s1, s2    The 3 source bytes
	 */
	private static void encodeGroup(byte [] dest, int dest_index, int s0, int s1, int s2) {
		int num = ((s2 & 0xFF) << 16) | ((s1 & 0xFF) << 8) | (s0 & 0xFF);

		dest[dest_index  ] = encodeArray[((num >>  0) & 0x3F)];
		dest[dest_index+1] = encodeArray[((num >>  6) & 0x3F)];
		dest[dest_index+2] = encodeArray[((num >> 12) & 0x3F)];
		dest[dest_index+3] = encodeArray[((num >> 18) & 0x3F)];
	}

	/**
	 * Encode an array of 8-bit binary data into an array of 6-bit ASCII data.
	 *
	 * @param data          The 8-bit binary data to encode.
	 * @return a string that encodes the data.
	 */
	public static String Jogre64Encode(byte [] data) {
		int dataLength = data.length;
		int excessBytes = dataLength % 3;
		int groups = (dataLength / 3);
		int encodedLength = 4 * ( groups + ((excessBytes == 0) ? 0 : 1));

		// Allocate space for the encoding
		byte [] encodedData = new byte [encodedLength + 1];
		encodedData[0] = excessTable[excessBytes];

		// Convert the 3-byte groups into 4-character encodings
		int src_index = 0;
		int dest_index = 1;
		for (int i = 0; i < groups; i++) {
			encodeGroup(encodedData, dest_index, data[src_index], data[src_index+1], data[src_index+2]);
			src_index += 3;
			dest_index += 4;
		}

		// Convert any excess bytes at the end and add "=" padding bytes
		if (excessBytes == 1) {
			encodeGroup(encodedData, dest_index, data[src_index], 0, 0);
		} else if (excessBytes == 2) {
			encodeGroup(encodedData, dest_index, data[src_index], data[src_index+1], 0);
		}

		return new String(encodedData);
	}
}