/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.markers;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.example.component.button.ButtonCommand;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import javax.microedition.lcdui.Graphics;


/**
 * This MIDlet just sets example as current Displayable.
 */
public class MarkerMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {

        return new MarkerExample(display, this);
    }

    protected String getTitle() {
        return "Standard Map Marker";
    }

    protected String getDescription() {
        return "This demonstrates how to put a Standard map marker on the screen, and "
                + " how to make it draggable through the use of a Map Component";
    }

    /**
     * Adding a marker
     *
     * This example demonstrates adding map marker and moving it.
     */
    private class MarkerExample extends MapCanvasExample {

        private final Command SET_MARKER = new Command("Set marker",
                Command.ITEM, 1);
        private final Command MOVE_MARKER = new Command("Move marker",
                Command.ITEM, 1);
        private final ButtonCommand setMarkerButton;
        private final ButtonCommand moveMarkerButton;
        private MapStandardMarker marker;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public MarkerExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // initialise a couple of buttons for the canvas.
            setMarkerButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT,
                    this, getCommandListener(), SET_MARKER);
            moveMarkerButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT,
                    this, getCommandListener(), MOVE_MARKER);

            map.addMapComponent(setMarkerButton);
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));

            /* MarkerDragger component handles pointer events to move the marker */
            map.addMapComponent(
                    new MarkerDragger());
        }

        /**
         * Adds marker to center of the screen and returns its coordinate
         *
         * @return coordinate added
         */
        private GeoCoordinate selectPosition() {
            Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);
            GeoCoordinate gc = map.pixelToGeo(center);

            if (marker == null) {
                // create new marker
                marker = mapFactory.createStandardMarker(gc, 10, null,
                        MapStandardMarker.BALLOON);
                map.addMapObject(marker);
            } else {
                // move existing marker
                marker.setCoordinate(gc);
            }
            return gc;
        }

        /**
         * Called from thread executing the command
         */
        public void commandRun(Command c) {
            if (c == SET_MARKER || c == MOVE_MARKER) {
                if (marker == null) {
                    // change to move command
                    map.removeMapComponent(setMarkerButton);
                    map.addMapComponent(moveMarkerButton);

                }
                selectPosition();
                note("Marker set", 1500);
            }
        }
    }
}
