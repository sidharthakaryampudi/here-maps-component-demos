/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.map;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplayState;


/**
 * Basic map example
 *
 */
public class MapMIDlet extends BaseMIDlet {

    protected String getTitle() {
        return "Basic map example";
    }

    protected String getDescription() {
        return "This example shows how to create a basic map with few lines of code";
    }

    protected MapCanvas getDemo(Display display) {
        return new MapDemo(display, this);
    }

    /**
     * This example shows how create a basic map with few lines of code.
     *
     */
    private class MapDemo extends MapCanvasExample {

        public MapDemo(Display display, MIDlet midlet) {
            super(display, midlet);

            // Set the state of the map to show at a specified  location
            // and specified zoom. In this case we have chosen a location
            // in central Berlin.
            map.setState(
                    new MapDisplayState(new GeoCoordinate(52.5310, 13.3849, 0),
                    13));
        }
    }
}
