/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.gesture;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.GestureMapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplayState;


public class PinchZoomMIDlet extends BaseMIDlet {

    protected String getTitle() {
        return "Pinch Zoom Example";
    }

    protected String getDescription() {
        return "This example shows how create a map with the pinch zoom gesture enabled.";
    }

    protected MapCanvas getDemo(Display display) {
        return new PinchZoomDemo(display, this);
    }

    /**
     * This example shows how create a map with pinch zoom enabled.
     *
     */
    private class PinchZoomDemo extends GestureMapCanvasExample {

        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public PinchZoomDemo(Display display, MIDlet midlet) {
            super(display, midlet);
            if (isPinchSupported()) {
                // Since the Map supports  the Pinch Zoom Gesture, and the pinch zoom component
                // has been automatically added to the Gesture Map Canvas,  we can remove
                // the zoom buttons.
                MapComponent component = map.getMapComponent("ZoomImgComponent");

                if (component != null) {
                    map.removeMapComponent(component);
                }

            } else {
                // this demo requires gesture to work.
                note("Pinch Gesture not enabled.", 5000);
            }
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 15));
        }
    }
}
