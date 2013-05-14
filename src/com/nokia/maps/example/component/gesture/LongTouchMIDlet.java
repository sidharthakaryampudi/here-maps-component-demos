/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.gesture;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.gesture.GestureLongTouch;
import com.nokia.maps.component.gesture.LongTouchComponent;
import com.nokia.maps.component.gesture.TimerLongTouch;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.GestureMapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Long Press demo as current Displayable.
 */
public class LongTouchMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new LongTouchExample(display, this);

    }

    protected String getTitle() {
        return "Long Touch Gesture";
    }

    protected String getDescription() {
        return "Demonstrates adding a component responding to the long press gesture to the map. When the Map "
                + "is pressed and held, a command is fired with a notification of the "
                + "location of the Long Press";
    }

    /**
     * Demonstrates the Long Touch component. When a Long Touch occurs, the
     * GeoCoordinate pressed is displayed in an alert on the screen.
     */
    private class LongTouchExample extends GestureMapCanvasExample {

        private final Command LONG_TOUCH = new Command("Touch", Command.OK, 3);

        private final LongTouchComponent longTouch;

        private final GeoBoundingBox WORLDWIDE = new GeoBoundingBox(
                new GeoCoordinate(65, -90, 0), new GeoCoordinate(-65, 90, 0));

        /**
         * Constructor for the Long Press Demo.
         *
         * @param display
         * @param midlet
         */
        public LongTouchExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Removes unnecessary map components.
            map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
            map.removeMapComponent(map.getMapComponent("DefaultCursor"));

            // Register for long press events in the whole canvas area
            // Create the long Touch component. This class will handle the
            // LONG_TOUCH command.

            if (isGestureSupported()) {
                longTouch = new GestureLongTouch(this, getCommandListener(),
                        LONG_TOUCH);
            } else {
                longTouch = new TimerLongTouch(this, getCommandListener(),
                        LONG_TOUCH);
            }

            // This demo requires touch to work.
            if (!hasPointerEvents()) {
                note("Touch not enabled.", 5000);
            }

            map.addMapComponent(longTouch);

            // Set up the map.
            map.zoomTo(WORLDWIDE, false);

        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         * @param d
         */
        public void commandRun(final Command c) {
            if (c == LONG_TOUCH) {
                // When a long Touch has occured, display the Geo Coordinates
                // in an Alert.
                GeoCoordinate coord = longTouch.getTouchAt();
                double lat = (Math.floor(coord.getLatitude() * 10)) / 10d;
                double lng = (Math.floor(coord.getLongitude() * 10)) / 10d;
                Alert alertView = new Alert("Touch",
                        "Touched at:" + Math.abs(lat) + ((lat > 0) ? "N" : "S")
                        + " " + Math.abs(lng) + ((lng > 0) ? "E" : "W"),
                        null,
                        AlertType.ERROR);

                display.setCurrent(alertView);
            }
        }
    }

}
