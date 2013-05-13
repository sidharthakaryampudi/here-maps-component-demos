package com.nokia.maps.example.markers;


import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.example.component.button.ButtonCommand;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapMarker;
import com.nokia.maps.map.Point;
import javax.microedition.lcdui.Graphics;


/**
 * This MIDlet creates custom MapMarker and place it to center of the screen
 */
public class CustomMarkerMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new CustomMarkerExample(display, this);
    }

    protected String getTitle() {
        return "Custom Map marker";
    }

    protected String getDescription() {
        return "This demonstrates how to create image markers using the MapMarker class.";
    }

    /**
     * Implements "Add marker" command to place custom icon marker to center of
     * the screen
     */
    private class CustomMarkerExample extends MapCanvasExample {

        private final Command ADD_MARKER = new Command("Add marker", Command.OK,
                1);
        private final ButtonCommand addMarkerButton;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public CustomMarkerExample(Display display, MIDlet midlet) {
            super(display, midlet);
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));

            addMarkerButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT,
                    this, getCommandListener(), ADD_MARKER);
            map.addMapComponent(addMarkerButton);

        }

        /**
         * Called from thread executing the command
         */
        public void commandRun(Command c) {
            if (c == ADD_MARKER) {
                Image markerIcon = null;

                // load image resource from MIDlet's jar file
                try {
                    markerIcon = Image.createImage("/marker/doughnut.png");
                } catch (IOException e) {
                    e.printStackTrace();
                    error("marker image not found");
                    return;
                }
                // get center coordinate of the screen
                Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);
                GeoCoordinate gc = map.pixelToGeo(center);

                // create marker with custom icon
                MapMarker mm = mapFactory.createMapMarker(gc, markerIcon);

                // set anchor point to center the icon
                Point anchor = new Point(markerIcon.getWidth() / 2,
                        markerIcon.getHeight() / 2);

                mm.setAnchor(anchor);
                map.addMapObject(mm);
            }
        }
    }
}
