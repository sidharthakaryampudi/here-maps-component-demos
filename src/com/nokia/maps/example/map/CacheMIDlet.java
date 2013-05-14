/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.map;


import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplayState;


/**
 * This MIDlet sets MapDisplay to use cache.
 */
public class CacheMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {

        return new CacheDemo(display, this);
    }

    protected String getTitle() {
        return "Map caching";
    }

    protected String getDescription() {
        return "The API can be set up to cache map tiles on the device to reduce the overhead of "
                + "network traffic. The cache will be created in a directory called "
                + "CachedDemo/cache on the last writeable volume  ";
    }

    /**
     * MapCanvas using cache
     */
    private class CacheDemo extends MapCanvasExample {

        // Change this to alter the initial location of the map.
        // The longitude and latitude as given here  will start
        // with a map centered over Berlin.
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public CacheDemo(Display display, MIDlet midlet) {
            super(display, midlet);
            // Start by viewing central Berlin at medium zoom.
            map.setState(new MapDisplayState(BERLIN, 13));
            try {
                // enables default file based caching with
                // max 10000 cached items and will not be used when
                // 15 percent of the volume free or less is free.
                // The cache will be created in a directory called
                // "CachedDemo/cache" on the last writeable volume
                map.setCache("CachedDemo/cache", 10000, 15);
            } catch (IOException e) {
                error("Error setting cache");
            }
        }
    }

}
