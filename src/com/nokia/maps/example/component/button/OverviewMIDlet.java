/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.button;


import javax.microedition.lcdui.Command;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Pic-in-Pic demo as current Displayable.
 */
public class OverviewMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new OverviewExample(display, this);
    }

    protected String getTitle() {
        return "Overview Component";
    }

    protected String getDescription() {
        return "Demonstrates the use of the overview (picture-in-picture) button, the "
                + "small Picture-in-Picture will track the central location of the main map as it is "
                + "panned and zoomed.";
    }

    /**
     * Demonstrates adding an overview picture in picture button to the map, the
     * small Pic-in-Pic will track the central location of the main map as it is
     * panned/zoomed.
     */
    private class OverviewExample extends MapCanvasExample {

        private final Command OVERVIEW_COMMAND = new Command("Overview",
                Command.ITEM, 1);
        private final Overview overview;
        private Thread overviewUpdater;

        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Pic-in-Pic Constructor
         *
         * @param display
         * @param midlet
         */
        public OverviewExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Add the button - the try catch is in case the Images fail to
            // load.
            try {

                overview = new Overview();

                map.addMapComponent(overview);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);

            if (!hasPointerEvents()) {
                //
                // Since touch is unavailable, access the overview via
                // a command instead.
                //
                addCommand(OVERVIEW_COMMAND);
            }

        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         *
         */
        public void commandRun(final Command c) {
            if (c == OVERVIEW_COMMAND) {
                // For Non- touch phones only use a command button.
                // The  overview button toggle is on a separate thread, since the overview
                // control will initiate an http request.
                overviewUpdater = new Thread(new Runnable() {
                    public void run() {
                        overview.toggleButton();
                        onMapContentUpdated();
                    }
                });
                overviewUpdater.start();
            }
        }
    }
}
