package com.nokia.maps.example.location;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.ui.helpers.Orientation;
import javax.microedition.lcdui.Command;


/**
 * This MIDlet just sets the geolocator demo as current Displayable.
 */
public class PositioningMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new PositioningExample(display, this);

    }

    protected String getTitle() {
        return "Postioning Component";
    }

    protected String getDescription() {
        return "Demonstrates adding a positioning geolocator button to the map.  When the "
                + "button is pressed, the app will listen for GPS or Cell Id location events as"
                + " appropriate, and update a marker to the center of the screen.";
    }

    /**
     * Demonstrates adding a positioning geolocator button to the map. When
     * pressed, the app will listen for GPS or Cell Id location events as
     * appropriate.
     */
    private class PositioningExample extends MapCanvasExample {

        private final Command POSITIONING_COMMAND = new Command("Positioning",
                Command.ITEM, 1);
        private final Positioning positioning;
        
        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Geolocation Demo.
         *
         * @param display
         * @param midlet
         */
        public PositioningExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Add the button - the try catch is in case the Images fail to
            // load.
            try {

                positioning = new Positioning(this);
                map.addMapComponent(positioning);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            // Set up the map, this will initially display a map of Europe.          
            map.zoomTo(EUROPE, false);

            if (!hasPointerEvents()) {
                //
                // Since touch is unavailable, access the positioning button via
                // a command instead.
                //
                addCommand(POSITIONING_COMMAND);
            }

        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         *
         */
        public void commandRun(final Command c) {
            if (c == POSITIONING_COMMAND) {
                // For Non- touch phones only use a command button.
                positioning.toggleButton();
                onMapContentUpdated();
            }
        }
    }
}
