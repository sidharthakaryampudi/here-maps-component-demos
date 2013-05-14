/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.touch;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Centering Component demo as current Displayable.
 */
public class CenteringMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new CenteringExample(display, this);

    }

    protected String getTitle() {
        return "Centering Component";
    }

    protected String getDescription() {
        return "Demonstrates the use of the centering component. This means that when a "
                + "MapObject is pressed, the map state is altered to bring it to the center "
                + "of the map";
    }

    /**
     * Demonstrates the use of the centering component. This means that when a
     * MapObject is pressed, the map state is altered to bring it to the center
     * of the map.
     */
    private class CenteringExample extends MapCanvasExample {

        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Centering Component Demo.
         *
         * @param display
         * @param midlet
         */
        public CenteringExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Removes unnecessary map components.
            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
            } else {
                note("Touch not enabled.", 5000);
            }

            // Add the centering component.
            map.addMapComponent(new CenteringComponent(this));

            // Now we can set up the markers..
            addCityMarker(new GeoCoordinate(40.4, -3.683333, 0));
            addCityMarker(new GeoCoordinate(51.477811d, -0.001475d, 0));
            addCityMarker(new GeoCoordinate(60.170833, 24.9375, 0));
            addCityMarker(new GeoCoordinate(59.949444, 10.756389, 0));

            addCityMarker(new GeoCoordinate(45.4375, 12.335833, 0));

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

        }

        /**
         * Helper function to add map markers.
         *
         * @param coord
         */
        private void addCityMarker(GeoCoordinate coord) {
            MapStandardMarker marker = mapFactory.createStandardMarker(coord);

            map.addMapObject(marker);
        }

    }

}
