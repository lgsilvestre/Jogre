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
package org.jogre.common;

import java.util.Locale;

/**
 * Class for setting global variables such as the current
 * locale etc.
 *
 * @author Bob Marks
 * @version Alpha 0.2.3
 */
public class JogreGlobals {

    public static final String FRENCH = "fr";
    public static final String ENGLISH = "en";
    
    public static final String SUPPORTED_LANGS = "en, fr";    
    
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    
    private static Locale locale = DEFAULT_LOCALE;

    /**
     * Return the locale.
     * 
     * @return
     */
    public static Locale getLocale () {
        return locale;
    }

    /**
     * Set the locale using a language or a language and country.
     * 
     * @param locale   Locale as a string e.g. "en,gb" or "fr".
     */
    public static void setLocale (String localeStr) {
        int pos = localeStr.indexOf (",");
        if (pos != -1) {
            String language = localeStr.substring (0, pos - 1);
            String country = localeStr.substring (pos + 1);
            locale = new Locale (language, country);
        }
        else 
            locale = new Locale (localeStr);
    }
}
