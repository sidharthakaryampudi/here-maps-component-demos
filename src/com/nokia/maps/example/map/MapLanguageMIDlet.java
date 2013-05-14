/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.map;


import java.util.Random;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplayState;


/**
 * Minimal MIDP application to show maps of various languages to the user.
 */
public class MapLanguageMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new MapLanguageExample(display, this);
    }

    protected String getTitle() {
        return "Map Language";
    }

    protected String getDescription() {
        return "This displays the map in one of eight random languages. "
                + "Both Simplified and Traditional Chinese character "
                + "sets are supported.";
    }

    private final static String[] MARC_CODES = {
        "ARA", "CHI", "CHT", "GER", "ENG", "FRE", "ITA", "RUS", "SPA" };
    private final static String[] LANGUAGES = {
        "Arabic", "Simplified Chinese", "Traditional Chinese", "German",
        "English", "French", "Italian", "Russian", "Spanish" };

    /**
     * This displays the map in one of eight random languages
     *
     */
    private class MapLanguageExample extends MapCanvasExample {

        public MapLanguageExample(Display display, MIDlet midlet) {
            super(display, midlet);

            Random r = new Random();
            int i = r.nextInt(LANGUAGES.length);

            ApplicationContext.getInstance().setDefaultLanguage(MARC_CODES[i]);
            setTitle(LANGUAGES[i]);

            map.setState(
                    new MapDisplayState(new GeoCoordinate(53.1, 13.1, 0), 4));
        }
    }
}
